package com.athlunakms.webhook.processor;

import com.athlunakms.webhook.repository.ShopifyProductRepository;
import com.athlunakms.webhook.repository.ShopifyProductVariantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.athlunakms.webhook.entity.ShopifyProductVariant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InventoryProcessor
implements WebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger(InventoryProcessor.class);
    private final ShopifyProductVariantRepository variantRepository;
    private final ShopifyProductRepository productRepository;

    public boolean supports(String topic) {
        return topic != null && (topic.startsWith("inventory_levels/") || topic.startsWith("variants/"));
    }

    public String getCategory() {
        return "inventory";
    }

    @Transactional
    public void process(String topic, String shopDomain, JsonNode payload) {
        log.info("[InventoryProcessor] Processing topic={} for shop={}", topic, shopDomain);
        switch (topic) {
            case "inventory_levels/update": 
            case "inventory_levels/connect": {
                this.handleInventoryUpdate(shopDomain, payload);
                break;
            }
            case "variants/in_stock": 
            case "variants/out_of_stock": {
                this.handleVariantStockChange(shopDomain, payload, topic);
                break;
            }
            default: {
                log.info("[InventoryProcessor] Ignored topic: {}", topic);
            }
        }
    }

    private void handleInventoryUpdate(String shopDomain, JsonNode payload) {
        Long inventoryItemId = payload.path("inventory_item_id").asLong();
        int available = payload.path("available").asInt(0);
        log.info("[InventoryProcessor] UPDATE inventory: inventoryItemId={}, available={}", inventoryItemId, available);
        this.variantRepository.findByInventoryItemId(inventoryItemId).ifPresentOrElse(variant -> {
            variant.setInventoryQuantity(available);
            this.variantRepository.save(variant);
            log.info("[InventoryProcessor] Variant inventory updated: variantId={}, qty={}", variant.getShopifyVariantId(), available);
            this.updateProductTotalInventory(variant.getProductId());
        }, () -> log.warn("[InventoryProcessor] Variant not found for inventoryItemId={}", inventoryItemId));
    }

    private void updateProductTotalInventory(Long productId) {
        if (productId == null) {
            return;
        }
        this.productRepository.findById(productId).ifPresent(product -> {
            List<ShopifyProductVariant> variants = this.variantRepository.findByProductId(productId);
            int totalInventory = variants.stream().mapToInt(v -> v.getInventoryQuantity() != null ? v.getInventoryQuantity() : 0).sum();
            product.setTotalInventory(totalInventory);
            this.productRepository.save(product);
            log.info("[InventoryProcessor] Product total inventory updated: productId={}, total={}", productId, totalInventory);
        });
    }

    private void handleVariantStockChange(String shopDomain, JsonNode payload, String topic) {
        Long variantId = payload.path("id").asLong();
        int inventoryQty = payload.path("inventory_quantity").asInt(0);
        log.info("[InventoryProcessor] {} variant: variantId={}, qty={}", topic, variantId, inventoryQty);
        this.variantRepository.findByShopifyVariantId(variantId).ifPresent(variant -> {
            variant.setInventoryQuantity(inventoryQty);
            this.variantRepository.save(variant);
            log.info("[InventoryProcessor] Variant stock updated: variantId={}, qty={}", variantId, inventoryQty);
        });
    }

    public InventoryProcessor(ShopifyProductVariantRepository variantRepository, ShopifyProductRepository productRepository) {
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
    }
}

