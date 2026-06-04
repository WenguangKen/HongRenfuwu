<template>
  <a-modal
    v-model:open="visible"
    :width="560"
    :confirmLoading="loading"
    @ok="handleSubmit"
    @cancel="handleCancel"
    class="bind-influencer-modal"
    :footer="null"
  >
    <template #title>
      <div class="modal-header">
        <div class="header-icon">
          <UserAddOutlined />
        </div>
        <div class="header-text">
          <span class="header-title">绑定红人</span>
          <span class="header-subtitle">为订单关联红人信息</span>
        </div>
      </div>
    </template>
    
    <div class="modal-body">
      <!-- 订单信息卡片 -->
      <div class="order-card" v-if="orderInfo">
        <div class="card-header">
          <ShoppingOutlined class="card-icon" />
          <span>订单信息</span>
        </div>
        <div class="card-content">
          <div class="info-item">
            <span class="info-label">订单号</span>
            <span class="info-value order-no">{{ orderInfo.orderNo }}</span>
          </div>
          <div class="info-item" v-if="orderInfo.currentInfluencer">
            <span class="info-label">当前绑定</span>
            <a-tag color="orange">{{ orderInfo.currentInfluencer }}</a-tag>
          </div>
          <div class="info-item" v-if="orderInfo.isSplit">
            <span class="info-label">拆单提示</span>
            <a-tag color="blue">将同时绑定所有相关订单</a-tag>
          </div>
        </div>
      </div>
      
      <!-- 选择红人 -->
      <div class="select-section">
        <div class="section-header">
          <TeamOutlined class="section-icon" />
          <span>选择红人</span>
          <span class="section-hint">仅显示待联系、已联系、沟通中和合作中的红人</span>
        </div>
        <a-select
          v-model:value="formState.influencerId"
          placeholder="搜索红人姓名或昵称..."
          show-search
          :filter-option="false"
          :options="influencerOptions"
          :loading="searchLoading"
          @search="handleSearch"
          size="large"
          class="influencer-select"
          :not-found-content="searchLoading ? '加载中...' : '暂无可选红人'"
        >
          <template #option="{ label, extra }">
            <div class="influencer-option">
              <a-avatar :size="32" class="option-avatar">
                {{ label.charAt(0) }}
              </a-avatar>
              <div class="option-info">
                <span class="option-name">{{ label }}</span>
                <span class="option-status" :class="extra?.statusClass">{{ extra?.statusText }}</span>
              </div>
            </div>
          </template>
        </a-select>
      </div>
    </div>
    
    <!-- 底部操作 -->
    <div class="modal-footer">
      <a-button size="large" @click="handleCancel" class="btn-cancel">取消</a-button>
      <a-button 
        type="primary" 
        size="large" 
        @click="handleSubmit" 
        :loading="loading"
        :disabled="!formState.influencerId"
        class="btn-confirm"
      >
        确认绑定
      </a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { message } from 'ant-design-vue';
import { UserAddOutlined, ShoppingOutlined, TeamOutlined } from '@ant-design/icons-vue';
import { influencerService } from '@/services/influencerService';
import { bindSampleOrderToInfluencer } from '@/services/influencerOrderService';

const props = defineProps<{
  open: boolean;
  orderInfo?: {
    orderId: number;
    shopifyOrderNumber: number;
    orderNo: string;
    currentInfluencer?: string;
    currentInfluencerId?: number;
    isSplit?: boolean;
  };
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
  'success': [];
}>();

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val)
});

const loading = ref(false);
const searchLoading = ref(false);
const influencerOptions = ref<{ label: string; value: number; extra?: any }[]>([]);

const formState = reactive({
  influencerId: undefined as number | undefined
});

let searchTimeout: ReturnType<typeof setTimeout>;

// 加载红人列表 - 支持后端搜索过滤
const loadInfluencers = async (keyword: string = '') => {
  searchLoading.value = true;
  try {
    // 构造并发请求：分别搜索待联系(PENDING)、已联系(CONTACTED)、沟通中(COMMUNICATING)和合作中(COOPERATING)的红人
    const [pending, contacted, communicating, cooperating] = await Promise.all([
      influencerService.getList({
        page: 0,
        size: 50,
        status: 'PENDING',
        searchKey: keyword || undefined
      }),
      influencerService.getList({
        page: 0,
        size: 50,
        status: 'CONTACTED',
        searchKey: keyword || undefined
      }),
      influencerService.getList({
        page: 0,
        size: 50, // 每次搜索返回前50条足够
        status: 'COMMUNICATING',
        searchKey: keyword || undefined
      }),
      influencerService.getList({
        page: 0,
        size: 50,
        status: 'COOPERATING',
        searchKey: keyword || undefined
      })
    ]);
    
    const list = [
      ...(pending.content || []).map((inf: any) => ({ ...inf, statusText: '待联系', statusClass: 'status-pending' })),
      ...(contacted.content || []).map((inf: any) => ({ ...inf, statusText: '已联系', statusClass: 'status-contacted' })),
      ...(communicating.content || []).map((inf: any) => ({ ...inf, statusText: '沟通中', statusClass: 'status-communicating' })),
      ...(cooperating.content || []).map((inf: any) => ({ ...inf, statusText: '合作中', statusClass: 'status-cooperating' }))
    ];
    
    influencerOptions.value = list.map((inf: any) => ({
      label: inf.realName || inf.nickname || inf.nickName || `红人#${inf.id}`,
      value: inf.id,
      extra: {
        statusText: inf.statusText,
        statusClass: inf.statusClass
      }
    }));
    
    // 如果当前有选中的人但不在搜索结果里，且不是在搜索状态下，可以考虑把它加进去
    // 这里如果只是打开默认，后端不一定会返回这个人
  } catch (e) {
    console.error('获取红人列表失败', e);
    message.error('获取红人列表失败');
  } finally {
    searchLoading.value = false;
  }
};

const handleSearch = (value: string) => {
  if (searchTimeout) {
    clearTimeout(searchTimeout);
  }
  searchTimeout = setTimeout(() => {
    loadInfluencers(value);
  }, 400); // 400ms 防抖
};

// 禁用前端本地过滤，使用后端过滤
const filterOption = false;

const handleSubmit = async () => {
  if (!formState.influencerId) {
    message.warning('请选择红人');
    return;
  }
  
  if (!props.orderInfo) {
    message.error('订单信息缺失');
    return;
  }
  
  const selectedOption = influencerOptions.value.find(opt => opt.value === formState.influencerId);
  const influencerName = selectedOption?.label || '';
  
  loading.value = true;
  try {
    const requestPayload = {
      orderId: props.orderInfo.orderId,
      shopifyOrderNumber: props.orderInfo.shopifyOrderNumber,
      influencerId: formState.influencerId,
      influencerName: influencerName
    };
    console.log('绑定请求参数:', JSON.stringify(requestPayload));
    await bindSampleOrderToInfluencer(requestPayload);
    
    message.success('绑定成功');
    emit('success');
    visible.value = false;
  } catch (e: any) {
    console.error('绑定失败', e);
    message.error('绑定失败: ' + (e.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  formState.influencerId = undefined;
  visible.value = false;
};

// 打开时加载红人列表（默认无关键字，加载最近记录）
watch(() => props.open, async (val) => {
  if (val) {
    await loadInfluencers();
    // 如果已有绑定，需要确保它在列表中
    if (props.orderInfo?.currentInfluencerId) {
      const id = props.orderInfo.currentInfluencerId;
      const exist = influencerOptions.value.find(opt => opt.value === id);
      if (!exist && props.orderInfo.currentInfluencer) {
        // 如果当前选项没显示，手动构造一个假的以便回填
        influencerOptions.value.unshift({
          label: props.orderInfo.currentInfluencer,
          value: id,
          extra: { statusText: '已绑定', statusClass: 'status-cooperating' }
        });
      }
      formState.influencerId = id;
    } else {
      formState.influencerId = undefined;
    }
  }
});
</script>

<style scoped>
.bind-influencer-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.bind-influencer-modal :deep(.ant-modal-header) {
  padding: 20px 24px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-bottom: none;
}

.bind-influencer-modal :deep(.ant-modal-body) {
  padding: 0;
}

.modal-header {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
}

.header-subtitle {
  font-size: 13px;
  color: #64748b;
}

.modal-body {
  padding: 24px;
}

.order-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
  font-size: 14px;
}

.card-icon {
  color: #3b82f6;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-label {
  color: #94a3b8;
  font-size: 13px;
  min-width: 70px;
}

.info-value {
  color: #1e293b;
  font-weight: 500;
}

.order-no {
  font-family: 'Monaco', 'Menlo', monospace;
  font-size: 15px;
  color: #3b82f6;
}

.select-section {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 12px;
  font-size: 14px;
}

.section-icon {
  color: #10b981;
}

.section-hint {
  font-weight: 400;
  color: #94a3b8;
  font-size: 12px;
  margin-left: auto;
}

.influencer-select {
  width: 100%;
}

.influencer-select :deep(.ant-select-selector) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  padding: 4px 12px !important;
  min-height: 48px !important;
}

.influencer-select :deep(.ant-select-selector:hover) {
  border-color: #3b82f6 !important;
}

.influencer-select :deep(.ant-select-focused .ant-select-selector) {
  border-color: #3b82f6 !important;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important;
}

.influencer-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.option-avatar {
  background: linear-gradient(135deg, #f472b6 0%, #ec4899 100%);
  color: white;
  font-weight: 600;
}

.option-info {
  display: flex;
  flex-direction: column;
}

.option-name {
  font-weight: 500;
  color: #1e293b;
}

.option-status {
  font-size: 12px;
}

.status-pending {
  color: #94a3b8;
}

.status-contacted {
  color: #3b82f6;
}

.status-communicating {
  color: #f59e0b;
}

.status-cooperating {
  color: #10b981;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  min-width: 100px;
  border-radius: 8px;
}

.btn-confirm {
  min-width: 120px;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-confirm:hover:not(:disabled) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
}

.btn-confirm:disabled {
  background: #cbd5e1;
  box-shadow: none;
}
</style>
