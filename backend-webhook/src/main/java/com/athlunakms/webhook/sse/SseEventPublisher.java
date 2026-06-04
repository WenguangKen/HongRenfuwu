package com.athlunakms.webhook.sse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(SseEventPublisher.class);
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter registerClient() {
        SseEmitter emitter = new SseEmitter(Long.valueOf(1800000L));
        emitter.onCompletion(() -> {
            this.emitters.remove(emitter);
            log.debug("[SSE] Client disconnected, total clients: {}", this.emitters.size());
        });
        emitter.onTimeout(() -> {
            this.emitters.remove(emitter);
            log.debug("[SSE] Client timed out, total clients: {}", this.emitters.size());
        });
        emitter.onError(e -> {
            this.emitters.remove(emitter);
            log.debug("[SSE] Client error: {}", e.getMessage());
        });
        this.emitters.add(emitter);
        log.info("[SSE] New client connected, total clients: {}", this.emitters.size());
        try {
            HashMap<String, Object> msg = new HashMap<String, Object>();
            msg.put("message", "SSE connection established");
            msg.put("clientCount", this.emitters.size());
            emitter.send(SseEmitter.event().name("connected").data(msg));
        }
        catch (IOException e2) {
            log.warn("[SSE] Failed to send initial event: {}", e2.getMessage());
        }
        return emitter;
    }

    public void publishWebhookEvent(String eventType, String category, Map<String, Object> data) {
        log.info("[SSE] Publishing event: type={}, category={}, clients={}", eventType, category, this.emitters.size());
        HashMap<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("type", eventType);
        eventData.put("category", category);
        eventData.put("data", data);
        eventData.put("timestamp", System.currentTimeMillis());
        for (SseEmitter emitter : this.emitters) {
            try {
                emitter.send(SseEmitter.event().name("webhook").data(eventData));
            }
            catch (IOException e) {
                log.debug("[SSE] Failed to send to client, removing: {}", e.getMessage());
                this.emitters.remove(emitter);
            }
        }
    }

    public int getClientCount() {
        return this.emitters.size();
    }
}

