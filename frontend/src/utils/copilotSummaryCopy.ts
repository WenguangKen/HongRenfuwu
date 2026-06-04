import type { CopilotSummaryPayload } from '@/utils/copilotSummary';
import { formatFilterLines, influencerListQuickActions } from '@/utils/copilotQuickActions';
import type { CopilotSummaryStep } from '@/utils/copilotSummary';

/** 订单 / 红人执行结果的人性化文案 */
export function orderSummaryCopy(opts: {
  honorific: string;
  total: number;
  applied: boolean;
  orderNo?: string;
  pageLabel: string;
}): Pick<CopilotSummaryPayload, 'result' | 'hint' | 'status'> {
  const h = opts.honorific;
  const lead = h === '您' ? '我' : `${h}，我`;
  const noLabel = opts.orderNo ? `单号 ${opts.orderNo}` : '当前条件';

  if (!opts.applied || opts.total <= 0) {
    return {
      status: 'empty',
      result: `${lead}在【${opts.pageLabel}】里没查到 ${noLabel} 对应的订单，列表保持原样。`,
      hint: '请核对单号是否正确；若是转化单请说「转化订单」，样品/红人单可说「红人订单」。',
    };
  }
  if (opts.total === 1) {
    return {
      status: 'success',
      result: `${lead}在【${opts.pageLabel}】里找到 1 笔订单（${noLabel}），已在左侧列表帮您定位好啦。`,
      hint: '点开那一行就能看订单详情～',
    };
  }
  return {
    status: 'success',
    result: `${lead}在【${opts.pageLabel}】里找到 ${opts.total} 笔相关订单（${noLabel}），列表已更新。`,
    hint: '可以说具体订单号，我帮您进一步缩小范围。',
  };
}

export function influencerSummaryCopy(opts: {
  honorific: string;
  total: number;
  applied: boolean;
  pendingOpenDetail?: boolean;
  filterSteps?: CopilotSummaryStep[];
  matchedTotal?: number;
  listLimit?: number;
  sortBy?: string;
}): Pick<CopilotSummaryPayload, 'result' | 'hint' | 'status' | 'lines' | 'actions'> {
  const h = opts.honorific;
  const lead = h === '您' ? '我' : `${h}，我`;
  const lines = opts.filterSteps?.length ? formatFilterLines(opts.filterSteps) : undefined;

  if (!opts.applied || opts.total <= 0) {
    const hasFilters = (opts.filterSteps?.length ?? 0) > 0;
    return {
      status: 'empty',
      result: `${lead}在红人列表里没找到匹配的红人，列表保持原样。`,
      hint: hasFilters
        ? '当前筛选条件下没有命中，可放宽平台/粉丝条件，或确认「待联系」等 Tab 下是否确有数据。'
        : '可以换个 handle、姓名或筛选条件再试。',
      lines,
    };
  }
  if (opts.pendingOpenDetail && opts.total === 1) {
    return {
      status: 'success',
      result: `${lead}找到 1 位匹配红人，正在打开详情页。`,
      lines,
      actions: influencerListQuickActions(1),
    };
  }
  if (opts.pendingOpenDetail && opts.total > 1) {
    return {
      status: 'info',
      result: `${lead}批量打开红人详情暂不支持（当前 ${opts.total} 位）。`,
      hint: '左侧表格已展示全部结果，可用下方按钮做批量操作。',
      lines,
      actions: influencerListQuickActions(opts.total),
    };
  }
  if (opts.total === 1) {
    return {
      status: 'success',
      result: `${lead}找到 1 位匹配红人，已在【红人列表】定位。`,
      hint: undefined,
      lines,
      actions: influencerListQuickActions(1),
    };
  }

  const limit = opts.listLimit;
  const sorted = opts.sortBy === 'fansDesc' ? '粉丝最高' : opts.sortBy === 'fansAsc' ? '粉丝最低' : '';
  // 用户指定了条数（如「找出 10 个」）→ 全程只谈这 N 人，不暴露后台命中池总量
  if (limit != null && limit > 0) {
    const sortHint = sorted ? `${sorted}的 ` : '';
    return {
      status: 'success',
      result: `${lead}已找出${sortHint}${opts.total} 位红人，左侧列表已更新${
        opts.total < limit ? `（符合条件的不足 ${limit} 人，已全部列出）` : ''
      }。`,
      hint: '可用下方按钮批量发邮件或打标；要查看某一位请提供 handle 或姓名。',
      lines,
      actions: influencerListQuickActions(opts.total),
    };
  }
  if (sorted && opts.total > 1) {
    return {
      status: 'success',
      result: `${lead}已按${sorted}排序，列出 ${opts.total} 位红人，左侧列表已更新。`,
      hint: '多人结果请用下方快捷操作；要查看某一位请提供 handle 或姓名。',
      lines,
      actions: influencerListQuickActions(opts.total),
    };
  }

  return {
    status: 'success',
    result: `${lead}已按条件筛出 ${opts.total} 位红人，左侧列表已更新。`,
    hint: '多人结果请用下方快捷操作；要查看某一位请提供 handle 或姓名。',
    lines,
    actions: influencerListQuickActions(opts.total),
  };
}

export function orderPendingCopy(honorific: string, pageLabel: string, orderNo?: string): string {
  const lead = honorific === '您' ? '我' : `${honorific}，我`;
  const no = orderNo ? `单号 ${orderNo}` : '您的条件';
  return `${lead}正在【${pageLabel}】里查 ${no}，稍等片刻…`;
}
