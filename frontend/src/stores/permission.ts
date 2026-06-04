/**
 * 权限管理 Store
 *
 * 管理当前用户的权限状态：
 * - 页面级权限（控制菜单可见性）
 * - 操作级权限（控制按钮/功能可用性）
 *
 * 权限来源：登录后从后端 /permissions/me 接口获取
 * 持久化：通过 localStorage 缓存，页面刷新后恢复
 *
 * 使用方式：
 * - v-permission="'user:create'" 指令（模板中）
 * - isAllowedOp('user:create') 方法（脚本中）
 * - isAllowedPage('user') 方法（路由守卫中）
 *
 * @module stores/permission
 */
import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

export const usePermissionStore = defineStore('permission', () => {
  /** localStorage 存储 key */
  const STORAGE_KEY = 'permissions';

  /** 页面级权限列表（如 'dashboard', 'user', 'influencer'） */
  const pagePermissions = ref<string[]>(['dashboard']); // 默认仅展示看板

  /** 是否已从服务端同步过权限（避免登录后重复请求） */
  const syncedFromServer = ref(false);

  /**
   * 操作级权限列表
   * - 正常模式：从后端获取具体权限列表
   * - 开发调试：设置 VITE_DEV_ALL_OPS=true 可授予全权限 '*'
   */
  const enableDevAllOps = String(import.meta.env?.VITE_DEV_ALL_OPS) === 'true';
  const defaultOperationPermissions = enableDevAllOps ? ['*'] : [];
  const operationPermissions = ref<string[]>(defaultOperationPermissions);

  // 启动时从 localStorage 恢复权限状态
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed?.pagePermissions)) {
        pagePermissions.value = parsed.pagePermissions;
      }
      if (Array.isArray(parsed?.operationPermissions)) {
        operationPermissions.value = parsed.operationPermissions;
      }
      if (Array.isArray(parsed?.pagePermissions) && parsed.pagePermissions.length > 0) {
        syncedFromServer.value = true;
      }
    }
  } catch { /* localStorage 读取失败时使用默认值 */ }

  /**
   * 检查是否有页面访问权限
   * @param key - 页面标识（如 'user', 'influencer'）
   * @returns true 表示有权限
   */
  const isAllowedPage = (key?: string) => {
    if (!key) return true;
    return pagePermissions.value.includes(key);
  };

  /**
   * 检查是否有操作执行权限
   * @param key - 操作标识（如 'user:create', 'influencer:delete'）
   * @returns true 表示有权限
   */
  const isAllowedOp = (key?: string) => {
    if (!key) return true;
    if (operationPermissions.value.includes('*')) return true; // 超级管理员
    return operationPermissions.value.includes(key);
  };

  /** 设置页面权限列表 */
  const setPagePermissions = (keys: string[]) => {
    pagePermissions.value = [...keys];
    if (keys.length > 0) syncedFromServer.value = true;
  };

  /** 设置操作权限列表 */
  const setOperationPermissions = (keys: string[]) => {
    operationPermissions.value = [...keys];
  };

  /** 登出时重置权限缓存状态 */
  const resetPermissions = () => {
    pagePermissions.value = ['dashboard'];
    operationPermissions.value = defaultOperationPermissions;
    syncedFromServer.value = false;
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch { /* ignore */ }
  };

  // 权限变更时自动持久化到 localStorage
  watch([pagePermissions, operationPermissions], () => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        pagePermissions: pagePermissions.value,
        operationPermissions: operationPermissions.value,
      }));
    } catch (error: unknown) {
      if (typeof window !== 'undefined' && (window as any).logger) {
        (window as any).logger.error('保存权限配置失败', error);
      }
    }
  });

  /**
   * 从后端刷新权限
   * 使用动态 import 避免循环依赖
   * @returns true 表示刷新成功
   */
  const refreshPermissions = async () => {
    try {
      const { fetchCurrentUserPermissions } = await import('@/services/permissionService');
      const newPerms = await fetchCurrentUserPermissions({ showError: false });
      if (newPerms) {
        setPagePermissions(newPerms.pagePermissions);
        setOperationPermissions(newPerms.operationPermissions);
        return true;
      }
      return false;
    } catch (error) {
      console.error('刷新权限失败', error);
      return false;
    }
  };

  return {
    pagePermissions,
    operationPermissions,
    syncedFromServer,
    isAllowedPage,
    isAllowedOp,
    setPagePermissions,
    setOperationPermissions,
    refreshPermissions,
    resetPermissions
  };
});
