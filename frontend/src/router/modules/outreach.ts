import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'outreach/tk',
    name: 'outreach-tk',
    component: () => import('@/views/outreach/TkChat.vue'),
    meta: { title: 'TK 沟通' }
  },
  {
    path: 'outreach/email',
    name: 'outreach-email',
    component: () => import('@/views/outreach/EmailOutreach.vue'),
    meta: { title: '邮件建联' }
  },
  {
    path: 'outreach/template',
    name: 'outreach-template',
    component: () => import('@/views/outreach/TemplateManage.vue'),
    meta: { title: '模板管理' }
  },
  {
    path: 'outreach/sender',
    name: 'outreach-sender',
    component: () => import('@/views/outreach/SenderConfig.vue'),
    meta: { title: '发件配置' }
  },
];

export default routes;
