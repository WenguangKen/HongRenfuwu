<template>
  <div class="commission-tab">
    <div class="section-header">
      <div class="header-tips">管理红人独立佣金配置</div>
      <a-button @click="openEditModal" class="premium-edit-btn">
        <template #icon><EditOutlined /></template>
        编辑佣金
      </a-button>
    </div>

    <!-- 编辑弹窗 - 重构版 -->
    <a-modal
      v-model:open="editing"
      :width="520"
      class="commission-config-modal"
      :footer="null"
      centered
      :closable="false"
      destroyOnClose
    >
      <!-- 简洁头部 -->
      <div class="config-modal-header">
        <div class="header-left">
          <div class="header-icon">
            <PercentageOutlined />
          </div>
          <div class="header-text">
            <div class="header-title">佣金配置</div>
            <div class="header-subtitle">设置红人的独立基础佣金率</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="editing = false">
          <CloseOutlined />
        </a-button>
      </div>
      
      <!-- 主体内容 -->
      <div class="config-modal-body">
        <!-- 提示信息 -->
        <div class="info-alert">
          <div class="alert-icon">
            <InfoCircleOutlined />
          </div>
          <div class="alert-content">
            <div class="alert-title">规则说明</div>
            <div class="alert-desc">此佣金率仅在订单未绑定特定折扣码时生效，绑定折扣码时使用折扣码配置的佣金率</div>
          </div>
        </div>
        
        <!-- 佣金率输入区域 -->
        <div class="rate-config-section">
          <div class="rate-label">基础佣金率</div>
          <div class="rate-input-container">
            <a-input-number
              v-model:value="formState.commissionRate"
              :min="0"
              :max="100"
              :precision="2"
              :step="0.5"
              class="rate-input-field"
              placeholder="0.00"
            />
            <span class="rate-unit">%</span>
          </div>
          <div class="rate-hint">
            <span class="hint-dot"></span>
            建议范围 5% - 30%
          </div>
        </div>
        
        <!-- 当前配置信息 -->
        <div class="current-config-info" v-if="formState.commissionRate">
          <div class="config-preview">
            <span class="preview-label">配置预览：</span>
            <span class="preview-value">订单金额 × <strong>{{ formState.commissionRate }}%</strong> = 红人佣金</span>
          </div>
        </div>
      </div>
      
      <!-- 底部操作 -->
      <div class="config-modal-footer">
        <a-button class="btn-cancel" @click="editing = false">取消</a-button>
        <a-button type="primary" class="btn-confirm" @click="handleSave">
          <CheckOutlined />
          保存配置
        </a-button>
      </div>
    </a-modal>

    <div class="tab-content view-mode full-height-flex">
      <!-- 顶部卡片区域：固定高度 -->
      <div class="top-cards-section">
        <div class="info-grid reduced-gap">
          <!-- 左侧：生效状态卡片 -->
          <div class="info-card minimalist status-card">
            <div class="card-title">
              <span class="icon-wrapper gold"><WalletOutlined /></span> 当前生效佣金
            </div>
            <div class="card-body centered-body">
              <div class="current-rate-display">
                <span class="rate-value">{{ formatRate(effectiveRate) }}</span>
                <span class="rate-unit">%</span>
              </div>
              <div class="status-desc" style="margin-top: 12px;">
                {{ isDiscountActive ? '订单绑定折扣码时使用其配置，否则使用红人基础佣金' : '当前无折扣码特殊配置，使用红人基础佣金结算' }}
              </div>
            </div>
          </div>

          <!-- 右侧：结算资金概览 -->
          <div class="info-card minimalist">
            <div class="card-title">
              <span class="icon-wrapper purple"><BankOutlined /></span> 结算资金概览
            </div>
            <div class="card-body">
              <div class="fund-item">
                <label>累计总佣金</label>
                <div class="fund-value">US$ {{ formatCurrency(props.influencerData?.totalCommission || 0) }}</div>
              </div>
              <div class="fund-row">
                <div class="fund-sub-item">
                  <label>已结算</label>
                  <div class="val text-success">US$ {{ formatCurrency(props.influencerData?.settledAmount || 0) }}</div>
                </div>
                <div class="divider"></div>
                <div class="fund-sub-item">
                  <label>待结算</label>
                  <div class="val text-warning">US$ {{ formatCurrency(props.influencerData?.pendingAmount || 0) }}</div>
                </div>
              </div>
              <div class="settlement-status-box">
                <div class="ss-label">分佣状态</div>
                <div class="ss-value">订单自动结算 / 专员发起分佣</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 佣金配置历史：可以单独滚动 -->
      <div class="history-section scrollable-history">
        <div class="section-title sticky-header">
          <span class="icon-wrapper blue-dark"><HistoryOutlined /></span> 佣金变更历史
        </div>
        
        <div class="timeline-container">
          <a-timeline>
            <a-timeline-item v-for="(item, index) in historyList" :key="item.key" :color="index === 0 ? 'green' : 'gray'">
              <template #dot>
                <div class="timeline-dot" :class="{ 'latest': index === 0 }"></div>
              </template>
              <div class="timeline-card">
                <div class="tl-header">
                  <div class="tl-rate">
                    调整为 <span class="rate-highlight">{{ item.rate }}%</span>
                  </div>
                  <div class="tl-time">{{ item.time }}</div>
                </div>
                <div class="tl-content">
                  <div class="tl-remark">{{ item.remark || '无备注' }}</div>
                  <div class="tl-operator">
                    <a-avatar size="small" :style="{ backgroundColor: index === 0 ? '#10b981' : '#94a3b8' }">
                      {{ item.operator[0] }}
                    </a-avatar>
                    <span>{{ item.operator }}</span>
                  </div>
                </div>
              </div>
            </a-timeline-item>
          </a-timeline>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from 'vue';
import { message } from 'ant-design-vue';
import { EditOutlined, WalletOutlined, HistoryOutlined, BankOutlined, ClockCircleOutlined, ShopOutlined, InfoCircleOutlined, CheckOutlined, PercentageOutlined, CloseOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  influencerData: any;
}>();

const editing = ref(false);

const formState = reactive<any>({
  commissionRate: undefined as number | undefined,
});

// 模拟历史数据
const historyList = ref<any[]>([]);

const historyColumns = [
  { title: '变更时间', dataIndex: 'time', key: 'time', width: 160 },
  { title: '调整后佣金率', key: 'rate', width: 120, align: 'right' as const },
  { title: '操作人', key: 'operator', width: 120 },
  { title: '备注', dataIndex: 'remark', key: 'remark' },
];

import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

const fetchLogs = async (id: number) => {
  try {
    const logs = await influencerService.getLogs(id);
    // 只显示佣金相关的变更记录，其他日志在"红人日志"Tab查看
    const filtered = logs.filter((log: any) => {
      const field = log.fieldName || '';
      return field.includes('佣金') || field.includes('分佣') || field.includes('提现');
    });
    historyList.value = filtered.map((log: any) => {
      let operatorName = log.operator || 'System';
      try {
        if (operatorName.includes('%')) {
          operatorName = decodeURIComponent(operatorName);
        }
      } catch (err) {
        // ignore decode error
      }
      return {
        key: log.id,
        time: dayjs(log.createdAt).format('YYYY-MM-DD HH:mm:ss'),
        rate: log.newValue,
        operator: operatorName,
        remark: log.remark || '手动调整'
      };
    });
  } catch (e) {
    console.error('Failed to fetch logs', e);
  }
};

const syncFromProps = () => {
  const d = props.influencerData || {};
  formState.commissionRate = typeof d.commissionRate === 'number' ? d.commissionRate : (d.commissionRate ? Number(d.commissionRate) : undefined);
  
  if (d.id) {
    fetchLogs(d.id);
  } else {
    historyList.value = [];
  }
};

watch(
  () => props.influencerData,
  () => { if (!editing.value) syncFromProps(); },
  { immediate: true, deep: true },
);

const openEditModal = () => {
  syncFromProps();
  editing.value = true;
};

import { influencerService } from '@/services/influencerService';

/* ... props definition (kept in file) ... */

const handleSave = async () => {
  try {
    if (!props.influencerData?.id) {
        message.error('红人ID不存在');
        return;
    }
    
    const operator = (userStore.userInfo as any)?.realName || userStore.userInfo?.username || 'Admin';

    await influencerService.update({
        id: props.influencerData.id,
        commissionRate: formState.commissionRate
    }, operator);
    
    message.success('佣金配置更新成功');
    
    // Refresh logs immediately
    fetchLogs(props.influencerData.id);
    
    editing.value = false;
  } catch (error) {
    console.error('Failed to update commission:', error);
    message.error('更新失败');
  }
};
const formatRate = (val: number | undefined) => {
  if (val === undefined || val === null || Number.isNaN(Number(val))) return '-';
  return Number(val).toFixed(2);
};

const formatCurrency = (val: number) => {
  return val.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

// 计算逻辑
const discountCommissionRate = computed(() => {
  const val = props.influencerData?.discountCommissionRate;
  return typeof val === 'number' ? val : (val ? Number(val) : undefined);
});

const hasDiscountRate = computed(() => {
  return discountCommissionRate.value !== undefined && discountCommissionRate.value !== null && discountCommissionRate.value > 0;
});

const isDiscountActive = computed(() => hasDiscountRate.value);

const effectiveRate = computed(() => {
  return isDiscountActive.value ? discountCommissionRate.value : formState.commissionRate;
});
</script>

<style lang="scss" scoped>
.commission-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  margin-bottom: 0;
  padding-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  height: 48px;
  .header-tips { font-size: 14px; color: #94a3b8; font-weight: 500; }
}

.full-height-flex {
  display: flex;
  flex-direction: column;
  height: calc(100% - 48px); /* 减去 section-header 高度 */
  overflow: hidden;
}

.top-cards-section {
  flex-shrink: 0;
}

.premium-edit-btn {
  height: 36px;
  padding: 0 16px;
  border-radius: 8px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
  color: #475569;
  transition: all 0.3s;
  &:hover { color: #4f46e5; border-color: #4f46e5; transform: translateY(-2px); }
  
  /* Compact size for header */
  font-size: 13px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-bottom: 24px;
}

.info-card {
  background: #f8fafc;
  border-radius: 16px;
  padding: 16px;
  border: 1px solid #f1f5f9;
  transition: all 0.3s;
  &:hover { background: #fff; border-color: #e2e8f0; box-shadow: 0 10px 25px rgba(0, 0, 0, 0.03); }
  
  .card-title {
    font-size: 14px;
    font-weight: 700;
    color: #334155;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 8px;
    
    .icon-wrapper {
      width: 24px;
      height: 24px;
      border-radius: 6px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 13px;
      
      &.gold { background: #fffbeb; color: #d97706; }
      &.purple { background: #f3e8ff; color: #9333ea; }
    }
  }
  .card-body {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  .info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    label { font-size: 13px; color: #94a3b8; font-weight: 500; }
    .value { font-size: 14px; font-weight: 600; color: #334155; }
    .highlight { color: #4f46e5; }
  }
}

.history-section {
  /* margin-top moved to flex layout logic */
}

.scrollable-history {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding-right: 4px;
  position: relative;
  
  /* 自定义滚动条 */
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #334155;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 10px;
  
  .icon-wrapper {
    width: 24px;
    height: 24px;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    &.blue-dark { background: #e0f2fe; color: #0369a1; }
  }
}

.sticky-header {
  position: sticky;
  top: 0;
  background: #fff;
  z-index: 10;
  padding: 8px 0;
  margin-bottom: 16px;
}

.premium-table {
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    font-weight: 600;
    color: #64748b;
    font-size: 13px;
  }
  .rate-text {
    font-family: 'JetBrains Mono', monospace;
    font-weight: 600;
    color: #1e293b;
  }
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    span { font-size: 13px; color: #475569; font-weight: 500; }
  }
}

.scrollable-content {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.edit-mode { display: flex; flex-direction: column; }
.edit-scroll-area { flex: 1; padding: 10px 0; }
.edit-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}

.detail-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: 0 2px 6px rgba(59, 130, 246, 0.2);
  font-weight: 600;
  height: 32px;
  padding: 0 16px;
  border-radius: 8px;
  color: white;
  &:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); opacity: 0.9; }
}

.transfer-btn {
  border: 1px solid #e2e8f0;
  color: #475569;
  font-weight: 600;
  background: #fff;
  height: 32px;
  padding: 0 16px;
  border-radius: 8px;
  &:hover { color: #3b82f6; border-color: #3b82f6; background: #f0f7ff; }
}
</style>

<style lang="scss" scoped>
/* Additional Premium Styles for Commission Tab */
.status-card {
  background: linear-gradient(135deg, #fffbeb 0%, #fff 100%) !important;
  border-color: #fef3c7 !important;
}

.centered-body {
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  text-align: center;
}

.current-rate-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  
  .rate-value {
    font-size: 40px;
    font-weight: 800;
    font-family: 'JetBrains Mono', monospace;
    background: linear-gradient(135deg, #d97706 0%, #b45309 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: -2px;
  }
  
  .rate-unit {
    font-size: 16px;
    font-weight: 600;
    color: #d97706;
    margin-left: 4px;
    opacity: 0.8;
  }
}

/* Additional Styles for Fund Display */
.fund-item {
  margin-bottom: 12px;
  label { font-size: 12px; color: #64748b; display: block; margin-bottom: 4px; }
  .fund-value {
    font-size: 24px;
    font-weight: 800;
    color: #0f172a;
    font-family: 'JetBrains Mono', monospace;
  }
}

.fund-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
  
  .fund-sub-item {
    flex: 1;
    label { font-size: 12px; color: #94a3b8; display: block; margin-bottom: 2px; }
    .val {
      font-size: 16px;
      font-weight: 700;
      font-family: 'JetBrains Mono', monospace;
      
      &.text-success { color: #10b981; }
      &.text-warning { color: #f59e0b; }
    }
  }
  
  .divider { width: 1px; height: 32px; background: #e2e8f0; margin: 0 16px; }
}

.status-desc {
  font-size: 13px;
  color: #92400e;
  background: rgba(251, 191, 36, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 500;
}

.settlement-status-box {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
  border: 1px dashed #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .ss-label { font-size: 12px; color: #64748b; }
  .ss-value { font-size: 13px; color: #334155; font-weight: 600; }
}

.timeline-container {
  padding: 0 12px 12px 12px;
}

.timeline-dot {
  width: 10px;
  height: 10px;
  background: #cbd5e1;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #e2e8f0;
  
  &.latest {
    background: #10b981;
    box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.2);
  }
}

.timeline-card {
  background: #fff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 12px 16px;
  margin-top: -6px;
  margin-bottom: 24px;
  transition: all 0.3s;
  
  &:hover {
    border-color: #e2e8f0;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  }
  
  .tl-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    
    .tl-rate {
      font-size: 14px;
      color: #334155;
      .rate-highlight {
        font-weight: 700;
        color: #0f172a;
        font-size: 16px;
      }
    }
    
    .tl-time {
      font-size: 12px;
      color: #94a3b8;
    }
  }
  
  .tl-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid #f8fafc;
    padding-top: 8px;
    
    .tl-remark {
      font-size: 13px;
      color: #64748b;
    }
    
    .tl-operator {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 12px;
      color: #64748b;
    }
  }
}

/* Commission Config Modal - 重构版样式 */
.commission-config-modal {
  :deep(.ant-modal-content) {
    border-radius: 16px !important;
    overflow: hidden;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15) !important;
    padding: 0 !important;
  }
  
  :deep(.ant-modal-header) {
    display: none !important;
  }
  
  :deep(.ant-modal-body) {
    padding: 0 !important;
  }
}

.config-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-bottom: 1px solid #e2e8f0;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }
  
  .header-icon {
    width: 42px;
    height: 42px;
    background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
  }
  
  .header-text {
    .header-title {
      font-size: 17px;
      font-weight: 600;
      color: #1e293b;
    }
    .header-subtitle {
      font-size: 13px;
      color: #64748b;
      margin-top: 2px;
    }
  }
  
  .close-btn {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
    border-radius: 8px;
    transition: all 0.2s;
    
    &:hover {
      background: rgba(0, 0, 0, 0.04);
      color: #64748b;
    }
  }
}

.config-modal-body {
  padding: 24px;
  background: #fff;
  
  .info-alert {
    display: flex;
    gap: 12px;
    padding: 14px 16px;
    background: linear-gradient(135deg, #faf5ff 0%, #f3e8ff 100%);
    border: 1px solid #e9d5ff;
    border-radius: 10px;
    margin-bottom: 24px;
    
    .alert-icon {
      color: #9333ea;
      font-size: 16px;
      margin-top: 2px;
    }
    
    .alert-content {
      flex: 1;
      
      .alert-title {
        font-size: 13px;
        font-weight: 600;
        color: #7c3aed;
        margin-bottom: 4px;
      }
      .alert-desc {
        font-size: 13px;
        color: #6b21a8;
        line-height: 1.5;
      }
    }
  }
  
  .rate-config-section {
    text-align: center;
    padding: 20px 0;
    
    .rate-label {
      font-size: 13px;
      font-weight: 600;
      color: #64748b;
      text-transform: uppercase;
      letter-spacing: 1px;
      margin-bottom: 16px;
    }
    
    .rate-input-container {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 16px 32px;
      background: #f8fafc;
      border: 2px solid #e2e8f0;
      border-radius: 14px;
      transition: all 0.3s;
      
      &:focus-within {
        border-color: #8B5CF6;
        box-shadow: 0 0 0 4px rgba(139, 92, 246, 0.1);
        background: #fff;
      }
      
      .rate-input-field {
        width: 120px;
        border: none !important;
        background: transparent !important;
        box-shadow: none !important;
        font-size: 36px;
        font-weight: 700;
        color: #1e293b;
        font-family: 'JetBrains Mono', monospace;
        
        :deep(.ant-input-number-input) {
          text-align: center;
          padding: 0;
        }
        
        :deep(.ant-input-number-handler-wrap) {
          display: none;
        }
      }
      
      .rate-unit {
        font-size: 24px;
        font-weight: 600;
        color: #94a3b8;
      }
    }
    
    .rate-hint {
      margin-top: 14px;
      font-size: 12px;
      color: #94a3b8;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      
      .hint-dot {
        width: 4px;
        height: 4px;
        background: #cbd5e1;
        border-radius: 50%;
      }
    }
  }
  
  .current-config-info {
    margin-top: 20px;
    padding: 14px 16px;
    background: #f8fafc;
    border-radius: 10px;
    border: 1px dashed #e2e8f0;
    
    .config-preview {
      font-size: 13px;
      color: #64748b;
      text-align: center;
      
      .preview-label {
        color: #94a3b8;
      }
      
      .preview-value {
        strong {
          color: #8B5CF6;
          font-weight: 700;
        }
      }
    }
  }
}

.config-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 20px;
  background: #f8fafc;
  border-top: 1px solid #e2e8f0;
  
  .btn-cancel {
    height: 40px;
    padding: 0 24px;
    border-radius: 10px;
    font-weight: 500;
    font-size: 14px;
    border: 1px solid #e2e8f0;
    color: #64748b;
    background: #fff;
    transition: all 0.2s;
    
    &:hover {
      border-color: #cbd5e1;
      color: #475569;
    }
  }
  
  .btn-confirm {
    height: 40px;
    padding: 0 28px;
    border-radius: 10px;
    font-weight: 600;
    font-size: 14px;
    background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%);
    border: none;
    display: flex;
    align-items: center;
    gap: 6px;
    box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
    transition: all 0.2s;
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(139, 92, 246, 0.35);
    }
  }
}
</style>
