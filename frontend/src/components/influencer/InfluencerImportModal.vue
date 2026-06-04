<template>
  <a-modal
    :open="open"
    @cancel="handleCancel"
    :footer="null"
    :width="700"
    centered
    :maskClosable="false"
    class="premium-import-modal"
  >
    <template #title>
      <div class="modal-header">
        <div class="header-icon">
          <cloud-upload-outlined />
        </div>
        <div class="header-text">
          <span class="title">导入红人</span>
          <span class="subtitle">批量导入或更新红人数据</span>
        </div>
      </div>
    </template>

    <div class="import-content">
      <!-- 步骤条 -->
      <div class="steps-wrapper">
        <a-steps :current="currentStep" size="small">
          <a-step title="上传文件" />
          <a-step title="数据解析" />
          <a-step title="确认导入" />
        </a-steps>
      </div>

      <!-- 第一步：上传 -->
      <div v-if="currentStep === 0" class="step-upload">
        <div class="step-guide">
          <div class="guide-title">
            <info-circle-outlined /> 操作指引
          </div>
          <div class="guide-body">
            <p>1. 准备 Excel 文件，系统将根据 Email 自动匹配唯一红人。</p>
            <p>2. 支持批量设置：Pic (联络员)、KOL-Tier (层级)、分类、风格、地址等。</p>
            <div class="template-download">
              <a @click="downloadTemplate"><download-outlined /> 下载导入模板</a>
            </div>
          </div>
        </div>

        <a-upload-dragger
          name="file"
          :multiple="false"
          :file-list="fileList"
          :before-upload="beforeUpload"
          @remove="handleRemove"
          accept=".xlsx, .xls, .csv"
          class="custom-dragger"
        >
          <div class="dragger-inner">
            <div class="icon-box">
              <inbox-outlined />
            </div>
            <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
            <p class="ant-upload-hint">支持 .xlsx, .xls, .csv 格式 (最大 20MB)</p>
          </div>
        </a-upload-dragger>
        
        <div class="actions">
          <a-button @click="handleCancel" style="margin-right: 12px">取消</a-button>
          <a-button 
            type="primary" 
            :disabled="!fileList.length" 
            @click="startAnalysis"
            class="premium-btn"
          >
            开始解析 <right-outlined />
          </a-button>
        </div>
      </div>

      <!-- 第二步：解析中 -->
      <div v-if="currentStep === 1" class="step-progress">
        <div class="progress-info">
          <loading-outlined v-if="processingStatus === 'PROCESSING'" class="loading-spin" />
          <check-circle-outlined v-else-if="processingStatus === 'COMPLETED'" class="success-icon" />
          <span class="status-text">{{ statusText }}</span>
        </div>
        <a-progress 
          :percent="progressPercent" 
          :status="processingStatus === 'FAILED' ? 'exception' : (processingStatus === 'COMPLETED' ? 'success' : 'active')"
          stroke-color="linear-gradient(90deg, #1890ff 0%, #36cfc9 100%)"
        />
        <div v-if="jobResult?.errorMessage" class="error-detail">
          {{ jobResult.errorMessage }}
        </div>
      </div>

      <!-- 第三步：确认预览 -->
      <div v-if="currentStep === 2" class="step-preview">
        <div class="preview-stats">
          <div class="stat-card added">
            <div class="stat-val">{{ previewData?.addedCount || 0 }}</div>
            <div class="stat-label">新增红人</div>
          </div>
          <div class="stat-card updated">
            <div class="stat-val">{{ previewData?.updatedCount || 0 }}</div>
            <div class="stat-label">更新红人</div>
          </div>
          <div class="stat-card skipped">
            <div class="stat-val text-warning">{{ previewData?.warnings?.length || 0 }}</div>
            <div class="stat-label">跳过 (重复)</div>
          </div>
          <div class="stat-card failed">
            <div class="stat-val text-error">{{ previewData?.failCount || 0 }}</div>
            <div class="stat-label">读取失败</div>
          </div>
        </div>

        <div v-if="previewData?.errorMessages?.length" class="error-log">
          <div class="log-header">
            <span class="log-title">异常摘要 (共 {{ previewData.errorMessages.length }} 条)</span>
            <span class="log-subtitle">数据缺失或格式错误导致的失败</span>
          </div>
          <div class="log-items-scrollable">
            <div v-for="(err, idx) in previewData.errorMessages" :key="'err-'+idx" class="log-item">
              <span class="log-item-tag">ERROR</span>
              <span class="log-item-content">{{ err }}</span>
            </div>
          </div>
        </div>

        <div v-if="previewData?.warnings?.length" class="warning-log">
          <div class="log-header warning-header">
            <span class="log-title">跳过记录 (共 {{ previewData.warnings.length }} 条)</span>
            <span class="log-subtitle">以下红人因平台+Handle重复已跳过</span>
          </div>
          <div class="log-items-scrollable">
            <div v-for="(warn, idx) in previewData.warnings" :key="'warn-'+idx" class="log-item">
              <span class="log-item-tag warning-tag">SKIP</span>
              <span class="log-item-content">{{ warn }}</span>
            </div>
          </div>
        </div>

        <div class="confirm-msg" v-if="!importing">
          确认解析无误后，点击 “确认导入” 将数据持久化到系统。
        </div>

        <div v-if="importing" class="importing-box">
          <a-progress :percent="importProgress" status="active" />
          <p>正在提交导入任务...</p>
        </div>

        <div class="actions">
          <a-button @click="currentStep = 0" :disabled="importing">重新上传</a-button>
          <a-button 
            type="primary" 
            danger 
            v-if="previewData?.failCount > 0 && !importing" 
            @click="confirmImportAction"
            style="margin-left: 12px"
          >
            忽略错误并导入
          </a-button>
          <a-button 
            type="primary" 
            v-else-if="!importing"
            @click="confirmImportAction"
            class="premium-btn"
            style="margin-left: 12px"
          >
            确认导入
          </a-button>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, computed, onBeforeUnmount } from 'vue';
import { 
  InboxOutlined, 
  CloudUploadOutlined, 
  InfoCircleOutlined,
  DownloadOutlined,
  RightOutlined,
  LoadingOutlined,
  CheckCircleOutlined,
  CloseCircleFilled
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { influencerService } from '@/services/influencerService';

const props = defineProps<{
  open: boolean;
  stage?: string;  // 'POOL' | 'ONBOARDED'
}>();

const emit = defineEmits(['update:open', 'success']);

const currentStep = ref(0);
const fileList = ref<any[]>([]);
const processingStatus = ref('PENDING'); // PENDING, PROCESSING, COMPLETED, FAILED
const jobResult = ref<any>(null);
const previewData = ref<any>(null);
const progressPercent = ref(0);
const importing = ref(false);
const importProgress = ref(0);
const currentJobId = ref<number | null>(null);
const currentFileKey = ref<string | null>(null);

const statusText = computed(() => {
  if (processingStatus.value === 'PROCESSING') return '正在解析 Excel 数据...';
  if (processingStatus.value === 'COMPLETED') return '解析完成';
  if (processingStatus.value === 'FAILED') return '解析失败';
  return '准备就绪';
});

watch(() => props.open, (val) => {
  if (val) {
    resetState();
  }
});

const resetState = () => {
  currentStep.value = 0;
  fileList.value = [];
  processingStatus.value = 'PENDING';
  jobResult.value = null;
  previewData.value = null;
  progressPercent.value = 0;
  importing.value = false;
  importProgress.value = 0;
  currentJobId.value = null;
  currentFileKey.value = null;
  clearAllTimers();
};

const activeTimers: number[] = [];
const clearAllTimers = () => {
  activeTimers.forEach(t => clearInterval(t));
  activeTimers.length = 0;
};

onBeforeUnmount(() => {
  clearAllTimers();
});

const handleCancel = () => {
  if (processingStatus.value === 'PROCESSING' || importing.value) {
    return message.warn('正在处理中，请稍候');
  }
  emit('update:open', false);
};

const beforeUpload = (file: any) => {
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  fileList.value = [];
};

const downloadTemplate = async () => {
  try {
    const response = await fetch('/influencer-api/v1/influencer/import/template');
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'influencer_import_template.xlsx');
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (e) {
    message.error('下载模板失败');
  }
};

const startAnalysis = async () => {
  if (!fileList.value.length) return;
  
  currentStep.value = 1;
  processingStatus.value = 'PROCESSING';
  progressPercent.value = 10;
  
  const formData = new FormData();
  formData.append('file', fileList.value[0]);
  if (props.stage) {
    formData.append('stage', props.stage);
  }
  
  try {
    const res = await influencerService.analyzeImport(formData);
    currentJobId.value = res.jobId;
    currentFileKey.value = res.fileKey;
    pollStatus(res.jobId, (data) => {
      processingStatus.value = data.status;
      if (data.status === 'COMPLETED') {
        progressPercent.value = 100;
        previewData.value = data.resultJson ? JSON.parse(data.resultJson) : {};
        setTimeout(() => {
          currentStep.value = 2;
        }, 800);
      } else if (data.status === 'FAILED') {
        jobResult.value = data;
      } else {
        // Estimate progress based on counts if available
        if (data.totalCount > 0) {
          progressPercent.value = Math.min(99, Math.floor((data.processedCount / data.totalCount) * 100));
        }
      }
    });
  } catch (e: any) {
    processingStatus.value = 'FAILED';
    jobResult.value = { errorMessage: e.message || '分析请求失败' };
  }
};

const confirmImportAction = async () => {
  if (!currentFileKey.value) return;
  
  importing.value = true;
  importProgress.value = 10;
  
  try {
    const res = await influencerService.confirmImport(currentFileKey.value, fileList.value[0].name, props.stage);
    pollStatus(res.jobId, (data) => {
      if (data.status === 'COMPLETED') {
        importProgress.value = 100;
        message.success('导入成功');
        setTimeout(() => {
          emit('success');
          emit('update:open', false);
        }, 1000);
      } else if (data.status === 'FAILED') {
        importing.value = false;
        message.error('导入失败: ' + data.errorMessage);
      } else {
        if (data.totalCount > 0) {
          importProgress.value = Math.min(100, Math.floor((data.processedCount / data.totalCount) * 100));
        }
      }
    });
  } catch (e: any) {
    importing.value = false;
    message.error('提交失败: ' + e.message);
  }
};

const pollStatus = (jobId: number, callback: (data: any) => void) => {
  let errorCount = 0;
  const MAX_ERRORS = 5;
  const timer = setInterval(async () => {
    try {
      const data = await influencerService.getImportStatus(jobId);
      errorCount = 0; // 成功后重置错误计数
      callback(data);
      if (data.status === 'COMPLETED' || data.status === 'FAILED') {
        clearInterval(timer);
        const idx = activeTimers.indexOf(timer);
        if (idx > -1) activeTimers.splice(idx, 1);
      }
    } catch (e) {
      errorCount++;
      console.warn(`轮询失败 (${errorCount}/${MAX_ERRORS})`, e);
      if (errorCount >= MAX_ERRORS) {
        clearInterval(timer);
        const idx = activeTimers.indexOf(timer);
        if (idx > -1) activeTimers.splice(idx, 1);
        processingStatus.value = 'FAILED';
        jobResult.value = { errorMessage: '轮询超时，请刷新页面后重试' };
      }
    }
  }, 1500) as unknown as number;
  activeTimers.push(timer);
};
</script>

<style scoped>
.premium-import-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.premium-import-modal :deep(.ant-modal-header) {
  padding: 20px 24px;
  margin-bottom: 0;
  border-bottom: 1px solid #f1f5f9;
}

.modal-header {
  display: flex;
  align-items: center;
}

.header-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  margin-right: 12px;
  box-shadow: 0 4px 10px rgba(24, 144, 255, 0.3);
}

.header-text {
  display: flex;
  flex-direction: column;
}

.header-text .title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.2;
}

.header-text .subtitle {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.import-content {
  padding: 24px;
}

.steps-wrapper {
  margin-bottom: 32px;
  padding: 0 40px;
}

/* Step Upload */
.step-guide {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.guide-title {
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
}

.guide-body {
  font-size: 12px;
  color: #64748b;
  line-height: 1.6;
}

.guide-body p {
  margin-bottom: 4px;
}

.template-download {
  margin-top: 8px;
}

.template-download a {
  color: #1890ff;
  font-weight: 500;
}

.custom-dragger {
  background: #ffffff !important;
  border: 2px dashed #e2e8f0 !important;
  border-radius: 16px !important;
  transition: all 0.3s ease !important;
}

.custom-dragger:hover {
  border-color: #1890ff !important;
  background: #f0f7ff !important;
}

.dragger-inner {
  padding: 32px 0;
}

.icon-box {
  font-size: 48px;
  color: #1890ff;
  margin-bottom: 16px;
  opacity: 0.8;
}

.ant-upload-text {
  font-size: 14px !important;
  font-weight: 500 !important;
  color: #1e293b !important;
}

.ant-upload-hint {
  font-size: 12px !important;
  color: #94a3b8 !important;
}

.actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
}

.premium-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.25);
}

/* Step Progress */
.step-progress {
  padding: 40px 20px;
  text-align: center;
}

.progress-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 16px;
}

.loading-spin {
  font-size: 24px;
  color: #1890ff;
}

.success-icon {
  font-size: 24px;
  color: #52c41a;
}

.status-text {
  font-size: 15px;
  font-weight: 500;
  color: #334155;
}

.error-detail {
  margin-top: 16px;
  color: #ef4444;
  font-size: 13px;
  background: #fef2f2;
  padding: 12px;
  border-radius: 8px;
  text-align: left;
}

/* Step Preview */
.preview-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stat-val {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stat-card.added { border-bottom: 3px solid #36cfc9; }
.stat-card.updated { border-bottom: 3px solid #1890ff; }
.stat-card.skipped { border-bottom: 3px solid #faad14; }
.stat-card.failed { border-bottom: 3px solid #ff4d4f; }

.text-error { color: #ff4d4f; }
.text-warning { color: #faad14; }

.preview-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.error-log {
  background: #fff8f8;
  border: 1px solid #ffccc7;
  border-radius: 12px;
  padding: 0;
  margin-bottom: 24px;
  overflow: hidden;
}

.log-header {
  padding: 12px 16px;
  background: #fff1f0;
  border-bottom: 1px solid #ffccc7;
  display: flex;
  flex-direction: column;
}

.log-title {
  font-size: 13px;
  font-weight: 600;
  color: #cf1322;
}

.log-subtitle {
  font-size: 11px;
  color: #f5222d;
  opacity: 0.8;
}

.log-items-scrollable {
  max-height: 200px;
  overflow-y: auto;
  padding: 8px 0;
}

.log-item {
  padding: 6px 16px;
  font-size: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  border-bottom: 1px solid #fff1f0;
  transition: background 0.2s;
}

.log-item:last-child {
  border-bottom: none;
}

.log-item:hover {
  background: #fff1f0;
}

.log-item-tag {
  background: #ff4d4f;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 3px;
  font-weight: bold;
  margin-top: 2px;
}

.log-item-tag.warning-tag {
  background: #faad14;
}

.warning-log {
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 12px;
  padding: 0;
  margin-bottom: 24px;
  overflow: hidden;
}

.warning-header {
  background: #fff7e6 !important;
  border-bottom: 1px solid #ffe58f !important;
}

.warning-header .log-title {
  color: #d48806 !important;
}

.warning-header .log-subtitle {
  color: #faad14 !important;
}

.warning-log .log-item:hover {
  background: #fff7e6;
}

.warning-log .log-item {
  border-bottom: 1px solid #fff7e6;
}

.log-item-content {
  color: #333;
  line-height: 1.5;
  word-break: break-all;
}

.confirm-msg {
  text-align: center;
  color: #64748b;
  font-size: 13px;
  margin-bottom: 16px;
}

.importing-box {
  text-align: center;
  margin-bottom: 24px;
}

.importing-box p {
  margin-top: 12px;
  color: #64748b;
  font-size: 12px;
}
</style>
