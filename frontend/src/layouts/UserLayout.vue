<template>
  <div class="user-layout-wrapper">
    <div class="left-panel">
      <!-- 装饰背景图形 -->
      <div class="decorative-shape shape-1"></div>
      <div class="decorative-shape shape-2"></div>
      
      <div class="brand">
        <div class="text-logo-wrapper">
          <span class="text-logo">红人营销</span>
        </div>
        
        <div class="brand-text">
          <div class="brand-title">红人营销系统</div>
          <div class="brand-subtitle">红人营销 · 数据化管理平台</div>
        </div>
      </div>
      <div class="left-content">
        <h2 class="headline">连接商家与红人</h2>
        <p class="description">
          一站式管理合作达人、订单与佣金结算，让每一次投放都更可控、更高效。
        </p>
        <div class="highlights-container">
          <ul class="highlights">
            <li>
              <span class="icon">✓</span>
              <span>红人数据一屏洞察</span>
            </li>
            <li>
              <span class="icon">✓</span>
              <span>订单与佣金自动对账</span>
            </li>
            <li>
              <span class="icon">✓</span>
              <span>支持 Shopify 等电商平台对接</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="right-panel">
      <!-- 右侧背景装饰 -->
      <div class="bg-decoration-circle circle-1"></div>
      <div class="bg-decoration-circle circle-2"></div>
      
      <div class="login-wrapper">
        <RouterView />
      </div>
      <div class="footer">
        <div class="copyright">Copyright &copy; 2025 红人营销系统</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';
</script>

<style lang="scss" scoped>
.user-layout-wrapper {
  height: 100vh;
  display: flex;
  background: #f8fafc;
  overflow: hidden;
}

.left-panel {
  flex: 0 0 580px; /* 加宽至 580px */
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 80px; /* 增加内边距 */
  color: #fff;
  background: linear-gradient(150deg, #0f172a 0%, #1e293b 100%);
  position: relative;
  overflow: hidden;
  z-index: 10;
  box-shadow: 10px 0 30px rgba(0, 0, 0, 0.1); /* 增加分割阴影 */

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    background: radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.15) 0%, transparent 40%),
                radial-gradient(circle at 20% 80%, rgba(99, 102, 241, 0.15) 0%, transparent 40%);
    z-index: -1;
  }
}

.decorative-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px); /* 增加模糊度 */
  z-index: -1;
  opacity: 0.6;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: rgba(59, 130, 246, 0.25);
  top: -100px;
  right: -100px;
  animation: float 10s ease-in-out infinite;
}

.shape-2 {
  width: 500px;
  height: 500px;
  background: rgba(139, 92, 246, 0.15);
  bottom: -150px;
  left: -150px;
  animation: float 12s ease-in-out infinite reverse;
}

@keyframes float {
  0% { transform: translate(0, 0); }
  50% { transform: translate(20px, 20px); }
  100% { transform: translate(0, 0); }
}

.brand {
  margin-bottom: 64px;
  display: flex;
  align-items: center;
  gap: 20px;

  .text-logo-wrapper {
    background: #ffffff;
    padding: 6px 16px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .text-logo {
    font-size: 28px;
    font-weight: 900;
    color: #0f172a;
    font-family: -apple-system, BlinkMacSystemFont, 'Inter', 'Segoe UI', sans-serif;
    letter-spacing: -0.5px;
  }

  .brand-title {
    font-size: 28px;
    font-weight: 700;
    letter-spacing: 0.5px;
    line-height: 1.2;
  }

  .brand-subtitle {
    margin-top: 4px;
    font-size: 13px;
    opacity: 0.7;
    font-weight: 400;
    letter-spacing: 1.5px;
    text-transform: uppercase;
  }
}

.left-content {
  position: relative;
  z-index: 2;
}

.headline {
  font-size: 42px; /* 字体加大 */
  font-weight: 800;
  margin: 0 0 32px;
  line-height: 1.2;
  background: linear-gradient(to right, #ffffff, #94a3b8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.description {
  font-size: 16px;
  line-height: 1.8;
  color: #94a3b8;
  margin: 0 0 48px;
  max-width: 440px;
}

.highlights-container {
  padding-top: 32px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.highlights {
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 15px;
  color: #cbd5e1;

  li {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    
    .icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 24px;
      height: 24px;
      background: rgba(59, 130, 246, 0.2);
      color: #60a5fa;
      border-radius: 50%;
      font-size: 12px;
      font-weight: bold;
    }
  }
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  background: #f0f4f8; /* 稍微调整背景色 */
  overflow: hidden;
}

/* 右侧背景动态装饰 */
.bg-decoration-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  z-index: 0;
  opacity: 0.5;
}

.circle-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.08) 0%, transparent 70%);
  top: -200px;
  right: -200px;
}

.circle-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, transparent 70%);
  bottom: -100px;
  left: 0px;
}

.login-wrapper {
  width: 100%;
  max-width: 520px; /* 增加容器宽度，给卡片更多空间 */
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.footer {
  position: absolute;
  bottom: 24px;
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
  font-family: 'Inter', sans-serif;
  z-index: 1;
}

@media (max-width: 1200px) {
  .left-panel {
    flex: 0 0 450px;
    padding: 0 48px;
  }
  
  .headline {
    font-size: 32px;
  }
}

@media (max-width: 992px) {
  .user-layout-wrapper {
    flex-direction: column;
  }

  .left-panel {
    flex: none;
    padding: 60px 32px;
    min-height: auto;
    width: 100%;
  }
  
  .right-panel {
    flex: 1;
    padding: 60px 0;
  }
}
</style>
