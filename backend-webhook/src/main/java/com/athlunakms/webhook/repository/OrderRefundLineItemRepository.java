package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.OrderRefundLineItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRefundLineItemRepository
extends JpaRepository<OrderRefundLineItem, Long> {
    public Optional<OrderRefundLineItem> findByShopifyRefundLineId(String var1);

    public List<OrderRefundLineItem> findByRefundId(Long var1);
}

