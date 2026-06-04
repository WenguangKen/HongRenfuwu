import type { ChatMsg } from '@/utils/copilotTypes';
import type { CopilotSummaryStep } from '@/utils/copilotSummary';
import {
  isLikelySocialHandle,
  parseHandleFromText,
  parseMultipleHandlesFromText,
  parseSearchNameFromText,
  refersToPreviousInfluencer,
  userWantsOpenDetail,
} from '@/utils/copilotHandle';

export type CopilotSessionContext = {
  lastHandle?: string;
  /** 多红人场景下的 handle 列表 */
  lastHandles?: string[];
  lastInfluencerId?: number;
  /** 多红人场景下的 ID 列表 */
  lastInfluencerIds?: number[];
  lastSearchName?: string;
  /** 当前列表展示/勾选的人数（如 Top 10 则为 10） */
  lastMatchCount?: number;
  /** 筛选命中的总人数（如 Top 10 场景下可能为 91） */
  lastMatchedTotal?: number;
  /** AI 截顶展示条数（如 10） */
  lastListLimit?: number;
  /** 最近一次筛选条件摘要（供上下文条与跟进指令） */
  lastFilterSteps?: CopilotSummaryStep[];
  /** 最近一次操作类型 */
  lastAction?: 'filter' | 'export' | 'order' | 'discount' | 'navigate' | 'mail';
};

export type ResolvedMailInviteTargets = {
  ids: number[];
  handles: string[];
  missingContext: boolean;
};

/** 从当前句 + 会话上下文解析邀约邮件的目标红人 */
export function resolveMailInviteTargets(userMessage: string): ResolvedMailInviteTargets {
  const ctx = loadCopilotContext();
  const multi = parseMultipleHandlesFromText(userMessage);
  if (multi.length) {
    return { ids: [], handles: multi, missingContext: false };
  }
  const single = parseHandleFromText(userMessage);
  if (single) {
    return { ids: [], handles: [single], missingContext: false };
  }
  const idMatch = userMessage.match(/红人\s*(\d+)/);
  if (idMatch) {
    const id = Number(idMatch[1]);
    if (id > 0) return { ids: [id], handles: [], missingContext: false };
  }

  const refers =
    refersToPreviousInfluencer(userMessage) ||
    /这两个|这两位|上面.*红人|刚才.*红人/.test(userMessage);

  if (refers) {
    if (ctx.lastInfluencerIds?.length) {
      return {
        ids: ctx.lastInfluencerIds,
        handles: ctx.lastHandles ?? [],
        missingContext: false,
      };
    }
    if (ctx.lastHandles?.length) {
      return { ids: [], handles: ctx.lastHandles, missingContext: false };
    }
    if (ctx.lastHandle?.includes(',')) {
      const handles = ctx.lastHandle
        .split(/[,，]+/)
        .map((h) => h.trim())
        .filter(Boolean);
      if (handles.length) return { ids: [], handles, missingContext: false };
    }
    if (ctx.lastHandle) {
      return {
        ids: ctx.lastInfluencerId ? [ctx.lastInfluencerId] : [],
        handles: [ctx.lastHandle],
        missingContext: false,
      };
    }
    if (ctx.lastMatchCount && ctx.lastMatchCount > 0) {
      return { ids: [], handles: [], missingContext: true };
    }
  }

  return { ids: [], handles: [], missingContext: true };
}

/** 邀约邮件是否已能确定目标红人 */
export function hasMailInviteTarget(userMessage: string): boolean {
  const t = resolveMailInviteTargets(userMessage);
  return !t.missingContext && (t.ids.length > 0 || t.handles.length > 0);
}

/** 将会话上下文转为聊天面板顶部的 chips */
export function buildSessionContextChips(ctx: CopilotSessionContext): string[] {
  const chips: string[] = [];
  for (const step of ctx.lastFilterSteps ?? []) {
    if (step.label && step.value) chips.push(`${step.label} · ${step.value}`);
  }
  if (ctx.lastMatchCount != null && ctx.lastMatchCount > 0) {
    const n = ctx.lastListLimit ?? ctx.lastMatchCount;
    chips.push(`已选 ${n} 人`);
  } else if (ctx.lastHandles?.length) {
    chips.push(ctx.lastHandles.map((h) => `@${h}`).join('、'));
  } else if (ctx.lastHandle) {
    chips.push(`@${ctx.lastHandle}`);
  } else if (ctx.lastSearchName) {
    chips.push(ctx.lastSearchName);
  }
  if (ctx.lastAction === 'export') {
    chips.push('导出进行中');
  }
  return chips.slice(0, 5);
}

const CTX_KEY = 'ai_copilot_session_ctx';

export function loadCopilotContext(): CopilotSessionContext {
  try {
    const raw = sessionStorage.getItem(CTX_KEY);
    if (!raw) return {};
    return JSON.parse(raw) as CopilotSessionContext;
  } catch {
    return {};
  }
}

export function saveCopilotContext(ctx: CopilotSessionContext) {
  sessionStorage.setItem(CTX_KEY, JSON.stringify(ctx));
}

export function rememberCopilotMatch(partial: CopilotSessionContext) {
  const prev = loadCopilotContext();
  saveCopilotContext({ ...prev, ...partial });
}

/** 从列表筛选结果写入多红人/单红人上下文 */
export function rememberFromFilterHit(
  filterData: Record<string, unknown>,
  matchCount?: number
) {
  const handleRaw = filterData.socialHandle ? String(filterData.socialHandle) : undefined;
  const id =
    filterData.influencerId != null && !Number.isNaN(Number(filterData.influencerId))
      ? Number(filterData.influencerId)
      : undefined;
  const count = matchCount ?? (handleRaw?.includes(',') ? handleRaw.split(/[,，]+/).length : 1);

  if (handleRaw?.includes(',')) {
    const handles = handleRaw
      .split(/[,，]+/)
      .map((h) => h.trim().replace(/^@/, ''))
      .filter(Boolean);
    rememberCopilotMatch({
      lastHandle: handles.join(','),
      lastHandles: handles,
      lastInfluencerId: id,
      lastMatchCount: count,
    });
    return;
  }

  if (handleRaw || id != null) {
    rememberCopilotMatch({
      lastHandle: handleRaw,
      lastHandles: handleRaw ? [handleRaw.replace(/^@/, '')] : undefined,
      lastInfluencerId: id,
      lastMatchCount: count,
    });
  }
}

/** 从近期对话回复中回溯 handle / 姓名 */
export function extractHandleFromMessages(messages: ChatMsg[]): string | undefined {
  for (let i = messages.length - 1; i >= 0; i--) {
    const m = messages[i]!;
    if (m.role !== 'assistant') continue;
    const t = m.content;
    const patterns = [
      /社媒账号[「『"]?\s*([a-z0-9._-]{2,64})/i,
      /handle[「『"]\s*([a-z0-9._-]{2,64})/i,
      /账号[「『"]?\s*([a-z0-9._-]{2,64})/i,
      /筛选\s*→\s*[^·]*社媒账号[：:]\s*([a-z0-9._-]{2,64})/i,
    ];
    for (const re of patterns) {
      const hit = t.match(re);
      if (hit?.[1]) return hit[1].replace(/^@/, '');
    }
  }
  return undefined;
}

export type ResolvedCopilotTarget = {
  handle?: string;
  searchName?: string;
  influencerId?: number;
  openDetail?: boolean;
  /** 用户要开详情但未指明对象 */
  needsClarification?: boolean;
};

/** 结合当前句 + 会话上下文解析操作对象 */
export function resolveCopilotTarget(
  userMessage: string,
  messages: ChatMsg[]
): ResolvedCopilotTarget {
  const ctx = loadCopilotContext();
  const openDetail = userWantsOpenDetail(userMessage);
  const useCtx = refersToPreviousInfluencer(userMessage) || (openDetail && !parseHandleFromText(userMessage));

  let handle = parseHandleFromText(userMessage) ?? undefined;
  let searchName = parseSearchNameFromText(userMessage) ?? undefined;

  if (searchName && isLikelySocialHandle(searchName)) {
    handle = searchName.replace(/^@/, '');
    searchName = undefined;
  }

  if (!handle && !searchName && useCtx) {
    if (ctx.lastHandles?.length === 1) {
      handle = ctx.lastHandles[0];
    } else {
      handle = ctx.lastHandle ?? extractHandleFromMessages(messages);
    }
    searchName = ctx.lastSearchName;
  }

  let influencerId = ctx.lastInfluencerId;
  if (openDetail && !handle && !searchName && !influencerId && !useCtx) {
    return { openDetail: true, needsClarification: true };
  }
  if (openDetail && (handle || influencerId || (useCtx && ctx.lastMatchCount === 1))) {
    return { handle, searchName, influencerId, openDetail: true };
  }
  if (openDetail && useCtx && (ctx.lastHandle || ctx.lastSearchName)) {
    return {
      handle: ctx.lastHandle,
      searchName: ctx.lastSearchName,
      influencerId: ctx.lastInfluencerId,
      openDetail: true,
    };
  }
  if (handle) return { handle, influencerId };
  if (searchName) return { searchName };
  if (useCtx && ctx.lastHandle) return { handle: ctx.lastHandle, influencerId: ctx.lastInfluencerId };
  return {};
}
