package com.athlunakms.ai.dto.influencer;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class InfluencerQualityAnalyzeResponse {
    private Integer qualityScore;
    private String qualityTier;
    private Double confidence;
    private Map<String, Integer> dimensionScores;
    private List<String> strengths;
    private List<String> risks;
    private List<String> dataGaps;
    private String summaryZh;
    private String recommendedActionZh;
}
