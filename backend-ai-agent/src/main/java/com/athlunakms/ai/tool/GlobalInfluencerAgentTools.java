package com.athlunakms.ai.tool;

import com.athlunakms.ai.auth.InnerUserContext;
import com.athlunakms.ai.auth.InnerUserContextHolder;
import com.athlunakms.ai.service.CopilotInfluencerWorkflowService;
import com.athlunakms.ai.service.CopilotOrderSearchService;
import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import com.athlunakms.ai.util.PgVectorUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalInfluencerAgentTools {

    private final NamedParameterJdbcTemplate pgJdbcTemplate;
    private final EmbeddingModel embeddingModel;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CopilotUiActionPublisher uiActionPublisher;
    private final CopilotOrderSearchService orderSearchService;
    private final CopilotInfluencerWorkflowService workflowService;

    @Value("${services.backend-influencer.url}")
    private String influencerServiceUrl;

    @Tool("按社媒账号 handle 查找红人。仅当用户明确查红人/达人/账号是否存在时调用；用户查订单、单号、样品单、转化单时不要调用本工具。")
    @RateLimiter(name = "aiSearchLimiter")
    public String lookupInfluencerByHandle(@ToolMemoryId String sessionId, String handle) throws Exception {
        if (!StringUtils.hasText(handle)) {
            return "请提供要查找的 handle，例如 shaylabelle40";
        }
        String normalized = handle.trim().replace("@", "");
        String url =
                influencerServiceUrl
                        + "/internal/api/influencers/search-by-handle?handle="
                        + URLEncoder.encode(normalized, StandardCharsets.UTF_8);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = restTemplate.getForObject(url, List.class);
        int count = rows == null ? 0 : rows.size();
        Long singleId = null;
        String singleStatus = null;
        if (count == 1 && rows.get(0).get("id") != null) {
            singleId = Long.valueOf(rows.get(0).get("id").toString());
            if (rows.get(0).get("status") != null) {
                singleStatus = rows.get(0).get("status").toString();
            }
        }
        publishHandleUiAction(sessionId, normalized, count, singleId, singleStatus);
        if (count == 0) {
            return "【查询结果】数据库中未找到 handle 为「" + normalized + "」的红人。";
        }
        return objectMapper.writeValueAsString(rows);
    }

    @Tool("寻找并检索跨境红人列表。支持按平台、是否合作、国家、业务状态筛选，并用自然语言描述红人画像（如平台账号、品类、粉丝量级、国家、合作状态等）。")
    @RateLimiter(name = "aiSearchLimiter")
    @CircuitBreaker(name = "doubaoApiBreaker", fallbackMethod = "searchFallback")
    public String searchInfluencers(
            @ToolMemoryId String sessionId,
            String platform,
            Boolean isCooperated,
            String country,
            String status,
            String semanticQuery
    ) throws Exception {
        InnerUserContext user = InnerUserContextHolder.get();
        if (user != null && "STAFF".equals(user.getRole()) && semanticQuery != null && semanticQuery.contains("联系方式")) {
            return "【风控拦截】普通员工无权限检索商务红人敏感联系方式。";
        }

        if (!StringUtils.hasText(semanticQuery)) {
            return "请提供检索描述，例如：美国美妆类未合作红人";
        }

        float[] queryVector = embeddingModel.embed(semanticQuery).content().vector();
        String vectorLiteral = PgVectorUtils.toVectorLiteral(queryVector);

        String vectorSql = "SELECT influencer_id FROM ai_influencer_index " +
                "WHERE (:platform IS NULL OR platform = :platform) " +
                "AND (:isCooperated IS NULL OR is_cooperated = :isCooperated) " +
                "AND (:country IS NULL OR country = :country) " +
                "AND (:status IS NULL OR status = :status) " +
                "ORDER BY embedding <=> cast(:queryVector as vector) LIMIT 20";

        String normalizedStatus = normalizeStatus(status, isCooperated);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("platform", StringUtils.hasText(platform) ? platform : null)
                .addValue("isCooperated", isCooperated)
                .addValue("country", StringUtils.hasText(country) ? country : null)
                .addValue("status", normalizedStatus)
                .addValue("queryVector", vectorLiteral);

        List<Long> hitIds = pgJdbcTemplate.queryForList(vectorSql, params, Long.class);
        if (hitIds.isEmpty()) {
            publishUiAction(sessionId, platform, country, status, isCooperated, semanticQuery, 0);
            return "未检索到相关红人，向量库可能仍在同步中，请稍后再试。";
        }

        String targetUrl = influencerServiceUrl + "/internal/api/influencers/batch-details";
        List<?> results = restTemplate.postForObject(targetUrl, hitIds, List.class);
        int count = results != null ? results.size() : hitIds.size();
        publishUiAction(sessionId, platform, country, status, isCooperated, semanticQuery, count);
        return objectMapper.writeValueAsString(results);
    }

    /** 将模型传入的中文状态转为库内枚举（DORMANT / COOPERATING 等） */
    private String normalizeStatus(String status, Boolean isCooperated) {
        if (Boolean.TRUE.equals(isCooperated)) {
            return "COOPERATING";
        }
        if (!StringUtils.hasText(status)) {
            return null;
        }
        String s = status.trim();
        return switch (s) {
            case "休眠", "休眠中" -> "DORMANT";
            case "合作中", "已合作", "合作" -> "COOPERATING";
            case "待联系" -> "PENDING";
            case "已联系" -> "CONTACTED";
            case "沟通中" -> "COMMUNICATING";
            case "暂不合作" -> "PAUSED";
            case "黑名单" -> "BLACKLIST";
            case "不再合作" -> "TERMINATED";
            default -> s.toUpperCase();
        };
    }

    @Tool("关闭当前打开的红人详情弹窗。用户说关闭/收起/退出详情页时必须调用。")
    public String closeInfluencerDetail(@ToolMemoryId String sessionId) throws Exception {
        Map<String, Object> action = new java.util.LinkedHashMap<>();
        action.put("name", "CloseInfluencerDetail");
        action.put("data", Map.of("summary", "已关闭红人详情页。"));
        uiActionPublisher.publish(sessionId, objectMapper.writeValueAsString(action));
        return "【系统】已通知前端关闭红人详情页，请告知用户可继续在列表操作。";
    }

    @Tool("打开指定红人的详情弹窗。用户明确要求查看/打开某位红人详情时调用；至少传 influencerId 或 handle 之一。")
    @RateLimiter(name = "aiSearchLimiter")
    public String openInfluencerDetail(
            @ToolMemoryId String sessionId, Long influencerId, String handle) throws Exception {
        if (influencerId == null && !StringUtils.hasText(handle)) {
            return "请提供红人 ID 或 handle，或结合会话上下文中的红人。";
        }
        String json = CopilotUiActionBuilder.buildOpenInfluencerDetail(influencerId, handle);
        uiActionPublisher.publish(sessionId, json);
        return "【系统】已通知前端打开红人详情，请告知用户查看详情弹窗。";
    }

    @Tool(
            "查询订单是否存在并打开对应订单页筛选；可回答订单绑定了哪个红人。"
                    + "用户问单号、订单、和哪个红人绑定、关联红人、样品单、红人订单、转化订单时必须调用。"
                    + "orderType: sample=红人订单/样品单(默认)、conversion=转化订单、commission=分佣订单。"
                    + "至少传 orderNo 或 eccangOrderId 或 influencerName 或 discountCode 之一。")
    @RateLimiter(name = "aiSearchLimiter")
    public String searchOrders(
            @ToolMemoryId String sessionId,
            String orderType,
            String orderNo,
            String eccangOrderId,
            String influencerName,
            String discountCode)
            throws Exception {
        CopilotOrderSearchService.OrderSearchResult result =
                orderSearchService.search(sessionId, orderType, orderNo, eccangOrderId, influencerName, discountCode);
        return orderSearchService.buildToolReply(result);
    }

    @Tool(
            "跳转系统页面。page 可传英文 key 或中文名，例如："
                    + "content_pending/待上传、content_library/内容库、"
                    + "influencer_list/红人列表、influencer_pool/资源池、mail_hub/红人邮件、"
                    + "mail_templates/模版设置、mail_campaigns/批量发信、"
                    + "order_sample/红人订单、order_conversion/转化订单、"
                    + "commission_dist/分佣列表、commission_pay/打款列表、commission_order/分佣订单、"
                    + "product_list/商品列表、finance_remittance/汇款管理、"
                    + "dashboard/仪表盘，以及 system_user/用户管理 等系统设置子页。")
    public String navigateSystemPage(@ToolMemoryId String sessionId, String page) throws Exception {
        String json = CopilotUiActionBuilder.buildNavigatePage(page);
        uiActionPublisher.publish(sessionId, json);
        return "【系统】已通知前端跳转到指定页面，请告知用户查看左侧页面。";
    }

    private void publishHandleUiAction(String sessionId, String handle, int count, Long singleId) {
        publishHandleUiAction(sessionId, handle, count, singleId, null);
    }

    private void publishHandleUiAction(
            String sessionId, String handle, int count, Long singleId, String currentStatus) {
        try {
            String json = CopilotUiActionBuilder.buildApplyInfluencerFilterByHandle(
                    handle, count, singleId, currentStatus);
            uiActionPublisher.publish(sessionId, json);
        } catch (Exception e) {
            log.warn("生成 handle 联动指令失败", e);
        }
    }

    @Tool(
            "将单个或多个红人状态流转到目标状态（待联系/已联系/沟通中/合作中/休眠/暂不合作等）。"
                    + "influencerIds 为红人 ID 逗号分隔；或传 handle；或用户说「这个红人」时结合上下文。"
                    + "targetStatus 用中文或英文枚举，如：已联系、CONTACTED、沟通中。")
    @RateLimiter(name = "aiSearchLimiter")
    public String transitionInfluencerStatus(
            @ToolMemoryId String sessionId,
            String influencerIds,
            String handle,
            String targetStatus,
            String reason)
            throws Exception {
        return workflowService.transitionStatus(sessionId, influencerIds, handle, targetStatus, reason);
    }

    private void publishUiAction(
            String sessionId,
            String platform,
            String country,
            String status,
            Boolean isCooperated,
            String semanticQuery,
            int count
    ) {
        try {
            String json = CopilotUiActionBuilder.buildApplyInfluencerFilter(
                    platform, country, status, isCooperated, semanticQuery, count);
            uiActionPublisher.publish(sessionId, json);
        } catch (Exception e) {
            log.warn("生成页面联动指令失败", e);
        }
    }

    public String searchFallback(String sessionId, String platform, Boolean isCooperated, String country, String status, String semanticQuery, Throwable t) {
        log.error("AI 检索服务熔断，执行降级", t);
        return "【系统自动降级】AI 检索服务波动，请稍后再试。";
    }

    @Tool(
            "导出红人列表为 Excel。scope: filtered=当前筛选结果(默认)、all=全部、selected=已勾选。"
                    + "用户说导出红人/达人/列表时调用。")
    public String exportFilteredInfluencers(@ToolMemoryId String sessionId, String scope) throws Exception {
        InnerUserContext user = InnerUserContextHolder.get();
        if (user == null || (!"ADMIN".equals(user.getRole()) && !"BUSINESS".equals(user.getRole()))) {
            return "【权限不足】您不具备红人导出权限，请联系管理员。";
        }
        String json = CopilotUiActionBuilder.buildOpenExportModal("influencer", scope);
        uiActionPublisher.publish(sessionId, json);
        return "【系统】已通知前端在【红人列表】打开导出窗口，请告知用户在左侧选择字段并确认导出。";
    }

    @Tool(
            "导出订单列表为 Excel。orderType: sample=红人订单/样品单(默认)、conversion=转化订单。"
                    + "scope: filtered=当前筛选(默认)、all=全部、selected=已勾选。用户说导出订单时调用。")
    public String exportOrders(@ToolMemoryId String sessionId, String orderType, String scope) throws Exception {
        String target =
                orderType != null && orderType.toLowerCase().contains("conversion") ? "order_conversion" : "order_sample";
        String json = CopilotUiActionBuilder.buildOpenExportModal(target, scope);
        uiActionPublisher.publish(sessionId, json);
        String label = "order_conversion".equals(target) ? "转化订单" : "红人订单（样品）";
        return "【系统】已通知前端在【" + label + "】打开导出窗口，请告知用户选择字段并确认导出。";
    }

    /** 兼容旧工具名 */
    @Tool("批量导出检索到的红人详细业务数据（同 exportFilteredInfluencers）")
    public String exportInfluencerContacts(@ToolMemoryId String sessionId, String scope) throws Exception {
        return exportFilteredInfluencers(sessionId, scope);
    }
}
