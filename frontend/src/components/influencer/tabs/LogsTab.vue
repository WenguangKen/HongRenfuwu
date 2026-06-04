<template>
  <div class="logs-tab">
    <div class="logs-container" ref="scrollContainer">
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
                  <a-tag :class="['type-tag', log.type]">{{ getLogTypeText(log.type) }}</a-tag>
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
                
                <div v-if="log.details && Object.keys(log.details).length" class="details-box">
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
              <span class="sub-hint">当前筛选条件或时间范围内暂无日志，请尝试调整筛选设置</span>
            </div>
          </template>
        </a-empty>
      </div>
      
      <!-- 返回顶部 -->
      <a-back-top :target="() => scrollContainer" :visibility-height="200" style="right: 24px; bottom: 80px;" />
    </div>
    
    <div class="footer-pagination" v-if="filteredLogs.length > pageSize">
      <div class="footer-left">
        <span class="info-item">当前内容数量 <span class="count-value">{{ filteredLogs.length }}</span></span>
      </div>
      <div class="footer-right">
        <a-pagination
          v-model:current="currentPage"
          v-model:page-size="pageSize"
          :total="filteredLogs.length"
          :page-size-options="['10', '20', '50', '100']"
          show-size-changer
          show-quick-jumper
          size="small"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { UserOutlined } from '@ant-design/icons-vue';
import { Empty } from 'ant-design-vue';

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

const scrollContainer = ref<HTMLElement | null>(null);

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
const pageSize = ref(10);

watch(() => props.logs, () => { currentPage.value = 1; }, { deep: true });

const filteredLogs = computed(() => props.logs);
const paginatedLogs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredLogs.value.slice(start, start + pageSize.value);
});

const getLogTypeText = (type: string) => {
  const map: Record<string, string> = {
    created: '初始创建', sample: '样品申领', commission: '分佣变动',
    settlement: '财务结算', address: '地址维护', social: '社媒更新',
    basic: '资料变更', status_change: '状态流转', payment: '打款记录'
  };
  return map[type] || '业务操作';
};

const isChangeObject = (val: any) => {
  return val && typeof val === 'object' && ('old' in val || 'new' in val);
};
</script>

<style lang="scss" scoped>
.logs-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative; /* Add relative positioning */
}

.logs-container {
  flex: 1; /* Allow container to grow/shrink properly */
  min-height: 0; /* Important for nested flex containers */
  overflow-y: auto;
  padding: 8px 12px 8px 12px;
}

.premium-timeline {
  padding-right: 12px; /* Add some padding for scrollbar */
  :deep(.ant-timeline-item-tail) { border-left: 2px dashed #e2e8f0; }
  
  .log-node {
    padding-bottom: 32px;
    &:last-child { padding-bottom: 0; }
  }

  .node-dot {
    width: 12px; height: 12px; border-radius: 50%; border: 3px solid #fff;
    box-shadow: 0 0 0 2px #e2e8f0;
    &.status_change { background: #ef4444; box-shadow: 0 0 0 2px #fee2e2; }
    &.created { background: #3b82f6; box-shadow: 0 0 0 2px #dbeafe; }
    &.sample { background: #10b981; box-shadow: 0 0 0 2px #d1fae5; }
    &.settlement { background: #8b5cf6; box-shadow: 0 0 0 2px #ede9fe; }
    &.commission { background: #f59e0b; box-shadow: 0 0 0 2px #fef3c7; }
    &.basic { background: #6366f1; box-shadow: 0 0 0 2px #e0e7ff; }
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
      border: none; font-weight: 700; border-radius: 4px; font-size: 10px; padding: 0 6px; height: 18px; line-height: 18px;
      &.status_change { background: #fee2e2; color: #ef4444; }
      &.created { background: #dbeafe; color: #3b82f6; }
      &.sample { background: #d1fae5; color: #10b981; }
      &.settlement { background: #ede9fe; color: #8b5cf6; }
      &.basic { background: #e0e7ff; color: #6366f1; }
      &.payment { background: #fff7ed; color: #f97316; }
    }

    .operator { font-size: 12px; color: #475569; font-weight: 600; display: flex; align-items: center; gap: 4px; }
  }

  .log-body {
    .content { font-size: 13px; color: #1e293b; font-weight: 600; margin: 0 0 8px 0; line-height: 1.4; }
    
    .details-box {
      background: #f8fafc; border-radius: 6px; padding: 8px 12px; border: 1px dotted #e2e8f0;
      display: flex; flex-direction: column; gap: 4px;
      
      .detail-item {
        display: flex; align-items: center; gap: 8px; font-size: 11.5px;
        
        .label { 
          color: #64748b; font-weight: 600; min-width: 54px; 
          &::after { content: ':'; margin-left: 1px; }
        }
        
        .value-container {
          display: flex; align-items: center; gap: 6px; flex: 1;
          
          .value { color: #334155; font-weight: 500; }
          .old-val { color: #94a3b8; text-decoration: line-through; opacity: 0.7; }
          .change-arrow { color: #cbd5e1; font-weight: 700; font-size: 10px; }
          .new-val { color: #4f46e5; font-weight: 700; background: #eff6ff; padding: 0px 4px; border-radius: 3px; font-size: 11px; }
        }
      }
    }
  }
}

.footer-pagination {
  margin-top: auto; /* Push to bottom */
  padding: 12px 16px;
  border-top: 1px solid #f1f5f9;
  display: flex; 
  justify-content: space-between; /* Space out left/right content */
  align-items: center;
  background: #fff;
  flex-shrink: 0; /* Prevent shrinking */

  .footer-left {
    .info-item {
      font-size: 13px;
      color: #64748b;
      .count-value {
        color: #0f172a;
        font-weight: 600;
        margin-left: 4px;
      }
    }
  }

  .footer-right {
    display: flex;
    align-items: center;
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
    
    .main-hint {
      color: #64748b;
      font-size: 14px;
      font-weight: 600;
    }
    
    .sub-hint {
      color: #94a3b8;
      font-size: 12px;
      font-weight: 400;
    }
  }
}
</style>
