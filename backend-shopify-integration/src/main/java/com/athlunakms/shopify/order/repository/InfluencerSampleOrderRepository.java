package com.athlunakms.shopify.order.repository;

import com.athlunakms.shopify.order.entity.InfluencerSampleOrder;
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
public interface InfluencerSampleOrderRepository
extends JpaRepository<InfluencerSampleOrder, Long>,
JpaSpecificationExecutor<InfluencerSampleOrder> {
    public Optional<InfluencerSampleOrder> findByOrderId(Long var1);

    public boolean existsByOrderId(Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerSampleOrder s WHERE s.orderId = :orderId")
    public void deleteByOrderId(@Param(value="orderId") Long var1);

    public Page<InfluencerSampleOrder> findByInfluencerId(Long var1, Pageable var2);

    public Page<InfluencerSampleOrder> findByCreatedAtBetween(LocalDateTime var1, LocalDateTime var2, Pageable var3);

    public Page<InfluencerSampleOrder> findAllByOrderByCreatedAtDesc(Pageable var1);

    public List<InfluencerSampleOrder> findByInfluencerIdAndCreatedAtAfterOrderByCreatedAtDesc(Long var1, LocalDateTime var2);

    public long countByInfluencerId(Long var1);

    @Query(value="SELECT s.influencerId, COUNT(s) FROM InfluencerSampleOrder s WHERE s.influencerId IN :ids GROUP BY s.influencerId")
    public List<Object[]> countByInfluencerIds(@Param(value="ids") List<Long> var1);

    public List<InfluencerSampleOrder> findByShopifyOrderNumber(Long var1);

    public List<InfluencerSampleOrder> findByInfluencerIdOrderByCreatedAtDesc(Long var1);

    public Optional<InfluencerSampleOrder> findByShopifyOrderId(String var1);
}

