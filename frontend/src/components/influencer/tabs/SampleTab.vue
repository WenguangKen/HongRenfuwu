<template>
  <div class="sample-tab">
    <div class="section-header">
      <div class="header-tips">共申请 {{ sampleOrders.length }} 份样品</div>
    </div>
    
    <div class="table-container">
      <a-table
        :columns="sampleColumns"
        :data-source="sampleOrders.filter(s => s.spu || s.sku || s.status)"
        :pagination="false"
        size="middle"
        :row-key="(record: SampleOrder, index: number) => `${record.spu}-${record.sku}-${index}`"
        class="premium-table"
        :scroll="{ y: 460 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'spuSku'">
            <div class="product-info">
              <div class="spu">SPU: {{ record.spu }}</div>
              <div class="sku">SKU: {{ record.variantTitle ? `${record.sku}-${record.variantTitle.replace(/\s*\/\s*/g, '-')}` : record.sku }}</div>
            </div>
          </template>

          <template v-else-if="column.key === 'image'">
            <div class="image-wrapper">
              <a-image
                :src="record.image"
                :width="42"
                :height="42"
                class="premium-image"
                :preview="true"
                :fallback="'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7lm77niYfliqDovb3lpLHotKU8L3RleHQ+PC9zdmc+'"
              />
            </div>
          </template>

          <template v-else-if="column.key === 'price'">
            <span class="price-text">${{ record.price }}</span>
          </template>

          <template v-else-if="column.key === 'orderPackage'">
            <div class="order-pkg-info">
              <div class="item" v-if="record.orderNo">
                <span class="label">订单</span>
                <span class="value">{{ record.orderNo }}</span>
              </div>
              <div class="item" v-if="record.packageNo">
                <span class="label">包裹</span>
                <span class="value">{{ record.packageNo }}</span>
              </div>
              <span v-if="!record.orderNo && !record.packageNo" class="empty">-</span>
            </div>
          </template>

          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)" class="status-pill">{{ record.status }}</a-tag>
          </template>

          <template v-else-if="column.key === 'time'">
            <div class="time-flow">
              <div class="time-item" v-if="record.orderTime"><span>下单:</span> {{ record.orderTime }}</div>
              <div class="time-item highlight" v-if="record.shipTime"><span>发货:</span> {{ record.shipTime }}</div>
              <div class="time-item success" v-if="record.deliveryTime"><span>签收:</span> {{ record.deliveryTime }}</div>
            </div>
          </template>

          <template v-else-if="column.key === 'creator'">
            <span class="creator-name">{{ record.creator || 'SYS' }}</span>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { TableColumnsType } from 'ant-design-vue';

interface SampleOrder {
  spu: string;
  sku: string;
  variantTitle?: string;
  image: string;
  quantity: number;
  price: number;
  orderNo?: string;
  packageNo?: string;
  status: string;
  orderTime?: string;
  shipTime?: string;
  deliveryTime?: string;
  creator?: string;
}

const props = defineProps<{
  sampleOrders: SampleOrder[];
}>();

const sampleColumns: TableColumnsType = [
  { title: '商品标识', key: 'spuSku', width: 120 },
  { title: '商品图', key: 'image', width: 70, align: 'center' },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 40, align: 'center' },
  { title: '单价', key: 'price', width: 80, align: 'right' },
  { title: '单号/包裹', key: 'orderPackage', width: 140 },
  { title: '状态', key: 'status', width: 80, align: 'center' },
  { title: '时间轴', key: 'time', width: 160 },
  { title: '操作人', key: 'creator', width: 60, align: 'right' },
];

const getStatusColor = (status: string) => {
  if (status.includes('待')) return 'orange';
  if (status.includes('完') || status.includes('妥')) return 'success';
  if (status.includes('退') || status.includes('拒')) return 'error';
  return 'processing';
};
</script>

<style lang="scss" scoped>
.sample-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px; /* 统一压缩间距 */
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
  :deep(.ant-table) { background: transparent; }
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc !important;
    color: #475569 !important;
    font-weight: 700 !important;
    font-size: 13px !important;
    border-bottom: 1px solid #f1f5f9 !important;
    padding: 12px !important; /* 压缩表头高度 */
  }
  :deep(.ant-table-tbody > tr > td) {
    padding: 6px 12px !important; /* 极致压缩垂直间隙 */
    border-bottom: 1px solid #f8fafc !important;
    transition: all 0.2s;
  }
}

.product-info {
  .spu { font-weight: 700; color: #1e293b; font-size: 13px; }
  .sku { font-size: 11px; color: #94a3b8; font-family: 'JetBrains Mono', monospace; }
}

.image-wrapper {
  .premium-image { border-radius: 8px; border: 1px solid #f1f5f9; object-fit: cover; }
}

.price-text { font-family: 'JetBrains Mono', monospace; font-weight: 700; color: #1e293b; }

.order-pkg-info {
  display: flex; flex-direction: column; gap: 4px;
  .item {
    display: flex; gap: 4px; align-items: center; white-space: nowrap;
    .label { font-size: 10px; padding: 1px 4px; background: #f1f5f9; color: #64748b; border-radius: 4px; font-weight: 700; flex-shrink: 0; }
    .value { font-size: 12px; font-weight: 600; color: #475569; font-family: 'JetBrains Mono', monospace; }
  }
  .empty { color: #cbd5e1; }
}

.status-pill { border: none; font-weight: 700; padding: 2px 10px; border-radius: 6px; }

.time-flow {
  display: flex; flex-direction: column; gap: 2px;
  .time-item {
    font-size: 11px; color: #94a3b8;
    span { font-weight: 700; color: #64748b; margin-right: 4px; }
    &.highlight { color: #6366f1; span { color: #4f46e5; } }
    &.success { color: #10b981; span { color: #059669; } }
  }
}

.creator-name {
  font-size: 12px;
  font-weight: 600;
  color: #475569;
  text-align: right;
}
</style>
