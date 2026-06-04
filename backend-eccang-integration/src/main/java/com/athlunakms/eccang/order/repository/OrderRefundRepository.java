package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.OrderRefund;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRefundRepository
extends JpaRepository<OrderRefund, Long> {
    public Optional<OrderRefund> findByEccangRefundId(String var1);

    public List<OrderRefund> findByOrderId(Long var1);

    public List<OrderRefund> findByOrderIdOrderByCreatedAtEccangDesc(Long var1);
}

