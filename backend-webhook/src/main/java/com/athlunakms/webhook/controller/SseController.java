package com.athlunakms.webhook.controller;

import com.athlunakms.webhook.sse.SseEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value={"/v1/sse"})
@CrossOrigin(originPatterns={"*"}, allowedHeaders={"*"}, allowCredentials="false")
public class SseController {
    private static final Logger log = LoggerFactory.getLogger(SseController.class);
    private final SseEventPublisher eventPublisher;

    @GetMapping(value={"/events"}, produces={"text/event-stream"})
    public SseEmitter subscribe() {
        log.info("[SSE] New subscription request");
        return this.eventPublisher.registerClient();
    }

    public SseController(SseEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

