<template>
  <div class="influencer-crawl-container">
    <!-- Header Page Title and Action -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">红人爬取任务</h1>
        <p class="page-subtitle">自动化获取 Instagram 红人数据，分析发帖内容并智能关联对应品牌</p>
      </div>
      <a-button type="primary" class="premium-btn" @click="showCreateModal">
        <template #icon><PlusOutlined /></template>
        新建爬取任务
      </a-button>
    </div>

    <!-- Main List Card -->
    <a-card :bordered="false" class="premium-card">
      <a-table
        :columns="columns"
        :data-source="tasks"
        :loading="loading"
        row-key="id"
        :pagination="{ pageSize: 10 }"
        class="premium-table"
      >
        <!-- Custom Strategy Type Render -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'strategyType'">
            <span class="strategy-badge" :class="record.strategyType.toLowerCase()">
              {{ getStrategyLabel(record.strategyType) }}
            </span>
          </template>

          <!-- Custom Status Render -->
          <template v-else-if="column.key === 'status'">
            <span class="status-badge" :class="record.status.toLowerCase()">
              <span class="status-dot"></span>
              {{ getStatusLabel(record.status) }}
            </span>
          </template>

          <!-- Custom Target Brand Render -->
          <template v-else-if="column.key === 'targetBrand'">
            <a-tag v-if="record.targetBrand" color="processing" class="brand-tag">
              {{ record.targetBrand }}
            </a-tag>
            <span v-else class="text-secondary">-</span>
          </template>

          <!-- Followers range -->
          <template v-else-if="column.key === 'followersRange'">
            <span>{{ formatNumber(record.minFollowers) }} - {{ formatNumber(record.maxFollowers) }}</span>
          </template>

          <!-- Action buttons -->
          <template v-else-if="column.key === 'action'">
            <div class="action-buttons">
              <a-button
                v-if="record.status === 'PENDING' || record.status === 'FAILED' || record.status === 'COMPLETED'"
                type="link"
                size="small"
                class="action-btn run-btn"
                @click="startTask(record)"
              >
                <template #icon><PlayCircleOutlined /></template>
                执行
              </a-button>
              <span v-else-if="record.status === 'RUNNING'" class="running-indicator">
                <LoadingOutlined class="spin-icon" /> 执行中...
              </span>
              
              <a-popconfirm
                title="确定要删除这个爬取任务吗？"
                ok-text="确认"
                cancel-text="取消"
                @confirm="deleteTask(record.id)"
              >
                <a-button type="link" size="small" danger class="action-btn delete-btn">
                  <template #icon><DeleteOutlined /></template>
                  删除
                </a-button>
              </a-popconfirm>
            </div>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- Create Task Modal -->
    <a-modal
      v-model:visible="modalVisible"
      :confirm-loading="submitLoading"
      width="640px"
      wrap-class-name="premium-modal"
    >
      <template #title>
        <div class="modal-title-container">
          <CompassOutlined class="title-icon" />
          <span>新建红人自动化爬取任务</span>
        </div>
      </template>

      <a-form :model="form" layout="vertical" class="premium-form">
        <!-- Section 1: 策略与条件 -->
        <div class="form-section">
          <div class="section-title">
            <SearchOutlined class="sec-icon" /> 搜寻策略与条件
          </div>
          
          <a-form-item required>
            <template #label>
              <span class="custom-label">任务名称</span>
            </template>
            <a-input v-model:value="form.taskName" placeholder="例如：2026美妆红人标签搜寻">
              <template #prefix><CompassOutlined style="color: #6366f1" /></template>
            </a-input>
            <div class="input-helper">给您的爬取任务起一个有辨识度的名字</div>
          </a-form-item>

          <div class="form-row">
            <a-form-item required class="flex-item">
              <template #label>
                <span class="custom-label">搜寻策略</span>
              </template>
              <a-select v-model:value="form.strategyType" placeholder="选择搜索策略">
                <a-select-option value="HASHTAG">Hashtag 标签搜寻</a-select-option>
                <a-select-option value="COMPETITOR_FOLLOWER">竞品粉丝搜寻</a-select-option>
                <a-select-option value="KEYWORD">关键词检索</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item required class="flex-item">
              <template #label>
                <span class="custom-label">检索内容</span>
              </template>
              <a-input v-model:value="form.searchQuery" placeholder="例如：#skincare 或 @sephora">
                <template #prefix><SearchOutlined style="color: #6366f1" /></template>
              </a-input>
            </a-form-item>
          </div>
        </div>

        <!-- Section 2: 过滤与智能关联 -->
        <div class="form-section">
          <div class="section-title">
            <ControlOutlined class="sec-icon" /> 过滤与智能关联
          </div>

          <div class="form-row">
            <a-form-item class="flex-item">
              <template #label>
                <span class="custom-label">粉丝数量下限</span>
              </template>
              <a-input-number
                v-model:value="form.minFollowers"
                :min="0"
                style="width: 100%"
                placeholder="0"
              />
            </a-form-item>
            <a-form-item class="flex-item">
              <template #label>
                <span class="custom-label">粉丝数量上限</span>
              </template>
              <a-input-number
                v-model:value="form.maxFollowers"
                :min="0"
                style="width: 100%"
                placeholder="10,000,000"
              />
            </a-form-item>
          </div>

          <a-form-item>
            <template #label>
              <span class="custom-label">默认关联品牌</span>
            </template>
            <a-input v-model:value="form.targetBrand" placeholder="输入默认绑定的合作品牌，例如 EsteeLauder">
              <template #prefix><TagOutlined style="color: #6366f1" /></template>
            </a-input>
            <div class="input-helper">该任务爬取到的红人，在入库时会自动绑定此品牌</div>
          </a-form-item>

          <div class="checkbox-card" :class="{ active: form.autoBrandMentions }">
            <a-checkbox v-model:checked="form.autoBrandMentions">
              <div class="checkbox-text-wrapper">
                <span class="checkbox-title">自动内容解析关联品牌</span>
                <span class="checkbox-desc">智能解析帖子中提到/ @ 的品牌并自动与红人建立关联，无需手动分配。</span>
              </div>
            </a-checkbox>
          </div>
        </div>
      </a-form>

      <template #footer>
        <div class="modal-footer">
          <a-button @click="modalVisible = false" class="cancel-btn">取消</a-button>
          <a-button type="primary" :loading="submitLoading" @click="handleCreateTask" class="submit-btn">
            <template #icon><SendOutlined /></template>
            开始执行
          </a-button>
        </div>
      </template>
    </a-modal>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from 'vue';
import { message } from 'ant-design-vue';
import {
  PlusOutlined,
  PlayCircleOutlined,
  DeleteOutlined,
  LoadingOutlined,
  SendOutlined,
  SearchOutlined,
  UserOutlined,
  TagOutlined,
  InfoCircleOutlined,
  CompassOutlined,
  ControlOutlined
} from '@ant-design/icons-vue';
import influencerService from '@/services/influencerService';

export default defineComponent({
  name: 'InfluencerCrawl',
  components: {
    PlusOutlined,
    PlayCircleOutlined,
    DeleteOutlined,
    LoadingOutlined,
    SendOutlined,
    SearchOutlined,
    UserOutlined,
    TagOutlined,
    InfoCircleOutlined,
    CompassOutlined,
    ControlOutlined
  },
  setup() {
    const tasks = ref<any[]>([]);
    const loading = ref<boolean>(false);
    const modalVisible = ref<boolean>(false);
    const submitLoading = ref<boolean>(false);
    
    const form = ref({
      taskName: '',
      strategyType: 'HASHTAG',
      searchQuery: '',
      minFollowers: 10000,
      maxFollowers: 1000000,
      targetBrand: '',
      autoBrandMentions: true
    });

    const columns = [
      { title: '任务名称', dataIndex: 'taskName', key: 'taskName' },
      { title: '策略类型', dataIndex: 'strategyType', key: 'strategyType' },
      { title: '检索条件', dataIndex: 'searchQuery', key: 'searchQuery' },
      { title: '粉丝过滤范围', key: 'followersRange' },
      { title: '关联品牌', key: 'targetBrand' },
      { title: '任务状态', dataIndex: 'status', key: 'status' },
      { title: '爬取红人数量', dataIndex: 'scrapedCount', key: 'scrapedCount' },
      { title: '操作', key: 'action', width: '180px' }
    ];

    let timer: number | null = null;

    const fetchTasks = async (silent = false) => {
      if (!silent) loading.value = true;
      try {
        const res = await influencerService.getCrawlTasks();
        tasks.value = res || [];
      } catch (e) {
        console.error('加载任务失败:', e);
        message.error('加载爬取任务列表失败');
      } finally {
        if (!silent) loading.value = false;
      }
    };

    const startPolling = () => {
      if (timer) return;
      timer = window.setInterval(() => {
        // Only pool if there are running tasks
        const hasRunning = tasks.value.some(t => t.status === 'RUNNING');
        if (hasRunning) {
          fetchTasks(true);
        }
      }, 3000);
    };

    const stopPolling = () => {
      if (timer) {
        clearInterval(timer);
        timer = null;
      }
    };

    onMounted(() => {
      fetchTasks();
      startPolling();
    });

    onUnmounted(() => {
      stopPolling();
    });

    const showCreateModal = () => {
      form.value = {
        taskName: '',
        strategyType: 'HASHTAG',
        searchQuery: '',
        minFollowers: 10000,
        maxFollowers: 1000000,
        targetBrand: '',
        autoBrandMentions: true
      };
      modalVisible.value = true;
    };

    const handleCreateTask = async () => {
      if (!form.value.taskName.trim()) {
        message.warning('请输入任务名称');
        return;
      }
      if (!form.value.searchQuery.trim()) {
        message.warning('请输入检索内容');
        return;
      }

      submitLoading.value = true;
      try {
        const createdTask = await influencerService.createCrawlTask(form.value);
        message.success('创建任务成功');
        modalVisible.value = false;
        
        // Immediately start the task
        await influencerService.startCrawlTask(createdTask.id);
        message.success('爬取任务已在后台启动！');
        
        await fetchTasks();
      } catch (e) {
        console.error('创建/启动任务失败:', e);
        message.error('创建任务失败');
      } finally {
        submitLoading.value = false;
      }
    };

    const startTask = async (record: any) => {
      try {
        await influencerService.startCrawlTask(record.id);
        message.success('任务已在后台启动！');
        fetchTasks();
      } catch (e) {
        console.error(e);
        message.error('启动任务失败');
      }
    };

    const deleteTask = async (id: number) => {
      try {
        await influencerService.deleteCrawlTask(id);
        message.success('任务已删除');
        fetchTasks();
      } catch (e) {
        console.error(e);
        message.error('删除任务失败');
      }
    };

    const getStrategyLabel = (type: string) => {
      const map: Record<string, string> = {
        'HASHTAG': 'Hashtag 标签',
        'COMPETITOR_FOLLOWER': '竞品粉丝',
        'KEYWORD': '关键词检索'
      };
      return map[type] || type;
    };

    const getStatusLabel = (status: string) => {
      const map: Record<string, string> = {
        'PENDING': '待执行',
        'RUNNING': '进行中',
        'COMPLETED': '已完成',
        'FAILED': '执行失败'
      };
      return map[status] || status;
    };

    const formatNumber = (val: number) => {
      if (!val) return '0';
      if (val >= 1000000) {
        return (val / 1000000).toFixed(1) + 'M';
      }
      if (val >= 1000) {
        return (val / 1000).toFixed(1) + 'K';
      }
      return val.toString();
    };

    return {
      tasks,
      loading,
      columns,
      modalVisible,
      submitLoading,
      form,
      showCreateModal,
      handleCreateTask,
      startTask,
      deleteTask,
      getStrategyLabel,
      getStatusLabel,
      formatNumber
    };
  }
});
</script>

<style scoped>
.influencer-crawl-container {
  padding: 24px;
  background: #f8fafc;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.premium-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%);
  border: none;
  height: 40px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
  transition: all 0.3s;
}

.premium-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(79, 70, 229, 0.3);
  opacity: 0.95;
}

.premium-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

.strategy-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.strategy-badge.hashtag {
  background-color: #eff6ff;
  color: #2563eb;
}

.strategy-badge.competitor_follower {
  background-color: #f0fdf4;
  color: #16a34a;
}

.strategy-badge.keyword {
  background-color: #faf5ff;
  color: #7c3aed;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-badge.pending {
  background-color: #f1f5f9;
  color: #64748b;
}
.status-badge.pending .status-dot {
  background-color: #64748b;
}

.status-badge.running {
  background-color: #fffbeb;
  color: #d97706;
}
.status-badge.running .status-dot {
  background-color: #d97706;
  animation: pulse 1.5s infinite;
}

.status-badge.completed {
  background-color: #f0fdf4;
  color: #16a34a;
}
.status-badge.completed .status-dot {
  background-color: #16a34a;
}

.status-badge.failed {
  background-color: #fef2f2;
  color: #dc2626;
}
.status-badge.failed .status-dot {
  background-color: #dc2626;
}

.brand-tag {
  font-weight: 500;
  border-radius: 4px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  font-weight: 500;
  padding: 0;
  height: auto;
}

.run-btn {
  color: #4f46e5;
}

.running-indicator {
  font-size: 13px;
  color: #d97706;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.spin-icon {
  animation: spin 1s linear infinite;
}

.text-secondary {
  color: #94a3b8;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.flex-item {
  flex: 1;
}

/* Premium Modal Header & Title */
.modal-title-container {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #1e293b;
  font-weight: 600;
  font-size: 18px;
}

.title-icon {
  color: #6366f1;
  font-size: 20px;
}

/* Modal Sections */
.form-section {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 18px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.form-section:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #4f46e5;
  margin-bottom: 16px;
  border-bottom: 1px dashed #e2e8f0;
  padding-bottom: 8px;
}

.sec-icon {
  font-size: 15px;
}

/* Labels and Helper Text */
.custom-label {
  font-weight: 600;
  color: #334155;
  font-size: 13px;
}

.input-helper {
  font-size: 11px;
  color: #94a3b8;
  margin-top: 4px;
  line-height: 1.4;
}

/* Checkbox Card */
.checkbox-card {
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 8px;
  padding: 12px 16px;
  margin-top: 16px;
  transition: all 0.2s ease;
}

.checkbox-card:hover {
  border-color: #cbd5e1;
}

.checkbox-card.active {
  border-color: #a5b4fc;
  background: #f5f7ff;
}

.checkbox-text-wrapper {
  display: inline-flex;
  flex-direction: column;
  vertical-align: middle;
  margin-left: 8px;
}

.checkbox-title {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}

.checkbox-desc {
  font-size: 11px;
  color: #64748b;
  margin-top: 2px;
  white-space: normal;
  line-height: 1.4;
}

/* Modal Footer Custom Styles */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 8px 0;
}

.cancel-btn {
  border-radius: 8px;
  height: 38px;
  padding: 0 18px;
  border-color: #cbd5e1;
  color: #64748b;
  font-weight: 500;
  transition: all 0.2s;
}

.cancel-btn:hover {
  color: #1e293b;
  border-color: #94a3b8;
  background: #f8fafc;
}

.submit-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #3b82f6 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  height: 38px !important;
  padding: 0 20px !important;
  font-weight: 500 !important;
  box-shadow: 0 4px 10px rgba(79, 70, 229, 0.2) !important;
  transition: all 0.2s;
}

.submit-btn:hover {
  opacity: 0.95;
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(79, 70, 229, 0.3) !important;
}

/* Global Ant Design Modal Overrides */
:global(.premium-modal .ant-modal-content) {
  border-radius: 16px !important;
  overflow: hidden !important;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04) !important;
}

:global(.premium-modal .ant-modal-header) {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%) !important;
  border-bottom: 1px solid #e2e8f0 !important;
  padding: 16px 24px !important;
}

:global(.premium-modal .ant-modal-body) {
  padding: 24px 24px 8px 24px !important;
}

:global(.premium-modal .ant-modal-footer) {
  border-top: 1px solid #e2e8f0 !important;
  padding: 12px 24px !important;
  background: #f8fafc !important;
}

:global(.premium-modal .ant-input),
:global(.premium-modal .ant-select-selector),
:global(.premium-modal .ant-input-number) {
  border-radius: 8px !important;
  border-color: #cbd5e1 !important;
  transition: all 0.2s ease !important;
}

:global(.premium-modal .ant-input:focus),
:global(.premium-modal .ant-input-focused),
:global(.premium-modal .ant-select-focused .ant-select-selector),
:global(.premium-modal .ant-input-number-focused) {
  border-color: #6366f1 !important;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.15) !important;
}

:global(.premium-modal .ant-form-item-label > label) {
  margin-bottom: 4px !important;
}

@keyframes pulse {
  0% {
    transform: scale(0.9);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
  100% {
    transform: scale(0.9);
    opacity: 0.6;
  }
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}
</style>
