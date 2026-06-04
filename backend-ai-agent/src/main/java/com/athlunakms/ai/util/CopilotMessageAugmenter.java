package com.athlunakms.ai.util;

import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

/** 将称呼、会话上下文与用户消息合并为 Agent 输入 */
public final class CopilotMessageAugmenter {

    private CopilotMessageAugmenter() {}

    public static String augmentUserMessage(
            String message, Integer userGender, Map<String, Object> sessionContext) {
        StringBuilder sb = new StringBuilder();
        String honorific = CopilotHonorificContext.resolveHonorific(userGender);
        if (honorific.equals("您")) {
            sb.append("[称呼：用户性别未设置，请用「您」称呼，语气亲切专业。]\n\n");
        } else {
            sb.append("[称呼：请始终用「").append(honorific).append("」称呼当前用户，语气亲切专业。]\n\n");
        }

        if (sessionContext != null && !sessionContext.isEmpty()) {
            sb.append("[会话上下文]\n");
            appendList(sb, "最近查到的红人 handles", sessionContext.get("lastHandles"));
            appendScalar(sb, "最近红人 handle（合并）", sessionContext.get("lastHandle"));
            appendList(sb, "最近查到的红人 IDs", sessionContext.get("lastInfluencerIds"));
            appendScalar(sb, "最近单个红人 ID", sessionContext.get("lastInfluencerId"));
            appendScalar(sb, "最近命中/选中人数", sessionContext.get("lastMatchCount"));
            appendScalar(sb, "最近操作类型", sessionContext.get("lastAction"));
            appendFilterSteps(sb, sessionContext.get("lastFilterSteps"));
            sb.append(
                    "说明：用户说「这个/这两个/上面/刚才」红人时，默认指上述上下文中的红人；"
                            + "多个红人时用 IDs 或 handles 批量操作。\n\n");
        }

        sb.append("[用户消息]\n").append(message == null ? "" : message.trim());
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void appendFilterSteps(StringBuilder sb, Object steps) {
        if (!(steps instanceof List<?> list) || list.isEmpty()) {
            return;
        }
        sb.append("- 最近筛选条件：");
        boolean first = true;
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                Object label = map.get("label");
                Object value = map.get("value");
                if (label != null && value != null) {
                    if (!first) sb.append("；");
                    sb.append(label).append("=").append(value);
                    first = false;
                }
            }
        }
        if (!first) sb.append("\n");
    }

    private static void appendScalar(StringBuilder sb, String label, Object value) {
        if (value == null || !StringUtils.hasText(String.valueOf(value))) {
            return;
        }
        sb.append("- ").append(label).append("：").append(value).append("\n");
    }

    private static void appendList(StringBuilder sb, String label, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof List<?> list && !list.isEmpty()) {
            sb.append("- ").append(label).append("：").append(String.join(", ", list.stream().map(String::valueOf).toList())).append("\n");
        }
    }
}
