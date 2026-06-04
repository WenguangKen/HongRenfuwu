/**
 * 红人建联模块 - TypeScript 类型定义
 *
 * 定义前端使用的所有建联相关数据结构，包括：
 * - OutreachEmail（邮件记录）
 * - OutreachTemplate（邮件模板）
 * - OutreachSender（发件人配置）
 * - SendCloudConfig（SendCloud 服务配置）
 *
 * @module types/outreach
 */

/* ========== 邮件状态枚举 ========== */

export type EmailStatus =
  | 'draft'
  | 'pending'
  | 'sending'
  | 'delivered'
  | 'opened'
  | 'replied'
  | 'bounced'
  | 'failed';

export const EMAIL_STATUS_MAP: Record<EmailStatus, { label: string; color: string }> = {
  draft:     { label: '草稿',   color: '#94a3b8' },
  pending:   { label: '待发送', color: '#f59e0b' },
  sending:   { label: '发送中', color: '#3b82f6' },
  delivered: { label: '已送达', color: '#10b981' },
  opened:    { label: '已打开', color: '#8b5cf6' },
  replied:   { label: '已回复', color: '#06b6d4' },
  bounced:   { label: '退信',   color: '#ef4444' },
  failed:    { label: '失败',   color: '#ef4444' },
};

/* ========== 模板分类枚举 ========== */

export type TemplateCategory = 'invitation' | 'premium' | 'ambassador' | 'followup' | 'custom';

export const TEMPLATE_CATEGORY_MAP: Record<TemplateCategory, { label: string; color: string }> = {
  invitation:  { label: '合作邀约', color: '#3b82f6' },
  premium:     { label: '高端品牌', color: '#8b5cf6' },
  ambassador:  { label: '品牌大使', color: '#f59e0b' },
  followup:    { label: '跟进催促', color: '#10b981' },
  custom:      { label: '自定义',   color: '#64748b' },
};

/* ========== 邮件记录 ========== */

/** 邮件建联记录 */
export interface OutreachEmail {
  id: number;
  influencerId: number;
  influencerName?: string;
  influencerAvatar?: string;
  senderEmail: string;
  recipientEmail: string;
  templateId?: number;
  templateName?: string;
  subject: string;
  bodyHtml: string;
  status: EmailStatus;
  scheduledAt?: string;
  sentAt?: string;
  openedAt?: string;
  repliedAt?: string;
  sendcloudMessageId?: string;
  errorMessage?: string;
  createdBy?: number;
  createdByName?: string;
  createdAt: string;
  updatedAt: string;
}

/** 创建/发送邮件请求 */
export interface EmailSendDto {
  influencerIds: number[];
  senderEmail: string;
  templateId?: number;
  subject: string;
  bodyHtml: string;
  scheduledAt?: string;
}

/** 邮件列表筛选参数 */
export interface EmailFilterDto {
  influencerName?: string;
  recipientEmail?: string;
  status?: EmailStatus;
  templateId?: number;
  sentTimeStart?: string;
  sentTimeEnd?: string;
  page: number;
  size: number;
}

/** 邮件追踪事件 */
export interface EmailTrackEvent {
  id: number;
  emailId: number;
  eventType: 'send' | 'deliver' | 'open' | 'click' | 'bounce' | 'spam' | 'unsubscribe';
  eventTime: string;
  detail?: string;
}

/* ========== 邮件模板 ========== */

/** 邮件模板 */
export interface OutreachTemplate {
  id: number;
  name: string;
  category: TemplateCategory;
  subject: string;
  bodyHtml: string;
  variables?: string[];
  usageCount: number;
  lastUsedAt?: string;
  status: 'active' | 'archived';
  createdBy?: number;
  createdByName?: string;
  createdAt: string;
  updatedAt: string;
}

/** 创建/编辑模板请求 */
export interface TemplateDto {
  name: string;
  category: TemplateCategory;
  subject: string;
  bodyHtml: string;
}

/** 模板列表筛选参数 */
export interface TemplateFilterDto {
  name?: string;
  category?: TemplateCategory;
  status?: 'active' | 'archived';
  page?: number;
  size?: number;
}

/* ========== 发件人配置 ========== */

/** 发件人 */
export interface OutreachSender {
  id: number;
  email: string;
  displayName: string;
  isDefault: boolean;
  isVerified: boolean;
  dailyLimit: number;
  dailySent: number;
  createdAt: string;
  updatedAt: string;
}

/** 创建/编辑发件人请求 */
export interface SenderDto {
  email: string;
  displayName: string;
}

/* ========== SendCloud 配置 ========== */

/** SendCloud 服务配置 */
export interface SendCloudConfig {
  id?: number;
  apiUser: string;
  apiKey: string;
  webhookUrl?: string;
  webhookEvents?: string[];
  isConnected: boolean;
  lastVerifiedAt?: string;
}

/** SendCloud 配置更新请求 */
export interface SendCloudConfigDto {
  apiUser: string;
  apiKey: string;
  webhookUrl?: string;
  webhookEvents?: string[];
}

/* ========== 统计 ========== */

/** 发送概况统计 */
export interface OutreachStats {
  todaySent: number;
  weekSent: number;
  monthSent: number;
  successRate: number;
  openRate: number;
  replyRate: number;
}

/** 发送趋势数据点 */
export interface TrendDataPoint {
  date: string;
  sent: number;
  delivered: number;
  opened: number;
  replied: number;
}

/* ========== 分页响应 ========== */

/** 通用分页响应 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

/* ========== 模板变量 ========== */

/** 可用模板变量列表 */
export const TEMPLATE_VARIABLES = [
  { key: 'name',           label: '红人名称',     example: 'Anna Smith' },
  { key: 'email',          label: '红人邮箱',     example: 'anna@example.com' },
  { key: 'social_handle',  label: '社媒账号名',   example: '@anna_beauty' },
  { key: 'platform',       label: '社媒平台',     example: 'TikTok' },
  { key: 'followers',      label: '粉丝数',       example: '125,000' },
  { key: 'product_name',   label: '商品名称',     example: 'Summer Collection' },
  { key: 'product_link',   label: '商品链接',     example: 'https://shop.example.com/summer' },
  { key: 'brand_name',     label: '品牌名称',     example: 'YourBrand' },
] as const;
