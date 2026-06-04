/**
 * 统一的导出任务助手
 * 所有导出功能都应该使用这个助手来创建导出任务
 */

import { exportToCSVAsync } from './export';
import { exportWithAnalytics, exportPaymentWithAnalytics } from './excelReportGenerator';
import { useExportTaskStore } from '@/stores/exportTask';
import { notification, Button } from 'ant-design-vue';
import { h } from 'vue';
import { DownloadOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import { logger } from './logger';

export interface ExportOptions {
  data: any[];
  columns: Array<{ key: string; title: string; dataKey?: string; type?: 'text' | 'image'; formatter?: (value: any, record: any) => string }>;
  filename: string; // 文件名（不含扩展名）
  pageType?: string; // 页面类型，用于标识导出来源
  templateId?: string; // 使用的模板ID
  templateName?: string; // 使用的模板名称
  onProgress?: (progress: number) => void;
}

export interface ExportWithAnalyticsOptions extends ExportOptions {
  dateRange?: string; // 统计日期范围
}

// 导出任务队列：限制并发数
const MAX_CONCURRENT_EXPORTS = 3; // 最多同时进行3个导出任务
const exportQueue: Array<{ taskId: string; execute: () => Promise<void>; reject: (error: any) => void }> = [];
let runningExports = 0;

/**
 * 处理导出队列
 */
async function processExportQueue() {
  if (runningExports >= MAX_CONCURRENT_EXPORTS || exportQueue.length === 0) {
    return;
  }

  const { taskId, execute, reject } = exportQueue.shift()!;
  runningExports++;

  try {
    await execute();
  } catch (error) {
    reject(error);
  } finally {
    runningExports--;
    // 继续处理队列中的下一个任务
    processExportQueue();
  }
}

/**
 * 创建导出任务并执行导出
 * 这是所有导出功能的统一入口
 * 支持并发控制，最多同时执行MAX_CONCURRENT_EXPORTS个导出任务
 */
export async function createExportTask(options: ExportOptions): Promise<string> {
  const { data, columns, filename, pageType = 'unknown', templateId, templateName, onProgress } = options;
  const taskStore = useExportTaskStore();

  // 清理敏感数据
  const safeData = data.map(record => {
    const safeRecord: any = { ...record };
    delete safeRecord.password;
    delete safeRecord.token;
    delete safeRecord.secret;
    delete safeRecord.apiKey;
    delete safeRecord.privateKey;
    delete safeRecord.internalId;
    return safeRecord;
  });

  // 生成文件名
  const fileName = `${filename}_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`;

  // 创建导出任务
  const taskId = taskStore.addTask({
    fileName,
    pageType,
    templateId,
    templateName,
    exportData: safeData,
    exportColumns: columns,
    exportFilename: filename,
  });

  // 更新任务状态为处理中
  taskStore.updateTask(taskId, { status: 'processing' });

  // 使用Promise包装导出逻辑，支持队列管理
  return new Promise<string>((resolve, reject) => {
    const executeExport = async () => {
      try {
        // 异步执行导出
        const fileUrl = await exportToCSVAsync(safeData, columns, filename, onProgress);

        // 导出成功
        const notificationKey = `export-${taskId}`;
        taskStore.updateTask(taskId, {
          status: 'completed',
          fileUrl,
          isRead: false, // 新完成的任务标记为未读
          notificationKey,
        });

        // 用于标记是否手动操作（点击下载或手动关闭）
        let isManualAction = false;
        const notificationStartTime = Date.now();
        const autoCloseDuration = 6000; // 6秒

        // 发送通知（只有未读任务才显示通知）
        notification.success({
          key: notificationKey,
          placement: 'top',
          message: '导出任务处理完成',
          description: `文件 "${fileName}" 已准备就绪。`,
          duration: 6, // 6秒后自动关闭
          btn: h(Button, {
            type: 'primary',
            size: 'middle',
            style: {
              borderRadius: '8px',
              background: '#6366f1',
              border: 'none',
              boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
              fontWeight: '600',
              padding: '0 20px'
            },
            icon: h(DownloadOutlined),
            onClick: () => {
              const task = taskStore.tasks.find(t => t.id === taskId);
              if (task && task.fileUrl) {
                const link = document.createElement('a');
                const fileUrl = task.fileUrl;
                link.href = fileUrl;
                link.download = task.fileName;
                link.style.visibility = 'hidden';
                document.body.appendChild(link);
                link.click();

                // 延迟清理，确保下载完成（URL对象由浏览器管理，下载完成后会自动清理）
                // 如果担心内存泄漏，可以在下载完成后手动释放
                setTimeout(() => {
                  try {
                    if (document.body.contains(link)) {
                      document.body.removeChild(link);
                    }
                    // 注意：fileUrl 是 blob URL，通常不需要手动释放，因为它在下载完成后会被浏览器清理
                    // 如果需要手动释放：URL.revokeObjectURL(fileUrl);
                  } catch (error) {
                    // 忽略清理错误
                  }
                }, 1000);

                // 点击下载后标记为已读并关闭通知
                isManualAction = true;
                taskStore.markAsRead(taskId);
                notification.close(notificationKey);
              }
            }
          }, { default: () => '立即下载' }),
          onClose: () => {
            // 判断是手动关闭还是自动关闭
            // 如果关闭时间距离开始时间小于6秒，说明是手动关闭（点击X）
            // 如果关闭时间距离开始时间约等于6秒，说明是自动关闭
            const closeTime = Date.now();
            const elapsed = closeTime - notificationStartTime;

            // 如果已经手动操作（点击下载），则已标记为已读，无需再次标记
            // 如果是手动关闭（点击X），且关闭时间小于6秒，则标记为已读
            // 如果是自动关闭（约6秒后），则不标记为已读
            if (isManualAction) {
              // 已经标记为已读，无需再次标记
              return;
            }

            // 如果关闭时间小于5.5秒，认为是手动关闭（点击X），标记为已读
            // 如果关闭时间大于等于5.5秒，认为是自动关闭，不标记为已读
            if (elapsed < autoCloseDuration - 500) {
              taskStore.markAsRead(taskId);
            }
            // 否则是自动关闭，不标记为已读，保持未读状态
          },
        });

        resolve(taskId);
      } catch (error: any) {
        // 导出失败
        logger.error('导出失败', error);
        taskStore.updateTask(taskId, {
          status: 'failed',
          error: error.message || '导出失败',
        });

        notification.error({
          message: '导出失败',
          description: error.message || '导出失败，可在任务列表中点击重新导出',
          duration: 4,
        });

        reject(error);
      }
    };

    // 将导出任务加入队列
    exportQueue.push({
      taskId,
      execute: executeExport,
      reject,
    });

    // 尝试处理队列
    processExportQueue();
  });
}

/**
 * 创建带分析报告的导出任务
 * 使用ExcelJS生成多Sheet Excel文件，包含分析报告和原始数据
 */
export async function createExportTaskWithAnalytics(options: ExportWithAnalyticsOptions): Promise<string> {
  const { data, columns, filename, pageType = 'unknown', templateId, templateName, dateRange, onProgress } = options;
  const taskStore = useExportTaskStore();

  // 清理敏感数据
  const safeData = data.map(record => {
    const safeRecord: any = { ...record };
    delete safeRecord.password;
    delete safeRecord.token;
    delete safeRecord.secret;
    delete safeRecord.apiKey;
    delete safeRecord.privateKey;
    delete safeRecord.internalId;
    return safeRecord;
  });

  // 生成文件名
  const fileName = `${filename}_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`;

  // 创建导出任务
  const taskId = taskStore.addTask({
    fileName,
    pageType,
    templateId,
    templateName,
    exportData: safeData,
    exportColumns: columns,
    exportFilename: filename,
  });

  // 更新任务状态为处理中
  taskStore.updateTask(taskId, { status: 'processing' });

  // 使用Promise包装导出逻辑，支持队列管理
  return new Promise<string>((resolve, reject) => {
    const executeExport = async () => {
      try {
        // 使用新的Excel报告生成器
        // 从dateRange提取startDate和endDate
        let startDate: string | undefined;
        let endDate: string | undefined;

        if (dateRange) {
          const parts = dateRange.split(' 至 ');
          if (parts.length === 2) {
            startDate = parts[0]!.trim();
            endDate = parts[1]!.trim();
          }
        }

        const fileUrl = await exportWithAnalytics({
          data: safeData,
          columns,
          filename,
          dateRange,
          startDate,
          endDate,
        });

        // 导出成功
        const notificationKey = `export-${taskId}`;
        taskStore.updateTask(taskId, {
          status: 'completed',
          fileUrl,
          isRead: false,
          notificationKey,
        });

        let isManualAction = false;
        const notificationStartTime = Date.now();
        const autoCloseDuration = 6000;

        notification.success({
          key: notificationKey,
          placement: 'top',
          message: '导出任务处理完成',
          description: `文件 "${fileName}" 已准备就绪（包含数据分析报告）。`,
          duration: 6,
          btn: h(Button, {
            type: 'primary',
            size: 'middle',
            style: {
              borderRadius: '8px',
              background: '#6366f1',
              border: 'none',
              boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
              fontWeight: '600',
              padding: '0 20px'
            },
            icon: h(DownloadOutlined),
            onClick: () => {
              const task = taskStore.tasks.find(t => t.id === taskId);
              if (task && task.fileUrl) {
                const link = document.createElement('a');
                const fileUrl = task.fileUrl;
                link.href = fileUrl;
                link.download = task.fileName;
                link.style.visibility = 'hidden';
                document.body.appendChild(link);
                link.click();

                setTimeout(() => {
                  try {
                    if (document.body.contains(link)) {
                      document.body.removeChild(link);
                    }
                  } catch (error) {
                    // 忽略清理错误
                  }
                }, 1000);

                isManualAction = true;
                taskStore.markAsRead(taskId);
                notification.close(notificationKey);
              }
            }
          }, { default: () => '立即下载' }),
          onClose: () => {
            const closeTime = Date.now();
            const elapsed = closeTime - notificationStartTime;

            if (isManualAction) {
              return;
            }

            if (elapsed < autoCloseDuration - 500) {
              taskStore.markAsRead(taskId);
            }
          },
        });

        resolve(taskId);
      } catch (error: any) {
        logger.error('导出失败', error);
        taskStore.updateTask(taskId, {
          status: 'failed',
          error: error.message || '导出失败',
        });

        notification.error({
          message: '导出失败',
          description: error.message || '导出失败，可在任务列表中点击重新导出',
          duration: 4,
        });

        reject(error);
      }
    };

    exportQueue.push({
      taskId,
      execute: executeExport,
      reject,
    });

    processExportQueue();
  });
}


/**
 * 创建带分析报告的付款记录导出任务
 */
export async function createPaymentExportTaskWithAnalytics(options: ExportWithAnalyticsOptions): Promise<string> {
  const { data, columns, filename, pageType = 'unknown', templateId, templateName, dateRange, onProgress } = options;
  const taskStore = useExportTaskStore();

  // 清理敏感数据
  const safeData = data.map(record => {
    const safeRecord: any = { ...record };
    delete safeRecord.password;
    delete safeRecord.token;
    delete safeRecord.secret;
    delete safeRecord.apiKey;
    delete safeRecord.privateKey;
    delete safeRecord.internalId;
    return safeRecord;
  });

  // 生成文件名
  const fileName = `${filename}_${dayjs().format('YYYY-MM-DD_HH-mm-ss')}.xlsx`;

  // 创建导出任务
  const taskId = taskStore.addTask({
    fileName,
    pageType,
    templateId,
    templateName,
    exportData: safeData,
    exportColumns: columns,
    exportFilename: filename,
  });

  // 更新任务状态为处理中
  taskStore.updateTask(taskId, { status: 'processing' });

  // 使用Promise包装导出逻辑，支持队列管理
  return new Promise<string>((resolve, reject) => {
    const executeExport = async () => {
      try {
        const fileUrl = await exportPaymentWithAnalytics({
          data: safeData,
          columns,
          filename,
          dateRange,
        });

        // 导出成功
        const notificationKey = `export-${taskId}`;
        taskStore.updateTask(taskId, {
          status: 'completed',
          fileUrl,
          isRead: false,
          notificationKey,
        });

        let isManualAction = false;
        const notificationStartTime = Date.now();
        const autoCloseDuration = 6000;

        notification.success({
          key: notificationKey,
          placement: 'top',
          message: '导出任务处理完成',
          description: `文件 "${fileName}" 已准备就绪（包含分析报告）。`,
          duration: 6,
          btn: h(Button, {
            type: 'primary',
            size: 'middle',
            style: {
              borderRadius: '8px',
              background: '#6366f1',
              border: 'none',
              boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
              fontWeight: '600',
              padding: '0 20px'
            },
            icon: h(DownloadOutlined),
            onClick: () => {
              const task = taskStore.tasks.find(t => t.id === taskId);
              if (task && task.fileUrl) {
                const link = document.createElement('a');
                const fileUrl = task.fileUrl;
                link.href = fileUrl;
                link.download = task.fileName;
                link.style.visibility = 'hidden';
                document.body.appendChild(link);
                link.click();

                setTimeout(() => {
                  try {
                    if (document.body.contains(link)) {
                      document.body.removeChild(link);
                    }
                  } catch (error) {
                    // 忽略清理错误
                  }
                }, 1000);

                isManualAction = true;
                taskStore.markAsRead(taskId);
                notification.close(notificationKey);
              }
            }
          }, { default: () => '立即下载' }),
          onClose: () => {
            const closeTime = Date.now();
            const elapsed = closeTime - notificationStartTime;

            if (isManualAction) {
              return;
            }

            if (elapsed < autoCloseDuration - 500) {
              taskStore.markAsRead(taskId);
            }
          },
        });

        resolve(taskId);
      } catch (error: any) {
        logger.error('导出失败', error);
        taskStore.updateTask(taskId, {
          status: 'failed',
          error: error.message || '导出失败',
        });

        notification.error({
          message: '导出失败',
          description: error.message || '导出失败，可在任务列表中点击重新导出',
          duration: 4,
        });

        reject(error);
      }
    };

    exportQueue.push({
      taskId,
      execute: executeExport,
      reject,
    });

    processExportQueue();
  });
}
