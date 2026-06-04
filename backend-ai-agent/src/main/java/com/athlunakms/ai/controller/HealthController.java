package com.athlunakms.ai.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/ai-agent-api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "backend-ai-agent");
    }
}
