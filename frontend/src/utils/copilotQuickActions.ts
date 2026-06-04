import type { CopilotSummaryStep } from '@/utils/copilotSummary';

export type CopilotQuickAction = {
  id: 'export' | 'batch_mail' | 'batch_tag' | 'open_detail' | 'confirm_intent' | 'cancel_intent';
  label: string;
};

/** 红人列表筛选成功后的快捷操作 */
export function influencerListQuickActions(total: number): CopilotQuickAction[] {
  if (total <= 0) return [];
  if (total === 1) {
    return [
      { id: 'open_detail', label: '打开详情' },
      { id: 'export', label: '导出红人' },
    ];
  }
  return [
    { id: 'export', label: '导出红人' },
    { id: 'batch_mail', label: '批量发邮件' },
    { id: 'batch_tag', label: '批量打标' },
  ];
}

export function formatFilterLines(steps: CopilotSummaryStep[]): string[] {
  return steps.map((s) => `${s.label}：${s.value}`);
}
