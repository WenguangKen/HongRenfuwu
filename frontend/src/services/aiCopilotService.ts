import { useUserStore } from '@/stores/user';
import { readAuthToken } from '@/utils/authToken';
import { ngrokRequestHeaders } from '@/utils/ngrok';

const CHAT_URL = '/ai-agent-api/v1/copilot-chat';

/** 与 axios 一致：以 localStorage 为准（含 X-New-Token 刷新），再回退 Pinia */
function getAuthToken(): string | null {
  const fromStorage = readAuthToken()?.trim();
  if (fromStorage) {
    const store = useUserStore();
    if (store.token !== fromStorage) {
      store.token = fromStorage;
    }
    return fromStorage;
  }
  return useUserStore().token?.trim() || null;
}

function getSessionId(): string {
  const key = 'ai_copilot_session_id';
  let id = sessionStorage.getItem(key);
  if (!id) {
    id = `sess-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
    sessionStorage.setItem(key, id);
  }
  return id;
}

import type { AiUiAction } from '@/utils/aiActionBus';
import { mapCopilotHttpError } from '@/utils/copilotErrorMessage';
import { CopilotStreamFilter, parseSseEvent } from '@/utils/copilotStreamFilter';

export type CopilotChatResult = { text: string; uiActions: AiUiAction[] };

export type CopilotChatOptions = {
  /** 1=男(小哥) 2=女(公主)，未传则后端用「您」 */
  userGender?: 1 | 2;
  /** 前端会话上下文，供 Agent 理解指代 */
  sessionContext?: Record<string, unknown>;
};

export async function streamCopilotChat(
  message: string,
  onToken: (token: string) => void,
  signal?: AbortSignal,
  onStatus?: (hint: string) => void,
  onRawChunk?: (chunk: string) => void,
  onUiAction?: (action: AiUiAction) => void,
  options?: CopilotChatOptions
): Promise<CopilotChatResult> {
  const token = getAuthToken();
  if (!token) {
    throw new Error('未读取到登录凭证，请刷新页面或退出后重新登录');
  }

  const response = await fetch(CHAT_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      'X-Session-Id': getSessionId(),
      ...ngrokRequestHeaders(),
    },
    body: JSON.stringify({
      message,
      userGender: options?.userGender ?? null,
      sessionContext: options?.sessionContext ?? null,
    }),
    signal,
  });

  if (!response.ok) {
    const errText = await response.text().catch(() => '');
    throw new Error(mapCopilotHttpError(response.status, errText));
  }

  if (!response.body) {
    throw new Error('AI 服务未返回流式数据');
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let buffer = '';
  let fullText = '';
  const uiActions: AiUiAction[] = [];
  const filter = new CopilotStreamFilter();

  const pushUiAction = (raw: string) => {
    try {
      const action = JSON.parse(raw) as AiUiAction;
      if (action?.name) {
        uiActions.push(action);
        onUiAction?.(action);
      }
    } catch {
      /* ignore malformed */
    }
  };

  const handleChunk = (chunk: string) => {
    if (chunk === '[DONE]') return;
    onRawChunk?.(chunk);
    const { visible, statusHint } = filter.consume(chunk);
    if (statusHint) onStatus?.(statusHint);
    if (visible) {
      fullText += visible;
      onToken(visible);
    }
  };

  while (true) {
    const { done, value } = await reader.read();
    if (done) break;
    buffer += decoder.decode(value, { stream: true });

    const events = buffer.split('\n\n');
    buffer = events.pop() ?? '';

    for (const event of events) {
      const parsed = parseSseEvent(event);
      if (!parsed) continue;
      if (parsed.type === 'status') {
        onStatus?.(parsed.data);
      } else if (parsed.type === 'ui-action') {
        pushUiAction(parsed.data);
      } else {
        handleChunk(parsed.data);
      }
    }
  }

  if (buffer.trim()) {
    const parsed = parseSseEvent(buffer);
    if (parsed?.type === 'status') onStatus?.(parsed.data);
    else if (parsed?.type === 'ui-action') pushUiAction(parsed.data);
    else if (parsed?.type === 'token') handleChunk(parsed.data);
  }

  return { text: fullText, uiActions };
}

export type CopilotLookupReplyPayload = import('@/utils/copilotInfluencerLookup').CopilotLookupReplyPayload;

/** 查库完成后，让 AI 根据结构化事实生成口语回复 */
export async function generateCopilotLookupReply(
  payload: CopilotLookupReplyPayload,
  options?: CopilotChatOptions
): Promise<string> {
  const token = getAuthToken();
  if (!token) {
    throw new Error('未读取到登录凭证');
  }
  const response = await fetch('/ai-agent-api/v1/copilot-lookup-reply', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...ngrokRequestHeaders(),
    },
    body: JSON.stringify({
      ...payload,
      userGender: options?.userGender ?? null,
    }),
  });
  if (!response.ok) {
    const errText = await response.text().catch(() => '');
    throw new Error(mapCopilotHttpError(response.status, errText));
  }
  const data = (await response.json()) as { reply?: string };
  return (data.reply ?? '').trim();
}
