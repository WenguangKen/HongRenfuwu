/**
 * 认证服务模块
 *
 * 负责用户认证相关操作：
 * - 获取登录验证码
 * - 用户登录/登出
 * - 获取当前登录用户信息
 *
 * @module authService
 */
import apiService from '@/utils/api';
import type { ApiRequestConfig } from '@/utils/api';

/* ========== 请求/响应类型定义 ========== */

/** 登录请求参数 */
export interface LoginRequest {
  email: string;        // 登录邮箱
  password: string;     // 登录密码
  captcha: string;      // 验证码
  captchaKey: string;   // 验证码标识 key
}

/** 登录成功响应 */
export interface LoginResponse {
  token: string;         // JWT 访问令牌
  refreshToken: string;  // 刷新令牌
  expiresIn: number;     // 令牌过期时间（秒）
  user: {
    id: number;                 // 用户 ID
    username: string;           // 用户名
    email: string;              // 邮箱
    phone?: string;             // 手机号
    avatarUrl?: string;         // 头像 URL
    roles?: Array<{ id: number; name: string; description: string }>; // 角色列表
    lastLoginIp?: string;       // 上次登录 IP
    lastLoginTime?: string;     // 上次登录时间
    lastLoginLocation?: string; // 上次登录地点
    createdAt?: string;         // 注册时间
  };
}

/** 验证码响应 */
export interface CaptchaResponse {
  captchaKey: string;     // 验证码唯一标识
  captchaImage?: string;  // Base64 编码的验证码图片
}

/* ========== API 方法 ========== */

/** 获取登录验证码 */
export async function getCaptcha(config?: ApiRequestConfig): Promise<CaptchaResponse> {
  return apiService.get<CaptchaResponse>('/v1.0/auth/captcha', config);
}

/** 用户登录 */
export async function login(request: LoginRequest, config?: ApiRequestConfig): Promise<LoginResponse> {
  return apiService.post<LoginResponse>('/v1.0/auth/login', request, config);
}

/** 用户登出（清除服务端会话） */
export async function logout(): Promise<void> {
  return apiService.post<void>('/v1.0/auth/logout', {});
}

/** 获取当前已登录用户信息 */
export async function getCurrentUser(): Promise<LoginResponse['user']> {
  return apiService.get<LoginResponse['user']>('/v1.0/auth/me');
}
