package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.ShopifyStore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyStoreRepository
extends JpaRepository<ShopifyStore, Long> {
    public Optional<ShopifyStore> findByShopDomain(String var1);

    public Optional<ShopifyStore> findByMyshopifyDomain(String var1);
}

