<template>
  <div class="order-products-tab">
    <!-- 主订单商品 -->
    <div class="products-section">
      <div class="section-header">
        <div class="section-title">订单商品</div>
        <div class="section-subtitle">共 {{ mainOrderProducts.length }} 件商品</div>
      </div>
      <template v-if="mainOrderProducts.length <= 200">
        <a-table
          :columns="productColumns"
          :data-source="mainOrderProducts"
          :pagination="false"
          :scroll="{ y: 1000 }"
          size="small"
          class="premium-table flex-table"
          :row-key="(record: any, index: number) => `${record.sku}-${index}`"
        >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <div class="product-image-wrapper">
              <div class="product-image">
                <a-image 
                  v-if="record.image && record.image !== placeholderImage" 
                  :src="record.image" 
                  :alt="record.sku"
                  :preview="true"
                  class="clickable-image"
                  :fallback="placeholderImage"
                />
                <div v-else class="image-placeholder">
                  <a-image :src="placeholderImage" :preview="false" :width="24" style="opacity: 0.5" />
                  <span>无图</span>
                </div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'spu'">
            <span class="spu-text">{{ record.spu || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'sku'">
            <div class="product-sku">
              <div class="sku-colors">
                <span class="sku-main">{{ record.sku || '-' }}</span>
                <template v-if="record.variantTitle">
                  <span v-for="(spec, sIdx) in String(record.variantTitle).split('/')" :key="sIdx"
                        :style="{ color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3] }"
                        class="sku-spec"
                  >
                    -{{ spec.trim().replace(/\s+/g, '') }}
                  </span>
                </template>
              </div>
              <a-tag v-if="record.isExchanged" color="orange" class="status-tag-mini">换货</a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'quantity'">
            <span class="quantity-text">×{{ record.quantity }}</span>
          </template>
          <template v-else-if="column.key === 'price'">
            <span class="price-text">${{ record.price?.toFixed(2) || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'packageNo'">
            <a-tag v-if="record.packageNo" color="blue" class="package-tag">{{ record.packageNo }}</a-tag>
            <span v-else class="text-gray">-</span>
          </template>
          <template v-else-if="column.key === 'status' && record.status">
            <a-tag 
              :color="getStatusColor(record.status)" 
              class="status-tag"
            >
              {{ record.status }}
            </a-tag>
          </template>
        </template>
        </a-table>
      </template>
      <template v-else>
        <div class="virtual-header">
          <div class="hcell image">商品图片</div>
          <div class="hcell spu">SPU</div>
          <div class="hcell sku">SKU</div>
          <div class="hcell qty">数量</div>
          <div class="hcell price">单价</div>
          <div class="hcell pkg">关联包裹号</div>
          <div class="hcell status">状态</div>
        </div>
        <VirtualList
          :data-key="'sku'"
          :data-sources="mainOrderProducts"
          :keeps="20"
          :estimate-size="48"
          :data-component="VirtualOrderProductRow"
          class="virtual-list"
        />
      </template>
    </div>

    <!-- 订单商品变更记录 -->
    <div class="change-section" v-if="changeList && changeList.length > 0">
      <div class="section-header">
        <div class="section-title">商品变更记录</div>
        <a-tag color="orange">{{ changeList.length }} 条变更</a-tag>
      </div>
      
      <div class="change-timeline">
        <div 
          v-for="(change, index) in changeList" 
          :key="index"
          class="change-card-wrapper"
        >
          <div class="timeline-line" v-if="index !== changeList.length - 1"></div>
          <div class="timeline-dot"></div>
          
          <div class="change-card compact-card">
            <div class="change-main-row">
              <div class="change-left">
                <!-- 类型标签 -->
                <span class="change-type-tag" :class="change.type">
                  {{ change.type === 'exchange' ? '换货' : '退款' }}
                </span>
                
                <!-- 换货内容 -->
                <div v-if="change.type === 'exchange'" class="change-content-inline">
                  <div class="sku-colors delete">
                    <span class="sku-main">{{ change.oldProduct?.sku || '未指定商品' }}</span>
                    <template v-if="change.oldProduct?.variantTitle">
                      <span v-for="(spec, sIdx) in String(change.oldProduct.variantTitle).split('/')" :key="sIdx"
                            :style="{ color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3] }"
                            class="sku-spec"
                      >-{{ spec.trim().replace(/\s+/g, '') }}</span>
                    </template>
                  </div>
                  <span class="qty-text" v-if="change.oldProduct?.quantity">×{{ change.oldProduct?.quantity }}</span>
                  <span class="arrow-icon">➔</span>
                  <div class="sku-colors">
                    <span class="sku-main">{{ change.newProduct?.sku || '未指定商品' }}</span>
                    <template v-if="change.newProduct?.variantTitle">
                      <span v-for="(spec, sIdx) in String(change.newProduct.variantTitle).split('/')" :key="sIdx"
                            :style="{ color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3] }"
                            class="sku-spec"
                      >-{{ spec.trim().replace(/\s+/g, '') }}</span>
                    </template>
                  </div>
                  <span class="qty-text" v-if="change.newProduct?.quantity">×{{ change.newProduct?.quantity }}</span>
                </div>
                
                <!-- 退款内容 -->
                <div v-else-if="change.type === 'refund'" class="change-content-inline">
                  <template v-if="change.product">
                    <div class="sku-colors">
                      <span class="sku-main">{{ change.product.sku || '未知商品' }}</span>
                      <template v-if="change.product.variantTitle">
                        <span v-for="(spec, sIdx) in String(change.product.variantTitle).split('/')" :key="sIdx"
                              :style="{ color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3] }"
                              class="sku-spec"
                        >-{{ spec.trim().replace(/\s+/g, '') }}</span>
                      </template>
                    </div>
                    <span class="qty-text" v-if="change.product.quantity">×{{ change.product.quantity }}</span>
                  </template>
                  <template v-else>
                    <span class="sku-text">全单/取消</span>
                  </template>
                  <span class="refund-amount-text" v-if="change.refundAmount">退款: ${{ change.refundAmount?.toFixed(2) || '0.00' }}</span>
                </div>
              </div>

              <div class="change-right">
                <span class="change-time">{{ change.time }}</span>
              </div>
            </div>

            <!-- 原因行 (如果有) -->
            <div class="change-reason-row" v-if="change.reason">
              <span class="reason-label">原因:</span>
              <span class="reason-text">{{ change.reason }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import VirtualList from 'vue3-virtual-scroll-list';
import VirtualOrderProductRow from '@/components/common/VirtualOrderProductRow.vue';
import type { OrderProduct } from '@/types/order';

const props = defineProps<{
  orderData: any;
}>();

const placeholderImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTRhM2I4IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHJlY3QgeD0iMyIgeT0iMyIgd2lkdGg9IjE4IiBoZWlnaHQ9IjE4IiByeD0iMiIgcnk9IjIiPjwvcmVjdD48Y2lyY2xlIGN4PSI4LjUiIGN5PSI4LjUiIHI9IjEuNSI+PC9jaXJjbGU+PHBvbHlsaW5lIHBvaW50cz0iMjEgMTUgMTYgMTAgNSAyMSI+PC9wb2x5bGluZT48L3N2Zz4=';

// 主订单商品（包含所有商品，每个商品关联对应的包裹号）
const mainOrderProducts = computed<OrderProduct[]>(() => {
  if (!props.orderData?.products) return [];
  
  return props.orderData.products.map((p: any): OrderProduct & { isExchanged?: boolean } => {
    // 根据商品SKU找到对应的包裹号
    let packageNo = props.orderData.packageNo; // 默认使用主单包裹号
    
    // 如果商品在被拆出去的商品列表中，使用拆单包裹号
    if (props.orderData.isSplit && Array.isArray(props.orderData.splitProducts)) {
      const isSplitProduct = props.orderData.splitProducts.some((sp: any) => sp?.sku === p?.sku);
      if (isSplitProduct && props.orderData.packageNo) {
        packageNo = `${props.orderData.packageNo}-SPLIT`;
      }
    }
    
    return {
      ...p,
      image: p.image || placeholderImage,
      spu: p.spu || p.sku?.split('-')[0] || '-', // 如果没有SPU，从SKU提取
      packageNo: packageNo,
    };
  });
});

// 订单商品变更列表（包含换货和退款）
const changeList = computed(() => {
  const changes: Array<
    | { type: 'exchange'; time?: string; oldProduct: OrderProduct; newProduct: OrderProduct; reason?: string }
    | { type: 'refund'; time?: string; product: OrderProduct; refundAmount?: number; reason?: string }
  > = [];
  
  // 添加换货记录
  if (props.orderData?.exchanges) {
    props.orderData.exchanges.forEach((exchange: any) => {
      changes.push({
        type: 'exchange',
        time: exchange.time,
        oldProduct: exchange.oldProduct,
        newProduct: exchange.newProduct,
        reason: exchange.reason,
      });
    });
  }
  
  // 添加退款记录
  if (props.orderData?.refunds) {
    props.orderData.refunds.forEach((refund: any) => {
      changes.push({
        type: 'refund',
        time: refund.time,
        product: refund.product,
        refundAmount: refund.amount,
        reason: refund.reason,
      });
    });
  }
  
  // 按时间倒序排列
  return changes.sort((a, b) => {
    if (!a.time || !b.time) return 0;
    return new Date(b.time).getTime() - new Date(a.time).getTime();
  });
});

const productColumns = [
  {
    title: '商品图片',
    key: 'image',
    width: 100,
    align: 'center',
  },
  {
    title: 'SPU',
    key: 'spu',
    dataIndex: 'spu',
    width: 120,
  },
  {
    title: 'SKU',
    key: 'sku',
    dataIndex: 'sku',
    width: 350,
  },
  {
    title: '数量',
    key: 'quantity',
    dataIndex: 'quantity',
    width: 80,
    align: 'center',
  },
  {
    title: '单价',
    key: 'price',
    dataIndex: 'price',
    width: 100,
    align: 'right',
  },
  {
    title: '关联包裹号',
    key: 'packageNo',
    dataIndex: 'packageNo',
    width: 150,
  },
  {
    title: '状态',
    key: 'status',
    dataIndex: 'status',
    width: 100,
    align: 'center',
  },
];

// SVG placeholder for missing images
const defaultProductImage = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjOTRhM2I4IiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHJlY3QgeD0iMyIgeT0iMyIgd2lkdGg9IjE4IiBoZWlnaHQ9IjE4IiByeD0iMiIgcnk9IjIiPjwvcmVjdD48Y2lyY2xlIGN4PSI4LjUiIGN5PSI4LjUiIHI9IjEuNSI+PC9jaXJjbGU+PHBvbHlsaW5lIHBvaW50cz0iMjEgMTUgMTYgMTAgNSAyMSI+PC9wb2x5bGluZT48L3N2Zz4=';

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement;
  if (!target) return;
  
  // Prevent infinite loop if default image also fails
  if (target.src === defaultProductImage) return;
  
  target.src = defaultProductImage;
  target.style.objectFit = 'contain';
  target.style.padding = '8px';
  target.style.background = '#f1f5f9';
};

const getStatusColor = (status: string) => {
  switch (status) {
    case '正常': return 'success';
    case '已退款': return 'error';
    case '换货中': return 'warning';
    default: return 'default';
  }
};
</script>

<style lang="scss" scoped>
.order-products-tab {
  padding: 0 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.products-section {
  margin-bottom: 8px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;

  .section-header {
    display: flex;
    align-items: baseline;
    gap: 12px;
    margin-bottom: 8px;
    padding-left: 4px;
    flex-shrink: 0;
  }

  .section-title {
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
  }
  
  .section-subtitle {
    font-size: 12px;
    color: #64748b;
  }
}

/* 表格样式优化 */
.premium-table {
  &.flex-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;

    :deep(.ant-spin-nested-loading),
    :deep(.ant-spin-container),
    :deep(.ant-table),
    :deep(.ant-table-container) {
      flex: 1;
      display: flex;
      flex-direction: column;
      min-height: 0;
    }

    :deep(.ant-table-body) {
      flex: 1 !important;
      height: 0 !important;
    }
  }

  :deep(.ant-table) {
    border-radius: 8px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  }
  
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
    font-size: 12px;
    padding: 6px 8px;
  }

  :deep(.ant-table-tbody > tr > td) {
    padding: 4px 8px;
    font-size: 13px;
  }
}

.product-image-wrapper {
  display: flex;
  justify-content: center;
}

.product-image {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .image-placeholder {
    font-size: 10px;
    color: #94a3b8;
  }
}

.spu-text {
  color: #64748b;
  font-family: monospace;
}

.product-sku {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sku-colors {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
  white-space: nowrap;
  font-family: monospace;
  font-size: 14px;
  
  &.delete {
    text-decoration: line-through;
    opacity: 0.6;
    
    .sku-main, .sku-spec {
      text-decoration: line-through;
    }
  }

  .sku-main {
    font-weight: 700;
    color: #0369a1;
  }
  
  .sku-spec {
    font-weight: 600;
  }
}

.status-tag-mini {
  width: fit-content;
  font-size: 10px;
  line-height: 16px;
  padding: 0 6px;
  border-radius: 4px;
}

.quantity-text {
  font-weight: 600;
  color: #334155;
}

.price-text {
  font-family: monospace;
  color: #334155;
}

.package-tag {
  border-radius: 4px;
  font-family: monospace;
}

.status-tag {
  min-width: 60px;
  text-align: center;
  border-radius: 4px;
}

/* 变更记录时间轴样式 */
.change-section {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding-bottom: 12px;

  .section-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    padding-left: 4px;
    position: sticky;
    top: 0;
    background: #fff;
    z-index: 10;
    padding-top: 8px;
    
    .section-title {
      font-size: 15px;
      font-weight: 700;
      color: #1e293b;
    }
  }
}

.change-timeline {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding-left: 12px;
}

.change-card-wrapper {
  position: relative;
  padding-left: 32px;
  padding-bottom: 12px;
  
  &:last-child {
    padding-bottom: 0;
  }
  
  .timeline-line {
    position: absolute;
    left: 5px;
    top: 24px;
    bottom: -24px;
    width: 2px;
    background: #e2e8f0;
  }
  
  .timeline-dot {
    position: absolute;
    left: 0;
    top: 6px;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: #fff;
    border: 3px solid #6366f1;
    z-index: 2;
  }
}

.change-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  background: #fff;
  overflow: hidden;
  margin-bottom: 12px;
  transition: all 0.2s;

  &:hover {
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  }
}

.compact-card {
  padding: 8px 12px;
}

.change-main-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.change-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}

.change-type-tag {
  font-size: 12px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  white-space: nowrap;
  
  &.exchange {
    background: #fff7ed;
    color: #f59e0b;
    border: 1px solid #ffedd5;
  }
  
  &.refund {
    background: #fef2f2;
    color: #ef4444;
    border: 1px solid #fee2e2;
  }
}

.change-content-inline {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #334155;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  
  .sku-text {
    font-family: monospace;
    font-weight: 600;
    
    &.delete {
      text-decoration: line-through;
      color: #94a3b8;
    }
  }
  
  .qty-text {
    color: #64748b;
    font-size: 12px;
  }
  
  .arrow-icon {
    color: #94a3b8;
    font-size: 12px;
    margin: 0 2px;
  }
  
  .refund-amount-text {
    color: #ef4444;
    font-weight: 600;
    margin-left: 4px;
    font-size: 12px;
  }
}

.change-right {
  flex-shrink: 0;
}

.change-time {
  font-size: 12px;
  color: #94a3b8;
}

.change-reason-row {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #f1f5f9;
  font-size: 12px;
  color: #64748b;
  
  .reason-label {
    font-weight: 600;
    margin-right: 4px;
  }
}
</style>
