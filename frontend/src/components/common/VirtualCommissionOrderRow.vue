<template>
  <div class="row">
    <div class="cell order">
      <div class="short">{{ data.shortOrderNo || data.orderNo }}</div>
      <div class="long">{{ data.longOrderNo }}</div>
      <div class="pkg" v-if="data.packageNo">包裹号：{{ data.packageNo }}</div>
    </div>
    <div class="cell influencer">{{ data.influencerName || '-' }}</div>
    <div class="cell amount">${{ data.orderAmount }}</div>
    <div class="cell products">
      <div class="prod" v-for="(p, i) in (data.products || [])" :key="i">
        <span class="sku">{{ p.sku }}</span>
        <span class="qty">×{{ p.quantity }}</span>
        <span class="price">${{ Number(p.price).toFixed(2) }}</span>
      </div>
    </div>
    <div class="cell discount">{{ data.discountCode }}</div>
    <div class="cell type">
      <a-tag :color="data.commissionType === 'discount' ? 'purple' : 'blue'">
        {{ data.commissionType === 'discount' ? '折扣' : '红人' }}
      </a-tag>
    </div>
    <div class="cell rate">{{ data.commissionRate }}%</div>
    <div class="cell commission">${{ data.commissionAmount }}</div>
    <div class="cell time">
      <div v-if="data.timeInfo?.createTime">创建：{{ data.timeInfo.createTime }}</div>
      <div v-if="data.timeInfo?.paymentTime">支付：{{ data.timeInfo.paymentTime }}</div>
      <div v-if="data.timeInfo?.deliveredTime">妥投：{{ data.timeInfo.deliveredTime }}</div>
      <div v-if="data.timeInfo?.distributeTime">分佣：{{ data.timeInfo.distributeTime }}</div>
    </div>
    <div class="cell action">
      <a-button size="small" type="primary" @click.stop="detail">详情</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CommissionOrderRow } from '@/types/order';
const props = defineProps<{
  data: CommissionOrderRow;
  onDetail?: (data: CommissionOrderRow) => void;
}>();
const detail = () => props.onDetail?.(props.data);
</script>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 200px 180px 120px 250px 150px 100px 140px 120px 320px 100px;
  align-items: start;
  gap: 8px;
  min-height: 64px;
  padding: 8px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}
.cell { overflow: hidden; }
.cell:first-child, .cell:last-child { position: sticky; background: #fff; z-index: 1; }
.cell:first-child { left: 0; }
.cell:last-child { right: 0; }
.order .short { font-weight: 700; color: #1e293b; }
.order .long { font-size: 12px; color: #64748b; }
.order .pkg { font-size: 12px; color: #3b82f6; margin-top: 4px; }
.products .prod { font-size: 12px; color: #475569; }
.products .sku { font-weight: 600; color: #334155; margin-right: 6px; }
.products .qty { color: #64748b; margin: 0 6px; }
.products .price { color: #64748b; }
</style>
