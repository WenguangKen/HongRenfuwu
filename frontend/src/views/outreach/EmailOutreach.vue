<template>
  <div class="email-outreach-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="红人名称">
              <a-input v-model:value.trim="filterForm.influencerName" placeholder="搜索红人名称..." allow-clear class="premium-input-search">
                <template #prefix><UserOutlined style="color: #94a3b8" /></template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="收件邮箱">
              <a-input v-model:value.trim="filterForm.recipientEmail" placeholder="搜索邮箱地址..." allow-clear class="premium-input-search">
                <template #prefix><MailOutlined style="color: #94a3b8" /></template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="发送状态">
              <a-select v-model:value="filterForm.status" placeholder="全部状态" allow-clear>
                <a-select-option v-for="s in statusOptions" :key="s.value" :value="s.value">
                  {{ s.label }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
            <a-form-item label="发送时间">
              <a-range-picker v-model:value="filterForm.timeRange" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
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
            <a-radio-group v-model:value="activeTab" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="all">全部</a-radio-button>
              <a-radio-button value="pending">待发送</a-radio-button>
              <a-radio-button value="delivered">已发送</a-radio-button>
              <a-radio-button value="replied">已回复</a-radio-button>
              <a-radio-button value="failed">发送失败</a-radio-button>
            </a-radio-group>
          </div>
          <div class="toolbar-btns">
            <a-button @click="handleBatchSend" :disabled="selectedRowKeys.length === 0" class="batch-btn">
              <template #icon><SendOutlined /></template>
              批量发送 ({{ selectedRowKeys.length }})
            </a-button>
            <a-button type="primary" @click="openComposeDrawer()" class="primary-gradient">
              <template #icon><EditOutlined /></template>
              新建邮件
            </a-button>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="emails"
        :row-key="(record: any) => record.id"
        :pagination="false"
        :loading="loading"
        :scroll="tableScroll"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        class="premium-table"
      >
        <template #bodyCell="{ column, record }">
          <!-- 红人信息 -->
          <template v-if="column.key === 'influencer'">
            <div class="influencer-cell">
              <a-avatar :size="32" class="inf-avatar" :style="{ background: getAvatarColor(record.influencerName || '') }">
                {{ (record.influencerName || 'U').charAt(0).toUpperCase() }}
              </a-avatar>
              <div class="inf-info">
                <div class="inf-name">{{ record.influencerName || '—' }}</div>
                <div class="inf-email">{{ record.recipientEmail }}</div>
              </div>
            </div>
          </template>

          <!-- 邮件主题 -->
          <template v-else-if="column.key === 'subject'">
            <div class="subject-cell">
              <span class="subject-text">{{ record.subject }}</span>
            </div>
          </template>

          <!-- 模板 -->
          <template v-else-if="column.key === 'template'">
            <a-tag v-if="record.templateName" color="#f0f0ff" style="color: #6366f1; border: 1px solid #e0e7ff;">
              {{ record.templateName }}
            </a-tag>
            <span v-else class="text-muted">—</span>
          </template>

          <!-- 状态 -->
          <template v-else-if="column.key === 'status'">
            <div class="status-cell">
              <span class="status-dot" :style="{ background: getStatusColor(record.status) }"></span>
              <span class="status-label">{{ getStatusLabel(record.status) }}</span>
            </div>
          </template>

          <!-- 发送时间 -->
          <template v-else-if="column.key === 'sentAt'">
            <div class="time-cell">
              <template v-if="record.sentAt">
                <div>{{ formatDate(record.sentAt) }}</div>
                <div class="time-sub">{{ formatTime(record.sentAt) }}</div>
              </template>
              <span v-else class="text-muted">—</span>
            </div>
          </template>

          <!-- 操作 -->
          <template v-else-if="column.key === 'action'">
            <a-space :size="4">
              <a-button type="text" size="small" @click="openDetailDrawer(record)" class="action-btn view">
                查看
              </a-button>
              <a-button
                v-if="record.status === 'failed' || record.status === 'bounced'"
                type="text" size="small"
                @click="handleResend(record)"
                class="action-btn resend"
              >
                重发
              </a-button>
              <a-button
                v-if="record.status === 'draft' || record.status === 'pending'"
                type="text" size="small"
                @click="handleSendOne(record)"
                class="action-btn send"
              >
                发送
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 分页 -->
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

    <!-- 新建邮件抽屉 -->
    <a-drawer
      v-model:open="composeDrawerOpen"
      title=""
      placement="right"
      width="640"
      :headerStyle="{ display: 'none' }"
      :bodyStyle="{ padding: 0, display: 'flex', flexDirection: 'column' }"
      class="compose-drawer"
    >
      <div class="drawer-header">
        <div class="drawer-header-left">
          <div class="drawer-icon" style="background: linear-gradient(135deg, #6366f1, #8b5cf6)">
            <MailOutlined />
          </div>
          <div>
            <div class="drawer-title">新建邮件</div>
            <div class="drawer-subtitle">选择红人和模板，快速发送邀约邮件</div>
          </div>
        </div>
        <a-button type="text" @click="composeDrawerOpen = false" class="drawer-close">
          <CloseOutlined />
        </a-button>
      </div>

      <div class="drawer-body">
        <a-form :model="composeForm" layout="vertical">
          <a-form-item label="发件人">
            <a-select v-model:value="composeForm.senderEmail" placeholder="选择发件人">
              <a-select-option v-for="s in availableSenders" :key="s.email" :value="s.email">
                {{ s.displayName }} &lt;{{ s.email }}&gt;
                <a-tag v-if="s.isDefault" color="#8b5cf6" style="margin-left: 8px; font-size: 10px;">默认</a-tag>
              </a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="收件红人">
            <a-select
              v-model:value="composeForm.influencerIds"
              mode="multiple"
              placeholder="搜索并选择红人..."
              :filter-option="false"
              @search="handleSearchInfluencer"
              show-search
              :options="influencerSearchResults"
            />
            <div class="selected-count" v-if="composeForm.influencerIds.length > 0">
              已选 {{ composeForm.influencerIds.length }} 位红人
            </div>
          </a-form-item>

          <a-form-item label="邮件模板">
            <a-select v-model:value="composeForm.templateId" placeholder="选择模板（可选）" allow-clear @change="handleTemplateChange">
              <a-select-option v-for="tpl in availableTemplates" :key="tpl.id" :value="tpl.id">
                {{ tpl.name }}
              </a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="邮件主题">
            <a-input v-model:value="composeForm.subject" placeholder="输入邮件主题..." class="premium-input" />
          </a-form-item>

          <a-form-item label="邮件正文">
            <a-textarea
              v-model:value="composeForm.bodyHtml"
              placeholder="输入邮件正文..."
              :auto-size="{ minRows: 8, maxRows: 16 }"
              class="premium-input"
            />
          </a-form-item>

          <a-form-item label="发送方式">
            <a-radio-group v-model:value="composeForm.sendMode">
              <a-radio value="now">立即发送</a-radio>
              <a-radio value="schedule">定时发送</a-radio>
            </a-radio-group>
            <a-date-picker
              v-if="composeForm.sendMode === 'schedule'"
              v-model:value="composeForm.scheduledAt"
              show-time
              placeholder="选择发送时间"
              style="margin-top: 8px; width: 100%"
            />
          </a-form-item>
        </a-form>
      </div>

      <div class="drawer-footer">
        <a-button @click="composeDrawerOpen = false" class="btn-cancel">取消</a-button>
        <a-button type="primary" @click="handleComposeSend" :loading="composeSending" class="btn-submit">
          <template #icon><SendOutlined /></template>
          确认发送
        </a-button>
      </div>
    </a-drawer>

    <!-- 邮件详情抽屉 -->
    <a-drawer
      v-model:open="detailDrawerOpen"
      title=""
      placement="right"
      width="560"
      :headerStyle="{ display: 'none' }"
      :bodyStyle="{ padding: 0, display: 'flex', flexDirection: 'column' }"
    >
      <div class="drawer-header">
        <div class="drawer-header-left">
          <div class="drawer-icon" style="background: linear-gradient(135deg, #10b981, #059669)">
            <EyeOutlined />
          </div>
          <div>
            <div class="drawer-title">邮件详情</div>
            <div class="drawer-subtitle">查看邮件发送状态和内容</div>
          </div>
        </div>
        <a-button type="text" @click="detailDrawerOpen = false" class="drawer-close">
          <CloseOutlined />
        </a-button>
      </div>

      <div class="drawer-body" v-if="detailEmail">
        <!-- 状态条 -->
        <div class="detail-status-bar" :style="{ background: getStatusBg(detailEmail.status) }">
          <span class="status-dot-lg" :style="{ background: getStatusColor(detailEmail.status) }"></span>
          <span class="status-text-lg">{{ getStatusLabel(detailEmail.status) }}</span>
        </div>

        <div class="detail-section">
          <div class="detail-label">收件人</div>
          <div class="detail-value">
            <a-avatar :size="24" :style="{ background: getAvatarColor(detailEmail.influencerName || ''), fontSize: '11px' }">
              {{ (detailEmail.influencerName || 'U').charAt(0).toUpperCase() }}
            </a-avatar>
            <span style="margin-left: 8px">{{ detailEmail.influencerName || '—' }}</span>
            <span class="text-muted" style="margin-left: 4px">&lt;{{ detailEmail.recipientEmail }}&gt;</span>
          </div>
        </div>

        <div class="detail-section">
          <div class="detail-label">发件人</div>
          <div class="detail-value">{{ detailEmail.senderEmail }}</div>
        </div>

        <div class="detail-section">
          <div class="detail-label">邮件主题</div>
          <div class="detail-value" style="font-weight: 700;">{{ detailEmail.subject }}</div>
        </div>

        <div class="detail-section">
          <div class="detail-label">邮件正文</div>
          <div class="detail-body-preview" v-html="detailEmail.bodyHtml"></div>
        </div>

        <!-- 时间线 -->
        <div class="detail-section">
          <div class="detail-label">事件记录</div>
          <div class="timeline-list">
            <div class="timeline-item" v-if="detailEmail.createdAt">
              <div class="tl-dot" style="background: #94a3b8"></div>
              <div class="tl-content">
                <div class="tl-title">创建邮件</div>
                <div class="tl-time">{{ detailEmail.createdAt }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailEmail.sentAt">
              <div class="tl-dot" style="background: #3b82f6"></div>
              <div class="tl-content">
                <div class="tl-title">发送成功</div>
                <div class="tl-time">{{ detailEmail.sentAt }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailEmail.openedAt">
              <div class="tl-dot" style="background: #8b5cf6"></div>
              <div class="tl-content">
                <div class="tl-title">收件人打开</div>
                <div class="tl-time">{{ detailEmail.openedAt }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailEmail.repliedAt">
              <div class="tl-dot" style="background: #10b981"></div>
              <div class="tl-content">
                <div class="tl-title">收件人回复</div>
                <div class="tl-time">{{ detailEmail.repliedAt }}</div>
              </div>
            </div>
            <div class="timeline-item" v-if="detailEmail.errorMessage">
              <div class="tl-dot" style="background: #ef4444"></div>
              <div class="tl-content">
                <div class="tl-title">发送失败</div>
                <div class="tl-error">{{ detailEmail.errorMessage }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import type { Dayjs } from 'dayjs';
import {
  UserOutlined, MailOutlined, SendOutlined, EditOutlined,
  CloseOutlined, EyeOutlined,
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import type { OutreachEmail, EmailStatus, OutreachTemplate, OutreachSender } from '@/types/outreach';
import { EMAIL_STATUS_MAP } from '@/types/outreach';
import {
  getEmailList, sendEmail, resendEmail,
  getAllActiveTemplates, getSenderList,
} from '@/services/outreachService';

// --- Filter ---
const filterForm = reactive({
  influencerName: '',
  recipientEmail: '',
  status: undefined as EmailStatus | undefined,
  timeRange: null as [Dayjs, Dayjs] | null,
});

const statusOptions = Object.entries(EMAIL_STATUS_MAP).map(([value, info]) => ({
  value, label: info.label,
}));

const activeTab = ref('all');

// --- Table ---
const emails = ref<OutreachEmail[]>([]);
const loading = ref(false);
const selectedRowKeys = ref<number[]>([]);

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
});

const columns: TableColumnsType = [
  { title: '红人信息', key: 'influencer', width: 240, fixed: 'left' },
  { title: '邮件主题', key: 'subject', width: 260 },
  { title: '模板', key: 'template', width: 140, align: 'center' },
  { title: '状态', key: 'status', width: 120, align: 'center' },
  { title: '发送时间', key: 'sentAt', width: 160, align: 'center' },
  { title: '操作', key: 'action', width: 160, fixed: 'right', align: 'center' },
];

const tableScroll = computed(() => ({
  y: 'calc(100vh - 340px)',
  x: 'max-content',
}));

const fetchEmails = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    if (filterForm.influencerName) params.influencerName = filterForm.influencerName;
    if (filterForm.recipientEmail) params.recipientEmail = filterForm.recipientEmail;
    if (activeTab.value !== 'all') params.status = activeTab.value;
    else if (filterForm.status) params.status = filterForm.status;
    if (filterForm.timeRange && filterForm.timeRange[0]) {
      params.sentTimeStart = filterForm.timeRange[0].format('YYYY-MM-DD');
      params.sentTimeEnd = filterForm.timeRange[1].format('YYYY-MM-DD');
    }
    const data = await getEmailList(params);
    emails.value = data.content;
    pagination.total = data.totalElements;
  } catch {
    emails.value = [];
  } finally {
    loading.value = false;
  }
};

const handleFilter = () => { pagination.current = 1; fetchEmails(); };
const handleResetFilter = () => {
  filterForm.influencerName = '';
  filterForm.recipientEmail = '';
  filterForm.status = undefined;
  filterForm.timeRange = null;
  handleFilter();
};
const handleTabChange = () => { pagination.current = 1; fetchEmails(); };
const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  fetchEmails();
};
const onSelectChange = (keys: any[]) => { selectedRowKeys.value = keys; };

// --- Actions ---
const handleResend = async (record: OutreachEmail) => {
  try {
    await resendEmail(record.id);
    message.success('重发请求已提交');
    fetchEmails();
  } catch { message.error('重发失败'); }
};

const handleSendOne = async (record: OutreachEmail) => {
  try {
    await sendEmail({
      influencerIds: [record.influencerId],
      senderEmail: record.senderEmail,
      subject: record.subject,
      bodyHtml: record.bodyHtml,
    });
    message.success('发送成功');
    fetchEmails();
  } catch { message.error('发送失败'); }
};

const handleBatchSend = () => {
  if (selectedRowKeys.value.length === 0) return;
  message.info(`批量发送 ${selectedRowKeys.value.length} 封邮件`);
};

// --- Compose drawer ---
const composeDrawerOpen = ref(false);
const composeSending = ref(false);
const availableSenders = ref<OutreachSender[]>([]);
const availableTemplates = ref<OutreachTemplate[]>([]);
const influencerSearchResults = ref<{ label: string; value: number }[]>([]);

const composeForm = reactive({
  senderEmail: '',
  influencerIds: [] as number[],
  templateId: undefined as number | undefined,
  subject: '',
  bodyHtml: '',
  sendMode: 'now' as 'now' | 'schedule',
  scheduledAt: null as Dayjs | null,
});

const openComposeDrawer = () => {
  composeForm.senderEmail = availableSenders.value.find(s => s.isDefault)?.email || '';
  composeForm.influencerIds = [];
  composeForm.templateId = undefined;
  composeForm.subject = '';
  composeForm.bodyHtml = '';
  composeForm.sendMode = 'now';
  composeForm.scheduledAt = null;
  composeDrawerOpen.value = true;
};

const handleTemplateChange = (templateId: number) => {
  const tpl = availableTemplates.value.find(t => t.id === templateId);
  if (tpl) {
    composeForm.subject = tpl.subject;
    composeForm.bodyHtml = tpl.bodyHtml;
  }
};

const handleSearchInfluencer = (val: string) => {
  // Placeholder for influencer search — would call influencer API
  if (!val) {
    influencerSearchResults.value = [];
    return;
  }
  // Demo: would be replaced with actual search
  influencerSearchResults.value = [];
};

const handleComposeSend = async () => {
  if (!composeForm.senderEmail) { message.warning('请选择发件人'); return; }
  if (composeForm.influencerIds.length === 0) { message.warning('请选择收件红人'); return; }
  if (!composeForm.subject) { message.warning('请输入邮件主题'); return; }
  if (!composeForm.bodyHtml) { message.warning('请输入邮件正文'); return; }

  composeSending.value = true;
  try {
    await sendEmail({
      influencerIds: composeForm.influencerIds,
      senderEmail: composeForm.senderEmail,
      subject: composeForm.subject,
      bodyHtml: composeForm.bodyHtml,
      scheduledAt: composeForm.sendMode === 'schedule' && composeForm.scheduledAt
        ? composeForm.scheduledAt.format('YYYY-MM-DD HH:mm:ss')
        : undefined,
    });
    message.success('邮件已发送');
    composeDrawerOpen.value = false;
    fetchEmails();
  } catch {
    message.error('发送失败');
  } finally {
    composeSending.value = false;
  }
};

// --- Detail drawer ---
const detailDrawerOpen = ref(false);
const detailEmail = ref<OutreachEmail | null>(null);

const openDetailDrawer = (record: OutreachEmail) => {
  detailEmail.value = record;
  detailDrawerOpen.value = true;
};

// --- Helpers ---
const getStatusLabel = (status: string) => EMAIL_STATUS_MAP[status as EmailStatus]?.label || status;
const getStatusColor = (status: string) => EMAIL_STATUS_MAP[status as EmailStatus]?.color || '#94a3b8';
const getStatusBg = (status: string) => {
  const colorMap: Record<string, string> = {
    draft: '#f8fafc', pending: '#fffbeb', sending: '#eff6ff', delivered: '#ecfdf5',
    opened: '#f5f3ff', replied: '#ecfeff', bounced: '#fef2f2', failed: '#fef2f2',
  };
  return colorMap[status] || '#f8fafc';
};

const getAvatarColor = (name: string) => {
  const colors = ['#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981', '#3b82f6'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

const formatDate = (dt: string) => dt?.split(' ')[0] || '';
const formatTime = (dt: string) => dt?.split(' ')[1]?.slice(0, 5) || '';

// --- Init ---
const loadDeps = async () => {
  try { availableSenders.value = await getSenderList(); } catch { /* noop */ }
  try { availableTemplates.value = await getAllActiveTemplates(); } catch { /* noop */ }
};

onMounted(() => {
  fetchEmails();
  loadDeps();
});
</script>

<style lang="scss" scoped>
.email-outreach-page {
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
}

.premium-input-search {
  border-radius: 8px;
}

.primary-gradient {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
  font-weight: 600;
  border-radius: 8px;
  &:hover { box-shadow: 0 6px 16px rgba(99, 102, 241, 0.3); transform: translateY(-1px); }
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
      &:hover { color: #6366f1; background: #f1f5f9; }
      &.ant-radio-button-wrapper-checked {
        background: #eff6ff;
        color: #6366f1;
        border-color: #c7d2fe;
        font-weight: 600;
      }
    }
  }

  .toolbar-btns {
    display: flex;
    gap: 8px;
  }
}

.batch-btn {
  height: 32px;
  border-radius: 6px;
  font-weight: 600;
  font-size: 13px;
}

/* Premium table */
.premium-table {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  :deep(.ant-spin-nested-loading), :deep(.ant-spin-container), :deep(.ant-table), :deep(.ant-table-container) {
    flex: 1; display: flex; flex-direction: column; overflow: hidden;
  }
  :deep(.ant-table-content) { overflow: auto !important; }
  :deep(.ant-table) { background: transparent; }
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc !important; padding: 10px 8px !important; font-weight: 700; color: #64748b; font-size: 13px; border-bottom: 2px solid #f1f5f9;
  }
  :deep(.ant-table-tbody > tr > td) { padding: 10px 8px !important; border-bottom: 1px solid #f8fafc; }
  :deep(.ant-table-tbody > tr:hover > td) { background: #f0f7ff !important; }
  :deep(.ant-table-tbody > tr.ant-table-measure-row > td) {
    padding: 0 !important; border: none !important; height: 0 !important; line-height: 0 !important; overflow: hidden !important; font-size: 0 !important;
  }
}

/* Cells */
.influencer-cell {
  display: flex;
  align-items: center;
  gap: 10px;

  .inf-avatar {
    flex-shrink: 0;
    font-weight: 700;
    font-size: 13px;
    color: #fff;
  }

  .inf-info {
    .inf-name { font-weight: 700; color: #1e293b; font-size: 13px; }
    .inf-email { font-size: 11px; color: #94a3b8; margin-top: 1px; }
  }
}

.subject-cell {
  .subject-text {
    font-size: 13px;
    color: #334155;
    font-weight: 500;
  }
}

.status-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-label {
  font-size: 12px;
  font-weight: 600;
  color: #334155;
}

.time-cell {
  font-size: 12px;
  color: #64748b;
  .time-sub { font-size: 11px; color: #94a3b8; }
}

.text-muted { color: #cbd5e1; font-size: 12px; }

.action-btn {
  font-weight: 600; font-size: 12px;
  &.view { color: #3b82f6; &:hover { background: #eff6ff; } }
  &.resend { color: #f59e0b; &:hover { background: #fffbeb; } }
  &.send { color: #10b981; &:hover { background: #ecfdf5; } }
}

/* Pagination */
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

/* Drawers */
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #f1f5f9;
  background: #fafbfc;
}

.drawer-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.drawer-icon {
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

.drawer-title {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
}

.drawer-subtitle {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.drawer-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #94a3b8;
  &:hover { background: #f1f5f9; color: #ef4444; }
}

.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.drawer-footer {
  padding: 12px 24px;
  border-top: 1px solid #f1f5f9;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  gap: 12px;

  .btn-cancel {
    height: 36px; padding: 0 20px; border-radius: 8px; font-weight: 600;
  }
  .btn-submit {
    height: 36px; padding: 0 24px; border-radius: 8px; font-weight: 700;
    background: linear-gradient(135deg, #6366f1, #8b5cf6); border: none; color: #fff;
    &:hover { box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3); }
  }
}

.selected-count {
  font-size: 11px;
  color: #6366f1;
  font-weight: 600;
  margin-top: 4px;
}

.premium-input {
  border-radius: 8px;
}

/* Detail drawer */
.detail-status-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 20px;

  .status-dot-lg {
    width: 10px;
    height: 10px;
    border-radius: 50%;
  }
  .status-text-lg {
    font-size: 14px;
    font-weight: 700;
    color: #1e293b;
  }
}

.detail-section {
  margin-bottom: 20px;
}

.detail-label {
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.detail-value {
  font-size: 14px;
  color: #1e293b;
  display: flex;
  align-items: center;
}

.detail-body-preview {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 16px;
  font-size: 13px;
  color: #334155;
  line-height: 1.7;
  max-height: 300px;
  overflow-y: auto;
}

/* Timeline */
.timeline-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.timeline-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  position: relative;

  &:not(:last-child)::before {
    content: '';
    position: absolute;
    left: 5px;
    top: 22px;
    bottom: -2px;
    width: 2px;
    background: #f1f5f9;
  }
}

.tl-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 2px;
}

.tl-content {
  .tl-title { font-size: 13px; font-weight: 600; color: #1e293b; }
  .tl-time { font-size: 11px; color: #94a3b8; margin-top: 2px; }
  .tl-error { font-size: 11px; color: #ef4444; margin-top: 2px; }
}
</style>
