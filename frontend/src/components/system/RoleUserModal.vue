<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="800px"
    :footer="null"
    destroy-on-close
    centered
    class="premium-refined-modal"
    :closable="false"
    :mask-closable="false"
  >
    <!-- Custom Header -->
    <div class="ic-modal-header glass-header">
      <div class="ic-header-left">
        <div class="ic-header-icon-wrapper">
          <TeamOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">{{ roleData?.name }} - 关联用户</div>
          <div class="ic-header-subtitle">管理该角色的关联用户成员 (Manage users associated with this role)</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- Content Body -->
    <div class="ic-modal-body">
      <div class="ic-section">
        <div class="ic-section-header">
          <span class="ic-section-title">用户列表 (User List)</span>
          <span class="member-count-badge">{{ users.length }} 成员</span>
        </div>
        <div class="ic-section-body" style="padding: 0;">
          <a-table
            :data-source="users"
            :pagination="false"
            :loading="loading"
            :row-key="(u: RoleUser) => u.id"
            class="premium-table-minimal"
            :scroll="{ y: 400 }"
          >
            <a-table-column title="用户姓名" data-index="name" key="name">
              <template #default="{ text }">
                <div class="user-cell">
                  <div class="user-avatar-small" :style="{ background: getRandomGradient(text) }">
                    {{ text?.charAt(0)?.toUpperCase() }}
                  </div>
                  <span class="user-name-text">{{ text }}</span>
                </div>
              </template>
            </a-table-column>
            
            <a-table-column title="邮箱" data-index="email" key="email">
              <template #default="{ text }">
                <span class="email-text">{{ text || '-' }}</span>
              </template>
            </a-table-column>
            
            <a-table-column key="action" title="操作" width="100" align="center">
              <template #default="{ record }: { record: RoleUser }">
                <a-tooltip title="解除关联">
                  <a-button 
                    type="text" 
                    danger 
                    class="action-btn-icon" 
                    @click="unbindUser(record.id)"
                  >
                    <DisconnectOutlined />
                    <span style="font-size: 13px; margin-left: 4px;">解除</span>
                  </a-button>
                </a-tooltip>
              </template>
            </a-table-column>
          </a-table>
          
          <div v-if="!users.length && !loading" class="empty-state">
            <div class="empty-icon"><TeamOutlined /></div>
            <p>暂无关联用户</p>
          </div>
        </div>
      </div>
    </div>
      
    <!-- Footer -->
    <div class="ic-modal-footer">
      <a-button @click="handleCancel" class="premium-btn">关闭</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { TeamOutlined, CloseOutlined, DisconnectOutlined } from '@ant-design/icons-vue';
import { showDeleteConfirm } from '@/utils/modal';
import { getRoleUsers } from '@/services/roleService';

interface RoleUser {
  id: number;
  name: string;
  account: string;
  email?: string;
}

interface RoleRow {
  id: number;
  name: string;
  description?: string;
  users: RoleUser[];
}

const props = defineProps<{
  open: boolean;
  roleData: RoleRow | null;
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
  'unbind': [roleId: number, userId: number];
}>();

const visible = ref(false);
const users = ref<RoleUser[]>([]);
const loading = ref(false);

const fetchUsers = async () => {
  if (!props.roleData) return;
  loading.value = true;
  users.value = []; 
  try {
    const res = await getRoleUsers(props.roleData.id);
    users.value = res.map(u => ({
      id: u.id,
      name: u.username,
      account: u.username,
      email: u.email
    }));
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

watch(() => props.open, (val) => {
  visible.value = val;
  if (val) {
    fetchUsers();
  }
});

watch(visible, (val) => {
  emit('update:open', val);
});

const handleCancel = () => {
  visible.value = false;
};

const unbindUser = (userId: number) => {
  if (!props.roleData) return;
  showDeleteConfirm({
    title: '确定解除关联？',
    content: '解除所有关联后，用户将失去该角色权限。',
    onOk: () => {
        emit('unbind', props.roleData!.id, userId);
        users.value = users.value.filter(u => u.id !== userId);
    },
  });
};

const getRandomGradient = (name?: string) => {
  const gradients = [
    'linear-gradient(135deg, #3b82f6 0%, #2563eb 100%)',
    'linear-gradient(135deg, #10b981 0%, #059669 100%)',
    'linear-gradient(135deg, #f59e0b 0%, #d97706 100%)',
    'linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)',
    'linear-gradient(135deg, #ec4899 0%, #db2777 100%)',
  ];
  if (!name) return gradients[0];
  const index = name.charCodeAt(0) % gradients.length;
  return gradients[index];
};
</script>

<style lang="scss" scoped>
/* Reuse styles from InfluencerCreateModal for consistency */
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
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  }

  .ic-header-title {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    line-height: 1.2;
  }

  .ic-header-subtitle {
    font-size: 12px;
    color: #94a3b8;
    margin-top: 2px;
  }
  
  .close-btn {
    color: #94a3b8;
    &:hover { color: #64748b; background: #f1f5f9; }
  }
}

.ic-modal-body {
  padding: 24px;
  background: #f8fafc;
  min-height: 400px;
}

.ic-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.01);
  overflow: hidden;
  
  .ic-section-header {
    padding: 16px 20px;
    border-bottom: 1px solid #f1f5f9;
    background: #ffffff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .ic-section-title {
      font-size: 14px;
      font-weight: 700;
      color: #334155;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &:before {
        content: "";
        display: block;
        width: 4px;
        height: 16px;
        background: #3b82f6;
        border-radius: 2px;
      }
    }
    
    .member-count-badge {
      background: #eff6ff;
      color: #3b82f6;
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
    }
  }
  
  .ic-section-body {
    padding: 0;
  }
}

.premium-table-minimal {
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    color: #64748b;
    font-weight: 600;
    font-size: 13px;
    padding: 12px 20px;
    border-bottom: 1px solid #f1f5f9;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    padding: 14px 20px;
    font-size: 14px;
    color: #334155;
    border-bottom: 1px solid #f8fafc;
  }
  
  :deep(.ant-table-row:hover > td) {
    background: #f8fafc !important;
  }
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .user-avatar-small {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    font-size: 14px;
  }
  
  .user-name-text {
    font-weight: 500;
    color: #1e293b;
  }
}

.email-text {
  font-family: 'Roboto Mono', monospace;
  font-size: 13px;
  color: #64748b;
}

.action-btn-icon {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  border-radius: 6px;
  
  &:hover {
    background: #fee2e2;
    color: #ef4444;
  }
}

.empty-state {
  padding: 80px 0;
  text-align: center;
  
  .empty-icon {
    font-size: 48px;
    color: #cbd5e1;
    margin-bottom: 16px;
  }
  
  p {
    color: #94a3b8;
    font-size: 14px;
  }
}

.ic-modal-footer {
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
  text-align: right;
  
  .premium-btn {
    height: 36px;
    padding: 0 24px;
    border-radius: 8px;
    font-weight: 500;
    border: 1px solid #e2e8f0;
    
    &:hover {
      border-color: #3b82f6;
      color: #3b82f6;
    }
  }
}
</style>
