<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="800px"
    :footer="null"
    centered
    class="premium-refined-modal"
    :mask-closable="false"
    :closable="false"
    destroy-on-close
  >
    <!-- 头部 -->
    <template #title>
      <div class="glass-header-compact">
        <div class="header-main">
          <div class="logo-box">
            <span class="logo-text">TASK</span>
          </div>
          <div class="title-meta">
            <div class="main-title">新建任务</div>
            <div class="simple-subtitle">Create new content task for influencer</div>
          </div>
        </div>
      </div>
    </template>

    <!-- 内容 -->
    <div class="modal-body-container" style="padding: 24px; overflow-y: auto;">
      <div class="ic-form-container">
        <a-form :model="formState" layout="vertical" class="ic-form">
          <!-- 区域 1: 核心信息 -->
          <div class="section-container">
            <div class="section-title">
              <span class="icon">📌</span> 核心信息 (Core Info)
            </div>
            
            <a-row :gutter="24">
              <!-- 社媒名称 (Handle) -->
              <a-col :span="8">
                <a-form-item label="社媒名称 (Handle)" required>
                  <a-select
                    v-model:value="formState.influencerId"
                    placeholder="按 Handle 搜索"
                    show-search
                    :filter-option="false"
                    allow-clear
                    :loading="loadingInfluencers"
                    @search="handleRemoteSearch"
                    @change="handleInfluencerChange"
                    :not-found-content="searchTip"
                    class="premium-select"
                  >
                    <a-select-option 
                      v-for="inf in availableInfluencers" 
                      :key="inf.id" 
                      :value="inf.id"
                    >
                      {{ (inf as any).displayHandle || '未知账号' }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              
              <!-- 红人邮箱 (Email) -->
              <a-col :span="8">
                <a-form-item label="红人邮箱 (Email)">
                  <a-select
                    v-model:value="formState.influencerId"
                    placeholder="按邮箱搜索"
                    show-search
                    :filter-option="false"
                    allow-clear
                    :loading="loadingInfluencers"
                    @search="handleRemoteSearchByEmail"
                    @change="handleInfluencerChange"
                    :not-found-content="searchTip"
                    class="premium-select"
                  >
                    <a-select-option 
                      v-for="inf in availableInfluencers.filter((i: any) => i.email)" 
                      :key="inf.id" 
                      :value="inf.id"
                    >
                      {{ inf.email }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <!-- 真实姓名 (Name) -->
              <a-col :span="8">
                <a-form-item label="真实姓名 (Name)">
                  <a-select
                    v-model:value="formState.influencerId"
                    placeholder="按姓名搜索"
                    show-search
                    :filter-option="false"
                    allow-clear
                    :loading="loadingInfluencers"
                    @search="handleRemoteSearchByName"
                    @change="handleInfluencerChange"
                    :not-found-content="searchTip"
                    class="premium-select"
                  >
                    <a-select-option 
                      v-for="inf in availableInfluencers.filter((i: any) => i.realName || i.nickName)" 
                      :key="inf.id" 
                      :value="inf.id"
                    >
                      {{ inf.realName || inf.nickName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="目标平台 (Platform)" required>
                  <a-select
                    v-model:value="formState.platform"
                    placeholder="请选择平台"
                    show-search
                    :filter-option="filterOption"
                    allow-clear
                    :loading="loadingPlatforms"
                  >
                    <a-select-option v-for="p in availablePlatforms" :key="p.name" :value="p.name">{{ p.name }}</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-row :gutter="12">
                  <a-col :span="12">
                    <a-form-item label="负责人 (Owner)">
                      <a-select
                        v-model:value="formState.owner"
                        placeholder="请选择负责人"
                        show-search
                        :filter-option="filterOption"
                        allow-clear
                        class="premium-select"
                      >
                        <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.name">{{ u.name }}</a-select-option>
                      </a-select>
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item label="联络员 (Liaison)">
                      <a-input v-model:value="formState.contactPersonName" disabled placeholder="自动带出" class="premium-disabled-input" />
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
          </div>

          <!-- 区域 2: 关联信息 -->
          <div class="section-container">
            <div class="section-title">
              <span class="icon">🔗</span> 关联信息 (Relation Info)
            </div>
            
            <a-row :gutter="24">
              <a-col :span="24">
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
            </a-row>

          </div>

          <!-- 区域 3: 其他信息 -->
          <div class="section-container">
            <div class="section-title">
              <span class="icon">📝</span> 其他信息 (Other Info)
            </div>
            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-item label="任务描述 (Description)">
                  <a-textarea 
                    v-model:value="formState.desc" 
                    placeholder="请输入任务描述" 
                    :rows="3" 
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
      <a-button type="primary" @click="handleOk" :loading="loading" class="btn-submit">确认创建</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, watch, ref, onMounted, computed } from 'vue';
import { message } from 'ant-design-vue';
import contentService from '@/services/contentService';
import { influencerService } from '@/services/influencerService';
import type { Influencer } from '@/types/influencer';
import tagService, { type SystemTag } from '@/services/tagService';
import { getUserList, type User } from '@/services/userService';
import { getSampleOrdersByInfluencer, type SampleOrderDto } from '@/services/influencerOrderService';

const props = withDefaults(defineProps<{
  open: boolean;
}>(), {
  open: false,
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'ok'): void;
}>();

const visible = ref(false);
const loading = ref(false);
const loadingInfluencers = ref(false);
const loadingPlatforms = ref(false);
const loadingOrders = ref(false);
const availableInfluencers = ref<Influencer[]>([]);
const availablePlatforms = ref<{ name: string }[]>([]);
const associatedOrders = ref<SampleOrderDto[]>([]);

// 排除的状态
const EXCLUDED_STATUSES = ['Blacklisted', 'NotCooperating', 'NotRecommended', 'BLACKLISTED', 'NOT_COOPERATING', 'NOT_RECOMMENDED'];

const formState = reactive({
  influencerId: undefined as number | undefined,
  influencerEmail: '',
  realName: '',
  platform: undefined as string | undefined,
  owner: undefined as string | undefined,
  contactPersonName: undefined as string | undefined,
  orderNo: '',
  desc: ''
});

// 用户名映射（复用全局公共 Store 缓存，避免重复请求）
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';
const commonStore = useCommonDataStore();
const { ownerUsers } = storeToRefs(commonStore);
const userNameMap = computed(() => commonStore.userNameMap);

const loadUsers = async () => {
  await commonStore.loadUsers();
};

// 根据 ownerId 解析负责人名称（与列表页一致）
const resolveOwnerName = (inf: Influencer): string => {
  if (inf.ownerId) {
    const name = userNameMap.value[inf.ownerId];
    if (name) return name;
  }
  return inf.ownerName || '';
};

// 根据 contactPersonId 解析联络员名称
const resolveContactName = (inf: Influencer): string => {
  if (inf.contactPersonId) {
    const name = userNameMap.value[inf.contactPersonId];
    if (name) return name;
  }
  return inf.contactPersonName || '';
};

// 计算含有邮箱的红人列表，供邮箱下拉选项使用
const availableInfluencersWithEmail = computed(() => {
  return availableInfluencers.value.filter(i => !!i.email);
});

// 选择红人后自动填充邮箱、负责人和联络员，并加载订单
const handleInfluencerChange = async (influencerId: number | undefined) => {
  if (influencerId) {
    const inf = availableInfluencers.value.find(i => i.id === influencerId);
    if (inf) {
      formState.influencerEmail = inf.email || '';
      formState.realName = inf.realName || inf.nickName || '';
      formState.owner = resolveOwnerName(inf);
      formState.contactPersonName = resolveContactName(inf);
      
      // 加载该红人的样品订单
      loadInfluencerOrders(influencerId);
    }
  } else {
    formState.influencerEmail = '';
    formState.realName = '';
    formState.owner = undefined;
    formState.contactPersonName = undefined;
    associatedOrders.value = [];
    formState.orderNo = '';
  }
};

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

// 从 URL 或原始文本中提取 Handle 名称
const extractHandle = (text: string | undefined): string => {
  if (!text) return '';
  let clean: string = text.trim();
  if (clean.includes('/') || clean.includes('.com')) {
    const parts = clean.split('/').filter(Boolean);
    let h = parts.pop() || '';
    if (h.includes('?')) h = h.split('?')[0] || '';
    if (!h) return '';
    return h.startsWith('@') ? h : '@' + h;
  }
  return clean.startsWith('@') ? clean : '@' + clean;
};

// 加载平台标签
const loadPlatforms = async () => {
  loadingPlatforms.value = true;
  try {
    const tags = await tagService.getTagsByCategory('PLATFORM', true);
    availablePlatforms.value = tags.map((t: SystemTag) => ({ name: t.name }));
  } catch (e) {
    console.error('Failed to load platforms:', e);
    availablePlatforms.value = [
      { name: 'TikTok' },
      { name: 'Instagram' },
      { name: 'YouTube' },
      { name: 'Facebook' },
    ];
  } finally {
    loadingPlatforms.value = false;
  }
};

// 搜索提示文本
const searchTip = computed(() => {
  return loadingInfluencers.value ? '搜索中...' : '请输入至少2个字符搜索';
});

// 防抖定时器
let searchTimer: ReturnType<typeof setTimeout> | null = null;

// 处理搜索结果的通用处理器（提取 displayHandle）
const processInfluencerResults = (raw: any[]): Influencer[] => {
  return raw
    .map((inf: any) => {
      let handle = inf.defaultHandle;
      if (!handle) {
        const smList = inf.socialMedias || inf.socialMediaList || [];
        const primary = smList.find((s: any) => s.isDefault) || smList[0];
        if (primary) {
          handle = primary.handle || primary.url;
        }
      }
      if (!handle) {
        handle = inf.profileLink || inf.defaultUrl;
      }
      if (!handle) {
        handle = inf.realName || inf.nickName || inf.email || `User_${inf.id}`;
      }
      inf.displayHandle = extractHandle(handle);
      if (!inf.displayHandle && handle) {
        inf.displayHandle = handle;
      }
      return inf;
    })
    .filter((inf: any) => {
      const hasHandle = !!inf.displayHandle && inf.displayHandle !== '';
      const hasEmail = !!inf.email && inf.email !== '';
      const hasName = !!(inf.realName || inf.nickName);
      return hasHandle || hasEmail || hasName;
    });
};

// 服务端搜索：按 Handle
const handleRemoteSearch = (value: string) => {
  if (searchTimer) clearTimeout(searchTimer);
  if (!value || value.length < 2) {
    availableInfluencers.value = [];
    return;
  }
  searchTimer = setTimeout(async () => {
    loadingInfluencers.value = true;
    try {
      const result = await influencerService.getList({
        page: 1,
        size: 50,
        status: 'COOPERATING',
        socialHandle: value,
      });
      availableInfluencers.value = processInfluencerResults(result.content || []);
    } catch (e) {
      console.error('Failed to search influencers by handle:', e);
    } finally {
      loadingInfluencers.value = false;
    }
  }, 300);
};

// 服务端搜索：按邮箱
const handleRemoteSearchByEmail = (value: string) => {
  if (searchTimer) clearTimeout(searchTimer);
  if (!value || value.length < 2) {
    availableInfluencers.value = [];
    return;
  }
  searchTimer = setTimeout(async () => {
    loadingInfluencers.value = true;
    try {
      const result = await influencerService.getList({
        page: 1,
        size: 50,
        status: 'COOPERATING',
        email: value,
      });
      availableInfluencers.value = processInfluencerResults(result.content || []);
    } catch (e) {
      console.error('Failed to search influencers by email:', e);
    } finally {
      loadingInfluencers.value = false;
    }
  }, 300);
};

// 服务端搜索：按姓名
const handleRemoteSearchByName = (value: string) => {
  if (searchTimer) clearTimeout(searchTimer);
  if (!value || value.length < 2) {
    availableInfluencers.value = [];
    return;
  }
  searchTimer = setTimeout(async () => {
    loadingInfluencers.value = true;
    try {
      const result = await influencerService.getList({
        page: 1,
        size: 50,
        status: 'COOPERATING',
        searchKey: value,
      });
      availableInfluencers.value = processInfluencerResults(result.content || []);
    } catch (e) {
      console.error('Failed to search influencers by name:', e);
    } finally {
      loadingInfluencers.value = false;
    }
  }, 300);
};

// 通用过滤
const filterOption = (input: string, option: any) => {
  const text = option.children ? option.children : (option.label || '');
  return String(text).toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

onMounted(() => {
  loadUsers();
  loadPlatforms();
});

watch(
  () => props.open,
  (val) => {
    visible.value = val;
    if (val) resetForm();
  },
);

watch(visible, (val) => emit('update:open', val));

const resetForm = () => {
  formState.influencerId = undefined;
  formState.influencerEmail = '';
  formState.realName = '';
  formState.platform = undefined;
  formState.owner = undefined;
  formState.contactPersonName = undefined;
  formState.orderNo = '';
  formState.desc = '';
  loading.value = false;
};

const handleCancel = () => {
  visible.value = false;
};

const handleOk = async () => {
  if (!formState.influencerId) {
    message.warning('请选择关联红人');
    return;
  }
  if (!formState.influencerEmail) {
    message.warning('请填写红人邮箱');
    return;
  }
  if (!formState.platform) {
    message.warning('请选择目标平台');
    return;
  }
  // 负责人和联络员为非必填，自动从红人信息带出
  
  // 获取选中的红人信息
  const selectedInfluencer = availableInfluencers.value.find(i => i.id === formState.influencerId);
  const influencerName = selectedInfluencer?.realName || selectedInfluencer?.nickName || `红人${formState.influencerId}`;
  
  loading.value = true;
  try {
    // 调用真实 API 创建内容任务
    await contentService.createContent({
      influencerId: formState.influencerId,
      title: `${influencerName} - ${formState.platform} 内容任务`,
      description: formState.desc || '',
      platform: formState.platform,
      postType: 'VIDEO', // 默认视频类型
      owner: formState.owner,
      orderNo: formState.orderNo || undefined,
      productSku: undefined, // 不关联商品
    });
    
    message.success('创建任务成功');
    emit('ok');
    visible.value = false;
  } catch (e: any) {
    console.error('Failed to create task:', e);
    message.error('创建任务失败: ' + (e.response?.data?.message || e.message || '未知错误'));
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss">
.create-task-modal {
  .ant-modal-content {
    padding: 0 !important;
    border-radius: 24px;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.4);
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  }
}
</style>

<style scoped lang="scss">
.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(37, 99, 235, 0.08) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.ic-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ic-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 8px 16px rgba(37, 99, 235, 0.2);
}

.ic-header-text {
  display: flex;
  flex-direction: column;
}

.ic-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
}

.ic-header-subtitle {
  font-size: 12px;
  color: #64748b;
}

.close-btn {
  border-radius: 10px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  transition: all 0.2s;
  
  &:hover { 
    background: rgba(0, 0, 0, 0.05); 
    color: #ef4444; 
  }
}

.ic-modal-body {
  padding: 0;
  background: #fff;
  max-height: 60vh;
  overflow-y: auto;
}

.ic-form-container {
  padding: 24px;
}

.section-container {
  margin-bottom: 24px;
  padding: 0;
  
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

// 商品选择触发器样式
.product-select-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 32px;
  padding: 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: #fff;
  
  &:hover {
    border-color: #3b82f6;
  }

  .placeholder-text {
    color: #bfbfbf;
    font-size: 14px;
  }

  .selected-products-summary {
    display: flex;
    align-items: center;
    gap: 8px;
    overflow: hidden;
    flex: 1;

    .product-count {
      background: #e6f7ff;
      color: #1890ff;
      font-size: 12px;
      padding: 1px 8px;
      border-radius: 10px;
      white-space: nowrap;
      font-weight: 600;
    }

    .product-names {
      color: #333;
      font-size: 13px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .trigger-icon {
    color: #999;
    font-size: 14px;
    margin-left: 8px;
    flex-shrink: 0;
  }
}

// 已选商品列表
.selected-products-list {
  margin-top: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;

  .products-mini-table {
    :deep(.ant-table-thead > tr > th) {
      background: #fafafa;
      font-size: 12px;
      padding: 8px 12px;
      color: #666;
    }
    :deep(.ant-table-tbody > tr > td) {
      padding: 8px 12px;
    }
  }
}

.ic-modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.cancel-btn {
  height: 40px;
  border-radius: 10px;
  font-weight: 600;
}

.create-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 10px;
  font-weight: 600;
  border: none;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
  }
}

.influencer-select-option {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
  line-height: 1.4;
  
  .inf-main-info {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 2px;
    
    .inf-handle {
      color: #3b82f6;
      font-weight: 600;
      font-size: 14px;
    }
    
    .inf-name {
      color: #1e293b;
      font-weight: 500;
      font-size: 14px;
    }
  }
  
  .inf-sub-info {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    
    .inf-email {
      color: #64748b;
      font-size: 12px;
      max-width: 200px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .inf-status-tag {
      font-size: 11px;
      padding: 0 6px;
      border-radius: 4px;
      font-weight: 500;
      border: 1px solid transparent;
      
      &.cooperating { background: #e0f2fe; color: #0369a1; border-color: #7dd3fc; }
      &.pending { background: #fff7ed; color: #c2410c; border-color: #fdba74; }
      &.dormant { background: #f8fafc; color: #64748b; border-color: #cbd5e1; }
    }
  }
}
</style>
