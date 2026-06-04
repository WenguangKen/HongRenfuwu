/**
 * 红人管理系统 - TypeScript 类型定义
 *
 * 定义前端使用的所有红人相关数据结构，包括：
 * - Influencer（红人实体）
 * - InfluencerCreateDto（创建红人 DTO）
 * - InfluencerFilterDto（筛选查询 DTO）
 * - BatchWorkflowDto / BatchWorkflowResult（批量操作）
 * - Address / SocialMedia / PaymentRecord（子资源）
 *
 * @module types/influencer
 */

/* ========== 红人主实体 ========== */

/** 红人实体（列表和详情通用） */
export interface Influencer {
    id: number;                 // 红人唯一 ID
    realName?: string;          // 真实姓名
    nickName?: string;          // 昵称
    avatarUrl?: string;         // 头像 URL
    email?: string;             // 主邮箱
    backupEmail?: string;       // 备用邮箱
    phone?: string;             // 联系电话
    country?: string;           // 所在国家（ISO 代码）
    language?: string;          // 使用语言
    race?: string;              // 人种/肤色
    gender?: number;            // 性别：0=未知, 1=男, 2=女
    description?: string;       // 备注描述

    origin: 'IMPORT' | 'CRAWLER' | 'MANUAL'; // 数据来源：导入/爬取/手动
    source?: string;            // 红人来源（具体渠道）
    sourceValue?: string;       // 来源具体值（如 hashtag）
    influencerType?: string;    // 红人类型（Fashion/Beauty 等）
    isPaid: boolean;            // 是否有付费合作
    brand?: string;             // 合作品牌
    tagIds?: number[];          // 关联标签 ID 列表
    tags?: any[];               // 标签完整对象（含颜色等，用于展示）

    stage: 'POOL' | 'ONBOARDED' | 'TRASH';   // 阶段：资源池/红人列表/回收站
    status: 'PENDING' | 'COOPERATING' | 'DORMANT' | 'PAUSED' | 'BLACKLIST' | 'TERMINATED' | 'REJECTED' | 'UNSCREENED' | 'CONTACTED'; // 状态

    commissionRate?: number;    // 基础佣金率（%）
    paymentMethod?: string;     // 打款方式
    paymentAccount?: string;    // 打款账号

    defaultSocialId?: number;   // 默认社媒账号 ID
    ownerId?: number;           // 负责人 ID
    ownerName?: string;         // 负责人姓名
    contactPersonId?: number;   // 联络人 ID
    contactPersonName?: string; // 联络人姓名

    totalFans?: number;         // 粉丝总量
    totalOrders?: number;       // 总订单数
    sampleOrderCount?: number;  // 样品单数量
    sampleCount?: number;       // 合作次数
    createdAt?: string;         // 创建时间
    updatedAt?: string;         // 最近更新时间

    defaultPlatform?: string;   // 默认平台
    defaultHandle?: string;     // 默认账号名
    defaultUrl?: string;        // 默认主页链接

    lastSampleAt?: string;      // 最近样品单时间
    lastConversionAt?: string;  // 最近转化单时间

    auditorName?: string;       // 审核人姓名
    auditTime?: string;         // 审核时间

    platformName?: string;      // 默认平台名称
    profileLink?: string;       // 默认社媒主页链接
    socialMedias?: any[];       // 社交媒体账号列表
}

/* ========== 创建红人 DTO ========== */

/** 创建红人请求体 */
export interface InfluencerCreateDto {
    /* --- 必填字段 --- */
    realName: string;           // 真实姓名（必填）
    email: string;              // 邮箱（必填）

    /* --- 基本信息（选填） --- */
    nickName?: string;          // 昵称
    avatarUrl?: string;         // 头像 URL
    phone?: string;             // 电话
    country?: string;           // 国家
    language?: string;          // 语言
    race?: string;              // 人种/肤色
    gender?: number;            // 性别：0=未知, 1=男, 2=女
    description?: string;       // 备注
    influencerType?: string;    // 红人类型

    /* --- 业务属性 --- */
    source?: string;            // 红人来源
    ownerId?: number;           // 负责人 ID
    contactPersonId?: number;   // 联络人 ID
    isPaid?: boolean;           // 是否付费
    brand?: string;             // 品牌
    tagIds?: number[];          // 标签 ID 列表

    /* --- 财务信息 --- */
    commissionRate?: number;    // 佣金率（%）
    paymentMethod?: string;     // 打款方式
    paymentAccount?: string;    // 打款账号

    /* --- 默认社交媒体 --- */
    defaultPlatform?: string;   // 默认平台
    defaultHandle?: string;     // 默认账号名
    defaultProfileUrl?: string; // 默认主页链接
    defaultFollowerCount?: number; // 默认粉丝数

    /* --- 地址信息 --- */
    address?: string;           // 完整地址
    city?: string;              // 城市
    state?: string;             // 州/省
    street1?: string;           // 街道地址1
    street2?: string;           // 街道地址2
    postalCode?: string;        // 邮编

    /* --- 社交媒体列表 --- */
    socialMediaList?: {
        platform: string;       // 平台名称
        handle: string;         // 账号名
        url: string;            // 主页链接
        followerCount?: number; // 粉丝数
        isDefault?: boolean;    // 是否为默认账号
    }[];

    /* --- 初始状态（创建时指定） --- */
    initialStage?: 'POOL' | 'ONBOARDED';  // POOL=资源池, ONBOARDED=红人列表
    initialStatus?: string;     // UNSCREENED=待筛选, PENDING=待沟通 等
}

/* ========== 筛选查询 DTO ========== */

/** 红人列表筛选查询参数 */
export interface InfluencerFilterDto {
    /* --- API 查询字段 --- */
    searchKey?: string;         // 搜索关键词（匹配姓名/昵称/邮箱）
    stage?: string;             // 阶段筛选
    status?: string;            // 状态筛选
    country?: string;           // 国家筛选
    platform?: string | string[];          // 建联平台筛选
    brand?: string;             // 品牌筛选
    isPaid?: boolean;           // 是否付费合作
    tagIds?: number[];          // 标签 ID 筛选（OR 关系）
    liaisonTagIds?: number[];   // 联络员标签 ID 筛选
    ownerIds?: number[];        // 负责人 ID 列表
    contactPersonIds?: number[]; // 联络人 ID 列表
    fanRangeMin?: number;       // 粉丝数下限
    fanRangeMax?: number;       // 粉丝数上限

    /* --- 扩展 API 字段 --- */
    email?: string;             // 单个邮箱模糊搜索
    emails?: string[];          // 批量邮箱精确搜索（IN 查询）
    names?: string[];           // 批量姓名精确搜索（IN 查询）
    origin?: string;            // 数据来源枚举
    influencerType?: string;    // 红人类型
    profileLink?: string;       // 主页链接搜索
    timeType?: string;          // 时间类型：created/updated
    timeStart?: string;         // 时间范围起始
    timeEnd?: string;           // 时间范围结束
    race?: string | string[];              // 人种/肤色筛选

    /* --- 分页参数 --- */
    page: number;               // 当前页码（1-based）
    size: number;               // 每页大小

    /* --- UI 前端状态（不直接传后端） --- */
    name?: string;              // 红人名称输入框
    source?: string;            // 来源选择
    link?: string;              // 主页链接输入框
    fansRange?: string;         // 粉丝区间下拉值
    timeRange?: any[];          // 时间范围组件值 [start, end]
    owner?: string | number[];             // 负责人选择
    contactPerson?: string;     // 联络人选择
    socialHandle?: string;      // 社媒账号名搜索
    listStatus?: string;        // 「全部」Tab 红人状态（映射 API status）
    socialPlatform?: string[];  // 社媒平台多选
    fansMin?: number;           // 粉丝数最小值输入
    fansMax?: number;           // 粉丝数最大值输入

    /* --- 内容 --- */
    hasOutputContent?: boolean | string; // 是否有输出内容

    /* --- 合作次数 --- */
    minSampleCount?: number;    // 最小合作次数
    maxSampleCount?: number;    // 最大合作次数

    /* --- 空白筛选 --- */
    blankFields?: string[];     // 空白字段筛选列表
}

/* ========== 批量操作 DTO ========== */

/** 批量状态变更请求 */
export interface BatchWorkflowDto {
    ids: number[];              // 要操作的红人 ID 列表
    targetStage?: string;       // 目标阶段
    targetStatus?: string;      // 目标状态
    reason?: string;            // 操作原因/备注
}

/** 批量操作结果 */
export interface BatchWorkflowResult {
    successIds: number[];       // 成功的 ID 列表
    failedItems: {
        id: number;             // 失败的红人 ID
        errorType: string;      // 错误类型
        errorMessage: string;   // 错误结果/原因
    }[];
}

/** 休眠检查结果 */
export interface DormancyCheckResult {
    eligible: boolean;          // 是否满足休眠条件
    message: string;            // 检查说明
}

/* ========== 子资源类型 ========== */

/** 收货地址 */
export interface Address {
    id: number;                 // 地址 ID
    recipientName: string;      // 收件人姓名
    phone: string;              // 收件人电话
    email?: string;             // 收件人邮箱
    country?: string;           // 国家
    address: string;            // 完整地址
    postalCode?: string;        // 邮编
    isDefault: boolean;         // 是否为默认地址
}

/** 社交媒体账号 */
export interface SocialMedia {
    id?: number;                // 账号 ID
    platform: string;           // 平台名称（Instagram/TikTok/YouTube）
    handle: string;             // 账号名
    url: string;                // 主页链接
    followerCount?: number;     // 粉丝数
    isDefault?: boolean;        // 是否为默认账号
}

/** 打款记录 */
export interface PaymentRecord {
    id: number;                 // 记录 ID
    paymentNo: string;          // 支付单号
    transactionId?: string;     // 交易流水号
    amount: number;             // 打款金额
    currency: string;           // 币种（USD/CNY 等）
    status: 'PENDING' | 'PAID' | 'FAILED'; // 状态：待处理/已支付/失败
    paymentMethod?: string;     // 打款方式
    receiverAccount?: string;   // 收款账号
    batchId?: string;           // 批次号
    payer?: string;             // 付款人
    remark?: string;            // 备注
    paidAt?: string;            // 实际打款时间
    createdAt: string;          // 创建时间
}

/* ========== 汇款任务类型 ========== */

export type RemittanceStatus = 'PENDING_AUDIT' | 'PENDING_PAYMENT' | 'PAID' | 'REJECTED';

export interface RemittanceTask {
    id: number;
    taskNo: string;
    influencerId: number;
    influencerName?: string;
    influencerEmail?: string;
    orderNo?: string;
    currency: string;
    amount: number;
    fee?: number;
    totalAmount?: number;
    status: RemittanceStatus;
    paymentType?: string;
    paymentMethod?: string;
    paymentAccount?: string;
    paymentDetails?: string;
    remark?: string;
    creatorName?: string;
    auditorId?: number;
    auditorName?: string;
    payerId?: number;
    payerName?: string;
    auditTime?: string;
    payTime?: string;
    auditRemark?: string;
    paymentRemark?: string;
    voucherUrl?: string; // 打款凭证图片 URL
    ownerName?: string;  // 红人关联的负责人
    createdAt: string;
    updatedAt: string;
}

export interface RemittanceCreateDto {
    influencerId: number;
    orderNo?: string;
    currency: string;
    amount: number;
    fee?: number;
    totalAmount?: number;
    paymentType?: string;
    paymentMethod?: string;
    paymentAccount?: string;
    paymentDetails?: string;
    remark?: string;
}

export interface RemittanceAuditDto {
    action: 'approve' | 'reject' | 'reject_to_audit';
    remark?: string;
    paymentMethod?: string;
    paymentAccount?: string;
    paymentDetails?: string;
    amount?: number;
    fee?: number;
    totalAmount?: number;
}

export interface RemittancePayDto {
    voucherUrl: string; // 必须由前端先上传文件并获得 URL
    remark?: string;
    paymentMethod?: string;
    paymentAccount?: string;
    paymentDetails?: string;
    amount?: number;
    fee?: number;
    totalAmount?: number;
    paidAt?: string; // 实际打款时间，格式 YYYY-MM-DD HH:mm:ss
}
