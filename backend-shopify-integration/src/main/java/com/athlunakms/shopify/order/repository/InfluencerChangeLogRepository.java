package com.athlunakms.shopify.order.repository;

import com.athlunakms.shopify.order.entity.InfluencerChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerChangeLogRepository
extends JpaRepository<InfluencerChangeLog, Long> {
}

