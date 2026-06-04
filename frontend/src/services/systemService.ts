import apiService from '@/utils/api';

/**
 * 系统管理服务
 */

export interface DatabaseConfig {
  id: number;
  configType: 'master' | 'slave';
  host: string;
  port: number;
  database: string;
  username: string;
  description?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DatabaseConfigRequest {
  configType: 'master' | 'slave';
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  description?: string;
  isActive?: boolean;
}

/**
 * 获取数据库配置列表
 */
export async function getDatabaseConfigs(configType?: 'master' | 'slave'): Promise<DatabaseConfig[]> {
  return apiService.get<DatabaseConfig[]>('/v1.0/system/database-configs', {
    params: configType ? { configType } : undefined,
  });
}

/**
 * 获取数据库配置详情
 */
export async function getDatabaseConfigById(id: number): Promise<DatabaseConfig> {
  return apiService.get<DatabaseConfig>(`/v1.0/system/database-configs/${id}`);
}

/**
 * 创建数据库配置
 */
export async function createDatabaseConfig(request: DatabaseConfigRequest): Promise<DatabaseConfig> {
  return apiService.post<DatabaseConfig>('/v1.0/system/database-configs', request, {
    showSuccess: true,
    successMessage: '数据库配置创建成功',
  });
}

/**
 * 更新数据库配置
 */
export async function updateDatabaseConfig(id: number, request: DatabaseConfigRequest): Promise<DatabaseConfig> {
  return apiService.put<DatabaseConfig>(`/v1.0/system/database-configs/${id}`, request, {
    showSuccess: true,
    successMessage: '数据库配置更新成功',
  });
}

/**
 * 删除数据库配置
 */
export async function deleteDatabaseConfig(id: number): Promise<void> {
  return apiService.delete<void>(`/v1.0/system/database-configs/${id}`, {
    showSuccess: true,
    successMessage: '数据库配置删除成功',
  });
}

/**
 * 测试数据库连接
 */
export async function testDatabaseConnection(request: DatabaseConfigRequest): Promise<void> {
  return apiService.post<void>('/v1.0/system/database-configs/test', request, {
    showSuccess: true,
    successMessage: '数据库连接测试成功',
  });
}


/**
 * 系统配置相关接口
 */
export interface SystemConfigDto {
  key: string;
  value: string;
  description?: string;
  isEncrypted: boolean;
}

/**
 * 获取系统配置
 * @param key 配置键
 * @param reveal 是否解密显示 (仅加密项有效)
 */
export async function getSystemConfig(key: string, reveal = false): Promise<SystemConfigDto> {
  return apiService.get<SystemConfigDto>(`/v1.0/system/configs/${key}`, {
    params: { reveal },
    showError: false   // 404 = 配置尚未创建，属正常情况
  });
}

/**
 * 保存系统配置
 */
export async function saveSystemConfig(config: SystemConfigDto): Promise<void> {
  return apiService.post<void>('/v1.0/system/configs', config, {
    showSuccess: true,
    successMessage: '配置保存成功'
  });
}
