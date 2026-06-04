/**
 * 小A 用户话术归一化：口语、错字、冗余词修复，便于规则解析。
 */
export function normalizeCopilotUserText(text: string): string {
  return text
    .trim()
    .replace(/默人选中/g, '默认选中')
    .replace(/帮我找到/g, '帮我找')
    .replace(/给我找到/g, '给我找')
    .replace(/查找到/g, '查找')
    .replace(/的是个/g, '的')
    .replace(/是个/g, '一个')
    .replace(/得(?=[最高最低多少])/g, '的')
    .replace(/\s+/g, ' ');
}

/** 是否像「筛选/排序/统计」类问法，而非按姓名搜人 */
export function isFilterLikeInfluencerQuery(text: string): boolean {
  const t = normalizeCopilotUserText(text);
  if (!t) return false;
  if (
    /待联系|已联系|沟通中|合作中|休眠|黑名单|暂不合作|不再合作|未合作|已合作/.test(t)
  ) {
    return true;
  }
  if (/粉丝量|粉丝数|粉丝.*(最高|最低|最多|最少|排序|从低到高|从高到低)/.test(t)) {
    return true;
  }
  if (/(?:最高|最低|最多|最少|top\s*\d+|前\s*\d+\s*(?:个|位|名)).*(?:红人|达人|kol)/i.test(t)) {
    return true;
  }
  if (/tiktok|instagram|ins\b|ig\b|youtube|facebook|平台|国家|美妆|时尚|未付费|已付费/.test(t)) {
    return true;
  }
  if (/有多少|几个|统计|列出|展示|筛选/.test(t)) {
    return true;
  }
  return false;
}

/** 用户是否要求「勾选/选中/抱走」当前筛选结果（区别于只筛选） */
export type SelectIntent = {
  /** 是否要勾选行 */
  select: boolean;
  /** 勾选后的下一步动作 */
  followup?: 'mail' | 'tag' | null;
  /** 用户提到的数量（如「十个」「10 个」） */
  count?: number;
};

const CN_NUM: Record<string, number> = {
  一: 1,
  二: 2,
  两: 2,
  三: 3,
  四: 4,
  五: 5,
  六: 6,
  七: 7,
  八: 8,
  九: 9,
  十: 10,
};

function parseCnNumber(s: string): number | undefined {
  const t = s.trim();
  if (!t) return undefined;
  if (/^\d{1,4}$/.test(t)) return Number(t);
  if (t === '十') return 10;
  let m = t.match(/^十([一二两三四五六七八九])$/);
  if (m) return 10 + (CN_NUM[m[1]!] ?? 0);
  m = t.match(/^([一二两三四五六七八九])十([一二两三四五六七八九])?$/);
  if (m) return (CN_NUM[m[1]!] ?? 0) * 10 + (m[2] ? CN_NUM[m[2]] ?? 0 : 0);
  if (CN_NUM[t] != null) return CN_NUM[t];
  return undefined;
}

export function parseSelectIntent(text: string): SelectIntent {
  const t = normalizeCopilotUserText(text);
  if (!t) return { select: false };

  let count: number | undefined;
  let m = t.match(/(\d{1,4}|[一二两三四五六七八九十]+)\s*(?:个|位|名|条)/);
  if (m) {
    const n = parseCnNumber(m[1]!);
    if (n != null) count = Math.min(n, 500);
  }
  if (count == null) {
    m = t.match(/(?:找出|找|选|筛)\s*(\d{1,4}|[一二两三四五六七八九十]+)\s*(?:个|位|名)/);
    if (m) {
      const n = parseCnNumber(m[1]!);
      if (n != null) count = Math.min(n, 500);
    }
  }
  if (count == null) {
    m = t.match(/前\s*(\d{1,4}|[一二两三四五六七八九十]+)/);
    if (m) {
      const n = parseCnNumber(m[1]!);
      if (n != null) count = Math.min(n, 500);
    }
  }

  const autoSelect = /默认选中|自动选中|默认勾选|自动勾选|帮我选中|给我选中/.test(t);
  const findOutSelect = /找出|找出来/.test(t) && count != null;
  const explicitSelect =
    /选中|勾选|圈出|圈选|挑出|挑选|抱走|抱出|抱给|取出|取走|选出|拿出/.test(t) ||
    /帮我选|给我选|帮我勾/.test(t);

  const select = explicitSelect || autoSelect || findOutSelect;
  if (!select) return { select: false };

  let followup: SelectIntent['followup'] = null;
  if (/发(?:邮件|信)|批量发|群发/.test(t)) followup = 'mail';
  else if (/打标|加标签/.test(t)) followup = 'tag';

  return { select: true, count, followup };
}

/**
 * 是否在问「刚才做了什么 / 上一轮搜了什么」这类元问题。
 * 这类问题不应触发新的筛选，应该让小A 复述上一次的查询条件。
 */
export function isMetaRecapQuestion(text: string): boolean {
  const t = normalizeCopilotUserText(text);
  if (!t) return false;
  if (/^刚才/.test(t) || /刚刚/.test(t)) {
    if (/做了什么|搜(索|了)什么|搜了什么|找了什么|查了什么|筛(选|了)什么|怎么(做|搜|筛)|是怎么.*(搜|筛|查)/.test(t)) {
      return true;
    }
    if (/搜索了什么|筛选了什么|搜了啥|做了啥|查了啥/.test(t)) {
      return true;
    }
  }
  if (/上一(轮|次).*(搜|筛|查|做)/.test(t)) return true;
  if (/^我们.*(搜|筛|查).*什么/.test(t)) return true;
  if (/你刚.*(做|搜|筛|查)/.test(t)) return true;
  return false;
}

/** 提取结果是否像误匹配的整句片段，而非真实姓名 */
export function isImplausiblePersonName(name: string, fullText?: string): boolean {
  const n = name.trim();
  if (!n || n.length < 2) return true;
  if (n.length > 24) return true;
  if (/^(到|在|从|把|将|给|帮|请)/.test(n)) return true;
  if (/待联系|已联系|沟通中|合作中|粉丝|最高|最低|平台|红人|达人|筛选|排序|查询|搜索|一个|一位/.test(n)) {
    return true;
  }
  if (fullText && isFilterLikeInfluencerQuery(fullText)) {
    if (/待联系|粉丝|最高|最低|平台|ins|instagram|tiktok/i.test(n)) return true;
  }
  return false;
}
