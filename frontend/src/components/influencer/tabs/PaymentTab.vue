<template>
  <div class="payment-tab">
    <div class="section-header">
      <div class="header-tips">共录入 {{ payments.length }} 笔打款记录</div>
    </div>
    
    <div class="table-container">
      <a-table
        :columns="paymentColumns"
        :data-source="payments.filter(p => p.paymentId || p.amount)"
        :pagination="{ pageSize: 10, size: 'small', showTotal: (total: number) => `共 ${total} 条` }"
        size="middle"
        :row-key="(record: PaymentRecord, index: number) => record.paymentId || index"
        class="premium-table"
        :scroll="{ y: 460 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'paymentId'">
            <div class="id-stack">
              <div class="task-no">{{ record.paymentId }}</div>
              <div class="order-no" v-if="record.orderNo && record.orderNo !== '-'">
                <span class="label">单号:</span> {{ record.orderNo }}
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'creator'">
            <div class="user-time-cell">
              <div class="u-name">{{ record.creator || '系统' }}</div>
              <div class="u-time">{{ record.time || '-' }}</div>
            </div>
          </template>

          <template v-else-if="column.key === 'auditor'">
            <div v-if="record.auditor && record.auditor !== '-'" class="user-time-cell">
              <div class="u-name">{{ record.auditor }}</div>
              <div class="u-time">{{ record.auditTime || '-' }}</div>
            </div>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'payer'">
            <div v-if="record.payer && record.payer !== '-'" class="user-time-cell">
              <div class="u-name">{{ record.payer }}</div>
              <div class="u-time">{{ record.payTime || '-' }}</div>
            </div>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'amount'">
            <span class="amount-text">{{ record.amount }}</span>
          </template>

          <template v-else-if="column.key === 'remark'">
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

          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)" class="status-pill">{{ record.status }}</a-tag>
          </template>

          <template v-else-if="column.key === 'paymentType'">
            <span class="v-name">{{ record.paymentType || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'time'">
            <span class="time-text">{{ record.time }}</span>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
interface PaymentRecord {
  paymentId: string;
  amount: string;
  status: string;
  time: string;
  applicant?: string;
  creator?: string;
  auditor?: string;
  auditTime?: string;
  payer?: string;
  payTime?: string;
  paymentType?: string;
  orderNo?: string;
  remark?: string;
  auditRemark?: string;
  paymentRemark?: string;
}

const props = defineProps<{
  payments: PaymentRecord[];
}>();

const paymentColumns = [
  { title: '打款流水 ID/订单号', key: 'paymentId', width: 180 },
  { title: '结算金额', key: 'amount', width: 120, align: 'right' as const },
  { title: '创建', key: 'creator', width: 180 },
  { title: '审核', key: 'auditor', width: 180 },
  { title: '打款', key: 'payer', width: 180 },
  { title: '付费类型', key: 'paymentType', width: 150 },
  { title: '备注', key: 'remark', width: 220, ellipsis: true },
  { title: '结算状态', key: 'status', width: 110, align: 'center' as const },
];

const getStatusColor = (status: string) => {
  if (status.includes('成功') || status.includes('已完成')) return 'success';
  if (status.includes('失败') || status.includes('驳回')) return 'error';
  return 'processing';
};
</script>

<style lang="scss" scoped>
.payment-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px; /* 同步压缩 */
  .header-tips { font-size: 14px; color: #94a3b8; font-weight: 500; }
}

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
  :deep(.ant-table-wrapper), :deep(.ant-table), :deep(.ant-table-container) {
    height: 100% !important;
  }
  :deep(.ant-table-body) {
    overflow-y: auto !important;
  }
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
    transition: all 0.2s;
  }
  :deep(.ant-table-tbody > tr:hover > td) { background: #fcfdfe !important; }

  /* 分页样式优化 */
  :deep(.ant-pagination) {
    margin: 16px !important;
    padding: 0 !important;
  }
}

.mono-text { font-family: 'JetBrains Mono', monospace; color: #64748b; font-size: 13px; font-weight: 600; }
.amount-text { font-family: 'JetBrains Mono', monospace; font-weight: 800; color: #1e293b; font-size: 15px; }
.time-text { color: #64748b; font-size: 13px; font-weight: 500; }
.status-pill { border: none; font-weight: 700; padding: 2px 10px; border-radius: 6px; }

.user-info-cell.small {
  display: flex;
  align-items: center;
  gap: 8px;
  .premium-avatar {
    border: 1.5px solid #fff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    background: #6366f1;
    color: #fff;
    font-size: 11px;
    font-weight: 700;
    
    &.payer-avatar {
      background: #10b981;
    }
  }
  .v-name {
    font-weight: 600;
    color: #334155;
    font-size: 13px;
  }
}

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

.id-stack {
  display: flex;
  flex-direction: column;
  gap: 2px;
  .task-no {
    font-weight: 600;
    color: #1e293b;
    font-size: 13px;
  }
  .order-no {
    font-size: 11px;
    color: #94a3b8;
    .label { color: #cbd5e1; margin-right: 2px; }
  }
}
</style>
