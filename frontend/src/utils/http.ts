import axios, { AxiosError, type CancelTokenSource } from 'axios';
import type { AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';
import { logger } from './logger';
import { ngrokRequestHeaders } from './ngrok';

/** 扩展 Axios 请求配置，支持自定义控制字段 */
export interface CustomAxiosConfig {
  /** 跳过请求取消机制 */
  skipCancel?: boolean;
  /** 抑制全局错误提示 */
  showError?: boolean;
}

const baseURL = (import.meta.env?.VITE_API_BASE_URL as string) || '/api';

const http: AxiosInstance = axios.create({
  baseURL,
  timeout: 60000,
  withCredentials: false,
});

// 请求取消token映射
const cancelTokenMap = new Map<string, CancelTokenSource>();

/**
 * 取消指定URL的请求
 */
export const cancelRequest = (url: string) => {
  const source = cancelTokenMap.get(url);
  if (source) {
    source.cancel('Request cancelled');
    cancelTokenMap.delete(url);
  }
};

/**
 * 取消所有pending请求
 */
export const cancelAllRequests = () => {
  cancelTokenMap.forEach((source) => {
    source.cancel('All requests cancelled');
  });
  cancelTokenMap.clear();
};

http.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  try {
    const ngrokHeaders = ngrokRequestHeaders();
    if (Object.keys(ngrokHeaders).length) {
      Object.assign(config.headers, ngrokHeaders);
    }

    // --- 自动去除请求参数中的前后空格 ---
    const trimStrings = (obj: any): any => {
      if (obj === null || obj === undefined) return obj;
      if (typeof obj === 'string') return obj.trim();
      if (Array.isArray(obj)) return obj.map(item => trimStrings(item));
      if (typeof obj === 'object') {
        if (obj instanceof FormData || obj instanceof Blob || obj instanceof File || obj instanceof Date || obj instanceof RegExp) {
          return obj;
        }
        const newObj: any = {};
        for (const key in obj) {
          if (Object.prototype.hasOwnProperty.call(obj, key)) {
            // 密码字段不去除空格
            if (key.toLowerCase().includes('password')) {
              newObj[key] = obj[key];
            } else {
              newObj[key] = trimStrings(obj[key]);
            }
          }
        }
        return newObj;
      }
      return obj;
    };

    if (config.params) {
      config.params = trimStrings(config.params);
    }
    if (config.data && typeof config.data !== 'string' && !(config.data instanceof FormData)) {
      config.data = trimStrings(config.data);
    }
    // ------------------------------------

    const userStore = useUserStore();
    /**
     * 从 localStorage 读取 Token（带过期检查）
     * 注意：此逻辑与 stores/user.ts 中的 readAuth 保持一致
     */
    const readAuthFromStorage = (): string | null => {
      const raw = localStorage.getItem('auth_token');
      if (!raw) return null;
      try {
        const obj = JSON.parse(raw);
        const expiresAt = Number(obj?.expiresAt) || 0;
        if (expiresAt && Date.now() > expiresAt) {
          localStorage.removeItem('auth_token');
          return null;
        }
        return String(obj?.token || '');
      } catch {
        return null;
      }
    };
    const token = userStore.token || (String(import.meta.env?.VITE_DISABLE_TOKEN_LOCALSTORAGE) === 'true' ? null : readAuthFromStorage());

    // Add X-User-Name for activity log tracking
    const userName = userStore.userInfo?.username || '';
    const headers = config.headers || {};
    if (userName) {
      if (typeof headers.set === 'function') {
        headers.set('X-User-Name', encodeURIComponent(userName));
      } else {
        (headers as Record<string, string>)['X-User-Name'] = encodeURIComponent(userName);
      }
    }

    if (token) {
      const authValue = `Bearer ${token}`;
      if (typeof headers.set === 'function') {
        headers.set('Authorization', authValue);
      } else {
        (headers as Record<string, string>)['Authorization'] = authValue;
      }
    }
    config.headers = headers;

    // 检查是否跳过请求取消机制
    const customConfig = config as InternalAxiosRequestConfig & CustomAxiosConfig;
    const skipCancel = customConfig.skipCancel === true;
    // 写操作（POST/PUT/DELETE/PATCH）不参与取消机制，防止双击按钮导致请求被误杀
    const method = config.method?.toUpperCase() || 'GET';
    const isWriteOp = ['POST', 'PUT', 'DELETE', 'PATCH'].includes(method);

    if (!skipCancel && !isWriteOp) {
      // 仅对 GET 请求保留取消重复请求机制
      const source = axios.CancelToken.source();
      config.cancelToken = source.token;

      const requestKey = `${method}_${config.url}_${JSON.stringify(config.params || {})}`;

      const existingSource = cancelTokenMap.get(requestKey);
      if (existingSource) {
        existingSource.cancel('Request canceled due to new request');
        cancelTokenMap.delete(requestKey);
      }

      cancelTokenMap.set(requestKey, source);
    }

    // 请求完成后自动清理（在响应拦截器中处理）
  } catch (error) {
    // 请求拦截器错误，静默处理（避免影响正常请求）
    logger.error('Request interceptor error', error);
  }
  return config;
});


http.interceptors.response.use(
  (resp: AxiosResponse) => {
    // 请求成功，清理cancelToken
    const config = resp.config;
    const requestKey = `${config.method?.toUpperCase()}_${config.url}_${JSON.stringify(config.params || {})}`;
    cancelTokenMap.delete(requestKey);

    // 处理自动刷新的Token
    const newToken = resp.headers['x-new-token'];
    if (newToken) {
      const userStore = useUserStore();
      userStore.token = newToken;
      // 更新localStorage中的token
      const authRaw = localStorage.getItem('auth_token');
      if (authRaw) {
        try {
          const authObj = JSON.parse(authRaw);
          authObj.token = newToken;
          // 延长过期时间（6小时）
          authObj.expiresAt = Date.now() + 6 * 60 * 60 * 1000;
          localStorage.setItem('auth_token', JSON.stringify(authObj));
        } catch {
          // 忽略解析错误
        }
      }
      logger.debug('Token已自动刷新');
    }

    return resp;
  },
  (error: AxiosError) => {
    // 请求失败，清理cancelToken
    if (error.config) {
      const config = error.config;
      const requestKey = `${config.method?.toUpperCase()}_${config.url}_${JSON.stringify(config.params || {})}`;
      cancelTokenMap.delete(requestKey);
    }

    // 如果是取消请求，不显示错误
    if (axios.isCancel(error)) {
      return Promise.reject(error);
    }

    const status = error?.response?.status;
    const data = error?.response?.data as Record<string, string> | undefined;
    const customConfig = error.config as (InternalAxiosRequestConfig & CustomAxiosConfig) | undefined;
    const suppress = customConfig?.showError === false;

    // 使用统一的错误消息键，防止堆叠
    const ERROR_MESSAGE_KEY = 'global_error_message';

    if (status === 401) {
      if (!suppress) {
        message.error({ content: '登录已过期，请重新登录', key: ERROR_MESSAGE_KEY });
      }
      // 清除token并跳转登录
      const userStore = useUserStore();
      userStore.logout();
      // 延迟跳转，避免在路由守卫中处理
      setTimeout(() => {
        const path = window.location.pathname || '';
        if (!path.startsWith('/user')) {
          window.location.href = '/user/login';
        }
      }, 1000);
    } else if (typeof status === 'number' && status >= 500) {
      if (!suppress) {
        message.error({ content: '服务器错误，请稍后再试', key: ERROR_MESSAGE_KEY });
      }
    } else if (status === 403) {
      // 403 错误只显示消息，不自动跳转
      // 让各个业务模块自己决定如何处理权限问题
      if (!suppress) {
        const msg = data?.message || data?.msg || '没有权限访问该资源';
        message.error({ content: msg, key: ERROR_MESSAGE_KEY });
      }
    } else if (typeof status === 'number' && status >= 400) {
      // 客户端错误（4xx），显示具体错误信息
      const msg = data?.message || data?.msg || error?.message || '请求失败';
      if (!suppress) {
        message.error({ content: msg, key: ERROR_MESSAGE_KEY });
      }
    } else if (!axios.isCancel(error)) {
      // 网络错误等其他错误且非手动取消
      const msg = error?.message || '网络错误，请检查网络连接';
      if (!suppress) {
        message.error({ content: msg, key: ERROR_MESSAGE_KEY });
      }
    }

    return Promise.reject(error);
  }
);

export default http;
