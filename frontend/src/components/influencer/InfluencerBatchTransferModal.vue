<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    @ok="handleConfirm"
    @cancel="handleCancel"
    ok-text="确认"
    cancel-text="取消"
    width="800px"
  >
    <div class="batch-reason-modal">
      <div class="batch-reason-header">
        <div class="batch-reason-bulk">
          <a-input-group compact>
            <a-textarea
              v-model:value="commonReason"
              placeholder="批量应用原因（可选）"
              :rows="3"
              :maxlength="500"
              style="width: calc(100% - 100px);"
              show-count
            />
            <a-button 
              type="primary" 
              @click="applyCommonReason"
              style="width: 100px; height: auto;"
            >
              批量应用
            </a-button>
          </a-input-group>
        </div>
      </div>
      <div class="batch-reason-list">
        <div 
          v-for="(item, index) in reasonItems" 
          :key="item.id"
          class="batch-reason-item"
        >
          <div class="batch-reason-item-header">
            <a-avatar :size="32" style="background: #1890ff;">
              {{ item.name.charAt(0) }}
            </a-avatar>
            <div class="batch-reason-item-info">
              <div class="batch-reason-item-name">{{ item.name }}</div>
              <div class="batch-reason-item-id">ID: {{ item.id }}</div>
            </div>
          </div>
          <a-form-item 
            :label="`原因${index + 1}`"
            :validate-status="!item.reason ? 'error' : ''"
            :help="!item.reason ? '请输入原因' : ''"
          >
            <a-textarea
              v-model:value="item.reason"
              placeholder="请输入原因"
              :rows="3"
              :maxlength="500"
              show-count
            />
          </a-form-item>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { message } from 'ant-design-vue';

interface Item {
  id: string;
  name: string;
}

interface ReasonItem extends Item {
  reason: string;
}

const props = defineProps<{
  open: boolean;
  title: string;
  items: Item[];
}>();

const emit = defineEmits(['update:open', 'confirm']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const commonReason = ref('');
const reasonItems = ref<ReasonItem[]>([]);

watch(() => props.open, (val) => {
  if (val) {
    commonReason.value = '';
    reasonItems.value = props.items.map(item => ({
      ...item,
      reason: ''
    }));
  }
});

const applyCommonReason = () => {
  if (!commonReason.value.trim()) {
    message.warning('请输入批量应用的原因');
    return;
  }
  reasonItems.value.forEach(item => {
    item.reason = commonReason.value.trim();
  });
  message.success('已批量应用到所有红人');
};

const handleConfirm = () => {
  // Check if all items have reasons
  const emptyReasons = reasonItems.value.filter(item => !item.reason.trim());
  if (emptyReasons.length > 0) {
    message.warning(`还有 ${emptyReasons.length} 个红人未填写原因`);
    return;
  }
  
  emit('confirm', reasonItems.value);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
};
</script>

<style lang="scss" scoped>
.batch-reason-modal {
  padding: 24px;
  .batch-reason-header {
    background: #f8fafc;
    border-radius: 16px;
    padding: 20px;
    margin-bottom: 24px;
    border: 1px solid #e2e8f0;
  }
  
  .batch-reason-list {
    max-height: 450px;
    overflow-y: auto;
    padding-right: 12px;
    .batch-reason-item {
      background: #fff;
      border-radius: 16px;
      padding: 20px;
      margin-bottom: 16px;
      border: 1px solid #f1f5f9;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
      .batch-reason-item-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
      .batch-reason-item-name { font-weight: 700; color: #1e293b; }
      .batch-reason-item-id { font-size: 12px; color: #94a3b8; }
    }
  }
}
</style>
