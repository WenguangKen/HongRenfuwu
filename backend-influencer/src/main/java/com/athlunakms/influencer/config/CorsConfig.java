package com.athlunakms.influencer.config;

import jakarta.servlet.Filter;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Value(value="${app.cors.allowed-origins:http://localhost:5173,http://127.0.0.1:5173}")
    private String allowedOriginsRaw;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfiguration config = new CorsConfiguration();
        // 配置中显式声明的 origin 优先精确匹配；同时放开 OriginPattern("*") 以兼容
        // Cloudflare 隧道等动态域名访问。credentials=true 下不能用 setAllowedOrigins("*")，
        // 必须改用 setAllowedOriginPatterns。
        String raw = this.allowedOriginsRaw == null ? "" : this.allowedOriginsRaw.trim();
        if (!raw.isEmpty()) {
            for (String origin : raw.split(",")) {
                String o = origin.trim();
                if (!o.isEmpty()) {
                    config.addAllowedOriginPattern(o);
                }
            }
        }
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList("Content-Disposition", "X-Total-Count"));
        config.setAllowCredentials(Boolean.valueOf(true));
        config.setMaxAge(Long.valueOf(3600L));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean((Filter)new CorsFilter((CorsConfigurationSource)source), new ServletRegistrationBean[0]);
        bean.setOrder(Integer.MIN_VALUE);
        return bean;
    }
}

