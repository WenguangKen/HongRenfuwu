import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'commission/dist',
    name: 'commission-dist',
    component: () => import('@/views/commission/CommissionDistribution.vue'),
    meta: { title: '分佣列表', pageKey: 'commission.dist' }
  },
  {
    path: 'commission/pay',
    name: 'commission-pay',
    component: () => import('@/views/commission/CommissionPayment.vue'),
    meta: { title: '打款列表', pageKey: 'commission.pay' }
  },
  {
    path: 'commission/order',
    name: 'commission-order',
    component: () => import('@/views/commission/CommissionOrderList.vue'),
    meta: { title: '分佣订单列表', pageKey: 'commission.order' }
  },
];

export default routes;
