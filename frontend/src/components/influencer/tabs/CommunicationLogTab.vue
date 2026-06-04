<template>
  <div class="comm-log-tab-wrapper">
    <!-- 记录列表 -->
    <div class="log-list-area" ref="listContainer">
      <div v-if="loading" class="center-box"><a-spin /></div>
      <a-empty v-else-if="logs.length === 0" description="暂无沟通记录" style="padding: 40px 0;" />
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
                  :width="100"
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
      <div v-if="imagePreviews.length" class="image-preview-bar">
        <div v-for="(p, idx) in imagePreviews" :key="idx" class="preview-item">
          <img :src="p" class="preview-thumb" />
          <span class="preview-remove" @click="removeImage(idx)"><CloseOutlined /></span>
        </div>
      </div>
      <div class="input-row">
        <a-input
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import { message } from 'ant-design-vue';
import { CloseOutlined, PictureOutlined } from '@ant-design/icons-vue';
import { influencerService } from '@/services/influencerService';
import dayjs from 'dayjs';

const props = defineProps<{ influencerId: number | string }>();

const logs = ref<any[]>([]);
const loading = ref(false);
const newContent = ref('');
const submitting = ref(false);
const listContainer = ref<HTMLElement | null>(null);
const imageFiles = ref<File[]>([]);
const imagePreviews = ref<string[]>([]);

onMounted(() => { if (props.influencerId) loadLogs(); });

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
    if (items[i]?.type.startsWith('image/')) {
      const file = items[i]?.getAsFile();
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
.comm-log-tab-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.log-list-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
  background: #fafbfc;
  min-height: 0;
  &::-webkit-scrollbar { width: 4px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 2px; }
}
.center-box { display: flex; justify-content: center; padding: 40px; }
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
  padding-bottom: 14px;
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
  border-top: 1px solid #e2e8f0;
  background: #fff;
  flex-shrink: 0;
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
