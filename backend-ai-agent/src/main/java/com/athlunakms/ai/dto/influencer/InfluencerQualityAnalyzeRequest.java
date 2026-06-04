package com.athlunakms.ai.dto.influencer;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class InfluencerQualityAnalyzeRequest {
    private Long influencerId;
    private String displayName;
    private String status;
    private String stage;
    private String profileText;
    private List<String> tagNames;
    private String description;
    private Long totalFans;
    private Integer platformCount;
    private Integer contentCount90d;
    private Double avgEngagementRate90d;
    private Long conversionOrderCount;
    private Double conversionGmv;
    private Long sampleOrderCount;
    private String lastSampleAt;
    private Boolean isPaid;
    private Integer totalContentCount;
    private Integer commercialContentCount90d;
    private Double sampleFulfillmentRate;
    private String cooperationStage;
    private Integer daysSinceLastActivity;
    private Integer latestIntentScore;
    private String latestIntentSummary;
    private Integer mailInboundCount;
    private Integer profileCompleteness;
    private Map<String, Integer> ruleScores;
    private Integer ruleCompositeScore;
    private Double confidence;
}
