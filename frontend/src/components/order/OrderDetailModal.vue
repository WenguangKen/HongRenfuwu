<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="1200px"
    :footer="null"
    centered
    :draggable="false"
    wrap-class-name="order-detail-modal-wrap"
    @cancel="handleCancel"
    class="order-detail-modal"
    destroyOnClose
    :closable="false"
  >
    <!-- 自定义头部 (仿导出弹窗风格) -->
    <div class="ic-modal-header">
      <div class="ic-header-left">
        <div class="ic-header-icon">
          <ShoppingOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">
            订单详情
            <span class="order-no-tags" v-if="orderData">
              <!-- Regular Order -->
              <template v-if="!orderData.isSplit">
                <a-tag v-if="orderData.shortOrderNo">
                  短号：{{ orderData.shortOrderNo }}
                </a-tag>
                <a-tag color="blue" v-if="orderData.longOrderNo || orderData.orderNo">
                  订单号：{{ (orderData.longOrderNo || orderData.orderNo || '').replace('gid://shopify/Order/', '') }}
                </a-tag>
              </template>
              
              <!-- Split Order -->
              <template v-else>
                 <!-- Split Part (F1, F2...) -->
                 <template v-if="orderData.fulfillmentIds">
                   <a-tag color="purple" class="split-tag">
                     {{ orderData.orderNo }} [ {{ orderData.fulfillmentIds.split(',').map((id: string) => id.includes('/') ? id.split('/').pop() : id).join(',') }} ]
                   </a-tag>
                 </template>
                 <!-- Split Main Order (Remainder) -->
                 <template v-else>
                   <a-tag color="blue">
                     {{ orderData.orderNo }} [ 主 ]
                   </a-tag>
                   <a-tag v-if="orderData.longOrderNo">
                     ID: {{ orderData.longOrderNo.replace('gid://shopify/Order/', '') }}
                   </a-tag>
                 </template>
              </template>
            </span>
          </div>
          <div class="ic-header-subtitle">
            查看订单的基本信息、商品明细和操作日志
          </div>
        </div>
      </div>
      <div class="ic-header-right"></div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 核心：外层容器强制100%高度，作为所有Tab的高度基准 -->
    <div class="root-tabs-container">
      <a-tabs 
        v-model:activeKey="activeTab" 
        tab-position="left"
        class="unified-height-tabs"
      >
        <!-- 订单详情Tab -->
        <a-tab-pane key="detail" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><FileTextOutlined /> 订单信息</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense>
                <OrderDetailTab :order-data="orderData" class="unified-child-component" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 商品信息Tab -->
        <a-tab-pane v-if="!isOrderCancelled" key="products" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><ShoppingOutlined /> 商品信息</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper scrollable-y">
              <Suspense v-if="activeTab === 'products'">
                <OrderProductsTab :order-data="orderData" class="unified-child-component" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>

        <!-- 金额变动Tab -->
        <a-tab-pane key="financial" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><PayCircleOutlined /> 金额变动</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper scrollable-y">
              <!-- 移除Suspense以修复加载卡死问题 -->
              <OrderFinancialTab v-if="activeTab === 'financial'" :order-data="orderData" class="unified-child-component" />
            </div>
          </div>
        </a-tab-pane>

        <!-- 订单关系Tab（仅拆单订单显示） -->
        <a-tab-pane v-if="orderData?.isSplit" key="relation" class="unified-tab-pane">
          <template #tab>
            <span class="tab-label-box"><ApartmentOutlined /> 订单关系</span>
          </template>
          <div class="unified-tab-content locked-height">
            <div class="unified-content-wrapper">
              <Suspense v-if="activeTab === 'relation'">
                <OrderRelationTab 
                  :order-data="orderData" 
                  class="unified-child-component"
                  @view-order="handleViewRelatedOrder"
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
            <span class="tab-label-box"><HistoryOutlined /> 日志记录</span>
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
            <div class="unified-content-wrapper scrollable-y">
              <Suspense v-if="activeTab === 'logs'">
                <OrderLogsTab :logs="filteredLogs" class="unified-child-component" />
                <template #fallback>
                  <div class="unified-loading-wrapper"><a-spin :spinning="true" /></div>
                </template>
              </Suspense>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, defineAsyncComponent, computed } from 'vue';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import { getOrderDetails } from '@/services/influencerOrderService';
import { 
  PayCircleOutlined,
  ShoppingOutlined, 
  FileTextOutlined, 
  ApartmentOutlined, 
  HistoryOutlined,
  CloseOutlined
} from '@ant-design/icons-vue';

// 类型定义
interface Log {
  type: string;
  time: string;
  content: string;
  operator?: string;
  details?: Record<string, any>;
}

// Props & Emits
const props = withDefaults(defineProps<{
  open: boolean;
  orderData: any;
  orderType?: 'sample' | 'conversion' | 'commission';
}>(), {
  orderType: 'sample',
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'view-related-order', order: any): void;
}>();

const isOrderCancelled = computed(() => {
  if (!props.orderData) return false;
  const status = String(props.orderData.orderStatus || props.orderData.status || '').toLowerCase();
  return status === 'cancelled' || status === '已取消';
});

// 懒加载子组件
const OrderDetailTab = defineAsyncComponent(() => import('./tabs/OrderDetailTab.vue'));
const OrderProductsTab = defineAsyncComponent(() => import('./tabs/OrderProductsTab.vue'));
const OrderFinancialTab = defineAsyncComponent(() => import('./tabs/OrderFinancialTab.vue'));
const OrderRelationTab = defineAsyncComponent(() => import('./tabs/OrderRelationTab.vue'));
const OrderLogsTab = defineAsyncComponent(() => import('./tabs/OrderLogsTab.vue'));

// 响应式数据
const visible = ref(false);
const activeTab = ref('detail');
const logsList = ref<Log[]>([]);

const logFilterType = ref<string | undefined>();
const logFilterOperator = ref<string | undefined>();
const logFilterKeyword = ref<string>('');
const logFilterRange = ref<[Dayjs, Dayjs] | null>(null);

const logTypeOptions = computed(() => {
  const unique = Array.from(new Set(logsList.value.map((l) => l.type)));
  return unique.map((t) => ({ label: getLogTypeText(t), value: t }));
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

const getLogTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    created: '创建',
    payment: '付款',
    shipped: '发货',
    delivered: '妥投',
    completed: '完成',
    cancelled: '取消',
    refund: '退款',
    split: '拆单',
    commission: '分佣',
    status_change: '状态变更',
  };
  return textMap[type] || type;
};

// 监听弹窗显示/隐藏
watch(() => props.open, (val) => {
  visible.value = val;
  if (val) loadOrderData();
});
watch(visible, (val) => emit('update:open', val));

// 加载数据
const loadOrderData = async () => {
  if (!props.orderData) return;
  
  // 基础映射：确保列表中的字段能对应到详情需要的字段
  if (props.orderData.recipientName || props.orderData.receiverName) {
    props.orderData.shippingName = props.orderData.shippingName || props.orderData.recipientName || props.orderData.receiverName;
  }
  if (props.orderData.recipientPhone) {
    props.orderData.shippingPhone = props.orderData.shippingPhone || props.orderData.recipientPhone;
  }
  if (props.orderData.recipientAddress) {
    props.orderData.shippingAddress = props.orderData.shippingAddress || props.orderData.recipientAddress;
  }
  if (props.orderData.influencer && !props.orderData.influencerName) {
    props.orderData.influencerName = props.orderData.influencer;
  }

  // 基础映射：时间节点
  if (props.orderData.timeInfo) {
    const ti = props.orderData.timeInfo;
    props.orderData.createdAt = props.orderData.createdAt || ti.createTime || ti.createdAt;
    props.orderData.orderCreatedAt = props.orderData.orderCreatedAt || ti.createTime;
    props.orderData.paidAt = props.orderData.paidAt || ti.paymentTime;
    props.orderData.shippedAt = props.orderData.shippedAt || ti.shipTime;
    props.orderData.deliveredAt = props.orderData.deliveredAt || ti.deliveryTime;
  }

  // 如果orderData中有id，则调用API获取完整详情
  if (props.orderData?.id) {
    try {
      const details = await getOrderDetails(props.orderData.id, props.orderType);
      // 安全地合并数据，避免用空值或 '-' 覆盖已有数据
      const isValid = (val: any) => val !== null && val !== undefined && val !== '' && val !== '-' && val !== 'null';
      
      Object.assign(props.orderData, {
        // 基本信息
        orderName: details.orderName || props.orderData.orderName,
        storeName: details.storeName || props.orderData.storeName,
        
        // 收货人信息
        shippingName: isValid(details.shippingName) ? details.shippingName : props.orderData.shippingName,
        shippingPhone: isValid(details.shippingPhone) ? details.shippingPhone : props.orderData.shippingPhone,
        shippingAddress: isValid(details.shippingAddress) ? details.shippingAddress : props.orderData.shippingAddress,
        customerName: isValid(details.customerName) ? details.customerName : props.orderData.customerName,
        customerEmail: isValid(details.customerEmail) ? details.customerEmail : props.orderData.customerEmail,
        
        // 物流信息
        trackingNumber: isValid(details.trackingNumber) ? details.trackingNumber : (isValid(props.orderData.trackingNumber) ? props.orderData.trackingNumber : (isValid(props.orderData.trackingNo) ? props.orderData.trackingNo : props.orderData.packageNo)),
        trackingCompany: isValid(details.trackingCompany) ? details.trackingCompany : (isValid(props.orderData.trackingCompany) ? props.orderData.trackingCompany : props.orderData.logisticsName),
        trackingUrl: isValid(details.trackingUrl) ? details.trackingUrl : props.orderData.trackingUrl,
        
        // 时间节点
        orderCreatedAt: details.createdAt || props.orderData.orderCreatedAt || props.orderData.timeInfo?.createTime,
        createdAt: details.createdAt || props.orderData.createdAt || props.orderData.timeInfo?.createTime || props.orderData.timeInfo?.createdAt,
        paidAt: details.paidAt || props.orderData.paidAt || props.orderData.timeInfo?.paymentTime,
        shippedAt: details.shippedAt || props.orderData.shippedAt || props.orderData.timeInfo?.shipTime,
        deliveredAt: details.deliveredAt || props.orderData.deliveredAt || props.orderData.timeInfo?.deliveryTime,
        cancelledAt: details.cancelledAt || props.orderData.cancelledAt,
        updatedAt: details.updatedAt || props.orderData.updatedAt,
        
        // 商品列表
        products: (details.products || []).map((dp: any) => {
          // 尝试从列表数据中找到对应的项以补全图片等信息
          const lp = (props.orderData.products || []).find((p: any) => p.sku === dp.sku);
          return {
            ...lp,
            ...dp,
            image: dp.image || lp?.image || lp?.imageUrl
          };
        }),
        
        // 财务/交易
        totalShipping: details.totalShipping ?? props.orderData.totalShipping ?? props.orderData.shippingAmount,
        payments: details.payments || props.orderData.payments,
        refunds: details.refunds || props.orderData.refunds,
        discount: details.discount || props.orderData.discount,
        logs: details.logs || props.orderData.logs
      });
      logsList.value = details.logs || props.orderData.logs || generateMockLogs();
    } catch (error) {
      console.error('Failed to load order details:', error);
      logsList.value = props.orderData.logs || generateMockLogs();
    }
  } else {
    logsList.value = props.orderData.logs || generateMockLogs();
  }
};


// 模拟日志数据生成
const generateMockLogs = (): Log[] => {
  const operators = ['管理员A', '管理员B', '系统', '仓库管理员'];
  const logs: Log[] = [];
  
  if (props.orderData) {
    const createTime = props.orderData.timeInfo?.createTime || props.orderData.createTime || dayjs().format('YYYY-MM-DD HH:mm:ss');
    logs.push({
      type: 'created',
      time: createTime,
      content: '订单创建',
      operator: '系统',
      details: { '订单号': props.orderData.shortOrderNo || props.orderData.orderNo, '订单类型': props.orderType === 'sample' ? '样品订单' : (props.orderType === 'conversion' ? '转化订单' : '分佣订单') },
    });

    // 根据订单状态生成对应日志
    if (props.orderData.orderStatus === '已发货' || props.orderData.logisticsStatus === '已发货') {
      logs.push({
        type: 'shipped',
        time: props.orderData.timeInfo?.shipTime || dayjs(createTime).add(1, 'day').format('YYYY-MM-DD HH:mm:ss'),
        content: '订单已发货',
        operator: '仓库管理员',
        details: { '物流单号': props.orderData.trackingNo || '-', '发货时间': props.orderData.timeInfo?.shipTime || '-' },
      });
    }

    if (props.orderData.orderStatus === '已妥投' || props.orderData.logisticsStatus === '已妥投') {
      logs.push({
        type: 'delivered',
        time: props.orderData.timeInfo?.deliveredTime || dayjs(createTime).add(3, 'day').format('YYYY-MM-DD HH:mm:ss'),
        content: '订单已妥投',
        operator: '系统',
      });
    }

    // 如果有包裹号，添加拆单日志
    if (props.orderData.packageNo) {
      logs.push({
        type: 'split',
        time: dayjs(createTime).add(0.5, 'day').format('YYYY-MM-DD HH:mm:ss'),
        content: '订单拆单',
        operator: '系统',
        details: { '包裹号': props.orderData.packageNo, '拆单标识': '是' },
      });
    }
  }

  // 生成一些通用日志
  for (let i = 1; i <= 10; i++) {
    const logTypes = [
      { type: 'status_change', content: '订单状态变更', detailKeys: ['原状态', '新状态'] },
      { type: 'payment', content: '订单付款', detailKeys: ['付款金额', '付款方式'] },
      { type: 'refund', content: '订单退款', detailKeys: ['退款金额', '退款原因'] },
    ];
    const logType = logTypes[Math.floor(Math.random() * logTypes.length)]!;
    const date = dayjs().subtract(i, 'hour');
    const operator = operators[Math.floor(Math.random() * operators.length)];
    const details: Record<string, any> = {};

    if (logType.type === 'status_change') {
      details['原状态'] = '待处理';
      details['新状态'] = '已发货';
    } else if (logType.type === 'payment') {
      details['付款金额'] = `$${(Math.random() * 100).toFixed(2)}`;
      details['付款方式'] = 'PayPal';
    } else if (logType.type === 'refund') {
      details['退款金额'] = `$${(Math.random() * 50).toFixed(2)}`;
      details['退款原因'] = '用户申请';
      // 随机决定是否关联商品
      if (Math.random() > 0.5) {
        details['商品'] = `SKU-${Math.floor(Math.random() * 1000)} x${Math.floor(Math.random() * 3) + 1}`;
      } else {
        details['商品'] = '仅退款';
      }
    }

    logs.push({ type: logType.type, time: date.format('YYYY-MM-DD HH:mm:ss'), content: logType.content, operator, details });
  }

  return logs.sort((a, b) => new Date(b.time).getTime() - new Date(a.time).getTime());
};

// 关闭弹窗
const handleCancel = () => visible.value = false;

// 查看关联订单详情
const handleViewRelatedOrder = (order: any) => {
  // 通知父组件切换到新的订单
  emit('view-related-order', order);
};
</script>

<style lang="scss">
// 统一模态框样式
.order-detail-modal-wrap {
  .ant-modal-wrap {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 100vh;
  }
  
  .ant-modal {
    top: unset !important;
    transform: unset !important;
    margin: 0 auto;
    padding: 0 !important;
    width: 1200px !important;
    height: 760px !important;
    max-height: 90vh !important;
    overflow: hidden !important;
    border-radius: 16px;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  }
  
  .ant-modal-content {
    height: 100% !important;
    display: flex;
    flex-direction: column;
    overflow: hidden !important;
    padding: 0 !important;
    border-radius: 16px;
    background: #fff;
  }
  

}
</style>

<style lang="scss" scoped>
/* 头部样式 - 仿照InfluencerDetailModal */
.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(37, 99, 235, 0.08) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  flex-shrink: 0;
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
  gap: 0;
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

/* Tabs 容器样式 */
.root-tabs-container {
  height: 100%;
  background: transparent;
}

.unified-height-tabs {
  height: 100% !important;
  
  .ant-tabs-nav {
    width: 160px !important;
    background: #f8fafc;
    border-right: 1px solid #f1f5f9;
    padding: 16px 0 16px 8px !important;
    height: 100% !important;

    .ant-tabs-nav-list {
      gap: 4px;
      transform: none !important; /* 防止位移 */
    }

    /* 隐藏更多按钮 */
    .ant-tabs-nav-operations {
      display: none !important;
    }
    .ant-tabs-nav-wrap {
      overflow: visible !important;
    }

    .ant-tabs-tab {
      margin: 0 !important;
      padding: 10px 16px !important;
      border-radius: 10px 0 0 10px !important;
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
  }

  :deep(.ant-tabs-content-holder) {
    background: #fff;
    height: 100% !important;
    overflow: hidden;
    padding-left: 0 !important; /* 强制去除左侧内边距 */
    border-left: none !important;
  }
  
  :deep(.ant-tabs-content) {
    height: 100% !important;
    padding-left: 0 !important; /* 确保内容区域无左侧内边距 */
  }
  
  :deep(.ant-tabs-tabpane) {
    height: 100% !important;
    display: flex;
    flex-direction: column;
    padding-left: 8px !important; /* 强制左侧间距为 8px */
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
    height: 640px !important; 
  }
}

.unified-content-wrapper {
  flex: 1;
  height: 0; 
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden !important;
  
  &.scrollable-y {
    overflow-y: auto !important;
    overflow-x: hidden !important;
    padding-right: 8px;
    
    &::-webkit-scrollbar { 
      width: 6px; 
    }
    &::-webkit-scrollbar-track { 
      background: #f1f5f9; 
      border-radius: 3px; 
    }
    &::-webkit-scrollbar-thumb { 
      background: #cbd5e1; 
      border-radius: 3px; 
    }
    &::-webkit-scrollbar-thumb:hover { 
      background: #94a3b8; 
    }
  }
}

.unified-child-component {
  width: 100%;
  height: auto;
  /* 移除 min-height: 100% 以允许内容正常滚动 */
}

.unified-loading-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 筛选栏样式 */
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
    
    .ant-input {
      height: 32px !important;
      line-height: 32px;
    }
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
</style>