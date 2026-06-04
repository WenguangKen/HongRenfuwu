package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerCooperation;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InfluencerCooperationRepository
extends JpaRepository<InfluencerCooperation, Long> {
    public List<InfluencerCooperation> findByInfluencerIdOrderByCreatedAtDesc(Long var1);
    
    public java.util.Optional<InfluencerCooperation> findByRemittanceTaskId(Long remittanceTaskId);

    public boolean existsByInfluencerIdAndAmount(Long var1, BigDecimal var2);

    @Query(value="SELECT c.influencerId, c.currency, SUM(c.amount) FROM InfluencerCooperation c WHERE c.influencerId IN :influencerIds AND c.amount > 0 GROUP BY c.influencerId, c.currency")
    public List<Object[]> sumAmountByInfluencerIds(@Param(value="influencerIds") List<Long> var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerCooperation c WHERE c.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerCooperation c WHERE c.remittanceTaskId = :remittanceTaskId")
    public void deleteByRemittanceTaskId(@Param(value="remittanceTaskId") Long remittanceTaskId);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM InfluencerCooperation c WHERE c.amount = 0")
    public void deleteZeroAmountCooperations();
}

