package com.athlunakms.ai.embedding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 火山方舟多模态向量化（/embeddings/multimodal）
 * Doubao-embedding-vision 接入点不能走 LangChain4j 默认的 /embeddings 路径。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VolcengineMultimodalEmbeddingModel implements EmbeddingModel {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.volcengine.embedding.api-key}")
    private String apiKey;

    @Value("${app.volcengine.embedding.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${app.volcengine.embedding.endpoint-id}")
    private String endpointId;

    @Value("${app.volcengine.embedding.dimensions:1024}")
    private int dimensions;

    @Override
    public Response<Embedding> embed(String text) {
        return Response.from(Embedding.from(embedVector(text)));
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> segments) {
        List<Embedding> embeddings = new ArrayList<>();
        for (TextSegment segment : segments) {
            embeddings.add(Embedding.from(embedVector(segment.text())));
        }
        return Response.from(embeddings);
    }

    private float[] embedVector(String text) {
        try {
            String url = baseUrl.replaceAll("/$", "") + "/embeddings/multimodal";

            Map<String, Object> body = new HashMap<>();
            body.put("model", endpointId);
            body.put("dimensions", dimensions);
            body.put("input", List.of(Map.of("type", "text", "text", text)));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new IllegalStateException("向量化 HTTP 失败: " + response.getStatusCode());
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode embeddingNode = root.at("/data/embedding");
            if (embeddingNode.isMissingNode() || !embeddingNode.isArray()) {
                embeddingNode = root.at("/data/0/embedding");
            }
            if (embeddingNode.isMissingNode() || !embeddingNode.isArray()) {
                throw new IllegalStateException("无法解析向量化响应: " + response.getBody());
            }

            float[] vector = new float[embeddingNode.size()];
            for (int i = 0; i < embeddingNode.size(); i++) {
                vector[i] = (float) embeddingNode.get(i).asDouble();
            }
            return vector;
        } catch (Exception e) {
            log.error("火山方舟多模态向量化失败 endpoint={}", endpointId, e);
            throw new RuntimeException("向量化失败: " + e.getMessage(), e);
        }
    }
}
