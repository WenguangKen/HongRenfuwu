import type { CopilotQuickAction } from '@/utils/copilotQuickActions';

/** 小A 执行结果卡片结构 */
export type CopilotSummaryStep = { label: string; value: string };

export type CopilotSummaryPayload = {
  steps: CopilotSummaryStep[];
  status: 'success' | 'empty' | 'pending' | 'info';
  result: string;
  hint?: string;
  /** 结构化筛选摘要行 */
  lines?: string[];
  /** 底部快捷操作 */
  actions?: CopilotQuickAction[];
};

export function buildCopilotSummary(payload: CopilotSummaryPayload): string {
  const stepLines = payload.steps.map((s) => `${s.label}：${s.value}`).join('\n');
  const hint = payload.hint ? `\n${payload.hint}` : '';
  return `${stepLines}\n\n${payload.result}${hint}`;
}

export function parseStepLine(line: string): CopilotSummaryStep | null {
  const m = line.replace(/^·\s*/, '').match(/^([^→：:]+)[→：:]\s*(.+)$/);
  if (!m) return null;
  return { label: m[1]!.trim(), value: m[2]!.trim() };
}

export function stepsFromLegacy(lines: string[]): CopilotSummaryStep[] {
  return lines
    .map((l) => parseStepLine(l.replace(/^·\s*/, '')))
    .filter((s): s is CopilotSummaryStep => !!s);
}
