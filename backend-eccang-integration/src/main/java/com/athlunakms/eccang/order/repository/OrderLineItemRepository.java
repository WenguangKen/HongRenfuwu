package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.OrderLineItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineItemRepository
extends JpaRepository<OrderLineItem, Long> {
    public List<OrderLineItem> findByOrderId(Long var1);

    @Modifying
    @Query(value="DELETE FROM OrderLineItem li WHERE li.orderId = :orderId")
    public void deleteByOrderId(@Param(value="orderId") Long var1);

    @Modifying
    @Query(value="DELETE FROM OrderLineItem li WHERE li.eccangLineItemId IN :eccangLineItemIds")
    public void deleteByEccangLineItemIdIn(@Param(value="eccangLineItemIds") List<String> var1);

    public Optional<OrderLineItem> findByEccangLineItemId(String var1);
}

