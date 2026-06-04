package com.athlunakms.eccang.config.service.impl;

import com.athlunakms.eccang.common.security.EncryptionService;
import com.athlunakms.eccang.config.dto.EccangConfigDto;
import com.athlunakms.eccang.config.entity.EccangConfig;
import com.athlunakms.eccang.config.repository.EccangConfigRepository;
import com.athlunakms.eccang.config.service.EccangConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EccangConfigServiceImpl implements EccangConfigService {

    private final EccangConfigRepository configRepository;
    private final EncryptionService encryptionService;

    @Override
    public EccangConfigDto getConfig() {
        List<EccangConfig> configs = configRepository.findAll();
        EccangConfigDto dto = new EccangConfigDto();
        if (configs.isEmpty()) {
            dto.setIsConfigured(false);
            return dto;
        }
        EccangConfig config = configs.get(0);
        dto.setId(config.getId());
        dto.setAppKey(config.getAppKey());
        dto.setIsConfigured(true);
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());
        // Do not return secret and token to frontend for security
        return dto;
    }

    @Override
    @Transactional
    public EccangConfigDto saveConfig(EccangConfigDto configDto) {
        List<EccangConfig> configs = configRepository.findAll();
        EccangConfig config;
        if (configs.isEmpty()) {
            config = new EccangConfig();
        } else {
            config = configs.get(0);
        }

        config.setAppKey(configDto.getAppKey());
        
        if (configDto.getAppSecret() != null && !configDto.getAppSecret().isEmpty()) {
            config.setAppSecretEncrypted(encryptionService.encrypt(configDto.getAppSecret()));
        }
        
        if (configDto.getAccessToken() != null && !configDto.getAccessToken().isEmpty()) {
            config.setAccessTokenEncrypted(encryptionService.encrypt(configDto.getAccessToken()));
        }

        config = configRepository.save(config);
        
        EccangConfigDto result = new EccangConfigDto();
        result.setId(config.getId());
        result.setAppKey(config.getAppKey());
        result.setIsConfigured(true);
        return result;
    }
}
