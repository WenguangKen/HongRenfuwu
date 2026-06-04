package com.athlunakms.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class CopilotMailClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${services.backend-mail.url}")
    private String mailBaseUrl;

    public Map<String, Object> getMailSummary(Long influencerId) {
        @SuppressWarnings("unchecked")
        Map<String, Object> body = restTemplate.getForObject(
                mailBaseUrl + "/internal/api/mail/copilot/influencers/" + influencerId + "/summary",
                Map.class);
        return body != null ? body : Map.of("found", false);
    }

    public Map<String, Object> getMailSummaryByHandle(String handle) {
        @SuppressWarnings("unchecked")
        Map<String, Object> body = restTemplate.getForObject(
                mailBaseUrl + "/internal/api/mail/copilot/influencers/by-handle/" + handle + "/summary",
                Map.class);
        return body != null ? body : Map.of("found", false);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> listTemplates() {
        List<Map<String, Object>> list = restTemplate.getForObject(
                mailBaseUrl + "/internal/api/mail/copilot/templates",
                List.class);
        return list != null ? list : List.of();
    }

    public Map<String, Object> createInvite(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.postForObject(
                    mailBaseUrl + "/internal/api/mail/copilot/invite",
                    new HttpEntity<>(body, headers),
                    Map.class);
            return resp != null ? resp : Map.of();
        } catch (HttpStatusCodeException e) {
            log.error("invite outreach failed: {}", e.getResponseBodyAsString());
            throw new IllegalStateException(parseError(e.getResponseBodyAsString()), e);
        }
    }

    private String parseError(String raw) {
        try {
            JsonNode n = objectMapper.readTree(raw);
            return n.path("message").asText("邮件服务调用失败");
        } catch (Exception ignored) {
            return raw != null && raw.length() > 200 ? raw.substring(0, 200) : String.valueOf(raw);
        }
    }
}
