package com.athlunakms.eccang.order.service;

import com.athlunakms.eccang.influencer.entity.InfluencerReadOnly;
import com.athlunakms.eccang.influencer.repository.InfluencerReadOnlyRepository;
import com.athlunakms.eccang.order.entity.InfluencerConversionOrder;
import com.athlunakms.eccang.order.entity.InfluencerSampleOrder;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.repository.InfluencerConversionOrderRepository;
import com.athlunakms.eccang.order.repository.InfluencerSampleOrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import com.athlunakms.eccang.order.entity.OrderLineItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单分类服务
 * 将订单分类到红人样品单或转化订单表
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderClassificationService {

    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final com.athlunakms.eccang.order.repository.InfluencerSampleOrderItemRepository sampleItemRepository;
    private final com.athlunakms.eccang.order.repository.InfluencerConversionOrderItemRepository conversionItemRepository;
    private final InfluencerReadOnlyRepository influencerRepository;
    private final SampleOrderAutoMatchService autoMatchService;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;
    private final com.athlunakms.eccang.order.repository.OrderLineItemRepository lineItemRepository;
    private final com.athlunakms.eccang.order.repository.EccangOrderRepository eccangOrderRepository;

    // 样品单标签关键词
    private static final String SAMPLE_ORDER_TAG = "influencer order";

    /**
     * 修复业务表中 delivered_at 未同步的订单
     * 将 eccang_orders 中的 delivered_at 和 fulfillment_display_status 回填到业务表
     * 同时处理 fulfillment_display_status=delivered 但 delivered_at 为空的情况
     */
    @Transactional
    public void repairDeliveredStatus() {
        try {
            // 1. 从 eccang_orders 回填 delivered_at 到转化订单
            int convUpdated = jdbcTemplate.update("""
                        UPDATE influencer_conversion_order co
                        JOIN orders so ON co.order_id = so.id
                        SET co.delivered_at = COALESCE(co.delivered_at, so.delivered_at),
                            co.fulfillment_display_status = so.fulfillment_display_status
                        WHERE so.fulfillment_display_status = 'delivered'
                          AND (co.delivered_at IS NULL OR co.fulfillment_display_status != 'delivered')
                    """);

            // 2. 从 eccang_orders 回填 delivered_at 到样品单
            int sampleUpdated = jdbcTemplate.update("""
                        UPDATE influencer_sample_order so2
                        JOIN orders so ON so2.order_id = so.id
                        SET so2.delivered_at = COALESCE(so2.delivered_at, so.delivered_at),
                            so2.fulfillment_display_status = so.fulfillment_display_status
                        WHERE so.fulfillment_display_status = 'delivered'
                          AND (so2.delivered_at IS NULL OR so2.fulfillment_display_status != 'delivered')
                    """);

            // 3. 兜底fulfillment_display_status=delivered 但 delivered_at 仍为空
            int convFallback = jdbcTemplate.update("""
                        UPDATE influencer_conversion_order
                        SET delivered_at = NOW()
                        WHERE fulfillment_display_status = 'delivered' AND delivered_at IS NULL
                    """);

            int sampleFallback = jdbcTemplate.update("""
                        UPDATE influencer_sample_order
                        SET delivered_at = NOW()
                        WHERE fulfillment_display_status = 'delivered' AND delivered_at IS NULL
                    """);

            int total = convUpdated + sampleUpdated + convFallback + sampleFallback;
            if (total > 0) {
                log.info("[RepairDelivered] Fixed {} orders (conv: {}+{}, sample: {}+{})",
                        total, convUpdated, convFallback, sampleUpdated, sampleFallback);
            }

            // ====== 自动修复规则 ======

            // 规则1 已移除: 订单号以 1/2/3 开头 → 标记为已送达
            // 原因: 订单号已增长到 #10000+, 所有新订单都以 #1 开头, 导致新订单被误标记为已送达
            // 老订单(#1000-#3999)如果发货超过45天, 会被下面的规则2自动处理

            // 规则2: 下单超过45天未取消 → 标记为已送达
            int rule2Conv = jdbcTemplate
                    .update("""
                                UPDATE influencer_conversion_order
                                SET fulfillment_display_status = 'delivered',
                                    delivered_at = COALESCE(delivered_at, NOW())
                                WHERE fulfillment_created_at < DATE_SUB(NOW(), INTERVAL 45 DAY)
                                  AND cancelled_at IS NULL
                                  AND (fulfillment_display_status IS NULL OR fulfillment_display_status NOT IN ('delivered', 'canceled', 'cancelled', 'returned', 'restocked'))
                            """);
            int rule2Sample = jdbcTemplate
                    .update("""
                                UPDATE influencer_sample_order
                                SET fulfillment_display_status = 'delivered',
                                    delivered_at = COALESCE(delivered_at, NOW())
                                WHERE fulfillment_created_at < DATE_SUB(NOW(), INTERVAL 45 DAY)
                                  AND cancelled_at IS NULL
                                  AND (fulfillment_display_status IS NULL OR fulfillment_display_status NOT IN ('delivered', 'canceled', 'cancelled', 'returned', 'restocked'))
                            """);
            if (rule2Conv + rule2Sample > 0) {
                log.info("[AutoCorrect] Rule2 (>45 days): conv={}, sample={}", rule2Conv, rule2Sample);
            }

        } catch (Exception e) {
            log.error("[RepairDelivered] Failed to repair delivered status: {}", e.getMessage());
        }
    }

    /**
     * 同步业务表订单更新时调用
     */
    @Transactional
    public void syncBusinessTable(EccangOrder order,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> lineItems) {
        if (order == null || order.getId() == null) {
            return;
        }

        // 检查是否已在业务表中
        if (sampleOrderRepository.existsByOrderId(order.getId())) {
            // 样品单更新金额和物流状态
            sampleOrderRepository.findByOrderId(order.getId()).ifPresent(sample -> {
                // 更新红人信息 (支持手动绑定更新) - 仅当有新值时更新防止覆盖为空
                if (order.getInfluencerId() != null) {
                    sample.setInfluencerId(order.getInfluencerId());
                    sample.setInfluencerName(order.getInfluencerName());
                }

                sample.setTotalPrice(order.getTotalPrice());
                sample.setCurrency(order.getCurrency());
                sample.setFinancialStatus(order.getFinancialStatus());
                sample.setCancelledAt(order.getCancelledAtEccang());
                sample.setOrderCreatedAt(order.getCreatedAtEccang()); // 已同步创建时间
                sample.setIsDraft(order.getIsDraft());

                // 同步地址信息
                sample.setRecipientName(order.getShippingName());
                sample.setRecipientPhone(order.getShippingPhone());
                sample.setRecipientAddress(buildFullAddress(order));
                sample.setRecipientCountry(order.getShippingCountry());
                sample.setCustomerEmail(order.getCustomerEmail());

                updateFulfillmentInfo(sample, order);
                sampleOrderRepository.save(sample);

                // Update Items
                syncSampleOrderItems(sample.getId(), order.getId(), lineItems);

                log.debug("Updated sample order for order: {}", order.getName());
            });
            return;
        }

        if (conversionOrderRepository.existsByOrderId(order.getId())) {
            // 转化单更新金额和物流状态
            conversionOrderRepository.findByOrderId(order.getId()).ifPresent(conversion -> {
                // 更新红人信息 (支持手动绑定更新)
                // 更新红人信息 (支持手动绑定更新) - 仅当有新值时更新防止覆盖为空
                if (order.getInfluencerId() != null) {
                    conversion.setInfluencerId(order.getInfluencerId());
                    conversion.setInfluencerName(order.getInfluencerName());
                }

                updateConversionOrderAmounts(conversion, order);

                // 同步地址信息
                conversion.setRecipientName(order.getShippingName());
                conversion.setRecipientPhone(order.getShippingPhone());
                conversion.setRecipientAddress(buildFullAddress(order));
                conversion.setRecipientCountry(order.getShippingCountry());
                conversion.setCustomerEmail(order.getCustomerEmail());

                updateFulfillmentInfo(conversion, order);

                // FINAL SAFETY CHECK: Prevent SQL 1048 Error
                if (conversion.getInfluencerId() == null) {
                    log.error(
                            "ABORTING SAVE: InfluencerId is null for ConversionOrder ID: {}, EccangOrder: {}. This prevents SQL 1048 error.",
                            conversion.getId(), order.getName());
                    return;
                }

                conversionOrderRepository.save(conversion);

                // Update Items
                syncConversionOrderItems(conversion.getId(), order.getId(), lineItems);

                log.debug("Updated conversion order for order: {}", order.getName());
            });
            return;
        }

        // 不在业务表中尝试分类
        classifyOrder(order, lineItems);
    }

    /**
     * 删除草稿相关的业务表记录
     */
    @Transactional
    public void deleteDraftBusinessRecords(Long orderId) {
        if (orderId == null)
            return;
        sampleOrderRepository.deleteByOrderId(orderId);
        conversionOrderRepository.deleteByOrderId(orderId);
        log.info("Deleted draft business records for order id: {}", orderId);
    }

    /**
     * 强制重新分类订单用于数据修复
     * 会先删除已存在的业务订单样品单/转化单然后重新运行分类逻辑
     */
    @Transactional
    public void forceReclassify(EccangOrder order, List<OrderLineItem> lineItems) {
        if (order == null || order.getId() == null) {
            return;
        }

        // 1. 删除已存在的业务记录
        if (sampleOrderRepository.existsByOrderId(order.getId())) {
            sampleOrderRepository.deleteByOrderId(order.getId());
            log.info("Deleted existing sample order for re-classification: {}", order.getName());
        }
        if (conversionOrderRepository.existsByOrderId(order.getId())) {
            conversionOrderRepository.deleteByOrderId(order.getId());
            log.info("Deleted existing conversion order for re-classification: {}", order.getName());
        }

        // 2. 重新分类
        classifyOrder(order, lineItems);
    }

    /**
     * 手动绑定红人用于导入或其他强制绑定场景
     */
    @Transactional
    public void manualBindInfluencer(EccangOrder order,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> lineItems) {
        if (conversionOrderRepository.existsByOrderId(order.getId())) {
            syncBusinessTable(order, lineItems);
        } else if (sampleOrderRepository.existsByOrderId(order.getId())) {
            syncBusinessTable(order, lineItems);
        } else {
            // 强制创建样品单
            createOrUpdateSampleOrder(order, lineItems);
        }
    }

    /**
     * 对订单进行分类
     * 优先级样品单 > 转化单 > 普通订单
     */
    @Transactional
    public void classifyOrder(EccangOrder order,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> lineItems) {
        if (order == null || order.getId() == null) {
            return;
        }

        // 1. 检查是否为样品单
        if (isSampleOrder(order)) {
            createOrUpdateSampleOrder(order, lineItems);
            return;
        }

        // 2. 检查是否为转化订单
        InfluencerMatch match = matchInfluencerByDiscountCode(order);
        if (match != null) {
            createOrUpdateConversionOrder(order, match, lineItems);
            return;
        }

        // 3. 普通订单不处理
        log.debug("Order {} is a regular order, no classification needed", order.getName());
    }

    /**
     * 判断是否为样品单
     */
    private boolean isSampleOrder(EccangOrder order) {
        // 1. 检查标签
        String tags = order.getTags();
        if (tags != null && tags.toLowerCase().contains(SAMPLE_ORDER_TAG)) {
            return true;
        }

        // 2. 检查红人关联或来源 (针对手动创建的红人样品单)
        if (order.getInfluencerId() != null || "influencer".equalsIgnoreCase(order.getOrderSource())) {
            return true;
        }

        return false;
    }

    /**
     * 创建或更新样品单
     */
    private void createOrUpdateSampleOrder(EccangOrder order,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> lineItems) {
        if (sampleOrderRepository.existsByOrderId(order.getId())) {
            log.debug("Sample order already exists for order: {}", order.getName());
            return;
        }

        InfluencerSampleOrder sample = new InfluencerSampleOrder();
        sample.setOrderId(order.getId());
        sample.setEccangOrderId(order.getEccangOrderId());
        sample.setEccangOrderNumber(order.getEccangOrderNumber());
        sample.setOrderName(order.getName());
        sample.setIsDraft(order.getIsDraft());

        // 优先使用订单上已有的红人关联
        if (order.getInfluencerId() != null) {
            sample.setInfluencerId(order.getInfluencerId());
            sample.setInfluencerName(order.getInfluencerName());
        } else {
            // 1. 自动匹配根据收件人名称匹配红人真实姓名
            InfluencerReadOnly matchedInfluencer = matchInfluencerByShippingName(order.getShippingName());

            // 2. 自动匹配如果收件人没匹配到尝试用标签匹配
            if (matchedInfluencer == null && order.getTags() != null) {
                matchedInfluencer = matchInfluencerByTags(order.getTags());
            }

            if (matchedInfluencer != null) {
                sample.setInfluencerId(matchedInfluencer.getId());
                String influencerName = matchedInfluencer.getRealName() != null
                        ? matchedInfluencer.getRealName()
                        : matchedInfluencer.getNickName();
                sample.setInfluencerName(influencerName);

                // 同时更新主订单的红人关联
                order.setInfluencerId(matchedInfluencer.getId());
                order.setInfluencerName(influencerName);

                log.info("Auto-matched influencer {} for sample order {} by name/tag",
                        influencerName, order.getName());
            }
        }

        sample.setTotalPrice(order.getTotalPrice());
        sample.setCurrency(order.getCurrency());
        sample.setOrderCreatedAt(order.getCreatedAtEccang());
        sample.setFinancialStatus(order.getFinancialStatus());
        sample.setCancelledAt(order.getCancelledAtEccang());

        // 地址信息
        sample.setRecipientName(order.getShippingName());
        sample.setRecipientPhone(order.getShippingPhone());
        sample.setRecipientAddress(buildFullAddress(order));
        sample.setRecipientCountry(order.getShippingCountry());
        sample.setCustomerEmail(order.getCustomerEmail());

        updateFulfillmentInfo(sample, order);

        InfluencerSampleOrder saved = sampleOrderRepository.save(sample);

        // 只有在尚未关联红人时，才尝试基于收件人姓名+邮箱自动匹配
        if (saved.getInfluencerId() == null) {
            autoMatchService.autoMatchInfluencer(saved);
        } else {
            // 已关联红人（通过订单自带/收件人名/标签匹配），直接更新状态
            updateInfluencerToCooperating(saved.getInfluencerId(), saved.getOrderName());
            // 将订单地址同步到红人收件地址库
            syncOrderAddressToInfluencer(saved.getInfluencerId(), order);
        }

        syncSampleOrderItems(saved.getId(), order.getId(), lineItems);

        log.debug("Created sample order for order: {}", order.getName());
    }

    // ... (intermediate match methods skipped)

    /**
     * 根据收件人名称匹配红人
     * 匹配逻辑收件人名称与红人真实姓名完全匹配忽略大小写和空格
     */
    private InfluencerReadOnly matchInfluencerByShippingName(String shippingName) {
        if (shippingName == null || shippingName.trim().isEmpty()) {
            return null;
        }

        String normalizedName = shippingName.trim();

        // 精确匹配红人真实姓名
        var influencer = influencerRepository.findByRealNameIgnoreCase(normalizedName).orElse(null);
        if (influencer != null) {
            return influencer;
        }

        // 尝试匹配昵称
        influencer = influencerRepository.findByNickNameIgnoreCase(normalizedName).orElse(null);
        if (influencer != null) {
            return influencer;
        }

        log.debug("No influencer found matching shipping name: {}", shippingName);
        return null;
    }

    /**
     * 根据标签匹配红人
     * 遍历标签看是否有标签与红人姓名/昵称匹配
     */
    private InfluencerReadOnly matchInfluencerByTags(String tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        String[] tagArray = tags.split(",");
        for (String tag : tagArray) {
            String trimmedTag = tag.trim();
            if (trimmedTag.equalsIgnoreCase(SAMPLE_ORDER_TAG))
                continue;

            // 尝试匹配真实姓名
            var influencer = influencerRepository.findByRealNameIgnoreCase(trimmedTag).orElse(null);
            if (influencer != null)
                return influencer;

            // 尝试匹配昵称
            influencer = influencerRepository.findByNickNameIgnoreCase(trimmedTag).orElse(null);
            if (influencer != null)
                return influencer;
        }
        return null;
    }

    /**
     * 匹配红人折扣码 - 从红人表 influencer_discount_code 字段查询
     */
    private InfluencerMatch matchInfluencerByDiscountCode(EccangOrder order) {
        String discountCodes = order.getDiscountCodes();
        if (discountCodes == null || discountCodes.isEmpty()) {
            return null;
        }

        String[] codes = discountCodes.split(",");
        for (String code : codes) {
            String trimmedCode = code.trim();
            if (trimmedCode.isEmpty()) {
                continue;
            }

            var influencer = influencerRepository
                    .findByInfluencerDiscountCodeIgnoreCase(trimmedCode)
                    .orElse(null);
            if (influencer != null) {
                String influencerName = influencer.getRealName() != null ? influencer.getRealName()
                        : influencer.getNickName();
                log.trace("Found influencer {} for discount code {} via influencer table",
                        influencerName, trimmedCode);
                return new InfluencerMatch(influencer, trimmedCode, null);
            }
        }

        return null;
    }

    /**
     * 创建或更新转化订单
     */
    private void createOrUpdateConversionOrder(EccangOrder order, InfluencerMatch match,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> lineItems) {
        Optional<InfluencerConversionOrder> existingOpt = conversionOrderRepository.findByOrderId(order.getId());
        InfluencerConversionOrder conversion;

        if (existingOpt.isPresent()) {
            conversion = existingOpt.get();
            // 如果已经是该红人的，且不是强制刷新场景，则跳过
            if (conversion.getInfluencerId().equals(match.influencer().getId())) {
                log.debug("Conversion order already exists for same influencer: {}", order.getName());
                return;
            }

            // 重要：安全检查，如果订单已经结算分佣，绝对不允许再次迁移归属
            if ("settled".equalsIgnoreCase(conversion.getCommissionStatus())) {
                log.info("[Safety] Skipping re-attribution for settled order: {}", order.getName());
                return;
            }
            log.info("[Re-attribute] Updating existing conversion order {} from influencer {} to {}",
                    order.getName(), conversion.getInfluencerId(), match.influencer().getId());
            // 重置关键归属字段
            conversion.setCreatedAt(LocalDateTime.now()); // 重置观察期
        } else {
            conversion = new InfluencerConversionOrder();
            conversion.setOrderId(order.getId());
        }
        conversion.setEccangOrderId(order.getEccangOrderId());
        conversion.setEccangOrderNumber(order.getEccangOrderNumber());
        conversion.setOrderName(order.getName());
        conversion.setInfluencerId(match.influencer().getId());
        conversion.setInfluencerName(match.influencer().getRealName());
        conversion.setDiscountCode(match.discountCode());
        conversion.setFinancialStatus(order.getFinancialStatus());
        conversion.setCancelledAt(order.getCancelledAtEccang());
        conversion.setProcessedAt(order.getProcessedAtEccang());
        conversion.setPaidAt(order.getProcessedAtEccang()); // 支付时间使用 processedAt
        conversion.setOrderCreatedAt(order.getCreatedAtEccang());

        // 设置佣金比例来自折扣码绑定表或红人默认比例
        BigDecimal rate = null;
        String commissionSource = "influencer"; // 默认使用红人佣金率

        if (match.commissionRate() != null && match.commissionRate().compareTo(BigDecimal.ZERO) > 0) {
            rate = match.commissionRate();
            commissionSource = "discount"; // 使用折扣码绑定的佣金率
        } else if (match.influencer().getCommissionRate() != null) {
            rate = match.influencer().getCommissionRate();
            commissionSource = "influencer"; // 使用红人默认佣金率
        }

        if (rate != null) {
            conversion.setCommissionRate(rate);
        }
        conversion.setCommissionSource(commissionSource);

        updateConversionOrderAmounts(conversion, order);

        // 同步地址信息
        conversion.setRecipientName(order.getShippingName());
        conversion.setRecipientPhone(order.getShippingPhone());
        conversion.setRecipientAddress(buildFullAddress(order));
        conversion.setRecipientCountry(order.getShippingCountry());
        conversion.setCustomerEmail(order.getCustomerEmail());

        updateFulfillmentInfo(conversion, order);

        InfluencerConversionOrder saved = conversionOrderRepository.save(conversion);
        syncConversionOrderItems(saved.getId(), order.getId(), lineItems);

        log.info("Created conversion order for order: {}, influencer: {}, commissionRate: {}",
                order.getName(), match.influencer().getRealName(), conversion.getCommissionRate());
    }

    private void updateConversionOrderAmounts(InfluencerConversionOrder conversion, EccangOrder order) {
        conversion.setTotalPrice(order.getTotalPrice());
        conversion.setTotalShipping(
                order.getTotalShippingPrice() != null ? order.getTotalShippingPrice() : BigDecimal.ZERO);
        conversion.setTotalRefund(order.getRefundTotal() != null ? order.getRefundTotal() : BigDecimal.ZERO);
        conversion.setCurrency(order.getCurrency());
        conversion.setFinancialStatus(order.getFinancialStatus());
        conversion.setCancelledAt(order.getCancelledAtEccang());
        conversion.setOrderCreatedAt(order.getCreatedAtEccang());

        // 计算可分佣金额
        conversion.calculateCommissionableAmount();
    }

    /**
     * 更新样品单的物流信息
     * 以 EccangOrder 的直接字段为权威数据源JSON仅用于提取物流追踪信息
     */
    private void updateFulfillmentInfo(InfluencerSampleOrder sample, EccangOrder order) {
        // 1. 物流状态和送达时间始终从 EccangOrder 直接字段获取权威数据源
        sample.setFulfillmentDisplayStatus(order.getFulfillmentDisplayStatus());
        sample.setDeliveredAt(order.getDeliveredAt());

        // 2. 物流追踪信息优先从 JSON 解析回退到主订单字段
        FulfillmentInfo info = parseFulfillmentInfo(order);
        if (info != null) {
            sample.setTrackingCompany(info.trackingCompany);
            sample.setTrackingNumber(info.trackingNumber);
            sample.setTrackingUrl(info.trackingUrl);
        } else {
            sample.setTrackingNumber(order.getTrackingNumbers());
        }

        // 3. 兜底displayStatus=delivered 但 deliveredAt 为空 → 用当前时间
        if ("delivered".equalsIgnoreCase(order.getFulfillmentDisplayStatus()) && sample.getDeliveredAt() == null) {
            sample.setDeliveredAt(LocalDateTime.now());
        }

        // 4. 同步时间节点字段
        sample.setFulfillmentCreatedAt(order.getFulfillmentCreatedAt());
        sample.setInTransitAt(order.getInTransitAt());
        sample.setEstimatedDeliveryAt(order.getEstimatedDeliveryAt());
        sample.setFulfillmentStatus(order.getFulfillmentStatus());
        sample.setClosedAt(order.getClosedAtEccang());

        // 保存原始 JSON 用于轨迹展示
        sample.setTrackingEventsJson(order.getFulfillmentsJson());

        // 5. 应用内置自动修复逻辑实时应用免除依赖末尾的全量跑批
        applyAutoRepairLogic(sample, order);
    }

    /**
     * 更新转化单的物流信息
     * 以 EccangOrder 的直接字段为权威数据源JSON仅用于提取物流追踪信息
     */
    private void updateFulfillmentInfo(InfluencerConversionOrder conversion, EccangOrder order) {
        // 1. 物流状态和送达时间始终从 EccangOrder 直接字段获取权威数据源
        conversion.setFulfillmentDisplayStatus(order.getFulfillmentDisplayStatus());
        conversion.setDeliveredAt(order.getDeliveredAt());

        // 2. 物流追踪信息优先从 JSON 解析回退到主订单字段
        FulfillmentInfo info = parseFulfillmentInfo(order);
        if (info != null) {
            conversion.setTrackingCompany(info.trackingCompany);
            conversion.setTrackingNumber(info.trackingNumber);
            conversion.setTrackingUrl(info.trackingUrl);
        } else {
            conversion.setTrackingNumber(order.getTrackingNumbers());
        }

        // 3. 兜底displayStatus=delivered 但 deliveredAt 为空 → 用当前时间
        if ("delivered".equalsIgnoreCase(order.getFulfillmentDisplayStatus()) && conversion.getDeliveredAt() == null) {
            conversion.setDeliveredAt(LocalDateTime.now());
        }

        // 4. 同步时间节点字段
        conversion.setFulfillmentCreatedAt(order.getFulfillmentCreatedAt());
        conversion.setInTransitAt(order.getInTransitAt());
        conversion.setEstimatedDeliveryAt(order.getEstimatedDeliveryAt());
        conversion.setFulfillmentStatus(order.getFulfillmentStatus());
        conversion.setClosedAt(order.getClosedAtEccang());

        // 保存原始 JSON 用于轨迹展示
        conversion.setTrackingEventsJson(order.getFulfillmentsJson());

        // 5. 应用内置自动修复逻辑实时应用免除依赖末尾的全量跑批
        applyAutoRepairLogic(conversion, order);
    }

    /**
     * 应用订单物流状态自动修复逻辑 (针对发货超过 45 天的未取消订单)
     * 注意: 已移除"订单号以1/2/3开头"的规则, 因为订单号已增长到#10000+, 会误伤所有新订单
     */
    private void applyAutoRepairLogic(Object businessOrder, EccangOrder order) {
        if (order.getCancelledAtEccang() != null)
            return;

        boolean isOld = order.getFulfillmentCreatedAt() != null &&
                order.getFulfillmentCreatedAt().isBefore(LocalDateTime.now().minusDays(45));

        if (isOld) {
            String currentStatus = order.getFulfillmentDisplayStatus();
            boolean isUnmodifiable = "delivered".equalsIgnoreCase(currentStatus) ||
                    "canceled".equalsIgnoreCase(currentStatus) ||
                    "cancelled".equalsIgnoreCase(currentStatus) ||
                    "returned".equalsIgnoreCase(currentStatus) ||
                    "restocked".equalsIgnoreCase(currentStatus);

            if (!isUnmodifiable) {
                if (businessOrder instanceof InfluencerSampleOrder) {
                    InfluencerSampleOrder smp = (InfluencerSampleOrder) businessOrder;
                    smp.setFulfillmentDisplayStatus("delivered");
                    if (smp.getDeliveredAt() == null) {
                        smp.setDeliveredAt(LocalDateTime.now());
                    }
                } else if (businessOrder instanceof InfluencerConversionOrder) {
                    InfluencerConversionOrder cnv = (InfluencerConversionOrder) businessOrder;
                    cnv.setFulfillmentDisplayStatus("delivered");
                    if (cnv.getDeliveredAt() == null) {
                        cnv.setDeliveredAt(LocalDateTime.now());
                    }
                }
            }
        }
    }

    /**
     * 从 EccangOrder 的 fulfillmentsJson 解析第一个 Fulfillment 的物流信息
     * REST API 返回的 fulfillments 数组结构
     * [{ "status": "success", "tracking_company": "UPS", "tracking_number": "123",
     * "tracking_url": "...", ... }]
     */
    private FulfillmentInfo parseFulfillmentInfo(EccangOrder order) {
        String json = order.getFulfillmentsJson();
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            JsonNode fulfillments = objectMapper.readTree(json);
            if (fulfillments.isArray() && fulfillments.size() > 0) {
                JsonNode first = fulfillments.get(0);

                // 优先级displayStatus > shipment_status > status
                String displayStatus = null;
                if (first.hasNonNull("displayStatus")) {
                    displayStatus = first.get("displayStatus").asText();
                } else if (first.hasNonNull("display_status")) {
                    displayStatus = first.get("display_status").asText();
                } else if (first.hasNonNull("shipment_status")) {
                    displayStatus = first.get("shipment_status").asText();
                } else {
                    displayStatus = first.path("status").asText(null);
                }

                String trackingCompany = first.path("tracking_company").asText(null);
                String trackingNumber = first.path("tracking_number").asText(null);
                String trackingUrl = first.path("tracking_url").asText(null);

                // 已移除: 不再根据 fulfilled+closed 推断为 delivered
                // 原因: closedAt 可能由多种原因设置，不等于已送达
                // 应该信任 Eccang 的实际 fulfillment_display_status

                LocalDateTime deliveredAt = null;
                if ("delivered".equalsIgnoreCase(displayStatus)) {
                    // 如果状态是妥投尝试从 updated_at 获取时间
                    String updatedAtStr = first.path("updated_at").asText(null);
                    if (updatedAtStr != null && !updatedAtStr.isEmpty()) {
                        try {
                            deliveredAt = LocalDateTime.parse(updatedAtStr.substring(0, 19));
                        } catch (Exception e) {
                            deliveredAt = LocalDateTime.now();
                        }
                    } else {
                        deliveredAt = LocalDateTime.now();
                    }
                }

                return new FulfillmentInfo(displayStatus, trackingCompany, trackingNumber, trackingUrl, deliveredAt);
            }
        } catch (Exception e) {
            log.warn("Failed to parse fulfillments JSON for order {}: {}", order.getName(), e.getMessage());
        }
        return null;
    }

    /**
     * 物流信息解析结果
     */
    private record FulfillmentInfo(String displayStatus, String trackingCompany, String trackingNumber,
            String trackingUrl, LocalDateTime deliveredAt) {
    }

    /**
     * 构建完整地址字符串
     */
    private String buildFullAddress(EccangOrder order) {
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
        return sb.toString().trim();
    }

    /**
     * 将红人状态更新为 COOPERATING 并更新 last_sample_at
     * 仅当红人当前不在 BLACKLIST/TERMINATED 状态时才更新
     */
    private void updateInfluencerToCooperating(Long influencerId, String orderName) {
        if (influencerId == null) return;
        try {
            int updated = jdbcTemplate.update(
                "UPDATE influencer SET status = 'COOPERATING', last_sample_at = NOW() " +
                "WHERE id = ? AND status NOT IN ('BLACKLIST', 'TERMINATED') AND stage != 'TRASH'",
                influencerId);
            if (updated > 0) {
                log.info("[StatusTransition] Influencer {} moved to COOPERATING via sample order: {}",
                         influencerId, orderName);
            }
        } catch (Exception e) {
            log.error("[StatusTransition] Failed to update influencer {} status: {}", influencerId, e.getMessage());
        }
    }

    /**
     * 将订单收件地址自动同步到红人收件地址库
     * 根据地址文本去重，避免重复录入相同地址
     */
    private void syncOrderAddressToInfluencer(Long influencerId, EccangOrder order) {
        if (influencerId == null) return;
        String fullAddress = buildFullAddress(order);
        if (fullAddress == null || fullAddress.isBlank()) return;

        try {
            // 检查是否已存在相同地址（按 normalized address 去重）
            String normalizedAddress = fullAddress.trim().toLowerCase();
            int existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM influencer_address WHERE influencer_id = ? AND LOWER(TRIM(address)) = ?",
                Integer.class, influencerId, normalizedAddress);

            if (existing > 0) {
                log.debug("[AddressSync] Address already exists for influencer {}, skipping", influencerId);
                return;
            }

            // 判断该红人是否已有地址记录，没有的话设为默认
            int addressCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM influencer_address WHERE influencer_id = ?",
                Integer.class, influencerId);
            boolean isDefault = (addressCount == 0);

            // 插入新地址
            jdbcTemplate.update(
                "INSERT INTO influencer_address (influencer_id, recipient_name, phone, email, country, " +
                "address, city, state, street1, street2, postal_code, is_default, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                influencerId,
                order.getShippingName(),
                order.getShippingPhone(),
                order.getCustomerEmail(),
                order.getShippingCountry(),
                fullAddress,
                order.getShippingCity(),
                order.getShippingProvince(),
                order.getShippingAddress1(),
                order.getShippingAddress2(),
                order.getShippingZip(),
                isDefault);

            log.info("[AddressSync] Auto-saved shipping address for influencer {} from order {}",
                     influencerId, order.getName());
        } catch (Exception e) {
            log.error("[AddressSync] Failed to sync address for influencer {}: {}", influencerId, e.getMessage());
        }
    }

    /**
     * 红人匹配结果
     */
    private record InfluencerMatch(InfluencerReadOnly influencer, String discountCode, BigDecimal commissionRate) {
    }

    private void syncSampleOrderItems(Long sampleOrderId, Long eccangOrderId,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> sourceItems) {
        sampleItemRepository.deleteBySampleOrderId(sampleOrderId);

        // 获取订单状态以判断是否已取消
        var order = sampleOrderRepository.findByOrderId(eccangOrderId).orElse(null);
        boolean isCancelled = order != null && order.getCancelledAt() != null;

        if (sourceItems != null && !sourceItems.isEmpty()) {
            var newItems = sourceItems.stream().map(src -> {
                var item = new com.athlunakms.eccang.order.entity.InfluencerSampleOrderItem();
                item.setSampleOrderId(sampleOrderId);
                item.setEccangLineItemId(src.getEccangLineItemId());
                item.setEccangProductId(src.getEccangProductId());
                item.setEccangVariantId(src.getEccangVariantId());
                item.setSku(src.getSku());
                item.setTitle(src.getTitle());
                item.setVariantTitle(src.getVariantTitle());
                item.setQuantity(src.getQuantity());
                item.setPrice(src.getPrice());

                // Status Mapping Logic
                String status = src.getFulfillmentStatus();
                if (status == null) {
                    status = "unfulfilled";
                }

                // If order is cancelled and item is still unfulfilled, mark as cancelled
                if (isCancelled && "unfulfilled".equals(status)) {
                    status = "cancelled";
                }

                item.setFulfillmentStatus(status);
                item.setReturnedQuantity(src.getReturnedQuantity() != null ? src.getReturnedQuantity() : 0);

                // Fix: Map missing fields
                item.setImageUrl(src.getImageUrl());
                item.setFulfillmentId(src.getFulfillmentId());
                item.setVendor(src.getVendor());
                item.setTotal(src.getTotal());
                item.setTotalDiscount(src.getTotalDiscount());
                item.setRequiresShipping(src.getRequiresShipping());
                item.setGiftCard(src.getGiftCard());
                item.setFulfillableQuantity(src.getFulfillableQuantity());
                item.setIsReturned(src.getIsReturned() != null ? src.getIsReturned() : false);

                return item;
            }).collect(java.util.stream.Collectors.toList());
            sampleItemRepository.saveAll(newItems);
        }
    }

    private void syncConversionOrderItems(Long conversionOrderId, Long eccangOrderId,
            java.util.List<com.athlunakms.eccang.order.entity.OrderLineItem> sourceItems) {
        conversionItemRepository.deleteByConversionOrderId(conversionOrderId);

        // 获取订单状态以判断是否已取消
        var order = conversionOrderRepository.findByOrderId(eccangOrderId).orElse(null);
        boolean isCancelled = order != null && order.getCancelledAt() != null;

        if (sourceItems != null && !sourceItems.isEmpty()) {
            var newItems = sourceItems.stream().map(src -> {
                var item = new com.athlunakms.eccang.order.entity.InfluencerConversionOrderItem();
                item.setConversionOrderId(conversionOrderId);
                item.setEccangLineItemId(src.getEccangLineItemId());
                item.setEccangProductId(src.getEccangProductId());
                item.setEccangVariantId(src.getEccangVariantId());
                item.setSku(src.getSku());
                item.setTitle(src.getTitle());
                item.setVariantTitle(src.getVariantTitle());
                item.setQuantity(src.getQuantity());
                item.setPrice(src.getPrice());

                // Status Mapping Logic
                String status = src.getFulfillmentStatus();
                if (status == null) {
                    status = "unfulfilled";
                }

                // If order is cancelled and item is still unfulfilled, mark as cancelled
                if (isCancelled && "unfulfilled".equals(status)) {
                    status = "cancelled";
                }

                item.setFulfillmentStatus(status);
                item.setReturnedQuantity(src.getReturnedQuantity() != null ? src.getReturnedQuantity() : 0);

                // Fix: Map missing fields
                item.setImageUrl(src.getImageUrl());
                item.setFulfillmentId(src.getFulfillmentId());
                item.setVendor(src.getVendor());
                item.setTotal(src.getTotal());
                item.setTotalDiscount(src.getTotalDiscount());
                item.setRequiresShipping(src.getRequiresShipping());
                item.setGiftCard(src.getGiftCard());
                item.setFulfillableQuantity(src.getFulfillableQuantity());
                item.setIsReturned(src.getIsReturned() != null ? src.getIsReturned() : false);

                return item;
            }).collect(java.util.stream.Collectors.toList());
            conversionItemRepository.saveAll(newItems);
        }
    }

    /**
     * 根据折扣码重新分类订单（用于换绑场景）
     * 逻辑：
     * 1. 查找所有包含该折扣码的订单
     * 2. 更新主表 orders 的归属（排除已锁定的）
     * 3. 更新或创建业务表 ICO 记录（排除已锁定的）
     * 4. 对于转移的 ICO，重置 createdAt 以启动新的 3 天观察期
     */
    @Transactional
    public int reclassifyOrdersByDiscountCode(String discountCode) {
        log.info("[Reclassify] Discount binding feature removed; skipping reclassification for code: {}", discountCode);
        return 0;
    }

    @Transactional
    public int deleteConversionOrders(String discountCode, Long influencerId, boolean onlyUnsettled) {
        log.info("[Unbind] Discount binding feature removed; skipping delete for code: {}, influencer: {}",
                discountCode, influencerId);
        return 0;
    }
}
