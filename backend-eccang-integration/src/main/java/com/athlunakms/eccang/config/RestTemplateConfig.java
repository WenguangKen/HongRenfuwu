package com.athlunakms.eccang.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Timeout.ofMilliseconds((long)10000L)).setResponseTimeout(Timeout.ofMilliseconds((long)30000L)).setConnectionRequestTimeout(Timeout.ofMilliseconds((long)10000L)).build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setConnectionManager((HttpClientConnectionManager)connectionManager).setDefaultRequestConfig(requestConfig).build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory((HttpClient)httpClient);
        return new RestTemplate((ClientHttpRequestFactory)factory);
    }
}

