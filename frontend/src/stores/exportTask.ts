import { defineStore } from 'pinia';
import { ref, computed, watch } from 'vue';
import { logger } from '@/utils/logger';

export interface ExportTask {
  id: string;
  fileName: string;
  createdAt: string;
  completedAt?: string;
  status: 'pending' | 'processing' | 'completed' | 'failed';
  fileUrl?: string;
  error?: string;
  pageType: string; // 'influencer-list' | 'influencer-pool'
  templateId?: string; // 使用的模板ID
  templateName?: string; // 使用的模板名称
  // 用于重新导出
  exportData?: any[];
  exportColumns?: Array<{ key: string; title: string; dataKey?: string; formatter?: (value: any, record: any) => string }>;
  exportFilename?: string;
  // 已读状态
  isRead?: boolean;
  notificationKey?: string; // 通知的key，用于关闭通知
}

export const useExportTaskStore = defineStore('exportTask', () => {
  const tasks = ref<ExportTask[]>([]);
  const STORAGE_KEY = 'export_tasks';
  const CLEANUP_INTERVAL = 3 * 24 * 60 * 60 * 1000; // 3天（毫秒）

  // 从localStorage加载任务
  const loadTasks = () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      if (stored) {
        const parsed = JSON.parse(stored);
        tasks.value = parsed.map((task: any) => ({
          ...task,
          // 不恢复exportData和exportColumns（节省存储空间）
          exportData: undefined,
          exportColumns: undefined,
          // 确保isRead字段存在，如果不存在则默认为false（已完成或失败的任务）
          isRead: task.isRead !== undefined ? task.isRead : (
            (task.status === 'completed' || task.status === 'failed') ? false : undefined
          ),
        }));
      }
    } catch (error) {
      logger.error('加载任务失败', error);
    }
  };

  // 保存任务到localStorage（添加大小检查）
  const saveTasks = () => {
    try {
      const tasksToSave = tasks.value.map(({ exportData, exportColumns, ...task }) => task);
      const dataStr = JSON.stringify(tasksToSave);
      const dataSize = new Blob([dataStr]).size;
      
      // 检查localStorage大小限制（通常5-10MB，保守使用4MB）
      const MAX_SIZE = 4 * 1024 * 1024; // 4MB
      
      if (dataSize > MAX_SIZE) {
        logger.warn('导出任务数据过大，将清理旧任务');
        // 保留最近的50个任务
        const recentTasks = tasksToSave.slice(0, 50);
        const recentDataStr = JSON.stringify(recentTasks);
        const recentDataSize = new Blob([recentDataStr]).size;
        
        if (recentDataSize > MAX_SIZE) {
          logger.error('即使保留最近50个任务，数据仍然过大，保存失败');
          return;
        }
        
        localStorage.setItem(STORAGE_KEY, recentDataStr);
        tasks.value = recentTasks as ExportTask[];
        return;
      }
      
      localStorage.setItem(STORAGE_KEY, dataStr);
    } catch (error: any) {
      // 可能是QuotaExceededError
      if (error.name === 'QuotaExceededError' || error.code === 22) {
        logger.warn('localStorage空间不足，清理旧任务');
        // 保留最近的30个任务
        const recentTasks = tasks.value.slice(0, 30).map(({ exportData, exportColumns, ...task }) => task);
        try {
          localStorage.setItem(STORAGE_KEY, JSON.stringify(recentTasks));
          tasks.value = recentTasks as ExportTask[];
        } catch (e) {
          logger.error('清理后仍然无法保存', e);
        }
      } else {
        logger.error('保存任务失败', error);
      }
    }
  };

  // 自动清理已完成超过3天的任务
  const autoCleanup = () => {
    const now = Date.now();
    tasks.value = tasks.value.filter(task => {
      if (task.status === 'completed' && task.completedAt) {
        const completedTime = new Date(task.completedAt).getTime();
        const timeSinceCompleted = now - completedTime;
        return timeSinceCompleted < CLEANUP_INTERVAL;
      }
      return true;
    });
    saveTasks();
  };

  // 初始化：加载任务并启动自动清理
  loadTasks();
  autoCleanup();
  
  // 定期清理（每小时检查一次）- 使用单例模式确保只创建一个定时器
  let cleanupTimer: number | null = null;
  let beforeUnloadHandler: (() => void) | null = null;
  
  const startCleanup = () => {
    if (cleanupTimer !== null || typeof window === 'undefined') return;
    cleanupTimer = window.setInterval(() => {
      autoCleanup();
    }, 60 * 60 * 1000);
    
    // 添加beforeunload监听器，确保页面关闭时清理
    beforeUnloadHandler = () => {
      stopCleanup();
    };
    window.addEventListener('beforeunload', beforeUnloadHandler);
  };
  
  const stopCleanup = () => {
    if (cleanupTimer !== null) {
      clearInterval(cleanupTimer);
      cleanupTimer = null;
    }
    if (beforeUnloadHandler && typeof window !== 'undefined') {
      window.removeEventListener('beforeunload', beforeUnloadHandler);
      beforeUnloadHandler = null;
    }
  };
  
  // 启动清理（只在浏览器环境且未启动时）
  if (typeof window !== 'undefined') {
  startCleanup();
  }

  // 监听任务变化，自动保存（节流）- 保存停止函数以便清理
  let saveTimer: number | null = null;
  const stopWatch = watch(tasks, () => {
    if (saveTimer) {
      clearTimeout(saveTimer);
    }
    saveTimer = window.setTimeout(() => {
      saveTasks();
      saveTimer = null;
    }, 300);
  }, { deep: true });
  
  // 注意：Pinia store是单例，通常不需要手动停止watch
  // 但如果需要，可以通过返回stopWatch来处理

  // 获取进行中的任务数量
  const processingCount = computed(() => {
    return tasks.value.filter(t => t.status === 'pending' || t.status === 'processing').length;
  });

  // 获取已完成的任务数量
  const completedCount = computed(() => {
    return tasks.value.filter(t => t.status === 'completed').length;
  });

  // 获取失败的任务数量
  const failedCount = computed(() => {
    return tasks.value.filter(t => t.status === 'failed').length;
  });

  // 获取待处理的任务数量（用于徽章显示）
  const pendingCount = computed(() => {
    return processingCount.value;
  });

  // 获取未读任务数量（已完成或失败的未读任务）
  const unreadCount = computed(() => {
    return tasks.value.filter(t => {
      // 已完成或失败的任务，且isRead为false或undefined（未读）
      if (t.status === 'completed' || t.status === 'failed') {
        return t.isRead === false || t.isRead === undefined;
      }
      return false;
    }).length;
  });

  // 添加任务
  const addTask = (task: Omit<ExportTask, 'id' | 'createdAt' | 'status'>) => {
    const newTask: ExportTask = {
      ...task,
      id: `task_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      createdAt: new Date().toISOString(),
      status: 'pending',
      isRead: undefined, // 进行中的任务不需要已读状态
    };
    tasks.value.unshift(newTask);
    saveTasks();
    return newTask.id;
  };

  // 更新任务状态
  const updateTask = (taskId: string, updates: Partial<ExportTask>) => {
    const index = tasks.value.findIndex(t => t.id === taskId);
    if (index !== -1) {
      const task = tasks.value[index];
      if (task) {
        if (updates.status === 'completed' && !task.completedAt) {
          updates.completedAt = new Date().toISOString();
        }
        tasks.value[index] = { ...task, ...updates } as ExportTask;
        saveTasks();
      }
    }
  };

  // 删除任务
  const removeTask = (taskId: string) => {
    const index = tasks.value.findIndex(t => t.id === taskId);
    if (index !== -1) {
      tasks.value.splice(index, 1);
      saveTasks();
    }
  };

  // 清空已完成的任务
  const clearCompleted = () => {
    tasks.value = tasks.value.filter(t => t.status !== 'completed');
    saveTasks();
  };

  // 重新导出失败的任务
  const retryTask = (taskId: string) => {
    const task = tasks.value.find(t => t.id === taskId);
    if (task && task.status === 'failed') {
      // 重置任务状态
      updateTask(taskId, {
        status: 'pending',
        error: undefined,
        isRead: false, // 重新导出时重置为未读
      });
      return task;
    }
    return null;
  };

  // 标记任务为已读
  const markAsRead = (taskId: string) => {
    updateTask(taskId, { isRead: true });
  };

  // 标记所有任务为已读
  const markAllAsRead = () => {
    tasks.value.forEach(task => {
      if (!task.isRead && (task.status === 'completed' || task.status === 'failed')) {
        task.isRead = true;
      }
    });
    saveTasks();
  };

  return {
    tasks,
    pendingCount,
    processingCount,
    completedCount,
    failedCount,
    unreadCount,
    addTask,
    updateTask,
    removeTask,
    clearCompleted,
    retryTask,
    markAsRead,
    markAllAsRead,
  };
});
