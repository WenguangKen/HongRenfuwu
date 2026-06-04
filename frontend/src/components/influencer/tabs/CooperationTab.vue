<template>
  <div class="cooperation-tab">
    <div class="section-header">
      <div class="header-tips">共 {{ pagination.total }} 条付费合作记录 (由汇款任务自动关联)</div>
    </div>

    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="cooperations"
        :loading="loading"
        :pagination="pagination"
        size="middle"
        :row-key="(r: any) => r.id"
        class="premium-table"
        @change="handleTableChange"
        :scroll="{ y: 420 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'taskNo'">
            <div class="id-stack">
              <div class="task-no" v-if="record.taskNo">{{ record.taskNo }}</div>
              <div class="task-no fallback" v-else-if="record.remittanceTaskId">#{{ record.remittanceTaskId }}</div>
              <div class="order-no" v-if="record.orderNo">
                <span class="label">单号:</span> {{ record.orderNo }}
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'amount'">
            <span class="amount-text">{{ getCurrencySymbol(record.currency) }}{{ (record.amount || 0).toFixed(2) }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">{{ getStatusLabel(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'creator'">
            <div class="user-time-cell">
              <div class="u-name">{{ record.creatorName || '系统' }}</div>
              <div class="u-time">{{ record.createdAt || '-' }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'auditor'">
            <div v-if="record.auditorName" class="user-time-cell">
              <div class="u-name">{{ record.auditorName }}</div>
              <div class="u-time">{{ record.auditTime || '-' }}</div>
            </div>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'payer'">
            <div v-if="record.payerName" class="user-time-cell">
              <div class="u-name">{{ record.payerName }}</div>
              <div class="u-time">{{ record.payTime || '-' }}</div>
            </div>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.dataIndex === 'remark'">
            <div class="remark-stack">
              <div class="main-remark" v-if="record.remark && record.remark !== '-'">{{ record.remark }}</div>
              <div class="process-remark" v-if="record.auditRemark && record.auditRemark !== '-'">
                <span class="label">审核:</span> {{ record.auditRemark }}
              </div>
              <div class="process-remark" v-if="record.paymentRemark && record.paymentRemark !== '-'">
                <span class="label">打款:</span> {{ record.paymentRemark }}
              </div>
              <span v-if="(!record.remark || record.remark === '-') && (!record.auditRemark || record.auditRemark === '-') && (!record.paymentRemark || record.paymentRemark === '-')">-</span>
            </div>
          </template>
        </template>
      </a-table>
    </div>

    </div>
  </template>

  <script setup lang="ts">
  import { ref, watch, reactive } from 'vue';
  import { influencerService } from '@/services/influencerService';

  const props = defineProps<{
    influencerId: number;
  }>();

  const cooperations = ref<any[]>([]);
  const loading = ref(false);

  const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0,
    size: 'small' as const,
    showTotal: (total: number) => `共 ${total} 条`
  });

  const columns = [
    { title: '付款ID/订单号', dataIndex: 'taskNo', key: 'taskNo', width: 140, customRender: ({ record }: any) => record.taskNo || record.orderNo || ('#' + record.remittanceTaskId) },
    { title: '金额', key: 'amount', width: 120, align: 'right' as const },
    { title: '状态', key: 'status', width: 100, align: 'center' as const },
    { title: '创建', key: 'creator', width: 180 },
    { title: '审核', key: 'auditor', width: 180 },
    { title: '打款', key: 'payer', width: 180 },
    { title: '付费类型', dataIndex: 'paymentType', key: 'paymentType', width: 150 },
    { title: '备注', dataIndex: 'remark', width: 200, ellipsis: true },
  ];

  const loadData = async () => {
    if (!props.influencerId) return;
    loading.value = true;
    try {
      // 获取整合后的合作记录（包含手动记录和汇款任务）
      const res = await influencerService.getCooperations(props.influencerId);
      cooperations.value = res || [];
      pagination.total = (res || []).length;
    } catch (e) {
      console.error('Failed to load cooperations:', e);
      cooperations.value = [];
    } finally {
      loading.value = false;
    }
  };

  const handleTableChange = (pag: any) => {
    pagination.current = pag.current;
    pagination.pageSize = pag.pageSize;
    // loadData(); // Local pagination for now as the endpoint returns all
  };

  watch(() => props.influencerId, (v) => { if (v) loadData(); }, { immediate: true });

  const getStatusColor = (status: string) => {
    const map: Record<string, string> = {
      '待审核': 'blue',
      '待打款': 'orange',
      '已打款': 'green',
      '已拒绝': 'red',
      'PAID': 'green',
      'PENDING_AUDIT': 'blue',
      'PENDING_PAYMENT': 'orange',
      'REJECTED': 'red'
    };
    return map[status] || 'default';
  };

  const getStatusLabel = (status: string) => {
    return status || '-';
  };

  const getCurrencySymbol = (currency: string) => {
    if (!currency) return '$';
    const map: Record<string, string> = { 
      'USD': '$', 'CNY': '¥', 'EUR': '€', 'GBP': '£', 'HKD': 'HK$', 'JPY': '¥' 
    };
    return map[currency.toUpperCase()] || (currency + ' ');
  };
  </script>

  <style lang="scss" scoped>
  .cooperation-tab {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    .header-tips { font-size: 14px; color: #94a3b8; font-weight: 500; }
  }
  .add-btn { border-radius: 8px; font-weight: 600; }
  .table-container {
    flex: 1;
    border-radius: 12px;
    border: 1px solid #f1f5f9;
    background: #fff;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }
  .premium-table {
    flex: 1;
    overflow: hidden;
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc !important;
      color: #475569 !important;
      font-weight: 700 !important;
      font-size: 13px !important;
      border-bottom: 1px solid #f1f5f9 !important;
      padding: 16px !important;
    }
    :deep(.ant-table-tbody > tr > td) {
      padding: 8px 16px !important;
      border-bottom: 1px solid #f8fafc !important;
    }
    :deep(.ant-table-tbody > tr:hover > td) { background: #fcfdfe !important; }
    :deep(.ant-pagination) { margin: 16px !important; }
  }
  .amount-text { font-family: 'JetBrains Mono', monospace; font-weight: 800; color: #1e293b; font-size: 14px; }
  .date-text { color: #64748b; font-size: 13px; }
  .type-tag { border: none; font-weight: 600; border-radius: 6px; }

  /* Premium Modal UI */
  :deep(.premium-modal-wrapper) {
    .ant-modal-content {
      padding: 0;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    }
  }

  .modal-header {
    background: #334155;
    padding: 24px 32px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    color: #fff;

    .header-content {
      display: flex;
      gap: 16px;
    }

    .header-icon {
      width: 44px;
      height: 44px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      font-weight: 900;
      color: #fff;
    }

    .header-text {
      .main-title {
        font-size: 20px;
        font-weight: 700;
        margin-bottom: 4px;
      }
      .sub-title {
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);
      }
    }

    .header-close {
      cursor: pointer;
      font-size: 18px;
      color: rgba(255, 255, 255, 0.6);
      transition: all 0.2s;
      &:hover { color: #fff; }
    }
  }

  .modal-body {
    padding: 24px 32px;
    background: #f8fafc;
    max-height: 70vh;
    overflow-y: auto;
  }

  .form-section {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid #eef2f6;

    .section-title {
      font-size: 14px;
      font-weight: 800;
      color: #1e293b;
      margin-bottom: 20px;
      padding-left: 12px;
      position: relative;
      display: flex;
      align-items: center;
      gap: 8px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        width: 4px;
        height: 16px;
        background: #3b82f6;
        border-radius: 2px;
      }
    }
  }

  .modal-footer {
    padding: 16px 32px;
    background: #fff;
    border-top: 1px solid #f1f5f9;
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    .cancel-btn {
      height: 40px;
      border-radius: 8px;
      padding: 0 24px;
      font-weight: 600;
    }

    .save-btn {
      height: 40px;
      border-radius: 8px;
      padding: 0 24px;
      font-weight: 600;
      background: #1e293b;
      border-color: #1e293b;
      
      &:hover {
        background: #334155;
        border-color: #334155;
      }
    }
  }

  /* Form Element Styling */
  :deep(.ant-form-item) {
    margin-bottom: 0;
    .ant-form-item-label > label {
      color: #64748b;
      font-weight: 600;
      font-size: 13px;
    }
  }

  .premium-radio-group {
    display: flex;
    width: 100%;
    gap: 8px;
    
    :deep(.ant-radio-button-wrapper) {
      flex: 1;
      text-align: center;
      border-radius: 8px !important;
      border-left: 1px solid #d9d9d9 !important;
      height: 38px;
      line-height: 36px;
      font-weight: 600;

      &::before { display: none !important; }
      
      &.ant-radio-button-wrapper-checked {
        background: #3b82f6 !important;
        border-color: #3b82f6 !important;
        color: #fff !important;
      }
    }
  }

  :deep(.ant-input), :deep(.ant-select-selector), :deep(.ant-input-number), :deep(.ant-picker) {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    height: 38px !important;
    display: flex !important;
    align-items: center !important;
    
    &:hover { border-color: #cbd5e1 !important; }
    &:focus, &.ant-select-focused, &.ant-picker-focused {
      border-color: #3b82f6 !important;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
    }
  }

  :deep(.ant-input-number-input) { height: 36px !important; }
  :deep(.ant-select-selection-item) { line-height: 36px !important; }
  :deep(.ant-picker-input > input) { font-size: 13px !important; }

  .user-time-cell {
    display: flex;
    flex-direction: column;
    line-height: 1.4;
    
    .u-name {
      font-weight: 600;
      color: #475569;
      font-size: 13px;
    }
    
    .u-time {
      color: #94a3b8;
      font-size: 11px;
    }
  }

  .id-stack {
    display: flex;
    flex-direction: column;
    gap: 2px;
    .task-no {
      font-weight: 600;
      color: #1e293b;
      font-size: 13px;
      &.fallback { color: #64748b; font-style: italic; }
    }
    .order-no {
      font-size: 11px;
      color: #94a3b8;
      .label { color: #cbd5e1; margin-right: 2px; }
    }
  }

  .remark-stack {
    display: flex;
    flex-direction: column;
    gap: 2px;
    max-width: 250px;
    
    .main-remark {
      font-weight: 500;
      color: #334155;
      font-size: 13px;
    }
    
    .process-remark {
      font-size: 11px;
      color: #64748b;
      background: #f8fafc;
      padding: 1px 6px;
      border-radius: 4px;
      border: 1px solid #f1f5f9;
      
      .label {
        color: #94a3b8;
        font-weight: 600;
        margin-right: 2px;
      }
    }
  }
  </style>
