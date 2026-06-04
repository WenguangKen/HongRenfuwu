package com.athlunakms.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Value("${app.internal-api.token}")
    private String internalApiToken;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        ClientHttpRequestInterceptor internalAuth = (request, body, execution) -> {
            request.getHeaders().set("X-Internal-Token", internalApiToken);
            return execution.execute(request, body);
        };
        return builder.additionalInterceptors(internalAuth).build();
    }
}
