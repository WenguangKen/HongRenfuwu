<template>
  <a-modal
    v-model:open="modalVisible"
    :title="null"
    width="900px"
    :footer="null"
    centered
    class="content-detail-modal"
    @cancel="handleCancel"
    :closable="false"
  >
    <div class="cd-modal-header">
      <div class="cd-header-left">
        <div class="cd-header-icon">
          <VideoCameraOutlined />
        </div>
        <div class="cd-header-text">
          <div class="cd-header-title">
            关联内容详情
            <a-tag color="blue" class="header-name-tag">{{ influencerName }}</a-tag>
          </div>
          <div class="cd-header-subtitle">查看红人发布的关联内容及素材</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <div class="cd-modal-body">
      <a-table
        :columns="columns"
        :data-source="data"
        :pagination="pagination"
        :loading="loading"
        :scroll="{ y: 500 }"
        @change="handleTableChange"
        class="premium-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'taskId'">
            <div class="task-id-cell">{{ record.taskId }}</div>
          </template>
          <template v-else-if="column.key === 'platform'">
            <div class="platform-cell">
              <a-tag :class="['platform-tag', `platform-${record.platform.toLowerCase()}`]">
                {{ record.platform }}
              </a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'content'">
            <div class="content-cell">
              <div v-if="record.images && record.images.length > 0" class="media-group">
                <div 
                  v-for="(img, idx) in record.images.slice(0, 3)" 
                  :key="idx"
                  class="media-item image"
                  @click="previewImage(img)"
                >
                  <img :src="img" alt="内容图片" />
                  <div class="hover-overlay"><EyeOutlined /></div>
                </div>
                <div v-if="record.images.length > 3" class="media-more">
                  +{{ record.images.length - 3 }}
                </div>
              </div>
              
              <div v-if="record.videos && record.videos.length > 0" class="media-group">
                <div 
                  v-for="(video, idx) in record.videos.slice(0, 2)" 
                  :key="idx"
                  class="media-item video"
                  @click="previewVideo(video)"
                >
                  <PlayCircleOutlined class="play-icon" />
                </div>
                <div v-if="record.videos.length > 2" class="media-more">
                  +{{ record.videos.length - 2 }}
                </div>
              </div>

              <div v-if="record.link" class="link-wrapper">
                <a :href="record.link" target="_blank" class="content-link">
                  <LinkOutlined /> 查看链接
                </a>
              </div>
            </div>
          </template>
        </template>
      </a-table>
    </div>
    
    <!-- 图片预览Modal -->
    <a-modal
      v-model:open="imagePreviewVisible"
      :footer="null"
      centered
      width="800px"
      class="image-preview-modal"
      :body-style="{ padding: 0, background: 'transparent' }"
      :closable="false"
    >
      <div class="preview-container" @click="imagePreviewVisible = false">
        <img :src="previewImageUrl" alt="预览" />
      </div>
    </a-modal>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { PlayCircleOutlined, LinkOutlined, VideoCameraOutlined, CloseOutlined, EyeOutlined } from '@ant-design/icons-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  open: boolean;
  influencerId: string;
  influencerName: string;
}>();

const emit = defineEmits<{
  'update:open': [value: boolean];
}>();

const modalVisible = ref(false);
const loading = ref(false);
const imagePreviewVisible = ref(false);
const previewImageUrl = ref('');

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`,
});

const columns: TableColumnsType = [
  {
    title: '任务ID',
    key: 'taskId',
    width: 150,
    fixed: 'left',
  },
  {
    title: '关联平台',
    key: 'platform',
    width: 120,
    align: 'center',
  },
  {
    title: '关联SKU',
    dataIndex: 'sku',
    key: 'sku',
    width: 150,
  },
  {
    title: '内容',
    key: 'content',
    width: 350,
  },
  {
    title: '上传时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    align: 'center',
  },
];

const data = ref<any[]>([]);

const getStatusClass = (status: string) => {
  const map: Record<string, string> = {
    '待上传': 'pending',
    '已完成': 'success',
    '审核中': 'processing',
  };
  return map[status] || 'default';
};

const loadData = () => {
  loading.value = true;
  // Mock data
  setTimeout(() => {
    const mockData = Array.from({ length: 15 }).map((_, i) => ({
      key: i,
      taskId: `TASK-${2025000 + i}`,
      platform: ['TikTok', 'Instagram', 'YouTube', 'Facebook'][i % 4],
      sku: `SKU-${1000 + i}`,
      images: [
        `https://picsum.photos/200/200?random=${i * 2}`,
        `https://picsum.photos/200/200?random=${i * 2 + 1}`,
      ],
      videos: i % 3 === 0 ? [`video-${i}.mp4`] : [],
      link: i % 2 === 0 ? `https://example.com/content/${i}` : '',
      status: ['待上传', '已完成', '审核中'][i % 3],
      createTime: dayjs().subtract(i, 'day').format('YYYY-MM-DD HH:mm:ss'),
    }));
    data.value = mockData;
    pagination.total = mockData.length;
    loading.value = false;
  }, 500);
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
  loadData();
};

const handleCancel = () => {
  modalVisible.value = false;
  emit('update:open', false);
};

const previewImage = (url: string) => {
  previewImageUrl.value = url;
  imagePreviewVisible.value = true;
};

const previewVideo = (_video: string) => {
  // 这里可以实现视频预览逻辑
};

watch(() => props.open, (val) => {
  modalVisible.value = val;
  if (val) {
    loadData();
  }
});

watch(modalVisible, (val) => {
  if (!val) {
    emit('update:open', false);
  }
});
</script>

<style lang="scss" scoped>
:deep(.content-detail-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
  border-radius: 16px;
}

.cd-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.8) 0%, rgba(248, 250, 252, 0.8) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(10px);
}

.cd-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.cd-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.cd-header-text {
  .cd-header-title {
    font-size: 16px;
    font-weight: 700;
    color: #1e293b;
    margin-bottom: 2px;
    display: flex;
    align-items: center;
    gap: 8px;
  }
  .cd-header-subtitle {
    font-size: 12px;
    color: #64748b;
  }
}

.header-name-tag {
  border: none;
  background: #eff6ff;
  color: #3b82f6;
  font-weight: 600;
  border-radius: 6px;
  margin: 0;
}

.close-btn {
  border-radius: 8px;
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

.cd-modal-body {
  padding: 0;
  background: #fff;
}

.task-id-cell {
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
  color: #475569;
}

.platform-tag {
  border: none;
  font-weight: 600;
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
  
  &.platform-tiktok { background: #1a1a1a; color: #fff; }
  &.platform-instagram { background: linear-gradient(45deg, #f09433 0%, #e6683c 25%, #dc2743 50%, #cc2366 75%, #bc1888 100%); color: #fff; }
  &.platform-youtube { background: #ff0000; color: #fff; }
  &.platform-facebook { background: #1877f2; color: #fff; }
}

.status-tag {
  border: none;
  border-radius: 6px;
  padding: 2px 8px;
  font-weight: 600;
  font-size: 12px;
  
  &.status-pending { background: #fff7ed; color: #ea580c; }
  &.status-success { background: #f0fdf4; color: #16a34a; }
  &.status-processing { background: #eff6ff; color: #2563eb; }
}

.content-cell {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12px;

  .media-group {
    display: flex;
    flex-wrap: nowrap;
    gap: 6px;
    align-items: center;
    
    .media-item {
      width: 40px;
      height: 40px;
      border-radius: 6px;
      overflow: hidden;
      cursor: pointer;
      position: relative;
      border: 1px solid #e2e8f0;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s;
      }

      .hover-overlay {
        position: absolute;
        top: 0; left: 0; right: 0; bottom: 0;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        opacity: 0;
        transition: opacity 0.2s;
      }

      &:hover {
        img { transform: scale(1.1); }
        .hover-overlay { opacity: 1; }
      }

      &.video {
        background: #f8fafc;
        display: flex;
        align-items: center;
        justify-content: center;
        .play-icon { font-size: 16px; color: #64748b; }
        &:hover .play-icon { color: #3b82f6; }
      }
    }

    .media-more {
      width: 40px;
      height: 40px;
      border-radius: 6px;
      background: #f1f5f9;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #64748b;
      font-size: 12px;
      font-weight: 600;
      flex-shrink: 0;
    }
  }

  .link-wrapper {
    margin-left: auto; /* Push link to the right or keep it close if desired, user said 'one line' */
    flex-shrink: 0;
    
    .content-link {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #3b82f6;
      background: #eff6ff;
      padding: 2px 8px;
      border-radius: 4px;
      transition: all 0.2s;
      white-space: nowrap;
      
      &:hover {
        background: #dbeafe;
        text-decoration: none;
      }
    }
  }
}

.image-preview-modal {
  .preview-container {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: zoom-out;
    img {
      max-width: 100%;
      max-height: 80vh;
      border-radius: 8px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
    }
  }
}

.premium-table {
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc;
    color: #475569;
    font-weight: 600;
    font-size: 13px;
    padding: 12px 16px;
    border-bottom: 1px solid #e2e8f0;
  }
  
  :deep(.ant-table-tbody > tr > td) {
    padding: 12px 16px;
    border-bottom: 1px solid #f1f5f9;
  }

  :deep(.ant-table-tbody > tr:hover > td) {
    background: #f8fafc !important;
  }

  :deep(.ant-table-pagination) {
    width: 100%;
    margin: 16px 0 !important;
    display: flex !important;
    align-items: center;
    
    .ant-pagination-total-text {
      margin-right: auto;
      margin-left: 8px;
    }
  }
}
</style>
