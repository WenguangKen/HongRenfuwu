package com.athlunakms.shopify.order.repository;

import com.athlunakms.shopify.order.entity.ShopifyOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyOrderRepository
extends JpaRepository<ShopifyOrder, Long> {
    public Optional<ShopifyOrder> findByShopifyOrderId(String var1);

    public Optional<ShopifyOrder> findByShopifyOrderNumber(Long var1);

    public Optional<ShopifyOrder> findByName(String var1);

    public List<ShopifyOrder> findByStoreId(Long var1);

    public List<ShopifyOrder> findByStoreIdOrderByCreatedAtShopifyDesc(Long var1);

    public List<ShopifyOrder> findAllByOrderByCreatedAtShopifyDesc();

    public List<ShopifyOrder> findAllByShopifyOrderNumber(Long var1);

    public boolean existsByName(String var1);

    public boolean existsByShopifyOrderNumber(Long var1);

    public List<ShopifyOrder> findByDiscountCodesContaining(String var1);

    public Optional<ShopifyOrder> findFirstByNameStartingWithOrderByNameDesc(String var1);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ShopifyOrder o SET o.influencerId = :influencerId, o.influencerName = :influencerName WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    void updateInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId, @org.springframework.data.repository.query.Param("influencerName") String influencerName);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ShopifyOrder o SET o.influencerId = NULL, o.influencerName = NULL WHERE o.discountCodes LIKE %:discountCode% AND o.influencerId = :influencerId AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    void clearInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ShopifyOrder o SET o.influencerId = NULL, o.influencerName = NULL WHERE o.discountCodes LIKE %:discountCode% AND o.influencerId = :influencerId")
    void clearAllInfluencerIdByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode, @org.springframework.data.repository.query.Param("influencerId") Long influencerId);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM ShopifyOrder o WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled')")
    long countUnsettledByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) FROM ShopifyOrder o WHERE o.discountCodes LIKE %:discountCode% AND (o.commissionStatus IS NULL OR o.commissionStatus != 'settled') AND (o.financialStatus NOT IN ('refunded', 'partially_refunded')) AND (o.cancelledAtShopify IS NULL)")
    long countCommissionableByDiscountCode(@org.springframework.data.repository.query.Param("discountCode") String discountCode);
}
