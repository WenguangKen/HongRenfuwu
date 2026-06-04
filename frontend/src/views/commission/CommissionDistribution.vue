<template>
  <div class="commission-dist-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联红人">
              <a-input
                v-model:value="filterForm.influencer"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchInfluencer"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchInfluencer" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="电子邮件">
              <a-input
                v-model:value="filterForm.email"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchEmail"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <MailOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchEmail" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联折扣码">
              <a-input
                v-model:value="filterForm.discountCode"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchDiscount"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchDiscount" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="分佣状态">
              <a-select
                v-model:value="filterForm.status"
                placeholder="请选择状态"
                allow-clear
                mode="multiple"
                :max-tag-count="1"
                class="premium-select"
              >
                <a-select-option value="pending">待分佣</a-select-option>
                <a-select-option value="completed">已分佣</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="待分佣金范围">
              <a-input-group compact class="premium-range-group">
                <a-input-number
                  v-model:value="filterForm.pendingAmountMin"
                  placeholder="最小金额"
                  :min="0"
                  style="width: 50%; border-radius: 8px 0 0 8px !important;"
                  class="premium-input"
                />
                <a-input-number
                  v-model:value="filterForm.pendingAmountMax"
                  placeholder="最大金额"
                  :min="0"
                  style="width: 50%; border-radius: 0 8px 8px 0 !important; border-left: 0;"
                  class="premium-input"
                />
              </a-input-group>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="8">
            <a-form-item label="时间范围">
              <a-range-picker
                v-model:value="filterForm.timeRange"
                style="width: 100%"
                :placeholder="['开始时间', '结束时间']"
                format="YYYY-MM-DD"
                :presets="timeRanges"
                class="premium-datepicker"
              />
            </a-form-item>
          </a-col>

          <a-col :span="24" style="display: flex; justify-content: flex-end; align-items: flex-end;">
            <div class="filter-footer" style="padding-top: 0; border-top: none; margin-top: 0;">
              <a-space size="middle">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn" style="height: 32px; padding: 0 20px;">重置</a-button>
              </a-space>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-title">分佣列表</div>
      </template>
      <template #extra>
         <a-space size="small">
           <a-button class="premium-btn-small" v-permission="'commission.dist.export'" @click="handleExport">
             <template #icon><ExportOutlined /></template>
             导出
           </a-button>
           <a-button 
             v-permission="'commission.dist.batch'"
             :disabled="selectedRowKeys.length === 0"
             type="primary" 
             class="premium-btn blue-gradient" 
             @click="handleBatchDistribute"
           >
             <template #icon><SendOutlined /></template>
             批量发起分佣 ({{ selectedRowKeys.length }})
           </a-button>
         </a-space>
      </template>
      <a-table
        :columns="columns"
        :data-source="filteredData"
        :row-key="(record: any) => record.key ?? record.name"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :scroll="{ y: 'calc(100vh - 380px)', x: 'max-content' }"
        :row-selection="rowSelection"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'influencerInfo'">
            <div class="influencer-info-cell">
              <div class="name">{{ record.name }}</div>
              <div class="email">{{ record.email }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'conversionOrderCount'">
            <a-tag color="blue" class="count-tag">{{ record.conversionOrderCount }}</a-tag>
          </template>
          <template v-else-if="column.key === 'discountCodes'">
            <a-popover v-if="record.discountCodes && record.discountCodes.length > 0" placement="bottom">
              <template #content>
                <div style="max-height: 200px; overflow-y: auto; display: flex; flex-direction: column; gap: 4px;">
                  <a-tag v-for="tag in record.discountCodes" :key="tag" color="purple" style="margin: 0;">{{ tag }}</a-tag>
                </div>
              </template>
              <a-tag color="purple" style="cursor: pointer; display: inline-flex; max-width: 120px; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                {{ record.topDiscountCode || record.discountCodes[0] }} <span v-if="record.discountCodes.length > 1" style="margin-left: 4px; font-size: 11px;">({{ record.discountCodes.length }})</span>
              </a-tag>
            </a-popover>
            <span v-else class="empty-placeholder">-</span>
          </template>
          <template v-else-if="column.key === 'settledOrderCount'">
            <a-tag color="green" class="count-tag">{{ record.settledOrderCount }}</a-tag>
          </template>
          <template v-else-if="column.key === 'sampleOrderCount'">
            <a-tag color="orange" class="count-tag">{{ record.sampleOrderCount }}</a-tag>
          </template>
          <template v-else-if="column.key === 'pendingAmount'">
            <span class="amount-value pending">${{ record.pendingAmount }}</span>
          </template>
          <template v-else-if="column.key === 'paidAmount'">
            <span class="amount-value paid">${{ record.paidAmount }}</span>
          </template>
          <template v-else-if="column.key === 'payoutCount'">
            <a-tag v-if="record.payoutCount > 0" color="purple" class="count-tag" style="cursor: pointer" @click="handleViewPayouts(record)">
              {{ record.payoutCount }}
            </a-tag>
            <span v-else style="color: #999">0</span>
          </template>
          <template v-else-if="column.key === 'totalAmount'">
            <span class="amount-value total">${{ record.totalAmount }}</span>
          </template>
          <template v-else-if="column.key === 'commissionTimes'">
            <div class="commission-times-cell">
              <div class="time-row">
                <span class="time-label distribute" title="最近一次订单结转佣金的时间">最后结佣</span>
                <span class="time-value" :class="{ 'empty-placeholder': record.lastSettledTime === '-' }">{{ record.lastSettledTime }}</span>
              </div>
              <div class="time-row" style="margin-top: 4px;">
                <span class="time-label initiate" title="最近一次打款发放的时间">最后打款</span>
                <span class="time-value" :class="{ 'empty-placeholder': record.lastDistributeTime === '-' }">{{ record.lastDistributeTime }}</span>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <a-button type="link" size="small" @click="handleViewLog(record)" class="action-btn-refined" v-permission="'commission.dist.log'">
                <template #icon><HistoryOutlined /></template>日志
              </a-button>
              <a-button type="link" size="small" @click="handleDistribute(record)" :disabled="record.pendingAmount <= 0" class="action-btn-refined primary" v-permission="'commission.dist.create'">
                <template #icon><TransactionOutlined /></template>发起分佣
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前记录数量 <span class="count-value">{{ pagination.total }}</span></span>
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

    <DistributeModal
      v-model:open="distributeModalVisible"
      :form="distForm"
      :pending-amount="currentRecord?.pendingAmount"
      :influencer-name="currentRecord?.name || distForm.influencerName"
      :influencer-email="currentRecord?.email"
      @update:form="onUpdateDistForm"
      @ok="handleDistributeOk"
    />

    <CommissionLogModal
      v-model:open="logModalVisible"
      :columns="logColumns"
      :data-source="logData"
    />

    <MissingPaymentModal
      v-model:open="missingPaymentVisible"
      :influencers="missingPaymentInfluencers"
    />

    <!-- Export Modal -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="commissionExportFields"
      page-type="commission-distribution"
      :current-user-id="currentUserId"
      @export="handleExportFromModal"
    />
    <!-- 批量搜索红人昵称弹窗 -->
    <a-modal
      v-model:open="batchSearchInfluencerVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索红人昵称</div>
            <div class="ic-header-subtitle">请输入红人昵称，多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchInfluencerVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchInfluencerValue"
          placeholder="例如：&#10;Alice&#10;Bob"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchInfluencerVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchInfluencer" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量搜索邮箱弹窗 -->
    <a-modal
      v-model:open="batchSearchEmailVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <MailOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索邮箱</div>
            <div class="ic-header-subtitle">请输入邮箱地址，多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchEmailVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchEmailValue"
          placeholder="例如：&#10;alice@example.com&#10;bob@example.com"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchEmailVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchEmail" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量搜索折扣码弹窗 -->
    <a-modal
      v-model:open="batchSearchDiscountVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索折扣码</div>
            <div class="ic-header-subtitle">请输入折扣码，多个请用换行或逗号分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchDiscountVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchDiscountValue"
          placeholder="例如：&#10;SUMMER20&#10;WINTER30"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchDiscountVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchDiscount" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 汇佣金次数明细弹窗 -->
    <a-modal
      v-model:open="payoutDetailVisible"
      :title="null"
      :footer="null"
      class="premium-refined-modal influencer-create-modal"
      :closable="false"
      width="780px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); box-shadow: 0 4px 12px rgba(124, 58, 237, 0.25);">
            <TransactionOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">
              打款记录
              <span class="order-no-tags">
                <a-tag>{{ payoutDetailName }}</a-tag>
              </span>
            </div>
            <div class="ic-header-subtitle">
              已完成的打款历史明细及审核状态
            </div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="payoutDetailVisible = false">
          <CloseOutlined />
        </a-button>
      </div>
      <div class="ic-modal-body">
        <div class="premium-table-container">
          <a-table
            :columns="payoutDetailColumns"
            :data-source="payoutDetailData"
            :loading="payoutDetailLoading"
            :pagination="{ pageSize: 20, showSizeChanger: true, showQuickJumper: true }"
            size="middle"
            row-key="id"
            class="ultra-premium-table"
            :scroll="{ y: 350 }"
          >
            <template #bodyCell="{ column, record, text }">
              <template v-if="column.key === 'status'">
                <a-tag v-if="text === 'completed'" color="success" class="status-pill status-success">
                  <CheckCircleOutlined /> 已完成
                </a-tag>
                <a-tag v-else-if="text === 'rejected'" color="error" class="status-pill status-error">
                  <CloseCircleOutlined /> 已拒绝
                </a-tag>
                <a-tag v-else-if="text === 'approved'" color="processing" class="status-pill status-processing">
                  <ClockCircleOutlined /> 待打款
                </a-tag>
                <a-tag v-else color="warning" class="status-pill status-warning">
                  <SyncOutlined spin /> 处理中
                </a-tag>
              </template>
              <template v-else-if="column.key === 'createdBy'">
                <div class="user-info-cell" v-if="text && text !== '-'">
                  <a-avatar size="small" class="mini-avatar" :style="{ backgroundColor: getCreatorColor(text) }">{{ text.charAt(0).toUpperCase() }}</a-avatar>
                  <span class="name">{{ text }}</span>
                </div>
                <span v-else class="empty-placeholder">-</span>
              </template>
              <template v-else-if="column.key === 'approvedBy'">
                <div class="user-info-cell" v-if="text && text !== '-'">
                  <a-avatar size="small" class="mini-avatar" :style="{ backgroundColor: getCreatorColor(text) }">{{ text.charAt(0).toUpperCase() }}</a-avatar>
                  <span class="name">{{ text }}</span>
                </div>
                <span v-else class="empty-placeholder">-</span>
              </template>
              <template v-else-if="column.key === 'amount'">
                <span class="amount-value">{{ text }}</span>
              </template>
              <template v-else-if="column.key === 'paidAt'">
                <span class="time-value">{{ text }}</span>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button type="link" size="small" @click="handleViewProofDistribution(record)">
                  <template #icon><EyeOutlined /></template> 查看
                </a-button>
              </template>
            </template>
          </a-table>
        </div>
      </div>
    </a-modal>

    <CommissionProofModal
      v-model:open="proofModalVisible"
      :proof="currentProof"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, h, onMounted, watch } from 'vue';
import { LoadingOutlined, HistoryOutlined, TransactionOutlined, SendOutlined, ExportOutlined, DatabaseOutlined, MailOutlined, CloseOutlined, CheckCircleOutlined, CloseCircleOutlined, ClockCircleOutlined, SyncOutlined, EyeOutlined } from '@ant-design/icons-vue';
import { Modal, message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';
import DistributeModal from '@/components/commission/DistributeModal.vue';
import CommissionLogModal from '@/components/commission/CommissionLogModal.vue';
import MissingPaymentModal from '@/components/commission/MissingPaymentModal.vue';
import CommissionProofModal from '@/components/commission/CommissionProofModal.vue';
import { influencerService as influencerHttp } from '@/services/influencerService';
import { createExportTask } from '@/utils/exportTaskHelper';
import { fetchAllPages } from '@/utils/dataHelper';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';

const filterExpanded = ref(false);
const loading = ref(false);
const distributeModalVisible = ref(false);
const logModalVisible = ref(false);
const missingPaymentVisible = ref(false);
const proofModalVisible = ref(false);
const currentProof = ref<any>(null);
const missingPaymentInfluencers = ref<string[]>([]);
const currentRecord = ref<any>(null);
const exportModalVisible = ref(false);
const currentUserId = ref<string>('default_user');

const handleViewProofDistribution = (record: any) => {
  currentProof.value = {
    amount: record.amount,
    proof: record.paymentReference,
    rawData: record,
    payId: record.paymentId,
    payTime: record.paidAt
  };
  proofModalVisible.value = true;
};

// 获取当前用户ID
const initCurrentUserId = () => {
  try {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const userInfoObj = JSON.parse(userInfo);
      currentUserId.value = (userInfoObj.id || userInfoObj.userId || 'default_user') as string;
    }
  } catch (e) {
    console.error('Failed to get current user id', e);
  }
};

// Custom loading indicator for table
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 20,
  showSizeChanger: true,
  showQuickJumper: true,
});

const filterForm = reactive({
  influencer: '',
  email: '',
  discountCode: '',
  status: undefined as string[] | undefined,
  pendingAmountMin: undefined as number | undefined,
  pendingAmountMax: undefined as number | undefined,
  timeRange: undefined as any,
});

const selectedRowKeys = ref<any[]>([]);
const selectedRows = ref<any[]>([]);

const onSelectChange = (keys: any[], rows: any[]) => {
  selectedRowKeys.value = keys;
  selectedRows.value = rows;
};

const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: onSelectChange,
  getCheckboxProps: (record: any) => ({
    disabled: record.pendingAmount <= 0,
  }),
}));

const timeRanges = [
  { label: '最近7天', value: [dayjs().subtract(6, 'day'), dayjs()] },
  { label: '最近14天', value: [dayjs().subtract(13, 'day'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(29, 'day'), dayjs()] },
  { label: '最近90天', value: [dayjs().subtract(89, 'day'), dayjs()] },
];

const distForm = reactive({
  influencerName: '',
  amount: 0,
  remark: ''
});

const onUpdateDistForm = (val: any) => {
  Object.assign(distForm, val || {});
};

const columns: TableColumnsType = [
  {
    title: '红人信息',
    key: 'influencerInfo',
    width: 200,
    fixed: 'left',
  },
  {
    title: '转化订单',
    dataIndex: 'conversionOrderCount',
    key: 'conversionOrderCount',
    width: 100,
    sorter: (a: any, b: any) => a.conversionOrderCount - b.conversionOrderCount,
    align: 'center',
  },
  {
    title: '关联折扣码',
    key: 'discountCodes',
    width: 140,
    align: 'center',
  },
  {
    title: '已分佣订单',
    dataIndex: 'settledOrderCount',
    key: 'settledOrderCount',
    width: 110,
    sorter: (a: any, b: any) => a.settledOrderCount - b.settledOrderCount,
    align: 'center',
  },
  {
    title: '样品单数',
    dataIndex: 'sampleOrderCount',
    key: 'sampleOrderCount',
    width: 100,
    sorter: (a: any, b: any) => a.sampleOrderCount - b.sampleOrderCount,
    align: 'center',
  },
  {
    title: '待分佣金',
    key: 'pendingAmount',
    width: 120,
    sorter: (a: any, b: any) => a.pendingAmount - b.pendingAmount,
  },
  {
    title: '已分佣金',
    key: 'paidAmount',
    width: 120,
    sorter: (a: any, b: any) => a.paidAmount - b.paidAmount,
  },
  {
    title: '汇佣金次数',
    key: 'payoutCount',
    width: 110,
    sorter: (a: any, b: any) => a.payoutCount - b.payoutCount,
    align: 'center',
  },
  {
    title: '总佣金',
    key: 'totalAmount',
    width: 120,
    sorter: (a: any, b: any) => a.totalAmount - b.totalAmount,
  },
  {
    title: '分佣时间',
    key: 'commissionTimes',
    width: 230,
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right',
  },
];

const logColumns: TableColumnsType = [
  {
    title: '分佣时间',
    dataIndex: 'time',
    key: 'time',
    width: 170,
  },
  {
    title: '发起人',
    key: 'operator',
    width: 110,
  },
  {
    title: '分佣金额',
    key: 'amount',
    width: 110,
  },
  {
    title: '审核人',
    key: 'reviewer',
    width: 110,
  },
  {
    title: '状态',
    key: 'status',
    width: 90,
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
  },
];

// 红人余额数据 (从API获取)
interface BalanceRow {
  key: number;
  influencerId: number;
  name: string;
  email: string;
  pendingAmount: number;
  paidAmount: number;
  totalAmount: number;
  conversionOrderCount: number;
  settledOrderCount: number;
  sampleOrderCount: number;
  payoutCount: number;
  lastDistributeTime: string;
  lastInitiatedTime: string;
  lastSettledTime: string;
  topDiscountCode?: string;
  discountCodes?: string[];
}

const data = ref<BalanceRow[]>([]);

// 获取红人余额列表
const fetchBalances = async () => {
  loading.value = true;
  try {
    const params: any = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      influencer: filterForm.influencer,
      email: filterForm.email,
      discountCode: filterForm.discountCode,
    };
    // 结算状态筛选
    if (filterForm.status && filterForm.status.length > 0) {
      params.status = filterForm.status.join(',');
    }
    // 待结算金额范围
    if (filterForm.pendingAmountMin !== undefined) params.pendingAmountMin = filterForm.pendingAmountMin;
    if (filterForm.pendingAmountMax !== undefined) params.pendingAmountMax = filterForm.pendingAmountMax;
    // 时间范围
    if (filterForm.timeRange && filterForm.timeRange.length === 2) {
      params.startTime = dayjs(filterForm.timeRange[0]).format('YYYY-MM-DD 00:00:00');
      params.endTime = dayjs(filterForm.timeRange[1]).format('YYYY-MM-DD 23:59:59');
    }
    const response = await influencerHttp.getBalances(params);
    const resData = response;
    if (resData && resData.success && resData.data) {
      const balances = resData.data.content || [];
      data.value = balances.map((b: any, idx: number): BalanceRow => ({
        key: b.influencerId || idx,
        influencerId: b.influencerId,
        name: b.influencerName || '-',
        email: b.influencerEmail || '-',
        pendingAmount: parseFloat((b.pendingAmount || 0).toFixed(2)),
        paidAmount: parseFloat((b.paidAmount || 0).toFixed(2)),
        totalAmount: parseFloat((b.totalAmount || 0).toFixed(2)),
        conversionOrderCount: b.conversionOrderCount || 0,
        settledOrderCount: b.settledOrderCount || 0,
        sampleOrderCount: b.sampleOrderCount || 0,
        payoutCount: b.payoutCount || 0,
        lastDistributeTime: b.lastDistributeTime ? dayjs(b.lastDistributeTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        lastInitiatedTime: b.lastInitiatedTime ? dayjs(b.lastInitiatedTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        lastSettledTime: b.lastSettledTime ? dayjs(b.lastSettledTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        topDiscountCode: b.topDiscountCode,
        discountCodes: b.discountCodes
      }));
      pagination.total = resData.data.totalElements || 0;
    }
  } catch (error: any) {
    if (error?.code === 'ERR_CANCELED') return;
    console.error('获取余额列表失败:', error);
    message.error('获取分佣列表失败');
  } finally {
    loading.value = false;
  }
};

const logData = ref<Array<{
  key: number;
  time: string;
  amount: number;
  status: string;
  remark: string;
  operator: string;
  reviewer: string;
}>>([]);

const filteredData = computed(() => {
  let result = [...data.value];
  
  // Only apply filters NOT handled by backend API (influencer/email/discountCode are sent to backend)
  if (filterForm.status && filterForm.status.length > 0) {
    result = result.filter(item => {
      const isPending = item.pendingAmount > 0;
      const isCompleted = item.pendingAmount === 0;
      return filterForm.status!.some(s => {
        if (s === 'pending') return isPending;
        if (s === 'completed') return isCompleted;
        return false;
      });
    });
  }
  if (filterForm.pendingAmountMin !== undefined) {
    result = result.filter(item => item.pendingAmount >= filterForm.pendingAmountMin!);
  }
  if (filterForm.pendingAmountMax !== undefined) {
    result = result.filter(item => item.pendingAmount <= filterForm.pendingAmountMax!);
  }
  
  return result;
});

const handleTableChange = (paginationOrPage: any, filtersOrPageSize: any, sorter?: any) => {
  if (typeof paginationOrPage === 'number') {
    // 来源于 a-pagination 的 change 事件 (page, pageSize)
    pagination.current = paginationOrPage;
    pagination.pageSize = filtersOrPageSize;
    fetchBalances();
  } else {
    // 来源于 a-table 的 change 事件 (pagination, filters, sorter)
    // 主要是点击表头排序时触发，前端本地排序或者由后端在此处理
    // 避免 [object Object] 覆盖 pagination.current 导致 NaN
  }
};

const handleResetFilter = () => {
  filterForm.influencer = '';
  filterForm.email = '';
  filterForm.discountCode = '';
  filterForm.status = undefined;
  filterForm.pendingAmountMin = undefined;
  filterForm.pendingAmountMax = undefined;
  filterForm.timeRange = undefined;
  handleFilter();
};

// 批量搜索红人昵称
const batchSearchInfluencerVisible = ref(false);
const batchSearchInfluencerValue = ref('');

const openBatchSearchInfluencer = () => {
  batchSearchInfluencerValue.value = filterForm.influencer?.replace(/,/g, '\n') || '';
  batchSearchInfluencerVisible.value = true;
};

const handleBatchSearchInfluencer = () => {
  const values = batchSearchInfluencerValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.influencer = values.join(',');
  batchSearchInfluencerVisible.value = false;
  handleFilter();
};

// Removed duplicate block here

// 汇佣金次数明细弹窗
const payoutDetailVisible = ref(false);
const payoutDetailData = ref<any[]>([]);
const payoutDetailLoading = ref(false);
const payoutDetailName = ref('');

const payoutDetailColumns = [
  { title: '打款时间', dataIndex: 'paidAt', key: 'paidAt', width: 170 },
  { title: '操作人', dataIndex: 'createdBy', key: 'createdBy', width: 130 },
  { title: '审核人', dataIndex: 'approvedBy', key: 'approvedBy', width: 130 },
  { title: '打款金额', dataIndex: 'amount', key: 'amount', width: 100, align: 'right' as const },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100, align: 'right' as const },
  { title: '凭证', key: 'action', width: 100, align: 'center' as const },
];

const handleViewPayouts = async (record: BalanceRow) => {
  payoutDetailName.value = record.name;
  payoutDetailVisible.value = true;
  payoutDetailLoading.value = true;
  try {
    const res = await influencerHttp.getPayouts({ influencerId: record.influencerId });
    const list = res?.data || res || [];
    payoutDetailData.value = (Array.isArray(list) ? list : []).filter((p: any) => p.status === 'completed').map((p: any) => ({
      ...p,
      paidAt: p.paidAt ? dayjs(p.paidAt).format('YYYY-MM-DD HH:mm:ss') : '-',
      amount: `$${parseFloat((p.amount || 0).toFixed(2))}`,
      createdBy: p.createdBy || '-',
      approvedBy: p.approvedBy || '-',
    }));
  } catch (e) {
    payoutDetailData.value = [];
  } finally {
    payoutDetailLoading.value = false;
  }
};

// 批量搜索邮箱
const batchSearchEmailVisible = ref(false);
const batchSearchEmailValue = ref('');

const openBatchSearchEmail = () => {
  batchSearchEmailValue.value = filterForm.email?.replace(/,/g, '\n') || '';
  batchSearchEmailVisible.value = true;
};

const handleBatchSearchEmail = () => {
  const values = batchSearchEmailValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.email = values.join(',');
  batchSearchEmailVisible.value = false;
  handleFilter();
};

// 批量搜索折扣码
const batchSearchDiscountVisible = ref(false);
const batchSearchDiscountValue = ref('');

const openBatchSearchDiscount = () => {
  batchSearchDiscountValue.value = filterForm.discountCode?.replace(/,/g, '\n') || '';
  batchSearchDiscountVisible.value = true;
};

const handleBatchSearchDiscount = () => {
  const values = batchSearchDiscountValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.discountCode = values.join(',');
  batchSearchDiscountVisible.value = false;
  handleFilter();
};

const handleFilter = () => {
  fetchBalances();
};

const handleDistribute = async (record: any) => {
  // Check payment info first
  try {
    const paymentRes = await influencerHttp.getPaymentInfo(record.influencerId);
    const pInfo = paymentRes.data || paymentRes || {};
    
    // Check if either bank card or paypal exists
    const hasBank = !!(pInfo.bankName || pInfo.accountNumber);
    const hasPaypal = !!pInfo.paypalAccount;
    
    if (!hasBank && !hasPaypal) {
      missingPaymentInfluencers.value = [record.name];
      missingPaymentVisible.value = true;
      return;
    }
  } catch (error) {
    console.error('Failed to verify payment info', error);
  }

  currentRecord.value = record;
  distForm.influencerName = record.name;
  distForm.amount = record.pendingAmount;
  distForm.remark = '';
  distributeModalVisible.value = true;
};

const getCreatorColor = (name: string) => {
  const colors = ['#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981'];
  if (!name || name === 'System' || name === '-') return colors[0];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

const handleBatchDistribute = async () => {
  if (selectedRows.value.length === 0) return;
  
  // Create a loading modal during the check
  const hideLoading = message.loading('正在校验收款信息...', 0);
  
  // Find influencers without payment info
  const missingPaymentUsers = [];
  try {
    for (const row of selectedRows.value) {
      const paymentRes = await influencerHttp.getPaymentInfo(row.influencerId);
      const pInfo = paymentRes.data || paymentRes || {};
      const hasBank = !!(pInfo.bankName || pInfo.accountNumber);
      const hasPaypal = !!pInfo.paypalAccount;
      if (!hasBank && !hasPaypal) {
        missingPaymentUsers.push(row.name);
      }
    }
  } catch (error) {
    console.error('Failed to verify payment info during batch', error);
  } finally {
    hideLoading();
  }
  
  if (missingPaymentUsers.length > 0) {
    missingPaymentInfluencers.value = missingPaymentUsers;
    missingPaymentVisible.value = true;
    return;
  }
  
  Modal.confirm({
    title: '批量发起分佣确认',
    content: `确认要为选中的 ${selectedRows.value.length} 名红人发起全额分佣吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const requests = selectedRows.value.map(row => ({
          influencerId: row.influencerId,
          amount: row.pendingAmount,
          remark: '批量自动结算'
        }));
        
        await influencerHttp.batchCreatePayout(requests);
        message.success(`成功为 ${requests.length} 名红人发起分佣`);
        selectedRowKeys.value = [];
        selectedRows.value = [];
        fetchBalances();
      } catch (error: any) {
        message.error('批量操作失败: ' + (error.message || '未知错误'));
      }
    }
  });
};

const handleDistributeOk = async () => {
  if (distForm.amount <= 0) {
    message.warning('请输入有效的金额');
    return;
  }
  if (distForm.amount > currentRecord.value?.pendingAmount) {
    message.warning('打款金额不能超过待分佣金');
    return;
  }
  
  try {
    const response = await influencerHttp.createPayout({
      influencerId: currentRecord.value?.influencerId,
      amount: distForm.amount,
      remark: distForm.remark
    });
    const resData = response;
    if (resData && resData.success) {
      distributeModalVisible.value = false;
      message.success('发起打款成功，请前往打款列表查看');
      fetchBalances(); // 刷新列表
    } else {
      message.error(resData?.message || '发起打款失败');
    }
  } catch (error) {
    console.error('发起打款失败:', error);
    message.error('发起打款失败');
  }
};

const handleViewLog = async (record: any) => {
  try {
    const response = await influencerHttp.getPayouts({ influencerId: record.influencerId });
    const resData = response;
    if (resData && resData.success && resData.data) {
      logData.value = resData.data.map((p: any, idx: number) => ({
        key: p.id || idx,
        time: p.createdAt ? dayjs(p.createdAt).format('YYYY-MM-DD HH:mm:ss') : '',
        amount: parseFloat(p.amount || 0),
        status: p.status === 'completed' ? '已打款' : p.status === 'pending' ? '待审核' : p.status,
        remark: p.remark || '',
        operator: p.createdBy || 'System',
        reviewer: p.approvedBy || '-'
      }));
    }
  } catch (error) {
    console.error('获取打款日志失败:', error);
    // 显示空日志
    logData.value = [];
  }
  logModalVisible.value = true;
};

// 导出相关字段定义
const commissionExportFields = [
  { key: 'name', title: '红人姓名' },
  { key: 'email', title: '邮箱' },
  { key: 'totalOrders', title: '转化订单数' },
  { key: 'pendingOrdersCount', title: '待分分佣订单' },
  { key: 'pendingItemsCount', title: '待分单品数' },
  { key: 'pendingAmount', title: '待分佣金' },
  { key: 'paidAmount', title: '已分佣金' },
  { key: 'totalAmount', title: '累计佣金' },
  { key: 'lastDistributeTime', title: '最近分佣时间' },
];

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async (payload: { scope: 'all' | 'selected'; fields: string[]; columns: any[]; templateId?: string; templateName?: string }) => {
  const { scope, columns, templateId, templateName } = payload;
  let exportData: BalanceRow[] = [];

  if (scope === 'selected') {
    exportData = data.value.filter(item => selectedRowKeys.value.includes(item.key));
  } else {
    // 导出全部 - 获取全量数据
    const hideLoading = message.loading('正在获取全量导出数据...', 0);
    try {
      const allRawData = await fetchAllPages(
        (p) => influencerHttp.getBalances({
          page: p.page || 0,
          size: p.size || 100,
          influencer: filterForm.influencer,
          email: filterForm.email,
          discountCode: filterForm.discountCode,
        }),
        {}
      );
      
      // 转换为 BalanceRow 格式
      exportData = allRawData.map((b: any, idx: number): BalanceRow => ({
        key: b.influencerId || idx,
        influencerId: b.influencerId,
        name: b.influencerName || '-',
        email: b.influencerEmail || '-',
        pendingAmount: parseFloat((b.pendingAmount || 0).toFixed(2)),
        paidAmount: parseFloat((b.paidAmount || 0).toFixed(2)),
        totalAmount: parseFloat((b.totalAmount || 0).toFixed(2)),
        conversionOrderCount: b.conversionOrderCount || 0,
        settledOrderCount: b.settledOrderCount || 0,
        sampleOrderCount: b.sampleOrderCount || 0,
        payoutCount: b.payoutCount || 0,
        lastDistributeTime: b.lastDistributeTime ? dayjs(b.lastDistributeTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        lastInitiatedTime: b.lastInitiatedTime ? dayjs(b.lastInitiatedTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        lastSettledTime: b.lastSettledTime ? dayjs(b.lastSettledTime).format('YYYY-MM-DD HH:mm:ss') : '-',
      }));
    } catch (error) {
      console.error('获取全量数据失败:', error);
      message.error('数据准备失败，请重试');
      hideLoading();
      return;
    }
    hideLoading();
  }

  // 应用仅前端过滤的字段 (status, pendingAmountMin/Max)
  if (scope === 'all') {
    if (filterForm.status && filterForm.status.length > 0) {
      exportData = exportData.filter(item => {
        const isPending = item.pendingAmount > 0;
        const isCompleted = item.pendingAmount === 0;
        return filterForm.status!.some(s => {
          if (s === 'pending') return isPending;
          if (s === 'completed') return isCompleted;
          return false;
        });
      });
    }
    if (filterForm.pendingAmountMin !== undefined) {
      exportData = exportData.filter(item => item.pendingAmount >= filterForm.pendingAmountMin!);
    }
    if (filterForm.pendingAmountMax !== undefined) {
      exportData = exportData.filter(item => item.pendingAmount <= filterForm.pendingAmountMax!);
    }
  }

  if (exportData.length === 0) {
    message.warning('没有可导出的数据');
    return;
  }

  createExportTask({
    data: exportData,
    columns: columns,
    filename: `佣金结算列表_${dayjs().format('YYYYMMDD_HHmmss')}`,
    pageType: 'commission-distribution',
    templateId: templateId,
    templateName: templateName
  });
};

// 页面加载时获取数据
onMounted(() => {
  initCurrentUserId();
  fetchBalances();
});


</script>

<style lang="scss" scoped>
.commission-dist-page {
  height: 100%;
  padding: 12px;
  background: #f1f5f9;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: hidden;

  .glass-card {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.05), 0 8px 10px -6px rgba(0, 0, 0, 0.05);
    }
  }

  .filter-card {
    flex-shrink: 0;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(12px);
    
    :deep(.ant-form-item) {
      margin-bottom: 0;
      .ant-form-item-label > label {
        font-size: 11px;
        font-weight: 700;
        color: #475569;
        text-transform: uppercase;
        letter-spacing: 0.025em;
        margin-bottom: 4px;
      }
    }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;

    :deep(.ant-card-head) {
      border-bottom: 1px solid #f1f5f9;
      padding: 0 20px;
      min-height: 56px;
      background: linear-gradient(to right, #ffffff, #fafafa);
      
      .table-title {
        font-size: 16px;
        font-weight: 800;
        color: #0f172a;
        letter-spacing: -0.01em;
      }
    }
    
    :deep(.ant-card-body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }
  }

  .premium-table {
    flex: 1;
    overflow: hidden;

    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc;
      color: #64748b;
      font-weight: 700;
      font-size: 12px;
      padding: 14px 12px;
      border-bottom: 2px solid #f1f5f9;
      text-transform: uppercase;
      letter-spacing: 0.05em;

      &::before { display: none !important; } // Remove default dividers
      
      &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
        background: #f8fafc !important;
      }
    }

    :deep(.ant-table-tbody > tr > td) {
      padding: 12px 12px;
      border-bottom: 1px solid #f8fafc;
      transition: background 0.2s ease;
      
      &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
        background: #fff;
      }
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f9ff !important;
      &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
        background: #f0f9ff !important;
      }
    }
  }

  .influencer-info-cell {
    display: flex;
    flex-direction: column;
    gap: 2px;
    
    .name {
      font-weight: 700;
      color: #1e293b;
      font-size: 13.5px;
      line-height: 1.4;
    }
    .email {
      font-size: 11px;
      color: #94a3b8;
      font-family: 'JetBrains Mono', monospace;
      font-weight: 500;
    }
  }

  .amount-value {
    font-weight: 700;
    font-family: 'JetBrains Mono', monospace;
    font-size: 14px;
    padding: 2px 6px;
    border-radius: 4px;
    
    &.pending { color: #d97706; background: #fffbeb; }
    &.paid { color: #059669; background: #ecfdf5; }
    &.total { color: #2563eb; background: #eff6ff; }
  }

  .count-tag {
    font-weight: 700;
    border-radius: 20px;
    padding: 0 12px;
    height: 22px;
    line-height: 20px;
    border: 1px solid currentColor;
    background: transparent;
    font-family: 'JetBrains Mono', monospace;
    font-size: 12px;
    opacity: 0.9;
    
    &.ant-tag-blue { color: #3b82f6; border-color: #bfdbfe; background: #eff6ff; }
    &.ant-tag-green { color: #10b981; border-color: #bbf7d0; background: #f0fdf4; }
    &.ant-tag-orange { color: #f59e0b; border-color: #fed7aa; background: #fffbeb; }
  }

  .commission-times-cell {
    display: flex;
    flex-direction: column;
    justify-content: center;

    .time-row {
      display: flex;
      align-items: center;
      gap: 8px;

      .time-label {
        font-size: 11px;
        padding: 1px 6px;
        border-radius: 4px;
        font-weight: 600;
        white-space: nowrap;

        &.distribute {
          background: #ecfdf5;
          color: #059669;
          border: 1px solid #a7f3d0;
        }

        &.initiate {
          background: #eff6ff;
          color: #2563eb;
          border: 1px solid #bfdbfe;
        }
      }

      .time-value {
        color: #475569;
        font-family: 'JetBrains Mono', monospace;
        font-size: 13px;
        white-space: nowrap;
      }
    }
  }

  .time-value {
    color: #64748b;
    font-size: 11.5px;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 500;
  }

  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .action-btn-refined {
      color: #64748b;
      font-weight: 700;
      font-size: 12px;
      height: 28px;
      padding: 0 8px;
      border-radius: 6px;
      display: flex;
      align-items: center;
      gap: 4px;
      transition: all 0.2s;
      
      &:hover { 
        color: #0f172a; 
        background: #f1f5f9; 
      }
      
      &.primary { 
        color: #2563eb; 
        background: #eff6ff;
        &:hover {
           background: #dbeafe;
           transform: translateY(-1px);
        }
      }

      :deep(.anticon) { font-size: 14px; }
    }
  }

  .pagination-footer {
    padding: 12px 20px;
    background: #fff;
    border-top: 1px solid #f1f5f9;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .footer-left .count-value {
      background: #f1f5f9;
      color: #475569;
      font-weight: 700;
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  /* Premium Inputs */
  .premium-input, .premium-select :deep(.ant-select-selector), .premium-datepicker {
    border-radius: 8px !important;
    border: 1px solid #e2e8f0 !important;
    background: #ffffff !important;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05) !important;
    
    &:hover { border-color: #cbd5e1 !important; }
    &:focus, &.ant-select-focused .ant-select-selector {
      border-color: #3b82f6 !important;
      box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1) !important;
    }
  }

  .premium-btn {
    border-radius: 8px;
    font-weight: 700;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    
    &.primary-gradient {
      background: #0f172a;
      border: none;
      color: #fff;
      &:hover { background: #1e293b; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(15, 23, 42, 0.2); }
      &:active { transform: translateY(0); }
    }

    &.blue-gradient {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      color: #fff;
      &:hover { background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%); transform: translateY(-1px); box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }
      &:disabled { background: #cbd5e1; color: #94a3b8; transform: none; box-shadow: none; }
    }
  }
}

/* Refined Premium Modal Styles (Matching InfluencerDetailModal) */
:deep(.premium-refined-modal) {
    .ant-modal-content {
      padding: 0 !important;
      border-radius: 24px;
      overflow: hidden !important;
      display: flex;
      flex-direction: column;
      box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
      @supports (backdrop-filter: blur(20px)) {
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(20px);
      }
    }

    .ant-modal-header {
      padding: 0 !important;
      margin: 0 !important;
      border-bottom: none;
      background: transparent;
      flex-shrink: 0;
    }
  }

     .modal-body-container {
    padding: 32px;
    background: transparent;
    
    .premium-table-container {
      border: 1px solid #e2e8f0;
      border-radius: 16px;
      overflow: hidden;
      background: #ffffff;
      box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
    }
    
    :deep(.ultra-premium-table) {
      .ant-table-thead > tr > th {
        background: #f8fafc;
        color: #475569;
        font-weight: 700;
        border-bottom: 1px solid #e2e8f0;
        padding: 16px 20px;
        font-size: 13px;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        &:before { display: none; }
      }
      
      .ant-table-tbody > tr > td {
        padding: 16px 20px;
        border-bottom: 1px solid #f1f5f9;
        color: #334155;
      }
      
      .ant-table-tbody > tr:hover > td {
        background: #f8fafc !important;
      }
      
      .ant-table-tbody > tr:last-child > td {
        border-bottom: none;
      }
    }
    
    .status-pill {
      border-radius: 20px;
      padding: 4px 12px;
      font-weight: 600;
      font-size: 13px;
      display: inline-flex;
      align-items: center;
      gap: 6px;
      border: none;
      
      &.status-success { background: #dcfce7; color: #166534; }
      &.status-error { background: #fee2e2; color: #991b1b; }
      &.status-processing { background: #e0e7ff; color: #3730a3; }
      &.status-warning { background: #fef9c3; color: #854d0e; }
    }
    
    .operator-cell {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .operator-icon {
        width: 28px;
        height: 28px;
        border-radius: 8px;
        background: #f1f5f9;
        color: #64748b;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        
        &.approve {
          background: #eef2ff;
          color: #4f46e5;
        }
      }
      
      .operator-name {
        font-weight: 600;
        color: #334155;
      }
    }
    
    .amount-value {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
      font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }
    
    .time-value {
      color: #64748b;
      font-family: monospace;
      font-size: 14px;
    }
    
    .empty-placeholder {
      color: #cbd5e1;
      font-weight: 500;
      width: 24px;
      display: inline-block;
      text-align: center;
    }
  }
</style>

<style lang="less">
/* Batch Search Modal Styles */
.influencer-create-modal {
  .ant-modal-content {
    padding: 0 !important;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .ic-modal-header {
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
    padding: 16px 24px !important;
    background: #ffffff !important;
    border-bottom: 1px solid #f1f5f9 !important;
    
    &.glass-header {
       background: rgba(255, 255, 255, 0.95) !important;
    }
    
    .ic-header-left {
      display: flex !important;
      align-items: center !important;
      gap: 16px !important;
    }
    
    .ic-header-icon-wrapper {
      flex-shrink: 0 !important;
      width: 40px !important;
      height: 40px !important;
      border-radius: 10px !important;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
      color: #ffffff !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      font-size: 18px !important;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2) !important;
    }
    
    .ic-header-text {
      flex: 1 !important;
      .ic-header-title {
        font-size: 18px !important;
        font-weight: 700 !important;
        color: #1e293b !important;
        line-height: 1.2 !important;
        margin: 0 !important;
      }
      .ic-header-subtitle {
        font-size: 12px !important;
        color: #94a3b8 !important;
        margin-top: 2px !important;
      }
    }
    
    .close-btn {
      flex-shrink: 0 !important;
      color: #94a3b8 !important;
      &:hover {
        color: #64748b !important;
        background: #f1f5f9 !important;
      }
    }
  }

  .ic-modal-body {
    padding: 24px !important;
    background: #ffffff !important;
    
    .premium-textarea {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      font-size: 14px !important;
      padding: 12px !important;
      
      &:focus {
        border-color: #1890ff !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1) !important;
      }
    }
  }

  .ic-modal-footer {
    padding: 16px 24px !important;
    background: #f8fafc !important;
    border-top: 1px solid #f1f5f9 !important;
    text-align: right !important;
    
    .premium-btn {
      height: 36px !important;
      padding: 0 24px !important;
      border-radius: 8px !important;
      font-weight: 600 !important;
      border: 1px solid #d9d9d9;
      
      &.primary-gradient {
        background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%) !important;
        border: none !important;
        color: #fff !important;
        box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2) !important;
        
        &:hover {
          background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%) !important;
          transform: translateY(-1px);
          box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3) !important;
        }
      }
    }
  }
  /* Shared premium modal styles (matching logs modal) */
  &.influencer-create-modal {
    .ic-modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px 24px;
      background: linear-gradient(135deg, rgba(139, 92, 246, 0.05) 0%, rgba(124, 58, 237, 0.08) 100%);
      border-bottom: 1px solid rgba(226, 232, 240, 0.8);
    }

    .ic-header-left {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .ic-header-icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 12px;
      background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      box-shadow: 0 8px 16px rgba(124, 58, 237, 0.2);
    }

    .ic-header-text {
      display: flex;
      flex-direction: column;
    }

    .ic-header-title {
      font-size: 17px;
      font-weight: 700;
      color: #1e293b;
      margin-bottom: 2px;
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .ic-header-subtitle {
      font-size: 12px;
      color: #64748b;
    }

    .close-btn {
      border-radius: 10px;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #94a3b8;
      position: absolute;
      top: 16px;
      right: 16px;
      transition: all 0.2s;
      
      &:hover { 
        background: rgba(0, 0, 0, 0.05); 
        color: #ef4444; 
      }
    }

    .ic-modal-body {
      padding: 0 !important;
      background: #ffffff;
    }

    .premium-table-container {
      padding: 24px;
    }

    .premium-table, .ultra-premium-table {
      :deep(.ant-table-thead > tr > th) {
        background: #f8fafc;
        color: #64748b;
        font-weight: 700;
        font-size: 12px;
        padding: 14px 12px;
        border-bottom: 2px solid #f1f5f9;
        text-transform: uppercase;
        letter-spacing: 0.05em;

        &::before { display: none !important; }
      }

      :deep(.ant-table-tbody > tr > td) {
        padding: 12px 12px;
        border-bottom: 1px solid #f8fafc;
        transition: background 0.2s ease;
      }

      :deep(.ant-table-tbody > tr:hover > td) {
        background: #f0f9ff !important;
      }
    }

    .user-info-cell {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .mini-avatar {
        font-size: 13px;
        font-weight: 600;
      }
      
      .name {
        font-size: 13px;
        color: #475569;
        font-weight: 500;
      }
    }

    .amount-value {
      font-weight: 600;
      color: #0f172a;
      font-size: 14px;
    }

    .time-value {
      font-size: 13px;
      color: #64748b;
    }

    .empty-placeholder {
      color: #cbd5e1;
    }

    .status-pill {
      border-radius: 6px !important;
      font-weight: 600 !important;
      font-size: 12px !important;
      padding: 2px 8px !important;
    }
  }
}
</style>
