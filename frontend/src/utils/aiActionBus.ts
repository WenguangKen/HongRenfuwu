export type AiUiAction = { name: string; data: Record<string, unknown> };

type Listener = (action: AiUiAction) => void;

const listeners = new Set<Listener>();

export function onAiUiAction(listener: Listener): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

export function emitAiUiAction(action: AiUiAction): void {
  listeners.forEach((fn) => fn(action));
}

/** 发射关闭所有弹窗事件（页面导航前调用） */
export function emitCloseAllModals(): void {
  emitAiUiAction({ name: 'CloseAllModals', data: {} });
}

export type AiFilterCompletePayload = {
  total: number;
  /** 为 false 表示未改动列表（0 条命中已回滚） */
  applied?: boolean;
  handle?: string;
  searchName?: string;
  influencerId?: number;
  fansMax?: number;
  fansMin?: number;
  /** 符合筛选条件的总人数（可能与 total 不同，例如只展示前 N 条） */
  matchedTotal?: number;
  listLimit?: number;
  sortBy?: string;
};

type FilterCompleteListener = (payload: AiFilterCompletePayload) => void;
const filterCompleteListeners = new Set<FilterCompleteListener>();

export function onAiFilterComplete(listener: FilterCompleteListener): () => void {
  filterCompleteListeners.add(listener);
  return () => filterCompleteListeners.delete(listener);
}

export function emitAiFilterComplete(payload: AiFilterCompletePayload): void {
  filterCompleteListeners.forEach((fn) => fn(payload));
}

export type AiOrderFilterCompletePayload = {
  total: number;
  applied?: boolean;
  orderNo?: string;
  shopifyOrderId?: string;
  page: '/order/conversion' | '/order/sample' | '/commission/order';
};

type OrderFilterCompleteListener = (payload: AiOrderFilterCompletePayload) => void;
const orderFilterCompleteListeners = new Set<OrderFilterCompleteListener>();

export function onAiOrderFilterComplete(listener: OrderFilterCompleteListener): () => void {
  orderFilterCompleteListeners.add(listener);
  return () => orderFilterCompleteListeners.delete(listener);
}

export function emitAiOrderFilterComplete(payload: AiOrderFilterCompletePayload): void {
  orderFilterCompleteListeners.forEach((fn) => fn(payload));
}

import { parseAiUiActionsExtended } from '@/utils/aiInfluencerBridge';

/** 从助手回复文本中解析 UI 指令（含 searchInfluencers 工具调用） */
export function parseAiUiActions(text: string): AiUiAction[] {
  return parseAiUiActionsExtended(text);
}
