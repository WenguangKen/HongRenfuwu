<template>
  <div class="role-management-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="角色名称">
              <a-input v-model:value="filterForm.name" placeholder="请输入角色名称" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="状态">
              <a-select v-model:value="filterForm.status" placeholder="全部" allow-clear>
                <a-select-option value="enabled">启用</a-select-option>
                <a-select-option value="disabled">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="filter-actions">
              <a-space>
                <a-button type="primary" @click="handleFilter" class="primary-gradient">查询</a-button>
                <a-button @click="handleResetFilter">重置</a-button>
              </a-space>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="status-switcher-wrapper">
            <a-radio-group v-model:value="statusKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="all">全部角色</a-radio-button>
              <a-radio-button value="enabled">已启用</a-radio-button>
              <a-radio-button value="disabled">已停用</a-radio-button>
            </a-radio-group>
          </div>
          <div class="toolbar-btns">
            <a-button type="primary" @click="openCreate" class="primary-gradient" v-permission="'system.role.create'">
              <template #icon><plus-outlined /></template>
              新建角色
            </a-button>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="roles"
        :row-key="(record: any) => record.id"
        :pagination="false"
        :loading="loading"
        :scroll="tableScroll"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="role-info-cell">
              <div class="avatar-wrapper">
                <a-avatar :size="36" class="premium-avatar" :style="{ backgroundColor: getRoleColor(record.name) }">
                  {{ record.name.charAt(0).toUpperCase() }}
                </a-avatar>
              </div>
              <div class="info-content">
                <div class="role-name">{{ record.name }}</div>
                <div class="role-id">ID: {{ record.id }}</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'pageCount'">
            <div class="permission-cell-stacked">
              <div class="perm-row">
                <span class="label">页面</span>
                <a-badge :count="record.pagePermissions.length" :number-style="{ backgroundColor: '#6366f1' }" />
              </div>
              <div class="perm-row">
                <span class="label">操作</span>
                <a-badge :count="record.operationPermissions.length" :number-style="{ backgroundColor: '#10b981' }" />
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'userCount'">
            <div class="user-count-cell" @click="openUserModal(record)">
              <span class="count">{{ record.userCount || 0 }}</span>
              <span class="unit">位成员</span>
            </div>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-switch 
              :checked="record.status === 'enabled'" 
              checked-children="启用" 
              un-checked-children="停用"
              @change="(checked: boolean) => handleStatusChange(record, checked)"
              v-permission="'system.role.edit'"
            />
          </template>

          <template v-else-if="column.key === 'createTime'">
            <div class="time-cell-stacked">
              <div class="time-row">
                <span class="label">创建</span>
                <span class="value">{{ record.createTime }}</span>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="text" size="small" @click="openEdit(record)" class="action-btn edit" v-permission="'system.role.edit'">
                编辑
              </a-button>
              <a-button type="text" size="small" @click="removeRole(record.id)" class="action-btn delete" v-permission="'system.role.delete'">
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
      
      <!-- 自定义底部页脚 -->
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前内容数量 <span class="count-value">{{ pagination.total }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="onPageChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 弹窗 -->
    <RoleCreateModal
      v-model:open="modalOpen"
      :role-data="currentRole"
      :is-edit="isEdit"
      :page-tree-source="pageTreeSource"
      :operations-source="operationsSource"
      :all-page-keys="allPageKeys"
      @submit="handleRoleSubmit"
    />

    <RoleUserModal
      v-model:open="userModalOpen"
      :role-data="userModalRole"
      @unbind="handleUnbindUser"
    />

    <!-- Delete Confirmation Modal (Premium Heavy Operation Style) -->
    <a-modal
      v-model:open="deleteModalOpen"
      :title="null"
      :footer="null"
      :closable="false"
      width="480px"
      centered
      class="premium-delete-modal"
    >
      <div class="delete-modal-content">
        <!-- Header -->
        <div class="dm-header">
           <div class="dm-icon-wrapper">
             <ExclamationCircleFilled />
           </div>
           <div class="dm-title">删除角色确认</div>
           <div class="dm-subtitle">
             此操作为高风险操作，请谨慎处理
           </div>
        </div>

        <!-- Body -->
        <div class="dm-body">
           <div class="warning-text">
             您确定要永久删除角色 <span class="highlight-name">{{ deleteTarget?.name }}</span> 吗？
           </div>
           <div class="sub-warning">
             删除后，该角色下的 <span class="count">{{ deleteTarget?.userCount || 0 }}</span> 位用户将<span class="danger-text">立即失去</span>所有相关权限<br>且数据<span class="danger-text">无法恢复</span>。
           </div>
        </div>

        <!-- Footer -->
        <div class="dm-footer">
           <a-button class="cancel-btn" @click="deleteModalOpen = false">取消</a-button>
           <a-button 
             type="primary" 
             class="delete-btn" 
             :loading="deleteLoading" 
             @click="handleDeleteConfirm"
           >
             确认永久删除
           </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { PlusOutlined, ExclamationCircleFilled, CloseOutlined, SearchOutlined, SafetyCertificateOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import RoleCreateModal from '@/components/system/RoleCreateModal.vue';
import RoleUserModal from '@/components/system/RoleUserModal.vue';
// import { showDeleteConfirm } from '@/utils/modal'; // Removed generic modal
import { usePermissionStore } from '@/stores/permission';
import { 
  getRoleList, 
  createRole, 
  updateRole, 
  deleteRole, 
  updateRolePermissions as updateRolePermissionsApi,
} from '@/services/roleService';
import { getAllPermissions, fetchCurrentUserPermissions } from '@/services/permissionService';
import { getUserById, updateUser as updateUserApi } from '@/services/userService';
import { useUserStore } from '@/stores/user';

// ... (Existing Imports) ...

// 基础定义
interface RoleRow {
  id: number;
  name: string;
  description: string;
  status: 'enabled' | 'disabled';
  createTime: string;
  pagePermissions: string[];
  operationPermissions: string[];
  users: any[];
  userCount?: number;
}

const loading = ref(false);
const statusKey = ref('all');
const allPermissionMap = ref<Record<string, number>>({});
const permissionsFromApi = ref<Array<{ key: string; title: string; type: 'page' | 'tab' | 'operation'; parentKey?: string; }>>([]);
const loadingPermissions = ref(true);

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const filterForm = reactive({
  name: '',
  status: undefined as 'enabled' | 'disabled' | undefined,
});

const roles = ref<RoleRow[]>([]);

const columns: TableColumnsType = [
  { title: '角色信息', dataIndex: 'name', key: 'name', width: 220, fixed: 'left' },
  { title: '简介描述', dataIndex: 'description', key: 'description', width: 200 },
  { title: '权限概览', key: 'pageCount', width: 140, align: 'center' },
  { title: '关联用户', key: 'userCount', width: 120, align: 'center' },
  { title: '状态', key: 'status', width: 100, align: 'center' },
  { title: '创建时间', key: 'createTime', width: 180, align: 'center' },
  { title: '操作', key: 'action', width: 150, fixed: 'right', align: 'center' },
];

const tableScroll = computed(() => ({
  y: 'calc(100vh - 300px)',
  x: 'max-content'
}));

// 业务逻辑
const fetchRoles = async () => {
  loading.value = true;
  try {
    const userStore = useUserStore();
    if (!userStore.token) return;
    
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    
    if (statusKey.value !== 'all') params.status = statusKey.value;
    else if (filterForm.status) params.status = filterForm.status;
    
    if (filterForm.name) params.name = filterForm.name;

    const res = await getRoleList(params);
    roles.value = res.content.map(r => ({
      id: r.id,
      name: r.name,
      description: r.description || '',
      status: r.status,
      createTime: r.createdAt,
      pagePermissions: r.permissions?.filter(p => p.type === 'page').map(p => p.permissionKey) || [],
      operationPermissions: r.permissions?.filter(p => p.type !== 'page').map(p => p.permissionKey) || [],
      users: [],
      userCount: r.userCount || 0
    }));
    pagination.total = res.totalElements;
  } catch (error) {
    // Already handled globally
  } finally {
    loading.value = false;
  }
};

const handleTabChange = () => {
  pagination.current = 1;
  fetchRoles();
};

const handleFilter = () => {
  pagination.current = 1;
  fetchRoles();
};

const handleResetFilter = () => {
  filterForm.name = '';
  filterForm.status = undefined;
  handleFilter();
};

const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  fetchRoles();
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchRoles();
};

const handleStatusChange = async (record: RoleRow, checked: boolean) => {
  const newStatus = checked ? 'enabled' : 'disabled';
  const originalStatus = record.status;
  record.status = newStatus;
  try {
    await updateRole(record.id, { status: newStatus });
    message.success(`已${checked ? '启用' : '停用'}角色 ${record.name}`);
  } catch (error) {
    record.status = originalStatus;
  }
};

const getRoleColor = (name: string) => {
  const colors = ['#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

// --- Delete Role Logic ---
const deleteModalOpen = ref(false);
const deleteTarget = ref<RoleRow | null>(null);
const deleteLoading = ref(false);

const removeRole = (id: number) => {
  const target = roles.value.find(r => r.id === id);
  if (target) {
     deleteTarget.value = target;
     deleteModalOpen.value = true;
  }
};

const handleDeleteConfirm = async () => {
   if (!deleteTarget.value) return;
   deleteLoading.value = true;
   try {
     await deleteRole(deleteTarget.value.id);
     message.success('删除成功');
     deleteModalOpen.value = false;
     fetchRoles();
   } catch (e) {
     // error 
   } finally {
     deleteLoading.value = false;
   }
};

// 角色关联用户
const userModalOpen = ref(false);
const userModalRole = ref<any>(null);
const openUserModal = (role: RoleRow) => {
  userModalRole.value = { id: role.id, name: role.name, description: role.description };
  userModalOpen.value = true;
};
const handleUnbindUser = async (roleId: number, userId: number) => {
  try {
    const user = await getUserById(userId);
    const roleIds = user.roles?.map(r => r.id).filter(id => id !== roleId) || [];
    await updateUserApi(userId, { roleIds });
    message.success('已解除关联');
    fetchRoles();
  } catch (error) {
    // Error is handled by apiService
  }
};

// 新建/编辑
const modalOpen = ref(false);
const isEdit = ref(false);
const currentRole = ref<RoleRow | null>(null);

const openCreate = () => {
  isEdit.value = false;
  currentRole.value = null;
  modalOpen.value = true;
};

const openEdit = (role: RoleRow) => {
  isEdit.value = true;
  currentRole.value = role;
  modalOpen.value = true;
};

const handleRoleSubmit = async (data: any) => {
  const permissionIds = [...data.pagePermissions, ...data.operationPermissions]
    .map(k => allPermissionMap.value[k])
    .filter(id => id !== undefined);

  try {
    if (isEdit.value && data.id) {
      await updateRole(data.id, { name: data.name, description: data.description });
      await updateRolePermissionsApi(data.id, {
        pagePermissions: data.pagePermissions,
        operationPermissions: data.operationPermissions
      });
      message.success('保存成功');
      // 刷新权限
      const permissionStore = usePermissionStore();
      const permissions = await fetchCurrentUserPermissions();
      if (permissions) {
        permissionStore.setPagePermissions(permissions.pagePermissions);
        permissionStore.setOperationPermissions(permissions.operationPermissions);
      }
    } else {
      await createRole({ name: data.name, description: data.description, permissionIds });
      message.success('创建成功');
    }
    modalOpen.value = false;
    fetchRoles();
  } catch (error) {
    console.error('操作失败', error);
    message.error('操作失败，请稍后重试');
  }
};

// 权限配置源 - 从数据库 API 动态构建
const pageTreeSource = computed(() => {
  // 从 API 获取的权限中筛选出 page 类型的权限
  const pagePermissions = permissionsFromApi.value.filter(p => p.type === 'page');
  
  // 定义菜单分组（用于组织权限树的结构）
  const menuGroups: Record<string, { title: string; children: { key: string; title: string }[] }> = {
    'influencer': { title: '红人管理', children: [] },
    'order': { title: '订单管理', children: [] },
    'content': { title: '内容管理', children: [] },
    'finance': { title: '财务管理', children: [] },
    'commission': { title: '佣金管理', children: [] },
    'system': { title: '系统设置', children: [] },
  };
  
  const result: any[] = [];
  
  pagePermissions.forEach(p => {
    const key = p.key;
    const keyParts = key.split('.');
    const prefix = keyParts[0] as string;
    
    // 检查是否属于某个分组
    if (prefix in menuGroups && keyParts.length > 1) {
      menuGroups[prefix]?.children.push({ key: p.key, title: p.title });
    } else {
      // 顶级页面权限（如 dashboard, discount.list, product.list）
      result.push({ key: p.key, title: p.title });
    }
  });
  
  // 将有子项的分组添加到结果中
  Object.entries(menuGroups).forEach(([groupKey, group]) => {
    if (group.children.length > 0) {
      result.push({
        key: groupKey,
        title: group.title,
        children: group.children
      });
    }
  });
  
  return result;
});

const operationsSource = computed(() => {
  return permissionsFromApi.value
    .filter(p => p.type === 'operation' || p.type === 'tab')
    .map(p => ({
      key: p.key,
      title: p.title,
      pageKey: p.parentKey || '',
      type: p.type as 'tab' | 'operation',
      parentKey: p.parentKey
    }));
});

const allPageKeys = computed(() => {
  const keys: string[] = [];
  const dfs = (nodes: any[]) => {
    nodes.forEach(n => {
      if (!n.children) keys.push(n.key);
      else dfs(n.children);
    });
  };
  dfs(pageTreeSource.value);
  return keys;
});

onMounted(async () => {
  try {
    loadingPermissions.value = true;
    const res = await getAllPermissions();
    // Store for permission ID mapping
    res.forEach(p => (allPermissionMap.value[p.permissionKey] = p.id));
    // Convert to local format for operationsSource
    permissionsFromApi.value = res.map(p => ({
      key: p.permissionKey,
      title: p.title,
      type: p.type,
      parentKey: p.parentKey,
    }));
  } catch (e) {
    console.error('Failed to load permissions from API:', e);
  } finally {
    loadingPermissions.value = false;
  }
  fetchRoles();
});
</script>

<style lang="scss" scoped>
/* Previous Styles kept, adding new ones */
.role-management-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .glass-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
    transition: all 0.3s ease;
    &:hover { box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05); }
  }

  .filter-card {
    flex-shrink: 0;
    :deep(.ant-form-item) {
      margin-bottom: 0;
      .ant-form-item-label {
        padding: 0 !important;
        line-height: 1.2;
        > label { font-size: 12px; font-weight: 600; color: #64748b; height: 16px; margin-bottom: 4px; }
      }
    }
    .filter-actions { height: 100%; display: flex; align-items: flex-end; padding-bottom: 2px; }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;
    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 0 16px;
      min-height: 48px;
      display: flex;
      align-items: center;
      .ant-card-head-title { padding: 0; width: 100%; }
    }
    :deep(.ant-card-body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      padding: 0 !important;
    }
  }

  .table-actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    .premium-segmented {
      display: flex;
      gap: 4px;
      :deep(.ant-radio-button-wrapper) {
        border: 1px solid transparent;
        background: transparent;
        border-radius: 6px;
        height: 28px;
        line-height: 26px;
        font-weight: 500;
        color: #64748b;
        font-size: 13px;
        padding: 0 12px;
        transition: all 0.3s;
        &:before { display: none; }
        &:hover { color: #1890ff; background: #f1f5f9; }
        &.ant-radio-button-wrapper-checked {
          background: #eff6ff;
          color: #1890ff;
          border-color: #bfdbfe;
          font-weight: 600;
        }
      }
    }
  }

  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    :deep(.ant-spin-nested-loading), :deep(.ant-spin-container), :deep(.ant-table), :deep(.ant-table-container) {
      flex: 1; display: flex; flex-direction: column; overflow: hidden;
    }
    :deep(.ant-table-content) { overflow: auto !important; }
    :deep(.ant-table) { background: transparent; border-collapse: collapse !important; border-spacing: 0 !important; }
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc !important; padding: 10px 8px !important; font-weight: 700; color: #64748b; font-size: 13px; border-bottom: 2px solid #f1f5f9;
    }
    :deep(.ant-table-tbody > tr > td) { padding: 8px 8px !important; border-bottom: 1px solid #f8fafc; }
    :deep(.ant-table-tbody > tr:hover > td) { background: #f0f7ff !important; }
    /* Force override for measure row */
    :deep(.ant-table-tbody > tr.ant-table-measure-row > td) {
      padding: 0 !important;
      border: none !important;
      height: 0 !important;
      line-height: 0 !important;
      overflow: hidden !important;
      font-size: 0 !important;
    }
  }

  .primary-gradient {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    border: none; color: #fff; box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
    &:hover { box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3); transform: translateY(-1px); }
  }

  .action-btn {
    font-weight: 600;
    &.edit { color: #1890ff; &:hover { background: #eff6ff; } }
    &.delete { color: #ff4d4f; &:hover { background: #fff1f0; } }
  }

  /* 单元格扩展样式 */
  .role-info-cell {
    display: flex; align-items: center; gap: 12px;
    .avatar-wrapper { flex-shrink: 0; }
    .info-content {
      .role-name { font-weight: 600; color: #1e293b; font-size: 14px; }
      .role-id { font-size: 11px; color: #94a3b8; font-family: monospace; }
    }
  }

  .permission-cell-stacked {
    display: flex; flex-direction: column; gap: 4px; align-items: center;
    .perm-row {
      display: flex; align-items: center; gap: 8px; width: 100%; justify-content: center;
      .label { font-size: 11px; color: #94a3b8; font-weight: 600; width: 28px; text-align: right; }
      :deep(.ant-badge-count) { min-width: 32px; height: 16px; line-height: 16px; font-size: 10px; border-radius: 4px; }
    }
  }

  .user-count-cell {
    cursor: pointer; display: flex; flex-direction: column; align-items: center;
    padding: 4px 8px; border-radius: 8px; transition: all 0.3s;
    &:hover { background: #eff6ff; .count { color: #1890ff; } }
    .count { font-size: 16px; font-weight: 700; color: #334155; line-height: 1.2; }
    .unit { font-size: 10px; color: #94a3b8; }
  }

  .time-cell-stacked {
    display: flex; flex-direction: column; gap: 2px;
    .time-row {
      display: flex; gap: 6px; font-size: 12px;
      .label { color: #94a3b8; flex-shrink: 0; }
      .value { color: #64748b; }
    }
  }

  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    .footer-left {
      color: #94a3b8; font-size: 12px;
      .count-value { font-weight: 600; color: #1e293b; background: #f1f5f9; padding: 2px 6px; border-radius: 4px; }
    }
  }
}

</style>

<!-- Non-scoped styles for Modal (since it teleports to body) -->
<style lang="scss">
.premium-delete-modal {
  .ant-modal-content {
    border-radius: 16px;
    overflow: hidden;
    padding: 0;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  }
  
  .delete-modal-content {
    display: flex; flex-direction: column;
    
    .dm-header {
       background: #1e293b; /* Dark Header for Serious Op */
       padding: 24px;
       display: flex; flex-direction: column; align-items: center; justify-content: center;
       background: linear-gradient(135deg, #fee2e2 0%, #ffffff 100%);
       border-bottom: 1px solid #fecaca;
       
       .dm-icon-wrapper {
         width: 64px; height: 64px;
         background: linear-gradient(135deg, #ef4444 0%, #b91c1c 100%);
         border-radius: 50%;
         display: flex; align-items: center; justify-content: center;
         color: #fff; font-size: 32px;
         box-shadow: 0 10px 15px -3px rgba(239, 68, 68, 0.3);
         margin-bottom: 16px;
         animation: bounce 1s infinite;
       }
       
       .dm-title { font-size: 20px; font-weight: 800; color: #7f1d1d; margin-bottom: 4px; }
       .dm-subtitle { font-size: 13px; color: #991b1b; opacity: 0.8; }
    }
    
    .dm-body {
       padding: 32px 24px;
       text-align: center;
       
       .warning-text { font-size: 16px; color: #374151; margin-bottom: 12px; }
       .highlight-name { font-weight: 800; color: #111827; background: #f3f4f6; padding: 2px 8px; border-radius: 4px; }
       
       .sub-warning { 
          font-size: 13px; color: #6b7280; line-height: 1.6; background: #fef2f2; padding: 12px; border-radius: 8px; border: 1px solid #fee2e2;
          .count { font-weight: 700; color: #000; }
          .danger-text { color: #dc2626; font-weight: 700; }
       }
    }
    
    .dm-footer {
       padding: 16px 24px;
       background: #f9fafb;
       border-top: 1px solid #f3f4f6;
       display: flex; justify-content: flex-end; gap: 12px;
       
       .cancel-btn { height: 40px; border-radius: 8px; border: 1px solid #d1d5db; font-weight: 500; font-size: 14px; padding: 0 24px; }
       .delete-btn {
          height: 40px; border-radius: 8px; font-weight: 600; font-size: 14px; padding: 0 24px;
          background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); border: none;
          box-shadow: 0 4px 6px -1px rgba(220, 38, 38, 0.3);
          &:hover { box-shadow: 0 10px 15px -3px rgba(220, 38, 38, 0.4); transform: translateY(-1px); }
       }
    }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}
</style>