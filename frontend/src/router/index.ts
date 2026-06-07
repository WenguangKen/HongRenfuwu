import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import UserLayout from '@/layouts/UserLayout.vue'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import { useTabsStore } from '@/stores/tabs'
import { message } from 'ant-design-vue'
import { cancelAllRequests } from '@/utils/http'
import { startRouteProgress, finishRouteProgress } from '@/utils/routeProgress'
import contentRoutes from './modules/content'
import influencerRoutes from './modules/influencer'
import outreachRoutes from './modules/outreach'
import productRoutes from './modules/product'
import systemRoutes from './modules/system'
import financeRoutes from './modules/finance'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/user/login',
            name: 'login',
            component: () => import('@/views/auth/LoginView.vue')
        },
        {
            path: '/no-permission',
            component: BasicLayout,
            children: [
                {
                    path: '',
                    name: 'no-permission',
                    component: () => import('@/views/auth/NoPermissionView.vue'),
                    meta: { title: '无权限提示' }
                }
            ]
        },
        {
            path: '/',
            component: BasicLayout,
            redirect: '/dashboard',
            children: ([
                {
                    path: 'dashboard',
                    name: 'dashboard',
                    component: () => import('@/views/dashboard/DashboardView.vue'),
                    meta: { title: '仪表盘', pageKey: 'dashboard' },
                    beforeEnter: (to, from, next) => {
                        // 智能重定向：如果从根路径进入且有最后活跃路径，优先跳转
                        const tabsStore = useTabsStore();
                        if (from.path === '/' && tabsStore.activePath && tabsStore.activePath !== '/dashboard') {
                            next(tabsStore.activePath);
                        } else {
                            next();
                        }
                    }
                },
            ] as RouteRecordRaw[]).concat(
                contentRoutes,
                influencerRoutes,
                outreachRoutes,
                financeRoutes,
                productRoutes,
                systemRoutes
            )
        }
    ]
})

router.beforeEach(async (to, from, next) => {
    if (to.path !== from.path) {
        startRouteProgress();
    }
    const userStore = useUserStore();
    const permStore = usePermissionStore();

    // 获取 Token 的辅助函数
    const getValidToken = () => {
        // 优先使用 store 里的内存值（最快、最新）
        if (userStore.token) return userStore.token;

        // 降级使用 localStorage
        const raw = localStorage.getItem('auth_token');
        if (!raw) return null;
        try {
            const obj = JSON.parse(raw);
            const expiresAt = Number(obj?.expiresAt) || 0;
            if (expiresAt && Date.now() > expiresAt) {
                localStorage.removeItem('auth_token');
                return null;
            }
            const t = String(obj?.token || '');
            // 同步回 store 内存，防止后续抖动
            if (t) userStore.token = t;
            return t;
        } catch {
            return null;
        }
    };

    const token = getValidToken();
    const isLoginPage = to.path.startsWith('/user');
    const isNoPermissionPage = to.path === '/no-permission';
    const isProfilePage = to.path === '/system/profile';

    // 1. 未登录用户重定向到登录页
    if (!isLoginPage && !isNoPermissionPage && !token) {
        next({ path: '/user/login', query: { redirect: to.fullPath } });
        return;
    }

    // 2. 已登录用户逻辑
    if (token) {
        // 2.1 如果还没有加载用户信息（页面刷新场景），尝试同步加载一次
        if (!userStore.userInfo && !isLoginPage) {
            await userStore.loadUserInfo();
        }

        // 2.2 权限尚未从服务端同步时刷新一次（登录流程内已同步则跳过）
        if (!permStore.syncedFromServer && !isLoginPage && !isNoPermissionPage) {
            await permStore.refreshPermissions();
        }

        // 2.3 已登录用户访问登录页 -> 重定向到首页
        if (isLoginPage) {
            if (permStore.pagePermissions.length === 0) {
                next({ path: '/no-permission' });
            } else {
                next({ path: '/dashboard' });
            }
            return;
        }
    }

    // 3. 允许访问基础页面
    if (isProfilePage || isNoPermissionPage) {
        if (to.meta?.title) {
            document.title = `${to.meta.title} - 红人营销系统`;
        }
        next();
        return;
    }

    // 4. 检查页面权限
    // 类型安全的meta访问
    const pageKey = (to.meta as { pageKey?: string })?.pageKey;
    const allowed = permStore.isAllowedPage(pageKey);

    // 如果没有权限
    if (!allowed) {
        // 如果没有任何页面权限，重定向到无权限提示页
        if (permStore.pagePermissions.length === 0) {
            if (to.path !== '/no-permission') {
                next({ path: '/no-permission' });
            } else {
                next();
            }
            return;
        }
        // 如果有部分权限但当前页面无权限，静默重定向到首页
        if (to.path !== '/dashboard') {
            next({ path: '/dashboard' });
        } else {
            next();
        }
        return;
    }

    // 5. 准许访问
    if (to.meta?.title) {
        document.title = `${to.meta.title} - 红人营销系统`;
    }
    next();
});

router.onError(() => {
    finishRouteProgress();
});

router.afterEach((to, from) => {
    if (to.path !== from.path) {
        finishRouteProgress();
    }
    // 只有在路径改变时才取消请求（避免由 query 参数变化导致的请求取消）
    if (to.path !== from.path) {
        cancelAllRequests();
    }
    try {
        import('@/utils/analytics').then(m => m.trackPageView(to.fullPath));
    } catch {
    }
});

export default router
