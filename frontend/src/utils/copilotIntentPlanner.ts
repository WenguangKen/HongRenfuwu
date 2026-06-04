/**
 * 小A 执行前意图理解：错字纠正 + 仅在「意图不明确」时请求确认
 */
import { inferFilterFromUserMessage, isInfluencerRelatedMessage } from '@/utils/aiInfluencerBridge';
import { userWantsExport, inferExportTarget, resolveExportScope, exportTargetLabel } from '@/utils/copilotExport';
import {
  userWantsCloseDetail,
  userWantsOpenDetail,
  parseHandleFromText,
  parseMultipleHandlesFromText,
  userWantsRunAi,
  isMailInviteRequest,
  refersToPreviousInfluencer,
} from '@/utils/copilotHandle';
import { hasMailInviteTarget, loadCopilotContext } from '@/utils/copilotContext';
import {
  describeFilterData,
  mergeFilterData,
  parseListFiltersFromText,
} from '@/utils/copilotListFilters';
import { inferNavigationTarget, pageLabel } from '@/utils/copilotNavigation';
import {
  describeOrderFilterData,
  isOrderRelatedMessage,
  orderPageLabel,
  parseOrderFiltersFromText,
  resolveOrderPage,
  type OrderFilterData,
  type OrderPagePath,
} from '@/utils/copilotOrderFilters';
import { parseFanRangeFromText } from '@/utils/copilotFanRange';
import { isMetaRecapQuestion, parseSelectIntent } from '@/utils/copilotTextNormalize';
import type { CopilotSummaryStep } from '@/utils/copilotSummary';

export type CopilotActionKind =
  | 'meta_recap'
  | 'navigate'
  | 'influencer_filter'
  | 'handle_lookup'
  | 'order'
  | 'export'
  | 'close_detail'
  | 'open_detail'
  | 'mail_invite'
  | 'ai_chat';

/** 常见业务错字 / 同音字纠正（按序替换） */
const TYPO_REPLACEMENTS: Array<[RegExp, string, string]> = [
  [/请打/g, '请查', '请打→请查'],
  [/带上传/g, '待上传', '带上传→待上传'],
  [/代上传/g, '待上传', '代上传→待上传'],
  [/带联系/g, '待联系', '带联系→待联系'],
  [/代联系/g, '待联系', '代联系→待联系'],
  [/带审/g, '待审', '带审→待审'],
  [/红仁/g, '红人', '红仁→红人'],
  [/达仁/g, '达人', '达仁→达人'],
  [/资源地/g, '资源池', '资源地→资源池'],
  [/建廉/g, '建联', '建廉→建联'],
  [/邮见/g, '邮件', '邮见→邮件'],
  [/折抠/g, '折扣', '折抠→折扣'],
  [/优蕙码/g, '优惠码', '优蕙码→优惠码'],
  [/样平/g, '样品', '样平→样品'],
  [/转话订单/g, '转化订单', '转话订单→转化订单'],
  [/分拥/g, '分佣', '分拥→分佣'],
  [/打窾/g, '打款', '打窾→打款'],
  [/模饭/g, '模版', '模饭→模版'],
  [/模析/g, '模版', '模析→模版'],
  [/内荣库/g, '内容库', '内荣库→内容库'],
  [/已上穿/g, '已上传', '已上穿→已上传'],
  [/沟通过/g, '沟通中', '沟通过→沟通中'],
  [/合作种/g, '合作中', '合作种→合作中'],
];

export type CopilotIntentPlan = {
  originalText: string;
  correctedText: string;
  typoNotes: string[];
  actionKind: CopilotActionKind;
  description: string;
  summarySteps: CopilotSummaryStep[];
  /** 仅在意图不够明确时为 true */
  needsConfirm: boolean;
  /** 为何需要确认（给用户看） */
  ambiguityReasons: string[];
};

export function correctBusinessTypos(text: string): { text: string; notes: string[] } {
  let out = text;
  const notes: string[] = [];
  for (const [re, rep, label] of TYPO_REPLACEMENTS) {
    if (re.test(out)) {
      out = out.replace(re, rep);
      notes.push(label);
    }
  }
  return { text: out, notes };
}

export function isIntentConfirmReply(text: string): boolean {
  return /^(确认|执行|好的|可以|对的|对|是的|没问题|就这样|go|ok|yes|开始吧)/i.test(text.trim());
}

export function isIntentCancelReply(text: string): boolean {
  return /^(取消|不要|算了|不对|不是|错了|重来|我再想想|先不用)/i.test(text.trim());
}

function finish(
  base: Pick<CopilotIntentPlan, 'originalText' | 'correctedText' | 'typoNotes'>,
  actionKind: CopilotActionKind,
  description: string,
  summarySteps: CopilotSummaryStep[],
  ambiguityReasons: string[]
): CopilotIntentPlan {
  return {
    ...base,
    actionKind,
    description,
    summarySteps,
    ambiguityReasons,
    needsConfirm: ambiguityReasons.length > 0,
  };
}

function hasSpecificFilterCriteria(filterData: Record<string, unknown>): boolean {
  if (filterData.statusTab && filterData.statusTab !== 'all') return true;
  if (filterData.socialHandle || filterData.searchKey) return true;
  if (filterData.fansMin != null || filterData.fansMax != null) return true;
  if (filterData.country || filterData.socialPlatform || filterData.brand || filterData.influencerType) {
    return true;
  }
  if (filterData.listLimit != null) return true;
  if (filterData.minSampleCount != null || filterData.maxSampleCount != null) return true;
  if (filterData.isPaid === true || filterData.isPaid === false) return true;
  if (filterData.hasOrders === true || filterData.hasOrders === false) return true;
  return false;
}

function isExplicitHandleLookup(text: string, handle: string): boolean {
  if (text.includes('@')) return true;
  if (/查|找|有没有|是否存在|lookup|handle|账号/i.test(text)) return true;
  return handle.length >= 4 && new RegExp(handle, 'i').test(text);
}

function isVagueOrderQuery(text: string, orderData: Record<string, unknown>): boolean {
  const hasId = !!(orderData.orderNo || orderData.shopifyOrderId || orderData.discountCode);
  const hasInfluencer = !!orderData.influencer;
  if (hasId || hasInfluencer) return false;
  // 「查订单」但未给单号/红人
  return /订单|单号|样品|转化/i.test(text);
}

function isAmbiguousStatusOrMail(text: string): string[] {
  const reasons: string[] = [];
  if (!/改状态|状态流转|移到|设为|改成|建联|发.*邮件|邀约/.test(text)) return reasons;

  const ctx = loadCopilotContext();
  const hasTarget =
    parseHandleFromText(text) != null ||
    /这个红人|该红人|这位红人|这两个红人|这两位红人|这些红人|上面.*红人|刚才.*红人/i.test(text) ||
    refersToPreviousInfluencer(text) ||
    !!ctx.lastInfluencerIds?.length ||
    !!ctx.lastHandles?.length ||
    !!ctx.lastHandle ||
    /\b\d{4,}\b/.test(text);
  const hasStatusOrAction =
    /待联系|已联系|沟通|合作|休眠|暂不合作|黑名单|不再合作|建联|邮件|模版|模板/.test(text);

  if (!hasTarget) reasons.push('未指明是哪位红人（请补充 handle、姓名或 ID）');
  if (/改状态|移到|设为|改成/.test(text) && !hasStatusOrAction) {
    reasons.push('未说明要改到什么状态');
  }
  if (
    /建联|发.*邮件|邀约/.test(text) &&
    !hasTarget &&
    !/模版|模板|商品|红人\s*\d|@\w/.test(text)
  ) {
    reasons.push('建联/发邮件信息不完整（红人、模版或商品等）');
  }
  return reasons;
}

export function buildCopilotIntentPlan(raw: string): CopilotIntentPlan {
  const originalText = raw.trim();
  const { text: correctedText, notes: typoNotes } = correctBusinessTypos(originalText);
  const base = { originalText, correctedText, typoNotes };

  if (isMetaRecapQuestion(correctedText)) {
    return finish(base, 'meta_recap', '复述上一轮操作', [{ label: '操作', value: '查看上一轮记录' }], []);
  }

  const nav = inferNavigationTarget(correctedText);
  if (nav) {
    // 导航意图清晰（含错字纠正后）→ 直接执行
    return finish(
      base,
      'navigate',
      `打开【${nav.label}】`,
      [{ label: '目标页面', value: nav.label }],
      []
    );
  }

  if (userWantsCloseDetail(correctedText)) {
    return finish(base, 'close_detail', '关闭红人详情', [{ label: '操作', value: '关闭详情弹窗' }], []);
  }

  if (userWantsExport(correctedText)) {
    const target = inferExportTarget(correctedText);
    const scope = resolveExportScope(correctedText);
    return finish(
      base,
      'export',
      `导出【${exportTargetLabel(target)}】`,
      [
        { label: '对象', value: exportTargetLabel(target) },
        { label: '范围', value: scope === 'all' ? '全部' : scope === 'selected' ? '已勾选' : '当前筛选' },
      ],
      []
    );
  }

  if (isOrderRelatedMessage(correctedText)) {
    const orderData = (parseOrderFiltersFromText(correctedText) ?? { page: resolveOrderPage(correctedText) }) as OrderFilterData;
    const steps = describeOrderFilterData(orderData);
    const reasons: string[] = [];
    if (isVagueOrderQuery(correctedText, orderData)) {
      reasons.push('未提供订单号、Shopify 单号或关联红人，不确定查哪一笔');
    }
    if (/样品|转化/.test(correctedText) && !orderData.page) {
      reasons.push('未明确是红人订单还是转化订单');
    }
    return finish(
      base,
      'order',
      `在【${orderPageLabel((orderData.page ?? '/order/sample') as OrderPagePath)}】查订单`,
      steps.length ? steps : [{ label: '页面', value: orderPageLabel((orderData.page ?? '/order/sample') as OrderPagePath) }],
      reasons
    );
  }

  const multiHandles = parseMultipleHandlesFromText(correctedText);
  if (
    multiHandles.length > 1 &&
    !userWantsOpenDetail(correctedText) &&
    !isOrderRelatedMessage(correctedText)
  ) {
    return finish(
      base,
      'handle_lookup',
      `查找 ${multiHandles.length} 位红人`,
      multiHandles.map((h) => ({ label: '社媒账号', value: `@${h}` })),
      []
    );
  }

  const handle = parseHandleFromText(correctedText);
  if (
    handle &&
    !userWantsOpenDetail(correctedText) &&
    !isOrderRelatedMessage(correctedText)
  ) {
    const reasons: string[] = [];
    if (!isExplicitHandleLookup(correctedText, handle)) {
      reasons.push(`是否在查找社媒账号 @${handle}？请确认`);
    }
    return finish(
      base,
      'handle_lookup',
      `查找红人 @${handle}`,
      [{ label: '社媒账号', value: `@${handle}` }],
      reasons
    );
  }

  if (userWantsOpenDetail(correctedText)) {
    const handle2 = parseHandleFromText(correctedText);
    const reasons: string[] = [];
    if (!handle2) reasons.push('未指定要打开哪位红人（handle 或姓名）');
    return finish(
      base,
      'open_detail',
      handle2 ? `打开 @${handle2} 详情` : '打开红人详情',
      handle2 ? [{ label: '目标', value: `@${handle2}` }] : [{ label: '操作', value: '打开详情' }],
      reasons
    );
  }

  // 语义检索 / 运行 AI → 直接交给后端，不拦截
  if (userWantsRunAi(correctedText)) {
    return finish(base, 'ai_chat', '智能检索/分析', [{ label: '方式', value: 'AI 语义理解' }], []);
  }

  // 邀约/建联邮件：优先于泛化「红人筛选」，并继承上一轮查到的红人
  if (isMailInviteRequest(correctedText)) {
    const ctx = loadCopilotContext();
    const targets = hasMailInviteTarget(correctedText);
    const reasons: string[] = [];
    const steps: CopilotSummaryStep[] = [{ label: '操作', value: '合作邀约邮件（AI 个性化）' }];
    if (ctx.lastInfluencerIds?.length && (refersToPreviousInfluencer(correctedText) || !parseHandleFromText(correctedText))) {
      steps.unshift({ label: '红人', value: `${ctx.lastInfluencerIds.length} 位（会话上下文）` });
      if (ctx.lastHandles?.length) {
        steps.unshift({ label: '账号', value: ctx.lastHandles.map((h) => `@${h}`).join('、') });
      }
    } else {
      const multi = parseMultipleHandlesFromText(correctedText);
      const single = parseHandleFromText(correctedText);
      if (multi.length) {
        steps.unshift({ label: '红人', value: multi.map((h) => `@${h}`).join('、') });
      } else if (single) {
        steps.unshift({ label: '红人', value: `@${single}` });
      } else if (ctx.lastHandles?.length) {
        steps.unshift({ label: '红人', value: ctx.lastHandles.map((h) => `@${h}`).join('、') });
      }
    }
    if (!targets) {
      reasons.push('未指明要给哪位红人发邮件（请补充 @handle 或说「这两个红人」）');
    }
    return finish(
      base,
      'mail_invite',
      targets ? '给红人发送邀约邮件' : '发送邀约邮件',
      steps,
      reasons
    );
  }

  if (isInfluencerRelatedMessage(correctedText)) {
    const quickFilter = inferFilterFromUserMessage(correctedText);
    const extras = parseListFiltersFromText(correctedText);
    const fan = parseFanRangeFromText(correctedText);
    const selectIntent = parseSelectIntent(correctedText);
    const baseData =
      quickFilter?.name === 'ApplyInfluencerFilter'
        ? quickFilter.data
        : { statusTab: 'all' };
    const filterData = mergeFilterData({ ...baseData, ...fan }, extras) as Record<string, unknown>;
    if (selectIntent.select && selectIntent.count != null && filterData.listLimit == null) {
      filterData.listLimit = selectIntent.count;
    }
    const steps = describeFilterData(filterData);
    const reasons: string[] = [];
    const clearTopNQuery =
      selectIntent.select &&
      selectIntent.count != null &&
      (filterData.sortBy === 'fansDesc' || filterData.sortBy === 'fansAsc') &&
      hasSpecificFilterCriteria(filterData);
    if (!clearTopNQuery && !hasSpecificFilterCriteria(filterData)) {
      reasons.push('筛选条件比较笼统，不确定您要哪一类红人');
    }
    let desc = '在【红人列表】筛选红人';
    if (selectIntent.select) {
      desc = selectIntent.count ? `勾选前 ${selectIntent.count} 位红人` : '勾选当前筛选结果';
    }
    return finish(base, 'influencer_filter', desc, steps, reasons);
  }

  const statusMailReasons = isAmbiguousStatusOrMail(correctedText);
  if (statusMailReasons.length) {
    return finish(
      base,
      'ai_chat',
      '状态变更 / 建联 / 邮件相关操作',
      [{ label: '您的描述', value: correctedText }],
      statusMailReasons
    );
  }

  // 普通问答 → 直接问 AI
  return finish(base, 'ai_chat', '向小A 提问', [{ label: '问题', value: correctedText }], []);
}

/** 生成确认卡片标题 */
export function intentConfirmHead(plan: CopilotIntentPlan, honorific: string): string {
  const lead = honorific === '您' ? '我不太确定' : `${honorific}，我不太确定`;
  return `${lead}您的意思，想先确认一下再执行：`;
}

/** 生成确认卡片明细行 */
export function intentConfirmLines(plan: CopilotIntentPlan): string[] {
  const lines: string[] = [];
  if (plan.originalText !== plan.correctedText) {
    lines.push(`您输入：${plan.originalText}`);
    lines.push(`我理解：${plan.correctedText}`);
  } else {
    lines.push(`您说：${plan.originalText}`);
  }
  if (plan.typoNotes.length) {
    lines.push(`错字纠正：${plan.typoNotes.join('；')}`);
  }
  if (plan.ambiguityReasons.length) {
    lines.push(`不确定的点：${plan.ambiguityReasons.join('；')}`);
  }
  lines.push(`若正确，将执行：${plan.description}`);
  for (const s of plan.summarySteps) {
    lines.push(`${s.label}：${s.value}`);
  }
  return lines;
}

export function intentConfirmActions() {
  return [
    { id: 'confirm_intent' as const, label: '对的，执行' },
    { id: 'cancel_intent' as const, label: '不对，我再说' },
  ];
}

export { pageLabel };
