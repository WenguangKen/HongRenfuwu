<template>
  <div class="row">
    <div class="cell order">
      <div class="short">{{ data.shortOrderNo }}</div>
      <div class="long">{{ data.longOrderNo }}</div>
      <div class="pkg" v-if="data.packageNo">包裹号：{{ data.packageNo }}</div>
    </div>
    <div class="cell influencer">{{ data.influencer || '-' }}</div>
    <div class="cell owner">{{ data.ownerName || '-' }}</div>
    <div class="cell liaison">{{ data.contactPersonName || '-' }}</div>
    <div class="cell products">
      <div class="prod" v-for="(p, i) in (data.products || [])" :key="i">
        <div class="prod-left">
          <span v-if="p.variantTitle" style="display: inline-flex; width: 280px; flex-shrink: 0; margin-right: 8px;">
            <span v-for="(part, pIdx) in String(p.variantTitle).split('/')" :key="pIdx"
                  :style="{ 
                    color: ['#0284c7', '#16a34a', '#ea580c', '#8b5cf6'][pIdx % 4], 
                    fontWeight: '600', 
                    fontSize: '13px', 
                    marginRight: '4px',
                    display: 'inline-block',
                    width: pIdx === 0 ? '130px' : (pIdx === 1 ? '70px' : '70px'),
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'nowrap'
                  }">
              {{ part.trim() }}
            </span>
          </span>
          <span class="sku">{{ p.sku || p.shopifyVariantId || '无ID' }}</span>
        </div>
        <div class="prod-right">
          <span class="qty">×{{ effectiveQty(p) }}</span>
          <span v-if="(p.returnedQuantity ?? 0) > 0" class="returned">(退{{ p.returnedQuantity }})</span>
          <span class="price">${{ Number(p.price).toFixed(2) }}</span>
          <span v-if="p.status && p.status !== '正常'" class="status">{{ p.status }}</span>
        </div>
      </div>
    </div>
    <div class="cell orderStatus">{{ data.orderStatus }}</div>
    <div class="cell logistics">{{ data.logisticsStatus || '-' }}</div>
    <div class="cell recipient">{{ data.recipientAddress || '-' }}</div>
    <div class="cell receiver">{{ data.receiverName || '-' }}</div>
    <div class="cell time">
      <div v-if="data.timeInfo?.createTime">创建：{{ data.timeInfo.createTime }}</div>
      <div v-if="data.timeInfo?.shipTime">发货：{{ data.timeInfo.shipTime }}</div>
      <div v-if="data.timeInfo?.deliveredTime">妥投：{{ data.timeInfo.deliveredTime }}</div>
      <div v-if="data.timeInfo?.completedTime">完成：{{ data.timeInfo.completedTime }}</div>
    </div>
    <div class="cell action">
      <a-space :size="4">
        <a-button size="small" type="primary" @click.stop="detail">详情</a-button>
          <template v-if="data.isDraft || data.draft || (data.orderNo && data.orderNo.startsWith('DRAFT-'))">
            <a-button type="link" size="small" @click.stop="confirm">
              确认创建
            </a-button>
            <a-button type="link" size="small" @click.stop="edit">
              编辑
            </a-button>
            <a-button type="link" size="small" danger @click.stop="remove">
              删除
            </a-button>
          </template>



      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { SampleOrderRow } from '@/types/order';
const props = defineProps<{
  data: SampleOrderRow;
  onDetail?: (data: SampleOrderRow) => void;
  onEdit?: (data: SampleOrderRow) => void;
  onConfirm?: (data: SampleOrderRow) => void;
  onDelete?: (data: SampleOrderRow) => void;
}>();

// 计算有效数量（原数量 - 退货数量）
const effectiveQty = (p: any): number => {
  const qty = p.quantity || 0;
  const returned = p.returnedQuantity || 0;
  return Math.max(qty - returned, 0);
};

const detail = () => props.onDetail?.(props.data);
const edit = () => props.onEdit?.(props.data);
const confirm = () => props.onConfirm?.(props.data);
const remove = () => props.onDelete?.(props.data);
</script>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 190px 130px 100px 100px 350px 120px 120px 200px 120px 200px 100px;
  align-items: start;
  gap: 8px;
  min-height: 64px;
  padding: 8px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}
.cell { overflow: hidden; }
.cell.recipient {
  white-space: normal;
  word-break: break-word;
  line-height: 1.4;
  font-size: 12px;
  color: #475569;
}
.cell:first-child, .cell:last-child { position: sticky; background: #fff; z-index: 1; }
.cell:first-child { left: 0; }
.cell:last-child { right: 0; }
.order .short { font-weight: 700; color: #1e293b; }
.order .long { font-size: 12px; color: #64748b; }
.order .pkg { font-size: 12px; color: #3b82f6; margin-top: 4px; }
.products .prod { 
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: nowrap;
  gap: 4px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 4px 8px;
  margin-bottom: 6px;
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;
  white-space: nowrap;
  font-size: 13px; 
  color: #475569; 
}
.products .prod-left {
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.products .prod-left .variant-col {
  display: inline-block;
  width: 190px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-shrink: 0;
  margin-right: 8px;
}
.products .prod-right {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
.products .prod:last-child { margin-bottom: 0; }
.products .variant-title { color: #0ea5e9; font-weight: 500; font-size: 13px; }
.products .sku { 
  color: #0369a1; 
  background: #f0f9ff; 
  border: 1px solid #bae6fd; 
  font-weight: 700; 
  font-size: 12px; 
  font-family: monospace; 
  padding: 1px 6px; 
  border-radius: 4px; 
  margin-left: 2px;
}
.products .qty { color: #ef4444; font-weight: 600; margin: 0 6px; }
.products .price { color: #64748b; }
.products .returned { 
  color: #ef4444; 
  font-weight: 600; 
  margin-left: 4px;
  padding: 1px 6px;
  background: #fef2f2;
  border-radius: 4px;
  font-size: 11px;
}
.products .status {
  margin-left: 6px;
  padding: 1px 6px;
  background: #fef2f2;
  color: #ef4444;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}
</style>
