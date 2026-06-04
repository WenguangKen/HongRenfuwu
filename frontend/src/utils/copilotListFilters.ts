import dayjs from 'dayjs';
import { parseFanRangeFromText } from '@/utils/copilotFanRange';
import { tabLabel } from '@/utils/aiInfluencerBridge';
import { STATUS_LABEL, InfluencerStatus } from '@/types/enums';
import type { CopilotSummaryStep } from '@/utils/copilotSummary';
import { normalizeCopilotUserText } from '@/utils/copilotTextNormalize';
import { RACES } from '@/config/constants';

export type RaceValue = (typeof RACES)[number];

/** 与红人列表筛选表单对应的 AI 筛选字段（与 InfluencerList 展开区一致） */
export type CopilotListFilterData = {
  statusTab?: string;
  listStatus?: string;
  /** 红人名称 → filterForm.name */
  searchKey?: string;
  socialHandle?: string;
  email?: string;
  discountCode?: string;
  /** 负责人姓名（列表页解析为 owner id） */
  ownerName?: string;
  /** 联络人姓名（列表页解析为 liaisonTagIds） */
  contactPersonName?: string;
  /** 建联平台 */
  platform?: string;
  /** 社媒平台 */
  socialPlatform?: string;
  country?: string;
  brand?: string;
  source?: string;
  link?: string;
  influencerType?: string;
  race?: RaceValue | RaceValue[];
  tagNames?: string[];
  isPaid?: boolean;
  fansMin?: number;
  fansMax?: number;
  minSampleCount?: number;
  maxSampleCount?: number;
  hasOrders?: boolean;
  hasOutputContent?: boolean;
  timeType?: 'created' | 'updated';
  timeStart?: string;
  timeEnd?: string;
  listLimit?: number;
  sortBy?: 'fansDesc' | 'fansAsc';
};

export function parseListLimitFromText(text: string): number | undefined {
  const t = text.trim();
  let m = t.match(/(?:找|找出|给我|列出|要|筛|显示|选|抱)\s*(\d{1,3})\s*(?:个|位|名|条)/);
  if (m) return Number(m[1]);
  m = t.match(/(?:前|top\s*)(\d{1,3})\s*(?:个|位|名)?/i);
  if (m) return Number(m[1]);
  m = t.match(/(\d{1,3})\s*(?:个|位|名).*(?:红人|待联系|合作|粉丝)/);
  if (m) return Number(m[1]);
  return undefined;
}

export function parseListSortFromText(text: string): 'fansDesc' | 'fansAsc' | undefined {
  if (/粉丝量?\s*(最高|最多|最大|从高到低|降序)|粉丝.*(最高|最多|最大|从高到低|降序)|(?:最高|最多|最大).*粉丝/i.test(text)) {
    return 'fansDesc';
  }
  if (/粉丝量?\s*(最低|最少|最小|升序)|粉丝.*(最低|最少|最小|升序)|(?:最低|最少|最小).*粉丝/i.test(text)) {
    return 'fansAsc';
  }
  return undefined;
}

const PLATFORM_ALIASES: Array<{ re: RegExp; platform: string }> = [
  { re: /tiktok|抖音国际|tt\b/i, platform: 'TikTok' },
  { re: /instagram|ins(?![a-z])|ig\b/i, platform: 'Instagram' },
  { re: /youtube|yt\b/i, platform: 'YouTube' },
  { re: /facebook|fb\b/i, platform: 'Facebook' },
];

const COUNTRY_ALIASES: Array<{ re: RegExp; country: string }> = [
  { re: /美国|usa|united\s*states/i, country: 'US' },
  { re: /英国|uk|united\s*kingdom/i, country: 'UK' },
  { re: /加拿大|canada/i, country: 'CA' },
  { re: /德国|germany/i, country: 'DE' },
  { re: /法国|france/i, country: 'FR' },
  { re: /澳洲|澳大利亚|australia/i, country: 'AU' },
  { re: /日本|japan/i, country: 'JP' },
  { re: /韩国|korea/i, country: 'KR' },
  { re: /新加坡|singapore/i, country: 'SG' },
  { re: /墨西哥|mexico/i, country: 'MX' },
  { re: /巴西|brazil/i, country: 'BR' },
];

const RACE_ALIASES: Array<{ re: RegExp; race: RaceValue }> = [
  { re: /白人(?:\s*肤色|\s*人种)?|白种|白肤色|caucasian|\bwhite\b/i, race: 'White' },
  { re: /黑人(?:\s*肤色|\s*人种)?|黑种|非裔|african\s*descent|\bblack\b/i, race: 'Black / African descent' },
  { re: /亚裔|亚洲人|asian/i, race: 'Asian' },
  { re: /拉丁|拉美|hispanic|latino/i, race: 'Hispanic / Latino' },
  { re: /中东(?:\s*裔)?|middle\s*eastern/i, race: 'Middle Eastern' },
  { re: /混血|mixed/i, race: 'Mixed' },
];

const SOURCE_ALIASES: Array<{ re: RegExp; source: string }> = [
  { re: /导入|import/i, source: 'Import' },
  { re: /手动添加|手动|manual/i, source: 'Manual' },
  { re: /爬虫|crawler/i, source: 'Crawler' },
];

const TYPE_ALIASES: Array<{ re: RegExp; type: string }> = [
  { re: /美妆|beauty/i, type: 'Beauty' },
  { re: /时尚|fashion/i, type: 'Fashion' },
  { re: /科技|tech/i, type: 'Tech' },
  { re: /生活方式|生活类|lifestyle/i, type: 'Lifestyle' },
];

export function raceDisplayLabel(race: string): string {
  switch (race) {
    case 'White': return '白人 (White)';
    case 'Black / African descent': return '黑人 (Black)';
    case 'Asian': return '亚裔 (Asian)';
    case 'Hispanic / Latino': return '拉丁裔 (Hispanic / Latino)';
    case 'Middle Eastern': return '中东裔 (Middle Eastern)';
    case 'Mixed': return '混血 (Mixed)';
    default: return race;
  }
}

export function parseRaceFromText(text: string): RaceValue | undefined {
  const t = normalizeCopilotUserText(text);
  for (const { re, race } of RACE_ALIASES) {
    if (re.test(t)) return race;
  }
  return undefined;
}

function parsePlatformFromText(t: string): string | undefined {
  for (const { re, platform } of PLATFORM_ALIASES) {
    if (re.test(t)) return platform;
  }
  return undefined;
}

function parseCountryFromText(t: string): string | undefined {
  for (const { re, country } of COUNTRY_ALIASES) {
    if (re.test(t)) return country;
  }
  return undefined;
}

function parseEmailFromText(t: string): string | undefined {
  const labeled = t.match(/(?:红人)?邮箱[是为：:\s]+([^\s,，、]+@[^\s,，、]+)/i);
  if (labeled?.[1]) return labeled[1].trim();
  const plain = t.match(/([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,})/);
  return plain?.[1];
}

function parseDiscountCodeFromText(t: string): string | undefined {
  const m = t.match(/折扣码[是为：:\s]+([A-Za-z0-9_-]+)/i);
  return m?.[1]?.trim();
}

function parseOwnerNameFromText(t: string): string | undefined {
  const m = t.match(/负责人[是为：:\s]+([^，,、\n]+?)(?=$|[，,、]|平台|粉丝|国家|待联系|合作|白人|黑人)/i);
  return m?.[1]?.trim();
}

function parseContactPersonFromText(t: string): string | undefined {
  const m = t.match(/联络人[是为：:\s]+([^，,、\n]+)/i);
  return m?.[1]?.trim();
}

function parseLinkFromText(t: string): string | undefined {
  const labeled = t.match(/主页链接[是为：:\s]+(https?:\/\/[^\s,，]+)/i);
  if (labeled?.[1]) return labeled[1];
  const url = t.match(/(https?:\/\/[^\s,，]+)/i);
  return url?.[1];
}

function parseInfluencerNameFromText(t: string): string | undefined {
  const m = t.match(/红人名(?:称)?[是为：:\s]+([^，,、\n]+?)(?=$|[，,、]|平台|粉丝)/i);
  return m?.[1]?.trim();
}

function parseTagNamesFromText(t: string): string[] | undefined {
  const tags: string[] = [];
  const labeled = t.match(/(?:红人)?标签[是为包含：:\s]+([^，,、\n]+)/i);
  if (labeled?.[1]) {
    labeled[1].split(/[、,，/|]/).map((s) => s.trim()).filter(Boolean).forEach((s) => tags.push(s));
  }
  const withTag = t.match(/带(?:有)?[「"']?([^「"'\s]+)[」"']?标签/i);
  if (withTag?.[1]) tags.push(withTag[1].trim());
  return tags.length ? [...new Set(tags)] : undefined;
}

function parseTimeRangeFromText(t: string): Pick<CopilotListFilterData, 'timeType' | 'timeStart' | 'timeEnd'> {
  const out: Pick<CopilotListFilterData, 'timeType' | 'timeStart' | 'timeEnd'> = {};
  let timeType: 'created' | 'updated' = 'created';
  if (/更新时间|最近更新|更新于/i.test(t)) timeType = 'updated';
  if (/创建时间|新建|入库|添加时间/i.test(t)) timeType = 'created';

  const today = dayjs();
  if (/最近\s*7\s*天|近\s*7\s*天|近一周|过去一周/i.test(t)) {
    out.timeType = timeType;
    out.timeStart = today.subtract(7, 'day').format('YYYY-MM-DD');
    out.timeEnd = today.format('YYYY-MM-DD');
  } else if (/最近\s*30\s*天|近\s*30\s*天|近一个月|过去一月|最近一个月/i.test(t)) {
    out.timeType = timeType;
    out.timeStart = today.subtract(30, 'day').format('YYYY-MM-DD');
    out.timeEnd = today.format('YYYY-MM-DD');
  } else if (/今年/i.test(t)) {
    out.timeType = timeType;
    out.timeStart = today.startOf('year').format('YYYY-MM-DD');
    out.timeEnd = today.format('YYYY-MM-DD');
  } else if (/本月|这个月/i.test(t)) {
    out.timeType = timeType;
    out.timeStart = today.startOf('month').format('YYYY-MM-DD');
    out.timeEnd = today.format('YYYY-MM-DD');
  }

  const rangeM = t.match(/(\d{4}-\d{2}-\d{2})\s*(?:到|至|~|-)\s*(\d{4}-\d{2}-\d{2})/);
  if (rangeM) {
    out.timeType = timeType;
    out.timeStart = rangeM[1];
    out.timeEnd = rangeM[2];
  }
  return out;
}

/** 从自然语言补充列表筛选项（与页面上方/展开区筛选一致） */
export function parseListFiltersFromText(text: string): CopilotListFilterData {
  const t = normalizeCopilotUserText(text);
  const out: CopilotListFilterData = {};

  const fan = parseFanRangeFromText(t);
  if (fan.fansMin != null) out.fansMin = fan.fansMin;
  if (fan.fansMax != null) out.fansMax = fan.fansMax;

  const platformHit = parsePlatformFromText(t);
  if (/建联平台/i.test(t) && platformHit) {
    out.platform = platformHit;
  } else if (platformHit) {
    out.socialPlatform = platformHit;
  }

  const country = parseCountryFromText(t);
  if (country) out.country = country;

  const race = parseRaceFromText(t);
  if (race) out.race = race;

  const email = parseEmailFromText(t);
  if (email) out.email = email;

  const discountCode = parseDiscountCodeFromText(t);
  if (discountCode) out.discountCode = discountCode;

  const ownerName = parseOwnerNameFromText(t);
  if (ownerName) out.ownerName = ownerName;

  const contactPersonName = parseContactPersonFromText(t);
  if (contactPersonName) out.contactPersonName = contactPersonName;

  const link = parseLinkFromText(t);
  if (link) out.link = link;

  const searchKey = parseInfluencerNameFromText(t);
  if (searchKey) out.searchKey = searchKey;

  const tagNames = parseTagNamesFromText(t);
  if (tagNames) out.tagNames = tagNames;

  for (const { re, source } of SOURCE_ALIASES) {
    if (re.test(t)) {
      out.source = source;
      break;
    }
  }

  for (const { re, type } of TYPE_ALIASES) {
    if (re.test(t)) {
      out.influencerType = type;
      break;
    }
  }

  if (/未合作|尚未合作|未付费|non.?paid/i.test(t)) out.isPaid = false;
  if (/已合作|付费合作|已付费|paid/i.test(t)) out.isPaid = true;

  if (/有(?:输出)?内容|发过内容|有作品|有发帖|有发布|有内容输出/i.test(t)) {
    out.hasOutputContent = true;
  }
  if (/无(?:输出)?内容|没(?:有)?内容|从未发(?:布|帖)|无作品/i.test(t)) {
    out.hasOutputContent = false;
  }

  Object.assign(out, parseTimeRangeFromText(t));

  const limit = parseListLimitFromText(t);
  if (limit != null && limit > 0) out.listLimit = Math.min(limit, 500);
  const sortBy = parseListSortFromText(t);
  if (sortBy) out.sortBy = sortBy;

  const statusPatterns: Array<{ re: RegExp; tab: string }> = [
    { re: /休眠中|休眠/, tab: 'dormant' },
    { re: /合作中/, tab: 'cooperating' },
    { re: /待联系/, tab: 'pending' },
    { re: /已联系/, tab: 'contacted' },
    { re: /沟通中/, tab: 'communicating' },
    { re: /暂不合作/, tab: 'paused' },
    { re: /黑名单/, tab: 'blacklist' },
    { re: /不再合作/, tab: 'terminated' },
  ];
  for (const { re, tab } of statusPatterns) {
    if (re.test(t)) {
      out.statusTab = tab;
      break;
    }
  }

  const coopMin = t.match(/合作\s*(\d+)\s*次\s*以\s*上|至少\s*合作\s*(\d+)\s*次|合作次数\s*[≥>=]\s*(\d+)/i);
  if (coopMin) {
    out.minSampleCount = Number(coopMin[1] || coopMin[2] || coopMin[3]);
  }
  const coopMax = t.match(/合作\s*(\d+)\s*次\s*以\s*下|合作次数\s*[≤<=]\s*(\d+)/i);
  if (coopMax) {
    out.maxSampleCount = Number(coopMax[1] || coopMax[2]);
  }
  if (/有订单|下过单|有转化|有合作订单/i.test(t)) out.hasOrders = true;
  if (/无订单|没订单|从未下单/i.test(t)) {
    out.hasOrders = false;
    out.maxSampleCount = 0;
  }

  return out;
}

export function mergeFilterData(
  base: Record<string, unknown>,
  extra: CopilotListFilterData
): Record<string, unknown> {
  return { ...base, ...extra };
}

/** 小A 跳转红人列表时写入 URL，列表页 onMounted/watch 会立刻应用 */
export function influencerFilterToRouteQuery(
  data: Record<string, unknown>,
  opts?: { reset?: boolean }
): Record<string, string> {
  const q: Record<string, string> = {
    fromAi: '1',
    status: String(data.statusTab ?? 'all'),
  };
  if (opts?.reset) q.resetAi = '1';
  if (data.socialHandle) q.handle = String(data.socialHandle);
  if (data.searchKey) q.q = String(data.searchKey);
  if (data.email) q.email = String(data.email);
  if (data.discountCode) q.discountCode = String(data.discountCode);
  if (data.ownerName) q.ownerName = String(data.ownerName);
  if (data.contactPersonName) q.contactPersonName = String(data.contactPersonName);
  if (data.link) q.link = String(data.link);
  if (data.source) q.source = String(data.source);
  if (data.fansMax != null && data.fansMax !== '') q.fansMax = String(data.fansMax);
  if (data.fansMin != null && data.fansMin !== '') q.fansMin = String(data.fansMin);
  if (data.influencerId != null) q.openId = String(data.influencerId);
  if (data.isPaid === false) q.isPaid = '0';
  if (data.isPaid === true) q.isPaid = '1';
  if (data.hasOutputContent === true) q.hasOutputContent = '1';
  if (data.hasOutputContent === false) q.hasOutputContent = '0';
  if (data.listStatus) q.listStatus = String(data.listStatus);
  if (data.listLimit != null) q.limit = String(data.listLimit);
  if (data.sortBy) q.sortBy = String(data.sortBy);
  if (data.socialPlatform) q.socialPlatform = String(data.socialPlatform);
  if (data.platform) q.platform = String(data.platform);
  if (data.country) q.country = String(data.country);
  if (data.brand) q.brand = String(data.brand);
  if (data.influencerType) q.influencerType = String(data.influencerType);
  if (data.race) {
    const r = Array.isArray(data.race) ? data.race[0] : data.race;
    if (r) q.race = String(r);
  }
  if (data.tagNames) {
    const names = Array.isArray(data.tagNames) ? data.tagNames : [data.tagNames];
    if (names.length) q.tagNames = names.join('|');
  }
  if (data.timeType) q.timeType = String(data.timeType);
  if (data.timeStart) q.timeStart = String(data.timeStart);
  if (data.timeEnd) q.timeEnd = String(data.timeEnd);
  if (data.minSampleCount != null) q.minSampleCount = String(data.minSampleCount);
  if (data.maxSampleCount != null) q.maxSampleCount = String(data.maxSampleCount);
  return q;
}

const SOURCE_LABELS: Record<string, string> = {
  Import: '导入',
  Manual: '手动添加',
  Crawler: '爬虫抓取',
};

export function describeFilterData(data: Record<string, unknown>): CopilotSummaryStep[] {
  const steps: CopilotSummaryStep[] = [];
  const tab = String(data.statusTab ?? 'all');
  if (tab === 'all') {
    steps.push({ label: '列表视图', value: '全部红人（非状态筛选）' });
  } else {
    steps.push({ label: '列表 Tab', value: tabLabel(tab) });
  }
  if (data.currentStatus) {
    const st = String(data.currentStatus) as InfluencerStatus;
    steps.push({
      label: '红人状态',
      value: STATUS_LABEL[st] ?? String(data.currentStatus),
    });
  } else if (tab === 'all' && data.listStatus) {
    const st = String(data.listStatus) as InfluencerStatus;
    steps.push({
      label: '红人状态',
      value: STATUS_LABEL[st] ?? tabLabel(String(data.listStatus).toLowerCase()) ?? String(data.listStatus),
    });
  }
  if (data.listLimit != null) steps.push({ label: '展示条数', value: String(data.listLimit) });
  if (data.sortBy === 'fansDesc') steps.push({ label: '排序', value: '粉丝从高到低' });
  if (data.sortBy === 'fansAsc') steps.push({ label: '排序', value: '粉丝从低到高' });
  if (data.searchKey) steps.push({ label: '红人名称', value: String(data.searchKey) });
  if (data.socialHandle) steps.push({ label: '社媒账号', value: String(data.socialHandle) });
  if (data.email) steps.push({ label: '红人邮箱', value: String(data.email) });
  if (data.discountCode) steps.push({ label: '折扣码', value: String(data.discountCode) });
  if (data.ownerName) steps.push({ label: '负责人', value: String(data.ownerName) });
  if (data.contactPersonName) steps.push({ label: '联络人', value: String(data.contactPersonName) });
  if (data.socialPlatform) steps.push({ label: '社媒平台', value: String(data.socialPlatform) });
  if (data.platform) steps.push({ label: '建联平台', value: String(data.platform) });
  if (data.country) steps.push({ label: '国家', value: String(data.country) });
  if (data.race) {
    const races = Array.isArray(data.race) ? data.race : [data.race];
    steps.push({ label: '人种/肤色', value: races.map((r) => raceDisplayLabel(String(r))).join('、') });
  }
  if (data.source) steps.push({ label: '红人来源', value: SOURCE_LABELS[String(data.source)] ?? String(data.source) });
  if (data.link) steps.push({ label: '主页链接', value: String(data.link) });
  if (data.brand) steps.push({ label: '品牌', value: String(data.brand) });
  if (data.influencerType) steps.push({ label: '红人类型', value: String(data.influencerType) });
  if (data.tagNames) {
    const names = Array.isArray(data.tagNames) ? data.tagNames : [data.tagNames];
    steps.push({ label: '红人标签', value: names.map(String).join('、') });
  }
  if (data.isPaid === true) steps.push({ label: '是否付费', value: '是' });
  if (data.isPaid === false) steps.push({ label: '是否付费', value: '否' });
  if (data.fansMax != null) steps.push({ label: '粉丝', value: `≤ ${data.fansMax}` });
  if (data.fansMin != null) steps.push({ label: '粉丝', value: `≥ ${data.fansMin}` });
  if (data.minSampleCount != null) steps.push({ label: '合作次数', value: `≥ ${data.minSampleCount}` });
  if (data.maxSampleCount != null) steps.push({ label: '合作次数', value: `≤ ${data.maxSampleCount}` });
  if (data.hasOrders === true) steps.push({ label: '订单', value: '有合作/订单记录' });
  if (data.hasOrders === false) steps.push({ label: '订单', value: '无订单记录' });
  if (data.hasOutputContent === true) steps.push({ label: '输出内容', value: '有' });
  if (data.hasOutputContent === false) steps.push({ label: '输出内容', value: '无' });
  if (data.timeStart && data.timeEnd) {
    const kind = data.timeType === 'updated' ? '更新' : '创建';
    steps.push({ label: '时间范围', value: `${kind} ${data.timeStart} ~ ${data.timeEnd}` });
  }
  return steps;
}
