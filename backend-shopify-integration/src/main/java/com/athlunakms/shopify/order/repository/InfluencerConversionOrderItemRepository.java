package com.athlunakms.shopify.order.repository;

import com.athlunakms.shopify.order.entity.InfluencerConversionOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionOrderItemRepository
extends JpaRepository<InfluencerConversionOrderItem, Long> {
    public List<InfluencerConversionOrderItem> findByConversionOrderId(Long var1);

    public List<InfluencerConversionOrderItem> findByConversionOrderIdIn(List<Long> var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerConversionOrderItem i WHERE i.conversionOrderId = :conversionOrderId")
    public void deleteByConversionOrderId(@Param(value="conversionOrderId") Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerConversionOrderItem i WHERE i.conversionOrderId IN :ids")
    public void deleteByConversionOrderIdIn(@Param(value="ids") List<Long> var1);
}

