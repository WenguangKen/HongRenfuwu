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
    <div class="ra-modal-header glass-header">
      <div class="ra-header-left">
        <div class="ra-header-icon-wrapper" :class="mode">
          <SafetyCertificateOutlined v-if="mode === 'audit'" />
          <DollarOutlined v-else-if="mode === 'pay'" />
          <InfoCircleOutlined v-else />
        </div>
        <div class="ra-header-text">
          <div class="ra-header-title">
            {{ modalTitle }}
            <span class="header-tag" :class="mode">{{ mode.toUpperCase() }}</span>
          </div>
          <div class="ra-header-subtitle">
            <template v-if="mode === 'audit'">审核红人汇款申请 <span class="sub-en">Review remittance request</span></template>
            <template v-else-if="mode === 'pay'">上传凭证并确认打款 <span class="sub-en">Confirm payment & upload voucher</span></template>
            <template v-else>汇款任务详情清单 <span class="sub-en">Remittance task detail details</span></template>
          </div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="ra-modal-body">
      <!-- 详情模式下的信息展示 -->
      <div v-if="task && mode === 'detail'" class="task-info-card">
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">任务编号</span>
            <span class="info-value">{{ task.taskNo }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">关联红人</span>
            <span class="info-value highlighted">{{ task.influencerName }}</span>
          </div>
          <div class="info-item" v-if="task.orderNo">
            <span class="info-label">关联订单</span>
            <span class="info-value">{{ task.orderNo }}</span>
          </div>
          <div class="info-item" v-if="task.paymentMethod">
            <span class="info-label">打款方式</span>
            <span class="info-value">
              <span class="pay-method-tag-inline" :class="task.paymentMethod">{{ formatPaymentMethod(task.paymentMethod) }}</span>
            </span>
          </div>
          <div class="info-item">
            <span class="info-label">打款金额</span>
            <span class="info-value amount">{{ task.currency }} {{ task.amount }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">手续费</span>
            <span class="info-value">{{ task.currency }} {{ task.fee ?? '0.00' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">总付金额</span>
            <span class="info-value amount">{{ task.currency }} {{ task.totalAmount ?? task.amount }}</span>
          </div>
          <div class="info-item full-width highlight-payment">
            <span class="info-label">收款信息 (PAYMENT DETAILS)</span>
            <div class="payment-box" v-if="task.paymentMethod">
              <div class="payment-main">
                <span class="pay-method-tag" :class="task.paymentMethod">{{ formatPaymentMethod(task.paymentMethod) }}</span>
                <span class="pay-account">{{ task.paymentAccount }}</span>
              </div>
              <div class="preview-details-grid grid-detail" v-if="task.paymentMethod === 'bank_card' && parsedPaymentDetails">
                <div class="grid-item"><span class="lbl">国家/地区:</span><span class="val">{{ parsedPaymentDetails?.bankCountry || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">银行名称:</span><span class="val">{{ parsedPaymentDetails?.bankName || '-' }}</span></div>
                <div class="grid-item" v-if="parsedPaymentDetails?.branchName"><span class="lbl">支行名称:</span><span class="val">{{ parsedPaymentDetails?.branchName }}</span></div>
                <div class="grid-item"><span class="lbl">银行地址:</span><span class="val">{{ parsedPaymentDetails?.bankAddress || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">Swift Code:</span><span class="val">{{ parsedPaymentDetails?.swiftCode || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">Routing Number:</span><span class="val">{{ parsedPaymentDetails?.routingNumber || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">账户名:</span><span class="val">{{ parsedPaymentDetails?.accountName || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">账户号码:</span><span class="val">{{ task.paymentAccount || '-' }}</span></div>
                <div class="grid-item"><span class="lbl">收款人地址:</span><span class="val">{{ parsedPaymentDetails?.beneficiaryAddress || '-' }}</span></div>
              </div>
            </div>
          </div>
          <div class="info-item full-width" v-if="task.remark">
            <span class="info-label">任务备注</span>
            <span class="info-value remark-value">{{ task.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 审核/打款模式下的表单 -->
      <a-form v-if="mode !== 'detail'" :model="form" ref="formRef" layout="vertical" class="ra-form">
        <div v-if="task" class="task-info-card" style="margin-bottom: 20px;">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">任务编号</span>
              <span class="info-value">{{ task.taskNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">关联红人</span>
              <span class="info-value highlighted">{{ task.influencerName }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">打款金额 <span class="editable-hint">(必填)</span></span>
              <a-form-item name="editAmount" :rules="[{ required: true, message: '请输入打款金额' }]" style="margin-bottom: 0;">
                <a-input-number
                  v-model:value="form.editAmount"
                  :min="0"
                  :precision="2"
                  :step="0.01"
                  style="width: 100%"
                  :prefix="task.currency"
                  class="premium-input-number"
                />
              </a-form-item>
            </div>
            <div class="info-item">
              <span class="info-label">手续费 <span class="editable-hint">(必填)</span></span>
              <a-form-item name="editFee" :rules="[{ required: true, message: '请输入手续费' }]" style="margin-bottom: 0;">
                <a-input-number
                  v-model:value="form.editFee"
                  :min="0"
                  :precision="2"
                  :step="0.01"
                  style="width: 100%"
                  :prefix="task.currency"
                  class="premium-input-number"
                />
              </a-form-item>
            </div>
            <div class="info-item">
              <span class="info-label">总付费用</span>
              <span class="info-value amount computed-total">{{ task.currency }} {{ computedTotal }}</span>
            </div>
          </div>
        </div>
        <!-- 补充收款信息 -->
        <div class="ra-section">
          <div class="ra-section-header">
            <span class="ra-section-title">收款账户 (Payment Required)</span>
          </div>
          <div class="ra-section-body">
            <a-form-item name="paymentMethod" :rules="[{ required: true, message: '必须选择有效的打款方式' }]">
              <a-select 
                v-model:value="form.paymentMethod" 
                class="premium-select" 
                placeholder="请确认或补充付款方式"
                @change="handlePaymentMethodChange"
                :disabled="loadingPaymentInfo"
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

        <!-- 审核模式 -->
        <template v-if="mode === 'audit'">
          <div class="ra-section">
            <div class="ra-section-header">
              <span class="ra-section-title">审核决策 (Decision)</span>
            </div>
            <div class="ra-section-body">
              <a-form-item label="审核动作" name="action" :rules="[{ required: true, message: '请选择审核结果' }]">
                <a-radio-group v-model:value="form.action" class="premium-radio-group" button-style="solid">
                  <a-radio-button value="approve" class="approve-btn">通过 (Approve)</a-radio-button>
                  <a-radio-button value="reject" class="reject-btn">驳回 (Reject)</a-radio-button>
                </a-radio-group>
              </a-form-item>
              
              <a-form-item label="审核备注" name="remark" style="margin-bottom: 0;">
                <a-textarea 
                  v-model:value="form.remark" 
                  placeholder="请输入审核理由或备注说明..." 
                  :rows="3" 
                  class="premium-textarea"
                />
              </a-form-item>
            </div>
          </div>
        </template>

        <!-- 打款模式 -->
        <template v-if="mode === 'pay'">
          <div class="ra-section">
            <div class="ra-section-header">
              <span class="ra-section-title">打款凭证 (Voucher)</span>
            </div>
            <div class="ra-section-body">
              <a-form-item name="voucherUrl" :rules="[{ required: true, message: '请上传打款凭证' }]">
                <div class="voucher-upload-wrapper" @paste="handlePaste" tabindex="0">
                  <a-upload
                    list-type="picture-card"
                    :show-upload-list="false"
                    :before-upload="beforeUpload"
                    :customRequest="handleUpload"
                    accept="image/*"
                    class="premium-uploader"
                  >
                    <div v-if="form.voucherUrl" class="voucher-preview">
                      <img :src="form.voucherUrl" alt="voucher" />
                      <div class="replace-overlay">更换图片</div>
                    </div>
                    <div v-else class="upload-trigger">
                      <loading-outlined v-if="uploading" />
                      <plus-outlined v-else />
                      <div class="upload-text">点击上传打款凭证</div>
                    </div>
                  </a-upload>
                </div>
              </a-form-item>
              
              <a-form-item label="实际打款时间" name="paidAt" style="margin-bottom: 12px;">
                <a-date-picker
                  v-model:value="form.paidAt"
                  show-time
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  placeholder="请选择实际打款时间"
                  style="width: 100%"
                  class="premium-input"
                />
              </a-form-item>

              <a-form-item label="打款备注" name="remark" style="margin-bottom: 0;">
                <a-textarea 
                  v-model:value="form.remark" 
                  placeholder="请输入打款相关备注（可选）..." 
                  :rows="3" 
                  class="premium-textarea"
                />
              </a-form-item>
            </div>
          </div>
        </template>
      </a-form>

      <!-- 详情记录展示 (Detail/Audit History) -->
      <div v-if="mode === 'detail' || (task && (task.auditorName || task.payerName))" class="ra-section history-section">
        <div class="ra-section-header">
          <span class="ra-section-title">处理历史 (Process History)</span>
        </div>
        <div class="ra-section-body">
          <div class="history-item" v-if="task?.creatorName || task?.createdAt">
            <div class="history-label">
              <PlusCircleOutlined /> 创建记录
            </div>
            <div class="history-content">
              <span class="h-user">{{ task?.creatorName || '系统' }}</span>
              <span class="h-time">{{ task?.createdAt ? dayjs(task.createdAt).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
            </div>
          </div>

          <div class="history-item" v-if="task?.auditorName">
            <div class="history-label">
              <SafetyCertificateOutlined /> 审核记录
            </div>
            <div class="history-content">
              <span class="h-user">{{ task?.auditorName }}</span>
              <span class="h-time">{{ task?.auditTime ? dayjs(task.auditTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
              <div class="h-remark" v-if="task?.auditRemark">备注: {{ task?.auditRemark }}</div>
            </div>
          </div>
          
          <div class="history-item" v-if="task?.payerName">
            <div class="history-label">
              <DollarOutlined /> 打款记录
            </div>
            <div class="history-content">
              <span class="h-user">{{ task?.payerName }}</span>
              <span class="h-time">{{ task?.payTime ? dayjs(task.payTime).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
              <div class="h-remark" v-if="task?.paymentRemark">备注: {{ task?.paymentRemark }}</div>
              <div class="h-voucher" v-if="task?.voucherUrl">
                <a-image :src="task?.voucherUrl" :width="120" class="voucher-img" />
              </div>
            </div>
          </div>
          
          <div v-if="!task?.auditorName && !task?.payerName" class="empty-history">
            暂无处理记录 (No history records)
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="ra-modal-footer">
      <a-button @click="handleCancel" class="premium-btn">取消</a-button>
      
      <template v-if="mode === 'pay'">
        <a-popconfirm
          title="确定要拒绝此汇款申请吗？"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleRejectInPay"
        >
          <a-button type="primary" danger ghost class="premium-btn">拒绝打款</a-button>
        </a-popconfirm>
        <a-button type="default" danger class="premium-btn" @click="rejectModalVisible = true">驳回重审</a-button>
      </template>

      <a-button 
        v-if="mode !== 'detail'"
        type="primary" 
        @click="handleOk" 
        :loading="confirmLoading" 
        class="premium-btn primary-gradient"
        :class="mode === 'audit' && form.action === 'reject' ? 'danger-gradient' : ''"
      >
        {{ mode === 'audit' ? '确认审核' : '确认已打款' }}
      </a-button>
    </div>

    <!-- 驳回重审弹窗 -->
    <a-modal
      v-model:open="rejectModalVisible"
      title="驳回重审"
      ok-text="确认驳回"
      cancel-text="取消"
      :ok-button-props="{ danger: true, disabled: !rejectReason.trim() }"
      @ok="handleRejectToAudit"
      :confirm-loading="rejectLoading"
      centered
      width="480px"
    >
      <div style="margin-bottom: 8px; color: #64748b; font-size: 13px;">驳回后该任务将回到「待审核」列表重新审核，请填写驳回原因：</div>
      <a-textarea
        v-model:value="rejectReason"
        placeholder="请输入驳回原因（必填）..."
        :rows="4"
        class="premium-textarea"
      />
    </a-modal>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { message } from 'ant-design-vue';
import { 
  PlusOutlined, 
  LoadingOutlined, 
  SafetyCertificateOutlined, 
  DollarOutlined,
  CloseOutlined,
  InfoCircleOutlined
} from '@ant-design/icons-vue';
import { remittanceService } from '@/services/remittanceService';
import { influencerService } from '@/services/influencerService';
import { PlusCircleOutlined } from '@ant-design/icons-vue';
import type { RemittanceTask } from '@/types/influencer';
import dayjs from 'dayjs';

const emit = defineEmits(['success']);

const visible = ref(false);
const confirmLoading = ref(false);
const uploading = ref(false);
const mode = ref<'audit' | 'pay' | 'detail'>('audit');
const task = ref<RemittanceTask | null>(null);
const parsedPaymentDetails = ref<any>(null);
const formRef = ref();

// 驳回重审状态
const rejectModalVisible = ref(false);
const rejectReason = ref('');
const rejectLoading = ref(false);

const form = reactive({
  action: 'approve' as 'approve' | 'reject',
  remark: '',
  voucherUrl: '',
  paymentMethod: undefined as string | undefined,
  paymentAccount: '',
  paymentDetails: '',
  editAmount: 0 as number,
  editFee: 0 as number,
  paidAt: '' as string
});

const computedTotal = computed(() => {
  return ((form.editAmount || 0) + (form.editFee || 0)).toFixed(2);
});

const loadingPaymentInfo = ref(false);
const availablePayments = reactive({
  paypal: false,
  bank_card: false
});
const parsedBankDetails = ref<any>(null);

const modalTitle = computed(() => {
  if (mode.value === 'audit') return '任务审核';
  if (mode.value === 'pay') return '确认打款';
  return '任务详情';
});

const formatPaymentMethod = (method?: string) => {
  if (method === 'paypal') return 'PayPal';
  if (method === 'bank_card') return '银行卡 (Bank Transfer)';
  return method || '未指定';
};

const beforeUpload = (file: File) => {
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('凭证图片必须小于 2MB');
  }
  return isLt2M;
};

const handlePaste = (e: ClipboardEvent) => {
  if (mode.value !== 'pay' || uploading.value) return;
  
  const items = e.clipboardData?.items;
  if (!items) return;
  
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (!item) continue;
    if (item.type.indexOf('image') !== -1) {
      const file = item.getAsFile();
      if (file) {
        // 校验大小
        if (beforeUpload(file)) {
          handleUpload({ file, fileName: 'pasted_voucher.png' });
        }
        // 匹配到一个图片就跳出，符合“只能粘贴一张”的要求
        break;
      }
    }
  }
};

const handleUpload = async (options: any) => {
  const { file, fileName } = options;
  uploading.value = true;
  try {
    const res = await influencerService.uploadVoucher(file, fileName);
    if (res.success) {
      form.voucherUrl = res.data.url;
      message.success('上传成功');
    } else {
      message.error(res.message || '上传失败');
    }
  } catch (err) {
    message.error('文件服务器异常');
  } finally {
    uploading.value = false;
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

const handleRejectInPay = async () => {
  if (!task.value) return;
  confirmLoading.value = true;
  try {
    await remittanceService.audit(task.value!.id, {
      action: 'reject',
      remark: form.remark || '支付阶段驳回 (Rejected at payment stage)'
    });
    message.success('已成功驳回该汇款申请');
    emit('success');
    handleCancel();
  } catch (error: any) {
    message.error(error.message || '操作失败');
  } finally {
    confirmLoading.value = false;
  }
};

const handleRejectToAudit = async () => {
  if (!task.value || !rejectReason.value.trim()) return;
  rejectLoading.value = true;
  try {
    await remittanceService.audit(task.value!.id, {
      action: 'reject_to_audit',
      remark: rejectReason.value.trim()
    });
    message.success('已驳回，任务已回到待审核列表');
    rejectModalVisible.value = false;
    rejectReason.value = '';
    emit('success');
    handleCancel();
  } catch (error: any) {
    message.error(error.message || '驳回失败');
  } finally {
    rejectLoading.value = false;
  }
};

const handleOk = async () => {
  if (!task.value) return;
  try {
    await formRef.value.validate();
    confirmLoading.value = true;
    
    if (mode.value === 'audit') {
      await remittanceService.audit(task.value!.id, {
        action: form.action,
        remark: form.remark,
        paymentMethod: form.paymentMethod,
        paymentAccount: form.paymentAccount,
        paymentDetails: form.paymentDetails,
        amount: form.editAmount,
        fee: form.editFee,
        totalAmount: Number(computedTotal.value)
      });
      message.success('审核完成');
    } else {
      await remittanceService.pay(task.value!.id, {
        voucherUrl: form.voucherUrl,
        remark: form.remark,
        paymentMethod: form.paymentMethod,
        paymentAccount: form.paymentAccount,
        paymentDetails: form.paymentDetails,
        amount: form.editAmount,
        fee: form.editFee,
        totalAmount: Number(computedTotal.value),
        paidAt: form.paidAt || undefined
      });
      message.success('打款确认成功');
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
  rejectModalVisible.value = false;
  rejectReason.value = '';
};

const show = async (targetTask: RemittanceTask, targetMode: 'audit' | 'pay' | 'detail') => {
  task.value = targetTask;
  mode.value = targetMode;
  form.action = 'approve';
  form.remark = '';
  form.voucherUrl = '';
  
  form.paymentMethod = targetTask.paymentMethod || undefined;
  form.paymentAccount = targetTask.paymentAccount || '';
  form.paymentDetails = targetTask.paymentDetails || '';
  form.editAmount = Number(targetTask.amount) || 0;
  form.editFee = Number(targetTask.fee) || 0;
  form.paidAt = dayjs().format('YYYY-MM-DD HH:mm:ss');
  rejectReason.value = '';
  
  availablePayments.paypal = false;
  availablePayments.bank_card = false;
  parsedBankDetails.value = null;
  
  if (targetTask.paymentDetails) {
    try {
      parsedPaymentDetails.value = JSON.parse(targetTask.paymentDetails);
      if (form.paymentMethod === 'bank_card') {
        parsedBankDetails.value = parsedPaymentDetails.value;
      }
    } catch (e) {
      parsedPaymentDetails.value = null;
    }
  } else {
    parsedPaymentDetails.value = null;
  }
  
  visible.value = true;
  
  // Async fetch payment settings for editing
  if (targetMode !== 'detail' && targetTask.influencerId) {
    loadingPaymentInfo.value = true;
    try {
      const res = await influencerService.getPaymentInfo(targetTask.influencerId);
      const data = res.data || res;
      if (data) {
        if (data.paypalAccount) availablePayments.paypal = true;
        if (data.accountNumber || data.bankName) availablePayments.bank_card = true;
        (form as any)._rawPaymentInfo = data;
        
        // 如果当前任务没有关联付款方式，但红人有付款信息，我们尝试自动选中默认的一个
        if (!form.paymentMethod) {
            if (availablePayments.bank_card) {
                form.paymentMethod = 'bank_card';
                handlePaymentMethodChange('bank_card');
            } else if (availablePayments.paypal) {
                form.paymentMethod = 'paypal';
                handlePaymentMethodChange('paypal');
            }
        }
      }
    } catch(e) {
      console.error('获取付款信息失败', e);
    } finally {
      loadingPaymentInfo.value = false;
    }
  }
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

.ra-modal-header {
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

  .ra-header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .ra-header-icon-wrapper {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    
    &.audit {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
    }
    &.pay {
      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      box-shadow: 0 4px 12px rgba(245, 158, 11, 0.2);
    }
    &.detail {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
    }
  }

  .ra-header-text {
    .ra-header-title {
      font-size: 18px;
      font-weight: 700;
      color: #1e293b;
      line-height: 1.2;
      display: flex;
      align-items: center;
      gap: 8px;
      
      .header-tag {
        font-size: 10px;
        padding: 2px 6px;
        border-radius: 4px;
        font-weight: 600;
        
        &.audit { background: #ecfdf5; color: #10b981; }
        &.pay { background: #fffbeb; color: #f59e0b; }
        &.detail { background: #eff6ff; color: #3b82f6; }
      }
    }
    .ra-header-subtitle {
      font-size: 12px;
      color: #94a3b8;
      margin-top: 4px;
      .sub-en { opacity: 0.7; margin-left: 4px; }
    }
  }

  .close-btn { color: #94a3b8; &:hover { color: #64748b; background: #f1f5f9; } }
}

.ra-modal-body {
  padding: 24px;
  background: #f8fafc;
  max-height: 520px;
  overflow-y: auto;
  
  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.task-info-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  margin-bottom: 20px;

  .info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
    gap: 16px;
  }

  .info-item {
    display: flex;
    flex-direction: column;
    gap: 4px;

    &.full-width {
      grid-column: 1 / -1;
      margin-top: 4px;
      padding-top: 12px;
      border-top: 1px dashed #e2e8f0;
    }

    .info-label {
      font-size: 11px;
      color: #94a3b8;
      text-transform: uppercase;
      font-weight: 600;

      .editable-hint {
        color: #3b82f6;
        font-size: 10px;
        text-transform: none;
      }
    }
    .info-value {
      font-size: 14px;
      color: #1e293b;
      font-weight: 600;

      &.highlighted { color: #1890ff; }
      &.amount { color: #f43f5e; font-size: 16px; font-family: 'Outfit', sans-serif; }
      &.computed-total {
        color: #059669;
        font-size: 18px;
        font-weight: 700;
        padding: 4px 8px;
        background: rgba(5, 150, 105, 0.06);
        border-radius: 6px;
      }
      &.remark-value {
        font-size: 13px;
        color: #475569;
        font-weight: 500;
        line-height: 1.5;
        white-space: pre-wrap;
      }
    }
  }

  .highlight-payment {
    background: #f8fafc;
    border: 1px dashed #cbd5e1 !important;
    border-radius: 8px;
    padding: 12px;
    margin-top: 12px !important;
    border-top: 1px dashed #cbd5e1 !important;
    
    .info-label {
      color: #3b82f6 !important;
      margin-bottom: 8px;
    }
    
    .payment-box {
      display: flex;
      flex-direction: column;
      gap: 6px;
      
      &.empty-payment {
        justify-content: center;
        padding: 8px 0 4px;
        
        .empty-state-text {
          font-size: 13px;
          color: #94a3b8;
          font-style: italic;
        }
      }
      
      .payment-main {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .pay-method-tag {
          font-size: 11px;
          padding: 3px 8px;
          border-radius: 4px;
          font-weight: 700;
          color: white;
          
          &.paypal { background: #003087; }
          &.bank_card { background: #059669; }
        }
        
        .pay-account {
          font-size: 15px;
          font-weight: 700;
          color: #0f172a;
          font-family: monospace;
        }
      }
      
      .payment-extra {
        font-size: 12px;
        color: #64748b;
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 8px;
        
        .divider {
          color: #cbd5e1;
        }
      }
    }
  }
}

.ra-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  margin-bottom: 20px;
  overflow: hidden;
  
  .ra-section-header {
    padding: 10px 16px;
    border-bottom: 1px solid #f8fafc;
    background: #fafbfc;
    
    .ra-section-title {
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
  
  .ra-section-body { padding: 16px; }
}

.payment-preview {
  margin-top: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px dashed #cbd5e1;
  font-size: 13px;
  
  .preview-label { color: #64748b; font-weight: 600; margin-bottom: 8px; }
  
  .preview-details-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px 16px;
    background: #ffffff;
    padding: 12px;
    border-radius: 6px;
    border: 1px solid #e2e8f0;
    
    &.grid-detail {
        margin-top: 10px;
        background: #f8fafc;
        border: 1px dashed #cbd5e1;
    }
    
    .grid-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .lbl { color: #64748b; font-size: 11px; }
      .val { color: #0f172a; font-size: 13px; font-weight: 500; word-break: break-all; }
    }
  }
}

/* History Section Styles */
.history-section {
  .ra-section-body {
    padding: 0;
  }

  .history-item {
    display: flex;
    padding: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.04);
    
    &:last-child {
      border-bottom: none;
    }

    .history-label {
      width: 100px;
      flex-shrink: 0;
      font-weight: 600;
      color: #666;
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      
      .anticon {
        font-size: 16px;
        color: #1890ff;
      }
    }

    .history-content {
      flex: 1;
      
      .h-user {
        font-weight: 600;
        color: #1a1a1a;
        margin-right: 12px;
      }
      
      .h-time {
        color: #8c8c8c;
        font-size: 12px;
      }
      
      .h-remark {
        margin-top: 4px;
        color: #434343;
        background: rgba(0, 0, 0, 0.02);
        padding: 4px 10px;
        border-radius: 4px;
        font-size: 13px;
      }
      
      .h-voucher {
        margin-top: 10px;
        
        .voucher-img {
          border-radius: 8px;
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
          cursor: pointer;
          transition: transform 0.2s;
          
          &:hover {
            transform: scale(1.02);
          }
        }
      }
    }
  }

  .empty-history {
    padding: 30px;
    text-align: center;
    color: #bfbfbf;
    font-style: italic;
  }
}

.premium-radio-group {
  display: flex;
  gap: 12px;
  width: 100%;

  :deep(.ant-radio-button-wrapper) {
    flex: 1;
    height: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px !important;
    border-left: 1px solid #d9d9d9 !important;
    font-weight: 600;
    transition: all 0.3s;

    &::before { display: none; }

    &.approve-btn.ant-radio-button-wrapper-checked {
      background: #ecfdf5;
      color: #10b981;
      border-color: #10b981 !important;
    }
    &.reject-btn.ant-radio-button-wrapper-checked {
      background: #fff1f2;
      color: #f43f5e;
      border-color: #f43f5e !important;
    }
  }
}

.voucher-upload-wrapper {
  display: flex;
  justify-content: center;
  padding: 8px 0;
  outline: none;
  border-radius: 12px;
  transition: all 0.3s;
  
  &:focus-within {
    background: rgba(245, 158, 11, 0.05);
    :deep(.premium-uploader) .ant-upload-select-picture-card {
      border-color: #f59e0b;
      box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
    }
  }
}

:deep(.premium-uploader) {
  .ant-upload-select-picture-card {
    width: 240px;
    height: 160px;
    border-radius: 12px;
    border: 2px dashed #e2e8f0;
    background: #f8fafc;
    transition: all 0.3s;

    &:hover { border-color: #1890ff; background: #eff6ff; }
  }

  .upload-trigger {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: #94a3b8;
    
    .anticon { font-size: 24px; }
    .upload-text { font-size: 12px; font-weight: 500; }
  }

  .voucher-preview {
    position: relative;
    width: 100%;
    height: 100%;
    border-radius: 10px;
    overflow: hidden;

    img { width: 100%; height: 100%; object-fit: contain; }

    .replace-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 12px;
      font-weight: 600;
      opacity: 0;
      transition: opacity 0.3s;
    }
    &:hover .replace-overlay { opacity: 1; }
  }
}

:deep(.ra-form) {
  .ant-form-item {
    margin-bottom: 16px;
    .ant-form-item-label label { font-size: 13px; font-weight: 600; color: #475569; }
    .ant-textarea {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      &:hover, &:focus { border-color: #1890ff !important; box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.08) !important; }
    }
  }
}

.ra-modal-footer {
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

.danger-gradient {
  background: linear-gradient(135deg, #f43f5e 0%, #fb7185 100%);
  box-shadow: 0 4px 12px rgba(244, 63, 94, 0.25);
  &:hover { box-shadow: 0 6px 20px rgba(244, 63, 94, 0.35); }
}
</style>
