<template>
  <div class="no-permission-container">
    <div class="no-permission-card">
      <div class="icon-wrapper">
        <ExclamationCircleOutlined class="warning-icon" />
      </div>
      <h2 class="title">账号暂无权限</h2>
      <p class="description">您的账号当前没有访问权限，请联系管理员开通权限</p>
      
      <div class="user-info">
        <div class="info-item">
          <span class="label">账号名称：</span>
          <span class="value">{{ userInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱地址：</span>
          <span class="value">{{ userInfo.email || '未设置' }}</span>
        </div>
      </div>

      <div class="contact-info">
        <p class="contact-title">如需开通权限，请联系系统管理员：</p>
        <div class="contact-item">
          <span class="label">管理员邮箱：</span>
          <span class="value">admin@example.com</span>
        </div>
        <div class="contact-item">
          <span class="label">技术支持：</span>
          <span class="value">010-88888888</span>
        </div>
      </div>

      <div class="actions">
        <a-button type="primary" @click="goToProfile" class="action-btn">
          个人信息
        </a-button>
        <a-button @click="handleLogout" class="action-btn">
          退出登录
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { useUserStore } from '@/stores/user';
import { message } from 'ant-design-vue';

const router = useRouter();
const userStore = useUserStore();

const userInfo = computed(() => ({
  name: (userStore.userInfo && userStore.userInfo.username) ? userStore.userInfo.username : '未知用户',
  email: (userStore.userInfo && (userStore.userInfo as any).email) ? (userStore.userInfo as any).email : '未设置'
}));

const goToProfile = () => {
  router.push('/system/profile');
};

const handleLogout = () => {
  userStore.logout();
  message.success('已退出登录');
  router.push('/user/login');
};
</script>

<style lang="scss" scoped>
.no-permission-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.no-permission-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  padding: 48px 56px;
  max-width: 600px;
  width: 100%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  text-align: center;
}

.icon-wrapper {
  margin-bottom: 24px;
  
  .warning-icon {
    font-size: 80px;
    color: #ff9800;
  }
}

.title {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
}

.description {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 32px;
  line-height: 1.6;
}

.user-info {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  text-align: left;
  
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #e2e8f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .label {
      font-size: 14px;
      color: #64748b;
      font-weight: 500;
    }
    
    .value {
      font-size: 14px;
      color: #1e293b;
      font-weight: 600;
    }
  }
}

.contact-info {
  background: #fff7ed;
  border: 1px solid #fed7aa;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 32px;
  text-align: left;
  
  .contact-title {
    font-size: 14px;
    color: #92400e;
    font-weight: 600;
    margin-bottom: 16px;
  }
  
  .contact-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 0;
    
    .label {
      font-size: 14px;
      color: #92400e;
    }
    
    .value {
      font-size: 14px;
      color: #c2410c;
      font-weight: 600;
    }
  }
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  
  .action-btn {
    min-width: 120px;
    height: 44px;
    border-radius: 12px;
    font-weight: 600;
    font-size: 15px;
  }
}
</style>

