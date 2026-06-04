/**
 * 红人列表 SSE 实时推送 Composable
 *
 * 职责：
 * - 管理 EventSource 连接生命周期
 * - 防抖 + 指数退避重连
 * - 最大重试限制（防止无限循环）
 *
 * @module useInfluencerListSSE
 */
import { onUnmounted } from 'vue';

/** SSE 配置常量 */
const SSE_URL = '/influencer-api/v1/influencer/sse/changes';
const SSE_MAX_RETRIES = 10;
const SSE_DEBOUNCE_MS = 2000;
const SSE_BASE_DELAY_MS = 3000;

export function useInfluencerListSSE(onDataChanged: () => void) {
  let sseSource: EventSource | null = null;
  let debounceTimer: ReturnType<typeof setTimeout> | null = null;
  let retryCount = 0;

  /** 防抖触发数据刷新 */
  const debouncedRefresh = () => {
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(onDataChanged, SSE_DEBOUNCE_MS);
  };

  /** 建立 SSE 连接 */
  const setupSSE = () => {
    if (retryCount >= SSE_MAX_RETRIES) {
      console.warn(`[SSE] 已达最大重试次数 (${SSE_MAX_RETRIES})，停止重连`);
      return;
    }

    sseSource = new EventSource(SSE_URL);

    sseSource.addEventListener('influencer-changed', debouncedRefresh);
    sseSource.addEventListener('reconnect', debouncedRefresh);

    sseSource.onopen = () => {
      retryCount = 0; // 连接成功，重置计数
    };

    sseSource.onerror = () => {
      sseSource?.close();
      retryCount++;
      if (retryCount < SSE_MAX_RETRIES) {
        const delay = SSE_BASE_DELAY_MS * Math.min(retryCount, 5);
        setTimeout(setupSSE, delay);
      }
    };
  };

  /** 关闭 SSE 连接 */
  const closeSSE = () => {
    if (debounceTimer) {
      clearTimeout(debounceTimer);
      debounceTimer = null;
    }
    sseSource?.close();
    sseSource = null;
  };

  // 组件卸载时自动清理
  onUnmounted(closeSSE);

  return {
    setupSSE,
    closeSSE
  };
}
