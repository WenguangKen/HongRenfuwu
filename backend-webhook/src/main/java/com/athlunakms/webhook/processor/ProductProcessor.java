package com.athlunakms.webhook.processor;

import com.athlunakms.webhook.entity.ShopifyProduct;
import com.athlunakms.webhook.entity.ShopifyProductVariant;
import com.athlunakms.webhook.entity.ShopifyStore;
import com.athlunakms.webhook.repository.ShopifyProductRepository;
import com.athlunakms.webhook.repository.ShopifyProductVariantRepository;
import com.athlunakms.webhook.repository.ShopifyStoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductProcessor
implements WebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger(ProductProcessor.class);
    private final ShopifyProductRepository productRepository;
    private final ShopifyProductVariantRepository variantRepository;
    private final ShopifyStoreRepository storeRepository;

    public boolean supports(String topic) {
        return topic != null && topic.startsWith("products/");
    }

    public String getCategory() {
        return "products";
    }

    @Transactional
    public void process(String topic, String shopDomain, JsonNode payload) {
        log.info("[ProductProcessor] Processing topic={} for shop={}", topic, shopDomain);
        Long shopifyProductId = payload.path("id").asLong();
        switch (topic) {
            case "products/create": {
                this.handleProductCreate(shopDomain, shopifyProductId, payload);
                break;
            }
            case "products/update": {
                this.handleProductUpdate(shopDomain, shopifyProductId, payload);
                break;
            }
            case "products/delete": {
                this.handleProductDelete(shopDomain, shopifyProductId);
                break;
            }
            default: {
                log.warn("[ProductProcessor] Unknown topic: {}", (Object)topic);
            }
        }
    }

    private void handleProductCreate(String shopDomain, Long shopifyProductId, JsonNode payload) {
        log.info("[ProductProcessor] CREATE product: id={}", shopifyProductId);
        if (this.productRepository.findByShopifyProductId(shopifyProductId).isPresent()) {
            log.info("[ProductProcessor] Product already exists, updating instead");
            this.handleProductUpdate(shopDomain, shopifyProductId, payload);
            return;
        }
        ShopifyProduct product = new ShopifyProduct();
        this.mapPayloadToProduct(product, shopDomain, payload);
        product.setSyncedAt(LocalDateTime.now());
        this.productRepository.save(product);
        log.info("[ProductProcessor] Product saved: id={}, title={}", product.getId(), product.getTitle());
        this.saveVariants(product.getId(), shopifyProductId, payload);
    }

    private void handleProductUpdate(String shopDomain, Long shopifyProductId, JsonNode payload) {
        log.info("[ProductProcessor] UPDATE product: id={}", shopifyProductId);
        ShopifyProduct product = this.productRepository.findByShopifyProductId(shopifyProductId).orElseGet(() -> {
            log.info("[ProductProcessor] Product not found, creating new");
            return new ShopifyProduct();
        });
        this.mapPayloadToProduct(product, shopDomain, payload);
        product.setSyncedAt(LocalDateTime.now());
        this.productRepository.save(product);
        log.info("[ProductProcessor] Product updated: id={}, title={}", product.getId(), product.getTitle());
        this.saveVariants(product.getId(), shopifyProductId, payload);
    }

    private void handleProductDelete(String shopDomain, Long shopifyProductId) {
        log.info("[ProductProcessor] DELETE product: id={}", (Object)shopifyProductId);
        this.productRepository.findByShopifyProductId(shopifyProductId).ifPresent(product -> {
            product.setStatus("suspected_deleted");
            this.productRepository.save(product);
            log.info("[ProductProcessor] Product marked as suspected_deleted: id={}", (Object)shopifyProductId);
        });
    }

    private void mapPayloadToProduct(ShopifyProduct product, String shopDomain, JsonNode payload) {
        product.setShopifyProductId(Long.valueOf(payload.path("id").asLong()));
        ShopifyStore store = this.storeRepository.findByShopDomain(shopDomain).orElseThrow(() -> new RuntimeException("Store not found for domain: " + shopDomain + ". Please ensure the store is registered in the system."));
        product.setStoreId(store.getId());
        String titleText = payload.path("title").asText(null);
        product.setTitle(titleText);
        product.setHandle(payload.path("handle").asText(null));
        product.setVendor(payload.path("vendor").asText(null));
        product.setProductType(payload.path("product_type").asText(null));
        product.setTags(payload.path("tags").asText(null));
        product.setBodyHtml(payload.path("body_html").asText(null));
        String shopifyStatus = payload.path("status").asText("active");
        int totalInventory = 0;
        JsonNode variants = payload.path("variants");
        if (variants.isArray()) {
            for (JsonNode v : variants) {
                totalInventory += v.path("inventory_quantity").asInt(0);
            }
        }
        product.setTotalInventory(totalInventory);
        if ("archived".equalsIgnoreCase(shopifyStatus)) {
            product.setStatus("archived");
        } else if ("draft".equalsIgnoreCase(shopifyStatus)) {
            product.setStatus("draft");
        } else if ("active".equalsIgnoreCase(shopifyStatus)) {
            if (totalInventory <= 0) {
                product.setStatus("out_of_stock");
            } else {
                product.setStatus("active");
            }
        } else {
            product.setStatus(shopifyStatus);
        }
        JsonNode images = payload.path("images");
        if (images.isArray() && images.size() > 0) {
            product.setImageUrl(images.get(0).path("src").asText(null));
        }
        product.setCreatedAt(this.parseDateTime(payload.path("created_at").asText(null)));
        product.setUpdatedAt(this.parseDateTime(payload.path("updated_at").asText(null)));
    }

    private void saveVariants(Long localProductId, Long shopifyProductId, JsonNode payload) {
        JsonNode variants = payload.path("variants");
        if (!variants.isArray()) {
            return;
        }
        HashSet<Long> payloadVariantIds = new HashSet<Long>();
        for (JsonNode variantNode : variants) {
            Long shopifyVariantId = variantNode.path("id").asLong();
            payloadVariantIds.add(shopifyVariantId);
            ShopifyProductVariant variant = this.variantRepository.findByShopifyVariantId(shopifyVariantId).orElseGet(ShopifyProductVariant::new);
            variant.setShopifyVariantId(shopifyVariantId);
            variant.setShopifyProductId(shopifyProductId);
            variant.setProductId(localProductId);
            variant.setTitle(variantNode.path("title").asText(null));
            variant.setSku(variantNode.path("sku").asText(null));
            variant.setBarcode(variantNode.path("barcode").asText(null));
            variant.setPrice(this.parseBigDecimal(variantNode.path("price").asText(null)));
            variant.setCompareAtPrice(this.parseBigDecimal(variantNode.path("compare_at_price").asText(null)));
            variant.setInventoryItemId(Long.valueOf(variantNode.path("inventory_item_id").asLong(0L)));
            variant.setInventoryQuantity(variantNode.path("inventory_quantity").asInt(0));
            variant.setWeight(this.parseBigDecimal(variantNode.path("weight").asText(null)));
            variant.setWeightUnit(variantNode.path("weight_unit").asText(null));
            variant.setOption1(variantNode.path("option1").asText(null));
            variant.setOption2(variantNode.path("option2").asText(null));
            variant.setOption3(variantNode.path("option3").asText(null));
            variant.setCreatedAt(this.parseDateTime(variantNode.path("created_at").asText(null)));
            variant.setUpdatedAt(this.parseDateTime(variantNode.path("updated_at").asText(null)));
            this.variantRepository.save(variant);
            log.info("[ProductProcessor] Variant saved: id={}, sku={}", variant.getId(), variant.getSku());
        }
        List<ShopifyProductVariant> existingVariants = this.variantRepository.findByProductId(localProductId);
        for (ShopifyProductVariant existing : existingVariants) {
            if (payloadVariantIds.contains(existing.getShopifyVariantId())) continue;
            this.variantRepository.delete(existing);
            log.info("[ProductProcessor] Deleted old variant: id={}, shopifyVariantId={}", existing.getId(), existing.getShopifyVariantId());
        }
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
        }
        catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        }
        catch (Exception e) {
            return null;
        }
    }

    public ProductProcessor(ShopifyProductRepository productRepository, ShopifyProductVariantRepository variantRepository, ShopifyStoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.storeRepository = storeRepository;
    }
}

