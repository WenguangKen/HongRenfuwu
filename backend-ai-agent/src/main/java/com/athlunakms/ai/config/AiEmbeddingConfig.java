package com.athlunakms.ai.config;

import com.athlunakms.ai.embedding.VolcengineMultimodalEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiEmbeddingConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "app.volcengine.embedding", name = "endpoint-id")
    public EmbeddingModel volcengineEmbeddingModel(VolcengineMultimodalEmbeddingModel model) {
        return model;
    }
}
