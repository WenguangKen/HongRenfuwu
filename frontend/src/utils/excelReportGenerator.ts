/**
 * Excel 报告生成器
 * 用于生成带有分析报告的 Excel 文件
 */

import { exportToCSVAsync } from './export';

interface ExportWithAnalyticsOptions {
  data: any[];
  columns: any[];
  filename: string;
  dateRange?: string;
  startDate?: string;
  endDate?: string;
  onProgress?: (progress: number) => void;
}

/**
 * 创建带分析报告的导出任务
 * 目前暂时使用基础的 CSV/Excel 导出逻辑
 */
export async function exportWithAnalytics(options: ExportWithAnalyticsOptions): Promise<string> {
  const { data, columns, filename, onProgress } = options;
  
  // 暂时调用基础的异步导出逻辑
  // 如果以后找回了高级分析报告代码，可在此处替换
  return exportToCSVAsync(data, columns, filename, onProgress);
}

/**
 * 创建带分析报告的付款记录导出任务
 */
export async function exportPaymentWithAnalytics(options: {
  data: any[];
  columns: any[];
  filename: string;
  dateRange?: string;
  onProgress?: (progress: number) => void;
}): Promise<string> {
  const { data, columns, filename, onProgress } = options;
  
  // 暂时调用基础的异步导出逻辑
  return exportToCSVAsync(data, columns, filename, onProgress);
}
