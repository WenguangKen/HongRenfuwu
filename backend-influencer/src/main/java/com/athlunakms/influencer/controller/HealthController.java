package com.athlunakms.influencer.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1.0"})
public class HealthController {
    @GetMapping(value={"/health"})
    public Map<String, Object> health() {
        return Map.of("service", "influencer", "status", "UP", "version", "1.0.0");
    }
}

