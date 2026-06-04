import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'content/pending',
    name: 'content-pending',
    component: () => import('@/views/content/PendingContent.vue'),
    meta: { title: '待上传', pageKey: 'content.pending' }
  },
  {
    path: 'content/library',
    name: 'content-library',
    component: () => import('@/views/content/ContentLibrary.vue'),
    meta: { title: '内容库', pageKey: 'content.library' }
  },
];

export default routes;
