import { defineStore } from 'pinia';
import { ref } from 'vue';

/** Webhook SSE 已移除，保留 store 接口避免前端组件报错 */
export const useSseStore = defineStore('sse', () => {
    const isConnected = ref(false);
    const lastEvent = ref<any>(null);
    const lastSuccessAt = ref<number>(Date.now());

    const connect = () => {
        isConnected.value = false;
    };

    const disconnect = () => {
        isConnected.value = false;
    };

    return {
        isConnected,
        lastEvent,
        lastSuccessAt,
        connect,
        disconnect
    };
});
