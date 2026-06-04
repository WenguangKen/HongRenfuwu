package com.athlunakms.influencer.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(value=1)
public class RateLimitFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    @Value(value="${app.rate-limit.enabled:true}")
    private boolean enabled;
    @Value(value="${app.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;
    private static final int MAX_BUCKETS = 10000;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> accessOrder = new ConcurrentLinkedQueue<>();

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.enabled) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String uri = request.getRequestURI();
        if (uri.contains("/actuator") || uri.contains("/health")) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            return;
        }
        String clientIp = this.getClientIp(request);
        Bucket bucket = this.buckets.computeIfAbsent(clientIp, key -> {
            this.evictIfNeeded();
            this.accessOrder.add(key);
            return this.createBucket(key);
        });
        if (bucket.tryConsume(1L)) {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        } else {
            log.warn("Rate limit exceeded for IP: {}", (Object)clientIp);
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 429, \"message\": \"\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5\"}");
        }
    }

    private void evictIfNeeded() {
        String oldest;
        while (this.buckets.size() >= MAX_BUCKETS && (oldest = (String)this.accessOrder.poll()) != null) {
            this.buckets.remove(oldest);
        }
    }

    private Bucket createBucket(String key) {
        Bandwidth limit = Bandwidth.classic((long)this.requestsPerMinute, (Refill)Refill.greedy((long)this.requestsPerMinute, (Duration)Duration.ofMinutes(1L)));
        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

