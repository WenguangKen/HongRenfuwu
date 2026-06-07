import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: 'system/eccang',
    name: 'system-eccang',
    component: () => import('@/views/system/EccangSetting.vue'),
    meta: { title: '易仓设置', pageKey: 'system.eccang' }
  },
  {
    path: 'system/user',
    name: 'system-user',
    component: () => import('@/views/system/UserManagement.vue'),
    meta: { title: '用户管理', pageKey: 'system.user' }
  },
  {
    path: 'system/profile',
    name: 'system-profile',
    component: () => import('@/views/system/UserProfile.vue'),
    meta: { title: '个人信息' } // 移除pageKey，允许无权限用户访问
  },
  {
    path: 'system/role',
    name: 'system-role',
    component: () => import('@/views/system/RoleManagement.vue'),
    meta: { title: '角色管理', pageKey: 'system.role' }
  },
  {
    path: 'system/tag',
    name: 'system-tag',
    component: () => import('@/views/system/TagManagement.vue'),
    meta: { title: '标签管理', pageKey: 'system.tag' }
  },
  {
    path: 'system/rule',
    name: 'system-rule',
    component: () => import('@/views/system/RuleSetting.vue'),
    meta: { title: '规则设置', pageKey: 'system.rule' }
  },
  {
    path: 'system/permission',
    name: 'system-permission',
    component: () => import('@/views/system/PermissionList.vue'),
    meta: { title: '权限列表', pageKey: 'system.permission' }
  },
  {
    path: 'system/storage',
    name: 'system-storage',
    component: () => import('@/views/system/StorageSetting.vue'),
    meta: { title: '存储配置', pageKey: 'system.storage' }
  }
];

export default routes;
