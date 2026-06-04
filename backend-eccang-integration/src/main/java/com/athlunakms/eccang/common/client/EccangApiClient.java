package com.athlunakms.eccang.common.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Component
public class EccangApiClient {
    private static final Logger log = LoggerFactory.getLogger(EccangApiClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${eccang.api.url:https://openapi.eccang.com/openapi/v1}")
    private String defaultApiUrl;

    public EccangApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate((ClientHttpRequestFactory) factory);
    }

    /**
     * 发送易仓 API 请求
     *
     * @param appKey          全局 AppKey
     * @param appSecret       全局 AppSecret (用于签名)
     * @param interfaceMethod 调用的接口名称，例如 getWmsProductList
     * @param bizContentNode  业务参数 JSON 节点
     * @return 业务响应的 data 节点
     */
    public JsonNode invokeApi(String appKey, String appSecret, String interfaceMethod, ObjectNode bizContentNode) {
        try {
            String bizContentStr = objectMapper.writeValueAsString(bizContentNode);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            String version = "V1.0.0";
            
            // TODO: 生成真正的 AES 签名。当前暂时发送空字符串或占位符，如果易仓服务器严格校验则会报错。
            // 实际可能需要使用 appSecret 作为密钥，对特定参数做 AES 加密。
            String sign = generateSign(appKey, appSecret, interfaceMethod, bizContentStr, timestamp, nonceStr);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("app_key", appKey);
            requestBody.put("biz_content", bizContentStr);
            requestBody.put("charset", "UTF-8");
            requestBody.put("interface_method", interfaceMethod);
            requestBody.put("nonce_str", nonceStr);
            // 暂时使用默认服务ID，如果是动态的可以在配置中加
            requestBody.put("service_id", "EEFNT7");
            requestBody.put("sign", sign);
            requestBody.put("sign_type", "AES");
            requestBody.put("timestamp", timestamp);
            requestBody.put("version", version);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            log.info("Requesting Eccang API: method={}, url={}", interfaceMethod, defaultApiUrl);
            ResponseEntity<String> response = restTemplate.exchange(defaultApiUrl, HttpMethod.POST, entity, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Eccang API HTTP Error: status={}", response.getStatusCode());
                throw new RuntimeException("Eccang API error: HTTP " + response.getStatusCode());
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            String code = root.path("code").asText();
            if (!"200".equals(code)) {
                String msg = root.path("message").asText();
                log.error("Eccang API logic error: code={}, msg={}", code, msg);
                throw new RuntimeException("Eccang API response error: " + msg);
            }

            String respBizContentStr = root.path("biz_content").asText();
            JsonNode bizContentResp = objectMapper.readTree(respBizContentStr);
            
            String bizCode = bizContentResp.path("code").asText();
            if (!"200".equals(bizCode)) {
                throw new RuntimeException("Eccang Biz Error: " + bizContentResp.path("message").asText());
            }

            return bizContentResp.path("data");

        } catch (Exception e) {
            log.error("Failed to invoke Eccang API [{}]: {}", interfaceMethod, e.getMessage(), e);
            throw new RuntimeException("Failed to invoke Eccang API: " + e.getMessage());
        }
    }

    /**
     * 获取产品列表 (getWmsProductList)
     */
    public List<JsonNode> getWmsProductList(String appKey, String appSecret, int page, int pageSize) {
        ObjectNode bizContent = objectMapper.createObjectNode();
        bizContent.put("page", page);
        bizContent.put("page_size", pageSize);
        // 可以根据需要加入更多参数，例如产品状态、时间等
        
        JsonNode dataNode = invokeApi(appKey, appSecret, "getWmsProductList", bizContent);
        
        List<JsonNode> products = new ArrayList<>();
        JsonNode dataArray = dataNode.path("data");
        if (dataArray.isArray()) {
            for (JsonNode item : dataArray) {
                products.add(item);
            }
        }
        return products;
    }

    /**
     * TODO: 待完善真实的易仓签名算法
     */
    private String generateSign(String appKey, String appSecret, String method, String bizContent, String timestamp, String nonce) {
        // 在没有拿到易仓签名算法 Demo 之前，暂时返回一个假的签名字符串。
        // 如果文档中提到是 AES 对 biz_content 加密之类，请在此处实现。
        return "mock_signature_to_be_replaced_with_actual_aes_hash";
    }
}
