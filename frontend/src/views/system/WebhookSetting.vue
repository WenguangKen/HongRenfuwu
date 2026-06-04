<template>
  <div class="webhook-setting-page">
    <!-- 页面标题 -->
    <a-card :bordered="false" class="header-card glass-card" :body-style="{ padding: '20px 24px' }">
      <div class="page-header">
        <div class="header-left">
          <div class="icon-wrapper">
            <ApiOutlined />
          </div>
          <div class="header-text">
            <h1 class="page-title">Shopify Webhook 配置</h1>
            <p class="page-desc">配置用于接收 Shopify 店铺数据变更通知的 Webhook 端点</p>
          </div>
        </div>
        <div class="header-right">
          <a-button type="primary" class="primary-gradient" @click="refreshEndpoints">
            <template #icon><SyncOutlined :spin="loading" /></template>
            刷新状态
          </a-button>
        </div>
      </div>
    </a-card>

    <!-- 基础 URL 配置 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '20px 24px' }">
      <template #title>
        <div class="card-title">
          <SettingOutlined />
          <span>基础配置</span>
        </div>
      </template>
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12" :lg="8">
            <a-form-item label="Webhook 基础 URL">
              <a-input-group compact style="display: flex">
                <a-input 
                  v-model:value="baseUrl" 
                  placeholder="https://api.yourdomain.com"
                  style="flex: 1"
                >
                  <template #prefix><GlobalOutlined /></template>
                </a-input>
                <a-button type="primary" @click="saveBaseUrl">
                  保存
                </a-button>
              </a-input-group>
              <div class="form-hint">
                <InfoCircleOutlined /> 此 URL 将与端点路径拼接，形成完整的 Webhook 地址
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- 端点列表 -->
    <a-card :bordered="false" class="endpoints-card glass-card" :body-style="{ padding: 0 }">
      <template #title>
        <div class="card-title">
          <LinkOutlined />
          <span>Webhook 端点</span>
        </div>
      </template>

      <div class="endpoints-list">
        <div 
          v-for="endpoint in endpoints" 
          :key="endpoint.path" 
          class="endpoint-item"
        >
          <div class="endpoint-info">
            <div class="endpoint-icon" :class="endpoint.category">
              <component :is="getEndpointIcon(endpoint.category)" />
            </div>
            <div class="endpoint-details">
              <div class="endpoint-name">{{ endpoint.name }}</div>
              <div class="endpoint-url">
                <code>{{ getFullUrl(endpoint.path) }}</code>
                <a-space size="small">
                  <a-tooltip title="复制完整 URL">
                    <a-button 
                      type="text" 
                      size="small" 
                      class="icon-btn"
                      @click="copyUrl(endpoint)"
                    >
                      <CopyOutlined />
                    </a-button>
                  </a-tooltip>
                </a-space>
              </div>
              <div class="endpoint-topics">
                <a-tag 
                  v-for="topic in endpoint.topics" 
                  :key="topic" 
                  :color="getTopicColor(endpoint.category)"
                  :bordered="false"
                >
                  {{ topic }}
                </a-tag>
              </div>
            </div>
          </div>
            <div class="endpoint-status">
              <div class="status-badge" :class="endpoint.status || 'unknown'">
                <span class="pulse-dot"></span>
                {{ getStatusLabel(endpoint.status) }}
              </div>
              <div class="last-event" v-if="endpoint.lastEventAt">
                <a-popover v-if="endpoint.lastPayload" title="最后接收内容" trigger="hover" overlayClassName="payload-popover">
                  <template #content>
                    <div class="payload-preview">
                      <pre>{{ formatJson(endpoint.lastPayload) }}</pre>
                    </div>
                  </template>
                  <span class="time-text cursor-pointer">
                    最后事件: {{ formatTime(endpoint.lastEventAt) }} 
                    <InfoCircleOutlined style="font-size: 10px; margin-left: 2px" />
                  </span>
                </a-popover>
                <span v-else>最后事件: {{ formatTime(endpoint.lastEventAt) }}</span>
              </div>
            </div>
        </div>
      </div>
    </a-card>


    <!-- 使用说明 -->
    <a-card :bordered="false" class="help-card glass-card" :body-style="{ padding: '20px 24px' }">
      <template #title>
        <div class="card-title">
          <QuestionCircleOutlined />
          <span>配置说明 & 密钥</span>
        </div>
      </template>
      <div class="help-content">
        <a-row :gutter="24">
          <a-col :xs="24" :md="12">
            <div class="help-section">
              <h4>如何在 Shopify 中配置 Webhook</h4>
              <ol>
                <li>登录 Shopify 后台</li>
                <li>进入 <strong>设置 → 通知 → Webhook</strong></li>
                <li>点击 <strong>创建 Webhook</strong></li>
                <li>选择事件类型（如 <code>orders/create</code>）</li>
                <li>粘贴对应的 Webhook URL</li>
                <li>选择格式为 <strong>JSON</strong></li>
                <li>保存配置</li>
              </ol>
            </div>
          </a-col>
          <a-col :xs="24" :md="12">
            <div class="help-section">
              <h4>端点用途说明</h4>
              <ul>
                <li><strong>商品端点</strong>：接收商品创建、更新、删除事件</li>
                <li><strong>库存端点</strong>：接收库存数量变化事件</li>
                <li><strong>订单端点</strong>：接收订单、支付、退款、退货事件</li>
              </ul>
              
              <!-- Webhook 签名密钥配置 -->
              <div class="secret-config-box">
                <div class="secret-title">
                  <KeyOutlined /> Shopify Webhook 签名密钥
                </div>
                <div class="secret-desc">
                  用于验证 Webhook 请求的真实性 (HMAC 签名校验)。请从 Shopify 后台获取并在此配置。
                </div>
                <div class="secret-input-wrapper">
                  <a-input-password
                    v-model:value="webhookSecret"
                    placeholder="请输入 Shopify Webhook 签名密钥"
                    :visibilityToggle="true"
                    allow-clear
                  />
                  <a-button type="primary" @click="saveSecret" :loading="savingSecret">
                    保存密钥
                  </a-button>
                </div>
              </div>

            </div>
          </a-col>
        </a-row>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { 
  ApiOutlined, 
  SyncOutlined, 
  SettingOutlined, 
  GlobalOutlined,
  LinkOutlined,
  CopyOutlined,
  InfoCircleOutlined,
  QuestionCircleOutlined,
  ShoppingOutlined,
  InboxOutlined,
  ShoppingCartOutlined,
  KeyOutlined,
  EyeOutlined,
  EyeInvisibleOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import http from '@/utils/http';
import { useSseStore } from '@/stores/sse';
import { getSystemConfig, saveSystemConfig } from '@/services/systemService';

interface WebhookEndpoint {
  name: string;
  path: string;
  category: string;
  topics: string[];
  status?: 'active' | 'inactive' | 'unknown';
  lastEventAt?: string;
  lastPayload?: string;
  testing?: boolean;
}

const loading = ref(false);
const baseUrl = ref('');
const webhookSecret = ref('');
const savingSecret = ref(false);

// 预定义的端点列表
const endpoints = ref<WebhookEndpoint[]>([
  {
    name: '商品 (Products)',
    path: '/v1/webhookfw-shopify/products',
    category: 'products',
    topics: ['products/create', 'products/update', 'products/delete'],
    status: 'unknown'
  },
  {
    name: '库存 (Inventory)',
    path: '/v1/webhookfw-shopify/inventory',
    category: 'inventory',
    topics: ['inventory_levels/update', 'variants/in_stock', 'variants/out_of_stock'],
    status: 'unknown'
  },
  {
    name: '订单 (Orders)',
    path: '/v1/webhookfw-shopify/orders',
    category: 'orders',
    topics: ['orders/create', 'orders/paid', 'orders/cancelled', 'refunds/create', 'returns/*'],
    status: 'unknown'
  }
]);

// 获取端点图标
const getEndpointIcon = (category: string) => {
  const icons: Record<string, any> = {
    products: ShoppingOutlined,
    inventory: InboxOutlined,
    orders: ShoppingCartOutlined
  };
  return icons[category] || ApiOutlined;
};

// 获取主题颜色
const getTopicColor = (category: string) => {
  const colors: Record<string, string> = {
    products: 'blue',
    inventory: 'green',
    orders: 'purple'
  };
  return colors[category] || 'default';
};

// 获取完整 URL
const getFullUrl = (path: string) => {
  if (!baseUrl.value) return path;
  return `${baseUrl.value.replace(/\/$/, '')}${path}`;
};

// 复制 URL
const copyUrl = async (endpoint: WebhookEndpoint) => {
  const fullUrl = getFullUrl(endpoint.path);
  try {
    await navigator.clipboard.writeText(fullUrl);
    message.success(`已复制 ${endpoint.name} 端点 URL`);
  } catch (e) {
    message.error('复制失败');
  }
};

// 获取状态标签
const getStatusLabel = (status?: string) => {
  if (status === 'active') return '正常';
  if (status === 'inactive') return '未激活';
  return '未知';
};

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('MM-DD HH:mm');
};

// 保存基础 URL
const saveBaseUrl = async () => {
  try {
    await saveSystemConfig({
      key: 'webhook_base_url',
      value: baseUrl.value,
      description: 'Webhook 基础 URL',
      isEncrypted: false
    });
    message.success('基础 URL 已保存');
  } catch (e: any) {
    message.error(e.message || '保存失败');
  }
};

// 刷新端点状态
const refreshEndpoints = async () => {
  loading.value = true;
  try {
    // 请求后端获取最新状态
    const response = await http.get('/webhook/v1/webhookfw-shopify/endpoints');
    if (response.data && response.data.endpoints) {
      // 合并状态，而不是直接替换，防止列表消失
      const newEndpoints = response.data.endpoints;
      newEndpoints.forEach((ne: WebhookEndpoint) => {
        const existing = endpoints.value.find(e => e.category === ne.category);
        if (existing) {
          existing.status = ne.status;
          existing.lastEventAt = ne.lastEventAt;
          if (ne.path) existing.path = ne.path;
        }
      });
    }
    message.success('状态刷新完成');
  } catch (e) {
    console.error('刷新 Webhook 状态失败 (可能是后端未启动或代理未配置):', e);
  } finally {
    loading.value = false;
  }
};

// 格式化 JSON
const formatJson = (jsonStr: string) => {
  try {
    const obj = JSON.parse(jsonStr);
    return JSON.stringify(obj, null, 2);
  } catch (e) {
    return jsonStr;
  }
};

// Load Secret
const loadSecret = async () => {
  try {
    const config = await getSystemConfig('shopify_webhook_secret');
    if (config && config.value) {
      // 获取真实值给 input-password 遮罩
      const revealed = await getSystemConfig('shopify_webhook_secret', true);
      webhookSecret.value = revealed.value || '';
    }
  } catch (e) {
    // Ignore error
  }
};

// Save Secret
const saveSecret = async () => {
  if (!webhookSecret.value) {
    message.warning('请输入密钥');
    return;
  }
  savingSecret.value = true;
  try {
    await saveSystemConfig({
      key: 'shopify_webhook_secret',
      value: webhookSecret.value,
      description: 'Shopify Webhook 签名密钥',
      isEncrypted: true
    });
    message.success('密钥已保存，请等待生效');
  } catch (e: any) {
    message.error(e.message || '保存失败');
  } finally {
    savingSecret.value = false;
  }
};

const sseStore = useSseStore();

// Watch global SSE events
watch(() => sseStore.lastEvent, (data) => {
  if (data) {
    const category = data.category || 'unknown';
    refreshEndpointByCategory(category);
  }
});

// 按分类刷新单个端点状态
const refreshEndpointByCategory = async (category: string) => {
  try {
    const resp = await http.get(`/webhook/v1/webhookfw-shopify/status/${category}`);
    const data = resp.data?.data;
    if (data) {
      const endpoint = endpoints.value.find(e => e.category === category);
      if (endpoint) {
        endpoint.status = data.status;
        endpoint.lastEventAt = data.lastEventAt;
        endpoint.lastPayload = data.lastPayload;
      }
    }
  } catch (e) {
    console.error(`Failed to refresh ${category} status:`, e);
  }
};

onMounted(async () => {
  // 从后端加载基础 URL，回退到 localStorage
  try {
    const config = await getSystemConfig('webhook_base_url', true);
    if (config && config.value) {
      baseUrl.value = config.value;
    } else {
      const savedUrl = localStorage.getItem('webhook_base_url');
      if (savedUrl) baseUrl.value = savedUrl;
    }
  } catch (e: any) {
    // 404 表示配置尚未创建，属正常情况，静默降级到 localStorage
    const savedUrl = localStorage.getItem('webhook_base_url');
    if (savedUrl) baseUrl.value = savedUrl;
  }
  refreshEndpoints();
  loadSecret();
});
</script>

<style lang="scss" scoped>
// ... (Existing styles)

.secret-config-box {
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;

  .secret-title {
    font-weight: 700;
    color: #0369a1;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .secret-desc {
    font-size: 12px;
    color: #64748b;
    margin-bottom: 12px;
  }

  .secret-input-wrapper {
    display: flex;
    gap: 8px;
  }
}
</style>


<style lang="scss" scoped>
.webhook-setting-page {
  height: auto;
  min-height: 100%;
  padding: 16px;
  padding-bottom: 40px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow: visible;

  .glass-card {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.9);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
  }

  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
  }

  // 页面头部
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .icon-wrapper {
        width: 56px;
        height: 56px;
        background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;
        box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
      }

      .header-text {
        .page-title {
          font-size: 22px;
          font-weight: 800;
          color: #1e293b;
          margin: 0 0 4px 0;
        }
        .page-desc {
          font-size: 13px;
          color: #64748b;
          margin: 0;
        }
      }
    }
  }

  // 配置表单
  .form-hint {
    margin-top: 8px;
    font-size: 12px;
    color: #94a3b8;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  // 端点列表
  .endpoints-list {
    display: flex;
    flex-direction: column;
  }

  .endpoint-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;
    transition: background 0.2s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background: #f8fafc;
    }

    .endpoint-info {
      display: flex;
      align-items: flex-start;
      gap: 16px;

      .endpoint-icon {
        width: 44px;
        height: 44px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;

        &.products { background: #eff6ff; color: #3b82f6; border: 1px solid #bfdbfe; }
        &.inventory { background: #ecfdf5; color: #10b981; border: 1px solid #a7f3d0; }
        &.orders { background: #faf5ff; color: #8b5cf6; border: 1px solid #ddd6fe; }
      }

      .endpoint-details {
        .endpoint-name {
          font-size: 15px;
          font-weight: 700;
          color: #1e293b;
          margin-bottom: 6px;
        }

        .endpoint-url {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 10px;

          code {
            background: #f1f5f9;
            padding: 6px 12px;
            border-radius: 6px;
            font-size: 13px;
            color: #475569;
            font-family: 'SFMono-Regular', Consolas, monospace;
            max-width: 400px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .icon-btn {
            color: #64748b;
            &:hover { color: #3b82f6; background: #eff6ff; }
            &.test-btn {
              &:hover { color: #eab308; background: #fef9c3; }
            }
          }
        }

        .endpoint-topics {
          display: flex;
          flex-wrap: wrap;
          gap: 6px;

          :deep(.ant-tag) {
            margin: 0;
            font-size: 11px;
            padding: 2px 8px;
            border-radius: 4px;
          }
        }
      }
    }

    .endpoint-status {
      text-align: right;

      .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;

        &.active { 
          background: #f0fdf4; 
          color: #15803d; 
          border: 1px solid #bbf7d0;
          .pulse-dot { background: #22c55e; }
        }
        &.inactive { 
          background: #f1f5f9; 
          color: #64748b; 
          border: 1px solid #e2e8f0;
          .pulse-dot { background: #94a3b8; }
        }
        &.unknown { 
          background: #fefce8; 
          color: #a16207; 
          border: 1px solid #fef08a;
          .pulse-dot { background: #eab308; }
        }

        .pulse-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
        }
      }

      .last-event {
        margin-top: 6px;
        font-size: 11px;
        color: #94a3b8;
        
        .cursor-pointer {
          cursor: pointer;
          &:hover { color: #3b82f6; }
        }
      }
    }
  }

  // 帮助内容
  .help-content {
    .help-section {
      h4 {
        font-size: 14px;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 12px;
      }

      ol, ul {
        padding-left: 20px;
        margin: 0;
        
        li {
          font-size: 13px;
          color: #475569;
          line-height: 1.8;
          
          code {
            background: #f1f5f9;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 12px;
          }
        }
      }
    }
  }

  .primary-gradient {
    background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
    border: none;
    color: #fff;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
    
    &:hover {
      box-shadow: 0 6px 16px rgba(59, 130, 246, 0.4);
      transform: translateY(-1px);
    }
  }
}
</style>

<style lang="scss">
.payload-popover {
  .ant-popover-inner-content {
    padding: 12px;
    max-width: 400px;
    max-height: 400px;
    overflow: auto;
  }
  
  .payload-preview {
    pre {
      margin: 0;
      font-size: 11px;
      font-family: 'SFMono-Regular', Consolas, monospace;
      white-space: pre-wrap;
      word-break: break-all;
      color: #334155;
      background: #f8fafc;
      padding: 8px;
      border-radius: 6px;
      border: 1px solid #e2e8f0;
    }
  }
}
</style>
