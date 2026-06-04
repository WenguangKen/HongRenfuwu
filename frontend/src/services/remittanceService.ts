import axios from 'axios';
import { message as antMsg } from 'ant-design-vue';
import type {
    RemittanceTask,
    RemittanceCreateDto,
    RemittanceAuditDto,
    RemittancePayDto,
    RemittanceStatus
} from '@/types/influencer';

const remittanceHttp = axios.create({
    baseURL: '/influencer-api/v1/finance/remittance',
    timeout: 60000,
});

remittanceHttp.interceptors.request.use((config) => {
    const raw = localStorage.getItem('auth_token');
    if (raw) {
        try {
            const obj = JSON.parse(raw);
            const token = obj?.token || obj?.accessToken;
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch (err) { }
    }
    const userInfoRaw = localStorage.getItem('userInfo') || localStorage.getItem('user_info');
    if (userInfoRaw) {
        try {
            const userObj = JSON.parse(userInfoRaw);
            const userName = userObj.name || userObj.username || userObj.nickname;
            if (userName) {
                config.headers['X-User-Name'] = encodeURIComponent(userName);
            }
        } catch (err) { }
    }
    return config;
});

remittanceHttp.interceptors.response.use(
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

export const remittanceService = {
    /** 分页查询汇款任务 */
    getList(params: {
        status?: RemittanceStatus | 'ALL',
        taskNo?: string,
        influencerId?: number,
        ownerName?: string,
        paymentMethod?: string,
        auditorName?: string,
        payerName?: string,
        page: number,
        size: number
    }) {
        const queryParams = {
            ...params,
            page: params.page > 0 ? params.page - 1 : 0 // 转换为 0-based
        };
        if (queryParams.status === 'ALL') delete queryParams.status;
        return remittanceHttp.get('', { params: queryParams }) as unknown as Promise<{
            content: RemittanceTask[];
            totalElements: number;
        }>;
    },

    /** 获取状态统计计数 */
    getStatusCounts(params: {
        taskNo?: string,
        influencerId?: number,
        ownerName?: string,
        paymentMethod?: string,
        auditorName?: string,
        payerName?: string
    }) {
        return remittanceHttp.get('/status-counts', { params }) as unknown as Promise<Record<RemittanceStatus, number>>;
    },

    /** 创建汇款任务 */
    create(data: RemittanceCreateDto) {
        return remittanceHttp.post('', data) as unknown as Promise<RemittanceTask>;
    },

    /** 更新汇款任务 */
    update(taskId: number, data: RemittanceCreateDto) {
        return remittanceHttp.put(`/${taskId}`, data) as unknown as Promise<RemittanceTask>;
    },

    /** 审核汇款任务 (通过/拒绝) */
    audit(taskId: number, data: RemittanceAuditDto) {
        return remittanceHttp.post(`/${taskId}/audit`, data);
    },

    /** 确认打款 (上传凭证) */
    pay(taskId: number, data: RemittancePayDto) {
        return remittanceHttp.post(`/${taskId}/pay`, data);
    },

    /** 获取单个详情 */
    getDetail(taskId: number) {
        return remittanceHttp.get(`/${taskId}`) as unknown as Promise<RemittanceTask>;
    },

    /** 导入相关 API */
    analyzeImport(data: FormData) {
        return remittanceHttp.post('/import/analyze', data, {
            headers: { 'Content-Type': 'multipart/form-data' }
        }) as unknown as Promise<any>;
    },
    confirmImport(fileKey: string, originalFilename: string) {
        return remittanceHttp.post('/import/confirm', { fileKey, originalFilename }) as unknown as Promise<any>;
    },
    getImportStatus(jobId: number) {
        return remittanceHttp.get(`/import/status/${jobId}`) as unknown as Promise<any>;
    }
};

export default remittanceService;
