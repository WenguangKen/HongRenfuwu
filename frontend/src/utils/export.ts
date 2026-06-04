/**
 * 导出工具函数
 */



/**
 * 敏感字段列表（这些字段不应该被导出）
 */
const SENSITIVE_FIELDS = ['password', 'token', 'secret', 'apiKey', 'privateKey', 'userId', 'internalId'];

/**
 * 检查字段是否为敏感字段
 */
function isSensitiveField(key: string): boolean {
  const lowerKey = key.toLowerCase();
  return SENSITIVE_FIELDS.some(sensitive => lowerKey.includes(sensitive));
}

/**
 * 导出数据为CSV文件（同步版本，立即下载）
 * @param data 要导出的数据数组
 * @param columns 字段配置 [{key: 'id', title: 'ID', dataKey?: string}]
 * @param filename 文件名（不含扩展名）
 * @returns 文件URL（用于下载）
 */
export function exportToCSV(
  data: any[],
  columns: Array<{ key: string; title: string; dataKey?: string; formatter?: (value: any, record: any) => string }>,
  filename: string = 'export'
): string | null {
  if (!data || data.length === 0) {
    console.warn('没有数据可导出');
    return null;
  }

  // 过滤敏感字段
  const safeColumns = columns.filter(col => {
    if (isSensitiveField(col.key) || isSensitiveField(col.dataKey || '')) {
      console.warn(`敏感字段 ${col.key} 已被过滤，不会被导出`);
      return false;
    }
    return true;
  });

  if (safeColumns.length === 0) {
    console.error('没有可导出的字段（所有字段都是敏感字段）');
    return null;
  }

  // 生成表头
  const headers = safeColumns.map(col => col.title);

  // 生成数据行
  const rows = data.map(record => {
    return safeColumns.map(col => {
      const dataKey = col.dataKey || col.key;
      let value = getNestedValue(record, dataKey);

      // 如果提供了格式化函数，使用格式化函数
      if (col.formatter) {
        value = col.formatter(value, record);
      } else {
        // 默认格式化
        value = formatValue(value);
      }

      // 再次检查值中是否包含敏感信息（额外安全层）
      if (typeof value === 'string' && isSensitiveField(value)) {
        value = '***';
      }

      // CSV格式处理：如果包含逗号、引号或换行符，需要用引号包裹，并转义引号
      if (typeof value === 'string' && (value.includes(',') || value.includes('"') || value.includes('\n'))) {
        value = `"${value.replace(/"/g, '""')}"`;
      }

      return value || '';
    });
  });

  // 组合CSV内容
  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n');

  // 添加BOM以支持中文
  const BOM = '\uFEFF';
  const blob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });

  // 创建下载链接
  const link = document.createElement('a');
  const url = URL.createObjectURL(blob);
  link.setAttribute('href', url);

  // 生成安全的文件名（移除特殊字符，防止路径注入）
  // 注意：同步版本使用CSV格式，异步版本使用Excel格式
  const safeFilename = filename.replace(/[<>:"/\\|?*]/g, '_').substring(0, 100);
  const timestamp = new Date().toISOString().slice(0, 10);
  link.setAttribute('download', `${safeFilename}_${timestamp}.csv`);

  link.style.visibility = 'hidden';
  document.body.appendChild(link);

  // 监听下载完成事件，然后清理URL
  const cleanup = () => {
    // 延迟清理，确保下载真正开始
    setTimeout(() => {
      try {
        if (document.body.contains(link)) {
          document.body.removeChild(link);
        }
        URL.revokeObjectURL(url);
      } catch (error) {
        // 忽略清理错误
      }
    }, 1000); // 增加到1秒，确保下载完成
  };

  // 尝试监听下载完成（如果浏览器支持）
  link.addEventListener('click', cleanup, { once: true });
  link.click();

  // 备用清理机制
  cleanup();

  return url;
}

/**
 * 异步导出数据为Excel文件（用于任务队列）
 * @param data 要导出的数据数组
 * @param columns 字段配置
 * @param filename 文件名（不含扩展名）
 * @param onProgress 进度回调
 * @returns Promise<string> 返回文件URL
 */
export async function exportToCSVAsync(
  data: any[],
  columns: Array<{ key: string; title: string; dataKey?: string; type?: 'text' | 'image'; formatter?: (value: any, record: any) => string }>,
  _filename: string = 'export',
  onProgress?: (progress: number) => void
): Promise<string> {
  return new Promise(async (resolve, reject) => {
    try {
      const ExcelJS = (await import('exceljs')).default;
      if (onProgress) onProgress(10);

      if (!data || data.length === 0) {
        reject(new Error('没有数据可导出'));
        return;
      }

      // 过滤敏感字段
      const safeColumns = columns.filter(col => {
        if (isSensitiveField(col.key) || isSensitiveField(col.dataKey || '')) {
          return false;
        }
        return true;
      });

      if (safeColumns.length === 0) {
        reject(new Error('没有可导出的字段'));
        return;
      }

      if (onProgress) onProgress(20);

      // 创建工作簿
      const workbook = new ExcelJS.Workbook();
      const worksheet = workbook.addWorksheet('Sheet1');

      if (onProgress) onProgress(30);

      // 添加数据行前先定义样式 (使用 any 避免命名空间找不到错误)
      const headerStyle: any = {
        font: { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 },
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: 'FF1E293B' } // 深蓝灰色商务背景
        },
        alignment: {
          horizontal: 'center',
          vertical: 'middle'
        },
        border: {
          bottom: { style: 'medium', color: { argb: 'FF334155' } }
        }
      };

      const evenRowStyle: any = {
        fill: {
          type: 'pattern',
          pattern: 'solid',
          fgColor: { argb: 'FFF8FAFC' } // 极淡蓝灰色斑马纹
        },
        alignment: { vertical: 'middle' },
        border: {
          bottom: { style: 'thin', color: { argb: 'FFF1F5F9' } }
        }
      };

      const oddRowStyle: any = {
        alignment: { vertical: 'middle' },
        border: {
          bottom: { style: 'thin', color: { argb: 'FFF1F5F9' } }
        }
      };

      // 添加表头
      const headerRow = worksheet.addRow(safeColumns.map(col => col.title));
      headerRow.height = 30; // 增加表头高度
      headerRow.eachCell((cell) => {
        cell.style = headerStyle;
      });

      // 冻结第一行
      worksheet.views = [{ state: 'frozen', ySplit: 1 }];

      // 记录每列的最大宽度用于自适应
      const colWidths = safeColumns.map(col => Math.min(Math.max(col.title.length * 2, 12), 50));

      if (onProgress) onProgress(40);

      // 添加数据行
      for (let i = 0; i < data.length; i++) {
        const record = data[i];
        if (onProgress && i % 100 === 0) {
          onProgress(40 + (i / data.length) * 50);
        }

        const rowData = [];
        const imageTasks = [];

        for (let colIdx = 0; colIdx < safeColumns.length; colIdx++) {
          const col = safeColumns[colIdx];
          if (!col) continue;
          const dataKey = col.dataKey || col.key;
          let value = getNestedValue(record, dataKey);

          if (col && col.type === 'image' && value) {
            // Placeholder for image column, image will be added later
            rowData.push('');
            imageTasks.push({ colIdx, url: value });
          } else if (col) {
            if (col.formatter) {
              value = col.formatter(value, record);
            } else {
              value = formatValue(value);
            }

            if (typeof value === 'string' && isSensitiveField(value)) {
              value = '***';
            }

            // 动态更新列宽
            const valStr = String(value || '');
            const len = valStr.replace(/[^\x00-\xff]/g, 'aa').length; // 中文算两个字符
            colWidths[colIdx] = Math.min(Math.max(colWidths[colIdx] || 12, len + 2), 60);
            rowData.push(value || '');
          }
        }

        const row = worksheet.addRow(rowData);

        // If has images, increase row height
        if (imageTasks.length > 0) {
          row.height = 80;
          for (const imgTask of imageTasks) {
            try {
              const response = await fetch(imgTask.url);
              const buffer = await response.arrayBuffer();
              const extension = imgTask.url.split('.').pop()?.split('?')[0] || 'png';

              const imageId = workbook.addImage({
                buffer,
                extension: extension as any,
              });

              worksheet.addImage(imageId, {
                tl: { col: imgTask.colIdx, row: row.number - 1 },
                ext: { width: 100, height: 100 },
                editAs: 'oneCell'
              });

              // Ensure column width is sufficient for image
              colWidths[imgTask.colIdx] = Math.max(colWidths[imgTask.colIdx] || 0, 15);
            } catch (err) {
              console.error('Failed to embed image:', imgTask.url, err);
            }
          }
        } else {
          row.height = 25; // 增加行高
        }

        // 应用斑马纹
        const currentStyle = (i % 2 === 1) ? evenRowStyle : oddRowStyle;

        row.eachCell((cell, colNum) => {
          cell.style = { ...currentStyle };

          // 对齐逻辑：数字/日期右对齐，其余左对齐
          const value = cell.value;
          if (typeof value === 'number' || (value instanceof Date)) {
            cell.alignment = { ...cell.alignment, horizontal: 'right' };
          } else {
            cell.alignment = { ...cell.alignment, horizontal: 'left' };
          }
        });
      }

      // 最后设置所有列宽
      colWidths.forEach((width, index) => {
        worksheet.getColumn(index + 1).width = width;
      });

      if (onProgress) onProgress(90);

      // 生成Excel文件
      const buffer = await workbook.xlsx.writeBuffer();
      const blob = new Blob([buffer], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      const url = URL.createObjectURL(blob);

      if (onProgress) onProgress(100);

      // 注意：URL对象会在文件下载后被释放（在exportTaskHelper中处理）
      resolve(url);
    } catch (error) {
      reject(error);
    }
  });
}

/**
 * 获取嵌套对象的值（安全版本，不会暴露敏感字段）
 */
function getNestedValue(obj: any, path: string): any {
  const parts = path.split('.');
  let current = obj;

  for (const part of parts) {
    if (current == null) return undefined;

    // 检查路径部分是否为敏感字段
    if (isSensitiveField(part)) {
      return undefined;
    }

    current = current[part];
  }

  return current;
}

/**
 * 格式化值（安全版本，会对敏感数据进行脱敏处理）
 */
function formatValue(value: any): string {
  if (value === null || value === undefined) {
    return '';
  }

  if (typeof value === 'boolean') {
    return value ? '是' : '否';
  }

  if (Array.isArray(value)) {
    // 对数组中的每个值进行安全检查
    return value.map(item => {
      const str = String(item);
      return isSensitiveField(str) ? '***' : str;
    }).join('; ');
  }

  if (typeof value === 'object') {
    // 对于对象，只导出非敏感字段
    try {
      const filtered: any = {};
      for (const [key, val] of Object.entries(value)) {
        if (!isSensitiveField(key)) {
          filtered[key] = val;
        }
      }
      return JSON.stringify(filtered);
    } catch {
      return '[Object]';
    }
  }

  const strValue = String(value);

  // 检查值本身是否为敏感信息（如token、密码等）
  if (isSensitiveField(strValue) || strValue.length > 100 && /token|password|secret/i.test(strValue)) {
    return '***';
  }

  return strValue;
}

/**
 * 数据脱敏函数（可用于特定字段的脱敏处理）
 */
export function maskSensitiveData(data: string, maskChar: string = '*'): string {
  if (!data || typeof data !== 'string') return data;

  // 如果是明显的敏感数据（如邮箱、手机号等），进行部分脱敏
  if (data.includes('@')) {
    // 邮箱脱敏：只显示前2位和后2位，中间用*代替
    const [local, domain] = data.split('@');
    if (!local || !domain) return data;
    if (local.length <= 2) return '***@' + domain;
    return local.substring(0, 2) + maskChar.repeat(Math.min(local.length - 2, 4)) + '@' + domain;
  }

  if (/^\d{11}$/.test(data)) {
    // 手机号脱敏：显示前3位和后4位
    return data.substring(0, 3) + maskChar.repeat(4) + data.substring(7);
  }

  return maskChar.repeat(Math.min(data.length, 10));
}
