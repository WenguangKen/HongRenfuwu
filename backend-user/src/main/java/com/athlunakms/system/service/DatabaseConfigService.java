package com.athlunakms.system.service;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.system.dto.DatabaseConfigRequest;
import com.athlunakms.system.dto.DatabaseConfigResponse;
import com.athlunakms.system.entity.DatabaseConfig;
import com.athlunakms.system.repository.DatabaseConfigRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseConfigService {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfigService.class);
    private final DatabaseConfigRepository configRepository;
    private final AESEncryptionService encryptionService;

    @Transactional
    public DatabaseConfigResponse createConfig(DatabaseConfigRequest request) {
        this.testConnection(request);
        DatabaseConfig config = new DatabaseConfig();
        config.setConfigType(request.getConfigType());
        config.setHost(request.getHost());
        config.setPort(request.getPort());
        config.setDatabase(request.getDatabase());
        config.setUsername(request.getUsername());
        config.setPasswordEncrypted(this.encryptionService.encrypt(request.getPassword()));
        config.setDescription(request.getDescription());
        config.setIsActive(request.getIsActive());
        config = this.configRepository.save(config);
        log.info("\u521b\u5efa\u6570\u636e\u5e93\u914d\u7f6e: id={}, type={}, host={}", config.getId(), config.getConfigType(), config.getHost());
        return this.convertToResponse(config);
    }

    @Transactional
    public DatabaseConfigResponse updateConfig(Long id, DatabaseConfigRequest request) {
        DatabaseConfig config = this.configRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "\u6570\u636e\u5e93\u914d\u7f6e\u4e0d\u5b58\u5728"));
        this.testConnection(request);
        config.setConfigType(request.getConfigType());
        config.setHost(request.getHost());
        config.setPort(request.getPort());
        config.setDatabase(request.getDatabase());
        config.setUsername(request.getUsername());
        config.setPasswordEncrypted(this.encryptionService.encrypt(request.getPassword()));
        config.setDescription(request.getDescription());
        config.setIsActive(request.getIsActive());
        config = this.configRepository.save(config);
        log.info("\u66f4\u65b0\u6570\u636e\u5e93\u914d\u7f6e: id={}", id);
        return this.convertToResponse(config);
    }

    @Transactional(readOnly=true)
    public List<DatabaseConfigResponse> getConfigList(DatabaseConfig.ConfigType configType) {
        List<DatabaseConfig> configs = configType != null ? this.configRepository.findByConfigType(configType) : this.configRepository.findAll();
        return configs.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly=true)
    public DatabaseConfigResponse getConfigById(Long id) {
        DatabaseConfig config = this.configRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "\u6570\u636e\u5e93\u914d\u7f6e\u4e0d\u5b58\u5728"));
        return this.convertToResponse(config);
    }

    @Transactional
    public void deleteConfig(Long id) {
        DatabaseConfig config = this.configRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "\u6570\u636e\u5e93\u914d\u7f6e\u4e0d\u5b58\u5728"));
        this.configRepository.delete(config);
        log.info("\u5220\u9664\u6570\u636e\u5e93\u914d\u7f6e: id={}", id);
    }

    public void testConnection(DatabaseConfigRequest request) {
        try {
            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=Asia/Shanghai", request.getHost(), request.getPort(), request.getDatabase());
            Connection conn = DriverManager.getConnection(url, request.getUsername(), request.getPassword());
            conn.close();
            log.info("\u6570\u636e\u5e93\u8fde\u63a5\u6d4b\u8bd5\u6210\u529f: host={}, database={}", request.getHost(), request.getDatabase());
        }
        catch (Exception e) {
            log.error("\u6570\u636e\u5e93\u8fde\u63a5\u6d4b\u8bd5\u5931\u8d25", e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u6570\u636e\u5e93\u8fde\u63a5\u5931\u8d25: " + e.getMessage());
        }
    }

    private DatabaseConfigResponse convertToResponse(DatabaseConfig config) {
        DatabaseConfigResponse response = new DatabaseConfigResponse();
        response.setId(config.getId());
        response.setConfigType(config.getConfigType());
        response.setHost(config.getHost());
        response.setPort(config.getPort());
        response.setDatabase(config.getDatabase());
        response.setUsername(config.getUsername());
        response.setDescription(config.getDescription());
        response.setIsActive(config.getIsActive());
        response.setCreatedAt(config.getCreatedAt());
        response.setUpdatedAt(config.getUpdatedAt());
        return response;
    }

    public DatabaseConfigService(DatabaseConfigRepository configRepository, AESEncryptionService encryptionService) {
        this.configRepository = configRepository;
        this.encryptionService = encryptionService;
    }
}

