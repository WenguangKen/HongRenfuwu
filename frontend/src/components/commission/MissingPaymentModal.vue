<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="520px"
    class="premium-distribute-modal missing-payment-modal"
    centered
    :mask-closable="false"
  >
    <template #closeIcon>
      <div class="custom-close-icon">
        <CloseOutlined style="color: rgba(255,255,255,0.8); font-size: 18px;" />
      </div>
    </template>

    <div class="dist-modal-content">
      <!-- Header with Alert Style -->
      <div class="dist-header warning-header">
        <div class="header-overlay"></div>
        <div class="influencer-profile">
          <div class="avatar-info">
            <div class="initial-avatar warning-bg">
              <ExclamationCircleOutlined />
            </div>
            <div class="name-box">
              <div class="name">收款信息缺失</div>
              <div class="email-row">
                <span class="email-text">部分红人尚未配置结算账户</span>
              </div>
            </div>
          </div>
          <div class="header-action-tag warning-tag">
            需要完善
          </div>
        </div>
      </div>

      <div class="dist-body">
        <div class="warning-content">
          <p class="warning-desc">
            系统检测到以下 <span class="highlight-count">{{ influencers.length }}</span> 名红人未绑定 <strong>银行卡</strong> 或 <strong>PayPal</strong> 账户，暂时无法发起分佣：
          </p>
          
          <div class="influencer-list-container custom-scrollbar">
            <div v-for="(name, index) in influencers" :key="index" class="influencer-item-mini">
              <div class="item-dot"></div>
              <span class="item-name">{{ name }}</span>
            </div>
          </div>

          <div class="action-tip-card">
            <div class="tip-icon"><InfoCircleOutlined /></div>
            <div class="tip-text">请先前往“红人管理”页面完善这些红人的收款信息，然后再重新尝试发起分佣。</div>
          </div>
        </div>
      </div>

      <div class="dist-footer">
        <a-button type="primary" @click="visible = false" class="btn-submit-architect warning-btn">
          我知道了
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { CloseOutlined, ExclamationCircleOutlined, InfoCircleOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
  open: boolean;
  influencers: string[];
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});
</script>

<style lang="scss">
.missing-payment-modal {
  .warning-header {
    background: linear-gradient(135deg, #451a03 0%, #78350f 100%) !important;
    border-bottom: 4px solid #f59e0b !important;
    
    .warning-bg {
      background: linear-gradient(135deg, #f59e0b, #d97706) !important;
      box-shadow: 0 8px 16px rgba(245, 158, 11, 0.3) !important;
    }

    .warning-tag {
      background: rgba(245, 158, 11, 0.1) !important;
      color: #fbbf24 !important;
      border: 1px solid rgba(245, 158, 11, 0.2) !important;
    }
  }

  .warning-content {
    .warning-desc {
      font-size: 15px;
      color: #334155;
      line-height: 1.6;
      margin-bottom: 20px;
      
      .highlight-count {
        color: #d97706;
        font-weight: 800;
        font-size: 18px;
        margin: 0 2px;
      }
    }

    .influencer-list-container {
      background: #ffffff;
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      padding: 12px;
      max-height: 180px;
      overflow-y: auto;
      margin-bottom: 20px;
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 8px;

      .influencer-item-mini {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 6px 10px;
        background: #f8fafc;
        border-radius: 8px;
        
        .item-dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: #f59e0b;
        }
        
        .item-name {
          font-size: 13px;
          font-weight: 600;
          color: #475569;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }

    .action-tip-card {
      display: flex;
      gap: 12px;
      background: #fffbeb;
      border: 1px solid #fef3c7;
      border-radius: 12px;
      padding: 14px;
      
      .tip-icon {
        color: #f59e0b;
        font-size: 18px;
        margin-top: 2px;
      }
      
      .tip-text {
        font-size: 13px;
        color: #92400e;
        line-height: 1.5;
        font-weight: 500;
      }
    }
  }

  .warning-btn {
    background: #0f172a !important;
    &:hover {
      background: #1e293b !important;
    }
  }
}

/* Custom Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
