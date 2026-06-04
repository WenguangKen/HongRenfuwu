package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.InfluencerConversionOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionOrderRepository
extends JpaRepository<InfluencerConversionOrder, Long> {
    public Optional<InfluencerConversionOrder> findByShopifyOrderId(String var1);
}

