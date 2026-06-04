package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.InfluencerChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerChangeLogRepository
extends JpaRepository<InfluencerChangeLog, Long> {
}

