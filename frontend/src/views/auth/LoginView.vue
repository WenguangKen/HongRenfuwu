<template>
  <div class="auth-layout" @mousemove="handleMouseMove">
    <!-- Left Side: Hero & Widgets (Abstract Liquid Dark Canvas) -->
    <div class="auth-cover">
      <div class="cover-bg" :style="{ transform: `translate(${mouseX * 8}px, ${mouseY * 8}px)` }"></div>
      <div class="cover-overlay"></div>

      <!-- Ultra-Premium SaaS Data Widgets Floating Layout -->
      <div class="kol-elements-container">
        
        <!-- TikTok Widget -->
        <div class="parallax-layer" :style="{ transform: `translate(${mouseX * -10}px, ${mouseY * -10}px)` }">
          <div class="saas-widget w-tiktok" style="top: 12%; right: 40%; animation-delay: 0s;">
            <div class="widget-header">
               <div class="platform-name" style="color: #e2e8f0;">
                  <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M19.59 6.69a4.83 4.83 0 0 1-3.77-4.25V2h-3.45v13.67a2.89 2.89 0 0 1-5.2 1.74 2.89 2.89 0 0 1 2.31-4.64 2.93 2.93 0 0 1 .88.13V9.4a6.84 6.84 0 0 0-1-.05A6.33 6.33 0 0 0 5 20.1a6.34 6.34 0 0 0 10.86-4.43v-7a8.16 8.16 0 0 0 4.77 1.52v-3.4a4.85 4.85 0 0 1-1-.1z"/></svg>
                  TikTok
               </div>
               <div class="trend-badge positive">+14%</div>
            </div>
            <div class="widget-profile">
               <img src="https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=150&q=80" class="avatar" />
               <div class="info">
                  <div class="name">@jess.dance</div>
                  <div class="niche">Lifestyle & Trends</div>
               </div>
            </div>
            <div class="widget-data">
               <div class="data-item">
                  <span class="label">Total Views</span>
                  <span class="value">12.8M</span>
               </div>
               <div class="data-item">
                  <span class="label">Engagement</span>
                  <span class="value">14.2%</span>
               </div>
            </div>
          </div>
        </div>

        <!-- X / Twitter Widget -->
        <div class="parallax-layer" :style="{ transform: `translate(${mouseX * -15}px, ${mouseY * -15}px)` }">
          <div class="saas-widget w-twitter" style="top: 8%; right: 8%; animation-delay: -1s;">
            <div class="widget-header">
               <div class="platform-name" style="color: #1da1f2;">
                  <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"/></svg>
                  Twitter / X
               </div>
               <div class="trend-badge positive">+18%</div>
            </div>
            <div class="widget-profile">
               <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=150&q=80" class="avatar" />
               <div class="info">
                  <div class="name">@crypto_mark</div>
                  <div class="niche">Tech & Web3</div>
               </div>
            </div>
            <div class="widget-data">
               <div class="data-item">
                  <span class="label">Followers</span>
                  <span class="value">450K</span>
               </div>
               <div class="data-item">
                  <span class="label">Impressions</span>
                  <span class="value">12.5M</span>
               </div>
            </div>
          </div>
        </div>

        <!-- Instagram Widget (Hero Center) -->
        <div class="parallax-layer" :style="{ transform: `translate(${mouseX * -30}px, ${mouseY * -30}px)` }">
          <div class="saas-widget w-instagram" style="top: 36%; right: 22%; animation-delay: -2s; z-index: 10;">
            <div class="widget-header">
               <div class="platform-name" style="color: #e1306c;">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" width="16" height="16"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg>
                  Instagram
               </div>
               <div class="trend-badge positive">+32%</div>
            </div>
            <div class="widget-profile">
               <img src="https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=150&q=80" class="avatar" />
               <div class="info">
                  <div class="name">@emily_vogue</div>
                  <div class="niche">Fashion & Beauty</div>
               </div>
            </div>
            <div class="widget-data">
               <div class="data-item">
                  <span class="label">Followers</span>
                  <span class="value" style="color: #e1306c;">2.4M</span>
               </div>
               <div class="data-item">
                  <span class="label">Avg. ROI</span>
                  <span class="value">320%</span>
               </div>
            </div>
          </div>
        </div>

        <!-- Facebook Widget -->
        <div class="parallax-layer" :style="{ transform: `translate(${mouseX * -25}px, ${mouseY * -25}px)` }">
          <div class="saas-widget w-facebook" style="bottom: 15%; right: 42%; animation-delay: -3s;">
            <div class="widget-header">
               <div class="platform-name" style="color: #1877f2;">
                  <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M22 12c0-5.52-4.48-10-10-10S2 6.48 2 12c0 4.84 3.44 8.87 8 9.8V15H8v-3h2V9.5C10 7.57 11.57 6 13.5 6H16v3h-2c-.55 0-1 .45-1 1v2h3v3h-3v6.95c5.05-.5 9-4.76 9-9.95z"/></svg>
                  Facebook
               </div>
               <div class="trend-badge positive">+5%</div>
            </div>
            <div class="widget-profile">
               <img src="https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?auto=format&fit=crop&w=150&q=80" class="avatar" />
               <div class="info">
                  <div class="name">@daily_travels</div>
                  <div class="niche">Travel</div>
               </div>
            </div>
            <div class="widget-data">
               <div class="data-item">
                  <span class="label">Reach</span>
                  <span class="value">850K</span>
               </div>
               <div class="data-item">
                  <span class="label">Link Clicks</span>
                  <span class="value">45K</span>
               </div>
            </div>
          </div>
        </div>

        <!-- YouTube Widget -->
        <div class="parallax-layer" :style="{ transform: `translate(${mouseX * -20}px, ${mouseY * -20}px)` }">
          <div class="saas-widget w-youtube" style="bottom: 10%; right: 10%; animation-delay: -4s;">
            <div class="widget-header">
               <div class="platform-name" style="color: #ff0000;">
                  <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16"><path d="M21.58 7.19c-.23-.86-.91-1.54-1.77-1.77C18.25 5 12 5 12 5s-6.25 0-7.81.42c-.86.23-1.54.91-1.77 1.77C2 8.75 2 12 2 12s0 3.25.42 4.81c.23.86.91 1.54 1.77 1.77C5.75 19 12 19 12 19s6.25 0 7.81-.42c.86-.23 1.54-.91 1.77-1.77C22 15.25 22 12 22 12s0-3.25-.42-4.81zM9.5 15.5v-7l6.5 3.5-6.5 3.5z"/></svg>
                  YouTube
               </div>
               <div class="trend-badge positive">+8%</div>
            </div>
            <div class="widget-profile">
               <img src="https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?auto=format&fit=crop&w=150&q=80" class="avatar" />
               <div class="info">
                  <div class="name">@summer_vlogs</div>
                  <div class="niche">Vlogs & Lifestyle</div>
               </div>
            </div>
            <div class="widget-data">
               <div class="data-item">
                  <span class="label">Subscribers</span>
                  <span class="value">3.2M</span>
               </div>
               <div class="data-item">
                  <span class="label">Avg Views</span>
                  <span class="value">8.5M</span>
               </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Text Content -->
      <div class="cover-content fade-in-up">
        <div class="logo-text">
          <span class="logo-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span class="brand-name">KOL SYSTEM</span>
        </div>
        
        <div class="slogan-wrapper">
          <div class="modern-badge">
            <span class="badge-dot"></span>
            Enterprise Edition
          </div>
          <h1 class="slogan-title">
            Unleash the Power of<br>
            <span class="text-gradient">Influencer Marketing.</span>
          </h1>
          <div class="slogan-desc-box">
            <p class="slogan-desc">下一代红人营销与数据追踪中枢。<br>基于全链路数据分析，赋能品牌的每一次商业决策，驱动全球化指数级增长。</p>
          </div>
        </div>
        
        <div class="cover-footer">
          <div class="testimonial">
            <p>"KOL SYSTEM 重新定义了我们的营销自动化标准，极大提升了投放效率与 ROI转化率。"</p>
            <div class="author">— Marketing Director, Global Brand</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Right Side: Distinct Premium Login Area -->
    <div class="auth-form-side">
      <div class="glass-form-wrapper fade-in-up delay-2">
        <div class="form-header">
          <div class="brand-tag">
            <span class="live-dot"></span> Secure Authentication
          </div>
          <h2 class="title">登录控制台</h2>
          <p class="subtitle">请输入您的企业账户凭证继续访问</p>
        </div>

        <a-form
          :model="formState"
          name="epic_dark_login"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
          layout="vertical"
          class="login-form"
        >
          <div class="fade-in-up delay-3">
            <a-form-item
              name="email"
              :rules="[{ required: true, message: '请输入电子邮箱' }, { type: 'email', message: '格式不正确' }]"
            >
              <div class="form-field">
                <label>企业邮箱</label>
                <a-input
                  v-model:value="formState.email"
                  placeholder="admin@company.com"
                  class="epic-input"
                  :bordered="false"
                />
              </div>
            </a-form-item>
          </div>

          <div class="fade-in-up delay-4">
            <a-form-item
              name="password"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <div class="form-field">
                <div class="label-row">
                  <label>登录密码</label>
                </div>
                <a-input-password
                  v-model:value="formState.password"
                  placeholder="••••••••"
                  class="epic-input epic-password"
                  :bordered="false"
                />
              </div>
            </a-form-item>
          </div>

          <div v-if="showCaptcha" class="fade-in-up delay-4">
            <a-form-item
              name="captcha"
              :rules="[{ required: showCaptcha, message: '请输入验证码' }]"
            >
              <div class="form-field">
                <label>人机验证</label>
                <div class="captcha-row">
                  <a-input
                    v-model:value="formState.captcha"
                    placeholder="输入图中字符"
                    class="epic-input captcha-input"
                    :bordered="false"
                  />
                  <div class="captcha-img-box" @click="refreshCaptcha" :class="{ 'is-loading': loadingCaptcha }">
                    <img v-if="captchaImage" :src="captchaImage" alt="captcha" />
                    <div v-else class="captcha-placeholder">
                      <ReloadOutlined :spin="loadingCaptcha" />
                    </div>
                  </div>
                </div>
              </div>
            </a-form-item>
          </div>

          <div class="fade-in-up delay-5">
            <a-button
              type="primary"
              html-type="submit"
              class="epic-submit-btn"
              :loading="loading"
              block
            >
              <span class="btn-text">授权进入系统</span>
              <ArrowRightOutlined class="btn-icon" />
            </a-button>
          </div>
        </a-form>
      </div>
      
      <p class="copyright fade-in-up delay-5">Copyright © 2026 KOL SYSTEM. All rights reserved.</p>
    </div>

    <!-- 登录成功后跳转遮罩 -->
    <div v-if="redirecting" class="login-redirect-overlay">
      <a-spin size="large" />
      <p class="redirect-text">{{ redirectStatus }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, onUnmounted } from 'vue';
import { 
  ReloadOutlined,
  ArrowRightOutlined
} from '@ant-design/icons-vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import { useUserStore } from '@/stores/user';
import { trackLoginIP } from '@/utils/analytics';
import { getCaptcha } from '@/services/authService';

// --- Parallax Effect ---
const mouseX = ref(0);
const mouseY = ref(0);
let rafId = 0;

const handleMouseMove = (e: MouseEvent) => {
  if (rafId) cancelAnimationFrame(rafId);
  rafId = requestAnimationFrame(() => {
    // Normalize mouse coordinates to -1 to 1 range
    mouseX.value = (e.clientX / window.innerWidth - 0.5) * 2;
    mouseY.value = (e.clientY / window.innerHeight - 0.5) * 2;
  });
};

onUnmounted(() => {
  if (rafId) cancelAnimationFrame(rafId);
});
// -----------------------

const router = useRouter();
const route = useRoute();
const loading = ref(false);
const redirecting = ref(false);
const redirectStatus = ref('正在进入系统...');
const loadingCaptcha = ref(false);
const rememberMe = ref(true);
const loginFailureCount = ref(0);
const showCaptcha = computed(() => loginFailureCount.value >= 5);

const formState = reactive({
  email: '',
  password: '',
  captcha: ''
});

const captcha = ref<string>('点击获取');
const captchaKey = ref<string>('');
const captchaImage = ref<string>('');

const refreshCaptcha = async () => {
  if (loadingCaptcha.value) return;
  loadingCaptcha.value = true;
  try {
    const response = await getCaptcha({ showError: false });
    const key = String(response?.captchaKey || '');
    const img = String(response?.captchaImage || '');
    if (!key || !img.startsWith('data:image')) {
      throw new Error('接口未启动或验证码服务异常');
    }
    captchaKey.value = key;
    captchaImage.value = img;
    captcha.value = '';
  } catch (error) {
    message.error('获取验证码失败：接口未启动或服务异常');
    captchaImage.value = '';
    captcha.value = '获取失败';
  } finally {
    setTimeout(() => {
      loadingCaptcha.value = false;
    }, 400);
  }
};

onMounted(() => {
  const savedEmail = localStorage.getItem('remembered_user_email');
  if (savedEmail) {
    formState.email = savedEmail;
    rememberMe.value = true;
  }
  
  const savedFailureCount = localStorage.getItem('login_failure_count');
  if (savedFailureCount) {
    loginFailureCount.value = parseInt(savedFailureCount, 10) || 0;
    if (loginFailureCount.value >= 5) {
      refreshCaptcha();
    }
  }
});

const userStore = useUserStore();

const onFinish = async (values: any) => {
  loading.value = true;
  redirecting.value = false;
  try {
    // 不阻塞登录：外网 IP 接口在隧道环境下可能极慢
    void trackLoginIP();

    const loginRequest = {
      email: values.email,
      password: values.password,
      captcha: values.captcha || '',
      captchaKey: captchaKey.value || '',
    };

    redirectStatus.value = '正在验证账户...';
    await userStore.login(loginRequest);

    loginFailureCount.value = 0;
    localStorage.removeItem('login_failure_count');

    if (rememberMe.value) {
      localStorage.setItem('remembered_user_email', values.email);
    } else {
      localStorage.removeItem('remembered_user_email');
    }

    message.success('登录成功');
    const redirect = (route.query?.redirect as string) || '/dashboard';
    redirecting.value = true;
    redirectStatus.value = '正在加载工作台...';
    loading.value = false;
    await router.push(redirect);
  } catch (error: any) {
    console.error('Login failed:', error);
    
    loginFailureCount.value++;
    localStorage.setItem('login_failure_count', loginFailureCount.value.toString());
    
    if (loginFailureCount.value >= 5 && !captchaImage.value) {
      refreshCaptcha();
    } else if (captchaImage.value) {
      refreshCaptcha();
    }

    const errorMessage = error?.message || error?.response?.data?.message || '登录失败，请检查邮箱和密码';
    message.error(errorMessage);
  } finally {
    loading.value = false;
    redirecting.value = false;
  }
};

const onFinishFailed = (_errorInfo: any) => {
};
</script>

<style lang="scss" scoped>
/* Force system-level premium typography across everything */
* {
  box-sizing: border-box;
}

.auth-layout {
  display: flex;
  min-height: 100vh;
  width: 100%;
  background-color: #09090b; /* Zinc 950 */
  font-family: 'Inter', system-ui, -apple-system, 'Microsoft YaHei', sans-serif;
  user-select: none;
  -webkit-user-select: none;
  overflow: hidden;
}

img {
  -webkit-user-drag: none;
  pointer-events: none;
}

/* =========================================
   Global Animations
========================================= */
.fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.delay-1 { animation-delay: 0.1s; }
.delay-2 { animation-delay: 0.2s; }
.delay-3 { animation-delay: 0.3s; }
.delay-4 { animation-delay: 0.4s; }
.delay-5 { animation-delay: 0.5s; }

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* =========================================
   Left Side (Hero Section & Widgets)
========================================= */
.auth-cover {
  flex: 1;
  position: relative;
  z-index: 10;
  display: none;
  flex-direction: column;
  overflow: hidden;
  background-color: #000;
  
  @media (min-width: 900px) {
    display: flex;
  }
}

.cover-bg {
  position: absolute;
  top: -5%; left: -5%;
  width: 110%; height: 110%;
  z-index: 1;
  background-image: url('https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=2564&auto=format&fit=crop');
  background-size: cover;
  background-position: center;
  transition: transform 0.1s ease-out;
}

.cover-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  z-index: 2;
  background: linear-gradient(135deg, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.45) 100%);
}

.kol-elements-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 10;
}

.parallax-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: transform 0.15s cubic-bezier(0.2, 0, 0.2, 1);
}

.saas-widget {
  position: absolute;
  width: 250px;
  background: rgba(15, 23, 42, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 24px 48px rgba(0,0,0,0.4);
  animation: float-widget 6s ease-in-out infinite alternate;
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
  pointer-events: auto;
  
  @media (min-width: 1400px) {
    width: 270px;
  }
}

.saas-widget:hover {
  transform: translateY(-5px) scale(1.02) !important;
  box-shadow: 0 30px 60px rgba(0,0,0,0.6);
  z-index: 20 !important;
  border-color: rgba(255, 255, 255, 0.25);
}

.w-instagram { transform: scale(1.05); }

@keyframes float-widget {
  0% { margin-top: 0px; }
  100% { margin-top: -15px; }
}

/* Platform Specific Accents */
.w-tiktok { border-top: 2px solid #06b6d4; }
.w-instagram { border-top: 2px solid #e1306c; }
.w-youtube { border-top: 2px solid #ff0000; }
.w-facebook { border-top: 2px solid #1877f2; }
.w-twitter { border-top: 2px solid #1da1f2; }

.widget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.platform-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.trend-badge {
  font-size: 0.75rem;
  font-weight: 700;
  color: #10b981;
  background: rgba(16, 185, 129, 0.15);
  padding: 3px 8px;
  border-radius: 6px;
  border: 1px solid rgba(16, 185, 129, 0.2);
}

.widget-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}
.widget-profile .avatar {
  width: 46px;
  height: 46px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid rgba(255, 255, 255, 0.15);
}
.widget-profile .info {
  display: flex;
  flex-direction: column;
}
.widget-profile .name {
  color: #fff;
  font-weight: 600;
  font-size: 0.95rem;
}
.widget-profile .niche {
  color: #94a3b8;
  font-size: 0.75rem;
  margin-top: 2px;
}

.widget-data {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  background: rgba(0, 0, 0, 0.25);
  border-radius: 10px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}
.data-item {
  display: flex;
  flex-direction: column;
}
.data-item .label {
  color: #64748b;
  font-size: 0.7rem;
  text-transform: uppercase;
  font-weight: 600;
  margin-bottom: 4px;
}
.data-item .value {
  color: #f8fafc;
  font-size: 1.15rem;
  font-weight: 700;
}

/* Left Content Section */
.cover-content {
  position: relative;
  z-index: 10;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 4rem 5rem;
  color: #ffffff;
  pointer-events: none;
}

.logo-text {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: 0.5px;
  
  .logo-icon {
    width: 36px;
    height: 36px;
    background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
    color: white;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(6, 182, 212, 0.4);
  }
}

.slogan-wrapper {
  margin: auto 0;
  max-width: 540px;
}

.modern-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 100px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #f8fafc;
  margin-bottom: 2rem;
  backdrop-filter: blur(8px);
}

.badge-dot {
  width: 8px;
  height: 8px;
  background-color: #22d3ee;
  border-radius: 50%;
  box-shadow: 0 0 10px #22d3ee;
}

.slogan-title {
  font-size: 4.2rem;
  font-weight: 800;
  line-height: 1.1;
  color: #ffffff;
  margin-bottom: 1.5rem;
  letter-spacing: -0.02em;
}

.text-gradient {
  background: linear-gradient(135deg, #22d3ee 0%, #a78bfa 50%, #22d3ee 100%);
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: inline-block;
}

.slogan-desc-box {
  border-left: 3px solid rgba(255, 255, 255, 0.25);
  padding-left: 1.5rem;
  margin-top: 1rem;
}

.slogan-desc {
  font-size: 1.15rem;
  line-height: 1.7;
  color: #e2e8f0;
  margin: 0;
  font-weight: 500;
}

.cover-footer .testimonial {
  background: rgba(15, 23, 42, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 1.5rem 2rem;
  max-width: 500px;
  
  p {
    font-size: 1.05rem;
    color: #f8fafc;
    line-height: 1.6;
    margin-bottom: 1rem;
    font-style: italic;
  }
  
  .author {
    font-size: 0.85rem;
    color: #94a3b8;
    font-weight: 600;
    text-transform: uppercase;
  }
}

/* =========================================
   Right Side: Clean Modern Light
========================================= */
.auth-form-side {
  flex: 0 0 45%;
  max-width: 560px;
  min-width: 400px;
  position: relative;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 3rem;
  
  /* Pure Crisp White Sidebar */
  background-color: #ffffff;
  border-left: 1px solid #e2e8f0;
}

.glass-form-wrapper {
  width: 100%;
  max-width: 420px;
  background: transparent;
  border: none;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
}

.form-header {
  margin-bottom: 2.5rem;
  
  .brand-tag {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 0.8rem;
    font-weight: 600;
    color: #475569;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 1.5rem;
    background: #f1f5f9;
    padding: 6px 12px;
    border-radius: 100px;
    border: 1px solid #e2e8f0;
    
    .live-dot {
      width: 6px;
      height: 6px;
      background-color: #10b981;
      border-radius: 50%;
      box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
    }
  }
  
  .title {
    font-size: 2.2rem;
    font-weight: 800;
    color: #0f172a;
    margin-bottom: 0.5rem;
    letter-spacing: -0.5px;
  }
  
  .subtitle {
    font-size: 1rem;
    color: #64748b;
    font-weight: 400;
  }
}

.login-form {
  :deep(.ant-form-item) {
    margin-bottom: 0;
  }
}

.form-field {
  margin-bottom: 1.5rem;
  
  label {
    display: block;
    font-size: 0.85rem;
    font-weight: 600;
    color: #334155;
    margin-bottom: 0.6rem;
    letter-spacing: 0.2px;
  }
}

.label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.6rem;
  
  label {
    margin-bottom: 0;
  }
}

.forgot-link {
  font-size: 0.8rem;
  color: #3b82f6;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
  
  &:hover {
    color: #2563eb;
    text-decoration: underline;
  }
}

/* =========================================
   Strict Typography & Sleek Inputs
========================================= */
:deep(.epic-input) {
  border-radius: 12px;
  height: 56px !important; /* FORCED HEIGHT */
  background-color: #f8fafc !important;
  border: 1px solid #cbd5e1;
  transition: all 0.2s ease;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.01);
}

/* Styling for the root input (Email & Captcha) */
:deep(input.epic-input),
:deep(.epic-input.ant-input) {
  padding: 0 16px !important;
  font-family: 'Inter', system-ui, -apple-system, 'Microsoft YaHei', sans-serif !important;
  font-size: 1.05rem !important;
  color: #0f172a !important;
  font-weight: 500 !important;
  letter-spacing: 0.3px !important;
}

/* Styling for the wrapper of Password input */
:deep(.epic-password.ant-input-affix-wrapper) {
  padding: 0 16px !important;
  display: flex !important;
  align-items: center !important;
}

/* Styling for the internal input of Password input */
:deep(.epic-password .ant-input) {
  height: 100% !important;
  background: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0 !important;
  font-family: 'Inter', system-ui, -apple-system, 'Microsoft YaHei', sans-serif !important;
  font-size: 1.05rem !important;
  color: #0f172a !important;
  font-weight: 500 !important;
  letter-spacing: 0.3px !important;
}

:deep(.epic-input:hover) {
  border-color: #94a3b8 !important;
}

:deep(.epic-input.ant-input-focused),
:deep(.epic-input.ant-input-affix-wrapper-focused),
:deep(.epic-input:focus) {
  background-color: #ffffff !important;
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15) !important;
}

:deep(input::placeholder) {
  color: #94a3b8 !important;
  font-weight: 400 !important;
}

:deep(.ant-input-password-icon) {
  color: #94a3b8;
  transition: color 0.2s;
  font-size: 1.2rem;
  &:hover { color: #0f172a; }
}

.captcha-row {
  display: flex;
  gap: 12px;
  
  .captcha-input {
    flex: 1;
  }
  
  .captcha-img-box {
    width: 140px;
    height: 56px;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafc;
    border: 1px solid #cbd5e1;
    transition: all 0.2s;
    
    &:hover {
      border-color: #94a3b8;
    }
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .captcha-placeholder {
      color: #94a3b8;
      font-size: 1.25rem;
    }
  }
}

.form-actions {
  margin-bottom: 2rem;
  margin-top: 1rem;
}

:deep(.epic-checkbox) {
  .ant-checkbox-inner {
    border-radius: 4px;
    border: 1px solid #cbd5e1;
    width: 18px;
    height: 18px;
    background: #ffffff;
  }
  .ant-checkbox-checked .ant-checkbox-inner {
    background-color: #3b82f6;
    border-color: #3b82f6;
  }
  .ant-checkbox-checked .ant-checkbox-inner::after {
    border-color: #ffffff;
  }
  span {
    color: #475569;
    font-weight: 500;
    font-size: 0.95rem;
  }
}

/* Linear-style Sleek Button */
.epic-submit-btn {
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(135deg, #0ea5e9 0%, #2563eb 100%);
  border: none;
  font-size: 1.05rem;
  font-weight: 600;
  color: #ffffff;
  box-shadow: 0 4px 14px rgba(37, 99, 235, 0.25);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  
  &:hover {
    transform: translateY(-1px);
    background: linear-gradient(135deg, #0284c7 0%, #1d4ed8 100%);
    box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35);
  }
  
  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(37, 99, 235, 0.2);
  }
  
  .btn-text, .btn-icon {
    position: relative;
    z-index: 2;
  }
  
  .btn-icon {
    font-size: 1.2rem;
    transition: transform 0.2s;
  }
  
  &:hover .btn-icon {
    transform: translateX(4px);
  }
}

.copyright {
  position: absolute;
  bottom: 2rem;
  font-size: 0.85rem;
  color: #94a3b8;
}

.login-redirect-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);

  .redirect-text {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    color: #334155;
  }
}
</style>
