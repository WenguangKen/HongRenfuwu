package com.athlunakms.ai.tool;

import com.athlunakms.ai.client.CopilotMailClient;
import com.athlunakms.ai.service.CopilotMailService;
import com.athlunakms.ai.service.CopilotSessionFocusService;
import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalMailAgentTools {

    private final CopilotMailClient mailClient;
    private final CopilotMailService mailService;
    private final CopilotSessionFocusService sessionFocus;
    private final CopilotUiActionPublisher uiActionPublisher;
    private final ObjectMapper objectMapper;

    @Tool("查询指定红人的邮件往来、最新合作意向与最近邮件摘要。用户问有没有邮件、合作意愿、回信情况时必须调用。"
            + "influencerId 与 handle 至少传一个；说「这个红人」时可只传 influencerId（结合上下文）。")
    @RateLimiter(name = "aiSearchLimiter")
    public String getInfluencerMailSummary(
            @ToolMemoryId String sessionId,
            Long influencerId,
            String handle) throws Exception {
        Map<String, Object> summary;
        if (influencerId != null) {
            summary = mailClient.getMailSummary(influencerId);
        } else if (StringUtils.hasText(handle)) {
            summary = mailClient.getMailSummaryByHandle(handle.replace("@", ""));
        } else {
            CopilotSessionFocusService.Focus focus = sessionFocus.get(sessionId);
            if (focus == null) {
                return "请提供红人 ID 或 handle，或先告诉我查哪位红人。";
            }
            summary = mailClient.getMailSummary(focus.influencerId());
        }
        if (Boolean.TRUE.equals(summary.get("found")) && summary.get("influencerId") != null) {
            publishMailHub(sessionId, summary);
        }
        return mailService.buildMailSummaryReply(summary);
    }

    @Tool("列出系统内可用的邮件模版（建联/邀约用）。用户问有哪些模版、用哪个模版建联时调用。")
    @RateLimiter(name = "aiSearchLimiter")
    public String listMailTemplates() throws Exception {
        List<Map<String, Object>> templates = mailClient.listTemplates();
        if (templates.isEmpty()) {
            return "当前没有邮件模版，请先到【邮件管理 → 模版设置】创建。";
        }
        return objectMapper.writeValueAsString(templates);
    }

    @Tool(
            "打开邀约邮件编写页，预选红人供用户确认后发送。"
                    + "用户说发邀约/建联邮件但未要求立即发送时优先调用。"
                    + "influencerIds 为红人 ID 列表，逗号分隔，如 \"23289,23290\"；可结合会话上下文。")
    @RateLimiter(name = "aiSearchLimiter")
    public String openInviteMailComposer(@ToolMemoryId String sessionId, String influencerIds) throws Exception {
        if (!StringUtils.hasText(influencerIds)) {
            return "请提供至少一位红人 ID（influencerIds），或结合 [会话上下文] 中的 lastInfluencerIds。";
        }
        String json = CopilotUiActionBuilder.buildOpenMailCampaignCreate(influencerIds.trim());
        uiActionPublisher.publish(sessionId, json);
        return "【系统】已打开邀约邮件编写页，红人已预选；请告知用户在页面确认模版后发送。";
    }

    @Tool(
            "为一批红人发起 AI 个性化建联邮件（真实进入发送队列）。"
                    + "templateVersionId 或 templateName 二选一；productIds 为商品库 ID，逗号分隔。"
                    + "系统会自动：① 去掉该红人已寄样/已推广过的重复商品；② 若指定商品后该红人无剩余可推商品则跳过该红人。"
                    + "influencerIds 为红人 ID 列表，逗号分隔字符串，如 \"23289,23290\"。")
    @RateLimiter(name = "aiSearchLimiter")
    public String createInviteOutreachCampaign(
            @ToolMemoryId String sessionId,
            String influencerIds,
            Long templateVersionId,
            String templateName,
            String productIds,
            String aiBrief,
            String campaignName) throws Exception {
        List<Long> ids = parseIds(influencerIds);
        if (ids.isEmpty()) {
            return "请提供至少一位红人 ID（influencerIds）。";
        }
        Map<String, Object> body = new HashMap<>();
        body.put("influencerIds", ids);
        if (templateVersionId != null) {
            body.put("templateVersionId", templateVersionId);
        }
        if (StringUtils.hasText(templateName)) {
            body.put("templateName", templateName);
        }
        if (StringUtils.hasText(productIds)) {
            body.put("productIds", productIds);
        }
        body.put("aiBrief", StringUtils.hasText(aiBrief) ? aiBrief : "品牌合作邀约：语气像真人 BD 私信，点出为什么找这位红人，突出产品卖点与佣金/寄样，避免模版套话。");
        if (StringUtils.hasText(campaignName)) {
            body.put("campaignName", campaignName);
        }
        Map<String, Object> result = mailClient.createInvite(body);
        try {
            String json = CopilotUiActionBuilder.buildNavigatePage("mail_hub");
            uiActionPublisher.publish(sessionId, json);
        } catch (Exception e) {
            log.warn("navigate mail_hub failed", e);
        }
        return buildInviteResultText(result);
    }

    private void publishMailHub(String sessionId, Map<String, Object> summary) {
        try {
            Long id = Long.valueOf(summary.get("influencerId").toString());
            String handle = summary.get("handle") != null ? summary.get("handle").toString() : null;
            uiActionPublisher.publish(sessionId, CopilotUiActionBuilder.buildOpenMailHub(id, handle));
        } catch (Exception e) {
            log.warn("open mail hub ui action failed", e);
        }
    }

    private static List<Long> parseIds(String csv) {
        if (!StringUtils.hasText(csv)) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        for (String part : csv.split("[,，;\\s]+")) {
            if (part.isBlank()) {
                continue;
            }
            try {
                ids.add(Long.parseLong(part.trim()));
            } catch (NumberFormatException ignored) {
                /* skip */
            }
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    private String buildInviteResultText(Map<String, Object> result) {
        int queued = result.get("queuedCount") instanceof Number n ? n.intValue() : 0;
        StringBuilder sb = new StringBuilder();
        sb.append("建联任务已提交：成功排队 **").append(queued).append("** 位红人。\n");
        Object skipped = result.get("skipped");
        if (skipped instanceof List<?> list && !list.isEmpty()) {
            sb.append("跳过 ").append(list.size()).append(" 位：\n");
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    sb.append("- 红人 ").append(m.get("influencerId")).append("：")
                            .append(m.get("reason")).append('\n');
                }
            }
        }
        Object adj = result.get("productAdjustments");
        if (adj instanceof List<?> list && !list.isEmpty()) {
            sb.append("商品去重：\n");
            for (Object o : list) {
                if (o instanceof Map<?, ?> m) {
                    sb.append("- 红人 ").append(m.get("influencerId"))
                            .append(" 已去掉重复商品 ").append(m.get("removedProductIds")).append('\n');
                }
            }
        }
        Object campaignIds = result.get("campaignIds");
        if (campaignIds instanceof List<?> list && !list.isEmpty()) {
            sb.append("活动 ID：").append(list.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return sb.toString();
    }
}
