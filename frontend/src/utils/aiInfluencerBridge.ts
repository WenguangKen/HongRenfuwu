import type { AiUiAction } from '@/utils/aiActionBus';
import {
  fanRangeLabel,
  isBroadQueryText,
  parseFanRangeFromText,
  type FanRange,
} from '@/utils/copilotFanRange';
import {
  isHandleLookupQuestion,
  isLikelySocialHandle,
  parseHandleFromText,
  parseNavigatePageFromText,
  parseSearchNameFromText,
  userWantsCloseDetail,
} from '@/utils/copilotHandle';
import { mergeFilterData, parseListFiltersFromText, type CopilotListFilterData } from '@/utils/copilotListFilters';
import {
  isFilterLikeInfluencerQuery,
  normalizeCopilotUserText,
} from '@/utils/copilotTextNormalize';
import { isOrderRelatedMessage } from '@/utils/copilotOrderFilters';

/** 中文/英文状态 → 红人列表 Tab key */
const STATUS_TAB_MAP: Record<string, string> = {
  休眠: 'dormant',
  休眠中: 'dormant',
  DORMANT: 'dormant',
  合作中: 'cooperating',
  已合作: 'cooperating',
  合作: 'cooperating',
  COOPERATING: 'cooperating',
  待联系: 'pending',
  PENDING: 'pending',
  已联系: 'contacted',
  CONTACTED: 'contacted',
  沟通中: 'communicating',
  COMMUNICATING: 'communicating',
  暂不合作: 'paused',
  PAUSED: 'paused',
  黑名单: 'blacklist',
  BLACKLIST: 'blacklist',
  不再合作: 'terminated',
  TERMINATED: 'terminated',
};

const TAB_LABEL: Record<string, string> = {
  dormant: '休眠中',
  cooperating: '合作中',
  pending: '待联系',
  contacted: '已联系',
  communicating: '沟通中',
  paused: '暂不合作',
  blacklist: '黑名单',
  terminated: '不再合作',
  all: '全部',
};

export function resolveStatusTab(status?: string): string | undefined {
  if (!status?.trim()) return undefined;
  const key = status.trim();
  return STATUS_TAB_MAP[key] ?? STATUS_TAB_MAP[key.toUpperCase()];
}

export function tabLabel(tab: string): string {
  return TAB_LABEL[tab] ?? tab;
}

/** 从模型泄漏的 FunctionCall 文本解析 searchInfluencers 参数 */
export function parseSearchInfluencersCall(text: string): {
  platform?: string;
  isCooperated?: boolean;
  country?: string;
  status?: string;
  statusTab?: string;
  semanticQuery?: string;
} | null {
  if (!/functionCallBegin/i.test(text) && !/"name"\s*:\s*"searchInfluencers"/i.test(text)) {
    return null;
  }
  const jsonMatch =
    text.match(/\[\s*\{[\s\S]*?"name"\s*:\s*"searchInfluencers"[\s\S]*?\}\s*\]/i) ??
    text.match(/\{\s*"name"\s*:\s*"searchInfluencers"[\s\S]*?\}/i);
  if (!jsonMatch) return null;
  try {
    const raw = JSON.parse(jsonMatch[0]);
    const item = Array.isArray(raw) ? raw[0] : raw;
    const p = item?.parameters ?? item?.params ?? {};
    const statusRaw = p.status as string | undefined;
    let statusTab = resolveStatusTab(statusRaw);
    if (p.isCooperated === true || statusRaw === '已合作') {
      statusTab = statusTab ?? 'cooperating';
    }
    return {
      platform: p.platform,
      isCooperated: p.isCooperated,
      country: p.country,
      status: statusRaw,
      statusTab,
      semanticQuery: p.semanticQuery ?? p.query,
    };
  } catch {
    return null;
  }
}

type FilterParams = ReturnType<typeof parseSearchInfluencersCall> &
  FanRange &
  CopilotListFilterData & { socialHandle?: string; influencerId?: number };

function pickListFilterFields(params: CopilotListFilterData): CopilotListFilterData {
  const keys: (keyof CopilotListFilterData)[] = [
    'socialPlatform', 'platform', 'race', 'sortBy', 'listLimit', 'influencerType', 'isPaid',
    'minSampleCount', 'maxSampleCount', 'hasOrders', 'listStatus', 'brand', 'country',
    'email', 'ownerName', 'contactPersonName', 'link', 'source', 'tagNames',
    'hasOutputContent', 'timeType', 'timeStart', 'timeEnd', 'searchKey', 'statusTab',
  ];
  const out: CopilotListFilterData = {};
  for (const k of keys) {
    const v = params[k];
    if (v !== undefined && v !== null && v !== '') {
      (out as Record<string, unknown>)[k] = v;
    }
  }
  return out;
}

export function buildFilterAction(params: FilterParams | null, userMsg?: string): AiUiAction | null {
  if (!params) return null;
  const tab = params.statusTab ?? (params.isCooperated ? 'cooperating' : undefined);
  const label = tab ? tabLabel(tab) : '全部';
  const fan =
    params.fansMin !== undefined || params.fansMax !== undefined
      ? { fansMin: params.fansMin, fansMax: params.fansMax }
      : parseFanRangeFromText(userMsg || params.semanticQuery || '');
  const fanDesc = fanRangeLabel(fan);
  const useName =
    params.semanticQuery && !isBroadQueryText(params.semanticQuery) && !params.socialHandle;
  if (params.socialHandle) {
    return {
      name: 'ApplyInfluencerFilter',
      data: {
        statusTab: tab ?? 'all',
        socialHandle: params.socialHandle,
        ...pickListFilterFields(params),
        ...(params.influencerId != null ? { influencerId: params.influencerId } : {}),
        summary: `已在【红人列表】按社媒账号「${params.socialHandle}」筛选，请查看左侧表格。`,
      },
    };
  }
  const parts = [label];
  if (fanDesc) parts.push(fanDesc);
  const cond = parts.join(' · ');
  return {
    name: 'ApplyInfluencerFilter',
    data: {
      statusTab: tab ?? 'all',
      platform: params.platform,
      country: params.country,
      fansMin: fan.fansMin,
      fansMax: fan.fansMax,
      ...pickListFilterFields(params),
      ...(params.influencerId != null ? { influencerId: params.influencerId } : {}),
      ...(useName ? { searchKey: params.semanticQuery } : {}),
      summary: `已为您在【红人列表】应用筛选：${cond}。请在左侧表格查看结果与数量。`,
    },
  };
}

export function buildHandleLookupAction(handle: string): AiUiAction {
  return {
    name: 'ApplyInfluencerFilter',
    data: {
      statusTab: 'all',
      socialHandle: handle,
      summary: `已在【红人列表】按账号「${handle}」筛选，请查看左侧表格。`,
    },
  };
}

export function buildNameSearchAction(name: string): AiUiAction {
  return {
    name: 'ApplyInfluencerFilter',
    data: {
      statusTab: 'all',
      searchKey: name.trim(),
      summary: `已在【红人列表】按名称「${name.trim()}」筛选，请查看左侧表格。`,
    },
  };
}

/** 用户话术兜底：模型未调工具时仍尝试跳转列表 */
export function inferFilterFromUserMessage(msg: string): AiUiAction | null {
  const text = normalizeCopilotUserText(msg);
  if (!text) return null;
  if (userWantsCloseDetail(text)) {
    return { name: 'CloseInfluencerDetail', data: { summary: '已关闭红人详情页。' } };
  }
  if (isOrderRelatedMessage(text)) {
    return null;
  }
  const statusPatterns: Array<{ re: RegExp; tab: string }> = [
    { re: /休眠中|休眠(?!\s*账)/, tab: 'dormant' },
    { re: /合作中|已合作|在合作/, tab: 'cooperating' },
    { re: /待联系/, tab: 'pending' },
    { re: /已联系/, tab: 'contacted' },
    { re: /沟通中/, tab: 'communicating' },
    { re: /暂不合作/, tab: 'paused' },
    { re: /黑名单/, tab: 'blacklist' },
    { re: /不再合作/, tab: 'terminated' },
  ];
  const listExtra = parseListFiltersFromText(text);
  const fan = parseFanRangeFromText(text);
  const filterLike = isFilterLikeInfluencerQuery(text);

  const handle = parseHandleFromText(text);
  if (handle) {
    return {
      name: 'ApplyInfluencerFilter',
      data: {
        statusTab: listExtra.statusTab ?? 'all',
        socialHandle: handle,
        ...pickListFilterFields({ ...fan, ...listExtra }),
        summary: `已在【红人列表】按社媒账号「${handle}」筛选，请查看左侧表格。`,
      },
    };
  }

  for (const { re, tab } of statusPatterns) {
    if (re.test(text)) {
      return buildFilterAction({ statusTab: tab, ...fan, ...listExtra }, text);
    }
  }
  if (fan.fansMin != null || fan.fansMax != null || Object.keys(listExtra).length > 0) {
    return buildFilterAction({ statusTab: listExtra.statusTab ?? 'all', ...fan, ...listExtra }, text);
  }

  const personName = parseSearchNameFromText(text);
  if (personName && !filterLike) {
    if (isLikelySocialHandle(personName)) {
      return buildHandleLookupAction(personName.replace(/^@/, ''));
    }
    return buildNameSearchAction(personName);
  }
  const nav = parseNavigatePageFromText(text);
  if (nav) {
    return {
      name: 'NavigateToPage',
      data: { path: nav, summary: '已为您跳转到对应页面。' },
    };
  }
  if (isHandleLookupQuestion(text)) {
    return null;
  }
  if (isInfluencerRelatedMessage(text)) {
    return buildFilterAction({ statusTab: 'all', ...listExtra }, text);
  }
  return null;
}

/** 是否属于红人列表/筛选类指令（用于发送后立刻跳转列表） */
export function isInfluencerRelatedMessage(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  if (isOrderRelatedMessage(t)) return false;
  if (/样品单|样品订单/i.test(t)) return false;
  if (/导出|下载.*(excel|表格|xlsx)/i.test(t)) return false;
  return /红人|达人|kol|influencer|呈现|显示|列出|展示|检索|筛选|查询|搜索|有多少|美妆|tiktok|instagram|粉丝|handle|社媒|@|白人|黑人|肤色|人种|亚裔|拉丁/i.test(
    t
  );
}

export function parseAiUiActionsExtended(text: string): AiUiAction[] {
  const actions: AiUiAction[] = [];
  const fc = parseSearchInfluencersCall(text);
  const filterAction = buildFilterAction(
    fc ? { ...fc, ...parseFanRangeFromText(fc.semanticQuery || '') } : null
  );
  if (filterAction) actions.push(filterAction);

  if (text.includes('[OpenExportModal]') || text.includes('[RenderExportConfirmModal]')) {
    const target = text.includes('order_conversion')
      ? 'order_conversion'
      : text.includes('order_sample') || text.includes('样品')
        ? 'order_sample'
        : 'influencer';
    actions.push({
      name: 'OpenExportModal',
      data: { target, scope: 'filtered' },
    });
  }
  const jsonMatch = text.match(/\[[\s\S]*"id"[\s\S]*\]/);
  if (jsonMatch) {
    try {
      const arr = JSON.parse(jsonMatch[0]);
      if (Array.isArray(arr) && arr.length > 0) {
        actions.push({
          name: 'RenderInfluencerList',
          data: { influencers: arr, count: arr.length },
        });
        actions.push({
          name: 'ApplyInfluencerFilter',
          data: {
            statusTab: 'all',
            summary: `小A 共检索到 ${arr.length} 位红人，已在工作台展示；您也可在红人列表查看。`,
          },
        });
      }
    } catch {
      /* ignore */
    }
  }
  return actions;
}
