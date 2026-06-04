package com.athlunakms.shopify.order.service;

import com.athlunakms.shopify.common.security.EncryptionService;
import com.athlunakms.shopify.order.dto.SyncProgressDto;
import com.athlunakms.shopify.order.entity.OrderLineItem;
import com.athlunakms.shopify.order.entity.OrderRefund;
import com.athlunakms.shopify.order.entity.OrderRefundLineItem;
import com.athlunakms.shopify.order.entity.OrderTransaction;
import com.athlunakms.shopify.order.entity.ShopifyOrder;
import com.athlunakms.shopify.order.repository.*;
import com.athlunakms.shopify.store.entity.ShopifyStore;
import com.athlunakms.shopify.store.repository.ShopifyStoreRepository;
import com.athlunakms.shopify.product.repository.ShopifyProductVariantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Shopify 订单同步服务
 * 负责与 Shopify API 交互拉取并同步订单、交易、退款及履约数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopifySyncService {

    private final ShopifyStoreRepository storeRepository;
    private final ShopifyOrderRepository orderRepository;
    private final OrderLineItemRepository lineItemRepository;
    private final OrderTransactionRepository transactionRepository;
    private final OrderRefundRepository refundRepository;
    private final OrderRefundLineItemRepository refundLineItemRepository;
    private final ShopifyProductVariantRepository variantRepository;
    private final EncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderClassificationService classificationService;
    private final PlatformTransactionManager transactionManager;

    private final Map<Long, SyncProgressDto> syncProgressMap = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> cancelFlagMap = new ConcurrentHashMap<>();

    private static final String API_VERSION = "2025-01";
    private static final Pattern NEXT_PAGE_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");
    /** 进程崩溃后进度可能永久 RUNNING，导致之后所有手动同步被静默跳过 */
    private static final long STALE_RUNNING_SYNC_MS = 45 * 60 * 1000L;

    /** 仅写完主表及明细；localOrderId 为 null 表示未写库（如跳过未变更订单） */
    private record OrderGraphqlPersistOutcome(boolean isNew, Long localOrderId) {
    }

    /**
     * 异步同步订单逻辑
     *
     * @param useCreatedAtForShopifyQuery true=Shopify 查询与 REST count 按「创建时间」筛选（适合界面手动选日期范围）；
     *                                    false=按「更新时间」筛选（适合定时增量同步）
     */
    @Async
    public void syncOrders(Long storeId, LocalDateTime startTime, LocalDateTime endTime, boolean forceUpdate,
            boolean useCreatedAtForShopifyQuery) {
        SyncProgressDto progress = syncProgressMap.get(storeId);
        if (progress != null && "RUNNING".equals(progress.getStatus())) {
            long started = progress.getStartTime() != null ? progress.getStartTime() : 0L;
            long ageMs = started > 0 ? System.currentTimeMillis() - started : STALE_RUNNING_SYNC_MS + 1;
            if (ageMs > STALE_RUNNING_SYNC_MS) {
                log.warn("Clearing stale RUNNING order sync for storeId={} (age {} ms), starting new sync", storeId,
                        ageMs);
                syncProgressMap.remove(storeId);
                cancelFlagMap.remove(storeId);
            } else {
                log.warn("Sync already running for storeId: {}, skipping", storeId);
                return;
            }
        }

        progress = SyncProgressDto.builder()
                .status("RUNNING")
                .startTime(System.currentTimeMillis())
                .message("正在从 Shopify (GraphQL) 获取订单数据...")
                .build();
        syncProgressMap.put(storeId, progress);
        cancelFlagMap.remove(storeId);

        try {
            ShopifyStore store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new RuntimeException("Store not found"));

            String storeUrl = encryptionService.decrypt(store.getStoreUrlEncrypted());
            String accessToken = encryptionService.decrypt(store.getAccessTokenEncrypted());
            String storeName = store.getStoreName();
            String apiDomainUrl = isNonEmpty(store.getMyshopifyDomain()) ? store.getMyshopifyDomain() : storeUrl;
            String shopDomain = normalizeDomain(storeUrl);

            log.info("Starting sync for store: {} (domain: {}), dateFilter={}, forceUpdate={}",
                    storeName, apiDomainUrl, useCreatedAtForShopifyQuery ? "created_at" : "updated_at", forceUpdate);

            // 0. 获取订单总数 (使用 REST API)
            int estimatedTotal = 0;
            try {
                estimatedTotal = fetchOrderCountREST(apiDomainUrl, accessToken, startTime, endTime,
                        useCreatedAtForShopifyQuery);
            } catch (Exception e) {
                log.warn("Failed to fetch order count via REST, progress total might be inaccurate", e);
            }

            progress.setTotal(estimatedTotal);
            progress.setMessage(String.format("发现 %d 个订单，准备同步...", estimatedTotal));

            int totalProcessed = 0;
            int totalSuccess = 0;
            int totalError = 0;
            int totalNew = 0;
            int totalUpdated = 0;

            String cursor = null;
            boolean hasNextPage = true;

            while (hasNextPage) {
                if (Boolean.TRUE.equals(cancelFlagMap.get(storeId))) {
                    log.info("Sync cancelled by user for storeId: {}", storeId);
                    progress.setStatus("CANCELLED");
                    progress.setMessage(String.format("同步已取消: 已处理 %d 个订单", totalProcessed));
                    progress.setEndTime(System.currentTimeMillis());
                    return;
                }

                JsonNode response = fetchOrdersGraphQL(apiDomainUrl, accessToken, startTime, endTime, cursor,
                        useCreatedAtForShopifyQuery);
                if (response == null || !response.has("data")) {
                    log.error("GraphQL response invalid or empty");
                    break;
                }

                JsonNode ordersData = response.path("data").path("orders");
                JsonNode edges = ordersData.path("edges");
                JsonNode pageInfo = ordersData.path("pageInfo");

                if (!edges.isArray() || edges.isEmpty()) {
                    break;
                }

                for (JsonNode edge : edges) {
                    JsonNode orderNode = edge.path("node");
                    cursor = edge.path("cursor").asText();
                    totalProcessed++;

                    try {
                        boolean isNewOrder = syncOrderFromGraphqlNode(storeId, storeName, shopDomain, orderNode,
                                forceUpdate);

                        if (isNewOrder)
                            totalNew++;
                        else
                            totalUpdated++;
                        totalSuccess++;
                    } catch (Exception e) {
                        totalError++;
                        log.error("Error processing order {}: {}", orderNode.path("name").asText(), e.getMessage());
                    }

                    progress.setProcessed(totalProcessed);
                    if (totalProcessed > progress.getTotal())
                        progress.setTotal(totalProcessed);
                    progress.setSuccess(totalSuccess);
                    progress.setError(totalError);
                    progress.setMessage(String.format("正在同步: %d / %d (成功: %d, 失败: %d)",
                            totalProcessed, progress.getTotal(), totalSuccess, totalError));
                }

                hasNextPage = pageInfo.path("hasNextPage").asBoolean(false);
            }

            log.info("Synced orders (GraphQL) for store {}: Total {}, New {}, Updated {}, Failed {}",
                    storeName, totalSuccess, totalNew, totalUpdated, totalError);

            // 自动修复交付状态
            classificationService.repairDeliveredStatus();

            progress.setStatus("COMPLETED");
            progress.setMessage(String.format("同步完成: 共 %d 个订单 (新增: %d, 更新: %d, 失败: %d)",
                    totalProcessed, totalNew, totalUpdated, totalError));
            progress.setEndTime(System.currentTimeMillis());

        } catch (Exception e) {
            log.error("Order sync (GraphQL) failed for store: " + storeId, e);
            progress.setStatus("FAILED");
            progress.setMessage("同步失败: " + e.getMessage());
            progress.setEndTime(System.currentTimeMillis());
        }
    }

    private JsonNode fetchOrdersGraphQL(String storeUrl, String accessToken, LocalDateTime startTime,
            LocalDateTime endTime, String cursor, boolean useCreatedAtForShopifyQuery) {
        String graphqlUrl = buildGraphqlUrl(storeUrl);
        HttpHeaders headers = createHeaders(accessToken);
        ObjectNode requestBody = objectMapper.createObjectNode();

        String query = """
                query getOrders($cursor: String, $query: String) {
                  orders(first: 100, after: $cursor, query: $query, sortKey: UPDATED_AT, reverse: true) {
                    pageInfo { hasNextPage endCursor }
                    edges {
                      cursor
                      node {
                        id legacyResourceId name createdAt updatedAt processedAt closedAt cancelledAt cancelReason
                        displayFinancialStatus displayFulfillmentStatus email phone note tags
                        subtotalPriceSet { shopMoney { amount currencyCode } }
                        totalTaxSet { shopMoney { amount currencyCode } }
                        totalDiscountsSet { shopMoney { amount currencyCode } }
                        totalShippingPriceSet { shopMoney { amount currencyCode } }
                        totalPriceSet { shopMoney { amount currencyCode } }
                        discountCodes
                        customer { id email firstName lastName phone }
                        shippingAddress { name address1 address2 city province provinceCode country countryCodeV2 zip phone }
                        lineItems(first: 50) {
                            nodes {
                                id sku title variantTitle quantity
                                originalUnitPriceSet { shopMoney { amount } }
                                variant { id sku image { url } product { id } }
                                requiresShipping fulfillmentStatus vendor
                            }
                        }
                        transactions(first: 10) {
                            id kind status gateway amountSet { shopMoney { amount currencyCode } } createdAt paymentId
                        }
                        fulfillments {
                            id createdAt status displayStatus inTransitAt deliveredAt estimatedDeliveryAt
                            trackingInfo { number url }
                        }
                        refunds {
                            id createdAt note totalRefundedSet { shopMoney { amount } }
                            refundLineItems(first: 20) {
                                nodes { id lineItem { id } quantity subtotalSet { shopMoney { amount } } }
                            }
                        }
                      }
                    }
                  }
                }
                """;
        requestBody.put("query", query);
        ObjectNode variables = objectMapper.createObjectNode();
        if (cursor != null)
            variables.put("cursor", cursor);

        StringBuilder queryString = new StringBuilder("status:any");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        String dateField = useCreatedAtForShopifyQuery ? "created_at" : "updated_at";
        if (startTime != null)
            queryString.append(' ').append(dateField).append(":>=")
                    .append(startTime.atOffset(java.time.ZoneOffset.UTC).format(formatter));
        if (endTime != null)
            queryString.append(' ').append(dateField).append(":<=")
                    .append(endTime.atOffset(java.time.ZoneOffset.UTC).format(formatter));
        variables.put("query", queryString.toString());
        requestBody.set("variables", variables);

        try {
            log.info("Shopify orders GraphQL search query: {}", queryString);
            HttpEntity<ObjectNode> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(graphqlUrl, HttpMethod.POST, entity,
                    JsonNode.class);
            JsonNode body = response.getBody();
            if (body != null && body.has("errors") && !body.path("errors").isEmpty()) {
                log.error("Shopify GraphQL errors on orders query: {}", body.get("errors"));
            }
            return body;
        } catch (Exception e) {
            log.error("GraphQL request failed", e);
            return null;
        }
    }

    private boolean syncOrderFromGraphqlNode(Long storeId, String storeName, String shopDomain, JsonNode node,
            boolean forceUpdate) {
        TransactionTemplate persistTx = new TransactionTemplate(transactionManager);
        persistTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        OrderGraphqlPersistOutcome outcome = persistTx
                .execute(status -> persistShopifyOrderGraphqlPayload(storeId, storeName, shopDomain, node, forceUpdate));
        if (outcome != null && outcome.localOrderId() != null) {
            runClassificationInSeparateTransaction(outcome.localOrderId());
        }
        return outcome != null && outcome.isNew();
    }

    private void runClassificationInSeparateTransaction(Long localOrderId) {
        TransactionTemplate classifyTx = new TransactionTemplate(transactionManager);
        classifyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        try {
            classifyTx.execute(status -> {
                ShopifyOrder order = orderRepository.findById(localOrderId).orElse(null);
                if (order == null) {
                    log.warn("[OrderSync] Cannot classify: order id {} not found after persist", localOrderId);
                    return null;
                }
                List<OrderLineItem> lineItems = lineItemRepository.findByOrderId(localOrderId);
                classificationService.syncBusinessTable(order, lineItems);
                return null;
            });
        } catch (Exception e) {
            // 主表已提交：分类失败不得掩盖「订单已同步」的事实，避免整单从 DB 消失
            log.error(
                    "[OrderSync] syncBusinessTable failed after order was saved (localOrderId={}, name may be in logs above): {}",
                    localOrderId, e.toString(), e);
        }
    }

    private OrderGraphqlPersistOutcome persistShopifyOrderGraphqlPayload(Long storeId, String storeName,
            String shopDomain, JsonNode node, boolean forceUpdate) {
        String shopifyOrderId = extractLegacyId(node.path("id").asText());
        String name = node.path("name").asText();

        // 1. 更健壮的订单号解析 (e.g. "#1001" -> 1001)
        Long shopifyOrderNumber = 0L;
        try {
            String numStr = name.replace("#", "").trim();
            shopifyOrderNumber = Long.parseLong(numStr);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse order number from name: {}", name);
        }

        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime incomingUpdatedAt = parseDateTime(node.path("updatedAt").asText(null), isoFormatter);

        ShopifyOrder order = orderRepository.findByShopifyOrderId(shopifyOrderId).orElse(new ShopifyOrder());
        boolean isNew = order.getId() == null;

        if (!forceUpdate && !isNew && order.getUpdatedAtShopify() != null && incomingUpdatedAt != null) {
            if (!incomingUpdatedAt.isAfter(order.getUpdatedAtShopify())) {
                log.debug("Skipping stale order {}", name);
                return new OrderGraphqlPersistOutcome(false, null);
            }
        }

        order.setStoreId(storeId);
        order.setStoreName(storeName);
        order.setShopDomain(shopDomain);
        order.setShopifyOrderId(shopifyOrderId);
        order.setShopifyOrderNumber(shopifyOrderNumber);
        order.setName(name);
        if (order.getOrderSource() == null)
            order.setOrderSource("shopify_sync_gql");

        order.setCreatedAtShopify(parseDateTime(node.path("createdAt").asText(null), isoFormatter));
        order.setUpdatedAtShopify(incomingUpdatedAt);
        order.setProcessedAtShopify(parseDateTime(node.path("processedAt").asText(null), isoFormatter));
        order.setClosedAtShopify(parseDateTime(node.path("closedAt").asText(null), isoFormatter));
        order.setCancelledAtShopify(parseDateTime(node.path("cancelledAt").asText(null), isoFormatter));
        order.setCancelReason(node.path("cancelReason").asText(null));
        order.setFinancialStatus(node.path("displayFinancialStatus").asText("").toLowerCase());
        order.setFulfillmentStatus(node.path("displayFulfillmentStatus").asText("").toLowerCase());

        // Money
        order.setCurrency(node.path("totalPriceSet").path("shopMoney").path("currencyCode").asText("USD"));
        order.setSubtotalPrice(
                parseDecimal(node.path("subtotalPriceSet").path("shopMoney").path("amount").asText("0")));
        order.setTotalTax(parseDecimal(node.path("totalTaxSet").path("shopMoney").path("amount").asText("0")));
        order.setTotalDiscounts(
                parseDecimal(node.path("totalDiscountsSet").path("shopMoney").path("amount").asText("0")));
        order.setTotalShippingPrice(
                parseDecimal(node.path("totalShippingPriceSet").path("shopMoney").path("amount").asText("0")));
        order.setTotalPrice(parseDecimal(node.path("totalPriceSet").path("shopMoney").path("amount").asText("0")));

        // Customer
        JsonNode customer = node.path("customer");
        if (!customer.isMissingNode() && !customer.isNull()) {
            order.setShopifyCustomerId(extractLegacyId(customer.path("id").asText(null)));
            order.setCustomerEmail(customer.path("email").asText(node.path("email").asText()));
            order.setCustomerFirstName(customer.path("firstName").asText(null));
            order.setCustomerLastName(customer.path("lastName").asText(null));
            order.setCustomerPhone(customer.path("phone").asText(node.path("phone").asText()));
        } else {
            order.setCustomerEmail(node.path("email").asText());
            order.setCustomerPhone(node.path("phone").asText());
        }

        // Shipping Address
        JsonNode address = node.path("shippingAddress");
        if (!address.isMissingNode() && !address.isNull()) {
            order.setShippingName(address.path("name").asText(
                    address.path("firstName").asText("") + " " + address.path("lastName").asText("")).trim());
            order.setShippingAddress1(address.path("address1").asText(null));
            order.setShippingAddress2(address.path("address2").asText(null));
            order.setShippingCity(address.path("city").asText(null));
            order.setShippingProvince(address.path("province").asText(null));
            order.setShippingProvinceCode(address.path("provinceCode").asText(null));
            order.setShippingCountry(address.path("country").asText(null));
            order.setShippingCountryCode(address.path("countryCodeV2").asText(null));
            order.setShippingZip(address.path("zip").asText(null));
            order.setShippingPhone(address.path("phone").asText(null));
        }


        order.setNote(node.path("note").asText(null));
        order.setTags(node.path("tags").toString());

        // Discount Codes
        List<String> codes = new ArrayList<>();
        JsonNode discountCodesNode = node.path("discountCodes");
        if (discountCodesNode.isArray()) {
            for (JsonNode dc : discountCodesNode) {
                String code = dc.asText();
                if (code != null && !code.isEmpty())
                    codes.add(code);
            }
        }
        if (!codes.isEmpty()) {
            order.setDiscountCodes(String.join(",", codes));
        }

        // Fulfillments 处理 (缺失逻辑补全：解析多个履约单及物流信息)
        processGraphqlFulfillments(order, node.path("fulfillments"));

        ShopifyOrder savedOrder = orderRepository.save(order);
        List<OrderLineItem> lineItems = syncLineItems(savedOrder, node.path("lineItems").path("nodes"),
                node.path("fulfillments"));
        syncTransactions(savedOrder, node.path("transactions"));
        syncRefunds(savedOrder, node.path("refunds"), lineItems);

        return new OrderGraphqlPersistOutcome(isNew, savedOrder.getId());
    }

    private void processGraphqlFulfillments(ShopifyOrder order, JsonNode fulfillmentsNode) {
        if (fulfillmentsNode.isMissingNode() || !fulfillmentsNode.isArray())
            return;

        List<String> trackingNumbers = new ArrayList<>();
        List<String> fulfillmentIds = new ArrayList<>();
        String firstDisplayStatus = null;

        DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime latestCreated = null;
        LocalDateTime latestDelivered = null;
        LocalDateTime latestInTransit = null;
        LocalDateTime latestEstimatedDelivery = null;

        for (JsonNode f : fulfillmentsNode) {
            String fid = extractLegacyId(f.path("id").asText());
            fulfillmentIds.add(fid);

            JsonNode trackingInfo = f.path("trackingInfo");
            if (trackingInfo.isArray()) {
                for (JsonNode t : trackingInfo) {
                    String num = t.path("number").asText();
                    if (!num.isEmpty())
                        trackingNumbers.add(num);
                }
            }

            String status = f.path("displayStatus").asText(null);
            if (firstDisplayStatus == null && status != null && !status.isEmpty()) {
                firstDisplayStatus = status;
            }

            LocalDateTime created = parseDateTime(f.path("createdAt").asText(null), isoFormatter);
            if (created != null && (latestCreated == null || created.isAfter(latestCreated)))
                latestCreated = created;

            LocalDateTime delivered = parseDateTime(f.path("deliveredAt").asText(null), isoFormatter);
            if (delivered != null && (latestDelivered == null || delivered.isAfter(latestDelivered)))
                latestDelivered = delivered;

            LocalDateTime inTransit = parseDateTime(f.path("inTransitAt").asText(null), isoFormatter);
            if (inTransit != null && (latestInTransit == null || inTransit.isAfter(latestInTransit)))
                latestInTransit = inTransit;

            LocalDateTime estimated = parseDateTime(f.path("estimatedDeliveryAt").asText(null), isoFormatter);
            if (estimated != null && (latestEstimatedDelivery == null || estimated.isAfter(latestEstimatedDelivery)))
                latestEstimatedDelivery = estimated;
        }

        order.setFulfillmentIds(String.join(",", fulfillmentIds));
        order.setTrackingNumbers(String.join(",", trackingNumbers));
        order.setFulfillmentCreatedAt(latestCreated);
        order.setDeliveredAt(latestDelivered);
        order.setInTransitAt(latestInTransit);
        order.setEstimatedDeliveryAt(latestEstimatedDelivery);

        if (firstDisplayStatus != null) {
            order.setFulfillmentDisplayStatus(firstDisplayStatus.toLowerCase());
            if ("delivered".equalsIgnoreCase(firstDisplayStatus) && order.getDeliveredAt() == null) {
                order.setDeliveredAt(LocalDateTime.now());
            }
        }
    }

    private List<OrderLineItem> syncLineItems(ShopifyOrder order, JsonNode nodes, JsonNode fulfillmentNodes) {
        List<OrderLineItem> items = new ArrayList<>();
        List<String> currentIds = new ArrayList<>();

        // 映射行项目到履约 ID
        Map<String, String> lineItemFulfillmentMap = new HashMap<>();
        if (fulfillmentNodes != null && fulfillmentNodes.isArray()) {
            for (JsonNode f : fulfillmentNodes) {
                String fid = extractLegacyId(f.path("id").asText());
                JsonNode lines = f.path("fulfillmentLineItems").path("nodes");
                if (lines != null && lines.isArray()) {
                    for (JsonNode fl : lines) {
                        String liId = extractLegacyId(fl.path("lineItem").path("id").asText());
                        lineItemFulfillmentMap.put(liId, fid);
                    }
                }
            }
        }

        for (JsonNode n : nodes) {
            String sid = extractLegacyId(n.get("id").asText());
            currentIds.add(sid);
            OrderLineItem item = lineItemRepository.findByShopifyLineItemId(sid).orElse(new OrderLineItem());
            item.setOrderId(order.getId());
            item.setShopifyLineItemId(sid);
            item.setSku(n.path("sku").asText(null));
            item.setTitle(n.path("title").asText(null));
            item.setVariantTitle(n.path("variantTitle").asText(null));
            item.setQuantity(n.path("quantity").asInt(0));
            item.setPrice(parseDecimal(n.path("originalUnitPriceSet").path("shopMoney").path("amount").asText("0")));
            item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            if (lineItemFulfillmentMap.containsKey(sid)) {
                item.setFulfillmentId(lineItemFulfillmentMap.get(sid));
            }

            JsonNode v = n.path("variant");
            if (!v.isMissingNode() && !v.isNull()) {
                item.setShopifyVariantId(Long.parseLong(extractLegacyId(v.path("id").asText("0"))));
                item.setShopifyProductId(Long.parseLong(extractLegacyId(v.path("product").path("id").asText("0"))));

                JsonNode img = v.path("image");
                if (!img.isMissingNode() && !img.isNull()) {
                    item.setImageUrl(img.path("url").asText(null));
                }

                if ((item.getImageUrl() == null || item.getImageUrl().isEmpty())
                        && item.getShopifyVariantId() != null) {
                    variantRepository.findByShopifyVariantId(item.getShopifyVariantId())
                            .ifPresent(var -> item.setImageUrl(var.getImageUrl()));
                }
            }
            item.setFulfillmentStatus(n.path("fulfillmentStatus").asText("").toLowerCase());
            item.setVendor(n.path("vendor").asText(""));
            items.add(lineItemRepository.save(item));
        }
        lineItemRepository.findByOrderId(order.getId()).forEach(existing -> {
            if (!currentIds.contains(existing.getShopifyLineItemId()))
                lineItemRepository.delete(existing);
        });
        return items;
    }

    private void syncTransactions(ShopifyOrder order, JsonNode txs) {
        if (!txs.isArray())
            return;
        List<String> currentIds = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_DATE_TIME;
        for (JsonNode n : txs) {
            String sid = extractLegacyId(n.path("id").asText());
            currentIds.add(sid);
            OrderTransaction tx = transactionRepository.findByShopifyTransactionId(sid).orElse(new OrderTransaction());
            tx.setOrderId(order.getId());
            tx.setShopifyTransactionId(sid);
            tx.setKind(n.path("kind").asText(null));
            tx.setStatus(n.path("status").asText(null));
            tx.setGateway(n.path("gateway").asText(null));
            tx.setAmount(parseDecimal(n.path("amountSet").path("shopMoney").path("amount").asText("0")));
            tx.setCurrency(n.path("amountSet").path("shopMoney").path("currencyCode").asText(null));
            tx.setCreatedAtShopify(parseDateTime(n.path("createdAt").asText(null), iso));
            transactionRepository.save(tx);
        }
        transactionRepository.findByOrderId(order.getId()).forEach(existing -> {
            if (!currentIds.contains(existing.getShopifyTransactionId()))
                transactionRepository.delete(existing);
        });
    }

    private void syncRefunds(ShopifyOrder order, JsonNode refunds, List<OrderLineItem> lineItems) {
        if (!refunds.isArray())
            return;
        List<String> currentIds = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_DATE_TIME;
        BigDecimal totalRefunded = BigDecimal.ZERO;
        Map<String, Integer> qtyMap = new HashMap<>();

        for (JsonNode n : refunds) {
            String sid = extractLegacyId(n.path("id").asText());
            currentIds.add(sid);
            OrderRefund refund = refundRepository.findByShopifyRefundId(sid).orElse(new OrderRefund());
            refund.setOrderId(order.getId());
            refund.setShopifyRefundId(sid);
            refund.setCreatedAtShopify(parseDateTime(n.path("createdAt").asText(null), iso));
            refund.setNote(n.path("note").asText(null));

            BigDecimal rAmt = BigDecimal.ZERO;
            JsonNode rTxs = n.path("transactions"); // GraphQL nodes structure may differ, assuming simple list or
                                                    // adjust if needed
            if (rTxs.isArray()) {
                for (JsonNode rtx : rTxs) {
                    if ("SUCCESS".equalsIgnoreCase(rtx.path("status").asText())) {
                        rAmt = rAmt
                                .add(parseDecimal(rtx.path("amountSet").path("shopMoney").path("amount").asText("0")));
                    }
                }
            } else {
                rAmt = parseDecimal(n.path("totalRefundedSet").path("shopMoney").path("amount").asText("0"));
            }

            refund.setTotalRefunded(rAmt);
            totalRefunded = totalRefunded.add(rAmt);
            refund = refundRepository.save(refund);

            // 保存退款行项目详情 (缺失逻辑补全)
            JsonNode rLines = n.path("refundLineItems").path("nodes");
            if (rLines.isArray()) {
                for (JsonNode rl : rLines) {
                    String rlId = extractLegacyId(rl.path("id").asText());
                    String liId = extractLegacyId(rl.path("lineItem").path("id").asText());
                    int qty = rl.path("quantity").asInt(0);
                    qtyMap.merge(liId, qty, Integer::sum);

                    OrderRefundLineItem refundLineItem = refundLineItemRepository.findByShopifyRefundLineId(rlId)
                            .orElse(new OrderRefundLineItem());
                    refundLineItem.setRefundId(refund.getId());
                    refundLineItem.setShopifyRefundLineId(rlId);
                    refundLineItem.setShopifyOrderLineItemId(liId);
                    refundLineItem.setQuantity(qty);
                    refundLineItem.setSubtotal(
                            parseDecimal(rl.path("subtotalSet").path("shopMoney").path("amount").asText("0")));
                    refundLineItem.setTaxAmount(
                            parseDecimal(rl.path("totalTaxSet").path("shopMoney").path("amount").asText("0")));
                    refundLineItem.setCurrency(order.getCurrency());
                    refundLineItemRepository.save(refundLineItem);
                }
            }
        }

        // 更新订单行项目的退货状态
        qtyMap.forEach((lsid, q) -> {
            lineItemRepository.findByShopifyLineItemId(lsid).ifPresent(item -> {
                item.setReturnedQuantity(q);
                item.setIsReturned(q >= (item.getQuantity() != null ? item.getQuantity() : 1));
                lineItemRepository.save(item);
            });
        });

        if (totalRefunded.compareTo(BigDecimal.ZERO) > 0) {
            order.setRefundTotal(totalRefunded);
            if (totalRefunded.compareTo(order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO) >= 0) {
                order.setFinancialStatus("refunded");
            } else {
                order.setFinancialStatus("partially_refunded");
            }
            orderRepository.save(order);
        }

        refundRepository.findByOrderId(order.getId()).forEach(existing -> {
            if (!currentIds.contains(existing.getShopifyRefundId()))
                refundRepository.delete(existing);
        });
    }

    private int fetchOrderCountREST(String storeUrl, String accessToken, LocalDateTime start, LocalDateTime end,
            boolean useCreatedAtForShopifyQuery) {
        String url = "https://" + normalizeDomain(storeUrl) + "/admin/api/" + API_VERSION
                + "/orders/count.json?status=any";
        DateTimeFormatter iso = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (useCreatedAtForShopifyQuery) {
            if (start != null)
                url += "&created_at_min=" + start.atOffset(java.time.ZoneOffset.UTC).format(iso);
            if (end != null)
                url += "&created_at_max=" + end.atOffset(java.time.ZoneOffset.UTC).format(iso);
        } else {
            if (start != null)
                url += "&updated_at_min=" + start.atOffset(java.time.ZoneOffset.UTC).format(iso);
            if (end != null)
                url += "&updated_at_max=" + end.atOffset(java.time.ZoneOffset.UTC).format(iso);
        }
        HttpHeaders headers = createHeaders(accessToken);
        ResponseEntity<JsonNode> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers),
                JsonNode.class);
        return resp.getBody().path("count").asInt(0);
    }

    // 同步进度查询（带自动过期清理：终态超过10分钟自动移除）
    private static final long PROGRESS_TTL_MS = 10 * 60 * 1000L;

    public SyncProgressDto getProgress(Long storeId) {
        SyncProgressDto p = syncProgressMap.get(storeId);
        if (p != null && p.getEndTime() != null && p.getEndTime() > 0) {
            long elapsed = System.currentTimeMillis() - p.getEndTime();
            if (elapsed > PROGRESS_TTL_MS) {
                syncProgressMap.remove(storeId);
                cancelFlagMap.remove(storeId);
                return null;
            }
        }
        return p;
    }

    public void clearProgress(Long storeId) {
        syncProgressMap.remove(storeId);
        cancelFlagMap.remove(storeId);
    }

    public boolean cancelSync(Long storeId) {
        SyncProgressDto p = syncProgressMap.get(storeId);
        if (p != null && "RUNNING".equals(p.getStatus())) {
            cancelFlagMap.put(storeId, true);
            return true;
        }
        return false;
    }

    private String buildGraphqlUrl(String storeUrl) {
        return "https://" + normalizeDomain(storeUrl) + "/admin/api/" + API_VERSION + "/graphql.json";
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Shopify-Access-Token", token);
        h.set("Content-Type", "application/json");
        return h;
    }

    private String normalizeDomain(String d) {
        if (d == null)
            return "";
        d = d.replace("https://", "").replace("http://", "");
        if (d.endsWith("/"))
            d = d.substring(0, d.length() - 1);
        return d;
    }

    private boolean isNonEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    private String extractLegacyId(String gid) {
        if (gid == null || !gid.contains("/"))
            return gid;
        return gid.substring(gid.lastIndexOf("/") + 1);
    }

    private BigDecimal parseDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Shopify GraphQL 多为带 Z 或时区偏移的 ISO-8601（如 2026-05-07T12:00:00Z），
     * 不能仅用 {@link DateTimeFormatter#ISO_DATE_TIME} + {@link LocalDateTime#parse}，否则会解析失败得到 null，
     * 进而导致 orders / 样品单 orderCreatedAt 为空，列表按时间筛选时整单被排除。
     */
    private LocalDateTime parseDateTime(String s, DateTimeFormatter f) {
        if (s == null || s.isEmpty())
            return null;
        String trimmed = s.trim();
        try {
            if (trimmed.endsWith("Z") || trimmed.contains("+") || trimmed.matches(".*[+-]\\d{2}:\\d{2}$")) {
                return java.time.OffsetDateTime.parse(trimmed)
                        .withOffsetSameInstant(java.time.ZoneOffset.UTC)
                        .toLocalDateTime();
            }
            return LocalDateTime.parse(trimmed, f);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e2) {
                log.warn("parseDateTime failed for Shopify timestamp: {}", trimmed);
                return null;
            }
        }
    }
}
