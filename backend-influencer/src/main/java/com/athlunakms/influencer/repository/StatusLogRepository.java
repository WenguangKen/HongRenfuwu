package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.StatusLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusLogRepository
extends JpaRepository<StatusLog, Long> {
    public List<StatusLog> findByInfluencerIdOrderByCreatedAtDesc(Long var1);
}

