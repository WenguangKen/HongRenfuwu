<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="900px"
    :footer="null"
    centered
    class="premium-refined-modal"
    :mask-closable="false"
    destroy-on-close
  >
    <!-- 统一高端头部 -->
    <template #title>
      <div class="glass-header-compact">
        <div class="header-main">
          <div class="logo-box">
            <span class="logo-text">UP</span>
          </div>
          <div class="title-meta">
            <div class="main-title">上传凭证并确认打款</div>
            <div class="simple-subtitle">核对锁定的收款账户信息并上传银行回执</div>
          </div>
        </div>
      </div>
    </template>

    <!-- 内容 -->
    <div class="modal-body-container" style="padding: 24px; overflow-y: auto; max-height: 65vh;">
      <a-form :model="formLocal" layout="vertical" class="uv-form">
        <a-row :gutter="24">
           <!-- 左侧：只读账单信息 + 处理历史 -->
           <a-col :span="12">
              <div class="uv-section">
                <div class="uv-section-header" style="background: #f8fafc; border-bottom: 1px solid #e2e8f0;">
                  <span class="uv-section-title" style="color: #475569;"><SafetyCertificateOutlined style="margin-right: 6px; color: #3b82f6;" /> 收款目标账户 (审核时锁定)</span>
                </div>
                <div class="uv-section-body">
                   <div class="readonly-field-group">
                      <div class="ro-label">支付方式</div>
                      <div class="ro-value"><a-tag color="blue">{{ formLocal.paymentMethodDisplay || '未指定' }}</a-tag></div>
                   </div>

                   <!-- PayPal 视图 -->
                   <template v-if="formLocal.paymentMethodDisplay?.toLowerCase() === 'paypal' || formLocal.rawData?.paymentMethod === 'paypal'">
                      <div class="readonly-field-group">
                         <div class="ro-label">PayPal 账号 (邮箱)</div>
                         <div class="ro-value">{{ formLocal.paymentDetails?.paypalAccount || '-' }}</div>
                      </div>
                   </template>

                   <!-- Bank Transfer 视图 -->
                   <template v-if="formLocal.paymentMethodDisplay?.toLowerCase() === 'bank transfer' || formLocal.rawData?.paymentMethod === 'bank_card'">
                      <a-row :gutter="12">
                         <a-col :span="12">
                            <div class="readonly-field-group">
                               <div class="ro-label">银行所在国家</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.bankCountry || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12">
                            <div class="readonly-field-group">
                               <div class="ro-label">银行名称</div>
                               <div class="ro-value" style="font-weight: 600;">{{ formLocal.paymentDetails?.bankName || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="24">
                            <div class="readonly-field-group">
                               <div class="ro-label">银行地址</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.bankAddress || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12" v-if="formLocal.paymentDetails?.branchName">
                            <div class="readonly-field-group">
                               <div class="ro-label">支行名称</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.branchName || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12">
                            <div class="readonly-field-group">
                               <div class="ro-label">Swift Code</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.swiftCode || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12" v-if="formLocal.paymentDetails?.bankCountry === 'US' || formLocal.paymentDetails?.routingNumber">
                            <div class="readonly-field-group">
                               <div class="ro-label">ACH Routing Number</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.routingNumber || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12">
                            <div class="readonly-field-group">
                               <div class="ro-label">收款人姓名</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.accountName || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="12">
                            <div class="readonly-field-group">
                               <div class="ro-label">收款人账号</div>
                               <div class="ro-value account-highlight">{{ formLocal.paymentDetails?.accountNumber || '-' }}</div>
                            </div>
                         </a-col>
                         <a-col :span="24">
                            <div class="readonly-field-group mb-0">
                               <div class="ro-label">收款人地址</div>
                               <div class="ro-value">{{ formLocal.paymentDetails?.beneficiaryAddress || '-' }}</div>
                            </div>
                         </a-col>
                      </a-row>
                   </template>

                   <!-- 兜底防错 -->
                   <template v-if="!formLocal.paymentMethodDisplay">
                      <div style="padding: 20px 0; color: #f43f5e; font-size: 13px;">
                         未检测到有效的收款方式。如果该条目为较早生成的旧数据，请先退回重新审核。
                      </div>
                   </template>
                </div>
              </div>

              <!-- 处理历史 (Process History) -->
              <div class="uv-section" style="margin-top: 12px;">
                <div class="uv-section-header">
                  <span class="uv-section-title"><HistoryOutlined style="margin-right: 6px; color: #8b5cf6;" /> 处理历史 (PROCESS HISTORY)</span>
                </div>
                <div class="uv-section-body history-body">
                  <div class="history-item" v-if="formLocal.rawData?.createdBy || formLocal.rawData?.createdAt">
                    <div class="history-icon create"><PlusCircleOutlined /></div>
                    <div class="history-content">
                      <div class="history-label">创建记录</div>
                      <div class="history-meta">
                        <span class="h-user">{{ formLocal.rawData?.createdBy || '系统' }}</span>
                        <span class="h-time">{{ formLocal.rawData?.createdAt ? dayjs(formLocal.rawData.createdAt).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
                      </div>
                      <div class="h-remark" v-if="formLocal.rawData?.remark">备注: {{ formLocal.rawData.remark }}</div>
                    </div>
                  </div>

                  <div class="history-item" v-if="formLocal.rawData?.approvedBy">
                    <div class="history-icon audit"><SafetyCertificateOutlined /></div>
                    <div class="history-content">
                      <div class="history-label">审核记录</div>
                      <div class="history-meta">
                        <span class="h-user">{{ formLocal.rawData.approvedBy }}</span>
                        <span class="h-time">{{ formLocal.rawData?.approvedAt ? dayjs(formLocal.rawData.approvedAt).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
                      </div>
                      <div class="h-remark" v-if="formLocal.rawData?.reviewRemark">备注: {{ formLocal.rawData.reviewRemark }}</div>
                    </div>
                  </div>

                  <div class="history-item" v-if="formLocal.rawData?.rejectedBy">
                    <div class="history-icon reject"><CloseCircleOutlined /></div>
                    <div class="history-content">
                      <div class="history-label">驳回记录</div>
                      <div class="history-meta">
                        <span class="h-user">{{ formLocal.rawData.rejectedBy }}</span>
                        <span class="h-time">{{ formLocal.rawData?.rejectedAt ? dayjs(formLocal.rawData.rejectedAt).format('YYYY-MM-DD HH:mm:ss') : '' }}</span>
                      </div>
                      <div class="h-remark" v-if="formLocal.rawData?.reviewRemark">原因: {{ formLocal.rawData.reviewRemark }}</div>
                    </div>
                  </div>

                  <div v-if="!formLocal.rawData?.createdBy && !formLocal.rawData?.approvedBy && !formLocal.rawData?.rejectedBy" class="empty-history">
                    暂无处理记录
                  </div>
                </div>
              </div>
           </a-col>
           
           <!-- 右侧：凭证上传及确认金额 -->
           <a-col :span="12">
              <div class="uv-section h-full">
                <div class="uv-section-header">
                  <span class="uv-section-title">确认及凭证上传</span>
                </div>
                <div class="uv-section-body">
                   <a-form-item label="实打金额 (若存在扣费可调整)" required class="mb-4">
                     <a-input-number
                       v-model:value="formLocal.paymentAmount"
                       :min="0.01"
                       :precision="2"
                       style="width: 100%"
                       addon-before="$"
                       class="premium-input-number focus-money"
                     />
                   </a-form-item>

                   <a-form-item label="手续费 (必填)" required class="mb-4">
                     <a-input-number
                       v-model:value="formLocal.fee"
                       :min="0"
                       :precision="2"
                       :step="0.01"
                       style="width: 100%"
                       addon-before="$"
                       class="premium-input-number"
                       placeholder="请输入手续费"
                     />
                   </a-form-item>

                   <div class="total-amount-display mb-4">
                     <span class="total-label">总付费用</span>
                     <span class="total-value">${{ computedTotal }}</span>
                   </div>

                   <a-form-item label="实际打款时间" class="mb-4">
                     <a-date-picker
                       v-model:value="formLocal.actualPaidAt"
                       show-time
                       format="YYYY-MM-DD HH:mm:ss"
                       value-format="YYYY-MM-DD HH:mm:ss"
                       placeholder="请选择实际打款时间"
                       style="width: 100%"
                       class="premium-input"
                     />
                   </a-form-item>

                   <a-form-item label="打款凭证 (必填)" required class="mb-4">
                     <div class="voucher-paste-zone" @paste="handlePaste" tabindex="0">
                       <a-upload-dragger
                         v-model:fileList="fileListLocal"
                         name="file"
                         :maxCount="1"
                         :before-upload="handleBeforeUpload"
                         @remove="handleRemoveFile"
                         :show-upload-list="false"
                         class="premium-dragger"
                       >
                         <template v-if="!previewUrl">
                           <p class="ant-upload-drag-icon">
                             <InboxOutlined style="color: #3b82f6;" />
                           </p>
                           <p class="ant-upload-text">点击或将回执文件拖拽至此</p>
                           <p class="ant-upload-hint">支持 Ctrl+V 粘贴截图 · 必须提供打款水单截图作为凭据</p>
                         </template>
                         <template v-else>
                           <div class="preview-container" @click.stop>
                             <img :src="previewUrl" alt="预览" class="preview-image" />
                             <div class="preview-overlay">
                               <a-button type="primary" size="small" danger @click.stop="handleRemoveFile">移除再传</a-button>
                             </div>
                           </div>
                         </template>
                       </a-upload-dragger>
                     </div>
                   </a-form-item>

                   <a-form-item label="补充留言 (仅内部可见无通知)">
                     <a-textarea v-model:value="formLocal.remark" :rows="2" placeholder="如遇特殊情况可在此备注..." />
                   </a-form-item>
                </div>
              </div>
           </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 底部按钮 -->
    <div class="modal-fixed-footer">
      <a-button @click="visible = false" class="btn-cancel">取消配置</a-button>
      <a-button 
        type="primary" 
        @click="emitOk" 
        class="btn-submit" 
        :disabled="fileListLocal.length === 0 || formLocal.fee === undefined || formLocal.fee === null"
      >
        完成并确认打款
      </a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, reactive, watch, ref } from 'vue';
import { InboxOutlined, SafetyCertificateOutlined, HistoryOutlined, PlusCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  open: boolean;
  form: any;
  fileList: any[];
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'update:form', value: any): void;
  (e: 'update:file-list', value: any[]): void;
  (e: 'ok'): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const formLocal = reactive({ ...props.form, fee: 0, actualPaidAt: dayjs().format('YYYY-MM-DD HH:mm:ss') });
const fileListLocal = reactive<any[]>([...props.fileList]);
const previewUrl = ref<string>('');

const computedTotal = computed(() => {
  return ((formLocal.paymentAmount || 0) + (formLocal.fee || 0)).toFixed(2);
});

watch(() => props.form, (val) => {
  Object.assign(formLocal, val || {});
  // Initialize fee and actualPaidAt if not present
  if (formLocal.fee === undefined || formLocal.fee === null) formLocal.fee = 0;
  if (!formLocal.actualPaidAt) formLocal.actualPaidAt = dayjs().format('YYYY-MM-DD HH:mm:ss');
}, { deep: true });

watch(() => props.fileList, (val) => {
  fileListLocal.splice(0, fileListLocal.length, ...(val || []));
  if (!val || val.length === 0) {
    previewUrl.value = '';
  }
}, { deep: true });

watch(formLocal, (val) => {
  emit('update:form', { ...val });
}, { deep: true });

watch(fileListLocal, (val) => {
  emit('update:file-list', [...val]);
}, { deep: true });

const handleBeforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/');
  if (isImage) {
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
    fileListLocal.splice(0, fileListLocal.length, {
       uid: '-1',
       name: file.name,
       status: 'done',
       url: '',
       originFileObj: file
    });
  } else {
    previewUrl.value = '';
    message.error('请上传图片格式的文件');
  }
  return false;
};

const handlePaste = (e: ClipboardEvent) => {
  const items = e.clipboardData?.items;
  if (!items) return;

  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (!item) continue;
    if (item.type.indexOf('image') !== -1) {
      const file = item.getAsFile();
      if (file) {
        // Validate size (2MB max)
        const isLt2M = file.size / 1024 / 1024 < 2;
        if (!isLt2M) {
          message.error('粘贴的图片必须小于 2MB');
          break;
        }
        // Preview
        const reader = new FileReader();
        reader.onload = (ev) => {
          previewUrl.value = ev.target?.result as string;
        };
        reader.readAsDataURL(file);
        // Add to file list
        fileListLocal.splice(0, fileListLocal.length, {
          uid: '-paste-' + Date.now(),
          name: 'pasted_voucher.png',
          status: 'done',
          url: '',
          originFileObj: file
        });
        message.success('已粘贴截图');
        break;
      }
    }
  }
};

const handleRemoveFile = () => {
  fileListLocal.splice(0, fileListLocal.length);
  previewUrl.value = '';
};

const emitOk = () => {
  if (formLocal.fee === undefined || formLocal.fee === null) {
    message.warning('请输入手续费（可填0）');
    return;
  }
  // Inject computed total into form
  formLocal.totalAmount = parseFloat(computedTotal.value);
  emit('update:form', { ...formLocal });
  emit('ok');
};
</script>

<style scoped lang="scss">
.upload-voucher-modal :deep(.ant-modal-content) {
  padding: 0;
  border-radius: 12px;
  overflow: hidden;
}

.uv-section {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  margin-bottom: 12px;
  
  &.h-full {
     height: 100%;
     display: flex;
     flex-direction: column;
  }
}

.uv-section-header {
  padding: 12px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #e2e8f0;
}

.uv-section-title {
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
}

.uv-section-body {
  padding: 16px;
  flex: 1;
}

.readonly-field-group {
   margin-bottom: 16px;
   
   &.mb-0 {
     margin-bottom: 0px;
   }
   
   .ro-label {
      font-size: 12px;
      color: #64748b;
      margin-bottom: 4px;
   }
   
   .ro-value {
      font-size: 14px;
      color: #0f172a;
      word-break: break-all;
      
      &.account-highlight {
         font-family: 'JetBrains Mono', monospace;
         color: #1e3a8a;
         font-weight: 600;
      }
   }
}

/* Total amount display */
.total-amount-display {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .total-label {
    font-size: 13px;
    color: #166534;
    font-weight: 600;
  }

  .total-value {
    font-size: 20px;
    font-weight: 700;
    color: #16a34a;
    font-family: 'JetBrains Mono', monospace;
  }
}

/* Process History */
.history-body {
  padding: 12px 16px !important;
}

.history-item {
  display: flex;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f1f5f9;

  &:last-child { border-bottom: none; }
}

.history-icon {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;

  &.create { background: #eff6ff; color: #3b82f6; }
  &.audit { background: #ecfdf5; color: #10b981; }
  &.reject { background: #fef2f2; color: #ef4444; }
}

.history-content {
  flex: 1;
  min-width: 0;
}

.history-label {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 2px;
}

.history-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
}

.h-user {
  color: #3b82f6;
  font-weight: 600;
}

.h-time {
  color: #94a3b8;
}

.h-remark {
  margin-top: 4px;
  font-size: 12px;
  color: #64748b;
  background: #f8fafc;
  padding: 4px 8px;
  border-radius: 4px;
  border-left: 3px solid #e2e8f0;
}

.empty-history {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  padding: 16px 0;
}

/* Paste zone */
.voucher-paste-zone {
  outline: none;
  
  &:focus-within {
    .premium-dragger {
      border-color: #3b82f6;
      background: #eff6ff;
    }
  }
}

.premium-dragger {
   background: #f8fafc;
   border: 1px dashed #cbd5e1;
   border-radius: 8px;
   transition: all 0.3s ease;
   
   &:hover {
      border-color: #3b82f6;
      background: #eff6ff;
   }
}

.focus-money :deep(.ant-input-number-input) {
   font-size: 18px;
   font-weight: 600;
   color: #16a34a;
}

.mb-4 { margin-bottom: 20px; }

.preview-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  background: #f1f5f9;
  border-radius: 8px;
  
  .preview-image {
    max-width: 100%;
    max-height: 180px;
    border-radius: 6px;
    object-fit: contain;
  }
  
  .preview-overlay {
    position: absolute;
    top: 0; right: 0; bottom: 0; left: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0,0,0,0.4);
    border-radius: 8px;
    opacity: 0;
    transition: opacity 0.2s ease;
  }
  
  &:hover .preview-overlay {
    opacity: 1;
  }
}

.glass-header-compact {
  padding: 16px 24px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7ff 100%);
  border-bottom: 1px solid #e5e7eb;

  .header-main {
    display: flex;
    align-items: center;
    gap: 16px;

    .logo-box {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #f59e0b, #d97706);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);

      .logo-text {
        color: #fff;
        font-weight: 800;
        font-size: 14px;
        letter-spacing: 1px;
      }
    }

    .title-meta {
      .main-title {
        font-size: 18px;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 2px;
      }

      .simple-subtitle {
        font-size: 13px;
        color: #64748b;
      }
    }
  }
}

.modal-fixed-footer {
  padding: 12px 24px 16px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  border-radius: 8px;
  height: 38px;
  font-weight: 600;
  padding: 0 20px;
  border: 1px solid #cbd5e1;
  color: #64748b;
}

.btn-submit {
  border-radius: 8px;
  height: 38px;
  font-weight: 600;
  padding: 0 24px;
  background: linear-gradient(135deg, #1890ff, #1d4ed8);
  border: none;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  &:hover {
    background: linear-gradient(135deg, #40a9ff, #2563eb);
    transform: translateY(-1px);
  }
}

.premium-input {
  :deep(.ant-picker) {
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    background-color: #f8fafc;
  }
}
</style>
