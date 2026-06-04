/**
 * 用户状态管理 Store
 *
 * 管理当前登录用户的认证状态：
 * - JWT Token 的存储与读取
 * - 用户基本信息（姓名/邮箱/角色等）
 * - 登录/登出流程
 * - 权限同步
 *
 * 持久化机制：通过 localStorage 存储 Token 和用户信息，
 * 页面刷新后自动恢复登录状态
 *
 * @module stores/user
 */
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { login as apiLogin, logout as apiLogout, getCurrentUser, type LoginRequest, type LoginResponse } from '@/services/authService';
import type { ApiRequestConfig } from '@/utils/api';
import { fetchCurrentUserPermissions } from '@/services/permissionService';
import { usePermissionStore } from './permission';

export const useUserStore = defineStore('user', () => {
    /** localStorage 中 Token 的存储 key */
    const AUTH_KEY = 'auth_token';

    /**
     * 从 localStorage 读取 Token
     * 自动检查过期时间，过期后清除并返回 null
     */
    const readAuth = () => {
        const raw = localStorage.getItem(AUTH_KEY);
        if (!raw) return null;
        try {
            const obj = JSON.parse(raw);
            if (!obj || typeof obj !== 'object') return null;
            // 检查 Token 是否已过期
            const expiresAt = Number(obj.expiresAt) || 0;
            if (expiresAt && Date.now() > expiresAt) {
                localStorage.removeItem(AUTH_KEY);
                return null;
            }
            return String(obj.token || '');
        } catch {
            return null;
        }
    };

    /** 从 localStorage 读取用户信息 */
    const readUserInfo = () => {
        const raw = localStorage.getItem('user_info');
        if (!raw) return null;
        try { return JSON.parse(raw); } catch { return null; }
    };

    /** 当前 JWT Token（null 表示未登录） */
    const token = ref<string | null>(readAuth());

    /** 当前登录用户信息 */
    const userInfo = ref<LoginResponse['user'] | null>(readUserInfo());

    /**
     * 用户登录
     * 流程：调用登录 API → 存储 Token → 存储用户信息 → 加载权限
     *
     * @param loginForm - 登录表单（邮箱/密码/验证码）
     * @param config - 可选的请求配置
     * @throws 登录失败时抛出异常
     */
    const login = async (loginForm: LoginRequest, config?: ApiRequestConfig) => {
        try {
            const response = await apiLogin(loginForm, config);

            // 存储 Token 和过期时间
            token.value = response.token;
            const expiresAt = Date.now() + response.expiresIn * 1000;
            localStorage.setItem(AUTH_KEY, JSON.stringify({
                token: response.token,
                expiresAt
            }));

            // 存储用户信息
            userInfo.value = response.user;
            localStorage.setItem('user_info', JSON.stringify(response.user));

            // 登录成功后立即获取用户权限
            const permissionStore = usePermissionStore();
            const permissions = await fetchCurrentUserPermissions();
            if (permissions) {
                permissionStore.setPagePermissions(permissions.pagePermissions);
                permissionStore.setOperationPermissions(permissions.operationPermissions);
            }
        } catch (error) {
            throw error;
        }
    };

    /**
     * 用户登出
     * 流程：调用登出 API（可失败） → 清除本地 Token 和用户信息
     */
    const logout = async () => {
        try {
            await apiLogout();
        } catch (error) {
            console.error('登出接口调用失败', error);
        } finally {
            // 无论接口是否成功，都清除本地登录状态
            token.value = null;
            userInfo.value = null;
            localStorage.removeItem(AUTH_KEY);
            localStorage.removeItem('user_info');
            usePermissionStore().resetPermissions();
        }
    };

    /**
     * 刷新用户信息
     * 用于页面刷新后重新获取最新的用户信息
     * 如果 Token 已失效（401），自动触发登出
     */
    const loadUserInfo = async () => {
        try {
            const info = await getCurrentUser();
            userInfo.value = info;
            localStorage.setItem('user_info', JSON.stringify(info));
        } catch (error) {
            console.error('获取用户信息失败', error);
            if ((error as any)?.response?.status === 401) {
                logout();
            }
        }
    };

    return {
        token,
        userInfo,
        login,
        logout,
        loadUserInfo
    };
});
