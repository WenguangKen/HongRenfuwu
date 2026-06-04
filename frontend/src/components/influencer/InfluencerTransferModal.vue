<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="520px"
    centered
    class="reason-modal"
    destroy-on-close
  >
    <div class="rm-header">
      <div class="rm-header-icon"><swap-outlined /></div>
      <div class="rm-header-text">
        <div class="rm-title">{{ title }}</div>
        <div class="rm-subtitle">请填写流转原因以记录变更历史</div>
      </div>
    </div>
    
    <div class="rm-body">
      <a-form ref="formRef" :model="formState" layout="vertical" class="premium-form">
        <a-form-item label="原因类型" name="reasonType" :rules="[{ required: true, message: '请选择原因类型' }]">
          <a-select 
            v-model:value="formState.reasonType" 
            placeholder="请选择原因类型" 
            :options="reasonOptions" 
            class="premium-select-full"
          />
        </a-form-item>
        <a-form-item label="其它原因" name="reason">
          <a-textarea
            v-model:value="formState.reason"
            placeholder="请输入详细原因（选填）"
            :rows="4"
            :maxlength="500"
            show-count
            class="premium-textarea"
          />
        </a-form-item>
      </a-form>
    </div>

    <div class="rm-footer">
      <a-button @click="handleCancel" class="premium-btn-ghost">取消</a-button>
      <a-button type="primary" @click="handleConfirm" class="premium-btn-primary">确认流转</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { SwapOutlined } from '@ant-design/icons-vue';
import type { FormInstance } from 'ant-design-vue';

const props = defineProps<{
  open: boolean;
  title: string;
}>();

const emit = defineEmits(['update:open', 'confirm']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const formRef = ref<FormInstance>();
const formState = reactive({
  reasonType: undefined,
  reason: '',
});

const reasonOptions = [
  { value: '价格不合适', label: '价格不合适' },
  { value: '档期不匹配', label: '档期不匹配' },
  { value: '内容风格不符', label: '内容风格不符' },
  { value: '回复慢/无回复', label: '回复慢/无回复' },
  { value: '数据造假', label: '数据造假' },
  { value: '其它', label: '其它' },
];

watch(() => props.open, (val) => {
  if (val) {
    formState.reasonType = undefined;
    formState.reason = '';
  }
});

const handleCancel = () => {
  visible.value = false;
};

const handleConfirm = () => {
  formRef.value?.validate().then(() => {
    emit('confirm', { ...formState });
    visible.value = false;
  });
};
</script>

<style lang="scss" scoped>
.reason-modal {
  :deep(.ant-modal-content) { padding: 0; border-radius: 16px; overflow: hidden; }
  
  .rm-header {
    padding: 24px;
    background: #f8fafc;
    display: flex;
    align-items: center;
    gap: 16px;
    border-bottom: 1px solid #e2e8f0;
    
    .rm-header-icon {
      width: 48px; height: 48px;
      background: #fff;
      border-radius: 12px;
      display: flex; align-items: center; justify-content: center;
      font-size: 24px; color: #3b82f6;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    }
    
    .rm-title { font-size: 18px; font-weight: 700; color: #1e293b; }
    .rm-subtitle { font-size: 13px; color: #64748b; margin-top: 2px; }
  }
  
  .rm-body { padding: 24px; }
  
  .rm-footer {
    padding: 16px 24px;
    background: #fff;
    border-top: 1px solid #e2e8f0;
    display: flex; justify-content: flex-end; gap: 12px;
  }
}

.premium-btn-ghost {
  border-radius: 8px; font-weight: 600;
  &:hover { color: #3b82f6; border-color: #3b82f6; }
}

.premium-btn-primary {
  border-radius: 8px; font-weight: 600;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  &:hover { box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3); transform: translateY(-1px); }
}
</style>
