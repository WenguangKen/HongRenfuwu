package com.athlunakms.shopify.order.repository;

import com.athlunakms.shopify.order.entity.OrderLineItem;
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
    @Query(value="DELETE FROM OrderLineItem li WHERE li.shopifyLineItemId IN :shopifyLineItemIds")
    public void deleteByShopifyLineItemIdIn(@Param(value="shopifyLineItemIds") List<String> var1);

    public Optional<OrderLineItem> findByShopifyLineItemId(String var1);
}

