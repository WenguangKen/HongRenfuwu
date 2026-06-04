<template>
  <div class="vft-wrapper" :style="sizeStyle">
    <img v-if="thumbSrc" :src="thumbSrc" alt="视频缩略图" :style="sizeStyle" loading="lazy" decoding="async" />
    <div v-else class="vft-loading" :style="sizeStyle">
      <LoadingOutlined v-if="loading" style="color: #94a3b8; font-size: 16px;" />
      <VideoCameraOutlined v-else style="color: #94a3b8; font-size: 18px;" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { LoadingOutlined, VideoCameraOutlined } from '@ant-design/icons-vue';

const props = withDefaults(defineProps<{
  src: string;
  width?: number;
  height?: number;
  frameTime?: number; // 秒，默认 0.33 ≈ 第10帧@30fps
}>(), {
  width: 60,
  height: 60,
  frameTime: 0.33,
});

const thumbSrc = ref('');
const loading = ref(false);

const sizeStyle = {
  width: `${props.width}px`,
  height: `${props.height}px`,
  objectFit: 'cover' as const,
  borderRadius: '6px',
};

const extractFrame = (url: string) => {
  if (!url) return;
  loading.value = true;

  const video = document.createElement('video');
  video.crossOrigin = 'anonymous';
  video.preload = 'metadata';
  video.muted = true;
  video.playsInline = true;

  const objUrl = url.startsWith('blob:') ? url : '';
  video.src = url;

  const cleanup = () => {
    loading.value = false;
    if (objUrl) URL.revokeObjectURL(objUrl);
    video.src = '';
    video.load();
  };

  video.onloadedmetadata = () => {
    video.currentTime = Math.min(props.frameTime, video.duration || 0);
  };

  video.onseeked = () => {
    try {
      const canvas = document.createElement('canvas');
      canvas.width = props.width * 2; // 2x for retina
      canvas.height = props.height * 2;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        thumbSrc.value = canvas.toDataURL('image/jpeg', 0.75);
      }
    } catch (e) {
      // CORS or other error — silently fail, show fallback icon
    }
    cleanup();
  };

  video.onerror = () => cleanup();

  // 超时保护：5秒内没有结果就放弃
  setTimeout(() => {
    if (loading.value) cleanup();
  }, 5000);
};

onMounted(() => extractFrame(props.src));
watch(() => props.src, (newSrc) => {
  thumbSrc.value = '';
  extractFrame(newSrc);
});
</script>

<style scoped>
.vft-wrapper {
  display: inline-flex;
  position: relative;
  overflow: hidden;
  border-radius: 6px;
  background: #f1f5f9;
}

.vft-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border-radius: 6px;
}
</style>
