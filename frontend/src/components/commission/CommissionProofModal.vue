<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="900px"
    centered
    class="premium-refined-modal"
  >
    <template #title>
      <div class="glass-header-compact">
        <div class="header-main">
          <div class="logo-box">
            <span class="logo-text">PROOF</span>
          </div>
          <div class="title-meta">
            <div class="main-title">查看打款凭证</div>
            <div class="simple-subtitle">查看详细的打款信息及支付凭证</div>
          </div>
        </div>
      </div>
    </template>

    <div class="modal-body-container" style="padding: 24px; overflow-y: auto;">
      <div v-if="proof">
        <!-- 支付目标明细 -->
        <a-card title="支付目标明细" style="margin-bottom: 24px;" class="info-card">
          <!-- PayPal 视图 -->
          <template v-if="proof.rawData?.paymentMethod === 'PayPal'">
            <a-descriptions :column="2" bordered size="small">
              <a-descriptions-item label="支付方式"><a-tag color="blue">PayPal</a-tag></a-descriptions-item>
              <a-descriptions-item label="PayPal 账号">{{ parsedDetails?.paypalAccount || proof.rawData?.paymentAccount || '-' }}</a-descriptions-item>
              <a-descriptions-item label="付款金额" :span="2">
                <span style="font-weight: 700; color: #16a34a; font-size: 16px;">${{ proof.amount || '-' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="打款单号">{{ proof.payId || '-' }}</a-descriptions-item>
              <a-descriptions-item label="申请时间">{{ proof.applyTime || '-' }}</a-descriptions-item>
            </a-descriptions>
          </template>

          <!-- Bank Transfer 视图 -->
          <template v-else-if="proof.rawData?.paymentMethod === 'Bank Transfer'">
            <a-descriptions :column="2" bordered size="small">
              <a-descriptions-item label="支付方式"><a-tag color="cyan">Bank Transfer</a-tag></a-descriptions-item>
              <a-descriptions-item label="收款人姓名">{{ parsedDetails?.accountName || '-' }}</a-descriptions-item>
              <a-descriptions-item label="银行名称">{{ parsedDetails?.bankName || '-' }}</a-descriptions-item>
              <a-descriptions-item label="银行账号" class="account-highlight">{{ parsedDetails?.accountNumber || proof.rawData?.paymentAccount || '-' }}</a-descriptions-item>
              <a-descriptions-item label="Swift Code">{{ parsedDetails?.swiftCode || '-' }}</a-descriptions-item>
              <a-descriptions-item label="国家">{{ parsedDetails?.bankCountry || '-' }}</a-descriptions-item>
              <a-descriptions-item label="付款金额" :span="2">
                <span style="font-weight: 700; color: #16a34a; font-size: 16px;">${{ proof.amount || '-' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="打款单号">{{ proof.payId || '-' }}</a-descriptions-item>
              <a-descriptions-item label="打款时间">{{ proof.payTime || '-' }}</a-descriptions-item>
            </a-descriptions>
          </template>

          <template v-else>
             <a-descriptions :column="2" bordered size="small">
              <a-descriptions-item label="支付方式">{{ proof.rawData?.paymentMethod || '未知' }}</a-descriptions-item>
              <a-descriptions-item label="支付账号">{{ proof.rawData?.paymentAccount || '-' }}</a-descriptions-item>
              <a-descriptions-item label="付款金额" :span="2">
                <span style="font-weight: 700; color: #16a34a; font-size: 16px;">${{ proof.amount || '-' }}</span>
              </a-descriptions-item>
            </a-descriptions>
          </template>
        </a-card>
        
        <!-- 备注信息 -->
        <a-card title="备注与留言" v-if="proof.remark || proof.rawData?.reviewRemark" style="margin-bottom: 24px;">
           <div v-if="proof.rawData?.reviewRemark" style="margin-bottom: 8px;">
              <span style="color: #64748b; font-size: 12px;">审核备注:</span>
              <p style="margin: 4px 0 0 0; color: #1e293b;">{{ proof.rawData.reviewRemark }}</p>
           </div>
           <div v-if="proof.remark">
              <span style="color: #64748b; font-size: 12px;">打款留言:</span>
              <p style="margin: 4px 0 0 0; color: #1e293b;">{{ proof.remark }}</p>
           </div>
        </a-card>

        <!-- 打款凭证 -->
        <a-card title="打款凭证 (水单)" class="proof-card">
          <div style="margin-bottom: 16px;">
            <a :href="fullProofUrl" target="_blank" class="download-link">
              <file-outlined style="color: #3b82f6; font-size: 20px;" />
              <span>下载/查看原始文件 ({{ parsedDetails?.proofFileName || proof.rawData?.proofFileName || '凭证文件' }})</span>
            </a>
          </div>
          <div class="proof-preview">
            <template v-if="isImageFile(parsedDetails?.proofFileName || proof.rawData?.proofFileName || proof.proof)">
              <img :src="fullProofUrl" alt="打款凭证" class="voucher-img" />
            </template>
            <div v-else class="no-preview">
              <file-text-outlined class="no-preview-icon" />
              <div class="no-preview-text">该文件格式不支持在线预览，请点击上方链接查看</div>
            </div>
          </div>
        </a-card>
      </div>
    </div>

    <div class="modal-fixed-footer">
      <a-button @click="visible = false" class="btn-cancel">关闭</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { FileOutlined, FileTextOutlined } from '@ant-design/icons-vue';

const props = defineProps<{
  open: boolean;
  proof: any;
}>();

const emit = defineEmits(['update:open']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const parsedDetails = computed(() => {
  if (!props.proof?.rawData?.paymentDetails) return null;
  try {
    return JSON.parse(props.proof.rawData.paymentDetails);
  } catch (e) {
    console.error('Failed to parse payment details:', e);
    return null;
  }
});

const fullProofUrl = computed(() => {
  const url = props.proof?.proof;
  if (!url) return '';
  if (url.startsWith('http') || url.startsWith('blob:')) return url;
  // Prepend /influencer-api if it's a relative path starting with /v1/files
  if (url.startsWith('/v1/files')) {
    return '/influencer-api' + url;
  }
  return url;
});

const isImageFile = (fileName: string) => {
  if (!fileName) return false;
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp'];
  const lastDotIndex = fileName.lastIndexOf('.');
  if (lastDotIndex === -1) return true; // No extension, assume image (or at least try to render)
  const ext = fileName.toLowerCase().substring(lastDotIndex);
  return imageExtensions.includes(ext);
};
</script>

<style scoped lang="scss">
.premium-refined-modal {
  :deep(.ant-modal-content) {
    padding: 0;
    overflow: hidden;
    border-radius: 12px;
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
      background: #1890ff;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);

      .logo-text {
        color: #fff;
        font-weight: 800;
        font-size: 10px;
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
  text-align: right;
}

.btn-cancel {
  border-radius: 8px;
  height: 36px;
  font-weight: 500;
}

.info-card, .proof-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  border: 1px solid #f1f5f9;
  
  :deep(.ant-card-head) {
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;
    min-height: 44px;
    padding: 0 16px;
    
    .ant-card-head-title {
      font-size: 14px;
      font-weight: 600;
      color: #475569;
      padding: 12px 0;
    }
  }
}

.account-highlight {
  font-family: 'JetBrains Mono', monospace;
  color: #1e40af !important;
  font-weight: 600;
}

.download-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #3b82f6;
  padding: 8px 12px;
  background: #eff6ff;
  border-radius: 6px;
  width: fit-content;
  transition: all 0.2s;
  
  &:hover {
    background: #dbeafe;
    color: #2563eb;
  }
}

.proof-preview {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #fdfdfd;
  border: 1px dashed #e2e8f0;
  border-radius: 8px;
  
  .voucher-img {
    max-width: 100%;
    max-height: 500px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
    border-radius: 4px;
    cursor: zoom-in;
  }
}

.no-preview {
  padding: 48px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  
  .no-preview-icon {
    font-size: 48px;
    color: #cbd5e1;
  }
  
  .no-preview-text {
    color: #94a3b8;
    font-size: 13px;
  }
}
</style>
