<template>
  <div class="user-profile-page">
    <a-row :gutter="8">
      <a-col :span="24" :md="8">
        <a-card :bordered="false" class="profile-card glass-card">
          <div class="profile-header">
            <div class="avatar-wrapper">
              <a-avatar :size="100" class="profile-avatar" :style="{ background: 'linear-gradient(135deg, #fb7185 0%, #fda4af 100%)' }">
                {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'A' }}
              </a-avatar>
              <div class="role-badge">{{ userInfo.role }}</div>
            </div>
            
            <div class="profile-name-section">
              <template v-if="editingField === 'name'">
                <a-input 
                  v-model:value="editValue" 
                  size="small" 
                  style="width: 150px; text-align: center;" 
                  @pressEnter="saveEdit('name')"
                />
                <a-button type="link" size="small" @click="saveEdit('name')" :loading="loadingUpdate"><check-outlined /></a-button>
                <a-button type="link" size="small" @click="cancelEdit"><close-outlined /></a-button>
              </template>
              <template v-else>
                <h2 class="profile-name">{{ userInfo.name }}</h2>
                <edit-outlined class="edit-icon" @click="startEdit('name', userInfo.name)" />
              </template>
            </div>
            
            <div class="profile-email-section">
              <mail-outlined /> {{ userInfo.email }}
            </div>
          </div>
          
          <a-divider />
          
          <div class="profile-info-list">
            <div class="info-item">
              <span class="label"><user-outlined /> 用户名</span>
              <div class="value-wrapper">
                <template v-if="editingField === 'username'">
                  <a-input 
                    v-model:value="editValue" 
                    size="small" 
                    style="width: 120px;" 
                    @pressEnter="saveEdit('username')"
                  />
                  <a-space :size="4">
                    <a-button type="link" size="small" @click="saveEdit('username')" :loading="loadingUpdate"><check-outlined /></a-button>
                    <a-button type="link" size="small" @click="cancelEdit"><close-outlined /></a-button>
                  </a-space>
                </template>
                <template v-else>
                  <span class="value">{{ userInfo.username }}</span>
                  <edit-outlined class="edit-icon-small" @click="startEdit('username', userInfo.username)" />
                </template>
              </div>
            </div>
            <div class="info-item">
              <span class="label"><phone-outlined /> 电话</span>
              <div class="value-wrapper">
                <template v-if="editingField === 'phone'">
                  <a-input 
                    v-model:value="editValue" 
                    size="small" 
                    style="width: 120px;" 
                    @pressEnter="saveEdit('phone')"
                  />
                  <a-space :size="4">
                    <a-button type="link" size="small" @click="saveEdit('phone')" :loading="loadingUpdate"><check-outlined /></a-button>
                    <a-button type="link" size="small" @click="cancelEdit"><close-outlined /></a-button>
                  </a-space>
                </template>
                <template v-else>
                  <span class="value">{{ userInfo.phone || '-' }}</span>
                  <edit-outlined class="edit-icon-small" @click="startEdit('phone', userInfo.phone)" />
                </template>
              </div>
            </div>
            <div class="info-item">
              <span class="label"><clock-circle-outlined /> 注册时间</span>
              <div class="value-wrapper">
                <span class="value">{{ userInfo.createTime }}</span>
                <span class="edit-icon-placeholder"></span>
              </div>
            </div>
            <div class="info-item">
              <span class="label"><GlobalOutlined /> 最后登录IP</span>
              <div class="value-wrapper">
                <span class="value">{{ formatIp(userInfo.lastLoginIp) }}</span>
                <span class="edit-icon-placeholder"></span>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <a-col :span="24" :md="16">
        <a-card :bordered="false" title="安全设置" class="security-card glass-card">
          <a-list item-layout="horizontal">
            <a-list-item>
              <a-list-item-meta
                title="账户密码"
                description="定期修改密码可以保护您的账户安全"
              >
                <template #avatar>
                  <a-avatar style="background-color: #e0e7ff; color: #6366f1">
                    <template #icon><lock-outlined /></template>
                  </a-avatar>
                </template>
              </a-list-item-meta>
              <template #actions>
                <a-button type="link" @click="showPasswordModal">修改</a-button>
              </template>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- 操作日志区域 -->
    <a-row style="margin-top: 8px;">
      <a-col :span="24">
        <a-card :bordered="false" title="操作日志" class="log-card-container glass-card">
          <div class="log-list-container">
            <a-timeline class="premium-timeline">
              <a-timeline-item
                v-for="log in userLogs"
                :key="log.id"
                class="log-node"
              >
                <template #dot>
                  <div :class="['node-dot', getLogTypeClass(log.type)]"></div>
                </template>
                
                <div class="log-card">
                  <div class="log-header">
                    <div class="left">
                      <a-tag :class="['type-tag', getLogTypeClass(log.type)]">{{ log.type }}</a-tag>
                      <span class="operator" v-if="log.operator">
                        <UserOutlined /> {{ log.operator }}
                      </span>
                    </div>
                    <div class="right">
                      <div class="ip-info">
                        <span class="ip">{{ log.ip }}</span>
                        <span class="location">{{ log.location }}</span>
                      </div>
                      <span class="time">{{ log.time }}</span>
                    </div>
                  </div>
                  
                  <div class="log-body">
                    <p class="content">{{ log.desc }}</p>
                    
                    <div v-if="log.details && Object.keys(log.details).length" class="details-box">
                      <div v-for="(value, key) in log.details" :key="key" class="detail-item">
                        <span class="label">{{ key }}</span>
                        <div class="value-container">
                          <template v-if="isChangeObject(value)">
                            <span class="old-val">{{ value.old || '-' }}</span>
                            <span class="change-arrow">→</span>
                            <span class="new-val">{{ value.new || '-' }}</span>
                          </template>
                          <template v-else>
                            <span class="value">{{ value }}</span>
                          </template>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </a-timeline-item>
            </a-timeline>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 修改密码弹窗 -->
    <ChangePasswordModal
      v-model:open="passwordModalVisible"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { 
  UserOutlined, 
  PhoneOutlined, 
  ClockCircleOutlined,
  LockOutlined,
  MailOutlined,
  EditOutlined,
  CheckOutlined,
  CloseOutlined,
  LoadingOutlined,
  CameraOutlined,
  GlobalOutlined // Added GlobalOutlined for IP
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import ChangePasswordModal from '@/components/system/ChangePasswordModal.vue';
import { useUserStore } from '@/stores/user';
import { getUserLogs } from '@/services/userService';

const userStore = useUserStore();

interface LogDetailItem {
  old: string;
  new: string;
}

interface UserLog {
  id: number;
  type: string;
  time: string;
  desc: string;
  operator: string;
  ip: string;
  location: string;
  details: Record<string, string | LogDetailItem>;
}

// 真实用户信息 - 优先从 store 获取
const userInfo = computed(() => ({
  username: userStore.userInfo?.username || '-',
  name: userStore.userInfo?.username || '用户',
  email: userStore.userInfo?.email || '-',
  phone: userStore.userInfo?.phone || '-',
  role: (userStore.userInfo as any)?.roles?.[0]?.name || '普通用户',
  avatar: userStore.userInfo?.avatarUrl || '',
  createTime: (userStore.userInfo as any)?.createdAt ? dayjs((userStore.userInfo as any).createdAt).format('YYYY-MM-DD') : '-',
  lastLoginIp: userStore.userInfo?.lastLoginIp || '-' // Added lastLoginIp
}));

const editableUserInfo = reactive({
  name: '',
  username: '',
  phone: ''
});

// 编辑状态管理
const editingField = ref<string | null>(null);
const editValue = ref('');
const loadingUpdate = ref(false);

const startEdit = (field: string, value: string) => {
  editingField.value = field;
  editValue.value = value;
};

const cancelEdit = () => {
  editingField.value = null;
  editValue.value = '';
};

const saveEdit = async (field: string) => {
  if (!editValue.value && field !== 'phone') {
    message.warning('内容不能为空');
    return;
  }
  
  loadingUpdate.value = true;
  // 模拟 API 调用以更新用户信息
  setTimeout(() => {
    // 这里实际上应该调用后端接口更新用户
    // 现在先模拟成功提示并更新本地编辑参考
    message.success('更新成功');
    editingField.value = null;
    loadingUpdate.value = false;
  }, 500);
};

// 模拟日志数据

const userLogs = ref<UserLog[]>([]);

const fetchRealLogs = async () => {
  if (!userStore.userInfo?.id) return;
  try {
    const res = await getUserLogs(userStore.userInfo.id, { page: 0, size: 20 });
    userLogs.value = res.content.map((l: any) => ({
      id: l.id,
      type: l.actionType,
      time: dayjs(l.createdAt).format('YYYY-MM-DD HH:mm:ss'),
      desc: l.actionDesc,
      operator: l.operatorName || 'System',
      ip: formatIp(l.ipAddress),
      location: l.location || '未知',
      details: l.details ? JSON.parse(l.details) : {}
    }));
  } catch (error) {
    console.error('获取日志失败:', error);
  }
};

const formatIp = (ip?: string) => {
  if (!ip) return '-';
  if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '127.0.0.1';
  return ip;
};

onMounted(() => {
  userStore.loadUserInfo();
  fetchRealLogs();
});

const getLogTypeClass = (type: string) => {
  switch (type) {
    case '登录': return 'login';
    case '退出': return 'logout';
    case '编辑资料': return 'edit';
    case '修改密码': return 'security';
    case '启用': return 'active';
    case '停用': return 'inactive';
    default: return 'default';
  }
};

const isChangeObject = (val: any): val is LogDetailItem => {
  return val && typeof val === 'object' && 'old' in val && 'new' in val;
};

// 修改密码相关
const passwordModalVisible = ref(false);

const showPasswordModal = () => {
  passwordModalVisible.value = true;
};
</script>

<style lang="scss" scoped>
.user-profile-page {
  .avatar-container {
    position: relative;
    cursor: pointer;
    border-radius: 50%;
    overflow: hidden;
    
    &:hover .avatar-overlay {
      opacity: 1;
    }
    
    .avatar-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      justify-content: center;
      align-items: center;
      color: #fff;
      font-size: 24px;
      opacity: 0;
      transition: opacity 0.3s;
    }
  }

  .glass-card {
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(16px);
    border: 1px solid rgba(251, 113, 133, 0.1);
    border-radius: 20px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.03);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    
    &:hover {
      box-shadow: 0 15px 50px rgba(251, 113, 133, 0.08);
      transform: translateY(-4px);
    }
  }

  .profile-card {
    text-align: center;
    
    .profile-header {
      padding: 20px 0;
      
      .avatar-wrapper {
        position: relative;
        display: inline-block;
        margin-bottom: 16px;
        
        .role-badge {
          position: absolute;
          bottom: 0;
          right: -10px;
          background: #fb7185;
          color: #fff;
          font-size: 11px;
          padding: 2px 10px;
          border-radius: 12px;
          border: 2px solid #fff;
          font-weight: 800;
          box-shadow: 0 4px 10px rgba(251, 113, 133, 0.3);
        }
      }
      
      .profile-name-section {
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;
        height: 32px;
      }

      .profile-name {
        font-size: 20px;
        font-weight: 700;
        color: #1e293b;
        margin: 0;
      }
      
      .profile-email-section {
        color: #64748b;
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
      }
      
      .edit-icon {
        color: #94a3b8;
        cursor: pointer;
        font-size: 14px;
        transition: color 0.3s;
        
        &:hover {
          color: #6366f1;
        }
      }
    }
    
    .profile-info-list {
      padding: 0 16px;
      
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f1f5f9;
        font-size: 14px;
        height: 46px;
        
        &:last-child {
          border-bottom: none;
        }
        
        .label {
          color: #64748b;
          display: flex;
          align-items: center;
          gap: 8px;
        }
        
        .value-wrapper {
          display: flex;
          align-items: center;
          gap: 8px;
        }
        
        .value {
          color: #1e293b;
          font-weight: 500;
        }
        
        .edit-icon-small {
          color: #cbd5e1;
          cursor: pointer;
          font-size: 12px;
          opacity: 0;
          transition: all 0.3s;
        }
        
        &:hover .edit-icon-small {
          opacity: 1;
        }
        
        .edit-icon-small:hover {
          color: #6366f1;
        }
        
        .edit-icon-placeholder {
          width: 12px;
          height: 12px;
          display: inline-block;
        }
      }
    }
  }
  
  .security-card {
    height: 100%;
  }

  .premium-input {
    border-radius: 8px;
    height: 36px;
  }
}

.log-card-container {
  min-height: 460px;
}

.log-list-container {
  padding: 12px 0;
  max-height: 445px;
  overflow-y: auto;
  overflow-x: hidden;
  width: 100%;
  
  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 3px;
  }
  &::-webkit-scrollbar-track {
    background: transparent;
  }
}

.premium-timeline {
  padding-left: 12px;
  
  :deep(.ant-timeline-item-tail) { border-left: 2px dashed #e2e8f0; }
  
  .log-node {
    padding-bottom: 32px;
    &:last-child { padding-bottom: 0; }
  }

  .node-dot {
    width: 12px; height: 12px; border-radius: 50%; border: 3px solid #fff;
    box-shadow: 0 0 0 2px #e2e8f0;
    &.login { background: #3b82f6; box-shadow: 0 0 0 2px #dbeafe; }
    &.logout { background: #64748b; box-shadow: 0 0 0 2px #e2e8f0; }
    &.edit { background: #6366f1; box-shadow: 0 0 0 2px #e0e7ff; }
    &.security { background: #f59e0b; box-shadow: 0 0 0 2px #fef3c7; }
    &.active { background: #10b981; box-shadow: 0 0 0 2px #d1fae5; }
    &.inactive { background: #ef4444; box-shadow: 0 0 0 2px #fee2e2; }
  }
}

.log-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 16px;
  margin-left: 8px;
  border: 1px solid #edf2f7;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.01);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover { 
    transform: translateX(4px);
    border-color: #e2e8f0; 
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.08); 
  }

  .log-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;
    .left { display: flex; align-items: center; gap: 8px; }
    .right { 
      display: flex; 
      align-items: center; 
      gap: 12px;
      
      .ip-info {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 11px;
        color: #64748b;
        background: #f1f5f9;
        padding: 2px 8px;
        border-radius: 4px;
        
        .ip { font-family: 'JetBrains Mono', monospace; font-weight: 500; }
        .location { color: #94a3b8; padding-left: 6px; border-left: 1px solid #cbd5e1; }
      }

      .time { font-size: 11px; color: #94a3b8; font-weight: 500; font-family: 'Inter', sans-serif; } 
    }

    .type-tag {
      border: none; font-weight: 700; border-radius: 4px; font-size: 10px; padding: 0 6px; height: 18px; line-height: 18px;
      &.login { background: #dbeafe; color: #3b82f6; }
      &.logout { background: #f1f5f9; color: #64748b; }
      &.edit { background: #e0e7ff; color: #6366f1; }
      &.security { background: #fef3c7; color: #f59e0b; }
      &.active { background: #d1fae5; color: #10b981; }
      &.inactive { background: #fee2e2; color: #ef4444; }
    }

    .operator { font-size: 12px; color: #475569; font-weight: 600; display: flex; align-items: center; gap: 4px; }
  }

  .log-body {
    .content { font-size: 13px; color: #1e293b; font-weight: 600; margin: 0 0 8px 0; line-height: 1.4; }
    
    .details-box {
      background: #f8fafc; border-radius: 6px; padding: 8px 12px; border: 1px dotted #e2e8f0;
      display: flex; flex-direction: column; gap: 4px;
      
      .detail-item {
        display: flex; align-items: center; gap: 8px; font-size: 11.5px;
        
        .label { 
          color: #64748b; font-weight: 600; min-width: 40px; 
          &::after { content: ':'; margin-left: 1px; }
        }
        
        .value-container {
          display: flex; align-items: center; gap: 6px; flex: 1;
          
          .value { color: #334155; font-weight: 500; }
          .old-val { color: #94a3b8; text-decoration: line-through; opacity: 0.7; }
          .change-arrow { color: #cbd5e1; font-weight: 700; font-size: 10px; }
          .new-val { color: #4f46e5; font-weight: 700; background: #eff6ff; padding: 0px 4px; border-radius: 3px; font-size: 11px; }
        }
      }
    }
  }
}
</style>