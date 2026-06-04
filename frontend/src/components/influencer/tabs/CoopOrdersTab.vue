<template>
  <div class="sample-tab">
    <div class="section-header">
      <div class="header-tips">共关联 {{ orders.length }} 个合作订单</div>
    </div>
    
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="flatOrders"
        :pagination="false"
        size="middle"
        :row-key="(record: any, index: number) => `${record.orderNo}-${index}`"
        class="premium-table"
        :scroll="{ y: 460 }"
        :loading="loading"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'orderNo'">
            <div class="order-no-text">{{ record.orderNo }}</div>
          </template>

          <template v-else-if="column.key === 'time'">
            <div class="time-flow">
              <div class="time-item" v-if="record.orderTime"><span>下单:</span> {{ record.orderTime }}</div>
              <div class="time-item" v-if="record.deliveryTime"><span>签收:</span> {{ record.deliveryTime }}</div>
              <div class="time-item" v-else-if="record.orderTime"><span>签收:</span> <a-tag size="small" :bordered="false" style="margin-left: 4px;">未签收</a-tag></div>
              <div v-if="!record.orderTime && !record.deliveryTime">-</div>
            </div>
          </template>

          <template v-else-if="column.key === 'sku'">
            <div class="spu-list" v-if="record.products && record.products.length > 0">
              <div v-for="(p, idx) in record.products" :key="idx" style="display: flex; align-items: center; justify-content: space-between; flex-wrap: nowrap; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 4px 8px; margin-bottom: 4px; width: 100%; box-sizing: border-box; overflow: hidden; white-space: nowrap;">
                <div style="display: flex; align-items: center; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; margin-right: 8px;">
                  <span style="color: #0369a1; font-weight: 700; font-size: 13px; font-family: monospace;">{{ p.sku || p.variantTitle || p.shopifyVariantId || '-' }}</span>
                  <template v-if="p.variantTitle && p.sku !== p.variantTitle">
                    <span v-for="(spec, sIdx) in String(p.variantTitle).split('/')" :key="sIdx"
                          :style="{ 
                            color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3], 
                            fontWeight: '600',
                            fontSize: '13px'
                          }">
                      -{{ spec.trim().replace(/\s+/g, '') }}
                    </span>
                  </template>
                </div>
                <div style="display: flex; align-items: center; flex-shrink: 0;">
                  <span style="color: #ef4444; font-weight: 700; font-size: 14px;">×{{ p.quantity || 1 }}</span>
                </div>
              </div>
            </div>
            <div v-else class="spu-text">-</div>
          </template>
          
          <template v-else-if="column.key === 'price'">
            <div class="price-edit-cell">
              <a-input-number
                v-model:value="record.cooperationPrice"
                :min="0"
                :precision="2"
                :controls="false"
                placeholder="输入价格"
                size="small"
                class="price-input"
                :formatter="(val: any) => val ? `$ ${val}` : ''"
                :parser="(val: any) => val ? val.replace(/\$\s?/g, '') : ''"
              />
              <a-button type="link" size="small" class="save-price-btn" @click="saveCoopPrice(record)" :loading="record._saving">
                保存
              </a-button>
            </div>
          </template>

          <template v-else-if="column.key === 'materials'">
            <div class="material-link" @click="viewMaterials(record)">
              <span class="count">{{ record.materialCount || 0 }}</span> 条
            </div>
          </template>

        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { message } from 'ant-design-vue';
import { getSampleOrdersByInfluencer } from '@/services/influencerOrderService';
import contentService from '@/services/contentService';
import type { TableColumnsType } from 'ant-design-vue';

const props = defineProps<{
  influencerId: number;
}>();

const orders = ref<any[]>([]);
const flatOrders = ref<any[]>([]);
const loading = ref(false);

const columns: TableColumnsType = [
  { title: '交易号', key: 'orderNo', width: 140 },
  { title: '时间线', key: 'time', width: 180 },
  { title: 'SKU', key: 'sku', width: 340 },
  { title: '已回收素材', key: 'materials', width: 100, align: 'center' },
  { title: '合作价格', key: 'price', width: 160, align: 'center' },
];

const loadOrders = async () => {
    if (!props.influencerId) return;
    loading.value = true;
    try {
        const [response, contentsRes] = await Promise.all([
            getSampleOrdersByInfluencer(props.influencerId),
            contentService.getContents({ influencerId: props.influencerId, size: 9999 })
        ]);
        
        orders.value = response || [];
        const contents = contentsRes?.content || [];
        
        const list: any[] = [];
        for (const order of orders.value) {
          const oNo = String(order.orderNo || order.orderName || '');
          let oNoSafe = oNo.startsWith('#') ? oNo.substring(1) : oNo;

          let mCount = 0;
          contents.forEach(c => {
             const cOrderStr = String(c.orderNo || '');
             if (oNoSafe && cOrderStr.includes(oNoSafe)) {
                 if (c.filePath || c.postUrl || c.description) {
                     mCount++;
                 }
             }
          });

          list.push({
            products: order.products || [],
            price: parseFloat(String(order.totalPrice)) || 0,
            cooperationPrice: order.cooperationPrice != null ? parseFloat(String(order.cooperationPrice)) : undefined,
            orderNo: oNo,
            orderId: order.id,
            status: order.status,
            orderTime: order.orderCreatedAt || order.createdAt,
            deliveryTime: order.deliveredAt || (order.fulfillmentStatus === 'fulfilled' && order.updatedAt ? order.updatedAt : undefined),
            materialCount: mCount,
            _saving: false
          });
        }
        flatOrders.value = list;

    } catch (e) {
        console.error('Fetch coop orders error', e);
        flatOrders.value = [];
    } finally {
        loading.value = false;
    }
}

watch(() => props.influencerId, (newId) => {
    if (newId) {
        loadOrders();
    }
}, { immediate: true });

const viewMaterials = (record: any) => {
    // TODO: implementation for viewing bound materials
    console.log('View materials for SP', record);
}

const saveCoopPrice = async (record: any) => {
    if (record.cooperationPrice == null && record.cooperationPrice !== 0) {
        return;
    }
    record._saving = true;
    try {
        const { updateSampleOrderCoopPrice } = await import('@/services/influencerOrderService');
        await updateSampleOrderCoopPrice(record.orderId, record.cooperationPrice);
        message.success('合作价格已保存');
    } catch (e) {
        console.error('Save coop price error', e);
        message.error('保存失败');
    } finally {
        record._saving = false;
    }
}

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
  margin-bottom: 12px; 
  .header-tips { font-size: 13px; color: #94a3b8; font-weight: 500; }
}

.table-container { 
  flex: 1; 
  border-radius: 10px; 
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
    padding: 10px 12px !important; 
  }
  :deep(.ant-table-tbody > tr > td) {
    padding: 10px 12px !important;
    border-bottom: 1px solid #f8fafc !important;
    transition: all 0.2s;
  }
}

.order-no-text {
  font-weight: 700; color: #1e293b; font-size: 13px; font-family: 'JetBrains Mono', monospace;
}

.spu-text {
  color: #334155; font-weight: 600;
}

.price-edit-cell {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: center;
}

.price-input {
  width: 100px !important;
  :deep(.ant-input-number-input) {
    text-align: center;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 600;
    font-size: 13px;
  }
}

.save-price-btn {
  font-size: 12px;
  font-weight: 600;
  padding: 0 4px;
  color: #3b82f6;
  &:hover { color: #2563eb; }
}

.material-link {
   cursor: pointer;
   color: #3b82f6;
   font-weight: 500;
   transition: all 0.2s;
   .count {
       text-decoration: underline;
       font-weight: 700;
       font-size: 14px;
   }
   &:hover {
       color: #2563eb;
   }
}

.time-flow {
  display: flex; flex-direction: column; gap: 4px;
  .time-item {
    font-size: 12px; color: #64748b;
    span { font-weight: 700; color: #94a3b8; margin-right: 4px; }
  }
}
</style>
