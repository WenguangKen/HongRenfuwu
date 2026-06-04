package com.athlunakms.ai.controller;

import com.athlunakms.ai.auth.JwtAuthService;
import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 本地排查小A 401：对比用户服务与 AI 服务是否使用同一 JWT 密钥。
 */
@RestController
@RequestMapping("/ai-agent-api/v1")
@RequiredArgsConstructor
public class AuthCheckController {

    private final JwtAuthService jwtAuthService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @GetMapping("/auth-check")
    public Map<String, Object> authCheck(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("jwtSecretFingerprint", fingerprint(jwtSecret));
        result.put("hasAuthorizationHeader", StringUtils.hasText(authorization));

        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            result.put("ok", false);
            result.put("reason", "MISSING_BEARER");
            return result;
        }

        String token = authorization.substring(7).trim();
        result.put("tokenLength", token.length());

        try {
            if (jwtAuthService.isExpired(token)) {
                result.put("ok", false);
                result.put("reason", "EXPIRED_OR_INVALID_SIGNATURE");
                return result;
            }
            Claims claims = jwtAuthService.parseToken(token);
            result.put("ok", true);
            result.put("reason", "OK");
            result.put("subject", claims.getSubject());
            result.put("roles", claims.get("roles"));
            return result;
        } catch (Exception e) {
            result.put("ok", false);
            result.put("reason", "PARSE_ERROR");
            result.put("detail", e.getClass().getSimpleName() + ": " + e.getMessage());
            return result;
        }
    }

    private static String fingerprint(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash, 0, 4);
        } catch (Exception e) {
            return "unknown";
        }
    }
}
