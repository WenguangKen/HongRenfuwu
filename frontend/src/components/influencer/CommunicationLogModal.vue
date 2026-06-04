<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="600px"
    :footer="null"
    centered
    :closable="false"
    class="premium-refined-modal"
    destroyOnClose
  >
    <!-- 顶部 -->
    <div class="log-header">
      <div class="log-header-info">
        <div class="log-title">沟通记录</div>
        <div class="log-subtitle">{{ influencerName || '红人' }}</div>
      </div>
      <a-button type="text" @click="visible = false" style="color: #64748b;">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 记录列表 -->
    <div class="log-list-area" ref="listContainer">
      <div v-if="loading" class="center-box"><a-spin /></div>
      <a-empty v-else-if="logs.length === 0" description="暂无沟通记录" style="padding: 50px 0;" />
      <div v-else class="log-records">
        <div v-for="log in logs" :key="log.id" class="log-record">
          <div class="record-left">
            <div class="record-dot"></div>
            <div class="record-line"></div>
          </div>
          <div class="record-right">
            <div class="record-header">
              <span class="record-operator">{{ log.operatorName }}</span>
              <span class="record-time">{{ formatTime(log.createdAt) }}</span>
            </div>
            <div class="record-content" v-if="log.content">{{ log.content }}</div>
            <div class="record-images" v-if="parseImages(log.imageUrls).length">
              <a-image-preview-group>
                <a-image
                  v-for="(url, idx) in parseImages(log.imageUrls)"
                  :key="idx"
                  :src="url"
                  :width="120"
                  class="record-img"
                />
              </a-image-preview-group>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入 -->
    <div class="log-input-area">
      <!-- 图片预览 -->
      <div v-if="imagePreviews.length" class="image-preview-bar">
        <div v-for="(p, idx) in imagePreviews" :key="idx" class="preview-item">
          <img :src="p" class="preview-thumb" />
          <span class="preview-remove" @click="removeImage(idx)"><CloseOutlined /></span>
        </div>
      </div>
      <div class="input-row">
        <a-input
          ref="inputRef"
          v-model:value="newContent"
          placeholder="输入沟通内容（可粘贴图片）..."
          :maxlength="200"
          allow-clear
          @pressEnter="handleAdd"
          @paste="handlePaste"
        />
        <a-upload
          :before-upload="handleSelectImage"
          :show-upload-list="false"
          accept="image/*"
          :multiple="true"
        >
          <a-button title="上传图片"><PictureOutlined /></a-button>
        </a-upload>
        <a-button
          type="primary"
          :loading="submitting"
          :disabled="!newContent.trim() && !imageFiles.length"
          @click="handleAdd"
        >确定</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import { CloseOutlined, PictureOutlined } from '@ant-design/icons-vue';
import { influencerService } from '@/services/influencerService';
import dayjs from 'dayjs';

const props = defineProps<{
  open: boolean;
  influencerId: number | string;
  influencerName?: string;
}>();
const emit = defineEmits<{ (e: 'update:open', v: boolean): void }>();

const visible = ref(false);
const logs = ref<any[]>([]);
const loading = ref(false);
const newContent = ref('');
const submitting = ref(false);
const listContainer = ref<HTMLElement | null>(null);
const inputRef = ref();
const imageFiles = ref<File[]>([]);
const imagePreviews = ref<string[]>([]);

watch(() => props.open, v => { visible.value = v; if (v && props.influencerId) { loadLogs(); newContent.value = ''; clearImages(); } });
watch(visible, v => emit('update:open', v));

const scrollToBottom = () => nextTick(() => { if (listContainer.value) listContainer.value.scrollTop = listContainer.value.scrollHeight; });

const loadLogs = async () => {
  loading.value = true;
  try {
    const res = await influencerService.getCommunicationLogs(Number(props.influencerId));
    logs.value = Array.isArray(res) ? [...res].reverse() : [];
    scrollToBottom();
  } catch { logs.value = []; }
  finally { loading.value = false; }
};

const parseImages = (imageUrls: string | null): string[] => {
  if (!imageUrls) return [];
  try { return JSON.parse(imageUrls); } catch { return []; }
};

const handleSelectImage = (file: File) => {
  imageFiles.value.push(file);
  imagePreviews.value.push(URL.createObjectURL(file));
  return false;
};

const handlePaste = (e: ClipboardEvent) => {
  const items = e.clipboardData?.items;
  if (!items) return;
  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (item && item.type.startsWith('image/')) {
      const file = item.getAsFile();
      if (file) {
        imageFiles.value.push(file);
        imagePreviews.value.push(URL.createObjectURL(file));
      }
      e.preventDefault();
    }
  }
};

const removeImage = (idx: number) => {
  imageFiles.value.splice(idx, 1);
  imagePreviews.value.splice(idx, 1);
};

const clearImages = () => {
  imageFiles.value = [];
  imagePreviews.value = [];
};

const handleAdd = async () => {
  if (!newContent.value.trim() && !imageFiles.value.length) return;
  submitting.value = true;
  try {
    await influencerService.addCommunicationLog(
      Number(props.influencerId),
      newContent.value.trim(),
      imageFiles.value.length > 0 ? imageFiles.value : undefined
    );
    message.success('记录已添加');
    newContent.value = '';
    clearImages();
    await loadLogs();
  } catch { message.error('添加失败'); }
  finally { submitting.value = false; }
};

const formatTime = (t: string) => t ? dayjs(t).format('YYYY-MM-DD HH:mm') : '-';
</script>

<style scoped lang="scss">
.log-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
}
.log-title { font-size: 16px; font-weight: 700; color: #1e293b; }
.log-subtitle { font-size: 12px; color: #94a3b8; margin-top: 2px; }

.log-list-area {
  height: 400px;
  overflow-y: auto;
  padding: 16px 20px;
  background: #fafbfc;
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }
}
.center-box { display: flex; justify-content: center; padding: 50px; }
.log-records { display: flex; flex-direction: column; }

.log-record {
  display: flex;
  gap: 12px;
  &:last-child .record-line { display: none; }
}

.record-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 6px;
  width: 12px;
  flex-shrink: 0;
}
.record-dot { width: 8px; height: 8px; border-radius: 50%; background: #3b82f6; flex-shrink: 0; }
.record-line { width: 1px; flex: 1; background: #e2e8f0; margin-top: 4px; }

.record-right {
  flex: 1;
  padding-bottom: 16px;
  min-width: 0;
}
.record-header { display: flex; align-items: center; gap: 8px; margin-bottom: 4px; }
.record-operator { font-size: 13px; font-weight: 600; color: #334155; }
.record-time { font-size: 11px; color: #94a3b8; }
.record-content {
  font-size: 13px; color: #475569; line-height: 1.6;
  white-space: pre-wrap; word-break: break-word;
  background: #fff; padding: 8px 12px; border-radius: 6px; border: 1px solid #e2e8f0;
}
.record-images {
  margin-top: 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  .record-img { border-radius: 6px; cursor: pointer; border: 1px solid #e2e8f0; }
}

.log-input-area {
  padding: 12px 20px;
  border-top: 1px solid #f1f5f9;
  background: #fff;
}
.image-preview-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}
.preview-item {
  position: relative;
  display: inline-block;
}
.preview-thumb {
  height: 52px; width: 52px;
  border-radius: 4px;
  border: 1px solid #e2e8f0;
  object-fit: cover;
}
.preview-remove {
  position: absolute;
  top: -6px; right: -6px;
  width: 18px; height: 18px;
  background: #ef4444;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}
.input-row {
  display: flex;
  gap: 8px;
}
</style>
