import http from '@/utils/http';

export interface EccangStoreConfig {
  id?: number;
  storeName: string;
  eccangStoreCode?: string;
  platform?: string;
  platformAccounts?: string[];
  activeProductCount?: number;
  inactiveProductCount?: number;
  totalProductCount?: number;
  activeSkuCount?: number;
  inactiveSkuCount?: number;
  totalSkuCount?: number;
  accountCounts?: {
    site: string;
    userAccount: string;
    activeProductCount: number;
    inactiveProductCount: number;
    totalProductCount: number;
    activeSkuCount?: number;
    inactiveSkuCount?: number;
    totalSkuCount?: number;
  }[];
  createdAt?: string;
  updatedAt?: string;
}

export interface EccangConfigDto {
  id?: number;
  appKey?: string;
  appSecret?: string;
  accessToken?: string;
  isConfigured?: boolean;
}

export interface EccangProductItem {
  sku: string;
  spu?: string;
  asin: string;
  name: string;
  mainImage: string;
  price: string;
  fbaInventory: number;
  storeId: number;
  storeName: string;
  siteCode?: string;
  platformAccount?: string;
  platformSku?: string;
  amazonShopName?: string;
  createdAt?: string;
  variants?: any[];
}

export const getEccangConfig = async (): Promise<EccangConfigDto> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-config');
    return response.data || {};
  } catch (error) {
    console.error('Failed to fetch Eccang config', error);
    return {};
  }
};

export const saveEccangConfig = async (config: EccangConfigDto): Promise<EccangConfigDto> => {
  const response = await http.post('/eccang/v1.0/eccang-config', config);
  return response.data;
};

// Get configured stores
export const getEccangStores = async (): Promise<EccangStoreConfig[]> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-stores');
    return response.data?.content || response.data || [];
  } catch (error) {
    console.error('Failed to fetch Eccang stores', error);
    return [];
  }
};

// Fetch products from our backend (which synced with Eccang)
export const getEccangProducts = async (params: any): Promise<EccangProductItem[] | { content: EccangProductItem[]; totalElements: number; totalPages: number }> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-products', { params });
    const data = response.data;
    if (data?.content) {
      return data;
    }
    return data?.data || data || [];
  } catch (error) {
    console.error('Failed to fetch Eccang products', error);
    return [];
  }
};

export const getEccangProductStatistics = async (params: any): Promise<Record<string, number>> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-products/statistics', { params });
    return response.data || {};
  } catch (error) {
    console.error('Failed to fetch product statistics', error);
    return {};
  }
};

export const getEccangPlatformAccounts = async (): Promise<string[]> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-products/platform-accounts');
    return response.data || [];
  } catch (error) {
    console.error('Failed to fetch platform accounts', error);
    return [];
  }
};

export const getEccangSites = async (): Promise<string[]> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-products/sites');
    return response.data || [];
  } catch (error) {
    console.error('Failed to fetch sites', error);
    return [];
  }
};

export interface EccangSyncProgress {
  status: string;
  message?: string;
  processed?: number;
  total?: number;
  success?: number;
  error?: number;
  added?: number;
  updated?: number;
  spuAdded?: number;
  spuUpdated?: number;
  progress?: number;
  errorDetails?: string;
}

export const syncEccangProducts = async (storeId: number, mode?: string): Promise<{ success: boolean; message: string }> => {
  const response = await http.post('/eccang/v1.0/eccang-products/sync', null, { params: { storeId, mode } });
  return response.data;
};

export const syncEccangFbaInventory = async (storeId: number, onlyActive?: boolean): Promise<{ success: boolean; message: string }> => {
  const response = await http.post('/eccang/v1.0/eccang-products/sync/fba', null, { params: { storeId, onlyActive } });
  return response.data;
};

export const getEccangProductSyncProgress = async (storeId: number): Promise<EccangSyncProgress> => {
  const response = await http.get('/eccang/v1.0/eccang-products/sync/progress', { params: { storeId } });
  return response.data || { status: 'IDLE' };
};

export const clearEccangProductSyncProgress = async (storeId: number): Promise<void> => {
  await http.post('/eccang/v1.0/eccang-products/sync/clear', null, { params: { storeId } });
};

export const updateEccangStore = async (id: number, store: Partial<EccangStoreConfig>): Promise<EccangStoreConfig> => {
  const response = await http.put(`/eccang/v1.0/eccang-stores/${id}`, store);
  return response.data;
};

