package com.athlunakms.shopify.product.repository;

import com.athlunakms.shopify.product.entity.ShopifyProduct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductRepository
extends JpaRepository<ShopifyProduct, Long>,
JpaSpecificationExecutor<ShopifyProduct> {
    public Optional<ShopifyProduct> findByShopifyProductId(Long var1);

    public List<ShopifyProduct> findByStoreId(Long var1);

    public List<ShopifyProduct> findByShopifyProductIdIn(Collection<Long> var1);
}

