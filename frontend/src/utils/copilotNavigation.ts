/**
 * AI 小A 页面导航 — 与侧栏 menu.ts 对齐，支持子页面跳转
 */
import menu, { type MenuItem } from '@/config/menu';

export interface NavTarget {
  path: string;
  label: string;
}

type PageEntry = { path: string; label: string; keywords: string[] };

function flattenMenu(
  items: MenuItem[],
  out: Array<{ path: string; label: string; pageKey?: string }> = []
) {
  for (const item of items) {
    if (item.path) {
      out.push({ path: item.path, label: item.title, pageKey: item.pageKey });
    }
    if (item.children?.length) {
      flattenMenu(item.children, out);
    }
  }
  return out;
}

/** 菜单标题之外的口语别名 */
const KEYWORD_ALIASES: Record<string, string[]> = {
  '/dashboard': ['仪表盘', '首页', '看板', '主页', 'dashboard'],
  '/influencer/list': ['红人列表', '红人管理', '红人页', 'influencer list'],
  '/influencer/pool': ['资源池', '红人资源', 'pool'],
  '/influencer/copilot': ['小a', '小a工作台', '智能检索', 'copilot', 'ai工作台'],
  '/mail/hub': ['红人邮件', '邮件中心', '邮件hub', 'mail hub', '邮件首页'],
  '/mail/templates': ['邮件模版', '邮件模板', '模版设置', 'mail template'],
  '/mail/campaigns': ['批量发信', '邮件活动', '群发', 'mail campaign'],
  '/order/sample': ['红人订单', '样品订单', '寄样', 'sample order'],
  '/order/conversion': ['转化订单', '转化', 'conversion order'],
  '/content/pending': ['待上传', '待上传页面', '待传', 'pending upload', '素材待上传'],
  '/content/library': ['内容库', '内容管理', 'content library', '素材库'],
  '/finance/remittance': ['财务', '汇款', '财务管理', '汇款管理', 'finance', 'remittance'],
  '/commission/dist': ['分佣列表', '分佣分配', '佣金分配', 'commission dist'],
  '/commission/pay': ['打款列表', '分佣打款', '佣金打款', '打款', 'commission pay'],
  '/commission/order': ['分佣订单', '分佣订单列表', '佣金订单', 'commission order'],
  '/product/list': ['商品列表', '商品管理', '产品管理', 'product'],
  '/system/user': ['用户管理', '用户列表', 'user management'],
  '/system/role': ['角色管理', '角色', 'role'],
  '/system/tag': ['标签管理', '标签', 'tag'],
  '/system/rule': ['规则设置', '规则管理', '规则', 'rule'],
  '/system/permission': ['权限列表', '权限管理', '权限', 'permission'],
  '/system/shopify': ['shopify', '店铺配置', '店铺设置'],
  '/system/webhook': ['webhook', '钩子'],
  '/system/storage': ['存储配置', '存储管理', '文件管理', 'storage'],
};

const PAGE_MAP: PageEntry[] = flattenMenu(menu).map((item) => {
  const aliases = KEYWORD_ALIASES[item.path] ?? [];
  const keywords = [...new Set([item.label, ...aliases, item.pageKey ?? ''].filter(Boolean))];
  return { path: item.path, label: item.label, keywords };
});

/** path → 中文名（供小A 摘要展示） */
export const PAGE_NAME: Record<string, string> = Object.fromEntries(
  PAGE_MAP.map((p) => [p.path, p.label])
);

/** 导航意图前缀 */
const NAV_INTENT_PATTERNS = [
  /^(打开|去|跳转到?|前往|进入|切换到?|看看|查看|访问|到|转到|打开一下|帮我打开|帮我去|帮我切换|我想去|我要去|我要看|导航到?)\s*/,
  /^(open|go\s+to|navigate\s+to|switch\s+to|show\s+me)\s*/i,
];

/** 去掉末尾「页面 / 页 / 模块」等 */
function stripPageSuffix(text: string): string {
  return text.replace(/[的]?(页面|页|模块|管理页|列表页|管理|列表)?\s*$/, '').trim();
}

function matchPage(content: string): PageEntry | null {
  const lower = content.toLowerCase();
  if (!lower) return null;

  // 优先最长关键词，减少误匹配
  let best: PageEntry | null = null;
  let bestLen = 0;
  for (const page of PAGE_MAP) {
    for (const kw of page.keywords) {
      const kwLower = kw.toLowerCase();
      if (kwLower.length < bestLen) continue;
      if (lower === kwLower || lower.includes(kwLower)) {
        best = page;
        bestLen = kwLower.length;
      }
    }
  }
  return best;
}

/**
 * 判断用户是否有页面导航意图，若有则返回目标页面
 */
export function inferNavigationTarget(text: string): NavTarget | null {
  const trimmed = text.trim();
  let content = trimmed;
  let hasIntent = false;

  for (const pattern of NAV_INTENT_PATTERNS) {
    const m = content.match(pattern);
    if (m) {
      content = content.slice(m[0].length).trim();
      hasIntent = true;
      break;
    }
  }
  if (!hasIntent) return null;

  content = stripPageSuffix(content);
  const page = matchPage(content);
  return page ? { path: page.path, label: page.label } : null;
}

/** 模型/兜底解析：仅在有明确导航前缀时返回 path */
export function parseNavigatePageFromText(text: string): string | null {
  return inferNavigationTarget(text)?.path ?? null;
}

/** 根据 path 或 pageKey 解析路由（供 SSE 指令兜底） */
export function resolvePagePath(pathOrKey: string): string {
  const raw = pathOrKey.trim();
  if (raw.startsWith('/')) return raw;
  const lower = raw.toLowerCase();
  for (const page of PAGE_MAP) {
    if (page.path.toLowerCase().includes(lower) || lower.includes(page.path.toLowerCase())) {
      return page.path;
    }
    for (const kw of page.keywords) {
      if (kw.toLowerCase() === lower) return page.path;
    }
  }
  return '/influencer/list';
}

export function pageLabel(path: string): string {
  return PAGE_NAME[path] ?? path;
}

export function allPageLabels(): string[] {
  return PAGE_MAP.map((p) => p.label);
}
