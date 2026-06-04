/** 将小A / AI 接口 HTTP 错误转成对用户有意义的文案 */
export function mapCopilotHttpError(status: number, bodyText: string): string {
  if (status === 401) {
    return '您已登录红人系统，但 AI 无法验证当前凭证。请退出后重新登录，再试小A。';
  }

  let detail = '';
  try {
    const json = JSON.parse(bodyText) as { message?: string; error?: string };
    detail = String(json.message || json.error || '');
  } catch {
    if (bodyText && !bodyText.trim().startsWith('<')) {
      detail = bodyText.trim().slice(0, 300);
    }
  }

  const blob = `${detail} ${bodyText}`.toLowerCase();

  if (status === 500 || status === 502 || status === 503) {
    if (
      blob.includes('sql') ||
      blob.includes('jdbc') ||
      blob.includes('only_full_group_by') ||
      blob.includes('3065') ||
      blob.includes('distinct')
    ) {
      return '红人库查询异常（数据库 SQL）。请重启 backend-influencer(8082) 并确认已部署最新 search-by-handle 修复。';
    }
    if (
      blob.includes('search-by-handle') ||
      blob.includes('backend-influencer') ||
      blob.includes('influencer') && blob.includes('internal')
    ) {
      return '红人服务 (8082) 调用失败，无法按 handle 查库。请确认 8082 已启动并重试。';
    }
    if (
      blob.includes('streamingchatmodel') ||
      blob.includes('chatmodel') ||
      blob.includes('ep-') ||
      blob.includes('api key') ||
      blob.includes('doubao') ||
      blob.includes('ark.cn')
    ) {
      return 'AI 对话模型未就绪。请确认 backend-ai-agent 已配置豆包 API Key 与对话接入点 (ep-)。';
    }
    if (detail) {
      const short = detail.length > 160 ? `${detail.slice(0, 160)}…` : detail;
      return `AI 服务异常 (${status})：${short}`;
    }
    return `AI 服务异常 (${status})，请查看 backend-ai-agent / backend-influencer 日志。`;
  }

  if (detail) return detail;
  return `AI 服务异常 (${status})`;
}
