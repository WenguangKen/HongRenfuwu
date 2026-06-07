export interface MenuItem {
  key: string;
  title: string;
  icon?: string;
  path?: string;
  pageKey?: string;
  children?: MenuItem[];
}

const menu: MenuItem[] = [
  { key: 'dashboard', title: '仪表盘', icon: 'DashboardOutlined', path: '/dashboard', pageKey: 'dashboard' },
  {
    key: 'influencer',
    title: '红人管理',
    icon: 'TeamOutlined',
    children: [
      { key: 'influencer-list', title: '红人列表', path: '/influencer/list', pageKey: 'influencer.list' },
      { key: 'influencer-pool', title: '资源池', path: '/influencer/pool', pageKey: 'influencer.pool' },
      { key: 'influencer-crawl', title: '爬取任务', path: '/influencer/crawl', pageKey: 'influencer.crawl' },
    ],
  },
  {
    key: 'outreach',
    title: '红人建联',
    icon: 'SendOutlined',
    children: [
      { key: 'outreach-tk',       title: 'TK 沟通',  path: '/outreach/tk' },
      { key: 'outreach-email',    title: '邮件建联',  path: '/outreach/email' },
      { key: 'outreach-template', title: '模板管理',  path: '/outreach/template' },
      { key: 'outreach-sender',   title: '发件配置',  path: '/outreach/sender' },
    ],
  },
  {
    key: 'content',
    title: '内容管理',
    icon: 'VideoCameraOutlined',
    children: [
      { key: 'content-pending', title: '待上传', path: '/content/pending', pageKey: 'content.pending' },
      { key: 'content-library', title: '内容库', path: '/content/library', pageKey: 'content.library' },
    ],
  },
  {
    key: 'finance',
    title: '财务管理',
    icon: 'PayCircleOutlined',
    children: [
      { key: 'remittance', title: '汇款管理', path: '/finance/remittance', pageKey: 'finance.remittance' },
    ],
  },
  { key: 'product', title: '商品列表', icon: 'SkinOutlined', path: '/product/list', pageKey: 'product.list' },
  {
    key: 'system',
    title: '系统设置',
    icon: 'SettingOutlined',
    children: [
      { key: 'system-user', title: '用户管理', path: '/system/user', pageKey: 'system.user' },
      { key: 'system-role', title: '角色管理', path: '/system/role', pageKey: 'system.role' },
      { key: 'system-tag', title: '标签管理', path: '/system/tag', pageKey: 'system.tag' },
      { key: 'system-rule', title: '规则设置', path: '/system/rule', pageKey: 'system.rule' },
      { key: 'system-permission', title: '权限列表', path: '/system/permission', pageKey: 'system.permission' },
      { key: 'system-eccang', title: '易仓设置', path: '/system/eccang', pageKey: 'system.eccang' },
      { key: 'system-storage', title: '存储配置', path: '/system/storage', pageKey: 'system.storage' },
    ],
  },
];

export default menu;
