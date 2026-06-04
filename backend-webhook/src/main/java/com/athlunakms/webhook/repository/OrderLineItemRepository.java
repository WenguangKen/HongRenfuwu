package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.OrderLineItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineItemRepository
extends JpaRepository<OrderLineItem, Long> {
    public List<OrderLineItem> findByOrderId(Long var1);

    public Optional<OrderLineItem> findByShopifyLineItemId(String var1);

    public void deleteByOrderId(Long var1);
}

