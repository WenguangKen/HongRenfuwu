import apiService from '@/utils/api';

/**
 * 用户管理服务
 */

export interface User {
  id: number;
  username: string;
  email: string;
  phone?: string;
  status: 'active' | 'inactive';
  avatarUrl?: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  lastLoginLocation?: string;
  createdAt: string;
  updatedAt: string;
  roles?: Array<{
    id: number;
    name: string;
    description?: string;
  }>;
}

export interface UserCreateRequest {
  username: string;
  email: string;
  password: string;
  phone?: string;
  roleIds: number[];
  avatarUrl?: string;
}

export interface UserUpdateRequest {
  username?: string;
  email?: string;
  phone?: string;
  roleIds?: number[];
  avatarUrl?: string;
}

export interface UserPasswordRequest {
  oldPassword?: string;
  newPassword: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface UserLog {
  id: number;
  userId: number;
  actionType: string;
  actionDesc: string;
  ipAddress?: string;
  userAgent?: string;
  details?: string;
  location?: string;
  createdAt: string;
}

/**
 * 获取用户列表
 */
export async function getUserList(params: {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: 'ASC' | 'DESC';
  username?: string;
  role?: string;
  phone?: string;
  email?: string;
  status?: 'active' | 'inactive';
}): Promise<PaginatedResponse<User>> {
  return apiService.get<PaginatedResponse<User>>('/v1.0/users', { params });
}

/**
 * 获取用户详情
 */
export async function getUserById(id: number): Promise<User> {
  return apiService.get<User>(`/v1.0/users/${id}`);
}

/**
 * 创建用户
 */
export async function createUser(request: UserCreateRequest): Promise<User> {
  return apiService.post<User>('/v1.0/users', request, {
    showSuccess: true,
    successMessage: '用户创建成功',
  });
}

/**
 * 更新用户
 */
export async function updateUser(id: number, request: UserUpdateRequest): Promise<User> {
  return apiService.put<User>(`/v1.0/users/${id}`, request, {
    showSuccess: true,
    successMessage: '用户更新成功',
  });
}

/**
 * 更新用户状态
 */
export async function updateUserStatus(id: number, status: 'active' | 'inactive'): Promise<void> {
  return apiService.put<void>(`/v1.0/users/${id}/status`, null, {
    params: { status },
    showSuccess: true,
    successMessage: '用户状态更新成功',
  });
}

/**
 * 修改用户密码
 */
export async function changeUserPassword(id: number, request: UserPasswordRequest): Promise<void> {
  return apiService.put<void>(`/v1.0/users/${id}/password`, request, {
    showSuccess: true,
    successMessage: '密码修改成功，请重新登录',
  });
}

/**
 * 获取用户操作日志
 */
export async function getUserLogs(id: number, params: {
  page?: number;
  size?: number;
}): Promise<PaginatedResponse<UserLog>> {
  return apiService.get<PaginatedResponse<UserLog>>(`/v1.0/users/${id}/logs`, { params });
}
