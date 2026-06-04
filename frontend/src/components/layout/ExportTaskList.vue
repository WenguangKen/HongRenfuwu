<template>
  <div class="task-list-wrapper">
    <a-dropdown 
      v-model:open="dropdownVisible"
      :trigger="['click']" 
      placement="bottomRight" 
      :overlay-style="{ width: '640px' }"
    >
      <div class="task-list-btn-container">
        <a-button type="text" class="task-list-btn" @click.prevent>
          <template #icon>
            <file-text-outlined />
          </template>
        </a-button>
        <!-- 自定义徽章显示 - 只显示未读任务数量（红色） -->
        <div class="task-badges" v-if="taskStore.unreadCount > 0">
          <span 
            class="task-badge task-badge-unread"
            :title="`未读: ${taskStore.unreadCount}`"
          >
            {{ taskStore.unreadCount > 99 ? '99+' : taskStore.unreadCount }}
          </span>
        </div>
      </div>
      
      <template #overlay>
        <div class="export-task-list" @click.stop>
          <div class="task-list-header">
            <span class="task-list-title">导出任务</span>
            <div class="header-actions">
              <a-button 
                type="link" 
                size="small" 
                @click="handleClearCompleted"
                :disabled="!hasCompletedTasks"
              >
                清空已完成
              </a-button>
            </div>
          </div>

          <!-- 状态筛选器 -->
          <div class="task-filter">
            <a-radio-group v-model:value="filterStatus" size="small" button-style="solid">
              <a-radio-button value="all">
                全部 <small v-if="taskStore.unreadCount > 0" class="tab-count">{{ taskStore.unreadCount }}</small>
              </a-radio-button>
              <a-radio-button value="processing">进行中</a-radio-button>
              <a-radio-button value="completed">
                已完成 <small v-if="getUnreadCountByStatus('completed') > 0" class="tab-count">{{ getUnreadCountByStatus('completed') }}</small>
              </a-radio-button>
              <a-radio-button value="failed">
                失败 <small v-if="getUnreadCountByStatus('failed') > 0" class="tab-count failed">{{ getUnreadCountByStatus('failed') }}</small>
              </a-radio-button>
            </a-radio-group>
          </div>
          
          <!-- 统计信息 -->
          <div class="task-stats">
            <div class="stat-item">
              <span class="stat-label">进行中：</span>
              <span class="stat-value processing">{{ taskStore.processingCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">已完成：</span>
              <span class="stat-value completed">{{ taskStore.completedCount }}</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">失败：</span>
              <span class="stat-value failed">{{ taskStore.failedCount }}</span>
            </div>
          </div>
          
          <div class="task-list-body" :class="{ 'empty-body': filteredTasks.length === 0 }">
            <a-empty 
              v-if="filteredTasks.length === 0" 
              :description="emptyDescription"
              :image-style="{ height: '80px' }"
            />
            
            <div v-else class="task-items">
              <div
                v-for="task in filteredTasks"
                :key="task.id"
                class="task-item"
                :class="{
                  'task-item-processing': task.status === 'processing',
                  'task-item-completed': task.status === 'completed',
                  'task-item-failed': task.status === 'failed',
                  'task-item-unread': !task.isRead && (task.status === 'completed' || task.status === 'failed')
                }"
                @click="handleTaskClick(task)"
              >
                <div class="task-item-content">
                  <div class="task-main-info">
                    <div class="task-top">
                      <span class="task-page-tag">{{ getPageName(task.pageType) }}</span>
                      <span class="task-template">{{ task.templateName || '默认模板' }}</span>
                      <a-tag :color="getStatusColor(task.status)" class="premium-status-tag">
                        {{ getStatusText(task.status) }}
                      </a-tag>
                    </div>
                    <div class="task-file-row">
                      <div class="task-filename" :title="task.fileName">
                        {{ task.fileName }}
                        <span v-if="!task.isRead && (task.status === 'completed' || task.status === 'failed')" class="unread-dot"></span>
                      </div>
                    </div>
                    <div class="task-meta">
                      <span v-if="task.status !== 'processing'" class="task-time">{{ formatTime(task.createdAt) }}</span>
                      <span v-if="task.error" class="task-error-msg">{{ task.error }}</span>
                    </div>
                  </div>
                  
                  <div class="task-actions" @click.stop>
                    <a-tooltip title="重新导出" v-if="task.status === 'failed'">
                      <a-button type="text" shape="circle" size="small" @click="handleRetry(task)" class="action-btn retry">
                        <reload-outlined />
                      </a-button>
                    </a-tooltip>
                    
                    <a-tooltip title="下载文件" v-if="task.status === 'completed' && task.fileUrl">
                      <a-button type="text" shape="circle" size="small" @click="downloadFile(task)" class="action-btn download">
                        <download-outlined />
                      </a-button>
                    </a-tooltip>

                    <a-tooltip title="标记已读" v-if="!task.isRead && (task.status === 'completed' || task.status === 'failed')">
                      <a-button type="text" shape="circle" size="small" @click="handleMarkTaskAsRead(task)" class="action-btn check">
                        <check-outlined />
                      </a-button>
                    </a-tooltip>

                    <a-button type="text" shape="circle" size="small" danger @click="handleRemoveTask(task.id)" class="action-btn delete">
                      <delete-outlined />
                    </a-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </a-dropdown>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { FileTextOutlined, DownloadOutlined, DeleteOutlined, ReloadOutlined, CheckOutlined } from '@ant-design/icons-vue';
import { useExportTaskStore, type ExportTask } from '@/stores/exportTask';
import { exportToCSVAsync } from '@/utils/export';
import { notification, Button } from 'ant-design-vue';
import { h } from 'vue';
import dayjs from 'dayjs';

const taskStore = useExportTaskStore();

// 下拉菜单显隐控制
const dropdownVisible = ref(false);

// 筛选状态
const filterStatus = ref('all');

// 过滤后的任务列表
const filteredTasks = computed(() => {
  if (filterStatus.value === 'all') return taskStore.tasks;
  return taskStore.tasks.filter((t: ExportTask) => t.status === filterStatus.value);
});

// 获取各状态未读数量
const getUnreadCountByStatus = (status: string) => {
  return taskStore.tasks.filter(t => t.status === status && !t.isRead).length;
};

const hasCompletedTasks = computed(() => {
  return taskStore.tasks.some((t: ExportTask) => t.status === 'completed');
});

// 计算空状态文案
const emptyDescription = computed(() => {
  const statusMap: Record<string, string> = {
    all: '暂无导出任务',
    processing: '暂时没有进行中的任务',
    completed: '暂时没有已完成的任务',
    failed: '暂时没有失败的任务',
  };
  return statusMap[filterStatus.value] || '暂无任务';
});

const formatTime = (timeStr: string) => {
  return dayjs(timeStr).format('MM-DD HH:mm');
};

// 获取页面名称
const getPageName = (pageType: string): string => {
  const pageNameMap: Record<string, string> = {
    'influencer-list': '红人列表',
    'influencer-pool': '资源池',
    'unknown': '未知页面',
  };
  return pageNameMap[pageType] || pageType;
};

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    pending: 'default',
    processing: 'processing',
    completed: 'success',
    failed: 'error',
  };
  return colorMap[status] || 'default';
};

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    pending: '等待中',
    processing: '处理中',
    completed: '已完成',
    failed: '失败',
  };
  return textMap[status] || status;
};

const downloadFile = (task: ExportTask) => {
  if (!task.fileUrl) return;
  
  const link = document.createElement('a');
  link.href = task.fileUrl;
  link.download = task.fileName;
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  
  setTimeout(() => {
    document.body.removeChild(link);
    URL.revokeObjectURL(task.fileUrl!);
  }, 100);
  
  // 下载后标记为已读并关闭通知
  if (task.notificationKey) {
    notification.close(task.notificationKey);
  }
  taskStore.markAsRead(task.id);
};

const handleRetry = async (task: ExportTask) => {
  if (!task.exportData || !task.exportColumns || !task.exportFilename) {
    notification.error({
      message: '重新导出失败',
      description: '任务数据已丢失，无法重新导出',
    });
    return;
  }
  
  // 重置任务状态
  taskStore.updateTask(task.id, {
    status: 'processing',
    error: undefined,
  });
  
  try {
    const fileUrl = await exportToCSVAsync(
      task.exportData,
      task.exportColumns,
      task.exportFilename
    );
    
    // 导出成功
    taskStore.updateTask(task.id, {
      status: 'completed',
      fileUrl,
    });
    
    // 发送通知
    const notificationKey = `export-${task.id}`;
    taskStore.updateTask(task.id, {
      notificationKey,
      isRead: false,
    });
    
    notification.success({
      key: notificationKey,
      placement: 'top',
      message: '导出任务处理完成',
      description: `文件 "${task.fileName}" 已准备就绪。`,
      duration: 0,
      btn: h(Button, {
        type: 'primary',
        size: 'middle',
        style: { 
          borderRadius: '8px', 
          background: '#6366f1', 
          border: 'none',
          boxShadow: '0 4px 12px rgba(99, 102, 241, 0.3)',
          fontWeight: '600'
        },
        icon: h(DownloadOutlined),
        onClick: () => {
          downloadFile(taskStore.tasks.find(t => t.id === task.id)!);
        }
      }, { default: () => '立即下载' }),
      onClose: () => {
        taskStore.markAsRead(task.id);
      },
    });
  } catch (error: any) {
    // 导出失败
    taskStore.updateTask(task.id, {
      status: 'failed',
      error: error.message || '导出失败',
    });
    notification.error({
      message: '重新导出失败',
      description: error.message || '导出失败，请稍后重试',
    });
  }
};

// 处理任务点击（标记为已读）
const handleTaskClick = (task: ExportTask) => {
  // 如果是未读的已完成或失败任务，点击即标记为已读
  if (!task.isRead && (task.status === 'completed' || task.status === 'failed')) {
    taskStore.markAsRead(task.id);
  }
};

// 标记单个任务为已读
const handleMarkTaskAsRead = (task: ExportTask) => {
  taskStore.markAsRead(task.id);
  // 如果有关联的通知，关闭它
  if (task.notificationKey) {
    notification.close(task.notificationKey);
  }
};

const handleRemoveTask = (taskId: string) => {
  taskStore.removeTask(taskId);
};

const handleClearCompleted = () => {
  taskStore.clearCompleted();
};
</script>

<style lang="scss" scoped>
.task-list-wrapper {
  position: relative;
}

.task-list-btn-container {
  position: relative;
  display: inline-block;
}

.task-list-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #6366f1; /* Vibrant Indigo to ensure visibility */
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(99, 102, 241, 0.08); /* Light indigo background */
  border: 1px solid rgba(99, 102, 241, 0.1);
  
  &:hover {
    color: #4f46e5;
    background: rgba(99, 102, 241, 0.15);
    transform: scale(1.05);
    border-color: rgba(99, 102, 241, 0.2);
  }
}

.task-badges {
  position: absolute;
  top: -6px;
  right: -6px;
  display: flex;
  flex-direction: column;
  gap: 3px;
  z-index: 10;
  pointer-events: none; /* 让徽章不阻挡点击 */
}

.task-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  border-radius: 9px;
  font-size: 11px;
  font-weight: 600;
  color: #fff;
  line-height: 1;
  box-shadow: 0 0 0 2px #fff;
  white-space: nowrap;
  animation: badgeAppear 0.3s ease-out;
  
  &.task-badge-unread {
    background-color: #ff4d4f; /* 红色背景 */
  }
}

@keyframes badgeAppear {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.export-task-list {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px) saturate(180%);
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
  
  .task-list-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    
    .task-list-title {
      font-size: 16px;
      font-weight: 800;
      color: #1e293b;
      letter-spacing: -0.5px;
    }
  }

  .task-filter {
    padding: 12px 20px;
    background: rgba(248, 250, 252, 0.5);
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    
    :deep(.ant-radio-group) {
        display: flex;
        width: 100%;
        .ant-radio-button-wrapper {
            flex: 1;
            text-align: center;
            border-radius: 6px !important;
            margin: 0 2px;
            border: none !important;
            background: transparent;
            color: #64748b;
            font-size: 12px;
            height: 28px;
            line-height: 28px;
            padding: 0;

            &::before { display: none; }
            
            &-checked {
                background: #fff !important;
                color: #6366f1 !important;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05) !important;
                font-weight: 700;
            }
        }
    }

    .tab-count {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        background: #f43f5e;
        color: #fff;
        height: 16px;
        min-width: 16px;
        padding: 0 4px;
        border-radius: 8px;
        font-size: 10px;
        font-weight: 700;
        margin-left: 4px;
        vertical-align: middle;
        line-height: normal;

        &.failed {
            background: #ef4444;
        }
    }
  }
  
  .task-stats {
    display: none; /* Removed in favor of filter */
  }
  
  .task-list-body {
    height: 480px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;

    &.empty-body {
      justify-content: center;
      align-items: center;
    }
    
    &::-webkit-scrollbar {
        width: 4px;
    }
    &::-webkit-scrollbar-thumb {
        background: rgba(0,0,0,0.1);
        border-radius: 4px;
    }

    .task-items {
      padding: 8px;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    
    .task-item {
      padding: 14px 16px;
      border-radius: 12px;
      background: #fff;
      border: 1px solid #e2e8f0; /* Clearer border */
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      cursor: pointer;
      position: relative;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02); /* Light shadow */
      
      &:hover {
        border-color: #6366f1;
        box-shadow: 0 4px 16px rgba(99, 102, 241, 0.12);
        transform: translateY(-1px);
      }
      
      &.task-item-unread {
        background: linear-gradient(90deg, #f8faff 0%, #ffffff 100%);
        border-left: 3px solid #6366f1;
      }

      .task-item-content {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 16px;
      }

      .task-main-info {
        flex: 1;
        min-width: 0;

        .task-top {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 6px;

            .task-page-tag {
                font-size: 11px;
                font-weight: 700;
                color: #6366f1;
                background: rgba(99, 102, 241, 0.08);
                padding: 1px 6px;
                border-radius: 4px;
            }

            .task-template {
                font-size: 12px;
                color: #64748b;
                font-weight: 500;
            }

            .premium-status-tag {
              margin: 0;
              font-size: 10px;
              border-radius: 4px;
              line-height: normal;
              padding: 1px 6px;
              border: none;
            }
        }

        .task-file-row {
            display: flex;
            align-items: center;
            margin-bottom: 4px;

            .task-filename {
                font-size: 14px;
                font-weight: 700;
                color: #1e293b;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                display: flex;
                align-items: center;
                gap: 6px;

                .unread-dot {
                    width: 6px;
                    height: 6px;
                    border-radius: 50%;
                    background: #f43f5e;
                    flex-shrink: 0;
                }
            }
        }

        .task-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 11px;
            color: #94a3b8;

            .task-error-msg {
                color: #f43f5e;
                background: rgba(244, 63, 94, 0.05);
                padding: 0 4px;
                border-radius: 2px;
            }
        }
      }

      .task-actions {
        display: flex;
        align-items: center;
        gap: 4px;

        .action-btn {
            color: #94a3b8;
            transition: all 0.2s;
            
            &:hover {
                background: #f1f5f9;
                transform: scale(1.1);
            }

            &.retry:hover { color: #6366f1; background: rgba(99, 102, 241, 0.08); }
            &.download:hover { color: #10b981; background: rgba(16, 185, 129, 0.08); }
            &.check:hover { color: #6366f1; background: rgba(99, 102, 241, 0.08); }
            &.delete:hover { color: #f43f5e; background: rgba(244, 63, 94, 0.08); }
        }
      }
    }
  }
}
</style>
