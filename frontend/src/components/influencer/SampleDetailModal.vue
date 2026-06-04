<template>
  <a-modal
    v-model:open="modalVisible"
    :title="null"
    width="900px"
    :footer="null"
    centered
    class="sample-detail-modal"
    @cancel="handleCancel"
    :closable="false"
  >
    <div class="sd-modal-header">
      <div class="sd-header-left">
        <div class="sd-header-icon">
          <GiftOutlined />
        </div>
        <div class="sd-header-text">
          <div class="sd-header-title">
            关联样品详情
            <a-tag color="blue" class="header-name-tag">{{ influencerName }}</a-tag>
          </div>
          <div class="sd-header-subtitle">查看样品订单、物流状态及收货信息</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="sd-modal-body">
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
              <div v-if="record.packageNo" class="package-no">
                <span class="label">包裹号:</span> {{ record.packageNo }}
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'products'">
            <div class="product-list">
              <div v-for="(product, idx) in record.products" :key="idx" class="product-item">
                <span class="sku">{{ product.sku }}</span>
                <span class="qty">×{{ product.quantity }}</span>
                <span class="price">${{ product.price.toFixed(2) }}</span>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'address'">
            <div class="address-cell" :title="record.address">{{ record.address }}</div>
          </template>
          <template v-else-if="column.key === 'logisticsStatus'">
            <a-tag :class="['status-tag', `logistics-${getLogisticsClass(record.logisticsStatus)}`]">
              {{ record.logisticsStatus || '-' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'orderStatus'">
            <a-tag :class="['status-tag', `order-${getOrderStatusClass(record.orderStatus)}`]">
              {{ record.orderStatus }}
            </a-tag>
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
              <div class="time-item" v-if="record.timeInfo.shipTime">
                <div class="time-dot ship"></div>
                <div class="time-content">
                  <span class="label">发货</span>
                  <span class="value">{{ record.timeInfo.shipTime }}</span>
                </div>
              </div>
              <div class="time-item" v-if="record.timeInfo.deliveredTime">
                <div class="time-dot deliver"></div>
                <div class="time-content">
                  <span class="label">妥投</span>
                  <span class="value">{{ record.timeInfo.deliveredTime }}</span>
                </div>
              </div>
              <div class="time-item" v-if="record.timeInfo.completedTime">
                <div class="time-dot complete"></div>
                <div class="time-content">
                  <span class="label">完成</span>
                  <span class="value">{{ record.timeInfo.completedTime }}</span>
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
import { GiftOutlined, CloseOutlined } from '@ant-design/icons-vue';
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
    title: '订单号 / 包裹号',
    key: 'orderNo',
    width: 220, // Increased width slightly for package no
    fixed: 'left',
  },
  {
    title: '订单商品',
    key: 'products',
    width: 260, // Reduced
  },
  {
    title: '状态',
    children: [
      { title: '物流状态', key: 'logisticsStatus', width: 90, align: 'center' },
      { title: '订单状态', key: 'orderStatus', width: 90, align: 'center' },
    ],
  },
  {
    title: '时间节点',
    key: 'time',
    width: 240, // Reduced
  },
];

const data = ref<any[]>([]);

const getLogisticsClass = (status: string) => {
  const map: Record<string, string> = {
    '已发货': 'shipped',
    '已妥投': 'delivered',
    '已完成': 'completed',
  };
  return map[status] || 'default';
};

const getOrderStatusClass = (status: string) => {
  const map: Record<string, string> = {
    '待处理': 'pending',
    '待发货': 'processing',
    '已发货': 'shipped',
    '已妥投': 'delivered',
    '已完成': 'completed',
    '取消中': 'cancelling',
    '已取消': 'cancelled',
  };
  return map[status] || 'default';
};

const loadData = () => {
  loading.value = true;
  // Mock data
  setTimeout(() => {
    const orderStatuses = ['待处理', '待发货', '已发货', '已妥投', '已完成'];
    const mockData = Array.from({ length: 12 }).map((_, i) => {
      const orderStatus = orderStatuses[i % orderStatuses.length];
      const timeInfo: any = {
        createTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
      };
      
      if (orderStatus === '已发货' || orderStatus === '已妥投' || orderStatus === '已完成') {
        timeInfo.shipTime = dayjs().subtract(i - 1, 'day').format('YYYY-MM-DD HH:mm:ss');
      }
      if (orderStatus === '已妥投' || orderStatus === '已完成') {
        timeInfo.deliveredTime = dayjs().subtract(i - 2, 'day').format('YYYY-MM-DD HH:mm:ss');
      }
      if (orderStatus === '已完成') {
        timeInfo.completedTime = dayjs().subtract(i - 3, 'day').format('YYYY-MM-DD HH:mm:ss');
      }
      
      let logisticsStatus = '';
      if (orderStatus === '已发货' || orderStatus === '已妥投' || orderStatus === '已完成') {
        logisticsStatus = orderStatus;
      }
      
      return {
        key: i,
        shortOrderNo: `SMP${2025000 + i}`,
        longOrderNo: `SAMPLE-ORDER-${2025000 + i}-${Date.now()}`,
        packageNo: i % 2 === 0 ? `PKG${Math.random().toString().slice(2, 10)}` : '', // Mock package no
        products: [
          { sku: 'LEG-M-BLACK', quantity: 2, price: 29.99 },
          { sku: 'BRA-S-WHITE', quantity: 1, price: 19.99 },
        ],
        address: `123 Main St, Los Angeles, CA 90001, USA`,
        receiverName: `John Doe ${i + 1}`,
        postalCode: `9000${i}`,
        email: `receiver${i + 1}@example.com`,
        phone: `+1-555-000${i}`,
        logisticsStatus: logisticsStatus,
        orderStatus: orderStatus,
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
:deep(.sample-detail-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
}

.sd-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(248, 250, 252, 0.8) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(10px);
}

.sd-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sd-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.2);
}

.sd-header-text {
  .sd-header-title {
    font-size: 16px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 2px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .sd-header-subtitle {
    font-size: 12px;
    color: #64748b;
  }
}

.header-name-tag {
  border: none;
  background: #fffbeb;
  color: #d97706;
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

.sd-modal-body {
  padding: 0;
  background: #fff;
}

.order-no-cell {
  .short-no {
    font-weight: 700;
    color: #1e293b;
    font-family: 'JetBrains Mono', monospace;
  }
  .long-no {
    font-size: 12px;
    color: #94a3b8;
    transform: scale(0.95);
    transform-origin: left;
    margin-top: -2px;
  }

  .package-no {
    font-size: 12px;
    color: #64748b;
    margin-top: 4px;
    padding-top: 4px;
    border-top: 1px dashed #f1f5f9;

    .label { color: #94a3b8; margin-right: 4px; }
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
    }
  }
}

.address-cell {
  font-size: 12px;
  color: #475569;
  line-height: 1.4;
}

.status-tag {
  border: none;
  border-radius: 6px;
  padding: 2px 8px;
  font-weight: 600;
  font-size: 11px;
  
  &.logistics-shipped { background: #eff6ff; color: #2563eb; }
  &.logistics-delivered { background: #ecfeff; color: #0891b2; }
  &.logistics-completed { background: #f0fdf4; color: #16a34a; }
  
  &.order-pending { background: #fff7ed; color: #ea580c; }
  &.order-processing { background: #eff6ff; color: #2563eb; }
  &.order-shipped { background: #eff6ff; color: #2563eb; }
  &.order-delivered { background: #ecfeff; color: #0891b2; }
  &.order-completed { background: #f0fdf4; color: #16a34a; }
  &.order-cancelling { background: #fffbeb; color: #d97706; }
  &.order-cancelled { background: #f1f5f9; color: #64748b; }
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
      &.ship { background: #3b82f6; }
      &.deliver { background: #06b6d4; }
      &.complete { background: #10b981; }
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

