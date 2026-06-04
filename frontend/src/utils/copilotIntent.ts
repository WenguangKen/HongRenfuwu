import type { ChatMsg } from '@/utils/copilotTypes';
import { isOrderRelatedMessage } from '@/utils/copilotOrderFilters';
import {
  extractHandleFromMessages,
  loadCopilotContext,
  saveCopilotContext,
} from '@/utils/copilotContext';
import {
  parseHandleFromText,
  refersToPreviousInfluencer,
} from '@/utils/copilotHandle';

/** 新一轮红人条件检索（应清空上一轮 handle/筛选残留） */
export function isNewBroadInfluencerQuery(text: string): boolean {
  const t = text.trim();
  if (!t || isOrderRelatedMessage(t)) return false;
  if (refersToPreviousInfluencer(t)) return false;
  if (parseHandleFromText(t) && !/有多少|多少|粉丝|筛选|看看|尚未|未合作/i.test(t)) {
    return false;
  }
  return /有多少|多少位|让我看看|看看有|筛选|查询|搜索|粉丝|尚未合作|未合作|合作中|休眠|美妆|tiktok|instagram/i.test(
    t
  );
}

export function clearCopilotSessionContext(): void {
  saveCopilotContext({});
}

/** 订单/折扣码场景：解析句中或上下文红人 handle/姓名 */
export function resolveInfluencerRefForCopilot(
  text: string,
  messages: ChatMsg[]
): string | undefined {
  const fromText = parseHandleFromText(text);
  if (fromText) return fromText;

  const named = text.match(
    /^@?([a-z][a-z0-9._-]{2,64})\s+这个红人/i
  );
  if (named?.[1]) return named[1];

  if (refersToPreviousInfluencer(text) || /这个红人|该红人|这位红人/i.test(text)) {
    const ctx = loadCopilotContext();
    return ctx.lastHandle ?? extractHandleFromMessages(messages) ?? ctx.lastSearchName;
  }
  return undefined;
}
