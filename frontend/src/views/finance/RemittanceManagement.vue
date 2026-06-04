<template>
  <div class="remittance-management">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="任务编号">
          <a-input v-model:value="searchForm.taskNo" placeholder="请输入任务编号" allow-clear class="premium-input-search" />
        </a-form-item>
        <a-form-item label="红人">
          <a-select
            v-model:value="searchForm.influencerId"
            placeholder="搜索红人"
            show-search
            :filter-option="false"
            allow-clear
            style="width: 200px"
            class="premium-select"
            @search="handleInfluencerSearch"
          >
            <a-select-option v-for="item in influencerOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="负责人">
          <a-select v-model:value="searchForm.ownerName" placeholder="全部" show-search allow-clear style="width: 140px" class="premium-select" :filter-option="filterUserOption">
            <a-select-option v-for="u in ownerUsers" :key="u.name" :value="u.name">{{ u.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="打款方式">
          <a-select v-model:value="searchForm.paymentMethod" placeholder="全部" allow-clear style="width: 120px" class="premium-select">
            <a-select-option value="paypal">PayPal</a-select-option>
            <a-select-option value="bank_card">银行卡</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="审核人">
          <a-select v-model:value="searchForm.auditorName" placeholder="全部" show-search allow-clear style="width: 140px" class="premium-select" :filter-option="filterUserOption">
            <a-select-option v-for="u in ownerUsers" :key="u.name" :value="u.name">{{ u.name }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="打款人">
          <a-select v-model:value="searchForm.payerName" placeholder="全部" show-search allow-clear style="width: 140px" class="premium-select" :filter-option="filterUserOption">
            <a-select-option v-for="u in userOptions" :key="u.username" :value="u.username">{{ u.username }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch" class="premium-btn primary-gradient">查询</a-button>
            <a-button @click="resetSearch" class="premium-btn">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card :bordered="false" class="table-card glass-card" style="margin-top: 8px">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="status-switcher-wrapper">
            <a-radio-group v-model:value="activeStatus" button-style="solid" class="premium-segmented" @change="handleStatusChange">
              <a-radio-button value="PENDING_AUDIT">待审核 ({{ counts.PENDING_AUDIT || 0 }})</a-radio-button>
              <a-radio-button value="PENDING_PAYMENT">待打款 ({{ counts.PENDING_PAYMENT || 0 }})</a-radio-button>
              <a-radio-button value="PAID">已打款 ({{ counts.PAID || 0 }})</a-radio-button>
              <a-radio-button value="REJECTED">已拒绝 ({{ counts.REJECTED || 0 }})</a-radio-button>
            </a-radio-group>
          </div>
          
          <div class="toolbar-btns">
            <a-button @click="handleExport" class="premium-btn" style="margin-right: 12px;" v-permission="'finance.remittance.export'">
              <template #icon><ExportOutlined /></template>
              导出
            </a-button>

            <a-button type="primary" @click="handleAdd" class="premium-btn primary-gradient" v-permission="'finance.remittance.create'">
              <template #icon><PlusOutlined /></template>
              创建汇款任务
            </a-button>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="false"
        @change="handleTableChange"
        row-key="id"
        class="premium-table"
        size="middle"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :scroll="{ x: 'max-content', y: 'calc(100vh - 320px)' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)" :bordered="false" class="status-tag">
              {{ getStatusLabel(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'paymentType'">
            <span class="payment-type-text">{{ record.paymentType || '-' }}</span>
          </template>
          <template v-if="column.key === 'amount'">
            <span class="amount-text">{{ record.currency }} {{ record.totalAmount || record.amount }}</span>
          </template>
          <template v-if="column.key === 'paymentMethodDisplay'">
            <a-tag v-if="record.paymentMethod === 'paypal'" color="blue" :bordered="false">PayPal</a-tag>
            <a-tag v-else-if="record.paymentMethod === 'bank_card'" color="cyan" :bordered="false">银行卡</a-tag>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-if="column.key === 'createdAt'">
            <div v-if="record.createdAt" class="time-cell">
              <div class="time-date">{{ dayjs(record.createdAt).format('YYYY-MM-DD') }}</div>
              <div class="time-clock">{{ dayjs(record.createdAt).format('HH:mm:ss') }}</div>
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-if="column.key === 'payTime'">
            <div v-if="record.payTime" class="time-cell">
              <div class="time-date">{{ dayjs(record.payTime).format('YYYY-MM-DD') }}</div>
              <div class="time-clock">{{ dayjs(record.payTime).format('HH:mm:ss') }}</div>
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-if="column.key === 'voucherUrl'">
            <a-image v-if="record.voucherUrl" :width="40" :src="record.voucherUrl" class="voucher-preview" />
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <template v-if="record.status === 'PENDING_AUDIT'">
                <a-button type="primary" size="small" @click="handleAudit(record)" class="detail-btn">审核</a-button>
              </template>
              <template v-if="record.status === 'PENDING_PAYMENT'">
                <a-button type="primary" size="small" @click="handlePay(record)" class="detail-btn">打款</a-button>
                <a-button size="small" danger @click="handleRejectToAudit(record)" class="reject-btn">驳回重审</a-button>
              </template>
              <template v-if="record.status === 'PENDING_AUDIT' || record.status === 'REJECTED'">
                <a-button type="link" size="small" @click="handleEdit(record)" v-permission="'finance.remittance.edit'" class="action-btn-link edit-btn">编辑</a-button>
              </template>
              <a-button type="link" size="small" @click="handleDetail(record)" class="action-btn-link">详情</a-button>
            </div>
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
            size="small"
            @change="handlePaginationChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 弹窗组件 -->
    <RemittanceCreateModal ref="createModalRef" @success="fetchList" />
    <RemittanceAuditModal ref="auditModalRef" @success="fetchList" />

    <!-- 驳回重审弹窗 -->
    <a-modal
      v-model:open="rejectToAuditVisible"
      title="驳回重审"
      ok-text="确认驳回"
      cancel-text="取消"
      :ok-button-props="{ danger: true, disabled: !rejectToAuditReason.trim() }"
      @ok="confirmRejectToAudit"
      :confirm-loading="rejectToAuditLoading"
      centered
      width="480px"
    >
      <div style="margin-bottom: 8px; color: #64748b; font-size: 13px;">驳回后该任务将回到「待审核」列表重新审核，请填写驳回原因：</div>
      <a-textarea
        v-model:value="rejectToAuditReason"
        placeholder="请输入驳回原因（必填）..."
        :rows="4"
      />
    </a-modal>

    
    <!-- 导出弹窗 -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="exportFields"
      :page-type="'remittance-list'"
      :current-user-id="String(currentUserId)"
      :all-users="allUsers as any"
      @export="handleExportFromModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined, ExportOutlined, CloudUploadOutlined } from '@ant-design/icons-vue';
import type { TableProps } from 'ant-design-vue';
import { remittanceService } from '@/services/remittanceService';
import { influencerService } from '@/services/influencerService';
import { useUserStore } from '@/stores/user';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';
import { createExportTask } from '@/utils/exportTaskHelper';
import RemittanceCreateModal from './components/RemittanceCreateModal.vue';
import RemittanceAuditModal from './components/RemittanceAuditModal.vue';
import RemittanceImportModal from './components/RemittanceImportModal.vue';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import type { RemittanceTask, RemittanceStatus } from '@/types/influencer';
import dayjs from 'dayjs';

const loading = ref(false);
const activeStatus = ref<RemittanceStatus>('PENDING_AUDIT');
const dataSource = ref<RemittanceTask[]>([]);
const counts = ref<Record<string, number>>({
  PENDING_AUDIT: 0,
  PENDING_PAYMENT: 0,
  PAID: 0,
  REJECTED: 0
});
const influencerOptions = ref<{ id: number; name?: string }[]>([]);
const createModalRef = ref();
const auditModalRef = ref();
const importModalVisible = ref(false);

const searchForm = reactive({
  taskNo: '',
  influencerId: undefined as number | undefined,
  ownerName: undefined as string | undefined,
  paymentMethod: undefined as string | undefined,
  auditorName: undefined as string | undefined,
  payerName: undefined as string | undefined
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true
});

const userStore = useUserStore();
const currentUserId = computed(() => userStore.userInfo?.id || '');
const commonStore = useCommonDataStore();
const { allUsers, ownerUsers } = storeToRefs(commonStore);
const userOptions = computed(() => allUsers.value.map(u => ({ id: u.id, username: u.name })));

// 导出相关
const exportModalVisible = ref(false);
const selectedRowKeys = ref<number[]>([]);
let exportCounter = 1; // 导出递增计数器
const exportFields = [
  { key: 'createdAtText', title: '创建时间' },
  { key: 'influencerEmail', title: '红人邮箱' },
  { key: 'orderNo', title: '合作交易号' },
  { key: 'totalAmountText', title: '合作总金额' },
  { key: 'paymentMethodLabel', title: '支付方式' },
  { key: 'paypalAccount', title: 'Paypal账号' },
  { key: 'accountNumber', title: '银行卡号' },
  { key: 'bankName', title: '银行名称' },
  { key: 'bankCountry', title: '银行国家/地区' },
  { key: 'branchName', title: '支行名称' },
  { key: 'bankAddress', title: '银行地址' },
  { key: 'swiftCode', title: 'Swift Code' },
  { key: 'routingNumber', title: 'Routing Number' },
  { key: 'accountName', title: '账户名' },
  { key: 'beneficiaryAddress', title: '收款人地址' },
  { key: 'remark', title: '任务备注' },
  { key: 'paymentType', title: '本次支付类型' },
  { key: 'amountText', title: '付款金额' },
  { key: 'feeText', title: '手续费' },
  { key: 'totalPayText', title: '付款合计' },
  { key: 'statusLabel', title: '付款状态' },
  { key: 'payTimeText', title: '付款时间' },
  { key: 'payerName', title: '付款人' },
  { key: 'voucherUrl', title: '凭证图片', type: 'image' }
];

const columns = [
  { title: '任务编号', dataIndex: 'taskNo', key: 'taskNo', width: 150 },
  { title: '红人', dataIndex: 'influencerName', key: 'influencerName', width: 140 },
  { title: '负责人', dataIndex: 'ownerName', key: 'ownerName', width: 100 },
  { title: '付费类型', dataIndex: 'paymentType', key: 'paymentType', width: 240 },
  { title: '总付费用', dataIndex: 'totalAmount', key: 'amount', width: 120 },
  { title: '打款方式', dataIndex: 'paymentMethod', key: 'paymentMethodDisplay', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '关联订单', dataIndex: 'orderNo', key: 'orderNo', width: 120 },
  { title: '审核人', dataIndex: 'auditorName', key: 'auditorName', width: 100 },
  { title: '打款人', dataIndex: 'payerName', key: 'payerName', width: 100 },
  { title: '申请时间', dataIndex: 'createdAt', key: 'createdAt', width: 160 },
  { title: '打款时间', dataIndex: 'payTime', key: 'payTime', width: 160 },
  { title: '凭证', dataIndex: 'voucherUrl', key: 'voucherUrl', width: 80 },
  { title: '操作', key: 'action', fixed: 'right' as const, width: 220 }
];

const fetchCounts = async () => {
  try {
    const res = await remittanceService.getStatusCounts({
      ...searchForm
    });
    counts.value = res as any;
  } catch (err) {
    console.error('Failed to fetch counts:', err);
  }
};

const fetchList = async () => {
  loading.value = true;
  try {
    const res = await remittanceService.getList({
      ...searchForm,
      status: activeStatus.value,
      page: pagination.current,
      size: pagination.pageSize
    });
    dataSource.value = res.content;
    pagination.total = res.totalElements;
    // 同时也刷新计数
    fetchCounts();
  } catch (err) {
    message.error('加载列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.current = 1;
  fetchList();
};

const resetSearch = () => {
  searchForm.taskNo = '';
  searchForm.influencerId = undefined;
  searchForm.ownerName = undefined;
  searchForm.paymentMethod = undefined;
  searchForm.auditorName = undefined;
  searchForm.payerName = undefined;
  handleSearch();
};

const handleStatusChange = () => {
  pagination.current = 1;
  fetchList();
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current || 1;
  pagination.pageSize = pag.pageSize || 10;
  fetchList();
};

const handlePaginationChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  fetchList();
};

const getStatusColor = (status: RemittanceStatus) => {
  const map: Record<RemittanceStatus, string> = {
    PENDING_AUDIT: 'blue',
    PENDING_PAYMENT: 'orange',
    PAID: 'green',
    REJECTED: 'red'
  };
  return map[status] || 'default';
};

const getStatusLabel = (status: RemittanceStatus) => {
  const map: Record<RemittanceStatus, string> = {
    PENDING_AUDIT: '待审核',
    PENDING_PAYMENT: '待打款',
    PAID: '已打款',
    REJECTED: '已拒绝'
  };
  return map[status] || (status as string);
};

const handleAdd = () => {
  createModalRef.value.show();
};

const handleEdit = (record: RemittanceTask) => {
  createModalRef.value.show(record);
};

const handleImport = () => {
  importModalVisible.value = true;
};

const handleAudit = (record: RemittanceTask) => {
  auditModalRef.value.show(record, 'audit');
};

const handlePay = (record: RemittanceTask) => {
  auditModalRef.value.show(record, 'pay');
};

const handleDetail = (record: RemittanceTask) => {
  auditModalRef.value.show(record, 'detail');
};

// 驳回重审
const rejectToAuditVisible = ref(false);
const rejectToAuditReason = ref('');
const rejectToAuditLoading = ref(false);
const rejectToAuditRecord = ref<RemittanceTask | null>(null);

const handleRejectToAudit = (record: RemittanceTask) => {
  rejectToAuditRecord.value = record;
  rejectToAuditReason.value = '';
  rejectToAuditVisible.value = true;
};

const confirmRejectToAudit = async () => {
  if (!rejectToAuditRecord.value || !rejectToAuditReason.value.trim()) return;
  rejectToAuditLoading.value = true;
  try {
    await remittanceService.audit(rejectToAuditRecord.value.id, {
      action: 'reject_to_audit',
      remark: rejectToAuditReason.value.trim()
    });
    message.success('已驳回，任务已回到待审核列表');
    rejectToAuditVisible.value = false;
    fetchList();
  } catch (error: any) {
    message.error(error.message || '驳回失败');
  } finally {
    rejectToAuditLoading.value = false;
  }
};

const onSelectChange = (keys: number[]) => {
  selectedRowKeys.value = keys;
};

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async ({ scope, fields, columns, templateName }: any) => {
  try {
    let exportData: RemittanceTask[] = [];
    if (scope === 'selected') {
      exportData = dataSource.value.filter(item => selectedRowKeys.value.includes(item.id as number));
    } else {
      // 导出全部（根据当前筛选条件）
      const res = await remittanceService.getList({
        ...searchForm,
        status: activeStatus.value,
        page: 1,
        size: pagination.total > 0 ? pagination.total : 5000
      });
      exportData = res.content;
    }

    const formattedData = exportData.map(item => {
      // 解析 paymentDetails JSON
      let details: any = {};
      try {
        if (item.paymentDetails) details = JSON.parse(item.paymentDetails);
      } catch { /* ignore */ }

      const isPaypal = item.paymentMethod === 'paypal';
      const isBankCard = item.paymentMethod === 'bank_card';

      return {
        ...item,
        influencerEmail: item.influencerEmail || '-',
        paymentMethodLabel: isPaypal ? 'PayPal' : isBankCard ? '银行卡' : (item.paymentMethod || '-'),
        amountText: `${item.currency} ${item.amount ?? ''}`,
        feeText: item.fee != null ? `${item.currency} ${item.fee}` : '-',
        totalAmountText: `${item.currency} ${item.totalAmount ?? item.amount ?? ''}`,
        totalPayText: `${item.currency} ${item.totalAmount ?? item.amount ?? ''}`,
        statusLabel: getStatusLabel(item.status),
        createdAtText: item.createdAt ? dayjs(item.createdAt).format('YYYY-MM-DD HH:mm:ss') : '-',
        auditTimeText: item.auditTime ? dayjs(item.auditTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        payTimeText: item.payTime ? dayjs(item.payTime).format('YYYY-MM-DD HH:mm:ss') : '-',
        voucherUrl: item.voucherUrl || '',
        // PayPal：只有账号
        paypalAccount: isPaypal ? (details.paypalAccount || item.paymentAccount || '-') : '',
        // 银行卡：完整信息
        bankCountry: isBankCard ? (details.bankCountry || '-') : '',
        bankName: isBankCard ? (details.bankName || '-') : '',
        branchName: isBankCard ? (details.branchName || '-') : '',
        bankAddress: isBankCard ? (details.bankAddress || '-') : '',
        swiftCode: isBankCard ? (details.swiftCode || '-') : '',
        routingNumber: isBankCard ? (details.routingNumber || '-') : '',
        accountName: isBankCard ? (details.accountName || '-') : '',
        accountNumber: isBankCard ? (details.accountNumber || item.paymentAccount || '-') : '',
        beneficiaryAddress: isBankCard ? (details.beneficiaryAddress || '-') : ''
      };
    });

    const timestamp = dayjs().format('YYYYMMDD_HHmmss');
    const seq = String(exportCounter++).padStart(3, '0');
    await createExportTask({
      data: formattedData,
      columns: columns,
      filename: `红人回款表_${timestamp}_${seq}`,
      pageType: 'remittance-list',
      templateName
    });

    message.success('导出记录已提交，请在导出历史中查看');
    exportModalVisible.value = false;
  } catch (err) {
    console.error(err);
    message.error('导出失败');
  }
};

const loadInfluencers = async (keyword: string = '') => {
  try {
    const res = await influencerService.getList({ page: 1, size: 50, searchKey: keyword || undefined });
    influencerOptions.value = res.content.map(i => ({ id: i.id, name: i.realName || '-' }));
  } catch (err) {
    console.error(err);
  }
};

// 远程搜索防抖
let influencerSearchTimer: any = null;
const handleInfluencerSearch = (value: string) => {
  if (influencerSearchTimer) clearTimeout(influencerSearchTimer);
  influencerSearchTimer = setTimeout(() => {
    loadInfluencers(value);
  }, 300);
};

const filterUserOption = (input: string, option: any) => {
  return (option?.children?.[0]?.children || option?.label || '').toLowerCase().includes(input.toLowerCase());
};

onMounted(() => {
  fetchList();
  loadInfluencers();
  commonStore.loadUsers();
});
</script>

<style lang="scss" scoped>
.remittance-management {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 56px);
  overflow: hidden;
  
  .glass-card {
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.05);
  }

  .filter-card {
    padding: 0;
    margin-bottom: 8px;
    :deep(.ant-card-body) { padding: 12px 20px; }
  }

  .table-card {
    display: flex;
    flex-direction: column;
    flex: 1;
    min-height: 0;
    overflow: hidden;
    
    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 0 16px;
      min-height: 52px;
      display: flex;
      align-items: center;
      .ant-card-head-title { padding: 0; width: 100%; }
    }
    :deep(.ant-card-body) { 
      padding: 0; 
      display: flex;
      flex-direction: column;
      flex: 1;
      min-height: 0;
      overflow: hidden;
    }
  }

  .table-actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    
    .status-switcher-wrapper {
      .premium-segmented {
        background: transparent;
        border: none;
        display: flex;
        gap: 4px;

        :deep(.ant-radio-button-wrapper) {
          border: 1px solid transparent;
          background: transparent;
          border-radius: 6px;
          height: 32px;
          line-height:30px;
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
            box-shadow: none;
            font-weight: 600;
          }
        }
      }
    }
  }

  .premium-table {
    flex: 1;
    min-height: 0;
    overflow: hidden;
    :deep(.ant-table-wrapper) {
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    :deep(.ant-spin-nested-loading) {
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    :deep(.ant-spin-container) {
      height: 100%;
      display: flex;
      flex-direction: column;
    }
    :deep(.ant-table) {
      flex: 1;
      min-height: 0;
      display: flex;
      flex-direction: column;
    }
    :deep(.ant-table-container) {
      flex: 1;
      min-height: 0;
      display: flex;
      flex-direction: column;
    }
    :deep(.ant-table-body) {
      flex: 1;
      min-height: 0;
      overflow-y: auto !important;
    }
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc;
      color: #475569;
      font-weight: 700;
      font-size: 13px;
      padding: 12px 16px;
    }
    :deep(.ant-table-tbody > tr > td) {
      padding: 12px 16px;
    }
    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }
  }

  .payment-type-text {
    white-space: nowrap;
    color: #475569;
  }

  .status-tag {
    border-radius: 20px;
    padding: 2px 12px;
    font-weight: 600;
    font-size: 11px;
  }

  .amount-text {
    font-family: 'JetBrains Mono', monospace;
    font-weight: 700;
    color: #1e293b;
  }

  .time-cell {
    line-height: 1.4;
    .time-date {
      font-size: 13px;
      color: #1e293b;
      font-weight: 600;
    }
    .time-clock {
      font-size: 12px;
      color: #94a3b8;
      font-weight: 400;
    }
  }

  .voucher-preview {
    border-radius: 4px;
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: transform 0.2s;
    &:hover { transform: scale(1.1); }
  }

  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .detail-btn {
    border-radius: 6px;
    font-weight: 600;
    height: 28px;
    padding: 0 12px;
    font-size: 12px;
    background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
    border: none;
    box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
    color: #fff;
    &:hover {
      background: linear-gradient(135deg, #93c5fd 0%, #3b82f6 100%);
      transform: translateY(-1px);
    }
  }

  .action-btn-link {
    font-weight: 600;
    color: #64748b;
    &:hover { color: #1890ff; }
  }

  .reject-btn {
    border-radius: 6px;
    font-weight: 600;
    height: 28px;
    padding: 0 10px;
    font-size: 12px;
  }

  .premium-btn {
    border-radius: 6px;
    font-weight: 600;
    &.primary-gradient {
      background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
      border: none;
      color: #fff;
      &:hover { transform: translateY(-1px); }
    }
  }

  .premium-input-search, .premium-select {
    :deep(.ant-input), :deep(.ant-select-selector) {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      background: #fafbfc !important;
    }
  }

  /* Custom Pagination Footer */
  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    
    .footer-left {
      color: #94a3b8;
      font-size: 12px;
      .count-value {
        font-weight: 600;
        color: #1e293b;
        background: #f1f5f9;
        padding: 2px 8px;
        border-radius: 4px;
        margin-left: 4px;
      }
    }
  }
}
</style>
