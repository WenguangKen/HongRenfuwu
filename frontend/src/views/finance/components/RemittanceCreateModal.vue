<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="640px"
    class="premium-refined-modal"
    :mask-closable="false"
    :closable="false"
    centered
    destroy-on-close
  >
    <!-- Custom Header -->
    <div class="rc-modal-header glass-header">
      <div class="rc-header-left">
        <div class="rc-header-icon-wrapper">
          <PayCircleOutlined />
        </div>
        <div class="rc-header-text">
          <div class="rc-header-title">
            {{ isEdit ? '编辑汇款任务' : '创建汇款任务' }}
            <span class="header-tag">Remittance</span>
          </div>
          <div class="rc-header-subtitle">
            {{ isEdit ? '修改红人汇款申请信息' : '发起新的红人汇款申请' }}
            <span class="sub-en">{{ isEdit ? 'Edit remittance task' : 'Create a new remittance task' }}</span>
          </div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- Content Body -->
    <div class="rc-modal-body">
      <a-form :model="form" :rules="rules" ref="formRef" layout="vertical" class="rc-form">
        <!-- Influencer Selection -->
        <div class="rc-section">
          <div class="rc-section-header">
            <span class="rc-section-title">对象与关联 (Target & Link)</span>
          </div>
          <div class="rc-section-body">
            <a-form-item label="关联红人" name="influencerId">
              <a-select
                v-model:value="form.influencerId"
                show-search
                placeholder="搜索并选择红人"
                :filter-option="false"
                @search="handleInfluencerSearch"
                @change="handleInfluencerChange"
                :loading="influencerLoading"
                class="premium-select"
              >
                <a-select-option v-for="item in influencerOptions" :key="item.id" :value="item.id">
                  {{ item.realName }}{{ item.defaultHandle ? ` @${item.defaultHandle}` : '' }} ({{ item.email }})
                </a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="关联订单号" name="orderNo" style="margin-bottom: 0;">
              <a-auto-complete
                v-model:value="form.orderNo"
                placeholder="可以直接输入，或选择红人已有订单"
                :disabled="!form.influencerId"
                :options="orderOptions.map(o => ({ value: o.orderName }))"
                class="premium-select"
              >
                <template #option="{ value }">
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ value }}</span>
                    <span style="color: #94a3b8; font-size: 12px;">(已有订单)</span>
                  </div>
                </template>
              </a-auto-complete>
              <div class="tip" v-if="!form.influencerId">
                <InfoCircleOutlined /> 请先选择红人以查看其关联订单建议
              </div>
            </a-form-item>
          </div>
        </div>

        <div class="rc-section">
          <div class="rc-section-header">
            <span class="rc-section-title">金额设置 (Amount Setting)</span>
          </div>
          <div class="rc-section-body">
            <div class="rc-form-row two-cols">
              <a-form-item label="付费类型" name="paymentType">
                <a-select v-model:value="form.paymentType" class="premium-select" placeholder="请选择">
                  <a-select-option v-for="tag in paymentTypeOptions" :key="tag.id" :value="tag.name">
                    {{ tag.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="币种" name="currency">
                <a-select v-model:value="form.currency" class="premium-select">
                  <a-select-option value="USD">USD</a-select-option>
                  <a-select-option value="CNY">CNY</a-select-option>
                  <a-select-option value="EUR">EUR</a-select-option>
                  <a-select-option value="GBP">GBP</a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <div class="rc-form-row two-cols">
              <a-form-item label="付费金额" name="amount">
                <a-input-number
                  v-model:value="form.amount"
                  placeholder="请输入金额"
                  style="width: 100%"
                  :min="0"
                  :precision="2"
                  class="premium-number-input"
                />
              </a-form-item>
              <a-form-item label="手续费">
                <a-input-number
                  v-model:value="form.fee"
                  placeholder="0.00"
                  style="width: 100%"
                  :min="0"
                  :precision="2"
                  class="premium-number-input"
                />
              </a-form-item>
            </div>
            <div class="total-amount-display">
              <span class="total-label">总付费用</span>
              <span class="total-value">{{ form.currency }} {{ totalAmount }}</span>
            </div>
          </div>
        </div>

        <!-- Payment Method Section -->
        <div class="rc-section">
          <div class="rc-section-header">
            <span class="rc-section-title">收款方式 (Payment Details)</span>
          </div>
          <div class="rc-section-body">
            <a-form-item label="付款方式" name="paymentMethod">
              <a-select 
                v-model:value="form.paymentMethod" 
                class="premium-select" 
                placeholder="请选择付款方式"
                @change="handlePaymentMethodChange"
                :disabled="!form.influencerId || loadingPaymentInfo"
              >
                <a-select-option value="paypal" :disabled="!availablePayments.paypal">
                  PayPal ({{ availablePayments.paypal ? '已配置' : '未配置' }})
                </a-select-option>
                <a-select-option value="bank_card" :disabled="!availablePayments.bank_card">
                  银行卡 ({{ availablePayments.bank_card ? '已配置' : '未配置' }})
                </a-select-option>
              </a-select>
              
              <div v-if="form.paymentMethod" class="payment-preview">
                <div class="preview-label">将要打入以下账号：</div>
                
                <div class="preview-details-grid" v-if="form.paymentMethod === 'bank_card'">
                  <div class="grid-item"><span class="lbl">国家/地区:</span><span class="val">{{ parsedBankDetails?.bankCountry || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">银行名称:</span><span class="val">{{ parsedBankDetails?.bankName || '-' }}</span></div>
                  <div class="grid-item" v-if="parsedBankDetails?.branchName"><span class="lbl">支行名称:</span><span class="val">{{ parsedBankDetails?.branchName }}</span></div>
                  <div class="grid-item"><span class="lbl">银行地址:</span><span class="val">{{ parsedBankDetails?.bankAddress || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">Swift Code:</span><span class="val">{{ parsedBankDetails?.swiftCode || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">Routing Number:</span><span class="val">{{ parsedBankDetails?.routingNumber || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">账户名:</span><span class="val">{{ parsedBankDetails?.accountName || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">账户号码:</span><span class="val">{{ form.paymentAccount || '-' }}</span></div>
                  <div class="grid-item"><span class="lbl">收款人地址:</span><span class="val">{{ parsedBankDetails?.beneficiaryAddress || '-' }}</span></div>
                </div>
                
                <div class="preview-details-grid" v-if="form.paymentMethod === 'paypal'">
                  <div class="grid-item"><span class="lbl">PayPal 账号:</span><span class="val">{{ form.paymentAccount || '-' }}</span></div>
                </div>
              </div>
            </a-form-item>
          </div>
        </div>

        <!-- Remark Section -->
        <div class="rc-section no-margin">
          <div class="rc-section-header">
            <span class="rc-section-title">备注说明 (Remarks)</span>
          </div>
          <div class="rc-section-body">
            <a-form-item name="remark" style="margin-bottom: 0;">
              <a-textarea 
                v-model:value="form.remark" 
                placeholder="请输入汇款备注内容..." 
                :rows="3" 
                class="premium-textarea"
              />
            </a-form-item>
          </div>
        </div>
      </a-form>
    </div>

    <!-- Footer -->
    <div class="rc-modal-footer">
      <a-button @click="handleCancel" class="premium-btn">取消</a-button>
      <a-button type="primary" @click="handleOk" :loading="confirmLoading" class="premium-btn primary-gradient">
        {{ isEdit ? '保存修改' : '确认提交任务' }}
      </a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import { 
  PayCircleOutlined, 
  CloseOutlined, 
  InfoCircleOutlined
} from '@ant-design/icons-vue';
import { influencerService } from '@/services/influencerService';
import { getSampleOrdersByInfluencer } from '@/services/influencerOrderService';
import { remittanceService } from '@/services/remittanceService';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';

const emit = defineEmits(['success']);

const visible = ref(false);
const confirmLoading = ref(false);
const influencerLoading = ref(false);
const loadingPaymentInfo = ref(false);
const isEdit = ref(false);
const editingTaskId = ref<number | null>(null);
const formRef = ref();
const influencerOptions = ref<any[]>([]);
const orderOptions = ref<any[]>([]);
const commonStore = useCommonDataStore();
const { paymentTypeTags } = storeToRefs(commonStore);
const paymentTypeOptions = computed(() => paymentTypeTags.value);

const form = reactive({
  influencerId: undefined as number | undefined,
  orderNo: undefined as string | undefined,
  currency: 'USD',
  amount: undefined as number | undefined,
  fee: 0 as number,
  paymentType: undefined as string | undefined,
  paymentMethod: undefined as string | undefined,
  paymentAccount: '' as string,
  paymentDetails: '' as string,
  remark: ''
});

const availablePayments = reactive({
  paypal: false,
  bank_card: false
});

const parsedBankDetails = ref<any>(null);

const rules = {
  influencerId: [{ required: true, message: '请选择关联红人', trigger: 'change' }],
  paymentType: [{ required: true, message: '请选择付费类型', trigger: 'change' }],
  currency: [{ required: true, message: '请选择币种', trigger: 'change' }],
  amount: [{ required: true, message: '请输入付费金额', trigger: 'blur' }],
  fee: [{ required: true, message: '请输入手续费', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择付款方式', trigger: 'change' }]
};

const totalAmount = computed(() => {
  const amt = Number(form.amount) || 0;
  const fee = Number(form.fee) || 0;
  return (amt + fee).toFixed(2);
});

const handleInfluencerSearch = async (val: string) => {
  // Allow empty search to load initial list
  influencerLoading.value = true;
  try {
    const res = await influencerService.getList({
      searchKey: val || undefined,
      page: 1,
      size: 20
    });
    // 按匹配度排序：精确匹配 > 开头匹配 > 模糊包含
    const keyword = (val || '').toLowerCase().trim();
    if (keyword) {
      res.content.sort((a: any, b: any) => {
        return getRelevanceScore(a, keyword) - getRelevanceScore(b, keyword);
      });
    }
    influencerOptions.value = res.content;
  } catch (err) {
    console.error(err);
  } finally {
    influencerLoading.value = false;
  }
};

// 匹配度评分（越小越靠前）
const getRelevanceScore = (item: any, keyword: string): number => {
  const name = (item.realName || '').toLowerCase();
  const handle = (item.defaultHandle || '').toLowerCase();
  const email = (item.email || '').toLowerCase();
  const nick = (item.nickName || '').toLowerCase();

  // 精确匹配
  if (name === keyword) return 0;
  if (handle === keyword) return 1;
  if (email === keyword) return 2;
  if (nick === keyword) return 3;

  // 开头匹配
  if (name.startsWith(keyword)) return 10;
  if (handle.startsWith(keyword)) return 11;
  if (email.startsWith(keyword)) return 12;
  if (nick.startsWith(keyword)) return 13;

  // 模糊包含
  if (name.includes(keyword)) return 20;
  if (handle.includes(keyword)) return 21;
  if (email.includes(keyword)) return 22;
  if (nick.includes(keyword)) return 23;

  return 99;
};

const handleInfluencerChange = async (val: number) => {
  form.orderNo = undefined;
  form.paymentMethod = undefined;
  form.paymentAccount = '';
  form.paymentDetails = '';
  parsedBankDetails.value = null;
  availablePayments.paypal = false;
  availablePayments.bank_card = false;
  orderOptions.value = [];
  
  if (!val) return;
  
  // Load Orders
  try {
    const orders = await getSampleOrdersByInfluencer(val);
    orderOptions.value = orders;
  } catch (err) {
    message.warning('获取红人订单失败');
  }
  
  // Load Payment Info
  loadingPaymentInfo.value = true;
  try {
    const res = await influencerService.getPaymentInfo(val);
    const data = res.data || res;
    if (data) {
      if (data.paypalAccount) {
        availablePayments.paypal = true;
      }
      if (data.accountNumber || data.bankName) {
        availablePayments.bank_card = true;
      }
      
      // Store full response locally for method selection
      (form as any)._rawPaymentInfo = data;
      
      // Auto-select if only one is available
      if (availablePayments.paypal && !availablePayments.bank_card) {
        form.paymentMethod = 'paypal';
        handlePaymentMethodChange('paypal');
      } else if (!availablePayments.paypal && availablePayments.bank_card) {
        form.paymentMethod = 'bank_card';
        handlePaymentMethodChange('bank_card');
      }
    }
  } catch (err) {
    message.warning('获取红人收款信息失败，可能未配置');
  } finally {
    loadingPaymentInfo.value = false;
  }
};

const handlePaymentMethodChange = (val: string) => {
  const data = (form as any)._rawPaymentInfo;
  if (!data) return;
  
  if (val === 'paypal') {
    form.paymentAccount = data.paypalAccount || '';
    form.paymentDetails = JSON.stringify({ paypalAccount: data.paypalAccount }, null, 2);
    parsedBankDetails.value = null;
  } else if (val === 'bank_card') {
    form.paymentAccount = data.accountNumber || '';
    parsedBankDetails.value = {
      bankCountry: data.bankCountry,
      bankName: data.bankName,
      branchName: data.branchName,
      bankAddress: data.bankAddress,
      accountName: data.accountName,
      routingNumber: data.routingNumber,
      swiftCode: data.swiftCode,
      beneficiaryAddress: data.beneficiaryAddress
    };
    form.paymentDetails = JSON.stringify(parsedBankDetails.value, null, 2);
  }
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    const submitData = {
      ...form,
      totalAmount: Number(totalAmount.value)
    };
    
    if (isEdit.value && editingTaskId.value) {
      await remittanceService.update(editingTaskId.value, submitData as any);
      message.success('更新汇款任务成功');
    } else {
      await remittanceService.create(submitData as any);
      message.success('创建汇款任务成功');
    }
    visible.value = false;
    emit('success');
  } catch (err) {
    console.error(err);
  } finally {
    confirmLoading.value = false;
  }
};

const handleCancel = () => {
  visible.value = false;
};

const show = async (task?: any) => {
  visible.value = true;
  isEdit.value = !!task;
  editingTaskId.value = task ? task.id : null;
  
  if (task) {
    form.influencerId = task.influencerId;
    form.orderNo = task.orderNo;
    form.currency = task.currency;
    form.amount = task.amount;
    form.fee = task.fee || 0;
    form.paymentType = task.paymentType;
    form.paymentMethod = task.paymentMethod;
    form.paymentAccount = task.paymentAccount;
    form.paymentDetails = task.paymentDetails;
    form.remark = task.remark;
    
    // Load influencer details to populate options and raw info
    handleInfluencerSearch(task.influencerName || '');
    handleInfluencerChange(task.influencerId);
  } else {
    form.influencerId = undefined;
    form.orderNo = undefined;
    form.currency = 'USD';
    form.amount = undefined;
    form.fee = 0;
    form.paymentType = undefined;
    form.paymentMethod = undefined;
    form.paymentAccount = '';
    form.paymentDetails = '';
    form.remark = '';
    
    availablePayments.paypal = false;
    availablePayments.bank_card = false;
    parsedBankDetails.value = null;
    
    influencerOptions.value = [];
    orderOptions.value = [];
    
    // 初始加载前20名红人
    handleInfluencerSearch('');
  }
  
  commonStore.loadPaymentTypeTags();
};

defineExpose({ show });
</script>

<style lang="scss" scoped>
:deep(.premium-refined-modal) {
  .ant-modal-content {
    padding: 0;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }
}

.rc-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  
  &.glass-header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
  }

  .rc-header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .rc-header-icon-wrapper {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
  }

  .rc-header-text {
    .rc-header-title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      line-height: 1.2;
      display: flex;
      align-items: center;
      gap: 8px;
      
      .header-tag {
        font-size: 10px;
        background: #eff6ff;
        color: #1890ff;
        padding: 2px 6px;
        border-radius: 4px;
        font-weight: 600;
        text-transform: uppercase;
      }
    }
    .rc-header-subtitle {
      font-size: 12px;
      color: #94a3b8;
      margin-top: 4px;
      .sub-en { opacity: 0.7; margin-left: 4px; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    }
  }

  .close-btn { color: #94a3b8; &:hover { color: #64748b; background: #f1f5f9; } }
}

.rc-modal-body {
  padding: 24px;
  background: #f8fafc;
  max-height: 520px;
  overflow-y: auto;
  
  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.rc-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  margin-bottom: 20px;
  overflow: hidden;
  
  &.no-margin { margin-bottom: 0; }
  
  .rc-section-header {
    padding: 10px 16px;
    border-bottom: 1px solid #f8fafc;
    background: #fafbfc;
    
    .rc-section-title {
      font-size: 11px;
      font-weight: 700;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      &:before {
        content: ""; display: inline-block; width: 3px; height: 10px; 
        background: #1890ff; border-radius: 2px; margin-right: 6px; vertical-align: middle;
      }
    }
  }
  
  .rc-section-body { padding: 16px; }
}

.rc-form-row {
  display: grid; grid-template-columns: 140px 1fr; gap: 16px;
  &.three-cols {
    grid-template-columns: 1fr 100px 1fr;
  }
  &.two-cols {
    grid-template-columns: 1fr 1fr;
  }
}

.total-amount-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #eff6ff 0%, #e0f2fe 100%);
  border-radius: 8px;
  border: 1px solid #bfdbfe;
  margin-top: 4px;

  .total-label {
    font-size: 13px;
    font-weight: 700;
    color: #1e40af;
  }

  .total-value {
    font-size: 18px;
    font-weight: 800;
    color: #1d4ed8;
    font-family: 'JetBrains Mono', monospace;
  }
}

:deep(.rc-form) {
  .ant-form-item {
    margin-bottom: 16px;
    .ant-form-item-label label { font-size: 13px; font-weight: 600; color: #475569; }
    .ant-input, .ant-select-selector, .ant-input-number {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      &:hover, &:focus { border-color: #1890ff !important; box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.08) !important; }
    }
  }
}

.tip {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.rc-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
}

.premium-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s;
}

.primary-gradient {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.25);
  
  &:hover {
    box-shadow: 0 6px 20px rgba(24, 144, 255, 0.35);
    transform: translateY(-1px);
  }
}

.payment-preview {
  margin-top: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px dashed #cbd5e1;
  font-size: 13px;
  
  .preview-label {
    color: #64748b;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .preview-details-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px 16px;
    background: #ffffff;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #e2e8f0;
    
    .grid-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .lbl { color: #64748b; font-size: 11px; }
      .val { color: #0f172a; font-size: 13px; font-weight: 500; word-break: break-all; }
    }
  }
}
</style>
