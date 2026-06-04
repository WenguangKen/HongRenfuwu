<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    width="900px"
    class="premium-refined-modal influencer-create-modal"
    centered
    :closable="false"
  >
    <div class="ic-modal-header">
      <div class="ic-header-left">
        <div class="ic-header-icon">
          <HistoryOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">分佣打款日志</div>
          <div class="ic-header-subtitle">查看该红人历次的打款操作与审核记录</div>
        </div>
      </div>
      <div class="ic-header-right"></div>
      <a-button type="text" class="close-btn" @click="visible = false">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="modal-body-container" style="padding: 24px;">
      <a-table
        class="premium-table"
        :columns="columns"
        :data-source="dataSource"
        :row-key="(record: any) => record.key ?? record.id"
        :pagination="false"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'amount'">
            <span style="font-weight: 500;">${{ record.amount }}</span>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag color="processing" v-if="record.status === '待审核'">待审核</a-tag>
            <a-tag color="success" v-else-if="record.status === '已打款'">已打款</a-tag>
            <a-tag color="error" v-else-if="record.status === '已驳回'">已驳回</a-tag>
            <a-tag v-else>{{ record.status }}</a-tag>
          </template>
          <template v-else-if="column.key === 'operator' || column.key === 'reviewer'">
            <div class="user-info-cell" v-if="record[column.key] && record[column.key] !== '-'">
              <a-avatar size="small" class="mini-avatar" :style="{ backgroundColor: getCreatorColor(record[column.key]) }">{{ record[column.key].charAt(0) }}</a-avatar>
              <span class="name">{{ record[column.key] }}</span>
            </div>
            <span v-else class="empty-placeholder">-</span>
          </template>
        </template>
      </a-table>
    </div>

    <div class="modal-fixed-footer">
      <a-button @click="visible = false" class="btn-cancel">关闭</a-button>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { HistoryOutlined, CloseOutlined } from '@ant-design/icons-vue';
import type { TableColumnsType } from 'ant-design-vue';

const props = defineProps<{
  open: boolean;
  columns: TableColumnsType;
  dataSource: any[];
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const getCreatorColor = (name: string) => {
  const colors = ['#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981'];
  if (!name || name === 'System' || name === '-') return colors[0];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};
</script>

<style scoped lang="scss">
.user-info-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .mini-avatar {
    font-size: 13px;
    font-weight: 600;
  }
  
  .name {
    font-size: 13px;
    color: #475569;
    font-weight: 500;
  }
}

.empty-placeholder {
  color: #cbd5e1;
}

.premium-table {
  flex: 1;
  overflow: hidden;

  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    color: #64748b;
    font-weight: 700;
    font-size: 12px;
    padding: 14px 12px;
    border-bottom: 2px solid #f1f5f9;
    text-transform: uppercase;
    letter-spacing: 0.05em;

    &::before { display: none !important; } // Remove default dividers
    
    &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
      background: #f8fafc !important;
    }
  }

  :deep(.ant-table-tbody > tr > td) {
    padding: 12px 12px;
    border-bottom: 1px solid #f8fafc;
    transition: background 0.2s ease;
    
    &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
      background: #fff;
    }
  }

  :deep(.ant-table-tbody > tr:hover > td) {
    background: #f0f9ff !important;
    &.ant-table-cell-fix-left, &.ant-table-cell-fix-right {
      background: #f0f9ff !important;
    }
  }
}
</style>

<style lang="scss">
.influencer-create-modal {
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
    display: flex;
    align-items: center;
    gap: 12px;
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
    position: absolute;
    top: 16px;
    right: 16px;
    transition: all 0.2s;
    
    &:hover { 
      background: rgba(0, 0, 0, 0.05); 
      color: #ef4444; 
    }
  }
}
</style>
