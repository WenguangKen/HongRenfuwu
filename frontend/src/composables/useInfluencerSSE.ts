/**
 * SSE 实时监听红人变更 — 独立 composable
 * 
 * 功能：连接后端 SSE 端点，接收变更事件后防抖触发回调
 * 包含：指数退避重连、最大重试限制、组件卸载自动清理
 */
import { onUnmounted } from 'vue';

const SSE_MAX_RETRIES = 10;
const SSE_DEBOUNCE_MS = 2000;
const SSE_URL = '/influencer-api/v1/influencer/sse/changes';

export function useInfluencerSSE(onDataChanged: () => void) {
  let source: EventSource | null = null;
  let debounceTimer: ReturnType<typeof setTimeout> | null = null;
  let reconnectTimer: ReturnType<typeof setTimeout> | null = null;
  let retryCount = 0;

  const debouncedRefresh = () => {
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(onDataChanged, SSE_DEBOUNCE_MS);
  };

  const connect = () => {
    if (retryCount >= SSE_MAX_RETRIES) {
      console.warn('[SSE] 已达最大重试次数，停止重连');
      return;
    }
    source = new EventSource(SSE_URL);
    source.addEventListener('influencer-changed', debouncedRefresh);
    source.addEventListener('reconnect', debouncedRefresh);
    source.onopen = () => { retryCount = 0; };
    source.onerror = () => {
      source?.close();
      retryCount++;
      if (retryCount < SSE_MAX_RETRIES) {
        const delay = Math.min(3000 * Math.pow(2, retryCount - 1), 30000);
        reconnectTimer = setTimeout(connect, delay);
      }
    };
  };

  const close = () => {
    source?.close();
    source = null;
    if (debounceTimer) { clearTimeout(debounceTimer); debounceTimer = null; }
    if (reconnectTimer) { clearTimeout(reconnectTimer); reconnectTimer = null; }
  };

  onUnmounted(close);

  return { connect, close };
}
