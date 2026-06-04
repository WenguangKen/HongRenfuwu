package com.athlunakms.auth.service;

import com.athlunakms.auth.entity.UserSession;
import com.athlunakms.auth.repository.UserSessionRepository;
import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.jwt.JwtTokenService;
import com.athlunakms.common.util.LocationUtils;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);
    private final UserSessionRepository sessionRepository;
    private final JwtTokenService jwtTokenService;
    private final LocationUtils locationUtils;
    @Value(value="${app.login.session-timeout:14400}")
    private int sessionTimeout;

    @Transactional
    public UserSession createSession(Long userId, String token, String ipAddress, String deviceInfo) {
        this.sessionRepository.findActiveSessionByUserId(userId).ifPresent(existingSession -> {
            log.info("\u7528\u6237\u5df2\u6709\u6d3b\u8dc3\u4f1a\u8bdd\uff0c\u5c06\u5931\u6548\u65e7\u4f1a\u8bdd: userId={}, sessionId={}", (Object)userId, (Object)existingSession.getId());
            this.sessionRepository.deactivateSession(existingSession.getId());
        });
        String tokenHash = token != null ? this.jwtTokenService.hashToken(token) : "";
        String location = this.locationUtils.getLocationByIP(ipAddress);
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setTokenHash(tokenHash);
        session.setIpAddress(ipAddress);
        session.setLocation(location);
        session.setDeviceInfo(deviceInfo);
        session.setLoginTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusSeconds(this.sessionTimeout));
        session.setIsActive(true);
        session = this.sessionRepository.save(session);
        log.info("\u521b\u5efa\u7528\u6237\u4f1a\u8bdd: userId={}, sessionId={}, ip={}", userId, session.getId(), ipAddress);
        return session;
    }

    @Transactional
    public void updateActivityTime(Long sessionId) {
        this.sessionRepository.findById(sessionId).ifPresent(session -> {
            LocalDateTime now = LocalDateTime.now();
            if (session.getLastActivityTime() == null || session.getLastActivityTime().isBefore(now.minusMinutes(1L))) {
                session.setLastActivityTime(now);
                // 滑动会话：只要有活动，不仅更新活动时间，也重置绝对过期时间
                session.setExpiresAt(now.plusSeconds(this.sessionTimeout));
                this.sessionRepository.save(session);
            }
        });
    }

    @Transactional
    public void updateToken(Long sessionId, String newToken) {
        this.sessionRepository.findById(sessionId).ifPresent(session -> {
            String tokenHash = this.jwtTokenService.hashToken(newToken);
            session.setTokenHash(tokenHash);
            session.setExpiresAt(LocalDateTime.now().plusSeconds(this.sessionTimeout));
            this.sessionRepository.save(session);
            log.debug("\u4f1a\u8bddToken\u5df2\u66f4\u65b0: sessionId={}", (Object)sessionId);
        });
    }

    public UserSession findByToken(String token) {
        Long sessionId = this.jwtTokenService.getSessionIdFromToken(token);
        if (sessionId != null && sessionId > 0) {
            return this.sessionRepository.findById(sessionId).orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID, "会话不存在"));
        }
        
        // Fallback for tokens without a valid sessionId
        String tokenHash = this.jwtTokenService.hashToken(token);
        return this.sessionRepository.findByTokenHash(tokenHash).orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID, "会话不存在"));
    }

    public void validateSession(UserSession session) {
        if (!session.getIsActive().booleanValue()) {
            throw new BusinessException(ErrorCode.SESSION_EXPIRED, "\u4f1a\u8bdd\u5df2\u5931\u6548");
        }
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setIsActive(false);
            this.sessionRepository.save(session);
            throw new BusinessException(ErrorCode.SESSION_EXPIRED, "\u4f1a\u8bdd\u5df2\u8fc7\u671f");
        }
        LocalDateTime timeoutTime = LocalDateTime.now().minusSeconds(this.sessionTimeout);
        if (session.getLastActivityTime().isBefore(timeoutTime)) {
            session.setIsActive(false);
            this.sessionRepository.save(session);
            throw new BusinessException(ErrorCode.SESSION_EXPIRED, "\u4f1a\u8bdd\u5df2\u8d85\u65f6");
        }
    }

    @Transactional
    public void deactivateSession(Long sessionId) {
        this.sessionRepository.deactivateSession(sessionId);
        log.info("\u4f1a\u8bdd\u5df2\u5931\u6548: sessionId={}", (Object)sessionId);
    }

    @Transactional
    public void deactivateAllUserSessions(Long userId) {
        this.sessionRepository.deactivateAllUserSessions(userId);
        log.info("\u7528\u6237\u6240\u6709\u4f1a\u8bdd\u5df2\u5931\u6548: userId={}", (Object)userId);
    }

    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeoutTime = now.minusSeconds(this.sessionTimeout);
        this.sessionRepository.findExpiredSessions(now).forEach(session -> {
            session.setIsActive(false);
            this.sessionRepository.save(session);
        });
        this.sessionRepository.findTimeoutSessions(timeoutTime).forEach(session -> {
            session.setIsActive(false);
            this.sessionRepository.save(session);
        });
    }

    public SessionService(UserSessionRepository sessionRepository, JwtTokenService jwtTokenService, LocationUtils locationUtils) {
        this.sessionRepository = sessionRepository;
        this.jwtTokenService = jwtTokenService;
        this.locationUtils = locationUtils;
    }
}

