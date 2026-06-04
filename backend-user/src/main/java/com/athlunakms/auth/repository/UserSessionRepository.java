package com.athlunakms.auth.repository;

import com.athlunakms.auth.entity.UserSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository
extends JpaRepository<UserSession, Long> {
    public Optional<UserSession> findByTokenHash(String var1);

    @Query(value="SELECT s FROM UserSession s WHERE s.userId = :userId AND s.isActive = true")
    public Optional<UserSession> findActiveSessionByUserId(@Param(value="userId") Long var1);

    public List<UserSession> findByUserId(Long var1);

    @Query(value="SELECT s FROM UserSession s WHERE s.expiresAt < :now")
    public List<UserSession> findExpiredSessions(@Param(value="now") LocalDateTime var1);

    @Query(value="SELECT s FROM UserSession s WHERE s.lastActivityTime < :timeoutTime AND s.isActive = true")
    public List<UserSession> findTimeoutSessions(@Param(value="timeoutTime") LocalDateTime var1);

    @Modifying
    @Query(value="UPDATE UserSession s SET s.isActive = false WHERE s.id = :sessionId")
    public void deactivateSession(@Param(value="sessionId") Long var1);

    @Modifying
    @Query(value="UPDATE UserSession s SET s.isActive = false WHERE s.userId = :userId AND s.isActive = true")
    public void deactivateAllUserSessions(@Param(value="userId") Long var1);
}

