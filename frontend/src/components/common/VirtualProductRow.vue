<template>
  <div class="row">
    <div class="cell image">
      <img :src="data.image" alt="" class="img" @click.stop="preview" />
    </div>
    <div class="cell name">
      <div class="title">{{ data.name }}</div>
      <div class="sid">{{ data.shopifyId }}</div>
    </div>
    <div class="cell sku">{{ data.sku }}</div>
    <div class="cell price">${{ data.price }}</div>
    <div class="cell link">
      <a-button size="small" type="primary" @click.stop="openLink">查看商品</a-button>
    </div>
    <div class="cell status">
      <a-tag v-if="data.status === 'active'" class="status-tag tag-success">已上架</a-tag>
      <a-tag v-else-if="data.status === 'inactive'" class="status-tag tag-black">下架</a-tag>
      <a-tag v-else-if="data.status === 'draft'" class="status-tag tag-purple">草稿</a-tag>
      <a-tag v-else-if="data.status === 'out_of_stock'" class="status-tag tag-red">缺货</a-tag>
      <span v-else>{{ data.status }}</span>
    </div>
    <div class="cell inventory">{{ data.inventory }}</div>
    <div class="cell time">
      <div v-if="data.createTime">创建: {{ data.createTime }}</div>
      <div v-if="data.publishTime">上架: {{ data.publishTime }}</div>
      <div v-if="data.offShelfTime">下架: {{ data.offShelfTime }}</div>
      <div v-if="data.outOfStockTime">缺货: {{ data.outOfStockTime }}</div>
      <div v-if="data.suspectedDeletedTime">疑删: {{ data.suspectedDeletedTime }}</div>
    </div>
    <div class="cell action">
      <a-button size="small" type="primary" @click.stop="detail">详情</a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ProductItem } from '@/types/product';
const props = defineProps<{
  data: ProductItem;
  onDetail?: (data: ProductItem) => void;
  onPreview?: (url: string) => void;
}>();

const detail = () => props.onDetail?.(props.data);
const preview = () => props.onPreview?.(props.data.image);
const openLink = () => {
  const url = props.data.link;
  if (url) window.open(url, '_blank');
};
</script>

<style scoped>
.row {
  display: grid;
  grid-template-columns: 70px 250px 150px 120px 150px 120px 120px 220px 100px;
  align-items: center;
  gap: 8px;
  height: 64px;
  padding: 8px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #fff;
}
.cell { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; }
.cell:first-child, .cell:last-child { position: sticky; background: #fff; z-index: 1; }
.cell:first-child { left: 0; }
.cell:last-child { right: 0; }
.image .img {
  width: 44px; height: 44px; border-radius: 8px; object-fit: cover;
}
.name .title { font-weight: 600; color: #1e293b; }
.name .sid { font-size: 12px; color: #94a3b8; font-family: monospace; }
</style>
