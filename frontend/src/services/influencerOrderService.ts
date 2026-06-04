import http from '@/utils/http';

// ==================== 类型定义 ====================

export interface SampleOrderDto {
    id: number;
    orderId: number;
    shopifyOrderId: string;
    shopifyOrderNumber: number;
    orderName: string;
    orderNo?: string; // 前端展示短号 (#1015)
    longOrderNo?: string; // 前端展示长号 (GID)
    influencerId: number;
    influencerName: string;
    totalPrice: number;
    currency: string;
    sampleReason: string;
    orderCreatedAt: string;
    createdAt: string;
    financialStatus: string;
    fulfillmentStatus: string;
    customerName: string;
    customerEmail: string;
    shippingAddress: string;
    products?: any[]; // OrderLineItemDto
    packageNo?: string;
    fulfillmentIds?: string;
    isSplit?: boolean;
    isDraft?: boolean;
    storeId?: number;
    storeName?: string;
    updatedAt?: string;
    shippingName?: string;
    shippingPhone?: string;
    // 新增物流字段
    fulfillmentDisplayStatus?: string;
    trackingCompany?: string;
    trackingNumber?: string;
    trackingUrl?: string;
    trackingEventsJson?: string;
    // 取消状态
    cancelledAt?: string;
    closedAt?: string;  // 订单关闭时间

    // 时间节点字段
    processedAtShopify?: string;    // 支付时间
    fulfillmentCreatedAt?: string;  // 发货时间
    inTransitAt?: string;           // 运输中时间
    deliveredAt?: string;           // 到货时间
    estimatedDeliveryAt?: string;   // 预计送达时间

    // 收件人信息
    recipientName?: string;         // 收件人姓名
    recipientPhone?: string;        // 收件人电话
    recipientAddress?: string;      // 收件人地址
    recipientCountry?: string;      // 收件人国家

    // 自动匹配标志
    autoMatched?: boolean;          // 是否为自动匹配

    ownerId?: number;               // 负责人ID
    contactPersonId?: number;       // 联络员ID
    contactPersonName?: string;     // 联络员名称(后端从tags解析)
    ownerName?: string;             // 负责人名称
}

export interface ConversionOrderDto {
    id: number;
    orderId: number;
    shopifyOrderId: string;
    shopifyOrderNumber: number;
    orderName: string;
    influencerId: number;
    influencerName: string;
    discountCode: string;
    totalPrice: number;
    totalShipping: number;
    totalRefund: number;
    commissionableAmount: number;
    currency: string;
    commissionRate: number;
    commissionAmount: number;
    commissionStatus: string;
    financialStatus: string;
    fulfillmentStatus: string;
    orderCreatedAt: string;
    createdAt: string;
    updatedAt: string;
    customerName: string;
    customerEmail: string;
    shippingAddress: string;
    shippingName?: string;
    shippingPhone?: string;
    shippingCountry?: string;
    recipientName?: string;
    recipientPhone?: string;
    recipientAddress?: string;
    recipientCountry?: string;
    products?: any[]; // OrderLineItemDto
    // 新增物流字段
    packageNo?: string;
    fulfillmentIds?: string;
    isSplit?: boolean;
    isDraft?: boolean;
    storeId?: number;
    storeName?: string;
    fulfillmentDisplayStatus?: string;
    trackingCompany?: string;
    trackingNumber?: string;
    trackingUrl?: string;
    trackingEventsJson?: string;
    // 取消状态
    cancelledAt?: string;
    closedAt?: string;  // 订单关闭时间

    // 时间节点字段
    processedAtShopify?: string;    // 支付时间
    fulfillmentCreatedAt?: string;  // 发货时间
    inTransitAt?: string;           // 运输中时间
    deliveredAt?: string;           // 到货时间
    estimatedDeliveryAt?: string;   // 预计送达时间
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
}

export interface OrderStats {
    sampleOrderCount: number;
    conversionOrderCount: number;
}

// ==================== API 调用 ====================

const API_BASE = '/shopify/v1/influencer-orders';

/**
 * 获取样品单列表
 */
export async function getSampleOrders(
    page: number = 0,
    size: number = 20,
    filters: {
        orderNo?: string;
        shopifyOrderId?: string;
        influencerName?: string;
        logisticsStatus?: string;
        tab?: string;
        customerEmail?: string;
        ownerId?: number[];
        contactPersonId?: number[];
    } = {}
): Promise<PageResponse<SampleOrderDto>> {
    const params: Record<string, any> = { page, size };
    const response = await http.post<PageResponse<SampleOrderDto>>(`${API_BASE}/sample/search`, filters, { params });
    return response.data;
}

/**
 * 获取样品单各 Tab 计数
 */
export async function getSampleTabCounts(filters: {
    orderNo?: string;
    influencerName?: string;
    logisticsStatus?: string;
    packageNo?: string;
    spu?: string;
    timeType?: string;
    startTime?: string;
    endTime?: string;
    influencerSocialSearch?: string;
    recipientName?: string;
    recipientCountry?: string;
    customerEmail?: string;
    ownerId?: number[];
    contactPersonId?: number[];
} = {}): Promise<Record<string, number>> {
    const response = await http.post<Record<string, number>>(`${API_BASE}/sample/tab-counts/search`, filters);
    return response.data;
}

/**
 * 获取样品单详情
 */
export async function getSampleOrderDetail(id: number): Promise<SampleOrderDto> {
    const response = await http.get<SampleOrderDto>(`${API_BASE}/sample/${id}`);
    return response.data;
}

/**
 * 获取转化订单列表
 */
export async function getConversionOrders(
    page: number = 0,
    size: number = 20,
    filters: {
        influencerId?: number;
        influencerName?: string;
        orderNo?: string;
        discountCode?: string;
        commissionStatus?: string;
        tab?: string;
        customerEmail?: string;
        startTime?: string;
        endTime?: string;
    } = {}
): Promise<PageResponse<ConversionOrderDto>> {
    const params: Record<string, any> = { page, size, sort: 'orderCreatedAt,desc' };
    const response = await http.post<PageResponse<ConversionOrderDto>>(`${API_BASE}/conversion/search`, filters, { params });
    return response.data;
}

/**
 * 获取转化订单各 Tab 计数
 */
export async function getConversionTabCounts(filters: {
    influencerName?: string;
    orderNo?: string;
    discountCode?: string;
    commissionStatus?: string;
    spu?: string;
    timeType?: string;
    startTime?: string;
    endTime?: string;
    influencerSocialSearch?: string;
    customerEmail?: string;
} = {}): Promise<Record<string, number>> {
    const response = await http.post<Record<string, number>>(`${API_BASE}/conversion/tab-counts/search`, filters);
    return response.data;
}

/**
 * 获取转化订单详情
 */
export async function getConversionOrderDetail(id: number): Promise<ConversionOrderDto> {
    const response = await http.get<ConversionOrderDto>(`${API_BASE}/conversion/${id}`);
    return response.data;
}

/**
 * 订单详情响应（包含付款、退款、日志等完整信息）
 */
export interface OrderDetailResponse {
    id: number;
    orderNo: string;
    orderName?: string;
    shopifyOrderId: string;
    totalAmount: number;
    totalShipping?: number; // 运费
    financialStatus: string;
    fulfillmentStatus: string;
    storeName?: string;

    // 收货人信息
    shippingName?: string;
    shippingPhone?: string;
    shippingAddress?: string;
    customerName?: string;
    customerEmail?: string;
    shippingCountry?: string;

    // 物流信息
    trackingNumber?: string;
    trackingCompany?: string;
    trackingUrl?: string;

    // 时间节点
    createdAt?: string;
    paidAt?: string;
    shippedAt?: string;
    deliveredAt?: string;
    cancelledAt?: string;
    updatedAt?: string;

    // 商品
    products?: ProductInfo[];

    // 付款/退款/折扣/日志
    payments: OrderPayment[];
    refunds: OrderRefund[];
    discount: DiscountInfo;
    logs: OrderLog[];
}

export interface ProductInfo {
    id: number;
    sku: string;
    spu?: string;
    name?: string;
    image?: string;
    quantity: number;
    price?: number;
    status?: string;
    variantTitle?: string;
    shopifyProductId?: number;
    shopifyVariantId?: number;
    _isOrderLine?: boolean;
}

export interface OrderPayment {
    id: string;
    paymentNo: string;
    time: string;
    amount: string;
    method: string;
    transactionId: string;
    status: string;
    operator: string;
}

export interface OrderRefund {
    id: string;
    refundNo: string;
    time: string;
    amount: string;
    reason: string;
    method: string;
    status: string;
    operator: string;
}

export interface DiscountInfo {
    code: number;
    voucher: number;
}

export interface OrderLog {
    type: string;
    time: string;
    content: string;
    operator: string;
    details?: Record<string, any>;
}

/**
 * 获取订单完整详情（包含付款、退款、日志等）
 */
export async function getOrderDetails(orderId: number, orderType: string = 'sample'): Promise<OrderDetailResponse> {
    const response = await http.get<OrderDetailResponse>(`/shopify/v1/orders/${orderId}/details`, {
        params: { orderType }
    });
    return response.data;
}


/**
 * 触发订单同步
 */
export async function syncInfluencerOrders(
    storeId: number,
    startTime?: string,
    endTime?: string
): Promise<{ success: boolean; message: string }> {
    const params: Record<string, any> = {};
    if (startTime) params.startTime = startTime;
    if (endTime) params.endTime = endTime;

    const response = await http.post<{ success: boolean; message: string }>(`${API_BASE}/sync/${storeId}`, {}, { params });
    return response.data;
}

/**
 * 检测红人最近3天是否有样品单
 */
export interface DuplicateCheckResult {
    hasDuplicate: boolean;
    lastOrderDate?: string;
    lastOrderName?: string;
    lastOrderId?: number;
    influencerName?: string;
}

export async function checkDuplicateSampleOrder(influencerId: number): Promise<DuplicateCheckResult> {
    const response = await http.get<DuplicateCheckResult>(`${API_BASE}/sample/check-duplicate`, {
        params: { influencerId }
    });
    return response.data;
}

/**
 * 获取订单统计
 */
export async function getOrderStats(): Promise<OrderStats> {
    const response = await http.get<OrderStats>(`${API_BASE}/stats`);
    return response.data;
}

// ==================== 绑定红人相关 ====================

export interface BindInfluencerRequest {
    orderId?: number;
    shopifyOrderNumber: number;
    influencerId: number;
    influencerName?: string;  // 前端直接传递，避免后端跨服务调用
}

export interface BindInfluencerResponse {
    success: boolean;
    message: string;
    ordersUpdated?: number;
    sampleOrdersUpdated?: number;
}

/**
 * 绑定红人到样品订单
 * 会自动关联所有相关拆单/主单
 */
export async function bindSampleOrderToInfluencer(request: BindInfluencerRequest): Promise<BindInfluencerResponse> {
    const response = await http.post<BindInfluencerResponse>(`${API_BASE}/sample/bind`, request);
    return response.data;
}

export async function getSampleOrdersByInfluencer(influencerId: number): Promise<SampleOrderDto[]> {
    const response = await http.get<SampleOrderDto[]>(`${API_BASE}/sample/by-influencer/${influencerId}`);
    return response.data;
}

/**
 * 更新样品订单的合作价格（手动填写）
 */
export async function updateSampleOrderCoopPrice(orderId: number, price: number): Promise<void> {
    await http.put(`${API_BASE}/sample/${orderId}/cooperation-price`, null, {
        params: { price }
    });
}

/**
 * 导出样品订单
 */
export async function exportSampleOrders(params: any, filename: string = '样品订单导出'): Promise<void> {
    const response = await http.get(`${API_BASE}/sample/export`, {
        params,
        responseType: 'blob'
    });

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;

    // 获取当前日期作为文件名后缀
    const date = new Date().toISOString().split('T')[0];
    link.setAttribute('download', `${filename}_${date}.csv`);

    document.body.appendChild(link);
    link.click();

    // 清理
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
}

export default {
    getSampleOrders,
    getSampleOrderDetail,
    getSampleTabCounts,
    getConversionOrders,
    getConversionOrderDetail,
    getConversionTabCounts,
    syncInfluencerOrders,
    getOrderStats,
    bindSampleOrderToInfluencer,
    getSampleOrdersByInfluencer,
    updateSampleOrderCoopPrice,
    exportSampleOrders,
};

