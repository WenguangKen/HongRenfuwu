package com.athlunakms.eccang.sync.scheduler;

import com.athlunakms.eccang.product.service.EccangProductService;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "eccang.sync.scheduler", name = "enabled", havingValue = "true")
public class EccangSyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(EccangSyncScheduler.class);
    private final EccangStoreRepository storeRepository;
    private final EccangProductService productService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${eccang.sync.store-delay-ms:3000}")
    private long storeDelayMs;

    @Scheduled(cron="0 5 * * * ?")
    public void syncProducts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusHours(1L);
        log.info("=".repeat(80));
        log.info("[ProductSync] Starting product sync | Window: {} \u2192 {}", (Object)startTime.format(FORMATTER), (Object)endTime.format(FORMATTER));
        this.runForAllStores("ProductSync", storeId -> this.productService.syncProductsIncremental(storeId, startTime, endTime));
    }

    private void runForAllStores(String taskName, StoreTask task) {
        List<EccangStore> activeStores = this.storeRepository.findByStatus("active");
        if (activeStores.isEmpty()) {
            log.warn("[{}] No active stores found, skipping", (Object)taskName);
            return;
        }
        log.info("[{}] Found {} active store(s)", (Object)taskName, (Object)activeStores.size());
        int successCount = 0;
        int failureCount = 0;
        for (EccangStore store : activeStores) {
            try {
                if (this.productService.isSyncRunning(store.getId())) {
                    log.warn("[{}] Store {} (ID: {}) sync already running, skipping", taskName, store.getStoreName(), store.getId());
                    continue;
                }
                log.info("[{}] \u2192 Syncing store: {} (ID: {})", new Object[]{taskName, store.getStoreName(), store.getId()});
                task.execute(store.getId());
                ++successCount;
                log.info("[{}] \u2713 Store {} sync task submitted", (Object)taskName, (Object)store.getStoreName());
                if (activeStores.size() <= 1) continue;
                Thread.sleep(storeDelayMs);
            }
            catch (Exception e) {
                ++failureCount;
                log.error("[{}] \u2717 Failed to sync store: {} (ID: {})", new Object[]{taskName, store.getStoreName(), store.getId(), e});
            }
        }
        log.info("[{}] Completed \u2014 Success: {}, Failed: {}, Total: {}", new Object[]{taskName, successCount, failureCount, activeStores.size()});
        log.info("=".repeat(80));
    }

    public EccangSyncScheduler(EccangStoreRepository storeRepository, EccangProductService productService) {
        this.storeRepository = storeRepository;
        this.productService = productService;
    }

    @FunctionalInterface
    public interface StoreTask {
        void execute(Long storeId) throws Exception;
    }
}
