package com.athlunakms.shopify.product.repository;

import com.athlunakms.shopify.product.entity.ShopifyProductVariant;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductVariantRepository
extends JpaRepository<ShopifyProductVariant, Long> {
    public List<ShopifyProductVariant> findByProductId(Long var1);

    public List<ShopifyProductVariant> findByProductIdIn(Collection<Long> var1);

    public Optional<ShopifyProductVariant> findByShopifyVariantId(Long var1);

    public Optional<ShopifyProductVariant> findFirstBySkuIgnoreCase(String var1);

    @Modifying
    @Query(value="DELETE FROM ShopifyProductVariant v WHERE v.productId = :productId")
    public void deleteByProductId(@Param(value="productId") Long var1);

    @Lock(value=LockModeType.PESSIMISTIC_WRITE)
    @Query(value="SELECT v FROM ShopifyProductVariant v WHERE v.id = :id")
    public Optional<ShopifyProductVariant> findByIdWithLock(@Param(value="id") Long var1);

    @Query(value="SELECT DISTINCT v.sku FROM ShopifyProductVariant v WHERE v.sku IS NOT NULL AND v.sku != '' AND v.sku != 'null' ORDER BY v.sku")
    public List<String> findDistinctSkus();
}

