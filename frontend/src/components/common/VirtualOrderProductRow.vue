<template>
  <div class="row">
    <div class="cell image">
      <a-image :src="data.image" :width="36" :height="36" :fallback="fallback" />
    </div>
    <div class="cell spu">{{ data.spu }}</div>
    <div class="cell sku">
      <a-tooltip :title="data.sku">
        <span class="sku-text">{{ data.sku }}</span>
      </a-tooltip>
      <a-tag v-if="data.isExchanged" color="orange" class="status-tag-mini">换货</a-tag>
    </div>
    <div class="cell qty">×{{ data.quantity }}</div>
    <div class="cell price">${{ data.price.toFixed(2) }}</div>
    <div class="cell pkg">
      <a-tag v-if="data.packageNo" color="blue" class="package-tag">{{ data.packageNo }}</a-tag>
      <span v-else class="text-gray">-</span>
    </div>
    <div class="cell status" v-if="data.status">
      <a-tag :color="statusColor(data.status)" class="status-tag">{{ data.status }}</a-tag>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { OrderProduct } from '@/types/order';

const props = defineProps<{
  data: OrderProduct & { isExchanged?: boolean };
}>();

const fallback = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzYiIGhlaWdodD0iMzYiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjM2IiBoZWlnaHQ9IjM2IiBmaWxsPSIjZjhmYWZjIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTAiIGZpbGw9IiM5NGEzYjgiIHRleHQtYW5jaG9yPSJtaWRkbGUiPk5vSW1nPC90ZXh0Pjwvc3ZnPg==';

const statusColor = (status: string) => {
  switch (status) {
    case '正常': return 'success';
    case '已退款': return 'error';
    case '换货中': return 'warning';
    default: return 'default';
  }
};
</script>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 70px 120px 200px 80px 100px 150px 100px;
  align-items: center;
  gap: 8px;
  min-height: 48px;
  padding: 6px 12px;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}
.cell { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.sku-text { font-weight: 600; color: #334155; font-family: monospace; }
.status-tag-mini { width: fit-content; font-size: 10px; line-height: 16px; padding: 0 6px; border-radius: 4px; margin-left: 6px; }
.package-tag { border-radius: 4px; font-family: monospace; }
</style>
