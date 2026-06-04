<template>
  <Teleport v-if="showCopilot" to="body">
    <aside
      v-show="open"
      ref="panelRef"
      class="ai-copilot-panel"
      :class="{ dragging: isDragging }"
      :style="panelStyle"
    >
      <header class="ai-copilot-header" @mousedown="onDragStart">
        <div class="header-left">
          <span class="drag-hint" title="拖动标题栏移动窗口">⠿</span>
          <div class="avatar">
            <span class="avatar-core">A</span>
          </div>
          <div class="title-wrap">
            <span class="title">小A</span>
            <span class="subtitle">{{ headerSubtitle }}</span>
          </div>
        </div>
        <div class="header-actions">
          <button
            v-if="messages.length > 0"
            type="button"
            class="header-btn header-btn-text"
            title="清空本会话记录"
            @mousedown.stop
            @click="clearChatHistory()"
          >
            清空
          </button>
          <button type="button" class="header-btn" title="最小化" @mousedown.stop @click="open = false">—</button>
          <button type="button" class="header-btn" title="关闭" @mousedown.stop @click="closePanel">×</button>
        </div>
      </header>

      <div v-if="sessionContextChips.length" class="session-context-bar">
        <span class="ctx-label">当前上下文</span>
        <span v-for="(chip, ci) in sessionContextChips" :key="ci" class="ctx-chip">{{ chip }}</span>
      </div>

      <main class="ai-copilot-messages" ref="scrollRef">
        <div v-if="messages.length === 0" class="empty-state">
          <div class="empty-hero">
            <h3 class="empty-heading">{{ emptyGreeting }}</h3>
            <p class="empty-desc">为{{ honorific }}服务：红人检索、列表筛选、转化/样品订单与页面联动。</p>
          </div>
          <div v-if="!userGender" class="gender-pick">
            <span class="gender-pick-label">怎么称呼您？</span>
            <button type="button" class="gender-btn" @click="setUserGender(1)">男生 · 小哥</button>
            <button type="button" class="gender-btn gender-btn-f" @click="setUserGender(2)">女生 · 公主</button>
          </div>
        </div>
        <div v-for="(msg, idx) in messages" :key="idx" :class="['msg', msg.role]">
          <div
            v-if="isThinkingBubble(msg, idx)"
            class="bubble thinking-bubble"
          >
            <div class="thinking-inner">
              <span class="thinking-spinner" aria-hidden="true" />
              <span class="thinking-label">{{ thinkingLabel }}</span>
            </div>
          </div>
          <div
            v-else
            class="bubble"
            :class="{ error: isErrorMsg(msg.summary?.result || msg.content), 'bubble-structured': !!msg.summary?.actions?.length }"
          >
            <template v-if="msg.summary?.result && (msg.summary.actions?.length || msg.summary.lines?.length)">
              <div class="summary-head">{{ msg.summary.result }}</div>
              <ul v-if="msg.summary.lines?.length" class="summary-lines">
                <li v-for="(line, li) in msg.summary.lines" :key="li">{{ line }}</li>
              </ul>
              <p v-if="msg.summary.hint" class="summary-hint">{{ msg.summary.hint }}</p>
              <div v-if="msg.summary.actions?.length" class="quick-actions">
                <button
                  v-for="act in msg.summary.actions"
                  :key="act.id"
                  type="button"
                  class="quick-action-btn"
                  @click="runQuickAction(act.id)"
                >
                  {{ act.label }}
                </button>
              </div>
            </template>
            <template v-else>
              {{ displayMsgContent(msg) }}
            </template>
          </div>
        </div>
        <div ref="scrollAnchorRef" class="scroll-anchor" aria-hidden="true" />
      </main>

      <footer class="ai-copilot-input">
        <textarea
          v-model="input"
          rows="4"
          placeholder="输入问题，Enter 发送；意图不明确时小A 会先向您确认"
          :disabled="loading"
          @keydown.enter.exact.prevent="send"
        />
        <button type="button" class="send-btn" :disabled="loading || !input.trim()" @click="send">
          <span v-if="!loading">发送</span>
          <span v-else class="send-loading"><i class="dot" /><i class="dot" /><i class="dot" /></span>
        </button>
      </footer>
    </aside>

    <button
      type="button"
      class="ai-copilot-fab"
      :class="{ open }"
      :aria-label="open ? '收起小A' : '打开小A'"
      @click="toggleOpen"
    >
      <span class="fab-badge" aria-hidden="true">A</span>
      <span class="fab-label">{{ open ? '收起' : '小A' }}</span>
    </button>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/stores/user';
import { streamCopilotChat, generateCopilotLookupReply } from '@/services/aiCopilotService';
import type { AiUiAction } from '@/utils/aiActionBus';
import {
  emitAiUiAction,
  emitCloseAllModals,
  onAiFilterComplete,
  onAiOrderFilterComplete,
  parseAiUiActions,
  type AiFilterCompletePayload,
  type AiOrderFilterCompletePayload,
} from '@/utils/aiActionBus';
import { inferNavigationTarget, pageLabel as navPageLabel, PAGE_NAME } from '@/utils/copilotNavigation';
import {
  buildHandleLookupAction,
  buildNameSearchAction,
  inferFilterFromUserMessage,
  isInfluencerRelatedMessage,
} from '@/utils/aiInfluencerBridge';
import {
  buildSessionContextChips,
  loadCopilotContext,
  rememberCopilotMatch,
  rememberFromFilterHit,
  resolveCopilotTarget,
  resolveMailInviteTargets,
} from '@/utils/copilotContext';
import {
  clearCopilotSessionContext,
  isNewBroadInfluencerQuery,
  resolveInfluencerRefForCopilot,
} from '@/utils/copilotIntent';
import {
  describeFilterData,
  influencerFilterToRouteQuery,
  mergeFilterData,
  parseListFiltersFromText,
  parseListSortFromText,
} from '@/utils/copilotListFilters';
import { parseFanRangeFromText } from '@/utils/copilotFanRange';
import {
  describeOrderFilterData,
  isOrderRelatedMessage,
  orderFiltersToQuery,
  orderPageLabel,
  parseOrderFiltersFromText,
  resolveOrderPage,
  type OrderFilterData,
  type OrderPagePath,
} from '@/utils/copilotOrderFilters';
import {
  influencerSummaryCopy,
  orderPendingCopy,
  orderSummaryCopy,
} from '@/utils/copilotSummaryCopy';
import {
  type CopilotSummaryPayload,
  type CopilotSummaryStep,
} from '@/utils/copilotSummary';
import { formatFilterLines, type CopilotQuickAction } from '@/utils/copilotQuickActions';
import type { ChatMsg } from '@/utils/copilotTypes';
import {
  exportTargetLabel,
  exportTargetPath,
  inferExportScope,
  inferExportTarget,
  parseExportTemplateName,
  resolveExportScope,
  userWantsExport,
  type CopilotExportTarget,
} from '@/utils/copilotExport';
import {
  isBatchOpenDetailRequest,
  isMailInviteRequest,
  parseHandleFromText,
  parseMultipleHandlesFromText,
  userWantsCloseDetail,
  userWantsOpenDetail,
  userWantsRunAi,
} from '@/utils/copilotHandle';
import { readAuthToken } from '@/utils/authToken';
import { isToolCallArtifact } from '@/utils/copilotStreamFilter';
import { getOffTopicReply } from '@/utils/copilotOffTopic';
import { normalizeCopilotUserText, parseSelectIntent, isMetaRecapQuestion } from '@/utils/copilotTextNormalize';
import {
  buildCopilotIntentPlan,
  correctBusinessTypos,
  intentConfirmActions,
  intentConfirmHead,
  intentConfirmLines,
  isIntentCancelReply,
  isIntentConfirmReply,
  type CopilotIntentPlan,
} from '@/utils/copilotIntentPlanner';
import {
  buildDestinationForHit,
  buildLookupReplyFallback,
  buildLookupReplyPayload,
  lookupInfluencerByHandle,
  lookupMultipleInfluencersByHandle,
  resolveMultiHandleLookup,
  type CopilotLookupReplyPayload,
} from '@/utils/copilotInfluencerLookup';
import {
  honorificGreeting,
  loadUserGender,
  resolveHonorific,
  saveUserGender,
  type CopilotUserGender,
} from '@/utils/copilotHonorific';

const PANEL_W = 720;
const PANEL_H = 920;
const POS_KEY = 'ai_copilot_panel_pos';
const MAX_STORED_MESSAGES = 80;

const userGender = ref<CopilotUserGender | null>(null);

function userStorageKey(): string {
  return String(
    userInfo.value?.id ??
      userInfo.value?.email ??
      (userInfo.value as { username?: string })?.username ??
      'default'
  );
}

function refreshUserGender() {
  userGender.value = loadUserGender(userStorageKey());
}

const honorific = computed(() => resolveHonorific(userGender.value));

const headerSubtitle = computed(() => {
  const h = honorific.value;
  if (h === '您') return '智能助手 · 语义检索 · 页面联动';
  return `为${h}服务 · 语义检索 · 页面联动`;
});

const emptyGreeting = computed(() => honorificGreeting(userGender.value));

function setUserGender(g: CopilotUserGender) {
  saveUserGender(userStorageKey(), g);
  userGender.value = g;
  message.success(g === 1 ? '好的小哥，小A 记住了～' : '好的公主，小A 记住了～');
}

const thinkingDefault = computed(() =>
  honorific.value === '您' ? '小A 正在思考…' : `小A 正在帮${honorific.value}处理…`
);

const thinkingLabel = ref('');

/** 最近一次执行总结的索引，用于列表加载后回填条数 */
const pendingSummaryIdx = ref<number | null>(null);
const pendingSummaryContext = ref<'order' | 'influencer' | null>(null);
const pendingOrderPage = ref<OrderPagePath | null>(null);
const pendingOrderNo = ref<string | undefined>();
const pendingOpenDetail = ref(false);
const pendingBulkSelect = ref<{ mode: 'select' | 'mail' | 'tag'; maxCount?: number } | null>(null);
/** 待用户确认的意图（理解 + 错字纠正后） */
const pendingIntentConfirm = ref<{ plan: CopilotIntentPlan; userText: string } | null>(null);
let lastSummaryPayload: CopilotSummaryPayload | null = null;
const lastFilterSteps = ref<CopilotSummaryStep[]>(loadCopilotContext().lastFilterSteps ?? []);
const sessionCtxSnapshot = ref(loadCopilotContext());

function refreshSessionContext() {
  sessionCtxSnapshot.value = loadCopilotContext();
}

const sessionContextChips = computed(() =>
  buildSessionContextChips({
    ...sessionCtxSnapshot.value,
    lastFilterSteps: lastFilterSteps.value,
  })
);

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const { token: storeToken, userInfo } = storeToRefs(userStore);

/** 已登录且不在登录/注册页时才显示小A */
const showCopilot = computed(() => {
  if (route.path.startsWith('/user')) return false;
  return !!(storeToken.value || readAuthToken());
});

function messagesStorageKey(): string {
  const u =
    userInfo.value?.email ||
    (userInfo.value as { username?: string })?.username ||
    userInfo.value?.id ||
    'default';
  return `ai_copilot_messages_${String(u)}`;
}

function loadStoredMessages(): ChatMsg[] {
  try {
    const raw = sessionStorage.getItem(messagesStorageKey());
    if (!raw) return [];
    const parsed = JSON.parse(raw) as ChatMsg[];
    if (!Array.isArray(parsed)) return [];
    return parsed
      .filter((m) => (m.role === 'user' || m.role === 'assistant') && typeof m.content === 'string')
      .filter((m) => {
        if (m.role !== 'assistant') return true;
        if (isToolCallArtifact(m.content)) return false;
        return !!(m.content.trim() || m.summary?.result);
      })
      .map((m) => {
        if (m.summary?.result) {
          return {
            ...m,
            kind: m.summary.actions?.length ? ('summary' as const) : ('text' as const),
          };
        }
        return { ...m, kind: 'text' as const };
      })
      .slice(-MAX_STORED_MESSAGES);
  } catch {
    return [];
  }
}

function persistMessages() {
  const toSave = messages.value
    .filter((m) => m.content.trim() && !(m.role === 'assistant' && isToolCallArtifact(m.content)))
    .slice(-MAX_STORED_MESSAGES);
  sessionStorage.setItem(messagesStorageKey(), JSON.stringify(toSave));
}

function clearChatHistory(silent = false) {
  messages.value = [];
  sessionStorage.removeItem(messagesStorageKey());
  if (!silent) {
    message.info('已清空小A 对话记录');
  }
}

const open = ref(false);
const input = ref('');
const loading = ref(false);
const messages = ref<ChatMsg[]>(loadStoredMessages());
const scrollRef = ref<HTMLElement | null>(null);
const scrollAnchorRef = ref<HTMLElement | null>(null);
const panelRef = ref<HTMLElement | null>(null);
let scrollRafId = 0;

const panelPos = ref({ x: 0, y: 0 });
const isDragging = ref(false);

let abortController: AbortController | null = null;
let dragStart = { x: 0, y: 0, posX: 0, posY: 0 };

const panelStyle = computed(() => ({
  left: `${panelPos.value.x}px`,
  top: `${panelPos.value.y}px`,
  width: `${PANEL_W}px`,
  height: `${PANEL_H}px`,
}));

function defaultPanelPos() {
  return {
    x: Math.max(16, window.innerWidth - PANEL_W - 24),
    y: Math.max(16, window.innerHeight - PANEL_H - 88),
  };
}

function loadPos(key: string, fallback: () => { x: number; y: number }) {
  try {
    const raw = sessionStorage.getItem(key);
    if (raw) {
      const p = JSON.parse(raw) as { x?: number; y?: number };
      if (typeof p.x === 'number' && typeof p.y === 'number') {
        return p as { x: number; y: number };
      }
    }
  } catch {
    /* ignore */
  }
  return fallback();
}

function savePos(key: string, pos: { x: number; y: number }) {
  sessionStorage.setItem(key, JSON.stringify(pos));
}

function clampPanel(pos: { x: number; y: number }) {
  const maxX = window.innerWidth - PANEL_W - 8;
  const maxY = window.innerHeight - PANEL_H - 8;
  return {
    x: Math.min(Math.max(8, pos.x), maxX),
    y: Math.min(Math.max(8, pos.y), maxY),
  };
}

function isErrorMsg(text: string) {
  return /失败|过期|401|登录|凭证|异常|错误|无法验证/.test(text);
}

function isThinkingBubble(msg: ChatMsg, idx: number) {
  return (
    msg.role === 'assistant' &&
    loading.value &&
    idx === messages.value.length - 1 &&
    (!msg.content.trim() || isToolCallArtifact(msg.content))
  );
}

function startThinkingAnimation() {
  thinkingLabel.value = thinkingDefault.value;
}

function stopThinkingAnimation() {
  thinkingLabel.value = thinkingDefault.value;
}

function setThinkingStatus(hint: string) {
  thinkingLabel.value = hint.startsWith('小A') ? hint : `小A ${hint}`;
  scheduleScrollToBottom();
}

async function speakLookupResult(
  assistantIdx: number,
  payload: CopilotLookupReplyPayload
) {
  setThinkingStatus('正在组织回复…');
  const fallback = buildLookupReplyFallback(payload, honorific.value);
  try {
    messages.value[assistantIdx]!.content = await generateCopilotLookupReply(payload, {
      userGender: userGender.value ?? undefined,
    });
    if (!messages.value[assistantIdx]!.content.trim()) {
      messages.value[assistantIdx]!.content = fallback;
    }
  } catch {
    messages.value[assistantIdx]!.content = fallback;
  }
}

function onDragStart(e: MouseEvent) {
  if (e.button !== 0) return;
  isDragging.value = true;
  dragStart = { x: e.clientX, y: e.clientY, posX: panelPos.value.x, posY: panelPos.value.y };
  document.addEventListener('mousemove', onDragMove);
  document.addEventListener('mouseup', onDragEnd);
  e.preventDefault();
}

function onDragMove(e: MouseEvent) {
  if (!isDragging.value) return;
  const dx = e.clientX - dragStart.x;
  const dy = e.clientY - dragStart.y;
  panelPos.value = clampPanel({
    x: dragStart.posX + dx,
    y: dragStart.posY + dy,
  });
}

function onDragEnd() {
  if (!isDragging.value) return;
  isDragging.value = false;
  savePos(POS_KEY, panelPos.value);
  document.removeEventListener('mousemove', onDragMove);
  document.removeEventListener('mouseup', onDragEnd);
}

function onWindowResize() {
  panelPos.value = clampPanel(panelPos.value);
}

function toggleOpen() {
  open.value = !open.value;
}

function closePanel() {
  open.value = false;
  abortController?.abort();
}

function scheduleScrollToBottom() {
  if (scrollRafId) cancelAnimationFrame(scrollRafId);
  scrollRafId = requestAnimationFrame(() => {
    scrollRafId = 0;
    const el = scrollRef.value;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
    scrollAnchorRef.value?.scrollIntoView({ block: 'end', behavior: 'auto' });
  });
}

async function scrollToBottom() {
  await nextTick();
  scheduleScrollToBottom();
  await nextTick();
}

async function send() {
  const text = normalizeCopilotUserText(input.value.trim());
  if (!text || loading.value) return;

  if (pendingIntentConfirm.value) {
    if (isIntentConfirmReply(text)) {
      const pending = pendingIntentConfirm.value;
      pendingIntentConfirm.value = null;
      input.value = '';
      await runExecutePipeline(pending.plan.correctedText, { userAlreadyShown: true });
      return;
    }
    if (isIntentCancelReply(text)) {
      pendingIntentConfirm.value = null;
      messages.value.push({ role: 'user', content: text });
      input.value = '';
      messages.value.push({
        role: 'assistant',
        content:
          honorific.value === '您'
            ? '好的，已取消。您可以重新描述需求～'
            : `${honorific.value}，好的，已取消。您可以重新描述需求～`,
      });
      persistMessages();
      await scrollToBottom();
      return;
    }
    pendingIntentConfirm.value = null;
  }

  if (isMetaRecapQuestion(text)) {
    messages.value.push({ role: 'user', content: text });
    input.value = '';
    const lead = honorific.value === '您' ? '我' : `${honorific.value}，我`;
    const recap = lastFilterSteps.value || [];
    let reply = '';
    if (recap.length) {
      const bullets = recap.map((st) => `• ${st.label}：${st.value}`).join('\n');
      reply = `${lead}上一轮按这些条件帮您查了：\n${bullets}`;
      const ctx = loadCopilotContext();
      const mh = ctx?.lastHandle ? `\n关联红人 handle：@${ctx.lastHandle}` : '';
      const mc = ctx?.lastMatchCount != null ? `\n命中：${ctx.lastMatchCount} 人` : '';
      reply += mh + mc + '\n\n需要在此基础上继续筛、勾选或换条件，直接说就好～';
    } else {
      reply = `${lead}本会话还没有执行过明确的筛选动作哦，说一下您想找哪类红人/订单，我马上去查。`;
    }
    messages.value.push({ role: 'assistant', content: reply });
    persistMessages();
    await scrollToBottom();
    return;
  }

  const plan = buildCopilotIntentPlan(text);
  if (plan.needsConfirm) {
    messages.value.push({ role: 'user', content: text });
    input.value = '';
    messages.value.push({
      role: 'assistant',
      content: '',
      summary: {
        result: intentConfirmHead(plan, honorific.value),
        lines: intentConfirmLines(plan),
        hint:
          plan.ambiguityReasons.length > 0
            ? '以上理解若有误，请点「不对，我再说」或直接补充说明。'
            : '请确认后我再执行。',
        actions: intentConfirmActions(),
        status: 'pending',
        steps: plan.summarySteps,
      },
    });
    pendingIntentConfirm.value = { plan, userText: text };
    persistMessages();
    await scrollToBottom();
    return;
  }

  messages.value.push({ role: 'user', content: text });
  input.value = '';
  await runExecutePipeline(plan.correctedText, { userAlreadyShown: true });
}

/** 用户确认意图后执行（或无需确认的直接执行） */
async function runExecutePipeline(text: string, opts?: { userAlreadyShown?: boolean }) {
  if (!opts?.userAlreadyShown) {
    messages.value.push({ role: 'user', content: text });
    input.value = '';
  }

  loading.value = true;
  startThinkingAnimation();
  await scrollToBottom();

  abortController?.abort();
  abortController = new AbortController();

  const assistantIdx = messages.value.length;
  messages.value.push({ role: 'assistant', content: '' });
  await scrollToBottom();

  executedUiActionKeys.clear();
  let preAppliedFilter = false;
  let preAppliedOrder = false;

  const navTarget = inferNavigationTarget(text);
  if (navTarget) {
    emitCloseAllModals();
    Modal.destroyAll();
    await navigateToPage(navTarget.path);
    messages.value[assistantIdx]!.content =
      `${honorific.value === '您' ? '已' : honorific.value + '，已'}为您打开【${navTarget.label}】页面。`;
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  if (
    !userWantsCloseDetail(text) &&
    !userWantsOpenDetail(text) &&
    !isOrderRelatedMessage(text)
  ) {
    const multiHandles = parseMultipleHandlesFromText(text);
    if (multiHandles.length > 1) {
      setThinkingStatus(`正在全库查找 ${multiHandles.length} 位红人…`);
      const items = await lookupMultipleInfluencersByHandle(multiHandles);
      const plan = resolveMultiHandleLookup(text, items);
      if (plan.destination) {
        await router.push({ path: plan.destination.path, query: plan.destination.query });
        const foundHandles = plan.foundItems
          .map((i) => i.result.primary?.handle)
          .filter((h): h is string => !!h);
        const foundIds = plan.foundItems
          .map((i) => i.result.primary?.id)
          .filter((id): id is number => id != null && id > 0);
        rememberCopilotMatch({
          lastHandle: foundHandles.join(','),
          lastHandles: foundHandles,
          lastInfluencerIds: foundIds,
          lastInfluencerId: foundIds[0],
          lastMatchCount: plan.foundItems.length,
        });
        refreshSessionContext();
      }
      await speakLookupResult(assistantIdx, plan.payload);
      loading.value = false;
      stopThinkingAnimation();
      persistMessages();
      await scrollToBottom();
      return;
    }

    const rawHandle = parseHandleFromText(text);
    if (rawHandle) {
      setThinkingStatus(`正在全库查找 @${rawHandle}…`);
      const lookup = await lookupInfluencerByHandle(rawHandle);
      if (lookup.primary) {
        const dest = buildDestinationForHit(lookup.primary);
        await router.push({ path: dest.path, query: dest.query });
        rememberCopilotMatch({
          lastHandle: lookup.primary.handle,
          lastInfluencerId: lookup.primary.id,
          lastMatchCount: 1,
        });
        refreshSessionContext();
        const payload = buildLookupReplyPayload(
          text,
          [{ handle: rawHandle, result: lookup }],
          dest
        );
        await speakLookupResult(assistantIdx, payload);
        loading.value = false;
        stopThinkingAnimation();
        persistMessages();
        await scrollToBottom();
        return;
      }
      if (lookup.total === 0) {
        rememberCopilotMatch({
          lastHandle: rawHandle,
          lastInfluencerId: undefined,
          lastMatchCount: 0,
        });
        await speakLookupResult(
          assistantIdx,
          buildLookupReplyPayload(text, [{ handle: rawHandle, result: lookup }])
        );
        loading.value = false;
        stopThinkingAnimation();
        persistMessages();
        await scrollToBottom();
        return;
      }
    }
  }

  if (isMailInviteRequest(text)) {
    const targets = resolveMailInviteTargets(text);
    let ids = [...targets.ids];
    if (!ids.length && targets.handles.length) {
      setThinkingStatus('正在确认红人信息…');
      const items = await lookupMultipleInfluencersByHandle(targets.handles);
      ids = items
        .map((i) => i.result.primary?.id)
        .filter((id): id is number => id != null && id > 0);
    }
    if (!ids.length) {
      const lead = honorific.value === '您' ? '我' : `${honorific.value}，我`;
      messages.value[assistantIdx]!.content = targets.missingContext
        ? `${lead}还没记住您要给哪位红人发邮件。您可以先说「查 @xxx 红人」，或直接带上 handle。`
        : `${lead}没找到对应的红人记录，请确认 handle 是否正确。`;
      loading.value = false;
      stopThinkingAnimation();
      persistMessages();
      await scrollToBottom();
      return;
    }
    await router.push({
      path: '/mail/campaigns/create',
      query: { ids: ids.join(','), ai: '1', from: 'invite' },
    });
    rememberCopilotMatch({
      lastInfluencerIds: ids,
      lastMatchCount: ids.length,
      lastAction: 'mail',
    });
    refreshSessionContext();
    const n = ids.length;
    const lead = honorific.value === '您' ? '好的' : `${honorific.value}，好的`;
    messages.value[assistantIdx]!.content =
      `${lead}，已为您打开邀约邮件页面，${n} 位红人已选好。您可以在页面上确认模版并发送～`;
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  if (isOrderRelatedMessage(text)) {
    const orderData = parseOrderFiltersFromText(text) ?? { page: resolveOrderPage(text) };
    const ref = resolveInfluencerRefForCopilot(text, messages.value);
    if (ref) orderData.influencer = ref;
    const hasTarget = !!(
      orderData.influencer ||
      orderData.orderNo ||
      orderData.shopifyOrderId ||
      orderData.discountCode
    );
    if (hasTarget || /样品|订单/i.test(text)) {
      preAppliedOrder = true;
      pendingSummaryContext.value = 'order';
      pendingSummaryIdx.value = assistantIdx;
      pendingOrderPage.value = orderData.page;
      await router.push({
        path: orderData.page,
        query: orderFiltersToQuery(orderData, { reset: true }),
      });
      emitAiUiAction({ name: 'ApplyOrderFilter', data: orderData as Record<string, unknown> });
      messages.value[assistantIdx]!.content = orderPendingCopy(
        honorific.value,
        orderPageLabel(orderData.page),
        orderData.orderNo
      );
      loading.value = false;
      stopThinkingAnimation();
      persistMessages();
      await scrollToBottom();
      return;
    }
  }

  const resetInfluencerFilters = isNewBroadInfluencerQuery(text);
  if (resetInfluencerFilters) {
    clearCopilotSessionContext();
    lastFilterSteps.value = [];
    refreshSessionContext();
  }

  const selectIntent = parseSelectIntent(text);

  if (userWantsExport(text)) {
    const target = inferExportTarget(text);
    const scope = resolveExportScope(text);
    const templateName = parseExportTemplateName(text);
    await openExportForTarget(target, scope, templateName);
    pendingSummaryIdx.value = null;
    pendingSummaryContext.value = null;
    const scopeHint =
      scope === 'selected'
        ? loadCopilotContext().lastMatchCount
          ? `已勾选的 ${loadCopilotContext().lastMatchCount} 位红人`
          : '已勾选的红人'
        : scope === 'all'
          ? '全部红人'
          : '当前筛选结果';
    rememberCopilotMatch({ lastAction: 'export', lastFilterSteps: lastFilterSteps.value });
    refreshSessionContext();
    setAssistantPlainReply(assistantIdx, {
      steps: [],
      status: 'success',
      result: `${honorific.value === '您' ? '我' : honorific.value + '，我'}已在【${exportTargetLabel(target)}】打开导出窗口，将导出${scopeHint}。`,
      hint: '请确认字段与范围后点击「导出数据」。',
    });
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  if (isInfluencerRelatedMessage(text) && !userWantsRunAi(text) && !isMailInviteRequest(text)) {
    const quickFilter = inferFilterFromUserMessage(text);
    const extras = parseListFiltersFromText(text);
    const fan = parseFanRangeFromText(text);
    const base =
      quickFilter?.name === 'ApplyInfluencerFilter'
        ? quickFilter.data
        : { statusTab: 'all' };
    const filterData = mergeFilterData({ ...base, ...fan }, extras);
    const wantCount =
      selectIntent.count ??
      (filterData.listLimit != null ? Number(filterData.listLimit) : undefined);
    if (wantCount != null && filterData.listLimit == null) {
      filterData.listLimit = wantCount;
    }
    lastFilterSteps.value = describeFilterData(filterData);
    preAppliedFilter = true;
    pendingSummaryContext.value = 'influencer';
    pendingSummaryIdx.value = assistantIdx;
    await goInfluencerListNow(filterData, resetInfluencerFilters);
    executedUiActionKeys.add(
      uiActionKey({ name: 'ApplyInfluencerFilter', data: filterData })
    );
    emitAiUiAction({
      name: 'ApplyInfluencerFilter',
      data: { ...filterData, resetFilters: resetInfluencerFilters },
    });
    messages.value[assistantIdx]!.content =
      `${honorific.value === '您' ? '我' : honorific.value + '，我'}正在打开红人列表并应用筛选，稍等片刻。`;
  }

  if (userWantsCloseDetail(text)) {
    emitAiUiAction({ name: 'CloseInfluencerDetail', data: {} });
    messages.value[assistantIdx]!.content = '已为您关闭红人详情页。';
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  const target = resolveCopilotTarget(text, messages.value);
  if (userWantsOpenDetail(text) && !target.needsClarification && (target.handle || target.influencerId)) {
    pendingOpenDetail.value = !target.influencerId;
    await applyAssistantUiActions(
      '',
      assistantIdx,
      [
        {
          name: 'OpenInfluencerDetail',
          data: {
            forceOpen: true,
            socialHandle: target.handle,
            influencerId: target.influencerId,
          },
        },
      ],
      null,
      text
    );
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  const offTopicReply = getOffTopicReply(text, undefined, honorific.value);
  if (offTopicReply) {
    messages.value[assistantIdx]!.content = offTopicReply;
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    await scrollToBottom();
    return;
  }

  let rawStream = '';
  const sseUiActions: AiUiAction[] = [];
  try {
    const { text: full, uiActions } = await streamCopilotChat(
      text,
      (token) => {
        const cur = messages.value[assistantIdx]!.content + token;
        if (isToolCallArtifact(cur)) {
          messages.value[assistantIdx]!.content = '';
        } else {
          messages.value[assistantIdx]!.content = cur;
        }
        scheduleScrollToBottom();
      },
      abortController.signal,
      setThinkingStatus,
      (raw) => {
        rawStream += raw;
      },
      (action) => {
        sseUiActions.push(action);
        void flushUiActionImmediate(action, text);
      },
      { userGender: userGender.value ?? undefined }
    );
    const merged = [...(uiActions ?? []), ...sseUiActions];
    if (!preAppliedOrder && !isOrderRelatedMessage(text)) {
      if (target.handle && !merged.some((a) => a.data?.socialHandle === target.handle)) {
        merged.push(buildHandleLookupAction(target.handle));
      } else if (
        target.searchName &&
        !merged.some((a) => a.data?.searchKey === target.searchName)
      ) {
        merged.push(buildNameSearchAction(target.searchName));
      }
    }
    const filteredMerged = preAppliedFilter
      ? merged.filter((a) => a.name !== 'ApplyInfluencerFilter')
      : merged;
    const fallback =
      preAppliedFilter || filteredMerged.length > 0 ? null : inferFilterFromUserMessage(text);
    await applyAssistantUiActions(rawStream || full, assistantIdx, filteredMerged, fallback, text);
  } catch (e: unknown) {
    const errMsg = e instanceof Error ? e.message : '对话失败';
    messages.value[assistantIdx]!.content = errMsg;
  } finally {
    loading.value = false;
    stopThinkingAnimation();
    persistMessages();
    const reply = messages.value[assistantIdx]?.content?.trim() ?? '';
    if (!reply || isToolCallArtifact(reply)) {
      const merged = [...sseUiActions];
      if (merged.length === 0 && !preAppliedFilter) {
        const ctxTarget = resolveCopilotTarget(text, messages.value);
        if (ctxTarget.handle) merged.push(buildHandleLookupAction(ctxTarget.handle));
        else if (ctxTarget.searchName) merged.push(buildNameSearchAction(ctxTarget.searchName));
        const fallback = merged.length === 0 ? inferFilterFromUserMessage(text) : null;
        void applyAssistantUiActions(rawStream, assistantIdx, merged, fallback, text);
      }
    }
    refreshSessionContext();
    await scrollToBottom();
  }
}

const PAGE_NAME_MAP = PAGE_NAME;

const executedUiActionKeys = new Set<string>();

async function navigateToPage(path: string, extraQuery?: Record<string, unknown>) {
  const query: Record<string, string> = { fromAi: '1' };
  for (const key of ['influencerId', 'ids', 'from', 'ai', 'handle', 'socialHandle'] as const) {
    if (extraQuery?.[key] != null) {
      query[key] = String(extraQuery[key]);
    }
  }
  if (route.path !== path) {
    await router.push({ path, query });
  } else {
    await router.replace({ path, query });
  }
  await nextTick();
}

function uiActionKey(action: AiUiAction): string {
  return `${action.name}:${JSON.stringify(action.data ?? {})}`;
}

async function openExportForTarget(
  target: CopilotExportTarget,
  scope: string,
  templateName?: string
) {
  const path = exportTargetPath(target);
  if (route.path !== path) {
    await router.push({ path, query: { fromAi: '1' } });
    await nextTick();
    await nextTick();
  }
  emitAiUiAction({
    name: 'OpenExportModal',
    data: { target, scope, templateName },
  });
}

/** 立刻打开红人列表（URL 带筛选，列表页挂载后即可应用，不依赖 emit 时机） */
async function goInfluencerListNow(filterData: Record<string, unknown>, reset = false) {
  const query = influencerFilterToRouteQuery(filterData, { reset });
  if (route.path !== '/influencer/list') {
    await router.push({ path: '/influencer/list', query });
  } else {
    await router.replace({ path: '/influencer/list', query });
  }
  await nextTick();
  await nextTick();
}

/** SSE 或兜底：单条 UI 指令立即落地（避免等流式结束） */
async function flushUiActionImmediate(action: AiUiAction, userMessage: string) {
  const key = uiActionKey(action);
  if (executedUiActionKeys.has(key)) return;

  const ctxTarget = resolveCopilotTarget(userMessage, messages.value);
  const listExtras = parseListFiltersFromText(userMessage);

  if (action.name === 'NavigateToPage') {
    executedUiActionKeys.add(key);
    const path = String(action.data.path ?? '/influencer/list');
    await navigateToPage(path, action.data);
    if (path.includes('/mail/campaigns/create') && action.data.ids) {
      const ids = String(action.data.ids)
        .split(/[,，]+/)
        .map((s) => Number(s.trim()))
        .filter((n) => !Number.isNaN(n) && n > 0);
      if (ids.length) {
        rememberCopilotMatch({
          lastInfluencerIds: ids,
          lastMatchCount: ids.length,
          lastAction: 'mail',
        });
        refreshSessionContext();
      }
    }
    emitAiUiAction(action);
    return;
  }

  if (action.name === 'ApplyInfluencerFilter') {
    executedUiActionKeys.add(key);
    const filterData = mergeFilterData(
      {
        ...action.data,
        statusTab: action.data.statusTab ?? 'all',
        socialHandle: action.data.socialHandle ?? ctxTarget.handle,
        searchKey: action.data.searchKey ?? ctxTarget.searchName,
      },
      listExtras
    );
    pendingSummaryContext.value = 'influencer';
    await goInfluencerListNow(filterData);
    emitAiUiAction({ name: 'ApplyInfluencerFilter', data: filterData });
    return;
  }

  if (action.name === 'OpenExportModal') {
    executedUiActionKeys.add(key);
    const target = (action.data.target as CopilotExportTarget) || inferExportTarget(userMessage);
    const scope = String(action.data.scope ?? resolveExportScope(userMessage));
    const templateName = action.data.templateName
      ? String(action.data.templateName)
      : parseExportTemplateName(userMessage);
    await openExportForTarget(target, scope, templateName);
    return;
  }

  if (action.name === 'OpenInfluencerDetail') {
    if (isBatchOpenDetailRequest(userMessage) || Number(action.data.resultCount) > 1) {
      executedUiActionKeys.add(key);
      return;
    }
    executedUiActionKeys.add(key);
    const id = action.data.influencerId != null ? Number(action.data.influencerId) : ctxTarget.influencerId;
    const handle = action.data.socialHandle
      ? String(action.data.socialHandle)
      : ctxTarget.handle || undefined;
    const query = influencerFilterToRouteQuery({
      statusTab: 'all',
      socialHandle: handle,
      influencerId: id,
    });
    if (id != null) query.openId = String(id);
    if (route.path !== '/influencer/list') {
      await router.push({ path: '/influencer/list', query });
    } else {
      await router.replace({ path: '/influencer/list', query });
    }
    await nextTick();
    emitAiUiAction({
      ...action,
      data: { ...action.data, forceOpen: true, socialHandle: handle, influencerId: id },
    });
  }
}

function displayMsgContent(msg: ChatMsg): string {
  if (msg.summary?.result) {
    return msg.summary.hint ? `${msg.summary.result}\n${msg.summary.hint}` : msg.summary.result;
  }
  return msg.content;
}

function setAssistantPlainReply(assistantIdx: number, payload: CopilotSummaryPayload) {
  lastSummaryPayload = payload;
  const parts = [payload.result];
  if (payload.hint?.trim() && payload.hint.trim() !== payload.result.trim()) {
    parts.push(payload.hint.trim());
  }
  messages.value[assistantIdx] = {
    role: 'assistant',
    kind: payload.actions?.length || payload.lines?.length ? 'summary' : 'text',
    content: parts.join('\n'),
    summary: payload,
  };
}

async function runQuickAction(actionId: CopilotQuickAction['id']) {
  if (actionId === 'confirm_intent') {
    if (pendingIntentConfirm.value) {
      const pending = pendingIntentConfirm.value;
      pendingIntentConfirm.value = null;
      await runExecutePipeline(pending.plan.correctedText, { userAlreadyShown: true });
    }
    return;
  }
  if (actionId === 'cancel_intent') {
    pendingIntentConfirm.value = null;
    messages.value.push({
      role: 'assistant',
      content:
        honorific.value === '您'
          ? '好的，已取消。您可以重新描述需求～'
          : `${honorific.value}，好的，已取消。您可以重新描述需求～`,
    });
    persistMessages();
    await scrollToBottom();
    return;
  }
  switch (actionId) {
    case 'export':
      await openExportForTarget('influencer', 'filtered');
      break;
    case 'batch_mail': {
      const ctx = loadCopilotContext();
      const maxCount =
        ctx.lastListLimit ??
        (route.query.limit ? Number(route.query.limit) : undefined);
      emitAiUiAction({ name: 'CopilotBulkAction', data: { mode: 'mail', maxCount } });
      message.info(
        maxCount
          ? `正在勾选当前列表前 ${maxCount} 位红人并进入批量发信…`
          : '正在勾选当前筛选的全部红人并进入批量发信…'
      );
      break;
    }
    case 'batch_tag': {
      const ctx = loadCopilotContext();
      const maxCount =
        ctx.lastListLimit ??
        (route.query.limit ? Number(route.query.limit) : undefined);
      emitAiUiAction({ name: 'CopilotBulkAction', data: { mode: 'tag', maxCount } });
      message.info(
        maxCount
          ? `正在勾选当前列表前 ${maxCount} 位红人，请确认打标内容…`
          : '正在勾选当前筛选的全部红人，请确认打标内容…'
      );
      break;
    }
    case 'open_detail': {
      const ctx = loadCopilotContext();
      if (ctx.lastInfluencerId != null || ctx.lastHandle) {
        emitAiUiAction({
          name: 'OpenInfluencerDetail',
          data: {
            forceOpen: true,
            influencerId: ctx.lastInfluencerId,
            socialHandle: ctx.lastHandle,
          },
        });
      } else {
        message.warning('请先在列表中确认唯一匹配的红人');
      }
      break;
    }
  }
}

/** 解析小A 指令：跳转红人列表并在页面展示筛选结果 */
async function applyAssistantUiActions(
  text: string,
  assistantIdx: number,
  sseActions: AiUiAction[] = [],
  fallback: AiUiAction | null = null,
  userMessage = ''
) {
  const actions = [...sseActions, ...parseAiUiActions(text)];
  if (fallback) actions.push(fallback);
  const ctxTarget = resolveCopilotTarget(userMessage, messages.value);
  const userHandle = ctxTarget.handle ?? parseHandleFromText(userMessage);
  const userSearchName = ctxTarget.searchName;
  if (userHandle) {
    const idx = actions.findIndex((a) => a.name === 'ApplyInfluencerFilter' || a.name === 'OpenInfluencerDetail');
    const target = idx >= 0 ? actions[idx] : undefined;
    if (target) {
      actions[idx] = {
        ...target,
        data: { ...target.data, socialHandle: userHandle },
      };
    }
  } else if (userSearchName) {
    const idx = actions.findIndex((a) => a.name === 'ApplyInfluencerFilter');
    const target = idx >= 0 ? actions[idx] : undefined;
    if (target) {
      actions[idx] = {
        ...target,
        data: { ...target.data, searchKey: userSearchName },
      };
    }
  }
  if (actions.length === 0) {
    const reply = messages.value[assistantIdx]?.content?.trim() ?? '';
    if (reply && !isToolCallArtifact(reply)) return;
  }

  const steps: CopilotSummaryStep[] = [];
  let resultLine = '请在左侧主界面查看执行结果。';
  let status: CopilotSummaryPayload['status'] = 'pending';
  let appliedHandle = false;
  let pageTouched = false;
  let orderFlow = false;
  const listExtras = parseListFiltersFromText(userMessage);

  for (const action of actions) {
    if (executedUiActionKeys.has(uiActionKey(action))) {
      continue;
    }
    const resultCount = action.data.resultCount;
    if (typeof resultCount === 'number' && resultCount === 0) {
      steps.push({ label: '结果', value: '未命中' });
      if (action.name === 'ApplyOrderFilter') {
        const path = (action.data.page as OrderPagePath) || '/order/sample';
        const copy = orderSummaryCopy({
          honorific: honorific.value,
          total: 0,
          applied: false,
          orderNo: action.data.orderNo as string | undefined,
          pageLabel: orderPageLabel(path),
        });
        resultLine = copy.result;
        status = copy.status;
      } else {
        const copy = influencerSummaryCopy({
          honorific: honorific.value,
          total: 0,
          applied: false,
        });
        resultLine = copy.result;
        status = copy.status;
      }
      continue;
    }
    if (action.name === 'CloseInfluencerDetail') {
      emitAiUiAction(action);
      steps.push({ label: '操作', value: '关闭详情页' });
      resultLine = '已关闭红人详情弹窗。';
      status = 'success';
      await nextTick();
    } else if (action.name === 'ApplyOrderFilter') {
      const orderData = action.data as OrderFilterData & { page?: string };
      const path = (orderData.page || '/order/sample') as OrderPagePath;
      const pageLabel = orderPageLabel(path);
      const orderNo = orderData.orderNo ?? orderData.shopifyOrderId;
      orderFlow = true;
      pendingSummaryContext.value = 'order';
      pendingOrderPage.value = path;
      pendingOrderNo.value = orderNo;
      pageTouched = true;
      await router.push({ path: String(path), query: orderFiltersToQuery(orderData) });
      steps.push(...describeOrderFilterData(orderData));
      resultLine = orderPendingCopy(honorific.value, pageLabel, orderNo);
      status = 'pending';
      await nextTick();
    } else if (action.name === 'NavigateToPage') {
      const path = String(action.data.path ?? '/influencer/list');
      await navigateToPage(path, action.data);
      executedUiActionKeys.add(uiActionKey(action));
      steps.push({ label: '页面', value: PAGE_NAME_MAP[path] ?? navPageLabel(path) });
      resultLine = String(action.data.summary ?? `已打开${PAGE_NAME_MAP[path] ?? '目标页面'}。`);
      if (path.includes('/mail/campaigns/create')) {
        resultLine =
          honorific.value === '您'
            ? '已打开邀约邮件编写页，红人已预选。'
            : `${honorific.value}，已打开邀约邮件编写页，红人已预选。`;
      }
      status = 'success';
      pageTouched = true;
      await nextTick();
      emitAiUiAction(action);
    } else if (action.name === 'OpenExportModal') {
      const target = (action.data.target as CopilotExportTarget) || inferExportTarget(userMessage);
      const scope = String(action.data.scope ?? resolveExportScope(userMessage));
      const templateName = action.data.templateName
        ? String(action.data.templateName)
        : parseExportTemplateName(userMessage);
      executedUiActionKeys.add(uiActionKey(action));
      await openExportForTarget(target, scope, templateName);
      const ctx = loadCopilotContext();
      const scopeHint =
        scope === 'selected' && ctx.lastMatchCount
          ? `已勾选的 ${ctx.lastMatchCount} 位红人`
          : scope === 'selected'
            ? '已勾选的红人'
            : scope === 'all'
              ? '全部红人'
              : '当前筛选结果';
      steps.push({ label: '导出', value: exportTargetLabel(target) });
      resultLine = `已在【${exportTargetLabel(target)}】打开导出窗口，将导出${scopeHint}。`;
      status = 'success';
    } else if (action.name === 'OpenInfluencerDetail') {
      const id =
        action.data.influencerId != null
          ? Number(action.data.influencerId)
          : ctxTarget.influencerId;
      const handle = action.data.socialHandle
        ? String(action.data.socialHandle)
        : userHandle || undefined;
      const wantOpen = userWantsOpenDetail(userMessage) || action.data.forceOpen === true;
      if (wantOpen && (isBatchOpenDetailRequest(userMessage) || Number(action.data.resultCount) > 1)) {
        steps.push({ label: '详情', value: '批量打开（不支持）' });
        resultLine = '批量打开红人详情暂不支持，请一次只指定一位红人。';
        status = 'info';
        pageTouched = true;
      } else if (wantOpen) {
        pendingOpenDetail.value = id == null;
        pageTouched = true;
        if (route.path !== '/influencer/list') {
          await router.push({ path: '/influencer/list', query: { fromAi: '1', status: 'all' } });
        }
        if (id != null) {
          await router.replace({
            path: '/influencer/list',
            query: {
              fromAi: '1',
              status: 'all',
              openId: String(id),
              ...(handle ? { handle } : {}),
            },
          });
        }
        steps.push({
          label: '打开详情',
          value: handle ? `@${handle.replace(/^@/, '')}` : id != null ? `ID ${id}` : '当前红人',
        });
        resultLine = '正在打开红人详情页…';
        status = 'pending';
        await nextTick();
        emitAiUiAction({
          ...action,
          data: { ...action.data, forceOpen: true, socialHandle: handle, influencerId: id },
        });
      } else if (handle) {
        appliedHandle = true;
        pageTouched = true;
        const filterData = mergeFilterData(
          { statusTab: 'all', socialHandle: handle, influencerId: id },
          listExtras
        );
        steps.push(...describeFilterData(filterData));
        resultLine = '正在按条件筛选列表…';
        status = 'pending';
        emitAiUiAction({ name: 'ApplyInfluencerFilter', data: filterData });
      }
    } else if (action.name === 'ApplyInfluencerFilter') {
      pendingSummaryContext.value = 'influencer';
      const filterData = mergeFilterData(
        {
          ...action.data,
          statusTab: action.data.statusTab ?? 'all',
          socialHandle: action.data.socialHandle ?? userHandle,
          searchKey: action.data.searchKey ?? userSearchName,
        },
        listExtras
      );
      const handle = filterData.socialHandle ? String(filterData.socialHandle) : undefined;
      const id =
        filterData.influencerId != null ? Number(filterData.influencerId) : undefined;
      appliedHandle = !!handle;
      pageTouched = true;
      executedUiActionKeys.add(uiActionKey({ name: 'ApplyInfluencerFilter', data: filterData }));
      lastFilterSteps.value = describeFilterData(filterData);
      await goInfluencerListNow(filterData);
      emitAiUiAction({ name: 'ApplyInfluencerFilter', data: filterData });
      const fanHint =
        filterData.fansMax != null
          ? `粉丝不超过 ${filterData.fansMax}`
          : filterData.fansMin != null
            ? `粉丝不少于 ${filterData.fansMin}`
            : '';
      resultLine = fanHint
        ? `${honorific.value === '您' ? '我' : honorific.value + '，我'}正在按「${fanHint}」筛选红人列表，稍等我统计人数。`
        : `${honorific.value === '您' ? '我' : honorific.value + '，我'}正在按您的条件筛选红人列表，稍等我统计人数。`;
      status = 'pending';
      rememberFromFilterHit(
        filterData,
        typeof resultCount === 'number' ? resultCount : undefined
      );
      if (filterData.searchKey) {
        rememberCopilotMatch({
          lastSearchName: String(filterData.searchKey),
        });
      }
    } else {
      emitAiUiAction(action);
    }
  }

  const reply = messages.value[assistantIdx]?.content?.trim() ?? '';
  const contradicts =
    appliedHandle &&
    reply &&
    /未检索到|未找到|不存在|没有.*红人/i.test(reply) &&
    !/已找到|共有.*位|找到.*位/i.test(reply);
  if (orderFlow && reply && !isToolCallArtifact(reply)) {
    resultLine = reply.length > 220 ? `${reply.slice(0, 220)}…` : reply;
    if (status === 'pending') status = 'success';
  } else if (orderFlow && !reply.trim()) {
    resultLine = orderPendingCopy(honorific.value, orderPageLabel(pendingOrderPage.value ?? '/order/sample'), pendingOrderNo.value);
  } else if (reply && !isToolCallArtifact(reply) && !contradicts) {
    resultLine = reply.length > 400 ? `${reply.slice(0, 400)}…` : reply;
    if (status === 'pending' && pageTouched) status = 'success';
  }

  const llmNote =
    !orderFlow && reply && !isToolCallArtifact(reply) && !contradicts && resultLine !== reply
      ? reply.length > 100
        ? `${reply.slice(0, 100)}…`
        : reply
      : undefined;

  const hint =
    llmNote ??
    (pageTouched && !orderFlow
      ? status === 'pending'
        ? '列表刷新后将更新匹配数量。'
        : undefined
      : orderFlow
        ? '订单列表加载完成后会更新结果。'
        : undefined);

  pendingSummaryIdx.value = assistantIdx;
  setAssistantPlainReply(assistantIdx, {
    steps: [],
    status,
    result: resultLine,
    hint,
  });
  scheduleScrollToBottom();
}

function patchSummaryWithTotal(payload: AiFilterCompletePayload) {
  if (pendingSummaryContext.value === 'order') return;
  const idx = pendingSummaryIdx.value;
  if (idx == null) return;
  const msg = messages.value[idx];
  if (!msg || msg.role !== 'assistant') return;
  const total = payload.total;
  const applied = payload.applied !== false && total > 0;
  const base = lastSummaryPayload ?? {
    steps: [] as CopilotSummaryStep[],
    status: 'pending' as const,
    result: '',
  };

  let copy = influencerSummaryCopy({
    honorific: honorific.value,
    total,
    applied,
    pendingOpenDetail: pendingOpenDetail.value,
    filterSteps: lastFilterSteps.value,
    matchedTotal: payload.matchedTotal,
    listLimit: payload.listLimit,
    sortBy: payload.sortBy,
  });

  if (applied) {
    const handleRaw = payload.handle;
    if (handleRaw?.includes(',')) {
      rememberFromFilterHit({ socialHandle: handleRaw, influencerId: payload.influencerId }, total);
      rememberCopilotMatch({
        lastMatchedTotal: payload.matchedTotal,
        lastListLimit: payload.listLimit,
        lastFilterSteps: lastFilterSteps.value,
        lastAction: 'filter',
      });
    } else {
      rememberCopilotMatch({
        lastHandle: payload.handle,
        lastSearchName: payload.searchName,
        lastInfluencerId: payload.influencerId,
        lastMatchCount: total,
        lastMatchedTotal: payload.matchedTotal,
        lastListLimit: payload.listLimit,
        lastFilterSteps: lastFilterSteps.value,
        lastAction: 'filter',
      });
    }
    refreshSessionContext();
    if (pendingBulkSelect.value) {
      const bulk = pendingBulkSelect.value;
      pendingBulkSelect.value = null;
      emitAiUiAction({
        name: 'CopilotBulkAction',
        data: { mode: bulk.mode, maxCount: bulk.maxCount },
      });
    }
    if (pendingOpenDetail.value && total === 1 && payload.influencerId != null) {
      emitAiUiAction({
        name: 'OpenInfluencerDetail',
        data: { forceOpen: true, influencerId: payload.influencerId, socialHandle: payload.handle },
      });
      pendingOpenDetail.value = false;
    } else if (pendingOpenDetail.value && total > 1) {
      pendingOpenDetail.value = false;
      copy = influencerSummaryCopy({
        honorific: honorific.value,
        total,
        applied: true,
        pendingOpenDetail: true,
        filterSteps: lastFilterSteps.value,
        matchedTotal: payload.matchedTotal,
        listLimit: payload.listLimit,
        sortBy: payload.sortBy,
      });
    }
  }

  setAssistantPlainReply(idx, {
    steps: [],
    ...copy,
  });
  pendingSummaryIdx.value = null;
  pendingSummaryContext.value = null;
  persistMessages();
  scheduleScrollToBottom();
}

function patchOrderSummaryWithTotal(payload: AiOrderFilterCompletePayload) {
  if (pendingSummaryContext.value !== 'order') return;
  const idx = pendingSummaryIdx.value;
  if (idx == null) return;
  const msg = messages.value[idx];
  if (!msg || msg.role !== 'assistant') return;

  const base = lastSummaryPayload ?? {
    steps: [] as CopilotSummaryStep[],
    status: 'pending' as const,
    result: '',
  };
  const pageLabel = orderPageLabel(payload.page);
  const orderNo = payload.orderNo ?? pendingOrderNo.value;
  const applied = payload.applied !== false && payload.total > 0;
  const copy = orderSummaryCopy({
    honorific: honorific.value,
    total: payload.total,
    applied,
    orderNo,
    pageLabel,
  });

  setAssistantPlainReply(idx, {
    steps: [],
    ...copy,
  });
  pendingSummaryIdx.value = null;
  pendingSummaryContext.value = null;
  pendingOrderPage.value = null;
  pendingOrderNo.value = undefined;
  persistMessages();
  scheduleScrollToBottom();
}

let unsubscribeFilterComplete: (() => void) | undefined;
let unsubscribeOrderFilterComplete: (() => void) | undefined;

onMounted(() => {
  refreshUserGender();
  thinkingLabel.value = thinkingDefault.value;
  panelPos.value = clampPanel(loadPos(POS_KEY, defaultPanelPos));
  messages.value = loadStoredMessages();
  window.addEventListener('resize', onWindowResize);
  unsubscribeFilterComplete = onAiFilterComplete(patchSummaryWithTotal);
  unsubscribeOrderFilterComplete = onAiOrderFilterComplete(patchOrderSummaryWithTotal);
});

onUnmounted(() => {
  unsubscribeFilterComplete?.();
  unsubscribeOrderFilterComplete?.();
  stopThinkingAnimation();
  window.removeEventListener('resize', onWindowResize);
  document.removeEventListener('mousemove', onDragMove);
  document.removeEventListener('mouseup', onDragEnd);
  abortController?.abort();
});

watch(
  messages,
  () => {
    if (!loading.value) {
      persistMessages();
    }
    if (open.value && (loading.value || messages.value.length > 0)) {
      scheduleScrollToBottom();
    }
  },
  { deep: true, flush: 'post' }
);

watch(thinkingLabel, () => {
  if (open.value && loading.value) {
    scheduleScrollToBottom();
  }
});

watch(open, (v) => {
  if (v) {
    const last = messages.value[messages.value.length - 1];
    if (last?.role === 'assistant' && isErrorMsg(last.content)) {
      messages.value = messages.value.slice(0, -1);
      persistMessages();
    }
    panelPos.value = clampPanel(panelPos.value);
    nextTick(() => scrollToBottom());
  }
});

watch(showCopilot, (visible) => {
  if (!visible) {
    open.value = false;
    loading.value = false;
    stopThinkingAnimation();
    abortController?.abort();
  }
});

watch(storeToken, (t) => {
  if (!t) {
    clearChatHistory(true);
  }
});

watch(
  () => userInfo.value?.email ?? userInfo.value?.id,
  () => {
    refreshUserGender();
    messages.value = loadStoredMessages();
  }
);
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

.ai-copilot-panel {
  position: fixed;
  z-index: 1001;
  display: flex;
  flex-direction: column;
  border-radius: 22px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid rgba(99, 102, 241, 0.15);
  box-shadow:
    0 25px 60px rgba(15, 23, 42, 0.15),
    0 8px 24px rgba(99, 102, 241, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.9) inset;
  user-select: none;
  transition: box-shadow 0.3s ease, transform 0.3s ease;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
}
.ai-copilot-panel.dragging {
  box-shadow: 0 30px 70px rgba(15, 23, 42, 0.22), 0 10px 30px rgba(99, 102, 241, 0.1);
  cursor: grabbing;
}

.ai-copilot-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #f8f7ff 0%, #eef2ff 50%, #f0f4ff 100%);
  border-bottom: 1px solid rgba(99, 102, 241, 0.1);
  cursor: grab;
  position: relative;
  overflow: hidden;
}
.ai-copilot-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(99, 102, 241, 0.2), transparent);
}
.ai-copilot-panel.dragging .ai-copilot-header {
  cursor: grabbing;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}
.drag-hint {
  color: #a5b4fc;
  font-size: 14px;
  line-height: 1;
  flex-shrink: 0;
  opacity: 0.6;
}
.avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  position: relative;
}
.avatar::after {
  content: '';
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #22c55e;
  border: 2px solid #f8f7ff;
  animation: pulse-dot 2s ease-in-out infinite;
}
@keyframes pulse-dot {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(0.85); }
}
.avatar-core {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  background: linear-gradient(135deg, #4338ca 0%, #6366f1 50%, #818cf8 100%);
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 14px rgba(79, 70, 229, 0.4);
}
.title-wrap {
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.title {
  font-weight: 800;
  font-size: 17px;
  color: #1e1b4b;
  letter-spacing: -0.01em;
  line-height: 1.3;
}
.subtitle {
  font-size: 11px;
  color: #818cf8;
  margin-top: 2px;
  font-weight: 500;
  letter-spacing: 0.02em;
}
.header-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
.header-btn {
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.7);
  color: #64748b;
  border: 1px solid rgba(99, 102, 241, 0.1);
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  transition: all 0.2s ease;
  backdrop-filter: blur(4px);
}
.header-btn:hover {
  background: rgba(99, 102, 241, 0.08);
  color: #4f46e5;
  border-color: rgba(99, 102, 241, 0.2);
  transform: scale(1.05);
}
.header-btn-text {
  width: auto;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.ai-copilot-messages {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 16px 14px 8px;
  background: #f8fafc;
  user-select: text;
  overscroll-behavior: contain;
}
.scroll-anchor {
  height: 1px;
  flex-shrink: 0;
}
.ai-copilot-messages::-webkit-scrollbar {
  width: 6px;
}
.ai-copilot-messages::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 10px;
  margin-top: 8px;
  padding: 0 2px;
}
.gender-pick {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  background: linear-gradient(135deg, #faf5ff 0%, #eef2ff 100%);
  border: 1px solid rgba(99, 102, 241, 0.15);
}
.gender-pick-label {
  font-size: 12px;
  color: #64748b;
  margin-right: 4px;
}
.gender-btn {
  padding: 6px 12px;
  border-radius: 999px;
  border: 1px solid rgba(79, 70, 229, 0.25);
  background: #fff;
  color: #4338ca;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
}
.gender-btn:hover {
  background: #eef2ff;
  border-color: #6366f1;
}
.gender-btn-f {
  border-color: rgba(236, 72, 153, 0.3);
  color: #be185d;
}
.gender-btn-f:hover {
  background: #fdf2f8;
  border-color: #ec4899;
}
.empty-hero {
  text-align: center;
  padding: 16px 12px;
  margin-bottom: 8px;
  border-radius: 12px;
  background: linear-gradient(180deg, #fff 0%, #fafbff 100%);
  border: 1px solid rgba(99, 102, 241, 0.12);
}
.empty-heading {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 800;
  color: #1e1b4b;
  letter-spacing: -0.01em;
}
.empty-desc {
  margin: 0;
  font-size: 13px;
  line-height: 1.55;
  color: #64748b;
}
.empty-title {
  text-align: left;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: #94a3b8;
  margin: 4px 0 2px 4px;
}
.hint-chip {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  text-align: left;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: #fff;
  color: #334155;
  font-size: 13px;
  line-height: 1.45;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(15, 23, 42, 0.04);
}
.hint-chip:hover {
  border-color: #94a3b8;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
}
.hint-index {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: 6px;
  background: linear-gradient(135deg, #4f46e5, #6366f1);
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}
.hint-text {
  flex: 1;
}

.msg {
  display: flex;
  margin-bottom: 12px;
}
.msg.user {
  justify-content: flex-end;
}
.msg.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: 92%;
  min-width: 48px;
  padding: 14px 18px;
  border-radius: 14px;
  font-size: 15px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.msg.user .bubble {
  background: linear-gradient(135deg, #4338ca 0%, #6366f1 60%, #818cf8 100%);
  color: #f8fafc;
  border-bottom-right-radius: 6px;
  box-shadow: 0 6px 24px rgba(79, 70, 229, 0.3);
  position: relative;
  overflow: hidden;
}
.msg.user .bubble::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
}
.msg.assistant .bubble {
  background: rgba(255, 255, 255, 0.9);
  color: #334155;
  border: 1px solid rgba(99, 102, 241, 0.08);
  border-left: 3px solid rgba(99, 102, 241, 0.4);
  border-bottom-left-radius: 6px;
  backdrop-filter: blur(8px);
}
.bubble.error {
  background: #fef2f2;
  color: #b91c1c;
  border-color: #fecaca;
  border-left: 3px solid #ef4444;
}

.bubble-report {
  max-width: 96%;
  padding: 0;
  overflow: hidden;
  border: 1px solid #e2e8f0;
  background: #fff;
}
.report-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.06em;
  color: #475569;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}
.report-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #64748b;
}
.report-success .report-dot {
  background: #10b981;
}
.report-empty .report-dot {
  background: #94a3b8;
}
.report-pending .report-dot {
  background: #3b82f6;
}
.report-info .report-dot {
  background: #f59e0b;
}
.report-steps {
  padding: 10px 14px 4px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.report-step {
  display: grid;
  grid-template-columns: 72px 1fr;
  gap: 8px;
  font-size: 13px;
  line-height: 1.45;
}
.step-label {
  color: #94a3b8;
  flex-shrink: 0;
}
.step-value {
  color: #1e293b;
  font-weight: 500;
  word-break: break-all;
}
.report-result {
  padding: 8px 14px 10px;
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
}
.report-empty .report-result {
  color: #64748b;
  font-weight: 500;
}
.report-hint {
  padding: 0 14px 12px;
  font-size: 12px;
  line-height: 1.5;
  color: #64748b;
}

.thinking-bubble {
  min-width: 200px;
  max-width: 96%;
  padding: 14px 18px;
  background: linear-gradient(135deg, #fff 0%, #eef2ff 100%);
  border: 1px solid rgba(99, 102, 241, 0.2);
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.1);
}
.thinking-inner {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
}
.thinking-spinner {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  border: 2px solid #c7d2fe;
  border-top-color: #4f46e5;
  animation: thinking-spin 0.85s linear infinite;
}
.thinking-label {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
  color: #4338ca;
  letter-spacing: 0.02em;
  line-height: 1.4;
}
@keyframes thinking-spin {
  to {
    transform: rotate(360deg);
  }
}
@keyframes thinking-bounce {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.45;
  }
  30% {
    transform: translateY(-7px);
    opacity: 1;
  }
}

.ai-copilot-input {
  display: flex;
  gap: 10px;
  padding: 14px 18px 18px;
  border-top: 1px solid rgba(99, 102, 241, 0.06);
  background: linear-gradient(180deg, rgba(255,255,255,0.95) 0%, rgba(248,250,252,0.95) 100%);
  user-select: text;
}
.ai-copilot-input textarea {
  flex: 1;
  resize: none;
  border: 1.5px solid rgba(99, 102, 241, 0.12);
  border-radius: 16px;
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;
  font-family: inherit;
  background: rgba(255, 255, 255, 0.8);
  transition: border-color 0.2s, box-shadow 0.2s;
  outline: none;
}
.ai-copilot-input textarea:focus {
  border-color: #818cf8;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.12);
  background: #fff;
}
.send-btn {
  align-self: flex-end;
  min-width: 68px;
  padding: 12px 18px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #4338ca 0%, #6366f1 100%);
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  font-family: inherit;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(79, 70, 229, 0.35);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.send-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(79, 70, 229, 0.45);
}
.send-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  box-shadow: none;
}
.send-loading {
  display: inline-flex;
  gap: 4px;
  align-items: center;
  height: 14px;
}
.send-loading .dot {
  display: block;
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #fff;
  animation: thinking-bounce 1s ease-in-out infinite;
}
.send-loading .dot:nth-child(2) {
  animation-delay: 0.12s;
}
.send-loading .dot:nth-child(3) {
  animation-delay: 0.24s;
}

.ai-copilot-fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1002;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 22px 0 8px;
  height: 50px;
  border-radius: 999px;
  border: 1px solid rgba(99, 102, 241, 0.2);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  color: #312e81;
  font-weight: 700;
  cursor: pointer;
  box-shadow:
    0 8px 28px rgba(79, 70, 229, 0.18),
    0 2px 8px rgba(15, 23, 42, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  animation: fab-entrance 0.5s ease-out;
}
@keyframes fab-entrance {
  from { opacity: 0; transform: translateY(20px) scale(0.9); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}
.ai-copilot-fab:hover {
  transform: translateY(-3px) scale(1.02);
  border-color: rgba(99, 102, 241, 0.4);
  box-shadow:
    0 14px 36px rgba(79, 70, 229, 0.24),
    0 4px 12px rgba(15, 23, 42, 0.08);
}
.ai-copilot-fab.open {
  background: linear-gradient(135deg, #eef2ff, #f5f3ff);
  color: #6366f1;
  border-color: rgba(99, 102, 241, 0.3);
}
.fab-badge {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4338ca 0%, #6366f1 50%, #818cf8 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(79, 70, 229, 0.4);
  animation: fab-pulse 3s ease-in-out infinite;
}
@keyframes fab-pulse {
  0%, 100% { box-shadow: 0 4px 14px rgba(79, 70, 229, 0.4); }
  50% { box-shadow: 0 4px 20px rgba(79, 70, 229, 0.6), 0 0 0 4px rgba(99, 102, 241, 0.1); }
}
.fab-label {
  font-size: 14px;
  letter-spacing: 0.03em;
  font-weight: 700;
}

.bubble-structured {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.summary-head {
  font-weight: 600;
  color: #1e293b;
  line-height: 1.5;
}
.summary-lines {
  margin: 0;
  padding-left: 18px;
  color: #64748b;
  font-size: 13px;
  line-height: 1.6;
}
.summary-hint {
  margin: 0;
  font-size: 13px;
  color: #94a3b8;
  line-height: 1.5;
}
.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
}
.quick-action-btn {
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid rgba(99, 102, 241, 0.35);
  background: #f5f3ff;
  color: #4f46e5;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.quick-action-btn:hover {
  background: #ede9fe;
  border-color: #6366f1;
}

.session-context-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  padding: 8px 14px;
  background: linear-gradient(90deg, #f8fafc 0%, #eef2ff 100%);
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}
.ctx-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-transform: uppercase;
  color: #6366f1;
  margin-right: 4px;
  flex-shrink: 0;
}
.ctx-chip {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #e2e8f0;
  color: #475569;
  line-height: 1.4;
  white-space: nowrap;
}
</style>
