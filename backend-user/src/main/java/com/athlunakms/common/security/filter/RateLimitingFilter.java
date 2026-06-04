package com.athlunakms.common.security.filter;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.util.IPUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitingFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final RedisTemplate<String, Object> redisTemplate;
    @Value(value="${app.login.ip-rate-limit:5}")
    private int ipRateLimit;
    @Value(value="${app.login.email-rate-limit:10}")
    private int emailRateLimit;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ipAddress;
        String path = request.getRequestURI();
        if (path.contains("/auth/login") && !this.checkRateLimit("ip:" + (ipAddress = IPUtils.getClientIP(request)), this.ipRateLimit, 60L, TimeUnit.SECONDS)) {
            log.warn("IP\u9650\u6d41\u89e6\u53d1: ip={}, path={}", ipAddress, path);
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED, "\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5");
        }
        filterChain.doFilter(request, response);
    }

    private boolean checkRateLimit(String key, int limit, long window, TimeUnit timeUnit) {
        String redisKey = "rate_limit:" + key;
        Integer count = (Integer)this.redisTemplate.opsForValue().get(redisKey);
        if (count == null) {
            this.redisTemplate.opsForValue().set(redisKey, 1, window, timeUnit);
            return true;
        }
        if (count >= limit) {
            return false;
        }
        this.redisTemplate.opsForValue().increment(redisKey);
        return true;
    }

    public RateLimitingFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

