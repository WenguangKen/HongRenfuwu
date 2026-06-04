<template>
  <a-modal
    :open="open"
    :title="null"
    :footer="null"
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
          <div class="header-title">同步易仓(亚马逊)商品</div>
          <div class="header-subtitle">将易仓中的亚马逊店铺商品数据同步到本地数据库</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="closeModal">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="modal-body">
      <div class="form-section">
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
          <InfoCircleOutlined /> 将自动拉取该店铺的商品和最新的 FBA 库存
        </div>
      </div>

      <div class="sync-info" v-if="loading">
        <a-progress :percent="progress" :status="progressStatus" />
        <div class="status-text">{{ statusText }}</div>
      </div>
    </div>

    <div class="modal-footer">
      <a-space size="middle">
        <a-button @click="closeModal" class="premium-btn">取消</a-button>
        <a-button 
          type="primary" 
          @click="startSync" 
          class="premium-btn primary-gradient"
          :loading="loading"
          :disabled="!selectedStoreId"
        >
          开始同步
        </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { SyncOutlined, CloseOutlined, InfoCircleOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { getEccangStores, syncEccangProducts, type EccangStoreConfig } from '@/services/eccangService';

const props = defineProps<{
  open: boolean;
}>();

const emit = defineEmits(['update:open', 'success']);

const loading = ref(false);
const fetchingStores = ref(false);
const selectedStoreId = ref<number | undefined>(undefined);
const stores = ref<EccangStoreConfig[]>([]);
const progress = ref(0);
const progressStatus = ref<'active' | 'success' | 'exception'>('active');
const statusText = ref('准备就绪');
let pollTimer: any = null;

const storeOptions = computed(() => {
  return stores.value
    .map(s => ({
      label: s.storeName,
      value: s.id,
      domain: s.amazonShopName || s.platform
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
    progress.value = 0;
    progressStatus.value = 'active';
    statusText.value = '准备就绪';
    selectedStoreId.value = undefined;
  }, 300);
};

const startSync = async () => {
  if (!selectedStoreId.value) return;
  
  loading.value = true;
  progress.value = 5;
  statusText.value = '正在请求同步任务...';
  progressStatus.value = 'active';
  
  try {
    const storeId = selectedStoreId.value;
    // 1. Start Sync Task
    await syncEccangProducts();
    
    // Simulate progress for UX
    statusText.value = '正在拉取易仓数据...';
    let p = 10;
    pollTimer = setInterval(() => {
      p += Math.floor(Math.random() * 15) + 5;
      if (p >= 90) {
        clearInterval(pollTimer);
        progress.value = 100;
        progressStatus.value = 'success';
        statusText.value = '同步完成';
        message.success('商品及 FBA 库存同步成功');
        
        setTimeout(() => {
          emit('success');
          closeModal();
        }, 1000);
      } else {
        progress.value = p;
      }
    }, 500);

  } catch (error) {
    progressStatus.value = 'exception';
    statusText.value = '请求失败';
    loading.value = false;
    console.error(error);
  }
};

watch(() => props.open, (val) => {
  if (val) {
    fetchStores();
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
</style>
