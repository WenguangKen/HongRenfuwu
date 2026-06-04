export interface OrderProduct {
  sku: string;
  spu?: string;
  variantTitle?: string;
  shopifyVariantId?: string | number;
  shopifyLineItemId?: string | number;
  quantity: number;
  returnedQuantity?: number;
  isReturned?: boolean;
  price: number;
  image?: string;
  status?: string;
  packageNo?: string;
}

export interface ConversionOrderRow {
  key: number | string;
  orderNo: string;
  shortOrderNo?: string;
  longOrderNo?: string;
  products: OrderProduct[];
  productAmount: string;
  discountCode?: string;
  address?: string;
  email?: string;
  phone?: string;
  receiverPhone?: string;
  postalCode?: string;
  receiverName?: string;
  totalAmount?: string;
  orderStatus: string;
  logisticsStatus?: string;
  logisticsName?: string;
  trackingNo?: string;
  timeInfo?: {
    createTime?: string;
    paymentTime?: string;
    shipTime?: string;
    deliveredTime?: string;
    completedTime?: string;
    cancelApplyTime?: string;
    cancelTime?: string;
    exceptionTime?: string;
  };
  packageNo?: string;
  isSplit?: boolean;
  storeName?: string;
  [extra: string]: unknown;
}

export interface SampleOrderRow {
  key: number | string;
  orderNo: string;
  shortOrderNo?: string;
  longOrderNo?: string;
  packageNo?: string;
  isSplit?: boolean;
  storeName?: string;
  influencer?: string;
  products: OrderProduct[];
  logisticsStatus: string;
  orderStatus: string;
  receiverName?: string;
  timeInfo?: {
    createTime?: string;
    paymentTime?: string;
    shipTime?: string;
    deliveredTime?: string;
    completedTime?: string;
    cancelApplyTime?: string;
    cancelTime?: string;
  };
  [extra: string]: unknown;
}

export interface CommissionOrderRow {
  key: number | string;
  orderNo?: string;
  shortOrderNo?: string;
  longOrderNo?: string;
  packageNo?: string;
  influencerName?: string;
  influencerId?: string;
  orderAmount: string;
  productAmount?: string;
  products: OrderProduct[];
  discountCode?: string;
  commissionRate: string;
  /** 佣金类型：红人 / 折扣 */
  commissionType?: 'influencer' | 'discount';
  commissionAmount: string;
  timeInfo?: {
    createTime?: string;
    paymentTime?: string;
    deliveredTime?: string;
    distributeTime?: string;
  };
  [extra: string]: unknown;
}
