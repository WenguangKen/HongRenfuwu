package com.athlunakms.shopify.sync.service;

import com.athlunakms.shopify.common.security.EncryptionService;
import com.athlunakms.shopify.store.entity.ShopifyStore;
import com.athlunakms.shopify.store.repository.ShopifyStoreRepository;
import com.athlunakms.shopify.sync.service.ProvinceSyncService;
import com.athlunakms.user.entity.Province;
import com.athlunakms.user.repository.ProvinceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ProvinceSyncService {
    private static final Logger log = LoggerFactory.getLogger(ProvinceSyncService.class);
    private final ShopifyStoreRepository storeRepository;
    private final ProvinceRepository provinceRepository;
    private final EncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public SyncResult syncProvinces(Long storeId) {
        log.info("Starting province sync for storeId: {}", (Object)storeId);
        ShopifyStore store = this.storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found: " + storeId));
        String storeUrl = this.encryptionService.decrypt(store.getStoreUrlEncrypted());
        String accessToken = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
        if (!storeUrl.startsWith("http")) {
            storeUrl = "https://" + storeUrl;
        }
        if (!storeUrl.endsWith("/")) {
            storeUrl = storeUrl + "/";
        }
        int totalCountries = 0;
        int totalProvinces = 0;
        String url = storeUrl + "admin/api/2024-01/countries.json?limit=250";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Shopify-Access-Token", accessToken);
        try {
            while (url != null) {
                JsonNode countries;
                log.info("Fetching countries from: {}", url);
                HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
                ResponseEntity<JsonNode> response = this.restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
                JsonNode root = response.getBody();
                if (root != null && root.has("countries") && (countries = root.get("countries")).isArray()) {
                    log.info("Received {} countries in this page", countries.size());
                    for (JsonNode countryNode : countries) {
                        int provincesInCountry = this.processCountry(countryNode, store.getId());
                        ++totalCountries;
                        totalProvinces += provincesInCountry;
                    }
                }
                if ((url = this.getNextPageUrl(response.getHeaders())) == null) continue;
                log.info("Found next page: {}", url);
            }
            log.info("Province sync completed for storeId: {}. Countries: {}, Provinces: {}", new Object[]{storeId, totalCountries, totalProvinces});
            return new SyncResult(totalCountries, totalProvinces);
        }
        catch (Exception e) {
            log.error("Failed to sync provinces for storeId: {}", storeId, e);
            throw new RuntimeException("Sync failed: " + e.getMessage());
        }
    }

    private String getNextPageUrl(HttpHeaders responseHeaders) {
        List<String> linkHeaders = responseHeaders.get("Link");
        if (linkHeaders != null) {
            for (String link : linkHeaders) {
                String[] parts;
                for (String part : parts = link.split(",")) {
                    if (!part.contains("rel=\"next\"") && !part.contains("rel=next")) continue;
                    String urlPart = part.split(";")[0].trim();
                    return urlPart.replace("<", "").replace(">", "");
                }
            }
        }
        return null;
    }

    private int processCountry(JsonNode countryNode, Long storeId) {
        String countryName = countryNode.path("name").asText();
        String countryCode = countryNode.path("code").asText();
        long countryId = countryNode.path("id").asLong();
        JsonNode provinces = countryNode.path("provinces");
        int count = 0;
        if (provinces.isArray() && provinces.size() > 0) {
            log.info("Processing country: {} ({}) with {} provinces", new Object[]{countryName, countryCode, provinces.size()});
            for (JsonNode prov : provinces) {
                this.saveProvince(prov, Long.valueOf(countryId), countryCode, countryName, storeId);
                ++count;
            }
        } else {
            log.debug("Country {} ({}) has no provinces", countryName, countryCode);
        }
        return count;
    }

    private void saveProvince(JsonNode provNode, Long countryId, String countryCode, String countryName, Long storeId) {
        String code = provNode.path("code").asText();
        String name = provNode.path("name").asText();
        Optional<Province> existing = this.provinceRepository.findByCountryCodeAndProvinceCode(countryCode, code);
        Province province = existing.orElse(new Province());
        province.setStoreId(storeId);
        province.setCountryId(countryId);
        province.setCountryCode(countryCode);
        province.setCountryName(countryName);
        province.setProvinceId(Long.valueOf(provNode.path("id").asLong()));
        province.setProvinceCode(code);
        province.setProvinceName(name);
        if (provNode.has("tax")) {
            province.setTax(new BigDecimal(provNode.path("tax").asText("0")));
        }
        if (provNode.has("tax_percentage")) {
            province.setTaxPercentage(new BigDecimal(provNode.path("tax_percentage").asText("0")));
        }
        province.setTaxName(provNode.path("tax_name").asText(null));
        province.setEnabled(Boolean.valueOf(true));
        this.provinceRepository.save(province);
    }

    public ProvinceSyncService(ShopifyStoreRepository storeRepository, ProvinceRepository provinceRepository, EncryptionService encryptionService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.storeRepository = storeRepository;
        this.provinceRepository = provinceRepository;
        this.encryptionService = encryptionService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public static class SyncResult {
        private final int totalCountries;
        private final int totalProvinces;

        public SyncResult(int totalCountries, int totalProvinces) {
            this.totalCountries = totalCountries;
            this.totalProvinces = totalProvinces;
        }

        public int getTotalCountries() {
            return totalCountries;
        }

        public int getTotalProvinces() {
            return totalProvinces;
        }
    }
}


