package com.athlunakms.common.security.filter;

import com.athlunakms.auth.entity.UserSession;
import com.athlunakms.auth.service.SessionService;
import com.athlunakms.common.security.jwt.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter
extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenService jwtTokenService;
    private final SessionService sessionService;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = this.extractToken(request);
            if (StringUtils.hasText(token)) {
                if (this.jwtTokenService.isTokenExpired(token)) {
                    log.debug("JWT Token\u5df2\u8fc7\u671f");
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"\u767b\u5f55\u5df2\u8fc7\u671f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55\"}");
                    return;
                }
                UserSession session = this.sessionService.findByToken(token);
                this.sessionService.validateSession(session);
                this.sessionService.updateActivityTime(session.getId());
                Long userId = this.jwtTokenService.getUserIdFromToken(token);
                String email = this.jwtTokenService.getEmailFromToken(token);
                List<String> roles = this.jwtTokenService.getRolesFromToken(token);
                List<SimpleGrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                if (authorities.isEmpty()) {
                    authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                }
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, email, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                Date expiration = this.jwtTokenService.getExpirationDateFromToken(token);
                long remainingTime = expiration.getTime() - System.currentTimeMillis();
                if (remainingTime < 3600000L) {
                    String newToken = this.jwtTokenService.generateToken(userId, email, session.getId(), roles);
                    response.setHeader("X-New-Token", newToken);
                    this.sessionService.updateToken(session.getId(), newToken);
                    log.debug("Token\u5df2\u81ea\u52a8\u5237\u65b0\uff0c\u5269\u4f59\u65f6\u95f4: {}\u5206\u949f", remainingTime / 60000L);
                }
            }
        }
        catch (Exception e) {
            log.debug("JWT\u8ba4\u8bc1\u5931\u8d25: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"\u8ba4\u8bc1\u5931\u8d25\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }
        return null;
    }

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, SessionService sessionService) {
        this.jwtTokenService = jwtTokenService;
        this.sessionService = sessionService;
    }
}

