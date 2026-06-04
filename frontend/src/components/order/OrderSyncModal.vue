<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="740px"
    centered
    class="premium-restored-modal order-sync-modal"
    :mask-closable="syncStatus.status !== 'RUNNING'"
    :closable="false"
    destroyOnClose
  >
    <!-- Header -->
    <div class="sc-modal-header">
      <div class="sc-header-left">
        <div class="sc-header-icon-box">
          <SyncOutlined :spin="syncStatus.status === 'RUNNING'" />
        </div>
        <div class="sc-header-info">
          <div class="sc-title-row">
            <span class="sc-main-title">Shopify 订单同步</span>
            <span class="ing-badge" v-if="syncStatus.status === 'RUNNING'">ING</span>
          </div>
        </div>
      </div>
      <a-button v-if="syncStatus.status !== 'RUNNING'" type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="sc-modal-body">
      <!-- Section: 参数配置 -->
      <div class="sc-segment config-segment">
        <div class="segment-title">
          <AppstoreOutlined class="segment-icon" />
          <span>参数配置</span>
        </div>
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="input-label">选择同步店铺</div>
            <a-select
              v-model:value="form.storeId"
              placeholder="请选择"
              style="width: 100%"
              :options="storeOptions"
              :loading="storesLoading"
              class="sc-select"
              :disabled="syncStatus.status === 'RUNNING'"
            />
          </a-col>
          <a-col :span="12">
            <div class="input-label">同步时间范围<span class="input-hint">（按 Shopify 订单更新时间 updated_at）</span></div>
            <a-range-picker
              v-model:value="form.timeRange"
              style="width: 100%"
              class="sc-range-picker"
              show-time
              :presets="customPresets"
              placeholder="['开始时间', '结束时间']"
              :disabled="syncStatus.status === 'RUNNING'"
            />
          </a-col>
        </a-row>
      </div>

      <!-- Section: 执行进度 -->
      <div class="sc-segment execution-segment">
        <div class="segment-title">
          <LineChartOutlined class="segment-icon" />
          <span>执行进度</span>
        </div>
        
        <div class="progress-info">
          <div class="progress-status-text">
            正在同步: <span class="stats-num">{{ syncStatus.processed || 0 }} / {{ syncStatus.total || 0 }}</span>
            <span class="stats-detail">(成功: {{ syncStatus.success || 0 }}, 失败: {{ syncStatus.error || 0 }})</span>
          </div>
          <div class="progress-percentage">{{ syncStatus.progress || 0 }}<span class="percent-unit">%</span></div>
        </div>

        <div class="sc-progress-track">
          <div class="sc-progress-fill" :style="{ width: (syncStatus.progress || 0) + '%' }"></div>
        </div>

        <!-- Log Content -->
        <div class="log-viewport" ref="logBox">
          <div v-for="(log, index) in logs" :key="index" class="log-entry" :class="log.type">
            <span class="log-time">[{{ log.time }}]</span>
            <span class="log-msg">{{ log.text }}</span>
          </div>
          <div v-if="logs.length === 0" class="log-placeholder">等待进度开始...</div>
        </div>
      </div>

      <!-- Section: 统计卡片 -->
      <div class="stats-container">
        <div class="stat-card">
          <div class="stat-inner">
            <div class="stat-label">待处理</div>
            <div class="stat-value">{{ pendingCount }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-inner">
            <div class="stat-label">已处理</div>
            <div class="stat-value">{{ syncStatus.processed || 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-inner">
            <div class="stat-label success-label">同步成功</div>
            <div class="stat-value success-value">{{ syncStatus.success || 0 }}</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-inner">
            <div class="stat-label failure-label">同步失败</div>
            <div class="stat-value failure-value">{{ syncStatus.error || 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="sc-modal-footer">
      <div class="footer-left">
        <span class="sync-estimate">同步预计耗时: {{ estimatedTime }}</span>
      </div>
      <div class="footer-right">
        <template v-if="syncStatus.status === 'RUNNING'">
          <a-button class="sc-btn min-btn" @click="handleMinimize">
            <DesktopOutlined /> 后台运行
          </a-button>
          <a-button class="sc-btn abort-btn" danger @click="handleCancelSync">中止同步</a-button>
        </template>
        <template v-else>
          <a-button class="sc-btn cancel-btn" @click="handleCancel">取 消</a-button>
          <a-button type="primary" class="sc-btn primary-solid sync-btn" @click="handleStartSync" :loading="loading" :disabled="!form.storeId">立即同步</a-button>
        </template>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick, computed, onUnmounted } from 'vue';
import { 
  SyncOutlined, CloseOutlined, AppstoreOutlined, LineChartOutlined, 
  DesktopOutlined, InfoCircleOutlined 
} from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import dayjs, { Dayjs } from 'dayjs';
const syncShopifyOrders = async (...args: any[]): Promise<void> => {};
const getShopifyOrderSyncProgress = async (...args: any[]): Promise<any> => ({ status: 'COMPLETED' });
const clearShopifyOrderSyncProgress = async (...args: any[]): Promise<void> => {};
const cancelShopifyOrderSync = async (...args: any[]): Promise<void> => {};
const getShopifyStores = async (...args: any[]): Promise<any> => [];
import { syncInfluencerOrders } from '@/services/influencerOrderService';

const props = defineProps<{
  open: boolean;
  type: 'sample' | 'conversion';
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'sync-finished'): void;
  (e: 'status-change', status: any): void;
}>();

const visible = ref(false);
const loading = ref(false);
const storesLoading = ref(false);
const storeOptions = ref<any[]>([]);
const logBox = ref<HTMLElement | null>(null);

const form = reactive({
  storeId: undefined as number | undefined,
  timeRange: null as [Dayjs, Dayjs] | null,
});

const syncStatus = reactive({
  status: 'IDLE',
  progress: 0,
  total: 0,
  processed: 0,
  success: 0,
  error: 0,
  message: '',
});

const logs = ref<{ time: string; text: string; type?: string }[]>([]);

let timer: any = null;
let pollErrorCount = 0;
const typeLabel = computed(() => props.type === 'sample' ? '样品单' : '转化单');

// Time ranges from user screenshot
const customPresets = [
  { label: '近 24H', value: [dayjs().subtract(1, 'day'), dayjs()] },
  { label: '近 7 天', value: [dayjs().subtract(7, 'day'), dayjs()] },
  { label: '近 30 天', value: [dayjs().subtract(30, 'day'), dayjs()] },
  { label: '近 90 天', value: [dayjs().subtract(90, 'day'), dayjs()] },
  { label: '近 180 天', value: [dayjs().subtract(180, 'day'), dayjs()] },
];

const pendingCount = computed(() => Math.max(0, (syncStatus.total || 0) - (syncStatus.processed || 0)));

const estimatedTime = computed(() => {
  if (syncStatus.status !== 'RUNNING') return '2分钟';
  const remaining = (syncStatus.total || 0) - (syncStatus.processed || 0);
  if (remaining <= 0) return '即将完成';
  const mins = Math.ceil(remaining / 100);
  return `${mins}分钟`;
});

const addLog = (text: string, type: string = 'info') => {
  const time = dayjs().format('HH:mm:ss');
  logs.value.push({ time, text, type });
  nextTick(() => {
    if (logBox.value) {
      logBox.value.scrollTop = logBox.value.scrollHeight;
    }
  });
};

const handleStartSync = async () => {
  if (!form.storeId) return;
  
  loading.value = true;
  logs.value = [];
  addLog(`准备开始同步 [${typeLabel.value}]`, 'info');
  
  try {
    const startTime = form.timeRange ? form.timeRange[0].toISOString() : dayjs().subtract(7, 'day').toISOString();
    const endTime = form.timeRange ? form.timeRange[1].toISOString() : dayjs().toISOString();

    addLog(
      `设定范围(Shopify updated_at): ${dayjs(startTime).format('YYYY-MM-DD HH:mm:ss')} ~ ${dayjs(endTime).format('YYYY-MM-DD HH:mm:ss')}`,
      'info'
    );
    
    const res = await syncInfluencerOrders(form.storeId, startTime, endTime);
    if (res.success) {
      addLog('连接成功，正在拉取数据', 'success');
      syncStatus.status = 'RUNNING';
      startPolling();
    } else {
      addLog(`任务启动失败: ${res.message}`, 'error');
      message.error(res.message);
    }
  } catch (err: any) {
    addLog(`系统错误: ${err.message}`, 'error');
  } finally {
    loading.value = false;
  }
};

const startPolling = () => {
  if (timer) return;
  pollErrorCount = 0;
  const poll = async () => {
    if (!form.storeId) return;
    try {
      const progress = await getShopifyOrderSyncProgress(form.storeId, true);
      pollErrorCount = 0; // 成功后重置
      Object.assign(syncStatus, progress);
      
      if (progress.status === 'COMPLETED') {
        stopPolling();
        addLog(`同步完成: 已处理 ${progress.processed ?? progress.total} 条`, 'success');
        emit('sync-finished');
      } else if (progress.status === 'FAILED' || progress.status === 'CANCELLED') {
        stopPolling();
        addLog(`同步任务终止: ${progress.message}`, 'error');
      }
    } catch (e) {
      pollErrorCount++;
      if (pollErrorCount >= 3) {
        addLog('连接中断: 无法获取同步进度，请检查网络', 'error');
        stopPolling();
      }
    }
  };
  poll();
  timer = setInterval(poll, 3000);
};

const stopPolling = () => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
};

const fetchStores = async () => {
  storesLoading.value = true;
  try {
    const res = await getShopifyStores();
    const list = Array.isArray(res) ? res : (res as any).content || [];
    storeOptions.value = list.map((s: any) => ({ label: s.storeName, value: s.id }));
    if (list.length > 0 && !form.storeId) form.storeId = list[0].id;
  } finally {
    storesLoading.value = false;
  }
};

const handleCancel = () => { visible.value = false; };
const handleMinimize = () => {
  visible.value = false;
  emit('status-change', { ...syncStatus, minimized: true });
};
const handleCancelSync = () => {
  Modal.confirm({
    title: '确认终止',
    content: '将立即停止当前的同步进程。',
    okText: '确认终止',
    okType: 'danger',
    async onOk() {
      if (!form.storeId) return;
      await cancelShopifyOrderSync(form.storeId);
      addLog('用户强制中止了同步过程', 'warning');
    }
  });
};

watch(() => props.open, (val) => {
  visible.value = val;
  if (val) {
    fetchStores();
    if (syncStatus.status === 'RUNNING') startPolling();
  }
});

watch(visible, val => emit('update:open', val));
onUnmounted(stopPolling);
</script>

<style lang="scss" scoped>
.order-sync-modal {
  :deep(.ant-modal-content) {
    border-radius: 20px;
    padding: 0;
    overflow: hidden;
    box-shadow: 0 40px 80px -20px rgba(0, 0, 0, 0.2);
  }
}

.sc-modal-header {
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .sc-header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .sc-header-icon-box {
      width: 44px;
      height: 44px;
      background: #2563eb;
      color: white;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      box-shadow: 0 6px 16px -4px rgba(37, 99, 235, 0.5);
    }

    .sc-main-title {
      font-size: 20px;
      font-weight: 700;
      color: #0f172a;
    }

    .ing-badge {
      margin-left: 10px;
      background: #eff6ff;
      color: #2563eb;
      padding: 1px 12px;
      border-radius: 6px;
      font-size: 11px;
      font-weight: 800;
    }
  }

  .close-btn {
    font-size: 18px;
    color: #94a3b8;
    &:hover { color: #64748b; background: #f8fafc; }
  }
}

.sc-modal-body {
  padding: 0 32px 32px;
}

.sc-segment {
  margin-bottom: 28px;

  .segment-title {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #475569;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 16px;

    .segment-icon { color: #3b82f6; font-size: 14px; }
  }
}

.config-segment {
  background: #f8fbff;
  padding: 20px;
  border-radius: 16px;
  border: 1px solid #edf4ff;

  .input-label { font-size: 14px; font-weight: 600; color: #334155; margin-bottom: 10px;
    .input-hint { font-weight: 400; color: #94a3b8; font-size: 12px; margin-left: 6px; }
  }

  :deep(.sc-select) .ant-select-selector,
  :deep(.sc-range-picker) {
    background: #fff !important;
    border-radius: 10px !important;
    height: 42px !important;
    border: 1px solid #e2e8f0 !important;
    padding: 0 12px !important;
    &:hover { border-color: #3b82f6 !important; }
  }
}

.execution-segment {
  .progress-info {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 10px;

    .progress-status-text {
      font-size: 15px;
      font-weight: 600;
      color: #1e293b;
      .stats-num { color: #2563eb; margin: 0 4px; }
      .stats-detail { font-size: 13px; color: #94a3b8; margin-left: 8px; font-weight: 400; }
    }

    .progress-percentage {
      font-size: 28px;
      font-weight: 800;
      color: #0f172a;
      .percent-unit { font-size: 12px; margin-left: 1px; }
    }
  }

  .sc-progress-track {
    height: 12px;
    background: #f1f5f9;
    border-radius: 6px;
    overflow: hidden;
    margin-bottom: 20px;

    .sc-progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #3b82f6 0%, #2563eb 100%);
      transition: width 0.4s ease;
      position: relative;
      &::after {
        content: ''; position: absolute; top:0; left:0; right:0; bottom:0;
        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
        animation: shine 2s infinite;
      }
    }
  }

  @keyframes shine {
    from { transform: translateX(-100%); }
    to { transform: translateX(100%); }
  }

  .log-viewport {
    height: 180px;
    background: #fff;
    border: 1px solid #f1f5f9;
    border-radius: 12px;
    padding: 12px 16px;
    overflow-y: auto;
    font-family: 'JetBrains Mono', 'Courier New', monospace;
    font-size: 12px;
    line-height: 1.6;

    .log-entry {
      display: flex; gap: 8px; margin-bottom: 2px;
      .log-time { color: #cbd5e1; }
      .log-msg { color: #475569; }
      &.success .log-msg { color: #10b981; font-weight: 600; }
      &.error .log-msg { color: #ef4444; }
    }
    .log-placeholder { color: #e2e8f0; text-align: center; margin-top: 70px; font-style: italic; }
  }
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  .stat-card {
    background: #fff;
    border: 1px solid #f1f5f9;
    border-radius: 16px;
    padding: 16px;
    text-align: center;

    .stat-label { font-size: 12px; font-weight: 600; color: #64748b; margin-bottom: 6px; }
    .stat-value { font-size: 24px; font-weight: 800; color: #1e293b; }
    .success-label { color: #10b981; }
    .success-value { color: #10b981; }
    .failure-label { color: #ef4444; }
    .failure-value { color: #ef4444; }
  }
}

.sc-modal-footer {
  padding: 24px 32px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .sync-estimate { color: #94a3b8; font-size: 13px; font-weight: 500; }

  .footer-right { display: flex; gap: 12px; }

  .sc-btn {
    height: 42px;
    padding: 0 20px;
    border-radius: 10px;
    font-weight: 600;
    font-size: 14px;
    transition: all 0.2s;
    
    &.cancel-btn { background: #fff; color: #64748b; border: 1px solid #e2e8f0; }
    &.primary-solid { background: #2563eb; color: #fff; border: none; box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }
    &.primary-solid:hover { transform: translateY(-1px); background: #1d4ed8; }
    &.min-btn { color: #3b82f6; border: 1px solid #dbeafe; }
    &.abort-btn:hover { background: #fef2f2; }
  }
}
</style>
