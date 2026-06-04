package com.athlunakms.common.security.jwt;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);
    @Value(value="${app.jwt.secret}")
    private String jwtSecret;
    @Value(value="${app.jwt.expiration}")
    private Long jwtExpiration;
    @Value(value="${app.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor((byte[])this.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long userId, String email, Long sessionId, List<String> roles) {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("sessionId", sessionId);
        claims.put("roles", roles);
        return this.createToken(claims, email, this.jwtExpiration);
    }

    public String generateRefreshToken(Long userId, String email) {
        HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "refresh");
        return this.createToken(claims, email, this.refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder().claims(claims).subject(subject).issuedAt(now).expiration(expiryDate).signWith((Key)this.getSigningKey()).compact();
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return (Claims)Jwts.parser().verifyWith(this.getSigningKey()).build().parseSignedClaims((CharSequence)token).getPayload();
        }
        catch (Exception e) {
            log.error("\u89e3\u6790Token\u5931\u8d25", (Throwable)e);
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Token\u65e0\u6548");
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = this.getClaimsFromToken(token);
        Object userId = claims.get((Object)"userId");
        if (userId instanceof Integer) {
            return ((Integer)userId).longValue();
        }
        return (Long)userId;
    }

    public String getEmailFromToken(String token) {
        return (String)this.getClaimFromToken(token, Claims::getSubject);
    }

    public Long getSessionIdFromToken(String token) {
        Claims claims = this.getClaimsFromToken(token);
        Object sessionId = claims.get((Object)"sessionId");
        if (sessionId == null) {
            return null;
        }
        if (sessionId instanceof Integer) {
            return ((Integer)sessionId).longValue();
        }
        return (Long)sessionId;
    }

    public Date getExpirationDateFromToken(String token) {
        return (Date)this.getClaimFromToken(token, Claims::getExpiration);
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = this.getClaimsFromToken(token);
        Object roles = claims.get((Object)"roles");
        if (roles instanceof List) {
            return (List)roles;
        }
        return Collections.emptyList();
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = this.getExpirationDateFromToken(token);
            return expiration.before(new Date());
        }
        catch (Exception e) {
            return true;
        }
    }

    public Boolean validateToken(String token, String email) {
        try {
            String tokenEmail = this.getEmailFromToken(token);
            return tokenEmail.equals(email) && this.isTokenExpired(token) == false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e) {
            log.error("\u8ba1\u7b97Token\u54c8\u5e0c\u5931\u8d25", (Throwable)e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u8ba1\u7b97Token\u54c8\u5e0c\u5931\u8d25");
        }
    }
}

