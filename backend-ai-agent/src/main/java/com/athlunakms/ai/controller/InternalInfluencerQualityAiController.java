package com.athlunakms.ai.controller;

import com.athlunakms.ai.dto.influencer.InfluencerQualityAnalyzeRequest;
import com.athlunakms.ai.dto.influencer.InfluencerQualityAnalyzeResponse;
import com.athlunakms.ai.service.InfluencerQualityAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/api/influencer")
@RequiredArgsConstructor
public class InternalInfluencerQualityAiController {

    private final InfluencerQualityAiService qualityAiService;
    private final ObjectMapper objectMapper;

    @PostMapping("/analyze-quality")
    public InfluencerQualityAnalyzeResponse analyzeQuality(@RequestBody Map<String, Object> body) {
        InfluencerQualityAnalyzeRequest request = objectMapper.convertValue(body, InfluencerQualityAnalyzeRequest.class);
        return qualityAiService.analyze(request);
    }
}
