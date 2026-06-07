<template>
  <div class="user-management-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="用户名">
              <a-input v-model:value="filterForm.username" placeholder="请输入用户名" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="角色">
              <a-select v-model:value="filterForm.role" placeholder="全部" allow-clear>
                <a-select-option value="Admin">管理员</a-select-option>
                <a-select-option value="Operation">运营人员</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="邮箱">
              <a-input v-model:value="filterForm.email" placeholder="请输入邮箱" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="状态">
              <a-select v-model:value="filterForm.status" placeholder="全部" allow-clear>
                <a-select-option value="active">启用</a-select-option>
                <a-select-option value="inactive">停用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
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
            <a-radio-group v-model:value="activeKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="all">全部用户</a-radio-button>
              <a-radio-button value="active">已启用</a-radio-button>
              <a-radio-button value="inactive">已停用</a-radio-button>
            </a-radio-group>
          </div>
          <div class="toolbar-btns">
            <a-button type="primary" @click="showModal()" class="primary-gradient" v-permission="'system.user.create'">
              <template #icon><plus-outlined /></template>
              新增用户
            </a-button>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="data"
        :row-key="(record: any) => record.id"
        :pagination="false"
        :loading="loading"
        :scroll="tableScroll"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'username'">
            <div class="user-info-cell">
              <div class="avatar-wrapper">
                <a-avatar :size="36" class="premium-avatar" :src="record.avatarUrl" :style="{ backgroundColor: getAvatarColor(record.username) }">
                  {{ !record.avatarUrl ? record.username.charAt(0).toUpperCase() : '' }}
                </a-avatar>
              </div>
              <div class="info-content">
                <div class="user-name">{{ record.username }}</div>
                <div class="user-id">ID: {{ record.id }}</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'role'">
            <a-tag :color="record.roles && record.roles.length > 0 && record.roles[0].name === 'Admin' ? 'rose' : 'orange'" class="status-tag">
              {{ record.roles && record.roles.length > 0 ? record.roles[0].name : '普通用户' }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'allocatedStores'">
            <div class="allocated-stores-cell" style="max-height: 80px; overflow-y: auto; display: flex; flex-wrap: wrap; gap: 4px; padding: 4px 0;">
              <template v-if="record.allocatedStores && record.allocatedStores.length > 0">
                <div 
                  v-for="store in record.allocatedStores" 
                  :key="store" 
                  class="store-tag-mini"
                  style="display: inline-flex; align-items: center; gap: 4px; background: rgba(251, 113, 133, 0.06); border: 1px solid rgba(251, 113, 133, 0.15); padding: 2px 6px; border-radius: 4px; font-size: 11px;"
                >
                  <span style="font-weight: 600; color: #1e293b;">{{ store.split('|')[1] || store }}</span>
                  <span style="font-size: 9px; background: rgba(251, 113, 133, 0.12); color: #fb7185; padding: 0 4px; border-radius: 2px; text-transform: uppercase; font-weight: bold;">{{ store.split('|')[0] }}</span>
                </div>
              </template>
              <span v-else style="color: #94a3b8; font-size: 12px; font-style: italic;">未分配</span>
            </div>
          </template>

          <template v-else-if="column.key === 'email'">
            <div class="email-cell">
              <span v-if="isEncryptedData(record.email)" class="encrypted-data-warning">[解密失败]</span>
              <span v-else>{{ record.email }}</span>
            </div>
          </template>

          <template v-else-if="column.key === 'time'">
            <div class="time-cell-stacked">
              <div class="time-row">
                <span class="label">创建</span>
                <span class="value">{{ record.createdAt }}</span>
              </div>
              <div class="time-row">
                <span class="label">登录</span>
                <span class="value">{{ record.lastLoginTime || '-' }}</span>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'loginInfo'">
            <div class="login-info-cell">
              <div class="ip-row">
                <span class="label">IP:</span>
                <span class="value">{{ formatIp(record.lastLoginIp) }}</span>
              </div>
              <div class="location-row">
                <span class="value">{{ record.lastLoginLocation || '-' }}</span>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-switch 
              :checked="record.status === 'active'" 
              checked-children="启用" 
              un-checked-children="停用"
              @change="(checked: boolean) => handleStatusChange(record, checked)"
              v-permission="'system.user.switch'"
            />
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="text" size="small" @click="showModal(record)" class="action-btn edit" v-permission="'system.user.edit'">
                编辑
              </a-button>
              <a-button type="text" size="small" @click="openLogModal(record)" class="action-btn log" v-permission="'system.user.log'">
                日志
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

    <!-- 弹窗组件 -->
    <UserEditModal
      v-if="visible"
      v-model:open="visible"
      :record="currentRecord"
      @ok="handleOk"
    />
    
    <UserLogModal
      v-if="logModalOpen"
      v-model:open="logModalOpen"
      :user="logModalUser"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import UserEditModal from '@/components/system/UserEditModal.vue';
import UserLogModal from '@/components/system/UserLogModal.vue';
import { 
  getUserList, 
  createUser, 
  updateUser, 
  updateUserStatus, 
  type User 
} from '@/services/userService';
import { useUserStore } from '@/stores/user';

// 状态定义
const visible = ref(false);
const loading = ref(false);
const currentRecord = ref<User | null>(null);
const activeKey = ref('all');
const data = ref<User[]>([]);

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

const filterForm = reactive({
  username: '',
  role: undefined as string | undefined,
  email: '',
  status: undefined as string | undefined,
});

// 表格列定义
const columns: TableColumnsType = [
  { title: '用户信息', dataIndex: 'username', key: 'username', width: 200, fixed: 'left' },
  { title: '角色', key: 'role', width: 120, align: 'center' },
  { title: '已分配店铺', dataIndex: 'allocatedStores', key: 'allocatedStores', width: 220 },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: 200 },
  { title: '时间', key: 'time', width: 200, align: 'center' },
  { title: '最近登录', key: 'loginInfo', width: 200, align: 'left' },
  { title: '状态', key: 'status', width: 100, align: 'center' },
  { title: '操作', key: 'action', width: 150, fixed: 'right', align: 'center' },
];

// 计算属性
const tableScroll = computed(() => ({
  y: 'calc(100vh - 320px)',
  x: 'max-content'
}));

// 方法定义
const fetchUsers = async () => {
  loading.value = true;
  try {
    const userStore = useUserStore();
    if (!userStore.token) return;
    
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    
    if (activeKey.value !== 'all') params.status = activeKey.value;
    else if (filterForm.status) params.status = filterForm.status;
    
    if (filterForm.username) params.username = filterForm.username;
    if (filterForm.role) params.role = filterForm.role;
    if (filterForm.email) params.email = filterForm.email;
    
    const res = await getUserList(params);
    data.value = res.content;
    pagination.total = res.totalElements;
  } catch (error) {
    // 错误已由 global interceptor 处理
  } finally {
    loading.value = false;
  }
};

const handleTabChange = () => {
  pagination.current = 1;
  fetchUsers();
};

const handleFilter = () => {
  pagination.current = 1;
  fetchUsers();
};

const handleResetFilter = () => {
  Object.keys(filterForm).forEach(key => (filterForm as any)[key] = undefined);
  handleFilter();
};

const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  fetchUsers();
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  fetchUsers();
};

const handleStatusChange = async (record: User, checked: boolean) => {
  const newStatus = checked ? 'active' : 'inactive';
  const originalStatus = record.status;
  record.status = newStatus;
  try {
    await updateUserStatus(record.id, newStatus);
    message.success(`已${checked ? '启用' : '停用'}用户 ${record.username}`);
  } catch (error) {
    record.status = originalStatus;
  }
};

const formatIp = (ip?: string) => {
  if (!ip) return '-';
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '127.0.0.1';
  return ip;
};

const getAvatarColor = (username: string) => {
  const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#1890ff', '#52c41a'];
  let hash = 0;
  for (let i = 0; i < username.length; i++) hash = username.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

const isEncryptedData = (value: string | undefined): boolean => {
  if (!value) return false;
  return value.length > 40 && /^[A-Za-z0-9+/=]+$/.test(value) && !value.includes('@');
};

const logModalOpen = ref(false);
const logModalUser = ref<any>(null);
const openLogModal = (record: any) => {
  logModalUser.value = record;
  logModalOpen.value = true;
};

const showModal = (record?: any) => {
  currentRecord.value = record || null;
  visible.value = true;
};

const handleOk = async (formData: any) => {
  try {
    if (formData.isEdit) {
      await updateUser(formData.id, {
        username: formData.username,
        email: formData.email,
        phone: formData.phone,
        roleIds: formData.roleIds || [],
        avatarUrl: formData.avatar,
        allocatedStores: formData.allocatedStores || []
      });
      message.success('更新成功');
    } else {
      await createUser({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        phone: formData.phone,
        roleIds: formData.roleIds || [],
        avatarUrl: formData.avatar,
        allocatedStores: formData.allocatedStores || []
      });
      message.success('创建成功');
    }
    visible.value = false;
    fetchUsers();
  } catch (error) {
    console.error('操作失败', error);
    message.error('操作失败，请稍后重试');
  }
};

onMounted(fetchUsers);
</script>

<style lang="scss" scoped>
.user-management-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;

  /* 玻璃拟态卡片基础样式 */
  .glass-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    }
  }

  .filter-card {
    flex-shrink: 0;
    
    :deep(.ant-form-item) {
      margin-bottom: 0;
      .ant-form-item-label {
        padding: 0 !important;
        line-height: 1.2;
        > label {
          font-size: 12px;
          font-weight: 600;
          color: #64748b;
          height: 16px;
          margin-bottom: 4px;
        }
      }
    }

    .filter-actions {
      height: 100%;
      display: flex;
      align-items: flex-end;
      padding-bottom: 2px;
    }
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
    
    :deep(.ant-spin-nested-loading),
    :deep(.ant-spin-container),
    :deep(.ant-table),
    :deep(.ant-table-container) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    :deep(.ant-table-content) {
      overflow: auto !important;
    }

    :deep(.ant-table) {
      background: transparent;
      border-collapse: collapse !important;
      border-spacing: 0 !important;
    }
    
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc !important;
      padding: 10px 8px !important;
      font-weight: 700;
      color: #64748b;
      font-size: 13px;
      border-bottom: 2px solid #f1f5f9;
    }

    :deep(.ant-table-tbody > tr > td) {
      padding: 8px 8px !important;
      border-bottom: 1px solid #f8fafc;
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }

    /* 隐藏测量行 */
    :deep(.ant-table-tbody > tr.ant-table-measure-row > td) {
      padding: 0 !important;
      border: none !important;
      height: 0 !important;
      line-height: 0 !important;
      overflow: hidden !important;
      font-size: 0 !important;
    }
  }

  /* 单元格样式 */
  .user-info-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    .avatar-wrapper { flex-shrink: 0; }
    .info-content {
      .user-name { font-weight: 600; color: #1e293b; font-size: 14px; }
      .user-id { font-size: 11px; color: #94a3b8; font-family: monospace; }
    }
  }

  .time-cell-stacked {
    display: flex;
    flex-direction: column;
    gap: 2px;
    .time-row {
      display: flex;
      gap: 6px;
      font-size: 12px;
      .label { color: #94a3b8; flex-shrink: 0; }
      .value { color: #64748b; }
    }
  }

  .login-info-cell {
    display: flex;
    flex-direction: column;
    .ip-row { font-size: 12px; color: #64748b; }
    .location-row { font-size: 12px; color: #94a3b8; }
  }

  /* 按钮与标签 */
  .primary-gradient {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    border: none;
    color: #fff;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
    &:hover { box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3); transform: translateY(-1px); }
  }

  .action-btn {
    font-weight: 600;
    &.edit { color: #1890ff; &:hover { background: #eff6ff; } }
    &.log { color: #64748b; &:hover { background: #f1f5f9; } }
  }

  .encrypted-data-warning {
    color: #f59e0b;
    font-size: 12px;
    font-weight: 600;
    background: #fef3c7;
    padding: 2px 8px;
    border-radius: 4px;
    border: 1px solid #fcd34d;
  }

  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    
    .footer-left {
      color: #94a3b8;
      font-size: 12px;
      .count-value { font-weight: 600; color: #1e293b; background: #f1f5f9; padding: 2px 6px; border-radius: 4px; }
    }
  }
}
</style>
