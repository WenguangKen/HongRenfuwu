/**
 * 内容管理服务模块
 *
 * 负责红人产出内容的管理操作：
 * - 内容 CRUD（创建/查询/更新/删除）
 * - 文件上传（视频/图片等）
 * - 内容审核
 * - 社交媒体互动数据更新
 * - 任务组管理
 * - 存储空间统计
 *
 * @module contentService
 */
import axios from 'axios';
import { message as antMsg } from 'ant-design-vue';

/* ========== HTTP 客户端配置 ========== */

/**
 * 内容服务专用 Axios 实例
 * 不使用 /api 前缀，直接访问红人服务 API
 */
const influencerHttp = axios.create({
    baseURL: '',
    timeout: 60000,
});

/** 请求拦截器 —— 注入认证 Token */
influencerHttp.interceptors.request.use(
    (config) => {
        const raw = localStorage.getItem('auth_token');
        if (raw) {
            try {
                const obj = JSON.parse(raw);
                const token = obj?.token || obj?.accessToken;
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
            } catch { /* Token 解析失败 */ }
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
            antMsg.error({ content: '登录已过期，请重新登录', key: 'auth_expired' });
            localStorage.removeItem('auth_token');
            setTimeout(() => {
                if (!window.location.pathname.startsWith('/user')) {
                    window.location.href = '/user/login';
                }
            }, 1000);
        } else if (typeof status === 'number' && status >= 500) {
            antMsg.error({ content: '服务器错误，请稍后再试', key: 'server_error' });
        }
        return Promise.reject(error);
    }
);

/** 内容 API 基础路径 */
const API_BASE = '/influencer-api/v1/contents';

/* ========== 类型定义 ========== */

/** 内容实体 DTO */
export interface ContentDto {
    id: number;                   // 内容 ID
    influencerId: number;         // 关联红人 ID
    influencerName?: string;      // 红人名称
    influencerEmail?: string;     // 红人邮箱
    title: string;                // 内容标题
    description?: string;         // 内容描述
    status: string;               // 状态（DRAFT/PENDING/APPROVED/REJECTED）
    platform?: string;            // 发布平台
    postType?: string;            // 内容类型（图片/视频/帖子）
    contentType?: string;         // 作品类型 (IG-ReelIG, TikTok video 等)
    postUrl?: string;             // 帖子链接
    filePath?: string;            // 文件存储路径
    fileName?: string;            // 文件名
    fileSize?: number;            // 文件大小（字节）
    fileType?: string;            // 文件 MIME 类型
    thumbnailPath?: string;       // 缩略图存储路径
    duration?: number;            // 视频时长（秒）
    width?: number;               // 宽度（像素）
    height?: number;              // 高度（像素）
    previewUrl?: string;          // 预览 URL（签名链接）
    thumbnailUrl?: string;        // 缩略图 URL（签名链接）
    reviewerId?: number;          // 审核人 ID
    reviewedAt?: string;          // 审核时间
    reviewNote?: string;          // 审核备注
    createdAt?: string;           // 创建时间
    updatedAt?: string;           // 更新时间
    taskGroupId?: string;         // 任务组 ID（用于批量任务关联）
    productSku?: string;          // 产品 SKU
    variantTitle?: string;        // 产品变体名称
    orderNo?: string;             // 关联订单号
    owner?: string;               // 负责人
    tags?: string[];              // 标签名称列表
    tagIds?: number[];            // 标签 ID 列表
    isCommercial?: boolean;       // 是否为商业内容
    publishDate?: string;         // 发帖时间
    views?: number;               // 播放/浏览量
    likes?: number;               // 点赞数
    comments?: number;            // 评论数
    shares?: number;              // 分享数
    saves?: number;               // 收藏数
    defaultHandle?: string;       // 红人默认社媒账号名
    contactPersonName?: string;   // 联络人名称
    ownerName?: string;           // 负责人名称
    remark?: string;              // 备注
    contentGroupIndex?: number;   // 内容项目分组索引
}

/** 创建内容请求 DTO */
export interface ContentCreateDto {
    influencerId: number;         // 关联红人 ID
    title: string;                // 内容标题
    description?: string;         // 描述
    platform?: string;            // 发布平台
    postType?: string;            // 内容类型
    contentType?: string;         // 作品类型
    postUrl?: string;             // 帖子链接
    owner?: string;               // 负责人
    orderNo?: string;             // 关联订单号
    productSku?: string;          // 产品 SKU
    taskGroupId?: string;         // 任务组 ID
    status?: string;              // 初始状态
    tagIds?: number[];            // 标签 ID 列表
    isCommercial?: boolean;       // 是否商业内容
    publishDate?: string;         // 发帖时间
    remark?: string;              // 备注
    contentGroupIndex?: number;   // 内容项目分组索引
}

/** 分页查询结果通用类型 */
export interface PageResult<T> {
    content: T[];                 // 当前页数据
    totalElements: number;        // 总记录数
    totalPages: number;           // 总页数
    number: number;               // 当前页码（0-based）
    size: number;                 // 每页大小
}

/* ========== API 方法 ========== */

/**
 * 分页查询内容列表
 * 支持多条件筛选：状态、红人、任务组、SKU、标签等
 */
export async function getContents(params: {
    status?: string;
    influencerId?: number;
    influencerEmail?: string;
    taskGroupId?: string;
    orderNo?: string;
    isCommercial?: boolean;
    defaultHandle?: string;
    contactPersonName?: string;
    owner?: string;
    productSku?: string;
    contentType?: string;
    tagIds?: number[];
    reviewerId?: number;
    startTime?: string;
    endTime?: string;
    publishStartDate?: string;
    publishEndDate?: string;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: string;
    grouped?: boolean;
}): Promise<PageResult<ContentDto>> {
    const res = await influencerHttp.get(API_BASE, { params });
    return res.data;
}

/** 获取内容详情 */
export async function getContent(id: number): Promise<ContentDto> {
    const res = await influencerHttp.get(`${API_BASE}/${id}`);
    return res.data;
}

/** 创建内容记录 */
export async function createContent(data: ContentCreateDto): Promise<ContentDto> {
    const res = await influencerHttp.post(API_BASE, data);
    return res.data;
}

/** 更新内容信息 */
export async function updateContent(id: number, data: Partial<ContentCreateDto>): Promise<ContentDto> {
    const res = await influencerHttp.put(`${API_BASE}/${id}`, data);
    return res.data;
}

/** 批量更新内容标签 */
export async function batchUpdateTags(ids: number[], tagIds: number[]): Promise<any> {
    const res = await influencerHttp.post(`${API_BASE}/batch-tags`, { ids, tagIds });
    return res.data;
}

/** 删除单个内容 */
export async function deleteContent(id: number): Promise<void> {
    await influencerHttp.delete(`${API_BASE}/${id}`);
}

/**
 * 上传文件到指定内容
 * 大于 20MB 的文件自动进行分片上传以绕过外部网关大小限制
 * @param contentId - 内容 ID
 * @param file - 要上传的文件对象
 * @param thumbnailBlob - 前端生成的缩略图
 * @param width - 宽度
 * @param height - 高度
 * @param duration - 视频时长
 * @param onUploadProgress - 进度回调
 */
export async function uploadFile(
    contentId: number, 
    file: File, 
    thumbnailBlob?: Blob | null,
    width?: number,
    height?: number,
    duration?: number,
    onUploadProgress?: (progressEvent: any) => void
): Promise<ContentDto> {
    const CHUNK_SIZE = 20 * 1024 * 1024; // 20MB
    if (file.size > CHUNK_SIZE) {
        return uploadFileChunked(contentId, file, thumbnailBlob, width, height, duration, onUploadProgress);
    }

    const formData = new FormData();
    formData.append('file', file);
    if (thumbnailBlob) {
        formData.append('thumbnail', thumbnailBlob, 'thumbnail.jpg');
    }
    if (width !== undefined) formData.append('width', width.toString());
    if (height !== undefined) formData.append('height', height.toString());
    if (duration !== undefined) formData.append('duration', duration.toString());
    
    const res = await influencerHttp.post(`${API_BASE}/${contentId}/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 600000, // 10 分钟超时，适配大视频/图片上传
        onUploadProgress
    });
    return res.data;
}

/** 分片上传文件核心逻辑 */
async function uploadFileChunked(
    contentId: number, 
    file: File, 
    thumbnailBlob?: Blob | null,
    width?: number,
    height?: number,
    duration?: number,
    onUploadProgress?: (progressEvent: any) => void
): Promise<ContentDto> {
    const CHUNK_SIZE = 20 * 1024 * 1024; // 20MB
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    const uploadId = Date.now().toString(36) + Math.random().toString(36).substring(2);
    let uploadedBytes = 0;
    
    for (let i = 0; i < totalChunks; i++) {
        const start = i * CHUNK_SIZE;
        const end = Math.min(start + CHUNK_SIZE, file.size);
        const chunk = file.slice(start, end);
        
        const chunkFormData = new FormData();
        chunkFormData.append('chunk', chunk, 'chunk');
        chunkFormData.append('uploadId', uploadId);
        chunkFormData.append('chunkIndex', i.toString());
        chunkFormData.append('totalChunks', totalChunks.toString());
        
        await influencerHttp.post(`${API_BASE}/${contentId}/upload-chunk`, chunkFormData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            timeout: 120000,
            onUploadProgress: (progressEvent) => {
                if (onUploadProgress && progressEvent.loaded) {
                    const currentTotalLoaded = uploadedBytes + progressEvent.loaded;
                    onUploadProgress({ loaded: currentTotalLoaded, total: file.size });
                }
            }
        });
        uploadedBytes += chunk.size;
    }
    
    // 通知进度已达接近完成状态，等待后端合并完成
    if (onUploadProgress) {
        onUploadProgress({ loaded: file.size - 1, total: file.size });
    }
    
    const mergeFormData = new FormData();
    mergeFormData.append('uploadId', uploadId);
    mergeFormData.append('fileName', file.name);
    mergeFormData.append('fileType', file.type || 'application/octet-stream');
    mergeFormData.append('totalSize', file.size.toString());
    
    if (thumbnailBlob) {
        mergeFormData.append('thumbnail', thumbnailBlob, 'thumbnail.jpg');
    }
    if (width !== undefined) mergeFormData.append('width', width.toString());
    if (height !== undefined) mergeFormData.append('height', height.toString());
    if (duration !== undefined) mergeFormData.append('duration', duration.toString());
    
    const res = await influencerHttp.post(`${API_BASE}/${contentId}/upload-merge`, mergeFormData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 600000 // 合并存入 MinIO 可能较久
    });
    
    if (onUploadProgress) {
        onUploadProgress({ loaded: file.size, total: file.size });
    }
    
    return res.data;
}

/** 重命名内容文件 */
export async function renameFile(contentId: number, fileName: string): Promise<ContentDto> {
    const res = await influencerHttp.put(`${API_BASE}/${contentId}/rename`, { fileName });
    return res.data;
}

/**
 * 审核内容
 * @param contentId - 内容 ID
 * @param approved - true=通过, false=驳回
 * @param note - 审核备注
 */
export async function reviewContent(contentId: number, approved: boolean, note?: string): Promise<ContentDto> {
    const res = await influencerHttp.post(`${API_BASE}/${contentId}/review`, { approved, note });
    return res.data;
}

/** 获取内容预览 URL（签名链接，有效期有限） */
export async function getPreviewUrl(contentId: number): Promise<string> {
    const res = await influencerHttp.get(`${API_BASE}/${contentId}/preview-url`);
    return res.data.url;
}

/** 获取内容缩略图 URL */
export async function getThumbnailUrl(contentId: number): Promise<string> {
    const res = await influencerHttp.get(`${API_BASE}/${contentId}/thumbnail-url`);
    return res.data.url;
}

/**
 * 更新内容的社交媒体互动数据
 * 用于手动录入或同步平台统计数据
 */
export async function updateSocialMetrics(contentId: number, metrics: {
    views?: number;       // 播放/浏览量
    likes?: number;       // 点赞数
    comments?: number;    // 评论数
    shares?: number;      // 分享数
    saves?: number;       // 收藏数
}): Promise<ContentDto> {
    const res = await influencerHttp.put(`${API_BASE}/${contentId}/social-metrics`, metrics);
    return res.data;
}

/** 按任务组 ID 批量删除关联内容 */
export async function deleteTaskGroup(taskGroupId: string): Promise<void> {
    await influencerHttp.delete(`${API_BASE}/group/${taskGroupId}`);
}

/** 批量删除内容 */
export async function batchDeleteContents(ids: number[]): Promise<void> {
    return influencerHttp.delete(`${API_BASE}/batch`, { data: ids });
}

/** 获取存储空间统计（已用/总量/类型） */
export async function getStorageStats(): Promise<{ used: number; total: number; type: string }> {
    const res = await influencerHttp.get(`${API_BASE}/storage/stats`);
    return res.data;
}

/* ========== 默认导出 ========== */

export default {
    getContents,
    getContent,
    createContent,
    updateContent,
    deleteContent,
    deleteTaskGroup,
    batchDeleteContents,
    uploadFile,
    renameFile,
    reviewContent,
    getPreviewUrl,
    getThumbnailUrl,
    getStorageStats,
    batchUpdateTags,
    updateSocialMetrics
};
