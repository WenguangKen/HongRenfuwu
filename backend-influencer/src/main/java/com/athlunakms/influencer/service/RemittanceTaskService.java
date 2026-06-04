package com.athlunakms.influencer.service;

import com.athlunakms.influencer.client.UserServiceClient;
import com.athlunakms.influencer.dto.RemittanceCreateDto;
import com.athlunakms.influencer.dto.RemittanceFilterDto;
import com.athlunakms.influencer.dto.RemittanceTaskDto;
import com.athlunakms.influencer.entity.RemittanceTask;
import com.athlunakms.influencer.entity.InfluencerPaymentInfo;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.InfluencerPaymentInfoRepository;
import com.athlunakms.influencer.repository.RemittanceTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemittanceTaskService {

    private final RemittanceTaskRepository remittanceTaskRepository;
    private final InfluencerRepository influencerRepository;
    private final InfluencerPaymentInfoRepository paymentInfoRepository;
    private final InfluencerCooperationService cooperationService;
    private final UserServiceClient userServiceClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public RemittanceTaskDto createRemittanceTask(RemittanceCreateDto createDto, String operator) {
        RemittanceTask task = new RemittanceTask();
        BeanUtils.copyProperties(createDto, task);

        String creatorName = "系统";
        if (StringUtils.hasText(operator)) {
            try {
                creatorName = java.net.URLDecoder.decode(operator, "UTF-8");
            } catch (Exception e) {
                creatorName = operator;
            }
        }

        // 生成流水号 HC + 年月日 + 递增
        task.setTaskNo(generateTaskNo());
        // 支持自定义状态，若无则默认为 PENDING_AUDIT
        if (StringUtils.hasText(createDto.getStatus())) {
            try {
                task.setStatus(RemittanceTask.RemittanceStatus.valueOf(createDto.getStatus()));
            } catch (Exception e) {
                task.setStatus(RemittanceTask.RemittanceStatus.PENDING_AUDIT);
            }
        } else {
            task.setStatus(RemittanceTask.RemittanceStatus.PENDING_AUDIT);
        }

        // 注入审核人、打款人（主要为了导入时直接生效）
        if (StringUtils.hasText(createDto.getAuditorName())) {
            task.setAuditorName(createDto.getAuditorName());
            task.setAuditTime(LocalDateTime.now());
        }
        if (StringUtils.hasText(createDto.getPayerName())) {
            task.setPayerName(createDto.getPayerName());
            task.setPayTime(LocalDateTime.now());
        }

        task.setCreatorName(creatorName);

        task = remittanceTaskRepository.save(task);
        cooperationService.syncFromRemittanceTask(task);
        return convertToDto(task);
    }

    private static String lastDate = "";
    private static int lastMaxId = 0;

    private synchronized String generateTaskNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "HC" + dateStr;

        if (dateStr.equals(lastDate) && lastMaxId > 0) {
            lastMaxId++;
        } else {
            String maxNo = remittanceTaskRepository.findMaxTaskNoByPrefix(prefix + "%");
            int nextId = 1;
            if (maxNo != null && maxNo.length() > 10) {
                try {
                    nextId = Integer.parseInt(maxNo.substring(10)) + 1;
                } catch (Exception e) {
                    log.warn("Failed to parse max task no: {}", maxNo);
                }
            }
            lastDate = dateStr;
            lastMaxId = nextId;
        }

        return String.format("%s%03d", prefix, lastMaxId);
    }

    @Transactional(readOnly = true)
    public Page<RemittanceTaskDto> getRemittanceTasks(RemittanceFilterDto filter) {
        // 如果按负责人名称筛选，先查出对应红人 ID
        List<Long> ownerFilterInfluencerIds = null;
        if (StringUtils.hasText(filter.getOwnerName())) {
            try {
                // 通过 UserServiceClient 获取所有用户名称，筛选匹配的
                // 先获取所有有负责人的红人
                List<com.athlunakms.influencer.entity.Influencer> allWithOwner = influencerRepository.findAll().stream()
                    .filter(inf -> inf.getOwnerId() != null)
                    .collect(Collectors.toList());
                java.util.Set<Long> allOwnerIds = allWithOwner.stream()
                    .map(com.athlunakms.influencer.entity.Influencer::getOwnerId)
                    .collect(Collectors.toSet());
                if (!allOwnerIds.isEmpty()) {
                    Map<Long, String> allOwnerNames = userServiceClient.getUserNames(new ArrayList<>(allOwnerIds));
                    // 找到名称匹配的 ownerId
                    java.util.Set<Long> matchedOwnerIds = allOwnerNames.entrySet().stream()
                        .filter(e -> e.getValue() != null && e.getValue().contains(filter.getOwnerName()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
                    // 找到这些负责人对应的红人 ID
                    ownerFilterInfluencerIds = allWithOwner.stream()
                        .filter(inf -> matchedOwnerIds.contains(inf.getOwnerId()))
                        .map(com.athlunakms.influencer.entity.Influencer::getId)
                        .collect(Collectors.toList());
                    if (ownerFilterInfluencerIds.isEmpty()) {
                        ownerFilterInfluencerIds = List.of(-1L);
                    }
                } else {
                    ownerFilterInfluencerIds = List.of(-1L);
                }
            } catch (Exception e) {
                log.warn("Failed to filter by ownerName: {}", filter.getOwnerName(), e);
            }
        }

        Pageable pageable = PageRequest.of(
                filter.getPage() != null ? filter.getPage() : 0,
                filter.getSize() != null ? filter.getSize() : 10,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Long> finalOwnerIds = ownerFilterInfluencerIds;
        Specification<RemittanceTask> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(filter.getTaskNo())) {
                predicates.add(cb.like(root.get("taskNo"), "%" + filter.getTaskNo() + "%"));
            }
            if (filter.getInfluencerId() != null) {
                predicates.add(cb.equal(root.get("influencerId"), filter.getInfluencerId()));
            }
            if (StringUtils.hasText(filter.getStatus()) && !"ALL".equals(filter.getStatus())) {
                try {
                    predicates.add(
                            cb.equal(root.get("status"), RemittanceTask.RemittanceStatus.valueOf(filter.getStatus())));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid status requested: {}", filter.getStatus());
                }
            }
            // 新增筛选条件
            if (StringUtils.hasText(filter.getPaymentMethod())) {
                predicates.add(cb.equal(root.get("paymentMethod"), filter.getPaymentMethod()));
            }
            if (StringUtils.hasText(filter.getAuditorName())) {
                predicates.add(cb.like(root.get("auditorName"), "%" + filter.getAuditorName() + "%"));
            }
            if (StringUtils.hasText(filter.getPayerName())) {
                predicates.add(cb.like(root.get("payerName"), "%" + filter.getPayerName() + "%"));
            }
            if (finalOwnerIds != null && !finalOwnerIds.isEmpty()) {
                predicates.add(root.get("influencerId").in(finalOwnerIds));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<RemittanceTask> page = remittanceTaskRepository.findAll(spec, pageable);

        // 补全红人名称和负责人信息（批量查询）
        List<Long> influencerIds = page.getContent().stream()
                .map(RemittanceTask::getInfluencerId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, com.athlunakms.influencer.entity.Influencer> influencerMap = influencerIds.isEmpty()
                ? new HashMap<>()
                : influencerRepository.findAllById(influencerIds).stream()
                        .collect(Collectors.toMap(i -> i.getId(), i -> i, (a, b) -> a));

        // 批量获取负责人名称（通过 UserServiceClient 跨服务查询）
        java.util.Set<Long> ownerIds = influencerMap.values().stream()
                .map(com.athlunakms.influencer.entity.Influencer::getOwnerId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, String> ownerNameMap;
        if (!ownerIds.isEmpty()) {
            Map<Long, String> resolvedNames = new HashMap<>();
            try {
                resolvedNames = userServiceClient.getUserNames(new ArrayList<>(ownerIds));
            } catch (Exception e) {
                log.warn("Failed to batch-resolve owner names for remittance list", e);
            }
            ownerNameMap = resolvedNames;
        } else {
            ownerNameMap = new HashMap<>();
        }
        Map<Long, String> finalOwnerNameMap = ownerNameMap;

        // 批量查询红人的付款信息，补全缺失 paymentMethod 或 paymentDetails 的任务
        List<Long> needPaymentInfoIds = page.getContent().stream()
                .filter(t -> t.getInfluencerId() != null && 
                        (!StringUtils.hasText(t.getPaymentMethod()) || !StringUtils.hasText(t.getPaymentDetails())))
                .map(RemittanceTask::getInfluencerId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, InfluencerPaymentInfo> paymentInfoMap = new HashMap<>();
        if (!needPaymentInfoIds.isEmpty()) {
            try {
                paymentInfoRepository.findAll().stream()
                    .filter(pi -> needPaymentInfoIds.contains(pi.getInfluencerId()))
                    .forEach(pi -> paymentInfoMap.put(pi.getInfluencerId(), pi));
            } catch (Exception e) {
                log.warn("Failed to batch-load payment info for remittance list", e);
            }
        }

        return page.map(task -> {
            RemittanceTaskDto dto = convertToDto(task);
            if (task.getInfluencerId() != null) {
                com.athlunakms.influencer.entity.Influencer inf = influencerMap.get(task.getInfluencerId());
                if (inf != null) {
                    dto.setInfluencerName(inf.getRealName() != null ? inf.getRealName() : "");
                    dto.setInfluencerEmail(inf.getEmail() != null ? inf.getEmail() : "");
                    // 设置负责人名称
                    if (inf.getOwnerId() != null) {
                        dto.setOwnerName(finalOwnerNameMap.getOrDefault(inf.getOwnerId(), ""));
                    }
                }
                // 补全 paymentMethod 和 paymentDetails（所有 Tab 导出都需要）
                InfluencerPaymentInfo pi = paymentInfoMap.get(task.getInfluencerId());
                if (pi != null) {
                    // 先确定打款方式
                    String method = dto.getPaymentMethod();
                    if (!StringUtils.hasText(method)) {
                        // 自动推断
                        if (StringUtils.hasText(pi.getPaypalAccount()) && 
                            (StringUtils.hasText(pi.getAccountNumber()) || StringUtils.hasText(pi.getBankName()))) {
                            method = "bank_card";
                        } else if (StringUtils.hasText(pi.getPaypalAccount())) {
                            method = "paypal";
                        } else if (StringUtils.hasText(pi.getAccountNumber()) || StringUtils.hasText(pi.getBankName())) {
                            method = "bank_card";
                        }
                        if (method != null) {
                            dto.setPaymentMethod(method);
                        }
                    }
                    // 补全 paymentAccount 和 paymentDetails（如果还没有）
                    if (StringUtils.hasText(method) && !StringUtils.hasText(dto.getPaymentDetails())) {
                        if ("paypal".equals(method)) {
                            if (!StringUtils.hasText(dto.getPaymentAccount())) {
                                dto.setPaymentAccount(pi.getPaypalAccount());
                            }
                            dto.setPaymentDetails("{\"paypalAccount\":\"" + (pi.getPaypalAccount() != null ? pi.getPaypalAccount() : "") + "\"}");
                        } else if ("bank_card".equals(method)) {
                            if (!StringUtils.hasText(dto.getPaymentAccount())) {
                                dto.setPaymentAccount(pi.getAccountNumber());
                            }
                            StringBuilder sb = new StringBuilder("{");
                            sb.append("\"bankCountry\":\"").append(pi.getBankCountry() != null ? pi.getBankCountry() : "").append("\"");
                            sb.append(",\"bankName\":\"").append(pi.getBankName() != null ? pi.getBankName() : "").append("\"");
                            sb.append(",\"branchName\":\"").append(pi.getBranchName() != null ? pi.getBranchName() : "").append("\"");
                            sb.append(",\"bankAddress\":\"").append(pi.getBankAddress() != null ? pi.getBankAddress() : "").append("\"");
                            sb.append(",\"swiftCode\":\"").append(pi.getSwiftCode() != null ? pi.getSwiftCode() : "").append("\"");
                            sb.append(",\"routingNumber\":\"").append(pi.getRoutingNumber() != null ? pi.getRoutingNumber() : "").append("\"");
                            sb.append(",\"accountName\":\"").append(pi.getAccountName() != null ? pi.getAccountName() : "").append("\"");
                            sb.append(",\"accountNumber\":\"").append(pi.getAccountNumber() != null ? pi.getAccountNumber() : "").append("\"");
                            sb.append(",\"beneficiaryAddress\":\"").append(pi.getBeneficiaryAddress() != null ? pi.getBeneficiaryAddress() : "").append("\"");
                            sb.append("}");
                            dto.setPaymentDetails(sb.toString());
                        }
                    }
                }
            }
            return dto;
        });
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStatusCounts(RemittanceFilterDto filter) {
        // 如果按负责人名称筛选，先查出对应红人 ID（与列表查询对齐）
        List<Long> ownerFilterInfluencerIds = null;
        if (StringUtils.hasText(filter.getOwnerName())) {
            try {
                List<com.athlunakms.influencer.entity.Influencer> allWithOwner = influencerRepository.findAll().stream()
                    .filter(inf -> inf.getOwnerId() != null)
                    .collect(Collectors.toList());
                java.util.Set<Long> allOwnerIds = allWithOwner.stream()
                    .map(com.athlunakms.influencer.entity.Influencer::getOwnerId)
                    .collect(Collectors.toSet());
                if (!allOwnerIds.isEmpty()) {
                    Map<Long, String> allOwnerNames = userServiceClient.getUserNames(new ArrayList<>(allOwnerIds));
                    java.util.Set<Long> matchedOwnerIds = allOwnerNames.entrySet().stream()
                        .filter(e -> e.getValue() != null && e.getValue().contains(filter.getOwnerName()))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());
                    ownerFilterInfluencerIds = allWithOwner.stream()
                        .filter(inf -> matchedOwnerIds.contains(inf.getOwnerId()))
                        .map(com.athlunakms.influencer.entity.Influencer::getId)
                        .collect(Collectors.toList());
                    if (ownerFilterInfluencerIds.isEmpty()) {
                        ownerFilterInfluencerIds = List.of(-1L);
                    }
                } else {
                    ownerFilterInfluencerIds = List.of(-1L);
                }
            } catch (Exception e) {
                log.warn("Failed to filter by ownerName in counts: {}", filter.getOwnerName(), e);
            }
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<RemittanceTask> root = query.from(RemittanceTask.class);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(filter.getTaskNo())) {
            predicates.add(cb.like(root.get("taskNo"), "%" + filter.getTaskNo() + "%"));
        }
        if (filter.getInfluencerId() != null) {
            predicates.add(cb.equal(root.get("influencerId"), filter.getInfluencerId()));
        }
        if (StringUtils.hasText(filter.getPaymentMethod())) {
            predicates.add(cb.equal(root.get("paymentMethod"), filter.getPaymentMethod()));
        }
        if (StringUtils.hasText(filter.getAuditorName())) {
            predicates.add(cb.like(root.get("auditorName"), "%" + filter.getAuditorName() + "%"));
        }
        if (StringUtils.hasText(filter.getPayerName())) {
            predicates.add(cb.like(root.get("payerName"), "%" + filter.getPayerName() + "%"));
        }
        if (ownerFilterInfluencerIds != null && !ownerFilterInfluencerIds.isEmpty()) {
            predicates.add(root.get("influencerId").in(ownerFilterInfluencerIds));
        }

        query.multiselect(root.get("status"), cb.count(root))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get("status"));

        List<Object[]> results = entityManager.createQuery(query).getResultList();

        Map<String, Long> counts = new HashMap<>();
        // Initialize with zeros
        for (RemittanceTask.RemittanceStatus s : RemittanceTask.RemittanceStatus.values()) {
            counts.put(s.name(), 0L);
        }

        for (Object[] result : results) {
            if (result[0] != null) {
                counts.put(((RemittanceTask.RemittanceStatus) result[0]).name(), (Long) result[1]);
            }
        }

        return counts;
    }

    @Transactional
    public RemittanceTaskDto auditTask(Long id, com.athlunakms.influencer.dto.RemittanceAuditDto auditDto, Long auditorId, String auditorName) {
        RemittanceTask task = remittanceTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (task.getStatus() != RemittanceTask.RemittanceStatus.PENDING_AUDIT &&
                task.getStatus() != RemittanceTask.RemittanceStatus.PENDING_PAYMENT) {
            throw new RuntimeException("当前状态不可审核/驳回");
        }
        
        boolean approved = "approve".equalsIgnoreCase(auditDto.getAction());
        boolean rejectToAudit = "reject_to_audit".equalsIgnoreCase(auditDto.getAction());

        if (rejectToAudit) {
            // 驳回：从待打款退回到待审核重新审核
            task.setStatus(RemittanceTask.RemittanceStatus.PENDING_AUDIT);
        } else {
            task.setStatus(
                    approved ? RemittanceTask.RemittanceStatus.PENDING_PAYMENT : RemittanceTask.RemittanceStatus.REJECTED);
        }
        task.setAuditorId(auditorId);

        String decodedAuditorName = auditorName;
        if (StringUtils.hasText(auditorName)) {
            try {
                decodedAuditorName = java.net.URLDecoder.decode(auditorName, "UTF-8");
            } catch (Exception e) {
            }
        }
        task.setAuditorName(decodedAuditorName);
        task.setAuditTime(LocalDateTime.now());
        task.setAuditRemark(auditDto.getRemark());
        
        if (StringUtils.hasText(auditDto.getPaymentMethod())) {
            task.setPaymentMethod(auditDto.getPaymentMethod());
        }
        if (StringUtils.hasText(auditDto.getPaymentAccount())) {
            task.setPaymentAccount(auditDto.getPaymentAccount());
        }
        if (StringUtils.hasText(auditDto.getPaymentDetails())) {
            task.setPaymentDetails(auditDto.getPaymentDetails());
        }
        // 允许审核时调整金额
        if (auditDto.getAmount() != null) {
            task.setAmount(auditDto.getAmount());
        }
        if (auditDto.getFee() != null) {
            task.setFee(auditDto.getFee());
        }
        if (auditDto.getTotalAmount() != null) {
            task.setTotalAmount(auditDto.getTotalAmount());
        }

        task = remittanceTaskRepository.save(task);
        cooperationService.syncFromRemittanceTask(task);

        return convertToDto(task);
    }

    @Transactional
    public RemittanceTaskDto payTask(Long id, com.athlunakms.influencer.dto.RemittancePayDto payDto, Long payerId, String payerName) {
        RemittanceTask task = remittanceTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (task.getStatus() != RemittanceTask.RemittanceStatus.PENDING_PAYMENT) {
            throw new RuntimeException("当前状态不可打款");
        }

        task.setStatus(RemittanceTask.RemittanceStatus.PAID);
        task.setVoucherUrl(payDto.getVoucherUrl());
        task.setPayerId(payerId);

        String decodedPayerName = payerName;
        if (StringUtils.hasText(payerName)) {
            try {
                decodedPayerName = java.net.URLDecoder.decode(payerName, "UTF-8");
            } catch (Exception e) {
            }
        }
        task.setPayerName(decodedPayerName);
        // 支持前端传入实际打款时间
        if (StringUtils.hasText(payDto.getPaidAt())) {
            try {
                String raw = payDto.getPaidAt().trim();
                LocalDateTime parsedTime;
                if (raw.length() <= 10) {
                    parsedTime = LocalDate.parse(raw).atStartOfDay();
                } else {
                    parsedTime = LocalDateTime.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                task.setPayTime(parsedTime);
            } catch (Exception e) {
                log.warn("Invalid paidAt format: {}, using now()", payDto.getPaidAt());
                task.setPayTime(LocalDateTime.now());
            }
        } else {
            task.setPayTime(LocalDateTime.now());
        }
        task.setPaymentRemark(payDto.getRemark());
        
        if (StringUtils.hasText(payDto.getPaymentMethod())) {
            task.setPaymentMethod(payDto.getPaymentMethod());
        }
        if (StringUtils.hasText(payDto.getPaymentAccount())) {
            task.setPaymentAccount(payDto.getPaymentAccount());
        }
        if (StringUtils.hasText(payDto.getPaymentDetails())) {
            task.setPaymentDetails(payDto.getPaymentDetails());
        }
        // 允许打款时调整金额
        if (payDto.getAmount() != null) {
            task.setAmount(payDto.getAmount());
        }
        if (payDto.getFee() != null) {
            task.setFee(payDto.getFee());
        }
        if (payDto.getTotalAmount() != null) {
            task.setTotalAmount(payDto.getTotalAmount());
        }

        task = remittanceTaskRepository.save(task);
        cooperationService.syncFromRemittanceTask(task);

        return convertToDto(task);
    }

    @Transactional
    public RemittanceTaskDto updateRemittanceTask(Long id, RemittanceCreateDto updateDto, String operator) {
        RemittanceTask task = remittanceTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));

        if (task.getStatus() != RemittanceTask.RemittanceStatus.PENDING_AUDIT &&
                task.getStatus() != RemittanceTask.RemittanceStatus.REJECTED) {
            throw new RuntimeException("当前状态不可编辑");
        }

        // 更新字段
        BeanUtils.copyProperties(updateDto, task, "id", "taskNo", "status", "creatorId", "creatorName", "createdAt");

        // 如果原本是驳回状态，重置为待审核
        if (task.getStatus() == RemittanceTask.RemittanceStatus.REJECTED) {
            task.setStatus(RemittanceTask.RemittanceStatus.PENDING_AUDIT);
        }

        task = remittanceTaskRepository.save(task);
        cooperationService.syncFromRemittanceTask(task);
        return convertToDto(task);
    }

    @Transactional(readOnly = true)
    public RemittanceTaskDto getDetail(Long id) {
        RemittanceTask task = remittanceTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        return convertToDto(task);
    }

    private RemittanceTaskDto convertToDto(RemittanceTask task) {
        RemittanceTaskDto dto = new RemittanceTaskDto();
        BeanUtils.copyProperties(task, dto);
        dto.setStatus(task.getStatus().name());
        return dto;
    }
}
