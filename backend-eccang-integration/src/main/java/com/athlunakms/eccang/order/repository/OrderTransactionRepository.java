package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.OrderTransaction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTransactionRepository
extends JpaRepository<OrderTransaction, Long> {
    public List<OrderTransaction> findByOrderId(Long var1);

    public List<OrderTransaction> findByOrderIdOrderByCreatedAtEccangDesc(Long var1);

    public Optional<OrderTransaction> findByEccangTransactionId(String var1);

    @Modifying
    @Query(value="DELETE FROM OrderTransaction t WHERE t.orderId = :orderId")
    public void deleteByOrderId(@Param(value="orderId") Long var1);
}

