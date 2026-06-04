<template>
  <div class="sender-config-page">
    <!-- SendCloud API 配置 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '24px' }">
      <template #title>
        <div class="card-title-row">
          <div class="card-title-left">
            <div class="card-icon" style="background: linear-gradient(135deg, #3b82f6, #1d4ed8)">
              <ApiOutlined />
            </div>
            <div>
              <div class="card-title">SendCloud 服务配置</div>
              <div class="card-subtitle">配置闪达 SendCloud 邮件发送服务的 API 凭证</div>
            </div>
          </div>
          <div class="connection-status">
            <template v-if="configLoading">
              <a-spin size="small" />
            </template>
            <template v-else-if="sendcloudConfig.isConnected">
              <span class="status-dot connected"></span>
              <span class="status-text connected">已连接</span>
              <span class="status-time" v-if="sendcloudConfig.lastVerifiedAt">
                上次验证: {{ sendcloudConfig.lastVerifiedAt }}
              </span>
            </template>
            <template v-else>
              <span class="status-dot disconnected"></span>
              <span class="status-text disconnected">未连接</span>
            </template>
          </div>
        </div>
      </template>

      <a-form :model="configForm" layout="vertical" class="config-form">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="API User">
              <a-input
                v-model:value="configForm.apiUser"
                placeholder="请输入 SendCloud API User"
                class="premium-input"
              >
                <template #prefix><UserOutlined style="color: #94a3b8" /></template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="API Key">
              <a-input-password
                v-model:value="configForm.apiKey"
                placeholder="请输入 SendCloud API Key"
                class="premium-input"
              >
                <template #prefix><KeyOutlined style="color: #94a3b8" /></template>
              </a-input-password>
            </a-form-item>
          </a-col>
        </a-row>
        <div class="form-actions">
          <a-button @click="handleTestConnection" :loading="testLoading" class="test-btn">
            <template #icon><ApiOutlined /></template>
            测试连接
          </a-button>
          <a-button type="primary" @click="handleSaveConfig" :loading="saveConfigLoading" class="primary-gradient">
            <template #icon><SaveOutlined /></template>
            保存配置
          </a-button>
        </div>
      </a-form>
    </a-card>

    <!-- 发件人管理 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '24px' }">
      <template #title>
        <div class="card-title-row">
          <div class="card-title-left">
            <div class="card-icon" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9)">
              <MailOutlined />
            </div>
            <div>
              <div class="card-title">发件人管理</div>
              <div class="card-subtitle">管理邮件发送地址，设置默认发件人</div>
            </div>
          </div>
          <a-button type="primary" @click="openSenderModal()" class="primary-gradient">
            <template #icon><PlusOutlined /></template>
            添加发件人
          </a-button>
        </div>
      </template>

      <div class="sender-list" v-if="senders.length > 0">
        <div
          v-for="sender in senders"
          :key="sender.id"
          class="sender-card"
          :class="{ 'is-default': sender.isDefault }"
        >
          <div class="sender-left">
            <div class="sender-avatar" :style="{ background: getSenderColor(sender.email) }">
              <MailOutlined />
            </div>
            <div class="sender-info">
              <div class="sender-email">
                {{ sender.email }}
                <a-tag v-if="sender.isDefault" color="#8b5cf6" class="default-tag">默认</a-tag>
              </div>
              <div class="sender-name">{{ sender.displayName }}</div>
            </div>
          </div>
          <div class="sender-middle">
            <div class="sender-stat">
              <span class="stat-label">验证状态</span>
              <span v-if="sender.isVerified" class="stat-value verified">
                <CheckCircleOutlined /> 已验证
              </span>
              <span v-else class="stat-value pending">
                <ClockCircleOutlined /> 验证中
              </span>
            </div>
            <div class="sender-stat">
              <span class="stat-label">今日已发</span>
              <span class="stat-value">
                {{ sender.dailySent }} / {{ sender.dailyLimit }}
              </span>
            </div>
          </div>
          <div class="sender-actions">
            <a-button type="text" size="small" @click="openSenderModal(sender)" class="action-btn edit">
              编辑
            </a-button>
            <a-button
              v-if="!sender.isDefault"
              type="text"
              size="small"
              @click="handleSetDefault(sender.id)"
              class="action-btn"
            >
              设为默认
            </a-button>
            <a-button
              type="text"
              size="small"
              @click="handleDeleteSender(sender.id)"
              class="action-btn delete"
              :disabled="sender.isDefault"
            >
              删除
            </a-button>
          </div>
        </div>
      </div>
      <a-empty v-else description="暂无发件人，请先添加" class="empty-state">
        <template #image>
          <div class="empty-icon-wrapper">
            <MailOutlined />
          </div>
        </template>
      </a-empty>
    </a-card>

    <!-- 发送概况 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '24px' }">
      <template #title>
        <div class="card-title-row">
          <div class="card-title-left">
            <div class="card-icon" style="background: linear-gradient(135deg, #10b981, #059669)">
              <BarChartOutlined />
            </div>
            <div>
              <div class="card-title">发送概况</div>
              <div class="card-subtitle">邮件发送统计数据总览</div>
            </div>
          </div>
        </div>
      </template>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.todaySent }}</div>
          <div class="stat-card-label">今日发送</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #3b82f6, #60a5fa)"></div>
        </div>
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.weekSent }}</div>
          <div class="stat-card-label">本周发送</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #8b5cf6, #a78bfa)"></div>
        </div>
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.monthSent }}</div>
          <div class="stat-card-label">本月发送</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #10b981, #34d399)"></div>
        </div>
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.successRate }}%</div>
          <div class="stat-card-label">成功率</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #f59e0b, #fbbf24)"></div>
        </div>
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.openRate }}%</div>
          <div class="stat-card-label">打开率</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #06b6d4, #22d3ee)"></div>
        </div>
        <div class="stat-card">
          <div class="stat-card-value">{{ stats.replyRate }}%</div>
          <div class="stat-card-label">回复率</div>
          <div class="stat-card-bar" style="background: linear-gradient(90deg, #ec4899, #f472b6)"></div>
        </div>
      </div>
    </a-card>

    <!-- Webhook 配置 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '24px' }">
      <template #title>
        <div class="card-title-row">
          <div class="card-title-left">
            <div class="card-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706)">
              <LinkOutlined />
            </div>
            <div>
              <div class="card-title">Webhook 配置</div>
              <div class="card-subtitle">配置 SendCloud 事件回调，自动追踪邮件状态</div>
            </div>
          </div>
        </div>
      </template>

      <a-form layout="vertical" class="config-form">
        <a-form-item label="回调 URL">
          <a-input
            v-model:value="configForm.webhookUrl"
            placeholder="https://api.yourdomain.com/webhook/sendcloud"
            class="premium-input"
          >
            <template #prefix><LinkOutlined style="color: #94a3b8" /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="订阅事件">
          <a-checkbox-group v-model:value="configForm.webhookEvents" class="webhook-events">
            <a-row :gutter="[16, 8]">
              <a-col :span="8" v-for="evt in webhookEventOptions" :key="evt.value">
                <a-checkbox :value="evt.value">{{ evt.label }}</a-checkbox>
              </a-col>
            </a-row>
          </a-checkbox-group>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 发件人编辑弹窗 -->
    <a-modal
      v-model:open="senderModalOpen"
      :title="null"
      :footer="null"
      :closable="true"
      width="500px"
      centered
      class="premium-refined-modal"
    >
      <div class="glass-header-compact">
        <div class="header-main">
          <div class="logo-box">
            <span class="logo-text">✉️</span>
          </div>
          <div class="title-meta">
            <h3 class="main-title">{{ editingSender ? '编辑发件人' : '添加发件人' }}</h3>
            <div class="simple-subtitle">配置邮件发送地址信息</div>
          </div>
        </div>
      </div>

      <div class="modal-body-container">
        <div style="padding: 24px">
          <a-form :model="senderForm" layout="vertical">
            <a-form-item label="发件人邮箱" :rules="[{ required: true, message: '请输入邮箱' }]">
              <a-input
                v-model:value="senderForm.email"
                placeholder="marketing@yourbrand.com"
                class="premium-input"
                :disabled="!!editingSender"
              >
                <template #prefix><MailOutlined style="color: #94a3b8" /></template>
              </a-input>
            </a-form-item>
            <a-form-item label="发件人名称" :rules="[{ required: true, message: '请输入名称' }]">
              <a-input
                v-model:value="senderForm.displayName"
                placeholder="YourBrand Marketing"
                class="premium-input"
              >
                <template #prefix><UserOutlined style="color: #94a3b8" /></template>
              </a-input>
            </a-form-item>
          </a-form>
        </div>
      </div>

      <div class="modal-fixed-footer">
        <a-button class="btn-cancel" @click="senderModalOpen = false">取消</a-button>
        <a-button type="primary" class="btn-submit" @click="handleSaveSender" :loading="saveSenderLoading">
          {{ editingSender ? '保存修改' : '添加发件人' }}
        </a-button>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import {
  ApiOutlined, MailOutlined, PlusOutlined, SaveOutlined,
  UserOutlined, KeyOutlined, CheckCircleOutlined, ClockCircleOutlined,
  BarChartOutlined, LinkOutlined,
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { OutreachSender, SendCloudConfig, OutreachStats } from '@/types/outreach';
import {
  getSenderList, createSender, updateSender, setDefaultSender, deleteSender,
  getSendCloudConfig, updateSendCloudConfig, testSendCloudConnection,
  getOutreachStats,
} from '@/services/outreachService';

// --- SendCloud Config ---
const configLoading = ref(false);
const testLoading = ref(false);
const saveConfigLoading = ref(false);

const sendcloudConfig = reactive<SendCloudConfig>({
  apiUser: '', apiKey: '', webhookUrl: '', webhookEvents: [], isConnected: false, lastVerifiedAt: '',
});

const configForm = reactive({
  apiUser: '',
  apiKey: '',
  webhookUrl: '',
  webhookEvents: ['deliver', 'open', 'click', 'bounce'] as string[],
});

const webhookEventOptions = [
  { value: 'deliver', label: '送达 (deliver)' },
  { value: 'open', label: '打开 (open)' },
  { value: 'click', label: '点击 (click)' },
  { value: 'bounce', label: '退信 (bounce)' },
  { value: 'spam', label: '举报垃圾邮件 (spam)' },
  { value: 'unsubscribe', label: '取消订阅 (unsubscribe)' },
];

const loadConfig = async () => {
  configLoading.value = true;
  try {
    const data = await getSendCloudConfig();
    Object.assign(sendcloudConfig, data);
    configForm.apiUser = data.apiUser || '';
    configForm.apiKey = data.apiKey || '';
    configForm.webhookUrl = data.webhookUrl || '';
    configForm.webhookEvents = data.webhookEvents || ['deliver', 'open', 'click', 'bounce'];
  } catch {
    // API not ready yet, use defaults
  } finally {
    configLoading.value = false;
  }
};

const handleTestConnection = async () => {
  if (!configForm.apiUser || !configForm.apiKey) {
    message.warning('请先填写 API User 和 API Key');
    return;
  }
  testLoading.value = true;
  try {
    const result = await testSendCloudConnection();
    if (result.success) {
      message.success('连接测试成功！');
      sendcloudConfig.isConnected = true;
    } else {
      message.error(result.message || '连接失败');
    }
  } catch {
    message.error('连接测试失败，请检查 API 凭证');
  } finally {
    testLoading.value = false;
  }
};

const handleSaveConfig = async () => {
  if (!configForm.apiUser || !configForm.apiKey) {
    message.warning('请先填写 API User 和 API Key');
    return;
  }
  saveConfigLoading.value = true;
  try {
    await updateSendCloudConfig({
      apiUser: configForm.apiUser,
      apiKey: configForm.apiKey,
      webhookUrl: configForm.webhookUrl,
      webhookEvents: configForm.webhookEvents,
    });
    message.success('配置保存成功');
    await loadConfig();
  } catch {
    message.error('保存失败');
  } finally {
    saveConfigLoading.value = false;
  }
};

// --- Senders ---
const senders = ref<OutreachSender[]>([]);
const senderModalOpen = ref(false);
const editingSender = ref<OutreachSender | null>(null);
const saveSenderLoading = ref(false);
const senderForm = reactive({ email: '', displayName: '' });

const loadSenders = async () => {
  try {
    senders.value = await getSenderList();
  } catch {
    // API not ready, use demo data
    senders.value = [];
  }
};

const openSenderModal = (sender?: OutreachSender) => {
  if (sender) {
    editingSender.value = sender;
    senderForm.email = sender.email;
    senderForm.displayName = sender.displayName;
  } else {
    editingSender.value = null;
    senderForm.email = '';
    senderForm.displayName = '';
  }
  senderModalOpen.value = true;
};

const handleSaveSender = async () => {
  if (!senderForm.email || !senderForm.displayName) {
    message.warning('请填写完整信息');
    return;
  }
  saveSenderLoading.value = true;
  try {
    if (editingSender.value) {
      await updateSender(editingSender.value.id, {
        email: senderForm.email,
        displayName: senderForm.displayName,
      });
      message.success('修改成功');
    } else {
      await createSender({
        email: senderForm.email,
        displayName: senderForm.displayName,
      });
      message.success('添加成功');
    }
    senderModalOpen.value = false;
    await loadSenders();
  } catch {
    message.error('操作失败');
  } finally {
    saveSenderLoading.value = false;
  }
};

const handleSetDefault = async (id: number) => {
  try {
    await setDefaultSender(id);
    message.success('已设为默认发件人');
    await loadSenders();
  } catch {
    message.error('操作失败');
  }
};

const handleDeleteSender = async (id: number) => {
  try {
    await deleteSender(id);
    message.success('删除成功');
    await loadSenders();
  } catch {
    message.error('删除失败');
  }
};

const getSenderColor = (email: string) => {
  const colors = [
    'linear-gradient(135deg, #6366f1, #4f46e5)',
    'linear-gradient(135deg, #8b5cf6, #6d28d9)',
    'linear-gradient(135deg, #3b82f6, #1d4ed8)',
    'linear-gradient(135deg, #10b981, #059669)',
    'linear-gradient(135deg, #f59e0b, #d97706)',
  ];
  let hash = 0;
  for (let i = 0; i < email.length; i++) hash = email.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

// --- Stats ---
const stats = reactive<OutreachStats>({
  todaySent: 0, weekSent: 0, monthSent: 0,
  successRate: 0, openRate: 0, replyRate: 0,
});

const loadStats = async () => {
  try {
    const data = await getOutreachStats();
    Object.assign(stats, data);
  } catch {
    // API not ready
  }
};

onMounted(() => {
  loadConfig();
  loadSenders();
  loadStats();
});
</script>

<style lang="scss" scoped>
.sender-config-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;

  .glass-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
    transition: all 0.3s ease;
    &:hover { box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05); }
  }

  .config-card {
    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 0 24px;
      min-height: 64px;
      .ant-card-head-title { padding: 16px 0; }
    }
  }
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-title-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
}

.card-subtitle {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

/* Connection status */
.connection-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  background: #f8fafc;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  &.connected {
    background: #10b981;
    box-shadow: 0 0 6px rgba(16, 185, 129, 0.4);
    animation: pulse-dot 2s infinite;
  }
  &.disconnected { background: #ef4444; }
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-text {
  font-size: 13px;
  font-weight: 600;
  &.connected { color: #10b981; }
  &.disconnected { color: #ef4444; }
}

.status-time {
  font-size: 11px;
  color: #94a3b8;
  margin-left: 4px;
}

/* Form */
.config-form {
  :deep(.ant-form-item-label > label) {
    font-size: 12px;
    font-weight: 600;
    color: #64748b;
  }
  .premium-input {
    border-radius: 8px;
    height: 40px;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 8px;
}

.test-btn {
  height: 36px;
  border-radius: 8px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
}

.primary-gradient {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
  height: 36px;
  border-radius: 8px;
  font-weight: 600;
  &:hover { box-shadow: 0 6px 16px rgba(99, 102, 241, 0.3); transform: translateY(-1px); }
}

/* Sender list */
.sender-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sender-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  transition: all 0.3s ease;

  &:hover {
    border-color: #e2e8f0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
    transform: translateY(-1px);
  }

  &.is-default {
    border-color: #c4b5fd;
    background: linear-gradient(135deg, #faf5ff 0%, #ffffff 100%);
  }
}

.sender-left {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
}

.sender-avatar {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.sender-info {
  .sender-email {
    font-size: 14px;
    font-weight: 700;
    color: #1e293b;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .sender-name {
    font-size: 12px;
    color: #94a3b8;
    margin-top: 2px;
  }
}

.default-tag {
  font-size: 10px;
  font-weight: 700;
  border-radius: 4px;
  line-height: 1;
  padding: 2px 6px;
}

.sender-middle {
  display: flex;
  gap: 32px;
}

.sender-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;

  .stat-label {
    font-size: 11px;
    color: #94a3b8;
    font-weight: 600;
  }
  .stat-value {
    font-size: 13px;
    font-weight: 700;
    color: #334155;
    display: flex;
    align-items: center;
    gap: 4px;

    &.verified { color: #10b981; }
    &.pending { color: #f59e0b; }
  }
}

.sender-actions {
  display: flex;
  gap: 4px;
}

.action-btn {
  font-weight: 600;
  font-size: 13px;
  &.edit { color: #3b82f6; &:hover { background: #eff6ff; } }
  &.delete { color: #ef4444; &:hover { background: #fef2f2; } }
}

/* Stats grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.stat-card {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  }

  .stat-card-value {
    font-size: 24px;
    font-weight: 800;
    color: #1e293b;
    line-height: 1.2;
  }

  .stat-card-label {
    font-size: 12px;
    color: #94a3b8;
    font-weight: 600;
    margin-top: 4px;
  }

  .stat-card-bar {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
  }
}

/* Webhook */
.webhook-events {
  :deep(.ant-checkbox-wrapper) {
    font-size: 13px;
    font-weight: 500;
  }
}

/* Empty */
.empty-state {
  padding: 48px 0;
}

.empty-icon-wrapper {
  width: 64px;
  height: 64px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 28px;
  color: #94a3b8;
}
</style>
