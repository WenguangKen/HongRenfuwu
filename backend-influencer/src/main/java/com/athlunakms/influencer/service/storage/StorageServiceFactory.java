package com.athlunakms.influencer.service.storage;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import com.athlunakms.influencer.repository.ContentStorageConfigRepository;
import com.athlunakms.influencer.service.storage.LocalStorageService;
import com.athlunakms.influencer.service.storage.MinioStorageService;
import com.athlunakms.influencer.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceFactory {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceFactory.class);
    private final ContentStorageConfigRepository configRepository;
    private final com.athlunakms.influencer.config.StorageProperties storageProperties;

    // 缓存已创建的 StorageService 实例，避免每次 toDto 都重新创建（构造函数会触发 ensureCors 等远程调用）
    private volatile StorageService cachedStorageService;
    private volatile Long cachedConfigId;

    public StorageService getStorageService() {
        ContentStorageConfig config = this.configRepository.findFirstByIsActiveTrue().orElseGet(() -> {
            log.info("No active storage config found in DB, auto-initializing from properties");
            ContentStorageConfig newConfig = new ContentStorageConfig();
            newConfig.setStorageType(storageProperties.getType());
            newConfig.setBasePath(storageProperties.getBasePath());
            newConfig.setPublicUrlPrefix(storageProperties.getPublicUrlPrefix());
            newConfig.setEndpoint(storageProperties.getEndpoint());
            newConfig.setAccessKey(storageProperties.getAccessKey());
            newConfig.setSecretKey(storageProperties.getSecretKey());
            newConfig.setRegion(storageProperties.getRegion());
            newConfig.setIsActive(true);
            return this.configRepository.save(newConfig);
        });

        // 如果配置没变，直接返回缓存的实例
        if (cachedStorageService != null && cachedConfigId != null && cachedConfigId.equals(config.getId())) {
            return cachedStorageService;
        }

        synchronized (this) {
            // 双重检查
            if (cachedStorageService != null && cachedConfigId != null && cachedConfigId.equals(config.getId())) {
                return cachedStorageService;
            }
            cachedStorageService = this.createStorageService(config);
            cachedConfigId = config.getId();
            return cachedStorageService;
        }
    }

    public StorageService createStorageService(ContentStorageConfig config) {
        String storageType = config.getStorageType();
        log.debug("Creating storage service: type={}", (Object) storageType);
        switch (storageType.toUpperCase()) {
            case "LOCAL": {
                return new LocalStorageService(config);
            }
            case "MINIO": {
                return new MinioStorageService(config);
            }
        }
        throw new IllegalArgumentException("不支持的存储类型: " + storageType);
    }

    public ContentStorageConfig getActiveConfig() {
        return this.configRepository.findFirstByIsActiveTrue().orElse(null);
    }

    public boolean testConfig(ContentStorageConfig config) {
        try {
            StorageService service = this.createStorageService(config);
            return service.testConnection();
        } catch (Exception e) {
            log.error("Storage config test failed", (Throwable) e);
            return false;
        }
    }

    /** 清除缓存（配置变更后调用） */
    public void invalidateCache() {
        synchronized (this) {
            cachedStorageService = null;
            cachedConfigId = null;
        }
    }

    public StorageServiceFactory(ContentStorageConfigRepository configRepository,
            com.athlunakms.influencer.config.StorageProperties storageProperties) {
        this.configRepository = configRepository;
        this.storageProperties = storageProperties;
    }
}
