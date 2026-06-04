package com.athlunakms.webhook.controller;

import com.athlunakms.webhook.service.WebhookService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/webhookfw-shopify"})
public class ShopifyWebhookController {
    private static final Logger log = LoggerFactory.getLogger(ShopifyWebhookController.class);
    private final WebhookService webhookService;

    @PostMapping(value={"/products"})
    public ResponseEntity<?> handleProductWebhook(@RequestHeader(value="X-Shopify-Topic", required=false) String topic, @RequestHeader(value="X-Shopify-Shop-Domain", required=false) String shopDomain, @RequestHeader(value="X-Shopify-API-Version", required=false) String apiVersion, @RequestHeader(value="X-Shopify-Webhook-Id", required=false) String webhookId, @RequestHeader(value="X-Shopify-Hmac-SHA256", required=false) String hmacHeader, @RequestBody String payload) {
        log.info("[Products] Received webhook: topic={}, shop={}, webhookId={}", topic, shopDomain, webhookId);
        return this.processWebhook("products", topic, shopDomain, apiVersion, webhookId, hmacHeader, payload);
    }

    @PostMapping(value={"/inventory"})
    public ResponseEntity<?> handleInventoryWebhook(@RequestHeader(value="X-Shopify-Topic", required=false) String topic, @RequestHeader(value="X-Shopify-Shop-Domain", required=false) String shopDomain, @RequestHeader(value="X-Shopify-API-Version", required=false) String apiVersion, @RequestHeader(value="X-Shopify-Webhook-Id", required=false) String webhookId, @RequestHeader(value="X-Shopify-Hmac-SHA256", required=false) String hmacHeader, @RequestBody String payload) {
        log.info("[Inventory] Received webhook: topic={}, shop={}, webhookId={}", topic, shopDomain, webhookId);
        return this.processWebhook("inventory", topic, shopDomain, apiVersion, webhookId, hmacHeader, payload);
    }

    @PostMapping(value={"/orders"})
    public ResponseEntity<?> handleOrderWebhook(@RequestHeader(value="X-Shopify-Topic", required=false) String topic, @RequestHeader(value="X-Shopify-Shop-Domain", required=false) String shopDomain, @RequestHeader(value="X-Shopify-API-Version", required=false) String apiVersion, @RequestHeader(value="X-Shopify-Webhook-Id", required=false) String webhookId, @RequestHeader(value="X-Shopify-Hmac-SHA256", required=false) String hmacHeader, @RequestBody String payload) {
        log.info("[Orders] Received webhook: topic={}, shop={}, webhookId={}", topic, shopDomain, webhookId);
        return this.processWebhook("orders", topic, shopDomain, apiVersion, webhookId, hmacHeader, payload);
    }

    @PostMapping
    public ResponseEntity<?> handleGenericWebhook(@RequestHeader(value="X-Shopify-Topic", required=false) String topic, @RequestHeader(value="X-Shopify-Shop-Domain", required=false) String shopDomain, @RequestHeader(value="X-Shopify-API-Version", required=false) String apiVersion, @RequestHeader(value="X-Shopify-Webhook-Id", required=false) String webhookId, @RequestHeader(value="X-Shopify-Hmac-SHA256", required=false) String hmacHeader, @RequestBody String payload) {
        log.info("[Generic] Received webhook: topic={}, shop={}, webhookId={}", topic, shopDomain, webhookId);
        return this.processWebhook("generic", topic, shopDomain, apiVersion, webhookId, hmacHeader, payload);
    }

    private ResponseEntity<?> processWebhook(String category, String topic, String shopDomain, String apiVersion, String webhookId, String hmacHeader, String payload) {
        if (topic == null || shopDomain == null) {
            log.warn("[{}] Missing required webhook headers", category);
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required headers"));
        }
        if (!this.webhookService.verifyWebhook(payload, hmacHeader)) {
            log.warn("[{}] HMAC verification failed for shop: {}", category, shopDomain);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid HMAC signature"));
        }
        try {
            Long eventId = this.webhookService.processWebhook(topic, shopDomain, apiVersion, webhookId, payload);
            if (eventId != null) {
                log.info("[{}] Webhook processed successfully: eventId={}", category, eventId);
                return ResponseEntity.ok(Map.of("success", true, "eventId", eventId, "category", category));
            }
            log.info("[{}] Webhook already processed: webhookId={}", category, webhookId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Already processed"));
        }
        catch (Exception e) {
            log.error("[{}] Error processing webhook", category, e);
            return ResponseEntity.ok(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping(value={"/health"})
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Webhookfw-Shopify", "version", "v1", "endpoints", Map.of("products", "/v1/webhookfw-shopify/products", "inventory", "/v1/webhookfw-shopify/inventory", "orders", "/v1/webhookfw-shopify/orders")));
    }

    @GetMapping(value={"/endpoints"})
    public ResponseEntity<?> getEndpoints() {
        Map<String, Object> productStatus = this.webhookService.getEndpointStatus("products");
        Map<String, Object> inventoryStatus = this.webhookService.getEndpointStatus("inventory");
        Map<String, Object> orderStatus = this.webhookService.getEndpointStatus("orders");
        return ResponseEntity.ok(Map.of("endpoints", List.of(this.createEndpointMap("\u5546\u54c1 (Products)", "/v1/webhookfw-shopify/products", "products", new String[]{"products/create", "products/update", "products/delete"}, productStatus), this.createEndpointMap("\u5e93\u5b58 (Inventory)", "/v1/webhookfw-shopify/inventory", "inventory", new String[]{"inventory_levels/update", "variants/in_stock", "variants/out_of_stock"}, inventoryStatus), this.createEndpointMap("\u8ba2\u5355 (Orders)", "/v1/webhookfw-shopify/orders", "orders", new String[]{"orders/create", "orders/paid", "orders/cancelled", "refunds/create"}, orderStatus))));
    }

    @GetMapping(value={"/status/{category}"})
    public ResponseEntity<?> getEndpointStatus(@PathVariable String category) {
        Map<String, Object> status = this.webhookService.getEndpointStatus(category);
        if (status == null || status.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("data", status));
    }

    private Map<String, Object> createEndpointMap(String name, String path, String category, String[] topics, Map<String, Object> statusInfo) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("name", name);
        result.put("path", path);
        result.put("category", category);
        result.put("topics", topics);
        result.put("status", statusInfo.getOrDefault("status", "unknown"));
        if (statusInfo.containsKey("lastEventAt")) {
            result.put("lastEventAt", statusInfo.get("lastEventAt"));
        }
        if (statusInfo.containsKey("lastPayload")) {
            result.put("lastPayload", statusInfo.get("lastPayload"));
        }
        return result;
    }

    @PostMapping(value={"/test"})
    public ResponseEntity<?> testWebhook(@RequestParam(defaultValue="orders/create") String topic, @RequestParam(defaultValue="test-shop.myshopify.com") String shopDomain, @RequestBody String payload) {
        // 生产环境应通过配置 app.webhook.test-enabled=false 禁用此端点
        String testEnabled = System.getProperty("app.webhook.test-enabled", "false");
        if (!"true".equalsIgnoreCase(testEnabled)) {
            log.warn("[Test] Test webhook endpoint is disabled in this environment");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Test endpoint is disabled"));
        }
        log.info("[Test] Test webhook: topic={}, shop={}", topic, shopDomain);
        try {
            Long eventId = this.webhookService.processWebhook(topic, shopDomain, "2024-01", "test-" + System.currentTimeMillis(), payload);
            return ResponseEntity.ok(Map.of("success", true, "eventId", eventId, "message", "Test webhook processed"));
        }
        catch (Exception e) {
            log.error("Error processing test webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    public ShopifyWebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }
}

