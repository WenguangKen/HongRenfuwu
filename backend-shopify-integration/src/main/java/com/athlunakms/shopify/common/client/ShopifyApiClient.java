package com.athlunakms.shopify.common.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class ShopifyApiClient {
    private static final Logger log = LoggerFactory.getLogger(ShopifyApiClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SHOPIFY_API_VERSION = "2025-01";
    private static final int PAGE_LIMIT = 250;
    private static final Pattern NEXT_PAGE_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");

    public ShopifyApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(30000);
        this.restTemplate = new RestTemplate((ClientHttpRequestFactory)factory);
    }

    public JsonNode fetchProducts(String storeUrl, String accessToken) {
        return this.fetchProductsGraphQL(storeUrl, accessToken, null);
    }

    public JsonNode fetchProductsWithTimeFilter(String storeUrl, String accessToken, String updatedAtMin, String updatedAtMax) {
        StringBuilder queryBuilder = new StringBuilder();
        if (updatedAtMin != null && !updatedAtMin.isEmpty()) {
            queryBuilder.append("updated_at:>=").append(updatedAtMin);
        }
        if (updatedAtMax != null && !updatedAtMax.isEmpty()) {
            if (queryBuilder.length() > 0) {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append("updated_at:<=").append(updatedAtMax);
        }
        String queryStr = queryBuilder.length() > 0 ? queryBuilder.toString() : null;
        return this.fetchProductsGraphQL(storeUrl, accessToken, queryStr);
    }

    private JsonNode fetchProductsGraphQL(String storeUrl, String accessToken, String queryStr) {
        String graphqlUrl = this.buildGraphqlUrl(storeUrl);
        HttpHeaders headers = this.createHeaders(accessToken);
        ArrayNode allProducts = this.objectMapper.createArrayNode();
        String cursor = null;
        boolean hasNextPage = true;
        int pageCount = 0;
        String graphqlQuery = "query getProducts($cursor: String, $query: String) {\n  products(first: 50, after: $cursor, query: $query) {\n    pageInfo {\n      hasNextPage\n      endCursor\n    }\n    edges {\n      node {\n        ...ProductNode\n      }\n    }\n  }\n}\n\nfragment ProductNode on Product {\n  id\n  legacyResourceId\n  title\n  bodyHtml\n  productType\n  vendor\n  handle\n  onlineStoreUrl\n  status\n  createdAt\n  updatedAt\n  publishedAt\n  options {\n    name\n    values\n  }\n  images(first: 50) {\n    edges {\n      node {\n        id\n        url\n      }\n    }\n  }\n  variants(first: 100) {\n    edges {\n      node {\n        ...VariantNode\n      }\n    }\n  }\n}\n\nfragment VariantNode on ProductVariant {\n  id\n  legacyResourceId\n  title\n  sku\n  barcode\n  price\n  compareAtPrice\n  inventoryQuantity\n  inventoryItem {\n    measurement {\n      weight {\n        value\n        unit\n      }\n    }\n  }\n  position\n  selectedOptions {\n    name\n    value\n  }\n  createdAt\n  updatedAt\n  image {\n    id\n    url\n  }\n}\n";
        try {
            while (hasNextPage) {
                log.info("Fetching products (GraphQL) page {} from: {}", (Object)(++pageCount), (Object)storeUrl);
                ObjectNode requestBody = this.objectMapper.createObjectNode();
                requestBody.put("query", graphqlQuery);
                ObjectNode variables = this.objectMapper.createObjectNode();
                if (cursor != null) {
                    variables.put("cursor", cursor);
                }
                if (queryStr != null) {
                    variables.put("query", queryStr);
                }
                requestBody.set("variables", (JsonNode)variables);
                HttpEntity entity = new HttpEntity((Object)this.objectMapper.writeValueAsString((Object)requestBody), (MultiValueMap)headers);
                ResponseEntity response = this.restTemplate.exchange(graphqlUrl, HttpMethod.POST, entity, String.class, new Object[0]);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode pageInfo;
                    JsonNode root = this.objectMapper.readTree((String)response.getBody());
                    if (root.has("errors")) {
                        log.error("GraphQL errors fetching products: {}", (Object)root.get("errors"));
                        throw new RuntimeException("Shopify GraphQL errors detected: " + root.get("errors").toString());
                    }
                    JsonNode productsData = root.path("data").path("products");
                    JsonNode edges = productsData.path("edges");
                    if (edges.isArray()) {
                        for (JsonNode edge : edges) {
                            allProducts.add(edge.path("node"));
                        }
                        log.info("Page {} fetched: {} products (total: {})", new Object[]{pageCount, edges.size(), allProducts.size()});
                    }
                    if (!(hasNextPage = (pageInfo = productsData.path("pageInfo")).path("hasNextPage").asBoolean(false))) continue;
                    cursor = pageInfo.path("endCursor").asText(null);
                    continue;
                }
                log.error("Failed to fetch products page {}: status={}", (Object)pageCount, (Object)response.getStatusCode());
                throw new RuntimeException("Shopify product fetch failed with status: " + response.getStatusCode());
            }
            log.info("Total products fetched from {}: {}", (Object)storeUrl, (Object)allProducts.size());
            return allProducts;
        }
        catch (Exception e) {
            log.error("Failed to fetch products from Shopify store via GraphQL: {}", (Object)storeUrl, (Object)e);
            throw new RuntimeException("Failed to sync products from Shopify: " + e.getMessage());
        }
    }

    private String normalizeDomain(String storeUrl) {
        String domain = storeUrl.trim().toLowerCase();
        domain = domain.replace("https://", "").replace("http://", "");
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        if (!domain.contains(".myshopify.com") && !domain.contains(".")) {
            domain = domain + ".myshopify.com";
        }
        return domain;
    }

    private String extractNextPageUrl(String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return null;
        }
        Matcher matcher = NEXT_PAGE_PATTERN.matcher(linkHeader);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String buildGraphqlUrl(String storeUrl) {
        String domain = this.normalizeDomain(storeUrl);
        return String.format("https://%s/admin/api/%s/graphql.json", domain, SHOPIFY_API_VERSION);
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Shopify-Access-Token", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

