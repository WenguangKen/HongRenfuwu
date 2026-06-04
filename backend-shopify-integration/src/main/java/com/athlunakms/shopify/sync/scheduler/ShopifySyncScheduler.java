package com.athlunakms.shopify.sync.scheduler;

import com.athlunakms.shopify.order.service.OrderClassificationService;
import com.athlunakms.shopify.order.service.ShopifyOrderService;
import com.athlunakms.shopify.product.service.ShopifyProductService;
import com.athlunakms.shopify.store.entity.ShopifyStore;
import com.athlunakms.shopify.store.repository.ShopifyStoreRepository;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ShopifySyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(ShopifySyncScheduler.class);
    private final ShopifyStoreRepository storeRepository;
    private final ShopifyOrderService orderService;
    private final ShopifyProductService productService;
    private final OrderClassificationService orderClassificationService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    public void repairOnStartup() {
        log.info("[Startup] Running delivered status repair...");
        this.orderClassificationService.repairDeliveredStatus();
    }

    @Scheduled(cron="0 0,30 * * * ?")
    public void syncOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusHours(2L);
        log.info("=".repeat(80));
        log.info("[OrderSync] Starting order sync | Window: {} \u2192 {}", (Object)startTime.format(FORMATTER), (Object)endTime.format(FORMATTER));
        this.runForAllStores("OrderSync", storeId -> {
            if (this.orderService.isSyncRunning(storeId)) {
                log.info("[OrderSync] \u26a0 Skipping store {} (ID: {}): manual sync in progress", "store", storeId);
                return;
            }
            this.orderService.syncOrders(storeId, startTime, endTime);
        });
        this.orderClassificationService.repairDeliveredStatus();
    }

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
        List<ShopifyStore> activeStores = this.storeRepository.findByStatus("active");
        if (activeStores.isEmpty()) {
            log.warn("[{}] No active stores found, skipping", (Object)taskName);
            return;
        }
        log.info("[{}] Found {} active store(s)", (Object)taskName, (Object)activeStores.size());
        int successCount = 0;
        int failureCount = 0;
        for (ShopifyStore store : activeStores) {
            try {
                log.info("[{}] \u2192 Syncing store: {} (ID: {})", new Object[]{taskName, store.getStoreName(), store.getId()});
                task.execute(store.getId());
                ++successCount;
                log.info("[{}] \u2713 Store {} synced", (Object)taskName, (Object)store.getStoreName());
                if (activeStores.size() <= 1) continue;
                Thread.sleep(1000L);
            }
            catch (Exception e) {
                ++failureCount;
                log.error("[{}] \u2717 Failed to sync store: {} (ID: {})", new Object[]{taskName, store.getStoreName(), store.getId(), e});
            }
        }
        log.info("[{}] Completed \u2014 Success: {}, Failed: {}, Total: {}", new Object[]{taskName, successCount, failureCount, activeStores.size()});
        log.info("=".repeat(80));
    }

    public ShopifySyncScheduler(ShopifyStoreRepository storeRepository, ShopifyOrderService orderService, ShopifyProductService productService, OrderClassificationService orderClassificationService) {
        this.storeRepository = storeRepository;
        this.orderService = orderService;
        this.productService = productService;
        this.orderClassificationService = orderClassificationService;
    }

    @FunctionalInterface
    public interface StoreTask {
        void execute(Long storeId) throws Exception;
    }
}
