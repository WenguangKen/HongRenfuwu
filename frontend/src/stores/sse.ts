import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSseStore = defineStore('sse', () => {
    let eventSource: EventSource | null = null;
    const isConnected = ref(false);
    const lastEvent = ref<any>(null);
    const lastSuccessAt = ref<number>(Date.now());
    let fallbackTimer: any = null;
    let retryCount = 0;
    const MAX_RETRIES = 10;
    let errorLogged = false;

    const startFallbackPolling = () => {
        if (fallbackTimer) return;

        fallbackTimer = setInterval(() => {
            if (!isConnected.value) {
                lastEvent.value = { category: 'system', data: { topic: 'fallback_poll' }, _timestamp: Date.now() };
            }
        }, 5 * 60 * 1000);
    };

    const stopFallbackPolling = () => {
        if (fallbackTimer) {
            clearInterval(fallbackTimer);
            fallbackTimer = null;
        }
    };

    const connect = () => {
        if (eventSource?.readyState === EventSource.OPEN) return;
        if (retryCount >= MAX_RETRIES) {
            if (!errorLogged) {
                console.warn('[SSE] Max retries reached, stopping reconnection. Webhook service may not be running.');
                errorLogged = true;
            }
            startFallbackPolling();
            return;
        }

        const savedUrl = localStorage.getItem('webhook_base_url') || '';
        const isDev = import.meta.env.DEV;
        const isNgrokUrl = savedUrl.includes('ngrok');

        let sseUrl: string;
        if (isDev) {
            const baseUrl = (savedUrl && isNgrokUrl) ? savedUrl : 'http://localhost:8083';
            sseUrl = `${baseUrl}/v1/sse/events`;
        } else {
            sseUrl = savedUrl ? `${savedUrl.replace(/\/$/, '')}/v1/sse/events` : '/api/webhook/v1/sse/events';
        }

        try {
            eventSource = new EventSource(sseUrl);

            eventSource.onopen = () => {
                stopFallbackPolling();
                retryCount = 0;
                errorLogged = false;

                const now = Date.now();
                if (!isConnected.value) {
                    lastEvent.value = { category: 'system', data: { topic: 'reconnect' }, _timestamp: now };
                }

                isConnected.value = true;
                lastSuccessAt.value = now;
            };

            eventSource.addEventListener('connected', (event) => {
                lastSuccessAt.value = Date.now();
                stopFallbackPolling();
            });

            eventSource.addEventListener('webhook', (event) => {
                try {
                    const data = JSON.parse(event.data);
                    lastSuccessAt.value = Date.now();
                    lastEvent.value = { ...data, _timestamp: Date.now() };
                } catch (e) {
                    console.error('[SSE] Failed to parse event:', e);
                }
            });

            eventSource.onerror = () => {
                if (retryCount === 0) {
                    console.warn('[SSE] Connection failed. Webhook service may not be running. Will retry...');
                }

                if (isConnected.value) {
                    lastEvent.value = { category: 'system', data: { topic: 'fallback_poll' }, _timestamp: Date.now() };
                }

                isConnected.value = false;
                eventSource?.close();

                startFallbackPolling();
                retryCount++;

                // 递增重试间隔：30s 基础 + 指数退避，最大 5 分钟
                const delay = Math.min(30000 * Math.pow(1.5, retryCount - 1), 300000);
                setTimeout(() => {
                    connect();
                }, delay);
            };

        } catch (e) {
            if (!errorLogged) {
                console.error('[SSE] Failed to create EventSource:', e);
                errorLogged = true;
            }
        }
    };

    const disconnect = () => {
        stopFallbackPolling();
        retryCount = 0;
        errorLogged = false;
        if (eventSource) {
            eventSource.close();
            eventSource = null;
            isConnected.value = false;
        }
    };

    return {
        isConnected,
        lastEvent,
        lastSuccessAt,
        connect,
        disconnect
    };
});
