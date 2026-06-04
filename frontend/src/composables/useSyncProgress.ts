import { ref, computed, watch } from 'vue';
import { useSseStore } from '@/stores/sse';
import { message } from 'ant-design-vue';

export function useSyncProgress() {
    const sseStore = useSseStore();
    
    // Status can be: 'pending', 'running', 'completed', 'failed'
    const status = ref<'pending' | 'running' | 'completed' | 'failed'>('pending');
    const syncMessage = ref('');
    const progress = ref(0);
    const current = ref(0);
    const total = ref(0);

    // Watch for SSE events
    watch(() => sseStore.lastEvent, (event) => {
        if (!event || event.category !== 'sync_progress') return;

        const data = event.data || {};
        
        if (data.status) {
            switch (data.status.toLowerCase()) {
                case 'running':
                    status.value = 'running';
                    break;
                case 'completed':
                case 'success':
                    status.value = 'completed';
                    progress.value = 100;
                    break;
                case 'failed':
                case 'error':
                    status.value = 'failed';
                    break;
                default:
                    // Keep current status if unknown
                    break;
            }
        }

        if (data.message) syncMessage.value = data.message;
        if (data.current !== undefined) current.value = data.current;
        if (data.total !== undefined) total.value = data.total;
        
        if (total.value > 0) {
            progress.value = Math.round((current.value / total.value) * 100);
        } else if (status.value === 'completed') {
            progress.value = 100;
        }
    }, { deep: true });

    const startSync = async () => {
        status.value = 'running';
        progress.value = 0;
        current.value = 0;
        total.value = 0;
        syncMessage.value = '正在启动同步任务...';
        // In actual implementation, this might call a specific backend API
        // For now we set status and wait for SSE
    };

    const cancelSync = async () => {
        status.value = 'failed';
        syncMessage.value = '同步已取消';
    };

    const dismiss = () => {
        status.value = 'pending';
        progress.value = 0;
        syncMessage.value = '';
    };

    return {
        progress,
        status,
        message: syncMessage,
        current,
        total,
        startSync,
        cancelSync,
        dismiss
    };
}
