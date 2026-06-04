package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerCommissionOrder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerCommissionOrderRepository
extends JpaRepository<InfluencerCommissionOrder, Long>,
JpaSpecificationExecutor<InfluencerCommissionOrder> {
    public Optional<InfluencerCommissionOrder> findByConversionOrderId(Long var1);

    public boolean existsByConversionOrderId(Long var1);

    public boolean existsByInfluencerId(Long influencerId);

    public Page<InfluencerCommissionOrder> findByInfluencerId(Long var1, Pageable var2);

    public Page<InfluencerCommissionOrder> findBySettlementStatus(String var1, Pageable var2);

    public List<InfluencerCommissionOrder> findByInfluencerIdAndSettlementStatus(Long var1, String var2);

    @Query(value="SELECT COALESCE(SUM(c.commissionAmount), 0) FROM InfluencerCommissionOrder c WHERE c.influencerId = :influencerId AND c.settlementStatus = 'pending'")
    public BigDecimal sumPendingCommissionByInfluencerId(@Param(value="influencerId") Long var1);

    @Query(value="SELECT COALESCE(SUM(c.commissionAmount), 0) FROM InfluencerCommissionOrder c WHERE c.influencerId = :influencerId AND c.settlementStatus = 'settled'")
    public BigDecimal sumSettledCommissionByInfluencerId(@Param(value="influencerId") Long var1);

    @Query(value="SELECT COUNT(c) FROM InfluencerCommissionOrder c WHERE c.settledAt >= :startTime AND c.settledAt <= :endTime")
    public Long countBySettledAtBetween(@Param(value="startTime") LocalDateTime var1, @Param(value="endTime") LocalDateTime var2);

    public long countByInfluencerIdAndSettlementStatus(Long var1, String var2);

    public Optional<InfluencerCommissionOrder> findFirstByInfluencerIdAndSettlementStatusOrderBySettledAtDesc(Long var1, String var2);

    public Page<InfluencerCommissionOrder> findByInfluencerIdIn(List<Long> var1, Pageable var2);
}

