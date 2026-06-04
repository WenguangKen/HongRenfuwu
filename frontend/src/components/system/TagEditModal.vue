<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="600px"
    :footer="null"
    class="tag-modal"
    :mask-closable="false"
    :closable="false"
    centered
    destroy-on-close
  >
    <!-- 自定义头部 -->
    <div class="tm-modal-header">
      <div class="tm-header-left">
        <div class="tm-header-icon">
          <TagOutlined />
        </div>
        <div class="tm-header-text">
          <div class="tm-header-title">
            {{ isEdit ? '编辑标签' : '新增标签' }}
            <span v-if="isEdit && record" class="tag-id-badge">ID: {{ record.key ?? record.id }}</span>
          </div>
          <div class="tm-header-subtitle">
            {{ isEdit ? 'Edit tag information and color settings' : 'Create new tag with color settings' }}
          </div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 内容 -->
    <div class="tm-modal-body">
      <div class="tm-form-container">
        <a-form :model="formState" layout="vertical">
          <div class="section-container">
            <div class="section-title">
              <span class="icon">🏷️</span> 基本信息 (Basic Info)
            </div>
            
            <a-form-item 
              label="标签名称 (Name)" 
              required
              :validate-status="!formState.name && submitTried ? 'error' : ''"
              :help="!formState.name && submitTried ? '请填写标签名称' : ''"
            >
              <a-input 
                v-model:value="formState.name" 
                placeholder="请输入标签名称"
                allow-clear
                class="premium-input"
              />
            </a-form-item>

            <a-form-item label="标签颜色配置 (Color Configuration)">
              <div class="color-picker-container">
                <div class="color-config-item" v-for="field in colorFields" :key="field.key">
                  <span class="color-label">{{ field.label }}</span>
                  <div class="custom-color-picker">
                    <!-- Color Box -->
                    <div class="color-box" :style="{ backgroundColor: formState[field.key as keyof typeof formState] }">
                      <input 
                        type="color" 
                        :value="getHexColor(formState[field.key as keyof typeof formState])"
                        @input="(e: any) => handleColorChange(field.key, e.target.value)"
                        class="native-color-input" 
                      />
                    </div>
                    
                    <!-- Opacity Control -->
                    <div class="opacity-control">
                      <span class="opacity-label">透明度</span>
                      <a-input-number 
                        :value="getOpacity(formState[field.key as keyof typeof formState])"
                        @change="(val: any) => handleOpacityChange(field.key, Number(val))"
                        :min="0"
                        :max="100"
                        size="small"
                        style="width: 60px; margin-left: 8px;"
                        :formatter="(value: any) => `${value}%`"
                        :parser="(value: any) => value.replace('%', '')"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </a-form-item>

            <a-form-item label="描述 (Description)">
              <a-textarea 
                v-model:value="formState.desc" 
                placeholder="请输入描述"
                :auto-size="{ minRows: 3, maxRows: 6 }"
                class="premium-textarea"
              />
            </a-form-item>
          </div>
        </a-form>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="tm-modal-footer">
      <a-button @click="handleCancel" class="cancel-btn">取消</a-button>
      <a-button type="primary" @click="handleOk" class="create-btn primary-gradient">
        {{ isEdit ? '保存修改' : '立即创建' }}
      </a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { TagOutlined, CloseOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';

const props = defineProps<{
  open: boolean;
  record: any;
}>();

const emit = defineEmits(['update:open', 'ok']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const isEdit = ref(false);
const submitTried = ref(false);

const formState = reactive({
  name: '',
  backgroundColor: '#1890ff',
  borderColor: '#1890ff',
  textColor: '#ffffff',
  desc: '',
});

const colorFields = [
  { label: '背景色', key: 'backgroundColor' },
  { label: '线框色', key: 'borderColor' },
  { label: '字体颜色', key: 'textColor' }
];

watch(() => props.open, (val) => {
  if (val) {
    submitTried.value = false;
    if (props.record) {
      isEdit.value = true;
      const record = props.record;
      formState.name = record.name;
      formState.backgroundColor = record.backgroundColor;
      formState.borderColor = record.borderColor;
      formState.textColor = record.textColor;
      formState.desc = record.desc;
    } else {
      isEdit.value = false;
      formState.name = '';
      formState.backgroundColor = '#e6f7ff';
      formState.borderColor = '#91d5ff';
      formState.textColor = '#1890ff';
      formState.desc = '';
    }
  }
}, { immediate: true });

const handleCancel = () => {
  visible.value = false;
};

const handleOk = () => {
  submitTried.value = true;
  if (!formState.name) {
    message.warning('请填写标签名称');
    return;
  }
  emit('ok', {
    ...formState,
    isEdit: isEdit.value,
    id: props.record?.id
  });
  visible.value = false;
};

// Color Helper Functions
const getHexColor = (colorStr: string) => {
  if (!colorStr) return '#000000';
  if (colorStr.startsWith('#')) {
    return colorStr.slice(0, 7);
  }
  return colorStr;
};

const getOpacity = (colorStr: string) => {
  if (!colorStr) return 100;
  if (colorStr.startsWith('#')) {
    if (colorStr.length === 9) {
      const alphaHex = colorStr.slice(7, 9);
      return Math.round((parseInt(alphaHex, 16) / 255) * 100);
    }
    return 100;
  }
  return 100;
};

const handleColorChange = (key: string, hex: string) => {
  const current = formState[key as keyof typeof formState] as string;
  const opacity = getOpacity(current);
  updateColorState(key, hex, opacity);
};

const handleOpacityChange = (key: string, opacity: number) => {
  const current = formState[key as keyof typeof formState] as string;
  const hex = getHexColor(current);
  updateColorState(key, hex, opacity);
};

const updateColorState = (key: string, hex: string, opacity: number) => {
  let finalColor = hex;
  if (opacity < 100) {
    const alphaVal = Math.round((opacity / 100) * 255);
    const alphaHex = alphaVal.toString(16).padStart(2, '0');
    finalColor = `${hex}${alphaHex}`;
  }
  // @ts-ignore
  formState[key as keyof typeof formState] = finalColor;
};
</script>

<style lang="scss" scoped>
/* 弹窗样式 */
:deep(.tag-modal) .ant-modal-content {
  padding: 0 !important;
  overflow: hidden;
}

.tm-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(37, 99, 235, 0.08) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.tm-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tm-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 8px 16px rgba(37, 99, 235, 0.2);
}

.tm-header-text {
  .tm-header-title {
    font-size: 17px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 2px;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .tag-id-badge {
    font-size: 12px;
    font-weight: 600;
    color: #3b82f6;
    background: rgba(59, 130, 246, 0.1);
    padding: 2px 8px;
    border-radius: 6px;
    border: 1px solid rgba(59, 130, 246, 0.2);
    font-family: 'JetBrains Mono', monospace;
  }

  .tm-header-subtitle {
    font-size: 12px;
    color: #64748b;
  }
}

.close-btn {
  border-radius: 10px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  transition: all 0.2s;
  
  &:hover { 
    background: rgba(239, 68, 68, 0.1); 
    color: #ef4444; 
  }
}

.tm-modal-body {
  padding: 0;
  background: #fff;
  max-height: 70vh;
  overflow-y: auto;
}

.tm-form-container {
  padding: 24px;
}

.section-container {
  margin-bottom: 0;
  
  .section-title {
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 8px;
    .icon { font-size: 16px; }
  }
}

.premium-input, 
.premium-textarea {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  transition: all 0.3s;
  
  &:hover {
    border-color: #3b82f6 !important;
  }
  
  &:focus {
    border-color: #3b82f6 !important;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
  }
}

.premium-textarea {
  resize: none;
}

.tm-modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  height: 40px;
  border-radius: 10px;
  font-weight: 600;
}

.create-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 10px;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  color: #fff;
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
  }
}

.color-picker-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 24px;
  width: 100%;
  margin-bottom: 16px;
}

.color-config-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
  
  .color-label {
    font-size: 12px;
    color: #64748b;
    font-weight: 500;
  }

  .custom-color-picker {
    display: flex;
    align-items: center;
    gap: 8px;

    .color-box {
      width: 32px;
      height: 32px;
      border-radius: 6px;
      border: 1px solid #d9d9d9;
      position: relative;
      overflow: hidden;
      cursor: pointer;
      flex-shrink: 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
      transition: transform 0.2s, box-shadow 0.2s;

      &:hover {
        transform: scale(1.05);
        box-shadow: 0 4px 8px rgba(0,0,0,0.1);
      }

      .native-color-input {
        position: absolute;
        top: -50%;
        left: -50%;
        width: 200%;
        height: 200%;
        opacity: 0;
        cursor: pointer;
        padding: 0;
        border: none;
      }
    }

    .opacity-control {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #64748b;
      
      .opacity-label {
        margin-right: 4px;
      }
      
      .opacity-value {
        min-width: 32px;
        text-align: right;
        font-variant-numeric: tabular-nums;
      }
    }
  }
}
</style>
