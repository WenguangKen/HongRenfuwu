package com.athlunakms.ai.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/** 会话内最近一次定位到的红人，供「这个红人」类追问使用 */
@Component
public class CopilotSessionFocusService {

    public record Focus(Long influencerId, String handle, String displayName) {}

    private final ConcurrentHashMap<String, Focus> bySession = new ConcurrentHashMap<>();

    public void remember(String sessionId, Long influencerId, String handle, String displayName) {
        if (sessionId == null || influencerId == null) {
            return;
        }
        bySession.put(sessionId, new Focus(influencerId, handle, displayName));
    }

    public void rememberFromRow(String sessionId, String handle, Map<String, Object> row) {
        if (row == null || row.get("id") == null) {
            return;
        }
        Long id = Long.valueOf(row.get("id").toString());
        String name = String.valueOf(row.getOrDefault("nick_name", row.getOrDefault("real_name", "")));
        remember(sessionId, id, handle, name);
    }

    public Focus get(String sessionId) {
        return sessionId == null ? null : bySession.get(sessionId);
    }
}
