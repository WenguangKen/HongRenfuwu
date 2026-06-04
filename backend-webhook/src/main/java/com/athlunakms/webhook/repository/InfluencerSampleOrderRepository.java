package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.InfluencerSampleOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerSampleOrderRepository
extends JpaRepository<InfluencerSampleOrder, Long> {
    public Optional<InfluencerSampleOrder> findByShopifyOrderId(String var1);
}

