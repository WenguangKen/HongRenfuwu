package com.athlunakms.user.controller;

import com.athlunakms.user.dto.SystemConfigDto;
import com.athlunakms.user.entity.SystemConfig;
import com.athlunakms.user.service.SystemConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/system/configs"})
public class SystemConfigController {
    private final SystemConfigService systemConfigService;

    @GetMapping(value={"/{key}"})
    @PreAuthorize(value="hasRole('ADMIN')")
    public ResponseEntity<SystemConfigDto> getConfig(@PathVariable(value="key") String key, @RequestParam(value="reveal", defaultValue="false") boolean reveal) {
        SystemConfig config = this.systemConfigService.getConfig(key);
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        SystemConfigDto dto = new SystemConfigDto();
        dto.setKey(config.getConfigKey());
        dto.setDescription(config.getDescription());
        dto.setIsEncrypted(config.getIsEncrypted());
        if (Boolean.TRUE.equals(config.getIsEncrypted())) {
            if (reveal) {
                dto.setValue(this.systemConfigService.getConfigValue(key));
            } else {
                dto.setValue("****************");
            }
        } else {
            dto.setValue(config.getConfigValue());
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize(value="hasRole('ADMIN')")
    public ResponseEntity<Void> saveConfig(@RequestBody SystemConfigDto dto) {
        this.systemConfigService.saveConfig(dto.getKey(), dto.getValue(), dto.getDescription(), Boolean.TRUE.equals(dto.getIsEncrypted()));
        return ResponseEntity.ok().build();
    }

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }
}

