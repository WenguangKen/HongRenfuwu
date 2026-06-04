package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.ShopifyProductVariant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyProductVariantRepository
extends JpaRepository<ShopifyProductVariant, Long> {
    public Optional<ShopifyProductVariant> findByShopifyVariantId(Long var1);

    public Optional<ShopifyProductVariant> findByInventoryItemId(Long var1);

    public List<ShopifyProductVariant> findByShopifyProductId(Long var1);

    public List<ShopifyProductVariant> findByProductId(Long var1);

    public void deleteByShopifyProductId(Long var1);
}

