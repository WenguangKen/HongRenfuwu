import type { CopilotSummaryStep } from '@/utils/copilotSummary';

export type OrderPagePath = '/order/conversion' | '/order/sample' | '/commission/order';

/** 转化/样品订单页筛选（与页面表单字段一致） */
export type OrderFilterData = {
  page: OrderPagePath;
  orderNo?: string;
  shopifyOrderId?: string;
  influencer?: string;
  discountCode?: string;
  trackingNo?: string;
  status?: string;
  commissionStatus?: string;
  tab?: string;
  customerEmail?: string;
};

const CONVERSION_STATUS: Array<{ re: RegExp; value: string }> = [
  { re: /待付款/, value: 'pending_payment' },
  { re: /待揽收|待发货/, value: 'pending_ship' },
  { re: /已发货|运输中/, value: 'shipped' },
  { re: /已妥投/, value: 'delivered' },
  { re: /已取消|取消/, value: 'cancelled' },
  { re: /异常/, value: 'exception' },
];

const COMMISSION_STATUS: Array<{ re: RegExp; value: string }> = [
  { re: /未结算/, value: 'unsettled' },
  { re: /已结算/, value: 'settled' },
];

/** 是否在与订单相关的指令 */
export function isOrderRelatedMessage(text: string): boolean {
  const t = text.trim();
  if (/样品单|样品订单|红人订单|转化单|分佣订单/i.test(t)) return true;
  if (/红人.*(?:订单|样品)|(?:订单|样品).*红人/i.test(t)) return true;
  return /订单|order|交易号|shopify|物流单|哪个订单|哪笔订单/i.test(t);
}

export function resolveOrderPage(text: string): OrderPagePath {
  if (/转化|conversion|转化单/i.test(text)) return '/order/conversion';
  if (/样品|红人订单|sample/i.test(text)) return '/order/sample';
  if (/分佣|commission/i.test(text)) return '/commission/order';
  /** 泛称「订单」默认查红人订单（样品单） */
  if (/订单|order/i.test(text)) return '/order/sample';
  return '/order/sample';
}

/** 解析订单号（GID / Shopify / 交易号 / 短数字单号） */
export function parseOrderNoFromText(text: string): string | undefined {
  const t = text.trim();
  const gid = t.match(/gid:\/\/shopify\/Order\/(\d+)/i);
  if (gid) return `gid://shopify/Order/${gid[1]}`;
  const shopify = t.match(/\b\d{10,}\b/);
  if (shopify && /订单号|shopify/i.test(t)) return shopify[0];
  const trade = t.match(/交易号[：:\s]*([A-Za-z0-9_-]{4,64})/i);
  if (trade?.[1]) return trade[1];

  if (isOrderRelatedMessage(t)) {
    const labeled = t.match(/(?:订单号?|单号|编号|order\s*#?)[：:\s#]*([A-Za-z0-9_-]{3,64})/i);
    if (labeled?.[1]) return labeled[1].trim();
    const leading = t.match(/^(\d{3,20})\b/);
    if (leading?.[1]) return leading[1];
    const trailing = t.match(/(\d{3,20})\s*(?:这|该|此)?(?:个)?订单/i);
    if (trailing?.[1]) return trailing[1];
  }
  return undefined;
}

export function parseDiscountCodeFromText(text: string): string | undefined {
  const m = text.match(/折扣码[：:\s]*([A-Za-z0-9_-]{2,32})/i);
  return m?.[1]?.trim();
}

export function parseInfluencerForOrder(text: string): string | undefined {
  const patterns = [
    /^@?([a-z][a-z0-9._-]{2,64})\s+这个红人/i,
    /([a-z][a-z0-9._-]{2,64})\s+这个红人.*(?:样品|订单)/i,
    /这个红人.*?([a-z][a-z0-9._-]{2,64}).*(?:样品|订单)/i,
    /关联红人[：:\s]*([^\s,，]+)/i,
    /红人[：:\s]*([^\s,，]+?)(?:的)?(?:样品|订单)/i,
    /订单.*红人[：:\s]*([^\s,，]+)/i,
    /influencer[：:\s]*([a-z0-9._-]{2,64})/i,
  ];
  for (const re of patterns) {
    const m = text.match(re);
    if (m?.[1]) return m[1].replace(/^@/, '').trim();
  }
  return undefined;
}

export function parseOrderFiltersFromText(text: string): OrderFilterData | null {
  if (!isOrderRelatedMessage(text)) return null;
  const t = text.trim();
  const page = resolveOrderPage(t);
  const out: OrderFilterData = { page };

  const orderNo = parseOrderNoFromText(t);
  if (orderNo) {
    if (/gid:/i.test(orderNo)) {
      out.orderNo = orderNo;
    } else if (/^\d{10,}$/.test(orderNo)) {
      out.shopifyOrderId = orderNo;
    } else {
      out.orderNo = orderNo;
    }
  }

  const influencer = parseInfluencerForOrder(t);
  if (influencer) out.influencer = influencer;

  const discount = parseDiscountCodeFromText(t);
  if (discount) out.discountCode = discount;

  const tracking = t.match(/物流(?:单号)?[：:\s]*([A-Za-z0-9_-]{6,64})/i);
  if (tracking?.[1]) out.trackingNo = tracking[1];

  for (const { re, value } of CONVERSION_STATUS) {
    if (re.test(t)) {
      out.status = value;
      break;
    }
  }
  for (const { re, value } of COMMISSION_STATUS) {
    if (re.test(t) && /佣金/.test(t)) {
      out.commissionStatus = value;
      break;
    }
  }

  if (/待付款/.test(t) && page === '/order/conversion') out.tab = 'pending';
  if (/待发货|待揽收/.test(t)) out.tab = 'pending_ship';
  if (/已完成/.test(t)) out.tab = 'completed';

  return out;
}

export function orderPageLabel(path: OrderPagePath): string {
  switch (path) {
    case '/order/sample':
      return '红人订单（样品）';
    case '/commission/order':
      return '分佣订单';
    default:
      return '转化订单';
  }
}

export function describeOrderFilterData(data: OrderFilterData): CopilotSummaryStep[] {
  const steps: CopilotSummaryStep[] = [{ label: '订单页', value: orderPageLabel(data.page) }];
  if (data.orderNo) steps.push({ label: '交易号', value: data.orderNo });
  if (data.shopifyOrderId) steps.push({ label: '订单号', value: data.shopifyOrderId });
  if (data.influencer) steps.push({ label: '关联红人', value: data.influencer });
  if (data.discountCode) steps.push({ label: '折扣码', value: data.discountCode });
  if (data.status) steps.push({ label: '订单状态', value: data.status });
  if (data.commissionStatus) steps.push({ label: '佣金', value: data.commissionStatus });
  if (data.trackingNo) steps.push({ label: '物流单号', value: data.trackingNo });
  if (data.tab) steps.push({ label: 'Tab', value: data.tab });
  return steps;
}

export function orderFiltersToQuery(
  data: OrderFilterData,
  opts?: { reset?: boolean }
): Record<string, string> {
  const q: Record<string, string> = { fromAi: '1', page: '1' };
  if (opts?.reset) q.resetAi = '1';
  if (data.tab) q.tab = data.tab;
  if (data.orderNo) q.orderNo = data.orderNo;
  if (data.shopifyOrderId) q.shopifyOrderId = data.shopifyOrderId;
  if (data.influencer) q.influencer = data.influencer;
  if (data.discountCode) q.discountCode = data.discountCode;
  if (data.trackingNo) q.trackingNo = data.trackingNo;
  if (data.status) q.status = data.status;
  if (data.commissionStatus) q.commissionStatus = data.commissionStatus;
  if (data.customerEmail) q.customerEmail = data.customerEmail;
  return q;
}
