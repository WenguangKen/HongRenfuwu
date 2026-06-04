package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.StorageConfigDto;
import com.athlunakms.influencer.entity.ContentStorageConfig;
import com.athlunakms.influencer.repository.ContentStorageConfigRepository;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/storage"})
public class StorageConfigController {
    private static final Logger log = LoggerFactory.getLogger(StorageConfigController.class);
    private final ContentStorageConfigRepository configRepository;
    private final StorageServiceFactory storageServiceFactory;

    @GetMapping(value={"/config"})
    public ResponseEntity<?> getConfig() {
        try {
            ContentStorageConfig config = this.configRepository.findFirstByIsActiveTrue().orElse(null);
            if (config == null) {
                log.info("No active config found, returning default");
                StorageConfigDto defaultDto = new StorageConfigDto();
                defaultDto.setStorageType("LOCAL");
                return ResponseEntity.ok(defaultDto);
            }
            return ResponseEntity.ok(this.toDto(config));
        }
        catch (Exception e) {
            log.error("Failed to get storage config", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getClass().getName(), "message", e.getMessage()));
        }
    }

    @PutMapping(value={"/config"})
    public ResponseEntity<?> updateConfig(@RequestBody StorageConfigDto dto) {
        try {
            ContentStorageConfig config = dto.getId() != null ? this.configRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("配置不存在")) : this.configRepository.findFirstByIsActiveTrue().orElse(new ContentStorageConfig());
            config.setStorageType(dto.getStorageType());
            config.setBasePath(dto.getBasePath());
            config.setPublicUrlPrefix(dto.getPublicUrlPrefix());
            config.setRegion(dto.getRegion());
            config.setEndpoint(dto.getEndpoint());
            config.setCdnDomain(dto.getCdnDomain());
            config.setMaxFileSize(dto.getMaxFileSize());
            config.setAllowedTypes(dto.getAllowedTypes());
            config.setIsActive(true);
            if (dto.getAccessKey() != null && !dto.getAccessKey().contains("***")) {
                config.setAccessKey(dto.getAccessKey());
            }
            if (dto.getSecretKey() != null && !dto.getSecretKey().contains("***")) {
                config.setSecretKey(dto.getSecretKey());
            }
            ContentStorageConfig saved = this.configRepository.save(config);
            log.info("Storage config updated: type={}", saved.getStorageType());
            return ResponseEntity.ok(this.toDto(saved));
        }
        catch (Exception e) {
            log.error("Failed to update storage config", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getClass().getName(), "message", e.getMessage()));
        }
    }

    @PostMapping(value={"/test"})
    public ResponseEntity<?> testConnection(@RequestBody StorageConfigDto dto) {
        try {
            ContentStorageConfig config = new ContentStorageConfig();
            config.setStorageType(dto.getStorageType());
            config.setBasePath(dto.getBasePath());
            config.setPublicUrlPrefix(dto.getPublicUrlPrefix());
            config.setAccessKey(dto.getAccessKey());
            config.setSecretKey(dto.getSecretKey());
            config.setRegion(dto.getRegion());
            config.setEndpoint(dto.getEndpoint());
            boolean success = this.storageServiceFactory.testConfig(config);
            if (success) {
                return ResponseEntity.ok(Map.of("success", true, "message", "连接测试成功"));
            }
            return ResponseEntity.ok(Map.of("success", false, "message", "连接测试失败请检查配置"));
        }
        catch (Exception e) {
            log.error("Storage test failed", e);
            return ResponseEntity.ok(Map.of("success", false, "message", "测试失败: " + e.getMessage()));
        }
    }

    private StorageConfigDto toDto(ContentStorageConfig entity) {
        StorageConfigDto dto = new StorageConfigDto();
        dto.setId(entity.getId());
        dto.setStorageType(entity.getStorageType());
        dto.setBasePath(entity.getBasePath());
        dto.setPublicUrlPrefix(entity.getPublicUrlPrefix());
        dto.setRegion(entity.getRegion());
        dto.setEndpoint(entity.getEndpoint());
        dto.setCdnDomain(entity.getCdnDomain());
        dto.setMaxFileSize(entity.getMaxFileSize());
        dto.setAllowedTypes(entity.getAllowedTypes());
        dto.setIsActive(entity.getIsActive());
        if (entity.getAccessKey() != null && !entity.getAccessKey().isEmpty()) {
            dto.setAccessKey(this.maskString(entity.getAccessKey()));
        }
        if (entity.getSecretKey() != null && !entity.getSecretKey().isEmpty()) {
            dto.setSecretKey(this.maskString(entity.getSecretKey()));
        }
        return dto;
    }

    private String maskString(String str) {
        if (str == null || str.length() <= 4) {
            return "****";
        }
        return str.substring(0, 2) + "***" + str.substring(str.length() - 2);
    }

    public StorageConfigController(ContentStorageConfigRepository configRepository, StorageServiceFactory storageServiceFactory) {
        this.configRepository = configRepository;
        this.storageServiceFactory = storageServiceFactory;
    }
}

