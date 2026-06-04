/**
 * 统一API服务封装
 * 提供统一的API调用接口，统一错误处理和响应处理
 */

import http from '../http';
import axios, { AxiosError } from 'axios';
import type { AxiosResponse, AxiosRequestConfig } from 'axios';
import { logger } from '../logger';
import { apiCache } from './cache';
import { getCacheKey } from './cache';

/**
 * API响应基础结构
 */
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  success?: boolean;
}

/**
 * 分页响应结构
 */
export interface PaginatedResponse<T = unknown> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

/**
 * API请求配置（扩展Axios配置）
 */
export interface ApiRequestConfig extends AxiosRequestConfig {
  /** 是否显示错误提示（默认true） */
  showError?: boolean;
  /** 是否显示成功提示（默认false） */
  showSuccess?: boolean;
  /** 成功提示文案 */
  successMessage?: string;
  /** 是否使用缓存（默认false） */
  useCache?: boolean;
  /** 缓存TTL（毫秒，默认5分钟） */
  cacheTTL?: number;
  /** 缓存键（如果不提供，将根据URL和params自动生成） */
  cacheKey?: string;
}

/**
 * API服务基类
 */
class ApiService {
  /**
   * GET请求
   */
  async get<T = unknown>(url: string, config?: ApiRequestConfig): Promise<T> {
    // 检查缓存
    if (config?.useCache) {
      const cacheKey = config.cacheKey || getCacheKey(url, config?.params);
      const cached = apiCache.get<T>(cacheKey);
      if (cached !== null) {
        logger.debug(`API缓存命中: ${url}`);
        return cached;
      }
    }

    try {
      const response: AxiosResponse<ApiResponse<T>> = await http.get(url, config);
      const data = this.handleResponse<T>(response, config);

      // 保存到缓存
      if (config?.useCache) {
        const cacheKey = config.cacheKey || getCacheKey(url, config?.params);
        apiCache.set(cacheKey, data, config.cacheTTL);
      }

      return data;
    } catch (error) {
      return this.handleError(error, config);
    }
  }

  /**
   * POST请求
   */
  async post<T = unknown>(url: string, data?: unknown, config?: ApiRequestConfig): Promise<T> {
    try {
      const response: AxiosResponse<ApiResponse<T>> = await http.post(url, data, config);
      return this.handleResponse<T>(response, config);
    } catch (error) {
      return this.handleError(error, config);
    }
  }

  /**
   * PUT请求
   */
  async put<T = unknown>(url: string, data?: unknown, config?: ApiRequestConfig): Promise<T> {
    try {
      const response: AxiosResponse<ApiResponse<T>> = await http.put(url, data, config);
      return this.handleResponse<T>(response, config);
    } catch (error) {
      return this.handleError(error, config);
    }
  }

  /**
   * DELETE请求
   */
  async delete<T = unknown>(url: string, config?: ApiRequestConfig): Promise<T> {
    try {
      const response: AxiosResponse<ApiResponse<T>> = await http.delete(url, config);
      return this.handleResponse<T>(response, config);
    } catch (error) {
      return this.handleError(error, config);
    }
  }

  /**
   * PATCH请求
   */
  async patch<T = unknown>(url: string, data?: unknown, config?: ApiRequestConfig): Promise<T> {
    try {
      const response: AxiosResponse<ApiResponse<T>> = await http.patch(url, data, config);
      return this.handleResponse<T>(response, config);
    } catch (error) {
      return this.handleError(error, config);
    }
  }

  /**
   * 处理响应
   */
  private handleResponse<T>(response: AxiosResponse<ApiResponse<T>>, config?: ApiRequestConfig): T {
    const { data } = response;

    // 统一检查响应结构
    if (data && typeof data === 'object' && ('code' in data || 'data' in data)) {
      const apiResponse = data as ApiResponse<T>;

      // 检查业务状态码
      if (apiResponse.code !== undefined && apiResponse.code !== 200 && apiResponse.code !== 0) {
        const errorMessage = apiResponse.message || '请求失败';
        logger.error('API业务错误', new Error(errorMessage), { code: apiResponse.code, url: response.config.url });

        if (config?.showError !== false) {
          // 错误提示已在http拦截器中处理，这里不需要重复
        }

        throw new Error(errorMessage);
      }

      // 显示成功提示（使用动态导入避免循环依赖）
      if (config?.showSuccess && config?.successMessage) {
        // 使用setTimeout避免阻塞主流程
        setTimeout(() => {
          import('ant-design-vue').then((mod) => {
            mod.message.success(config.successMessage!);
          });
        }, 0);
      }

      return apiResponse.data;
    }

    // 如果响应数据就是结果本身，直接返回
    return data as T;
  }

  /**
   * 处理错误
   */
  private handleError(error: unknown, config?: ApiRequestConfig): never {
    try {
      const err = error as AxiosError;
      const isCanceled = !!err && (axios.isCancel(err) || err.code === 'ERR_CANCELED' || (typeof err.message === 'string' && (err.message.includes('canceled') || err.message.includes('aborted'))));

      const status = err?.response?.status;
      const url = err?.config?.url;
      const method = err?.config?.method?.toUpperCase();
      const msg = err?.message;

      if (isCanceled) {
        logger.info('请求已取消', { method, url, code: err?.code });
      } else {
        logger.error('API请求错误', error, { status, method, url, message: msg });
      }
    } catch {
      logger.error('API请求错误', error, { config });
    }

    // 错误已经在http拦截器中处理，这里直接抛出
    throw error;
  }
}

// 导出API服务实例
export const apiService = new ApiService();

// 导出默认实例（方便直接使用）
export default apiService;
