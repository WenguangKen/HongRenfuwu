<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="650px"
    :footer="null"
    centered
    class="premium-refined-modal"
    :mask-closable="false"
    :closable="false"
    destroy-on-close
  >
    <!-- 头部 -->
    <template #title>
      <div class="glass-header-compact" style="background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(5, 150, 105, 0.08) 100%);">
        <div class="header-main">
          <div class="logo-box" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
            <span class="logo-text">EDIT</span>
          </div>
          <div class="title-meta">
            <div class="main-title">编辑任务</div>
            <div class="simple-subtitle">Update task information and metadata</div>
          </div>
        </div>
      </div>
    </template>

    <!-- 内容 -->
    <div class="modal-body-container" style="padding: 24px; overflow-y: auto;">
      <div class="ic-form-container">
        <a-form :model="formState" layout="vertical" class="ic-form">
          <!-- 区域 1: 基础信息 (只读) -->
          <div class="section-container">
            <div class="section-title">
              <span class="icon">ℹ️</span> 基础信息 (Basic Info)
            </div>
            
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="任务ID (Task Group ID)">
                  <a-input :value="taskData?.taskId" disabled class="premium-disabled-input" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="关联红人 (Influencer)">
                  <a-input :value="taskData?.influencer" disabled class="premium-disabled-input" />
                </a-form-item>
              </a-col>
            </a-row>
          </div>

          <!-- 区域 2: 可编辑信息 -->
          <div class="section-container">
            <div class="section-title">
              <span class="icon">✏️</span> 可编辑信息 (Editable Info)
            </div>
            
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="目标平台 (Platform)" required>
                  <a-select
                    v-model:value="formState.platform"
                    placeholder="请选择平台"
                    allow-clear
                  >
                    <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="负责人 (Owner)">
                  <a-select
                    v-model:value="formState.owner"
                    placeholder="请选择负责人"
                    show-search
                    :filter-option="filterOption"
                    allow-clear
                  >
                    <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.name">{{ u.name }}</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="关联订单 (Order No.)">
                  <a-select
                    v-model:value="formState.orderNo"
                    placeholder="选择关联的样品订单"
                    allow-clear
                    show-search
                    :loading="loadingOrders"
                  >
                    <a-select-option v-for="order in associatedOrders" :key="order.orderName" :value="order.orderName">
                      <div style="display: flex; justify-content: space-between;">
                        <span>{{ order.orderName }}</span>
                        <span style="font-size: 12px; color: #999;">{{ order.orderCreatedAt?.split('T')[0] }}</span>
                      </div>
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="关联商品 SKU (Product SKU)">
                  <a-input v-model:value="formState.productSku" placeholder="请输入商品 SKU" allow-clear />
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="任务描述 (Description)">
                  <a-textarea 
                    v-model:value="formState.description" 
                    placeholder="请输入任务描述" 
                    :rows="4" 
                    allow-clear
                    show-count
                    :maxlength="500"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
        </a-form>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="modal-fixed-footer">
      <a-button @click="handleCancel" class="btn-cancel">取消</a-button>
      <a-button type="primary" @click="handleOk" :loading="loading" class="btn-submit" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%); border: none;">保存修改</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, watch, ref } from 'vue';
import { message } from 'ant-design-vue';
import contentService from '@/services/contentService';
import { getSampleOrdersByInfluencer, type SampleOrderDto } from '@/services/influencerOrderService';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';

const props = withDefaults(defineProps<{
  open: boolean;
  taskData: any;
  platforms: string[];
}>(), {
  open: false,
  platforms: () => ['TikTok', 'Instagram', 'YouTube', 'Facebook'],
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'ok'): void;
}>();

const visible = ref(false);
const loading = ref(false);
const loadingOrders = ref(false);
const associatedOrders = ref<SampleOrderDto[]>([]);

const commonStore = useCommonDataStore();
const { ownerUsers } = storeToRefs(commonStore);

const formState = reactive({
  platform: undefined as string | undefined,
  owner: undefined as string | undefined,
  orderNo: '',
  productSku: '',
  description: ''
});

const filterOption = (input: string, option: any) => {
  const text = option.children ? option.children : (option.label || '');
  return String(text).toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

// 监听 open 变化，初始化表单
watch(
  () => props.open,
  (val) => {
    visible.value = val;
    if (val && props.taskData) {
      formState.platform = props.taskData.platform;
      formState.owner = props.taskData.owner || props.taskData.ownerName;
      formState.orderNo = props.taskData.orderNo || '';
      formState.productSku = props.taskData.productSku || '';
      formState.description = props.taskData.description || props.taskData.desc || '';
      
      // 初始化已有关联订单
      if (props.taskData.orderNo) {
        formState.orderNo = props.taskData.orderNo;
      }

      // 如果有红人 ID，加载订单
      if (props.taskData.influencerId) {
        loadInfluencerOrders(props.taskData.influencerId);
      }
    }
  },
);

const loadInfluencerOrders = async (influencerId: number) => {
  loadingOrders.value = true;
  try {
    const orders = await getSampleOrdersByInfluencer(influencerId);
    associatedOrders.value = orders || [];
  } catch (e) {
    console.error('Failed to load influencer orders:', e);
    associatedOrders.value = [];
  } finally {
    loadingOrders.value = false;
  }
};

watch(visible, (val) => emit('update:open', val));

const handleCancel = () => {
  visible.value = false;
};

const handleOk = async () => {
  if (!formState.platform) {
    message.warning('请选择目标平台');
    return;
  }
  
  const ids = props.taskData?.ids;
  if (!ids || !Array.isArray(ids) || ids.length === 0) {
    message.error('无法定位任务 ID');
    return;
  }

  loading.value = true;
  try {
    // 遍历组内所有 ID 进行更新
    const updatePromises = ids.map(id => 
      contentService.updateContent(id, {
        platform: formState.platform,
        owner: formState.owner || undefined,
        orderNo: formState.orderNo || undefined,
        productSku: formState.productSku || undefined,
        description: formState.description || undefined,
      })
    );

    await Promise.all(updatePromises);
    
    message.success('修改任务成功');
    emit('ok');
    visible.value = false;
  } catch (e: any) {
    console.error('Failed to update task:', e);
    message.error('修改任务失败: ' + (e.response?.data?.message || e.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.modal-fixed-footer {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-cancel {
  height: 40px;
  border-radius: 10px;
  font-weight: 600;
  padding: 0 20px;
}

.btn-submit {
  height: 40px;
  padding: 0 24px;
  border-radius: 10px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.3);
  }
}

.section-container {
  margin-bottom: 24px;
  
  .section-title {
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;
    .icon { font-size: 16px; }
  }
}

.premium-disabled-input {
  background: #f1f5f9 !important;
  color: #64748b !important;
  border-color: #e2e8f0 !important;
  cursor: not-allowed;
}

:deep(.ant-input), :deep(.ant-select-selector) {
  border-radius: 8px !important;
}
</style>
