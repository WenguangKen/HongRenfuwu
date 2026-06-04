package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerSampleOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerSampleOrderRepository
extends JpaRepository<InfluencerSampleOrder, Long>,
JpaSpecificationExecutor<InfluencerSampleOrder> {
    public long countByInfluencerId(Long var1);

    @Query(value="SELECT s.influencerId, COUNT(s) FROM InfluencerSampleOrder s WHERE s.influencerId IN :influencerIds GROUP BY s.influencerId")
    public List<Object[]> countByInfluencerIds(@Param(value="influencerIds") List<Long> var1);

    @Query(value="SELECT s.influencerId, MAX(s.orderCreatedAt) FROM InfluencerSampleOrder s WHERE s.influencerId IN :influencerIds GROUP BY s.influencerId")
    public List<Object[]> findLatestOrderTimeByInfluencerIds(@Param(value="influencerIds") List<Long> var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerSampleOrder s WHERE s.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);
}

