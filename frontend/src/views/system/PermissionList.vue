<template>
  <div class="permission-list-page">
    <a-card :bordered="false" class="main-card glass-card">
      <template #title>
        <div class="card-header">
          <div class="header-title">系统权限总览</div>
          <div class="header-subtitle">查看系统中所有页面、Tab页签及操作功能的权限定义</div>
        </div>
      </template>
      
      <div class="permission-explorer">
        <!-- 1. 页面列表 -->
        <div class="pe-column pe-col-page">
          <div class="pe-col-header">
            <div class="header-top">
              <span class="pe-col-title">页面 (Page)</span>
              <span class="pe-count-badge">{{ pageCount }}</span>
            </div>
            <div class="header-search">
              <a-input v-model:value="pageSearchText" placeholder="搜索页面..." allow-clear class="pe-search-input">
                <template #prefix><search-outlined /></template>
              </a-input>
            </div>
          </div>
          <div class="pe-col-body">
            <div class="menu-tree">
              <template v-for="item in filteredMenu" :key="item.key">
                <!-- 一级菜单：如果是页面直接显示，如果是目录显示标题 -->
                <div v-if="!item.children" 
                  class="pe-item level-1"
                  :class="{ active: activePageKey === (item.pageKey || item.key) }"
                  @click="handleMenuPageClick(item.pageKey, item.key)"
                >
                  <div class="pe-item-main">
                    <div class="pe-item-row">
                      <component :is="iconMap[item.icon]" v-if="item.icon" class="pe-item-icon" />
                      <span class="pe-item-title">{{ item.title }}</span>
                    </div>
                  </div>
                  <div class="pe-item-right">
                    <span class="pe-id-badge" v-if="item.pageKey || item.key">{{ item.pageKey || item.key }}</span>
                    <RightOutlined class="pe-arrow" v-if="activePageKey === (item.pageKey || item.key)" />
                  </div>
                </div>

                <div v-else class="menu-group">
                  <div class="group-title" @click="toggleExpand(item.key)" :class="{ active: activePageKey === item.key }">
                    <div class="group-title-left">
                      <component :is="iconMap[item.icon]" v-if="item.icon" class="pe-item-icon" />
                      <span class="group-name">{{ item.title }}</span>
                    </div>
                    <div class="group-title-right">
                      <span class="pe-id-badge group-badge">{{ item.key }}</span>
                      <DownOutlined class="group-arrow" :class="{ rotated: !expandedKeys.includes(item.key) }" />
                    </div>
                  </div>
                  <div class="group-children" v-show="expandedKeys.includes(item.key)">
                    <div 
                      v-for="subItem in item.children" 
                      :key="subItem.key"
                      class="pe-item level-2"
                      :class="{ active: activePageKey === (subItem.pageKey || subItem.key) }"
                      @click="handleMenuPageClick(subItem.pageKey, subItem.key)"
                    >
                      <div class="pe-item-main">
                        <span class="pe-item-title">{{ subItem.title }}</span>
                      </div>
                      <div class="pe-item-right">
                        <span class="pe-id-badge" v-if="subItem.pageKey || subItem.key">{{ subItem.pageKey || subItem.key }}</span>
                        <RightOutlined class="pe-arrow" v-if="activePageKey === (subItem.pageKey || subItem.key)" />
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>

        <!-- 2. Tab列表 -->
        <div class="pe-column pe-col-tab">
          <div class="pe-col-header">
            <div class="header-top">
              <span class="pe-col-title">资源 (Resource)</span>
              <span class="pe-count-badge">{{ activePageKey ? filteredTabs.length + 1 : 0 }}</span>
            </div>
            <div class="header-search">
              <a-input v-model:value="tabSearchText" placeholder="搜索资源..." allow-clear class="pe-search-input" :disabled="!activePageKey">
                <template #prefix><search-outlined /></template>
              </a-input>
            </div>
          </div>
          <div class="pe-col-body">
            <div v-if="!activePageKey" class="pe-empty-state">
              请先选择一个页面
            </div>
            <div v-else>
              <!-- 页面权限 (固定项) -->
              <div 
                class="pe-item special-item"
                :class="{ active: activeTabKey === PAGE_PERMISSION_KEY }"
                @click="handleSpecialTabClick(PAGE_PERMISSION_KEY)"
              >
                <div class="pe-item-main">
                  <div class="pe-item-row">
                    <component :is="iconMap['DashboardOutlined']" class="pe-item-icon" />
                    <span class="pe-item-title">页面权限</span>
                  </div>
                </div>
                <div class="pe-item-right">
                  <span class="pe-id-badge">PAGE_ACCESS</span>
                  <RightOutlined class="pe-arrow" v-if="activeTabKey === PAGE_PERMISSION_KEY" />
                </div>
              </div>

              <div 
                v-for="tab in filteredTabs" 
                :key="tab.key"
                class="pe-item"
                :class="{ active: activeTabKey === tab.key }"
                @click="handleTabClick(tab)"
              >
                <div class="pe-item-main">
                  <div class="pe-item-row">
                    <component :is="iconMap['TagOutlined']" class="pe-item-icon" />
                    <span class="pe-item-title">{{ tab.title }}</span>
                  </div>
                </div>
                <div class="pe-item-right">
                  <span class="pe-id-badge">{{ tab.key }}</span>
                  <RightOutlined class="pe-arrow" v-if="activeTabKey === tab.key" />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 3. 操作列表 -->
        <div class="pe-column pe-col-action">
          <div class="pe-col-header">
            <div class="header-top">
              <span class="pe-col-title">操作权限 (Action)</span>
              <span class="pe-count-badge">{{ activePageKey ? filteredActions.length : 0 }}</span>
            </div>
            <div class="header-search">
              <a-input v-model:value="actionSearchText" placeholder="搜索操作..." allow-clear class="pe-search-input" :disabled="!activePageKey">
                <template #prefix><search-outlined /></template>
              </a-input>
            </div>
          </div>
          <div class="pe-col-body">
            <div v-if="!activePageKey" class="pe-empty-state">
              请先选择一个页面
            </div>
            <div v-else-if="!filteredActions.length" class="pe-empty-state">
              无相关操作权限
            </div>
            <div v-else>
              <template v-for="op in filteredActions" :key="op.key">
                <!-- 普通操作 -->
                <div v-if="!op.children" class="pe-item cursor-default">
                  <div class="pe-item-main">
                    <div class="pe-item-row">
                      <ThunderboltOutlined class="pe-item-icon" />
                      <span class="pe-item-title">{{ op.title }}</span>
                    </div>
                    <span class="pe-item-desc" v-if="op.description">{{ op.description }}</span>
                  </div>
                  <div class="pe-item-right">
                    <span class="pe-id-badge">{{ op.key }}</span>
                  </div>
                </div>

                <!-- 分组操作 (二级) -->
                <div v-else class="menu-group op-group">
                  <div class="group-title" @click="toggleExpand(op.key)" :class="{ active: expandedKeys.includes(op.key) }">
                    <div class="group-title-left">
                      <ThunderboltOutlined class="pe-item-icon" />
                      <span class="group-name">{{ op.title }}</span>
                      <span class="pe-item-desc" style="margin-left: 8px; font-weight: normal;">{{ op.description }}</span>
                    </div>
                    <div class="group-title-right">
                      <DownOutlined class="group-arrow" :class="{ rotated: !expandedKeys.includes(op.key) }" />
                    </div>
                  </div>
                  <div class="group-children" v-show="expandedKeys.includes(op.key)">
                    <div 
                      v-for="subOp in op.children" 
                      :key="subOp.key"
                      class="pe-item level-2 cursor-default"
                    >
                      <div class="pe-item-main">
                        <span class="pe-item-title">{{ subOp.title }}</span>
                        <span class="pe-item-desc" v-if="subOp.description">{{ subOp.description }}</span>
                      </div>
                      <div class="pe-item-right">
                        <span class="pe-id-badge">{{ subOp.key }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { 
  RightOutlined,
  DownOutlined,
  DashboardOutlined,
  TeamOutlined,
  TagOutlined,
  ShoppingOutlined,
  VideoCameraOutlined,
  PayCircleOutlined,
  SkinOutlined,
  SettingOutlined,
  ThunderboltOutlined,
  SearchOutlined,
  UserOutlined,
  LockOutlined,
  ProjectOutlined,
  GlobalOutlined,
  AppstoreOutlined,
  BarChartOutlined,
  ShareAltOutlined,
  FileTextOutlined
} from '@ant-design/icons-vue';
import { getAllPermissions, type PermissionInfo } from '@/services/permissionService';
import menu from '@/config/menu';
import type { MenuItem } from '@/config/menu';

// 统一权限类型
interface PermissionDefinition {
  key: string;
  title: string;
  type: 'page' | 'tab' | 'operation';
  parentKey?: string;
  description?: string;
}

// API 数据
 const ALL_PERMISSIONS = ref<PermissionDefinition[]>([]);
const loading = ref(true);
const loadError = ref(false);

// 加载权限数据
const loadPermissions = async () => {
  loading.value = true;
  loadError.value = false;
  try {
    const data = await getAllPermissions();
    ALL_PERMISSIONS.value = data.map((p: PermissionInfo) => ({
      key: p.permissionKey,
      title: p.title,
      type: p.type,
      parentKey: p.parentKey || undefined,
      description: p.description || undefined
    }));
  } catch {
    loadError.value = true;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadPermissions();
});

const PAGE_PERMISSION_KEY = '__PAGE_PERMISSION__';

const iconMap: Record<string, any> = {
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  LockOutlined,
  SettingOutlined,
  ShoppingOutlined,
  ProjectOutlined,
  GlobalOutlined,
  AppstoreOutlined,
  BarChartOutlined,
  ShareAltOutlined,
  FileTextOutlined,
  VideoCameraOutlined,
  PayCircleOutlined,
  SkinOutlined,
  TagOutlined
};

const activePageKey = ref<string>('');
const activeTabKey = ref<string>('');
// Track expanded parent menus
const expandedKeys = ref<string[]>([]);

// 搜索文本
const pageSearchText = ref('');
const tabSearchText = ref('');
const actionSearchText = ref('');

// Helper to count total items (parents + children)
const countMenuItems = (items: MenuItem[]): number => {
  let count = 0;
  items.forEach(item => {
    count++; // Count current item
    if (item.children) {
      count += countMenuItems(item.children);
    }
  });
  return count;
};

const pageCount = computed(() => {
  return countMenuItems(menu);
});

const toggleExpand = (key: string) => {
  const index = expandedKeys.value.indexOf(key);
  if (index > -1) {
    expandedKeys.value.splice(index, 1);
  } else {
    expandedKeys.value.push(key);
  }
};

const handleMenuPageClick = (pageKey?: string, itemKey?: string) => {
  // Try to find by pageKey first
  if (pageKey) {
    const permission = ALL_PERMISSIONS.value.find(p => p.key === pageKey && p.type === 'page');
    if (permission) {
      handlePageClick(permission);
      return;
    }
    // If has pageKey but not in permissions (maybe parent?), set as active anyway
    activePageKey.value = pageKey;
    activeTabKey.value = PAGE_PERMISSION_KEY;
    return;
  }
  
  // If no pageKey but has itemKey (e.g. parent folder), select it for visual feedback
  if (itemKey) {
    activePageKey.value = itemKey;
    activeTabKey.value = PAGE_PERMISSION_KEY;
  }
};

// 1. 页面列表 - 已替换为 menu 渲染

// 2. 当前页面的 Tab 列表
const activePageTabs = computed(() => {
  if (!activePageKey.value) return [];
  return ALL_PERMISSIONS.value.filter(p => p.type === 'tab' && p.parentKey === activePageKey.value);
});

// 3. 当前上下文的操作列表 (依赖于 Page 或 Tab)
const activeContextOps = computed(() => {
  if (!activePageKey.value) return [];
  
  let ops: PermissionDefinition[] = [];
  
  // Case 1: Page Permission selected - show page-level operations
  if (activeTabKey.value === PAGE_PERMISSION_KEY) {
    ops = ALL_PERMISSIONS.value.filter(p => p.type === 'operation' && p.parentKey === activePageKey.value);
  } else {
    // Case 2: Specific Tab selected - show tab-level operations
    ops = ALL_PERMISSIONS.value.filter(p => p.type === 'operation' && p.parentKey === activeTabKey.value);
  }
  
  // Grouping Logic for "Status View" permissions
  // We want to group "influencer.list.status.*" under a virtual parent
  const groupedOps: any[] = [];
  const statusGroup: any = {
    key: 'influencer.list.status_group',
    title: '状态查看',
    description: '查看不同状态红人的权限',
    children: []
  };

  const transferGroup: any = {
    key: 'influencer.list.transfer_group',
    title: '流转操作',
    description: '包含单个和批量流转操作',
    children: []
  };
  
  ops.forEach(op => {
    if (op.key.startsWith('influencer.list.status.')) {
      statusGroup.children.push(op);
    } else if (op.key.startsWith('influencer.list.transfer.') || op.key === 'influencer.list.batch_transfer') {
      transferGroup.children.push(op);
    } else {
      groupedOps.push(op);
    }
  });
  
  // If we found status items, add the group
  if (statusGroup.children.length > 0) {
    groupedOps.push(statusGroup);
  }

  // If we found transfer items, add the group
  if (transferGroup.children.length > 0) {
    groupedOps.push(transferGroup);
  }
  
  return groupedOps;
});

const handlePageClick = (page: PermissionDefinition) => {
  activePageKey.value = page.key;
  activeTabKey.value = PAGE_PERMISSION_KEY; // Default to Page Permission view
};

const handleSpecialTabClick = (key: string) => {
  activeTabKey.value = key;
};

const handleTabClick = (tab: PermissionDefinition) => {
  activeTabKey.value = tab.key;
};

// 过滤后的数据
const filteredMenu = computed(() => {
  if (!pageSearchText.value) return menu;
  
  const searchStr = pageSearchText.value.toLowerCase();
  return menu.map(item => {
    if (item.children) {
      const filteredChildren = item.children.filter(child => 
        child.title.toLowerCase().includes(searchStr) ||
        (child.pageKey && child.pageKey.toLowerCase().includes(searchStr)) ||
        child.key.toLowerCase().includes(searchStr)
      );
      if (filteredChildren.length > 0 || item.title.toLowerCase().includes(searchStr)) {
        return { ...item, children: filteredChildren };
      }
      return null;
    } else {
      const matches = item.title.toLowerCase().includes(searchStr) ||
        (item.pageKey && item.pageKey.toLowerCase().includes(searchStr)) ||
        item.key.toLowerCase().includes(searchStr);
      return matches ? item : null;
    }
  }).filter(item => item !== null) as MenuItem[];
});

const filteredTabs = computed(() => {
  if (!tabSearchText.value) return activePageTabs.value;
  const searchStr = tabSearchText.value.toLowerCase();
  return activePageTabs.value.filter(tab => 
    tab.title.toLowerCase().includes(searchStr) ||
    tab.key.toLowerCase().includes(searchStr)
  );
});

const filteredActions = computed(() => {
  const ops = activeContextOps.value;
  if (!actionSearchText.value) return ops;
  
  const searchStr = actionSearchText.value.toLowerCase();
  return ops.filter(action => 
    action.title.toLowerCase().includes(searchStr) ||
    action.key.toLowerCase().includes(searchStr)
  );
});

</script>

<style lang="scss" scoped>
.permission-list-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .main-card {
    flex: 1;
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 12px 20px;
      .ant-card-head-title { padding: 0; }
    }

    :deep(.ant-card-body) {
      flex: 1;
      padding: 0 !important;
      display: flex;
      flex-direction: column;
    }
  }

  .card-header {
    .header-title { font-size: 16px; font-weight: 700; color: #1e293b; margin-bottom: 2px; }
    .header-subtitle { font-size: 12px; color: #94a3b8; }
  }

  .permission-explorer {
    flex: 1;
    display: flex;
    overflow: hidden;
    background: #f1f5f9;
  }

  .pe-column {
    display: flex;
    flex-direction: column;
    background: #fff;
    border-right: 1px solid #e2e8f0;
    
    &.pe-col-page { width: 30%; min-width: 250px; }
    &.pe-col-tab { width: 30%; min-width: 250px; }
    &.pe-col-action { flex: 1; border-right: none; background: #f8fafc; }
  }

  .pe-col-header {
    padding: 12px 16px;
    border-bottom: 1px solid #e2e8f0;
    background: #f8fafc;
    flex-shrink: 0;
    
    .header-top {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;
    }

    .pe-col-title { font-size: 13px; font-weight: 700; color: #475569; }
    .pe-count-badge {
      background: #e2e8f0;
      color: #64748b;
      padding: 2px 8px;
      border-radius: 10px;
      font-size: 11px;
      font-weight: 600;
    }

    .pe-search-input {
      :deep(.ant-input) { font-size: 12px; }
      :deep(.ant-input-affix-wrapper) {
        border-radius: 6px;
        padding: 2px 8px;
        &:hover, &:focus { border-color: #fb7185; }
      }
    }
  }

  .pe-col-body {
    flex: 1;
    overflow-y: auto;
    padding: 8px 0;

    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 4px; }
  }

  .pe-item {
    padding: 10px 16px;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.2s;
    border-left: 3px solid transparent;

    &:hover { background: #f8fafc; }
    &.active {
      background: #fff1f2;
      border-left-color: #fb7185;
      .pe-item-title { color: #fb7185; font-weight: 600; }
      .pe-arrow { color: #fb7185; }
    }

    .pe-item-main { flex: 1; min-width: 0; }
    .pe-item-row { display: flex; align-items: center; gap: 8px; }
    .pe-item-icon { color: #64748b; font-size: 14px; }
    .pe-item-title { font-size: 13px; color: #334155; }
    
    .pe-item-right {
      display: flex;
      align-items: center;
      gap: 8px;
      .pe-id-badge {
        font-size: 10px;
        color: #94a3b8;
        background: #f1f5f9;
        padding: 1px 4px;
        border-radius: 4px;
        font-family: monospace;
      }
      .pe-arrow { font-size: 10px; color: #cbd5e1; }
    }

    &.special-item {
      background-color: #f9f9f9;
      margin-bottom: 4px;
      border-left: 3px solid #d9d9d9;
      
      &.active {
        border-left-color: #fb7185;
        background-color: #fff1f2;
      }
    }
  }

  .menu-group {
    .group-title {
      padding: 10px 16px;
      cursor: pointer;
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: #fcfcfc;
      &:hover { background: #f8fafc; }
      
      .group-title-left { display: flex; align-items: center; gap: 8px; }
      .group-title-right { display: flex; align-items: center; gap: 8px; }
      .group-name { font-size: 13px; font-weight: 600; color: #475569; }
      .group-arrow { font-size: 10px; color: #94a3b8; transition: transform 0.3s; &.rotated { transform: rotate(-90deg); } }
    }
    
    .group-children {
      .pe-item.level-2 { padding-left: 36px; }
    }
  }

  .pe-empty-state {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
    font-size: 13px;
  }

  .op-group {
    margin-bottom: 12px;
    padding: 0 8px;
    .op-group-header {
      padding: 8px 12px;
      .op-group-title { font-weight: 700; color: #1e293b; font-size: 13px; }
      .op-group-desc { font-size: 11px; color: #94a3b8; }
    }
  }
}
</style>