package com.athlunakms.ai.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JwtAuthService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isExpired(String token) {
        try {
            Date exp = parseToken(token).getExpiration();
            return exp == null || exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public InnerUserContext toUserContext(String token) {
        Claims claims = parseToken(token);
        InnerUserContext ctx = new InnerUserContext();
        ctx.setUsername(claims.getSubject());
        Object rolesObj = claims.get("roles");
        List<String> roles = rolesObj instanceof List ? (List<String>) rolesObj : Collections.emptyList();
        ctx.setRole(mapAiRole(roles));
        return ctx;
    }

    private String mapAiRole(List<String> roles) {
        for (String role : roles) {
            if (!StringUtils.hasText(role)) {
                continue;
            }
            String r = role.toUpperCase();
            if (r.contains("ADMIN") || role.contains("管理员")) {
                return "ADMIN";
            }
            if (r.contains("BUSINESS") || role.contains("商务")) {
                return "BUSINESS";
            }
        }
        return "STAFF";
    }
}
