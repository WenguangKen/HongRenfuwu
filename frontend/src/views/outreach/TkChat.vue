<template>
  <div class="tk-chat-page">
    <div class="coming-soon-container">
      <div class="illustration-wrapper">
        <div class="pulse-ring"></div>
        <div class="pulse-ring delay-1"></div>
        <div class="pulse-ring delay-2"></div>
        <div class="icon-circle">
          <MessageOutlined class="main-icon" />
        </div>
      </div>
      
      <div class="content-section">
        <h1 class="title">TK 沟通</h1>
        <p class="subtitle">TikTok 站内信即将接入</p>
        <p class="description">
          我们正在接入 TikTok Business API，届时您可以直接在系统内与红人进行站内信沟通，
          实现消息同步、会话管理和快捷回复等功能。
        </p>
        
        <div class="feature-cards">
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #3b82f6, #1d4ed8)">
              <SyncOutlined />
            </div>
            <div class="feature-text">
              <div class="feature-title">消息同步</div>
              <div class="feature-desc">自动同步 TikTok 站内信</div>
            </div>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #8b5cf6, #6d28d9)">
              <CommentOutlined />
            </div>
            <div class="feature-text">
              <div class="feature-title">会话管理</div>
              <div class="feature-desc">集中管理所有红人对话</div>
            </div>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #10b981, #059669)">
              <ThunderboltOutlined />
            </div>
            <div class="feature-text">
              <div class="feature-title">快捷回复</div>
              <div class="feature-desc">预设话术模板一键发送</div>
            </div>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #f59e0b, #d97706)">
              <TeamOutlined />
            </div>
            <div class="feature-text">
              <div class="feature-title">红人关联</div>
              <div class="feature-desc">自动匹配系统内红人记录</div>
            </div>
          </div>
        </div>

        <div class="status-badge">
          <ClockCircleOutlined />
          <span>预计上线时间：敬请期待</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  MessageOutlined,
  SyncOutlined,
  CommentOutlined,
  ThunderboltOutlined,
  TeamOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons-vue';
</script>

<style lang="scss" scoped>
.tk-chat-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coming-soon-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 640px;
  padding: 48px 32px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 24px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.04);
}

/* Animated illustration */
.illustration-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin-bottom: 32px;
}

.pulse-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 2px solid rgba(99, 102, 241, 0.15);
  animation: pulse-expand 3s ease-in-out infinite;

  &.delay-1 { animation-delay: 1s; }
  &.delay-2 { animation-delay: 2s; }
}

@keyframes pulse-expand {
  0% { transform: scale(0.8); opacity: 1; }
  100% { transform: scale(1.6); opacity: 0; }
}

.icon-circle {
  position: absolute;
  inset: 20px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);

  .main-icon {
    font-size: 36px;
    color: #fff;
  }
}

/* Content */
.content-section {
  text-align: center;
}

.title {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 16px;
  font-weight: 600;
  color: #6366f1;
  margin: 0 0 12px 0;
}

.description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.7;
  margin: 0 0 32px 0;
  max-width: 480px;
}

/* Feature cards */
.feature-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 32px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  transition: all 0.3s ease;
  text-align: left;

  &:hover {
    background: #fff;
    border-color: #e2e8f0;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  }
}

.feature-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  flex-shrink: 0;
}

.feature-text {
  .feature-title {
    font-size: 13px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 2px;
  }
  .feature-desc {
    font-size: 11px;
    color: #94a3b8;
  }
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #eff6ff, #f0f9ff);
  border: 1px solid #bfdbfe;
  border-radius: 24px;
  color: #3b82f6;
  font-size: 13px;
  font-weight: 600;
}
</style>
