package com.athlunakms.influencer.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 保护 /internal/api/** 仅供微服务间调用
 */
@Component
@Order(1)
public class InternalApiAuthFilter extends OncePerRequestFilter {

    @Value("${app.internal-api.token}")
    private String internalApiToken;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.contains("/internal/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("X-Internal-Token");
        if (!StringUtils.hasText(internalApiToken) || !internalApiToken.equals(token)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"code\":403,\"message\":\"内部接口鉴权失败\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
