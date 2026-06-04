<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="500px"
    class="premium-distribute-modal"
    centered
    :mask-closable="false"
    :closable="true"
  >
    <!-- Use custom close icon to ensure it's outside the dark header or clearly visible -->
    <template #closeIcon>
      <div class="custom-close-icon">
        <CloseOutlined style="color: rgba(255,255,255,0.8); font-size: 18px;" />
      </div>
    </template>

    <div class="dist-modal-content">
      <!-- Header with Influencer Info -->
      <div class="dist-header">
        <div class="header-overlay"></div>
        <div class="influencer-profile">
          <div class="avatar-info">
            <div class="initial-avatar">{{ influencerName?.charAt(0).toUpperCase() || 'I' }}</div>
            <div class="name-box">
              <div class="name">{{ influencerName || '未知红人' }}</div>
              <div class="email-row" v-if="influencerEmail">
                <MailOutlined class="mail-icon" />
                <span class="email-text">{{ influencerEmail }}</span>
              </div>
            </div>
          </div>
          <div class="header-action-tag">
            <TransactionOutlined /> 结算中
          </div>
        </div>
      </div>

      <div class="dist-body">
        <a-form :model="formLocal" layout="vertical" class="premium-form">
          <div class="form-section">
            <div class="section-badge">结算详情 SETTLEMENT DETAILS</div>
            
            <a-form-item required class="amount-form-item">
              <template #label>
                <div class="item-label-row">
                  <span class="main-label">打款金额</span>
                  <span class="currency-tag">USD / 美元</span>
                </div>
              </template>
              
              <div class="premium-input-wrapper">
                <div class="input-prefix">$</div>
                <a-input-number
                  v-model:value="formLocal.amount"
                  :min="0.01"
                  :max="pendingAmount"
                  class="amount-input-refined"
                  :precision="2"
                  :bordered="false"
                  placeholder="0.00"
                />
              </div>
              
              <div class="balance-status-bar">
                <div class="bar-label">待结算余额</div>
                <div class="bar-value">${{ pendingAmount.toLocaleString(undefined, { minimumFractionDigits: 2 }) }}</div>
                <div class="bar-progress" :style="{ width: (formLocal.amount / (pendingAmount || 1) * 100) + '%' }"></div>
              </div>
            </a-form-item>

            <a-form-item label="结算单备注 (Optional)">
              <a-textarea 
                v-model:value="formLocal.remark" 
                :rows="2" 
                placeholder="在此输入本次结算的相关说明..."
                class="premium-textarea-refined"
              />
            </a-form-item>
          </div>
        </a-form>
      </div>

      <div class="dist-footer">
        <a-button @click="visible = false" class="btn-cancel-glass">放弃操作</a-button>
        <a-button type="primary" @click="emitOk" class="btn-submit-architect" :disabled="formLocal.amount <= 0">
          确认发起打款
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import { CloseOutlined, MailOutlined, TransactionOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
  open: boolean;
  form: { influencerName: string; amount: number; remark: string };
  pendingAmount?: number;
  influencerName?: string;
  influencerEmail?: string;
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'update:form', value: any): void;
  (e: 'ok'): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const formLocal = reactive({
  influencerName: '',
  amount: 0,
  remark: '',
});

const pendingAmount = computed(() => props.pendingAmount ?? 0);

watch(
  () => props.form,
  (val) => {
    Object.assign(formLocal, val || {});
  },
  { deep: true, immediate: true },
);

watch(formLocal, (val) => {
  emit('update:form', { ...val });
}, { deep: true });

watch(
  () => props.influencerName,
  (val) => {
    if (val) formLocal.influencerName = val;
  },
);

const emitOk = () => {
  emit('ok');
};
</script>

<style lang="scss">
.premium-distribute-modal {
  .ant-modal-content {
    padding: 0 !important;
    border-radius: 20px;
    overflow: hidden;
    background: #ffffff;
    border: 1px solid rgba(0,0,0,0.05);
  }

  // Close Icon fix
  .ant-modal-close {
    top: 16px;
    right: 16px;
    z-index: 100;
    transition: all 0.3s;
    &:hover {
      transform: rotate(90deg);
      background: rgba(255,255,255,0.1);
      border-radius: 8px;
    }
  }

  .dist-modal-content {
    display: flex;
    flex-direction: column;
  }

  .dist-header {
    background: #0f172a; // Deep slate
    padding: 36px 28px;
    position: relative;
    border-bottom: 4px solid #3b82f6;

    .header-overlay {
      position: absolute;
      top: 0; left: 0; right: 0; bottom: 0;
      background: linear-gradient(135deg, rgba(37, 99, 235, 0.2) 0%, transparent 100%);
    }

    .influencer-profile {
      position: relative;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;

      .avatar-info {
        display: flex;
        align-items: center;
        gap: 20px;

        .initial-avatar {
          width: 56px;
          height: 56px;
          background: linear-gradient(135deg, #1d4ed8, #2563eb);
          border: 2px solid rgba(255,255,255,0.15);
          border-radius: 16px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #fff;
          font-size: 24px;
          font-weight: 800;
          box-shadow: 0 8px 16px rgba(0,0,0,0.2);
        }

        .name-box {
          .name {
            color: #ffffff;
            font-size: 20px;
            font-weight: 800;
            letter-spacing: -0.02em;
          }
          .email-row {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #94a3b8;
            font-size: 13px;
            margin-top: 4px;
            font-family: 'JetBrains Mono', monospace;
            .mail-icon { font-size: 12px; }
          }
        }
      }

      .header-action-tag {
        display: flex;
        align-items: center;
        gap: 6px;
        background: rgba(16, 185, 129, 0.1);
        color: #10b981;
        padding: 6px 14px;
        border-radius: 8px;
        font-size: 12px;
        font-weight: 700;
        border: 1px solid rgba(16, 185, 129, 0.2);
      }
    }
  }

  .dist-body {
    padding: 28px;
    background: #f8fafc;

    .section-badge {
      font-size: 10px;
      font-weight: 900;
      color: #64748b;
      letter-spacing: 0.15em;
      margin-bottom: 16px;
      opacity: 0.6;
    }

    // Custom Input Number Styling
    .premium-input-wrapper {
      background: #ffffff;
      border: 2px solid #e2e8f0;
      border-radius: 12px;
      padding: 4px 16px;
      display: flex;
      align-items: center;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 1px 2px rgba(0,0,0,0.02);

      &:hover, &:focus-within {
        border-color: #3b82f6;
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.08);
      }

      .input-prefix {
        font-size: 20px;
        font-weight: 700;
        color: #3b82f6;
        margin-right: 12px;
      }

      .amount-input-refined {
        flex: 1;
        width: 100%;
        .ant-input-number-input {
          font-size: 24px !important;
          font-weight: 800 !important;
          color: #1e293b !important;
          font-family: 'JetBrains Mono', monospace !important;
          padding: 8px 0 !important;
        }
      }
    }

    .item-label-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      margin-bottom: 8px;
      .main-label { font-weight: 700; color: #1e293b; font-size: 15px; }
      .currency-tag { font-size: 11px; font-weight: 800; color: #94a3b8; }
    }

    .balance-status-bar {
      margin-top: 16px;
      background: #ffffff;
      border-radius: 10px;
      padding: 10px 14px;
      border: 1px solid #e2e8f0;
      position: relative;
      overflow: hidden;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .bar-label { font-size: 12px; font-weight: 600; color: #64748b; z-index: 1; }
      .bar-value { font-size: 14px; font-weight: 800; color: #0f172a; font-family: 'JetBrains Mono', monospace; z-index: 1; }
      
      .bar-progress {
        position: absolute;
        bottom: 0; left: 0; height: 2px;
        background: #3b82f6;
        transition: width 0.4s ease;
      }
    }

    .premium-textarea-refined {
      background: #ffffff !important;
      border: 2px solid #e2e8f0 !important;
      border-radius: 12px !important;
      padding: 12px !important;
      font-size: 14px !important;
      transition: all 0.3s !important;
      &:hover, &:focus {
        border-color: #3b82f6 !important;
        background: #fff !important;
      }
    }

    .ant-form-item { margin-bottom: 24px; }
  }

  .dist-footer {
    padding: 24px 28px 32px;
    background: #ffffff;
    display: flex;
    gap: 16px;

    .btn-cancel-glass {
      flex: 1;
      height: 48px;
      border-radius: 12px;
      font-weight: 700;
      color: #64748b;
      border: 2px solid #f1f5f9;
      background: #f8fafc;
      &:hover {
        background: #f1f5f9;
        color: #1e293b;
      }
    }

    .btn-submit-architect {
      flex: 2;
      height: 48px;
      border-radius: 12px;
      font-weight: 800;
      background: #0f172a;
      border: none;
      color: #fff;
      box-shadow: 0 10px 20px rgba(15, 23, 42, 0.15);
      &:hover {
        background: #1e293b;
        transform: translateY(-2px);
        box-shadow: 0 12px 24px rgba(15, 23, 42, 0.2);
      }
      &:active { transform: translateY(0); }
      &:disabled { background: #cbd5e1; box-shadow: none; transform: none; }
    }
  }
}
</style>

