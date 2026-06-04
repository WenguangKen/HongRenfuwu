package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.InfluencerConversionOrder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionOrderRepository
extends JpaRepository<InfluencerConversionOrder, Long>,
JpaSpecificationExecutor<InfluencerConversionOrder> {
    public Optional<InfluencerConversionOrder> findByOrderId(Long var1);

    public boolean existsByOrderId(Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerConversionOrder c WHERE c.orderId = :orderId")
    public void deleteByOrderId(@Param(value="orderId") Long var1);

    public Page<InfluencerConversionOrder> findByInfluencerId(Long var1, Pageable var2);

    public Page<InfluencerConversionOrder> findByCommissionStatus(String var1, Pageable var2);

    public Page<InfluencerConversionOrder> findByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2, Pageable var3);

    public Page<InfluencerConversionOrder> findAllByOrderByCreatedAtDesc(Pageable var1);

    public Page<InfluencerConversionOrder> findByDiscountCode(String var1, Pageable var2);

    @Query(value="SELECT o.influencerId, COUNT(o) FROM InfluencerConversionOrder o WHERE o.influencerId IN :ids GROUP BY o.influencerId")
    public List<Object[]> countByInfluencerIds(@Param(value="ids") List<Long> var1);
}

