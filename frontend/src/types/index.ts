export interface Influencer {
  id: string | number;
  key?: string | number;
  name: string;
  avatar?: string;
  platforms?: string[];
  country?: string;
  email?: string;
  link?: string;
  isPaid?: boolean;
  source?: string;
  sourceType?: string;
  sourceValue?: string;
  influencerType?: string;
  tags?: string[];
  fans?: number | string;
  discountCode?: string;
  /** 红人佣金率（百分比数值，如 15 表示 15%） */
  commissionRate?: number;
  /** 折扣码佣金率（百分比数值，如 12 表示 12%） */
  discountCommissionRate?: number;
  owner?: string;
  status?: string;
  contentCount?: number;
  sampleCount?: number;
  conversionCount?: number;
  createdAt?: string;
  updatedAt?: string;
  socialMediaList?: Array<{ platform: string; name: string; link: string; fans: number; editing: boolean }>;
  addressList?: Array<{ recipientName: string; phone: string; email: string; postalCode: string; address: string; editing: boolean }>;
  logs?: InfluencerLog[];
  payments?: any[];
  sampleOrders?: any[];
}

export interface InfluencerLog {
  type: string;
  time: string;
  content: string;
  operator?: string;
  details?: Record<string, string>;
}

export interface Order {
  orderNo: string;
  status?: string;
  skus?: string[];
  createTime?: string;
}

/**
 * 导出列配置类型
 */
export interface ExportColumn<T = unknown> {
  key: string;
  title: string;
  dataKey?: string;
  formatter?: (value: unknown, record: T) => string;
}

/**
 * 批量流转项（带原因）
 */
export interface BatchTransferItem {
  id: string | number;
  name: string;
  reason?: string;
}

/**
 * Select Option 类型
 */
export interface SelectOption {
  value: string | number | boolean;
  label: string;
  children?: string;
}

/**
 * 表格变更参数类型
 */
export interface TableChangeParams {
  pagination?: {
    current?: number;
    pageSize?: number;
    total?: number;
  };
  filters?: Record<string, any[]>;
  sorter?: {
    field?: string;
    order?: 'ascend' | 'descend';
  };
}
