/**
 * 应用入口文件
 * 
 * 职责：
 * 1. 初始化 Vue 应用实例
 * 2. 注册全局插件（Pinia 状态管理、路由、Ant Design Vue）
 * 3. 注册全局指令（权限控制）
 * 4. 初始化 Sentry 错误监控
 * 5. 启动时加载当前用户权限
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import App from './App.vue'
import router from './router'
import permission from '@/directives/permission'
import * as Sentry from '@sentry/vue'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'
import { fetchCurrentUserPermissions } from '@/services/permissionService'

import './assets/main.scss'

/* ========== 1. 创建应用实例 ========== */
const app = createApp(App)

/* ========== 2. 注册全局插件 ========== */
app.use(createPinia())     // Pinia 状态管理
app.use(router)            // Vue Router 路由
app.use(Antd)              // Ant Design Vue 组件库（全局注册，确保 Menu 上下文正确）
app.directive('permission', permission)  // v-permission 权限指令

/* ========== 3. Sentry 错误监控初始化 ========== */
const dsn = import.meta.env?.VITE_SENTRY_DSN;
if (dsn) {
  Sentry.init({
    app,
    dsn,
    integrations: import.meta.env.DEV
      ? [Sentry.browserTracingIntegration(), Sentry.replayIntegration()]
      : [Sentry.browserTracingIntegration()],
    // 开发环境全量采集，生产环境低采样
    tracesSampleRate: import.meta.env.DEV ? 1.0 : 0.1,
    replaysSessionSampleRate: import.meta.env.DEV ? 0.1 : 0.0,
    replaysOnErrorSampleRate: import.meta.env.DEV ? 1.0 : 0.05,
  })
}

/* ========== 4. 启动时加载当前用户权限 ========== */
{
  const store = usePermissionStore();
  const userStore = useUserStore();
  if (userStore.token) {
    fetchCurrentUserPermissions()
      .then((data) => {
        if (data) {
          store.setPagePermissions(data.pagePermissions);
          store.setOperationPermissions(data.operationPermissions);
          store.syncedFromServer = true;
        }
      })
      .catch(() => {
        // 权限获取失败时静默处理（用户未登录等场景）
      });
  }
}

/* ========== 5. 生产环境日志控制 ========== */
// 生产环境下抑制 Vue 框架的无害警告，但保留 console.warn/error 以便排障
if (!import.meta.env.DEV) {
  const __originalConsoleError__ = console.error;
  const __originalConsoleWarn__ = console.warn;
  
  // 需要抑制的 Vue 框架无害警告模式
  const __suppressPatterns__ = [
    'Vue warn',
    'Runtime directive used on component with non-element root node',
    'Extraneous non-props attributes',
    'Non-function value encountered for default slot'
  ];

  console.error = (msg: any, ...args: any[]) => {
    if (typeof msg === 'string' && __suppressPatterns__.some(p => msg.includes(p))) {
      return;
    }
    __originalConsoleError__(msg, ...args);
  };

  console.warn = (msg: any, ...args: any[]) => {
    if (typeof msg === 'string' && __suppressPatterns__.some(p => msg.includes(p))) {
      return;
    }
    __originalConsoleWarn__(msg, ...args);
  };
}

/* ========== 6. 挂载应用 ========== */
app.mount('#app')
