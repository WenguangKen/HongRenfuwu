<template>
  <a-layout class="basic-layout">
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible theme="dark" width="220" class="sider">
      <div class="logo">
        <template v-if="!collapsed">
          <div class="logo-flex text-logo-wrapper">
            <img :src="LogoImg" alt="INFLUENCER" class="logo-image" />
            <span class="text-logo">INFLUENCER</span>
          </div>
        </template>
        <div v-else class="logo-collapsed text-logo-wrapper">
          <img :src="LogoImg" alt="INFLUENCER" class="logo-image-collapsed" />
        </div>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        :openKeys="openKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
        @openChange="onOpenChange">
        <template v-for="item in filteredMenu" :key="item.key">
          <!-- 1级带子项 -> SubMenu -->
          <a-sub-menu v-if="item.children && item.children.length" :key="'sub_' + item.key">
            <template #icon>
              <component :is="icons[item.icon || '']" v-if="icons[item.icon || '']" class="anticon" />
            </template>
            <template #title>
              <span>{{ item.title }}</span>
            </template>
            
            <template v-for="child in item.children" :key="child.key">
              <!-- 2级带子项 (如：财务管理 -> 佣金管理) -->
              <a-sub-menu v-if="child.children && child.children.length" :key="'sub_' + child.key" :title="child.title">
                <a-menu-item v-for="grandChild in child.children" :key="grandChild.key">
                  {{ grandChild.title }}
                </a-menu-item>
              </a-sub-menu>
              
              <!-- 2级无子项 (如：财务管理 -> 汇款管理) -->
              <a-menu-item v-else :key="child.key">
                {{ child.title }}
              </a-menu-item>
            </template>
          </a-sub-menu>

          <!-- 1级无子项 -->
          <a-menu-item v-else :key="item.key">
            <template #icon>
              <component :is="icons[item.icon || '']" v-if="icons[item.icon || '']" class="anticon" />
            </template>
            <span>{{ item.title }}</span>
          </a-menu-item>
        </template>
      </a-menu>
    </a-layout-sider>
    <a-layout :style="{ marginLeft: collapsed ? '80px' : '220px', transition: 'margin-left 0.2s', height: '100vh', display: 'flex', flexDirection: 'column', background: '#f4f5f8', overflow: 'hidden' }">
      <a-layout-header class="main-header">
        <component :is="TriggerIcon" class="trigger" @click="toggleCollapsed" />
        
        <div class="header-center">
          <TopTabsBar />
        </div>
        
        <div class="header-right">
          <!-- 任务列表 -->
          <ExportTaskList />
          
          <!-- 用户菜单 -->
          <a-dropdown :trigger="['click']" placement="bottomRight" overlayClassName="premium-user-dropdown">
            <div class="user-link pill-style">
              <a-avatar v-if="userStore.userInfo?.avatarUrl" :src="userStore.userInfo.avatarUrl" :size="32" />
              <a-avatar v-else :size="32" style="background-color: #8b5cf6; font-weight: 700; display: flex; align-items: center; justify-content: center;">
                {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
              </a-avatar>
              <div class="user-info-text">
                <span class="user-name">{{ userStore.userInfo?.username || '用户' }}</span>
              </div>
              <DownOutlined class="dropdown-arrow" />
            </div>
            <template #overlay>
              <div class="user-dropdown-content">
                <div class="user-header">
                  <a-avatar v-if="userStore.userInfo?.avatarUrl" :src="userStore.userInfo.avatarUrl" :size="48" />
                  <a-avatar v-else :size="48" style="background-color: #8b5cf6; font-size: 20px;">
                    {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
                  </a-avatar>
                  <div class="header-info">
                    <div class="name">{{ userStore.userInfo?.username || '用户' }}</div>
                    <div class="email">{{ (userStore.userInfo?.email && userStore.userInfo.email.includes('@')) ? userStore.userInfo.email : '' }}</div>
                  </div>
                </div>
                <div class="menu-divider"></div>
                <a-menu @click="handleUserMenuClick" class="custom-menu">
                  <a-menu-item key="profile">
                    <UserOutlined /> 个人信息
                  </a-menu-item>
                  <div class="menu-divider"></div>
                  <a-menu-item key="logout" class="danger-item">
                    <LogoutOutlined /> 退出登录
                  </a-menu-item>
                </a-menu>
              </div>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      
      <a-layout-content class="main-content">
        <RouterView v-slot="{ Component, route }">
          <keep-alive :include="tabsStore.cachedViews">
            <component 
              :is="Component" 
              :key="route.name || route.path"
              style="flex: 1; display: flex; flex-direction: column; overflow: visible;"
            />
          </keep-alive>
        </RouterView>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onUnmounted, type Component as VueComponent } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import LogoImg from '@/assets/logo.png';
import {
  DashboardOutlined,
  TeamOutlined,
  TagOutlined,
  ShoppingOutlined,
  PayCircleOutlined,
  SkinOutlined,
  SettingOutlined,
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  VideoCameraOutlined,
  MinusSquareOutlined,
  SendOutlined,
  DownOutlined,
  LogoutOutlined,
  UserOutlined
} from '@ant-design/icons-vue';
import type { MenuProps } from 'ant-design-vue';
import { notification, Modal, message } from 'ant-design-vue';
import TopTabsBar from '@/components/layout/TopTabsBar.vue';
import ExportTaskList from '@/components/layout/ExportTaskList.vue';
import { useTabsStore } from '@/stores/tabs';
import { useUserStore } from '@/stores/user';
import { usePermissionStore } from '@/stores/permission';
import { useSseStore } from '@/stores/sse';
import menuConfig from '@/config/menu';
import { onAiUiAction } from '@/utils/aiActionBus';

const collapsed = ref(false);
const selectedKeys = ref<string[]>(['dashboard']);
const openKeys = ref<string[]>([]);
const router = useRouter();
const route = useRoute();
const tabsStore = useTabsStore();
const userStore = useUserStore();
const permStore = usePermissionStore();
const sseStore = useSseStore();

const icons: any = {
  DashboardOutlined,
  TeamOutlined,
  TagOutlined,
  ShoppingOutlined,
  PayCircleOutlined,
  SkinOutlined,
  SettingOutlined,
  VideoCameraOutlined,
  MinusSquareOutlined,
  SendOutlined
};

const TriggerIcon = computed<VueComponent>(() => (collapsed.value ? MenuUnfoldOutlined : MenuFoldOutlined));
const toggleCollapsed = () => { collapsed.value = !collapsed.value; };

const filteredMenu = computed(() => {
  const allow = (pageKey?: string) => {
    if (!pageKey) return true;
    return permStore.isAllowedPage(pageKey);
  };
  const filterRecursive = (items: any[]): any[] => {
    return items
      .map((it) => {
        if (it.children && it.children.length) {
          const sub = filterRecursive(it.children);
          if (sub.length === 0) return null;
          return { ...it, children: sub };
        }
        return allow(it.pageKey) ? it : null;
      })
      .filter(Boolean);
  };
  return filterRecursive(menuConfig) as typeof menuConfig;
});

const routePathMap = computed<Record<string, string>>(() => {
  const map: Record<string, string> = {};
  const walk = (items: any[]) => {
    items.forEach((it) => {
      if (it.path) map[it.key] = it.path;
      if (it.children) walk(it.children);
    });
  };
  walk(filteredMenu.value);
  return map;
});

const rootSubmenuKeys = menuConfig.filter(m => m.children).map(m => m.key);

// 递归查找父级 Key 列表 (用于展开菜单)
const getParentKeys = (targetKey: string): string[] => {
  const keys: string[] = [];
  const find = (items: any[], parents: string[]) => {
    for (const it of items) {
      if (it.key === targetKey) {
        keys.push(...parents.map(p => 'sub_' + p));
        return true;
      }
      if (it.children) {
        if (find(it.children, [...parents, it.key])) return true;
      }
    }
    return false;
  };
  find(menuConfig, []);
  return keys;
};

// 根据当前路由，同步选中项
const syncMenuByRoute = () => {
  const name = (route.name as string) || 'dashboard';
  selectedKeys.value = [name];

  // 路由变更时，点击菜单项不应收起父级，递归寻找所有父级以保持展开
  const parentKeys = getParentKeys(selectedKeys.value[0] || '');
  if (parentKeys.length) {
    // 合并当前已打开的、且是层级内的 key
    openKeys.value = Array.from(new Set([...openKeys.value, ...parentKeys]));
  }
};

watch(
  () => [route.name, collapsed.value],
  syncMenuByRoute,
  { immediate: true }
);

// 监听路由变化，自动添加标签
watch(
  () => route.fullPath,
  (fullPath) => {
    if (fullPath && fullPath !== '/user/login') {
      tabsStore.addView(route);
    }
  },
  { immediate: true }
);

const handleMenuClick: MenuProps['onClick'] = (e) => {
  const path = routePathMap.value[e.key as string];
  if (path) {
    router.push(path);
  }
};

const onOpenChange: MenuProps['onOpenChange'] = (keys) => {
  const latestOpenKey = (keys as string[]).find((key) => !openKeys.value.includes(key));
  // 顶级 sub_ 格式： sub_finance, sub_influencer 等
  const isRoot = latestOpenKey && rootSubmenuKeys.some(rk => latestOpenKey === 'sub_' + rk);
  
  if (latestOpenKey && isRoot) {
    openKeys.value = [latestOpenKey];
  } else {
    openKeys.value = keys as string[];
  }
};

const collapseAllMenus = () => {
  openKeys.value = [];
};

const handleUserMenuClick: MenuProps['onClick'] = async (e) => {
  if (e.key === 'logout') {
    // 关闭所有弹窗、通知和消息
    Modal.destroyAll();
    notification.destroy();
    message.destroy();
    
    // 执行退出登录逻辑
    try {
      await userStore.logout();
      // 清空所有标签页
      tabsStore.clearAll();
    } finally {
      router.push('/user/login');
    }
  } else if (e.key === 'profile') {
    router.push('/system/profile');
  }
};

// 权限自动刷新（每5分钟）
let permissionRefreshTimer: any = null;
let unsubscribeCloseAllModals: (() => void) | undefined;

onMounted(async () => {
  if (!userStore.userInfo) {
    await userStore.loadUserInfo();
  }
  if (!permStore.syncedFromServer) {
    await permStore.refreshPermissions();
  }

  // 启动 SSE 连接
  sseStore.connect();

  // 监听 AI 导航时的 CloseAllModals 事件
  unsubscribeCloseAllModals = onAiUiAction((action) => {
    if (action.name === 'CloseAllModals') {
      Modal.destroyAll();
    }
  });

  // 启动权限刷新定时器
  permissionRefreshTimer = setInterval(async () => {
    try {
      await permStore.refreshPermissions();
    } catch (error) {
      console.error('自动刷新权限失败', error);
    }
  }, 300000); // 5分钟刷新一次
});

onUnmounted(() => {
  // 清理定时器
  if (permissionRefreshTimer) {
    clearInterval(permissionRefreshTimer);
  }
  // 断开 SSE 连接
  sseStore.disconnect();
  // 取消 CloseAllModals 监听
  unsubscribeCloseAllModals?.();
});

</script>

<style lang="scss" scoped>
/* SaaS Base */
.basic-layout {
  min-height: 100vh;
  background: #f4f5f8;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
}

.sider {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  height: 100vh;
  z-index: 100;
  background: #0f172a !important;
  border-right: none;
  transition: all 0.2s;

  :deep(.ant-layout-sider-children) {
      display: flex;
      flex-direction: column;
  }

  :deep(.ant-menu) {
      border-right: none !important;
      padding: 12px 10px;
      background: #0f172a !important;
  }
}

/* Logo Area - Clean and Minimal */
.logo {
  height: 80px; padding: 0 16px; display: flex; align-items: center; justify-content: center; overflow: hidden;
  position: relative;
  
  .logo-flex {
    display: flex; align-items: center; gap: 8px;
  }
  
  .text-logo-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .logo-image {
    width: 24px;
    height: 24px;
    object-fit: contain;
  }
  
  .logo-image-collapsed {
    width: 28px;
    height: 28px;
    object-fit: contain;
  }
  
  .logo-collapsed {
    display: flex; align-items: center; justify-content: center;
    margin: 0 auto;
  }

  .text-logo {
    font-size: 18px;
    font-weight: 900;
    color: #f8fafc;
    letter-spacing: 1.5px;
    background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
}

/* Removed glass-container */

/* Main Header */
.main-header {
  height: 64px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: none;
  box-shadow: 0 4px 20px -8px rgba(0,0,0,0.06);
  z-index: 99;
}

.trigger {
  width: 36px;
  height: 36px;
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #86868b;
  cursor: pointer;
  border-radius: 8px;
  background: transparent;
  transition: all 0.2s;
  
  &:hover {
    color: #1d1d1f;
    background: rgba(0,0,0,0.04);
  }
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  min-width: 0;
  margin: 0 16px;
}

/* User Profile Pill */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  
  .user-link.pill-style {
      display: flex;
      align-items: center;
      padding: 4px 12px 4px 4px;
      border-radius: 30px;
      background: #f8fafc;
      border: 1px solid #f1f5f9;
      transition: all 0.2s;
      cursor: pointer;
      gap: 10px;

      &:hover {
        background: #ffffff;
        box-shadow: 0 4px 12px rgba(0,0,0,0.06);
        transform: translateY(-1px);
      }

      .user-info-text {
        display: flex;
        flex-direction: column;
        line-height: 1;
        .user-name {
          color: #1d1d1f;
          font-weight: 600;
          font-size: 14px;
        }
      }

      .dropdown-arrow {
        font-size: 12px;
        color: #1d1d1f;
        font-weight: bold;
      }
  }
}

.main-content {
  flex: 1;
  padding: 8px;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(0,0,0,0.1);
    border-radius: 3px;
  }
}

/* Global Styles for Premium Dropdown */
:global(.premium-user-dropdown) {
  .ant-dropdown-content {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(30px) saturate(180%);
    -webkit-backdrop-filter: blur(30px) saturate(180%);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.06);
    padding: 0;
    overflow: hidden;
    min-width: 220px;
  }
}

.user-dropdown-content {
  display: flex;
  flex-direction: column;

  .user-header {
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    background: transparent;
    
    .header-info {
      display: flex;
      flex-direction: column;
      gap: 2px;
      .name { font-weight: 600; color: #1d1d1f; font-size: 14px; }
      .email { font-size: 12px; color: #86868b; }
    }
  }

  .menu-divider {
    height: 1px;
    background: rgba(0, 0, 0, 0.06);
    margin: 4px 0;
  }

  .custom-menu {
    border: none;
    box-shadow: none;
    background: transparent;
    padding: 6px;
    
    :deep(.ant-dropdown-menu-item) {
      padding: 8px 12px;
      border-radius: 6px;
      margin-bottom: 2px;
      color: #1d1d1f;
      font-weight: 400;
      font-size: 13px;
      transition: all 0.1s;
      
      .anticon {
        margin-right: 10px;
        font-size: 14px;
        color: #86868b;
      }

      &:hover {
        background: #0066cc; /* macOS Blue */
        color: #ffffff;
        .anticon { color: #ffffff; }
      }
    }
    
    .danger-item {
      &:hover {
        background: #ff3b30 !important; /* macOS Red */
        color: #ffffff !important;
        .anticon { color: #ffffff !important; }
      }
    }
  }
}

/* Sidebar Menu Item Styles - Dark Theme */
:deep(.ant-menu-item) {
  margin: 4px 8px !important;
  width: calc(100% - 16px) !important;
  height: 40px !important;
  line-height: 40px !important;
  border-radius: 6px !important;
  display: flex;
  align-items: center;
  padding: 0 16px !important;
  transition: all 0.2s;
  color: #cbd5e1;
  font-weight: 500;
  border: none !important;
  outline: none !important;
  
  .anticon {
    font-size: 16px !important;
    margin-right: 12px !important;
    color: #94a3b8;
    transition: color 0.2s;
  }

  &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
      color: #f8fafc !important;
  }

  &.ant-menu-item-selected {
      background: linear-gradient(90deg, rgba(139, 92, 246, 0.25) 0%, rgba(139, 92, 246, 0.05) 100%) !important;
      border-left: 3px solid #8b5cf6 !important;
      color: #ffffff !important;
      border-radius: 0 6px 6px 0 !important;
      margin-left: 5px !important;

      .anticon {
        color: #ffffff !important;
      }

      &::after { display: none; }
  }
}

:deep(.ant-menu-inline .ant-menu-submenu-title) {
  margin: 4px 8px !important;
  width: calc(100% - 16px) !important;
  height: 40px !important;
  line-height: 40px !important;
  border-radius: 6px !important;
  padding: 0 16px !important;
  transition: all 0.2s;
  color: #cbd5e1;
  font-weight: 500;
  border: none !important;
  outline: none !important;

  .anticon {
    font-size: 16px !important;
    margin-right: 12px !important;
    color: #94a3b8;
  }

  &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
      color: #f8fafc !important;
  }
}

:deep(.ant-menu-submenu-open > .ant-menu-submenu-title) {
  color: #f8fafc !important;
  .anticon {
    color: #8b5cf6 !important;
  }
}

:deep(.ant-menu-sub) {
  background: transparent !important;
  
  .ant-menu-item {
      height: 36px !important;
      line-height: 36px !important;
      font-size: 13px;
      font-weight: 500;
      color: #94a3b8;
      padding-left: 44px !important;
      margin: 2px 8px !important;
      width: calc(100% - 16px) !important;

      &:hover {
          background: rgba(255, 255, 255, 0.08) !important;
          color: #f8fafc !important;
      }

      &.ant-menu-item-selected {
          background: rgba(139, 92, 246, 0.15) !important;
          color: #a78bfa !important;
      }
  }
}

:deep(.ant-layout-sider-collapsed) {
  .ant-menu {
      padding: 12px 0;
  }
  
  .ant-menu-item, .ant-menu-submenu-title {
      width: 44px !important;
      height: 44px !important;
      margin: 8px auto !important;
      padding: 0 !important;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 12px !important;

      .anticon {
          margin: 0 !important;
          font-size: 20px !important;
      }
      
      .ant-menu-title-content, .ant-menu-submenu-arrow {
          display: none !important;
      }
  }
}
</style>
