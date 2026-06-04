package com.athlunakms.ai.service;

import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CopilotInfluencerWorkflowService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CopilotSessionFocusService sessionFocus;
    private final CopilotUiActionPublisher uiActionPublisher;

    @Value("${services.backend-influencer.url}")
    private String influencerServiceUrl;

    public String transitionStatus(
            String sessionId,
            String influencerIdsCsv,
            String handle,
            String targetStatus,
            String reason) throws Exception {
        List<Long> ids = resolveInfluencerIds(sessionId, influencerIdsCsv, handle);
        if (ids.isEmpty()) {
            return "请提供红人 ID（逗号分隔）、handle，或先指定「这个红人」再流转状态。";
        }
        String normalized = normalizeTargetStatus(targetStatus);
        if (normalized == null) {
            return "无法识别目标状态「" + targetStatus + "」。可用：待联系、已联系、沟通中、合作中、休眠中、暂不合作、黑名单、不再合作。";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("ids", ids);
        body.put("targetStatus", normalized);
        body.put("reason", StringUtils.hasText(reason) ? reason : "AI助手状态流转");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String resp = restTemplate.postForObject(
                influencerServiceUrl + "/internal/api/influencers/workflow/batch-status-change",
                new HttpEntity<>(body, headers),
                String.class);

        JsonNode root = objectMapper.readTree(resp != null ? resp : "{}");
        List<Long> success = new ArrayList<>();
        if (root.path("successIds").isArray()) {
            root.path("successIds").forEach(n -> success.add(n.asLong()));
        }
        int failed = root.path("failedItems").isArray() ? root.path("failedItems").size() : 0;

        if (!success.isEmpty()) {
            String tab = statusToListTab(normalized);
            uiActionPublisher.publish(
                    sessionId, CopilotUiActionBuilder.buildSwitchInfluencerStatusTab(tab, success.size()));
        }

        String statusZh = statusLabelZh(normalized);
        StringBuilder sb = new StringBuilder();
        sb.append("状态流转完成：").append(success.size()).append(" 位红人已更新为「").append(statusZh).append("」。");
        if (failed > 0) {
            sb.append(" ").append(failed).append(" 位失败，请查看日志。");
        }
        if (success.size() == 1) {
            sessionFocus.remember(sessionId, success.get(0), handle, null);
        }
        return sb.toString();
    }

    private List<Long> resolveInfluencerIds(String sessionId, String csv, String handle) {
        List<Long> ids = new ArrayList<>();
        if (StringUtils.hasText(csv)) {
            for (String part : csv.split("[,，;\\s]+")) {
                if (part.isBlank()) continue;
                try {
                    ids.add(Long.parseLong(part.trim()));
                } catch (NumberFormatException ignored) {
                    /* skip */
                }
            }
        }
        if (ids.isEmpty() && StringUtils.hasText(handle)) {
            lookupIdByHandle(handle.replace("@", "")).ifPresent(ids::add);
        }
        if (ids.isEmpty()) {
            CopilotSessionFocusService.Focus focus = sessionFocus.get(sessionId);
            if (focus != null && focus.influencerId() != null) {
                ids.add(focus.influencerId());
            }
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    private Optional<Long> lookupIdByHandle(String handle) {
        try {
            List<Map<String, Object>> rows = restTemplate.getForObject(
                    influencerServiceUrl + "/internal/api/influencers/search-by-handle?handle=" + handle,
                    List.class);
            if (rows != null && rows.size() == 1 && rows.get(0).get("id") != null) {
                return Optional.of(Long.valueOf(rows.get(0).get("id").toString()));
            }
        } catch (Exception e) {
            log.warn("lookup handle failed: {}", handle, e);
        }
        return Optional.empty();
    }

    static String normalizeTargetStatus(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String s = raw.trim();
        return switch (s) {
            case "待联系", "PENDING" -> "PENDING";
            case "已联系", "CONTACTED" -> "CONTACTED";
            case "沟通中", "COMMUNICATING" -> "COMMUNICATING";
            case "合作中", "已合作", "合作", "COOPERATING" -> "COOPERATING";
            case "休眠", "休眠中", "DORMANT" -> "DORMANT";
            case "暂不合作", "PAUSED" -> "PAUSED";
            case "黑名单", "BLACKLIST" -> "BLACKLIST";
            case "不再合作", "TERMINATED" -> "TERMINATED";
            case "待筛选", "UNSCREENED" -> "UNSCREENED";
            case "暂不合适", "拒绝", "REJECTED" -> "REJECTED";
            default -> s.equals(s.toUpperCase()) ? s : null;
        };
    }

    private static String statusToListTab(String status) {
        return switch (status) {
            case "PENDING" -> "pending";
            case "CONTACTED" -> "contacted";
            case "COMMUNICATING" -> "communicating";
            case "COOPERATING" -> "cooperating";
            case "DORMANT" -> "dormant";
            case "PAUSED" -> "paused";
            case "BLACKLIST" -> "blacklist";
            case "TERMINATED" -> "terminated";
            default -> "all";
        };
    }

    private static String statusLabelZh(String status) {
        return switch (status) {
            case "PENDING" -> "待联系";
            case "CONTACTED" -> "已联系";
            case "COMMUNICATING" -> "沟通中";
            case "COOPERATING" -> "合作中";
            case "DORMANT" -> "休眠中";
            case "PAUSED" -> "暂不合作";
            case "BLACKLIST" -> "黑名单";
            case "TERMINATED" -> "不再合作";
            default -> status;
        };
    }
}
