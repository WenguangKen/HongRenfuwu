<template>
  <div class="order-detail-tab">
    <!-- 顶部状态概览 -->
    <div class="status-overview">
      <div class="order-title">
        <span class="order-number">{{ orderData?.orderName || '-' }}</span>
        <a-tag v-if="orderData?.financialStatus" :color="getFinancialStatusColor(orderData.financialStatus)" class="status-tag">
          {{ mapFinancialStatus(orderData.financialStatus) }}
        </a-tag>
        <a-tag v-if="orderData?.fulfillmentStatus" :color="getFulfillmentStatusColor(orderData.fulfillmentStatus)" class="status-tag">
          {{ mapFulfillmentStatus(orderData.fulfillmentStatus) }}
        </a-tag>
      </div>
      <div class="order-meta">
        <span class="meta-item">
          <ClockCircleOutlined />
          {{ formatDate(orderData?.orderCreatedAt || orderData?.createdAt) }}
        </span>
        <span v-if="orderData?.storeName" class="meta-item">
          <ShopOutlined />
          {{ orderData.storeName }}
        </span>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧：红人与收件信息 -->
      <div class="info-section">
        <div class="section-title">
          <UserOutlined /> 红人与收件信息
        </div>
        <div class="section-body">
          <div class="info-row">
            <span class="label">关联红人</span>
            <div class="value influencer-value">
              <template v-if="orderData?.influencerName || orderData?.influencer">
                <span class="influencer-name">{{ orderData.influencerName || orderData.influencer }}</span>
                <a-button 
                  v-if="isSampleOrder && canEditInfluencer" 
                  type="link" 
                  size="small" 
                  @click="handleLinkInfluencer"
                  class="edit-btn"
                >
                  <EditOutlined />
                </a-button>
              </template>
              <template v-else>
                <span class="no-data">未关联</span>
                <a-button 
                  v-if="isSampleOrder && canLinkInfluencer" 
                  type="link" 
                  size="small" 
                  @click="handleLinkInfluencer"
                  class="link-btn"
                >
                  <PlusOutlined /> 关联
                </a-button>
              </template>
            </div>
          </div>
          <div class="info-row">
            <span class="label">收件人</span>
            <span class="value">{{ orderData?.shippingName || orderData?.recipientName || orderData?.customerName || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系电话</span>
            <span class="value mono">{{ orderData?.shippingPhone || orderData?.recipientPhone || '-' }}</span>
          </div>
          <div class="info-row address-row">
            <span class="label">收件地址</span>
            <span class="value address">{{ orderData?.shippingAddress || orderData?.recipientAddress || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">国家</span>
            <span class="value">{{ orderData?.shippingCountry || orderData?.recipientCountry || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系邮箱</span>
            <span class="value">{{ orderData?.customerEmail || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧：物流信息 -->
      <div class="info-section">
        <div class="section-title">
          <SendOutlined /> 物流信息
        </div>
        <div class="section-body">
          <!-- 拆单主单提示 -->
          <template v-if="orderData?.isSplit && !orderData?.fulfillmentIds">
            <div class="split-notice">
              <InfoCircleOutlined />
              <span>此订单已拆分，物流信息请查看子单</span>
            </div>
          </template>
          <template v-else>
            <div class="info-row">
              <span class="label">物流单号</span>
              <div class="value tracking-value">
                <template v-if="orderData?.trackingNumber">
                  <span class="mono">{{ orderData.trackingNumber }}</span>
                  <a v-if="orderData?.trackingUrl" :href="orderData.trackingUrl" target="_blank" class="track-link">
                    <ExportOutlined /> 追踪
                  </a>
                </template>
                <span v-else>-</span>
              </div>
            </div>
            <div class="info-row">
              <span class="label">物流公司</span>
              <span class="value">{{ orderData?.trackingCompany || '-' }}</span>
            </div>
            <div class="info-row" v-if="orderData?.fulfillmentIds">
              <span class="label">包裹ID</span>
              <span class="value mono">{{ formatFulfillmentId(orderData.fulfillmentIds) }}</span>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 财务信息摘要 -->
    <div class="financial-summary">
      <div class="summary-item">
        <span class="summary-label">订单总价</span>
        <span class="summary-value primary">${{ formatAmount(orderData?.totalPrice) }}</span>
      </div>
      <template v-if="(orderData?.totalShipping > 0) || (orderData?.shippingAmount > 0)">
        <div class="summary-divider"></div>
        <div class="summary-item">
          <span class="summary-label">运费</span>
          <span class="summary-value">${{ formatAmount(orderData?.totalShipping || orderData?.shippingAmount) }}</span>
        </div>
      </template>
      <template v-if="(orderData?.totalRefund > 0) || (orderData?.refundAmount > 0)">
        <div class="summary-divider"></div>
        <div class="summary-item">
          <span class="summary-label">退款金额</span>
          <span class="summary-value warning">${{ formatAmount(orderData?.totalRefund || orderData?.refundAmount) }}</span>
        </div>
      </template>
      <template v-if="orderData?.commissionAmount > 0">
        <div class="summary-divider"></div>
        <div class="summary-item">
          <span class="summary-label">佣金</span>
          <span class="summary-value success">${{ formatAmount(orderData?.commissionAmount) }}</span>
        </div>
      </template>
      <template v-if="orderData?.commissionStatus">
        <div class="summary-divider"></div>
        <div class="summary-item">
          <span class="summary-label">佣金状态</span>
          <a-tag :color="getCommissionStatusColor(orderData.commissionStatus)" size="small">
            {{ mapCommissionStatus(orderData.commissionStatus) }}
          </a-tag>
        </div>
      </template>
    </div>

    <!-- 时间节点 -->
    <div class="time-section">
      <div class="time-title"><ClockCircleOutlined /> 时间节点</div>
      <div class="time-items">
        <div class="time-item">
          <span class="time-label">创建时间</span>
          <span class="time-value">{{ formatDate(orderData?.orderCreatedAt || orderData?.createdAt || orderData?.createTime) }}</span>
        </div>
        <div class="time-item">
          <span class="time-label">支付时间</span>
          <span class="time-value success">{{ formatDate(orderData?.paidAt || orderData?.paymentTime) }}</span>
        </div>
        <div class="time-item">
          <span class="time-label">发货时间</span>
          <span class="time-value">{{ formatDate(orderData?.shippedAt || orderData?.shipTime) }}</span>
        </div>
        <div class="time-item">
          <span class="time-label">妥投时间</span>
          <span class="time-value success">{{ formatDate(orderData?.deliveredAt || orderData?.deliveryTime) }}</span>
        </div>
        <div class="time-item" v-if="orderData?.updatedAt">
          <span class="time-label">更新时间</span>
          <span class="time-value">{{ formatDate(orderData?.updatedAt) }}</span>
        </div>
        <div class="time-item" v-if="orderData?.cancelledAt">
          <span class="time-label">取消时间</span>
          <span class="time-value warning">{{ formatDate(orderData?.cancelledAt) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { 
  PlusOutlined, 
  EditOutlined, 
  UserOutlined, 
  SendOutlined, 
  ClockCircleOutlined,
  ShopOutlined,
  ExportOutlined,
  InfoCircleOutlined 
} from '@ant-design/icons-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  orderData: any;
  orderType?: 'sample' | 'conversion' | 'regular';
  canLinkInfluencer?: boolean;
  canEditInfluencer?: boolean;
}>();

const emit = defineEmits<{
  (e: 'update-influencer', data: { influencerId: number | null }): void;
  (e: 'link-influencer'): void;
}>();

const isSampleOrder = computed(() => props.orderType === 'sample');

const handleLinkInfluencer = () => {
  emit('link-influencer');
};

const formatDate = (date: string) => {
  if (!date) return '-';
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

const formatFulfillmentId = (fulfillmentIds: string) => {
  if (!fulfillmentIds) return '-';
  const ids = fulfillmentIds.split(',').map(id => {
    if (id.includes('/')) {
      return id.split('/').pop() || id;
    }
    return id;
  });
  return ids.join(', ');
};

const formatAmount = (amount: any) => {
  if (amount === null || amount === undefined) return '0.00';
  const num = parseFloat(amount);
  if (isNaN(num)) return '0.00';
  return num.toFixed(2);
};

// 状态映射
const mapFinancialStatus = (status: string) => {
  const map: Record<string, string> = {
    'paid': '已付款',
    'pending': '待付款',
    'refunded': '已退款',
    'partially_refunded': '部分退款',
    'voided': '已作废',
    'authorized': '已授权',
    'partially_paid': '部分付款',
  };
  return map[status] || status || '-';
};

const mapFulfillmentStatus = (status: string) => {
  const map: Record<string, string> = {
    'fulfilled': '已发货',
    'unfulfilled': '待发货',
    'partial': '部分发货',
    'null': '待处理',
    'restocked': '已退货',
  };
  return map[status] || status || '待处理';
};

const mapCommissionStatus = (status: string) => {
  if (!status) return '-';
  const s = status.toLowerCase();
  const map: Record<string, string> = {
    'pending': '未结算',
    'unsettled': '未结算',
    'calculated': '未结算',
    'paid': '已结算',
    'settled': '已结算',
    'cancelled': '已取消',
  };
  return map[s] || status;
};

// 状态颜色
const getFinancialStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    'paid': 'success',
    'pending': 'orange',
    'refunded': 'default',
    'partially_refunded': 'warning',
    'voided': 'error',
    'authorized': 'blue',
  };
  return colorMap[status] || 'default';
};

const getFulfillmentStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    'fulfilled': 'success',
    'unfulfilled': 'orange',
    'partial': 'blue',
    'null': 'default',
  };
  return colorMap[status] || 'default';
};

const getCommissionStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    'pending': 'orange',
    'calculated': 'blue',
    'paid': 'success',
    'cancelled': 'default',
  };
  return colorMap[status] || 'default';
};
</script>

<style lang="scss" scoped>
.order-detail-tab {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow-y: auto;
}

// 顶部状态概览
.status-overview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 14px;
  border-bottom: 1px solid #f0f0f0;

  .order-title {
    display: flex;
    align-items: center;
    gap: 10px;

    .order-number {
      font-size: 18px;
      font-weight: 700;
      color: #1a1a2e;
    }

    .status-tag {
      margin: 0;
    }
  }

  .order-meta {
    display: flex;
    gap: 16px;
    color: #8c8c8c;
    font-size: 13px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

// 主内容区 2 列布局
.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.info-section {
  background: #fafafa;
  border-radius: 10px;
  padding: 14px;
  border: 1px solid #f0f0f0;

  .section-title {
    font-size: 13px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 6px;
    
    .anticon {
      color: #6366f1;
    }
  }

  .section-body {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;

  .label {
    font-size: 12px;
    color: #8c8c8c;
    flex-shrink: 0;
  }

  .value {
    font-size: 13px;
    color: #333;
    font-weight: 500;
    text-align: right;
    word-break: break-all;
    max-width: 65%;

    &.mono {
      font-family: 'JetBrains Mono', monospace;
      font-size: 12px;
      color: #666;
    }

    &.address {
      line-height: 1.4;
    }
  }

  &.address-row {
    align-items: flex-start;
  }
}

.influencer-value {
  display: flex;
  align-items: center;
  gap: 6px;

  .influencer-name {
    font-weight: 600;
    color: #1a1a2e;
  }

  .no-data {
    color: #bbb;
    font-style: italic;
  }

  .link-btn, .edit-btn {
    padding: 0 4px;
    height: auto;
    font-size: 12px;
  }

  .link-btn { color: #10b981; }
  .edit-btn { color: #6366f1; }
}

.tracking-value {
  display: flex;
  align-items: center;
  gap: 8px;

  .track-link {
    font-size: 12px;
    color: #6366f1;
    display: flex;
    align-items: center;
    gap: 2px;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

.split-notice {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 8px;
  font-size: 13px;
  color: #ad8b00;

  .anticon {
    color: #faad14;
  }
}

// 财务信息摘要
.financial-summary {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f8f9ff 0%, #f0f4ff 100%);
  border-radius: 10px;
  border: 1px solid #e8ecff;
  flex-wrap: wrap;

  .summary-item {
    display: flex;
    align-items: center;
    gap: 6px;

    .summary-label {
      font-size: 12px;
      color: #666;
    }

    .summary-value {
      font-size: 14px;
      font-weight: 700;

      &.primary { color: #6366f1; }
      &.warning { color: #f59e0b; }
      &.success { color: #10b981; }
    }
  }

  .summary-divider {
    width: 1px;
    height: 20px;
    background: #d1d5eb;
  }
}

// 时间节点
.time-section {
  background: #fafafa;
  border-radius: 10px;
  padding: 12px 14px;
  border: 1px solid #f0f0f0;

  .time-title {
    font-size: 13px;
    font-weight: 600;
    color: #333;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 6px;
    
    .anticon {
      color: #6366f1;
    }
  }

  .time-items {
    display: flex;
    gap: 24px;
    flex-wrap: wrap;
  }

  .time-item {
    display: flex;
    align-items: center;
    gap: 8px;

    .time-label {
      font-size: 12px;
      color: #8c8c8c;
    }

    .time-value {
      font-size: 13px;
      color: #333;
      font-family: 'JetBrains Mono', monospace;

      &.warning {
        color: #f59e0b;
      }
      
      &.success {
        color: #10b981;
      }
    }
  }
}
</style>
