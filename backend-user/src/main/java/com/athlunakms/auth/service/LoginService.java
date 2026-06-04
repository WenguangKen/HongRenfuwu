package com.athlunakms.auth.service;

import com.athlunakms.auth.dto.LoginRequest;
import com.athlunakms.auth.dto.LoginResponse;
import com.athlunakms.auth.entity.UserSession;
import com.athlunakms.auth.repository.UserSessionRepository;
import com.athlunakms.auth.service.CaptchaService;
import com.athlunakms.auth.service.SessionService;
import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.common.security.encryption.BCryptService;
import com.athlunakms.common.security.jwt.JwtTokenService;
import com.athlunakms.common.util.IPUtils;
import com.athlunakms.role.repository.RoleRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import com.athlunakms.user.entity.User;
import com.athlunakms.user.entity.UserLog;
import com.athlunakms.user.repository.UserLogRepository;
import com.athlunakms.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final UserLogRepository userLogRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final BCryptService bcryptService;
    private final AESEncryptionService encryptionService;
    private final JwtTokenService jwtTokenService;
    private final SessionService sessionService;
    private final CaptchaService captchaService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value(value="${app.jwt.expiration:14400000}")
    private Long jwtExpiration;
    @Value(value="${app.login.email-rate-limit:10}")
    private int emailRateLimit;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email;
        User user;
        String ipAddress = IPUtils.getClientIP((HttpServletRequest)httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String account = request.getEmail();
        if (account == null || account.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "\u90ae\u7bb1\u6216\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a");
        }
        boolean isEmail = account.contains("@");
        String emailRateLimitKey = isEmail ? "email_rate_limit:" + account : null;
        String usernameRateLimitKey = isEmail ? null : "username_rate_limit:" + account;
        Integer rateCount = (Integer)this.redisTemplate.opsForValue().get(isEmail ? emailRateLimitKey : usernameRateLimitKey);
        if (rateCount != null && rateCount >= this.emailRateLimit) {
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED, "\u767b\u5f55\u5c1d\u8bd5\u8fc7\u4e8e\u9891\u7e41\uff0c\u8bf71\u5c0f\u65f6\u540e\u518d\u8bd5");
        }
        String captchaExemptKey = "captcha_exempt:" + account;
        Boolean hasCaptchaExemption = this.redisTemplate.hasKey(captchaExemptKey);
        if (!Boolean.TRUE.equals(hasCaptchaExemption) && rateCount != null && rateCount >= 5) {
            this.captchaService.validateCaptcha(request.getCaptchaKey(), request.getCaptcha(), ipAddress);
        }
        log.info("\u5c1d\u8bd5\u767b\u5f55: account={}", account);
        if (isEmail) {
            String emailHash = this.hashEmail(account);
            user = this.userRepository.findByEmailHash(emailHash).orElseThrow(() -> {
                log.warn("\u767b\u5f55\u5931\u8d25: \u90ae\u7bb1\u4e0d\u5b58\u5728 - account={}", account);
                if (rateCount == null) {
                    this.redisTemplate.opsForValue().set(emailRateLimitKey, 1, 12L, TimeUnit.HOURS);
                } else {
                    this.redisTemplate.opsForValue().increment(emailRateLimitKey);
                    this.redisTemplate.expire(emailRateLimitKey, 12L, TimeUnit.HOURS);
                }
                this.saveLoginLog(null, ipAddress, userAgent, false, "\u7528\u6237\u4e0d\u5b58\u5728");
                return new BusinessException(ErrorCode.INVALID_CREDENTIALS, "\u90ae\u7bb1\u6216\u5bc6\u7801\u9519\u8bef");
            });
            email = this.decryptEmail(user.getEmailEncrypted());
        } else {
            user = this.userRepository.findByUsername(account).orElseThrow(() -> {
                log.warn("\u767b\u5f55\u5931\u8d25: \u7528\u6237\u540d\u4e0d\u5b58\u5728 - account={}", account);
                if (rateCount == null) {
                    this.redisTemplate.opsForValue().set(usernameRateLimitKey, 1, 12L, TimeUnit.HOURS);
                } else {
                    this.redisTemplate.opsForValue().increment(usernameRateLimitKey);
                    this.redisTemplate.expire(usernameRateLimitKey, 12L, TimeUnit.HOURS);
                }
                this.saveLoginLog(null, ipAddress, userAgent, false, "\u7528\u6237\u4e0d\u5b58\u5728");
                return new BusinessException(ErrorCode.INVALID_CREDENTIALS, "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef");
            });
            email = this.decryptEmail(user.getEmailEncrypted());
        }
        log.info("\u627e\u5230\u7528\u6237: id={}, username={}", user.getId(), user.getUsername());
        if (user.getStatus() != User.UserStatus.active) {
            throw new BusinessException(ErrorCode.ACCOUNT_INACTIVE, "\u8d26\u6237\u672a\u6fc0\u6d3b");
        }
        boolean passwordMatch = this.bcryptService.matches(request.getPassword(), user.getPasswordHash());
        log.info("\u5bc6\u7801\u9a8c\u8bc1\u7ed3\u679c: match={}, userId={}", (Object)passwordMatch, (Object)user.getId());
        if (!passwordMatch) {
            if (isEmail) {
                if (rateCount == null) {
                    this.redisTemplate.opsForValue().set(emailRateLimitKey, 1, 12L, TimeUnit.HOURS);
                } else {
                    this.redisTemplate.opsForValue().increment(emailRateLimitKey);
                    this.redisTemplate.expire(emailRateLimitKey, 12L, TimeUnit.HOURS);
                }
            } else if (rateCount == null) {
                this.redisTemplate.opsForValue().set(usernameRateLimitKey, 1, 12L, TimeUnit.HOURS);
            } else {
                this.redisTemplate.opsForValue().increment(usernameRateLimitKey);
                this.redisTemplate.expire(usernameRateLimitKey, 12L, TimeUnit.HOURS);
            }
            this.saveLoginLog(user.getId(), ipAddress, userAgent, false, "\u5bc6\u7801\u9519\u8bef");
            log.warn("\u767b\u5f55\u5931\u8d25\uff1a\u5bc6\u7801\u9519\u8bef - account={}, ip={}", account, ipAddress);
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, isEmail ? "\u90ae\u7bb1\u6216\u5bc6\u7801\u9519\u8bef" : "\u7528\u6237\u540d\u6216\u5bc6\u7801\u9519\u8bef");
        }
        List<Long> roleIds = this.userRoleRepository.findRoleIdsByUserId(user.getId());
        List<LoginResponse.UserInfo.RoleInfo> roleInfos = roleIds.stream().flatMap(roleId -> this.roleRepository.findById(roleId).stream()).map(role -> {
            LoginResponse.UserInfo.RoleInfo info = new LoginResponse.UserInfo.RoleInfo();
            info.setId(role.getId());
            info.setName(role.getName());
            info.setDescription(role.getDescription());
            return info;
        }).collect(Collectors.toList());
        List<String> roleNames = roleInfos.stream().map(LoginResponse.UserInfo.RoleInfo::getName).map(name -> "\u8d85\u7ea7\u7ba1\u7406\u5458".equals(name) ? "ROLE_ADMIN" : name).collect(Collectors.toList());
        String tempToken = this.jwtTokenService.generateToken(user.getId(), email, Long.valueOf(0L), roleNames);
        UserSession session = this.sessionService.createSession(user.getId(), tempToken, ipAddress, userAgent);
        String token = this.jwtTokenService.generateToken(user.getId(), email, session.getId(), roleNames);
        String refreshToken = this.jwtTokenService.generateRefreshToken(user.getId(), email);
        String tokenHash = this.jwtTokenService.hashToken(token);
        session.setTokenHash(tokenHash);
        this.sessionRepository.save(session);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setLastLoginLocation(IPUtils.getLocation(ipAddress));
        user.setLastActivityTime(LocalDateTime.now());
        this.userRepository.save(user);
        this.saveLoginLog(user.getId(), ipAddress, userAgent, true, null);
        String rateLimitKey = isEmail ? emailRateLimitKey : usernameRateLimitKey;
        this.redisTemplate.delete(rateLimitKey);
        this.redisTemplate.opsForValue().set(captchaExemptKey, "1", 6L, TimeUnit.HOURS);
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(Long.valueOf(this.jwtExpiration / 1000L));
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(this.decryptEmail(user.getEmailEncrypted()));
        userInfo.setAvatarUrl(user.getAvatarUrl());
        response.setUser(userInfo);
        userInfo.setPhone(this.decryptPhone(user.getPhoneEncrypted()));
        userInfo.setLastLoginIp(user.getLastLoginIp());
        userInfo.setLastLoginLocation(user.getLastLoginLocation());
        userInfo.setLastLoginTime(user.getLastLoginTime());
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setStatus(user.getStatus());
        userInfo.setRoles(roleInfos);
        if (isEmail) {
            this.redisTemplate.delete(emailRateLimitKey);
        } else {
            this.redisTemplate.delete(usernameRateLimitKey);
        }
        this.saveLoginLog(user.getId(), ipAddress, userAgent, true, null);
        log.info("\u7528\u6237\u767b\u5f55\u6210\u529f: userId={}, email={}, ip={}", user.getId(), email, ipAddress);
        return response;
    }

    private void saveLoginLog(Long userId, String ipAddress, String userAgent, boolean success, String failureReason) {
        try {
            UserLog userLog = new UserLog();
            userLog.setUserId(userId);
            userLog.setActionType("\u767b\u5f55");
            userLog.setActionDesc(success ? "\u767b\u5f55\u6210\u529f" : "\u767b\u5f55\u5931\u8d25: " + failureReason);
            userLog.setIpAddress(ipAddress);
            userLog.setLocation(IPUtils.getLocation(ipAddress));
            userLog.setUserAgent(userAgent);
            HashMap<String, Object> details = new HashMap<String, Object>();
            details.put("success", success);
            if (failureReason != null) {
                details.put("failureReason", failureReason);
            }
            userLog.setDetails(this.objectMapper.writeValueAsString(details));
            userLog.setCreatedAt(LocalDateTime.now());
            this.userLogRepository.save(userLog);
        }
        catch (Exception e) {
            log.error("\u4fdd\u5b58\u767b\u5f55\u65e5\u5fd7\u5931\u8d25", (Throwable)e);
        }
    }

    private String hashEmail(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.toLowerCase().getBytes(StandardCharsets.UTF_8));
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
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u8ba1\u7b97\u90ae\u7bb1\u54c8\u5e0c\u5931\u8d25");
        }
    }

    private String decryptEmail(String encryptedEmail) {
        try {
            return this.encryptionService.decrypt(encryptedEmail);
        }
        catch (Exception e) {
            log.error("\u89e3\u5bc6\u90ae\u7bb1\u5931\u8d25", (Throwable)e);
            return "***";
        }
    }

    private String decryptPhone(String encryptedPhone) {
        try {
            return encryptedPhone != null ? this.encryptionService.decrypt(encryptedPhone) : null;
        }
        catch (Exception e) {
            log.error("\u89e3\u5bc6\u7535\u8bdd\u5931\u8d25", (Throwable)e);
            return "***";
        }
    }

    public LoginService(UserRepository userRepository, UserSessionRepository sessionRepository, UserLogRepository userLogRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, BCryptService bcryptService, AESEncryptionService encryptionService, JwtTokenService jwtTokenService, SessionService sessionService, CaptchaService captchaService, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.userLogRepository = userLogRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.bcryptService = bcryptService;
        this.encryptionService = encryptionService;
        this.jwtTokenService = jwtTokenService;
        this.sessionService = sessionService;
        this.captchaService = captchaService;
        this.redisTemplate = redisTemplate;
    }
}

