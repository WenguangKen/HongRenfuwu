/** 从自然语言解析粉丝数区间（与列表 filterForm.fansMin / fansMax 对应） */
export type FanRange = { fansMin?: number; fansMax?: number };

function parseCountToken(raw: string): number | undefined {
  const s = raw.trim().replace(/,/g, '');
  if (!s) return undefined;
  if (/万/.test(s)) {
    const n = parseFloat(s.replace(/万/g, ''));
    return Number.isFinite(n) ? Math.round(n * 10000) : undefined;
  }
  const n = parseInt(s.replace(/[^\d]/g, ''), 10);
  return Number.isFinite(n) ? n : undefined;
}

export function parseFanRangeFromText(text: string): FanRange {
  const t = text.replace(/\s+/g, '');
  if (!t) return {};

  const below =
    t.match(/([\d.]+万?)\s*粉丝量?\s*(以下|以内|不超过|少于|小于|低于)/) ??
    t.match(/粉丝量?\s*([\d.]+万?)\s*(以下|以内|不超过|少于|小于|低于)/) ??
    t.match(/(少于|小于|不超过|低于)\s*([\d.]+万?)\s*粉丝量?/) ??
    t.match(/粉丝量?\s*(少于|小于|不超过|低于)\s*([\d.]+万?)/);
  if (below) {
    const num = parseCountToken((below[1] || below[2])!);
    if (num !== undefined) return { fansMax: num };
  }

  const above =
    t.match(/([\d.]+万?)\s*粉丝量?\s*(以上|超过|大于|不少于|不低于)/) ??
    t.match(/粉丝量?\s*([\d.]+万?)\s*(以上|超过|大于|不少于|不低于)/) ??
    t.match(/(超过|大于|不少于|不低于)\s*([\d.]+万?)\s*粉丝量?/) ??
    t.match(/粉丝量?\s*(超过|大于|不少于|不低于)\s*([\d.]+万?)/);
  if (above) {
    const num = parseCountToken((above[1] || above[2])!);
    if (num !== undefined) return { fansMin: num };
  }

  const range = t.match(/([\d.]+万?)\s*(?:到|-|~|至)\s*([\d.]+万?)\s*粉丝?/);
  if (range) {
    const min = parseCountToken(range[1]!);
    const max = parseCountToken(range[2]!);
    if (min !== undefined && max !== undefined) {
      return { fansMin: Math.min(min, max), fansMax: Math.max(min, max) };
    }
  }

  const plainAbove = t.match(/([\d.]+万?)\s*以上/);
  if (plainAbove) {
    const num = parseCountToken(plainAbove[1]!);
    if (num !== undefined) return { fansMin: num };
  }
  const plainBelow = t.match(/([\d.]+万?)\s*以下/);
  if (plainBelow) {
    const num = parseCountToken(plainBelow[1]!);
    if (num !== undefined) return { fansMax: num };
  }

  return {};
}

export function fanRangeLabel(range: FanRange): string {
  if (range.fansMin != null && range.fansMax != null) {
    return `粉丝 ${range.fansMin}–${range.fansMax}`;
  }
  if (range.fansMax != null) return `粉丝 ≤ ${range.fansMax}`;
  if (range.fansMin != null) return `粉丝 ≥ ${range.fansMin}`;
  return '';
}

/** 整句问法不应写入「红人名称」搜索框 */
export function isBroadQueryText(text?: string): boolean {
  if (!text?.trim()) return false;
  return /有多少|多少|几个|统计|数量|以下|以上|以内|粉丝|筛选|查询|搜索|红人|达人|kol/i.test(text);
}
