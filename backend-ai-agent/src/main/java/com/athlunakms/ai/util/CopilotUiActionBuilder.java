package com.athlunakms.ai.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class CopilotUiActionBuilder {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private CopilotUiActionBuilder() {}

    public static String buildApplyInfluencerFilter(
            String platform,
            String country,
            String status,
            Boolean isCooperated,
            String semanticQuery,
            int resultCount
    ) throws JsonProcessingException {
        String tab = resolveStatusTab(status, isCooperated);
        String label = tabLabel(tab);
        CopilotFanRangeParser.FanRange fan = CopilotFanRangeParser.parse(semanticQuery);
        String fanDesc = CopilotFanRangeParser.label(fan);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("statusTab", tab);
        if (StringUtils.hasText(platform)) {
            data.put("platform", platform);
        }
        if (StringUtils.hasText(country)) {
            data.put("country", country);
        }
        if (fan.fansMin() != null) {
            data.put("fansMin", fan.fansMin());
        }
        if (fan.fansMax() != null) {
            data.put("fansMax", fan.fansMax());
        }
        boolean broadQuery =
                StringUtils.hasText(semanticQuery)
                        && semanticQuery.matches(".*(有多少|多少|以下|以上|粉丝|筛选|查询|红人|达人).*");
        if (StringUtils.hasText(semanticQuery) && !broadQuery) {
            data.put("searchKey", semanticQuery);
        }
        String cond = StringUtils.hasText(fanDesc) ? label + " · " + fanDesc : label;
        String summary;
        if (resultCount > 0) {
            summary =
                    String.format(
                            "已为您在【红人列表】应用筛选：%s，语义检索约 %d 位，请在左侧表格查看。",
                            cond, resultCount);
        } else if (fan.fansMin() != null || fan.fansMax() != null) {
            summary =
                    String.format(
                            "已为您在【红人列表】应用筛选：%s。请在左侧表格查看符合粉丝条件的红人数量。",
                            cond);
        } else {
            summary =
                    String.format(
                            "已在【红人列表】切换到「%s」，当前向量库暂无匹配，请稍后再试或调整条件。",
                            label);
        }
        data.put("summary", summary);
        data.put("resultCount", resultCount);

        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "ApplyInfluencerFilter");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    /** 状态流转后刷新红人列表到对应 Tab */
    public static String buildSwitchInfluencerStatusTab(String statusTab, int updatedCount)
            throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("statusTab", statusTab);
        data.put("summary", String.format("已更新 %d 位红人状态，列表已切换到「%s」。", updatedCount, tabLabel(statusTab)));
        data.put("resultCount", updatedCount);
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "ApplyInfluencerFilter");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    public static String buildApplyInfluencerFilterByHandle(String handle, int matchCount, Long singleId)
            throws JsonProcessingException {
        return buildApplyInfluencerFilterByHandle(handle, matchCount, singleId, null);
    }

    public static String buildApplyInfluencerFilterByHandle(
            String handle, int matchCount, Long singleId, String currentStatus)
            throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("statusTab", "all");
        data.put("socialHandle", handle);
        if (singleId != null) {
            data.put("influencerId", singleId);
        }
        if (StringUtils.hasText(currentStatus)) {
            data.put("currentStatus", currentStatus);
        }
        String statusNote =
                StringUtils.hasText(currentStatus)
                        ? "，当前状态「" + enumStatusLabel(currentStatus) + "」"
                        : "";
        data.put(
                "summary",
                matchCount == 1
                        ? String.format(
                                "已找到社媒账号「%s」%s，已在【红人列表】按账号筛选，请在左侧表格查看。",
                                handle, statusNote)
                        : matchCount > 1
                                ? String.format(
                                        "找到 %d 位社媒账号含「%s」的红人，已在列表按【社媒账号】筛选。",
                                        matchCount, handle)
                                : String.format(
                                        "库内未精确命中「%s」，已在列表按【社媒账号】=%s 筛选，请查看表格。",
                                        handle, handle));
        data.put("resultCount", matchCount);

        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "ApplyInfluencerFilter");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    public static String buildApplyOrderFilter(
            String pagePath,
            String orderNo,
            String eccangOrderId,
            String influencerName,
            String discountCode,
            int resultCount)
            throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("page", pagePath);
        if (StringUtils.hasText(orderNo)) {
            data.put("orderNo", orderNo.trim());
        }
        if (StringUtils.hasText(eccangOrderId)) {
            data.put("eccangOrderId", eccangOrderId.trim());
        }
        if (StringUtils.hasText(influencerName)) {
            data.put("influencer", influencerName.trim());
        }
        if (StringUtils.hasText(discountCode)) {
            data.put("discountCode", discountCode.trim());
        }
        data.put("resultCount", resultCount);
        String label = pageLabel(pagePath);
        data.put(
                "summary",
                resultCount > 0
                        ? String.format("已在【%s】筛选到 %d 笔订单，请在左侧表格查看。", label, resultCount)
                        : String.format("已在【%s】按条件筛选，当前未命中订单。", label));

        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "ApplyOrderFilter");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    public static String buildOpenExportModal(String target, String scope) throws JsonProcessingException {
        String normalized =
                switch (target == null ? "" : target.trim().toLowerCase()) {
                    case "order_conversion", "conversion", "转化订单", "转化单" -> "order_conversion";
                    case "order_sample", "sample", "样品", "红人订单", "样品单" -> "order_sample";
                    default -> "influencer";
                };
        String scopeNorm =
                scope != null && scope.matches("(?i)all|selected") ? scope.toLowerCase() : "filtered";
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("target", normalized);
        data.put("scope", scopeNorm);
        data.put("summary", "已为您打开「" + exportTargetLabel(normalized) + "」导出窗口。");
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "OpenExportModal");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    private static String exportTargetLabel(String target) {
        return switch (target) {
            case "order_conversion" -> "转化订单";
            case "order_sample" -> "红人订单（样品）";
            default -> "红人列表";
        };
    }

    public static String buildNavigatePage(String pageKey) throws JsonProcessingException {
        String path = resolveNavigatePath(pageKey);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("path", path);
        data.put("summary", "已为您跳转到「" + pageLabel(path) + "」页面。");
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "NavigateToPage");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    /** 解析 page 参数为前端路由 path */
    static String resolveNavigatePath(String pageKey) {
        if (pageKey == null) {
            return "/influencer/list";
        }
        String key = pageKey.trim().toLowerCase();
        // 已是完整 path
        if (key.startsWith("/")) {
            return pageKey.trim();
        }
        return switch (key) {
            case "dashboard", "首页", "仪表盘" -> "/dashboard";
            case "influencer_list", "influencer list", "红人列表", "列表" -> "/influencer/list";
            case "influencer_pool", "pool", "资源池" -> "/influencer/pool";
            case "influencer_copilot", "copilot", "工作台", "小a", "智能检索" -> "/influencer/copilot";
            case "order_conversion", "conversion", "转化订单" -> "/order/conversion";
            case "order_sample", "sample", "样品订单", "红人订单" -> "/order/sample";
            case "commission_dist", "分佣列表", "分佣分配" -> "/commission/dist";
            case "commission_pay", "打款列表", "分佣打款", "打款" -> "/commission/pay";
            case "commission_order", "分佣订单" -> "/commission/order";
            case "product_list", "product", "商品列表", "商品管理" -> "/product/list";
            case "content_pending", "content.pending", "待上传", "待上传页面" -> "/content/pending";
            case "content_library", "content.library", "内容库" -> "/content/library";
            case "finance_remittance", "汇款", "汇款管理", "财务" -> "/finance/remittance";
            case "mail_hub", "mail", "邮件", "红人邮件", "邮件中心" -> "/mail/hub";
            case "mail_templates", "邮件模版", "邮件模板", "模版设置" -> "/mail/templates";
            case "mail_campaigns", "批量发信", "邮件活动" -> "/mail/campaigns";
            case "system_user", "用户管理" -> "/system/user";
            case "system_role", "角色管理" -> "/system/role";
            case "system_tag", "标签管理" -> "/system/tag";
            case "system_rule", "规则设置", "规则管理" -> "/system/rule";
            case "system_permission", "权限管理", "权限列表" -> "/system/permission";
            case "system_eccang", "eccang", "店铺设置" -> "/system/eccang";
            case "system_webhook", "webhook" -> "/system/webhook";
            case "system_storage", "存储配置", "存储管理" -> "/system/storage";
            default -> "/influencer/list";
        };
    }

    private static String pageLabel(String path) {
        return switch (path) {
            case "/dashboard" -> "仪表盘";
            case "/influencer/pool" -> "资源池";
            case "/influencer/copilot" -> "小A 智能检索";
            case "/order/conversion" -> "转化订单";
            case "/order/sample" -> "红人订单";
            case "/commission/dist" -> "分佣列表";
            case "/commission/pay" -> "打款列表";
            case "/commission/order" -> "分佣订单";
            case "/product/list" -> "商品列表";
            case "/content/pending" -> "待上传";
            case "/content/library" -> "内容库";
            case "/finance/remittance" -> "汇款管理";
            case "/mail/hub" -> "红人邮件";
            case "/mail/templates" -> "模版设置";
            case "/mail/campaigns" -> "批量发信";
            case "/system/user" -> "用户管理";
            case "/system/role" -> "角色管理";
            case "/system/tag" -> "标签管理";
            case "/system/rule" -> "规则设置";
            case "/system/permission" -> "权限列表";
            case "/system/eccang" -> "Eccang 设置";
            case "/system/webhook" -> "Webhook 设置";
            case "/system/storage" -> "存储配置";
            default -> "红人列表";
        };
    }

    /** 打开红人邮件页并定位到指定红人 */
    public static String buildOpenMailHub(Long influencerId, String handle) throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("path", "/mail/hub");
        if (influencerId != null) {
            data.put("influencerId", influencerId);
        }
        if (StringUtils.hasText(handle)) {
            data.put("socialHandle", handle);
        }
        data.put("summary", "已为您打开【红人邮件】并定位到该红人往来记录。");
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "NavigateToPage");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    /** 打开邀约邮件编写页，预选红人 */
    public static String buildOpenMailCampaignCreate(String influencerIds) throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("path", "/mail/campaigns/create");
        data.put("ids", influencerIds.trim());
        data.put("from", "invite");
        data.put("ai", "1");
        data.put("summary", "已为您打开邀约邮件编写页，红人已预选。");
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "NavigateToPage");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    /** 打开红人详情弹窗 */
    public static String buildOpenInfluencerDetail(Long influencerId, String handle)
            throws JsonProcessingException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("forceOpen", true);
        if (influencerId != null) {
            data.put("influencerId", influencerId);
        }
        if (StringUtils.hasText(handle)) {
            data.put("socialHandle", handle.replace("@", ""));
        }
        data.put("summary", "正在打开红人详情页…");
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("name", "OpenInfluencerDetail");
        action.put("data", data);
        return MAPPER.writeValueAsString(action);
    }

    public static String buildApplyInfluencerFilterFromToolCall(String fcBlock) throws JsonProcessingException {
        String platform = extractJsonString(fcBlock, "platform");
        String country = extractJsonString(fcBlock, "country");
        String status = extractJsonString(fcBlock, "status");
        String semanticQuery = extractJsonString(fcBlock, "semanticQuery");
        if (!StringUtils.hasText(semanticQuery)) {
            semanticQuery = extractJsonString(fcBlock, "query");
        }
        Boolean isCooperated = extractJsonBoolean(fcBlock, "isCooperated");
        return buildApplyInfluencerFilter(platform, country, status, isCooperated, semanticQuery, -1);
    }

    private static String resolveStatusTab(String status, Boolean isCooperated) {
        if (Boolean.TRUE.equals(isCooperated)) {
            return "cooperating";
        }
        if (!StringUtils.hasText(status)) {
            return "all";
        }
        String s = status.trim();
        return switch (s) {
            case "休眠", "休眠中", "DORMANT" -> "dormant";
            case "合作中", "已合作", "合作", "COOPERATING" -> "cooperating";
            case "待联系", "PENDING" -> "pending";
            case "已联系", "CONTACTED" -> "contacted";
            case "沟通中", "COMMUNICATING" -> "communicating";
            case "暂不合作", "PAUSED" -> "paused";
            case "黑名单", "BLACKLIST" -> "blacklist";
            case "不再合作", "TERMINATED" -> "terminated";
            default -> "all";
        };
    }

    private static String tabLabel(String tab) {
        return switch (tab) {
            case "dormant" -> "休眠中";
            case "cooperating" -> "合作中";
            case "pending" -> "待联系";
            case "contacted" -> "已联系";
            case "communicating" -> "沟通中";
            case "paused" -> "暂不合作";
            case "blacklist" -> "黑名单";
            case "terminated" -> "不再合作";
            default -> "全部";
        };
    }

    private static String enumStatusLabel(String status) {
        return switch (status.trim().toUpperCase()) {
            case "PENDING" -> "待联系";
            case "CONTACTED" -> "已联系";
            case "COMMUNICATING" -> "沟通中";
            case "COOPERATING" -> "合作中";
            case "DORMANT" -> "休眠中";
            case "PAUSED" -> "暂不合作";
            case "BLACKLIST" -> "黑名单";
            case "TERMINATED" -> "不再合作";
            case "UNSCREENED" -> "待筛选";
            case "REJECTED" -> "暂不合适";
            default -> status;
        };
    }

    private static String extractJsonString(String block, String key) {
        java.util.regex.Pattern p =
                java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(block);
        return m.find() ? m.group(1) : null;
    }

    private static Boolean extractJsonBoolean(String block, String key) {
        java.util.regex.Pattern p =
                java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)", java.util.regex.Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher m = p.matcher(block);
        if (!m.find()) {
            return null;
        }
        return Boolean.parseBoolean(m.group(1));
    }
}
