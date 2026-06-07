package com.athlunakms.eccang.order.controller;

import com.athlunakms.eccang.order.dto.OrderDetailLogDto;
import com.athlunakms.eccang.order.dto.OrderDetailResponseDto;
import com.athlunakms.eccang.order.dto.OrderPaymentDto;
import com.athlunakms.eccang.order.dto.OrderRefundDto;
import com.athlunakms.eccang.order.entity.InfluencerConversionOrder;
import com.athlunakms.eccang.order.entity.InfluencerSampleOrder;
import com.athlunakms.eccang.order.entity.OrderRefund;
import com.athlunakms.eccang.order.entity.OrderTransaction;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.repository.InfluencerConversionOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerSampleOrderRepository;
import com.athlunakms.eccang.order.repository.OrderLineItemRepository;
import com.athlunakms.eccang.order.repository.OrderRefundRepository;
import com.athlunakms.eccang.order.repository.OrderTransactionRepository;
import com.athlunakms.eccang.order.repository.EccangOrderRepository;
import com.athlunakms.eccang.product.entity.EccangProductVariant;
import com.athlunakms.eccang.product.repository.EccangProductVariantRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/orders"})
public class OrderDetailController {
    private static final Logger log = LoggerFactory.getLogger(OrderDetailController.class);
    private final EccangOrderRepository orderRepository;
    private final OrderLineItemRepository lineItemRepository;
    private final OrderTransactionRepository transactionRepository;
    private final OrderRefundRepository refundRepository;
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final EccangProductVariantRepository variantRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value={"/{id}/details"})
    public ResponseEntity<OrderDetailResponseDto> getOrderDetails(@PathVariable(value="id") Long id, @RequestParam(value="orderType", defaultValue="sample") String orderType) {
        log.info("\u83b7\u53d6\u8ba2\u5355\u8be6\u60c5: id={}, orderType={}", (Object)id, (Object)orderType);
        EccangOrder order = null;
        Object businessEntity = null;
        if ("conversion".equals(orderType)) {
            InfluencerConversionOrder co = this.conversionOrderRepository.findById(id).orElse(null);
            if (co != null) {
                businessEntity = co;
                if (co.getOrderId() != null) {
                    order = this.orderRepository.findById(co.getOrderId()).orElse(null);
                }
                if (order == null && co.getEccangOrderId() != null) {
                    order = this.orderRepository.findByEccangOrderId(this.cleanEccangId(co.getEccangOrderId())).orElse(null);
                }
            }
        } else {
            InfluencerSampleOrder so = this.sampleOrderRepository.findById(id).orElse(null);
            if (so != null) {
                businessEntity = so;
                if (so.getOrderId() != null) {
                    order = this.orderRepository.findById(so.getOrderId()).orElse(null);
                }
                if (order == null && so.getEccangOrderId() != null) {
                    order = this.orderRepository.findByEccangOrderId(this.cleanEccangId(so.getEccangOrderId())).orElse(null);
                }
            }
        }
        if (order == null) {
            log.warn("\u627e\u4e0d\u5230\u5173\u8054\u7684 Eccang \u8ba2\u5355: id={}, orderType={}", (Object)id, (Object)orderType);
            return ResponseEntity.notFound().build();
        }
        OrderDetailResponseDto dto = new OrderDetailResponseDto();
        dto.setId(order.getId());
        dto.setOrderNo(order.getName());
        dto.setOrderName(order.getName());
        dto.setEccangOrderId(order.getEccangOrderId());
        dto.setTotalAmount(order.getTotalPrice());
        dto.setTotalShipping(order.getTotalShippingPrice());
        dto.setFinancialStatus(order.getFinancialStatus());
        dto.setFulfillmentStatus(order.getFulfillmentStatus());
        dto.setStoreName(order.getStoreName());
        dto.setShippingName(order.getShippingName());
        dto.setShippingPhone(order.getShippingPhone());
        dto.setShippingAddress(this.buildFullAddress(order));
        dto.setCustomerName(this.buildCustomerName(order));
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setShippingCountry(order.getShippingCountry());
        dto.setTrackingNumber(order.getTrackingNumbers());
        dto.setTrackingCompany(this.parseTrackingCompany(order));
        dto.setTrackingUrl(this.parseTrackingUrl(order));
        if (businessEntity instanceof InfluencerConversionOrder) {
            InfluencerConversionOrder co = (InfluencerConversionOrder) businessEntity;
            if (this.isEffectivelyEmpty(dto.getShippingName())) {
                dto.setShippingName(co.getRecipientName());
            }
            if (this.isEffectivelyEmpty(dto.getShippingPhone())) {
                dto.setShippingPhone(co.getRecipientPhone());
            }
            if (this.isEffectivelyEmpty(dto.getShippingAddress())) {
                dto.setShippingAddress(co.getRecipientAddress());
            }
            if (this.isEffectivelyEmpty(dto.getCustomerEmail())) {
                dto.setCustomerEmail(co.getCustomerEmail());
            }
            if (this.isEffectivelyEmpty(dto.getShippingCountry())) {
                dto.setShippingCountry(co.getRecipientCountry());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingNumber())) {
                dto.setTrackingNumber(co.getTrackingNumber());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingCompany())) {
                dto.setTrackingCompany(co.getTrackingCompany());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingUrl())) {
                dto.setTrackingUrl(co.getTrackingUrl());
            }
        } else if (businessEntity instanceof InfluencerSampleOrder) {
            InfluencerSampleOrder so = (InfluencerSampleOrder)businessEntity;
            if (this.isEffectivelyEmpty(dto.getShippingName())) {
                dto.setShippingName(so.getRecipientName());
            }
            if (this.isEffectivelyEmpty(dto.getShippingPhone())) {
                dto.setShippingPhone(so.getRecipientPhone());
            }
            if (this.isEffectivelyEmpty(dto.getShippingAddress())) {
                dto.setShippingAddress(so.getRecipientAddress());
            }
            if (this.isEffectivelyEmpty(dto.getCustomerEmail())) {
                dto.setCustomerEmail(so.getCustomerEmail());
            }
            if (this.isEffectivelyEmpty(dto.getShippingCountry())) {
                dto.setShippingCountry(so.getRecipientCountry());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingNumber())) {
                dto.setTrackingNumber(so.getTrackingNumber());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingCompany())) {
                dto.setTrackingCompany(so.getTrackingCompany());
            }
            if (this.isEffectivelyEmpty(dto.getTrackingUrl())) {
                dto.setTrackingUrl(so.getTrackingUrl());
            }
        }
        dto.setCreatedAt(order.getCreatedAtEccang() != null ? order.getCreatedAtEccang() : order.getCreatedAt());
        dto.setPaidAt(this.resolvePaidAt(order));
        dto.setShippedAt(order.getFulfillmentCreatedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        dto.setCancelledAt(order.getCancelledAtEccang());
        dto.setUpdatedAt(order.getUpdatedAtEccang() != null ? order.getUpdatedAtEccang() : order.getUpdatedAt());
        dto.setProducts(this.buildProductList(order.getId()));
        dto.setPayments(this.buildPaymentList(order.getId()));
        dto.setRefunds(this.buildRefundList(order.getId()));
        dto.setDiscount(this.buildDiscountInfo(order));
        dto.setLogs(this.buildLogList(order));
        return ResponseEntity.ok(dto);
    }

    private String cleanEccangId(String rawId) {
        if (rawId == null) {
            return null;
        }
        if (rawId.contains("/")) {
            String[] parts = rawId.split("/");
            return parts[parts.length - 1];
        }
        return rawId;
    }

    private boolean isEffectivelyEmpty(String val) {
        return val == null || val.trim().isEmpty() || "-".equals(val.trim()) || "null".equalsIgnoreCase(val.trim());
    }

    private String buildFullAddress(EccangOrder order) {
        StringBuilder sb = new StringBuilder();
        if (order.getShippingAddress1() != null) {
            sb.append(order.getShippingAddress1());
        }
        if (order.getShippingAddress2() != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(order.getShippingAddress2());
        }
        if (order.getShippingCity() != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(order.getShippingCity());
        }
        if (order.getShippingProvince() != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(order.getShippingProvince());
        }
        if (order.getShippingCountry() != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(order.getShippingCountry());
        }
        if (order.getShippingZip() != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(order.getShippingZip());
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    private String buildCustomerName(EccangOrder order) {
        String first = order.getCustomerFirstName();
        String last = order.getCustomerLastName();
        if (first != null && last != null) {
            return first + " " + last;
        }
        if (first != null) {
            return first;
        }
        if (last != null) {
            return last;
        }
        return null;
    }

    private String parseTrackingCompany(EccangOrder order) {
        if (order.getFulfillmentsJson() == null) {
            return null;
        }
        try {
            JsonNode first;
            JsonNode arr = objectMapper.readTree(order.getFulfillmentsJson());
            if (arr.isArray() && arr.size() > 0 && (first = arr.get(0)).has("tracking_company")) {
                return first.get("tracking_company").asText(null);
            }
        }
        catch (Exception e) {
            log.debug("\u89e3\u6790\u7269\u6d41\u516c\u53f8\u5931\u8d25: {}", (Object)e.getMessage());
        }
        return null;
    }

    private String parseTrackingUrl(EccangOrder order) {
        if (order.getFulfillmentsJson() == null) {
            return null;
        }
        try {
            JsonNode arr = objectMapper.readTree(order.getFulfillmentsJson());
            if (arr.isArray() && arr.size() > 0) {
                JsonNode first = arr.get(0);
                if (first.has("tracking_url")) {
                    return first.get("tracking_url").asText(null);
                }
                if (first.has("tracking_urls") && first.get("tracking_urls").isArray() && first.get("tracking_urls").size() > 0) {
                    return first.get("tracking_urls").get(0).asText(null);
                }
            }
        }
        catch (Exception e) {
            log.debug("\u89e3\u6790\u7269\u6d41\u94fe\u63a5\u5931\u8d25: {}", (Object)e.getMessage());
        }
        return null;
    }

    private LocalDateTime resolvePaidAt(EccangOrder order) {
        if ("paid".equals(order.getFinancialStatus()) || "partially_paid".equals(order.getFinancialStatus())) {
            List<OrderTransaction> transactions = this.transactionRepository.findByOrderId(order.getId());
            Optional<LocalDateTime> paidTime = transactions.stream().filter(t -> ("sale".equals(t.getKind()) || "capture".equals(t.getKind())) && "success".equals(t.getStatus())).map(OrderTransaction::getCreatedAtEccang).filter(Objects::nonNull).min(LocalDateTime::compareTo);
            if (paidTime.isPresent()) {
                return paidTime.get();
            }
            if (order.getProcessedAtEccang() != null) {
                return order.getProcessedAtEccang();
            }
        }
        return null;
    }

    private List<OrderDetailResponseDto.ProductInfo> buildProductList(Long orderId) {
        List<com.athlunakms.eccang.order.entity.OrderLineItem> items = this.lineItemRepository.findByOrderId(orderId);
        return items.stream().map(item -> {
            OrderDetailResponseDto.ProductInfo pi = new OrderDetailResponseDto.ProductInfo();
            pi.setId(item.getId());
            pi.setSku(item.getSku());
            pi.setSpu(item.getSku() != null ? item.getSku().split("-")[0] : null);
            pi.setVariantTitle(item.getVariantTitle());
            pi.setName(item.getTitle());
            pi.setEccangVariantId(item.getEccangVariantId());
            pi.setEccangProductId(item.getEccangProductId());
            String image = item.getImageUrl();
            if (image == null || image.isEmpty()) {
                if (item.getEccangVariantId() != null) {
                    image = this.variantRepository.findById(item.getEccangVariantId()).map(EccangProductVariant::getImageUrl).orElse(null);
                }
                if ((image == null || image.isEmpty()) && item.getSku() != null && !item.getSku().isEmpty()) {
                    image = this.variantRepository.findFirstBySkuIgnoreCase(item.getSku()).map(EccangProductVariant::getImageUrl).orElse(null);
                }
            }
            pi.setImage(image);
            pi.setQuantity(item.getQuantity());
            pi.setPrice(item.getPrice());
            if (Boolean.TRUE.equals(item.getIsReturned())) {
                pi.setStatus("\u5df2\u9000\u8d27");
            } else if (item.getReturnedQuantity() != null && item.getReturnedQuantity() > 0) {
                pi.setStatus("\u90e8\u5206\u9000\u8d27");
            } else if ("fulfilled".equals(item.getFulfillmentStatus())) {
                pi.setStatus("\u5df2\u53d1\u8d27");
            } else {
                pi.setStatus("\u5f85\u53d1\u8d27");
            }
            return pi;
        }).collect(Collectors.toList());
    }

    private List<OrderPaymentDto> buildPaymentList(Long orderId) {
        List<OrderTransaction> transactions = this.transactionRepository.findByOrderIdOrderByCreatedAtEccangDesc(orderId);
        return transactions.stream().filter(t -> ("sale".equals(t.getKind()) || "capture".equals(t.getKind())) && "success".equals(t.getStatus())).map(t -> {
            OrderPaymentDto dto = new OrderPaymentDto();
            dto.setId(String.valueOf(t.getId()));
            dto.setPaymentNo("PAY-" + t.getEccangTransactionId());
            dto.setTime(t.getCreatedAtEccang() != null ? t.getCreatedAtEccang().format(FMT) : null);
            dto.setAmount(t.getAmount() != null ? t.getAmount().toPlainString() : "0.00");
            dto.setMethod(t.getGateway() != null ? t.getGateway() : "-");
            dto.setTransactionId(t.getEccangTransactionId());
            dto.setStatus("success");
            dto.setOperator("\u7cfb\u7edf");
            return dto;
        }).collect(Collectors.toList());
    }

    private List<OrderRefundDto> buildRefundList(Long orderId) {
        List<OrderRefund> refunds = this.refundRepository.findByOrderIdOrderByCreatedAtEccangDesc(orderId);
        return refunds.stream().map(r -> {
            OrderRefundDto dto = new OrderRefundDto();
            dto.setId(String.valueOf(r.getId()));
            dto.setRefundNo("RF-" + r.getEccangRefundId());
            dto.setTime(r.getCreatedAtEccang() != null ? r.getCreatedAtEccang().format(FMT) : null);
            dto.setAmount(r.getTotalRefunded() != null ? r.getTotalRefunded().toPlainString() : "0.00");
            dto.setReason(r.getNote() != null ? r.getNote() : "-");
            dto.setMethod("\u539f\u8def\u9000\u56de");
            dto.setStatus("success");
            dto.setOperator("\u7cfb\u7edf");
            return dto;
        }).collect(Collectors.toList());
    }

    private OrderDetailResponseDto.DiscountInfo buildDiscountInfo(EccangOrder order) {
        OrderDetailResponseDto.DiscountInfo info = new OrderDetailResponseDto.DiscountInfo();
        BigDecimal codeDiscount = BigDecimal.ZERO;
        BigDecimal voucherDiscount = BigDecimal.ZERO;
        if (order.getDiscountCodesJson() != null) {
            try {
                JsonNode arr = objectMapper.readTree(order.getDiscountCodesJson());
                if (arr.isArray()) {
                    for (JsonNode node : arr) {
                        BigDecimal amount;
                        String type = node.has("type") ? node.get("type").asText("") : "";
                        BigDecimal bigDecimal = amount = node.has("amount") ? new BigDecimal(node.get("amount").asText("0")) : BigDecimal.ZERO;
                        if ("percentage".equals(type) || "fixed_amount".equals(type)) {
                            codeDiscount = codeDiscount.add(amount);
                            continue;
                        }
                        voucherDiscount = voucherDiscount.add(amount);
                    }
                }
            }
            catch (Exception e) {
                log.debug("\u89e3\u6790\u6298\u6263\u7801JSON\u5931\u8d25: {}", (Object)e.getMessage());
            }
        }
        if (codeDiscount.compareTo(BigDecimal.ZERO) == 0 && voucherDiscount.compareTo(BigDecimal.ZERO) == 0 && order.getTotalDiscounts() != null && order.getTotalDiscounts().compareTo(BigDecimal.ZERO) > 0) {
            codeDiscount = order.getTotalDiscounts();
        }
        info.setCode(String.valueOf(codeDiscount));
        info.setVoucher(voucherDiscount);
        return info;
    }

    private List<OrderDetailLogDto> buildLogList(EccangOrder order) {
        HashMap<String, String> details;
        LocalDateTime paidAt;
        ArrayList<OrderDetailLogDto> logs = new ArrayList<OrderDetailLogDto>();
        if (order.getCreatedAtEccang() != null) {
            logs.add(this.buildLog("created", order.getCreatedAtEccang(), "\u8ba2\u5355\u521b\u5efa", "\u7cfb\u7edf", Map.of("\u8ba2\u5355\u53f7", order.getName(), "\u6765\u6e90", order.getSourceName() != null ? order.getSourceName() : "Eccang")));
        }
        if ((paidAt = this.resolvePaidAt(order)) != null) {
            logs.add(this.buildLog("payment", paidAt, "\u8ba2\u5355\u5df2\u652f\u4ed8", "\u7cfb\u7edf", Map.of("\u91d1\u989d", "$" + (order.getTotalPrice() != null ? order.getTotalPrice().toPlainString() : "0"), "\u72b6\u6001", order.getFinancialStatus() != null ? order.getFinancialStatus() : "-")));
        }
        if (order.getFulfillmentCreatedAt() != null) {
            Map<String, Object> detailsMap = new HashMap<>();
            detailsMap.put("\u65f6\u95f4", order.getFulfillmentCreatedAt().format(FMT));
            if (order.getTrackingNumbers() != null) {
                detailsMap.put("\u7269\u6d41\u5355\u53f7", order.getTrackingNumbers());
            }
            logs.add(this.buildLog("shipped", order.getFulfillmentCreatedAt(), "\u8ba2\u5355\u5df2\u53d1\u8d27", "\u4ed3\u5e93", detailsMap));
        }
        if (order.getDeliveredAt() != null) {
            logs.add(this.buildLog("delivered", order.getDeliveredAt(), "\u8ba2\u5355\u5df2\u59a5\u6295", "\u7cfb\u7edf", null));
        }
        if (order.getCancelledAtEccang() != null) {
            Map<String, Object> detailsMap = new HashMap<>();
            if (order.getCancelReason() != null) {
                detailsMap.put("\u539f\u56e0", order.getCancelReason());
            }
            logs.add(this.buildLog("cancelled", order.getCancelledAtEccang(), "\u8ba2\u5355\u5df2\u53d6\u6d88", "\u7cfb\u7edf", detailsMap));
        }
        List<OrderRefund> refunds = this.refundRepository.findByOrderIdOrderByCreatedAtEccangDesc(order.getId());
        for (OrderRefund refund : refunds) {
            if (refund.getCreatedAtEccang() == null) continue;
            HashMap<String, Object> details2 = new HashMap<String, Object>();
            details2.put("\u9000\u6b3e\u91d1\u989d", "$" + (refund.getTotalRefunded() != null ? refund.getTotalRefunded().toPlainString() : "0"));
            if (refund.getNote() != null) {
                details2.put("\u5907\u6ce8", refund.getNote());
            }
            logs.add(this.buildLog("refund", refund.getCreatedAtEccang(), "\u8ba2\u5355\u9000\u6b3e", "\u7cfb\u7edf", details2));
        }
        logs.sort((a, b) -> {
            if (a.getTime() == null || b.getTime() == null) {
                return 0;
            }
            return b.getTime().compareTo(a.getTime());
        });
        return logs;
    }

    private OrderDetailLogDto buildLog(String type, LocalDateTime time, String content, String operator, Map<String, Object> details) {
        OrderDetailLogDto log = new OrderDetailLogDto();
        log.setType(type);
        log.setTime(time.format(FMT));
        log.setContent(content);
        log.setOperator(operator);
        log.setDetails(details);
        return log;
    }

    public OrderDetailController(EccangOrderRepository orderRepository, OrderLineItemRepository lineItemRepository, OrderTransactionRepository transactionRepository, OrderRefundRepository refundRepository, InfluencerSampleOrderRepository sampleOrderRepository, InfluencerConversionOrderRepository conversionOrderRepository, EccangProductVariantRepository variantRepository) {
        this.orderRepository = orderRepository;
        this.lineItemRepository = lineItemRepository;
        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
        this.sampleOrderRepository = sampleOrderRepository;
        this.conversionOrderRepository = conversionOrderRepository;
        this.variantRepository = variantRepository;
    }
}

