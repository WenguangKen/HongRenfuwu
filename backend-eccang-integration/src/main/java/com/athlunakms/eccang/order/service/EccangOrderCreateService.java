package com.athlunakms.eccang.order.service;

import com.athlunakms.eccang.common.security.EncryptionService;
import com.athlunakms.eccang.order.dto.OrderCreateRequest;
import com.athlunakms.eccang.order.dto.OrderCreateResponse;
import com.athlunakms.eccang.order.entity.OrderLineItem;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.repository.*;
import com.athlunakms.eccang.influencer.repository.InfluencerReadOnlyRepository;
import com.athlunakms.eccang.product.entity.EccangProductVariant;
import com.athlunakms.eccang.product.repository.EccangProductVariantRepository;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Eccang 订单创建与管理服务
 * 负责本地订单草稿的创建、确认以及通过 Eccang GraphQL API 创建真实订单
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EccangOrderCreateService {

    private final EccangOrderRepository orderRepository;
    private final OrderLineItemRepository lineItemRepository;
    private final EccangStoreRepository storeRepository;
    private final EccangProductVariantRepository variantRepository;
    private final EncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderClassificationService orderClassificationService;
    private final InfluencerReadOnlyRepository influencerRepository;

    private static final String API_VERSION = "2025-01";

    /**
     * 创建订单包含本地草稿流程和 Eccang 真实下单流程
     */
    @Transactional
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        log.info("执行订单创建: 店铺={}, 是否草稿={}", request.getStoreId(), request.getIsDraft());

        EccangStore store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new RuntimeException("未找到店铺信息"));

        if (Boolean.TRUE.equals(request.getIsDraft())) {
            return handleLocalDraft(request, store);
        }

        return handleEccangOrderCreation(request, store);
    }

    private OrderCreateResponse handleLocalDraft(OrderCreateRequest request, EccangStore store) {
        String draftId = "gid://eccang/Order/draft_" + UUID.randomUUID().toString();
        String draftName = "DRAFT-"
                + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        java.util.Map.Entry<EccangOrder, List<OrderLineItem>> result = saveOrderFromCreate(request, store,
                extractLegacyId(draftId), 0L, draftName, "-", null, null);

        EccangOrder order = result.getKey();
        order.setIsDraft(true);
        order.setOrderSource("manual_draft");
        orderRepository.save(order);

        orderClassificationService.classifyOrder(order, result.getValue());
        return OrderCreateResponse.success(draftId, 0L, draftName, order.getId());
    }

    private OrderCreateResponse handleEccangOrderCreation(OrderCreateRequest request, EccangStore store) {
        lockInventory(request);

        String currency = store.getCurrency() != null ? store.getCurrency() : "USD";
        ObjectNode graphqlBody = buildOrderCreateGraphQL(request, currency);

        // TODO: Use global EccangConfig
        String accessToken = "mock_token";
        String apiDomain = store.getStoreName();
        String url = String.format("https://%s/admin/api/%s/graphql.json", normalizeDomain(apiDomain), API_VERSION);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Eccang-Access-Token", accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(graphqlBody, headers), JsonNode.class);
            JsonNode body = response.getBody();

            validateEccangResponse(body);

            JsonNode orderNode = body.path("data").path("orderCreate").path("order");
            String eccangOrderId = orderNode.path("id").asText();
            String name = orderNode.path("name").asText();
            String financialStatus = orderNode.path("displayFinancialStatus").asText();
            String createdAt = orderNode.path("createdAt").asText();
            JsonNode lineItemsNode = orderNode.path("lineItems").path("nodes");
            Long eccangOrderNumber = Long.valueOf(extractLegacyId(eccangOrderId));

            java.util.Map.Entry<EccangOrder, List<OrderLineItem>> result = saveOrderFromCreate(request, store,
                    eccangOrderId, eccangOrderNumber, name, financialStatus, createdAt, lineItemsNode);

            orderClassificationService.classifyOrder(result.getKey(), result.getValue());
            return OrderCreateResponse.success(eccangOrderId, eccangOrderNumber, name, result.getKey().getId());

        } catch (Exception e) {
            log.error("Eccang 下单失败: {}", e.getMessage());
            throw new RuntimeException("Eccang API 调用失败: " + e.getMessage());
        }
    }

    @Transactional
    public OrderCreateResponse confirmDraft(Long draftOrderId) {
        EccangOrder draft = orderRepository.findById(draftOrderId)
                .orElseThrow(() -> new RuntimeException("未找到草稿订单"));

        if (!Boolean.TRUE.equals(draft.getIsDraft())) {
            throw new RuntimeException("该订单不是草稿状态无法确认");
        }

        OrderCreateRequest request = mapEntityToRequest(draft);
        request.setIsDraft(false);

        OrderCreateResponse res = createOrder(request);
        if (res.isSuccess()) {
            orderClassificationService.deleteDraftBusinessRecords(draftOrderId);
            lineItemRepository.deleteByOrderId(draftOrderId);
            orderRepository.delete(draft);
        }
        return res;
    }

    private void lockInventory(OrderCreateRequest request) {
        for (OrderCreateRequest.OrderLineItemRequest item : request.getLineItems()) {
            if (item.getVariantId() == null)
                continue;
            EccangProductVariant variant = variantRepository.findByIdWithLock(item.getVariantId())
                    .orElseThrow(() -> new RuntimeException("商品变体不存在: " + item.getVariantId()));
            if (variant.getInventoryQuantity() < item.getQuantity()) {
                throw new RuntimeException("库存不足: " + variant.getTitle());
            }
            variant.setInventoryQuantity(variant.getInventoryQuantity() - item.getQuantity());
            variantRepository.save(variant);
        }
    }

    private ObjectNode buildOrderCreateGraphQL(OrderCreateRequest request, String defaultCurrency) {
        ObjectNode root = objectMapper.createObjectNode();
        String query = """
                mutation orderCreate($order: OrderCreateOrderInput!, $options: OrderCreateOptionsInput) {
                  orderCreate(order: $order, options: $options) {
                    userErrors { field message }
                    order {
                      id name displayFinancialStatus createdAt
                      totalPriceSet { shopMoney { amount currencyCode } }
                      lineItems(first: 250) {
                        nodes {
                          id title quantity sku
                          originalUnitPriceSet { shopMoney { amount currencyCode } }
                        }
                      }
                    }
                  }
                }
                """;
        root.put("query", query);

        ObjectNode variables = objectMapper.createObjectNode();
        ObjectNode orderInput = objectMapper.createObjectNode();
        String currency = request.getCurrency() != null ? request.getCurrency() : defaultCurrency;
        orderInput.put("currency", currency);

        ArrayNode lineItemsArray = objectMapper.createArrayNode();
        for (OrderCreateRequest.OrderLineItemRequest item : request.getLineItems()) {
            ObjectNode lineItem = objectMapper.createObjectNode();
            String variantGid = item.getEccangVariantId() != null
                    ? "gid://eccang/ProductVariant/" + item.getEccangVariantId()
                    : null;
            if (variantGid != null)
                lineItem.put("variantId", variantGid);
            lineItem.put("quantity", item.getQuantity());

            BigDecimal finalPrice = item.getPriceOverride() != null ? item.getPriceOverride() : BigDecimal.ZERO;
            ObjectNode priceSet = objectMapper.createObjectNode();
            ObjectNode shopMoney = objectMapper.createObjectNode();
            shopMoney.put("amount", finalPrice.toString());
            shopMoney.put("currencyCode", currency);
            priceSet.set("shopMoney", shopMoney);
            lineItem.set("priceSet", priceSet);
            lineItemsArray.add(lineItem);
        }
        orderInput.set("lineItems", lineItemsArray);

        if (request.getCustomerEmail() != null)
            orderInput.put("email", request.getCustomerEmail());
        if (request.getShippingAddress() != null)
            orderInput.set("shippingAddress", buildAddressNode(request.getShippingAddress()));
        if (request.getNote() != null)
            orderInput.put("note", request.getNote());
        if (request.getTags() != null)
            orderInput.put("tags", String.join(",", request.getTags()));

        if (Boolean.TRUE.equals(request.getIsFbaShipment())) {
            if (request.getFbaWarehouseCode() != null) {
                orderInput.put("warehouseCode", request.getFbaWarehouseCode());
            }
            if (request.getFbaShippingMethod() != null) {
                orderInput.put("shippingMethodCode", request.getFbaShippingMethod());
            }
        }

        variables.set("order", orderInput);
        root.set("variables", variables);
        return root;
    }

    private ObjectNode buildAddressNode(OrderCreateRequest.AddressRequest addr) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("firstName", addr.getFirstName() != null ? addr.getFirstName() : "");
        node.put("lastName", addr.getLastName() != null ? addr.getLastName() : ".");
        node.put("address1", addr.getAddress1());
        node.put("address2", addr.getAddress2());
        node.put("city", addr.getCity());
        node.put("countryCode", addr.getCountryCode());
        node.put("zip", addr.getZip());
        if (addr.getProvinceCode() != null)
            node.put("provinceCode", addr.getProvinceCode());
        return node;
    }

    private java.util.Map.Entry<EccangOrder, List<OrderLineItem>> saveOrderFromCreate(OrderCreateRequest request,
            EccangStore store, String eccangOrderId, Long eccangOrderNumber, String orderName,
            String financialStatus,
            String createdAt, JsonNode lineItemsNode) {

        EccangOrder order = new EccangOrder();
        order.setEccangOrderId(eccangOrderId);
        order.setEccangOrderNumber(eccangOrderNumber);
        order.setName(orderName);
        order.setStoreId(store.getId());
        order.setFinancialStatus(financialStatus);
        order.setFulfillmentStatus("unfulfilled");

        if (createdAt != null) {
            try {
                order.setCreatedAtEccang(
                        LocalDateTime.parse(createdAt, java.time.format.DateTimeFormatter.ISO_DATE_TIME));
            } catch (Exception e) {
            }
        } else {
            order.setCreatedAtEccang(LocalDateTime.now());
        }

        if (request.getShippingAddress() != null) {
            OrderCreateRequest.AddressRequest addr = request.getShippingAddress();
            order.setShippingName(addr.getFirstName() + " " + addr.getLastName());
            order.setShippingAddress1(addr.getAddress1());
            order.setShippingAddress2(addr.getAddress2());
            order.setShippingCity(addr.getCity());
            order.setShippingProvince(addr.getProvince());
            order.setShippingProvinceCode(addr.getProvinceCode());
            order.setShippingZip(addr.getZip());
            order.setShippingCountry(addr.getCountry());
            order.setShippingCountryCode(addr.getCountryCode());
            order.setShippingPhone(addr.getPhone());
        }

        order.setCurrency(request.getCurrency());
        if (request.getTags() != null)
            order.setTags(String.join(",", request.getTags()));
        if (request.getInfluencerId() != null) {
            order.setInfluencerId(request.getInfluencerId());
            influencerRepository.findById(request.getInfluencerId())
                    .ifPresent(inf -> order.setInfluencerName(inf.getRealName()));
        }

        order.setIsFbaShipment(Boolean.TRUE.equals(request.getIsFbaShipment()));
        order.setFbaWarehouseCode(request.getFbaWarehouseCode());
        order.setFbaShippingMethod(request.getFbaShippingMethod());

        EccangOrder savedOrder = orderRepository.save(order);
        List<OrderLineItem> savedItems = saveLineItemsFromCreate(savedOrder, request, lineItemsNode);

        return java.util.Map.entry(savedOrder, savedItems);
    }

    private List<OrderLineItem> saveLineItemsFromCreate(EccangOrder order, OrderCreateRequest request,
            JsonNode lineItemsNode) {
        List<OrderLineItem> items = new ArrayList<>();
        int index = 0;
        for (OrderCreateRequest.OrderLineItemRequest itemReq : request.getLineItems()) {
            OrderLineItem item = new OrderLineItem();
            item.setOrderId(order.getId());

            if (lineItemsNode != null && lineItemsNode.isArray() && index < lineItemsNode.size()) {
                JsonNode n = lineItemsNode.get(index);
                item.setEccangLineItemId(extractLegacyId(n.path("id").asText()));
                item.setTitle(n.path("title").asText(null));
                item.setSku(n.path("sku").asText(null));
                item.setPrice(
                        parseDecimal(n.path("originalUnitPriceSet").path("shopMoney").path("amount").asText("0")));
            }

            if (item.getEccangLineItemId() == null) {
                item.setEccangLineItemId("draft_" + UUID.randomUUID().toString());
            }

            if (itemReq.getVariantId() != null) {
                variantRepository.findById(itemReq.getVariantId()).ifPresent(v -> {
                    item.setEccangVariantId(v.getId());
                    item.setEccangProductId(v.getProductId());
                    if (item.getSku() == null)
                        item.setSku(v.getSku());
                    if (item.getTitle() == null)
                        item.setTitle(v.getTitle());
                    item.setImageUrl(v.getImageUrl());
                    if (item.getPrice() == null)
                        item.setPrice(v.getPrice());
                });
            }

            item.setQuantity(itemReq.getQuantity());
            if (itemReq.getPriceOverride() != null)
                item.setPrice(itemReq.getPriceOverride());
            if (item.getPrice() == null)
                item.setPrice(BigDecimal.ZERO);
            item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setFulfillmentStatus("unfulfilled");

            items.add(item);
            index++;
        }
        return lineItemRepository.saveAll(items);
    }

    private OrderCreateRequest mapEntityToRequest(EccangOrder order) {
        OrderCreateRequest req = new OrderCreateRequest();
        req.setStoreId(order.getStoreId());
        req.setCustomerEmail(order.getCustomerEmail());
        req.setCustomerPhone(order.getCustomerPhone());
        req.setCurrency(order.getCurrency());
        req.setNote(order.getNote());
        req.setInfluencerId(order.getInfluencerId());

        OrderCreateRequest.AddressRequest addr = new OrderCreateRequest.AddressRequest();
        addr.setAddress1(order.getShippingAddress1());
        addr.setCity(order.getShippingCity());
        addr.setCountryCode(order.getShippingCountryCode());
        req.setShippingAddress(addr);

        List<OrderLineItem> items = lineItemRepository.findByOrderId(order.getId());
        req.setLineItems(items.stream().map(i -> {
            OrderCreateRequest.OrderLineItemRequest ir = new OrderCreateRequest.OrderLineItemRequest();
            ir.setEccangVariantId(i.getEccangVariantId() != null ? String.valueOf(i.getEccangVariantId()) : null);
            ir.setQuantity(i.getQuantity());
            ir.setPriceOverride(i.getPrice());
            return ir;
        }).collect(java.util.stream.Collectors.toList()));
        return req;
    }

    @Transactional
    public OrderCreateResponse updateDraft(Long id, OrderCreateRequest request) {
        log.info("更新草稿订单 id={}", id);
        EccangOrder draft = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到草稿订单"));

        if (!Boolean.TRUE.equals(draft.getIsDraft())) {
            throw new RuntimeException("该订单不是草稿状态无法更新");
        }

        // 更新基本信息
        draft.setCustomerEmail(request.getCustomerEmail());
        draft.setCustomerPhone(request.getCustomerPhone());
        draft.setCurrency(request.getCurrency());
        draft.setNote(request.getNote());
        if (request.getTags() != null)
            draft.setTags(String.join(",", request.getTags()));

        if (request.getShippingAddress() != null) {
            OrderCreateRequest.AddressRequest addr = request.getShippingAddress();
            draft.setShippingName(addr.getFirstName() + " " + addr.getLastName());
            draft.setShippingAddress1(addr.getAddress1());
            draft.setShippingAddress2(addr.getAddress2());
            draft.setShippingCity(addr.getCity());
            draft.setShippingProvince(addr.getProvince());
            draft.setShippingZip(addr.getZip());
            draft.setShippingCountry(addr.getCountry());
            draft.setShippingCountryCode(addr.getCountryCode());
            draft.setShippingPhone(addr.getPhone());
        }

        if (request.getInfluencerId() != null) {
            draft.setInfluencerId(request.getInfluencerId());
            influencerRepository.findById(request.getInfluencerId())
                    .ifPresent(inf -> draft.setInfluencerName(inf.getRealName()));
        }

        EccangOrder savedOrder = orderRepository.save(draft);

        // 更新行项目
        lineItemRepository.deleteByOrderId(id);
        List<OrderLineItem> savedItems = saveLineItemsFromCreate(savedOrder, request, null);

        // 重新分类业务记录
        orderClassificationService.classifyOrder(savedOrder, savedItems);

        return OrderCreateResponse.success(savedOrder.getEccangOrderId(), savedOrder.getEccangOrderNumber(),
                savedOrder.getName(), savedOrder.getId());
    }

    @Transactional
    public void deleteDraft(Long id) {
        log.info("删除草稿订单 id={}", id);
        EccangOrder draft = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("未找到草稿订单"));

        if (!Boolean.TRUE.equals(draft.getIsDraft())) {
            throw new RuntimeException("非草稿订单无法在此删除");
        }

        orderClassificationService.deleteDraftBusinessRecords(id);
        lineItemRepository.deleteByOrderId(id);
        orderRepository.delete(draft);
    }

    private void validateEccangResponse(JsonNode body) {
        if (body == null || body.has("errors"))
            throw new RuntimeException("GraphQL 系统错误");
        JsonNode userErrors = body.path("data").path("orderCreate").path("userErrors");
        if (userErrors.isArray() && userErrors.size() > 0)
            throw new RuntimeException(userErrors.get(0).path("message").asText());
    }

    private String extractLegacyId(String gid) {
        return (gid == null || !gid.contains("/")) ? gid : gid.substring(gid.lastIndexOf("/") + 1);
    }

    private String normalizeDomain(String url) {
        return url.replace("https://", "").replace("http://", "").split("/")[0];
    }

    private boolean isNonEmpty(String s) {
        return s != null && !s.isEmpty();
    }

    private BigDecimal parseDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
