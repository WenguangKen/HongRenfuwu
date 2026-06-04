package com.athlunakms.ai.service;

import com.athlunakms.ai.client.CopilotMailClient;
import com.athlunakms.ai.util.CopilotHandleResolver;
import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CopilotHandlePreflightService {

    private final CopilotHandleResolver handleResolver;
    private final CopilotUiActionPublisher uiActionPublisher;
    private final CopilotSessionFocusService sessionFocus;
    private final CopilotMailService mailService;
    private final CopilotMailClient mailClient;

    public record PreflightOutcome(String replyText, boolean handled) {}

    public Optional<PreflightOutcome> tryHandleLookup(String sessionId, String message) {
        if (!handleResolver.handleRelatedQuestion(message)) {
            return Optional.empty();
        }
        List<String> handles = handleResolver.parseHandles(message);
        if (handles.isEmpty()) {
            return Optional.empty();
        }
        if (handles.size() > 1) {
            return tryMultiHandleLookup(sessionId, message, handles);
        }
        Optional<CopilotHandleResolver.HandleLookupResult> lookup = handleResolver.lookup(message);
        if (lookup.isEmpty()) {
            return Optional.empty();
        }
        return buildSingleHandleOutcome(sessionId, message, lookup.get());
    }

    private Optional<PreflightOutcome> tryMultiHandleLookup(
            String sessionId, String message, List<String> handles) {
        CopilotHandleResolver.MultiHandleLookupResult multi = handleResolver.lookupMultiple(message);
        CopilotHandleResolver.HandleLookupResult firstFound = null;
        for (CopilotHandleResolver.HandleLookupResult item : multi.items()) {
            if (!item.hasError() && !item.rows().isEmpty()) {
                firstFound = item;
                break;
            }
        }
        if (firstFound != null) {
            try {
                int count = firstFound.rows().size();
                Long singleId = null;
                String singleStatus = null;
                if (count == 1 && firstFound.rows().get(0).get("id") != null) {
                    singleId = Long.valueOf(firstFound.rows().get(0).get("id").toString());
                    if (firstFound.rows().get(0).get("status") != null) {
                        singleStatus = firstFound.rows().get(0).get("status").toString();
                    }
                }
                String actionJson =
                        CopilotUiActionBuilder.buildApplyInfluencerFilterByHandle(
                                firstFound.handle(), count, singleId, singleStatus);
                uiActionPublisher.publish(sessionId, actionJson);
            } catch (JsonProcessingException e) {
                return Optional.empty();
            }
            if (firstFound.rows().size() == 1) {
                sessionFocus.rememberFromRow(sessionId, firstFound.handle(), firstFound.rows().get(0));
            }
        }
        String reply = handleResolver.buildMultiReply(multi);
        return Optional.of(new PreflightOutcome(reply, true));
    }

    private Optional<PreflightOutcome> buildSingleHandleOutcome(
            String sessionId, String message, CopilotHandleResolver.HandleLookupResult result) {
        int count = result.rows().size();
        Long singleId = null;
        String singleStatus = null;
        if (count == 1 && result.rows().get(0).get("id") != null) {
            singleId = Long.valueOf(result.rows().get(0).get("id").toString());
            if (result.rows().get(0).get("status") != null) {
                singleStatus = result.rows().get(0).get("status").toString();
            }
        }
        if (count > 0) {
            try {
                String actionJson =
                        CopilotUiActionBuilder.buildApplyInfluencerFilterByHandle(
                                result.handle(), count, singleId, singleStatus);
                uiActionPublisher.publish(sessionId, actionJson);
            } catch (JsonProcessingException e) {
                return Optional.empty();
            }
        }
        if (count == 1) {
            Map<String, Object> row = result.rows().get(0);
            sessionFocus.rememberFromRow(sessionId, result.handle(), row);
        }
        String reply = handleResolver.buildReply(result);
        if (count == 1 && CopilotMailService.mailRelatedQuestion(message)) {
            Long id = Long.valueOf(result.rows().get(0).get("id").toString());
            reply = mailService.buildMailSummaryReply(mailClient.getMailSummary(id));
        }
        return Optional.of(new PreflightOutcome(reply, true));
    }
}