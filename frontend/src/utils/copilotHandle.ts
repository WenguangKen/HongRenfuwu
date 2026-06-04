import {
  isImplausiblePersonName,
  normalizeCopilotUserText,
} from '@/utils/copilotTextNormalize';

/** 是否像社媒 handle（无空格、字母数字点下划线） */
export function isLikelySocialHandle(s: string): boolean {
  const h = s.trim().replace(/^@/, '');
  return h.length >= 3 && !/\s/.test(h) && /^[a-z0-9._-]+$/i.test(h);
}

/** 从用户话术提取社媒 handle（社媒账号名，对应列表「社媒账号」筛选项） */
export function parseHandleFromText(text: string): string | null {
  const t = text.trim();
  if (!t) return null;

  const patterns = [
    /handle\s*(?:名字\s*)?叫\s*@?([a-zA-Z0-9._-]{2,64})/i,
    /(?:handle|账号名?|帐号|用户名)\s*(?:名叫|名字叫|是|为|叫)?\s*@?([a-zA-Z0-9._-]{2,64})/i,
    /@([a-zA-Z0-9._-]{2,64})/,
    /(?:名叫|名字叫|叫做)\s*@?([a-zA-Z0-9._-]{2,64})/i,
    /(?:是不是|有没有|是否存在|查找|搜索).*?([a-z][a-z0-9._-]{4,48})/i,
  ];

  for (const re of patterns) {
    const m = t.match(re);
    if (m?.[1]) {
      const h = m[1].replace(/^@/, '').trim();
      if (h && !/^(handle|tiktok|instagram|youtube)$/i.test(h) && isLikelySocialHandle(h)) {
        return h;
      }
    }
  }

  if (/红人|数据库|有没有|是否存在|查找|搜索|handle/i.test(t)) {
    if (/折扣码|优惠码|promo\s*code|coupon|discount\s*code/i.test(t)) {
      return null;
    }
    if (/订单|单号|交易号|样品单|样品订单|红人订单|转化单|分佣订单/i.test(t)) {
      return null;
    }
    const word = t.match(/\b([a-z][a-z0-9._-]{3,48})\b/i);
    if (word?.[1] && isLikelySocialHandle(word[1])) {
      return word[1];
    }
  }
  return null;
}

/** 提取红人姓名/昵称（含空格，走列表「名称」筛选） */
export function parseSearchNameFromText(text: string): string | null {
  const t = normalizeCopilotUserText(text);
  if (!t) return null;

  const patterns = [
    /(?:找|搜|查|招|有没有|是否存在).*?(?:叫|名为|名字[是为叫]|姓名[是为])\s*[「『"']?([^「』"'\?？\n]+?)[」』"']?(?:\s*的)?红人/i,
    /(?:一个叫|一位|一名)\s*[「『"']?([^「』"'\?？\n]+?)[」』"']?\s*(?:的)?红人/i,
    /(?:帮我找|帮我招|帮我查)\s*(?:一个|一位)\s*(?:叫|名叫)\s*[「『"']?([^「』"'\?？\n]+?)[」』"']?(?:\s*的)?红人/i,
    /红人\s*[「『"']([^「』"'\n]+)[」』"']/,
  ];

  for (const re of patterns) {
    const m = t.match(re);
    if (m?.[1]) {
      const name = m[1].trim();
      if (
        name.length >= 2 &&
        !/^(handle|tiktok|instagram|youtube|红人)$/i.test(name) &&
        !isImplausiblePersonName(name, t)
      ) {
        return name;
      }
    }
  }
  return null;
}

/** 是否在指代上一轮提到的红人（这个/该/他/她…） */
export function refersToPreviousInfluencer(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  if (/这两个红人|这两位红人|上面.*红人|刚才.*红人|刚查.*红人|刚找.*红人/.test(t)) return true;
  return (
    /这个|该|此|这位|这名|那位|刚才|上面|前述|这些|这批|上述|两位|二位|俩|其|他|她|它/.test(t) &&
    /红人|详情|账号|handle|资料|导出|模板|模版|邮件|邀约|建联/.test(t)
  );
}

/** 用户要发邀约/建联邮件 */
export function isMailInviteRequest(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return /(?:建联|邀约|发送?.*(?:邮件|信)|发.*(?:邮件|信)|邮件.*(?:邀约|建联))/.test(t);
}

export function isHandleLookupQuestion(text: string): boolean {
  return /handle|账号|帐号|社媒|@|数据库.*有没有|有没有.*红人/i.test(text.trim());
}

/** 用户明确要求打开详情页（否则仅列表筛选） */
export function userWantsOpenDetail(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return /(打开|查看|进入|点开|跳转).*(详情|资料)/.test(t) || /详情页/.test(t);
}

const HANDLE_TOKEN = /(?:^|[\s@])@?([a-z][a-z0-9._-]{2,48})\b/i;

function extractHandleToken(fragment: string): string | null {
  const m = fragment.trim().match(HANDLE_TOKEN);
  if (!m?.[1]) return null;
  const h = m[1].replace(/^@/, '').trim();
  if (!h || /^(handle|tiktok|instagram|youtube|shopify)$/i.test(h)) return null;
  return isLikelySocialHandle(h) ? h : null;
}

function dedupeHandles(handles: string[]): string[] {
  const seen = new Set<string>();
  const out: string[] = [];
  for (const raw of handles) {
    const h = raw.trim().replace(/^@/, '');
    if (!h) continue;
    const key = h.toLowerCase();
    if (seen.has(key)) continue;
    seen.add(key);
    out.push(h);
  }
  return out;
}

/** 从话术提取多个 handle（@xxx、逗号/换行、或「和/与/跟」连接） */
export function parseMultipleHandlesFromText(text: string): string[] {
  const t = text.trim();
  if (!t) return [];

  const fromAt = dedupeHandles(
    [...t.matchAll(/@([a-zA-Z0-9._-]{2,64})/gi)].map((m) => m[1]!)
  );
  if (fromAt.length > 1) return fromAt;

  const chunk = t.match(/(?:打开|查看|进入).*?[:：]?\s*([^\n]+)/i)?.[1] ?? t;

  if (/[，,、；;\n]/.test(chunk)) {
    const parts = chunk
      .split(/[，,、；;\n]+/)
      .map((s) => extractHandleToken(s))
      .filter((s): s is string => !!s);
    if (parts.length > 1) return dedupeHandles(parts);
  }

  if (/(?:和|与|跟|以及)/.test(chunk)) {
    const parts = chunk
      .split(/(?:和|与|跟|以及)/)
      .map((s) => extractHandleToken(s))
      .filter((s): s is string => !!s);
    if (parts.length > 1) return dedupeHandles(parts);
  }

  if (/红人|数据库|有没有|是否存在|查找|搜索|handle/i.test(t)) {
    if (/折扣码|优惠码|promo\s*code|coupon|discount\s*code/i.test(t)) return [];
    if (/订单|单号|交易号|样品单|样品订单|红人订单|转化单|分佣订单/i.test(t)) return [];
    const bare = dedupeHandles(
      [...t.matchAll(/\b([a-z][a-z0-9._-]{3,48})\b/gi)]
        .map((m) => m[1]!)
        .filter((h): h is string => !!h && !/^(handle|tiktok|instagram|youtube|shopify)$/i.test(h) && isLikelySocialHandle(h))
    );
    if (bare.length > 1) return bare;
  }

  return [];
}

/** 批量打开红人详情（暂不支持） */
export function isBatchOpenDetailRequest(text: string): boolean {
  const t = text.trim();
  if (!userWantsOpenDetail(t)) return false;
  if (/批量|多个|分别|都打开|全部打开|一起打开|同时打开|挨个打开/i.test(t)) return true;
  if (parseMultipleHandlesFromText(t).length > 1) return true;
  if (/打开.*[，,、；;].+(详情|资料)/.test(t)) return true;
  if (/打开\s*.+\s+和\s+.+\s*(的)?(详情|资料)/.test(t)) return true;
  return false;
}

/** 用户要求运行 AI 检索/筛选（走模型工具，不做仅跳转列表的预置） */
export function userWantsRunAi(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return /运行\s*AI|执行\s*AI|运行\s*小A|执行\s*小A|语义检索|向量检索|智能检索/i.test(t);
}

/** 用户要求关闭红人详情弹窗 */
export function userWantsCloseDetail(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return (
    /关闭.*详情|收起.*详情|退出.*详情|关掉.*详情|详情.*关闭|详情页.*关/i.test(t) ||
    /^关闭详情页?$/i.test(t)
  );
}

export { parseNavigatePageFromText } from '@/utils/copilotNavigation';

export const COPILOT_PAGE_ROUTES: Record<string, string> = {
  influencer_list: '/influencer/list',
  list: '/influencer/list',
  红人列表: '/influencer/list',
  influencer_pool: '/influencer/pool',
  pool: '/influencer/pool',
  资源池: '/influencer/pool',
  influencer_copilot: '/influencer/copilot',
  copilot: '/influencer/copilot',
  小A: '/influencer/copilot',
  工作台: '/influencer/copilot',
  content_pending: '/content/pending',
  待上传: '/content/pending',
  content_library: '/content/library',
  内容库: '/content/library',
  mail_hub: '/mail/hub',
  mail_templates: '/mail/templates',
  mail_campaigns: '/mail/campaigns',
  commission_dist: '/commission/dist',
  commission_pay: '/commission/pay',
  product_list: '/product/list',
  finance_remittance: '/finance/remittance',
};
