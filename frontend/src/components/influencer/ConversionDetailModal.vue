<template>
  <a-modal
    v-model:open="modalVisible"
    :title="null"
    width="900px"
    :footer="null"
    centered
    class="conversion-detail-modal"
    @cancel="handleCancel"
    :closable="false"
  >
    <div class="cd-modal-header">
      <div class="cd-header-left">
        <div class="cd-header-icon">
          <DollarCircleOutlined />
        </div>
        <div class="cd-header-text">
          <div class="cd-header-title">
            转化订单详情
            <a-tag color="blue" class="header-name-tag">{{ influencerName }}</a-tag>
          </div>
          <div class="cd-header-subtitle">查看转化订单、佣金明细及结算状态</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="cd-modal-body">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        :loading="loading"
        :scroll="{ y: 500 }"
        @change="handleTableChange"
        class="premium-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'orderNo'">
            <div class="order-no-cell">
              <div class="short-no">{{ record.shortOrderNo }}</div>
              <div class="long-no">{{ record.longOrderNo }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'orderAmount'">
            <div class="amount-cell">${{ record.orderAmount }}</div>
          </template>
          <template v-else-if="column.key === 'products'">
            <div class="product-list">
              <div v-for="(product, idx) in record.products" :key="idx" class="product-item">
                <span class="sku">{{ product.sku }}</span>
                <span class="qty">×{{ product.quantity }}</span>
                <span class="price">${{ product.price.toFixed(2) }}</span>
                <a-tag 
                  :class="['status-tag', `product-${product.status === '正常' ? 'normal' : (product.status === '已退款' ? 'refunded' : 'warning')}`]"
                >
                  {{ product.status }}
                </a-tag>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'discountCode'">
            <a-tag class="code-tag">{{ record.discountCode }}</a-tag>
          </template>
          <template v-else-if="column.key === 'commissionRate'">
            <span class="rate-text">{{ record.commissionRate }}%</span>
          </template>
          <template v-else-if="column.key === 'commissionAmount'">
            <div class="commission-cell">${{ record.commissionAmount }}</div>
          </template>
          <template v-else-if="column.key === 'time'">
            <div class="time-timeline" v-if="record.timeInfo">
              <div class="time-item" v-if="record.timeInfo.createTime">
                <div class="time-dot create"></div>
                <div class="time-content">
                  <span class="label">创建</span>
                  <span class="value">{{ record.timeInfo.createTime }}</span>
                </div>
              </div>
              <div class="time-item" v-if="record.timeInfo.payTime">
                <div class="time-dot pay"></div>
                <div class="time-content">
                  <span class="label">支付</span>
                  <span class="value">{{ record.timeInfo.payTime }}</span>
                </div>
              </div>
              <div class="time-item" v-if="record.timeInfo.completeTime">
                <div class="time-dot complete"></div>
                <div class="time-content">
                  <span class="label">完成</span>
                  <span class="value">{{ record.timeInfo.completeTime }}</span>
                </div>
              </div>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { DollarCircleOutlined, CloseOutlined } from '@ant-design/icons-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  open: boolean;
  influencerId: string;
  influencerName: string;
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
}>();

const modalVisible = ref(false);
const loading = ref(false);

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
});

const columns: TableColumnsType = [
  {
    title: '订单号',
    key: 'orderNo',
    width: 200,
    fixed: 'left',
  },
  {
    title: '订单金额',
    key: 'orderAmount',
    width: 120,
    sorter: true,
    align: 'right',
  },
  {
    title: '订单商品',
    key: 'products',
    width: 280,
  },
  {
    title: '折扣码',
    key: 'discountCode',
    width: 140,
    align: 'center',
  },
  {
    title: '佣金率',
    key: 'commissionRate',
    width: 100,
    sorter: true,
    align: 'center',
  },
  {
    title: '已分佣金',
    key: 'commissionAmount',
    width: 120,
    sorter: true,
    align: 'right',
  },
  {
    title: '时间节点',
    key: 'time',
    width: 250,
  },
];

const data = ref<any[]>([]);

const loadData = () => {
  loading.value = true;
  // Mock data
  setTimeout(() => {
    const mockData = Array.from({ length: 10 }).map((_, i) => {
      const orderAmount = Math.floor(Math.random() * 500) + 50;
      const commissionRate = Math.floor(Math.random() * 20) + 5;
      const commissionAmount = (orderAmount * commissionRate / 100).toFixed(2);
      
      const timeInfo: any = {
        createTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      };
      
      if (i % 2 === 0) {
        timeInfo.payTime = dayjs().subtract(i - 1, 'day').format('YYYY-MM-DD HH:mm:ss');
      }
      if (i % 3 === 0) {
        timeInfo.completeTime = dayjs().subtract(i - 2, 'day').format('YYYY-MM-DD HH:mm:ss');
      }
      
      return {
        key: i,
        shortOrderNo: `ORD${2025000 + i}`,
        longOrderNo: `ORDER-${2025000 + i}-${Date.now()}`,
        orderAmount: orderAmount.toFixed(2),
        products: [
          { sku: 'LEG-M-BLACK', quantity: 2, price: 29.99, status: '正常' },
          { sku: 'BRA-S-WHITE', quantity: 1, price: 19.99, status: i % 3 === 0 ? '已退款' : '正常' },
        ],
        discountCode: `CODE${1000 + i}`,
        commissionRate: commissionRate,
        commissionAmount: commissionAmount,
        timeInfo: timeInfo,
      };
    });
    data.value = mockData;
    pagination.total = mockData.length;
    loading.value = false;
  }, 500);
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  loadData();
};

const handleCancel = () => {
  modalVisible.value = false;
  emit('update:open', false);
};

watch(() => props.open, (val) => {
  modalVisible.value = val;
  if (val) {
    loadData();
  }
});

watch(modalVisible, (val) => {
  if (!val) {
    emit('update:open', false);
  }
});
</script>

<style lang="scss" scoped>
:deep(.conversion-detail-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
}

.cd-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(248, 250, 252, 0.8) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(10px);
}

.cd-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cd-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
}

.cd-header-text {
  .cd-header-title {
    font-size: 16px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 2px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .cd-header-subtitle {
    font-size: 12px;
    color: #64748b;
  }
}

.header-name-tag {
  border: none;
  background: #ecfdf5;
  color: #059669;
  font-weight: 600;
  border-radius: 6px;
  margin: 0;
}

.close-btn {
  border-radius: 8px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  transition: all 0.2s;
  
  &:hover { 
    background: rgba(0, 0, 0, 0.05); 
    color: #ef4444; 
  }
}

.cd-modal-body {
  padding: 0;
  background: #fff;
}

.order-no-cell {
  .short-no {
    font-weight: 700;
    color: #1e293b;
    font-family: 'JetBrains Mono', monospace;
  }
  .package-no {
    font-size: 11px;
    color: #64748b;
    margin-top: 2px;
    font-family: 'JetBrains Mono', monospace;
    .label {
      color: #94a3b8;
    }
  }
  .long-no {
    font-size: 11px;
    color: #94a3b8;
    margin-top: 2px;
  }
}

.financial-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-end;
  
  .amount-row, .commission-row {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    
    .label {
      color: #94a3b8;
      font-size: 11px;
    }
    
    .amount {
      font-family: 'JetBrains Mono', monospace;
      font-weight: 600;
    }
  }
  
  .amount-row .amount {
    color: #1e293b;
  }
  
  .commission-row {
    .amount {
      color: #10b981;
    }
    .rate {
      color: #64748b;
      font-size: 11px;
    }
  }
}

.product-list {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  
  .product-item {
    display: flex;
    align-items: center;
    font-size: 12px;
    background: #f8fafc;
    padding: 2px 6px;
    border-radius: 4px;
    border: 1px solid #f1f5f9;
    
    .sku {
      font-weight: 600;
      color: #475569;
      margin-right: 4px;
    }
    
    .qty {
      color: #64748b;
      margin-right: 4px;
      font-weight: 600;
      background: transparent;
      padding: 0;
    }
    
    .price {
      color: #94a3b8;
      margin-right: 4px;
    }
  }
}

.status-tag {
  border: none;
  border-radius: 4px;
  padding: 0 6px;
  font-weight: 600;
  font-size: 10px;
  height: 20px;
  line-height: 20px;
  
  &.product-normal { background: #f0fdf4; color: #16a34a; }
  &.product-refunded { background: #fef2f2; color: #ef4444; }
  &.product-warning { background: #fffbeb; color: #d97706; }
}

.code-tag {
  border: 1px dashed #e2e8f0;
  background: #f8fafc;
  color: #64748b;
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
}

.rate-text {
  font-weight: 600;
  color: #475569;
}

.time-timeline {
  display: flex;
  flex-direction: column;
  position: relative;
  padding-left: 12px;
  
  &::before {
    content: '';
    position: absolute;
    left: 3px;
    top: 6px;
    bottom: 6px;
    width: 1px;
    background: #e2e8f0;
  }
  
  .time-item {
    display: flex;
    align-items: flex-start;
    margin-bottom: 8px;
    position: relative;
    
    &:last-child { margin-bottom: 0; }
    
    .time-dot {
      width: 7px;
      height: 7px;
      border-radius: 50%;
      margin-top: 5px;
      margin-right: 12px;
      z-index: 1;
      position: absolute;
      left: -9px;
      border: 1px solid #fff;
      
      &.create { background: #94a3b8; }
      &.pay { background: #10b981; }
      &.complete { background: #3b82f6; }
    }
    
    .time-content {
      display: flex;
      flex-direction: column;
      
      .label {
        font-size: 11px;
        font-weight: 600;
        color: #64748b;
        line-height: 1;
        margin-bottom: 2px;
      }
      
      .value {
        font-size: 11px;
        color: #334155;
        font-family: 'JetBrains Mono', monospace;
      }
    }
  }
}

.premium-table {
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
    font-size: 13px;
    padding: 12px 16px;
    border-bottom: 1px solid #e2e8f0;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    padding: 12px 16px;
    border-bottom: 1px solid #f1f5f9;
    vertical-align: top;
  }

  :deep(.ant-table-tbody > tr:hover > td) {
    background: #f8fafc !important;
  }

  :deep(.ant-table-pagination) {
    width: 100%;
    margin: 16px 0 !important;
    display: flex !important;
    align-items: center;
    
    .ant-pagination-total-text {
      margin-right: auto;
      margin-left: 8px;
    }
  }
}
</style>

