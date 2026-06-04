package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.InfluencerSampleOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerSampleOrderItemRepository
extends JpaRepository<InfluencerSampleOrderItem, Long> {
    public List<InfluencerSampleOrderItem> findBySampleOrderId(Long var1);

    public void deleteBySampleOrderId(Long var1);
}

