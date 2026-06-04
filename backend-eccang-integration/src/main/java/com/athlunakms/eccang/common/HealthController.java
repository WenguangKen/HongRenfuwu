package com.athlunakms.eccang.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping(value={"/health"})
    public ResponseEntity<Map<String, Object>> health() {
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("status", "UP");
        response.put("service", "eccang-integration");
        return ResponseEntity.ok(response);
    }
}

