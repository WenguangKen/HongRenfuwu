package com.athlunakms.ai.service;

import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CopilotMailPreflightService {

    private final CopilotMailService mailService;
    private final CopilotUiActionPublisher uiActionPublisher;

    public Optional<CopilotHandlePreflightService.PreflightOutcome> tryMailQuestion(
            String sessionId, String message) {
        if (!CopilotMailService.mailRelatedQuestion(message)) {
            return Optional.empty();
        }
        Optional<Map<String, Object>> summary = mailService.resolveMailSummary(sessionId, message.trim());
        if (summary.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Object> data = summary.get();
        if (Boolean.TRUE.equals(data.get("found")) && data.get("influencerId") != null) {
            try {
                Long id = Long.valueOf(data.get("influencerId").toString());
                String handle = data.get("handle") != null ? data.get("handle").toString() : null;
                String json = CopilotUiActionBuilder.buildOpenMailHub(id, handle);
                uiActionPublisher.publish(sessionId, json);
            } catch (Exception ignored) {
                /* ignore ui action errors */
            }
        }
        String reply = mailService.buildMailSummaryReply(data);
        return Optional.of(new CopilotHandlePreflightService.PreflightOutcome(reply, true));
    }
}
