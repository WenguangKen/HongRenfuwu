package com.athlunakms.influencer.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ShopifyIntegrationClient {
    private static final Logger log = LoggerFactory.getLogger(ShopifyIntegrationClient.class);
    private final RestTemplate restTemplate;
    @Value(value="${services.shopify-integration.url:http://localhost:8081}")
    private String shopifyIntegrationUrl;

    public Map<Long, Integer> getSampleOrderCounts(List<Long> influencerIds) {
        if (influencerIds == null || influencerIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            String url = UriComponentsBuilder.fromUriString((String)(this.shopifyIntegrationUrl + "/api/shopify/v1/influencer-orders/stats/sample-counts")).queryParam("influencerIds", influencerIds.toArray()).toUriString();
            log.debug("Calling shopify-integration service: {}", (Object)url);
            ResponseEntity<Map<Long, Integer>> response = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<Long, Integer>>() {}, new Object[0]);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        }
        catch (Exception e) {
            log.warn("Failed to get sample order counts from shopify-integration service: {}", (Object)e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Map<Long, Integer> getConversionOrderCounts(List<Long> influencerIds) {
        if (influencerIds == null || influencerIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            String url = UriComponentsBuilder.fromUriString((String)(this.shopifyIntegrationUrl + "/api/shopify/v1/influencer-orders/stats/conversion-counts")).queryParam("influencerIds", influencerIds.toArray()).toUriString();
            log.debug("Calling shopify-integration service: {}", (Object)url);
            ResponseEntity<Map<Long, Integer>> response = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<Long, Integer>>() {}, new Object[0]);
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        }
        catch (Exception e) {
            log.warn("Failed to get conversion order counts from shopify-integration service: {}", (Object)e.getMessage());
            return Collections.emptyMap();
        }
    }

    public void bindInfluencer(String orderNo, Long influencerId, String influencerName) {
        try {
            String url = this.shopifyIntegrationUrl + "/api/shopify/v1/influencer-orders/bind";
            Map<String, Object> request = Map.of("orderNo", orderNo, "influencerId", influencerId, "influencerName", influencerName != null ? influencerName : "");
            this.restTemplate.postForObject(url, request, Void.class);
            log.info("Successfully bound order {} to influencer {}", orderNo, influencerId);
        }
        catch (Exception e) {
            log.error("Failed to bind order {} to influencer {}: {}", orderNo, influencerId, e.getMessage());
            throw new RuntimeException("Failed to bind order to influencer", e);
        }
    }

    public boolean checkOrderExists(String orderNo) {
        try {
            String url = UriComponentsBuilder.fromUriString((String)(this.shopifyIntegrationUrl + "/api/shopify/v1/influencer-orders/check-order")).queryParam("orderNo", new Object[]{orderNo}).toUriString();
            ResponseEntity<Map<String, Object>> response = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {});
            if (response.getBody() != null && response.getBody().containsKey("exists")) {
                return Boolean.TRUE.equals(response.getBody().get("exists"));
            }
            return false;
        }
        catch (Exception e) {
            log.warn("Failed to check order existence: {}", (Object)e.getMessage());
            return false;
        }
    }

    public ShopifyIntegrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
