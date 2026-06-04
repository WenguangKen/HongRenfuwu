import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'order/sample',
    name: 'order-sample',
    component: () => import('@/views/order/SampleOrder.vue'),
    meta: { title: '红人订单', pageKey: 'order.sample' }
  },
  {
    path: 'order/conversion',
    name: 'order-conversion',
    component: () => import('@/views/order/ConversionOrder.vue'),
    meta: { title: '转化订单', pageKey: 'order.conversion' }
  },
];

export default routes;
