package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.client.UserServiceClient;
import com.athlunakms.influencer.dto.ApiResponse;
import com.athlunakms.influencer.dto.CommissionPayoutDto;
import com.athlunakms.influencer.dto.InfluencerCommissionDto;
import com.athlunakms.influencer.entity.CommissionPayout;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerBalance;
import com.athlunakms.influencer.entity.InfluencerBalanceLog;
import com.athlunakms.influencer.entity.InfluencerCommissionOrder;
import com.athlunakms.influencer.entity.InfluencerConversionOrder;
import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.repository.CommissionPayoutRepository;
import com.athlunakms.influencer.repository.InfluencerBalanceLogRepository;
import com.athlunakms.influencer.repository.InfluencerBalanceRepository;
import com.athlunakms.influencer.repository.InfluencerCommissionOrderRepository;
import com.athlunakms.influencer.repository.InfluencerConversionOrderRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.InfluencerSampleOrderRepository;
import com.athlunakms.influencer.repository.SystemTagRepository;
import com.athlunakms.influencer.scheduler.CommissionScheduler;
import com.athlunakms.influencer.service.InfluencerLogService;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = { "/v1/commission" })
public class CommissionOrderController {
    private static final Logger log = LoggerFactory.getLogger(CommissionOrderController.class);

    // ========== 打款状态常量 ==========
    private static final String STATUS_PENDING = "pending";
    private static final String STATUS_APPROVED = "approved";
    private static final String STATUS_REJECTED = "rejected";
    private static final String STATUS_COMPLETED = "completed";
    private static final String PAYOUT_ID_PREFIX = "PAY";
    private static final DateTimeFormatter PAYOUT_ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int PAYOUT_SEQ_LENGTH = 3;

    private final InfluencerCommissionOrderRepository commissionOrderRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final InfluencerBalanceRepository balanceRepository;
    private final InfluencerBalanceLogRepository balanceLogRepository;
    private final CommissionPayoutRepository payoutRepository;
    private final CommissionScheduler commissionScheduler;
    private final InfluencerRepository influencerRepository;
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final ObjectMapper objectMapper;
    private final InfluencerLogService logService;
    private final JdbcTemplate jdbcTemplate;
    private final UserServiceClient userServiceClient;
    private final SystemTagRepository systemTagRepository;
    private final StorageServiceFactory storageServiceFactory;

    @PostMapping(value = { "/upload" })
    public ApiResponse<Map<String, String>> uploadVoucher(@RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "fileName", required = false) String customFileName) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.error("上传文件不能为空");
        }
        try {
            StorageService storageService = this.storageServiceFactory.getStorageService();
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String relativePath = "vouchers/" + datePath;
            String finalFileName = customFileName;
            if (finalFileName == null || finalFileName.isEmpty()) {
                finalFileName = file.getOriginalFilename();
            } else {
                String originalName = file.getOriginalFilename();
                if (originalName != null && originalName.contains(".") && !finalFileName.contains(".")) {
                    finalFileName = finalFileName + originalName.substring(originalName.lastIndexOf("."));
                }
            }
            // 给文件名加 UUID 前缀，防止同名文件（如 pasted_voucher.png）互相覆盖
            String uniquePrefix = java.util.UUID.randomUUID().toString().substring(0, 8);
            finalFileName = uniquePrefix + "_" + finalFileName;
            String fileKey = storageService.upload(file, relativePath, finalFileName);
            String url = "/influencer-api/v1/files/" + fileKey;
            log.info("Voucher uploaded successfully: fileKey={}, url={}, originalName={}", fileKey, url,
                    file.getOriginalFilename());
            return ApiResponse.success(Map.of("fileKey", fileKey, "url", url, "fileName",
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "voucher"));
        } catch (Exception e) {
            log.error("Failed to upload voucher", e);
            return ApiResponse.error("上传凭证失败: " + e.getMessage());
        }
    }

    @GetMapping(value = { "/orders" })
    public ApiResponse<Page<InfluencerCommissionOrder>> getCommissionOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "influencerId", required = false) Long influencerId,
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "status", required = false) String status) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "settledAt"));
        List<Long> resolvedInfluencerIds = new ArrayList<>();
        if (influencerName != null && !influencerName.trim().isEmpty()) {
            String[] names = influencerName.split(",");
            for (String name : names) {
                String trimmed = name.trim();
                if (trimmed.isEmpty())
                    continue;
                List<Influencer> matched = this.influencerRepository.findByNickNameContainingIgnoreCase(trimmed);
                for (Influencer inf : matched) {
                    if (resolvedInfluencerIds.contains(inf.getId()))
                        continue;
                    resolvedInfluencerIds.add(inf.getId());
                }
            }
            if (resolvedInfluencerIds.isEmpty()) {
                return ApiResponse.success(Page.empty(pageable));
            }
        }
        Page<InfluencerCommissionOrder> orders = influencerId != null
                ? this.commissionOrderRepository.findByInfluencerId(influencerId, pageable)
                : (!resolvedInfluencerIds.isEmpty()
                        ? this.commissionOrderRepository.findByInfluencerIdIn(resolvedInfluencerIds, pageable)
                        : (status != null && !status.isEmpty()
                                ? this.commissionOrderRepository.findBySettlementStatus(status, pageable)
                                : this.commissionOrderRepository.findAll(pageable)));
        return ApiResponse.success(orders);
    }

    @GetMapping(value = { "/orders/{id}" })
    public ApiResponse<InfluencerCommissionOrder> getCommissionOrder(@PathVariable(value = "id") Long id) {
        return this.commissionOrderRepository.findById(id).map(ApiResponse::success).orElse(ApiResponse.error("订单不存在"));
    }

    @GetMapping(value = { "/balance/{influencerId}" })
    public ApiResponse<InfluencerBalance> getInfluencerBalance(
            @PathVariable(value = "influencerId") Long influencerId) {
        InfluencerBalance balance = this.balanceRepository.findByInfluencerId(influencerId).orElseGet(() -> {
            InfluencerBalance newBalance = new InfluencerBalance();
            newBalance.setInfluencerId(influencerId);
            newBalance.setPendingAmount(BigDecimal.ZERO);
            newBalance.setAvailableAmount(BigDecimal.ZERO);
            newBalance.setPaidAmount(BigDecimal.ZERO);
            return newBalance;
        });
        return ApiResponse.success(balance);
    }

    @GetMapping(value = { "/balance/{influencerId}/logs" })
    public ApiResponse<Page<InfluencerBalanceLog>> getBalanceLogs(
            @PathVariable(value = "influencerId") Long influencerId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<InfluencerBalanceLog> logs = this.balanceLogRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId,
                pageable);
        return ApiResponse.success(logs);
    }

    @PostMapping(value = { "/settlement/trigger" })
    public ApiResponse<Map<String, Object>> triggerSettlement() {
        int settledCount = this.commissionScheduler.manualSettlement();
        Map<String, Object> result = new HashMap<>();
        result.put("settledCount", settledCount);
        result.put("message", "分佣结算完成");
        return ApiResponse.success(result);
    }

    @GetMapping(value = { "/stats" })
    public ApiResponse<Map<String, Object>> getCommissionStats(
            @RequestParam(value = "influencerId", required = false) Long influencerId) {
        Map<String, Object> stats = new HashMap<>();
        if (influencerId != null) {
            InfluencerBalance balance = this.balanceRepository.findByInfluencerId(influencerId).orElse(null);
            if (balance != null) {
                stats.put("pendingAmount", balance.getPendingAmount());
                stats.put("availableAmount", balance.getAvailableAmount());
                stats.put("paidAmount", balance.getPaidAmount());
                stats.put("totalBalance", balance.getTotalBalance());
            }
            stats.put("pendingCommission",
                    this.commissionOrderRepository.sumPendingCommissionByInfluencerId(influencerId));
            stats.put("settledCommission",
                    this.commissionOrderRepository.sumSettledCommissionByInfluencerId(influencerId));
        } else {
            long totalOrders = this.commissionOrderRepository.count();
            stats.put("totalCommissionOrders", totalOrders);
        }
        return ApiResponse.success(stats);
    }

    @GetMapping(value = { "/balances" })
    public ApiResponse<Page<InfluencerCommissionDto>> getAllBalances(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "influencer", required = false) String influencer,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "discountCode", required = false) String discountCode) {
        Page<InfluencerBalance> balances;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        if (influencer != null && !influencer.isEmpty() || email != null && !email.isEmpty()
                || discountCode != null && !discountCode.isEmpty()) {
            StringBuilder sql = new StringBuilder(
                    "SELECT * FROM influencer_balance WHERE (pending_amount > 0 OR available_amount > 0 OR paid_amount > 0)");
            List<Object> params = new ArrayList<>();
            if (influencer != null && !influencer.isEmpty()) {
                String[] names = influencer.split(",");
                sql.append(" AND (");
                for (int i = 0; i < names.length; ++i) {
                    if (i > 0) {
                        sql.append(" OR ");
                    }
                    sql.append("influencer_name LIKE ?");
                    params.add("%" + names[i].trim() + "%");
                }
                sql.append(")");
            }
            if (email != null && !email.isEmpty()) {
                String[] emails = email.split(",");
                sql.append(" AND influencer_id IN (SELECT id FROM influencer WHERE ");
                for (int i = 0; i < emails.length; ++i) {
                    if (i > 0) {
                        sql.append(" OR ");
                    }
                    sql.append("email LIKE ?");
                    params.add("%" + emails[i].trim() + "%");
                }
                sql.append(")");
            }
            if (discountCode != null && !discountCode.isEmpty()) {
                String[] codes = discountCode.split(",");
                sql.append(" AND influencer_id IN (SELECT influencer_id FROM influencer_conversion_order WHERE ");
                for (int i = 0; i < codes.length; ++i) {
                    if (i > 0) {
                        sql.append(" OR ");
                    }
                    sql.append("discount_code LIKE ?");
                    params.add("%" + codes[i].trim() + "%");
                }
                sql.append(")");
            }
            String countSql = sql.toString().replaceFirst("SELECT \\*", "SELECT COUNT(*)");
            Long total = this.jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());
            sql.append(" ORDER BY updated_at DESC LIMIT ? OFFSET ?");
            params.add(pageable.getPageSize());
            params.add((int) pageable.getOffset());
            List<InfluencerBalance> list = this.jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
                InfluencerBalance b = new InfluencerBalance();
                b.setId(rs.getLong("id"));
                b.setInfluencerId(rs.getLong("influencer_id"));
                b.setInfluencerName(rs.getString("influencer_name"));
                b.setPendingAmount(rs.getBigDecimal("pending_amount"));
                b.setAvailableAmount(rs.getBigDecimal("available_amount"));
                b.setPaidAmount(rs.getBigDecimal("paid_amount"));
                b.setUpdatedAt(
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                b.setCreatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                return b;
            }, params.toArray());
            balances = new PageImpl<>(list, pageable, total != null ? total : 0L);
        } else {
            balances = this.balanceRepository.findAllWithNonZeroBalance(pageable);
        }
        List<InfluencerCommissionDto> dtos = balances.getContent().stream().map(b -> {
            InfluencerCommissionDto dto = new InfluencerCommissionDto();
            dto.setInfluencerId(b.getInfluencerId());
            dto.setInfluencerName(b.getInfluencerName());
            dto.setPendingAmount(b.getPendingAmount());
            dto.setPaidAmount(b.getPaidAmount());
            dto.setAvailableAmount(b.getAvailableAmount());
            dto.setTotalAmount(b.getTotalBalance());
            List<CommissionPayout> payouts = this.payoutRepository
                    .findByInfluencerIdOrderByCreatedAtDesc(b.getInfluencerId());
            if (!payouts.isEmpty()) {
                dto.setLastInitiatedTime(payouts.get(0).getCreatedAt());
                payouts.stream().filter(p -> "completed".equals(p.getStatus())).findFirst().ifPresentOrElse(
                        p -> dto.setLastDistributeTime(p.getUpdatedAt() != null ? p.getUpdatedAt() : p.getCreatedAt()),
                        () -> dto.setLastDistributeTime(null));
            } else {
                dto.setLastInitiatedTime(null);
                dto.setLastDistributeTime(null);
            }
            this.commissionOrderRepository
                    .findFirstByInfluencerIdAndSettlementStatusOrderBySettledAtDesc(b.getInfluencerId(), "settled")
                    .ifPresentOrElse(order -> dto.setLastSettledTime(order.getSettledAt()),
                            () -> dto.setLastSettledTime(null));
            this.influencerRepository.findById(b.getInfluencerId()).ifPresent(inf -> {
                dto.setInfluencerEmail(inf.getEmail());
                if (dto.getInfluencerName() == null || dto.getInfluencerName().isEmpty()) {
                    String realName = inf.getRealName();
                    if (realName == null || realName.isEmpty()) {
                        realName = inf.getNickName();
                    }
                    if (realName == null || realName.isEmpty()) {
                        realName = inf.getEmail();
                    }
                    dto.setInfluencerName(realName);
                    b.setInfluencerName(realName);
                    this.balanceRepository.save(b);
                }
            });
            dto.setConversionOrderCount(this.conversionOrderRepository.countByInfluencerId(b.getInfluencerId()));
            dto.setSampleOrderCount(this.sampleOrderRepository.countByInfluencerId(b.getInfluencerId()));
            dto.setSettledOrderCount(this.commissionOrderRepository
                    .countByInfluencerIdAndSettlementStatus(b.getInfluencerId(), "settled"));
            dto.setPayoutCount(this.payoutRepository.countByInfluencerIdAndStatus(b.getInfluencerId(), "completed"));
            List<Object[]> discountUsage = this.conversionOrderRepository
                    .findDiscountCodeUsageByInfluencerId(b.getInfluencerId());
            if (!discountUsage.isEmpty()) {
                dto.setTopDiscountCode((String) discountUsage.get(0)[0]);
                List<String> codes = discountUsage.stream().map(row -> (String) row[0]).collect(Collectors.toList());
                dto.setDiscountCodes(codes);
            } else {
                dto.setTopDiscountCode(null);
                dto.setDiscountCodes(Collections.emptyList());
            }
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(new PageImpl<>(dtos, pageable, balances.getTotalElements()));
    }

    @PostMapping(value = { "/payout" })
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CommissionPayout> createPayout(@RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        Long influencerId = getRequiredLong(request, "influencerId");
        BigDecimal amount = getRequiredBigDecimal(request, "amount");
        if (influencerId == null || amount == null) {
            return ApiResponse.error("influencerId 和 amount 不能为空");
        }
        String remark = getOptionalString(request, "remark", "");
        String operator = decodeOperator(encodedOperator);
        try {
            return ApiResponse.success(this.createPayoutInternal(influencerId, amount, remark, 0, operator));
        } catch (Exception e) {
            log.error("创建打款失败: influencerId={}, amount={}", influencerId, amount, e);
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping(value = { "/batch-payouts" })
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<List<CommissionPayout>> batchPayout(@RequestBody List<Map<String, Object>> requests,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        List<CommissionPayout> results = new ArrayList<>();
        int seqOffset = 0;
        String operator = decodeOperator(encodedOperator);
        for (Map<String, Object> request : requests) {
            Long influencerId = getRequiredLong(request, "influencerId");
            BigDecimal amount = getRequiredBigDecimal(request, "amount");
            if (influencerId == null || amount == null) {
                throw new RuntimeException("批量打款参数错误: influencerId 和 amount 不能为空");
            }
            String remark = getOptionalString(request, "remark", "");
            results.add(this.createPayoutInternal(influencerId, amount, remark, seqOffset++, operator));
        }
        return ApiResponse.success(results);
    }

    /**
     * 内部打款创建方法（必须在事务上下文中调用）
     */
    @Transactional(rollbackFor = Exception.class)
    protected CommissionPayout createPayoutInternal(Long influencerId, BigDecimal amount, String remark, int seqOffset,
            String operator) {
        InfluencerBalance balance = this.balanceRepository.findByInfluencerId(influencerId)
                .orElseThrow(() -> new RuntimeException("红人余额记录不存在, influencerId=" + influencerId));
        if (amount.compareTo(balance.getPendingAmount()) > 0) {
            throw new RuntimeException("红人 " + balance.getInfluencerName() + " 打款金额($" + amount + ")超过待分佣金($"
                    + balance.getPendingAmount() + ")");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("打款金额必须大于0");
        }
        String timestamp = PAYOUT_ID_FORMATTER.format(LocalDateTime.now());
        String sequenceStr = String.format("%0" + PAYOUT_SEQ_LENGTH + "d", seqOffset);
        String paymentId = PAYOUT_ID_PREFIX + timestamp + sequenceStr;
        CommissionPayout payout = new CommissionPayout();
        payout.setPaymentId(paymentId);
        payout.setInfluencerId(influencerId);
        payout.setInfluencerName(balance.getInfluencerName());
        payout.setAmount(amount);
        payout.setStatus(STATUS_PENDING);
        payout.setRemark(remark);
        payout.setCreatedBy(operator);
        this.payoutRepository.save(payout);
        int rows = this.balanceRepository.transferPendingToPaid(influencerId, amount);
        if (rows == 0) {
            throw new RuntimeException("余额扣减失败: 红人 " + balance.getInfluencerName() + " 待结算金额不足");
        }
        log.info("打款创建成功: paymentId={}, influencerId={}, amount={}", paymentId, influencerId, amount);
        this.logService.logChange(influencerId, "提现申请", "-", "申请提现: $" + amount, operator,
                remark != null && !remark.isEmpty() ? remark : "发起分佣结算");
        return payout;
    }

    @GetMapping(value = { "/payouts/{influencerId}" })
    public ApiResponse<List<CommissionPayout>> getPayoutsByInfluencer(
            @PathVariable(value = "influencerId") Long influencerId) {
        List<CommissionPayout> payouts = this.payoutRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);
        return ApiResponse.success(payouts);
    }

    @GetMapping(value = { "/payouts" })
    public ApiResponse<Page<CommissionPayoutDto>> getAllPayouts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "payId", required = false) String payId,
            @RequestParam(value = "influencerName", required = false) String influencerName,
            @RequestParam(value = "ownerName", required = false) String ownerName,
            @RequestParam(value = "liaison", required = false) String liaison,
            @RequestParam(value = "timeType", required = false) String timeType,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Specification<CommissionPayout> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (payId != null && !payId.isEmpty()) {
                predicates.add(cb.like(root.get("paymentId"), "%" + payId + "%"));
            }
            if (influencerName != null && !influencerName.isEmpty()) {
                predicates.add(cb.like(root.get("influencerName"), "%" + influencerName + "%"));
            }
            List<Long> resolvedOwnerIds = new ArrayList<>();
            List<String> resolvedLiaisons = new ArrayList<>();
            if (ownerName != null && !ownerName.trim().isEmpty()) {
                String[] ownerNames = ownerName.split(",");
                try {
                    Map<Long, String> allNames = this.userServiceClient.getAllUserNames();
                    for (Map.Entry<Long, String> entry : allNames.entrySet()) {
                        for (String on : ownerNames) {
                            if (entry.getValue() == null
                                    || !entry.getValue().toLowerCase().contains(on.trim().toLowerCase()))
                                continue;
                            resolvedOwnerIds.add(entry.getKey());
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to resolve owner names", e);
                }
            }
            if (liaison != null && !liaison.trim().isEmpty()) {
                String[] liaisonNames = liaison.split(",");
                for (String ln : liaisonNames) {
                    resolvedLiaisons.add(ln.trim());
                }
            }
            if (!resolvedOwnerIds.isEmpty() || !resolvedLiaisons.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Influencer> infRoot = subquery.from(Influencer.class);
                subquery.select(infRoot.get("id"));
                List<Predicate> infPredicates = new ArrayList<>();
                if (!resolvedOwnerIds.isEmpty()) {
                    infPredicates.add(infRoot.get("ownerId").in(resolvedOwnerIds));
                }
                if (!resolvedLiaisons.isEmpty()) {
                    List<SystemTag> liaisonTags = this.systemTagRepository
                            .findByCategoryAndNameInAndEnabledTrue("LIAISON", resolvedLiaisons);
                    if (!liaisonTags.isEmpty()) {
                        List<Predicate> tagPredicates = new ArrayList<>();
                        for (SystemTag tag : liaisonTags) {
                            tagPredicates.add(cb.like(infRoot.get("tags"), "%" + tag.getId() + "%"));
                        }
                        infPredicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
                    } else {
                        infPredicates.add(cb.disjunction());
                    }
                }
                if (!infPredicates.isEmpty()) {
                    subquery.where(cb.and(infPredicates.toArray(new Predicate[0])));
                    predicates.add(root.get("influencerId").in(subquery));
                }
            }
            if (timeType != null && !timeType.isEmpty() && startTime != null && endTime != null) {
                String timeField = "createdAt";
                if ("audit_time".equals(timeType)) {
                    timeField = "approvedAt";
                } else if ("pay_time".equals(timeType)) {
                    timeField = "paidAt";
                }
                predicates.add(cb.between(root.get(timeField), startTime, endTime));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommissionPayout> payouts = this.payoutRepository.findAll(spec, pageable);
        if (payouts.isEmpty()) {
            return ApiResponse.success(payouts.map(CommissionPayoutDto::new));
        }
        List<Long> influencerIds = payouts.getContent().stream().map(CommissionPayout::getInfluencerId)
                .filter(id -> id != null).distinct().collect(Collectors.toList());
        List<Influencer> influencers = influencerIds.isEmpty() ? new ArrayList<>()
                : this.influencerRepository.findAllById(influencerIds);
        Map<Long, Influencer> influencerMap = influencers.stream().collect(Collectors.toMap(Influencer::getId, i -> i));
        Set<Long> ownerIds = new HashSet<>();
        Set<Long> tagIdsToFetch = new HashSet<>();
        for (Influencer inf : influencers) {
            if (inf.getOwnerId() != null) {
                ownerIds.add(inf.getOwnerId());
            }
            if (inf.getTags() == null)
                continue;
            try {
                List<Long> tIds = this.objectMapper.readValue(inf.getTags(), new TypeReference<List<Long>>() {
                });
                tagIdsToFetch.addAll(tIds);
            } catch (Exception e) {
                log.warn("Failed to parse tags JSON for influencer {}: {}", inf.getId(), e.getMessage());
            }
        }
        Map<Long, String> ownerNames = new HashMap<>();
        if (!ownerIds.isEmpty()) {
            try {
                ownerNames = this.userServiceClient.getUserNames(new ArrayList<>(ownerIds));
                log.debug("Fetched Owner Names: {}", ownerNames);
            } catch (Exception e) {
                log.error("Failed to fetch owner names: ", e);
            }
        }
        Map<Long, SystemTag> tagMap = new HashMap<>();
        if (!tagIdsToFetch.isEmpty()) {
            List<Long> tagIdsList = new ArrayList<>(tagIdsToFetch);
            List<SystemTag> tagsList = this.systemTagRepository.findAllById(tagIdsList);
            tagMap = tagsList.stream().collect(Collectors.toMap(SystemTag::getId, t -> t));
        }
        Map<Long, String> finalOwnerNames = ownerNames;
        Map<Long, SystemTag> finalTagMap = tagMap;
        Page<CommissionPayoutDto> dtoPage = payouts.map(payout -> {
            CommissionPayoutDto dto = new CommissionPayoutDto(payout);
            Influencer inf = influencerMap.get(payout.getInfluencerId());
            if (inf != null) {
                if (inf.getOwnerId() != null) {
                    dto.setOwnerName(finalOwnerNames.getOrDefault(inf.getOwnerId(), ""));
                }
                if (inf.getTags() != null) {
                    try {
                        List<Long> tIds = this.objectMapper.readValue(inf.getTags(), new TypeReference<List<Long>>() {
                        });
                        for (Long tId : tIds) {
                            SystemTag tag = finalTagMap.get(tId);
                            if (tag == null || !"LIAISON".equals(tag.getCategory()))
                                continue;
                            dto.setLiaisonName(tag.getName());
                            break;
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse tags for influencer " + inf.getId(), e);
                    }
                }
            }
            log.debug("Mapped Payout DTO: payId={}, ownerName={}, liaisonName={}", dto.getPaymentId(),
                    dto.getOwnerName(), dto.getLiaisonName());
            return dto;
        });
        return ApiResponse.success(dtoPage);
    }

    @PostMapping(value = { "/payout/{id}/audit" })
    @Transactional
    public ApiResponse<CommissionPayout> auditPayout(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        String remark;
        CommissionPayout payout = this.payoutRepository.findById(id).orElse(null);
        if (payout == null) {
            return ApiResponse.error("打款记录不存在");
        }
        String operator = "System";
        try {
            operator = URLDecoder.decode(encodedOperator, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to decode operator name: {}", encodedOperator, e);
        }
        String action = getOptionalString(request, "action", "");
        if (action.isEmpty()) {
            return ApiResponse.error("操作类型(action)不能为空");
        }
        remark = request.get("remark") != null ? request.get("remark").toString() : "";
        if ("approve".equals(action)) {
            Object detailsObj;
            payout.setStatus(STATUS_APPROVED);
            payout.setApprovedAt(LocalDateTime.now());
            payout.setApprovedBy(operator);
            payout.setReviewRemark(remark);
            if (request.get("paymentMethod") != null) {
                payout.setPaymentMethod(request.get("paymentMethod").toString());
            }
            if (request.get("paymentAccount") != null) {
                payout.setPaymentAccount(request.get("paymentAccount").toString());
            }
            if ((detailsObj = request.get("paymentDetails")) != null) {
                try {
                    String detailsJson = detailsObj instanceof String ? (String) detailsObj
                            : this.objectMapper.writeValueAsString(detailsObj);
                    payout.setPaymentDetails(detailsJson);
                } catch (Exception e) {
                    log.error("Failed to serialize payment details", e);
                }
            }
            payout.setRejectedAt(null);
            payout.setRejectedBy(null);
            this.logService.logChange(payout.getInfluencerId(), "提现审核", "待审核", "审核通过 ($" + payout.getAmount() + ")",
                    operator, remark);
        } else if ("reject".equals(action)) {
            payout.setStatus(STATUS_REJECTED);
            payout.setRejectedAt(LocalDateTime.now());
            payout.setRejectedBy(operator);
            payout.setReviewRemark(remark);
            int rows = this.balanceRepository.transferPaidToPending(payout.getInfluencerId(), payout.getAmount());
            if (rows == 0) {
                log.error("审核驳回余额回退失败: influencerId={}, amount={}", payout.getInfluencerId(), payout.getAmount());
                return ApiResponse.error("余额回退失败，请检查红人余额数据");
            }
            this.logService.logChange(payout.getInfluencerId(), "提现审核", "待审核", "审核驳回 ($" + payout.getAmount() + ")",
                    operator, remark);
        } else {
            return ApiResponse.error("无效的操作");
        }
        this.payoutRepository.save(payout);
        return ApiResponse.success(payout);
    }

    @PostMapping(value = { "/payout/{id}/confirm" })
    @Transactional
    public ApiResponse<CommissionPayout> confirmPayout(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        CommissionPayout payout = this.payoutRepository.findById(id).orElse(null);
        if (payout == null) {
            return ApiResponse.error("打款记录不存在");
        }
        payout.setStatus(STATUS_COMPLETED);
        // 实际打款时间：优先使用前端传入的 actualPaidAt，否则取当前时间
        if (request.get("actualPaidAt") != null && !request.get("actualPaidAt").toString().isEmpty()) {
            try {
                payout.setActualPaidAt(LocalDateTime.parse(request.get("actualPaidAt").toString(),
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                payout.setPaidAt(payout.getActualPaidAt());
            } catch (Exception e) {
                log.warn("Failed to parse actualPaidAt: {}", request.get("actualPaidAt"), e);
                payout.setPaidAt(LocalDateTime.now());
            }
        } else {
            payout.setPaidAt(LocalDateTime.now());
        }
        // 手续费 & 总付金额
        if (request.get("fee") != null) {
            payout.setFee(new BigDecimal(request.get("fee").toString()));
        }
        if (request.get("totalAmount") != null) {
            payout.setTotalAmount(new BigDecimal(request.get("totalAmount").toString()));
        }
        if (request.get("paymentMethod") != null) {
            payout.setPaymentMethod(request.get("paymentMethod").toString());
        }
        if (request.get("paymentAccount") != null) {
            payout.setPaymentAccount(request.get("paymentAccount").toString());
        }
        if (request.get("paymentReference") != null) {
            payout.setPaymentReference(request.get("paymentReference").toString());
        }
        try {
            payout.setPaymentDetails(this.objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error("Failed to serialize payment details", e);
        }
        this.payoutRepository.save(payout);
        String operator = decodeOperator(encodedOperator);
        this.logService.logChange(payout.getInfluencerId(), "提现确认", "待打款", "已打款 ($" + payout.getAmount() + ")",
                operator, "完成支付打款");
        return ApiResponse.success(payout);
    }

    @PostMapping(value = { "/payout/{id}/reinitiate" })
    @Transactional
    public ApiResponse<CommissionPayout> reinitiatePayout(@PathVariable("id") Long id,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        CommissionPayout payout = this.payoutRepository.findById(id).orElse(null);
        if (payout == null) {
            return ApiResponse.error("打款记录不存在");
        }
        if (!STATUS_REJECTED.equals(payout.getStatus())) {
            return ApiResponse.error("只能重新发起被拒绝的打款");
        }
        int rows = this.balanceRepository.transferPendingToPaid(payout.getInfluencerId(), payout.getAmount());
        if (rows == 0) {
            return ApiResponse.error("待打款余额不足");
        }
        payout.setStatus(STATUS_PENDING);
        payout.setRejectedAt(null);
        payout.setRejectedBy(null);
        payout.setReviewRemark(null);
        payout.setApprovedAt(null);
        payout.setApprovedBy(null);
        this.payoutRepository.save(payout);
        String operator = decodeOperator(encodedOperator);
        this.logService.logChange(payout.getInfluencerId(), "提现申请", "被驳回", "重新发起提现 ($" + payout.getAmount() + ")",
                operator, "重新发起失败的提现申请");
        return ApiResponse.success(payout);
    }

    @PostMapping(value = { "/payout/{id}/reject" })
    @Transactional
    public ApiResponse<CommissionPayout> rejectPayout(@PathVariable("id") Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Name", defaultValue = "System") String encodedOperator) {
        CommissionPayout payout = this.payoutRepository.findById(id).orElse(null);
        if (payout == null) {
            return ApiResponse.error("打款记录不存在");
        }
        if (!STATUS_APPROVED.equals(payout.getStatus())) {
            return ApiResponse.error("只能拒绝待打款状态的记录");
        }
        String operator = decodeOperator(encodedOperator);
        String remark = request.get("remark") != null ? request.get("remark").toString() : "";
        payout.setStatus(STATUS_REJECTED);
        payout.setRejectedAt(LocalDateTime.now());
        payout.setRejectedBy(operator);
        payout.setReviewRemark(remark);
        int rows = this.balanceRepository.transferPaidToPending(payout.getInfluencerId(), payout.getAmount());
        if (rows == 0) {
            log.error("驳回余额回退失败: influencerId={}, amount={}", payout.getInfluencerId(), payout.getAmount());
            return ApiResponse.error("余额回退失败，请检查红人余额数据");
        }
        this.payoutRepository.save(payout);
        this.logService.logChange(payout.getInfluencerId(), "提现驳回", "待打款", "审核驳回 ($" + payout.getAmount() + ")",
                operator, remark);
        return ApiResponse.success(payout);
    }

    // ========== 内部工具方法 ==========

    /**
     * 解码 URL 编码的操作员名
     */
    private String decodeOperator(String encoded) {
        try {
            return URLDecoder.decode(encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("Failed to decode operator name: {}", encoded, e);
            return "System";
        }
    }

    /**
     * 从 Map 中安全提取 Long 参数
     */
    private Long getRequiredLong(Map<String, Object> request, String key) {
        Object value = request.get(key);
        if (value == null)
            return null;
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException e) {
            log.warn("参数 {} 格式错误: {}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中安全提取 BigDecimal 参数
     */
    private BigDecimal getRequiredBigDecimal(Map<String, Object> request, String key) {
        Object value = request.get(key);
        if (value == null)
            return null;
        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            log.warn("参数 {} 格式错误: {}", key, value);
            return null;
        }
    }

    /**
     * 从 Map 中安全提取可选 String 参数
     */
    private String getOptionalString(Map<String, Object> request, String key, String defaultValue) {
        Object value = request.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public CommissionOrderController(InfluencerCommissionOrderRepository commissionOrderRepository,
            InfluencerConversionOrderRepository conversionOrderRepository,
            InfluencerBalanceRepository balanceRepository, InfluencerBalanceLogRepository balanceLogRepository,
            CommissionPayoutRepository payoutRepository, CommissionScheduler commissionScheduler,
            InfluencerRepository influencerRepository, InfluencerSampleOrderRepository sampleOrderRepository,
            ObjectMapper objectMapper, InfluencerLogService logService,
            JdbcTemplate jdbcTemplate, UserServiceClient userServiceClient, SystemTagRepository systemTagRepository,
            StorageServiceFactory storageServiceFactory) {
        this.commissionOrderRepository = commissionOrderRepository;
        this.conversionOrderRepository = conversionOrderRepository;
        this.balanceRepository = balanceRepository;
        this.balanceLogRepository = balanceLogRepository;
        this.payoutRepository = payoutRepository;
        this.commissionScheduler = commissionScheduler;
        this.influencerRepository = influencerRepository;
        this.sampleOrderRepository = sampleOrderRepository;
        this.objectMapper = objectMapper;
        this.logService = logService;
        this.jdbcTemplate = jdbcTemplate;
        this.userServiceClient = userServiceClient;
        this.systemTagRepository = systemTagRepository;
        this.storageServiceFactory = storageServiceFactory;
    }
}
