package com.athlunakms.influencer.service;

import java.util.Map;
import org.springframework.util.StringUtils;

/**
 * 构建供 AI 向量化的红人画像文本（筛选维度尽量完整）
 */
public final class InfluencerVectorProfileBuilder {

    private InfluencerVectorProfileBuilder() {}

    public static String buildEmbedText(Map<String, Object> row) {
        StringBuilder sb = new StringBuilder();
        append(sb, "姓名", row.get("real_name"));
        append(sb, "昵称", row.get("nick_name"));
        append(sb, "状态", row.get("status"));
        append(sb, "阶段", row.get("stage"));
        append(sb, "默认平台", row.get("default_platform"));
        append(sb, "默认账号", row.get("default_handle"));
        append(sb, "主页", row.get("default_url"));
        append(sb, "国家", row.get("country"));
        append(sb, "语言", row.get("language"));
        append(sb, "人种", row.get("race"));
        append(sb, "性别", formatGender(row.get("gender")));
        append(sb, "类型", row.get("influencer_type"));
        append(sb, "品牌", row.get("brand"));
        append(sb, "来源", row.get("source"));
        append(sb, "来源值", row.get("source_value"));
        // tags 列为 JSON 标签 ID 数组，向量文本只用解析后的名称（tag_names）
        append(sb, "标签", row.get("tag_names"));
        append(sb, "粉丝数", row.get("total_fans"));
        append(sb, "订单数", row.get("total_orders"));
        append(sb, "已付费合作", formatBool(row.get("is_paid")));
        append(sb, "社媒账号", row.get("social_summary"));
        append(sb, "介绍", row.get("description"));
        return sb.toString().trim();
    }

    public static boolean isCooperating(Map<String, Object> row) {
        return "COOPERATING".equals(asString(row.get("status")));
    }

    private static void append(StringBuilder sb, String label, Object value) {
        String text = asString(value);
        if (!StringUtils.hasText(text)) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(label).append(':').append(text.trim());
    }

    private static String asString(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private static String formatGender(Object gender) {
        if (gender == null) {
            return "";
        }
        return switch (gender.toString()) {
            case "1" -> "男";
            case "2" -> "女";
            case "0" -> "未知";
            default -> gender.toString();
        };
    }

    private static String formatBool(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Boolean b) {
            return b ? "是" : "否";
        }
        return "1".equals(value.toString()) || "true".equalsIgnoreCase(value.toString()) ? "是" : "否";
    }
}
