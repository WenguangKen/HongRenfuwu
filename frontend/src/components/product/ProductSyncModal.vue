<template>
  <a-modal
    :open="open"
    :title="null"
    :footer="null"
    :closable="false"
    class="premium-modal product-sync-modal"
    width="500px"
    centered
    @update:open="updateOpen"
  >
    <div class="modal-header glass-header">
      <div class="header-left">
        <div class="header-icon-wrapper primary-gradient">
          <SyncOutlined :spin="loading" />
        </div>
        <div class="header-text">
          <div class="header-title">{{ syncType === 'fba' ? '同步 FBA 库存' : '同步易仓(亚马逊)商品' }}</div>
          <div class="header-subtitle">{{ syncType === 'fba' ? '将最新的亚马逊 FBA 库存数据同步并更新到本地变体' : '将易仓中的亚马逊店铺商品数据同步到本地数据库' }}</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="closeModal">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="modal-body">
      <!-- 报告页面 -->
      <div class="report-section" v-if="showReport && reportData">
        <div class="report-header">
          <div class="report-status" :class="reportData.status === 'COMPLETED' ? 'status-success' : 'status-failed'">
            <CheckCircleOutlined v-if="reportData.status === 'COMPLETED'" class="status-icon" />
            <CloseCircleOutlined v-else class="status-icon" />
            <span class="status-label">{{ reportData.status === 'COMPLETED' ? '同步任务完成' : '同步任务失败' }}</span>
          </div>
          <div class="report-msg">{{ reportData.message }}</div>
        </div>

        <div class="report-grid">
          <div class="report-card card-pulled">
            <div class="card-num">{{ reportData.total || 0 }}</div>
            <div class="card-label">拉取批次/页数</div>
          </div>
          <div class="report-card card-failed">
            <div class="card-num error-text">{{ reportData.error || 0 }}</div>
            <div class="card-label">失败变体数量</div>
          </div>
        </div>

        <div class="report-section-title">数据同步统计 (SPU / SKU)</div>
        <div class="stats-table">
          <div class="table-row table-header">
            <div class="col-type">数据类型</div>
            <div class="col-num success-color">新增 (Created)</div>
            <div class="col-num update-color">更新 (Updated)</div>
          </div>
          <div class="table-row">
            <div class="col-type">SPU (商品)</div>
            <div class="col-num success-color">{{ reportData.spuAdded || 0 }}</div>
            <div class="col-num update-color">{{ reportData.spuUpdated || 0 }}</div>
          </div>
          <div class="table-row">
            <div class="col-type">SKU (变体)</div>
            <div class="col-num success-color">{{ reportData.added || 0 }}</div>
            <div class="col-num update-color">{{ reportData.updated || 0 }}</div>
          </div>
        </div>

        <!-- 错误明细 -->
        <div class="error-details-section" v-if="reportData.errorDetails || reportData.status === 'FAILED'">
          <div class="section-title">错误明细 / 原因</div>
          <div class="details-content">
            {{ reportData.errorDetails || reportData.message || '部分唯一键冲突或请求超时，已被系统跳过并隔离。' }}
          </div>
        </div>
      </div>

      <div class="form-section" v-if="!loading && !showReport">
        <div class="section-label">选择店铺</div>
        <a-select
          v-model:value="selectedStoreId"
          placeholder="请选择要同步的亚马逊店铺"
          class="premium-select full-width"
          :loading="fetchingStores"
          :options="storeOptions"
        >
          <template #option="{ label, value, domain }">
            <div class="store-option">
              <span class="store-name">{{ label }}</span>
              <span class="store-domain">{{ domain }}</span>
            </div>
          </template>
        </a-select>
        <div class="helper-text">
          <InfoCircleOutlined /> {{ syncType === 'fba' ? '将自动拉取该店铺的最新 FBA 库存数据' : '将自动拉取该店铺的商品和最新的 FBA 库存' }}
        </div>

        <!-- 商品同步模式选择 -->
        <div v-if="syncType === 'product'" style="margin-top: 20px;">
          <div class="section-label">同步范围</div>
          <a-radio-group v-model:value="syncMode" class="premium-radio-group" style="width: 100%;">
            <a-radio value="incremental" style="display: block; margin-bottom: 12px;">
              <span class="radio-title">增量更新（推荐，避免卡顿）</span>
              <div class="radio-desc">仅拉取过去 24 小时内发生变动的商品。由于数量少，耗时极短。</div>
            </a-radio>
            <a-radio value="full" style="display: block;">
              <span class="radio-title">全量拉取</span>
              <div class="radio-desc">同步该店铺在易仓中的全部商品。数据量过大时，由于易仓服务器的分页查询限制，可能会有明显卡顿。</div>
            </a-radio>
          </a-radio-group>
        </div>

        <div class="sync-option-checkbox" v-if="syncType === 'fba'" style="margin-top: 16px;">
          <a-checkbox v-model:checked="onlyActive">
            <span style="font-size: 14px; font-weight: 500; color: #334155;">只同步在售商品的库存 (Active)</span>
          </a-checkbox>
          <div style="font-size: 12px; color: #94a3b8; margin-top: 4px; margin-left: 24px;">
            勾选后，仅为在售 (Active) 状态 of 商品匹配并更新最新的 FBA 库存，忽略草稿或已下架的商品以大幅提升同步速度。
          </div>
        </div>
      </div>

      <div class="sync-info" v-if="loading && !showReport">
        <a-progress :percent="progress" :status="progressStatus" />
        <div class="status-text">{{ statusText }}</div>
      </div>
    </div>

    <div class="modal-footer">
      <a-space size="middle">
        <template v-if="showReport">
          <a-button type="primary" @click="closeModal" class="premium-btn primary-gradient">我知道了</a-button>
        </template>
        <template v-else-if="loading">
          <a-button type="primary" @click="closeModal" class="premium-btn primary-gradient">后台运行</a-button>
        </template>
        <template v-else>
          <a-button @click="closeModal" class="premium-btn">取消</a-button>
          <a-button 
            type="primary" 
            @click="startSync" 
            class="premium-btn primary-gradient"
            :disabled="!selectedStoreId"
          >
            开始同步
          </a-button>
        </template>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { SyncOutlined, CloseOutlined, InfoCircleOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { getEccangStores, syncEccangProducts, syncEccangFbaInventory, getEccangProductSyncProgress, type EccangStoreConfig, type EccangSyncProgress } from '@/services/eccangService';

const props = withDefaults(defineProps<{
  open: boolean;
  syncType?: 'product' | 'fba';
}>(), {
  syncType: 'product'
});

const emit = defineEmits(['update:open', 'success']);

const loading = ref(false);
const showReport = ref(false);
const reportData = ref<EccangSyncProgress | null>(null);
const fetchingStores = ref(false);
const selectedStoreId = ref<number | undefined>(undefined);
const stores = ref<EccangStoreConfig[]>([]);
const progress = ref(0);
const progressStatus = ref<'active' | 'success' | 'exception'>('active');
const statusText = ref('准备就绪');
const onlyActive = ref(true);
const syncMode = ref<'incremental' | 'full'>('incremental');
let pollTimer: any = null;

const storeOptions = computed(() => {
  return stores.value
    .map(s => ({
      label: s.storeName,
      value: s.id,
      domain: s.storeName || s.platform || s.eccangStoreCode
    }));
});

const fetchStores = async () => {
  fetchingStores.value = true;
  try {
    const res = await getEccangStores();
    stores.value = res || [];
  } catch (error) {
    message.error('获取店铺列表失败');
  } finally {
    fetchingStores.value = false;
  }
};

const updateOpen = (val: boolean) => {
  emit('update:open', val);
};

const closeModal = () => {
  if (pollTimer) clearInterval(pollTimer);
  updateOpen(false);
  // Reset state after delay
  setTimeout(() => {
    loading.value = false;
    showReport.value = false;
    reportData.value = null;
    progress.value = 0;
    progressStatus.value = 'active';
    statusText.value = '准备就绪';
    selectedStoreId.value = undefined;
  }, 300);
};

const pollSyncProgress = (storeId: number) => {
  if (pollTimer) clearInterval(pollTimer);

  const updateProgress = async () => {
    try {
      const prog = await getEccangProductSyncProgress(storeId);
      statusText.value = prog.message || '同步中...';

      if (prog.status === 'IDLE') {
        progress.value = Math.min(progress.value + 2, 15);
        statusText.value = '正在启动同步任务...';
        return;
      }

      if (typeof prog.progress === 'number' && prog.progress > 0) {
        progress.value = Math.min(99, prog.progress);
      } else if (prog.total && prog.total > 0) {
        progress.value = Math.min(99, Math.round(((prog.processed || 0) / prog.total) * 100));
      } else {
        progress.value = Math.min(progress.value + 3, 90);
      }

      if (prog.status === 'COMPLETED') {
        clearInterval(pollTimer);
        pollTimer = null;
        progress.value = 100;
        progressStatus.value = 'success';
        statusText.value = prog.message || '同步完成';
        loading.value = false;
        reportData.value = prog;
        showReport.value = true;
        message.success(props.syncType === 'fba' ? 'FBA 库存同步成功' : '易仓商品同步成功');
        emit('success');
      } else if (prog.status === 'FAILED') {
        clearInterval(pollTimer);
        pollTimer = null;
        progressStatus.value = 'exception';
        statusText.value = prog.message || '同步失败';
        loading.value = false;
        reportData.value = prog;
        showReport.value = true;
        message.error(prog.message || '同步失败');
      }
    } catch (e) {
      console.error(e);
    }
  };

  void updateProgress();
  pollTimer = setInterval(updateProgress, 1500);
};

const startSync = async () => {
  if (!selectedStoreId.value) return;

  loading.value = true;
  progress.value = 5;
  statusText.value = '正在请求同步任务...';
  progressStatus.value = 'active';

  try {
    const storeId = selectedStoreId.value;
    const startRes = props.syncType === 'fba' 
      ? await syncEccangFbaInventory(storeId, onlyActive.value) 
      : await syncEccangProducts(storeId, syncMode.value);
    const alreadyRunning = (startRes.message || '').includes('正在进行中');

    if (!startRes.success && !alreadyRunning) {
      progressStatus.value = 'exception';
      statusText.value = startRes.message || '无法启动同步';
      loading.value = false;
      message.warning(startRes.message || '无法启动同步');
      return;
    }

    if (alreadyRunning) {
      message.info('检测到同步任务正在进行，已接入进度');
    }

    statusText.value = '正在拉取易仓数据...';
    pollSyncProgress(storeId);
  } catch (error) {
    progressStatus.value = 'exception';
    statusText.value = '请求失败，请确认易仓服务已启动';
    loading.value = false;
    console.error(error);
  }
};

watch(() => props.open, (val) => {
  if (val) {
    fetchStores();
  }
});

watch(selectedStoreId, async (newStoreId) => {
  if (newStoreId) {
    try {
      const prog = await getEccangProductSyncProgress(newStoreId);
      if (prog && prog.status === 'RUNNING') {
        loading.value = true;
        progress.value = prog.progress || 0;
        statusText.value = prog.message || '同步中...';
        progressStatus.value = 'active';
        pollSyncProgress(newStoreId);
      }
    } catch (e) {
      console.error('检查同步状态失败:', e);
    }
  }
});

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer);
});
</script>

<style lang="scss" scoped>
.premium-modal {
  :deep(.ant-modal-content) {
    border-radius: 20px;
    padding: 0;
    overflow: hidden;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  }
}

.modal-header {
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);

  .header-left {
    display: flex;
    gap: 16px;

    .header-icon-wrapper {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: white;
      box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.3);

      &.primary-gradient {
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      }
    }

    .header-text {
      .header-title {
        font-size: 18px;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 4px;
      }
      .header-subtitle {
        font-size: 14px;
        color: #64748b;
      }
    }
  }

  .close-btn {
    color: #94a3b8;
    &:hover { color: #64748b; background: rgba(0,0,0,0.05); }
  }
}

.modal-body {
  padding: 32px 24px;
  background: #f8fafc;

  .form-section {
    margin-bottom: 24px;

    .section-label {
      font-size: 14px;
      font-weight: 600;
      color: #334155;
      margin-bottom: 8px;
    }

    .full-width {
      width: 100%;
    }

    .helper-text {
      margin-top: 8px;
      font-size: 12px;
      color: #94a3b8;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .sync-info {
    padding: 16px;
    background: white;
    border-radius: 12px;
    border: 1px solid #e2e8f0;

    .status-text {
      text-align: center;
      margin-top: 8px;
      color: #64748b;
      font-size: 13px;
    }
  }
}

.modal-footer {
  padding: 16px 24px;
  background: white;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
}

.premium-select :deep(.ant-select-selector) {
  height: 44px !important;
  border-radius: 12px !important;
  border-color: #e2e8f0 !important;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);

  &:hover {
    border-color: #3b82f6 !important;
  }
}

.store-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .store-name {
    font-weight: 500;
    color: #334155;
  }
  .store-domain {
    font-size: 12px;
    color: #94a3b8;
  }
}

.premium-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 10px;
  font-weight: 600;
  
  &.primary-gradient {
    border: none;
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
    
    &:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
    }
    
    &:disabled {
      background: #cbd5e1;
      color: #fff;
      box-shadow: none;
    }
  }
}

.premium-radio-group {
  :deep(.ant-radio-wrapper) {
    align-items: flex-start;
    padding: 12px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    background: white;
    transition: all 0.2s ease;
    margin-right: 0;

    &:hover {
      border-color: #3b82f6;
      background: rgba(59, 130, 246, 0.02);
    }

    &.ant-radio-wrapper-checked {
      border-color: #3b82f6;
      background: rgba(59, 130, 246, 0.04);
      box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.05);
    }

    .ant-radio {
      margin-top: 3px;
    }

    .radio-title {
      font-size: 14px;
      font-weight: 600;
      color: #334155;
    }

    .radio-desc {
      font-size: 12px;
      color: #94a3b8;
      margin-top: 4px;
      white-space: normal;
      line-height: 1.5;
    }
  }
}

.report-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.report-header {
  text-align: center;
  background: white;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;

  .report-status {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 700;
    margin-bottom: 6px;

    &.status-success {
      color: #10b981;
    }
    &.status-failed {
      color: #ef4444;
    }

    .status-icon {
      font-size: 20px;
    }
  }

  .report-msg {
    font-size: 13px;
    color: #64748b;
    line-height: 1.5;
  }
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.report-card {
  background: white;
  padding: 14px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  text-align: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02);
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  }

  .card-num {
    font-size: 22px;
    font-weight: 800;
    color: #1e293b;
    line-height: 1.2;
    margin-bottom: 4px;

    &.error-text {
      color: #ef4444;
    }
  }

  .card-label {
    font-size: 12px;
    color: #64748b;
    font-weight: 500;
  }

  &.card-pulled {
    border-left: 4px solid #3b82f6;
  }
  &.card-failed {
    border-left: 4px solid #ef4444;
  }
}

.report-section-title {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  margin-top: 8px;
  margin-bottom: 4px;
}

.stats-table {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  .table-row {
    display: flex;
    padding: 12px 16px;
    border-bottom: 1px solid #f1f5f9;
    align-items: center;

    &:last-child {
      border-bottom: none;
    }

    &.table-header {
      background: #f8fafc;
      font-weight: 600;
      color: #475569;
      font-size: 12px;
    }

    .col-type {
      flex: 2;
      font-size: 13px;
      color: #334155;
      font-weight: 600;
    }

    .col-num {
      flex: 1.5;
      text-align: center;
      font-size: 14px;
      font-weight: 700;
      
      &.success-color {
        color: #10b981;
      }
      &.update-color {
        color: #f59e0b;
      }
    }
  }
}

.error-details-section {
  background: #fef2f2;
  border: 1px solid #fee2e2;
  border-radius: 12px;
  padding: 14px;

  .section-title {
    font-size: 13px;
    font-weight: 600;
    color: #991b1b;
    margin-bottom: 6px;
  }

  .details-content {
    font-size: 12px;
    color: #b91c1c;
    line-height: 1.6;
    max-height: 120px;
    overflow-y: auto;
    font-family: monospace;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>
