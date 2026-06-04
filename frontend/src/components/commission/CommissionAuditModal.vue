<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="580px"
    class="premium-distribute-modal audit-modal"
    centered
    :mask-closable="false"
  >
    <template #closeIcon>
      <div class="custom-close-icon">
        <CloseOutlined style="color: rgba(255,255,255,0.8); font-size: 18px;" />
      </div>
    </template>

    <div class="dist-modal-content">
      <!-- Header with Audit Style -->
      <div class="dist-header">
        <div class="header-overlay"></div>
        <div class="influencer-profile">
          <div class="avatar-info">
            <div class="initial-avatar audit-avatar">
              <SafetyCertificateOutlined />
            </div>
            <div class="name-box">
              <div class="name">佣金审核</div>
              <div class="email-row">
                <span class="email-text">请核对打款信息并确认审核结果</span>
              </div>
            </div>
          </div>
          <div class="header-action-tag audit-tag">
            <ClockCircleOutlined /> 审核中
          </div>
        </div>
      </div>

      <div class="dist-body">
        <div class="audit-summary-card">
          <div class="summary-item">
            <span class="label">申请红人</span>
            <span class="value">{{ record?.name || '-' }}</span>
          </div>
          <div class="summary-item">
            <span class="label">申请金额</span>
            <span class="value amount">${{ record?.amount?.toLocaleString(undefined, { minimumFractionDigits: 2 }) }}</span>
          </div>
        </div>

        <a-form :model="formState" layout="vertical" class="premium-form">
          <div class="form-section">
            <div class="section-badge">支付配置 PAYMENT CONFIGURATION</div>
            
            <a-row :gutter="16">
              <a-col :span="12">
                <a-form-item label="支付方式" required>
                  <a-select v-model:value="formState.paymentMethod" placeholder="选择支付方式" class="premium-select-refined">
                    <a-select-option value="bank_card">银行卡 (Bank Transfer)</a-select-option>
                    <a-select-option value="paypal">PayPal</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="结算账号" required>
                  <a-input v-model:value="formState.paymentAccount" placeholder="输入打款账号" class="premium-input-refined" />
                </a-form-item>
              </a-col>
            </a-row>

            <!-- 银行卡打款明细 -->
            <template v-if="formState.paymentMethod === 'bank_card'">
              <div class="section-badge detail-badge">打款明细 PAYMENT DETAILS</div>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="银行名称">
                    <a-input v-model:value="bankDetails.bankName" placeholder="Bank Name" class="premium-input-refined" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="账户持有人">
                    <a-input v-model:value="bankDetails.accountHolder" placeholder="Account Holder" class="premium-input-refined" />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="路由号 (Routing Number)">
                    <a-input v-model:value="bankDetails.routingNumber" placeholder="Routing Number" class="premium-input-refined" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="SWIFT 代码">
                    <a-input v-model:value="bankDetails.swiftCode" placeholder="SWIFT / BIC Code" class="premium-input-refined" />
                  </a-form-item>
                </a-col>
              </a-row>
            </template>

            <!-- PayPal 打款明细 -->
            <template v-else-if="formState.paymentMethod === 'paypal'">
              <div class="section-badge detail-badge">打款明细 PAYMENT DETAILS</div>
              <a-form-item label="PayPal 账号">
                <a-input v-model:value="paypalDetails.paypalAccount" placeholder="PayPal Email" class="premium-input-refined" />
              </a-form-item>
            </template>

            <a-form-item label="审核备注">
              <a-textarea 
                v-model:value="formState.remark" 
                :rows="2" 
                placeholder="请输入审核备注信息..."
                class="premium-textarea-refined"
              />
            </a-form-item>
          </div>
        </a-form>
      </div>

      <div class="dist-footer">
        <a-button @click="visible = false" class="btn-cancel-glass">取消</a-button>
        <a-button type="primary" @click="handleOk" class="btn-submit-architect">
          审核通过并进入待打款
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, reactive, watch } from 'vue';
import { CloseOutlined, SafetyCertificateOutlined, ClockCircleOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { influencerService } from '@/services/influencerService';

const props = defineProps<{
  open: boolean;
  record: any;
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'ok', data: any): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const formState = reactive({
  paymentMethod: undefined as string | undefined,
  paymentAccount: '',
  remark: '',
});

const bankDetails = reactive({
  bankName: '',
  accountHolder: '',
  routingNumber: '',
  swiftCode: '',
});

const paypalDetails = reactive({
  paypalAccount: '',
});

// Auto-fill payment info when record changes
watch(() => props.open, async (val) => {
  if (val && props.record?.influencerId) {
    try {
      const res = await influencerService.getPaymentInfo(props.record.influencerId);
      const data = res.data || res;
      if (data) {
        // Preference: Bank card, then PayPal
        if (data.accountNumber || data.bankName) {
          formState.paymentMethod = 'bank_card';
          formState.paymentAccount = data.accountNumber || '';
          bankDetails.bankName = data.bankName || '';
          bankDetails.accountHolder = data.accountHolder || '';
          bankDetails.routingNumber = data.routingNumber || '';
          bankDetails.swiftCode = data.swiftCode || '';
        } else if (data.paypalAccount) {
          formState.paymentMethod = 'paypal';
          formState.paymentAccount = data.paypalAccount;
          paypalDetails.paypalAccount = data.paypalAccount;
        }
      }
    } catch (e) {
      console.error('Failed to pre-fetch payment info', e);
    }
  }
});

const handleOk = () => {
  if (!formState.paymentMethod) {
    message.warning('请选择支付方式');
    return;
  }
  if (!formState.paymentAccount) {
    message.warning('请输入结算账号');
    return;
  }

  let details: Record<string, any> = {};
  if (formState.paymentMethod === 'bank_card') {
    details = { ...bankDetails };
  } else if (formState.paymentMethod === 'paypal') {
    details = { ...paypalDetails };
  }

  emit('ok', {
    paymentMethod: formState.paymentMethod,
    paymentAccount: formState.paymentAccount,
    paymentDetails: details,
    remark: formState.remark
  });
};
</script>

<script lang="ts">
export default {
  name: 'CommissionAuditModal'
}
</script>

<style lang="scss" scoped>
.audit-modal {
  .audit-avatar {
    background: linear-gradient(135deg, #8b5cf6, #6d28d9) !important;
    box-shadow: 0 8px 16px rgba(139, 92, 246, 0.3) !important;
  }

  .audit-tag {
    background: rgba(139, 92, 246, 0.1) !important;
    color: #a78bfa !important;
    border: 1px solid rgba(139, 92, 246, 0.2) !important;
  }

  .audit-summary-card {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 24px;
    display: flex;
    justify-content: space-around;
    box-shadow: 0 1px 3px rgba(0,0,0,0.02);

    .summary-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 4px;

      .label {
        font-size: 12px;
        color: #64748b;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.05em;
      }

      .value {
        font-size: 16px;
        font-weight: 700;
        color: #1e293b;
        
        &.amount {
          color: #3b82f6;
          font-family: 'JetBrains Mono', monospace;
          font-size: 18px;
        }
      }
    }
  }

  .premium-select-refined {
    width: 100%;
    :deep(.ant-select-selector) {
      height: 42px !important;
      border-radius: 8px !important;
      border: 1px solid #cbd5e1 !important;
      background-color: #f8fafc !important;
      display: flex;
      align-items: center;
      transition: all 0.3s;
      
      &:hover { border-color: #8b5cf6 !important; }
    }
    :deep(.ant-select-selection-item) {
      font-weight: 500;
      color: #1e293b;
    }
  }

  .premium-input-refined {
    height: 42px;
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    background-color: #f8fafc;
    padding: 0 12px;
    font-weight: 500;
    color: #1e293b;
    transition: all 0.3s;
    &:hover, &:focus {
      border-color: #8b5cf6 !important;
      background-color: #ffffff;
      box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1) !important;
    }
    &::placeholder {
      color: #94a3b8;
      font-weight: 400;
    }
  }

  .premium-textarea-refined {
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    background-color: #f8fafc;
    padding: 10px 12px;
    font-weight: 500;
    color: #1e293b;
    transition: all 0.3s;
    &:hover, &:focus {
      border-color: #8b5cf6 !important;
      background-color: #ffffff;
      box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1) !important;
    }
    &::placeholder {
      color: #94a3b8;
      font-weight: 400;
    }
  }

  .section-badge {
    font-size: 12px;
    font-weight: 700;
    color: #64748b;
    letter-spacing: 0.05em;
    margin-bottom: 16px;
    border-bottom: 1px solid #f1f5f9;
    padding-bottom: 8px;
  }

  .detail-badge {
    margin-top: 12px;
    margin-bottom: 16px;
  }

  .dist-footer {
    padding: 16px 24px;
    background: #f8fafc;
    border-top: 1px solid #e2e8f0;
    border-radius: 0 0 12px 12px;
    display: flex;
    justify-content: flex-end;
    gap: 12px;

    .btn-cancel-glass {
      height: 38px;
      padding: 0 20px;
      border-radius: 8px;
      border: 1px solid #cbd5e1;
      color: #64748b;
      font-weight: 600;
      background: #ffffff;
      transition: all 0.3s;
      &:hover {
        color: #1e293b;
        border-color: #94a3b8;
        background: #f1f5f9;
      }
    }

    .btn-submit-architect {
      height: 38px;
      padding: 0 24px;
      border-radius: 8px;
      background: linear-gradient(135deg, #8b5cf6, #7c3aed);
      color: #ffffff;
      font-weight: 600;
      border: none;
      box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
      transition: all 0.3s;
      &:hover {
        background: linear-gradient(135deg, #7c3aed, #6d28d9);
        box-shadow: 0 6px 16px rgba(139, 92, 246, 0.4);
        transform: translateY(-1px);
      }
      &:active {
        transform: translateY(1px);
      }
    }
  }
}
</style>
