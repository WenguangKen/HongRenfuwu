/**
 * 集中管理 auth_token 读取逻辑
 */

/** localStorage 中 Token 的存储 key */
const AUTH_KEY = 'auth_token';

/**
 * 从 localStorage 读取 Token
 * 自动检查过期时间，过期后清除并返回 null
 */
export const readAuthToken = (): string | null => {
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
