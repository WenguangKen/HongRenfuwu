<template>
  <div class="content-manage-page">
    <a-row justify="space-between" align="middle" class="page-header">
      <a-col>
        <h2 class="page-title">内容管理</h2>
        <p class="page-subtitle">统一管理达人内容素材，支持上传前审核与内容复用</p>
      </a-col>
      <a-col>
        <a-space>
          <a-button type="default" @click="handleRefresh">
            <template #icon>
              <reload-outlined />
            </template>
            刷新
          </a-button>
          <a-button type="primary">
            <template #icon>
              <plus-outlined />
            </template>
            新建内容
          </a-button>
        </a-space>
      </a-col>
    </a-row>

    <a-card :bordered="false" class="content-card">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="pending" tab="待上传">
          <a-table
            :columns="pendingColumns"
            :data-source="pendingList"
            :loading="pendingLoading"
            row-key="id"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space :size="8">
                  <a-button type="link" size="small" @click="handleView(record)">
                    查看
                  </a-button>
                  <a-button type="link" size="small" @click="handleEdit(record)">
                    编辑
                  </a-button>
                  <a-button type="link" size="small" danger @click="handleDelete(record)">
                    删除
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
        <a-tab-pane key="library" tab="内容库">
          <a-table
            :columns="libraryColumns"
            :data-source="libraryList"
            :loading="libraryLoading"
            row-key="id"
            :pagination="false"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space :size="8">
                  <a-button type="link" size="small" @click="handleView(record)">
                    查看
                  </a-button>
                  <a-button type="link" size="small" @click="handleEdit(record)">
                    编辑
                  </a-button>
                  <a-button type="link" size="small" danger @click="handleDelete(record)">
                    删除
                  </a-button>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-card>

    <!-- 标签编辑弹窗 -->
    <a-modal
      v-model:open="tagModalVisible"
      title="编辑标签"
      @ok="handleSaveTags"
      :confirmLoading="savingTags"
      width="400px"
      class="glass-modal"
    >
      <div style="padding: 12px 0">
        <p style="color: #64748b; font-size: 13px; margin-bottom: 8px">为此内容添加或修改分类标签：</p>
        <a-select
          v-model:value="editingTags"
          mode="multiple"
          placeholder="请选择标签"
          style="width: 100%"
          :options="tagOptions"
          allow-clear
          class="premium-select"
        />
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, h, onMounted, watch } from 'vue';
import { PlusOutlined, ReloadOutlined, ExclamationCircleFilled } from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import { showDeleteConfirm } from '@/utils/modal';
import contentService, { type ContentDto } from '@/services/contentService';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';

const activeTab = ref<'pending' | 'library'>('pending');

const pendingLoading = ref(false);
const libraryLoading = ref(false);

const pendingList = ref<ContentDto[]>([]);
const libraryList = ref<ContentDto[]>([]);
const tagModalVisible = ref(false);
const editingTags = ref<number[]>([]);
const currentRecord = ref<ContentDto | null>(null);
const savingTags = ref(false);
const commonStore = useCommonDataStore();
const { contentTags } = storeToRefs(commonStore);
const tagOptions = computed(() => contentTags.value.map((t: any) => ({ value: t.id, label: t.name })));

// 加载待上传列表
const loadPendingList = async () => {
  pendingLoading.value = true;
  try {
    const result = await contentService.getContents({ status: 'PENDING_UPLOAD', size: 50 });
    pendingList.value = result.content || [];
  } catch (e: any) {
    console.error('Failed to load pending list:', e);
    message.error('加载待上传列表失败');
  } finally {
    pendingLoading.value = false;
  }
};

// 加载内容库列表
const loadLibraryList = async () => {
  libraryLoading.value = true;
  try {
    const result = await contentService.getContents({ status: 'PENDING_REVIEW', size: 50 });
    libraryList.value = result.content || [];
  } catch (e: any) {
    console.error('Failed to load library list:', e);
    message.error('加载内容库失败');
  } finally {
    libraryLoading.value = false;
  }
};

// 刷新当前 tab 数据
const handleRefresh = () => {
  if (activeTab.value === 'pending') {
    loadPendingList();
  } else {
    loadLibraryList();
  }
  message.success('刷新成功');
};

// Tab 切换时加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'pending' && pendingList.value.length === 0) {
    loadPendingList();
  } else if (newTab === 'library' && libraryList.value.length === 0) {
    loadLibraryList();
  }
});

onMounted(() => {
  loadPendingList();
  commonStore.loadContentTags();
});


const pendingColumns = [
  { title: '内容标题', dataIndex: 'title', key: 'title' },
  { title: '达人ID', dataIndex: 'influencerId', key: 'influencerId' },
  { title: '目标平台', dataIndex: 'platform', key: 'platform' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '负责人', dataIndex: 'ownerName', key: 'ownerName' },
  { title: '联络员', dataIndex: 'contactPersonName', key: 'contactPersonName' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 180, fixed: 'right' },
];

const libraryColumns = [
  { title: '内容标题', dataIndex: 'title', key: 'title' },
  { title: '达人ID', dataIndex: 'influencerId', key: 'influencerId' },
  { title: '负责人', dataIndex: 'ownerName', key: 'ownerName' },
  { title: '联络员', dataIndex: 'contactPersonName', key: 'contactPersonName' },
  { title: '平台', dataIndex: 'platform', key: 'platform' },
  { title: '文件名', dataIndex: 'fileName', key: 'fileName' },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 180, fixed: 'right' },
];

const handleView = (record: ContentDto) => {
  message.info(`查看内容：${record.title}`);
  // TODO: 打开查看详情弹窗
};

const handleEdit = (record: ContentDto) => {
  currentRecord.value = record;
  editingTags.value = record.tagIds || [];
  tagModalVisible.value = true;
};

const handleSaveTags = async () => {
  if (!currentRecord.value) return;
  savingTags.value = true;
  try {
    await contentService.updateContent(currentRecord.value.id, {
      tagIds: editingTags.value
    });
    currentRecord.value.tagIds = [...editingTags.value];
    message.success('标签更新成功');
    tagModalVisible.value = false;
  } catch (e: any) {
    message.error('保存失败');
  } finally {
    savingTags.value = false;
  }
};

const performDownload = (record: any) => {
  // Implement actual download logic here
  message.success(`开始下载：${record.fileName || record.title}`);

  // Example: window.open(record.downloadUrl, '_blank');
};

const handleDownload = (record: any) => {
  if (record.isCommercial === false) {
    Modal.confirm({
      title: '法律风险警告',
      icon: h(ExclamationCircleFilled),
      content: '未授权商用素材使用有法律风险，谨慎使用！！！',
      okText: '确认下载',
      cancelText: '取消',
      onOk: () => {
        performDownload(record);
      }
    });
  } else {
    performDownload(record);
  }
};

const handleDelete = (record: ContentDto) => {
  showDeleteConfirm({
    title: '确定要删除该内容吗？',
    content: '删除后无法恢复，请谨慎操作。',
    onOk: async () => {
      try {
        await contentService.deleteContent(record.id);
        if (activeTab.value === 'pending') {
          pendingList.value = pendingList.value.filter((item) => item.id !== record.id);
        } else {
          libraryList.value = libraryList.value.filter((item) => item.id !== record.id);
        }
        message.success('删除成功');
      } catch (e: any) {
        message.error('删除失败: ' + (e.message || '未知错误'));
      }
    },
  });
};
</script>

<style lang="scss" scoped>
.content-manage-page {
  .page-header {
    margin-bottom: 16px;
  }

  .page-title {
    margin: 0;
    font-size: 22px;
    font-weight: 600;
    color: #1f1f1f;
  }

  .page-subtitle {
    margin: 4px 0 0;
    color: #8c8c8c;
    font-size: 13px;
  }

  .content-card {
    border-radius: 12px;
  }
}
</style>

