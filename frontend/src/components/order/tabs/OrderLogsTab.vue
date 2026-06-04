<template>
  <div class="logs-section">
    <div :class="['logs-timeline-wrapper', { 'with-pagination': filteredLogs.length > pageSize }]">
      <template v-if="paginatedLogs.length">
        <a-timeline class="premium-timeline">
          <a-timeline-item
            v-for="(log, index) in paginatedLogs"
            :key="`${log.time}-${index}`"
            class="log-node"
          >
            <template #dot>
              <div :class="['node-dot', log.type]"></div>
            </template>
            
            <div class="log-card">
              <div class="log-header">
                <div class="left">
                  <span :class="['type-tag', log.type]">{{ getLogTypeText(log.type) }}</span>
                  <span class="operator" v-if="log.operator">
                    <UserOutlined /> {{ log.operator }}
                  </span>
                </div>
                <div class="right">
                  <span class="time">{{ log.time }}</span>
                </div>
              </div>
              
              <div class="log-body">
                <p class="content">{{ log.content }}</p>
                
                <div v-if="log.details" class="details-box">
                  <div v-for="(value, key) in log.details" :key="key" class="detail-item">
                    <span class="label">{{ key }}</span>
                    <div class="value-container">
                      <template v-if="isChangeObject(value)">
                        <span class="old-val">{{ value.old || '-' }}</span>
                        <span class="change-arrow">→</span>
                        <span class="new-val">{{ value.new || '-' }}</span>
                      </template>
                      <template v-else>
                        <span class="value">{{ value }}</span>
                      </template>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </template>
      <div v-else class="empty-state-wrapper">
        <a-empty :image="simpleImage">
          <template #description>
            <div class="empty-hint">
              <span class="main-hint">未发现相关记录</span>
              <span class="sub-hint">当前筛选条件或时间范围内暂无日志</span>
            </div>
          </template>
        </a-empty>
      </div>
    </div>
    
    <div class="pagination-container" v-if="filteredLogs.length > pageSize">
      <a-pagination
        v-model:current="currentPage"
        v-model:page-size="pageSize"
        :total="filteredLogs.length"
        :page-size-options="['10', '20', '50', '100']"
        show-size-changer
        show-quick-jumper
        size="small"
        :show-total="(total: number) => `共 ${total} 条`"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { UserOutlined } from '@ant-design/icons-vue';
import { Empty } from 'ant-design-vue';

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

interface Log {
  type: string;
  time: string;
  content: string;
  operator?: string;
  details?: Record<string, any>;
}

const props = defineProps<{
  logs: Log[];
}>();

const currentPage = ref(1);
const pageSize = ref(20);

watch(() => props.logs, () => {
  currentPage.value = 1;
}, { deep: true });

const filteredLogs = computed(() => {
  return props.logs;
});

const paginatedLogs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredLogs.value.slice(start, end);
});

const getLogTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    created: '订单创建',
    payment: '支付成功',
    shipped: '订单发货',
    delivered: '订单妥投',
    completed: '订单完成',
    cancelled: '订单取消',
    refund: '订单退款',
    split: '订单拆单',
    commission: '分佣变动',
    status_change: '状态变更',
  };
  return textMap[type] || type;
};

const isChangeObject = (val: any) => {
  return val && typeof val === 'object' && ('old' in val || 'new' in val);
};
</script>

<style lang="scss" scoped>
.logs-section {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding-top: 8px;
}

.logs-timeline-wrapper {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 8px 12px 8px 16px;
  
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
}

.premium-timeline {
  :deep(.ant-timeline-item-tail) { border-left: 2px dashed #e2e8f0; }
  
  .log-node {
    padding-bottom: 24px;
    &:last-child { padding-bottom: 0; }
  }

  .node-dot {
    width: 12px; height: 12px; border-radius: 50%; border: 3px solid #fff;
    box-shadow: 0 0 0 2px #e2e8f0;
    
    /* 颜色映射 */
    &.created { background: #3b82f6; box-shadow: 0 0 0 2px #dbeafe; }
    &.payment { background: #10b981; box-shadow: 0 0 0 2px #d1fae5; }
    &.shipped { background: #06b6d4; box-shadow: 0 0 0 2px #cffafe; }
    &.delivered { background: #8b5cf6; box-shadow: 0 0 0 2px #ede9fe; }
    &.completed { background: #22c55e; box-shadow: 0 0 0 2px #dcfce7; }
    &.cancelled { background: #ef4444; box-shadow: 0 0 0 2px #fee2e2; }
    &.refund { background: #f97316; box-shadow: 0 0 0 2px #ffedd5; }
    &.split { background: #6366f1; box-shadow: 0 0 0 2px #e0e7ff; }
    &.commission { background: #f59e0b; box-shadow: 0 0 0 2px #fef3c7; }
    &.status_change { background: #84cc16; box-shadow: 0 0 0 2px #ecfccb; }
  }
}

.log-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 12px 16px;
  margin-left: 8px;
  border: 1px solid #edf2f7;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.01);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:hover { 
    transform: translateX(4px);
    border-color: #e2e8f0; 
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.08); 
  }

  .log-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;
    .left { display: flex; align-items: center; gap: 8px; }
    .right { .time { font-size: 11px; color: #94a3b8; font-weight: 500; font-family: 'Inter', sans-serif; } }

    .type-tag {
      font-weight: 700; border-radius: 4px; font-size: 11px; padding: 1px 6px;
      
      /* 颜色映射 - 与 dot 保持一致但更淡 */
      &.created { background: #eff6ff; color: #3b82f6; }
      &.payment { background: #ecfdf5; color: #10b981; }
      &.shipped { background: #ecfeff; color: #06b6d4; }
      &.delivered { background: #f5f3ff; color: #8b5cf6; }
      &.completed { background: #f0fdf4; color: #22c55e; }
      &.cancelled { background: #fef2f2; color: #ef4444; }
      &.refund { background: #fff7ed; color: #f97316; }
      &.split { background: #eef2ff; color: #6366f1; }
      &.commission { background: #fffbeb; color: #f59e0b; }
      &.status_change { background: #f7fee7; color: #65a30d; }
    }

    .operator { font-size: 12px; color: #64748b; font-weight: 500; display: flex; align-items: center; gap: 4px; }
  }

  .log-body {
    .content { font-size: 13px; color: #1e293b; font-weight: 600; margin: 0 0 8px 0; line-height: 1.4; }
    
    .details-box {
      background: #f8fafc; border-radius: 6px; padding: 12px 16px; border: 1px dotted #e2e8f0;
      display: flex; flex-direction: row; flex-wrap: wrap; gap: 12px 100px;
      
      .detail-item {
        display: flex; align-items: center; gap: 12px; font-size: 13px;
        min-width: 0;
        
        .label { 
          color: #64748b; font-weight: 500; min-width: fit-content;
          &::after { content: ':'; margin-left: 2px; }
        }
        
        .value-container {
          display: flex; align-items: center; gap: 6px; flex: 1;
          
          .value { color: #1e293b; font-weight: 600; font-size: 13px; }
          .old-val { color: #94a3b8; text-decoration: line-through; opacity: 0.7; }
          .change-arrow { color: #cbd5e1; font-weight: 700; font-size: 10px; }
          .new-val { color: #4f46e5; font-weight: 700; background: #eff6ff; padding: 0px 4px; border-radius: 3px; font-size: 12px; }
        }
      }
    }
  }
}

.pagination-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.03);
  
  .footer-left {
    color: #64748b;
    font-size: 13px;
    .info-divider { margin: 0 8px; color: #cbd5e1; }
    .count-value { font-weight: 600; color: #1e293b; &.highlight { color: #1890ff; } }
  }
}

.empty-state-wrapper {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding-bottom: 40px;

  .empty-hint {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    
    .main-hint { color: #64748b; font-size: 14px; font-weight: 600; }
    .sub-hint { color: #94a3b8; font-size: 12px; }
  }
}
</style>