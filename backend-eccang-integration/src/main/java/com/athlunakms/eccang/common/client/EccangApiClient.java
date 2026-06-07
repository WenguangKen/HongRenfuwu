package com.athlunakms.eccang.common.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class EccangApiClient {
    private static final Logger log = LoggerFactory.getLogger(EccangApiClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 易仓业务层限流错误码：1010=频率超限, 1011=并发超限, 1012=配额超限 */
    private static final Set<String> RATE_LIMIT_BIZ_CODES = Set.of("1010", "1011", "1012");

    @Value("${eccang.api.url:http://openapi-web.eccang.com/openApi/api/unity}")
    private String apiUrl;

    @Value("${eccang.api.service-id:}")
    private String serviceId;

    @Value("${eccang.sync.rate-limit-retries:5}")
    private int rateLimitRetries;

    @Value("${eccang.sync.rate-limit-backoff-ms:10000}")
    private long rateLimitBackoffMs;

    public EccangApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate((ClientHttpRequestFactory) factory);
    }

    public JsonNode invokeApi(String appKey, String appSecret, String interfaceMethod, ObjectNode bizContentNode) {
        if (!StringUtils.hasText(serviceId)) {
            throw new RuntimeException("未配置易仓 service_id，请在易仓后台「授权状态→查看」获取后配置 eccang.api.service-id");
        }
        int attempt = 0;
        while (true) {
            try {
                String bizContentStr = objectMapper.writeValueAsString(bizContentNode);
                String timestamp = String.valueOf(System.currentTimeMillis());
                String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

                Map<String, String> signParams = new LinkedHashMap<>();
                signParams.put("app_key", appKey);
                signParams.put("biz_content", bizContentStr);
                signParams.put("charset", "UTF-8");
                signParams.put("interface_method", interfaceMethod);
                signParams.put("nonce_str", nonceStr);
                signParams.put("service_id", serviceId);
                signParams.put("sign_type", "MD5");
                signParams.put("timestamp", timestamp);
                signParams.put("version", "V1.0.0");

                String sign = generateMd5Sign(signParams, appSecret);

                ObjectNode requestBody = objectMapper.createObjectNode();
                requestBody.put("app_key", appKey);
                requestBody.put("biz_content", bizContentStr);
                requestBody.put("charset", "UTF-8");
                requestBody.put("interface_method", interfaceMethod);
                requestBody.put("nonce_str", nonceStr);
                requestBody.put("service_id", serviceId);
                requestBody.put("sign", sign);
                requestBody.put("sign_type", "MD5");
                requestBody.put("timestamp", timestamp);
                requestBody.put("version", "V1.0.0");

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

                log.info("Requesting Eccang API: method={}, url={}, attempt={}", interfaceMethod, apiUrl, attempt + 1);
                ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    log.error("Eccang API HTTP Error: status={}", response.getStatusCode());
                    throw new RuntimeException("Eccang API error: HTTP " + response.getStatusCode());
                }

                JsonNode root = objectMapper.readTree(response.getBody());
                String code = root.path("code").asText();

                // 外层 code 限流判定（1010/1011/1012）
                if (RATE_LIMIT_BIZ_CODES.contains(code)) {
                    attempt++;
                    if (attempt > rateLimitRetries) {
                        String msg = root.path("message").asText(root.path("msg").asText("rate limited"));
                        throw new RuntimeException(String.format(
                            "易仓 API 限流(code=%s)，已重试 %d 次仍失败: %s", code, rateLimitRetries, msg));
                    }
                    long waitMs = rateLimitBackoffMs * (1L << (attempt - 1));
                    log.warn("Eccang API biz rate limit code={}, method={}, retry {}/{} after {}ms",
                        code, interfaceMethod, attempt, rateLimitRetries, waitMs);
                    Thread.sleep(waitMs);
                    continue;
                }

                if (!"200".equals(code) && !"0000".equals(code)) {
                    String msg = root.path("message").asText(root.path("msg").asText("unknown error"));
                    log.error("Eccang API logic error: code={}, msg={}", code, msg);
                    throw new RuntimeException("Eccang API response error: " + msg);
                }

                JsonNode bizContentResp;
                if (root.path("biz_content").isTextual()) {
                    bizContentResp = objectMapper.readTree(root.path("biz_content").asText());
                } else {
                    bizContentResp = root.path("biz_content");
                }

                String bizCode = bizContentResp.path("code").asText("200");

                // 内层 biz_content.code 限流判定（1010/1011/1012）
                if (RATE_LIMIT_BIZ_CODES.contains(bizCode)) {
                    attempt++;
                    if (attempt > rateLimitRetries) {
                        String msg = bizContentResp.path("message").asText(bizContentResp.path("msg").asText("rate limited"));
                        throw new RuntimeException(String.format(
                            "易仓 API 业务限流(biz_code=%s)，已重试 %d 次仍失败: %s", bizCode, rateLimitRetries, msg));
                    }
                    long waitMs = rateLimitBackoffMs * (1L << (attempt - 1));
                    log.warn("Eccang API biz_content rate limit code={}, method={}, retry {}/{} after {}ms",
                        bizCode, interfaceMethod, attempt, rateLimitRetries, waitMs);
                    Thread.sleep(waitMs);
                    continue;
                }

                if (!"200".equals(bizCode) && !"0000".equals(bizCode)) {
                    throw new RuntimeException("Eccang Biz Error: " + bizContentResp.path("message").asText(bizContentResp.path("msg").asText()));
                }

                return bizContentResp.path("data");

            } catch (HttpClientErrorException.TooManyRequests e) {
                attempt++;
                if (attempt > rateLimitRetries) {
                    log.error("Eccang API rate limited after {} retries: method={}", rateLimitRetries, interfaceMethod);
                    throw new RuntimeException("易仓 API 请求频率超限(429)，已重试 " + rateLimitRetries + " 次仍失败，请稍后再试");
                }
                long waitMs = rateLimitBackoffMs * (1L << (attempt - 1));
                log.warn("Eccang API 429 rate limit, method={}, retry {}/{} after {}ms",
                    interfaceMethod, attempt, rateLimitRetries, waitMs);
                try {
                    Thread.sleep(waitMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("易仓 API 请求被中断");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("易仓 API 请求被中断");
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.error("Failed to invoke Eccang API [{}]: {}", interfaceMethod, e.getMessage(), e);
                throw new RuntimeException("Failed to invoke Eccang API: " + e.getMessage());
            }
        }
    }

    public List<JsonNode> getWmsProductList(String appKey, String appSecret, int page, int pageSize) {
        return getWmsProductList(appKey, appSecret, page, pageSize, null, null);
    }

    public List<JsonNode> getWmsProductList(String appKey, String appSecret, int page, int pageSize, String updateDateFrom, String updateDateTo) {
        ObjectNode bizContent = objectMapper.createObjectNode();
        bizContent.put("page", page);
        bizContent.put("page_size", pageSize);
        // docId=504：请求时带上分类/自定义属性开关
        bizContent.put("getProperty", 1);
        bizContent.put("getProductCustomCategory", 1);
        bizContent.put("getProductCombination", 1);
        if (StringUtils.hasText(updateDateFrom)) {
            bizContent.put("update_date_from", updateDateFrom);
        }
        if (StringUtils.hasText(updateDateTo)) {
            bizContent.put("update_date_to", updateDateTo);
        }
        return extractList(invokeApi(appKey, appSecret, "getWmsProductList", bizContent));
    }

    /** 易仓开放平台 docId=737：WMS-获取产品列表（需 ERP 产品权限，部分应用仅授权 WMS） */
    public List<JsonNode> getProductList(String appKey, String appSecret, int page, int pageSize) {
        ObjectNode bizContent = objectMapper.createObjectNode();
        bizContent.put("page", page);
        bizContent.put("pageSize", pageSize);
        bizContent.put("getProductCombination", 1);
        // docId=504：产品分类属性
        bizContent.put("getProperty", 1);
        bizContent.put("getProductCustomCategory", 1);
        return extractList(invokeApi(appKey, appSecret, "getProductList", bizContent));
    }

    /** 亚马逊 Listing 列表（父 ASIN、站点、账号、真实图片、Seller SKU） */
    public List<JsonNode> getAmazonListing(String appKey, String appSecret, int page, int pageSize,
            List<String> userAccounts, String site) {
        return getAmazonListing(appKey, appSecret, page, pageSize, userAccounts, site, null, null);
    }

    public List<JsonNode> getAmazonListing(String appKey, String appSecret, int page, int pageSize,
            List<String> userAccounts, String site, String updateDateFrom, String updateDateTo) {
        ObjectNode bizContent = objectMapper.createObjectNode();
        bizContent.put("page", page);
        bizContent.put("page_size", pageSize);
        if (userAccounts != null && !userAccounts.isEmpty()) {
            ArrayNode accounts = objectMapper.createArrayNode();
            for (String account : userAccounts) {
                if (StringUtils.hasText(account)) {
                    accounts.add(account.trim());
                }
            }
            if (!accounts.isEmpty()) {
                bizContent.set("user_account", accounts);
            }
        }
        if (StringUtils.hasText(site)) {
            bizContent.put("site", site.trim());
        }
        if (StringUtils.hasText(updateDateFrom)) {
            bizContent.put("update_date_from", updateDateFrom);
        }
        if (StringUtils.hasText(updateDateTo)) {
            bizContent.put("update_date_to", updateDateTo);
        }
        return extractList(invokeApi(appKey, appSecret, "AmazonListing", bizContent));
    }

    /** 易仓开放平台 docId=445：FBA 库存（含 ASIN、站点账号、可售库存） */
    public List<JsonNode> getFbaInventory(String appKey, String appSecret, int page, int pageSize) {
        ObjectNode bizContent = objectMapper.createObjectNode();
        bizContent.put("page", page);
        bizContent.put("page_size", pageSize);
        return extractList(invokeApi(appKey, appSecret, "getFbaInventory", bizContent));
    }



    private List<JsonNode> extractList(JsonNode dataNode) {
        List<JsonNode> items = new ArrayList<>();
        JsonNode dataArray = dataNode.isArray() ? dataNode : dataNode.path("data");
        if (dataArray.isArray()) {
            for (JsonNode item : dataArray) {
                items.add(item);
            }
        }
        return items;
    }

    private String generateMd5Sign(Map<String, String> params, String appSecret) throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                sb.append('&');
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            first = false;
        }
        sb.append(appSecret);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
