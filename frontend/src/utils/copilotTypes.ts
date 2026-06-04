import type { CopilotSummaryPayload } from '@/utils/copilotSummary';

export type ChatMsg = {
  role: 'user' | 'assistant';
  content: string;
  kind?: 'text' | 'summary';
  summary?: CopilotSummaryPayload;
};
