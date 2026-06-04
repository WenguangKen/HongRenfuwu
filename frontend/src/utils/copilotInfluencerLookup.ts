import { influencerService } from '@/services/influencerService';
import type { InfluencerFilterDto } from '@/types/influencer';

export type CopilotLookupHit = {
  id: number;
  name?: string;
  handle?: string;
  /** POOL / ONBOARDED / TRASH */
  stage: string;
  /** 红人状态：UNSCREENED / REJECTED / PENDING / CONTACTED ... */
  status: string;
  defaultPlatform?: string;
};

export type CopilotLookupResult = {
  /** 总命中数（含所有 stage） */
  total: number;
  /** 唯一命中或最佳候选（优先 ONBOARDED → POOL） */
  primary?: CopilotLookupHit;
  /** 全部命中（最多 10 条） */
  hits: CopilotLookupHit[];
};

/** 跨 stage 全库查找指定 handle（不限资源池/红人列表） */
export async function lookupInfluencerByHandle(
  handle: string
): Promise<CopilotLookupResult> {
  const key = handle.trim().replace(/^@/, '');
  if (!key) return { total: 0, hits: [] };
  const params: InfluencerFilterDto = {
    socialHandle: key,
    page: 1,
    size: 10,
  } as unknown as InfluencerFilterDto;
  try {
    const res = await influencerService.getList(params);
    const list = (res.content || []).filter((x) => x && (x as { id?: number }).id);
    const hits: CopilotLookupHit[] = list.map((item) => {
      const x = item as unknown as Record<string, unknown>;
      const sm = (x.socialMedias || x.socialMediaList || []) as Array<{ handle?: string; platform?: string }>;
      const matched = sm.find((s) => (s?.handle || '').toLowerCase() === key.toLowerCase()) ?? sm[0];
      return {
        id: Number(x.id),
        name: String(x.name || x.realName || x.nickName || ''),
        handle: matched?.handle ?? key,
        stage: String(x.stage || 'ONBOARDED'),
        status: String(x.status || 'PENDING'),
        defaultPlatform: matched?.platform ?? (x.defaultPlatform as string | undefined),
      };
    });
    const sorted = [...hits].sort((a, b) => stageRank(a.stage) - stageRank(b.stage));
    return { total: hits.length, primary: sorted[0], hits };
  } catch (e) {
    console.error('lookupInfluencerByHandle failed', e);
    return { total: 0, hits: [] };
  }
}

function stageRank(stage: string): number {
  if (stage === 'ONBOARDED') return 0;
  if (stage === 'POOL') return 1;
  if (stage === 'TRASH') return 2;
  return 3;
}

const STATUS_TO_LIST_TAB: Record<string, string> = {
  PENDING: 'pending',
  CONTACTED: 'contacted',
  COMMUNICATING: 'communicating',
  COOPERATING: 'cooperating',
  DORMANT: 'dormant',
  PAUSED: 'paused',
  BLACKLIST: 'blacklist',
  TERMINATED: 'terminated',
};

const POOL_STATUS_TO_TAB: Record<string, string> = {
  UNSCREENED: 'to-filter',
  REJECTED: 'rejected',
};

/** 红人列表 Tab 中文名 */
const LIST_TAB_LABEL: Record<string, string> = {
  pending: '待联系',
  contacted: '已联系',
  communicating: '沟通中',
  cooperating: '合作中',
  dormant: '休眠中',
  paused: '暂不合作',
  blacklist: '黑名单',
  terminated: '不再合作',
};

const POOL_TAB_LABEL: Record<string, string> = {
  'to-filter': '资源池 · 待筛选',
  rejected: '资源池 · 暂不合适',
};

export type CopilotInfluencerDestination = {
  path: string;
  query: Record<string, string>;
  pageLabel: string;
  tabLabel: string;
};

export type CopilotMultiLookupItem = {
  handle: string;
  result: CopilotLookupResult;
};

/** 批量按 handle 全库查找 */
export async function lookupMultipleInfluencersByHandle(
  handles: string[]
): Promise<CopilotMultiLookupItem[]> {
  const unique = dedupeHandles(handles);
  return Promise.all(
    unique.map(async (handle) => ({
      handle,
      result: await lookupInfluencerByHandle(handle),
    }))
  );
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

/** 描述单个 handle 的查找结果（用于多条汇总） */
export function describeHandleLookupLine(
  handle: string,
  result: CopilotLookupResult
): string {
  if (result.primary) {
    const dest = buildDestinationForHit(result.primary);
    const name = result.primary.name || `@${result.primary.handle}`;
    return `@${handle}：在【${dest.pageLabel} · ${dest.tabLabel}】找到 ${name}`;
  }
  return `@${handle}：红人列表和资源池均未找到`;
}

export type CopilotLookupReplyPayload = {
  userMessage: string;
  entries: Array<{
    handle: string;
    found: boolean;
    name?: string;
    pageLabel?: string;
    tabLabel?: string;
  }>;
  listUpdated: boolean;
  listPageLabel?: string;
  listTabLabel?: string;
  combinedHandles?: string;
};

export type MultiHandleLookupPlan = {
  destination?: CopilotInfluencerDestination;
  foundItems: CopilotMultiLookupItem[];
  payload: CopilotLookupReplyPayload;
};

/** 解析多 handle 查库结果：导航 + 供 AI 组织回复的结构化事实 */
export function resolveMultiHandleLookup(
  userMessage: string,
  items: CopilotMultiLookupItem[]
): MultiHandleLookupPlan {
  const found = items.filter(({ result }) => !!result.primary);
  const destination = buildBatchDestination(found);
  return {
    destination,
    foundItems: found,
    payload: buildLookupReplyPayload(userMessage, items, destination),
  };
}

export function buildLookupReplyPayload(
  userMessage: string,
  items: CopilotMultiLookupItem[],
  destination?: CopilotInfluencerDestination
): CopilotLookupReplyPayload {
  const entries = items.map(({ handle, result }) => {
    if (result.primary) {
      const dest = buildDestinationForHit(result.primary);
      return {
        handle,
        found: true,
        name: result.primary.name,
        pageLabel: dest.pageLabel,
        tabLabel: dest.tabLabel,
      };
    }
    return { handle, found: false };
  });
  return {
    userMessage,
    entries,
    listUpdated: !!destination,
    listPageLabel: destination?.pageLabel,
    listTabLabel: destination?.tabLabel,
    combinedHandles: destination?.query?.handle,
  };
}

/** 多位已命中红人：合并 handle 一次性筛出 */
export function buildBatchDestination(
  foundItems: CopilotMultiLookupItem[]
): CopilotInfluencerDestination | undefined {
  const hits = foundItems
    .map((i) => i.result.primary)
    .filter((h): h is CopilotLookupHit => !!h);
  if (hits.length === 0) return undefined;
  if (hits.length === 1) return buildDestinationForHit(hits[0]!);

  const onboarded = hits.filter((h) => h.stage !== 'POOL' && h.stage !== 'TRASH');
  const poolOnly = hits.filter((h) => h.stage === 'POOL');

  if (onboarded.length === hits.length) {
    const tabs = onboarded.map((h) => STATUS_TO_LIST_TAB[h.status] ?? 'all');
    const uniqueTabs = [...new Set(tabs)];
    const tab = uniqueTabs.length === 1 ? uniqueTabs[0]! : 'all';
    const handles = onboarded
      .map((h) => h.handle || '')
      .filter(Boolean)
      .join(',');
    const sameStatus = uniqueTabs.length === 1 ? onboarded[0]!.status : '';
    return {
      path: '/influencer/list',
      query: {
        fromAi: '1',
        status: tab,
        handle: handles,
        listStatus: tab === 'all' && sameStatus ? sameStatus : '',
      },
      pageLabel: '红人列表',
      tabLabel:
        uniqueTabs.length === 1 ? (LIST_TAB_LABEL[tab] ?? tab) : LIST_TAB_LABEL.all ?? '全部',
    };
  }

  if (poolOnly.length === hits.length) {
    const tabs = poolOnly.map((h) => POOL_STATUS_TO_TAB[h.status] ?? 'to-filter');
    const uniqueTabs = [...new Set(tabs)];
    const tab = uniqueTabs.length === 1 ? uniqueTabs[0]! : 'to-filter';
    const handles = poolOnly
      .map((h) => h.handle || '')
      .filter(Boolean)
      .join(',');
    return {
      path: '/influencer/pool',
      query: {
        fromAi: '1',
        status: poolOnly[0]!.status || 'UNSCREENED',
        activeKey: tab,
        handle: handles,
      },
      pageLabel: '资源池',
      tabLabel:
        uniqueTabs.length === 1
          ? (POOL_TAB_LABEL[tab] ?? '资源池')
          : POOL_TAB_LABEL['to-filter'] ?? '资源池',
    };
  }

  return buildDestinationForHit((onboarded[0] ?? hits[0])!);
}

/** 查库失败时的极简兜底（AI 不可用时） */
export function buildLookupReplyFallback(
  payload: CopilotLookupReplyPayload,
  honorific: string
): string {
  const lead = honorific === '您' ? '' : `${honorific}，`;
  const found = payload.entries.filter((e) => e.found);
  if (found.length === 0) {
    const names = payload.entries.map((e) => `@${e.handle}`).join('、');
    return `${lead}查遍了列表和资源池，${names} 都没找到，您核对下拼写试试。`;
  }
  if (payload.listUpdated && payload.listTabLabel) {
    return `${lead}查完了，${found.length} 位在系统里，已经在【${payload.listTabLabel}】列表筛出来给您看了。`;
  }
  return `${lead}查完了，${found.length} 位在系统里。`;
}

export function buildDestinationForHit(hit: CopilotLookupHit): CopilotInfluencerDestination {
  if (hit.stage === 'POOL') {
    const tab = POOL_STATUS_TO_TAB[hit.status] ?? 'to-filter';
    return {
      path: '/influencer/pool',
      query: {
        fromAi: '1',
        status: hit.status || 'UNSCREENED',
        activeKey: tab,
        handle: hit.handle || '',
      },
      pageLabel: '资源池',
      tabLabel: POOL_TAB_LABEL[tab] ?? '资源池',
    };
  }
  const tab = STATUS_TO_LIST_TAB[hit.status] ?? 'all';
  return {
    path: '/influencer/list',
    query: {
      fromAi: '1',
      status: tab,
      handle: hit.handle || '',
      listStatus: tab === 'all' ? hit.status : '',
    },
    pageLabel: '红人列表',
    tabLabel: LIST_TAB_LABEL[tab] ?? '全部',
  };
}
