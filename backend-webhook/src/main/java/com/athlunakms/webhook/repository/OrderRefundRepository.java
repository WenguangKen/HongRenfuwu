package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.OrderRefund;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRefundRepository
extends JpaRepository<OrderRefund, Long> {
    public Optional<OrderRefund> findByShopifyRefundId(Long var1);

    public Optional<OrderRefund> findByShopifyOrderId(String var1);
}

