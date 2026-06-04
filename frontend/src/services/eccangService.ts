import http from '@/utils/http';

export interface EccangStoreConfig {
  id?: number;
  storeName: string;
  eccangStoreCode?: string;
  platform?: string;
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
export const getEccangProducts = async (params: any): Promise<EccangProductItem[]> => {
  try {
    const response = await http.get('/eccang/v1.0/eccang-products', { params });
    return response.data?.data || response.data?.content || response.data || [];
  } catch (error) {
    console.error('Failed to fetch Eccang products', error);
    return [];
  }
};
