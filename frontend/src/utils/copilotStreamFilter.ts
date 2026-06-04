const FC_BEGIN_RE = /<\|functionCallBegin\|>/i;
const FC_END_RE = /<\|functionCallEnd\|>/i;

const TOOL_STATUS: Record<string, string> = {
  searchInfluencers: '正在检索红人向量库与业务库…',
  lookupInfluencerByHandle: '正在按 handle 查询业务数据库…',
  navigateSystemPage: '正在为您跳转系统页面…',
  exportInfluencerContacts: '正在打开红人导出…',
  exportFilteredInfluencers: '正在打开红人导出…',
  exportOrders: '正在打开订单导出…',
};

/** 是否仅为模型泄漏的工具调用片段（不应展示给用户） */
export function isToolCallArtifact(text: string): boolean {
  const t = text.trim();
  if (!t) return false;
  if (FC_BEGIN_RE.test(t) || FC_END_RE.test(t)) return true;
  if (/searchInfluencers/i.test(t) && /"name"\s*:/i.test(t)) return true;
  if (/^\[\s*\{\s*"name"\s*:\s*"/.test(t)) return true;
  return false;
}

export function mapToolStatus(toolName: string): string {
  return TOOL_STATUS[toolName] ?? `正在执行「${toolName}」…`;
}

/** 流式 token 过滤器：剥离工具调用原始输出 */
export class CopilotStreamFilter {
  private pending = '';
  private insideFc = false;
  private lastHint: string | null = null;

  consume(token: string): { visible: string; statusHint: string | null } {
    this.lastHint = null;
    if (!token) return { visible: '', statusHint: null };
    this.pending += token;
    const visible: string[] = [];
    this.drain(visible);
    return {
      visible: visible.join(''),
      statusHint: this.lastHint,
    };
  }

  private drain(out: string[]) {
    while (this.pending.length > 0) {
      if (this.insideFc) {
        const endMatch = this.pending.match(FC_END_RE);
        if (!endMatch || endMatch.index === undefined) {
          if (!this.mightBePartialEnd()) return;
          continue;
        }
        const block = this.pending.slice(0, endMatch.index);
        this.resolveHint(block);
        this.pending = this.pending.slice(endMatch.index + endMatch[0].length);
        this.insideFc = false;
        continue;
      }
      const beginMatch = this.pending.match(FC_BEGIN_RE);
      if (!beginMatch || beginMatch.index === undefined) {
        if (!this.mightBePartialBegin()) {
          out.push(this.pending);
          this.pending = '';
        }
        return;
      }
      if (beginMatch.index > 0) {
        out.push(this.pending.slice(0, beginMatch.index));
      }
      this.pending = this.pending.slice(beginMatch.index + beginMatch[0].length);
      this.insideFc = true;
    }
  }

  private mightBePartialBegin(): boolean {
    const p = this.pending.toLowerCase();
    const full = '<|functioncallbegin|>';
    for (let i = 1; i < full.length; i++) {
      if (full.startsWith(p) && p.length === i) return true;
    }
    return false;
  }

  private mightBePartialEnd(): boolean {
    const p = this.pending.toLowerCase();
    const full = '<|functioncallend|>';
    for (let i = 1; i < full.length; i++) {
      if (full.startsWith(p) && p.length === i) return true;
    }
    return false;
  }

  private resolveHint(block: string) {
    const m = block.match(/"name"\s*:\s*"([^"]+)"/i);
    this.lastHint = m ? mapToolStatus(m[1]!) : '正在调用智能体工具…';
  }
}

export type SseChunk =
  | { type: 'token'; data: string }
  | { type: 'status'; data: string }
  | { type: 'ui-action'; data: string };

/** 解析 SSE 事件块（支持 event: status） */
export function parseSseEvent(raw: string): SseChunk | null {
  let eventType = 'message';
  const dataLines: string[] = [];
  for (const line of raw.split('\n')) {
    if (line.startsWith('event:')) eventType = line.slice(6).trim();
    else if (line.startsWith('data:')) dataLines.push(line.slice(5).trimStart());
  }
  const data = dataLines.join('\n');
  if (!data || data === '[DONE]') return null;
  if (eventType === 'status') return { type: 'status', data };
  if (eventType === 'ui-action') return { type: 'ui-action', data };
  return { type: 'token', data };
}
