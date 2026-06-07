package com.athlunakms.eccang.config;

import com.athlunakms.eccang.common.security.EncryptionService;
import com.athlunakms.eccang.config.entity.EccangConfig;
import com.athlunakms.eccang.config.repository.EccangConfigRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class EccangConfigInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger log = LoggerFactory.getLogger(EccangConfigInitializer.class);

    private final EccangConfigRepository configRepository;
    private final EncryptionService encryptionService;

    @Value("${eccang.api.app-key:}")
    private String defaultAppKey;

    @Value("${eccang.api.app-secret:}")
    private String defaultAppSecret;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (configRepository.count() > 0) {
            return;
        }
        if (!StringUtils.hasText(defaultAppKey) || !StringUtils.hasText(defaultAppSecret)) {
            log.warn("易仓 API 未写入数据库，且未配置 eccang.api.app-key/app-secret，请在系统设置中填写");
            return;
        }
        EccangConfig config = new EccangConfig();
        config.setAppKey(defaultAppKey.trim());
        config.setAppSecretEncrypted(encryptionService.encrypt(defaultAppSecret.trim()));
        configRepository.save(config);
        log.info("已从配置文件初始化易仓 API 授权（红人系统应用，appKey={}）", defaultAppKey);
    }
}
