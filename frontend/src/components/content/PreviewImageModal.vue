<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    :footer="null"
    centered
    :width="1080"
    class="luxe-preview-modal"
    :closable="false"
    destroyOnClose
  >
    <div class="preview-shell">
      <!-- Floating close -->
      <button class="floating-close" @click="handleClose">
        <CloseOutlined />
      </button>

      <!-- Counter pill -->
      <div v-if="dataList && dataList.length > 1" class="counter-pill">
        {{ currentIndex + 1 }}<span class="sep">/</span>{{ dataList.length }}
      </div>

      <!-- Main stage -->
      <div class="stage-area">
        <button v-if="dataList && dataList.length > 1" class="arrow-btn arrow-prev" @click.stop="handlePrev" :disabled="currentIndex === 0">
          <LeftOutlined />
        </button>

        <div class="media-viewport">
          <div class="media-glow" :class="currentType === 'video' ? 'glow-violet' : 'glow-indigo'"></div>
          <!-- Image -->
          <img 
            v-if="currentType === 'image'" 
            :src="currentData?.url" 
            alt="Preview" 
            @load="onImgLoad" 
            ref="imgRef" 
          />
          <!-- Video -->
          <video 
            v-else 
            ref="videoRef"
            :key="currentData?.url"
            :src="currentData?.url" 
            controls 
            @loadedmetadata="onVideoLoaded"
          />
        </div>

        <button v-if="dataList && dataList.length > 1" class="arrow-btn arrow-next" @click.stop="handleNext" :disabled="currentIndex === (dataList?.length || 1) - 1">
          <RightOutlined />
        </button>
      </div>

      <!-- Bottom dock -->
      <div class="bottom-dock">
        <div class="dock-left">
          <div class="file-chip" :class="currentType === 'video' ? 'chip-video' : 'chip-image'">
            <VideoCameraOutlined v-if="currentType === 'video'" class="chip-icon" />
            <PictureOutlined v-else class="chip-icon" />
            <span class="chip-name">{{ truncateFileName(currentData?.name) || (currentType === 'video' ? '视频' : '图片') }}</span>
            <span class="chip-ext">.{{ getFileExtension(currentData?.name) || (currentType === 'video' ? 'MP4' : 'PNG') }}</span>
          </div>
        </div>
        <div class="dock-center">
          <div class="meta-item" v-if="displayResolution !== '-'">
            <span class="meta-label">分辨率</span>
            <span class="meta-val">{{ displayResolution }}</span>
          </div>
          <div class="meta-divider" v-if="displayResolution !== '-' && hasNextMeta"></div>
          <div class="meta-item" v-if="currentData?.size && currentData.size !== '-'">
            <span class="meta-label">大小</span>
            <span class="meta-val">{{ currentData.size }}</span>
          </div>
          <div class="meta-divider" v-if="videoDuration !== '-'"></div>
          <div class="meta-item" v-if="videoDuration !== '-'">
            <span class="meta-label">时长</span>
            <span class="meta-val">{{ videoDuration }}</span>
          </div>
          <div class="meta-divider" v-if="currentData?.format"></div>
          <div class="meta-item" v-if="currentData?.format">
            <span class="meta-label">格式</span>
            <span class="meta-val accent">{{ currentData.format }}</span>
          </div>
        </div>
        <div class="dock-right">
          <button class="dl-btn" :class="currentType === 'video' ? 'dl-video' : 'dl-image'" @click="handleDownload" :disabled="downloading">
            <DownloadOutlined />
            <span>{{ downloading ? '下载中...' : '下载' }}</span>
          </button>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted, onUnmounted } from 'vue';
import { DownloadOutlined, PictureOutlined, VideoCameraOutlined, CloseOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';

const props = defineProps<{
  open: boolean;
  data: any;
  dataList?: any[];
  initialIndex?: number;
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
}>();

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const currentIndex = ref(props.initialIndex || 0);
const imgRef = ref<HTMLImageElement | null>(null);
const videoRef = ref<HTMLVideoElement | null>(null);
const naturalWidth = ref(0);
const naturalHeight = ref(0);
const videoWidth = ref(0);
const videoHeight = ref(0);
const videoDurationSeconds = ref(0);

const currentData = computed(() => {
  if (props.dataList && props.dataList.length > 0) {
    return props.dataList[currentIndex.value];
  }
  return props.data;
});

const currentType = computed(() => {
  return currentData.value?.type === 'video' ? 'video' : 'image';
});

const displayResolution = computed(() => {
  const d = currentData.value;
  if (d?.resolution && d.resolution !== '-') return d.resolution;
  if (currentType.value === 'video' && videoWidth.value && videoHeight.value) {
    return `${videoWidth.value} × ${videoHeight.value}`;
  }
  if (currentType.value === 'image' && naturalWidth.value && naturalHeight.value) {
    return `${naturalWidth.value} × ${naturalHeight.value}`;
  }
  return '-';
});

const videoDuration = computed(() => {
  if (currentType.value !== 'video') return '-';
  if (videoDurationSeconds.value > 0) {
    const mins = Math.floor(videoDurationSeconds.value / 60);
    const secs = Math.floor(videoDurationSeconds.value % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
  return currentData.value?.duration || '-';
});

const hasNextMeta = computed(() => {
  return (currentData.value?.size && currentData.value.size !== '-') || videoDuration.value !== '-' || currentData.value?.format;
});

const onImgLoad = () => {
  if (imgRef.value) {
    naturalWidth.value = imgRef.value.naturalWidth;
    naturalHeight.value = imgRef.value.naturalHeight;
  }
};

const onVideoLoaded = () => {
  if (videoRef.value) {
    videoWidth.value = videoRef.value.videoWidth;
    videoHeight.value = videoRef.value.videoHeight;
    videoDurationSeconds.value = videoRef.value.duration;
  }
};

const handleNext = () => {
  if (props.dataList && currentIndex.value < props.dataList.length - 1) {
    resetMediaState();
    currentIndex.value++;
  }
};

const handlePrev = () => {
  if (props.dataList && currentIndex.value > 0) {
    resetMediaState();
    currentIndex.value--;
  }
};

const resetMediaState = () => {
  naturalWidth.value = 0;
  naturalHeight.value = 0;
  videoWidth.value = 0;
  videoHeight.value = 0;
  videoDurationSeconds.value = 0;
};

const handleKeyDown = (e: KeyboardEvent) => {
  if (!visible.value) return;
  if (e.key === 'ArrowLeft') handlePrev();
  else if (e.key === 'ArrowRight') handleNext();
  else if (e.key === 'Escape') handleClose();
  else if (e.key === ' ' && currentType.value === 'video') {
    e.preventDefault(); // 阻止页面滚动
    if (videoRef.value) {
      videoRef.value.paused ? videoRef.value.play() : videoRef.value.pause();
    }
  }
};

onMounted(() => window.addEventListener('keydown', handleKeyDown));
onUnmounted(() => window.removeEventListener('keydown', handleKeyDown));

watch(() => props.open, (isOpen) => {
  if (isOpen) {
    currentIndex.value = props.initialIndex || 0;
    resetMediaState();
  }
});

const downloading = ref(false);

const truncateFileName = (name: string | undefined) => {
  if (!name) return '';
  const base = name.replace(/\.[^/.]+$/, '');
  return base.length > 30 ? base.slice(0, 30) + '…' : base;
};

const getFileExtension = (name: string | undefined) => {
  if (!name) return '';
  const parts = name.split('.');
  return parts.length > 1 ? parts.pop()?.toUpperCase() : '';
};

const handleDownload = async () => {
  const targetData = currentData.value;
  if (!targetData?.url) return;
  downloading.value = true;
  try {
    const response = await fetch(targetData.url);
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = targetData.name || (currentType.value === 'video' ? 'video.mp4' : 'image.png');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success({ content: '已开始下载', key: 'dl' });
  } catch (e) {
    console.error('下载失败:', e);
    window.open(targetData.url, '_blank');
  } finally {
    downloading.value = false;
  }
};

const handleClose = () => { visible.value = false; };
</script>

<style lang="scss">
.luxe-preview-modal {
  .ant-modal-content {
    padding: 0 !important;
    background: transparent !important;
    box-shadow: none !important;
    border-radius: 20px !important;
    overflow: hidden;
  }
  .ant-modal-body { padding: 0 !important; }
}
</style>

<style lang="scss" scoped>
.preview-shell {
  position: relative;
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow:
    0 0 0 1px rgba(0, 0, 0, 0.06),
    0 25px 80px -12px rgba(0, 0, 0, 0.2),
    0 0 40px rgba(99, 102, 241, 0.06);
}

/* ── Floating close ── */
.floating-close {
  position: absolute;
  top: 14px;
  right: 14px;
  z-index: 30;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(8px);
  color: #64748b;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    background: rgba(239, 68, 68, 0.1);
    color: #ef4444;
    transform: scale(1.1);
  }
}

/* ── Counter pill ── */
.counter-pill {
  position: absolute;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 30;
  padding: 4px 14px;
  border-radius: 100px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  letter-spacing: 0.02em;
  font-variant-numeric: tabular-nums;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .sep { opacity: 0.3; margin: 0 2px; }
}

/* ── Stage area ── */
.stage-area {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 480px;
  padding: 48px 24px 16px;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 50%, #f8fafc 100%);
}

.media-viewport {
  position: relative;
  max-width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  img, video {
    max-width: 100%;
    max-height: 60vh;
    object-fit: contain;
    border-radius: 12px;
    box-shadow:
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(0, 0, 0, 0.04);
    user-select: none;
  }

  video { outline: none; }
}

.media-glow {
  position: absolute;
  width: 70%;
  height: 60%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
  transition: background 0.4s ease;

  &.glow-indigo {
    background: radial-gradient(ellipse, rgba(99, 102, 241, 0.04) 0%, transparent 70%);
  }
  &.glow-violet {
    background: radial-gradient(ellipse, rgba(139, 92, 246, 0.04) 0%, transparent 70%);
  }
}

/* ── Arrow buttons ── */
.arrow-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 20;
  width: 40px;
  height: 40px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  color: #475569;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover:not(:disabled) {
    background: #ffffff;
    border-color: rgba(99, 102, 241, 0.3);
    color: #4f46e5;
    transform: translateY(-50%) scale(1.06);
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
  }

  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }

  &.arrow-prev { left: 16px; }
  &.arrow-next { right: 16px; }
}

/* ── Bottom dock ── */
.bottom-dock {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  background: #ffffff;
  border-top: 1px solid #e2e8f0;
}

.dock-left { flex-shrink: 0; }

.file-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 10px;
  transition: all 0.3s ease;

  &.chip-image {
    background: rgba(99, 102, 241, 0.06);
    border: 1px solid rgba(99, 102, 241, 0.15);
    .chip-icon { color: #6366f1; }
  }
  &.chip-video {
    background: rgba(139, 92, 246, 0.06);
    border: 1px solid rgba(139, 92, 246, 0.15);
    .chip-icon { color: #8b5cf6; }
  }

  .chip-name {
    font-size: 13px;
    font-weight: 600;
    color: #1e293b;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .chip-ext {
    font-size: 11px;
    font-weight: 700;
    color: #94a3b8;
    text-transform: uppercase;
  }
}

.dock-center {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  justify-content: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-label {
  font-size: 11px;
  font-weight: 500;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.meta-val {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  font-variant-numeric: tabular-nums;

  &.accent { color: #7c3aed; }
}

.meta-divider {
  width: 1px;
  height: 16px;
  background: #e2e8f0;
}

.dock-right { flex-shrink: 0; }

.dl-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border-radius: 10px;
  border: none;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &.dl-image {
    background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
    box-shadow: 0 4px 16px rgba(99, 102, 241, 0.25);
    &:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 6px 24px rgba(99, 102, 241, 0.35);
    }
  }
  &.dl-video {
    background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
    box-shadow: 0 4px 16px rgba(139, 92, 246, 0.25);
    &:hover:not(:disabled) {
      transform: translateY(-1px);
      box-shadow: 0 6px 24px rgba(139, 92, 246, 0.35);
    }
  }

  &:disabled {
    opacity: 0.6;
    cursor: wait;
  }
}
</style>
