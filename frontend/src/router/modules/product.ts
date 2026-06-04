import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'product/list',
    name: 'product-list',
    component: () => import('@/views/product/ProductList.vue'),
    meta: { title: '商品列表', pageKey: 'product.list' }
  },
];

export default routes;
