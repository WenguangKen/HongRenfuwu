package com.athlunakms.user.service;

import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.user.entity.SystemConfig;
import com.athlunakms.user.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemConfigService {
    private static final Logger log = LoggerFactory.getLogger(SystemConfigService.class);
    private final SystemConfigRepository systemConfigRepository;
    private final AESEncryptionService encryptionService;

    public String getConfigValue(String key) {
        return this.systemConfigRepository.findByConfigKey(key).map(config -> {
            if (Boolean.TRUE.equals(config.getIsEncrypted())) {
                try {
                    return this.encryptionService.decrypt(config.getConfigValue());
                }
                catch (Exception e) {
                    log.error("Error decrypting config key: {}", key, e);
                    return null;
                }
            }
            return config.getConfigValue();
        }).orElse(null);
    }

    @Transactional
    public void saveConfig(String key, String value, String description, boolean encrypt) {
        SystemConfig config = this.systemConfigRepository.findByConfigKey(key).orElse(new SystemConfig());
        config.setConfigKey(key);
        config.setDescription(description);
        config.setIsEncrypted(Boolean.valueOf(encrypt));
        if (encrypt && value != null && !value.isEmpty()) {
            config.setConfigValue(this.encryptionService.encrypt(value));
        } else {
            config.setConfigValue(value);
        }
        this.systemConfigRepository.save(config);
        log.info("Saved system config: {}", key);
    }

    public SystemConfig getConfig(String key) {
        return this.systemConfigRepository.findByConfigKey(key).orElse(null);
    }

    public SystemConfigService(SystemConfigRepository systemConfigRepository, AESEncryptionService encryptionService) {
        this.systemConfigRepository = systemConfigRepository;
        this.encryptionService = encryptionService;
    }
}

