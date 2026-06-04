package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.InfluencerConversionOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionOrderItemRepository
extends JpaRepository<InfluencerConversionOrderItem, Long> {
    public List<InfluencerConversionOrderItem> findByConversionOrderId(Long var1);

    public void deleteByConversionOrderId(Long var1);
}

