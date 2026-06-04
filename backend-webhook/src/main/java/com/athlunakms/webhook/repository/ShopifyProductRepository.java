package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.ShopifyProduct;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductRepository
extends JpaRepository<ShopifyProduct, Long> {
    public Optional<ShopifyProduct> findByShopifyProductId(Long var1);
}

