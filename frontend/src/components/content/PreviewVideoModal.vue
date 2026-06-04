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

        <div class="video-viewport">
          <div class="video-glow"></div>
          <video 
            ref="videoRef"
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
          <div class="file-chip">
            <VideoCameraOutlined class="chip-icon" />
            <span class="chip-name">{{ truncateFileName(currentData?.name) || '视频' }}</span>
            <span class="chip-ext">.{{ getFileExtension(currentData?.name) || 'MP4' }}</span>
          </div>
        </div>
        <div class="dock-center">
          <div class="meta-item" v-if="videoResolution !== '-'">
            <span class="meta-label">分辨率</span>
            <span class="meta-val">{{ videoResolution }}</span>
          </div>
          <div class="meta-divider" v-if="videoResolution !== '-'"></div>
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
          <button class="dl-btn" @click="handleDownload" :disabled="downloading">
            <DownloadOutlined />
            <span>{{ downloading ? '下载中...' : '下载' }}</span>
          </button>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { DownloadOutlined, VideoCameraOutlined, CloseOutlined, LeftOutlined, RightOutlined } from '@ant-design/icons-vue';
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

const videoRef = ref<HTMLVideoElement | null>(null);
const downloading = ref(false);
const videoWidth = ref(0);
const videoHeight = ref(0);
const videoDurationSeconds = ref(0);

const truncateFileName = (name: string | undefined) => {
  if (!name) return '';
  const base = name.replace(/\.[^/.]+$/, '');
  return base.length > 30 ? base.slice(0, 30) + '…' : base;
};

const getFileExtension = (name: string | undefined) => {
  if (!name) return '';
  const parts = name.split('.');
  return parts.length > 1 ? parts.pop()?.toUpperCase() : 'MP4';
};

const currentIndex = ref(props.initialIndex || 0);

const currentData = computed(() => {
  if (props.dataList && props.dataList.length > 0) {
    return props.dataList[currentIndex.value];
  }
  return props.data;
});

const handleNext = () => {
  if (props.dataList && currentIndex.value < props.dataList.length - 1) {
    currentIndex.value++;
  }
};

const handlePrev = () => {
  if (props.dataList && currentIndex.value > 0) {
    currentIndex.value--;
  }
};

const videoResolution = computed(() => {
  const target = currentData.value;
  if (target?.resolution && target.resolution !== '-') return target.resolution;
  if (videoWidth.value && videoHeight.value) return `${videoWidth.value} × ${videoHeight.value}`;
  return '-';
});

const videoDuration = computed(() => {
  if (props.data?.duration && props.data.duration !== '-') return props.data.duration;
  if (videoDurationSeconds.value > 0) return formatDuration(videoDurationSeconds.value);
  return '-';
});

const formatDuration = (seconds: number): string => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

const onVideoLoaded = () => {
  if (videoRef.value) {
    videoWidth.value = videoRef.value.videoWidth;
    videoHeight.value = videoRef.value.videoHeight;
    videoDurationSeconds.value = videoRef.value.duration;
  }
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
    link.download = targetData.name || 'video.mp4';
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

watch(() => props.open, (val) => {
  if (val) {
    currentIndex.value = props.initialIndex || 0;
  } else {
    videoWidth.value = 0;
    videoHeight.value = 0;
    videoDurationSeconds.value = 0;
  }
});
</script>

<style lang="scss">
.luxe-preview-modal {
  .ant-modal-content {
    padding: 0 !important;
    background: transparent !important;
    box-shadow: none !important;
    border-radius: 24px !important;
    overflow: hidden;
  }
  .ant-modal-body { padding: 0 !important; }
}
</style>

<style lang="scss" scoped>
.preview-shell {
  position: relative;
  background: #0c0c14;
  border-radius: 24px;
  overflow: hidden;
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.06),
    0 40px 120px -20px rgba(0, 0, 0, 0.85),
    0 0 80px rgba(139, 92, 246, 0.08);
}

/* ── Floating close ── */
.floating-close {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 30;
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.65);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    background: rgba(239, 68, 68, 0.25);
    color: #f87171;
    transform: scale(1.1);
  }
}

/* ── Counter pill ── */
.counter-pill {
  position: absolute;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 30;
  padding: 5px 14px;
  border-radius: 100px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.85);
  letter-spacing: 0.02em;
  font-variant-numeric: tabular-nums;

  .sep { opacity: 0.35; margin: 0 2px; }
}

/* ── Stage area ── */
.stage-area {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 480px;
  padding: 48px 24px 16px;
  background: linear-gradient(180deg, #0c0c14 0%, #111122 50%, #0c0c14 100%);
}

.video-viewport {
  position: relative;
  max-width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;

  video {
    max-width: 100%;
    max-height: 60vh;
    border-radius: 16px;
    box-shadow:
      0 16px 48px rgba(0, 0, 0, 0.55),
      0 0 0 1px rgba(255, 255, 255, 0.04);
    outline: none;
  }
}

.video-glow {
  position: absolute;
  width: 70%;
  height: 60%;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: radial-gradient(ellipse, rgba(139, 92, 246, 0.06) 0%, transparent 70%);
  pointer-events: none;
}

/* ── Arrow buttons ── */
.arrow-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 20;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: rgba(255, 255, 255, 0.06);
  backdrop-filter: blur(12px);
  color: rgba(255, 255, 255, 0.7);
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.12);
    border-color: rgba(255, 255, 255, 0.15);
    color: #fff;
    transform: translateY(-50%) scale(1.06);
  }

  &:disabled {
    opacity: 0.2;
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
  background: rgba(255, 255, 255, 0.03);
  border-top: 1px solid rgba(255, 255, 255, 0.05);
}

.dock-left {
  flex-shrink: 0;
}

.file-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: 10px;
  background: rgba(139, 92, 246, 0.08);
  border: 1px solid rgba(139, 92, 246, 0.12);

  .chip-icon {
    font-size: 14px;
    color: #a78bfa;
  }

  .chip-name {
    font-size: 13px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.8);
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .chip-ext {
    font-size: 11px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.3);
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
  color: rgba(255, 255, 255, 0.3);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.meta-val {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  font-variant-numeric: tabular-nums;

  &.accent { color: #a78bfa; }
}

.meta-divider {
  width: 1px;
  height: 16px;
  background: rgba(255, 255, 255, 0.08);
}

.dock-right {
  flex-shrink: 0;
}

.dl-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 16px rgba(139, 92, 246, 0.3);

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 6px 24px rgba(139, 92, 246, 0.45);
    background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  }

  &:disabled {
    opacity: 0.6;
    cursor: wait;
  }
}
</style>
