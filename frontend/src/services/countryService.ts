import apiService from '@/utils/api';

export interface Country {
    id: number;
    code: string;
    nameCn: string;
    nameEn?: string;
    phonePrefix?: string;
    timezone?: string;
    sortOrder?: number;
    enabled?: boolean;
}

export interface CountryListResponse {
    success: boolean;
    data: Country[];
    total: number;
}

export interface CountryResponse {
    success: boolean;
    data?: Country;
    message?: string;
}

const BASE_URL = '/v1.0/countries';

/**
 * 获取启用的国家列表（订单使用）
 */
export const getEnabledCountries = async (): Promise<Country[]> => {
    const res = await apiService.get<Country[]>(`${BASE_URL}/enabled`);
    return res || [];
};

/**
 * 获取所有国家列表（管理使用）
 */
export const getAllCountries = async (): Promise<Country[]> => {
    const res = await apiService.get<Country[]>(BASE_URL);
    return res || [];
};

/**
 * 创建国家
 */
export const createCountry = async (country: Partial<Country>): Promise<CountryResponse> => {
    return await apiService.post<CountryResponse>(BASE_URL, country);
};

/**
 * 更新国家
 */
export const updateCountry = async (id: number, country: Partial<Country>): Promise<CountryResponse> => {
    return await apiService.put<CountryResponse>(`${BASE_URL}/${id}`, country);
};

/**
 * 删除国家
 */
export const deleteCountry = async (id: number): Promise<CountryResponse> => {
    return await apiService.delete<CountryResponse>(`${BASE_URL}/${id}`);
};

export interface Province {
    id: number;
    code: string; // province_code
    name: string; // province_name
    provinceCode: string; // alias for code
    provinceName: string; // alias for name
}

// ... existing code ...

/**
 * 获取指定国家的省份列表
 */
export const getProvinces = async (countryCode: string): Promise<Province[]> => {
    const res = await apiService.get<Province[]>(`${BASE_URL}/${countryCode}/provinces`);
    return res || [];
};

/**
 * 获取所有省份列表
 */
export const getAllProvinces = async (): Promise<Province[]> => {
    const res = await apiService.get<Province[]>(`${BASE_URL}/provinces/all`);
    return res || [];
};

/**
 * 切换启用状态
 */
export const toggleCountryEnabled = async (id: number): Promise<CountryResponse> => {
    return await apiService.post<CountryResponse>(`${BASE_URL}/${id}/toggle`);
};

