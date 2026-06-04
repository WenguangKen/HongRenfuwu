import { loadCopilotContext } from '@/utils/copilotContext';
import { isOrderRelatedMessage } from '@/utils/copilotOrderFilters';

export type CopilotExportTarget = 'influencer' | 'order_sample' | 'order_conversion';

export type CopilotExportScope = 'filtered' | 'all' | 'selected';

/** 是否在指代上一轮列表/勾选结果（这些红人、上面那批等） */
export function refersToPreviousSelection(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return (
    /这些|这批|上面|刚才|上述|前述|刚找|刚筛|刚选/.test(t) &&
    /红人|达人|kol|数据|条|位|人|结果/.test(t)
  );
}

/** 用户是否要导出列表 */
export function userWantsExport(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  return /导出|下载.*(excel|表格|xlsx)|export\s/i.test(t);
}

/** 推断导出目标页 */
export function inferExportTarget(text: string): CopilotExportTarget {
  const t = text.trim();
  if (/转化订单|转化单/.test(t)) return 'order_conversion';
  if (/样品|红人订单|样品单/.test(t)) return 'order_sample';
  if (/红人|达人|kol|influencer/i.test(t)) return 'influencer';
  if (isOrderRelatedMessage(t)) return 'order_sample';
  return 'influencer';
}

export function exportTargetPath(target: CopilotExportTarget): string {
  switch (target) {
    case 'order_conversion':
      return '/order/conversion';
    case 'order_sample':
      return '/order/sample';
    default:
      return '/influencer/list';
  }
}

export function exportTargetLabel(target: CopilotExportTarget): string {
  switch (target) {
    case 'order_conversion':
      return '转化订单';
    case 'order_sample':
      return '红人订单（样品）';
    default:
      return '红人列表';
  }
}

export function inferExportScope(text: string): CopilotExportScope {
  if (/选中|勾选|已选/.test(text)) return 'selected';
  if (refersToPreviousSelection(text)) return 'selected';
  if (/全部|所有|全量/.test(text)) return 'all';
  return 'filtered';
}

/** 结合会话上下文解析导出范围（跟进「导出这些」时优先已勾选） */
export function resolveExportScope(text: string): CopilotExportScope {
  const scope = inferExportScope(text);
  if (scope !== 'filtered') return scope;
  const ctx = loadCopilotContext();
  if (refersToPreviousSelection(text) && (ctx.lastMatchCount ?? 0) > 0) {
    return 'selected';
  }
  return scope;
}

/** 从话术解析导出模板名称，如「模板 123」「导出模板123」 */
export function parseExportTemplateName(text: string): string | undefined {
  const t = text.trim();
  if (!t) return undefined;
  const m =
    t.match(
      /(?:使用|用|按|套用)?(?:红人)?(?:导出)?模板\s*[「『"'"]?\s*([^「」『』"'"\s，,。.]+)/i
    ) ?? t.match(/模板[「『"'"]?\s*([^「」『』"'"\s，,。.]+)/i);
  const name = m?.[1]?.trim();
  return name || undefined;
}
