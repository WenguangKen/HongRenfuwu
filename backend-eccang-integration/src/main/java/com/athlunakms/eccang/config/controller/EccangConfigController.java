package com.athlunakms.eccang.config.controller;

import com.athlunakms.eccang.config.dto.EccangConfigDto;
import com.athlunakms.eccang.config.service.EccangConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1.0/eccang-config")
@RequiredArgsConstructor
public class EccangConfigController {

    private final EccangConfigService configService;

    @GetMapping
    public ResponseEntity<EccangConfigDto> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PostMapping
    public ResponseEntity<EccangConfigDto> saveConfig(@RequestBody EccangConfigDto configDto) {
        return ResponseEntity.ok(configService.saveConfig(configDto));
    }
}
