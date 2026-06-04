<template>
  <div class="financial-section">
    <!-- 统计卡片 -->
    <div class="financial-summary">
      <a-tooltip title="计算公式：实付金额 + 优惠金额">
        <div class="summary-card total">
          <div class="card-icon">
            <ShoppingOutlined />
          </div>
          <div class="card-info">
            <div class="label">订单金额</div>
            <div class="value">${{ orderTotal.toFixed(2) }}</div>
          </div>
        </div>
      </a-tooltip>
      
      <a-tooltip title="计算公式：折扣码优惠 + 优惠券抵扣">
        <div class="summary-card discount">
          <div class="card-icon">
            <GiftOutlined />
          </div>
          <div class="card-info">
            <div class="label">优惠金额</div>
            <div class="value-row">
              <span class="value">${{ totalDiscount.toFixed(2) }}</span>
              <div class="hover-details">
                <span v-if="discountInfo.code > 0">折扣: ${{ discountInfo.code.toFixed(2) }}</span>
                <span v-if="discountInfo.voucher > 0">券: ${{ discountInfo.voucher.toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
      </a-tooltip>

      <a-tooltip title="来源：Shopify 订单运费总额">
        <div class="summary-card shipping">
          <div class="card-icon">
            <CarOutlined />
          </div>
          <div class="card-info">
            <div class="label">运费</div>
            <div class="value">${{ shippingTotal.toFixed(2) }}</div>
          </div>
        </div>
      </a-tooltip>
      
      <a-tooltip title="来源：所有退款记录总和">
        <div class="summary-card expense">
          <div class="card-icon">
            <RedEnvelopeOutlined />
          </div>
          <div class="card-info">
            <div class="label">已退款总额</div>
            <div class="value">${{ totalRefund.toFixed(2) }}</div>
          </div>
        </div>
      </a-tooltip>

      <a-tooltip title="来源：Shopify 订单实付总额（已扣除优惠）">
        <div class="summary-card actual">
          <div class="card-icon">
            <WalletOutlined />
          </div>
          <div class="card-info">
            <div class="label">实付金额</div>
            <div class="value">${{ actualPayment.toFixed(2) }}</div>
          </div>
        </div>
      </a-tooltip>
    </div>

    <!-- 付款记录 -->
    <div class="record-group">
      <div class="group-header">
        <div class="title">付款记录</div>
        <a-tag color="blue">{{ paymentList.length }} 笔</a-tag>
      </div>
      
      <a-table
        :columns="paymentColumns"
        :data-source="paymentList"
        :pagination="false"
        size="small"
        :scroll="{ y: 240 }"
        class="financial-table"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span class="amount-text income">+${{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'success' ? 'success' : 'processing'">
              {{ record.status === 'success' ? '支付成功' : '处理中' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 退款记录 -->
    <div class="record-group">
      <div class="group-header">
        <div class="title">退款记录</div>
        <a-tag color="orange">{{ refundList.length }} 笔</a-tag>
      </div>
      
      <a-table
        :columns="refundColumns"
        :data-source="refundList"
        :pagination="false"
        size="small"
        :scroll="{ y: 240 }"
        class="financial-table"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span class="amount-text expense">-${{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'success' ? 'success' : 'processing'">
              {{ record.status === 'success' ? '退款成功' : '处理中' }}
            </a-tag>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { PayCircleOutlined, RedEnvelopeOutlined, GiftOutlined, ShoppingOutlined, WalletOutlined, CarOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
  orderData: any;
}>();

// 使用 computed 确保数据响应式更新
const paymentList = computed(() => props.orderData?.payments || []);
const refundList = computed(() => props.orderData?.refunds || []);

const totalPayment = computed(() => {
  return paymentList.value.reduce((sum: number, item: any) => sum + parseFloat(item.amount || 0), 0);
});

const totalRefund = computed(() => {
  if (refundList.value.length > 0) {
    return refundList.value.reduce((sum: number, item: any) => sum + parseFloat(item.amount || 0), 0);
  }
  return parseFloat(String(props.orderData?.refundAmount || 0));
});

const shippingTotal = computed(() => {
  const val = props.orderData?.totalShipping || props.orderData?.shippingAmount || 0;
  return parseFloat(String(val));
});

const discountInfo = computed(() => {
  const d = props.orderData?.discount || { code: 0, voucher: 0 };
  return {
    code: parseFloat(String(d.code || 0)),
    voucher: parseFloat(String(d.voucher || 0))
  };
});

const totalDiscount = computed(() => {
  return parseFloat(String(discountInfo.value.code || 0)) + parseFloat(String(discountInfo.value.voucher || 0));
});

const orderTotal = computed(() => {
  const val = props.orderData?.totalAmount || props.orderData?.totalPrice;
  const price = val ? parseFloat(String(val)) : 0;
  // 订单金额 = 实付金额 + 优惠金额
  return price + totalDiscount.value;
});

const actualPayment = computed(() => {
  // 实付金额直接使用 totalAmount/totalPrice (Shopify返回的已经是实付金额)
  const val = props.orderData?.totalAmount || props.orderData?.totalPrice;
  return val ? parseFloat(String(val)) : 0;
});

const paymentColumns = [
  { title: '付款时间', dataIndex: 'time', key: 'time', width: 160 },
  { title: '付款单号', dataIndex: 'paymentNo', key: 'paymentNo', width: 180 },
  { title: '流水号', dataIndex: 'transactionId', key: 'transactionId', width: 160 },
  { title: '付款方式', dataIndex: 'method', key: 'method', width: 180 }, // 增加宽度防止换行
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 100, align: 'right' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100, align: 'center' },
  { title: '操作人', dataIndex: 'operator', key: 'operator', width: 100 },
];

const refundColumns = [
  { title: '退款时间', dataIndex: 'time', key: 'time', width: 160 },
  { title: '退款单号', dataIndex: 'refundNo', key: 'refundNo', width: 180 },
  { title: '退款原因', dataIndex: 'reason', key: 'reason', width: 160 },
  { title: '退款方式', dataIndex: 'method', key: 'method', width: 180 }, // 增加宽度
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 100, align: 'right' },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100, align: 'center' },
  { title: '操作人', dataIndex: 'operator', key: 'operator', width: 100 },
];
</script>

<style lang="scss" scoped>
.financial-section {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  height: 100%;
  overflow-y: auto;
}

.financial-summary {
  display: flex;
  gap: 16px;
  
  .summary-card {
    flex: 1;
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 12px 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    transition: all 0.2s;
    
    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      transform: translateY(-2px);
    }
    
    .card-icon {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
    }
    
    .card-info {
      .label {
        color: #64748b;
        font-size: 12px;
        margin-bottom: 2px;
      }
      .value {
        font-size: 20px;
        font-weight: 700;
        font-family: 'Inter', monospace;
      }
    }
    
    &.income {
      .card-icon {
        background: #ecfdf5;
        color: #10b981;
      }
      .value { color: #10b981; }
    }
    
    &.expense {
      .card-icon {
        background: #fff1f2;
        color: #f43f5e;
      }
      .value { color: #f43f5e; }
    }
    
    &.total {
      .card-icon {
        background: #f1f5f9;
        color: #64748b;
      }
      .value { color: #334155; }
    }

    &.actual {
      .card-icon {
        background: #f0fdfa;
        color: #0d9488;
      }
      .value { color: #0d9488; }
    }

    &.shipping {
      .card-icon {
        background: #fff7ed;
        color: #f97316;
      }
      .value { color: #f97316; }
    }

    &.discount {
      .card-icon {
        background: #f0f9ff;
        color: #0ea5e9;
      }
      .value { color: #0ea5e9; }
      
      .value-row {
        display: flex;
        align-items: center;
        gap: 8px;
        position: relative;
        
        .hover-details {
          display: none;
          position: absolute;
          left: 100%;
          top: 50%;
          transform: translateY(-50%);
          background: #fff;
          border: 1px solid #e2e8f0;
          padding: 4px 8px;
          border-radius: 6px;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          white-space: nowrap;
          z-index: 10;
          font-size: 12px;
          color: #64748b;
          margin-left: 8px;
          
          span {
            margin-right: 8px;
            &:last-child { margin-right: 0; }
          }
          
          &::before {
            content: '';
            position: absolute;
            left: -4px;
            top: 50%;
            transform: translateY(-50%) rotate(45deg);
            width: 8px;
            height: 8px;
            background: #fff;
            border-left: 1px solid #e2e8f0;
            border-bottom: 1px solid #e2e8f0;
          }
        }
        
        &:hover .hover-details {
          display: flex;
        }
      }
    }
  }
}

.record-group {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  
  .group-header {
    padding: 12px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    
    .title {
      font-weight: 600;
      color: #1e293b;
      font-size: 14px;
    }
  }
}

.financial-table {
  :deep(.ant-table-thead > tr > th) {
    background: #fff;
    font-weight: 600;
    color: #64748b;
    font-size: 12px;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    font-size: 13px;
    color: #334155;
  }
  
  .amount-text {
    font-family: 'Inter', monospace;
    font-weight: 600;
    
    &.income { color: #10b981; }
    &.expense { color: #f43f5e; }
  }
}
</style>