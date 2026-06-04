package com.athlunakms.ai.service;

import com.athlunakms.ai.client.CopilotMailClient;
import com.athlunakms.ai.util.CopilotHandleResolver;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CopilotMailService {

    private static final Pattern NAME_IN_MSG = Pattern.compile(
            "(?:名叫|叫|红人)\\s*([A-Za-z0-9\\u4e00-\\u9fa5][A-Za-z0-9\\u4e00-\\u9fa5 ._-]{1,48})",
            Pattern.CASE_INSENSITIVE);

    private final CopilotMailClient mailClient;
    private final CopilotHandleResolver handleResolver;
    private final CopilotSessionFocusService sessionFocus;

    public static boolean mailRelatedQuestion(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        return message.matches(".*(邮件|mail|建联|往来|回信|收件|发信|模版|模板|合作意愿|意向|跟进邮件|邀约).*");
    }

    public static boolean refersToCurrentInfluencer(String message) {
        return message != null && message.matches(".*(这个|该|此|上面|刚才|刚刚).*(红人|达人|kol|influencer|他|她).*");
    }

    public Optional<Map<String, Object>> resolveMailSummary(String sessionId, String message) {
        Optional<String> handle = handleResolver.parseHandle(message);
        if (handle.isPresent()) {
            Map<String, Object> summary = mailClient.getMailSummaryByHandle(handle.get());
            if (Boolean.TRUE.equals(summary.get("found"))) {
                return Optional.of(summary);
            }
        }
        Optional<String> name = parseName(message);
        if (name.isPresent()) {
            // 通过 handle 搜索失败时，mail 服务按名查需 influencer id — 先走 handle 全名匹配
        }
        if (refersToCurrentInfluencer(message)) {
            CopilotSessionFocusService.Focus focus = sessionFocus.get(sessionId);
            if (focus != null && focus.influencerId() != null) {
                return Optional.of(mailClient.getMailSummary(focus.influencerId()));
            }
        }
        return Optional.empty();
    }

    public String buildMailSummaryReply(Map<String, Object> summary) {
        if (!Boolean.TRUE.equals(summary.get("found"))) {
            return "未找到对应红人，无法查询邮件往来。请提供 handle 或红人姓名。";
        }
        String name = String.valueOf(summary.getOrDefault("displayName", "该红人"));
        String handle = summary.get("handle") != null ? "@" + summary.get("handle") : "";
        long out = toLong(summary.get("outboundCount"));
        long in = toLong(summary.get("inboundCount"));
        boolean active = Boolean.TRUE.equals(summary.get("hasMailActivity")) || out + in > 0;

        StringBuilder sb = new StringBuilder();
        sb.append(name).append(handle.isEmpty() ? "" : " (" + handle + ")").append("：\n");
        if (!active) {
            sb.append("· 邮件往来：**暂无**（系统中未记录发出或收到的邮件）\n");
            sb.append("· 建议：可在红人列表勾选后使用「批量发邮件」或在对话中让我用指定模版发起建联。");
            return sb.toString();
        }
        sb.append("· 邮件往来：**有**（发出 ").append(out).append(" 封，收到 ").append(in).append(" 封）\n");

        @SuppressWarnings("unchecked")
        Map<String, Object> intent = summary.get("latestIntent") instanceof Map<?, ?> m
                ? (Map<String, Object>) m : Map.of();
        if (!intent.isEmpty()) {
            sb.append("· 最新合作意向：")
                    .append(labelZh(String.valueOf(intent.getOrDefault("intentLabel", ""))))
                    .append(intent.get("intentScore") != null ? " · " + intent.get("intentScore") + "分" : "")
                    .append('\n');
            if (intent.get("summaryZh") != null) {
                sb.append("  摘要：").append(intent.get("summaryZh")).append('\n');
            }
            if (intent.get("suggestedActionZh") != null) {
                sb.append("  建议：").append(intent.get("suggestedActionZh")).append('\n');
            }
        } else if (in > 0) {
            sb.append("· 合作意向：红人已有回复，但尚未完成 AI 意向分析，可到【红人邮件】查看或让我帮你分析。\n");
        } else {
            sb.append("· 合作意向：已发出邮件，**尚未收到回复**。\n");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> recent = summary.get("recentMessages") instanceof List<?> l
                ? (List<Map<String, Object>>) l : List.of();
        if (!recent.isEmpty()) {
            sb.append("· 最近往来：\n");
            for (Map<String, Object> m : recent) {
                String dir = "IN".equals(String.valueOf(m.get("direction"))) ? "收" : "发";
                sb.append("  - ").append(dir).append("：")
                        .append(m.getOrDefault("subject", "(无主题)"));
                if (m.get("snippet") != null) {
                    sb.append(" — ").append(m.get("snippet"));
                }
                sb.append('\n');
            }
        }
        sb.append("· 如需建联/回复，可直接告诉我用哪个邮件模版、推广哪些商品。");
        return sb.toString();
    }

    private static Optional<String> parseName(String message) {
        Matcher m = NAME_IN_MSG.matcher(message);
        if (m.find()) {
            String n = m.group(1).trim();
            if (!n.isEmpty() && !n.matches("(?i)(红人|达人|kol)")) {
                return Optional.of(n);
            }
        }
        return Optional.empty();
    }

    private static long toLong(Object v) {
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return v != null ? Long.parseLong(v.toString()) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private static String labelZh(String label) {
        return switch (label) {
            case "STRONG" -> "合作意向强";
            case "MEDIUM" -> "意向一般";
            case "WEAK" -> "意向较弱";
            case "REJECT" -> "明确拒绝";
            case "UNCLEAR" -> "待判断";
            default -> StringUtils.hasText(label) ? label : "暂无分析";
        };
    }
}
