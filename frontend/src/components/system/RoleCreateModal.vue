<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="1200px"
    :footer="null"
    class="premium-refined-modal"
    :mask-closable="false"
    :closable="false"
    centered
    destroy-on-close
  >
    <!-- Custom Header -->
    <div class="ic-modal-header glass-header">
      <div class="ic-header-left">
        <div class="ic-header-icon-wrapper">
          <SafetyCertificateOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">
            {{ isEdit ? '编辑角色' : '新建角色' }}
            <span v-if="isEdit && roleData" class="role-id-tag">ID: {{ roleData.id }}</span>
          </div>
          <div class="ic-header-subtitle">
            配置角色的基本信息和功能权限
            <span class="sub-en">Configure role basic info and functional permissions</span>
          </div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- Content Body -->
    <div class="ic-modal-body">
      <a-form :model="formState" layout="vertical" class="ic-form">
        <!-- 1. Basic Info Section -->
        <div class="ic-section">
          <div class="ic-section-header">
            <span class="ic-section-title">基本信息 (Basic Info)</span>
          </div>
          <div class="ic-section-body">
            <div class="ic-form-row">
              <a-form-item label="角色名称" required>
                <a-input v-model:value="formState.name" placeholder="请输入角色名称" allow-clear />
              </a-form-item>
              <a-form-item label="状态">
                <a-select v-model:value="formState.status">
                  <a-select-option value="enabled">启用</a-select-option>
                  <a-select-option value="disabled">停用</a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <a-form-item label="角色说明" style="margin-bottom: 0;">
              <a-textarea 
                v-model:value="formState.description" 
                :autoSize="{ minRows: 2, maxRows: 3 }"
                placeholder="请输入角色职能描述..."
              />
            </a-form-item>
          </div>
        </div>

        <!-- 2. Permissions Matrix Table -->
        <div class="permissions-container">
          <div class="perm-toolbar">
            <div class="ph-title">权限配置 (Permissions Matrix)</div>
            <div class="ph-actions">
              <a-input 
                v-model:value="searchText" 
                placeholder="搜索页面或权限..." 
                style="width: 240px"
                allow-clear
              >
                <template #prefix><SearchOutlined /></template>
              </a-input>
              <div class="ph-stats">
                <span class="stat-item">已选页面: <span class="num">{{ checkedPageKeys.length }}</span></span>
                <span class="stat-item">权限点: <span class="num">{{ checkedOperationKeys.length }}</span></span>
              </div>
            </div>
          </div>

          <div class="matrix-table-wrapper">
             <div class="matrix-header">
                <div class="col-module">功能模块 (Module)</div>
                <div class="col-access">基础权限 (Access)</div>
                <div class="col-ops">操作权限 (Operations)</div>
             </div>
             
             <div class="matrix-body">
                <template v-for="(moduleNode, mIndex) in filteredPermissionStructure" :key="moduleNode.key">
                   
                   <!-- Module Header Row (if it's a group) -->
                   <div v-if="!moduleNode.isPage" class="matrix-row group-header-row">
                      <div class="col-module">
                         <div class="module-title-wrap">
                            <component :is="getModuleIcon(moduleNode.key)" class="module-icon" />
                            <span class="module-name">{{ moduleNode.title }}</span>
                            <a-checkbox 
                              :checked="isModuleAllChecked(moduleNode)" 
                              :indeterminate="isModuleIndeterminate(moduleNode)"
                              @change="(e: any) => toggleModuleAll(moduleNode, e.target.checked)"
                              @click.stop
                              class="module-check-all"
                            >
                               全选
                            </a-checkbox>
                         </div>
                      </div>
                      <div class="col-access bg-cell"></div>
                      <div class="col-ops bg-cell"></div>
                   </div>

                   <!-- Pages (Rows) -->
                   <template v-for="(pageRow, pIndex) in (moduleNode.isPage ? [moduleNode] : moduleNode.children)" :key="pageRow.key">
                      <div class="matrix-row page-row">
                         <!-- Module Column -->
                         <div class="col-module">
                            <div class="page-title-wrap" :class="{ 'is-root': moduleNode.isPage }">
                               <span class="page-name">{{ pageRow.title }}</span>
                            </div>
                         </div>
                         
                         <!-- Access Column -->
                         <div class="col-access">
                            <a-checkbox 
                               :checked="checkedPageKeys.includes(pageRow.key)"
                               @change="(e: any) => onPageCheck(pageRow.key, e.target.checked)"
                            >
                               访问页面
                            </a-checkbox>
                         </div>

                         <!-- Operations Column -->
                         <div class="col-ops">
                            <!-- Grouped Ops (Standard, Status, Transfer) -->
                            <div v-if="pageRow.opGroups && pageRow.opGroups.length > 0" class="ops-group-container">
                               <div v-for="group in pageRow.opGroups" :key="group.type" class="ops-group-block">
                                  <div v-if="group.label" class="ops-group-label-row">
                                     <span class="og-label">{{ group.label }}</span>
                                     <a-checkbox
                                        :checked="isGroupAllChecked(group)"
                                        :indeterminate="isGroupIndeterminate(group)"
                                        @change="(e: any) => onGroupCheck(group, e.target.checked, pageRow.key)"
                                        style="font-size: 12px; transform: scale(0.9);"
                                     >全选</a-checkbox>
                                  </div>
                                  <div class="ops-list">
                                     <a-checkbox 
                                        v-for="op in group.ops" 
                                        :key="op.key"
                                        :checked="checkedOperationKeys.includes(op.key)"
                                        @change="(e: any) => onOpCheck(op.key, e.target.checked, pageRow.key)"
                                     >
                                        {{ op.title }}
                                     </a-checkbox>
                                  </div>
                               </div>
                            </div>
                            
                            <!-- Tabs (Sub-groups in Ops Column) -->
                             <div v-if="pageRow.tabs && pageRow.tabs.length > 0" class="tabs-list">
                                <div v-for="tab in pageRow.tabs" :key="tab.key" class="tab-item-inline">
                                   <div class="tab-label">
                                      <a-checkbox 
                                         :checked="checkedOperationKeys.includes(tab.key)"
                                         @change="(e: any) => onTabCheck(tab, e.target.checked, pageRow.key)"
                                      >
                                      {{ tab.title.replace('(Tab)', '') }}
                                      </a-checkbox>
                                   </div>
                                   <div class="tab-ops">
                                      <a-checkbox 
                                         v-for="op in tab.ops" 
                                         :key="op.key"
                                         :checked="checkedOperationKeys.includes(op.key)"
                                         @change="(e: any) => onTabOpCheck(op.key, e.target.checked, pageRow.key, tab.key)"
                                      >
                                         {{ op.title }}
                                      </a-checkbox>
                                   </div>
                                </div>
                             </div>
                             
                             <div v-if="(!pageRow.opGroups || pageRow.opGroups.length === 0) && (!pageRow.tabs || pageRow.tabs.length === 0)" class="no-ops">
                                -
                             </div>
                         </div>
                      </div>
                   </template>
                </template>
                
                <div v-if="filteredPermissionStructure.length === 0" class="empty-search">
                  未找到匹配的权限配置
                </div>
             </div>
          </div>
        </div>

      </a-form>
    </div>

    <!-- Footer -->
    <div class="ic-modal-footer">
      <a-button @click="handleCancel" class="premium-btn">取消</a-button>
      <a-button type="primary" @click="submit" class="premium-btn primary-gradient">
        {{ isEdit ? '保存修改' : '立即创建' }}
      </a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { message } from 'ant-design-vue';
import { 
  SafetyCertificateOutlined, 
  CloseOutlined,
  AppstoreOutlined,
  UserOutlined,
  ShoppingOutlined,
  PayCircleOutlined,
  FileTextOutlined,
  TagsOutlined,
  SettingOutlined,
  GiftOutlined,
  SearchOutlined
} from '@ant-design/icons-vue';

// --- Types ---
interface RoleUser { id: number; name: string; }
interface RoleRow {
  id: number;
  name: string;
  description: string;
  status: 'enabled' | 'disabled';
  createTime: string;
  pagePermissions: string[];
  operationPermissions: string[];
  users: RoleUser[];
}
interface PageNode { key: string; title: string; children?: PageNode[]; }
interface OperationNode { key: string; title: string; pageKey: string; type: 'tab' | 'operation'; parentKey?: string; }

// --- Structure Helper Types ---
interface OpGroup {
  type: 'standard' | 'status' | 'transfer';
  label?: string;
  ops: OperationNode[];
}

interface RenderPage {
  key: string;
  title: string;
  isPage: boolean;
  // New: Grouped Ops
  opGroups: OpGroup[]; 
  tabs: { key: string; title: string; ops: OperationNode[]; rawOps: OperationNode[] }[];
  children?: RenderPage[];
}

const props = defineProps<{
  open: boolean;
  roleData: RoleRow | null;
  isEdit: boolean;
  pageTreeSource: PageNode[];
  operationsSource: OperationNode[];
  allPageKeys: string[];
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
  'submit': [data: any];
}>();

const visible = ref(false);
const searchText = ref('');
const formState = reactive({
  name: '',
  description: '',
  status: 'enabled' as 'enabled' | 'disabled',
});

const checkedPageKeys = ref<string[]>([]);
const checkedOperationKeys = ref<string[]>([]);

// --- Icons Mapping ---
const getModuleIcon = (key: string) => {
  if (key.includes('dashboard')) return AppstoreOutlined;
  if (key.includes('influencer')) return UserOutlined;
  if (key.includes('order')) return ShoppingOutlined;
  if (key.includes('commission')) return PayCircleOutlined;
  if (key.includes('content')) return FileTextOutlined;
  if (key.includes('tag')) return TagsOutlined;
  if (key.includes('system')) return SettingOutlined;
  if (key.includes('discount')) return GiftOutlined;
  return AppstoreOutlined;
};

// --- Prepare Structure ---
const permissionStructure = computed(() => {
  return props.pageTreeSource.map(node => {
    // Top Level Node
    const isLeafPage = !node.children || node.children.length === 0;
    
    if (isLeafPage) {
      return buildPageRenderNode(node);
    } else {
      // It's a Group (e.g. Influencer)
      return {
        key: node.key,
        title: node.title,
        isPage: false,
        children: node.children?.map(child => buildPageRenderNode(child)) || [],
        opGroups: [],
        tabs: []
      } as RenderPage;
    }
  });
});

const filteredPermissionStructure = computed(() => {
  if (!searchText.value) return permissionStructure.value;
  
  const text = searchText.value.toLowerCase();
  
  return permissionStructure.value.reduce((acc: RenderPage[], node) => {
    // If it's a page node, check title or ops
    if (node.isPage) {
       const mTitle = node.title.toLowerCase();
       const hasOpMatch = node.opGroups.some(g => g.ops.some(op => op.title.toLowerCase().includes(text)));
       const hasTabMatch = node.tabs.some(tab => tab.title.toLowerCase().includes(text) || tab.ops.some(op => op.title.toLowerCase().includes(text)));
       
       if (mTitle.includes(text) || hasOpMatch || hasTabMatch) {
         acc.push(node);
       }
    } else {
       // Group node, check children
       const mTitle = node.title.toLowerCase();
       const matchingChildren = node.children ? node.children.filter(child => {
         const cTitle = child.title.toLowerCase();
         // Check child ops/tabs
         const hasChildOpMatch = child.opGroups.some(g => g.ops.some(op => op.title.toLowerCase().includes(text)));
         const hasChildTabMatch = child.tabs.some(tab => tab.title.toLowerCase().includes(text) || tab.ops.some(op => op.title.toLowerCase().includes(text)));
         
         return cTitle.includes(text) || hasChildOpMatch || hasChildTabMatch;
       }) : [];
       
       // Include if group title matches (show all children) OR children match
       if (mTitle.includes(text)) {
         acc.push(node); // Show full group
       } else if (matchingChildren.length > 0) {
         acc.push({
           ...node,
           children: matchingChildren
         });
       }
    }
    return acc;
  }, []);
});

const buildPageRenderNode = (node: PageNode): RenderPage => {
  // Find direct operations
  const directOps = props.operationsSource.filter(op => op.pageKey === node.key && op.type === 'operation');
  
  // Group logic
  const opGroups: OpGroup[] = [];
  
  const statusOps = directOps.filter(op => op.key.includes('.status.'));
  const transferOps = directOps.filter(op => op.key.includes('.transfer.') || op.key.endsWith('batch_transfer'));
  const standardOps = directOps.filter(op => !op.key.includes('.status.') && !op.key.includes('.transfer.') && !op.key.endsWith('batch_transfer'));
  
  if (standardOps.length > 0) {
    opGroups.push({ type: 'standard', ops: standardOps });
  }
  
  if (statusOps.length > 0) {
    opGroups.push({ type: 'status', label: '查看状态', ops: statusOps });
  }
  
  if (transferOps.length > 0) {
    opGroups.push({ type: 'transfer', label: '流转操作', ops: transferOps });
  }
  
  // Find tabs
  const tabs = props.operationsSource.filter(op => op.pageKey === node.key && op.type === 'tab');
  
  // Build tabs with their ops
  const tabsRender = tabs.map(tab => {
    const tabOps = props.operationsSource.filter(op => op.pageKey === tab.key && op.type === 'operation');
    return {
      key: tab.key,
      title: tab.title,
      ops: tabOps,
      rawOps: tabOps
    };
  });

  return {
    key: node.key,
    title: node.title,
    isPage: true,
    opGroups,
    tabs: tabsRender
  };
};

// --- Watchers ---
watch(() => props.open, (val) => {
  visible.value = val;
  if (val) {
    if (props.isEdit && props.roleData) {
      formState.name = props.roleData.name;
      formState.description = props.roleData.description;
      formState.status = props.roleData.status;
      checkedPageKeys.value = [...props.roleData.pagePermissions];
      checkedOperationKeys.value = [...props.roleData.operationPermissions];
    } else {
      formState.name = '';
      formState.description = '';
      formState.status = 'enabled';
      checkedPageKeys.value = [];
      checkedOperationKeys.value = [];
    }
    searchText.value = '';
  }
});

watch(visible, (val) => emit('update:open', val));

// --- Actions ---

const handleCancel = () => visible.value = false;

const submit = () => {
  if (!formState.name) {
    message.warning('请填写角色名称');
    return;
  }
  emit('submit', {
    id: props.roleData?.id,
    name: formState.name,
    description: formState.description,
    status: formState.status,
    pagePermissions: checkedPageKeys.value,
    operationPermissions: checkedOperationKeys.value
  });
};

// --- Check Logic ---

// 1. Check Page
const onPageCheck = (pageKey: string, checked: boolean) => {
  if (checked) {
    if (!checkedPageKeys.value.includes(pageKey)) checkedPageKeys.value.push(pageKey);
  } else {
    checkedPageKeys.value = checkedPageKeys.value.filter(k => k !== pageKey);
    // Uncheck all children ops & tabs
    const pageNode = findPageNode(pageKey);
    if (pageNode) {
      const allOps = getAllOpsForPage(pageNode);
      checkedOperationKeys.value = checkedOperationKeys.value.filter(k => !allOps.includes(k));
    }
  }
};

// 2. Check Op (Direct)
const onOpCheck = (opKey: string, checked: boolean, pageKey: string) => {
  if (checked) {
    if (!checkedOperationKeys.value.includes(opKey)) checkedOperationKeys.value.push(opKey);
    // Auto check page
    if (!checkedPageKeys.value.includes(pageKey)) checkedPageKeys.value.push(pageKey);
  } else {
    checkedOperationKeys.value = checkedOperationKeys.value.filter(k => k !== opKey);
  }
};

// 3. Check Tab Op
const onTabOpCheck = (opKey: string, checked: boolean, pageKey: string, tabKey: string) => {
  if (checked) {
    if (!checkedOperationKeys.value.includes(opKey)) checkedOperationKeys.value.push(opKey);
    // Auto check Tab
    if (!checkedOperationKeys.value.includes(tabKey)) checkedOperationKeys.value.push(tabKey);
    // Auto check Page
    if (!checkedPageKeys.value.includes(pageKey)) checkedPageKeys.value.push(pageKey);
  } else {
    checkedOperationKeys.value = checkedOperationKeys.value.filter(k => k !== opKey);
  }
};

// 4. Check Tab (All Ops in Tab)
const onTabCheck = (tab: { key: string; ops: OperationNode[] }, checked: boolean, pageKey: string) => {
  if (checked) {
    if (!checkedOperationKeys.value.includes(tab.key)) checkedOperationKeys.value.push(tab.key);
    // Check all ops
    tab.ops.forEach(op => {
      if (!checkedOperationKeys.value.includes(op.key)) checkedOperationKeys.value.push(op.key);
    });
    // Auto check Page
    if (!checkedPageKeys.value.includes(pageKey)) checkedPageKeys.value.push(pageKey);
  } else {
    // Uncheck Tab
    checkedOperationKeys.value = checkedOperationKeys.value.filter(k => k !== tab.key);
    // Uncheck all ops
    const opKeys = tab.ops.map(o => o.key);
    checkedOperationKeys.value = checkedOperationKeys.value.filter(k => !opKeys.includes(k));
  }
};

// 5. Module All Check
const toggleModuleAll = (moduleNode: RenderPage, checked: boolean) => {
  const pagesToProcess = moduleNode.isPage ? [moduleNode] : (moduleNode.children || []);
  
  pagesToProcess.forEach(page => {
    if (checked) {
      // Check Page
      if (!checkedPageKeys.value.includes(page.key)) checkedPageKeys.value.push(page.key);
      
      // Check Groups Ops
      page.opGroups.forEach(g => {
        g.ops.forEach(op => {
          if (!checkedOperationKeys.value.includes(op.key)) checkedOperationKeys.value.push(op.key);
        });
      });
      
      // Check Tabs and Tab Ops
      page.tabs.forEach(tab => {
        if (!checkedOperationKeys.value.includes(tab.key)) checkedOperationKeys.value.push(tab.key);
        tab.ops.forEach(op => {
          if (!checkedOperationKeys.value.includes(op.key)) checkedOperationKeys.value.push(op.key);
        });
      });
    } else {
      // Uncheck Page
      checkedPageKeys.value = checkedPageKeys.value.filter(k => k !== page.key);
      
      // Uncheck all Ops
      const allOps = getAllOpsForPage(page);
      checkedOperationKeys.value = checkedOperationKeys.value.filter(k => !allOps.includes(k));
    }
  });
};

const isModuleAllChecked = (moduleNode: RenderPage) => {
  const pagesToProcess = moduleNode.isPage ? [moduleNode] : (moduleNode.children || []);
  if (pagesToProcess.length === 0) return false;
  
  return pagesToProcess.every(page => {
    if (!checkedPageKeys.value.includes(page.key)) return false;
    const allOps = getAllOpsForPage(page);
    return allOps.every(opKey => checkedOperationKeys.value.includes(opKey));
  });
};

const isModuleIndeterminate = (moduleNode: RenderPage) => {
  if (isModuleAllChecked(moduleNode)) return false;
  
  const pagesToProcess = moduleNode.isPage ? [moduleNode] : (moduleNode.children || []);
  
  const hasPageChecked = pagesToProcess.some(p => checkedPageKeys.value.includes(p.key));
  if (hasPageChecked) return true;
  
  for (const page of pagesToProcess) {
    const allOps = getAllOpsForPage(page);
    if (allOps.some(k => checkedOperationKeys.value.includes(k))) return true;
  }
  
  return false;
};

// Group Check
const onGroupCheck = (group: OpGroup, checked: boolean, pageKey: string) => {
  if (checked) {
    if (!checkedPageKeys.value.includes(pageKey)) checkedPageKeys.value.push(pageKey);
    group.ops.forEach(op => {
      if (!checkedOperationKeys.value.includes(op.key)) checkedOperationKeys.value.push(op.key);
    });
  } else {
    group.ops.forEach(op => {
      checkedOperationKeys.value = checkedOperationKeys.value.filter(k => k !== op.key);
    });
  }
};

const isGroupAllChecked = (group: OpGroup) => {
  return group.ops.every(op => checkedOperationKeys.value.includes(op.key));
};

const isGroupIndeterminate = (group: OpGroup) => {
  const checked = group.ops.filter(op => checkedOperationKeys.value.includes(op.key)).length;
  return checked > 0 && checked < group.ops.length;
};


// --- Helpers ---
const findPageNode = (key: string): RenderPage | undefined => {
  const stack = [...permissionStructure.value];
  while (stack.length) {
     const node = stack.pop()!;
     if (node.isPage && node.key === key) return node;
     if (!node.isPage && node.children) {
        const found = node.children.find(c => c.key === key);
        if (found) return found;
        stack.push(...node.children);
     }
  }
  return undefined;
};

const getAllOpsForPage = (page: RenderPage): string[] => {
  const keys: string[] = [];
  page.opGroups.forEach(g => {
    g.ops.forEach(op => keys.push(op.key));
  });
  page.tabs.forEach(tab => {
    keys.push(tab.key);
    tab.ops.forEach(op => keys.push(op.key));
  });
  return keys;
};

</script>

<style lang="scss" scoped>
/* Reuse Shared Styles */
:deep(.premium-refined-modal) {
  .ant-modal-content {
    padding: 0;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }
}

.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  
  &.glass-header {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
  }

  .ic-header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .ic-header-icon-wrapper {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
  }

  .ic-header-text {
    .ic-header-title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      line-height: 1.2;
      display: flex;
      align-items: center;
      gap: 8px;
      
      .role-id-tag {
        font-size: 11px;
        background: #f1f5f9;
        color: #64748b;
        padding: 2px 6px;
        border-radius: 4px;
        font-weight: 500;
      }
    }
    .ic-header-subtitle {
      font-size: 12px;
      color: #94a3b8;
      margin-top: 4px;
      .sub-en { opacity: 0.8; margin-left: 4px; }
    }
  }

  .close-btn { color: #94a3b8; &:hover { color: #64748b; background: #f1f5f9; } }
}

.ic-modal-body {
  padding: 24px;
  background: #f8fafc;
  height: 600px; /* Fixed Height as requested */
  overflow-y: auto;
  
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 3px; }
}

.ic-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  margin-bottom: 20px;
  overflow: hidden;
  
  .ic-section-header {
    padding: 12px 20px;
    border-bottom: 1px solid #f1f5f9;
    background: #fafbfc;
    
    .ic-section-title {
      font-size: 13px;
      font-weight: 700;
      color: #475569;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      &:before {
        content: ""; display: inline-block; width: 3px; height: 12px; 
        background: #10b981; border-radius: 2px; margin-right: 8px; vertical-align: middle;
      }
    }
  }
  
  .ic-section-body { padding: 20px; }
}

/* Form Styles */
:deep(.ic-form) {
  .ant-form-item {
    margin-bottom: 16px;
    &:last-child { margin-bottom: 0; }
    .ant-form-item-label label { font-size: 13px; font-weight: 600; color: #475569; }
    .ant-input, .ant-select-selector, .ant-input-number {
      border-radius: 8px !important;
    }
  }
}
.ic-form-row {
  display: grid; grid-template-columns: 1fr 200px; gap: 20px; margin-bottom: 16px;
}

/* Matrix Table Styles */
.permissions-container {
  display: flex; flex-direction: column; gap: 16px;
}

.perm-toolbar {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;
  .ph-title { 
    font-size: 14px; font-weight: 700; color: #334155; 
    &:before {
        content: ""; display: inline-block; width: 3px; height: 12px; 
        background: #3b82f6; border-radius: 2px; margin-right: 8px; vertical-align: middle;
    }
  }
  .ph-actions {
    display: flex; align-items: center; gap: 12px;
  }
  .ph-stats {
    font-size: 12px; color: #64748b; display: flex; gap: 8px;
    .stat-item { background: #e2e8f0; padding: 4px 10px; border-radius: 12px; }
    .num { font-weight: 700; color: #0f172a; margin-left: 2px; }
  }
}

.matrix-table-wrapper {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  overflow: scroll; /* Allow horizontal scroll if needed */
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);
}

.matrix-header {
  display: flex;
  position: sticky; /* Sticky Header */
  top: 0;
  z-index: 10;
  background: rgba(241, 245, 249, 0.95); /* Semi-transparent */
  backdrop-filter: blur(8px); /* Glassmorphism */
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px rgba(0,0,0,0.02); /* Subtle shadow */
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  min-width: 1000px;
  
  div { padding: 10px 16px; }
  .col-module { width: 260px; border-right: 1px solid #e2e8f0; flex-shrink: 0; }
  .col-access { width: 160px; border-right: 1px solid #e2e8f0; text-align: center; flex-shrink: 0; white-space: nowrap; }
  .col-ops { flex: 1; }
}

.matrix-body {
  min-width: 1000px;
  
  .matrix-row {
    display: flex;
    border-bottom: 1px solid #f1f5f9;
    border-left: 3px solid transparent; /* Prepare for hover highlight */
    transition: all 0.2s ease; /* Smooth transition */
    
    &:hover { 
      background: #f8fafc; 
      border-left-color: #10b981; /* Active Row Highlight */
    }
    &:last-child { border-bottom: none; }
    
    /* Adjust first column padding to account for border width if needed, 
       but since border is transparent by default, no layout shift occurs */
    
    .col-module { 
       width: 260px; 
       padding: 14px 16px; 
       border-right: 1px solid #f1f5f9; 
       display: flex; align-items: center;
       font-size: 13px; font-weight: 500; color: #334155;
       flex-shrink: 0;
       white-space: nowrap; overflow: hidden; text-overflow: ellipsis; /* Prevent wrapping */
    }
    .col-access { 
       width: 160px; 
       padding: 14px 16px; 
       border-right: 1px solid #f1f5f9; 
       display: flex; justify-content: center; align-items: center; /* Center vertically */
       flex-shrink: 0;
       white-space: nowrap; /* Prevent wrapping */
    }
    .col-ops { 
       flex: 1; 
       padding: 14px 16px; 
       display: flex; flex-direction: column; justify-content: center; /* Center ops content vertically if possible, or just top */
    }
    
    &.group-header-row {
       background: #fafbfc;
       border-bottom: 1px solid #e2e8f0;
       
       .col-module { border-right-color: #e2e8f0; font-weight: 700; color: #1e293b; align-items: center; }
       .bg-cell { background: repeating-linear-gradient(45deg, #f8fafc, #f8fafc 10px, #ffffff 10px, #ffffff 20px); }
       
       .module-title-wrap {
          display: flex; align-items: center; width: 100%;
          .module-icon { margin-right: 6px; color: #64748b; }
          .module-name { flex: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
          .module-check-all { font-size: 12px; margin-left: 8px; font-weight: 400; white-space: nowrap; }
       }
    }
    
    &.page-row {
       .page-title-wrap {
          padding-left: 24px;
          white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
          &.is-root { padding-left: 0; }
       }
    }
  }
}

.ops-group-container {
  display: flex; flex-direction: column; gap: 8px; margin-bottom: 8px;
}

.ops-group-block {
    background: #fdfdfd; 
    border: 1px solid #e2e8f0;
    border-radius: 6px;
    padding: 8px 12px;
    
    .ops-group-label-row {
        margin-bottom: 8px;
        display: flex; align-items: center; justify-content: space-between;
        border-bottom: 1px dashed #f1f5f9;
        padding-bottom: 4px;
        
        .og-label { 
            font-size: 12px; font-weight: 700; color: #475569; 
            display: flex; align-items: center; gap: 4px;
            &:before { content: ''; display: block; width: 6px; height: 6px; border-radius: 50%; background: #94a3b8; }
        }
    }
    .ops-list {
        display: flex; flex-wrap: wrap; gap: 12px;
    }
}

.tabs-list {
  display: flex; flex-direction: column; gap: 8px; border-top: 1px dashed #e2e8f0; padding-top: 10px; margin-top: 8px;
  
  .tab-item-inline {
     display: flex; align-items: flex-start; gap: 12px;
     background: #fdfdfd; padding: 6px 10px; border-radius: 6px; border: 1px solid #f1f5f9;
     
     .tab-label { font-size: 12px; font-weight: 600; color: #64748b; min-width: 80px; padding-top: 2px; }
     .tab-ops { display: flex; flex-wrap: wrap; gap: 10px; }
  }
}

.empty-search {
  padding: 40px; text-align: center; color: #94a3b8; font-size: 13px;
}

.no-ops { color: #cbd5e1; text-align: center; }

.ic-modal-footer {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px; /* Add gap between buttons */
  
  .premium-btn {
    height: 36px; padding: 0 24px; border-radius: 8px; font-weight: 500;
    
    &.primary-gradient {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      border: none; color: #ffffff; box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
      &:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(16, 185, 129, 0.3); }
    }
  }
}
</style>
