package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerSampleOrderItemRepository
extends JpaRepository<InfluencerSampleOrderItem, Long> {
    public List<InfluencerSampleOrderItem> findBySampleOrderId(Long var1);

    public List<InfluencerSampleOrderItem> findBySampleOrderIdIn(List<Long> var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerSampleOrderItem i WHERE i.sampleOrderId = :sampleOrderId")
    public void deleteBySampleOrderId(@Param(value="sampleOrderId") Long var1);
}

