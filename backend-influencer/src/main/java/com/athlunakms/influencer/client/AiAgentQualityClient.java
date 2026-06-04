package com.athlunakms.influencer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AiAgentQualityClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.ai-agent.url:http://localhost:8085}")
    private String aiAgentBaseUrl;

    @Value("${app.internal-api.token}")
    private String internalToken;

    public AiAgentQualityClient(
            @Qualifier("aiAgentRestTemplate") RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public JsonNode analyzeQuality(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Token", internalToken);
        try {
            String resp = restTemplate.postForObject(
                    aiAgentBaseUrl + "/internal/api/influencer/analyze-quality",
                    new HttpEntity<>(body, headers),
                    String.class);
            return objectMapper.readTree(resp != null ? resp : "{}");
        } catch (HttpStatusCodeException e) {
            String raw = e.getResponseBodyAsString();
            log.error("AI quality call failed status={} body={}", e.getStatusCode(), raw);
            throw new IllegalStateException("AI 质量分析失败：" + extractMessage(raw), e);
        } catch (Exception e) {
            log.error("AI quality call failed", e);
            throw new IllegalStateException("AI 服务调用失败: " + e.getMessage(), e);
        }
    }

    private String extractMessage(String raw) {
        if (raw == null || raw.isBlank()) {
            return "未知错误";
        }
        try {
            JsonNode node = objectMapper.readTree(raw);
            String msg = node.path("message").asText(null);
            return msg != null && !msg.isBlank() ? msg : raw.substring(0, Math.min(200, raw.length()));
        } catch (Exception ignored) {
            return raw.length() > 200 ? raw.substring(0, 200) + "…" : raw;
        }
    }
}
