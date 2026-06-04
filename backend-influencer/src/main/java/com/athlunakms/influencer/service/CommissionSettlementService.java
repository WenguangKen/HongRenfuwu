package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.CommissionSetting;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerBalance;
import com.athlunakms.influencer.entity.InfluencerBalanceLog;
import com.athlunakms.influencer.entity.InfluencerCommissionOrder;
import com.athlunakms.influencer.repository.CommissionSettingRepository;
import com.athlunakms.influencer.repository.InfluencerBalanceLogRepository;
import com.athlunakms.influencer.repository.InfluencerBalanceRepository;
import com.athlunakms.influencer.repository.InfluencerCommissionOrderRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommissionSettlementService {
    private static final Logger log = LoggerFactory.getLogger(CommissionSettlementService.class);
    private static final int SETTLEMENT_BATCH_LIMIT = 100;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /** 可配置汇率表（TODO: 迁移到数据库配置表或外部 API） */
    private static final Map<String, java.math.BigDecimal> EXCHANGE_RATES = new HashMap<>() {{
        put("CNY", new java.math.BigDecimal("0.14"));
        put("EUR", new java.math.BigDecimal("1.08"));
        put("GBP", new java.math.BigDecimal("1.25"));
        put("HKD", new java.math.BigDecimal("0.13"));
    }};
    
    private final CommissionSettingRepository settingRepository;
    private final InfluencerCommissionOrderRepository commissionOrderRepository;
    private final InfluencerRepository influencerRepository;
    private final InfluencerBalanceRepository balanceRepository;
    private final InfluencerBalanceLogRepository balanceLogRepository;
    private final JdbcTemplate jdbcTemplate;

    // Fix #9: Removed @Transactional — each settleOrder() has its own transaction
    public int processSettlement() {
        Optional<CommissionSetting> settingOpt = this.settingRepository.findFirstByEnabledTrueOrderByIdAsc();
        if (settingOpt.isEmpty()) {
            log.info("分佣规则未启用跳过结算");
            return 0;
        }
        CommissionSetting setting = settingOpt.get();
        log.info("开始分佣结算规则: 状态={}, 等待天数={}, 退款检查天数={}", setting.getOrderStatus(), setting.getWaitDays(), setting.getCheckRefundDays());
        List<Map<String, Object>> eligibleOrders = this.findEligibleOrders(setting);
        log.info("找到 {} 个符合条件的订单待结算", eligibleOrders.size());
        int settledCount = 0;
        for (Map<String, Object> order : eligibleOrders) {
            try {
                boolean success = this.settleOrder(order, setting);
                if (!success) continue;
                ++settledCount;
            }
            catch (Exception e) {
                log.error("结算订单失败: orderId={}, error={}", order.get("id"), e.getMessage(), e);
            }
        }
        log.info("分佣结算完成成功结算 {} 个订单", settledCount);
        return settledCount;
    }

    private List<Map<String, Object>> findEligibleOrders(CommissionSetting setting) {
        String sql = """
            SELECT
                ico.id, ico.order_id, ico.order_name, ico.influencer_id, ico.influencer_name,
                ico.discount_code, ico.total_price, ico.total_shipping, ico.total_refund,
                ico.commission_rate, ico.financial_status, ico.fulfillment_display_status,
                ico.delivered_at, ico.tracking_number, ico.updated_at, ico.currency
            FROM influencer_conversion_order ico
            WHERE ico.financial_status = 'paid'
              AND UPPER(ico.fulfillment_display_status) = UPPER(?)
              AND ico.cancelled_at IS NULL
              AND ico.delivered_at IS NOT NULL
              AND ico.delivered_at <= DATE_SUB(NOW(), INTERVAL ? DAY)
              AND ico.created_at <= DATE_SUB(NOW(), INTERVAL 3 DAY)
              AND (ico.commission_status IS NULL OR ico.commission_status != 'settled')
              AND NOT EXISTS (
                  SELECT 1 FROM influencer_commission_order icom
                  WHERE icom.conversion_order_id = ico.id
              )
            ORDER BY ico.delivered_at ASC
            LIMIT ?
        """;
        return this.jdbcTemplate.queryForList(sql, setting.getOrderStatus(), setting.getWaitDays(), SETTLEMENT_BATCH_LIMIT);
    }

    @Transactional
    public boolean settleOrder(Map<String, Object> orderData, CommissionSetting setting) {
        Long conversionOrderId = ((Number)orderData.get("id")).longValue();
        String orderName = (String)orderData.get("order_name");
        if (this.commissionOrderRepository.existsByConversionOrderId(conversionOrderId)) {
            log.debug("订单 {} 已结算跳过", orderName);
            return false;
        }
        if (!this.checkOrderStatus(conversionOrderId)) {
            log.info("订单 {} 状态异常已退款或取消跳过结算", orderName);
            return false;
        }
        InfluencerCommissionOrder commissionOrder = this.createCommissionOrder(orderData);
        this.commissionOrderRepository.save(commissionOrder);
        this.updateInfluencerBalance(commissionOrder);
        this.updateConversionOrderStatus(conversionOrderId);
        log.info("订单 {} 结算成功佣金: {}", orderName, commissionOrder.getCommissionAmount());
        return true;
    }

    private boolean checkOrderStatus(Long conversionOrderId) {
        String sql = """
            SELECT financial_status, cancelled_at
            FROM influencer_conversion_order
            WHERE id = ?
        """;
        List<Map<String, Object>> result = this.jdbcTemplate.queryForList(sql, conversionOrderId);
        if (result.isEmpty()) {
            return false;
        }
        Map<String, Object> order = result.get(0);
        String financialStatus = (String)order.get("financial_status");
        Object cancelledAt = order.get("cancelled_at");
        
        // 订单不能是退款(refunded/partially_refunded)和取消(cancelled)状态
        boolean isRefunded = financialStatus != null && financialStatus.toLowerCase().contains("refunded");
        boolean isCancelled = cancelledAt != null;
        boolean isVoided = "voided".equalsIgnoreCase(financialStatus);
        
        return !isRefunded && !isCancelled && !isVoided;
    }

    private InfluencerCommissionOrder createCommissionOrder(Map<String, Object> orderData) {
        Long influencerId;
        BigDecimal commissionRate;
        InfluencerCommissionOrder order = new InfluencerCommissionOrder();
        Long conversionOrderId = ((Number)orderData.get("id")).longValue();
        order.setConversionOrderId(conversionOrderId);
        order.setInfluencerId(Long.valueOf(((Number)orderData.get("influencer_id")).longValue()));
        order.setInfluencerName((String)orderData.get("influencer_name"));
        order.setOrderName((String)orderData.get("order_name"));
        order.setDiscountCode((String)orderData.get("discount_code"));
        BigDecimal totalPrice = orderData.get("total_price") != null ? new BigDecimal(orderData.get("total_price").toString()) : BigDecimal.ZERO;
        BigDecimal totalShipping = orderData.get("total_shipping") != null ? new BigDecimal(orderData.get("total_shipping").toString()) : BigDecimal.ZERO;
        BigDecimal totalRefund = orderData.get("total_refund") != null ? new BigDecimal(orderData.get("total_refund").toString()) : BigDecimal.ZERO;
        commissionRate = orderData.get("commission_rate") != null ? new BigDecimal(orderData.get("commission_rate").toString()) : BigDecimal.ZERO;
        if (commissionRate.compareTo(BigDecimal.ZERO) <= 0) {
            influencerId = Long.valueOf(((Number)orderData.get("influencer_id")).longValue());
            Optional<Influencer> influencerOpt = this.influencerRepository.findById(influencerId);
            if (influencerOpt.isPresent() && influencerOpt.get().getCommissionRate() != null && influencerOpt.get().getCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
                commissionRate = influencerOpt.get().getCommissionRate();
                log.info("从档案获取红人 {} 的佣金率: {}%", influencerId, commissionRate);
            }
        }
        order.setOrderTotal(totalPrice);
        order.setShippingAmount(totalShipping);
        order.setRefundAmount(totalRefund);
        order.setCommissionRate(commissionRate);
        order.setCurrency(orderData.get("currency") != null ? orderData.get("currency").toString() : "USD");
        order.calculateCommission();
        order.setSettlementStatus("settled");
        order.setSettledAt(LocalDateTime.now());
        if (orderData.get("delivered_at") != null) {
            Object deliveredAtObj = orderData.get("delivered_at");
            if (deliveredAtObj instanceof Timestamp) {
                order.setDeliveredAt(((Timestamp)deliveredAtObj).toLocalDateTime());
            } else if (deliveredAtObj instanceof LocalDateTime) {
                order.setDeliveredAt((LocalDateTime)deliveredAtObj);
            }
        }
        order.setTrackingNumber((String)orderData.get("tracking_number"));
        try {
            String itemsSql = "SELECT sku, title, variant_title, quantity, price FROM influencer_conversion_order_item WHERE conversion_order_id = ? ORDER BY id";
            List<Map<String, Object>> lineItems = this.jdbcTemplate.queryForList(itemsSql, conversionOrderId);
            if (!lineItems.isEmpty()) {
                // Fix #5: Use Jackson ObjectMapper instead of manual JSON building
                List<Map<String, Object>> itemList = new java.util.ArrayList<>();
                for (Map<String, Object> item : lineItems) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("sku", item.get("sku") != null ? item.get("sku").toString() : "");
                    itemMap.put("title", item.get("title") != null ? item.get("title").toString() : "");
                    itemMap.put("variant_title", item.get("variant_title") != null ? item.get("variant_title").toString() : "");
                    itemMap.put("quantity", item.get("quantity") != null ? item.get("quantity") : 1);
                    itemMap.put("price", item.get("price") != null ? item.get("price") : 0);
                    itemList.add(itemMap);
                }
                order.setItemsSummary(objectMapper.writeValueAsString(itemList));
                log.debug("订单 {} 保存了 {} 个商品", orderData.get("order_name"), lineItems.size());
            }
        }
        catch (Exception e) {
            log.warn("查询订单商品信息失败: conversionOrderId={}, error={}", conversionOrderId, e.getMessage());
        }
        return order;
    }

    @Transactional
    public void updateInfluencerBalance(InfluencerCommissionOrder commissionOrder) {
        Long influencerId = commissionOrder.getInfluencerId();
        String influencerName = commissionOrder.getInfluencerName();
        BigDecimal commissionAmount = commissionOrder.getCommissionAmount();
        InfluencerBalance balance = this.balanceRepository.findByInfluencerId(influencerId).orElseGet(() -> {
            InfluencerBalance newBalance = new InfluencerBalance();
            newBalance.setInfluencerId(influencerId);
            newBalance.setInfluencerName(influencerName);
            return this.balanceRepository.save(newBalance);
        });
        if (balance.getInfluencerName() == null || balance.getInfluencerName().isEmpty()) {
            balance.setInfluencerName(influencerName);
        }
        BigDecimal beforeAmount = balance.getPendingAmount();
        // 汇率转换：将佣金转换为账户币种（通常是 USD）
        BigDecimal rate = getExchangeRateToUsd(commissionOrder.getCurrency());
        BigDecimal commissionAmountInUsd = commissionAmount.multiply(rate);
        
        balance.addPendingAmount(commissionAmountInUsd);
        this.balanceRepository.save(balance);
        InfluencerBalanceLog logEntry = new InfluencerBalanceLog();
        logEntry.setInfluencerId(influencerId);
        logEntry.setBalanceType("pending");
        logEntry.setChangeType("add");
        logEntry.setAmount(commissionAmountInUsd);
        logEntry.setBalanceBefore(beforeAmount);
        logEntry.setBalanceAfter(balance.getPendingAmount());
        logEntry.setReferenceType("commission_order");
        logEntry.setReferenceId(commissionOrder.getId());
        logEntry.setRemark("订单 " + commissionOrder.getOrderName() + " 分佣结算 (原金额: " + commissionAmount + " " + commissionOrder.getCurrency() + ")");
        this.balanceLogRepository.save(logEntry);
        log.debug("\u7ea2\u4eba {} \u4f59\u989d\u66f4\u65b0\uff0c\u5f85\u7ed3\u7b97\u91d1\u989d +{}", influencerId, commissionAmount);
    }

    private void updateConversionOrderStatus(Long conversionOrderId) {
        String sql = "UPDATE influencer_conversion_order SET commission_status = 'settled', updated_at = NOW() WHERE id = ?";
        this.jdbcTemplate.update(sql, conversionOrderId);
    }

    public CommissionSetting getSetting() {
        Optional<CommissionSetting> opt = this.settingRepository.findFirstByOrderByIdAsc();
        if (opt.isPresent()) {
            CommissionSetting s = opt.get();
            log.info("Found existing commission setting: ID={}, waitDays={}, checkRefundDays={}", s.getId(), s.getWaitDays(), s.getCheckRefundDays());
            return s;
        }
        log.info("No commission setting found, creating default.");
        CommissionSetting defaultSetting = new CommissionSetting();
        CommissionSetting saved = this.settingRepository.save(defaultSetting);
        log.info("Created default commission setting: ID={}, waitDays={}, checkRefundDays={}", saved.getId(), saved.getWaitDays(), saved.getCheckRefundDays());
        return saved;
    }

    @Transactional
    public CommissionSetting updateSetting(CommissionSetting newSetting) {
        log.info("Updating commission setting. New values: waitDays={}, checkRefundDays={}", newSetting.getWaitDays(), newSetting.getCheckRefundDays());
        CommissionSetting setting = this.getSetting();
        log.info("Current setting before update: ID={}, waitDays={}", setting.getId(), setting.getWaitDays());
        setting.setEnabled(newSetting.getEnabled());
        setting.setOrderStatus(newSetting.getOrderStatus());
        setting.setWaitDays(newSetting.getWaitDays());
        setting.setCheckRefundDays(newSetting.getCheckRefundDays());
        setting.setOnlyUnsettled(newSetting.getOnlyUnsettled());
        CommissionSetting saved = this.settingRepository.save(setting);
        log.info("Saved commission setting: ID={}, waitDays={}, checkRefundDays={}", saved.getId(), saved.getWaitDays(), saved.getCheckRefundDays());
        return saved;
    }

    public CommissionSettlementService(CommissionSettingRepository settingRepository, InfluencerCommissionOrderRepository commissionOrderRepository, InfluencerRepository influencerRepository, InfluencerBalanceRepository balanceRepository, InfluencerBalanceLogRepository balanceLogRepository, JdbcTemplate jdbcTemplate) {
        this.settingRepository = settingRepository;
        this.commissionOrderRepository = commissionOrderRepository;
        this.influencerRepository = influencerRepository;
        this.balanceRepository = balanceRepository;
        this.balanceLogRepository = balanceLogRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    private BigDecimal getExchangeRateToUsd(String currency) {
        if (currency == null || "USD".equalsIgnoreCase(currency)) {
            return BigDecimal.ONE;
        }
        // Fix #4: Use configurable exchange rate map instead of hardcoded switch
        BigDecimal rate = EXCHANGE_RATES.get(currency.toUpperCase());
        if (rate != null) {
            return rate;
        }
        log.warn("未知货币 {} 使用默认汇率 1:1", currency);
        return BigDecimal.ONE;
    }
}

