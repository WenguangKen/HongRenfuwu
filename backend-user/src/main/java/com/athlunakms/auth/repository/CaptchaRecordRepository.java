package com.athlunakms.auth.repository;

import com.athlunakms.auth.entity.CaptchaRecord;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaRecordRepository
extends JpaRepository<CaptchaRecord, Long> {
    public Optional<CaptchaRecord> findByCaptchaKey(String var1);

    @Modifying
    @Query(value="DELETE FROM CaptchaRecord c WHERE c.expiresAt < :now OR (c.isUsed = true AND c.createdAt < :cleanupTime)")
    public void cleanupExpiredCaptchas(@Param(value="now") LocalDateTime var1, @Param(value="cleanupTime") LocalDateTime var2);
}

