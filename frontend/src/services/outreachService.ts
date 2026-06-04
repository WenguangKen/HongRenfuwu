/**
 * 红人建联服务模块
 *
 * 负责与后端建联模块 API 进行通信，包括：
 * - 邮件建联（发送/列表/详情/重发）
 * - 模板管理（CRUD/复制/预览）
 * - 发件人管理（CRUD/设为默认）
 * - SendCloud 配置（配置/测试连接）
 * - 统计数据
 *
 * @module outreachService
 */
import http from '@/utils/http';
import type {
  OutreachEmail,
  EmailSendDto,
  EmailFilterDto,
  EmailTrackEvent,
  OutreachTemplate,
  TemplateDto,
  TemplateFilterDto,
  OutreachSender,
  SenderDto,
  SendCloudConfig,
  SendCloudConfigDto,
  OutreachStats,
  TrendDataPoint,
  PageResponse,
} from '@/types/outreach';

const BASE = '/outreach';

/* ========== 邮件建联 ========== */

/** 获取邮件列表（分页） */
export const getEmailList = (params: EmailFilterDto) =>
  http.get<any, { data: PageResponse<OutreachEmail> }>(`${BASE}/emails`, { params }).then(r => r.data);

/** 获取邮件详情 */
export const getEmailDetail = (id: number) =>
  http.get<any, { data: OutreachEmail }>(`${BASE}/emails/${id}`).then(r => r.data);

/** 获取邮件追踪事件 */
export const getEmailEvents = (id: number) =>
  http.get<any, { data: EmailTrackEvent[] }>(`${BASE}/emails/${id}/events`).then(r => r.data);

/** 发送邮件（单发/群发） */
export const sendEmail = (data: EmailSendDto) =>
  http.post<any, { data: any }>(`${BASE}/emails/send`, data).then(r => r.data);

/** 保存邮件草稿 */
export const saveEmailDraft = (data: EmailSendDto) =>
  http.post<any, { data: OutreachEmail }>(`${BASE}/emails`, data).then(r => r.data);

/** 重发失败邮件 */
export const resendEmail = (id: number) =>
  http.post<any, { data: any }>(`${BASE}/emails/${id}/resend`).then(r => r.data);

/** 删除邮件记录 */
export const deleteEmail = (id: number) =>
  http.delete(`${BASE}/emails/${id}`);

/* ========== 模板管理 ========== */

/** 获取模板列表 */
export const getTemplateList = (params?: TemplateFilterDto) =>
  http.get<any, { data: PageResponse<OutreachTemplate> }>(`${BASE}/templates`, { params }).then(r => r.data);

/** 获取所有活跃模板（下拉选择用） */
export const getAllActiveTemplates = () =>
  http.get<any, { data: OutreachTemplate[] }>(`${BASE}/templates/active`).then(r => r.data);

/** 获取模板详情 */
export const getTemplateDetail = (id: number) =>
  http.get<any, { data: OutreachTemplate }>(`${BASE}/templates/${id}`).then(r => r.data);

/** 创建模板 */
export const createTemplate = (data: TemplateDto) =>
  http.post<any, { data: OutreachTemplate }>(`${BASE}/templates`, data).then(r => r.data);

/** 编辑模板 */
export const updateTemplate = (id: number, data: TemplateDto) =>
  http.put<any, { data: OutreachTemplate }>(`${BASE}/templates/${id}`, data).then(r => r.data);

/** 复制模板 */
export const copyTemplate = (id: number) =>
  http.post<any, { data: OutreachTemplate }>(`${BASE}/templates/${id}/copy`).then(r => r.data);

/** 删除模板 */
export const deleteTemplate = (id: number) =>
  http.delete(`${BASE}/templates/${id}`);

/** 预览模板（填入示例数据） */
export const previewTemplate = (id: number, variables: Record<string, string>) =>
  http.post<any, { data: { subject: string; bodyHtml: string } }>(`${BASE}/templates/${id}/preview`, variables).then(r => r.data);

/* ========== 发件人管理 ========== */

/** 获取发件人列表 */
export const getSenderList = () =>
  http.get<any, { data: OutreachSender[] }>(`${BASE}/senders`).then(r => r.data);

/** 添加发件人 */
export const createSender = (data: SenderDto) =>
  http.post<any, { data: OutreachSender }>(`${BASE}/senders`, data).then(r => r.data);

/** 编辑发件人 */
export const updateSender = (id: number, data: SenderDto) =>
  http.put<any, { data: OutreachSender }>(`${BASE}/senders/${id}`, data).then(r => r.data);

/** 设为默认发件人 */
export const setDefaultSender = (id: number) =>
  http.put(`${BASE}/senders/${id}/default`);

/** 删除发件人 */
export const deleteSender = (id: number) =>
  http.delete(`${BASE}/senders/${id}`);

/* ========== SendCloud 配置 ========== */

/** 获取 SendCloud 配置 */
export const getSendCloudConfig = () =>
  http.get<any, { data: SendCloudConfig }>(`${BASE}/sendcloud/config`).then(r => r.data);

/** 更新 SendCloud 配置 */
export const updateSendCloudConfig = (data: SendCloudConfigDto) =>
  http.put<any, { data: SendCloudConfig }>(`${BASE}/sendcloud/config`, data).then(r => r.data);

/** 测试 SendCloud 连接 */
export const testSendCloudConnection = () =>
  http.post<any, { data: { success: boolean; message: string } }>(`${BASE}/sendcloud/test`).then(r => r.data);

/* ========== 统计数据 ========== */

/** 获取发送概况统计 */
export const getOutreachStats = () =>
  http.get<any, { data: OutreachStats }>(`${BASE}/stats/overview`).then(r => r.data);

/** 获取发送趋势数据 */
export const getOutreachTrend = (days?: number) =>
  http.get<any, { data: TrendDataPoint[] }>(`${BASE}/stats/trend`, { params: { days: days || 7 } }).then(r => r.data);
