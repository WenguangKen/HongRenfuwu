/**
 * 通用分页数据获取工具
 * 用于递归获取分页接口的所有数据（全量抓取）
 */

/**
 * 分页请求参数接口
 */
interface PageParams {
  page?: number;
  size?: number;
  [key: string]: any;
}

/**
 * 后端分页响应结构接口
 */
interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  [key: string]: any;
}

/**
 * 分页解析选项
 */
export interface FetchOptions {
  pageSize?: number;
  onProgress?: (progress: number) => void;
  /** 并发请求数，默认 1（串行）。设为 3-5 可显著加快全量拉取速度。 */
  concurrency?: number;
}

/**
 * 递归获取所有页面的数据（支持并发拉取）
 * @param fetchFn 请求函数，接收 params 并返回包含 data 的 Promise (其中 data 为 PageResult)
 * @param params 初始请求参数
 * @param optionsOrPageSize 每页抓取数量或选项对象
 * @returns 所有页面的 content 拍平后的数组
 */
export async function fetchAllPages<T>(
  fetchFn: (params: PageParams) => Promise<any>,
  params: PageParams = {},
  optionsOrPageSize: number | FetchOptions = 100
): Promise<T[]> {
  // 解析选项
  const options: FetchOptions = typeof optionsOrPageSize === 'number' 
    ? { pageSize: optionsOrPageSize }
    : optionsOrPageSize;
    
  const pageSize = options.pageSize || 100;
  const concurrency = Math.max(1, Math.min(options.concurrency || 3, 10));

  try {
    // 第一步：先请求第 0 页以获取 totalPages
    const firstResponse = await fetchFn({
      ...params,
      page: 0,
      size: pageSize,
    });
    const firstResult: PageResult<T> = firstResponse.data || firstResponse;
    if (!firstResult || !Array.isArray(firstResult.content)) {
      console.warn('fetchAllPages: Invalid response format', firstResponse);
      return [];
    }

    const allData: T[] = [...firstResult.content];
    const totalPages = firstResult.totalPages || 0;

    if (options.onProgress) {
      options.onProgress(totalPages > 0 ? (1 / totalPages) * 100 : 100);
    }

    if (totalPages <= 1) {
      return allData;
    }

    // 安全检查
    if (totalPages > 1000) {
      console.error('fetchAllPages: totalPages exceeds limit (1000), truncating');
    }
    const maxPages = Math.min(totalPages, 1000);

    // 第二步：并发拉取剩余页面
    const remainingPages = Array.from({ length: maxPages - 1 }, (_, i) => i + 1);
    
    // 按 concurrency 分批执行
    const pageResults: Array<{ page: number; content: T[] }> = [];
    for (let i = 0; i < remainingPages.length; i += concurrency) {
      const batch = remainingPages.slice(i, i + concurrency);
      const batchPromises = batch.map(async (pageNum) => {
        const response = await fetchFn({
          ...params,
          page: pageNum,
          size: pageSize,
        });
        const result: PageResult<T> = response.data || response;
        if (!result || !Array.isArray(result.content)) {
          console.warn(`fetchAllPages: Invalid response for page ${pageNum}`);
          return { page: pageNum, content: [] as T[] };
        }
        return { page: pageNum, content: result.content };
      });

      const batchResults = await Promise.all(batchPromises);
      pageResults.push(...batchResults);

      if (options.onProgress) {
        const completedPages = 1 + pageResults.length;
        options.onProgress(Math.min((completedPages / maxPages) * 100, 100));
      }
    }

    // 按页码排序后合并（保证数据顺序一致性）
    pageResults.sort((a, b) => a.page - b.page);
    for (const pr of pageResults) {
      allData.push(...pr.content);
    }

    return allData;
  } catch (error) {
    console.error('fetchAllPages: Error during fetching', error);
    throw error;
  }
}

export default {
  fetchAllPages,
};
