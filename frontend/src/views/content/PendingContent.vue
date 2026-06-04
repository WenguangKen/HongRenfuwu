<template>
  <div class="pending-content-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '12px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="small">
        <a-row :gutter="[12, 8]">
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="任务ID (批量)">
              <a-input 
                v-model:value="filterForm.taskId" 
                placeholder="搜索任务ID (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('taskId')"
              >
                <template #prefix><SearchOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('taskId')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="关联红人">
              <a-select
                v-model:value="filterForm.influencer"
                placeholder="搜索红人"
                mode="multiple"
                show-search
                :filter-option="false"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
                @search="handleInfluencerRemoteSearch"
              >
                <a-select-option v-for="inf in influencerList" :key="inf.id" :value="inf.nickName || inf.realName">
                  {{ inf.nickName || inf.realName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="红人Handle (批量)">
              <a-input 
                v-model:value="filterForm.defaultHandle" 
                placeholder="搜索Handle (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('defaultHandle')"
              >
                <template #prefix><UserOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('defaultHandle')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="红人邮箱 (批量)">
              <a-input 
                v-model:value="filterForm.influencerEmail" 
                placeholder="搜索邮箱 (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('influencerEmail')"
              >
                <template #prefix><MailOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('influencerEmail')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="关联订单 (批量)">
              <a-input 
                v-model:value="filterForm.orderNo" 
                placeholder="搜索订单号 (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('orderNo')"
              >
                <template #prefix><ShoppingCartOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('orderNo')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="商品搜索 (SKU/SPU)">
              <a-input 
                v-model:value="filterForm.productSku" 
                placeholder="搜索SKU (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('productSku')"
              >
                <template #prefix><BarcodeOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('productSku')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>

          <!-- 第二行：下拉筛选 + 操作按钮 -->
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="目标平台">
              <a-select
                v-model:value="filterForm.platform"
                placeholder="多选平台"
                mode="multiple"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
              >
                <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="联络员">
              <a-select
                v-model:value="filterForm.contactPerson"
                placeholder="多选联络员"
                mode="multiple"
                show-search
                :filter-option="filterOption"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
              >
                <a-select-option v-for="name in contactPersonOptions" :key="name" :value="name">{{ name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="6" :lg="4">
            <a-form-item label="负责人">
              <a-select
                v-model:value="filterForm.owner"
                placeholder="多选负责人"
                mode="multiple"
                show-search
                :filter-option="filterOption"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
              >
                <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.name">{{ u.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="8">
              <a-form-item label="时间范围">
                <a-range-picker
                  v-model:value="filterForm.timeRange"
                  style="width: 100%"
                  :placeholder="['开始时间', '结束时间']"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  :presets="timeRanges"
                  class="premium-datepicker"
                />
              </a-form-item>
            </a-col>
          </template>

          <a-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="filter-footer" style="padding-top: 0; border-top: none; margin-top: 0;">
              <a-space size="small">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn" style="height: 32px; padding: 0 20px;">重置</a-button>
                <a-button type="link" @click="filterExpanded = !filterExpanded" style="height: 32px; padding: 0 4px; font-size: 13px;">
                  {{ filterExpanded ? '收起筛选' : '更多筛选' }}
                  <DownOutlined v-if="!filterExpanded" />
                  <UpOutlined v-if="filterExpanded" />
                </a-button>
              </a-space>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- Export Modal -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="contentExportFields"
      page-type="pending-content"
      :current-user-id="currentUserId"
      @export="handleExportFromModal"
    />

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="toolbar-left" style="display: flex; align-items: center; gap: 16px;">
            <div class="status-switcher-wrapper">
              <a-radio-group v-model:value="activeTab" button-style="solid" size="small" @change="handleTabChange">
                <a-radio-button value="pending">待上传</a-radio-button>
                <a-radio-button value="completed">已完成</a-radio-button>
              </a-radio-group>
            </div>
            <!-- 存储空间展示 -->
            <div class="storage-stats-mini" v-if="storageStats">
              <span class="label">存储空间:</span>
              <div class="progress-bar-wrapper">
                <div class="progress-bar-inner" :style="{ width: storagePercentage + '%', backgroundColor: storagePercentage > 90 ? '#ff4d4f' : '#1890ff' }"></div>
              </div>
              <span class="value">{{ formatSize(storageStats.used) }} / {{ formatSize(storageStats.total) }}</span>
            </div>
          </div>
          
          <a-space size="small" class="toolbar-btns">
            <a-popconfirm
              v-if="selectedRowKeys.length > 0"
              title="彻底删除选中的任务及其所有素材文件？"
              ok-text="确定删除"
              cancel-text="取消"
              @confirm="handleBatchDelete"
              placement="bottomRight"
              :ok-button-props="{ danger: true }"
            >
              <a-button class="premium-btn-small" danger v-permission="'content.pending.delete'">
                <template #icon><delete-outlined /></template>批量删除 ({{ selectedRowKeys.length }})
              </a-button>
            </a-popconfirm>
            <a-button class="premium-btn-small" v-permission="'content.pending.export'" @click="handleExport">
              <template #icon><export-outlined /></template>导出
            </a-button>
            <a-button v-if="activeTab === 'pending'" type="primary" @click="showCreateModal" class="premium-btn-small primary-gradient" v-permission="'content.pending.create'">
              <template #icon><plus-outlined /></template>新建任务
            </a-button>
          </a-space>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="data"
        :row-key="(record: any) => record.key ?? record.taskId"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :scroll="{ y: 'calc(100vh - 380px)', x: 'max-content' }"
        size="middle"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'taskId'">
            <div style="font-weight: 700; color: #1e293b;">{{ record.taskId }}</div>
          </template>
          <template v-else-if="column.key === 'influencer'">
            <div style="font-weight: 600; color: #475569;">{{ record.influencer }}</div>
          </template>
          <template v-else-if="column.key === 'defaultHandle'">
            <div v-if="record.defaultHandle" style="font-size: 12px; color: #0369a1; font-weight: 600;">{{ record.defaultHandle }}</div>
            <span v-else style="color: #94a3b8;">-</span>
          </template>
          <template v-else-if="column.key === 'influencerEmail'">
            <div style="font-size: 12px; color: #64748b; font-family: 'JetBrains Mono', monospace;">{{ record.influencerEmail || '-' }}</div>
          </template>
          <template v-else-if="column.key === 'platform'">
            <div class="platform-tags">
              <a-tag :class="['status-tag', `tag-${getPlatformColor(record.platform)}`]">
                {{ record.platform }}
              </a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'orderNo'">
            <span v-if="record.orderNo" style="font-family: 'JetBrains Mono', monospace; font-size: 13px; color: #1e293b;">{{ record.orderNo }}</span>
            <span v-else style="color: #94a3b8;">-</span>
          </template>
          <template v-else-if="column.key === 'productSku'">
            <div v-if="record.productSku">
              <div v-for="(rawSku, sIdx) in record.productSku.split(/[,，]\s*/).filter((s: string) => s.trim())" :key="sIdx" style="display: flex; align-items: center; flex-wrap: nowrap; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 4px 8px; margin-bottom: 4px; overflow: hidden; white-space: nowrap;">
                <!-- 判断是否为旧格式 sku_vid_dbId（含有_分隔的纯数字段），如果是则只显示第一段SKU -->
                <template v-if="rawSku.includes('_') && rawSku.split('_').some((seg: string) => /^\d{5,}$/.test(seg))">
                  <span style="color: #0369a1; font-weight: 700; font-size: 13px; font-family: 'JetBrains Mono', monospace;">{{ rawSku.split('_')[0] }}</span>
                </template>
                <!-- 新格式：SKU-Spec1-Spec2 -->
                <template v-else>
                  <span style="color: #0369a1; font-weight: 700; font-size: 13px; font-family: 'JetBrains Mono', monospace;">{{ rawSku.includes('-') ? rawSku.split('-')[0] : rawSku }}</span>
                  <template v-if="rawSku.includes('-')">
                    <span v-for="(spec, specIdx) in rawSku.split('-').slice(1)" :key="specIdx"
                      :style="{ color: ['#16a34a', '#ea580c', '#8b5cf6'][specIdx % 3], fontWeight: '600', fontSize: '13px' }"
                    >-{{ spec }}</span>
                  </template>
                </template>
              </div>
            </div>
            <span v-else style="color: #94a3b8;">-</span>
          </template>
          <template v-else-if="column.key === 'owner'">
            <div style="font-weight: 600; color: #334155;">{{ record.owner || '-' }}</div>
          </template>
          <template v-else-if="column.key === 'fileCount'">
            <div class="count-pill" :class="{ 'has-files': record.fileCount > 0 }">
              <file-outlined class="pill-icon" />
              <span class="pill-count">{{ record.fileCount }}</span>
            </div>
          </template>
          <template v-else-if="column.key === 'time'">
            <div style="font-size: 12px; line-height: 1.6; color: #64748b;">
              <div><span style="color: #94a3b8;">创建：</span>{{ record.createTime }}</div>
              <div v-if="record.uploadTime"><span style="color: #94a3b8;">上传：</span>{{ record.uploadTime }}</div>
              <div v-if="record.completeTime" style="color: #16a34a;"><span style="color: #94a3b8;">完成：</span>{{ record.completeTime }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <!-- 待上传 Tab 操作 -->
              <template v-if="activeTab === 'pending'">
                <a-button type="link" size="small" @click="handleUpload(record)" class="action-link primary" v-permission="'content.pending.upload'">
                  上传内容
                </a-button>
                <a-divider type="vertical" />
                <a-button type="link" size="small" @click="handleEdit(record)" class="action-link" v-permission="'content.pending.edit'">
                  编辑
                </a-button>
                <a-divider type="vertical" />
                <a-popconfirm
                  title="彻底删除该任务及其所有素材文件？"
                  ok-text="确定删除"
                  cancel-text="取消"
                  @confirm="handleDeleteGroup(record)"
                  placement="topRight"
                  :ok-button-props="{ danger: true }"
                >
                  <a-button type="link" size="small" class="action-link danger" v-permission="'content.pending.delete'">
                    删除
                  </a-button>
                </a-popconfirm>
              </template>
              <!-- 已完成 Tab 操作 -->
              <template v-else>
                <a-button type="link" size="small" @click="handleUpload(record)" class="action-link primary" v-permission="'content.pending.upload'">
                  编辑内容
                </a-button>
                <a-divider type="vertical" />
                <a-button type="link" size="small" @click="handleView(record)" class="action-link info">
                  查看详情
                </a-button>
              </template>
            </div>
          </template>
        </template>
      </a-table>
      
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前记录数量 <span class="count-value">{{ pagination.total }}</span></span>
          <span class="info-divider">/</span>
          <span class="info-item">选中数量 <span class="count-value highlight">{{ selectedRowKeys.length }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="handleTableChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 新建任务Modal -->
    <CreateTaskModal
      v-model:open="createModalVisible"
      :platforms="platforms"
      @ok="handleCreateOk"
    />

    <!-- 上传内容Modal -->
    <UploadContentModal
      v-model:open="uploadModalVisible"
      :task-data="currentTask"
      :platforms="platforms"
      :readonly="uploadReadonly"
      :sku-list="skuList"
      @ok="handleUploadOk"
      @change-order-no="handleOrderNoChange"
    />

    <!-- 编辑任务Modal -->
    <EditTaskModal
      v-model:open="editModalVisible"
      :task-data="currentTask"
      :platforms="platforms"
      @ok="handleEditOk"
    />

    <!-- 批量搜索弹窗 -->
    <a-modal
      v-model:open="batchSearchVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);">
            <SearchOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">{{ batchSearchTitles[batchSearchType]?.title }}</div>
            <div class="ic-header-subtitle">{{ batchSearchTitles[batchSearchType]?.subtitle }}</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchValue"
          :placeholder="batchSearchPlaceholder"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>

      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearch" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'ContentPending'
});
import { ref, reactive, computed, h, onMounted, watch } from 'vue';
import { 
  PlusOutlined, DownOutlined, UpOutlined, LoadingOutlined, ExportOutlined,
  SearchOutlined, CloseOutlined, DatabaseOutlined, MailOutlined,
  ShoppingCartOutlined, BarcodeOutlined, UserOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';
import { useRouter } from 'vue-router';
import UploadContentModal from '@/components/content/UploadContentModal.vue';
import CreateTaskModal from '@/components/content/CreateTaskModal.vue';
import EditTaskModal from '@/components/content/EditTaskModal.vue';
import { getContents, deleteContent, deleteTaskGroup, getStorageStats, batchDeleteContents, type ContentDto } from '@/services/contentService';
const getProductSkus = async (params?: any): Promise<any[]> => [];
import influencerService from '@/services/influencerService';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';
import { createExportTask } from '@/utils/exportTaskHelper';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';

import { useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
const filterExpanded = ref(false);

// 核心修复：初始值从 URL 中获取，默认为 pending
const activeTab = ref<'pending' | 'completed'>((route.query.tab as any) || 'pending'); 
const loading = ref(false);
const exportModalVisible = ref(false);
const currentUserId = ref<string>('default_user');

// 获取当前用户ID
const initCurrentUserId = () => {
  try {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const userInfoObj = JSON.parse(userInfo);
      currentUserId.value = (userInfoObj.id || userInfoObj.userId || 'default_user') as string;
    }
  } catch (e) {
    console.error('获取当前用户ID失败', e);
  }
};
const storageStats = ref<{ used: number; total: number; type: string } | null>(null);
const storagePercentage = computed(() => {
  if (!storageStats.value || storageStats.value.total === 0) return 0;
  return Math.min(100, (storageStats.value.used / storageStats.value.total) * 100);
});

const formatSize = (bytes: number) => {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

const loadStorageStats = async () => {
  try {
    storageStats.value = await getStorageStats();
  } catch (e) {
    console.error('加载存储空间统计失败:', e);
  }
};

// --- Batch Search State ---
const batchSearchVisible = ref(false);
const batchSearchValue = ref('');
const batchSearchType = ref<string>('');
const batchSearchTitles: Record<string, { title: string; subtitle: string }> = {
  taskId: { title: '批量搜索任务ID', subtitle: '请输入任务ID，多个ID请用换行或逗号分隔' },
  defaultHandle: { title: '批量搜索红人Handle', subtitle: '请输入红人Handle，多个Handle请用换行或逗号分隔' },
  influencerEmail: { title: '批量搜索红人邮箱', subtitle: '请输入红人邮箱，多个邮箱请用换行或逗号分隔' },
  orderNo: { title: '批量搜索关联订单', subtitle: '请输入订单号，多个订单号请用换行或逗号分隔' },
  productSku: { title: '批量搜索关联商品', subtitle: '请输入 SKU/SPU，多个串请用换行或逗号分隔' },
  influencer: { title: '批量搜索红人名称', subtitle: '请输入红人名称，多个名称请用换行或逗号分隔' },
};

const batchSearchPlaceholder = computed(() => {
  return batchSearchTitles[batchSearchType.value]?.subtitle || '请输入搜索内容...';
});

const openBatchSearch = (type: string) => {
  batchSearchType.value = type;
  const currentVal = (filterForm as any)[type];
  if (Array.isArray(currentVal)) {
    batchSearchValue.value = currentVal.join('\n');
  } else if (currentVal) {
    batchSearchValue.value = String(currentVal).split(',').join('\n');
  } else {
    batchSearchValue.value = '';
  }
  batchSearchVisible.value = true;
};

const handleBatchSearch = () => {
  const values = batchSearchValue.value
    .split(/[\n\s,;，；\t]+/)
    .filter(v => v.trim());
  
  if (Array.isArray((filterForm as any)[batchSearchType.value])) {
    (filterForm as any)[batchSearchType.value] = values;
  } else {
    (filterForm as any)[batchSearchType.value] = values.join(',');
  }
  
  batchSearchVisible.value = false;
  handleFilter();
};
const createModalVisible = ref(false);
const uploadModalVisible = ref(false);
const currentTask = ref<any>({});
const selectedRowKeys = ref<(string | number)[]>([]);

// Custom loading indicator for table
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

const filterForm = reactive({
  taskId: '',
  influencer: [] as string[],
  defaultHandle: '',
  influencerEmail: '',
  platform: [] as string[],
  owner: [] as string[],
  contactPerson: [] as string[],
  orderNo: '',
  productSku: '',
  timeRange: undefined as [string, string] | undefined,
});

const editModalVisible = ref(false);

const timeRanges = [
  { label: '最近7天', value: [dayjs().subtract(6, 'day'), dayjs()] },
  { label: '最近14天', value: [dayjs().subtract(13, 'day'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(29, 'day'), dayjs()] },
  { label: '最近90天', value: [dayjs().subtract(89, 'day'), dayjs()] },
];

const platforms = ['TikTok', 'Instagram', 'YouTube', 'Facebook'];
const skuList = ref<string[]>([]);
const commonStore = useCommonDataStore();
const { allUsers, ownerUsers, liaisonTagOptions } = storeToRefs(commonStore);
const influencerList = ref<any[]>([]);
const userList = computed(() => allUsers.value.map(u => ({ id: u.id, username: u.name })));
const contactPersonOptions = computed(() => liaisonTagOptions.value.map(t => t.name).sort());

// 加载红人列表（仅用于筛选下拉框，支持远程搜索）
const loadInfluencers = async (keyword: string = '') => {
  try {
    const result = await influencerService.getList({ page: 1, size: 50, searchKey: keyword || undefined });
    influencerList.value = (result.content || []).filter(inf => inf.nickName || inf.realName);
  } catch (e) {
    console.error('加载红人列表失败:', e);
  }
};

// 红人远程搜索防抖
let influencerSearchTimer: ReturnType<typeof setTimeout> | null = null;
const handleInfluencerRemoteSearch = (value: string) => {
  if (influencerSearchTimer) clearTimeout(influencerSearchTimer);
  influencerSearchTimer = setTimeout(() => {
    loadInfluencers(value);
  }, 300);
};



// 加载真实商品 SKU 列表
const loadSkuList = async () => {
  try {
    skuList.value = await getProductSkus();
  } catch (e) {
    console.error('加载SKU列表失败:', e);
  }
};

const filterOption = (input: string, option: any) => {
  const value = option.value || '';
  return value.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const getPlatformColor = (platform: string) => {
  const p = platform?.toUpperCase() || '';
  const colorMap: Record<string, string> = {
    'TIKTOK': 'black',
    'INSTAGRAM': 'pink',
    'YOUTUBE': 'red',
    'FACEBOOK': 'blue',
  };
  return colorMap[p] || 'blue';
};

const columns: TableColumnsType = [
  { title: '任务ID', key: 'taskId', width: 150, fixed: 'left' },
  { title: '关联红人', key: 'influencer', width: 150 },
  { title: '红人Handle', key: 'defaultHandle', dataIndex: 'defaultHandle', width: 160 },
  { title: '红人邮箱', key: 'influencerEmail', width: 180 },
  { title: '目标平台', key: 'platform', width: 120, align: 'center' },
  { title: '关联订单', key: 'orderNo', width: 150 },
  { title: '关联商品', key: 'productSku', width: 260 },
  { title: '素材数量', key: 'fileCount', width: 100, align: 'center' },
  { title: '负责人', dataIndex: 'ownerName', key: 'ownerName', width: 120, align: 'center' },
  { title: '联络员', dataIndex: 'contactPersonName', key: 'contactPersonName', width: 120, align: 'center' },
  { title: '时间信息', key: 'time', width: 240 },
  { title: '操作', key: 'action', width: 160, fixed: 'right', align: 'center' },
];

// 真实数据
const data = ref<any[]>([]);

// 加载内容列表（根据 Tab 加载不同状态）
// 由于前端按 taskGroupId 分组，后端的分页粒度（单条记录）和前端显示粒度（任务组）不一致，
// 所以一次性获取全部记录，分组后在前端做本地分页
const allGroupedData = ref<any[]>([]);

// 提取公共分组函数：将 ContentDto[] 按 taskGroupId 分组为 UI 行
const groupContentsByTask = (items: ContentDto[]): any[] => {
  const grouped = new Map<string, any>();
  items.forEach((item: ContentDto) => {
    const gid = item.taskGroupId || `TASK-${item.id}`;
    if (!grouped.has(gid)) {
      grouped.set(gid, {
        key: item.id,
        taskId: gid,
        influencer: item.influencerName || `Influencer ${item.influencerId}`,
        influencerEmail: item.influencerEmail,
        influencerId: item.influencerId,
        defaultHandle: item.defaultHandle || '',
        platform: item.platform || 'TikTok',
        orderNo: item.orderNo || null,
        productSku: item.productSku || null,
        ownerName: item.ownerName || '-',
        contactPersonName: item.contactPersonName || '-',
        owner: item.owner,
        createTime: item.createdAt,
        uploadTime: item.updatedAt,
        completeTime: item.status === 'PENDING_REVIEW' ? item.updatedAt : null,
        status: item.status,
        title: item.title,
        filePath: item.filePath,
        previewUrl: item.previewUrl,
        postUrl: item.postUrl,
        _isGroup: !!item.taskGroupId,
        fileCount: 0,
        ids: [] as number[],
        arrivalStatus: (item as any).arrivalStatus || '未送达',
        isCommercial: item.isCommercial,
        variantTitle: item.variantTitle || (item.productSku?.includes(' / ') ? item.productSku.split(' / ').slice(1).join(' / ') : (item.productSku?.includes('-') ? item.productSku.split('-').slice(1).join(' / ') : ''))
      });
    }
    
    const existing = grouped.get(gid)!;
    existing.ids.push(item.id);
    if (item.productSku && !existing.productSku) {
      existing.productSku = item.productSku;
      if (!existing.variantTitle) {
        existing.variantTitle = item.variantTitle || (item.productSku.includes(' / ') ? item.productSku.split(' / ').slice(1).join(' / ') : (item.productSku.includes('-') ? item.productSku.split('-').slice(1).join(' / ') : ''));
      }
    }
    if (item.orderNo && !existing.orderNo) existing.orderNo = item.orderNo;
    if (item.owner && !existing.owner) existing.owner = item.owner;
    if (item.influencerName && (!existing.influencer || existing.influencer.startsWith('Influencer '))) {
      existing.influencer = item.influencerName;
    }
    if (item.influencerEmail && !existing.influencerEmail) existing.influencerEmail = item.influencerEmail;
    if (item.defaultHandle && !existing.defaultHandle) existing.defaultHandle = item.defaultHandle;
    if (item.ownerName && existing.ownerName === '-') existing.ownerName = item.ownerName;
    if (item.contactPersonName && existing.contactPersonName === '-') existing.contactPersonName = item.contactPersonName;
    
    if (item.filePath || item.postUrl || item.description) {
      existing.fileCount++;
    }
  });
  return Array.from(grouped.values());
};

// 本地分页切片（从 allGroupedData 中取当前页）
const applyLocalPagination = () => {
  const filtered = filteredData.value;
  pagination.total = filtered.length;
  const start = (pagination.current - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  data.value = filtered.slice(start, end);
};

const loadData = async () => {
  loading.value = true;
  try {
    const status = activeTab.value === 'pending' ? 'PENDING_UPLOAD' : 'PENDING_REVIEW';
    // 构建后端筛选参数
    const apiParams: Record<string, any> = {
      status,
      page: 0,
      size: 9999, // 一次拉全部，前端按任务组分组后本地分页
    };
    // 将有值的筛选条件传给后端
    if (filterForm.taskId) apiParams.taskGroupId = filterForm.taskId;
    if (filterForm.influencerEmail) apiParams.influencerEmail = filterForm.influencerEmail;
    if (filterForm.orderNo) apiParams.orderNo = filterForm.orderNo;
    if (filterForm.productSku) apiParams.productSku = filterForm.productSku;
    if (filterForm.defaultHandle) apiParams.defaultHandle = filterForm.defaultHandle;
    if (filterForm.contactPerson?.length > 0) apiParams.contactPersonName = filterForm.contactPerson.join(',');
    if (filterForm.owner?.length > 0) apiParams.owner = filterForm.owner.join(',');
    
    const result = await getContents(apiParams);
    // 按任务组分组，再本地分页
    allGroupedData.value = groupContentsByTask(result.content || []);
    applyLocalPagination();
  } catch (e: any) {
    console.error('加载内容失败:', e);
    message.error('加载内容失败');
  } finally {
    loading.value = false;
  }
};

// Tab 切换处理：同步更新 URL，防止刷新后丢失状态
const handleTabChange = () => {
  pagination.current = 1;
  // 更新路由参数而不刷新页面
  router.replace({ query: { ...route.query, tab: activeTab.value } });
  loadData();
};

// 监听路由变化，确保浏览器前进/后退时 Tab 同步切换
watch(() => route.query.tab, (newTab) => {
  if (newTab && newTab !== activeTab.value) {
    activeTab.value = newTab as any;
    loadData();
  }
});

onMounted(() => {
  initCurrentUserId();
  // 所有请求并行发送，互不阻塞 — loadData 不再等待其他辅助数据
  loadData();
  loadInfluencers();
  commonStore.loadUsers();
  commonStore.loadLiaisonTags();
  loadSkuList();
  loadStorageStats();
});

const filteredData = computed(() => {
  let result = [...allGroupedData.value];
  
  // 1. 任务 ID 批量搜索
  if (filterForm.taskId) {
    const ids = filterForm.taskId.split(/[\s,;]+/).filter(id => id.trim()).map(id => id.toLowerCase());
    if (ids.length > 0) {
      result = result.filter(item => ids.some(id => item.taskId.toLowerCase().includes(id)));
    }
  }

  // 2. 红人多选筛选
  if (filterForm.influencer && filterForm.influencer.length > 0) {
    result = result.filter(item => filterForm.influencer.includes(item.influencer));
  }

  // 3. 红人邮箱批量搜索
  if (filterForm.influencerEmail) {
    const emails = filterForm.influencerEmail.split(/[\s,;]+/).filter(e => e.trim()).map(e => e.toLowerCase());
    if (emails.length > 0) {
      result = result.filter(item => 
        item.influencerEmail && emails.some(email => item.influencerEmail.toLowerCase().includes(email))
      );
    }
  }

  // 4. 红人Handle批量搜索
  if (filterForm.defaultHandle) {
    const handles = filterForm.defaultHandle.split(/[\s,;]+/).filter(h => h.trim()).map(h => h.toLowerCase());
    if (handles.length > 0) {
      result = result.filter(item => 
        item.defaultHandle && handles.some(handle => item.defaultHandle.toLowerCase().includes(handle))
      );
    }
  }

  // 5. 平台多选搜索
  if (filterForm.platform && filterForm.platform.length > 0) {
    result = result.filter(item => filterForm.platform.includes(item.platform));
  }

  // 5. 负责人多选搜索 (ownerName)
  if (filterForm.owner && filterForm.owner.length > 0) {
    result = result.filter(item => filterForm.owner.includes(item.ownerName) || filterForm.owner.includes(item.owner));
  }

  // 联络员多选搜索 (contactPersonName)
  if (filterForm.contactPerson && filterForm.contactPerson.length > 0) {
    result = result.filter(item => filterForm.contactPerson.includes(item.contactPersonName));
  }

  // 6. 订单号批量搜索
  if (filterForm.orderNo) {
    const orderNos = filterForm.orderNo.split(/[\s,;]+/).filter(o => o.trim()).map(o => o.toLowerCase());
    if (orderNos.length > 0) {
      result = result.filter(item => 
        item.orderNo && orderNos.some(order => item.orderNo.toLowerCase().includes(order))
      );
    }
  }

  // 7. 商品 SKU 批量搜索
  if (filterForm.productSku) {
    const keywords = filterForm.productSku.split(/[\s,;]+/).filter(s => s.trim()).map(s => s.toLowerCase());
    if (keywords.length > 0) {
      result = result.filter(item => 
        (item.productSku && keywords.some(k => item.productSku.toLowerCase().includes(k))) ||
        (item.variantTitle && keywords.some(k => item.variantTitle.toLowerCase().includes(k)))
      );
    }
  }

  // 8. 时间范围筛选
  if (filterForm.timeRange && filterForm.timeRange.length === 2) {
    const startDate = dayjs(filterForm.timeRange[0]).startOf('day');
    const endDate = dayjs(filterForm.timeRange[1]).endOf('day');
    result = result.filter(item => {
      const itemDate = item.createdAt ? dayjs(item.createdAt) : null;
      if (!itemDate || !itemDate.isValid()) return true;
      return !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
    });
  }
  
  return result;
});

const handleTableChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  // 本地分页：从已加载的分组数据中切片
  applyLocalPagination();
};

const onSelectChange = (keys: (string | number)[]) => {
  selectedRowKeys.value = keys;
};

const handleResetFilter = () => {
  filterForm.taskId = '';
  filterForm.influencer = [];
  filterForm.defaultHandle = '';
  filterForm.influencerEmail = '';
  filterForm.platform = [];
  filterForm.owner = [];
  filterForm.contactPerson = [];
  filterForm.orderNo = '';
  filterForm.productSku = '';
  filterForm.timeRange = undefined;
  handleFilter();
};

const handleFilter = () => {
  pagination.current = 1;
  loadData();
};

const showCreateModal = () => {
  createModalVisible.value = true;
};

const handleCreateOk = () => {
    createModalVisible.value = false;
    loadData(); // 立即刷新列表以获取新记录及其正式 ID
};

const handleEditOk = () => {
  editModalVisible.value = false;
  loadData();
  message.success('修改成功');
};

const handleDeleteGroup = async (record: any) => {
  try {
    if (record.taskId) {
      // 如果 taskId 是 TASK-ID 这种由于没有 taskGroupId 生成的，尝试获取实体 ID
      const taskGroupId = record.taskId.startsWith('TASK-') ? null : record.taskId;
      
      if (taskGroupId) {
        await deleteTaskGroup(taskGroupId);
      } else {
        await deleteContent(record.key);
      }
      
      message.success('任务已删除');
      loadData();
      loadStorageStats(); // 删除后刷新空间占用
    }
  } catch (e: any) {
    console.error('删除任务失败:', e);
    message.error('删除失败: ' + (e.response?.data?.message || e.message));
  }
};

const handleUpload = (record: any) => {
  currentTask.value = { ...record };
  uploadReadonly.value = false;
  uploadModalVisible.value = true;
};

const handleUploadOk = () => {
  uploadModalVisible.value = false;
  loadData();
  loadStorageStats();
};

const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) return;
  
  const allIds: number[] = [];
  selectedRowKeys.value.forEach(key => {
    // 从全量数据中查找，而不只是当前页
    const item = allGroupedData.value.find(d => d.key === key);
    if (item && item.ids) {
      allIds.push(...item.ids);
    }
  });

  if (allIds.length === 0) {
    message.warning('请选择要删除的项目');
    return;
  }

  const hide = message.loading('正在批量删除...', 0);
  try {
    await batchDeleteContents(allIds);
    message.success('批量删除成功');
    selectedRowKeys.value = [];
    loadData();
    loadStorageStats();
  } catch (e: any) {
    console.error('批量删除失败:', e);
    message.error('批量删除失败: ' + (e.response?.data?.message || e.message));
  } finally {
    hide();
  }
};

const handleOrderNoChange = (newOrderNo: string) => {
  // 实时更新本地列表中的订单号，防止弹窗关闭后状态丢失
  if (currentTask.value) {
    currentTask.value.orderNo = newOrderNo;
    const target = data.value.find(item => item.key === currentTask.value.key);
    if (target) {
      target.orderNo = newOrderNo;
    }
  }
};

// 查看已完成任务详情
const uploadReadonly = ref(false);

const handleView = (record: any) => {
  currentTask.value = { ...record };
  uploadReadonly.value = true;
  uploadModalVisible.value = true;
};

const handleEdit = (record: any) => {
  currentTask.value = { ...record };
  editModalVisible.value = true;
};

// 导出相关
const contentExportFields = [
  { key: 'taskId', title: '任务ID' },
  { key: 'influencer', title: '红人' },
  { key: 'defaultHandle', title: '红人Handle' },
  { key: 'influencerEmail', title: '红人邮箱' },
  { key: 'platform', title: '目标平台' },
  { key: 'productSku', title: '商品SKU' },
  { key: 'orderNo', title: '关联订单' },
  { key: 'previewImage', title: '素材预览', type: 'image' },
  { key: 'postUrl', title: '素材发布链接' },
  { key: 'mediaUrl', title: '素材下载链接' },
  { key: 'arrivalStatus', title: '到货状态' },
  { key: 'status', title: '发布状态' },
  { key: 'owner', title: '负责人' },
  { key: 'createdAt', title: '创建时间' },
];

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async (payload: { scope: 'all' | 'selected'; fields: string[]; columns: any[]; templateId?: string; templateName?: string }) => {
  const { scope, columns, templateId, templateName } = payload;
  let exportData: any[] = [];

  if (scope === 'selected') {
    // 从全量数据中查找选中项，而不只是当前页
    exportData = allGroupedData.value.filter((item: any) => selectedRowKeys.value.includes(item.key));
  } else {
    // 导出全部 - 直接使用已加载的全量分组数据 
    exportData = [...allGroupedData.value];
  }

  if (exportData.length === 0) {
    message.warning('没有可导出的数据');
    return;
  }

  // Process data for export
  const processedData = exportData.map(item => ({
    ...item,
    previewImage: item.previewUrl || '',
    postUrl: item.postUrl || '',
    mediaUrl: item.filePath || '',
    arrivalStatus: item.arrivalStatus || '未送达',
    status: item.status === 'PENDING_UPLOAD' ? '待上传' : '已上传'
  }));
  
  createExportTask({
    data: processedData,
    columns: columns,
    filename: `${activeTab.value === 'pending' ? '待上传' : '已上传'}素材列表_${dayjs().format('YYYYMMDD_HHmmss')}`,
    pageType: activeTab.value === 'pending' ? 'content-pending' : 'content-completed',
    templateId: templateId,
    templateName: templateName
  });
};

</script>

<style lang="scss" scoped>
.pending-content-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;

  /* Refined Glass Card */
  .glass-card {
    background: #ffffff;
    border: 1px solid rgba(0, 0, 0, 0.04);
    border-radius: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    }
  }

  .storage-stats-mini {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 11px;
    color: #475569;
    background: rgba(241, 245, 249, 0.8);
    padding: 2px 10px;
    border-radius: 20px;
    border: 1px solid rgba(226, 232, 240, 0.5);

    .label {
      font-weight: 600;
      color: #1e293b;
    }

    .progress-bar-wrapper {
      width: 60px;
      height: 6px;
      background: #e2e8f0;
      border-radius: 4px;
      overflow: hidden;
      
      .progress-bar-inner {
        height: 100%;
        transition: width 0.3s ease;
      }
    }
  }

  .count-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 2px 10px;
    background: #f1f5f9;
    border-radius: 12px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    color: #94a3b8;
    border: 1px solid transparent;

    .pill-icon {
      font-size: 13px;
    }
    
    .pill-count {
      font-family: 'JetBrains Mono', monospace;
      font-weight: 700;
      font-size: 13px;
    }

    &.has-files {
      background: rgba(16, 185, 129, 0.1);
      color: #059669;
      border: 1px solid rgba(16, 185, 129, 0.2);
      box-shadow: 0 2px 8px rgba(16, 185, 129, 0.05);

      &:hover {
        transform: translateY(-1px);
        background: rgba(16, 185, 129, 0.15);
        border-color: rgba(16, 185, 129, 0.3);
      }
    }
  }

  .filter-card {
    margin-bottom: 8px !important;
    flex-shrink: 0;
    overflow: visible !important;
    background: #ffffff !important;
    border: 1px solid #e2e8f0 !important;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06) !important;
    
    :deep(.ant-card-body) {
      overflow: visible !important;
    }
    
    :deep(.ant-form-item) {
      margin-bottom: 6px;
      .ant-form-item-label {
        padding: 0 0 2px 0 !important;
        line-height: 1.4;
        > label {
          font-size: 13px;
          font-weight: 700;
          color: #334155;
          height: auto;
          margin-bottom: 4px;
        }
      }
      .ant-form-item-control-input {
        min-height: 32px;
      }
    }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;

    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 0 16px;
      min-height: 48px;
      display: flex;
      align-items: center;
      .ant-card-head-title { padding: 0; }
    }

    :deep(.ant-card-body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      padding: 0 !important;
    }
  }

  .table-actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .toolbar-btns {
       .premium-btn-small {
        height: 28px;
        padding: 0 12px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 6px;
        border: 1px solid #e2e8f0;
        background: #fff;
        color: #64748b;
        transition: all 0.3s;

        &:hover {
          border-color: #1890ff;
          color: #1890ff;
          background: #f0f7ff;
        }

        &.primary-gradient {
          background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
          border: none;
          color: #fff;
          
          &:hover {
            box-shadow: 0 4px 10px rgba(24, 144, 255, 0.2);
            transform: translateY(-1px);
          }
        }
      }
    }
  }

  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 500px;

    :deep(.ant-spin-nested-loading),
    :deep(.ant-spin-container),
    :deep(.ant-table),
    :deep(.ant-table-container) {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #ffffff;
    }

    /* 防止表头被 flex 挤压 */
    :deep(.ant-table-header) {
      flex-shrink: 0 !important;
    }

    :deep(.ant-table-content) {
      overflow: auto !important;
      &::-webkit-scrollbar { height: 8px; width: 8px; }
      &::-webkit-scrollbar-thumb { 
        background: #e2e8f0; 
        border-radius: 10px; 
        &:hover { background: #cbd5e1; }
      }
      &::-webkit-scrollbar-track { background: rgba(0, 0, 0, 0.02); border-radius: 10px; }
    }
    
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc;
      color: #64748b;
      font-weight: 700;
      font-size: 13px;
      border-bottom: 2px solid #f1f5f9;
      padding: 10px 8px; /* Compact Header */
      /* Fix selection column padding */
      &.ant-table-selection-column {
        padding: 0 8px !important;
      }
    }
    
    /* Fixed Column Background Fix - Corrected for Headers */
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left),
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right) {
      background: #f8fafc !important;
      z-index: 3;
    }

    /* Fixed Column Body Background */
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right) {
      background: #fff;
      z-index: 2;
    }

    /* Fix hover state for fixed columns */
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-right) {
      background: #f0f7ff !important;
    }

    :deep(.ant-table-tbody > tr > td) {
      border-bottom: 1px solid #f8fafc;
      padding: 8px 8px; /* Compact Body */
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }

    /* 修复列固定时的阴影重叠（遮罩） */
    :deep(.ant-table-cell-fix-left-last::after),
    :deep(.ant-table-cell-fix-right-first::after) {
      display: none !important;
    }
  }

  /* Status Tags and Platform Tags */
  .status-tag {
    border-radius: 20px;
    padding: 2px 12px;
    font-weight: 600;
    font-size: 11px;
    letter-spacing: 0.5px;
    border: none;
    
    &.tag-black { background: #0f172a; color: #fff; } /* TikTok */
    &.tag-pink { background: #fdf2f8; color: #db2777; } /* Instagram */
    &.tag-red { background: #fef2f2; color: #b91c1c; } /* YouTube */
    &.tag-blue { background: #eff6ff; color: #1e40af; } /* Facebook */
  }

  /* Action Buttons */
  .action-btns-wrapper {
    display: flex;
    gap: 4px;
    align-items: center;
    justify-content: center;
    
    .action-link {
        font-size: 12px;
        padding: 0 4px;
        &.primary { color: #1890ff; font-weight: 500; }
        &.danger { color: #ff4d4f; }
    }
  }

  /* Compact Pagination Footer */
  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    z-index: 10;
    
    .footer-left {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #94a3b8;
      font-size: 12px;
      
      .count-value {
        font-weight: 600;
        color: #1e293b;
        background: #f1f5f9;
        padding: 1px 6px;
        border-radius: 4px;
        
        &.highlight {
          color: #1890ff;
          background: #e6f7ff;
        }
      }
    }
  }
  
  /* Premium Inputs */
  .premium-input, .premium-datepicker, .premium-input-search {
    border-radius: 8px !important;
    border-color: #cbd5e1 !important;
    background: #f8fafc !important;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    height: 32px !important;
    padding: 0 11px !important;
    display: flex;
    align-items: center;
    font-size: 13px;
    color: #1e293b;

    &:hover {
      border-color: #94a3b8 !important;
      background: #fff !important;
    }
    
    &:focus {
        border-color: #1890ff !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1) !important;
        background: #ffffff !important;
    }
  }

  .premium-select :deep(.ant-select-selector) {
    border-radius: 8px !important;
    border-color: #cbd5e1 !important;
    background: #f8fafc !important;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    min-height: 32px !important;
    padding: 0 11px !important;
    font-size: 13px;
    color: #1e293b;
  }

  .premium-select:hover :deep(.ant-select-selector) {
    border-color: #94a3b8 !important;
    background: #fff !important;
  }

  .premium-select.ant-select-focused :deep(.ant-select-selector) {
    border-color: #1890ff !important;
    box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1) !important;
    background: #ffffff !important;
  }
  
  :deep(.ant-select-selection-item), :deep(.ant-select-placeholder) {
    font-size: 13px !important;
  }

  :deep(.ant-select-selection-item) {
    color: #1e293b !important;
    font-weight: 500;
  }

  :deep(.ant-select-placeholder) {
    color: #94a3b8 !important;
  }

  :deep(.ant-input::placeholder) {
    color: #94a3b8 !important;
  }
  
  /* Global Premium Scrollbar */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  ::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 10px;
    transition: background 0.3s;
    &:hover { background: #cbd5e1; }
  }
  ::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.02);
    border-radius: 10px;
  }
  
  /* Premium Buttons */
  .premium-btn {
    border-radius: 6px;
    height: 32px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-weight: 500;
    transition: all 0.3s;
    &.primary-gradient {
      background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
      border: none;
      color: #fff;
      box-shadow: 0 2px 6px rgba(29, 78, 216, 0.15);
      
      &:hover {
        box-shadow: 0 4px 10px rgba(29, 78, 216, 0.2);
        transform: translateY(-1px);
      }
    }
  }
}
</style>

<style lang="scss">
/* Global Premium Modal Styles for Portals */
.influencer-create-modal {
  .ant-modal-content {
    padding: 0 !important;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .ic-modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 24px;
    background: #ffffff;
    border-bottom: 1px solid #f1f5f9;
    
    &.glass-header {
      background: rgba(255, 255, 255, 0.95);
    }
    
    .ic-header-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    
    .ic-header-icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
    }
    
    .ic-header-text {
      .ic-header-title {
        font-size: 18px;
        font-weight: 700;
        color: #1e293b;
      }
      .ic-header-subtitle {
        font-size: 12px;
        color: #94a3b8;
      }
    }
    
    .close-btn {
      color: #94a3b8;
      &:hover {
        color: #64748b;
        background: #f1f5f9;
      }
    }
  }

  .ic-modal-body {
    padding: 20px 24px;
    background: #ffffff;
    
    .premium-textarea {
      border-radius: 8px;
      border-color: #e2e8f0;
      font-size: 14px;
      
      &:focus {
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
      }
    }
  }

  .ic-modal-footer {
    padding: 16px 24px;
    background: #ffffff;
    border-top: 1px solid #f1f5f9;
    text-align: right;
    
    .premium-btn {
      height: 36px;
      padding: 0 24px;
      border-radius: 8px;
      font-weight: 600;
      
      &.primary-gradient {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border: none;
        color: #ffffff;
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 6px 15px rgba(16, 185, 129, 0.3);
        }
      }
    }
  }
}

  /* SKU List Display Styles */
  .p-sku-tag-list {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    gap: 2px;
    font-family: 'JetBrains Mono', monospace;
    font-size: 13px;
    white-space: nowrap;
    
    .sku-main {
      color: #0369a1;
      font-weight: 700;
    }
    
    .sku-spec {
      font-weight: 600;
      &.spec-0 { color: #16a34a; }
      &.spec-1 { color: #ea580c; }
      &.spec-2 { color: #8b5cf6; }
    }
  }
</style>