import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'influencer/list',
    name: 'influencer-list',
    component: () => import('@/views/influencer/InfluencerList.vue'),
    meta: { title: '红人列表', pageKey: 'influencer.list' }
  },
  {
    path: 'influencer/pool',
    name: 'influencer-pool',
    component: () => import('@/views/influencer/InfluencerPool.vue'),
    meta: { title: '资源池', pageKey: 'influencer.pool' }
  },
  {
    path: 'influencer/crawl',
    name: 'influencer-crawl',
    component: () => import('@/views/influencer/InfluencerCrawl.vue'),
    meta: { title: '爬取任务', pageKey: 'influencer.crawl' }
  },
];

export default routes;
