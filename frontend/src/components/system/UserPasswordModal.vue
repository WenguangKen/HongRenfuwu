<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="480px"
    :footer="null"
    centered
    class="password-modal"
    :mask-closable="false"
  >
    <!-- 自定义头部 -->
    <div class="pm-modal-header">
      <div class="pm-header-left">
        <div class="pm-header-icon">
          <lock-outlined />
        </div>
        <div class="pm-header-text">
          <div class="pm-header-title">修改密码</div>
          <div class="pm-header-subtitle">为用户 {{ user?.username }} 重置登录密码</div>
        </div>
      </div>
    </div>

    <div class="pm-modal-body">
      <a-form :model="formState" layout="vertical" class="pm-form">
        <a-form-item label="新密码" required v-bind="validateInfos.newPassword">
          <a-input-password 
            v-model:value="formState.newPassword" 
            placeholder="请输入新密码" 
            allow-clear
          />
        </a-form-item>
        <a-form-item label="确认新密码" required v-bind="validateInfos.confirmPassword">
          <a-input-password 
            v-model:value="formState.confirmPassword" 
            placeholder="请再次输入新密码" 
            allow-clear
          />
        </a-form-item>
      </a-form>
      
      <div class="warning-box">
        <info-circle-outlined class="warning-icon" />
        <span class="warning-text">密码修改后，该用户将被强制登出，需使用新密码重新登录。</span>
      </div>
    </div>

    <div class="pm-modal-footer">
      <a-space>
        <a-button @click="handleCancel">取消</a-button>
        <a-button type="primary" :loading="loading" @click="handleOk">确认修改</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, h } from 'vue';
import { message, Modal, Form } from 'ant-design-vue';
import { LockOutlined, InfoCircleOutlined, ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { changeUserPassword } from '@/services/userService';

const props = defineProps<{
  open: boolean;
  user: any;
}>();

const emit = defineEmits(['update:open', 'success']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const loading = ref(false);

const formState = reactive({
  newPassword: '',
  confirmPassword: '',
});

const rules = reactive({
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 8, max: 20, message: '密码长度需在8-20位之间' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: async (_rule: any, value: string) => {
        if (value && value !== formState.newPassword) {
          throw new Error('两次输入的密码不一致');
        }
      }
    }
  ]
});

const useForm = Form.useForm;
const { resetFields, validate, validateInfos } = useForm(formState, rules);

watch(() => props.open, (val) => {
  if (val) {
    resetFields();
  }
});

const handleCancel = () => {
  visible.value = false;
};

const handleOk = () => {
  validate().then(() => {
    Modal.confirm({
      title: '确认修改密码？',
      icon:  h(ExclamationCircleOutlined),
      content: '密码修改后会强制登出对应账户，是否确认？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        loading.value = true;
        try {
          await changeUserPassword(props.user.id, {
            newPassword: formState.newPassword
          });
          message.success('密码修改成功，用户已强制登出');
          emit('success');
          visible.value = false;
        } catch (error) {
          // Error handled by api service
        } finally {
          loading.value = false;
        }
      }
    });
  }).catch(() => {
    // 验证失败
  });
};
</script>

<style lang="scss" scoped>
:deep(.password-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.pm-modal-header {
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(251, 113, 133, 0.08) 0%, rgba(251, 191, 36, 0.05) 100%);
  border-bottom: 1px solid rgba(251, 113, 133, 0.1);
}

.pm-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pm-header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fb7185 0%, #f43f5e 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  box-shadow: 0 6px 16px rgba(251, 113, 133, 0.3);
}

.pm-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
}

.pm-header-subtitle {
  font-size: 13px;
  color: #64748b;
}

.pm-modal-body {
  padding: 24px;
}

.warning-box {
  margin-top: 16px;
  padding: 16px;
  background: #fff1f2; /* Rose tint */
  border: 1px solid #fecdd3;
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  
  .warning-icon {
    color: #f43f5e;
    margin-top: 2px;
    font-size: 16px;
  }
  
  .warning-text {
    font-size: 13px;
    color: #881337;
    line-height: 1.5;
    font-weight: 500;
  }
}

.pm-modal-footer {
  padding: 16px 24px;
  text-align: right;
  border-top: 1px solid #f1f5f9;
  background: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
