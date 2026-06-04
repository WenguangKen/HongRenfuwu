package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerConversionOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionOrderRepository
extends JpaRepository<InfluencerConversionOrder, Long> {
    @Query(value="SELECT COUNT(c), SUM(c.totalPrice), SUM(c.commissionAmount) FROM InfluencerConversionOrder c WHERE c.influencerId = :influencerId")
    public List<Object[]> getAggregatedStatsByInfluencerId(@Param(value="influencerId") Long var1);

    @Query(value="SELECT c.discountCode, COUNT(c) FROM InfluencerConversionOrder c WHERE c.influencerId = :influencerId AND c.discountCode IS NOT NULL AND c.discountCode != '' GROUP BY c.discountCode ORDER BY COUNT(c) DESC")
    public List<Object[]> findDiscountCodeUsageByInfluencerId(@Param(value="influencerId") Long var1);

    public long countByInfluencerId(Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerConversionOrder c WHERE c.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);
}

