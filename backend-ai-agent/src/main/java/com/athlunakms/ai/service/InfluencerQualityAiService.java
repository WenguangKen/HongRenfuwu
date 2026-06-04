package com.athlunakms.ai.service;

import com.athlunakms.ai.dto.influencer.InfluencerQualityAnalyzeRequest;
import com.athlunakms.ai.dto.influencer.InfluencerQualityAnalyzeResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluencerQualityAiService {

    private static final Pattern JSON_BLOCK = Pattern.compile("\\{[\\s\\S]*}");

    private static final String BRIEF_RULES = """
            === 业务术语（必须遵守）===
            - 样品单 = 合作订单 = 寄样记录，表示已向红人发过货/已进入样品合作，不是「付费合作」。
            - 「已付费合作/is_paid」仅指付稿费/付费坑位，与样品单无关；is_paid=否 不代表没有合作。
            - 转化单 = 带折扣码产生的销售订单，与样品单不同。
            - 样品单数 > 0 时：禁止写「无合作记录」「尚未合作」「先寄样」「寄样测试」等；下一步应围绕签收跟进、内容产出、素材回收、转化追踪。
            - 样品单数 = 0 且无转化单时，才可建议首次寄样或继续沟通。

            === 给运营的口头 briefing（summaryZh / recommendedActionZh 专用）===
            - 读者是内部 BD 同事，像 Slack 里转述「这人整体质量咋样、值不值得投」。
            - summaryZh：1~2 句口语，说清优势 + 主要短板/缺口。禁止报告体。必须如实反映样品单/转化单数据。
            - recommendedActionZh：1 句，直接说下一步（跟进内容/转化/继续聊/追加寄样/暂缓/不建议）。已有样品单时禁止建议「先寄样」。
            - 无转化数据时禁止编造 GMV；无内容数据时 conversion 维度可为 null。
            - qualityScore 在规则基准分 ruleCompositeScore 基础上微调，幅度不超过 ±15；须与 cooperationStage 一致。
            - dimensionScores 须含 cooperation（合作推进），与规则层子分趋势一致，可微调 ±8。
            - BLACKLIST/REJECTED/TERMINATED 状态 qualityTier 必须为 F，qualityScore ≤ 30。
            """;

    private final ChatLanguageModel chatLanguageModel;
    private final ObjectMapper objectMapper;

    public InfluencerQualityAnalyzeResponse analyze(InfluencerQualityAnalyzeRequest req) {
        String prompt = """
                你是 Athluna 红人商务系统的「红人质量分析师」，根据结构化数据评估红人整体合作价值。

                红人：%s（ID %s）
                状态/阶段：%s / %s
                画像：%s
                标签：%s
                粉丝：%s（%s 个平台）
                累计内容：%s 条；近90天内容：%s 条，平均互动率：%s，商单内容：%s 条
                合作阶段：%s（PROSPECT=待开发/SAMPLING=寄样中/PRODUCING=内容产出/CONVERTING=转化验证）
                样品履约率：%s
                转化单：%s 单，GMV：%s
                样品单（合作订单/寄样）：%s 单%s
                已付费合作（稿费/坑位）：%s
                距上次业务活跃：%s
                邮件意向：%s %s
                档案完整度：%s%%
                规则层子分：%s
                规则综合分：%s，置信度：%s

                输出纯 JSON：
                {
                  "qualityScore": 0-100,
                  "qualityTier": "S|A|B|C|D|F",
                  "confidence": 0-1,
                  "dimensionScores": {
                    "reach": 0-100或null,
                    "content": 0-100或null,
                    "conversion": 0-100或null,
                    "cooperation": 0-100或null,
                    "reliability": 0-100或null,
                    "communication": 0-100或null
                  },
                  "strengths": ["优势1","优势2"],
                  "risks": ["风险1"],
                  "dataGaps": ["缺什么数据"],
                  "summaryZh": "口语 briefing",
                  "recommendedActionZh": "下一步建议"
                }

                %s
                """.formatted(
                nullToEmpty(req.getDisplayName()),
                req.getInfluencerId(),
                nullToEmpty(req.getStatus()),
                nullToEmpty(req.getStage()),
                nullToEmpty(req.getProfileText()),
                req.getTagNames() != null ? String.join(", ", req.getTagNames()) : "-",
                req.getTotalFans() != null ? req.getTotalFans() : 0,
                req.getPlatformCount() != null ? req.getPlatformCount() : 0,
                req.getTotalContentCount() != null ? req.getTotalContentCount() : 0,
                req.getContentCount90d() != null ? req.getContentCount90d() : 0,
                req.getAvgEngagementRate90d() != null ? req.getAvgEngagementRate90d() + "%" : "无",
                req.getCommercialContentCount90d() != null ? req.getCommercialContentCount90d() : 0,
                nullToEmpty(req.getCooperationStage()),
                formatFulfillment(req.getSampleFulfillmentRate()),
                req.getConversionOrderCount() != null ? req.getConversionOrderCount() : 0,
                req.getConversionGmv() != null ? req.getConversionGmv() : 0,
                req.getSampleOrderCount() != null ? req.getSampleOrderCount() : 0,
                formatSampleHint(req),
                formatPaidCoop(req.getIsPaid()),
                formatDaysSince(req.getDaysSinceLastActivity()),
                req.getLatestIntentScore() != null ? req.getLatestIntentScore() + "分" : "无",
                nullToEmpty(req.getLatestIntentSummary()),
                req.getProfileCompleteness() != null ? req.getProfileCompleteness() : 0,
                req.getRuleScores() != null ? req.getRuleScores() : Map.of(),
                req.getRuleCompositeScore() != null ? req.getRuleCompositeScore() : 50,
                req.getConfidence() != null ? req.getConfidence() : 0.5,
                BRIEF_RULES);

        return parseJson(chatLanguageModel.generate(prompt), InfluencerQualityAnalyzeResponse.class, () -> {
            InfluencerQualityAnalyzeResponse f = new InfluencerQualityAnalyzeResponse();
            int base = req.getRuleCompositeScore() != null ? req.getRuleCompositeScore() : 50;
            f.setQualityScore(base);
            f.setQualityTier(tierFromScore(base, req.getStatus()));
            f.setConfidence(req.getConfidence() != null ? req.getConfidence() : 0.5);
            f.setDimensionScores(req.getRuleScores() != null ? new LinkedHashMap<>(req.getRuleScores()) : Map.of());
            f.setStrengths(List.of());
            f.setRisks(List.of());
            f.setDataGaps(List.of("AI 解析失败，仅展示规则分"));
            f.setSummaryZh(req.getDisplayName() + " 规则综合分 " + base + "，建议稍后重试 AI 分析");
            f.setRecommendedActionZh("查看规则子分后人工判断");
            return f;
        });
    }

    private <T> T parseJson(String raw, Class<T> type, java.util.function.Supplier<T> fallback) {
        try {
            String json = extractJson(raw);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("Quality AI JSON parse failed: {}", e.getMessage());
            return fallback.get();
        }
    }

    private String extractJson(String raw) {
        if (raw == null) {
            return "{}";
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```\\s*$", "").trim();
        }
        Matcher m = JSON_BLOCK.matcher(trimmed);
        if (m.find()) {
            return m.group();
        }
        return trimmed;
    }

    private static String tierFromScore(int score, String status) {
        if ("BLACKLIST".equals(status) || "REJECTED".equals(status) || "TERMINATED".equals(status)) {
            return "F";
        }
        if (score >= 90) return "S";
        if (score >= 75) return "A";
        if (score >= 60) return "B";
        if (score >= 40) return "C";
        return "D";
    }

    private static String formatSampleHint(InfluencerQualityAnalyzeRequest req) {
        long count = req.getSampleOrderCount() != null ? req.getSampleOrderCount() : 0;
        if (count <= 0) {
            return "";
        }
        if (req.getLastSampleAt() != null && !req.getLastSampleAt().isBlank()) {
            return "（最近寄样 " + req.getLastSampleAt() + "，表示已有合作推进）";
        }
        return "（表示已有合作推进，禁止建议先寄样）";
    }

    private static String formatFulfillment(Double rate) {
        if (rate == null) {
            return "无样品单";
        }
        return String.format("%.0f%%", rate * 100);
    }

    private static String formatDaysSince(Integer days) {
        if (days == null) {
            return "未知";
        }
        if (days <= 0) {
            return "今天";
        }
        return days + " 天前";
    }

    private static String formatPaidCoop(Boolean isPaid) {
        if (Boolean.TRUE.equals(isPaid)) {
            return "是（付稿费/坑位类）";
        }
        return "否（不影响样品合作记录）";
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
