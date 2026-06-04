<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="480px"
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
          <div class="pm-header-subtitle">定期修改密码可以保护您的账户安全</div>
        </div>
      </div>
    </div>

    <div class="pm-modal-body">
      <a-form :model="passwordForm" layout="vertical" ref="passwordFormRef" class="pm-form">
        <a-form-item
          label="当前密码"
          name="oldPassword"
          :rules="[{ required: true, message: '请输入当前密码' }]"
        >
          <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入当前密码" class="premium-input" />
        </a-form-item>
        <a-form-item
          label="新密码"
          name="newPassword"
          :rules="[{ required: true, message: '请输入新密码' }, { min: 6, message: '密码长度不能少于6位' }]"
        >
          <a-input-password v-model:value="passwordForm.newPassword" placeholder="请输入新密码" class="premium-input" />
        </a-form-item>
        <a-form-item
          label="确认新密码"
          name="confirmPassword"
          :rules="[
            { required: true, message: '请再次输入新密码' },
            { validator: validateConfirmPassword }
          ]"
        >
          <a-input-password v-model:value="passwordForm.confirmPassword" placeholder="请再次输入新密码" class="premium-input" />
        </a-form-item>
      </a-form>
    </div>

    <div class="pm-modal-footer">
      <a-space>
        <a-button @click="visible = false" class="cancel-btn">取消</a-button>
        <a-button type="primary" @click="handlePasswordSubmit" :loading="loading" class="submit-btn primary-gradient">
          确定
        </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { LockOutlined } from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import type { Rule } from 'ant-design-vue/es/form';
import type { FormInstance } from 'ant-design-vue';
import { changeUserPassword } from '@/services/userService';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const props = defineProps<{
  open: boolean;
}>();

const emit = defineEmits(['update:open']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const loading = ref(false);
const passwordFormRef = ref<FormInstance>();
const userStore = useUserStore();
const router = useRouter();

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// Watch for modal opening to reset form
watch(visible, (val) => {
  if (val) {
    passwordForm.oldPassword = '';
    passwordForm.newPassword = '';
    passwordForm.confirmPassword = '';
  }
});

const validateConfirmPassword = async (_rule: Rule, value: string) => {
  if (value && value !== passwordForm.newPassword) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
};

const handlePasswordSubmit = () => {
  passwordFormRef.value?.validate().then(async () => {
    if (!userStore.userInfo?.id) {
      message.error('用户信息获取失败，请尝试重新登录');
      return;
    }
    
    // 美化的确认弹窗
    Modal.confirm({
      title: '确认修改密码？',
      content: '密码修改后会强制退出当前账户，是否确认？',
      icon: '🔒',
      okText: '确认',
      cancelText: '取消',
      centered: true,
      okButtonProps: {
        danger: true,
      },
      onOk: async () => {
        if (!userStore.userInfo?.id) {
          message.error('用户信息获取失败');
          return;
        }
        
        loading.value = true;
        try {
          await changeUserPassword(userStore.userInfo.id, { 
            oldPassword: passwordForm.oldPassword,
            newPassword: passwordForm.newPassword 
          });
          
          message.success('密码修改成功，请重新登录');
          visible.value = false;
          
          // 清除token和用户信息
          localStorage.removeItem('token');
          localStorage.removeItem('userInfo');
          
          // 强制跳转到登录页（使用window.location确保完全刷新）
          window.location.href = '/login';
        } catch (error) {
          console.error('修改密码失败:', error);
        } finally {
          loading.value = false;
        }
      }
    });
  });
};
</script>

<style lang="scss" scoped>
/* 密码修改弹窗样式 */
:deep(.ant-modal-content) {
  padding: 0;
  overflow: hidden;
  border-radius: 12px;
}

.pm-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  
  .pm-header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .pm-header-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 44px;
    height: 44px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    font-size: 20px;
    backdrop-filter: blur(10px);
  }
  
  .pm-header-text {
    .pm-header-title {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 2px;
    }
    
    .pm-header-subtitle {
      font-size: 13px;
      opacity: 0.9;
    }
  }
}

.pm-modal-body {
  padding: 24px 20px;
}

.pm-form {
  :deep(.ant-form-item) {
    margin-bottom: 20px;
  }
  
  :deep(.ant-form-item-label > label) {
    font-weight: 500;
    color: #262626;
  }
  
  :deep(.premium-input) {
    height: 40px;
    border-radius: 8px;
    border: 1.5px solid #e0e0e0;
    transition: all 0.3s;
    
    &:hover {
      border-color: #667eea;
    }
    
    &:focus, &.ant-input-affix-wrapper-focused {
      border-color: #667eea;
      box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
    }
  }
}

.pm-modal-footer {
  padding: 16px 20px;
  background: #fafafa;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  
  .submit-btn {
    height: 36px;
    padding: 0 24px;
    border-radius: 8px;
    font-weight: 500;
    
    &.primary-gradient {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      
      &:hover {
        opacity: 0.9;
      }
    }
  }
  
  .cancel-btn {
    height: 36px;
    padding: 0 24px;
    border-radius: 8px;
    border: 1.5px solid #d9d9d9;
    font-weight: 500;
    
    &:hover {
      border-color: #667eea;
      color: #667eea;
    }
  }
}
</style>
