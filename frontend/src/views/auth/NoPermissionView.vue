<template>
  <div class="no-permission-container">
    <div class="no-permission-card">
      <div class="icon-wrapper">
        <ExclamationCircleOutlined class="warning-icon" />
      </div>
      <h2 class="title">账号暂无页面权限</h2>
      <p class="description">
        当前账号尚未分配可访问的页面权限。超级管理员账号将自动拥有全部权限，请尝试重新加载或重新登录。
      </p>

      <div class="user-info">
        <div class="info-item">
          <span class="label">账号名称</span>
          <span class="value">{{ userInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">绑定邮箱</span>
          <span class="value">{{ displayEmail }}</span>
        </div>
        <div class="info-item">
          <span class="label">已分配页面</span>
          <span class="value">{{ pageCount }} 项</span>
        </div>
      </div>

      <div class="contact-info">
        <p class="contact-title">如需开通权限，请联系超级管理员</p>
        <div class="contact-item">
          <span class="label">管理员邮箱</span>
          <span class="value">wenguangfeifan@outlook.com</span>
        </div>
      </div>

      <div class="actions">
        <a-button type="primary" :loading="refreshing" @click="handleRefresh" class="action-btn">
          重新加载权限
        </a-button>
        <a-button @click="goToProfile" class="action-btn">
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
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { useUserStore } from '@/stores/user';
import { usePermissionStore } from '@/stores/permission';
import { message } from 'ant-design-vue';

const SUPER_ADMIN_EMAIL = 'wenguangfeifan@outlook.com';

const router = useRouter();
const userStore = useUserStore();
const permStore = usePermissionStore();
const refreshing = ref(false);

const userInfo = computed(() => ({
  name: userStore.userInfo?.username || '未知用户',
}));

const displayEmail = computed(() => {
  const email = (userStore.userInfo as { email?: string } | null)?.email;
  if (!email || email.length > 80) {
    return SUPER_ADMIN_EMAIL;
  }
  return email;
});

const pageCount = computed(() => permStore.pagePermissions.length);

const handleRefresh = async () => {
  refreshing.value = true;
  try {
    permStore.resetPermissions();
    const ok = await permStore.refreshPermissions();
    if (ok && permStore.pagePermissions.length > 0) {
      message.success('权限已更新，正在进入系统');
      router.push('/dashboard');
      return;
    }
    message.warning('仍未获取到页面权限，请确认 backend-user 已启动后重试');
  } finally {
    refreshing.value = false;
  }
};

const goToProfile = () => {
  router.push('/system/profile');
};

const handleLogout = () => {
  userStore.logout();
  permStore.resetPermissions();
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
      word-break: break-all;
      text-align: right;
      max-width: 60%;
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
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;

  .action-btn {
    min-width: 120px;
    height: 44px;
    border-radius: 12px;
    font-weight: 600;
    font-size: 15px;
  }
}
</style>
