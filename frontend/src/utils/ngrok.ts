/** 当前是否通过 ngrok 公网域名访问 */
export function isNgrokHost(): boolean {
  if (typeof window === 'undefined') return false;
  return /\.ngrok(-free)?\.(app|dev)$/i.test(window.location.hostname);
}

/** ngrok 免费版：API 请求需带此头，避免部分请求被拦截 */
export function ngrokRequestHeaders(): Record<string, string> {
  return isNgrokHost() ? { 'ngrok-skip-browser-warning': '1' } : {};
}
