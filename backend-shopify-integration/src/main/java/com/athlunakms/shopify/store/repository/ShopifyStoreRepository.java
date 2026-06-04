package com.athlunakms.shopify.store.repository;

import com.athlunakms.shopify.store.entity.ShopifyStore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyStoreRepository
extends JpaRepository<ShopifyStore, Long> {
    public List<ShopifyStore> findByStatus(String var1);
}

