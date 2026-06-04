<template>
  <div class="commission-payment-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="打款ID">
              <a-input v-model:value="filterForm.payId" placeholder="请输入打款ID" class="premium-input" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联红人">
              <a-input
                v-model:value="filterForm.influencerName"
                placeholder="双击可批量搜索"
                class="premium-input cursor-pointer"
                allow-clear
                @dblclick="openBatchSearchInfluencer"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="负责人">
              <a-input
                v-model:value="filterForm.ownerName"
                placeholder="双击可批量搜索"
                class="premium-input cursor-pointer"
                allow-clear
                @dblclick="openBatchSearchOwner"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="联络员">
              <a-input
                v-model:value="filterForm.liaison"
                placeholder="双击可批量搜索"
                class="premium-input cursor-pointer"
                allow-clear
                @dblclick="openBatchSearchLiaison"
              />
            </a-form-item>
          </a-col>
          <!-- 打款状态已由上方 Tab 页签控制，无需重复筛选 -->
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="打款金额范围">
              <a-input-group compact class="premium-range-group">
                <a-input-number
                  v-model:value="filterForm.amountMin"
                  placeholder="最小金额"
                  :min="0"
                  style="width: 50%; border-radius: 8px 0 0 8px !important;"
                  class="premium-input"
                />
                <a-input-number
                  v-model:value="filterForm.amountMax"
                  placeholder="最大金额"
                  :min="0"
                  style="width: 50%; border-radius: 0 8px 8px 0 !important; border-left: 0;"
                  class="premium-input"
                />
              </a-input-group>
            </a-form-item>
          </a-col>
          
          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="8">
              <a-form-item label="时间范围">
                <div class="premium-range-group">
                  <a-select 
                    v-model:value="filterForm.timeType" 
                    style="width: 110px" 
                    class="premium-select"
                  >
                    <a-select-option value="apply_time">申请时间</a-select-option>
                    <a-select-option value="audit_time">审核时间</a-select-option>
                    <a-select-option value="pay_time">打款时间</a-select-option>
                  </a-select>
                  <a-range-picker
                    v-model:value="filterForm.timeRange"
                    style="flex: 1"
                    :placeholder="['开始时间', '结束时间']"
                    format="YYYY-MM-DD"
                    :presets="timeRanges"
                    class="premium-datepicker"
                  />
                </div>
              </a-form-item>
            </a-col>
          </template>

          <a-col :span="24" style="display: flex; justify-content: flex-end; align-items: flex-end;">
            <div class="filter-footer" style="padding-top: 0; border-top: none; margin-top: 0;">
              <a-space size="middle">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn" style="height: 32px; padding: 0 20px;">重置</a-button>
                <a-button type="link" @click="filterExpanded = !filterExpanded" class="expand-btn" style="font-size: 13px;">
                  {{ filterExpanded ? '收起筛选' : '更多筛选' }} <component :is="filterExpanded ? UpOutlined : DownOutlined" />
                </a-button>
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
              <a-radio-button value="pending_audit">待审核 ({{ statusCounts.pending || 0 }})</a-radio-button>
              <a-radio-button value="pending_pay">待打款 ({{ statusCounts.approved || 0 }})</a-radio-button>
              <a-radio-button value="paid">已打款 ({{ statusCounts.completed || 0 }})</a-radio-button>
              <a-radio-button value="rejected">已拒绝 ({{ statusCounts.rejected || 0 }})</a-radio-button>
            </a-radio-group>
          </div>
          <div class="toolbar-btns">
            <a-space size="small">
              <a-button 
                type="primary" 
                class="premium-btn-small" 
                @click="handleBatchApprove" 
                :disabled="activeKey !== 'pending_audit' || !selectedRowKeys.length"
                v-permission="'commission.pay.batch_approve'"
              >
                批量审核通过
              </a-button>
              <a-button 
                class="premium-btn-small" 
                danger 
                @click="handleBatchReject" 
                :disabled="activeKey !== 'pending_audit' || !selectedRowKeys.length"
                v-permission="'commission.pay.batch_reject'"
              >
                批量驳回
              </a-button>
              <a-button class="premium-btn-small" v-permission="'commission.pay.export'" @click="handleExport">
                <template #icon><ExportOutlined /></template>
                导出
              </a-button>
            </a-space>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="filteredData"
        :row-key="(record: any) => record.key ?? record.payId"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :scroll="{ y: 'calc(100vh - 380px)', x: 'max-content' }"
        :row-selection="rowSelection"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span style="font-weight: 600; color: #1890ff; font-family: 'JetBrains Mono', monospace;">${{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'proof'">
            <div v-if="record.proof && record.proofFileName">
              <a :href="record.proof.startsWith('http') ? record.proof : '/influencer-api' + record.proof" target="_blank" style="display: flex; align-items: center; gap: 8px;">
                <file-outlined style="color: #1890ff;" />
                <span>{{ record.proofFileName }}</span>
              </a>
            </div>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag color="processing" v-if="record.status === '待审核'" class="status-tag tag-blue">待审核</a-tag>
            <a-tag color="warning" v-else-if="record.status === '待打款'" class="status-tag tag-purple">待打款</a-tag>
            <a-tag color="success" v-else-if="record.status === '已打款'" class="status-tag tag-success">已打款</a-tag>
            <a-tag color="error" v-else-if="record.status === '已驳回'" class="status-tag tag-red">已驳回</a-tag>
            <span v-else>{{ record.status }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <template v-if="activeKey === 'pending_pay'">
                <a-button type="link" size="small" @click="handleUpload(record)" class="action-link primary" v-permission="'commission.pay.upload'">上传凭证并打款</a-button>
                <a-divider type="vertical" />
                <a-button type="link" size="small" @click="handleRejectPending(record)" class="action-link" style="color: #ff4d4f;" v-permission="'commission.pay.reject'">拒绝</a-button>
              </template>
              <template v-else-if="activeKey === 'pending_audit'">
                <a-button type="link" size="small" @click="handleAudit(record)" class="action-link primary" v-permission="'commission.pay.approve'">审核通过</a-button>
                <a-divider type="vertical" />
                <a-button type="link" size="small" @click="handleReject(record)" class="action-link" style="color: #ff4d4f;" v-permission="'commission.pay.reject'">驳回</a-button>
              </template>
              <template v-else-if="activeKey === 'paid'">
                <a-button type="link" size="small" @click="handleViewProof(record)" class="action-link">查看凭证</a-button>
              </template>
            </div>
          </template>
        </template>
      </a-table>

      <div class="pagination-footer">
        <div class="footer-left">
            <span class="info-item">当前记录数量 <span class="count-value">{{ pagination.total }}</span></span>
            <span class="info-divider">/</span>
            <span class="info-item">选中数量 <span class="count-value highlight">{{ selectedRowKeys.length }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="handleTableChange"
          />
        </div>
      </div>
    </a-card>

    <UploadVoucherModal
      v-model:open="uploadModalVisible"
      :form="uploadForm"
      :file-list="fileList"
      @update:form="onUpdateUploadForm"
      @update:file-list="onUpdateFileList"
      @ok="handleUploadOk"
    />

    <CommissionAuditModal
      v-model:open="auditModalVisible"
      :record="auditRecord"
      @ok="handleAuditOk"
    />

    <!-- 查看凭证Modal -->
    <CommissionProofModal
      v-model:open="proofModalVisible"
      :proof="currentProof"
    />
    
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :export-fields="payoutExportFields"
      :selected-count="selectedRowKeys.length"
      :current-user-id="String(userStore.userInfo?.id || '')"
      page-type="commission-payout"
      @export="handleExportFromModal"
    />
    
    <!-- 批量审核通过原因弹窗 -->
    <a-modal
      v-model:open="approveModalVisible"
      title="批量审核通过"
      ok-text="确认通过"
      cancel-text="取消"
      @ok="confirmBatchApprove"
    >
      <a-form layout="vertical">
        <a-form-item 
          label="原因" 
          required 
          :validate-status="!approveReason.trim() ? 'error' : ''" 
          :help="!approveReason.trim() ? '请输入原因' : ''"
        >
          <a-textarea 
            v-model:value="approveReason" 
            :rows="4" 
            :maxlength="500" 
            show-count 
            placeholder="请输入审核通过原因"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 驳回原因弹窗 (单条与批量共用) -->
    <a-modal
      v-model:open="rejectModalVisible"
      :title="null"
      :footer="null"
      width="500px"
      class="premium-distribute-modal reject-reason-modal"
      centered
      :mask-closable="false"
      :closable="true"
    >
      <!-- Use custom close icon to ensure visibility -->
      <template #closeIcon>
        <div class="custom-close-icon">
          <CloseOutlined style="color: rgba(255,255,255,0.8); font-size: 18px;" />
        </div>
      </template>

      <div class="dist-modal-content">
        <!-- Header with "Intercept Operation" Style -->
        <div class="dist-header warning-header">
          <div class="header-overlay"></div>
          <div class="influencer-profile">
            <div class="avatar-info" v-if="rejectActionType !== 'batch'">
              <div class="initial-avatar">{{ rejectRecordName?.charAt(0).toUpperCase() || 'R' }}</div>
              <div class="name-box">
                <div class="name">{{ rejectRecordName || '未知目标' }}</div>
                <div class="email-row">
                  <ExclamationCircleOutlined class="warning-icon" />
                  <span class="email-text">请填写驳回/拒绝原因</span>
                </div>
              </div>
            </div>
            <div class="avatar-info batch-info" v-else>
               <div class="initial-avatar batch-avatar">
                 <TeamOutlined />
               </div>
               <div class="name-box">
                <div class="name">批量驳回 ({{ selectedRowKeys.length }}人)</div>
                <div class="email-row">
                  <ExclamationCircleOutlined class="warning-icon" />
                  <span class="email-text">请填写统一的驳回原因</span>
                </div>
              </div>
            </div>
            <div class="header-action-tag warning-tag">
               拦截操作
            </div>
          </div>
        </div>

        <div class="dist-body" style="background: #fff;">
          <div class="warning-content">
            <p style="margin-top: 8px; margin-bottom: 20px; font-size: 15px; color: #334155; line-height: 1.6;">
               请输入详细的原因，以便通知相关关联方并留下操作记录：
            </p>
            <a-form layout="vertical" class="premium-form">
              <a-form-item>
                <a-textarea 
                  v-model:value="rejectReason" 
                  :rows="4" 
                  :maxlength="500" 
                  show-count 
                  placeholder="在此输入本次阻拦操作的详细原因 (选填)..."
                  class="premium-textarea-refined"
                  style="border-color: #fca5a5; background: #fffcfc; font-size: 14px; border-radius: 12px;"
                />
              </a-form-item>
            </a-form>
          </div>
        </div>

        <div class="dist-footer" style="padding: 16px 24px 24px;">
          <a-button type="primary" @click="confirmRejectAction" class="btn-submit-architect confirm-btn" style="height: 44px; font-size: 15px; border-radius: 10px; background: #fb7185; border: none; box-shadow: 0 4px 12px rgba(251, 113, 133, 0.2);">
            知道了并确认操作
          </a-button>
        </div>
      </div>
    </a-modal>

    <!-- 批量搜索红人弹窗 -->
    <a-modal v-model:open="batchSearchInfluencerVisible" title="批量搜索关联红人" @ok="handleBatchSearchInfluencerOk"
      @cancel="batchSearchInfluencerVisible = false" :width="500" class="premium-modal">
      <a-textarea v-model:value="batchSearchInfluencerInput"
        placeholder="请输入要搜索的关联红人列表，支持回车换行或逗号分隔。每行/逗号对应一个红人。&#10;&#10;例如：&#10;Natalie Wilson&#10;Hutton Butler" :rows="8" class="premium-input custom-scrollbar" />
      <div style="margin-top: 10px; color: #8c8c8c; font-size: 13px;">
        将精确匹配输入的名称列表
      </div>
    </a-modal>

    <!-- 批量搜索负责人弹窗 -->
    <a-modal v-model:open="batchSearchOwnerVisible" title="批量搜索负责人" @ok="handleBatchSearchOwnerOk"
      @cancel="batchSearchOwnerVisible = false" :width="500" class="premium-modal">
      <a-textarea v-model:value="batchSearchOwnerInput"
        placeholder="请输入要搜索的负责人名称列表，支持回车换行或逗号分隔。" :rows="6" class="premium-input custom-scrollbar" />
    </a-modal>

    <!-- 批量搜索联络员弹窗 -->
    <a-modal v-model:open="batchSearchLiaisonVisible" title="批量搜索联络员" @ok="handleBatchSearchLiaisonOk"
      @cancel="batchSearchLiaisonVisible = false" :width="500" class="premium-modal">
      <a-textarea v-model:value="batchSearchLiaisonInput"
        placeholder="请输入要搜索的联络员名称列表，支持回车换行或逗号分隔。" :rows="6" class="premium-input custom-scrollbar" />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, h, onMounted, watch } from 'vue';
import { DownOutlined, UpOutlined, FileOutlined, LoadingOutlined, ExportOutlined, CloseOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';
import UploadVoucherModal from '@/components/commission/UploadVoucherModal.vue';
import CommissionProofModal from '@/components/commission/CommissionProofModal.vue';
import CommissionAuditModal from '@/components/commission/CommissionAuditModal.vue';
import { influencerService } from '@/services/influencerService';
import { fetchAllPages } from '@/utils/dataHelper';
import { createPaymentExportTaskWithAnalytics } from '@/utils/exportTaskHelper';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import { useUserStore } from '@/stores/user';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';
import { useSseStore } from '@/stores/sse';

const influencerHttp = influencerService;
const userStore = useUserStore();
const sseStore = useSseStore();

// Fix #11: SSE auto-refresh for commission events
watch(() => sseStore.lastEvent, (event) => {
  if (event && (
    event.category === 'commission' ||
    event.category === 'payout' ||
    (event.category === 'system' && (event.data?.topic === 'reconnect' || event.data?.topic === 'fallback_poll'))
  )) {
    fetchPayouts();
    fetchStatusCounts();
  }
});

// Fix #10: Tab status counts
const statusCounts = ref<Record<string, number>>({});

const filterExpanded = ref(false);
const activeKey = ref('pending_audit');
const loading = ref(false);
const uploadModalVisible = ref(false);
const proofModalVisible = ref(false);
const currentRecord = ref<any>(null);
const currentProof = ref<any>(null);
const fileList = ref<any[]>([]);
const selectedRowKeys = ref<(string | number)[]>([]);
const approveModalVisible = ref(false);
const rejectModalVisible = ref(false);
const exportModalVisible = ref(false);
const auditModalVisible = ref(false);
const auditRecord = ref<any>(null);
const approveReason = ref('');
const rejectReason = ref('');
const rejectActionType = ref<'single' | 'batch' | 'pending'>('single');
const rejectRecordId = ref<number | null>(null);
const rejectRecordName = ref<string>('');

// Custom loading indicator for table
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

// --- State Persistence ---
const STATE_KEY = 'commission_payment_state';

const saveState = () => {
  const state = {
    activeKey: activeKey.value,
    filterForm: {
      ...filterForm,
      // Date range needs to be converted to strings for storage
      timeRange: filterForm.timeRange ? [
        filterForm.timeRange[0].toString(),
        filterForm.timeRange[1].toString()
      ] : undefined
    },
    pagination: {
      current: pagination.current,
      pageSize: pagination.pageSize
    },
    filterExpanded: filterExpanded.value
  };
  sessionStorage.setItem(STATE_KEY, JSON.stringify(state));
};

const loadState = () => {
  const saved = sessionStorage.getItem(STATE_KEY);
  if (saved) {
    try {
      const state = JSON.parse(saved);
      activeKey.value = state.activeKey || 'pending_audit';
      filterExpanded.value = !!state.filterExpanded;
      
      if (state.filterForm) {
        Object.assign(filterForm, state.filterForm);
        // Restore dayjs objects for date range
        if (state.filterForm.timeRange && Array.isArray(state.filterForm.timeRange)) {
           filterForm.timeRange = [
             dayjs(state.filterForm.timeRange[0]),
             dayjs(state.filterForm.timeRange[1])
           ] as any;
        }
      }
      
      if (state.pagination) {
        pagination.current = state.pagination.current || 1;
        pagination.pageSize = state.pagination.pageSize || 10;
      }
    } catch (e) {
      console.error('Failed to load state', e);
    }
  }
};

const onUpdateUploadForm = (val: any) => {
  Object.assign(uploadForm, val || {});
};

const onUpdateFileList = (val: any[]) => {
  fileList.value = val || [];
};

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 30,
  showSizeChanger: true,
  showQuickJumper: true,
});

const filterForm = reactive({
  payId: '',
  influencerName: undefined as string | undefined,
  ownerName: undefined as string | undefined,
  liaison: undefined as string | undefined,
  status: undefined as string | undefined,
  amountMin: undefined as number | undefined,
  amountMax: undefined as number | undefined,
  timeType: 'apply_time',
  timeRange: undefined as [string, string] | undefined,
});

const uploadForm = reactive({
  paymentAmount: undefined as number | undefined,
  remark: '',
  paymentMethodDisplay: '',
  paymentDetails: {} as any,
  fee: 0 as number,
  actualPaidAt: '' as string,
  totalAmount: 0 as number,
  rawData: {} as any,
});

const influencers = ['Influencer 1', 'Influencer 2', 'Influencer 3', 'Influencer 4', 'Influencer 5'];

// Batch Search Influencer State
const batchSearchInfluencerVisible = ref(false);
const batchSearchInfluencerInput = ref('');

const openBatchSearchInfluencer = () => {
  batchSearchInfluencerInput.value = filterForm.influencerName || '';
  batchSearchInfluencerVisible.value = true;
};

const handleBatchSearchInfluencerOk = () => {
  const list = batchSearchInfluencerInput.value
    .split(/[\n,\uff0c]+/)
    .map((s: string) => s.trim())
    .filter((s: string) => s.length > 0);
  
  filterForm.influencerName = list.join(',');
  batchSearchInfluencerVisible.value = false;
  handleFilter();
};

// Batch Search Owner State
const batchSearchOwnerVisible = ref(false);
const batchSearchOwnerInput = ref('');

const openBatchSearchOwner = () => {
  batchSearchOwnerInput.value = filterForm.ownerName || '';
  batchSearchOwnerVisible.value = true;
};

const handleBatchSearchOwnerOk = () => {
  const list = batchSearchOwnerInput.value
    .split(/[\n,\uff0c]+/)
    .map((s: string) => s.trim())
    .filter((s: string) => s.length > 0);
  filterForm.ownerName = list.join(',');
  batchSearchOwnerVisible.value = false;
  handleFilter();
};

// Batch Search Liaison State
const batchSearchLiaisonVisible = ref(false);
const batchSearchLiaisonInput = ref('');

const openBatchSearchLiaison = () => {
  batchSearchLiaisonInput.value = filterForm.liaison || '';
  batchSearchLiaisonVisible.value = true;
};

const handleBatchSearchLiaisonOk = () => {
  const list = batchSearchLiaisonInput.value
    .split(/[\n,\uff0c]+/)
    .map((s: string) => s.trim())
    .filter((s: string) => s.length > 0);
  filterForm.liaison = list.join(',');
  batchSearchLiaisonVisible.value = false;
  handleFilter();
};

const timeRanges = [
  { label: '最近7天', value: [dayjs().subtract(6, 'day'), dayjs()] },
  { label: '最近14天', value: [dayjs().subtract(13, 'day'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(29, 'day'), dayjs()] },
  { label: '最近90天', value: [dayjs().subtract(89, 'day'), dayjs()] },
];

const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const columns: TableColumnsType = [
  {
    title: '打款ID',
    dataIndex: 'payId',
    key: 'payId',
    width: 150,
    fixed: 'left',
  },
  {
    title: '分佣ID',
    dataIndex: 'commissionId',
    key: 'commissionId',
    width: 150,
  },
  {
    title: '关联红人',
    dataIndex: 'name',
    key: 'name',
    width: 150,
  },
  {
    title: '负责人',
    dataIndex: 'ownerName',
    key: 'ownerName',
    width: 120,
  },
  {
    title: '联络员',
    dataIndex: 'liaisonName',
    key: 'liaisonName',
    width: 120,
  },
  {
    title: '打款金额',
    key: 'amount',
    width: 120,
    sorter: (a: any, b: any) => a.amount - b.amount,
  },
  {
    title: '申请时间',
    dataIndex: 'applyTime',
    key: 'applyTime',
    width: 180,
  },
  {
    title: '审核时间',
    dataIndex: 'auditTime',
    key: 'auditTime',
    width: 180,
  },
  {
    title: '打款时间',
    dataIndex: 'payTime',
    key: 'payTime',
    width: 180,
  },
  {
    title: '打款凭证',
    key: 'proof',
    width: 200,
  },
  {
    title: '状态',
    key: 'status',
    width: 120,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    width: 200,
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
  },
];

const data = ref<any[]>([]);

// 获取打款列表
const fetchPayouts = async () => {
  loading.value = true;
  try {
    const statusMap: Record<string, string> = {
      'pending_audit': 'pending',
      'pending_pay': 'approved',
      'paid': 'completed',
      'rejected': 'rejected',
    };
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      status: statusMap[activeKey.value]
    }; // Closing the params object here
    if (filterForm.payId) params.payId = filterForm.payId;
    if (filterForm.influencerName) params.influencerName = filterForm.influencerName;
    if (filterForm.ownerName) params.ownerName = filterForm.ownerName;
    if (filterForm.liaison) params.liaison = filterForm.liaison;

    if (filterForm.timeRange && filterForm.timeRange.length === 2) {
      params.timeType = filterForm.timeType;
      params.startTime = dayjs(filterForm.timeRange[0]).format('YYYY-MM-DD 00:00:00');
      params.endTime = dayjs(filterForm.timeRange[1]).format('YYYY-MM-DD 23:59:59');
    }
    const response = await influencerHttp.getPayouts(params);
    const resData = response;
    if (resData && resData.success && resData.data) {
      const payouts = resData.data.content || [];
      data.value = payouts.map((p: any) => ({
        key: p.id,
        payId: p.paymentId || `PAY${p.id}`,
        commissionId: `COMM${p.id}`,
        name: p.influencerName || `红人 ${p.influencerId}`,
        ownerName: p.ownerName || '-',
        liaisonName: p.liaisonName || '-',
        influencerId: p.influencerId,
        amount: parseFloat((p.amount || 0).toFixed(2)),
        applyTime: p.createdAt ? dayjs(p.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        auditTime: p.approvedAt ? dayjs(p.approvedAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        payTime: p.paidAt ? dayjs(p.paidAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        proof: p.paymentReference || null,
        proofFileName: (() => {
            if (!p.paymentReference) return null;
            // 优先从 paymentDetails 中解析原始文件名
            if (p.paymentDetails) {
                try {
                    const details = JSON.parse(p.paymentDetails);
                    if (details.fileName) return details.fileName;
                } catch(e){}
            }
            // 兜底方案：使用打款ID作为文件名展示
            const ext = p.paymentReference.includes('.') ? p.paymentReference.split('.').pop() : 'pdf';
            return (p.paymentId || `PAY${p.id}`) + '.' + ext;
        })(),
        proofUrl: p.paymentReference
          ? (p.paymentReference.startsWith('http')
              ? p.paymentReference
              : `${window.location.origin}/influencer-api${p.paymentReference}`)
          : '',
        status: p.status === 'pending' ? '待审核' : p.status === 'approved' ? '待打款' : p.status === 'completed' ? '已打款' : '已驳回',
        remark: p.remark || '',
        feeText: p.fee != null ? `$${p.fee}` : '-',
        totalAmountText: p.totalAmount != null ? `$${p.totalAmount}` : '-',
        paymentMethodLabel: p.paymentMethod === 'paypal' ? 'PayPal' : p.paymentMethod === 'bank_card' ? 'Bank Transfer' : (p.paymentMethod || '-'),
        paymentAccount: p.paymentAccount || '-',
        actualPaidAtText: p.actualPaidAt ? dayjs(p.actualPaidAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        approvedBy: p.approvedBy || '-',
        rejectedBy: p.rejectedBy || '-',
        reviewRemark: p.reviewRemark || '',
        rawData: p
      }));
      pagination.total = resData.data.totalElements || 0;
    }
  } catch (error: any) {
    if (error?.code === 'ERR_CANCELED') return;
    console.error('获取打款列表失败:', error);
    message.error('获取打款列表失败');
  } finally {
    loading.value = false;
  }
};

const filteredData = computed(() => {
  let result = [...data.value];
  
  // Only apply filters NOT handled by backend API
  if (filterForm.amountMin !== undefined) {
    result = result.filter(item => item.amount >= filterForm.amountMin!);
  }
  if (filterForm.amountMax !== undefined) {
    result = result.filter(item => item.amount <= filterForm.amountMax!);
  }
  
  return result;
});

const handleTableChange = (paginationOrPage?: any, filtersOrPageSize?: any, sorter?: any) => {
  if (typeof paginationOrPage === 'number') {
    pagination.current = paginationOrPage;
    pagination.pageSize = filtersOrPageSize || 10;
  } else if (paginationOrPage && typeof paginationOrPage === 'object') {
    pagination.current = paginationOrPage.current ?? pagination.current;
    pagination.pageSize = paginationOrPage.pageSize ?? pagination.pageSize;
  }
  saveState();
  fetchPayouts();
};

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: (string | number)[]) => { selectedRowKeys.value = keys; },
}));

const handleResetFilter = () => {
  filterForm.payId = '';
  filterForm.influencerName = undefined;
  filterForm.ownerName = undefined;
  filterForm.liaison = undefined;
  filterForm.status = undefined;
  filterForm.amountMin = undefined;
  filterForm.amountMax = undefined;
  filterForm.timeRange = undefined;
  handleFilter();
};

const handleFilter = () => {
  pagination.current = 1;
  saveState();
  fetchPayouts();
};

const handleTabChange = () => {
  pagination.current = 1;
  selectedRowKeys.value = [];
  saveState();
  fetchPayouts();
};

const handleUpload = (record: any) => {
  currentRecord.value = record;
  let pDetails: any = {};
  if (record.rawData?.paymentDetails) {
     try {
         pDetails = JSON.parse(record.rawData.paymentDetails);
     } catch(e){}
  }
  
  uploadForm.paymentMethodDisplay = record.rawData?.paymentMethod || '';
  uploadForm.paymentDetails = pDetails;
  uploadForm.paymentAmount = record.amount;
  uploadForm.remark = '';
  uploadForm.fee = 0;
  uploadForm.actualPaidAt = '';
  uploadForm.totalAmount = 0;
  uploadForm.rawData = record.rawData || {};
  
  fileList.value = [];
  uploadModalVisible.value = true;
};

const handleUploadOk = async () => {
  if (!uploadForm.paymentAmount) {
     message.warning('请输入实际付款金额');
     return;
  }
  
  if (fileList.value.length === 0) {
     message.warning('请先上传打款凭证');
     return;
  }
  
  loading.value = true;
  try {
    let paymentReference = 'manual_confirm';
    let proofFileName = '';

    // 1. 如果有文件且是新上传的（originFileObj 存在），则先进行物理上传
    const fileItem = fileList.value[0];
    if (fileItem.originFileObj) {
      // 传递 payId 作为文件名建议
      const uploadRes = await influencerHttp.uploadVoucher(fileItem.originFileObj, currentRecord.value.payId);
      if (uploadRes && uploadRes.success) {
        paymentReference = uploadRes.data.url;
        proofFileName = uploadRes.data.fileName;
      } else {
        throw new Error(uploadRes?.message || '文件上传失败');
      }
    } else if (fileItem.url) {
      // 如果已经有 URL（可能是编辑场景，虽然这里目前不是），直接用
      paymentReference = fileItem.url;
      proofFileName = fileItem.name;
    }

    const payload = {
      ...uploadForm.paymentDetails,
      paymentMethod: uploadForm.paymentMethodDisplay,
      paymentAmount: uploadForm.paymentAmount,
      remark: uploadForm.remark,
      paymentReference: paymentReference,
      proofFileName: proofFileName || fileItem.name,
      fee: uploadForm.fee || 0,
      totalAmount: uploadForm.totalAmount || uploadForm.paymentAmount,
      actualPaidAt: uploadForm.actualPaidAt || ''
    };
    
    const response = await influencerHttp.confirmPayout(currentRecord.value.key, payload);
    if (response && response.success) {
      uploadModalVisible.value = false;
      message.success('上传凭证并打款成功');
      // 清空本地状态
      fileList.value = [];
      fetchPayouts();
      fetchStatusCounts();
    } else {
      message.error(response.message || '操作失败');
    }
  } catch (error: any) {
    console.error('确认打款失败:', error);
    message.error('确认打款失败: ' + (error.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

const handleAudit = (record: any) => {
  auditRecord.value = record;
  auditModalVisible.value = true;
};

const handleAuditOk = async (data: { paymentMethod: string; paymentAccount: string; paymentDetails: any; remark: string }) => {
  try {
    const payload = {
      paymentMethod: data.paymentMethod,
      paymentAccount: data.paymentAccount,
      paymentDetails: data.paymentDetails
    };
    const response = await influencerHttp.auditPayout(auditRecord.value.key, 'approve', data.remark, payload);
    if (response && response.success) {
      auditModalVisible.value = false;
      message.success('审核通过成功');
      fetchPayouts();
      fetchStatusCounts();
    } else {
      message.error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('审核失败:', error);
    message.error('审核失败');
  }
};

const handleReject = (record: any) => {
  rejectActionType.value = 'single';
  rejectRecordId.value = record.key;
  rejectRecordName.value = record.name;
  rejectReason.value = '';
  rejectModalVisible.value = true;
};

// 拒绝待打款的申请（从待打款列表中拒绝）
const handleRejectPending = (record: any) => {
  rejectActionType.value = 'pending';
  rejectRecordId.value = record.key;
  rejectRecordName.value = record.name;
  rejectReason.value = '';
  rejectModalVisible.value = true;
};

const handleViewProof = (record: any) => {
  currentProof.value = { ...record };
  proofModalVisible.value = true;
};

const handleBatchApprove = () => {
  if (!selectedRowKeys.value.length) {
    message.warning('请选择需要操作的记录');
    return;
  }
  approveReason.value = '';
  approveModalVisible.value = true;
};

const handleBatchReject = () => {
  if (!selectedRowKeys.value.length) {
    message.warning('请选择需要操作的记录');
    return;
  }
  rejectActionType.value = 'batch';
  rejectRecordName.value = '批量操作';
  rejectReason.value = '';
  rejectModalVisible.value = true;
};

const confirmBatchApprove = async () => {
  if (!approveReason.value.trim()) {
    message.warning('请输入原因');
    return;
  }
  
  // 简化处理：循环调用（实际应使用批量接口）
  let successCount = 0;
  for (const key of selectedRowKeys.value) {
    try {
      await influencerHttp.auditPayout(Number(key), 'approve', approveReason.value);
      successCount++;
    } catch (e) {
      console.error(`审核 ${key} 失败`, e);
    }
  }
  
  approveModalVisible.value = false;
  selectedRowKeys.value = [];
  if (successCount > 0) {
    message.success(`成功审核通过 ${successCount} 条记录`);
    fetchPayouts();
    fetchStatusCounts();
  }
};

const confirmRejectAction = async () => {
  if (rejectActionType.value === 'batch') {
    let successCount = 0;
    for (const key of selectedRowKeys.value) {
      try {
        await influencerHttp.auditPayout(Number(key), 'reject', rejectReason.value);
        successCount++;
      } catch (e) {
        console.error(`驳回 ${key} 失败`, e);
      }
    }
    selectedRowKeys.value = [];
    if (successCount > 0) {
      message.success(`成功驳回 ${successCount} 条记录`);
    }
  } else if (rejectActionType.value === 'single' && rejectRecordId.value) {
    try {
      const response = await influencerHttp.auditPayout(rejectRecordId.value, 'reject', rejectReason.value);
      if (response && response.success) {
        message.success('已驳回');
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('驳回失败');
    }
  } else if (rejectActionType.value === 'pending' && rejectRecordId.value) {
    try {
      const response = await influencerHttp.rejectPayout(rejectRecordId.value, rejectReason.value);
      if (response && response.success) {
        message.success('已拒绝该打款申请');
      } else {
        message.error(response.message || '操作失败');
      }
    } catch (error) {
      message.error('拒绝打款失败');
    }
  }
  
  rejectModalVisible.value = false;
  fetchPayouts();
  fetchStatusCounts();
};

// 导出功能
const payoutExportFields = [
  { key: 'payId', title: '打款ID' },
  { key: 'commissionId', title: '分佣ID' },
  { key: 'influencerId', title: '红人ID' },
  { key: 'name', title: '关联红人' },
  { key: 'ownerName', title: '负责人' },
  { key: 'liaisonName', title: '联络员' },
  { key: 'amount', title: '打款金额' },
  { key: 'feeText', title: '手续费' },
  { key: 'totalAmountText', title: '总付金额' },
  { key: 'paymentMethodLabel', title: '支付方式' },
  { key: 'paymentAccount', title: '支付账号' },
  { key: 'applyTime', title: '申请时间' },
  { key: 'auditTime', title: '审核时间' },
  { key: 'payTime', title: '打款时间' },
  { key: 'actualPaidAtText', title: '实际打款时间' },
  { key: 'approvedBy', title: '审核人' },
  { key: 'rejectedBy', title: '驳回人' },
  { key: 'status', title: '状态' },
  { key: 'remark', title: '申请备注' },
  { key: 'reviewRemark', title: '审核备注' },
  { key: 'proofUrl', title: '付款凭证', type: 'image' as const },
];

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async (payload: { scope: 'all' | 'selected'; fields: string[]; columns: any[]; templateId?: string; templateName?: string }) => {
  const { scope, templateId, templateName } = payload;
  let exportData: any[] = [];

  if (scope === 'selected') {
    if (selectedRowKeys.value.length === 0) {
      message.warning('请先选择要导出的数据');
      return;
    }
    // 注意：filteredData 已经包含了当前页或搜索后的数据
    const pUserId = window.localStorage.getItem('userId');
    const headers = {} as any;
    if (pUserId) {
      const curU = allUsers.value.find(u => Number(u.id) === Number(pUserId));
      if (curU && curU.name) {
        headers['X-User-Name'] = encodeURIComponent(curU.name);
      }
    }

    // 构造导出查询
    const exportParams: any = {
      payId: filterForm.payId,
      influencerName: filterForm.influencerName,
      status: activeKey.value === 'pending_audit' ? 'pending' : 
              activeKey.value === 'pending_pay' ? 'approved' : 
              activeKey.value === 'paid' ? 'completed' : 'rejected'
    };
    exportData = filteredData.value.filter((item: any) => selectedRowKeys.value.includes(item.key));
  } else {
    // 导出全部 - 跨页抓取
    const hideLoading = message.loading('正在获取全量导出数据...', 0);
    try {
      const statusMap: Record<string, string> = {
        'pending_audit': 'pending',
        'pending_pay': 'approved',
        'paid': 'completed',
        'rejected': 'rejected',
      };
      
      const allRawData = await fetchAllPages(
        (p) => {
          const params: any = {
            status: statusMap[activeKey.value],
            page: p.page,
            size: p.size
          };
          if (filterForm.payId) params.payId = filterForm.payId;
          if (filterForm.influencerName) params.influencerName = filterForm.influencerName;
          if (filterForm.ownerName) params.ownerName = filterForm.ownerName;
          if (filterForm.liaison) params.liaison = filterForm.liaison;
          // Note: Backend might expect 'amountMin'/'amountMax' or range. 
          if (filterForm.timeRange && filterForm.timeRange.length === 2) {
             params.timeType = filterForm.timeType;
             params.startTime = dayjs(filterForm.timeRange[0]).format('YYYY-MM-DD 00:00:00');
             params.endTime = dayjs(filterForm.timeRange[1]).format('YYYY-MM-DD 23:59:59');
          }
          return influencerHttp.getPayouts(params);
        },
        {}, 
        { pageSize: 100 }
      );

      // Apply client-side filters to the fetched ALL data
      // This duplicates the logic in `filteredData` for the full dataset
      let filteredAllData = allRawData;
      
      if (filterForm.payId) {
        filteredAllData = filteredAllData.filter((item: any) => 
          (item.paymentId || `PAY${item.id}`).toLowerCase().includes(filterForm.payId.toLowerCase())
        );
      }
      if (filterForm.influencerName) {
        const nameList = filterForm.influencerName.split(',').map((s: string) => s.trim().toLowerCase());
        filteredAllData = filteredAllData.filter((item: any) => 
          nameList.some((name: string) => (item.influencerName || '').toLowerCase().includes(name))
        );
      }
      if (filterForm.amountMin !== undefined) {
        filteredAllData = filteredAllData.filter((item: any) => 
          parseFloat(item.amount || 0) >= filterForm.amountMin!
        );
      }
      if (filterForm.amountMax !== undefined) {
        filteredAllData = filteredAllData.filter((item: any) => 
          parseFloat(item.amount || 0) <= filterForm.amountMax!
        );
      }

      const statusMapCN: Record<string, string> = {
        'pending': '待审核',
        'approved': '待打款',
        'paid': '已打款',
        'rejected': '已驳回',
        'completed': '已完成'
      };

      exportData = filteredAllData.map((p: any) => ({
        payId: p.paymentId || `PAY${p.id}`,
        commissionId: `COMM${p.id}`,
        influencerId: p.influencerId,
        name: p.influencerName || `红人 ${p.influencerId}`,
        ownerName: p.ownerName || '-',
        liaisonName: p.liaisonName || '-',
        amount: parseFloat((p.amount || 0).toFixed(2)),
        feeText: p.fee != null ? `$${p.fee}` : '-',
        totalAmountText: p.totalAmount != null ? `$${p.totalAmount}` : '-',
        paymentMethodLabel: p.paymentMethod === 'paypal' ? 'PayPal' : p.paymentMethod === 'bank_card' ? 'Bank Transfer' : (p.paymentMethod || '-'),
        paymentAccount: p.paymentAccount || '-',
        applyTime: p.createdAt ? dayjs(p.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        auditTime: p.approvedAt ? dayjs(p.approvedAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        payTime: p.paidAt ? dayjs(p.paidAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        actualPaidAtText: p.actualPaidAt ? dayjs(p.actualPaidAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        approvedBy: p.approvedBy || '-',
        rejectedBy: p.rejectedBy || '-',
        status: statusMapCN[p.status] || p.status,
        remark: p.remark || '',
        reviewRemark: p.reviewRemark || '',
        proofUrl: p.paymentReference
          ? (p.paymentReference.startsWith('http')
              ? p.paymentReference
              : `${window.location.origin}/influencer-api${p.paymentReference}`)
          : '',
      }));
    } catch (e) {
      console.error('导出获取数据失败:', e);
      message.error('获取全量数据失败');
      hideLoading();
      return;
    }
    hideLoading();
  }

  if (exportData.length === 0) {
    message.warning('无可供导出的数据');
    return;
  }

  try {
    const dateRangeText = filterForm.timeRange && filterForm.timeRange.length === 2
      ? `${dayjs(filterForm.timeRange[0]).format('YYYY-MM-DD')} 至 ${dayjs(filterForm.timeRange[1]).format('YYYY-MM-DD')}`
      : undefined;

    await createPaymentExportTaskWithAnalytics({
      data: exportData,
      columns: payoutExportFields,
      filename: `打款记录_${dayjs().format('YYYYMMDD')}`,
      templateId,
      templateName,
      pageType: 'commission-payout',
      dateRange: dateRangeText
    });
    message.success('导出任务已创建');
    exportModalVisible.value = false;
  } catch (e) {
    message.error('导出任务创建失败');
  }
};

const commonStore = useCommonDataStore();
const { allUsers, liaisonTagOptions } = storeToRefs(commonStore);

// Fix #10: Fetch status counts for tab labels
const fetchStatusCounts = async () => {
  try {
    const statusMap: Record<string, string> = {
      'pending': 'pending',
      'approved': 'approved',
      'completed': 'completed',
      'rejected': 'rejected',
    };
    const counts: Record<string, number> = {};
    await Promise.all(
      Object.entries(statusMap).map(async ([key, status]) => {
        try {
          const res = await influencerHttp.getPayouts({ page: 0, size: 1, status });
          counts[key] = res?.data?.totalElements || 0;
        } catch { counts[key] = 0; }
      })
    );
    statusCounts.value = counts;
  } catch (e) {
    console.error('获取状态计数失败:', e);
  }
};

// 页面加载
onMounted(() => {
  loadState();
  Promise.all([commonStore.loadUsers(), commonStore.loadLiaisonTags()]);
  fetchPayouts();
  fetchStatusCounts();
});

</script>

<style lang="scss">
/* Premium Distribute Modal System - Matching CommissionAuditModal.vue */
.premium-distribute-modal {
  .ant-modal-content {
    padding: 0;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  }

  .ant-modal-close-x {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .custom-close-icon {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: scale(1.05);
    }
  }

  .dist-header {
    position: relative;
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    padding: 24px;
    overflow: hidden;
    
    .header-overlay {
      position: absolute;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      background: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCI+PGRlZnM+PHBhdHRlcm4gaWQ9InBhdHRlcm4iIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdHRlcm4gaWQ9ImlubmVyUGF0dGVybiIgd2lkdGg9IjIwIiBoZWlnaHQ9IjIwIiBwYXR0ZXJuVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48Y2lyY2xlIGN4PSIyIiBjeT0iMiIgcj0iMSIgZmlsbD0icmdiYSgyNTUsMjU1LDI1NSwwLjA1KSIvPjwvcGF0dGVybj48cmVjdCB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIGZpbGw9InVybCgjaW5uZXJQYXR0ZXJuKSIvPjwvcGF0dGVybj48L2RlZnM+PHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0idXJsKCNwYXR0ZXJuKSIvPjwvc3ZnPg==');
      opacity: 0.5;
    }

    &.warning-header {
      border-bottom: 4px solid #f43f5e; /* Rose 500 for Interception/Rejection */
      
      .header-overlay {
        background: linear-gradient(135deg, rgba(244, 63, 94, 0.15) 0%, transparent 100%);
      }

      .initial-avatar {
        background: linear-gradient(135deg, #e11d48, #f43f5e);
        box-shadow: 0 4px 12px rgba(244, 63, 94, 0.3);
      }
      
      .warning-icon {
        color: #fb7185 !important;
      }
      
      .warning-tag {
        background: rgba(244, 63, 94, 0.1) !important;
        color: #fb7185 !important;
        border: 1px solid rgba(244, 63, 94, 0.2) !important;
      }
    }
  }

  .influencer-profile {
    position: relative;
    z-index: 1;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .avatar-info {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .initial-avatar {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    color: #fff;
    font-size: 26px;
    font-weight: 700;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    border: 2px solid rgba(255,255,255,0.1);
  }

  .batch-avatar {
    font-size: 30px;
  }

  .name-box {
    display: flex;
    flex-direction: column;
    gap: 4px;
    
    .name {
      font-size: 22px;
      font-weight: 700;
      color: #fff;
      letter-spacing: 0.5px;
    }
    
    .email-row {
      display: flex;
      align-items: center;
      gap: 6px;
      color: rgba(255,255,255,0.7);
      font-size: 13px;
    }
  }

  .header-action-tag {
    background: rgba(255,255,255,0.1);
    backdrop-filter: blur(4px);
    border: 1px solid rgba(255,255,255,0.15);
    color: #fff;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .dist-footer {
     .confirm-btn {
        width: 100%;
        &:hover {
           opacity: 0.9;
        }
     }
  }
}

.premium-textarea-refined {
  border: 1px solid #e2e8f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:focus {
    border-color: #fb7185 !important;
    box-shadow: 0 0 0 4px rgba(251, 113, 133, 0.1) !important;
  }
}

</style>

<style lang="scss" scoped>
.commission-payment-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;

  /* Refined Glass Card */
  .glass-card {
    background: #ffffff;
    border: 1px solid rgba(0, 0, 0, 0.04);
    border-radius: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    }
  }

  .filter-card {
    margin-bottom: 8px !important;
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
      .ant-form-item-control-input {
        min-height: 32px;
      }
    }
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
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
      .ant-card-head-title { padding: 0; }
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
    
    .status-switcher-wrapper {
        flex: 1;
        
        .premium-segmented {
          background: #f1f5f9;
          padding: 4px;
          border-radius: 8px;
          border: none;

          :deep(.ant-radio-button-wrapper) {
            border: none !important;
            background: transparent !important;
            box-shadow: none !important;
            height: 28px;
            line-height: 28px;
            padding: 0 16px;
            font-size: 13px;
            color: #64748b;
            border-radius: 6px !important;
            transition: all 0.3s;
            margin-right: 2px;

            &:hover { color: #1e293b; }
            &.ant-radio-button-wrapper-checked {
              background: #fff !important;
              color: #2563eb !important;
              box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05) !important;
              font-weight: 600;
            }
            
            &::before { display: none !important; }
          }
        }
      }
      
      .toolbar-btns {
         .premium-btn-small {
            height: 28px;
            padding: 0 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
            
            &.ant-btn-danger {
                background: #fef2f2;
                border: 1px solid #fecaca;
                color: #ef4444;
                &:hover { background: #fee2e2; }
            }
         }
      }
  }

  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 500px;

    :deep(.ant-spin-nested-loading),
    :deep(.ant-spin-container),
    :deep(.ant-table),
    :deep(.ant-table-container) {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #ffffff;
    }

    :deep(.ant-table-content) {
      overflow: auto !important;
      &::-webkit-scrollbar { height: 8px; width: 8px; }
      &::-webkit-scrollbar-thumb { 
        background: #e2e8f0; 
        border-radius: 10px; 
        &:hover { background: #cbd5e1; }
      }
      &::-webkit-scrollbar-track { background: rgba(0, 0, 0, 0.02); border-radius: 10px; }
    }
    
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc;
      color: #64748b;
      font-weight: 700;
      font-size: 13px;
      border-bottom: 2px solid #f1f5f9;
      padding: 10px 8px; /* Compact Header */
      &.ant-table-selection-column {
        padding: 0 8px !important;
      }
    }
    
    /* Fixed Column Background Fix - Corrected for Headers */
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left),
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right) {
      background: #f8fafc !important;
      z-index: 3;
    }

    /* Fixed Column Body Background */
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right) {
      background: #fff;
      z-index: 2;
    }

    /* Fix hover state for fixed columns */
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-right) {
      background: #f0f7ff !important;
    }

    :deep(.ant-table-tbody > tr > td) {
      border-bottom: 1px solid #f8fafc;
      padding: 8px 8px; /* Compact Body */
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }
  }

  /* Compact Pagination Footer */
  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    z-index: 10;
    
    .footer-left {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #94a3b8;
      font-size: 12px;
      
      .count-value {
        font-weight: 600;
        color: #1e293b;
        background: #f1f5f9;
        padding: 1px 6px;
        border-radius: 4px;
        
        &.highlight {
            color: #1890ff;
            background: #e6f7ff;
        }
      }
    }
  }
  
  /* Premium Inputs */
  .premium-input, .premium-select :deep(.ant-select-selector), .premium-datepicker, .premium-input-search {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    background: #fafbfc !important;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    height: 32px !important;
    padding: 0 11px !important;
    display: flex;
    align-items: center;

    &:hover {
      border-color: #cbd5e1 !important;
      background: #fff !important;
    }
    
    &:focus, &.ant-select-focused .ant-select-selector {
        border-color: #1890ff !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.06) !important;
        background: #ffffff !important;
    }
  }
  
  :deep(.ant-select-selection-item), :deep(.ant-select-placeholder) {
    line-height: 30px !important;
    font-size: 13px !important;
  }

  .premium-range-group {
      display: flex;
      height: 32px;
      .premium-select :deep(.ant-select-selector) {
        border-top-right-radius: 0 !important;
        border-bottom-right-radius: 0 !important;
        border-right: none !important;
      }
      .premium-datepicker {
        border-top-left-radius: 0 !important;
        border-bottom-left-radius: 0 !important;
        flex: 1;
      }
  }
  
  /* Global Premium Scrollbar */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  ::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 10px;
    transition: background 0.3s;
    &:hover { background: #cbd5e1; }
  }
  ::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.02);
    border-radius: 10px;
  }
  
  /* Premium Buttons */
  .premium-btn, .premium-btn-small {
    border-radius: 6px;
    height: 32px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-weight: 500;
    transition: all 0.3s;
    
    &.primary-gradient {
      background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
      border: none;
      color: #fff;
      box-shadow: 0 2px 6px rgba(29, 78, 216, 0.15);
      
      &:hover {
        box-shadow: 0 4px 10px rgba(29, 78, 216, 0.2);
        transform: translateY(-1px);
      }
    }
  }

  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .action-link {
      font-weight: 600;
      font-size: 13px;
      color: #64748b;
      padding: 0 4px;
      
      &:hover { color: #2563eb; background: rgba(37, 99, 235, 0.05); border-radius: 4px; }
      &.primary { color: #2563eb; }
    }
  }
  
  .status-tag {
     border-radius: 4px; font-weight: 600; font-size: 11px; padding: 0 8px; border: none;
     &.tag-blue { background: #eff6ff; color: #1e40af; }
     &.tag-purple { background: #fdf4ff; color: #a21caf; }
     &.tag-success { background: #f0fdf4; color: #15803d; }
     &.tag-red { background: #fef2f2; color: #b91c1c; }
  }
}
</style>
