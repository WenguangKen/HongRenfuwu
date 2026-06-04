import type { LocationQuery } from 'vue-router';
import { emitAiOrderFilterComplete } from '@/utils/aiActionBus';
import type { OrderPagePath } from '@/utils/copilotOrderFilters';

/** 订单列表加载完成后通知小A 更新人性化总结 */
export function notifyCopilotOrderSearchComplete(
  query: LocationQuery,
  total: number,
  filterForm: { orderNo?: string; shopifyOrderId?: string },
  page: OrderPagePath
): void {
  if (query.fromAi !== '1') return;
  const orderNo = [filterForm.orderNo, filterForm.shopifyOrderId].filter(Boolean).join(',') || undefined;
  emitAiOrderFilterComplete({
    total,
    applied: total > 0,
    orderNo,
    page,
  });
}

/** 将小A URL 参数写入转化订单筛选表单 */
export function applyConversionOrderQuery(
  query: LocationQuery,
  filterForm: {
    orderNo: string;
    shopifyOrderId: string;
    influencer: string;
    discountCode: string;
    trackingNo: string;
    status?: string;
    commissionStatus?: string;
    customerEmail: string;
  },
  activeKey: { value: string },
  filterExpanded: { value: boolean }
): boolean {
  if (query.fromAi !== '1') return false;
  if (query.resetAi === '1') {
    filterForm.orderNo = '';
    filterForm.shopifyOrderId = '';
    filterForm.influencer = '';
    filterForm.discountCode = '';
    filterForm.trackingNo = '';
    filterForm.customerEmail = '';
  }
  let applied = false;
  if (query.tab) {
    activeKey.value = String(query.tab);
    applied = true;
  }
  if (query.orderNo) {
    filterForm.orderNo = String(query.orderNo);
    applied = true;
  }
  if (query.shopifyOrderId) {
    filterForm.shopifyOrderId = String(query.shopifyOrderId);
    applied = true;
  }
  if (query.influencer) {
    filterForm.influencer = String(query.influencer);
    filterExpanded.value = true;
    applied = true;
  }
  if (query.discountCode) {
    filterForm.discountCode = String(query.discountCode);
    filterExpanded.value = true;
    applied = true;
  }
  if (query.trackingNo) {
    filterForm.trackingNo = String(query.trackingNo);
    filterExpanded.value = true;
    applied = true;
  }
  if (query.status) {
    filterForm.status = String(query.status);
    applied = true;
  }
  if (query.commissionStatus) {
    filterForm.commissionStatus = String(query.commissionStatus);
    applied = true;
  }
  if (query.customerEmail) {
    filterForm.customerEmail = String(query.customerEmail);
    applied = true;
  }
  return applied;
}

/** 将小A URL 参数写入样品订单筛选表单 */
export function applySampleOrderQuery(
  query: LocationQuery,
  filterForm: {
    orderNo: string;
    shopifyOrderId: string;
    influencer: string;
    trackingNo: string;
    customerEmail: string;
    orderStatus?: string;
    logisticsStatus?: string;
  },
  activeKey: { value: string },
  filterExpanded: { value: boolean }
): boolean {
  if (query.fromAi !== '1') return false;
  if (query.resetAi === '1') {
    filterForm.orderNo = '';
    filterForm.shopifyOrderId = '';
    filterForm.influencer = '';
    filterForm.trackingNo = '';
    filterForm.customerEmail = '';
  }
  let applied = false;
  if (query.tab) {
    activeKey.value = String(query.tab);
    applied = true;
  }
  if (query.orderNo) {
    filterForm.orderNo = String(query.orderNo);
    applied = true;
  }
  if (query.shopifyOrderId) {
    filterForm.shopifyOrderId = String(query.shopifyOrderId);
    applied = true;
  }
  if (query.influencer) {
    filterForm.influencer = String(query.influencer);
    filterExpanded.value = true;
    applied = true;
  }
  if (query.trackingNo) {
    filterForm.trackingNo = String(query.trackingNo);
    applied = true;
  }
  if (query.status) {
    filterForm.orderStatus = String(query.status);
    applied = true;
  }
  if (query.customerEmail) {
    filterForm.customerEmail = String(query.customerEmail);
    applied = true;
  }
  return applied;
}
