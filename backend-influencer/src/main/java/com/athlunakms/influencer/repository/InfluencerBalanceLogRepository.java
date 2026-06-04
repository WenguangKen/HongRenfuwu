package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerBalanceLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerBalanceLogRepository
extends JpaRepository<InfluencerBalanceLog, Long> {
    public Page<InfluencerBalanceLog> findByInfluencerIdOrderByCreatedAtDesc(Long var1, Pageable var2);

    public List<InfluencerBalanceLog> findByInfluencerIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long var1, LocalDateTime var2, LocalDateTime var3);

    public List<InfluencerBalanceLog> findByReferenceTypeAndReferenceId(String var1, Long var2);
}

