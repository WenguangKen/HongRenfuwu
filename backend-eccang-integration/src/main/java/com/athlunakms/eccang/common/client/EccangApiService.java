package com.athlunakms.eccang.common.client;


import com.athlunakms.eccang.common.exception.EccangApiException;
import com.athlunakms.eccang.store.dto.ShopInfoDto;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class EccangApiService {
    private static final Logger log = LoggerFactory.getLogger(EccangApiService.class);
    private final RestTemplate restTemplate;
    private static final String ECCANG_API_VERSION = "2025-01";

    public ShopInfoDto getShopInfo(String storeUrl, String accessToken) {
        String normalizedUrl = this.normalizeStoreUrl(storeUrl);
        String apiUrl = String.format("https://%s/admin/api/%s/shop.json", normalizedUrl, ECCANG_API_VERSION);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Eccang-Access-Token", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity requestEntity = new HttpEntity((MultiValueMap)headers);
        try {
            Map shopData;
            Map body;
            log.info("\u9a8c\u8bc1 Eccang \u5e97\u94fa\u8fde\u63a5: {}", (Object)normalizedUrl);
            ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {};
            ResponseEntity<Map<String, Object>> response = this.restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, responseType, new Object[0]);
            if (response.getStatusCode() == HttpStatus.OK && (body = (Map)response.getBody()) != null && (shopData = (Map)body.get("shop")) != null) {
                log.info("Eccang \u5e97\u94fa\u9a8c\u8bc1\u6210\u529f: {}", shopData.get("name"));
                return this.mapToShopInfo(shopData);
            }
            throw new EccangApiException("\u65e0\u6548\u7684\u54cd\u5e94\u683c\u5f0f", "INVALID_RESPONSE");
        }
        catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Eccang \u8ba4\u8bc1\u5931\u8d25: {}", (Object)normalizedUrl);
            throw new EccangApiException("Access Token \u65e0\u6548\u6216\u5df2\u8fc7\u671f", "UNAUTHORIZED", 400);
        }
        catch (HttpClientErrorException.NotFound e) {
            log.warn("Eccang \u5e97\u94fa\u4e0d\u5b58\u5728: {}", (Object)normalizedUrl);
            throw new EccangApiException("\u5e97\u94fa\u4e0d\u5b58\u5728\u6216URL\u683c\u5f0f\u9519\u8bef", "NOT_FOUND", 404);
        }
        catch (HttpClientErrorException.Forbidden e) {
            log.warn("Eccang \u6743\u9650\u4e0d\u8db3: {}", (Object)normalizedUrl);
            throw new EccangApiException("Access Token \u6743\u9650\u4e0d\u8db3", "FORBIDDEN", 400);
        }
        catch (HttpClientErrorException e) {
            log.error("Eccang API \u5ba2\u6237\u7aef\u9519\u8bef: {} - {}", (Object)e.getStatusCode(), (Object)e.getMessage());
            int status = e.getStatusCode().value();
            if (status == 401 || status == 403) {
                status = 400;
            }
            throw new EccangApiException("Eccang API \u9519\u8bef: " + e.getMessage(), "API_ERROR", status);
        }
        catch (HttpServerErrorException e) {
            log.error("Eccang \u670d\u52a1\u5668\u9519\u8bef: {}", (Object)e.getMessage());
            throw new EccangApiException("Eccang \u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528", "SERVER_ERROR", 503);
        }
        catch (ResourceAccessException e) {
            log.error("\u65e0\u6cd5\u8fde\u63a5\u5230 Eccang: {}", (Object)e.getMessage());
            throw new EccangApiException("\u65e0\u6cd5\u8fde\u63a5\u5230 Eccang \u670d\u52a1\u5668\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc", "CONNECTION_ERROR", e);
        }
        catch (EccangApiException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("\u9a8c\u8bc1 Eccang \u5e97\u94fa\u65f6\u53d1\u751f\u672a\u77e5\u9519\u8bef: {}", e.getMessage(), e);
            throw new EccangApiException("\u9a8c\u8bc1\u5931\u8d25: " + e.getMessage(), "UNKNOWN_ERROR", e);
        }
    }

    private String normalizeStoreUrl(String storeUrl) {
        String url = storeUrl.trim().toLowerCase();
        if (url.startsWith("https://")) {
            url = url.substring(8);
        } else if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.contains(".myeccang.com") && !url.contains(".")) {
            url = url + ".myeccang.com";
        }
        return url;
    }

    private ShopInfoDto mapToShopInfo(Map<String, Object> shopData) {
        return ShopInfoDto.builder().id(this.getLongValue(shopData, "id")).name((String)shopData.get("name")).domain((String)shopData.get("domain")).email((String)shopData.get("email")).plan((String)shopData.get("plan_display_name")).currency((String)shopData.get("currency")).timezone((String)shopData.get("iana_timezone")).country((String)shopData.get("country_name")).countryCode((String)shopData.get("country_code")).phone((String)shopData.get("phone")).createdAt((String)shopData.get("created_at")).build();
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number)val).longValue();
        }
        return null;
    }

    public EccangApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}

