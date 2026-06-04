<template>
  <div class="payment-info-tab">
    <div class="cards-row">
      <!-- 银行转账卡片 -->
      <div class="info-card bank-card">
        <div class="card-header">
          <span class="card-title">银行转账 (Bank Transfer)</span>
          <a-button v-if="!editingBank" type="primary" danger size="small" class="edit-btn" @click="editingBank = true">编 辑</a-button>
          <a-space v-else>
            <a-button size="small" @click="cancelBankEdit">取消</a-button>
            <a-button type="primary" danger size="small" :loading="saving" @click="handleSave">保存</a-button>
          </a-space>
        </div>
        <div class="card-body">
          <template v-if="!editingBank">
            <div class="info-row" v-for="field in bankFields" :key="field.key">
              <span class="info-label">{{ field.label }}</span>
              <span class="info-value">{{ formData[field.key] || '-' }}</span>
            </div>
          </template>
          <template v-else>
            <a-form layout="vertical" size="small">
              <a-form-item v-for="field in bankFields" :key="field.key" :label="field.label">
                <a-input v-model:value="formData[field.key]" :placeholder="'请输入' + field.label" />
              </a-form-item>
            </a-form>
          </template>
        </div>
      </div>

      <!-- PayPal 卡片 -->
      <div class="info-card paypal-card">
        <div class="card-header">
          <span class="card-title">PayPal 收款</span>
          <a-button v-if="!editingPaypal" type="primary" danger size="small" class="edit-btn" @click="editingPaypal = true">编 辑</a-button>
          <a-space v-else>
            <a-button size="small" @click="cancelPaypalEdit">取消</a-button>
            <a-button type="primary" danger size="small" :loading="saving" @click="handleSave">保存</a-button>
          </a-space>
        </div>
        <div class="card-body">
          <template v-if="!editingPaypal">
            <div class="info-row">
              <span class="info-label">PayPal 账号</span>
              <span class="info-value">{{ formData.paypalAccount || '-' }}</span>
            </div>
          </template>
          <template v-else>
            <a-form layout="vertical" size="small">
              <a-form-item label="PayPal 账号">
                <a-input v-model:value="formData.paypalAccount" placeholder="请输入 PayPal 邮箱/账号" />
              </a-form-item>
            </a-form>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { message } from 'ant-design-vue';
import { influencerService } from '@/services/influencerService';

const props = defineProps<{
  influencer: any;
}>();

const editingBank = ref(false);
const editingPaypal = ref(false);
const saving = ref(false);

const bankFields = [
  { key: 'bankCountry', label: '国家/地区' },
  { key: 'bankName', label: '银行名称' },
  { key: 'branchName', label: '支行名称' },
  { key: 'bankAddress', label: '银行地址' },
  { key: 'swiftCode', label: 'Swift Code' },
  { key: 'routingNumber', label: 'Routing Number' },
  { key: 'accountName', label: '账户名' },
  { key: 'accountNumber', label: '账户号码' },
  { key: 'beneficiaryAddress', label: '收款人地址' },
];

const formData = reactive<Record<string, string>>({
  bankCountry: '',
  bankName: '',
  branchName: '',
  bankAddress: '',
  swiftCode: '',
  routingNumber: '',
  accountName: '',
  accountNumber: '',
  beneficiaryAddress: '',
  paypalAccount: '',
});

let originalData: Record<string, string> = {};

const loadData = async () => {
  const id = props.influencer?.id || props.influencer?.influencerId;
  if (!id) return;
  try {
    const res = await influencerService.getPaymentInfo(id) as any;
    if (res) {
      for (const key of Object.keys(formData)) {
        formData[key] = res[key] || '';
      }
    }
    originalData = { ...formData };
  } catch {
    // 可能尚未创建记录
  }
};

watch(() => props.influencer?.id, (v) => { if (v) loadData(); }, { immediate: true });

const cancelBankEdit = () => {
  editingBank.value = false;
  Object.assign(formData, originalData);
};

const cancelPaypalEdit = () => {
  editingPaypal.value = false;
  Object.assign(formData, originalData);
};

const handleSave = async () => {
  const id = props.influencer?.id || props.influencer?.influencerId;
  if (!id) return;
  saving.value = true;
  try {
    await influencerService.savePaymentInfo(id, { ...formData });
    message.success('收款信息已保存');
    editingBank.value = false;
    editingPaypal.value = false;
    originalData = { ...formData };
  } catch (e: any) {
    message.error(e?.message || '保存失败');
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.payment-info-tab {
  height: 100%;
  overflow-y: auto;
  padding: 16px 8px;
}

.cards-row {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.info-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03), 0 1px 6px -1px rgba(0, 0, 0, 0.02), 0 2px 4px 0 rgba(0, 0, 0, 0.02);
  overflow: hidden;
  
  &.bank-card {
    flex: 3;
    min-width: 0; 
  }
  &.paypal-card {
    flex: 2;
    min-width: 0;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.edit-btn {
  border-radius: 4px;
  padding: 0 16px;
  height: 28px;
  font-size: 13px;
  box-shadow: none;
}

.card-body {
  padding: 24px;
}

.info-row {
  display: flex;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px solid #f8fafc;
  
  &:first-child {
    padding-top: 0;
  }
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.info-label {
  width: 140px;
  flex-shrink: 0;
  font-size: 14px;
  color: #4b5563;
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: #111827;
  word-break: break-all;
}

:deep(.ant-form-item) {
  margin-bottom: 16px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

:deep(.ant-form-item-label > label) {
  color: #4b5563;
  font-size: 14px;
}
</style>
