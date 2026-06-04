package com.athlunakms.eccang.order.controller;

import com.athlunakms.eccang.order.dto.BindInfluencerRequest;
import com.athlunakms.eccang.order.dto.ConversionOrderDto;
import com.athlunakms.eccang.order.dto.DuplicateCheckResult;
import com.athlunakms.eccang.order.dto.SampleOrderDto;
import com.athlunakms.eccang.order.dto.SyncProgressDto;
import com.athlunakms.eccang.order.dto.OrderFilterDto;
import com.athlunakms.eccang.order.entity.InfluencerConversionOrder;
import com.athlunakms.eccang.order.entity.InfluencerSampleOrder;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.repository.InfluencerConversionOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerSampleOrderRepository;
import com.athlunakms.eccang.order.repository.EccangOrderRepository;
import com.athlunakms.eccang.order.service.EccangOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 红人订单 API
 * 提供样品单和转化订单的查询接口
 */
@Slf4j
@RestController
@RequestMapping("/v1/influencer-orders")
@RequiredArgsConstructor
public class InfluencerOrderController {

    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final EccangOrderRepository orderRepository;
    private final EccangOrderService orderService;
    private final com.athlunakms.eccang.store.repository.EccangStoreRepository storeRepository;
    private final com.athlunakms.eccang.order.repository.OrderLineItemRepository lineItemRepository;

    private final com.athlunakms.eccang.order.repository.InfluencerSampleOrderItemRepository sampleItemRepository;
    private final com.athlunakms.eccang.order.repository.InfluencerConversionOrderItemRepository conversionItemRepository;
    private final com.athlunakms.eccang.product.repository.EccangProductVariantRepository variantRepository;
    private final com.athlunakms.eccang.order.repository.InfluencerChangeLogRepository changeLogRepository;
    private final com.athlunakms.eccang.influencer.repository.InfluencerReadOnlyRepository influencerReadOnlyRepository;
    private final com.athlunakms.eccang.influencer.repository.SystemTagReadOnlyRepository systemTagReadOnlyRepository;
    private final org.springframework.web.client.RestTemplate restTemplate;

    @org.springframework.beans.factory.annotation.Value("${influencer.service.url:http://localhost:8082/influencer-api}")
    private String influencerServiceUrl;

    private void logChange(Long influencerId, String fieldName, String oldVal, String newVal, String operator,
            String remark) {
        if (influencerId == null)
            return;
        com.athlunakms.eccang.order.entity.InfluencerChangeLog logEntry = new com.athlunakms.eccang.order.entity.InfluencerChangeLog();
        logEntry.setInfluencerId(influencerId);
        logEntry.setFieldName(fieldName);
        logEntry.setOldValue(oldVal);
        logEntry.setNewValue(newVal);
        logEntry.setOperator(operator);
        logEntry.setRemark(remark);
        changeLogRepository.save(logEntry);
    }

    @PostMapping("/bind")
    public ResponseEntity<Void> bindInfluencer(@RequestBody BindInfluencerRequest request) {
        log.debug("Received bind request: orderNo={}, influencerId={}", request.getOrderNo(),
                request.getInfluencerId());
        try {
            orderService.bindInfluencerToOrder(request.getOrderNo(), request.getInfluencerId(),
                    request.getInfluencerName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to bind influencer", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/check-order")
    public ResponseEntity<Map<String, Boolean>> checkOrderExists(@RequestParam(value = "orderNo") String orderNo) {
        // Check regular order number or eccang order number or name
        boolean exists = orderRepository.existsByName(orderNo) ||
                orderRepository.existsByEccangOrderNumber(parseOrderNo(orderNo));
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    private Long parseOrderNo(String val) {
        try {
            return Long.parseLong(val.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return -1L;
        }
    }

    @GetMapping("/sample")
    public ResponseEntity<Page<SampleOrderDto>> getSampleOrders(
            Pageable pageable,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "logisticsStatus", required = false) String logisticsStatus,
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "packageNo", required = false) String packageNo,
            @RequestParam(value = "trackingNo", required = false) String trackingNo,
            @RequestParam(value = "spu", required = false) String spu,
            @RequestParam(value = "timeType", required = false) String timeType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "influencerSocialSearch", required = false) String influencerSocialSearch,
            @RequestParam(value = "recipientName", required = false) String recipientName,
            @RequestParam(value = "recipientCountry", required = false) String recipientCountry,
            @RequestParam(value = "recipientCity", required = false) String recipientCity,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestParam(value = "ownerId", required = false) List<Long> ownerIds,
            @RequestParam(value = "contactPersonId", required = false) List<Long> contactPersonIds) {

        String effectivePackageNo = (packageNo != null && !packageNo.isEmpty()) ? packageNo : trackingNo;

        Specification<InfluencerSampleOrder> spec = createSampleSpec(orderNo, null, influencerName, logisticsStatus, tab,
                effectivePackageNo, spu, timeType, startTime, endTime, influencerSocialSearch, recipientName,
                recipientCountry, recipientCity,
                customerEmail, ownerIds, contactPersonIds);

        // 如果没有指定排序默认按下单时间倒序（最新的在前面）
        if (pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                            "orderCreatedAt"));
        }

        Page<InfluencerSampleOrder> entityPage = sampleOrderRepository.findAll(spec, pageable);

        // 批量获取当前页所有订单的商品明细
        List<Long> orderIds = entityPage.getContent().stream()
                .map(InfluencerSampleOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem>> itemsMap;
        if (!orderIds.isEmpty()) {
            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> allItems = sampleItemRepository
                    .findBySampleOrderIdIn(orderIds);
            itemsMap = allItems.stream()
                    .collect(Collectors.groupingBy(
                            com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem::getSampleOrderId));
        } else {
            itemsMap = new HashMap<>();
        }

        List<SampleOrderDto> dtos = entityPage.getContent().stream()
                .flatMap(entity -> splitToSampleDtos(entity,
                        itemsMap.getOrDefault(entity.getId(), java.util.Collections.emptyList())).stream())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new org.springframework.data.domain.PageImpl<>(dtos, pageable, entityPage.getTotalElements()));
    }

    @PostMapping("/sample/search")
    public ResponseEntity<Page<SampleOrderDto>> searchSampleOrders(
            Pageable pageable,
            @RequestBody OrderFilterDto filter) {
        String effectivePackageNo = (filter.getPackageNo() != null && !filter.getPackageNo().isEmpty()) ? filter.getPackageNo() : filter.getTrackingNo();
        Specification<InfluencerSampleOrder> spec = createSampleSpec(filter.getOrderNo(), filter.getEccangOrderId(), filter.getInfluencerName(), filter.getLogisticsStatus(), filter.getTab(),
                effectivePackageNo, filter.getSpu(), filter.getTimeType(), filter.getStartTime(), filter.getEndTime(), filter.getInfluencerSocialSearch(), filter.getRecipientName(),
                filter.getRecipientCountry(), filter.getRecipientCity(), filter.getCustomerEmail(), filter.getOwnerId(), filter.getContactPersonId());

        if (pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "orderCreatedAt"));
        }

        Page<InfluencerSampleOrder> entityPage = sampleOrderRepository.findAll(spec, pageable);
        List<Long> orderIds = entityPage.getContent().stream().map(InfluencerSampleOrder::getId).collect(Collectors.toList());

        Map<Long, List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem>> itemsMap;
        if (!orderIds.isEmpty()) {
            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> allItems = sampleItemRepository.findBySampleOrderIdIn(orderIds);
            itemsMap = allItems.stream().collect(Collectors.groupingBy(com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem::getSampleOrderId));
        } else {
            itemsMap = new HashMap<>();
        }

        List<SampleOrderDto> dtos = entityPage.getContent().stream()
                .flatMap(entity -> splitToSampleDtos(entity, itemsMap.getOrDefault(entity.getId(), java.util.Collections.emptyList())).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(dtos, pageable, entityPage.getTotalElements()));
    }

    @GetMapping("/sample/tab-counts")
    public ResponseEntity<Map<String, Long>> getSampleTabCounts(
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "logisticsStatus", required = false) String logisticsStatus,
            @RequestParam(value = "packageNo", required = false) String packageNo,
            @RequestParam(value = "trackingNo", required = false) String trackingNo,
            @RequestParam(value = "spu", required = false) String spu,
            @RequestParam(value = "timeType", required = false) String timeType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "influencerSocialSearch", required = false) String influencerSocialSearch,
            @RequestParam(value = "recipientName", required = false) String recipientName,
            @RequestParam(value = "recipientCountry", required = false) String recipientCountry,
            @RequestParam(value = "recipientCity", required = false) String recipientCity,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            @RequestParam(value = "ownerId", required = false) List<Long> ownerIds,
            @RequestParam(value = "contactPersonId", required = false) List<Long> contactPersonIds) {
        String[] tabs = { "draft", "pending", "pending_ship", "in_transit", "delivered", "exception", "cancelled" };
        Map<String, Long> counts = new HashMap<>();
        String effectivePackageNo = (packageNo != null && !packageNo.isEmpty()) ? packageNo : trackingNo;
        for (String tab : tabs) {
            Specification<InfluencerSampleOrder> spec = createSampleSpec(orderNo, null, influencerName, logisticsStatus, tab,
                    effectivePackageNo, spu, timeType, startTime, endTime, influencerSocialSearch, recipientName,
                    recipientCountry, recipientCity, customerEmail, ownerIds, contactPersonIds);
            counts.put(tab, sampleOrderRepository.count(spec));
        }
        return ResponseEntity.ok(counts);
    }

    @PostMapping("/sample/tab-counts/search")
    public ResponseEntity<Map<String, Long>> searchSampleTabCounts(@RequestBody OrderFilterDto filter) {
        String[] tabs = { "draft", "pending", "pending_ship", "in_transit", "delivered", "exception", "cancelled" };
        Map<String, Long> counts = new HashMap<>();
        String effectivePackageNo = (filter.getPackageNo() != null && !filter.getPackageNo().isEmpty()) ? filter.getPackageNo() : filter.getTrackingNo();
        for (String tab : tabs) {
            Specification<InfluencerSampleOrder> spec = createSampleSpec(filter.getOrderNo(), filter.getEccangOrderId(), filter.getInfluencerName(), filter.getLogisticsStatus(), tab,
                    effectivePackageNo, filter.getSpu(), filter.getTimeType(), filter.getStartTime(), filter.getEndTime(), filter.getInfluencerSocialSearch(), filter.getRecipientName(),
                    filter.getRecipientCountry(), filter.getRecipientCity(), filter.getCustomerEmail(), filter.getOwnerId(), filter.getContactPersonId());
            counts.put(tab, sampleOrderRepository.count(spec));
        }
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/sample/check-duplicate")
    public ResponseEntity<DuplicateCheckResult> checkDuplicateSampleOrder(
            @RequestParam(value = "influencerId") Long influencerId) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        List<InfluencerSampleOrder> recentOrders = sampleOrderRepository
                .findByInfluencerIdAndCreatedAtAfterOrderByCreatedAtDesc(influencerId, threeDaysAgo);

        if (recentOrders.isEmpty()) {
            return ResponseEntity.ok(DuplicateCheckResult.builder().hasDuplicate(false).build());
        }

        InfluencerSampleOrder lastOrder = recentOrders.get(0);
        return ResponseEntity.ok(DuplicateCheckResult.builder()
                .hasDuplicate(true)
                .lastOrderDate(lastOrder.getCreatedAt())
                .lastOrderName(lastOrder.getOrderName())
                .lastOrderId(lastOrder.getId())
                .influencerName(lastOrder.getInfluencerName())
                .build());
    }

    private LocalDateTime parseFlexibleDateTime(String value, boolean isEndTime) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String cleaned = value.trim().replace(" ", "T");
        try {
            // Handle ISO8601 with timezone suffix (e.g., 2026-02-04T09:00:00.000Z)
            if (cleaned.endsWith("Z") || cleaned.contains("+") || cleaned.matches(".*[+-]\\d{2}:\\d{2}$")) {
                java.time.OffsetDateTime odt = java.time.OffsetDateTime.parse(cleaned);
                return odt.withOffsetSameInstant(java.time.ZoneOffset.UTC).toLocalDateTime();
            }
            // Handle simple date formats
            if (cleaned.length() == 10) { // yyyy-MM-dd
                cleaned += isEndTime ? "T23:59:59" : "T00:00:00";
            } else if (cleaned.length() == 16) { // yyyy-MM-ddTHH:mm
                cleaned += ":00";
            }
            return LocalDateTime.parse(cleaned);
        } catch (Exception e) {
            log.warn("Invalid date format: {}", value);
            return null;
        }
    }

    private Specification<InfluencerSampleOrder> createSampleSpec(String orderNo, String eccangOrderIdFilter, String influencerName,
            String logisticsStatus, String tab,
            String packageNo, String spu, String timeType,
            String startTimeStr, String endTimeStr,
            String influencerSocialSearch,
            String recipientName, String recipientCountry, String recipientCity, String customerEmail,
            List<Long> ownerIds, List<Long> contactPersonIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDateTime startTime = parseFlexibleDateTime(startTimeStr, false);
            LocalDateTime endTime = parseFlexibleDateTime(endTimeStr, true);
            // 订单号搜索：仅匹配 orderName 和 eccangOrderNumber
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                String[] orderNos = orderNo.split("[\\n,]+");
                List<String> validOrderNos = Arrays.stream(orderNos)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validOrderNos.size() > 1) {
                    Predicate p1 = root.get("orderName").in(validOrderNos);
                    Predicate p2 = root.get("eccangOrderNumber").as(String.class).in(validOrderNos);
                    predicates.add(cb.or(p1, p2));
                } else if (validOrderNos.size() == 1) {
                    String singleNo = validOrderNos.get(0);
                    String pattern = "%" + singleNo + "%";
                    predicates.add(cb.or(
                            cb.like(root.get("orderName"), pattern),
                            cb.like(root.get("eccangOrderNumber").as(String.class), pattern)
                    ));
                }
            }

            // 交易号搜索：仅匹配 eccangOrderId
            if (eccangOrderIdFilter != null && !eccangOrderIdFilter.trim().isEmpty()) {
                String[] ids = eccangOrderIdFilter.split("[\\n,]+");
                List<String> validIds = Arrays.stream(ids)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                List<String> gidIds = validIds.stream()
                        .map(s -> s.matches("\\d{10,}") ? "gid://eccang/Order/" + s : s)
                        .collect(Collectors.toList());

                if (validIds.size() > 1) {
                    Predicate p1 = root.get("eccangOrderId").in(gidIds);
                    Predicate p2 = root.get("eccangOrderId").in(validIds);
                    predicates.add(cb.or(p1, p2));
                } else if (validIds.size() == 1) {
                    String singleId = validIds.get(0);
                    String pattern = "%" + singleId + "%";
                    String gidPattern = "%" + gidIds.get(0) + "%";
                    predicates.add(cb.or(
                            cb.like(root.get("eccangOrderId"), pattern),
                            cb.like(root.get("eccangOrderId"), gidPattern)
                    ));
                }
            }

            // Influencer Name Filter: Search both Order Table and Influencer Table via subquery
            if (influencerName != null && !influencerName.isEmpty()) {
                String[] names = influencerName.split("[\\n,]+");
                List<String> validNames = Arrays.stream(names)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (!validNames.isEmpty()) {
                    List<Predicate> mainNamePredicates = new ArrayList<>();
                    
                    // 1. Exact/Fuzzy match in Order Table (Snapshotted Name)
                    List<Predicate> orderTablePredicates = new ArrayList<>();
                    for (String name : validNames) {
                        orderTablePredicates.add(cb.like(cb.lower(root.get("influencerName")), "%" + name.toLowerCase() + "%"));
                    }
                    mainNamePredicates.add(cb.or(orderTablePredicates.toArray(new Predicate[0])));

                    // 2. Exact/Fuzzy match in Influencer Profile Table (Current Records)
                    jakarta.persistence.criteria.Subquery<Long> nameSub = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<com.athlunakms.eccang.influencer.entity.InfluencerReadOnly> nameInfRoot = 
                        nameSub.from(com.athlunakms.eccang.influencer.entity.InfluencerReadOnly.class);
                    nameSub.select(nameInfRoot.get("id"));
                    
                    List<Predicate> influencerTablePredicates = new ArrayList<>();
                    for (String name : validNames) {
                        String pattern = "%" + name.toLowerCase() + "%";
                        influencerTablePredicates.add(cb.or(
                            cb.like(cb.lower(nameInfRoot.get("nickName")), pattern),
                            cb.like(cb.lower(nameInfRoot.get("realName")), pattern),
                            cb.like(cb.lower(nameInfRoot.get("defaultHandle")), pattern)
                        ));
                    }
                    nameSub.where(cb.or(influencerTablePredicates.toArray(new Predicate[0])));
                    mainNamePredicates.add(root.get("influencerId").in(nameSub));

                    predicates.add(cb.or(mainNamePredicates.toArray(new Predicate[0])));
                }
            }

            // Batch Influencer Search
            if (influencerSocialSearch != null && !influencerSocialSearch.trim().isEmpty()) {
                List<Long> ids = resolveInfluencerIds(influencerSocialSearch);
                if (!ids.isEmpty()) {
                    predicates.add(root.get("influencerId").in(ids));
                } else {
                    // Search provided but no matches -> return nothing
                    predicates.add(cb.disjunction());
                }
            }

            // Package No (Tracking Number OR Fulfillment ID in Items)
            if (packageNo != null && !packageNo.trim().isEmpty()) {
                String[] pkgs = packageNo.split("[\\n,]+");
                List<String> validPkgs = Arrays.stream(pkgs)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validPkgs.size() > 1) {
                    // Batch Exact Match
                    Predicate trackingMatch = root.get("trackingNumber").in(validPkgs);
                    jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> itemRoot = itemSub
                            .from(com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem.class);
                    itemSub.select(itemRoot.get("sampleOrderId"))
                            .where(itemRoot.get("fulfillmentId").in(validPkgs));
                    predicates.add(cb.or(trackingMatch, root.get("id").in(itemSub)));
                } else if (validPkgs.size() == 1) {
                    // Fuzzy
                    String singlePkg = validPkgs.get(0);
                    String pkgPattern = "%" + singlePkg + "%";
                    Predicate trackingMatch = cb.like(root.get("trackingNumber"), pkgPattern);

                    // Subquery for Item Fulfillment ID
                    jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> itemRoot = itemSub
                            .from(com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem.class);
                    itemSub.select(itemRoot.get("sampleOrderId"))
                            .where(cb.like(itemRoot.get("fulfillmentId"), pkgPattern));

                    predicates.add(cb.or(trackingMatch, root.get("id").in(itemSub)));
                }
            }

            // SPU (Product ID or SKU in Items)
            if (spu != null && !spu.trim().isEmpty()) {
                String[] spus = spu.split("[\\n,]+");
                List<String> validSpus = Arrays.stream(spus)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> itemRoot = itemSub
                        .from(com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem.class);
                itemSub.select(itemRoot.get("sampleOrderId"));

                Predicate finalOrPredicate = cb.disjunction();
                // Normalize variantTitle by removing spaces for comparison
                jakarta.persistence.criteria.Expression<String> normalizedTitle = cb.function("REPLACE", String.class, 
                        itemRoot.get("variantTitle"), cb.literal(" "), cb.literal(""));

                for (String term : validSpus) {
                    // Try to match as direct ID first
                    Predicate idMatch = null;
                    try {
                        Long spuId = Long.parseLong(term);
                        idMatch = cb.or(
                            cb.equal(itemRoot.get("eccangProductId"), spuId),
                            cb.equal(itemRoot.get("eccangVariantId"), spuId)
                        );
                    } catch (Exception e) {}

                    // Multi-keyword match logic (split by '-')
                    String[] parts = term.split("-");
                    Predicate rowMatch = cb.conjunction();
                    for (String p : parts) {
                        String pTrim = p.trim();
                        if (!pTrim.isEmpty()) {
                            rowMatch = cb.and(rowMatch, cb.or(
                                cb.like(itemRoot.get("sku"), "%" + pTrim + "%"),
                                cb.like(normalizedTitle, "%" + pTrim + "%")
                            ));
                        }
                    }

                    if (idMatch != null) {
                        finalOrPredicate = cb.or(finalOrPredicate, cb.or(rowMatch, idMatch));
                    } else {
                        finalOrPredicate = cb.or(finalOrPredicate, rowMatch);
                    }
                }
                
                itemSub.where(finalOrPredicate);
                predicates.add(root.get("id").in(itemSub));
            }

            // Recipient Name Filter
            if (recipientName != null && !recipientName.trim().isEmpty()) {
                predicates.add(cb.like(root.get("recipientName"), "%" + recipientName.trim() + "%"));
            }

            // Recipient Country Filter (Support Multiple)
            if (recipientCountry != null && !recipientCountry.trim().isEmpty()) {
                String[] countries = recipientCountry.split("[\\n,]+");
                List<String> validCountries = Arrays.stream(countries)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                if (!validCountries.isEmpty()) {
                    predicates.add(root.get("recipientCountry").in(validCountries));
                }
            }

            // Recipient City Filter (LIKE match on recipientAddress)
            if (recipientCity != null && !recipientCity.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("recipientAddress")), "%" + recipientCity.trim().toLowerCase() + "%"));
            }

            // Customer Email Filter
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                String[] emails = customerEmail.split("[\\n,]+");
                List<String> validEmails = Arrays.stream(emails)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validEmails.size() > 1) {
                    List<Predicate> emailPredicates = new ArrayList<>();
                    for (String email : validEmails) {
                        emailPredicates
                                .add(cb.like(cb.lower(root.get("customerEmail")), "%" + email.toLowerCase() + "%"));
                    }
                    predicates.add(cb.or(emailPredicates.toArray(new Predicate[0])));
                } else if (validEmails.size() == 1) {
                    predicates.add(
                            cb.like(cb.lower(root.get("customerEmail")), "%" + validEmails.get(0).toLowerCase() + "%"));
                }
            }

            // Time Range
            if (timeType != null && startTime != null && endTime != null) {
                String field = "orderCreatedAt"; // default - 下单时间
                if ("processedAt".equalsIgnoreCase(timeType) || "paymentTime".equalsIgnoreCase(timeType)) {
                    field = "processedAt"; // 支付时间
                } else if ("fulfillmentCreatedAt".equalsIgnoreCase(timeType) || "shipTime".equalsIgnoreCase(timeType)) {
                    field = "fulfillmentCreatedAt"; // 发货时间
                } else if ("deliveredAt".equalsIgnoreCase(timeType) || "deliveryTime".equalsIgnoreCase(timeType)) {
                    field = "deliveredAt"; // 到货时间
                } else if ("createTime".equalsIgnoreCase(timeType) || "createdAt".equalsIgnoreCase(timeType)) {
                    field = "createdAt"; // system created at
                }

                predicates.add(cb.between(root.get(field), startTime, endTime));
            }

            // Owner and Contact Filtering (influencer joins)
            if ((ownerIds != null && !ownerIds.isEmpty()) || (contactPersonIds != null && !contactPersonIds.isEmpty())) {
                jakarta.persistence.criteria.Subquery<Long> infSub = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<com.athlunakms.eccang.influencer.entity.InfluencerReadOnly> infRoot = 
                    infSub.from(com.athlunakms.eccang.influencer.entity.InfluencerReadOnly.class);
                infSub.select(infRoot.get("id"));

                List<Predicate> infPredicates = new ArrayList<>();
                if (ownerIds != null && !ownerIds.isEmpty()) {
                    infPredicates.add(infRoot.get("ownerId").in(ownerIds));
                }
                if (contactPersonIds != null && !contactPersonIds.isEmpty()) {
                    // 同时搜索 contactPersonId 字段和 tags JSON 数组 (兼容逻辑)
                    List<Predicate> comboPredicates = new ArrayList<>();
                    
                    // 1. 匹配 contactPersonId 字段
                    comboPredicates.add(infRoot.get("contactPersonId").in(contactPersonIds));
                    
                    // 2. 匹配 tags 字符串 (容错性 LIKE 匹配)
                    for (Long tagId : contactPersonIds) {
                        String tagStr = String.valueOf(tagId);
                        comboPredicates.add(cb.or(
                            cb.like(infRoot.get("tags"), "%," + tagStr + ",%"),
                            cb.like(infRoot.get("tags"), "[" + tagStr + ",%"),
                            cb.like(infRoot.get("tags"), "%," + tagStr + "]"),
                            cb.like(infRoot.get("tags"), "[" + tagStr + "]"),
                            // 增加带空格的匹配逻辑
                            cb.like(infRoot.get("tags"), "%, " + tagStr + ",%"),
                            cb.like(infRoot.get("tags"), "[ " + tagStr + ",%"),
                            cb.like(infRoot.get("tags"), "%, " + tagStr + " ]"),
                            cb.like(infRoot.get("tags"), "[ " + tagStr + " ]")
                        ));
                    }
                    infPredicates.add(cb.or(comboPredicates.toArray(new Predicate[0])));
                }

                infSub.where(cb.and(infPredicates.toArray(new Predicate[0])));
                predicates.add(root.get("influencerId").in(infSub));
            }

            String statusFilter = logisticsStatus != null ? logisticsStatus : tab;
            if ("draft".equalsIgnoreCase(statusFilter) || "草稿单".equals(statusFilter)) {
                predicates.add(cb.equal(root.get("isDraft"), true));
            } else {
                // Not in draft tab -> mostly show non-drafts
                // Exception: "all" might show both? User said "Draft Order" is a separate tab.
                // If it's a separate tab, "all" usually means "all official orders".
                if (!"all".equalsIgnoreCase(statusFilter)) {
                    predicates.add(cb.or(cb.isNull(root.get("isDraft")), cb.equal(root.get("isDraft"), false)));
                }

                if (statusFilter != null && !statusFilter.isEmpty() && !"all".equals(statusFilter)) {
                    switch (statusFilter) {
                        case "pending_ship":
                        case "待发货":
                        case "待揽收":
                            predicates.add(cb.and(
                                    cb.isNull(root.get("cancelledAt")),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                    cb.isNull(root.get("inTransitAt")),
                                    cb.isNull(root.get("deliveredAt")),
                                    // closedAt could be non-null for archived unfulfilled? Assume if closed
                                    // provided it's considered handled?
                                    // User rule: closed -> delivered. So pending_ship should filter out closedAt.
                                    cb.isNull(root.get("closedAt")),
                                    cb.or(
                                            root.get("fulfillmentDisplayStatus").in("label_printed", "ready_for_pickup",
                                                    "label_purchased", "label_voided", "confirmed"),
                                            cb.and(
                                                    cb.lower(root.get("fulfillmentDisplayStatus")).in("fulfilled",
                                                            "marked_as_fulfilled", "success", "partial"),
                                                    cb.isNotNull(root.get("trackingNumber")),
                                                    cb.notEqual(root.get("trackingNumber"), "-"),
                                                    cb.notEqual(root.get("trackingNumber"), "")))));
                            break;
                        case "in_transit":
                        case "运输中":
                            predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                    cb.isNull(root.get("deliveredAt")), cb.isNull(root.get("closedAt")),
                                    cb.or(cb.isNotNull(root.get("inTransitAt")),
                                            cb.lower(root.get("fulfillmentDisplayStatus")).in("in_transit",
                                                    "out_for_delivery", "carrier_picked_up", "picked_up", "shipped"))));
                            break;
                        case "delivered":
                        case "已妥投":
                        case "已送达":
                        case "completed":
                        case "已完成":
                            // MERGED LOGIC: Delivered or Closed. No 30-day limit.
                            predicates.add(cb.and(
                                    cb.isNull(root.get("cancelledAt")),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                    cb.or(
                                            cb.isNotNull(root.get("deliveredAt")),
                                            cb.isNotNull(root.get("closedAt")),
                                            cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "delivered"))));
                            break;
                        case "exception":
                        case "异常":
                            predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                    cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                    cb.lower(root.get("fulfillmentDisplayStatus")).in("failure", "failed",
                                            "not_delivered",
                                            "exception", "attempted_delivery", "delayed")));
                            break;
                        case "cancelled":
                        case "已取消":
                            predicates.add(cb.or(cb.isNotNull(root.get("cancelledAt")),
                                    cb.equal(cb.lower(root.get("financialStatus")), "refunded"),
                                    cb.equal(cb.lower(root.get("financialStatus")), "voided"),
                                    cb.lower(root.get("fulfillmentDisplayStatus")).in("canceled", "cancelled",
                                            "label_voided", "restocked", "returned", "return_in_progress")));
                            break;
                        case "pending":
                        case "待处理":
                            predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                    cb.or(cb.isNull(root.get("financialStatus")),
                                            cb.and(cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                                    cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                                    cb.notEqual(cb.lower(root.get("financialStatus")),
                                                            "partially_refunded"))),
                                    cb.isNull(root.get("closedAt")),
                                    cb.or(
                                            cb.isNull(root.get("fulfillmentDisplayStatus")),
                                            cb.equal(root.get("fulfillmentDisplayStatus"), ""),
                                            cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "unfulfilled"),
                                            cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "pending"),
                                            cb.and(
                                                    cb.lower(root.get("fulfillmentDisplayStatus")).in("fulfilled",
                                                            "marked_as_fulfilled", "success"),
                                                    cb.or(cb.isNull(root.get("trackingNumber")),
                                                            cb.equal(root.get("trackingNumber"), "-"),
                                                            cb.equal(root.get("trackingNumber"), ""))))));
                            break;
                        default:
                            predicates.add(cb.equal(root.get("fulfillmentDisplayStatus"), statusFilter));
                    }
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<Long> resolveInfluencerIds(String input) {
        if (input == null || input.trim().isEmpty())
            return java.util.Collections.emptyList();
        try {
            // Split by newline or comma
            String[] parts = input.split("[\\n,]");
            List<String> keywords = new ArrayList<>();
            for (String part : parts) {
                if (!part.trim().isEmpty())
                    keywords.add(part.trim());
            }
            if (keywords.isEmpty())
                return java.util.Collections.emptyList();

            String url = influencerServiceUrl + "/v1/influencer/batch-resolve-ids";
            var response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(keywords), new ParameterizedTypeReference<List<Object>>() {
                    });
            List<Object> raw = response.getBody();
            if (raw != null) {
                // Safely cast to Long
                return raw.stream().map(obj -> {
                    if (obj instanceof Number)
                        return ((Number) obj).longValue();
                    return Long.parseLong(obj.toString());
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to resolve influencer IDs for input: {}", input, e);
        }
        return java.util.Collections.emptyList();
    }

    @PostMapping("/sync/{storeId}")
    public ResponseEntity<Map<String, Object>> syncOrders(
            @PathVariable(value = "storeId") Long storeId,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {
        orderService.syncOrdersFromAdminModal(storeId, parseFlexibleDateTime(startTime, false),
                parseFlexibleDateTime(endTime, true));
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sync started");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sync/progress")
    public ResponseEntity<SyncProgressDto> getSyncProgress(@RequestParam(value = "storeId") Long storeId) {
        SyncProgressDto progress = orderService.getSyncProgress(storeId);
        if (progress == null) {
            return ResponseEntity.ok(SyncProgressDto.builder().status("IDLE").build());
        }
        return ResponseEntity.ok(progress);
    }

    /**
     * 批量获取红人的样品单数量
     * 
     * @param influencerIds 红人ID列表
     * @return Map<influencerId, sampleOrderCount>
     */
    @GetMapping("/stats/sample-counts")
    public ResponseEntity<Map<Long, Integer>> getSampleOrderCounts(
            @RequestParam(value = "influencerIds") List<Long> influencerIds) {
        if (influencerIds == null || influencerIds.isEmpty()) {
            return ResponseEntity.ok(new HashMap<>());
        }

        List<Object[]> results = sampleOrderRepository.countByInfluencerIds(influencerIds);
        Map<Long, Integer> countMap = new HashMap<>();
        for (Object[] row : results) {
            Long influencerId = (Long) row[0];
            Long count = (Long) row[1];
            countMap.put(influencerId, count.intValue());
        }
        return ResponseEntity.ok(countMap);
    }

    /**
     * 批量获取红人的转化单数量
     * 
     * @param influencerIds 红人ID列表
     * @return Map<influencerId, conversionOrderCount>
     */
    @GetMapping("/stats/conversion-counts")
    public ResponseEntity<Map<Long, Integer>> getConversionOrderCounts(
            @RequestParam(value = "influencerIds") List<Long> influencerIds) {
        if (influencerIds == null || influencerIds.isEmpty()) {
            return ResponseEntity.ok(new HashMap<>());
        }

        List<Object[]> results = conversionOrderRepository.countByInfluencerIds(influencerIds);
        Map<Long, Integer> countMap = new HashMap<>();
        for (Object[] row : results) {
            Long influencerId = (Long) row[0];
            Long count = (Long) row[1];
            countMap.put(influencerId, count.intValue());
        }
        return ResponseEntity.ok(countMap);
    }

    /**
     * 绑定红人到样品订单
     * 会自动关联所有相关拆单/主单
     */
    @PostMapping("/sample/bind")
    public ResponseEntity<Map<String, Object>> bindSampleInfluencer(
            @RequestBody BindInfluencerRequest request,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        log.info("绑定红人请求: orderId={}, eccangOrderNumber={}, influencerId={}",
                request.getOrderId(), request.getEccangOrderNumber(), request.getInfluencerId());

        try {
            // 1. 根据订单号查找所有相关订单包括拆单
            Long orderNumber = request.getEccangOrderNumber();
            if (orderNumber == null && request.getOrderId() != null) {
                // 通过orderId获取订单号
                EccangOrder order = orderRepository.findById(request.getOrderId()).orElse(null);
                if (order != null) {
                    orderNumber = order.getEccangOrderNumber();
                }
            }

            if (orderNumber == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到订单"));
            }

            // 2. 获取红人名称优先使用前端传来的避免跨服务调用
            String influencerName = request.getInfluencerName();

            // 如果前端没有传名称才尝试跨服务获取兼容旧版本
            if (influencerName == null || influencerName.trim().isEmpty()) {
                log.info("前端未传递红人名称尝试跨服务获取");
                try {
                    String url = influencerServiceUrl + "/v1/influencer/" + request.getInfluencerId();
                    Map<String, Object> response = restTemplate
                            .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {
                            }).getBody();
                    if (response != null) {
                        influencerName = (String) response.getOrDefault("realName", response.get("name"));
                    }
                } catch (Exception ex) {
                    log.warn("获取红人信息失败(非致命): {}", ex.getMessage());
                    // 不阻塞流程使用默认名称
                    influencerName = "红人#" + request.getInfluencerId();
                }
            }

            // 确保名称不为空
            if (influencerName == null || influencerName.trim().isEmpty()) {
                influencerName = "红人#" + request.getInfluencerId();
            }

            // 3. 更新所有相关的EccangOrder
            List<EccangOrder> relatedOrders = orderRepository.findAllByEccangOrderNumber(orderNumber);
            if (relatedOrders.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "未找到对应的Eccang订单"));
            }

            for (EccangOrder order : relatedOrders) {
                // 如果是换绑记录旧的红人ID可选用于日志或清理逻辑
                Long oldInfluencerId = order.getInfluencerId();
                if (oldInfluencerId != null && !oldInfluencerId.equals(request.getInfluencerId())) {
                    log.info("订单 {} 从红人 {} 换绑到 {}", order.getEccangOrderNumber(), oldInfluencerId,
                            request.getInfluencerId());
                }

                order.setInfluencerId(request.getInfluencerId());
                order.setInfluencerName(influencerName);
                orderRepository.save(order);

                if (oldInfluencerId != null && !oldInfluencerId.equals(request.getInfluencerId())) {
                    logChange(request.getInfluencerId(), "转移接收样品单", "-",
                            order.getName() + " (" + order.getEccangOrderNumber() + ")",
                            operator != null ? operator : "SYS", "从红人ID:" + oldInfluencerId + " 转移");
                    logChange(oldInfluencerId, "转移送出样品单", order.getName() + " (" + order.getEccangOrderNumber() + ")",
                            "-", operator != null ? operator : "SYS", "转移至红人ID:" + request.getInfluencerId());
                } else {
                    logChange(request.getInfluencerId(), "手动关联样品单", "-",
                            order.getName() + " (" + order.getEccangOrderNumber() + ")",
                            operator != null ? operator : "SYS", "重新绑定样品订单");
                }
            }

            // 4. 更新或创建 InfluencerSampleOrder
            // 这一步至关重要如果之前订单没有绑定红人可能不存在 SampleOrder 记录
            // 必须确保记录存在否则红人详情页看不到这个订单
            List<InfluencerSampleOrder> relatedSampleOrders = sampleOrderRepository
                    .findByEccangOrderNumber(orderNumber);

            if (relatedSampleOrders.isEmpty()) {
                // 如果不存在根据 EccangOrder 创建新的记录
                for (EccangOrder order : relatedOrders) {
                    // 通常每个 EccangOrder 对应一个 SampleOrder (或者主单)
                    // 这里我们只为每个唯一的 eccangOrderId 创建一个记录避免重复
                    if (sampleOrderRepository.findByEccangOrderId(order.getEccangOrderId()).isEmpty()) {
                        InfluencerSampleOrder newSampleOrder = new InfluencerSampleOrder();
                        newSampleOrder.setEccangOrderId(order.getEccangOrderId());
                        newSampleOrder.setEccangOrderNumber(order.getEccangOrderNumber());
                        newSampleOrder.setOrderId(order.getId());
                        newSampleOrder.setOrderName(order.getName());
                        newSampleOrder.setInfluencerId(request.getInfluencerId());
                        newSampleOrder.setInfluencerName(influencerName);
                        newSampleOrder.setTotalPrice(order.getTotalPrice());
                        newSampleOrder.setCurrency(order.getCurrency());
                        newSampleOrder.setOrderCreatedAt(order.getCreatedAt());
                        newSampleOrder.setCreatedAt(java.time.LocalDateTime.now());
                        // 默认值
                        newSampleOrder.setSampleReason("Manual Binding");
                        newSampleOrder.setManualOverride(true); // 手动绑定标记为手动覆盖
                        newSampleOrder.setAutoMatched(false); // 不是自动匹配

                        InfluencerSampleOrder savedOrder = sampleOrderRepository.save(newSampleOrder);
                        logChange(request.getInfluencerId(), "新增绑定样品单", "-",
                                order.getName() + " (" + order.getEccangOrderNumber() + ")",
                                operator != null ? operator : "SYS", "系统创建样品单记录");

                        // populate items
                        List<com.athlunakms.eccang.order.entity.OrderLineItem> sourceItems = lineItemRepository
                                .findByOrderId(order.getId());
                        if (!sourceItems.isEmpty()) {
                            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> newItems = sourceItems
                                    .stream().map(src -> {
                                        com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem item = new com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem();
                                        item.setSampleOrderId(savedOrder.getId());
                                        item.setEccangLineItemId(src.getEccangLineItemId());
                                        item.setEccangProductId(src.getEccangProductId());
                                        item.setEccangVariantId(src.getEccangVariantId());
                                        item.setSku(src.getSku());
                                        item.setTitle(src.getTitle());
                                        item.setVariantTitle(src.getVariantTitle());
                                        item.setQuantity(src.getQuantity());
                                        item.setPrice(src.getPrice());
                                        item.setImageUrl(src.getImageUrl());
                                        item.setFulfillmentStatus(src.getFulfillmentStatus());
                                        return item;
                                    }).collect(Collectors.toList());
                            sampleItemRepository.saveAll(newItems);
                        }

                        relatedSampleOrders.add(savedOrder); // 添加到列表以便统计
                    }
                }
            } else {
                // 如果已存在更新关联
                for (InfluencerSampleOrder sampleOrder : relatedSampleOrders) {
                    sampleOrder.setInfluencerId(request.getInfluencerId());
                    sampleOrder.setInfluencerName(influencerName);
                    sampleOrder.setManualOverride(true); // 标记为手动覆盖防止自动匹配覆盖
                    sampleOrder.setAutoMatched(false); // 清除自动匹配标志
                    sampleOrderRepository.save(sampleOrder);
                }
            }

            log.info("绑定成功: 更新了 {} 个订单, {} 个样品单", relatedOrders.size(), relatedSampleOrders.size());

            // 注意活动时间戳更新已移除网络不通导致超时
            // 如需此功能建议通过数据库触发器或定时任务实现
            
            // 异步触发：自动流转状态为合作中
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    String url = influencerServiceUrl + "/v1/influencer/workflow/batch-status-change";
                    java.util.Map<String, Object> workflowReq = new java.util.HashMap<>();
                    workflowReq.put("ids", java.util.Collections.singletonList(request.getInfluencerId()));
                    workflowReq.put("targetStage", "ONBOARDED");
                    workflowReq.put("targetStatus", "COOPERATING");
                    workflowReq.put("reason", "绑定样品单自动流转");

                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    if (operator != null) {
                        headers.set("X-User-Name", operator);
                    }
                    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    org.springframework.http.HttpEntity<java.util.Map<String, Object>> entity = new org.springframework.http.HttpEntity<>(workflowReq, headers);
                    restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, entity, Void.class);
                    log.info("绑定样品单后，自动流转红人 {} 状态为合作中", request.getInfluencerId());
                } catch (Exception ex) {
                    log.error("绑定样品单后，自动流转红人状态失败: {}", ex.getMessage());
                }
            });

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "绑定成功",
                    "ordersUpdated", relatedOrders.size(),
                    "sampleOrdersUpdated", relatedSampleOrders.size()));

        } catch (Exception e) {
            log.error("绑定红人失败", e);
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 更新样品订单的合作价格（手动填写，非必填）
     */
    @PutMapping("/sample/{id}/cooperation-price")
    public ResponseEntity<Void> updateCooperationPrice(
            @PathVariable("id") Long id,
            @RequestParam(value = "price", required = false) java.math.BigDecimal price) {
        sampleOrderRepository.findById(id).ifPresentOrElse(order -> {
            order.setCooperationPrice(price);
            order.setUpdatedAt(LocalDateTime.now());
            sampleOrderRepository.save(order);
            log.info("Updated cooperation price for sample order {}: {}", id, price);
        }, () -> {
            log.warn("Sample order not found for cooperation price update: {}", id);
        });
        return ResponseEntity.ok().build();
    }

    /**
     * 获取红人的样品订单列表用于红人详情展示
     */
    @GetMapping("/sample/by-influencer/{influencerId}")
    public ResponseEntity<List<SampleOrderDto>> getSampleOrdersByInfluencer(
            @PathVariable(value = "influencerId") Long influencerId) {
        List<InfluencerSampleOrder> orders = sampleOrderRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);

        // 批量获取商品明细
        List<Long> orderIds = orders.stream()
                .map(InfluencerSampleOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem>> itemsMap;
        if (!orderIds.isEmpty()) {
            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> allItems = sampleItemRepository
                    .findBySampleOrderIdIn(orderIds);
            itemsMap = allItems.stream()
                    .collect(Collectors.groupingBy(
                            com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem::getSampleOrderId));
        } else {
            itemsMap = new HashMap<>();
        }

        List<SampleOrderDto> dtos = orders.stream()
                .flatMap(entity -> splitToSampleDtos(entity,
                        itemsMap.getOrDefault(entity.getId(), java.util.Collections.emptyList())).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * 获取转化订单列表
     */
    @GetMapping("/conversion")
    public ResponseEntity<Page<ConversionOrderDto>> getConversionOrders(
            @RequestParam(value = "influencerId", required = false) Long influencerId,
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "discountCode", required = false) String discountCode,
            @RequestParam(value = "commissionStatus", required = false) String commissionStatus,
            @RequestParam(value = "tab", required = false) String tab,
            @RequestParam(value = "packageNo", required = false) String packageNo,
            @RequestParam(value = "trackingNo", required = false) String trackingNo,
            @RequestParam(value = "spu", required = false) String spu,
            @RequestParam(value = "timeType", required = false) String timeType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "influencerSocialSearch", required = false) String influencerSocialSearch,
            @RequestParam(value = "recipientCountry", required = false) String recipientCountry,
            @RequestParam(value = "customerEmail", required = false) String customerEmail,
            Pageable pageable) {

        String effectivePackageNo = (packageNo != null && !packageNo.isEmpty()) ? packageNo : trackingNo;

        Specification<InfluencerConversionOrder> spec = createConversionSpec(
                influencerId, influencerName, orderNo, discountCode, commissionStatus,
                tab, effectivePackageNo, spu, timeType, startTime, endTime, influencerSocialSearch, recipientCountry,
                customerEmail);

        // 如果没有指定排序默认按下单时间倒序（最新的在前面）
        if (pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                            "orderCreatedAt"));
        }

        Page<InfluencerConversionOrder> entityPage = conversionOrderRepository.findAll(spec, pageable);

        // 批量获取当前页所有订单的商品明细
        List<Long> orderIds = entityPage.getContent().stream()
                .map(InfluencerConversionOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem>> itemsMap;
        if (!orderIds.isEmpty()) {
            List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> allItems = conversionItemRepository
                    .findByConversionOrderIdIn(orderIds);
            itemsMap = allItems.stream()
                    .collect(Collectors.groupingBy(
                            com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem::getConversionOrderId));
        } else {
            itemsMap = new HashMap<>(); // Empty map if no orders
        }

        List<ConversionOrderDto> dtos = entityPage.getContent().stream()
                .flatMap(entity -> splitToConversionDtos(entity,
                        itemsMap.getOrDefault(entity.getId(), java.util.Collections.emptyList())).stream())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new org.springframework.data.domain.PageImpl<>(dtos, pageable, entityPage.getTotalElements()));
    }

    @PostMapping("/conversion/search")
    public ResponseEntity<Page<ConversionOrderDto>> searchConversionOrders(
            @RequestBody OrderFilterDto filter,
            Pageable pageable) {

        String effectivePackageNo = (filter.getPackageNo() != null && !filter.getPackageNo().isEmpty()) ? filter.getPackageNo() : filter.getTrackingNo();

        Specification<InfluencerConversionOrder> spec = createConversionSpec(
                filter.getInfluencerId(), filter.getInfluencerName(), filter.getOrderNo(), filter.getDiscountCode(), filter.getCommissionStatus(),
                filter.getTab(), effectivePackageNo, filter.getSpu(), filter.getTimeType(), filter.getStartTime(), filter.getEndTime(), filter.getInfluencerSocialSearch(), filter.getRecipientCountry(),
                filter.getCustomerEmail());

        // 如果没有指定排序默认按下单时间倒序（最新的在前面）
        if (pageable.getSort().isUnsorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                            "orderCreatedAt"));
        }

        Page<InfluencerConversionOrder> entityPage = conversionOrderRepository.findAll(spec, pageable);

        List<Long> orderIds = entityPage.getContent().stream()
                .map(InfluencerConversionOrder::getId)
                .collect(Collectors.toList());

        Map<Long, List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem>> itemsMap;
        if (!orderIds.isEmpty()) {
            List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> allItems = conversionItemRepository
                    .findByConversionOrderIdIn(orderIds);
            itemsMap = allItems.stream()
                    .collect(Collectors.groupingBy(
                            com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem::getConversionOrderId));
        } else {
            itemsMap = new HashMap<>();
        }

        List<ConversionOrderDto> dtos = entityPage.getContent().stream()
                .flatMap(entity -> splitToConversionDtos(entity,
                        itemsMap.getOrDefault(entity.getId(), java.util.Collections.emptyList())).stream())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(dtos, pageable, entityPage.getTotalElements()));
    }

    @GetMapping("/conversion/tab-counts")
    public ResponseEntity<Map<String, Long>> getConversionTabCounts(
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "discountCode", required = false) String discountCode,
            @RequestParam(value = "commissionStatus", required = false) String commissionStatus,
            @RequestParam(value = "packageNo", required = false) String packageNo,
            @RequestParam(value = "trackingNo", required = false) String trackingNo,
            @RequestParam(value = "spu", required = false) String spu,
            @RequestParam(value = "timeType", required = false) String timeType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "influencerSocialSearch", required = false) String influencerSocialSearch,
            @RequestParam(value = "recipientCountry", required = false) String recipientCountry,
            @RequestParam(value = "customerEmail", required = false) String customerEmail) {
        String[] tabs = { "pending", "pending_ship", "in_transit", "delivered", "cancelled", "exception" };
        Map<String, Long> counts = new HashMap<>();
        String effectivePackageNo = (packageNo != null && !packageNo.isEmpty()) ? packageNo : trackingNo;
        for (String tab : tabs) {
            Specification<InfluencerConversionOrder> spec = createConversionSpec(null, influencerName, orderNo,
                    discountCode, commissionStatus, tab, effectivePackageNo, spu, timeType, startTime, endTime,
                    influencerSocialSearch, recipientCountry, customerEmail);
            counts.put(tab, conversionOrderRepository.count(spec));
        }
        return ResponseEntity.ok(counts);
    }

    @PostMapping("/conversion/tab-counts/search")
    public ResponseEntity<Map<String, Long>> searchConversionTabCounts(@RequestBody OrderFilterDto filter) {
        String[] tabs = { "pending", "pending_ship", "in_transit", "delivered", "cancelled", "exception" };
        Map<String, Long> counts = new HashMap<>();
        String effectivePackageNo = (filter.getPackageNo() != null && !filter.getPackageNo().isEmpty()) ? filter.getPackageNo() : filter.getTrackingNo();
        for (String tab : tabs) {
            Specification<InfluencerConversionOrder> spec = createConversionSpec(filter.getInfluencerId(), filter.getInfluencerName(), filter.getOrderNo(),
                    filter.getDiscountCode(), filter.getCommissionStatus(), tab, effectivePackageNo, filter.getSpu(), filter.getTimeType(), filter.getStartTime(), filter.getEndTime(),
                    filter.getInfluencerSocialSearch(), filter.getRecipientCountry(), filter.getCustomerEmail());
            counts.put(tab, conversionOrderRepository.count(spec));
        }
        return ResponseEntity.ok(counts);
    }

    private Specification<InfluencerConversionOrder> createConversionSpec(Long influencerId, String influencerName,
            String orderNo, String discountCode, String commissionStatus, String tab,
            String packageNo, String spu, String timeType,
            String startTimeStr, String endTimeStr,
            String influencerSocialSearch,
            String recipientCountry,
            String customerEmail) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            LocalDateTime startTime = parseFlexibleDateTime(startTimeStr, false);
            LocalDateTime endTime = parseFlexibleDateTime(endTimeStr, true);
            if (influencerId != null)
                predicates.add(cb.equal(root.get("influencerId"), influencerId));
            if (influencerName != null && !influencerName.isEmpty()) {
                String[] nameArr = influencerName.split("[\\n,]+");
                List<String> validNames = Arrays.stream(nameArr)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validNames.size() > 1) {
                    List<Predicate> namePredicates = new ArrayList<>();
                    for (String name : validNames) {
                        namePredicates
                                .add(cb.like(cb.lower(root.get("influencerName")), "%" + name.toLowerCase() + "%"));
                    }
                    predicates.add(cb.or(namePredicates.toArray(new Predicate[0])));
                } else if (validNames.size() == 1) {
                    predicates.add(
                            cb.like(cb.lower(root.get("influencerName")), "%" + validNames.get(0).toLowerCase() + "%"));
                }
            }
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                String[] orderNos = orderNo.split("[\\n,]+");
                List<String> validOrderNos = Arrays.stream(orderNos)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                List<String> eccangOrderIds = validOrderNos.stream()
                        .map(s -> s.matches("\\d{10,}") ? "gid://eccang/Order/" + s : s)
                        .collect(Collectors.toList());

                if (validOrderNos.size() > 1) {
                    Predicate p1 = root.get("orderName").in(validOrderNos);
                    Predicate p2 = root.get("eccangOrderNumber").as(String.class).in(validOrderNos);
                    Predicate p3 = root.get("eccangOrderId").in(eccangOrderIds);
                    Predicate p4 = root.get("eccangOrderId").in(validOrderNos);
                    predicates.add(cb.or(p1, p2, p3, p4));
                } else if (validOrderNos.size() == 1) {
                    String singleNo = validOrderNos.get(0);
                    String pattern = "%" + singleNo + "%";
                    String idPattern = "%" + eccangOrderIds.get(0) + "%";
                    predicates.add(cb.or(
                            cb.like(root.get("orderName"), pattern),
                            cb.like(root.get("eccangOrderNumber").as(String.class), pattern),
                            cb.like(root.get("eccangOrderId"), idPattern),
                            cb.like(root.get("eccangOrderId"), pattern)
                    ));
                }
            }
            if (discountCode != null && !discountCode.isEmpty()) {
                String[] codes = discountCode.split("[\\n,]+");
                List<String> validCodes = Arrays.stream(codes)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                if (validCodes.size() > 1) {
                    List<Predicate> codePredicates = new ArrayList<>();
                    for (String code : validCodes) {
                        codePredicates.add(cb.like(cb.lower(root.get("discountCode")), "%" + code.toLowerCase() + "%"));
                    }
                    predicates.add(cb.or(codePredicates.toArray(new Predicate[0])));
                } else if (!validCodes.isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("discountCode")), "%" + validCodes.get(0).toLowerCase() + "%"));
                }
            }
            if (commissionStatus != null && !commissionStatus.isEmpty()) {
                if ("unsettled".equalsIgnoreCase(commissionStatus)) {
                    predicates.add(root.get("commissionStatus").in("pending", "unsettled", "calculated"));
                } else if ("settled".equalsIgnoreCase(commissionStatus)) {
                    predicates.add(root.get("commissionStatus").in("paid", "settled"));
                } else {
                    predicates.add(cb.equal(root.get("commissionStatus"), commissionStatus));
                }
            }

            // Batch Influencer Search
            if (influencerSocialSearch != null && !influencerSocialSearch.trim().isEmpty()) {
                List<Long> ids = resolveInfluencerIds(influencerSocialSearch);
                if (!ids.isEmpty()) {
                    predicates.add(root.get("influencerId").in(ids));
                } else {
                    predicates.add(cb.disjunction());
                }
            }

            // Customer Email Search
            if (customerEmail != null && !customerEmail.trim().isEmpty()) {
                String[] emails = customerEmail.split("[\\n,]+");
                List<String> validEmails = Arrays.stream(emails)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validEmails.size() > 1) {
                    List<Predicate> emailPredicates = new ArrayList<>();
                    for (String email : validEmails) {
                        emailPredicates
                                .add(cb.like(cb.lower(root.get("customerEmail")), "%" + email.toLowerCase() + "%"));
                    }
                    predicates.add(cb.or(emailPredicates.toArray(new Predicate[0])));
                } else if (validEmails.size() == 1) {
                    predicates.add(
                            cb.like(cb.lower(root.get("customerEmail")), "%" + validEmails.get(0).toLowerCase() + "%"));
                }
            }

            // Package No
            if (packageNo != null && !packageNo.trim().isEmpty()) {
                String[] pkgs = packageNo.split("[\\n,]+");
                List<String> validPkgs = Arrays.stream(pkgs)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                if (validPkgs.size() > 1) {
                    Predicate trackingMatch = root.get("trackingNumber").in(validPkgs);
                    jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> itemRoot = itemSub
                            .from(com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem.class);
                    itemSub.select(itemRoot.get("conversionOrderId"))
                            .where(itemRoot.get("fulfillmentId").in(validPkgs));
                    predicates.add(cb.or(trackingMatch, root.get("id").in(itemSub)));
                } else if (validPkgs.size() == 1) {
                    String singlePkg = validPkgs.get(0);
                    String pkgPattern = "%" + singlePkg + "%";
                    Predicate trackingMatch = cb.like(root.get("trackingNumber"), pkgPattern);

                    jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> itemRoot = itemSub
                            .from(com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem.class);
                    itemSub.select(itemRoot.get("conversionOrderId"))
                            .where(cb.like(itemRoot.get("fulfillmentId"), pkgPattern));

                    predicates.add(cb.or(trackingMatch, root.get("id").in(itemSub)));
                }
            }

            // SPU
            if (spu != null && !spu.trim().isEmpty()) {
                String[] spus = spu.split("[\\n,]+");
                List<String> validSpus = Arrays.stream(spus)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                jakarta.persistence.criteria.Subquery<Long> itemSub = query.subquery(Long.class);
                jakarta.persistence.criteria.Root<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> itemRoot = itemSub
                        .from(com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem.class);
                itemSub.select(itemRoot.get("conversionOrderId"));

                Predicate finalOrPredicate = cb.disjunction();
                // Normalize variantTitle for space-insensitive matching
                jakarta.persistence.criteria.Expression<String> normalizedTitle = cb.function("REPLACE", String.class, 
                        itemRoot.get("variantTitle"), cb.literal(" "), cb.literal(""));

                for (String term : validSpus) {
                    // Try ID first
                    Predicate idMatch = null;
                    try {
                        Long spuId = Long.parseLong(term);
                        idMatch = cb.or(
                            cb.equal(itemRoot.get("eccangProductId"), spuId),
                            cb.equal(itemRoot.get("eccangVariantId"), spuId)
                        );
                    } catch (Exception e) {}

                    // Multi-keyword match (split by '-')
                    String[] parts = term.split("-");
                    Predicate rowMatch = cb.conjunction();
                    for (String p : parts) {
                        String pTrim = p.trim();
                        if (!pTrim.isEmpty()) {
                            rowMatch = cb.and(rowMatch, cb.or(
                                cb.like(itemRoot.get("sku"), "%" + pTrim + "%"),
                                cb.like(normalizedTitle, "%" + pTrim + "%")
                            ));
                        }
                    }

                    if (idMatch != null) {
                        finalOrPredicate = cb.or(finalOrPredicate, cb.or(rowMatch, idMatch));
                    } else {
                        finalOrPredicate = cb.or(finalOrPredicate, rowMatch);
                    }
                }
                
                itemSub.where(finalOrPredicate);
                predicates.add(root.get("id").in(itemSub));
            }

            // Time Range
            if (timeType != null && startTime != null && endTime != null) {
                String field = "orderCreatedAt";
                if ("processedAt".equalsIgnoreCase(timeType) || "paymentTime".equalsIgnoreCase(timeType))
                    field = "processedAt"; // Order payment time
                else if ("fulfillmentCreatedAt".equalsIgnoreCase(timeType) || "shipTime".equalsIgnoreCase(timeType))
                    field = "fulfillmentCreatedAt";
                else if ("deliveredAt".equalsIgnoreCase(timeType) || "deliveredTime".equalsIgnoreCase(timeType)
                        || "deliveryTime".equalsIgnoreCase(timeType))
                    field = "deliveredAt";
                else if ("createTime".equalsIgnoreCase(timeType) || "createdAt".equalsIgnoreCase(timeType))
                    field = "createdAt";
                else if ("completedTime".equalsIgnoreCase(timeType) || "closedAt".equalsIgnoreCase(timeType))
                    field = "closedAt";

                predicates.add(cb.between(root.get(field), startTime, endTime));
            }

            String statusFilter = tab;
            if (statusFilter != null && !statusFilter.isEmpty() && !"all".equals(statusFilter)) {
                switch (statusFilter) {
                    case "pending_ship":
                    case "待发货":
                    case "待揽收":
                        predicates.add(cb.and(
                                cb.isNull(root.get("cancelledAt")),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                cb.isNull(root.get("inTransitAt")),
                                cb.isNull(root.get("deliveredAt")),
                                // Also exclude cancelled/closed
                                cb.isNull(root.get("closedAt")),
                                cb.or(
                                        root.get("fulfillmentDisplayStatus").in("label_printed", "ready_for_pickup",
                                                "label_purchased", "label_voided", "confirmed"),
                                        cb.and(
                                                cb.lower(root.get("fulfillmentDisplayStatus")).in("fulfilled",
                                                        "marked_as_fulfilled", "success", "partial"),
                                                cb.isNotNull(root.get("trackingNumber")),
                                                cb.notEqual(root.get("trackingNumber"), "-"),
                                                cb.notEqual(root.get("trackingNumber"), "")))));
                        break;
                    case "in_transit":
                    case "运输中":
                        predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                cb.isNull(root.get("deliveredAt")),
                                cb.isNull(root.get("closedAt")),
                                cb.or(cb.isNotNull(root.get("inTransitAt")),
                                        cb.lower(root.get("fulfillmentDisplayStatus")).in("in_transit",
                                                "out_for_delivery", "carrier_picked_up", "picked_up", "shipped"))));
                        break;
                    case "delivered":
                    case "已妥投":
                    case "已送达":
                    case "completed":
                    case "已完成":
                        predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                cb.or(
                                        cb.isNotNull(root.get("deliveredAt")),
                                        cb.isNotNull(root.get("closedAt")),
                                        cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "delivered"))));
                        break;
                    case "cancelled":
                    case "已取消":
                        predicates.add(cb.or(cb.isNotNull(root.get("cancelledAt")),
                                cb.equal(cb.lower(root.get("financialStatus")), "refunded"),
                                cb.equal(cb.lower(root.get("financialStatus")), "voided"),
                                cb.lower(root.get("fulfillmentDisplayStatus")).in("canceled", "cancelled",
                                        "label_voided", "restocked", "returned", "return_in_progress")));
                        break;
                    case "exception":
                    case "异常":
                        predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                cb.lower(root.get("fulfillmentDisplayStatus")).in("failure", "failed", "not_delivered",
                                        "exception", "attempted_delivery", "delayed")));
                        break;
                    case "pending":
                    case "待处理":
                        predicates.add(cb.and(cb.isNull(root.get("cancelledAt")),
                                cb.or(cb.isNull(root.get("financialStatus")),
                                        cb.and(cb.notEqual(cb.lower(root.get("financialStatus")), "refunded"),
                                                cb.notEqual(cb.lower(root.get("financialStatus")), "voided"),
                                                cb.notEqual(cb.lower(root.get("financialStatus")),
                                                        "partially_refunded"))),
                                cb.isNull(root.get("closedAt")),
                                cb.or(
                                        cb.isNull(root.get("fulfillmentDisplayStatus")),
                                        cb.equal(root.get("fulfillmentDisplayStatus"), ""),
                                        cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "unfulfilled"),
                                        cb.equal(cb.lower(root.get("fulfillmentDisplayStatus")), "pending"),
                                        cb.and(
                                                cb.lower(root.get("fulfillmentDisplayStatus")).in("fulfilled",
                                                        "marked_as_fulfilled", "success"),
                                                cb.or(cb.isNull(root.get("trackingNumber")),
                                                        cb.equal(root.get("trackingNumber"), "-"),
                                                        cb.equal(root.get("trackingNumber"), ""))))));
                        break;
                    default:
                        predicates.add(cb.equal(root.get("fulfillmentDisplayStatus"), statusFilter));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 将一个样品单实体拆分为多个 DTO (根据 Fulfillment ID)
     * 如果订单没有包含 Fulfillment ID则返回单个 DTO
     * 如果包含多个 Fulfillment ID则按 ID 拆分未发货的商品归为一个 DTO
     */
    private List<SampleOrderDto> splitToSampleDtos(InfluencerSampleOrder entity,
            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> items) {
        // 商品明细已传入无需再次查询
        // 返回单个 DTO (解耦逻辑简化拆分)
        return java.util.Collections.singletonList(createBaseDto(entity, items));
    }

    private SampleOrderDto createBaseDto(InfluencerSampleOrder entity,
            List<com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem> items) {
        SampleOrderDto dto = new SampleOrderDto();
        dto.setId(entity.getId());
        dto.setOrderId(entity.getOrderId());
        dto.setEccangOrderId(entity.getEccangOrderId());
        dto.setEccangOrderNumber(entity.getEccangOrderNumber());
        dto.setOrderNo(entity.getOrderName());
        dto.setLongOrderNo(entity.getEccangOrderId());
        dto.setIsSplit(false);
        dto.setOrderName(entity.getOrderName());

        dto.setInfluencerId(entity.getInfluencerId());
        dto.setInfluencerName(entity.getInfluencerName());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setCooperationPrice(entity.getCooperationPrice());
        dto.setCurrency(entity.getCurrency());
        dto.setSampleReason(entity.getSampleReason());
        dto.setOrderCreatedAt(entity.getOrderCreatedAt());
        dto.setCreatedAt(entity.getCreatedAt());

        // Statuses
        dto.setFinancialStatus(entity.getFinancialStatus());
        dto.setFulfillmentStatus(entity.getFulfillmentStatus());
        dto.setFulfillmentDisplayStatus(entity.getFulfillmentDisplayStatus());

        // Logistics
        dto.setTrackingCompany(entity.getTrackingCompany());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setTrackingUrl(entity.getTrackingUrl());

        // Timestamps
        dto.setProcessedAtEccang(entity.getProcessedAt());
        dto.setCancelledAt(entity.getCancelledAt());
        dto.setClosedAt(entity.getClosedAt());
        dto.setFulfillmentCreatedAt(entity.getFulfillmentCreatedAt());
        dto.setInTransitAt(entity.getInTransitAt());
        dto.setDeliveredAt(entity.getDeliveredAt());
        dto.setEstimatedDeliveryAt(entity.getEstimatedDeliveryAt());

        // Customer
        dto.setCustomerName(entity.getRecipientName());
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setShippingAddress(entity.getRecipientAddress());
        dto.setShippingName(entity.getRecipientName());
        dto.setShippingPhone(entity.getRecipientPhone());

        // Recipient Info (新增字段映射)
        dto.setRecipientName(entity.getRecipientName());
        dto.setRecipientAddress(entity.getRecipientAddress());
        dto.setRecipientCountry(entity.getRecipientCountry());

        // Fill Owner and Contact from InfluencerReadOnly
        if (entity.getInfluencerId() != null && influencerReadOnlyRepository != null) {
            influencerReadOnlyRepository.findById(entity.getInfluencerId()).ifPresent(influencer -> {
                dto.setOwnerId(influencer.getOwnerId());
                dto.setContactPersonId(influencer.getContactPersonId());
                
                // 从红人tags JSON解析联络员名称通过 REST API 获取 LIAISON 标签
                String tagsJson = influencer.getTags();
                if (tagsJson != null && !tagsJson.isBlank()) {
                    try {
                        // 加载 LIAISON 标签映射从 system_tags 表
                        java.util.Map<Long, String> liaisonTagMap = new java.util.HashMap<>();
                        systemTagReadOnlyRepository.findByCategoryAndEnabledTrue("LIAISON").forEach(tag -> 
                            liaisonTagMap.put(tag.getId(), tag.getName())
                        );
                        
                        // 解析 tags JSON 并查找 LIAISON 标签
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        java.util.List<?> rawList = mapper.readValue(tagsJson, java.util.List.class);
                        for (Object obj : rawList) {
                            if (obj != null) {
                                Long tagId = (obj instanceof Number) ? ((Number) obj).longValue() : Long.parseLong(obj.toString());
                                String liaisonName = liaisonTagMap.get(tagId);
                                if (liaisonName != null) {
                                    dto.setContactPersonName(liaisonName);
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.warn("Failed to parse tags for liaison: {}", e.getMessage());
                    }
                }
            });
        }

        // Auto-match flag
        dto.setAutoMatched(entity.getAutoMatched());

        // Draft flag
        dto.setIsDraft(entity.getIsDraft());

        // Store Info - Fetch purely for StoreName
        orderRepository.findById(entity.getOrderId()).ifPresent(order -> {
            dto.setStoreId(order.getStoreId());
            if (order.getStoreId() != null) {
                storeRepository.findById(order.getStoreId()).ifPresent(store -> dto.setStoreName(store.getStoreName()));
            }
            if (dto.getCustomerName() == null || dto.getCustomerName().isEmpty()) {
                dto.setCustomerName(order.getShippingName());
                dto.setRecipientName(order.getShippingName());
                dto.setShippingName(order.getShippingName());
            }
            if (dto.getRecipientName() == null || dto.getRecipientName().isEmpty()) {
                dto.setRecipientName(order.getShippingName());
                dto.setShippingName(order.getShippingName());
            }
            if (dto.getCustomerEmail() == null || dto.getCustomerEmail().isEmpty()) {
                dto.setCustomerEmail(order.getCustomerEmail());
            }
            if (dto.getRecipientPhone() == null || dto.getRecipientPhone().isEmpty()) {
                dto.setRecipientPhone(order.getShippingPhone());
                dto.setShippingPhone(order.getShippingPhone());
            }
            if (dto.getRecipientCountry() == null || dto.getRecipientCountry().isEmpty()) {
                dto.setRecipientCountry(order.getShippingCountry());
                dto.setShippingCountry(order.getShippingCountry());
            }
            if (dto.getRecipientAddress() == null || dto.getRecipientAddress().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (order.getShippingAddress1() != null)
                    sb.append(order.getShippingAddress1()).append(" ");
                if (order.getShippingAddress2() != null)
                    sb.append(order.getShippingAddress2()).append(" ");
                if (order.getShippingCity() != null)
                    sb.append(order.getShippingCity()).append(", ");
                if (order.getShippingProvince() != null)
                    sb.append(order.getShippingProvince()).append(" ");
                if (order.getShippingZip() != null)
                    sb.append(order.getShippingZip()).append(", ");
                if (order.getShippingCountry() != null)
                    sb.append(order.getShippingCountry());
                String address = sb.toString().trim();
                dto.setRecipientAddress(address);
                dto.setShippingAddress(address);
            }
        });

        // Items mapping
        List<com.athlunakms.eccang.order.dto.OrderLineItemDto> productDtos = items.stream()
                .map(this::toSampleItemDto)
                .collect(Collectors.toList());
        dto.setProducts(productDtos);

        return dto;
    }

    private com.athlunakms.eccang.order.dto.OrderLineItemDto toSampleItemDto(
            com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem item) {
        com.athlunakms.eccang.order.dto.OrderLineItemDto dto = new com.athlunakms.eccang.order.dto.OrderLineItemDto();
        dto.setId(item.getId());
        dto.setEccangLineItemId(item.getEccangLineItemId());
        dto.setEccangProductId(item.getEccangProductId());
        dto.setEccangVariantId(item.getEccangVariantId());
        dto.setSku(item.getSku());
        dto.setTitle(item.getTitle());
        dto.setVariantTitle(item.getVariantTitle());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setFulfillmentStatus(item.getFulfillmentStatus());
        dto.setFulfillmentId(item.getFulfillmentId());

        // 图片逻辑如果订单项没有图片尝试从变体库反查 (ID -> SKU)
        String image = item.getImageUrl();
        if (image == null || image.isEmpty()) {
            if (item.getEccangVariantId() != null) {
                image = variantRepository.findByEccangVariantId(item.getEccangVariantId())
                        .map(com.athlunakms.eccang.product.entity.EccangProductVariant::getImageUrl)
                        .orElse(null);
            }
            if ((image == null || image.isEmpty()) && item.getSku() != null && !item.getSku().isEmpty()) {
                image = variantRepository.findFirstBySkuIgnoreCase(item.getSku())
                        .map(com.athlunakms.eccang.product.entity.EccangProductVariant::getImageUrl)
                        .orElse(null);
            }
        }
        dto.setImageUrl(image);

        dto.setReturnedQuantity(item.getReturnedQuantity());
        return dto;
    }

    private List<ConversionOrderDto> splitToConversionDtos(InfluencerConversionOrder entity,
            List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> items) {
        // Items are now passed in, no need to fetch
        return java.util.Collections.singletonList(createConversionBaseDto(entity, items));
    }

    private ConversionOrderDto createConversionBaseDto(InfluencerConversionOrder entity,
            List<com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem> items) {
        ConversionOrderDto dto = new ConversionOrderDto();
        dto.setId(entity.getId());
        dto.setOrderId(entity.getOrderId());
        dto.setEccangOrderId(entity.getEccangOrderId());
        dto.setEccangOrderNumber(entity.getEccangOrderNumber());
        dto.setOrderNo(entity.getOrderName());
        dto.setLongOrderNo(entity.getEccangOrderId());
        dto.setIsSplit(false);
        dto.setOrderName(entity.getOrderName());

        dto.setInfluencerId(entity.getInfluencerId());
        dto.setInfluencerName(entity.getInfluencerName());
        dto.setDiscountCode(entity.getDiscountCode());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setTotalShipping(entity.getTotalShipping());
        dto.setTotalRefund(entity.getTotalRefund());
        dto.setCommissionableAmount(entity.getCommissionableAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setCommissionRate(entity.getCommissionRate());
        if (entity.getCommissionAmount() != null) {
            dto.setCommissionAmount(entity.getCommissionAmount());
        } else if (entity.getCommissionableAmount() != null && entity.getCommissionRate() != null) {
            dto.setCommissionAmount(entity.getCommissionableAmount().multiply(entity.getCommissionRate())
                    .divide(new java.math.BigDecimal(100), 2, java.math.RoundingMode.HALF_UP));
        }

        dto.setCommissionStatus(entity.getCommissionStatus());
        dto.setFinancialStatus(entity.getFinancialStatus());

        // Logistics
        dto.setTrackingCompany(entity.getTrackingCompany());
        dto.setTrackingNumber(entity.getTrackingNumber());
        dto.setTrackingUrl(entity.getTrackingUrl());
        dto.setFulfillmentStatus(entity.getFulfillmentStatus());
        dto.setFulfillmentDisplayStatus(entity.getFulfillmentDisplayStatus());
        dto.setTrackingEventsJson(entity.getTrackingEventsJson());

        // Timestamps
        dto.setOrderCreatedAt(entity.getOrderCreatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setProcessedAtEccang(entity.getProcessedAt());
        dto.setCancelledAt(entity.getCancelledAt());
        dto.setClosedAt(entity.getClosedAt());
        dto.setFulfillmentCreatedAt(entity.getFulfillmentCreatedAt());
        dto.setInTransitAt(entity.getInTransitAt());
        dto.setDeliveredAt(entity.getDeliveredAt());
        dto.setEstimatedDeliveryAt(entity.getEstimatedDeliveryAt());

        // Customer
        // Recipient Info
        dto.setRecipientName(entity.getRecipientName());
        dto.setRecipientAddress(entity.getRecipientAddress());
        dto.setRecipientCountry(entity.getRecipientCountry());
        dto.setRecipientPhone(entity.getRecipientPhone());

        // Customer & Address consistency
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setShippingAddress(entity.getRecipientAddress());
        dto.setShippingName(entity.getRecipientName());
        dto.setShippingPhone(entity.getRecipientPhone());
        dto.setShippingCountry(entity.getRecipientCountry());

        // Store & Backup Info
        orderRepository.findById(entity.getOrderId()).ifPresent(order -> {
            dto.setStoreId(order.getStoreId());
            if (order.getStoreId() != null) {
                storeRepository.findById(order.getStoreId()).ifPresent(store -> dto.setStoreName(store.getStoreName()));
            }
            if (dto.getRecipientName() == null || dto.getRecipientName().isEmpty()) {
                dto.setRecipientName(order.getShippingName());
                dto.setShippingName(order.getShippingName());
            }
            if (dto.getCustomerName() == null || dto.getCustomerName().isEmpty()) {
                dto.setCustomerName(order.getShippingName());
            }
            if (dto.getCustomerEmail() == null || dto.getCustomerEmail().isEmpty()) {
                dto.setCustomerEmail(order.getCustomerEmail());
            }
            if (dto.getRecipientPhone() == null || dto.getRecipientPhone().isEmpty()) {
                dto.setRecipientPhone(order.getShippingPhone());
                dto.setShippingPhone(order.getShippingPhone());
            }
            if (dto.getRecipientCountry() == null || dto.getRecipientCountry().isEmpty()) {
                dto.setRecipientCountry(order.getShippingCountry());
                dto.setShippingCountry(order.getShippingCountry());
            }
            if (dto.getRecipientAddress() == null || dto.getRecipientAddress().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (order.getShippingAddress1() != null)
                    sb.append(order.getShippingAddress1()).append(" ");
                if (order.getShippingAddress2() != null)
                    sb.append(order.getShippingAddress2()).append(" ");
                if (order.getShippingCity() != null)
                    sb.append(order.getShippingCity()).append(", ");
                if (order.getShippingProvince() != null)
                    sb.append(order.getShippingProvince()).append(" ");
                if (order.getShippingZip() != null)
                    sb.append(order.getShippingZip()).append(", ");
                if (order.getShippingCountry() != null)
                    sb.append(order.getShippingCountry());
                String address = sb.toString().trim();
                dto.setRecipientAddress(address);
                dto.setShippingAddress(address);
            }
            if (dto.getTotalPrice() == null || dto.getTotalPrice().compareTo(java.math.BigDecimal.ZERO) == 0) {
                dto.setTotalPrice(order.getTotalPrice());
            }
        });

        // Items
        List<com.athlunakms.eccang.order.dto.OrderLineItemDto> productDtos = items.stream()
                .map(this::toConversionItemDto)
                .collect(Collectors.toList());
        dto.setProducts(productDtos);

        return dto;
    }

    private com.athlunakms.eccang.order.dto.OrderLineItemDto toConversionItemDto(
            com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem item) {
        com.athlunakms.eccang.order.dto.OrderLineItemDto dto = new com.athlunakms.eccang.order.dto.OrderLineItemDto();
        dto.setId(item.getId());
        dto.setEccangProductId(item.getEccangProductId());
        dto.setEccangVariantId(item.getEccangVariantId());
        dto.setSku(item.getSku());
        dto.setTitle(item.getTitle());
        dto.setVariantTitle(item.getVariantTitle());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setFulfillmentStatus(item.getFulfillmentStatus());
        dto.setFulfillmentId(item.getFulfillmentId());

        // 图片逻辑如果订单项没有图片尝试从变体库反查 (ID -> SKU)
        String image = item.getImageUrl();
        if (image == null || image.isEmpty()) {
            if (item.getEccangVariantId() != null) {
                image = variantRepository.findByEccangVariantId(item.getEccangVariantId())
                        .map(com.athlunakms.eccang.product.entity.EccangProductVariant::getImageUrl)
                        .orElse(null);
            }
            if ((image == null || image.isEmpty()) && item.getSku() != null && !item.getSku().isEmpty()) {
                image = variantRepository.findFirstBySkuIgnoreCase(item.getSku())
                        .map(com.athlunakms.eccang.product.entity.EccangProductVariant::getImageUrl)
                        .orElse(null);
            }
        }
        dto.setImageUrl(image);

        dto.setReturnedQuantity(item.getReturnedQuantity());
        return dto;
    }

}
