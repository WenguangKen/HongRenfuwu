package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.ShopifyOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopifyOrderRepository
extends JpaRepository<ShopifyOrder, Long> {
    public Optional<ShopifyOrder> findByShopifyOrderId(String var1);
}

