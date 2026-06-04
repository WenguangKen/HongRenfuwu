/**
 * 标签管理服务模块
 *
 * 负责系统标签的 CRUD 操作：
 * - 按类别查询标签（INFLUENCER/LIAISON/CONTENT 等）
 * - 创建/更新/删除标签
 * - 标签修复（补全缺失标签）
 *
 * @module tagService
 */
import axios from 'axios';

/* ========== 类型定义 ========== */

/** 系统标签实体 */
export interface SystemTag {
    id: number;                 // 标签 ID
    category: string;           // 标签类别（INFLUENCER/LIAISON/CONTENT/PAYMENT_TYPE 等）
    name: string;               // 标签名称
    description?: string;       // 标签描述
    backgroundColor: string;    // 背景色（HEX）
    borderColor: string;        // 边框色（HEX）
    textColor: string;          // 文字色（HEX）
    sortOrder: number;          // 排序序号
    enabled: boolean;           // 是否启用
    createdBy?: string;         // 创建人
    createdAt?: string;         // 创建时间
    updatedBy?: string;         // 更新人
    updatedAt?: string;         // 更新时间
    count?: number;             // 关联的红人数量（统计用，兼容旧字段）
    usageCount?: number;        // 关联的红人数量（后端返回字段名）
}

/** 创建/更新标签 DTO */
export interface CreateTagDto {
    category: string;           // 标签类别
    name: string;               // 标签名称
    description?: string;       // 标签描述
    backgroundColor?: string;   // 背景色
    borderColor?: string;       // 边框色
    textColor?: string;         // 文字色
    sortOrder?: number;         // 排序序号
    enabled?: boolean;          // 是否启用
    createdBy?: string;         // 创建人
    updatedBy?: string;         // 更新人
}

/* ========== HTTP 客户端配置 ========== */

/**
 * 标签服务专用 Axios 实例
 * 不使用 /api 前缀，直接访问红人服务 API
 */
const influencerHttp = axios.create({
    baseURL: '',
    timeout: 30000,
});

/**
 * 请求拦截器 —— 注入认证 Token
 * 兼容两种 Token 存储格式：
 * 1. localStorage['token'] —— 直接字符串
 * 2. localStorage['auth_token'] —— JSON 对象 { token: '...' }
 */
influencerHttp.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        } else {
            const raw = localStorage.getItem('auth_token');
            if (raw) {
                try {
                    const obj = JSON.parse(raw);
                    if (obj.token) {
                        config.headers.Authorization = `Bearer ${obj.token}`;
                    }
                } catch { /* Token 解析失败，静默跳过 */ }
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

/** 响应拦截器 —— 统一错误处理 */
influencerHttp.interceptors.response.use(
    (response) => response,
    (error) => {
        if (axios.isCancel(error)) return Promise.reject(error);
        const status = error?.response?.status;
        if (status === 401) {
            console.error('登录已过期');
            localStorage.removeItem('auth_token');
            setTimeout(() => {
                if (!window.location.pathname.startsWith('/user')) {
                    window.location.href = '/user/login';
                }
            }, 1000);
        }
        return Promise.reject(error);
    }
);

/** 标签 API 基础路径 */
const API_BASE = '/influencer-api/v1/influencers/tags';

/* ========== 服务方法 ========== */

/**
 * 解包后端响应 —— 兼容 { code, data, success } 包装格式和直接数据格式
 * 后端红人服务返回 { code: 200, data: T, success: true }，需提取 data 字段
 * 如果 success === false，抛出异常让调用者感知错误
 */
function unwrap<T>(responseData: any): T {
    if (responseData && typeof responseData === 'object' && 'data' in responseData) {
        // 检查后端是否返回了错误（success=false 时 data 为 null）
        if (responseData.success === false) {
            throw new Error(responseData.message || '请求失败');
        }
        return responseData.data as T;
    }
    return responseData as T;
}

export const tagService = {
    /**
     * 获取指定类别的标签列表
     * @param category - 标签类别（INFLUENCER/LIAISON/CONTENT/PAYMENT_TYPE 等）
     * @param enabledOnly - 是否仅返回启用的标签
     */
    async getTagsByCategory(category: string, enabledOnly: boolean = false): Promise<SystemTag[]> {
        const response = await influencerHttp.get(API_BASE, {
            params: { type: category, enabledOnly }
        });
        const result = unwrap<SystemTag[]>(response.data);
        return Array.isArray(result) ? result : [];
    },

    /** 获取单个标签详情 */
    async getTag(id: number): Promise<SystemTag> {
        const response = await influencerHttp.get(`${API_BASE}/${id}`);
        return unwrap<SystemTag>(response.data);
    },

    /** 创建新标签 */
    async createTag(dto: CreateTagDto): Promise<SystemTag> {
        const response = await influencerHttp.post(API_BASE, dto);
        return unwrap<SystemTag>(response.data);
    },

    /** 更新标签信息 */
    async updateTag(id: number, dto: CreateTagDto): Promise<SystemTag> {
        const response = await influencerHttp.put(`${API_BASE}/${id}`, dto);
        return unwrap<SystemTag>(response.data);
    },

    /** 删除标签 */
    async deleteTag(id: number): Promise<void> {
        await influencerHttp.delete(`${API_BASE}/${id}`);
    },

    /** 修复/补全缺失的系统默认标签 */
    async repairTags(): Promise<void> {
        await influencerHttp.post(`${API_BASE}/repair`);
    }
};

export default tagService;
