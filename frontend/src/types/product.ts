export type ProductStatus = 'active' | 'inactive' | 'draft' | 'out_of_stock' | 'suspected_deleted' | string;

export interface ProductLog {
  time: string;
  status: string;
  details?: string;
  operator?: string;
}

export interface ProductItem {
  key: number | string;
  image: string;
  skuImage?: string;
  name: string;
  spu?: string;
  asin?: string;
  parentAsin?: string;
  displayAsin?: string;
  asinTooltip?: string;
  hasMultipleAsins?: boolean;
  shopifyId?: string;
  sku: string;
  price: string;
  link: string;
  status: ProductStatus;
  inventory?: number;
  fbaInventory?: number;
  createTime?: string;
  publishTime?: string;
  offShelfTime?: string;
  outOfStockTime?: string;
  suspectedDeletedTime?: string;
  description?: string;
  logs?: ProductLog[];
  shopName?: string;
  platform?: string;
  storeId?: number;
  variants?: any[]; // Allow variants for filtering logic
  [extra: string]: any;
}
