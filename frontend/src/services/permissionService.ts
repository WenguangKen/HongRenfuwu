import apiService from '@/utils/api';
import type { ApiRequestConfig } from '@/utils/api';

export interface PermissionPayload {
  pagePermissions: string[];
  operationPermissions: string[];
}

export interface PermissionInfo {
  id: number;
  permissionKey: string;
  title: string;
  type: 'page' | 'tab' | 'operation';
  parentKey?: string;
  description?: string;
}

/**
 * 获取当前用户权限
 */
export async function fetchCurrentUserPermissions(config?: ApiRequestConfig): Promise<PermissionPayload | null> {
  try {
    const data = await apiService.get<PermissionPayload>('/v1.0/permissions/me', {
      ...config,
      showError: false, // 权限获取失败不显示错误提示
    });

    if (data && Array.isArray(data.pagePermissions) && Array.isArray(data.operationPermissions)) {
      return data;
    }
    return null;
  } catch {
    return null;
  }
}

/**
 * 获取所有权限
 */
export async function getAllPermissions(): Promise<PermissionInfo[]> {
  return apiService.get<PermissionInfo[]>('/v1.0/permissions');
}

/**
 * 更新角色权限
 */
export async function updateRolePermissions(
  roleId: number,
  payload: PermissionPayload,
  config?: ApiRequestConfig
): Promise<boolean> {
  try {
    await apiService.put(`/v1.0/roles/${roleId}/permissions`, payload, {
      ...config,
      showSuccess: true,
      successMessage: '保存成功',
    });
    return true;
  } catch {
    return false;
  }
}
