import apiService from '@/utils/api';

/**
 * 角色管理服务
 */

export interface Role {
  id: number;
  name: string;
  description?: string;
  status: 'enabled' | 'disabled';
  createdAt: string;
  updatedAt: string;
  permissions?: Array<{
    id: number;
    permissionKey: string;
    title: string;
    type: string;
  }>;
  userCount?: number;
}

export interface RoleCreateRequest {
  name: string;
  description?: string;
  permissionIds: number[];
}

export interface RoleUpdateRequest {
  name?: string;
  description?: string;
  permissionIds?: number[];
  status?: 'enabled' | 'disabled';
}

export interface RolePermissionRequest {
  pagePermissions: string[];
  operationPermissions: string[];
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

/**
 * 获取角色列表
 */
export async function getRoleList(params: {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: 'ASC' | 'DESC';
}): Promise<PaginatedResponse<Role>> {
  return apiService.get<PaginatedResponse<Role>>('/v1.0/roles', { params });
}

/**
 * 获取角色详情
 */
export async function getRoleById(id: number): Promise<Role> {
  return apiService.get<Role>(`/v1.0/roles/${id}`);
}

/**
 * 创建角色
 */
export async function createRole(request: RoleCreateRequest): Promise<Role> {
  return apiService.post<Role>('/v1.0/roles', request, {
    showSuccess: true,
    successMessage: '角色创建成功',
  });
}

/**
 * 更新角色
 */
export async function updateRole(id: number, request: RoleUpdateRequest): Promise<Role> {
  // Note: showSuccess disabled here to avoid duplicate messages when saving with permissions
  return apiService.put<Role>(`/v1.0/roles/${id}`, request);
}

/**
 * 删除角色
 */
export async function deleteRole(id: number): Promise<void> {
  return apiService.delete<void>(`/v1.0/roles/${id}`, {
    showSuccess: true,
    successMessage: '角色删除成功',
  });
}

/**
 * 更新角色权限
 */
export async function updateRolePermissions(id: number, request: RolePermissionRequest): Promise<void> {
  // Note: showSuccess handled by the caller or permissionService version
  return apiService.put<void>(`/v1.0/roles/${id}/permissions`, request);
}

/**
 * 获取角色关联的用户列表
 */
export async function getRoleUsers(roleId: number): Promise<Array<{
  id: number;
  username: string;
  email: string;
  status: 'active' | 'inactive';
}>> {
  return apiService.get<Array<{
    id: number;
    username: string;
    email: string;
    status: 'active' | 'inactive';
  }>>(`/v1.0/roles/${roleId}/users`);
}

