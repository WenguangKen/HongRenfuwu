package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerChangeLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerChangeLogRepository
extends JpaRepository<InfluencerChangeLog, Long> {
    public List<InfluencerChangeLog> findByInfluencerIdOrderByCreatedAtDesc(Long var1);

    public Page<InfluencerChangeLog> findByInfluencerId(Long var1, Pageable var2);
}

