<template>
  <a-drawer
    v-model:open="visible"
    :title="null"
    width="60%"
    :closable="false"
    class="premium-refined-drawer influencer-detail-drawer"
    destroyOnClose
  >
    <!-- 自定义头部 (仿订单详情风格) -->
    <div class="ic-modal-header">
      <div class="ic-header-left">
        <div class="ic-header-icon">
          <UserOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">
            红人详情
            <span class="order-no-tags">
              <a-tag v-if="influencerData?.id || influencerData?.influencerId">
                ID: {{ fullInfluencerData.id || fullInfluencerData.influencerId || influencerData?.id }}
              </a-tag>
            </span>
          </div>
          <div class="ic-header-subtitle">
            全方位查看红人画像、合作历史与业务日志
          </div>
        </div>
      </div>
      <div class="ic-header-right"></div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 核心：外层容器强制100%高度，作为所有Tab的高度基准 -->
    <div class="modal-body-container" style="padding: 0;">
      <div class="root-tabs-container">
      <a-tabs 
        v-model:activeKey="activeTab" 
        tab-position="left"
        class="unified-height-tabs"
      >
        <!-- 基本信息Tab -->
        <a-tab-pane key="basic" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><InfoCircleOutlined /> 基本信息</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper scrollable-y basic-info-super-container">
              <Suspense>
                <div class="basic-info-layout">
                  <BasicInfoTab :influencer-data="fullInfluencerData" :default-address="defaultAddress" class="unified-child-component" @refresh="handleChildRefresh" />

                  <div class="embedded-section-title"><ShareAltOutlined /> 社媒信息 (Social Media)</div>
                  <div class="embedded-card">
                    <SocialMediaTab v-model:data="socialMediaList" :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="embedded-tab" @default-changed="refreshSocialMedias" />
                  </div>

                  <div class="embedded-section-title"><EnvironmentOutlined /> 收货地址 (Shipping Addresses)</div>
                  <div class="embedded-card">
                    <AddressTab v-model:data="addressList" :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="embedded-tab" @default-changed="refreshAddresses" />
                  </div>

                  <div class="embedded-section-title"><DollarOutlined /> 付费合作 (Paid Cooperation)</div>
                  <div class="embedded-card">
                    <CooperationTab :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="embedded-tab" />
                  </div>


                  <div class="embedded-section-title"><BankOutlined /> 打款信息 (Payment Info)</div>
                  <div class="embedded-card">
                    <PaymentTab :payments="paymentList" class="embedded-tab" />
                  </div>
                </div>
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 佣金配置Tab -->
        <a-tab-pane key="commission" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><CreditCardOutlined /> 佣金配置</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'commission'">
                <CommissionTab :influencer-data="fullInfluencerData" class="unified-child-component" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 社媒信息Tab (Hidden) -->
        <a-tab-pane v-if="false" key="social" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><ShareAltOutlined /> 社媒信息</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'social'">
                <SocialMediaTab v-model:data="socialMediaList" :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="unified-child-component" @default-changed="refreshSocialMedias" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 付费合作Tab (Hidden) -->
        <a-tab-pane v-if="false" key="cooperation" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><CreditCardOutlined /> 付费合作</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'cooperation'">
                <CooperationTab :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="unified-child-component" @refresh="handleChildRefresh" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 打款信息Tab (Hidden) -->
        <a-tab-pane v-if="false" key="payment" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><CreditCardOutlined /> 打款信息</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'payment'">
                <PaymentTab :payments="paymentList" class="unified-child-component" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 收款信息Tab -->
        <a-tab-pane key="paymentInfo" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><BankOutlined /> 收款信息</span>
          </template>
          <div class="unified-tab-content locked-height">
             <div class="unified-content-wrapper">
               <Suspense v-if="activeTab === 'paymentInfo'">
                 <PaymentInfoTab 
                   :influencer="fullInfluencerData"
                   class="unified-child-component" 
                 />
                 <template #fallback>
                   <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                 </template>
               </Suspense>
             </div>
          </div>
        </a-tab-pane>


        <!-- 收件地址Tab (Hidden) -->
        <a-tab-pane v-if="false" key="address" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><EnvironmentOutlined /> 收货地址</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'address'">
                <AddressTab v-model:data="addressList" :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId" class="unified-child-component" @default-changed="refreshAddresses" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>
        <!-- 沟通日志Tab -->
        <a-tab-pane key="communication" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><MessageOutlined /> 沟通日志</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'communication'">
                <CommunicationLogTab
                  :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId"
                  class="unified-child-component"
                />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 合作订单Tab -->
        <a-tab-pane key="coopOrders" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><ShopOutlined /> 合作订单</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'coopOrders'">
                <CoopOrdersTab
                  :influencer-id="fullInfluencerData.id || fullInfluencerData.influencerId"
                  class="unified-child-component"
                />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 日志记录Tab -->
        <a-tab-pane key="logs" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><HistoryOutlined /> 信息日志</span>
          </template>
            <div class="unified-tab-content locked-height">
              <div class="fixed-filter-bar compact-bar">
                <div class="filter-row">
                  <div class="filter-main-title">
                    <HistoryOutlined /> 日志筛选
                  </div>
                  <div class="filter-items-group">
                    <a-select
                      v-model:value="logFilterType"
                      allow-clear
                      placeholder="事件类型"
                      style="width: 130px;"
                      :options="logTypeOptions"
                    />
                    <a-select
                      v-model:value="logFilterOperator"
                      allow-clear
                      placeholder="负责人"
                      style="width: 120px;"
                      :options="logOperatorOptions"
                    />
                    <a-input
                      v-model:value="logFilterKeyword"
                      allow-clear
                      placeholder="搜索内容"
                      style="width: 150px;"
                    />
                    <a-range-picker
                      v-model:value="logFilterRange"
                      :placeholder="['开始时间', '结束时间']"
                      style="width: 230px;"
                      format="YYYY-MM-DD"
                      allow-clear
                    />
                    <a-button @click="resetLogFilters" class="reset-btn">重置</a-button>
                  </div>
                </div>
              </div>
              <div class="unified-content-wrapper">
                <Suspense v-if="activeTab === 'logs'">
                  <LogsTab :logs="filteredLogs" class="unified-child-component" />
                  <template #fallback>
                    <div class="unified-loading-wrapper">
                      <a-spin :spinning="true" />
                    </div>
                  </template>
                </Suspense>
              </div>
            </div>
        </a-tab-pane>
      </a-tabs>
      </div>
    </div>
    <div class="modal-fixed-footer">
      <a-button @click="handleCancel" class="btn-cancel">关闭</a-button>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { ref, watch, defineAsyncComponent, computed } from 'vue';
import { influencerService } from '@/services/influencerService';
import http from '@/utils/http';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { 
  UserOutlined,
  InfoCircleOutlined, 
  ShareAltOutlined, 
  CreditCardOutlined, 
  GiftOutlined, 
  EnvironmentOutlined, 
  HistoryOutlined,
  TagsOutlined,
  CloseOutlined,
  BankOutlined,
  MessageOutlined,
  ShopOutlined,
  DollarOutlined,
  InboxOutlined
} from '@ant-design/icons-vue';
// 类型定义
interface SocialMedia {
  platform: string;
  name: string;
  link: string;
  fans: number | undefined;
  levelTime: Dayjs | undefined;
  editing: boolean;
}
interface Address {
  recipientName: string;
  phone: string;
  email: string;
  postalCode: string;
  address: string;
  isDefault?: boolean;
  editing: boolean;
}
interface Log {
  type: string;
  time: string;
  content: string;
  operator?: string;
  details?: Record<string, any>;
}
interface PaymentRecord {
  paymentId: string;
  amount: string;
  status: string;
  time: string;
  applicant?: string;
  payer?: string;
}
interface SampleOrder {
  spu: string;
  sku: string;
  variantTitle?: string;
  image: string;
  quantity: number;
  price: number;
  orderNo?: string;
  packageNo?: string;
  status: string;
  orderTime?: string;
  shipTime?: string;
  deliveryTime?: string;
  creator?: string;
}

// Props & Emits
const props = withDefaults(defineProps<{
  open: boolean;
  influencerData: any;
  /** 是否显示打款信息 / 样品单信息等财务相关 Tab，资源池详情中可以关闭 */
  showFinanceTabs?: boolean;
}>(), {
  showFinanceTabs: true,
});
const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'refresh'): void;
}>();

// 懒加载子组件
const BasicInfoTab = defineAsyncComponent(() => import('./tabs/BasicInfoTab.vue'));
const CommissionTab = defineAsyncComponent(() => import('./tabs/CommissionTab.vue'));
const SocialMediaTab = defineAsyncComponent(() => import('./tabs/SocialMediaTab.vue'));
const PaymentTab = defineAsyncComponent(() => import('./tabs/PaymentTab.vue'));
const SampleTab = defineAsyncComponent(() => import('./tabs/SampleTab.vue'));
const AddressTab = defineAsyncComponent(() => import('./tabs/AddressTab.vue'));
const PaymentInfoTab = defineAsyncComponent(() => import('./tabs/PaymentInfoTab.vue'));
const LogsTab = defineAsyncComponent(() => import('./tabs/LogsTab.vue'));
const CooperationTab = defineAsyncComponent(() => import('./tabs/CooperationTab.vue'));
const CommunicationLogTab = defineAsyncComponent(() => import('./tabs/CommunicationLogTab.vue'));
const CoopOrdersTab = defineAsyncComponent(() => import('./tabs/CoopOrdersTab.vue'));

// 响应式数据
const visible = ref(false);
const activeTab = ref('basic');
const socialMediaList = ref<any[]>([]);
const addressList = ref<Address[]>([]);
const logsList = ref<Log[]>([]);
const paymentList = ref<PaymentRecord[]>([]);
const sampleOrderList = ref<SampleOrder[]>([]);
const fullInfluencerData = ref<any>({});

// 计算默认地址
const defaultAddress = computed(() => {
  return addressList.value.find(addr => addr.isDefault) || addressList.value[0] || null;
});


const logFilterType = ref<string | undefined>();
const logFilterOperator = ref<string | undefined>();
const logFilterKeyword = ref<string>('');
const logFilterRange = ref<[Dayjs, Dayjs] | null>(null);

const logTypeOptions = computed(() => {
  const typeMap: Record<string, string> = {
    created: '创建',
    sample: '样品单',
    commission: '分佣发起',
    settlement: '结算',
    address: '收件地址',
    social: '社媒变更',
    basic: '基本信息',
    status_change: '状态流转',
  };
  const unique = Array.from(new Set(logsList.value.map((l) => l.type)));
  return unique.map((t) => ({ 
    label: typeMap[t] || t, 
    value: t 
  }));
});

const logOperatorOptions = computed(() => {
  const unique = Array.from(new Set(logsList.value.map((l) => (l.operator || '').trim()).filter(Boolean)));
  return unique.map((o) => ({ label: o, value: o }));
});

const filteredLogs = computed(() => {
  return logsList.value.filter((log) => {
    const matchType = logFilterType.value ? log.type === logFilterType.value : true;
    const matchOp = logFilterOperator.value ? (log.operator || '') === logFilterOperator.value : true;
    const matchKeyword = logFilterKeyword.value
      ? (log.content || '').toLowerCase().includes(logFilterKeyword.value.toLowerCase())
      : true;
    const matchRange = logFilterRange.value
      ? dayjs(log.time).isAfter(logFilterRange.value[0]) && dayjs(log.time).isBefore(logFilterRange.value[1])
      : true;
    return matchType && matchOp && matchKeyword && matchRange;
  });
});

const resetLogFilters = () => {
  logFilterType.value = undefined;
  logFilterOperator.value = undefined;
  logFilterKeyword.value = '';
  logFilterRange.value = null;
};

// 重置所有状态
const resetState = () => {
  socialMediaList.value = [];
  addressList.value = [];
  logsList.value = [];
  paymentList.value = [];
  sampleOrderList.value = [];
  fullInfluencerData.value = {};
  activeTab.value = 'basic';
};

// 监听弹窗显示/隐藏
watch(() => props.open, (val) => {
  visible.value = val;
  if (val) {
    resetState(); // Clear old data first
    loadInfluencerData();
  }
});

// 监听红人数据变化 - 切换红人时重新加载
watch(() => props.influencerData?.id, (newId, oldId) => {
  if (newId && newId !== oldId && visible.value) {
    resetState();
    loadInfluencerData();
  }
});

watch(visible, (val) => emit('update:open', val));

// Handle child component refresh - reload data and notify parent
const handleChildRefresh = async () => {
  await loadInfluencerData();
  emit('refresh'); // Notify parent list to refresh
};

// 加载数据
const loadInfluencerData = async () => {
  const userId = props.influencerData?.id;
  
  // Initialize from props
  fullInfluencerData.value = { ...props.influencerData };

  if (userId) {
      try {
          const detail = await influencerService.getDetail(userId) as any;
          if (detail) {
              fullInfluencerData.value = { ...fullInfluencerData.value, ...detail };
              
              // Map address fields to addressInfo object for the tab
              const addr = {
                  recipientName: detail.realName || detail.name,
                  phone: detail.phone || detail.mobile || '',
                  email: detail.email || '',
                  address: [detail.street1, detail.street2, detail.city, detail.state, detail.country].filter(Boolean).join(', '),
                  postalCode: detail.zip || '',
                  isDefault: true,
                  editing: false
              };
              // Only populate if there is meaningful data
              if (addr.recipientName || addr.address || addr.phone || addr.email) {
                  fullInfluencerData.value.addressInfo = addr;
              }
          }
      } catch (e) {
          console.error("Fetch detail failed", e);
      }
  }

  // Use fetched data for social media
  socialMediaList.value = fullInfluencerData.value.socialMediaList || [];
  
  // Load addresses from the actual influencer_address table via API
  const influencerId = fullInfluencerData.value.id || fullInfluencerData.value.influencerId;
  if (influencerId) {
    try {
      const addresses = await influencerService.getAddresses(influencerId) as unknown as Address[];
      addressList.value = (addresses || []).map((a: any) => ({ ...a, editing: false }));
    } catch (e) {
      console.error('Failed to fetch addresses:', e);
      addressList.value = [];
    }
    
    try {
      const socialMedias = await influencerService.getSocialMedias(influencerId) as unknown as any[];
      socialMediaList.value = (socialMedias || []).map((s: any) => ({ ...s, editing: false }));
    } catch (e) {
      console.error('Failed to fetch social medias:', e);
    }
  } else {
    addressList.value = [];
  }
  
  // 从真实 API 获取日志
  if (influencerId) {
    try {
      const logsResponse = await influencerService.getLogs(influencerId);
      const logsData = Array.isArray(logsResponse) ? logsResponse : (logsResponse as any)?.data || [];
      const realLogs = logsData.map((log: any) => ({
        type: log.fieldName === '创建' ? 'created' : 'basic',
        time: log.createdAt || '',
        content: log.remark || `${log.fieldName} 变更`,
        operator: log.operator || 'SYS',
        details: log.oldValue || log.newValue ? {
          '变更项': log.fieldName,
          '数值': { old: log.oldValue || '-', new: log.newValue || '-' }
        } : {}
      }));
      logsList.value = realLogs;
    } catch (e) {
      console.error('Failed to fetch logs:', e);
      logsList.value = [];
    }
  } else {
    logsList.value = [];
  }
  // 从真实 API 获取打款记录
  if (influencerId) {
    try {
      const payoutsRes = await influencerService.getPayouts({ influencerId });
      if (payoutsRes && payoutsRes.success && payoutsRes.data) {
        const payouts = payoutsRes.data;
        paymentList.value = payouts.map((p: any) => ({
          paymentId: p.paymentId || `#PAY${String(p.id).padStart(9, '0')}`,
          amount: `$${parseFloat(p.amount || 0).toFixed(2)}`,
          status: p.status === 'pending' ? '待审核' : p.status === 'approved' ? '待打款' : p.status === 'completed' ? '已打款' : p.status === 'rejected' ? '打款失败' : p.status,
          time: p.createdAt || '-',
          paymentType: p.paymentType || '-',
          orderNo: p.orderNo || '-',
          remark: p.remark || '-',
          auditRemark: p.auditRemark || '-',
          paymentRemark: p.paymentRemark || '-',
          applicant: p.creatorName || p.applicantName || '-',
          creator: p.creatorName || '-',
          auditor: p.auditorName || '-',
          auditTime: p.auditTime || '-',
          payer: p.payerName || '-',
          payTime: p.payTime || '-'
        }));
      } else {
        paymentList.value = [];
      }
    } catch (e) {
      console.error('Failed to fetch payouts:', e);
      paymentList.value = [];
    }
  } else {
    paymentList.value = [];
  }
  

};

// 刷新地址列表 - 当子组件改变默认地址时调用
const refreshAddresses = async () => {
  const influencerId = fullInfluencerData.value.id || fullInfluencerData.value.influencerId;
  if (influencerId) {
    try {
      const addresses = await influencerService.getAddresses(influencerId) as unknown as Address[];
      addressList.value = (addresses || []).map((a: any) => ({ ...a, editing: false }));
    } catch (e) {
      console.error('Failed to refresh addresses:', e);
    }
  }
};

// 刷新社媒列表 - 当子组件改变默认社媒时调用
const refreshSocialMedias = async () => {
  const influencerId = fullInfluencerData.value.id || fullInfluencerData.value.influencerId;
  if (influencerId) {
    try {
      const socialMedias = await influencerService.getSocialMedias(influencerId) as unknown as any[];
      socialMediaList.value = (socialMedias || []).map((s: any) => ({ ...s, editing: false }));
    } catch (e) {
      console.error('Failed to refresh social medias:', e);
    }
  }
};

// 辅助函数：根据订单状态返回显示状态
const getOrderStatus = (order: any): string => {
  if (order.cancelledAt) return '已取消';
  if (order.financialStatus === 'refunded') return '已退款';
  if (order.fulfillmentStatus === 'fulfilled') return '已签收';
  if (order.fulfillmentStatus === 'partial') return '运输中';
  if (order.financialStatus === 'paid') return '待发货';
  return '待处理';
};

// 关闭弹窗
const handleCancel = () => visible.value = false;
</script>

<style lang="scss">
.influencer-detail-drawer {
  .ant-drawer-body {
    padding: 0 !important;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    background: #f4f6f8;
  }
  .ant-drawer-content {
    border-top-left-radius: 20px;
    border-bottom-left-radius: 20px;
    overflow: hidden;
  }
}

.influencer-detail-modal-wrap {
  display: flex;
  align-items: center;
  justify-content: center;

  .ant-modal {
    width: 1000px !important;
    height: 680px !important;
    max-height: 90vh !important;
    top: 0 !important;
    padding-bottom: 0 !important;
    margin: 0 !important;
    
    .ant-modal-content {
      height: 100% !important;
      padding: 0 !important;
      border-radius: 24px;
      overflow: hidden !important;
      display: flex;
      flex-direction: column;
      @supports (backdrop-filter: blur(20px)) {
        background: rgba(255, 255, 255, 0.85);
        backdrop-filter: blur(20px);
      }
    }

    .ant-modal-header {
      padding: 0 !important;
      margin: 0 !important;
      border-bottom: none;
      background: transparent;
      flex-shrink: 0;
    }

    .ant-modal-close {
      display: none;
    }
  }
}
</style>

<style lang="scss" scoped>
/* CSS Reset for Modal Header to match Export Modal */
/* CSS Reset for Modal Header to match Create Modal */
.glass-header-compact {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(37, 99, 235, 0.08) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
}

.header-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-box {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  box-shadow: 0 4px 10px rgba(37, 99, 235, 0.2);
}

.title-meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.main-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
}

.simple-subtitle {
  font-size: 13px;
  color: #64748b;
  margin-top: 2px;
}

.header-close-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #94a3b8;
  border-radius: 8px;
  transition: all 0.3s;
  font-size: 16px;

  &:hover {
    background: rgba(0, 0, 0, 0.05);
    color: #64748b;
  }
}

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

.ic-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-no-tags {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  
  .ant-tag {
    margin: 0;
    font-weight: normal;
    font-size: 12px;
    word-break: break-all;
    white-space: normal;
    height: auto;
    padding: 2px 7px;
    line-height: 1.5;
  }
}

.header-id-tag {
  margin: 0;
  font-weight: normal;
  font-size: 12px;
}

.ic-header-subtitle {
  font-size: 12px;
  color: #64748b;
}

.ic-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-right: 40px;
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

.detail-modal-header { display: none; }

.root-tabs-container {
  height: 100%;
  background: transparent;
}

.unified-height-tabs {
  height: 100% !important;
  
  :deep(.ant-tabs-nav) {
    width: 160px !important;
    background: rgba(248, 250, 252, 0.5);
    border-right: 1px solid #f1f5f9;
    padding: 16px 8px !important;
    height: 100% !important;

    .ant-tabs-nav-list {
      gap: 4px;
    }

    .ant-tabs-tab {
      margin: 0 !important;
      padding: 10px 16px !important;
      border-radius: 10px !important;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border: 1px solid transparent;

      .ant-tabs-tab-btn {
        width: 100%;
        .tab-label-box {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 13px;
          font-weight: 600;
          color: #64748b;
          transition: all 0.2s;
          .anticon { font-size: 15px; opacity: 0.7; }
        }
      }

      &:hover {
        background: rgba(255, 255, 255, 0.6);
        .ant-tabs-tab-btn .tab-label-box { color: #334155; }
      }

      &.ant-tabs-tab-active {
        background: #fff;
        border-color: #e2e8f0;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

        .ant-tabs-tab-btn .tab-label-box {
          color: #4f46e5;
          font-weight: 700;
          .anticon { opacity: 1; }
        }
      }
    }

    .ant-tabs-ink-bar { display: none; }

    /* 强制隐藏更多按钮 (三个点)，并确保所有 Tab 可见 */
    .ant-tabs-nav-operations {
      display: none !important;
    }
    .ant-tabs-nav-wrap {
      overflow: visible !important;
    }
    .ant-tabs-nav-list {
      transform: none !important;
    }
  }

  :deep(.ant-tabs-content-holder) {
    background: #fff;
    height: 100% !important;
    overflow: hidden;
  }
  
  :deep(.ant-tabs-content) {
    height: 100% !important;
  }
  
  :deep(.ant-tabs-tabpane) {
    height: 100% !important;
    display: flex;
    flex-direction: column;
  }
}

.unified-tab-content {
  padding: 8px !important;
  height: 100% !important;
  min-height: 100% !important;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;

  &.locked-height {
    height: 580px !important; /* 恢复用户喜欢的高度，微调减少空挡 */
  }
}

.unified-content-wrapper {
  flex: 1;
  height: 0; 
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden !important; /* Force children to handle scroll */
  
  &.scrollable-y {
    overflow-y: auto;
    padding-right: 6px;
    
    &::-webkit-scrollbar { width: 4px; }
    &::-webkit-scrollbar-track { background: transparent; }
    &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
  }
}

.fixed-filter-bar {
  background: rgba(248, 250, 252, 0.65) !important;
  backdrop-filter: blur(12px) !important;
  padding: 12px 16px !important;
  border-radius: 12px;
  border: 1px solid rgba(226, 232, 240, 0.8) !important;
  margin-bottom: 12px !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);

  .filter-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
  }

  .filter-main-title {
    font-size: 13px;
    font-weight: 700;
    color: #475569;
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: 6px;
    &::before {
      content: '';
      width: 3px;
      height: 12px;
      background: #6366f1;
      border-radius: 4px;
    }
  }

  .filter-items-group {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: nowrap;
  }

  .reset-btn {
    border-radius: 6px !important;
    height: 32px;
    font-size: 12px;
    font-weight: 600;
    color: #64748b;
    border-color: #e2e8f0;
    transition: all 0.2s;
    &:hover {
      color: #6366f1;
      border-color: #6366f1;
      background: #f5f3ff;
    }
  }

  :deep(.ant-input), :deep(.ant-select-selector), :deep(.ant-picker), :deep(.ant-input-affix-wrapper) {
    border-radius: 6px !important;
    border-color: #e2e8f0 !important;
    height: 32px !important;
    font-size: 13px;
    background-color: #fff !important;
    &:hover { border-color: #6366f1 !important; }
  }

  // 修复带有图标/清除按钮的输入框内部重复背景及高度对齐
  :deep(.ant-input-affix-wrapper) {
    padding-top: 0 !important;
    padding-bottom: 0 !important;
    display: inline-flex;
    align-items: center;

    .ant-input {
      background: transparent !important;
      border: none !important;
      box-shadow: none !important;
      &:focus { box-shadow: none !important; }
    }
  }
}

.basic-info-super-container {
  overflow-y: auto !important;
  padding-right: 12px;
  
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 6px; }
}

.basic-info-layout {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.embedded-section-title {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
  margin-top: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  
  .anticon {
    color: #4f46e5;
  }
}

.embedded-card {
  background: #f8fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  padding: 8px;
  min-height: 0;
  display: flex;
  flex-direction: column;
  
  :deep(.embedded-tab) {
    height: 100%;
    
    .section-header { margin-bottom: 12px; }
    
    .table-container, .discount-main, .commission-main {
      border: none !important;
      background: transparent !important;
    }
  }
}

.placeholder-section {
  color: #94a3b8;
  font-size: 13px;
  padding: 16px;
  text-align: center;
}

</style>
