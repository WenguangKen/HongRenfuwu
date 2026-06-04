<template>
  <a-modal
    :open="open"
    :title="null"
    :footer="null"
    width="80%"
    style="top: 20px"
    class="browser-preview-modal"
    @update:open="$emit('update:open', $event)"
  >
    <div class="browser-header">
      <div class="url-bar">
        <GlobalOutlined class="icon" />
        <span class="url-text">{{ url }}</span>
      </div>
      <div class="actions">
        <a-button type="text" @click="openExternal">
          <LinkOutlined /> Open External
        </a-button>
        <a-button type="text" class="close-btn" @click="$emit('update:open', false)">
          <CloseOutlined />
        </a-button>
      </div>
    </div>
    <div class="browser-content">
      <iframe v-if="url" :src="url" frameborder="0" class="web-view"></iframe>
      <div v-else class="empty-view">
        <Empty description="No URL provided" />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { GlobalOutlined, LinkOutlined, CloseOutlined } from '@ant-design/icons-vue';
import { Empty } from 'ant-design-vue';

const props = defineProps<{
  open: boolean;
  url: string;
}>();

const emit = defineEmits(['update:open']);

const openExternal = () => {
    if (props.url) {
        window.open(props.url, '_blank');
    }
};
</script>

<style scoped lang="scss">
.browser-preview-modal {
    :deep(.ant-modal-content) {
        padding: 0;
        border-radius: 12px;
        overflow: hidden;
        height: 85vh;
        display: flex;
        flex-direction: column;
    }
    :deep(.ant-modal-body) {
         padding: 0;
         flex: 1;
         display: flex;
         flex-direction: column;
         height: 100%;
    }
}

.browser-header {
    height: 50px;
    background: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 16px;
    border-bottom: 1px solid #e2e8f0;

    .url-bar {
        background: #fff;
        padding: 4px 12px;
        border-radius: 20px;
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
        margin-right: 16px;
        border: 1px solid #e2e8f0;
        font-size: 13px;
        color: #64748b;
        max-width: 60%;
        
        .url-text {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    }
}

.browser-content {
    flex: 1;
    background: #fff;
    position: relative;
    overflow: hidden;

    .web-view {
        width: 100%;
        height: 100%;
        display: block;
    }
    
    .empty-view {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;
    }
}
</style>
