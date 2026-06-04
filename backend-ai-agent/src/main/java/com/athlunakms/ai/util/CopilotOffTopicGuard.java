package com.athlunakms.ai.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 拦截红人无关 / 摸鱼类闲聊，按上下班时间返回不同固定话术。 */
@Component
public class CopilotOffTopicGuard {

    public static final String OFF_TOPIC_REPLY_WORK = "小A正在上班哦，我们还是不要摸鱼吧";
    public static final String OFF_TOPIC_REPLY_OFF_WORK = "小A也下班啦，好好休息～红人业务明天上班再聊吧";

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Value("${app.copilot.work-start:09:00}")
    private String workStart;

    @Value("${app.copilot.work-end:18:00}")
    private String workEnd;

    private static final Pattern INFLUENCER_HINTS = Pattern.compile(
            "红人|达人|kol|influencer|合作|休眠|粉丝|检索|筛选|向量|导出|资源池|HANDLE|分佣|折扣|"
                    + "待联系|已联系|沟通|黑名单|平台|tiktok|instagram|youtube|macro|mid-tier|标签|美妆|护肤|"
                    + "国家|未合作|已合作|有多少|查询|搜索|列表|语义|商务|联络员|详情|关闭详情|收起详情|"
                    + "订单|单号|样品单|转化单|红人订单|交易号|折扣码|优惠码|"
                    + "待上传|内容库|邮件|建联|打款|汇款|商品|跳转|前往|打开|页面",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SLACK_OR_CASUAL = Pattern.compile(
            "摸鱼|划水|偷懒|摆烂|不想上班|不想工作|想下班|要下班|早点下班|下班了|休息吧|请假|放假|闲聊|"
                    + "玩游戏|看剧|追剧|无聊|我爱你|谈恋爱|天气|吃什么|周末|结婚|笑话|故事|写诗|翻译|代码|股票|彩票|"
                    + "你是谁|你叫什么|你能做什么|你会什么|聊天|随便聊|辛苦了|祝你|好好休息|下班愉快",
            Pattern.CASE_INSENSITIVE);

    public boolean isWorkHours() {
        return isWorkHours(ZonedDateTime.now(ZONE));
    }

    public boolean isWorkHours(ZonedDateTime now) {
        DayOfWeek dow = now.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalTime time = now.toLocalTime();
        LocalTime start = LocalTime.parse(workStart);
        LocalTime end = LocalTime.parse(workEnd);
        return !time.isBefore(start) && time.isBefore(end);
    }

    public String replyByTime() {
        return isWorkHours() ? OFF_TOPIC_REPLY_WORK : OFF_TOPIC_REPLY_OFF_WORK;
    }

    public Optional<String> replyIfOffTopic(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }
        String msg = message.trim();
        if (INFLUENCER_HINTS.matcher(msg).find()) {
            return Optional.empty();
        }
        if (SLACK_OR_CASUAL.matcher(msg).find()) {
            return Optional.of(replyByTime());
        }
        return Optional.empty();
    }
}
