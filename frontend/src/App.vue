<script setup lang="ts">
import { RouterView } from 'vue-router'
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
dayjs.locale('zh-cn');

import { defineAsyncComponent } from 'vue';

const CopilotFloatChat = defineAsyncComponent(() => import('@/components/ai/CopilotFloatChat.vue'));

const locale = zhCN;

</script>

<template>
  <a-config-provider :locale="locale" :theme="{ 
      token: { 
        colorPrimary: '#fb7185',
        borderRadius: 4, 
        wireframe: false 
      } 
    }">
    <RouterView />
    <CopilotFloatChat />
  </a-config-provider>
</template>

<style scoped>
</style>
