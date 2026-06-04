package com.athlunakms.eccang.order.service;

import com.athlunakms.eccang.order.dto.*;
import com.athlunakms.eccang.order.entity.OrderLineItem;
import com.athlunakms.eccang.order.entity.OrderRefund;
import com.athlunakms.eccang.order.entity.OrderTransaction;
import com.athlunakms.eccang.order.entity.EccangOrder;
import com.athlunakms.eccang.order.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Eccang 订单业务门面服务
 * 统一封装订单查询、同步、草稿管理及红人绑定逻辑
 * 提供统一的订单操作入口内部逻辑委派给 EccangSyncService 和 EccangOrderCreateService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EccangOrderService {

    private final EccangOrderRepository orderRepository;
    private final OrderLineItemRepository lineItemRepository;
    private final OrderTransactionRepository transactionRepository;
    private final OrderRefundRepository refundRepository;
    
    private final EccangSyncService syncService;
    private final EccangOrderCreateService orderCreateService;
    private final OrderClassificationService classificationService;

    // --- 同步集成接口 ---

    /**
     * 触发异步同步所有订单
     * @param storeId 店铺ID
     */
    public void syncOrders(Long storeId) {
        syncService.syncOrders(storeId, null, null, true, false);
    }

    /**
     * 指定时间范围的异步同步（按 Eccang 「更新时间」，供定时任务增量同步）
     */
    public void syncOrders(Long storeId, LocalDateTime startTime, LocalDateTime endTime) {
        syncService.syncOrders(storeId, startTime, endTime, false, false);
    }

    public void syncOrders(Long storeId, LocalDateTime startTime, LocalDateTime endTime, boolean forceUpdate) {
        syncService.syncOrders(storeId, startTime, endTime, forceUpdate, false);
    }

    /**
     * 管理端「订单同步」弹窗：与 Eccang Admin 一致按「更新时间 updated_at」筛选时间范围，并 forceUpdate=true，
     * 避免本地已有记录时因「Eccang 更新时间未变」被跳过；不臆测改为 created_at（会与「近期有变动的老单」场景冲突）。
     */
    public void syncOrdersFromAdminModal(Long storeId, LocalDateTime startTime, LocalDateTime endTime) {
        syncService.syncOrders(storeId, startTime, endTime, true, false);
    }

    /**
     * 获取指定店铺的同步进度
     */
    public SyncProgressDto getSyncProgress(Long storeId) {
        return syncService.getProgress(storeId);
    }

    /**
     * 取消进行中的同步任务
     */
    public boolean cancelSync(Long storeId) {
        return syncService.cancelSync(storeId);
    }

    /**
     * 判断同步任务是否在运行
     */
    public boolean isSyncRunning(Long storeId) {
        SyncProgressDto progress = syncService.getProgress(storeId);
        return progress != null && "RUNNING".equals(progress.getStatus());
    }

    /**
     * 清理同步进度
     */
    public void clearSyncProgress(Long storeId) {
        syncService.clearProgress(storeId);
    }

    // --- 订单创建接口 ---

    /**
     * 创建订单或草稿
     */
    public OrderCreateResponse createOrder(OrderCreateRequest request) {
        return orderCreateService.createOrder(request);
    }

    /**
     * 更新草稿订单
     */
    public OrderCreateResponse updateDraftOrder(Long id, OrderCreateRequest request) {
        return orderCreateService.updateDraft(id, request);
    }

    /**
     * 确认并转换本地草稿为正式订单
     */
    public OrderCreateResponse confirmDraftOrder(Long id) {
        return orderCreateService.confirmDraft(id);
    }

    /**
     * 删除草稿订单
     */
    public void deleteDraftOrder(Long id) {
        orderCreateService.deleteDraft(id);
    }

    /**
     * 绑定订单到指定红人 (由 InfluencerOrderController 调用)
     */
    public void bindInfluencerToOrder(String orderId, Long influencerId, String operator) {
        orderRepository.findByEccangOrderId(orderId).ifPresent(order -> {
            order.setInfluencerId(influencerId);
            // 更多绑定逻辑可以委派或直接在此实现
            orderRepository.save(order);
            log.info("订单 {} 已绑定至红人 {}, 操作人: {}", orderId, influencerId, operator);
        });
    }

    // --- 查询与转换接口 ---

    /**
     * 获取所有订单列表按创建时间倒序
     */
    public List<EccangOrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtEccangDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定店铺的订单列表
     */
    public List<EccangOrderDto> getOrdersByStoreId(Long storeId) {
        return orderRepository.findByStoreIdOrderByCreatedAtEccangDesc(storeId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取订单详情包含行项目、交易和退款记录
     */
    public EccangOrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDtoWithDetails)
                .orElse(null);
    }

    // --- 内部转换方法 (私有) ---

    private EccangOrderDto convertToDto(EccangOrder entity) {
        EccangOrderDto dto = new EccangOrderDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private EccangOrderDto convertToDtoWithDetails(EccangOrder entity) {
        EccangOrderDto dto = convertToDto(entity);
        
        // 加载行项目
        List<OrderLineItem> lineItems = lineItemRepository.findByOrderId(entity.getId());
        dto.setLineItems(lineItems.stream().map(this::convertLineItemToDto).collect(Collectors.toList()));

        // 加载交易记录
        List<OrderTransaction> transactions = transactionRepository.findByOrderIdOrderByCreatedAtEccangDesc(entity.getId());
        dto.setTransactions(transactions.stream().map(this::convertTransactionToDto).collect(Collectors.toList()));

        // 加载退款记录
        List<OrderRefund> refunds = refundRepository.findByOrderIdOrderByCreatedAtEccangDesc(entity.getId());
        dto.setRefunds(refunds.stream().map(this::convertRefundToDto).collect(Collectors.toList()));

        // 汇总支付状态
        calculateFinancialStats(dto, transactions);
        
        return dto;
    }

    private void calculateFinancialStats(EccangOrderDto dto, List<OrderTransaction> txs) {
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal refunded = BigDecimal.ZERO;
        for (OrderTransaction tx : txs) {
            if ("success".equals(tx.getStatus())) {
                if ("sale".equals(tx.getKind()) || "capture".equals(tx.getKind())) {
                    paid = paid.add(tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO);
                } else if ("refund".equals(tx.getKind())) {
                    refunded = refunded.add(tx.getAmount() != null ? tx.getAmount() : BigDecimal.ZERO);
                }
            }
        }
        dto.setTotalPaid(paid);
        dto.setTotalRefunded(refunded);
        dto.setNetPayment(paid.subtract(refunded));
    }

    private OrderLineItemDto convertLineItemToDto(OrderLineItem entity) {
        OrderLineItemDto dto = new OrderLineItemDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private OrderTransactionDto convertTransactionToDto(OrderTransaction entity) {
        OrderTransactionDto dto = new OrderTransactionDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private OrderRefundDto convertRefundToDto(OrderRefund entity) {
        OrderRefundDto dto = new OrderRefundDto();
        dto.setId(entity.getId().toString());
        if (entity.getCreatedAtEccang() != null) {
            dto.setTime(entity.getCreatedAtEccang().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        dto.setAmount(entity.getTotalRefunded() != null ? entity.getTotalRefunded().toString() : "0");
        dto.setReason(entity.getNote());
        return dto;
    }
}
