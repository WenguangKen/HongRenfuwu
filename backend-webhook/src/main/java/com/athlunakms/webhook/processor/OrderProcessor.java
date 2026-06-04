package com.athlunakms.webhook.processor;

import com.athlunakms.webhook.entity.ShopifyOrder;
import com.athlunakms.webhook.entity.OrderLineItem;
import com.athlunakms.webhook.entity.OrderTransaction;
import com.athlunakms.webhook.entity.OrderRefund;
import com.athlunakms.webhook.entity.OrderRefundLineItem;
import com.athlunakms.webhook.entity.ShopifyStore;
import com.athlunakms.webhook.entity.InfluencerConversionOrder;
import com.athlunakms.webhook.entity.InfluencerConversionOrderItem;
import com.athlunakms.webhook.entity.InfluencerSampleOrder;
import com.athlunakms.webhook.entity.InfluencerSampleOrderItem;
import com.athlunakms.webhook.entity.InfluencerReadOnly;
import com.athlunakms.webhook.repository.ShopifyOrderRepository;
import com.athlunakms.webhook.repository.OrderLineItemRepository;
import com.athlunakms.webhook.repository.OrderTransactionRepository;
import com.athlunakms.webhook.repository.OrderRefundRepository;
import com.athlunakms.webhook.repository.OrderRefundLineItemRepository;
import com.athlunakms.webhook.repository.ShopifyStoreRepository;
import com.athlunakms.webhook.repository.InfluencerConversionOrderRepository;
import com.athlunakms.webhook.repository.InfluencerConversionOrderItemRepository;
import com.athlunakms.webhook.repository.InfluencerSampleOrderRepository;
import com.athlunakms.webhook.repository.InfluencerSampleOrderItemRepository;
import com.athlunakms.webhook.repository.InfluencerReadOnlyRepository;
import com.athlunakms.webhook.service.SampleOrderAutoMatchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Shopify 订单 Webhook 处理器
 * 处理订单创建、更新、付款、取消及退款等实时事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProcessor {

    private final ShopifyOrderRepository orderRepository;
    private final OrderLineItemRepository lineItemRepository;
    private final OrderTransactionRepository transactionRepository;
    private final OrderRefundRepository refundRepository;
    private final OrderRefundLineItemRepository refundLineItemRepository;
    private final ShopifyStoreRepository storeRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final InfluencerConversionOrderItemRepository conversionItemRepository;
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final InfluencerSampleOrderItemRepository sampleItemRepository;
    private final InfluencerReadOnlyRepository influencerRepository;
    private final SampleOrderAutoMatchService autoMatchService;
    private final ObjectMapper objectMapper;

    // 样品单标记
    private static final String SAMPLE_ORDER_TAG = "influencer order";

    @Transactional
    public void process(String topic, String shopDomain, JsonNode payload) {
        log.info("[OrderProcessor] Processing topic: {}, shop: {}", topic, shopDomain);

        switch (topic) {
            case "orders/create":
                handleOrderCreate(shopDomain, payload);
                break;
            case "orders/updated":
            case "orders/edited":
                handleOrderUpdate(shopDomain, payload);
                break;
            case "orders/paid":
                handleOrderPaid(shopDomain, payload);
                break;
            case "orders/cancelled":
                handleOrderCancelled(shopDomain, payload);
                break;
            case "refunds/create":
                handleRefundCreate(shopDomain, payload);
                break;
            default:
                log.warn("[OrderProcessor] Unsupported topic: {}", topic);
        }
    }

    private void handleOrderCreate(String shopDomain, JsonNode payload) {
        String shopifyOrderId = extractNumericId(payload.path("id").asText());
        log.info("[OrderProcessor] Creating order: id={}, name={}", shopifyOrderId, payload.path("name").asText());

        // 检查是否已存在（幂等）
        ShopifyOrder order = orderRepository.findByShopifyOrderId(shopifyOrderId).orElse(new ShopifyOrder());

        // 映射基本信息
        mapPayloadToOrder(order, payload, shopDomain);
        order.setSyncedAt(LocalDateTime.now());

        ShopifyOrder savedOrder = orderRepository.save(order);
        log.info("[OrderProcessor] Order saved: id={}", savedOrder.getId());

        // 保存行项目
        saveLineItems(savedOrder, payload);

        // 分类逻辑：判断是转化订单还是样品单
        classifyOrder(savedOrder, payload);

        // 处理退货（如果创建时就有退货信息）
        processReturns(payload);
    }

    private void handleOrderUpdate(String shopDomain, JsonNode payload) {
        String shopifyOrderId = extractNumericId(payload.path("id").asText());
        log.info("[OrderProcessor] Updating order: id={}, name={}", shopifyOrderId, payload.path("name").asText());

        orderRepository.findByShopifyOrderId(shopifyOrderId).ifPresentOrElse(order -> {
            // 检查更新时间，避免旧数据覆盖新数据
            LocalDateTime updatedAtShopify = parseDateTime(payload.path("updated_at").asText(null));
            if (order.getUpdatedAtShopify() != null && updatedAtShopify != null
                    && !updatedAtShopify.isAfter(order.getUpdatedAtShopify())) {
                log.info("[OrderProcessor] Order update is not newer than current, skipping basic update");
            } else {
                mapPayloadToOrder(order, payload, shopDomain);
                order.setSyncedAt(LocalDateTime.now());
                orderRepository.save(order);
            }

            // 无论如何尝试同步行项目（可能涉及编辑订单）
            saveLineItems(order, payload);

            // 更新关联的业务表（转化单/样品单）
            updateBusinessOrders(order, payload);

            // 处理退货信息
            processReturns(payload);

        }, () -> {
            log.info("[OrderProcessor] Order {} not found for update, creating instead", shopifyOrderId);
            handleOrderCreate(shopDomain, payload);
        });
    }

    private void classifyOrder(ShopifyOrder order, JsonNode payload) {
        // 1. 优先判断是否带折扣码（转化订单）
        createConversionOrderIfNeeded(order.getShopifyOrderId(), order.getId(), payload);

        // 2. 判断是否带样品标签（样品单）
        createSampleOrderIfNeeded(order, payload);
    }

    private void updateBusinessOrders(ShopifyOrder order, JsonNode payload) {
        String shopifyOrderId = order.getShopifyOrderId();

        // 1. 更新转化订单
        conversionOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(conversion -> {
            log.info("[OrderProcessor] Updating conversion order: {}", shopifyOrderId);
            conversion.setFinancialStatus(payload.path("financial_status").asText(null));
            conversion.setFulfillmentStatus(payload.path("fulfillment_status").asText(null));
            conversion.setTotalPrice(parseBigDecimal(payload.path("total_price").asText(null)));
            conversion.setCancelledAt(parseDateTime(payload.path("cancelled_at").asText(null)));
            conversion.setClosedAt(parseDateTime(payload.path("closed_at").asText(null)));

            // 物流信息
            FulfillmentInfo info = extractFulfillmentInfo(payload);
            if (info != null) {
                conversion.setFulfillmentCreatedAt(info.createdAt);
                conversion.setInTransitAt(info.inTransitAt);
                conversion.setDeliveredAt(info.deliveredAt);
                conversion.setEstimatedDeliveryAt(info.estimatedDeliveryAt);
                conversion.setFulfillmentDisplayStatus(info.displayStatus);
                conversion.setTrackingCompany(info.trackingCompany);
                conversion.setTrackingNumber(info.trackingNumber);
                conversion.setTrackingUrl(info.trackingUrl);
            }

            // 重新计算分佣
            conversion.calculateCommissionableAmount();
            conversion.setUpdatedAt(LocalDateTime.now());

            InfluencerConversionOrder savedConversion = conversionOrderRepository.save(conversion);

            // 更新行项目
            boolean isCancelled = payload.hasNonNull("cancelled_at");
            List<InfluencerConversionOrderItem> newItems = extractConversionItems(payload, savedConversion.getId(),
                    isCancelled);
            List<InfluencerConversionOrderItem> oldItems = conversionItemRepository
                    .findByConversionOrderId(savedConversion.getId());

            if (isConversionItemsChanged(oldItems, newItems)) {
                conversionItemRepository.deleteByConversionOrderId(savedConversion.getId());
                conversionItemRepository.saveAll(newItems);
            }
        });

        // 2. 更新样品单
        sampleOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(sample -> {
            log.info("[OrderProcessor] Updating sample order: {}", shopifyOrderId);
            sample.setFinancialStatus(payload.path("financial_status").asText(null));
            sample.setCancelledAt(parseDateTime(payload.path("cancelled_at").asText(null)));
            sample.setTotalPrice(parseBigDecimal(payload.path("total_price").asText(null)));

            // 物流状态
            FulfillmentInfo info = extractFulfillmentInfo(payload);
            if (info != null) {
                sample.setFulfillmentDisplayStatus(info.displayStatus);
                sample.setTrackingNumber(info.trackingNumber);
                sample.setDeliveredAt(info.deliveredAt);
            }

            sample.setUpdatedAt(LocalDateTime.now());
            InfluencerSampleOrder savedSample = sampleOrderRepository.save(sample);

            // 更新行项目
            boolean isCancelled = payload.hasNonNull("cancelled_at");
            List<InfluencerSampleOrderItem> newItems = extractSampleItems(payload, savedSample.getId(), isCancelled);
            List<InfluencerSampleOrderItem> oldItems = sampleItemRepository.findBySampleOrderId(savedSample.getId());

            if (isSampleItemsChanged(oldItems, newItems)) {
                sampleItemRepository.deleteBySampleOrderId(savedSample.getId());
                sampleItemRepository.saveAll(newItems);
            }
        });
    }

    private void createSampleOrderIfNeeded(ShopifyOrder order, JsonNode payload) {
        String tags = payload.path("tags").asText("");
        if (!tags.toLowerCase().contains(SAMPLE_ORDER_TAG) && !"influencer".equalsIgnoreCase(order.getOrderSource())) {
            return;
        }

        if (sampleOrderRepository.findByShopifyOrderId(order.getShopifyOrderId()).isPresent()) {
            log.info("[OrderProcessor] Sample order already exists: {}", order.getShopifyOrderId());
            return;
        }

        log.info("[OrderProcessor] Creating sample order from webhook: {}", order.getShopifyOrderId());
        InfluencerSampleOrder sample = new InfluencerSampleOrder();
        sample.setOrderId(order.getId());
        sample.setShopifyOrderId(order.getShopifyOrderId());
        sample.setShopifyOrderNumber(payload.path("order_number").asLong(0));
        sample.setOrderName(payload.path("name").asText(null));
        sample.setTotalPrice(order.getTotalPrice());
        sample.setCurrency(order.getCurrency());
        sample.setOrderCreatedAt(order.getCreatedAtShopify());
        sample.setFinancialStatus(order.getFinancialStatus());
        sample.setCancelledAt(order.getCancelledAt());
        sample.setCreatedAt(LocalDateTime.now());
        sample.setUpdatedAt(LocalDateTime.now());

        // 收件人信息
        sample.setRecipientName(order.getShippingName());
        sample.setRecipientPhone(order.getShippingPhone());
        sample.setRecipientAddress(buildFullAddress(payload));
        sample.setRecipientCountry(order.getShippingCountry());
        sample.setCustomerEmail(order.getCustomerEmail());

        // 物流信息初期化
        FulfillmentInfo info = extractFulfillmentInfo(payload);
        if (info != null) {
            sample.setFulfillmentDisplayStatus(info.displayStatus);
            sample.setTrackingNumber(info.trackingNumber);
            sample.setDeliveredAt(info.deliveredAt);
        }

        InfluencerSampleOrder saved = sampleOrderRepository.save(sample);

        // 保存行项目
        boolean isCancelled = payload.hasNonNull("cancelled_at");
        List<InfluencerSampleOrderItem> items = extractSampleItems(payload, saved.getId(), isCancelled);
        sampleItemRepository.saveAll(items);

        // 触发自动匹配逻辑
        autoMatchService.autoMatchInfluencer(saved);
    }

    private String buildFullAddress(JsonNode payload) {
        JsonNode shipping = payload.path("shipping_address");
        if (shipping.isMissingNode())
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(shipping.path("address1").asText("")).append(" ");
        sb.append(shipping.path("address2").asText("")).append(", ");
        sb.append(shipping.path("city").asText("")).append(", ");
        sb.append(shipping.path("province").asText("")).append(" ");
        sb.append(shipping.path("zip").asText("")).append(", ");
        sb.append(shipping.path("country").asText(""));
        return sb.toString().trim().replaceAll(", ,", ",").replaceAll("^, ", "");
    }

    /**
     * 为样品单/转化单提取物流简报
     */
    private FulfillmentInfo extractFulfillmentInfo(JsonNode payload) {
        JsonNode fulfillments = payload.path("fulfillments");
        if (!fulfillments.isArray() || fulfillments.size() == 0) {
            return null;
        }

        FulfillmentInfo info = new FulfillmentInfo();
        JsonNode first = fulfillments.get(0); // 暂时取第一个

        info.trackingCompany = first.path("tracking_company").asText(null);
        info.trackingNumber = first.path("tracking_number").asText(null);
        info.trackingUrl = first.path("tracking_url").asText(null);

        // 提取状态 Display Status
        String status = null;
        if (first.hasNonNull("displayStatus")) {
            status = first.get("displayStatus").asText();
        } else if (first.hasNonNull("display_status")) {
            status = first.get("display_status").asText();
        } else if (first.hasNonNull("shipment_status")) {
            status = first.get("shipment_status").asText();
        } else {
            status = first.path("status").asText(null);
        }

        // 修正逻辑：如果状态是 success/fulfilled 但订单已关闭，视为 delivered
        String closedAt = payload.path("closed_at").asText(null);
        if ((status == null || "success".equals(status) || "fulfilled".equals(status)) && closedAt != null) {
            status = "delivered";
        }
        info.displayStatus = status;

        // 提取时间 - 遍历所有 fulfillment 取最新
        for (JsonNode f : fulfillments) {
            LocalDateTime created = parseDateTime(f.path("created_at").asText(null));
            if (created != null && (info.createdAt == null || created.isAfter(info.createdAt))) {
                info.createdAt = created;
            }

            LocalDateTime inTransit = parseDateTime(f.path("in_transit_at").asText(null));
            if (inTransit != null && (info.inTransitAt == null || inTransit.isAfter(info.inTransitAt))) {
                info.inTransitAt = inTransit;
            }

            LocalDateTime delivered = parseDateTime(f.path("delivered_at").asText(null));
            if (delivered != null && (info.deliveredAt == null || delivered.isAfter(info.deliveredAt))) {
                info.deliveredAt = delivered;
            }

            LocalDateTime estimated = parseDateTime(f.path("estimated_delivery_at").asText(null));
            if (estimated != null
                    && (info.estimatedDeliveryAt == null || estimated.isAfter(info.estimatedDeliveryAt))) {
                info.estimatedDeliveryAt = estimated;
            }
        }

        // 如果显示状态是 delivered 但没有妥投时间，用当前时间回退 (Webhook 场景可能不需要，因为我们依赖 payload，但为了保险)
        if ("delivered".equalsIgnoreCase(info.displayStatus) && info.deliveredAt == null) {
            info.deliveredAt = LocalDateTime.now();
        }

        return info;
    }

    /**
     * 辅助类
     */
    private static class FulfillmentInfo {
        String displayStatus;
        String trackingCompany;
        String trackingNumber;
        String trackingUrl;
        LocalDateTime createdAt;
        LocalDateTime inTransitAt;
        LocalDateTime deliveredAt;
        LocalDateTime estimatedDeliveryAt;
    }

    private void handleOrderPaid(String shopDomain, JsonNode payload) {
        String shopifyOrderId = extractNumericId(payload.path("id").asText());
        log.info("[OrderProcessor] PAID order: id={}", shopifyOrderId);

        // 解析本地订单 ID（与 shopify 共用表时 order_id 必填）
        Long orderId = orderRepository.findByShopifyOrderId(shopifyOrderId)
                .map(com.athlunakms.webhook.entity.ShopifyOrder::getId).orElse(null);
        if (orderId == null) {
            log.warn("[OrderProcessor] No local order for shopify_order_id={}, skipping transaction save",
                    shopifyOrderId);
        }

        // 保存付款交易记录
        JsonNode transactions = payload.path("transactions");
        if (transactions.isArray() && orderId != null) {
            for (JsonNode tx : transactions) {
                Long txId = tx.path("id").asLong();
                String kind = tx.path("kind").asText();

                // 只保存成功的付款交易（非退款）
                if (!"refund".equals(kind) && "success".equals(tx.path("status").asText())) {
                    // 检查是否已存在
                    if (transactionRepository.findByShopifyTransactionId(txId).isEmpty()) {
                        OrderTransaction transaction = new OrderTransaction();
                        transaction.setOrderId(orderId);
                        transaction.setShopifyTransactionId(txId);
                        transaction.setShopifyOrderId(shopifyOrderId);
                        transaction.setKind(kind);
                        transaction.setGateway(tx.path("gateway").asText(null));
                        transaction.setStatus(tx.path("status").asText(null));
                        transaction.setAmount(parseBigDecimal(tx.path("amount").asText("0")));
                        transaction.setCurrency(tx.path("currency").asText("USD"));
                        transaction.setAuthorization(tx.path("authorization").asText(null));
                        transaction.setParentId(tx.path("parent_id").asLong(0));
                        transaction.setErrorCode(tx.path("error_code").asText(null));
                        transaction.setMessage(tx.path("message").asText(null));
                        transaction.setProcessedAt(parseDateTime(tx.path("processed_at").asText(null)));
                        transaction.setCreatedAt(LocalDateTime.now());
                        transactionRepository.save(transaction);
                        log.info("[OrderProcessor] Transaction saved: id={}, kind={}, amount={}",
                                txId, kind, transaction.getAmount());
                    }
                }
            }
        }

        // 更新订单状态
        orderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(order -> {
            order.setFinancialStatus("paid");
            order.setUpdatedAtShopify(LocalDateTime.now());
            orderRepository.save(order);
            log.info("[OrderProcessor] Order {} marked as paid", shopifyOrderId);
        });

        // 更新转化订单状态
        conversionOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(conversion -> {
            conversion.setFinancialStatus("paid");
            conversion.setUpdatedAt(LocalDateTime.now());
            conversionOrderRepository.save(conversion);
            log.info("[OrderProcessor] Conversion order for {} marked as paid", shopifyOrderId);
        });
    }

    private void handleOrderCancelled(String shopDomain, JsonNode payload) {
        String shopifyOrderId = extractNumericId(payload.path("id").asText());
        log.info("[OrderProcessor] CANCELLED order: id={}", shopifyOrderId);

        orderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(order -> {
            order.setCancelledAt(LocalDateTime.now());
            order.setUpdatedAtShopify(LocalDateTime.now());
            // 更新财务状态为已取消
            order.setFinancialStatus("voided");
            order.setFulfillmentStatus("cancelled");
            orderRepository.save(order);
            log.info("[OrderProcessor] Order {} marked as cancelled/voided", shopifyOrderId);
        });

        conversionOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(conversion -> {
            conversion.setStatus("cancelled");
            conversion.setUpdatedAt(LocalDateTime.now());
            conversionOrderRepository.save(conversion);
        });

        // 更新样品单状态
        sampleOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(sample -> {
            sample.setUpdatedAt(LocalDateTime.now());
            sampleOrderRepository.save(sample);
        });
    }

    private void handleRefundCreate(String shopDomain, JsonNode payload) {
        Long shopifyRefundId = payload.path("id").asLong();
        String shopifyOrderId = extractNumericId(payload.path("order_id").asText());
        log.info("[OrderProcessor] REFUND created: refundId={}, orderId={}", shopifyRefundId, shopifyOrderId);

        // 检查是否已存在
        if (refundRepository.findByShopifyRefundId(shopifyRefundId).isPresent()) {
            log.info("[OrderProcessor] Refund already exists, skipping");
            return;
        }

        // 计算退款金额
        BigDecimal calculatedRefundAmount = BigDecimal.ZERO;
        JsonNode transactions = payload.path("transactions");
        if (transactions.isArray()) {
            for (JsonNode tx : transactions) {
                String kind = tx.path("kind").asText();
                if ("refund".equals(kind)) {
                    calculatedRefundAmount = calculatedRefundAmount.add(parseBigDecimal(tx.path("amount").asText("0")));
                }
            }
        }
        final BigDecimal refundAmount = calculatedRefundAmount;

        // 解析本地订单 ID（与 shopify 共用表时 order_id 必填）
        Long orderId = orderRepository.findByShopifyOrderId(shopifyOrderId)
                .map(com.athlunakms.webhook.entity.ShopifyOrder::getId).orElse(null);
        if (orderId == null) {
            log.warn("[OrderProcessor] Refund skipped: no local order for shopify_order_id={}", shopifyOrderId);
            return;
        }

        // 创建退款记录
        OrderRefund refund = new OrderRefund();
        refund.setOrderId(orderId);
        refund.setShopifyRefundId(shopifyRefundId);
        refund.setShopifyOrderId(shopifyOrderId);
        refund.setAmount(refundAmount);
        refund.setCurrency(payload.path("currency").asText("USD"));
        refund.setReason(payload.path("reason").asText(null));
        refund.setNote(payload.path("note").asText(null));
        refund.setRestock(payload.path("restock").asBoolean(false));
        refund.setProcessedAt(parseDateTime(payload.path("processed_at").asText(null)));
        refund.setCreatedAt(LocalDateTime.now());
        refundRepository.save(refund);
        log.info("[OrderProcessor] Refund saved: id={}, amount={}", refund.getId(), refundAmount);

        // 处理退货商品行(refund_line_items)
        JsonNode refundLineItems = payload.path("refund_line_items");
        if (refundLineItems.isArray() && refundLineItems.size() > 0) {
            log.info("[OrderProcessor] Processing {} refund line items", refundLineItems.size());
            for (JsonNode item : refundLineItems) {
                String refundLineId = item.path("id").asText();
                String lineItemId = item.path("line_item_id").asText();
                int quantity = item.path("quantity").asInt(0);
                String restockType = item.path("restock_type").asText("no_restock");

                log.info("[OrderProcessor] Refund line item: lineItemId={}, quantity={}, restockType={}",
                        lineItemId, quantity, restockType);

                // 保存退款商品明细到 order_refund_line_items 表
                OrderRefundLineItem refundLineItem = refundLineItemRepository.findByShopifyRefundLineId(refundLineId)
                        .orElse(new OrderRefundLineItem());
                refundLineItem.setRefundId(refund.getId());
                refundLineItem.setShopifyRefundLineId(refundLineId);
                refundLineItem.setShopifyOrderLineItemId(lineItemId);
                refundLineItem.setQuantity(quantity);
                refundLineItem.setSubtotal(parseBigDecimal(item.path("subtotal").asText("0")));
                refundLineItem.setTaxAmount(parseBigDecimal(item.path("total_tax").asText("0")));
                refundLineItem.setCurrency(payload.path("currency").asText("USD"));
                refundLineItemRepository.save(refundLineItem);
                log.info("[OrderProcessor] Saved refund line item: id={}, shopifyLineItemId={}",
                        refundLineItem.getId(), lineItemId);

                // 更新对应的行项目状态为已退货（支持部分退货）
                lineItemRepository.findByShopifyLineItemId(lineItemId).ifPresent(lineItem -> {
                    // 累加退货数量
                    int currentReturned = lineItem.getReturnedQuantity() != null ? lineItem.getReturnedQuantity() : 0;
                    int newReturned = currentReturned + quantity;
                    lineItem.setReturnedQuantity(newReturned);

                    // 只有当退货数量 >= 订购数量时才标记为已退货
                    int orderedQty = lineItem.getQuantity() != null ? lineItem.getQuantity() : 1;
                    if (newReturned >= orderedQty) {
                        lineItem.setIsReturned(true);
                        log.info("[OrderProcessor] Line item {} fully returned via refund ({}/{})", lineItemId,
                                newReturned, orderedQty);
                    } else {
                        log.info("[OrderProcessor] Line item {} partially returned via refund ({}/{})", lineItemId,
                                newReturned, orderedQty);
                    }
                    lineItemRepository.save(lineItem);
                });
            }
        }

        // 更新订单退款总额和状态
        orderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(order -> {
            BigDecimal currentRefund = order.getRefundTotal() != null ? order.getRefundTotal() : BigDecimal.ZERO;
            BigDecimal newRefundTotal = currentRefund.add(refundAmount);
            order.setRefundTotal(newRefundTotal);
            order.setUpdatedAtShopify(LocalDateTime.now());

            // 判断是否全额退款：退款总额 >= 订单总价
            BigDecimal totalPrice = order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO;
            if (newRefundTotal.compareTo(totalPrice) >= 0) {
                log.info("[OrderProcessor] Order {} fully refunded, marking as cancelled", shopifyOrderId);
                order.setFinancialStatus("refunded");
                order.setFulfillmentStatus("cancelled");
                order.setCancelledAt(LocalDateTime.now());
            } else {
                // 部分退款
                order.setFinancialStatus("partially_refunded");
            }

            orderRepository.save(order);
            log.info("[OrderProcessor] Order {} updated: refundTotal={}, financialStatus={}",
                    shopifyOrderId, newRefundTotal, order.getFinancialStatus());
        });

        // 更新转化订单退款金额和状态
        conversionOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(conversion -> {
            BigDecimal currentRefund = conversion.getTotalRefund() != null ? conversion.getTotalRefund()
                    : BigDecimal.ZERO;
            BigDecimal newRefundTotal = currentRefund.add(refundAmount);
            conversion.setTotalRefund(newRefundTotal);
            conversion.setUpdatedAt(LocalDateTime.now());

            // 判断是否全额退款
            BigDecimal totalPrice = conversion.getTotalPrice() != null ? conversion.getTotalPrice() : BigDecimal.ZERO;
            if (newRefundTotal.compareTo(totalPrice) >= 0) {
                conversion.setStatus("refunded");
            } else {
                conversion.setStatus("partially_refunded");
            }

            conversionOrderRepository.save(conversion);
            log.info("[OrderProcessor] Conversion order updated: refundTotal={}, status={}",
                    newRefundTotal, conversion.getStatus());
        });

        // 更新红人样品单状态（如果关联）
        sampleOrderRepository.findByShopifyOrderId(shopifyOrderId).ifPresent(sample -> {
            log.info("[OrderProcessor] Updating sample order status for refunded order");
            // 样品单无需计算佣金，只更新状态
            sample.setUpdatedAt(LocalDateTime.now());
            sampleOrderRepository.save(sample);
        });
    }

    private void mapPayloadToOrder(ShopifyOrder order, JsonNode payload, String shopDomain) {
        String shopifyOrderId = extractNumericId(payload.path("id").asText());
        order.setShopifyOrderId(shopifyOrderId);
        order.setShopifyOrderNumber(payload.path("order_number").asLong(0));
        order.setShopifyOrderNumberLegacy(payload.path("order_number").asLong(0)); // 兼容 DB 中的 shopify_order_number 列
        order.setOrderName(payload.path("name").asText(null));

        // 设置 store_id (必须) - 优先使用 myshopify_domain 匹配，再用 shop_domain
        ShopifyStore store = storeRepository.findByMyshopifyDomain(shopDomain)
                .or(() -> storeRepository.findByShopDomain(shopDomain))
                .orElseThrow(() -> new RuntimeException("Store not found for domain: " + shopDomain +
                        ". Please ensure the store is registered in the system."));
        order.setStoreId(store.getId());
        order.setCustomerEmail(payload.path("email").asText(null));
        order.setCustomerPhone(payload.path("phone").asText(null));
        order.setFinancialStatus(payload.path("financial_status").asText(null));
        order.setFulfillmentStatus(payload.path("fulfillment_status").asText(null));
        order.setTotalPrice(parseBigDecimal(payload.path("total_price").asText(null)));
        order.setSubtotalPrice(parseBigDecimal(payload.path("subtotal_price").asText(null)));
        order.setTotalTax(parseBigDecimal(payload.path("total_tax").asText(null)));
        order.setTotalDiscounts(parseBigDecimal(payload.path("total_discounts").asText(null)));
        order.setCurrency(payload.path("currency").asText(null));
        order.setNote(payload.path("note").asText(null));
        order.setTags(payload.path("tags").asText(null));
        order.setProcessedAtShopify(parseDateTime(payload.path("processed_at").asText(null)));
        order.setCreatedAtShopify(parseDateTime(payload.path("created_at").asText(null)));
        order.setUpdatedAtShopify(parseDateTime(payload.path("updated_at").asText(null)));
        order.setCancelledAt(parseDateTime(payload.path("cancelled_at").asText(null)));
        order.setClosedAt(parseDateTime(payload.path("closed_at").asText(null)));

        // 提取折扣码
        JsonNode discountCodes = payload.path("discount_codes");
        if (discountCodes.isArray() && discountCodes.size() > 0) {
            StringBuilder codes = new StringBuilder();
            for (JsonNode dc : discountCodes) {
                if (codes.length() > 0)
                    codes.append(",");
                codes.append(dc.path("code").asText());
            }
            order.setDiscountCodes(codes.toString());
        }

        // 客户信息
        JsonNode customer = payload.path("customer");
        if (!customer.isMissingNode()) {
            order.setCustomerFirstName(customer.path("first_name").asText(null));
            order.setCustomerLastName(customer.path("last_name").asText(null));
            order.setCustomerEmail(customer.path("email").asText(order.getCustomerEmail()));
            order.setCustomerPhone(customer.path("phone").asText(order.getCustomerPhone()));
        } else {
            order.setCustomerEmail(payload.path("email").asText(order.getCustomerEmail()));
            order.setCustomerPhone(payload.path("phone").asText(order.getCustomerPhone()));
        }

        // 使用优先级函数获取收件人姓名
        String shippingName = getRecipientNameFromOrder(payload);
        order.setShippingName(shippingName);

        // ===== 收货地址 - 只有新值非空 且 时间更新时才更新，避免旧数据覆盖新数据 =====
        JsonNode shipping = payload.path("shipping_address");
        LocalDateTime shopifyUpdatedAt = order.getUpdatedAtShopify();
        LocalDateTime dbSyncedAt = order.getSyncedAt();
        boolean isNew = order.getId() == null;
        boolean shouldUpdate = shouldUpdateField(isNew, shopifyUpdatedAt, dbSyncedAt);

        if (!shipping.isMissingNode() && !shipping.isNull()) {
            // 收件人姓名（如果上面没有获取到）
            String newShippingName = shippingName;
            if (newShippingName == null || newShippingName.isEmpty()) {
                String name = shipping.path("name").asText(null);
                if (name == null || name.isEmpty()) {
                    String fn = shipping.path("first_name").asText("");
                    String ln = shipping.path("last_name").asText("");
                    name = (fn + " " + ln).trim();
                }
                newShippingName = name.isEmpty() ? null : name;
            }
            // 只有新值非空 且 时间条件满足时才更新，避免旧数据覆盖新数据
            order.setShippingName(updateIfNewer(order.getShippingName(), newShippingName, shouldUpdate));
            order.setShippingPhone(
                    updateIfNewer(order.getShippingPhone(), shipping.path("phone").asText(null), shouldUpdate));
            order.setShippingAddress1(
                    updateIfNewer(order.getShippingAddress1(), shipping.path("address1").asText(null), shouldUpdate));
            order.setShippingAddress2(
                    updateIfNewer(order.getShippingAddress2(), shipping.path("address2").asText(null), shouldUpdate));
            order.setShippingCity(
                    updateIfNewer(order.getShippingCity(), shipping.path("city").asText(null), shouldUpdate));
            order.setShippingProvince(
                    updateIfNewer(order.getShippingProvince(), shipping.path("province").asText(null), shouldUpdate));
            order.setShippingProvinceCode(updateIfNewer(order.getShippingProvinceCode(),
                    shipping.path("province_code").asText(null), shouldUpdate));
            order.setShippingCountry(
                    updateIfNewer(order.getShippingCountry(), shipping.path("country").asText(null), shouldUpdate));
            order.setShippingCountryCode(updateIfNewer(order.getShippingCountryCode(),
                    shipping.path("country_code").asText(null), shouldUpdate));
            order.setShippingZip(
                    updateIfNewer(order.getShippingZip(), shipping.path("zip").asText(null), shouldUpdate));
            log.info("[Webhook] Order {} shipping_address (shouldUpdate={}): name={}, address1={}, city={}",
                    payload.path("name").asText(), shouldUpdate, order.getShippingName(),
                    order.getShippingAddress1(), order.getShippingCity());
        } else {
            log.warn("[Webhook] Order {} has no shipping_address, keeping existing values",
                    payload.path("name").asText());
        }

        // 物流信息
        JsonNode fulfillments = payload.path("fulfillments");
        if (fulfillments.isArray()) {
            order.setFulfillmentsJson(fulfillments.toString());
            List<String> trackingNos = new ArrayList<>();
            List<String> fids = new ArrayList<>();

            // 时间字段 - 取最新的 fulfillment 时间
            LocalDateTime latestFulfillmentCreated = null;
            LocalDateTime latestInTransit = null;
            LocalDateTime latestDelivered = null;
            LocalDateTime latestEstimatedDelivery = null;

            for (JsonNode f : fulfillments) {
                String fid = f.path("id").asText(null);
                if (fid != null && !fid.isEmpty() && !"null".equals(fid)) {
                    fids.add(fid);
                }

                if (f.hasNonNull("tracking_number")) {
                    trackingNos.add(f.get("tracking_number").asText());
                }
                if (f.hasNonNull("tracking_numbers") && f.get("tracking_numbers").isArray()) {
                    for (JsonNode tn : f.get("tracking_numbers")) {
                        trackingNos.add(tn.asText());
                    }
                }

                // 解析 Fulfillment 时间字段
                LocalDateTime fcreated = parseDateTime(f.path("created_at").asText(null));
                if (fcreated != null
                        && (latestFulfillmentCreated == null || fcreated.isAfter(latestFulfillmentCreated))) {
                    latestFulfillmentCreated = fcreated;
                }

                LocalDateTime inTransit = parseDateTime(f.path("in_transit_at").asText(null));
                if (inTransit != null && (latestInTransit == null || inTransit.isAfter(latestInTransit))) {
                    latestInTransit = inTransit;
                }

                LocalDateTime delivered = parseDateTime(f.path("delivered_at").asText(null));
                if (delivered != null && (latestDelivered == null || delivered.isAfter(latestDelivered))) {
                    latestDelivered = delivered;
                }

                LocalDateTime estimated = parseDateTime(f.path("estimated_delivery_at").asText(null));
                if (estimated != null
                        && (latestEstimatedDelivery == null || estimated.isAfter(latestEstimatedDelivery))) {
                    latestEstimatedDelivery = estimated;
                }
            }

            order.setTrackingNumbers(String.join(",", trackingNos.stream().distinct().collect(Collectors.toList())));
            order.setFulfillmentIds(String.join(",", fids.stream().distinct().collect(Collectors.toList())));

            // 设置时间字段
            order.setFulfillmentCreatedAt(latestFulfillmentCreated);
            order.setInTransitAt(latestInTransit);
            order.setDeliveredAt(latestDelivered);
            order.setEstimatedDeliveryAt(latestEstimatedDelivery);

            log.info("[OrderProcessor] Fulfillment times: created={}, inTransit={}, delivered={}, estimated={}",
                    latestFulfillmentCreated, latestInTransit, latestDelivered, latestEstimatedDelivery);
        }

        // 运费
        JsonNode shippingLines = payload.path("shipping_lines");
        if (shippingLines.isArray()) {
            BigDecimal totalShipping = BigDecimal.ZERO;
            for (JsonNode sl : shippingLines) {
                totalShipping = totalShipping.add(parseBigDecimal(sl.path("price").asText("0")));
            }
            order.setTotalShippingPrice(totalShipping);
        }
    }

    private void saveLineItems(ShopifyOrder order, JsonNode payload) {
        JsonNode lineItems = payload.path("line_items");
        if (!lineItems.isArray())
            return;

        // 建立 line_item_id -> fulfillment_id 的映射
        Map<Long, String> itemFulfillmentMap = new HashMap<>();
        JsonNode fulfillments = payload.path("fulfillments");
        if (fulfillments.isArray()) {
            for (JsonNode f : fulfillments) {
                String fid = f.path("id").asText(null);
                JsonNode fItems = f.path("line_items");
                if (fid != null && fItems.isArray()) {
                    for (JsonNode fi : fItems) {
                        itemFulfillmentMap.put(fi.path("id").asLong(), fid);
                    }
                }
            }
        }

        List<String> currentIds = new ArrayList<>();
        for (JsonNode node : lineItems) {
            String shopifyId = node.path("id").asText();
            currentIds.add(shopifyId);

            OrderLineItem item = lineItemRepository.findByShopifyLineItemId(shopifyId)
                    .orElse(new OrderLineItem());

            item.setOrderId(order.getId());
            item.setShopifyLineItemId(shopifyId);
            item.setShopifyProductId(node.path("product_id").asLong(0));
            item.setShopifyVariantId(node.path("variant_id").asLong(0));
            item.setSku(node.path("sku").asText(null));
            item.setTitle(node.path("title").asText(null));
            item.setVariantTitle(node.path("variant_title").asText(null));
            item.setQuantity(node.path("quantity").asInt(1));
            item.setPrice(parseBigDecimal(node.path("price").asText("0")));
            item.setTotal(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            item.setFulfillmentStatus(node.path("fulfillment_status").asText(null));
            item.setImageUrl(node.path("image_url").asText(null)); // REST Webhook 有时带 image_url

            // 关联 fulfillmentId
            if (itemFulfillmentMap.containsKey(node.path("id").asLong())) {
                item.setFulfillmentId(itemFulfillmentMap.get(node.get("id").asLong()));
            }

            // 折扣分摊
            BigDecimal discount = BigDecimal.ZERO;
            JsonNode da = node.path("discount_allocations");
            if (da.isArray()) {
                for (JsonNode d : da) {
                    discount = discount.add(parseBigDecimal(d.path("amount").asText("0")));
                }
            }
            item.setTotalDiscount(discount);

            lineItemRepository.save(item);
        }

        // 清理已删除的行
        List<OrderLineItem> existing = lineItemRepository.findByOrderId(order.getId());
        for (OrderLineItem e : existing) {
            if (!currentIds.contains(e.getShopifyLineItemId())) {
                lineItemRepository.delete(e);
            }
        }
    }

    private void createConversionOrderIfNeeded(String shopifyOrderId, Long orderId, JsonNode payload) {
        log.debug("[OrderProcessor] Skipping conversion order creation for {} — discount binding feature removed",
                shopifyOrderId);
    }

    /**
     * 处理 Webhook payload 中的 returns 数组
     * 将已完成退货的商品行项标记为已退货（支持部分退货）
     */
    private void processReturns(JsonNode payload) {
        JsonNode returns = payload.path("returns");
        if (!returns.isArray() || returns.size() == 0) {
            return;
        }

        log.info("[OrderProcessor] Processing {} returns from webhook payload", returns.size());

        for (JsonNode returnNode : returns) {
            String status = returnNode.path("status").asText("");
            // 只处理已关闭或已完成的退货
            if (!"closed".equals(status) && !"returned".equals(status)) {
                log.debug("[OrderProcessor] Skipping return with status: {}", status);
                continue;
            }

            JsonNode returnLineItems = returnNode.path("return_line_items");
            if (!returnLineItems.isArray()) {
                continue;
            }

            for (JsonNode lineNode : returnLineItems) {
                // 获取退货数量
                int returnedQty = lineNode.path("quantity").asInt(1);

                // 从 fulfillment_line_item 获取 line_item_id
                JsonNode fulfillmentLineItem = lineNode.path("fulfillment_line_item");
                if (fulfillmentLineItem.isMissingNode()) {
                    continue;
                }

                String lineItemId = fulfillmentLineItem.path("line_item_id").asText("");
                if (lineItemId.isEmpty() || "null".equals(lineItemId)) {
                    // 尝试从嵌套的 line_item 对象获取 ID
                    JsonNode lineItem = fulfillmentLineItem.path("line_item");
                    if (!lineItem.isMissingNode()) {
                        String gidLineItem = lineItem.path("id").asText("");
                        if (gidLineItem.contains("/")) {
                            lineItemId = gidLineItem.substring(gidLineItem.lastIndexOf("/") + 1);
                        } else if (!gidLineItem.isEmpty()) {
                            lineItemId = gidLineItem;
                        }
                    }
                }

                if (!lineItemId.isEmpty() && !"null".equals(lineItemId)) {
                    final String finalLineItemId = lineItemId;
                    final int finalReturnedQty = returnedQty;
                    lineItemRepository.findByShopifyLineItemId(finalLineItemId).ifPresent(item -> {
                        // 累加退货数量
                        int currentReturned = item.getReturnedQuantity() != null ? item.getReturnedQuantity() : 0;
                        int newReturned = currentReturned + finalReturnedQty;
                        item.setReturnedQuantity(newReturned);

                        // 只有当退货数量 >= 订购数量时才标记为已退货
                        int orderedQty = item.getQuantity() != null ? item.getQuantity() : 1;
                        if (newReturned >= orderedQty) {
                            item.setIsReturned(true);
                            log.info("[OrderProcessor] Line item {} fully returned ({}/{})", finalLineItemId,
                                    newReturned, orderedQty);
                        } else {
                            log.info("[OrderProcessor] Line item {} partially returned ({}/{})", finalLineItemId,
                                    newReturned, orderedQty);
                        }
                        lineItemRepository.save(item);
                    });
                }
            }
        }
    }

    /**
     * 解析 Shopify 的 ISO 8601 时间字符串
     * Shopify 返回的时间通常是 UTC（带 +00:00 或 Z 后缀）
     * 由于 JDBC 连接配置了 serverTimezone=Asia/Shanghai，
     * 我们需要直接获取原始时间部分（不做时区转换），让 JDBC 正确处理
     */
    /**
     * 解析时间字符串
     */
    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return null;
        try {
            // 强制转换为上海时间，确保无论输入是什么时区，最后得到的 LocalDateTime 都是北京时间
            return ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .withZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                    .toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractNumericId(String id) {
        if (id == null)
            return null;
        return id.contains("/") ? id.substring(id.lastIndexOf("/") + 1) : id;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty())
            return BigDecimal.ZERO;
        try {
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    // ===================== Name Utility Methods =====================

    private boolean isNonEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * 只有当新值非空时才更新字段，保护已有数据不被空值覆盖
     */
    private String updateIfNotEmpty(String existingValue, String newValue) {
        if (isNonEmpty(newValue)) {
            return newValue;
        }
        return existingValue;
    }

    /**
     * 判断是否应该更新字段：新值非空 且 (是新订单 或 Shopify更新时间 > 数据库同步时间)
     */
    private boolean shouldUpdateField(boolean isNew, LocalDateTime shopifyUpdatedAt, LocalDateTime dbSyncedAt) {
        if (isNew) {
            return true;
        }
        if (shopifyUpdatedAt == null) {
            return false;
        }
        if (dbSyncedAt == null) {
            return true;
        }
        return shopifyUpdatedAt.isAfter(dbSyncedAt);
    }

    /**
     * 基于时间判断的条件更新：只有当新值非空 且 时间条件满足时才更新
     */
    private String updateIfNewer(String existingValue, String newValue, boolean shouldUpdate) {
        if (shouldUpdate && isNonEmpty(newValue)) {
            return newValue;
        }
        return existingValue;
    }

    private String concatName(String first, String last) {
        String f = first == null ? "" : first.trim();
        String l = last == null ? "" : last.trim();
        if (!f.isEmpty() && !l.isEmpty()) {
            return f + " " + l;
        }
        if (!f.isEmpty())
            return f;
        if (!l.isEmpty())
            return l;
        return "";
    }

    /**
     * 从 email 里提取本地部分（@ 前面的部分）作为兜底名
     */
    private String emailLocalPart(String email) {
        if (email == null)
            return null;
        int idx = email.indexOf('@');
        if (idx <= 0)
            return null;
        String local = email.substring(0, idx).trim(); // 注意是 0 到 idx
        return local.isEmpty() ? null : local;
    }

    /**
     * 从地址 JSON 节点中提取姓名（name 或 first_name/last_name）
     */
    private String extractNameFromAddress(JsonNode addressNode) {
        if (addressNode == null || addressNode.isMissingNode() || addressNode.isNull()) {
            return null;
        }

        // 先尝试 name 字段
        String name = addressNode.path("name").asText(null);
        if (isNonEmpty(name)) {
            return name.trim();
        }

        // 再尝试 first_name + last_name
        String firstName = addressNode.path("first_name").asText(null);
        String lastName = addressNode.path("last_name").asText(null);
        name = concatName(firstName, lastName);
        if (isNonEmpty(name)) {
            return name;
        }

        return null;
    }

    /**
     * 从订单 JSON 中提取收件人姓名
     * 优先级：
     * 1) shipping_address.name / first_name / last_name
     * 2) billing_address.name / first_name / last_name
     * 3) customer.first_name + last_name
     * 4) customer.default_address.name / first_name / last_name
     * 5) shipping_address.company / billing_address.company /
     * customer.default_address.company
     * 6) email 本地部分（兜底）
     */
    private String getRecipientNameFromOrder(JsonNode orderNode) {
        if (orderNode == null || orderNode.isMissingNode() || orderNode.isNull()) {
            return null;
        }

        JsonNode shippingAddress = orderNode.path("shipping_address");
        JsonNode billingAddress = orderNode.path("billing_address");
        JsonNode customer = orderNode.path("customer");
        JsonNode defaultAddress = customer.path("default_address");

        // 1. shipping_address: name 或 first_name/last_name
        String name = extractNameFromAddress(shippingAddress);
        if (isNonEmpty(name)) {
            return name;
        }

        // 2. billing_address: name 或 first_name/last_name
        name = extractNameFromAddress(billingAddress);
        if (isNonEmpty(name)) {
            return name;
        }

        // 3. customer: first_name + last_name
        if (!customer.isMissingNode() && !customer.isNull()) {
            String cFull = concatName(
                    customer.path("first_name").asText(null),
                    customer.path("last_name").asText(null));
            if (isNonEmpty(cFull)) {
                return cFull;
            }
        }

        // 4. customer.default_address: name 或 first_name/last_name
        if (!defaultAddress.isMissingNode() && !defaultAddress.isNull()) {
            name = extractNameFromAddress(defaultAddress);
            if (isNonEmpty(name)) {
                return name;
            }
        }

        // 5. 最后兜底：从 email 提取 local part
        String email = orderNode.path("email").asText(null);
        if (!customer.isMissingNode() && !customer.isNull()) {
            String custEmail = customer.path("email").asText(null);
            if (custEmail != null) {
                email = custEmail;
            }
        }
        String local = emailLocalPart(email);
        if (local != null) {
            return local;
        }

        return "未填写收件人姓名";
    }

    private Map<String, Integer> calculateReturnCurrentQuantities(JsonNode payload) {
        Map<String, Integer> returnMap = new HashMap<>();
        JsonNode returns = payload.path("returns");
        if (!returns.isArray())
            return returnMap;

        for (JsonNode returnNode : returns) {
            // Count all returns that are processed (closed/returned/open) - usually we care
            // about 'returned' logic
            // But simplify: count quantity if it appears in 'returns'
            JsonNode returnLineItems = returnNode.path("return_line_items");
            if (returnLineItems.isArray()) {
                for (JsonNode lineNode : returnLineItems) {
                    int qty = lineNode.path("quantity").asInt(1);
                    JsonNode fulfillmentLineItem = lineNode.path("fulfillment_line_item");
                    String lineItemId = "";

                    if (!fulfillmentLineItem.isMissingNode()) {
                        lineItemId = fulfillmentLineItem.path("line_item_id").asText("");
                        if (lineItemId.isEmpty() || "null".equals(lineItemId)) {
                            JsonNode li = fulfillmentLineItem.path("line_item");
                            if (!li.isMissingNode()) {
                                String gid = li.path("id").asText("");
                                lineItemId = extractNumericId(gid);
                            }
                        }
                    }

                    if (!lineItemId.isEmpty()) {
                        returnMap.merge(lineItemId, qty, Integer::sum);
                    }
                }
            }
        }
        return returnMap;
    }

    private List<InfluencerSampleOrderItem> extractSampleItems(JsonNode payload, Long sampleOrderId,
            boolean isOrderCancelled) {
        List<InfluencerSampleOrderItem> items = new ArrayList<>();
        JsonNode lineItems = payload.path("line_items");
        Map<String, Integer> returnMap = calculateReturnCurrentQuantities(payload);

        if (lineItems.isArray()) {
            for (JsonNode itemNode : lineItems) {
                InfluencerSampleOrderItem item = new InfluencerSampleOrderItem();
                item.setSampleOrderId(sampleOrderId);
                item.setShopifyLineItemId(itemNode.path("id").asText());
                item.setShopifyProductId(itemNode.path("product_id").asLong());
                item.setShopifyVariantId(itemNode.path("variant_id").asLong());
                item.setSku(itemNode.path("sku").asText());
                item.setTitle(itemNode.path("title").asText());
                item.setVariantTitle(itemNode.path("variant_title").asText(null));
                item.setQuantity(itemNode.path("quantity").asInt(0));
                item.setPrice(parseBigDecimal(itemNode.path("price").asText("0")));

                String status = itemNode.path("fulfillment_status").asText(null);
                if (status == null) {
                    status = "unfulfilled";
                }
                if (isOrderCancelled && "unfulfilled".equals(status)) {
                    status = "cancelled";
                }
                item.setFulfillmentStatus(status);

                // Set returned quantity & isReturned
                int returnedQty = returnMap.getOrDefault(item.getShopifyLineItemId(), 0);
                item.setReturnedQuantity(returnedQty);

                int orderedQty = item.getQuantity();
                if (orderedQty > 0 && returnedQty >= orderedQty) {
                    item.setIsReturned(true);
                } else {
                    item.setIsReturned(false);
                }

                items.add(item);
            }
        }
        return items;
    }

    private List<InfluencerConversionOrderItem> extractConversionItems(JsonNode payload, Long conversionOrderId,
            boolean isOrderCancelled) {
        List<InfluencerConversionOrderItem> items = new ArrayList<>();
        JsonNode lineItems = payload.path("line_items");
        Map<String, Integer> returnMap = calculateReturnCurrentQuantities(payload);

        if (lineItems.isArray()) {
            for (JsonNode itemNode : lineItems) {
                InfluencerConversionOrderItem item = new InfluencerConversionOrderItem();
                item.setConversionOrderId(conversionOrderId);
                item.setShopifyLineItemId(itemNode.path("id").asText());
                item.setShopifyProductId(itemNode.path("product_id").asLong());
                item.setShopifyVariantId(itemNode.path("variant_id").asLong());
                item.setSku(itemNode.path("sku").asText());
                item.setTitle(itemNode.path("title").asText());
                item.setVariantTitle(itemNode.path("variant_title").asText(null));
                item.setQuantity(itemNode.path("quantity").asInt(0));
                item.setPrice(parseBigDecimal(itemNode.path("price").asText("0")));

                String status = itemNode.path("fulfillment_status").asText(null);
                if (status == null) {
                    status = "unfulfilled";
                }
                if (isOrderCancelled && "unfulfilled".equals(status)) {
                    status = "cancelled";
                }
                item.setFulfillmentStatus(status);

                // Set returned quantity
                int returnedQty = returnMap.getOrDefault(item.getShopifyLineItemId(), 0);
                item.setReturnedQuantity(returnedQty);

                int orderedQty = item.getQuantity();
                if (orderedQty > 0 && returnedQty >= orderedQty) {
                    item.setIsReturned(true);
                } else {
                    item.setIsReturned(false);
                }

                items.add(item);
            }
        }
        return items;
    }

    private boolean isSampleItemsChanged(List<InfluencerSampleOrderItem> oldItems,
            List<InfluencerSampleOrderItem> newItems) {
        if (oldItems.size() != newItems.size())
            return true;
        // Simple comparison: check if all IDs and Qty match.
        // Or strictly strictly map by ID.
        // Given order of items might change (rarely), mapping by ID is safer.
        Map<String, InfluencerSampleOrderItem> oldMap = oldItems.stream()
                .filter(i -> i.getShopifyLineItemId() != null)
                .collect(Collectors.toMap(InfluencerSampleOrderItem::getShopifyLineItemId, i -> i, (a, b) -> a));

        for (InfluencerSampleOrderItem newItem : newItems) {
            InfluencerSampleOrderItem oldItem = oldMap.get(newItem.getShopifyLineItemId());
            if (oldItem == null)
                return true; // New item added
            if (!Objects.equals(newItem.getQuantity(), oldItem.getQuantity()))
                return true;
            if (!Objects.equals(newItem.getFulfillmentStatus(), oldItem.getFulfillmentStatus()))
                return true;
            // Can add more fields like sku if they can change
        }
        return false;
    }

    private boolean isConversionItemsChanged(List<InfluencerConversionOrderItem> oldItems,
            List<InfluencerConversionOrderItem> newItems) {
        if (oldItems.size() != newItems.size())
            return true;
        Map<String, InfluencerConversionOrderItem> oldMap = oldItems.stream()
                .filter(i -> i.getShopifyLineItemId() != null)
                .collect(Collectors.toMap(InfluencerConversionOrderItem::getShopifyLineItemId, i -> i, (a, b) -> a));

        for (InfluencerConversionOrderItem newItem : newItems) {
            InfluencerConversionOrderItem oldItem = oldMap.get(newItem.getShopifyLineItemId());
            if (oldItem == null)
                return true;
            if (!Objects.equals(newItem.getQuantity(), oldItem.getQuantity()))
                return true;
            if (!Objects.equals(newItem.getPrice(), oldItem.getPrice()))
                return true;
        }
        return false;
    }
}
