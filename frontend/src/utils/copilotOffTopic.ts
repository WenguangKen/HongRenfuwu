/** 与红人业务相关的关键词（命中则走正常 AI） */
const INFLUENCER_HINTS =
  /红人|达人|\bkol\b|influencer|合作|休眠|粉丝|检索|筛选|向量|导出|资源池|HANDLE|分佣|折扣码|优惠码|折扣|待联系|已联系|沟通|黑名单|平台|TikTok|Instagram|YouTube|Macro|Mid-tier|标签|美妆|护肤|国家|未合作|已合作|有多少|查询|搜索|列表|语义|商务|联络员/i;

/** 摸鱼 / 闲聊意图 */
const SLACK_HINTS =
  /摸鱼|划水|偷懒|摆烂|不想上班|不想工作|想下班|要下班|早点下班|下班了|休息吧|请假|放假|闲聊|玩游戏|看剧|追剧|无聊|我爱你是|谈恋爱|天气怎么样|天气如何|吃什么|周末去哪|结婚|讲个笑话|笑话|故事|写诗|翻译一下|写代码|股票|彩票/;

/** 与业务无关的泛聊天 */
const CASUAL_HINTS = /你是谁|你叫什么|你能做什么|你会什么|聊天吗|聊聊天|随便聊|辛苦了|祝你|好好休息|下班愉快/;

/** 上班时间：周一至周五 09:00–18:00（本地系统时间） */
const WORK_START_MINUTES = 9 * 60;
const WORK_END_MINUTES = 18 * 60;

export const OFF_TOPIC_REPLY_WORK =
  '小A正在上班哦，我们还是不要摸鱼吧';

export const OFF_TOPIC_REPLY_OFF_WORK =
  '小A也下班啦，好好休息～红人业务明天上班再聊吧';

/** @deprecated 使用 getOffTopicReply 按时段返回 */
export const OFF_TOPIC_REPLY = OFF_TOPIC_REPLY_WORK;

export function isWorkHours(now: Date = new Date()): boolean {
  const day = now.getDay();
  const isWeekday = day >= 1 && day <= 5;
  const minutes = now.getHours() * 60 + now.getMinutes();
  return isWeekday && minutes >= WORK_START_MINUTES && minutes < WORK_END_MINUTES;
}

function withHonorific(honorific: string | undefined, base: string): string {
  if (!honorific || honorific === '您') return base;
  return `${honorific}，${base}`;
}

export function getOffTopicReplyByTime(now: Date = new Date(), honorific?: string): string {
  const base = isWorkHours(now) ? OFF_TOPIC_REPLY_WORK : OFF_TOPIC_REPLY_OFF_WORK;
  return withHonorific(honorific, base);
}

function isOffTopicMessage(msg: string): boolean {
  if (INFLUENCER_HINTS.test(msg)) return false;
  return SLACK_HINTS.test(msg) || CASUAL_HINTS.test(msg);
}

/**
 * 判断是否红人无关 / 摸鱼话题；是则按当前时间返回固定话术（不调用 AI）。
 */
export function getOffTopicReply(
  text: string,
  now: Date = new Date(),
  honorific?: string
): string | null {
  const msg = text.trim();
  if (!msg || !isOffTopicMessage(msg)) return null;
  return getOffTopicReplyByTime(now, honorific);
}
