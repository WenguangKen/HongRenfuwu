/**
 * 红人服务模块
 *
 * 负责与后端红人管理 API 进行通信，包括：
 * - 红人 CRUD（创建/查询/更新/删除）
 * - 地址管理
 * - 社交媒体账号管理
 * - 打款信息管理
 * - 佣金结算与提现
 * - 付费合作记录
 * - 导入/导出
 *
 * @module influencerService
 */
import axios from 'axios';
import { message as antMsg } from 'ant-design-vue';
import type {
    Influencer,
    InfluencerCreateDto,
    InfluencerFilterDto,
    BatchWorkflowDto,
    BatchWorkflowResult,
    DormancyCheckResult,
    Address,
    PaymentRecord,
} from '@/types/influencer';

/* ========== HTTP 客户端配置 ========== */

/**
 * 红人服务专用 Axios 实例
 * - baseURL 指向红人微服务的 API 前缀
 * - 独立于全局 /api 的 baseURL，避免路由冲突
 */
const influencerHttp = axios.create({
    baseURL: '/influencer-api/v1/influencer',
    timeout: 60000,
});

/**
 * 请求拦截器 —— 自动注入认证信息
 * 从 localStorage 中读取 JWT Token 和当前用户名，
 * 并添加到请求头中，供后端鉴权和操作日志使用
 */
influencerHttp.interceptors.request.use((config) => {
    // 注入 JWT Token
    const raw = localStorage.getItem('auth_token');
    if (raw) {
        try {
            const obj = JSON.parse(raw);
            const token = obj?.token || obj?.accessToken;
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch {
            // Token 解析失败，静默跳过
        }
    }

    // 注入当前操作人用户名（用于后端记录操作日志）
    const userInfoRaw = localStorage.getItem('userInfo') || localStorage.getItem('user_info');
    if (userInfoRaw) {
        try {
            const userObj = JSON.parse(userInfoRaw);
            const userName = userObj.name || userObj.username || userObj.nickname;
            if (userName) {
                config.headers['X-User-Name'] = encodeURIComponent(userName);
            }
        } catch {
            // 用户信息解析失败，静默跳过
        }
    }
    return config;
});

/**
 * 响应拦截器 —— 自动提取 response.data
 * 后端返回格式为 { data: ... }，此拦截器直接返回 data 层，
 * 使业务代码无需手动解包
 */
influencerHttp.interceptors.response.use(
    (response) => response.data,
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

/**
 * 通用红人 API Axios 实例（不自动解包 response.data）
 * - baseURL 指向 /influencer-api，适用于 settings、storage 等非红人 CRUD 接口
 * - 共享 token 注入和错误处理逻辑
 */
const influencerApiHttp = axios.create({
    baseURL: '/influencer-api',
    timeout: 30000,
});

// 复用相同的请求拦截器
influencerApiHttp.interceptors.request.use((config) => {
    const raw = localStorage.getItem('auth_token');
    if (raw) {
        try {
            const obj = JSON.parse(raw);
            const token = obj?.token || obj?.accessToken;
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch { /* ignore */ }
    }
    const userInfoRaw = localStorage.getItem('userInfo') || localStorage.getItem('user_info');
    if (userInfoRaw) {
        try {
            const userObj = JSON.parse(userInfoRaw);
            const userName = userObj.name || userObj.username || userObj.nickname;
            if (userName) {
                config.headers['X-User-Name'] = encodeURIComponent(userName);
            }
        } catch { /* ignore */ }
    }
    return config;
});

// 复用相同的错误拦截器（不自动解包 response.data）
influencerApiHttp.interceptors.response.use(
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

export { influencerApiHttp };

/** API 路径前缀（空字符串表示使用 baseURL 本身） */
const BASE_URL = "";

/* ========== 类型定义 ========== */

/** 分页响应通用类型 */
interface PagedResponse<T> {
    content: T[];          // 当前页数据列表
    totalElements: number; // 总记录数
    totalPages: number;    // 总页数
    size: number;          // 每页大小
    number: number;        // 当前页码（0-based）
}

/* ========== 服务方法定义 ========== */

export const influencerService = {

    /* ---------- 红人 CRUD ---------- */

    /** 创建红人，返回新建红人的 ID */
    create(data: InfluencerCreateDto): Promise<number> {
        return influencerHttp.post(BASE_URL, data) as Promise<number>;
    },

    /** 批量导入红人（JSON 数组格式） */
    batchImport(data: any[]): Promise<number> {
        return influencerHttp.post<number>(`${BASE_URL}/batch-import`, data) as unknown as Promise<number>;
    },

    /** 通过 Excel 文件导入红人 */
    importInfluencers(formData: FormData): Promise<{ successCount: number; failCount: number; errorMessages: string[] }> {
        return influencerHttp.post(`${BASE_URL}/import`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        }) as unknown as Promise<{ successCount: number; failCount: number; errorMessages: string[] }>;
    },

    /** 分析导入文件（预检查），返回 jobId 和 fileKey */
    analyzeImport(formData: FormData): Promise<{ jobId: number; fileKey: string; message: string }> {
        return influencerHttp.post(`${BASE_URL}/import/analyze`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        }) as unknown as Promise<{ jobId: number; fileKey: string; message: string }>;
    },

    /** 确认导入（在 analyzeImport 之后调用） */
    confirmImport(fileKey: string, originalFilename: string, stage?: string): Promise<{ jobId: number; message: string }> {
        return influencerHttp.post(`${BASE_URL}/import/confirm`, { fileKey, originalFilename, stage }) as unknown as Promise<{ jobId: number; message: string }>;
    },

    /** 查询导入任务状态 */
    getImportStatus(jobId: number): Promise<any> {
        return influencerHttp.get(`${BASE_URL}/import/status/${jobId}`) as unknown as Promise<any>;
    },

    /** 更新红人信息，operator 为操作人姓名（用于日志记录） */
    update(data: Partial<Influencer>, operator?: string): Promise<void> {
        const config = operator ? { headers: { 'X-User-Name': encodeURIComponent(operator) } } : {};
        return influencerHttp.put(BASE_URL, data, config) as Promise<void>;
    },

    /** 删除单个红人 */
    delete(id: number): Promise<void> {
        return influencerHttp.delete(`${BASE_URL}/${id}`) as Promise<void>;
    },

    /** 批量删除红人 */
    batchDelete(ids: number[]): Promise<void> {
        return influencerHttp.delete(`${BASE_URL}/batch`, { data: ids }) as Promise<void>;
    },

    /** 获取红人变更日志 */
    getLogs(id: number): Promise<any[]> {
        return influencerHttp.get(`${BASE_URL}/${id}/logs`) as unknown as Promise<any[]>;
    },

    /** 获取红人沟通记录（按时间倒序） */
    getCommunicationLogs(influencerId: number): Promise<any[]> {
        return influencerHttp.get(`${BASE_URL}/${influencerId}/communication-logs`) as unknown as Promise<any[]>;
    },

    /** 新增红人沟通记录（支持多张图片） */
    addCommunicationLog(influencerId: number, content: string, images?: File[]): Promise<any> {
        const formData = new FormData();
        if (content) formData.append('content', content);
        if (images && images.length > 0) {
            images.forEach(file => formData.append('images', file));
        }
        return influencerHttp.post(`${BASE_URL}/${influencerId}/communication-logs`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            timeout: 120000
        }) as Promise<any>;
    },

    /** 分页查询红人列表（支持多条件筛选） */
    getList(params: InfluencerFilterDto): Promise<PagedResponse<Influencer>> {
        return influencerHttp.post(`${BASE_URL}/search`, params) as Promise<PagedResponse<Influencer>>;
    },

    /** 获取受筛选条件影响的各状态统计数量 */
    getStatusCounts(params: InfluencerFilterDto): Promise<Record<string, number>> {
        return influencerHttp.post(`${BASE_URL}/status-counts/search`, params) as Promise<Record<string, number>>;
    },

    /** 合并查询：列表数据 + 状态统计（减少一次 HTTP 请求和 Specification 构建） */
    getListWithCounts(params: InfluencerFilterDto): Promise<{ list: PagedResponse<Influencer>; statusCounts: Record<string, number> }> {
        return influencerHttp.post(`${BASE_URL}/search-with-counts`, params) as Promise<{ list: PagedResponse<Influencer>; statusCounts: Record<string, number> }>;
    },

    /**
     * 获取红人详情（含标签完整信息）
     * 后端返回格式包含 influencer / tagIds / tagNames / tags 四个字段，
     * 此方法将它们合并为单一 Influencer 对象返回
     */
    async getDetail(id: number): Promise<Influencer> {
        const response = await influencerHttp.get<{
            influencer: Influencer,
            tagIds: number[],
            tagNames: string[],
            tags: Array<{ id: number; name: string; backgroundColor: string; borderColor: string; textColor: string }>
        }>(`${BASE_URL}/${id}`);
        const data = response as any;
        if (data.influencer) {
            return {
                ...data.influencer,
                tags: data.tags || [],
                tagNames: data.tagNames || [],
                tagIds: data.tagIds || []
            };
        }
        return data as Influencer;
    },

    /* ---------- 状态流转 ---------- */

    /** 批量变更红人状态（待联系 → 合作中 等） */
    batchChangeStatus(data: BatchWorkflowDto): Promise<BatchWorkflowResult> {
        return influencerHttp.post<BatchWorkflowResult>(`${BASE_URL}/workflow/batch-status-change`, data) as unknown as Promise<BatchWorkflowResult>;
    },

    /* ---------- 地址管理 ---------- */

    /** 获取红人的收货地址列表 */
    getAddresses(influencerId: number) {
        return influencerHttp.get<Address[]>(`${BASE_URL}/${influencerId}/addresses`);
    },

    /** 新增收货地址 */
    addAddress(influencerId: number, data: Partial<Address>) {
        return influencerHttp.post(`${BASE_URL}/${influencerId}/addresses`, data);
    },

    /** 更新收货地址 */
    updateAddress(influencerId: number, addressId: number, data: Partial<Address>) {
        return influencerHttp.put(`${BASE_URL}/${influencerId}/addresses/${addressId}`, data);
    },

    /** 删除收货地址 */
    deleteAddress(influencerId: number, addressId: number) {
        return influencerHttp.delete(`${BASE_URL}/${influencerId}/addresses/${addressId}`);
    },

    /* ---------- 社交媒体账号管理 ---------- */

    /** 获取红人关联的社交媒体账号列表 */
    getSocialMedias(influencerId: number) {
        return influencerHttp.get<any[]>(`${BASE_URL}/${influencerId}/social-medias`);
    },

    /** 新增社交媒体账号 */
    addSocialMedia(influencerId: number, data: any) {
        return influencerHttp.post(`${BASE_URL}/${influencerId}/social-medias`, data);
    },

    /** 更新社交媒体账号 */
    updateSocialMedia(socialMediaId: number, data: any) {
        return influencerHttp.put(`${BASE_URL}/social-medias/${socialMediaId}`, data);
    },

    /** 删除社交媒体账号 */
    deleteSocialMedia(socialMediaId: number) {
        return influencerHttp.delete(`${BASE_URL}/social-medias/${socialMediaId}`);
    },

    /* ---------- 打款信息管理 ---------- */

    /** 获取打款信息（银行账户/PayPal 等） */
    getPaymentInfo(influencerId: number) {
        return influencerHttp.get<any>(`${BASE_URL}/${influencerId}/payment-info`);
    },

    /** 保存/更新打款信息 */
    savePaymentInfo(influencerId: number, data: any) {
        return influencerHttp.post<any>(`${BASE_URL}/${influencerId}/payment-info`, data);
    },

    /** 获取历史打款记录 */
    getPayments(influencerId: number) {
        return influencerHttp.get<PaymentRecord[]>(`${BASE_URL}/${influencerId}/payments`);
    },

    /* ---------- 休眠检测 ---------- */

    /** 触发全局休眠检查（dryRun=true 为预检模式） */
    triggerDormancyCheck(dryRun: boolean = true) {
        return influencerHttp.post<string[]>(`${BASE_URL}/settings/dormancy-check`, null, {
            params: { dryRun }
        });
    },

    /** 检查单个红人是否满足休眠条件 */
    checkDormancy(id: number): Promise<DormancyCheckResult> {
        return influencerHttp.get<DormancyCheckResult>(`${BASE_URL}/${id}/dormancy-check`) as unknown as Promise<DormancyCheckResult>;
    },

    /* ---------- 标签管理 ---------- */

    /** 获取标签列表（默认为红人标签类别） */
    getTags(category: string = 'INFLUENCER'): Promise<any[]> {
        return influencerHttp.get<any[]>(`../influencers/tags`, { params: { type: category } }) as unknown as Promise<any[]>;
    },

    /* ---------- 批量操作 ---------- */

    /**
     * 批量更新红人字段
     * @param field - 操作类型：'ownerId' | 'commissionRate' | 'replaceTags' | 'appendTags' 等
     * @param value - 新值
     * @param removeTagIds - 仅 replaceTags 操作时使用，指定要移除的标签 ID
     */
    batchUpdate(
        data: { ids: number[]; field: string; value: any; removeTagIds?: number[] },
        operator?: string
    ): Promise<void> {
        const config = operator ? { headers: { 'X-User-Name': encodeURIComponent(operator) } } : {};
        return influencerHttp.post(`${BASE_URL}/batch-update`, data, config) as Promise<void>;
    },

    /* ---------- 佣金结算与提现 ---------- */

    /** 获取提现记录（支持按红人 ID 或全部查询） */
    getPayouts(params: { influencerId?: number; status?: string; page?: number; size?: number }): Promise<any> {
        if (params.influencerId) {
            return influencerHttp.get(`../commission/payouts/${params.influencerId}`) as unknown as Promise<any>;
        }
        return influencerHttp.get(`../commission/payouts`, { params }) as unknown as Promise<any>;
    },

    /** 获取佣金余额列表（分页） */
    getBalances(params: { page: number; size: number; influencer?: string; email?: string; discountCode?: string }): Promise<any> {
        return influencerHttp.get(`../commission/balances`, { params }) as unknown as Promise<any>;
    },

    /** 批量创建提现申请 */
    batchCreatePayout(data: Array<{ influencerId: number; amount: number; remark?: string }>): Promise<any> {
        return influencerHttp.post(`../commission/batch-payouts`, data) as unknown as Promise<any>;
    },

    /** 创建单个提现申请 */
    createPayout(data: { influencerId: number; amount: number; remark?: string }): Promise<any> {
        return influencerHttp.post(`../commission/payout`, data) as unknown as Promise<any>;
    },

    /** 审核提现申请（通过/驳回） */
    auditPayout(id: number, action: 'approve' | 'reject', remark: string, paymentData?: any): Promise<any> {
        return influencerHttp.post(`../commission/payout/${id}/audit`, { action, remark, ...paymentData }) as unknown as Promise<any>;
    },

    /** 确认提现完成（上传凭证等） */
    confirmPayout(id: number, data: any): Promise<any> {
        return influencerHttp.post(`../commission/payout/${id}/confirm`, data) as unknown as Promise<any>;
    },

    /** 驳回提现申请 */
    rejectPayout(id: number, remark: string): Promise<any> {
        return influencerHttp.post(`../commission/payout/${id}/reject`, { remark }) as unknown as Promise<any>;
    },

    /* ---------- 付费合作记录 ---------- */

    /** 获取红人的付费合作记录列表 */
    getCooperations(influencerId: number): Promise<any[]> {
        return influencerHttp.get<any[]>(`${BASE_URL}/${influencerId}/cooperations`) as unknown as Promise<any[]>;
    },

    /** 新增付费合作记录 */
    addCooperation(influencerId: number, data: any): Promise<any> {
        return influencerHttp.post(`${BASE_URL}/${influencerId}/cooperations`, data) as unknown as Promise<any>;
    },

    /** 更新付费合作记录 */
    updateCooperation(id: number, data: any): Promise<any> {
        return influencerHttp.put(`${BASE_URL}/cooperations/${id}`, data) as unknown as Promise<any>;
    },

    /** 删除付费合作记录 */
    deleteCooperation(id: number): Promise<any> {
        return influencerHttp.delete(`${BASE_URL}/cooperations/${id}`) as unknown as Promise<any>;
    },

    /* ---------- 文件上传 ---------- */

    /** 上传佣金凭证文件，返回文件 URL 和 fileKey */
    uploadVoucher(file: File, fileName?: string): Promise<{ success: boolean; message?: string; data: { url: string; fileKey: string; fileName: string } }> {
        const formData = new FormData();
        formData.append('file', file);
        if (fileName) {
            formData.append('fileName', fileName);
        }
        return influencerHttp.post('../commission/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            timeout: 300000, // 5 分钟超时，适配大文件凭证上传
        }) as unknown as Promise<{ success: boolean; message?: string; data: { url: string; fileKey: string; fileName: string } }>;
    },

    /** 获取佣金订单列表（分页） */
    getCommissionOrders(params: Record<string, any>): Promise<any> {
        return influencerHttp.get('../commission/orders', { params }) as unknown as Promise<any>;
    },

    /* ---------- 红人爬取任务 ---------- */

    /** 获取所有爬取任务 */
    getCrawlTasks(): Promise<any[]> {
        return influencerHttp.get('crawl/tasks') as unknown as Promise<any[]>;
    },

    /** 创建爬取任务 */
    createCrawlTask(data: any): Promise<any> {
        return influencerHttp.post('crawl/tasks', data) as unknown as Promise<any>;
    },

    /** 删除爬取任务 */
    deleteCrawlTask(id: number): Promise<any> {
        return influencerHttp.delete(`crawl/tasks/${id}`) as unknown as Promise<any>;
    },

    /** 触发/开始爬取任务 */
    startCrawlTask(id: number): Promise<any> {
        return influencerHttp.post(`crawl/tasks/${id}/start`) as unknown as Promise<any>;
    },
};

export default influencerService;
