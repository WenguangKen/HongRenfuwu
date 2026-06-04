package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.EccangOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EccangOrderRepository
extends JpaRepository<EccangOrder, Long> {
    public Optional<EccangOrder> findByEccangOrderId(String var1);

    public Optional<EccangOrder> findByEccangOrderNumber(Long var1);

    public Optional<EccangOrder> findByName(String var1);

    public List<EccangOrder> findByStoreId(Long var1);

    public List<EccangOrder> findByStoreIdOrderByCreatedAtEccangDesc(Long var1);

    public List<EccangOrder> findAllByOrderByCreatedAtEccangDesc();

    public List<EccangOrder> findAllByEccangOrderNumber(Long var1);

    public boolean existsByName(String var1);

    public boolean existsByEccangOrderNumber(Long var1);

    public List<EccangOrder> findByDiscountCodesContaining(String var1);

    public Optional<EccangOrder> findFirstByNameStartingWithOrderByNameDesc(String var1);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE EccangOrder o SET o.influencerId = :influencerId, o.influencerName = :influencerName WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    void updateInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId, @org.springframework.data.repository.query.Param("influencerName") String influencerName);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE EccangOrder o SET o.influencerId = NULL, o.influencerName = NULL WHERE o.discountCodes LIKE %:discountCode% AND o.influencerId = :influencerId AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    void clearInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE EccangOrder o SET o.influencerId = NULL, o.influencerName = NULL WHERE o.discountCodes LIKE %:discountCode% AND o.influencerId = :influencerId")
    void clearAllInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM EccangOrder o WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    long countUnsettledByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM EccangOrder o WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled') AND (o.financialStatus NOT IN ('refunded', 'partially_refunded')) AND (o.cancelledAtEccang IS NULL)")
    long countCommissionableByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode);
}
