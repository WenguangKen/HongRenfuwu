package com.athlunakms.ai.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.stereotype.Component;

/** 工具执行后向前端推送页面联动指令（按会话 ID 暂存，由 SSE 刷出） */
@Component
public class CopilotUiActionPublisher {

    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> queues = new ConcurrentHashMap<>();

    public void publish(String sessionId, String actionJson) {
        if (sessionId == null || sessionId.isBlank() || actionJson == null || actionJson.isBlank()) {
            return;
        }
        queues.computeIfAbsent(sessionId, k -> new ConcurrentLinkedQueue<>()).add(actionJson);
    }

    public List<String> drain(String sessionId) {
        ConcurrentLinkedQueue<String> q = queues.get(sessionId);
        if (q == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        String item;
        while ((item = q.poll()) != null) {
            out.add(item);
        }
        return out;
    }

    public void clear(String sessionId) {
        if (sessionId != null) {
            queues.remove(sessionId);
        }
    }
}
