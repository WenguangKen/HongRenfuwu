<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="720px"
    :footer="null"
    centered
    class="user-log-modal"
    destroy-on-close
  >
    <div class="ul-modal-header">
      <div class="ul-header-left">
        <div class="ul-header-icon">L</div>
        <div class="ul-header-text">
          <div class="ul-header-title">用户操作日志</div>
          <div class="ul-header-subtitle">
            用户：{{ user?.username || '-' }}
          </div>
        </div>
      </div>
    </div>
    <div class="ul-modal-body">
      <div class="ul-log-filter">
        <a-form :model="logFilterForm" layout="inline" class="compact-form">
          <a-form-item label="类型" style="margin-right: 12px;">
            <a-select
              v-model:value="logFilterForm.type"
              allow-clear
              placeholder="全部"
              style="width: 100px"
              size="small"
            >
              <a-select-option v-for="t in allLogTypes" :key="t" :value="t">
                {{ t }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="内容" style="margin-right: 12px;">
            <a-input
              v-model:value="logFilterForm.keyword"
              placeholder="关键字"
              allow-clear
              style="width: 130px"
              size="small"
            />
          </a-form-item>
          <a-form-item label="时间" style="margin-right: 0;">
            <a-range-picker
              v-model:value="logFilterForm.timeRange"
              style="width: 210px"
              format="YYYY-MM-DD"
              size="small"
            />
          </a-form-item>
        </a-form>
      </div>
      <div class="ul-log-list-container">
        <template v-if="logs.length">
          <a-timeline class="premium-timeline">
            <a-timeline-item
              v-for="log in logs"
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
                    <span class="ip" v-if="log.ip">{{ log.ip }} </span>
                    <span class="location" v-if="log.location">({{ log.location }})</span>
                    <span class="time">{{ log.time }}</span>
                  </div>
                </div>
                
                <div class="log-body">
                  <p class="content">{{ log.desc }}</p>
                  
                  <div v-if="hasVisibleDetails(log.details)" class="details-box">
                    <div v-for="(value, key) in filterDetails(log.details)" :key="key" class="detail-item">
                      <span class="label">{{ formatDetailKey(key as string) }}</span>
                      <div class="value-container">
                        <template v-if="isChangeObject(value)">
                          <span class="old-val">{{ value.old || '-' }}</span>
                          <span class="change-arrow">→</span>
                          <span class="new-val">{{ value.new || '-' }}</span>
                        </template>
                        <template v-else>
                          <span class="value">{{ formatDetailValue(value) }}</span>
                        </template>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </template>
        <div v-else class="empty-state-wrapper">
          <a-empty :image="simpleImage">
            <template #description>
              <div class="empty-hint">
                <span class="main-hint">未发现相关记录</span>
                <span class="sub-hint">当前筛选条件或时间范围内暂无日志</span>
              </div>
            </template>
          </a-empty>
        </div>
      </div>
    </div>
    <div class="ul-modal-footer">
      <div class="ul-footer-left">
        <span>共 {{ totalLogs }} 条</span>
      </div>
      <div class="ul-footer-right">
        <a-pagination
          size="small"
          :current="logPagination.current"
          :page-size="logPagination.pageSize"
          :total="totalLogs"
          :show-size-changer="true"
          @change="handleLogPageChange"
          @showSizeChange="handleLogPageChange"
        />
        <a-button type="primary" style="margin-left: 12px" @click="visible = false">
          关闭
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { UserOutlined } from '@ant-design/icons-vue';
import { Empty } from 'ant-design-vue';
import dayjs from 'dayjs';
import { getUserLogs } from '@/services/userService';

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

const props = defineProps<{
  open: boolean;
  user: any;
}>();

const emit = defineEmits(['update:open']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const logFilterForm = reactive<{
  type?: string;
  keyword: string;
  timeRange: [string, string] | undefined;
}>({
  type: undefined,
  keyword: '',
  timeRange: undefined,
});

const logPagination = reactive({
  current: 1,
  pageSize: 10,
});

const logs = ref<any[]>([]);
const totalLogs = ref(0);
const loading = ref(false);

const allLogTypes = [
  '登录', '登出', 
  '创建用户', '更新用户', '删除用户', '更新用户状态', 
  '修改密码', '编辑资料', 
  '创建角色', '更新角色', '删除角色', '更新角色权限', 
  '启用', '停用'
];

const fetchLogs = async () => {
  if (!props.user?.id) return;
  loading.value = true;
  try {
    const params: any = {
      page: logPagination.current - 1,
      size: logPagination.pageSize,
    };
    
    if (logFilterForm.type) params.actionType = logFilterForm.type;
    if (logFilterForm.keyword) params.keyword = logFilterForm.keyword;
    if (logFilterForm.timeRange && logFilterForm.timeRange[0]) {
       params.startTime = dayjs(logFilterForm.timeRange[0]).format('YYYY-MM-DD') + ' 00:00:00';
       params.endTime = dayjs(logFilterForm.timeRange[1]).format('YYYY-MM-DD') + ' 23:59:59';
    }

    const res = await getUserLogs(props.user.id, params);
    logs.value = res.content.map(l => ({
      id: l.id,
      type: l.actionType,
      time: dayjs(l.createdAt).format('YYYY-MM-DD HH:mm:ss'),
      desc: l.actionDesc,
      operator: 'System', 
      ip: formatIp(l.ipAddress),
      location: l.location || '未知',
      details: l.details ? tryParseDetails(l.details) : {}
    }));
    totalLogs.value = res.totalElements;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const tryParseDetails = (details: string) => {
    try { return JSON.parse(details); } catch { return {}; }
};

const formatIp = (ip?: string) => {
    if (!ip) return '-';
    if (ip === '0:0:0:0:0:0:0:1' || ip === '::1') return '127.0.0.1';
    return ip;
};

const hasVisibleDetails = (details: any) => {
  if (!details) return false;
  const filtered = filterDetails(details);
  return Object.keys(filtered).length > 0;
};

const filterDetails = (details: any) => {
  if (!details) return {};
  const result: any = {};
  
  // 深度平铺函数
  const flatten = (obj: any) => {
    if (!obj || typeof obj !== 'object') return;
    Object.keys(obj).forEach(k => {
      // 排除敏感字段和技术字段
      if (['password', 'oldPassword', 'newPassword', 'confirmPassword', 'token', 'refreshToken'].includes(k)) return;
      if (['id', 'userId', 'roleId', 'key', 'arg0', 'arg1'].includes(k)) return;
      
      const val = obj[k];
      if (val && typeof val === 'object' && !Array.isArray(val) && !isChangeObject(val)) {
        // 如果是嵌套对象（且非变更对象），继续平铺
        flatten(val);
      } else {
        result[k] = val;
      }
    });
  };

  // 如果有 params，从 params 开始平铺
  if (details.params && typeof details.params === 'object') {
    flatten(details.params);
  }

  // 排除已处理的技术字段
  Object.keys(details).forEach(k => {
    if (!['path', 'method', 'params', 'error'].includes(k) && !result[k]) {
      result[k] = details[k];
    }
    if (k === 'error') result['失败原因'] = details[k];
  });
  
  return result;
};

const formatDetailKey = (key: string) => {
  const map: any = {
    username: '用户名',
    email: '邮箱',
    phone: '电话',
    roleIds: '分配角色',
    status: '账号状态',
    active: '是否启用',
    avatarUrl: '头像地址',
    description: '描述备注',
    name: '名称',
    permissionIds: '权限列表'
  };
  return map[key] || key;
};

const formatDetailValue = (val: any) => {
  if (typeof val === 'object') return JSON.stringify(val);
  return val;
};

watch(() => props.open, (val) => {
  if (val) {
    logFilterForm.type = undefined;
    logFilterForm.keyword = '';
    logFilterForm.timeRange = undefined;
    logPagination.current = 1;
    fetchLogs();
  }
}, { immediate: true });

// Watch filter changes to refresh
watch(() => [logFilterForm.type, logFilterForm.timeRange], () => {
    logPagination.current = 1;
    fetchLogs();
});

// Debounce keyword search if needed, or just search on enter/blur. 
// For simplicity, let's watch keyword with a delay or just leave it for now.

const handleLogPageChange = (page: number, pageSize?: number) => {
  logPagination.current = page;
  if (pageSize) logPagination.pageSize = pageSize;
  fetchLogs();
};

const getLogTypeClass = (type: string) => {
  const map: Record<string, string> = {
    '登录': 'login',
    '退出': 'logout',
    '编辑资料': 'edit',
    '修改密码': 'security',
    '启用': 'active',
    '停用': 'inactive'
  };
  return map[type] || 'default';
};

const isChangeObject = (val: any) => {
  return val && typeof val === 'object' && ('old' in val || 'new' in val);
};
</script>

<style lang="scss" scoped>
/* 用户日志弹窗 */
:deep(.user-log-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
}

.ul-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(251, 113, 133, 0.1) 0%, rgba(251, 191, 36, 0.05) 100%);
  border-bottom: 1px solid rgba(251, 113, 133, 0.1);
}

.ul-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ul-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: #fb7185;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 900;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(251, 113, 133, 0.3);
}

.ul-header-title {
  font-size: 16px;
  font-weight: 600;
}

.ul-header-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.ul-modal-body {
  padding: 16px 24px;
  background: radial-gradient(circle at top right, rgba(251, 113, 133, 0.03), transparent 300px);
}

.ul-log-filter {
  margin-bottom: 16px;
}

.ul-log-list-container {
  height: 480px;
  overflow-y: auto;
  padding: 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(251, 113, 133, 0.1);
}

.premium-timeline {
  :deep(.ant-timeline-item-tail) { border-left: 2px dashed #e2e8f0; }
  
  .log-node {
    padding-bottom: 32px;
    &:last-child { padding-bottom: 0; }
  }

  .node-dot {
    width: 12px; height: 12px; border-radius: 50%; border: 3px solid #fff;
    box-shadow: 0 0 0 2px rgba(251, 113, 133, 0.1);
    &.login { background: #fb7185; box-shadow: 0 0 0 2px rgba(251, 113, 133, 0.2); }
    &.logout { background: #94a3b8; box-shadow: 0 0 0 2px #e2e8f0; }
    &.edit { background: #6366f1; box-shadow: 0 0 0 2px #e0e7ff; }
    &.security { background: #fbbf24; box-shadow: 0 0 0 2px #fef3c7; }
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
      display: flex; align-items: center; gap: 8px;
      .ip { font-size: 11px; color: #64748b; font-family: monospace; background: #f1f5f9; padding: 1px 4px; border-radius: 3px; }
      .location { font-size: 11px; color: #94a3b8; }
      .time { font-size: 11px; color: #94a3b8; font-weight: 500; font-family: 'Inter', sans-serif; } 
    }

    .type-tag {
      border: none; font-weight: 700; border-radius: 4px; font-size: 10px; padding: 0 6px; height: 18px; line-height: 18px;
      &.login { background: rgba(251, 113, 133, 0.1); color: #fb7185; }
      &.logout { background: #f1f5f9; color: #64748b; }
      &.edit { background: #e0e7ff; color: #6366f1; }
      &.security { background: #fef3c7; color: #fbbf24; }
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
          .new-val { color: #fb7185; font-weight: 700; background: rgba(251, 113, 133, 0.05); padding: 0px 4px; border-radius: 3px; font-size: 11px; }
        }
      }
    }
  }
}

.empty-state-wrapper {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 40px;

  .empty-hint {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    
    .main-hint {
      color: #64748b;
      font-size: 14px;
      font-weight: 600;
    }
    
    .sub-hint {
      color: #94a3b8;
      font-size: 12px;
      font-weight: 400;
    }
  }
}

.ul-modal-footer {
  padding: 10px 20px 14px;
  border-top: 1px solid #e5e7eb;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .ul-footer-left {
    color: #64748b;
    font-size: 13px;
    
    .info-divider { margin: 0 8px; color: #cbd5e1; }
    .count-value { font-weight: 600; color: #1e293b; &.highlight { color: #1890ff; } }
  }

  .ul-footer-right {
    display: flex;
    align-items: center;
  }
}
</style>
