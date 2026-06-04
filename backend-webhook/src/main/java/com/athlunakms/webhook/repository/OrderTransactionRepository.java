package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.OrderTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository
extends JpaRepository<OrderTransaction, Long> {
    public Optional<OrderTransaction> findByShopifyTransactionId(Long var1);

    public List<OrderTransaction> findByShopifyOrderId(String var1);

    public List<OrderTransaction> findByShopifyOrderIdAndKind(String var1, String var2);
}

