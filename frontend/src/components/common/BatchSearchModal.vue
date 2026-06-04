<template>
  <a-modal
    v-model:open="visible"
    title="批量查询"
    @ok="handleOk"
    @cancel="handleCancel"
    width="500px"
    centered
    class="premium-refined-modal"
  >
    <div class="batch-search-tip" style="margin-bottom: 12px; font-size: 13px; color: #64748b;">
      请输入查询项，多个项请用换行或逗号分隔。
    </div>
    <a-textarea
      v-model:value="inputValue"
      placeholder="例如：
OFF50
SAVE20
WELCOME"
      :rows="10"
      class="premium-textarea"
    />
    <template #footer>
      <a-button @click="handleCancel">取消</a-button>
      <a-button type="primary" @click="handleOk" class="primary-gradient">确认搜索</a-button>
    </template>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{
  open: boolean;
}>();

const emit = defineEmits(['update:open', 'search']);

const visible = ref(false);
const inputValue = ref('');

watch(() => props.open, (val) => {
  visible.value = val;
  if (val) {
    inputValue.value = '';
  }
});

watch(visible, (val) => {
  emit('update:open', val);
});

const handleOk = () => {
  if (!inputValue.value.trim()) {
    emit('search', []);
    visible.value = false;
    return;
  }

  // Split by newline, comma, or space, and filter out empty strings
  const results = inputValue.value
    .split(/[\n,\s]+/)
    .map(item => item.trim())
    .filter(item => item.length > 0);

  emit('search', results);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
};
</script>

<style scoped>
.premium-textarea {
  border-radius: 12px;
  padding: 12px;
  border: 1px solid #e2e8f0;
  font-family: 'JetBrains Mono', 'Courier New', monospace;
  font-size: 13px;
}
.premium-textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}
</style>
