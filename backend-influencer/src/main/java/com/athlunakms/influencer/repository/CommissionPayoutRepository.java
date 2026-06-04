package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.CommissionPayout;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionPayoutRepository
extends JpaRepository<CommissionPayout, Long>,
JpaSpecificationExecutor<CommissionPayout> {
    public List<CommissionPayout> findByInfluencerIdOrderByCreatedAtDesc(Long var1);

    public Page<CommissionPayout> findByInfluencerId(Long var1, Pageable var2);

    public Page<CommissionPayout> findByStatus(String var1, Pageable var2);

    public long countByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2);

    public long countByInfluencerIdAndStatus(Long var1, String var2);

    public long countByInfluencerId(Long var1);
}

