package com.athlunakms.ai.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        return path != null
                && (path.contains("/actuator/")
                        || path.contains("/internal/api/")
                        || path.endsWith("/health")
                        || path.endsWith("/auth-check"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractBearer(request);
            if (!StringUtils.hasText(token)) {
                log.warn("小A鉴权失败: 未携带 Authorization Bearer, path={}", request.getRequestURI());
                unauthorized(response);
                return;
            }
            if (jwtAuthService.isExpired(token)) {
                log.warn("小A鉴权失败: Token 过期或签名不匹配, path={}", request.getRequestURI());
                unauthorized(response);
                return;
            }
            InnerUserContextHolder.set(jwtAuthService.toUserContext(token));
        } catch (Exception e) {
            log.warn("小A鉴权失败: {} path={}", e.getMessage(), request.getRequestURI());
            unauthorized(response);
            return;
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            InnerUserContextHolder.clear();
        }
    }

    private String extractBearer(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"请先登录后再使用 AI 助手\"}");
    }
}
