import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'finance/remittance',
    name: 'remittance-management',
    component: () => import('@/views/finance/RemittanceManagement.vue'),
    meta: { title: '汇款管理', pageKey: 'finance.remittance' }
  }
];

export default routes;
