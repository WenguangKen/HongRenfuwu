<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    centered
    class="premium-refined-modal"
  >
    <img :src="url" style="width: 100%; border-radius: 8px;" alt="商品图片预览" />
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  open: boolean;
  url: string;
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});
</script>

