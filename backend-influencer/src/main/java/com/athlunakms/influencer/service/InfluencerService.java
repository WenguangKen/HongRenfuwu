package com.athlunakms.influencer.service;

import com.athlunakms.influencer.client.UserServiceClient;
import com.athlunakms.influencer.dto.BatchUpdateDto;
import com.athlunakms.influencer.dto.InfluencerCreateDto;
import com.athlunakms.influencer.dto.InfluencerFilterDto;
import com.athlunakms.influencer.dto.InfluencerImportDto;
import com.athlunakms.influencer.dto.InfluencerListDto;
import com.athlunakms.influencer.dto.InfluencerStatsDto;
import com.athlunakms.influencer.dto.InfluencerUpdateDto;
import com.athlunakms.influencer.dto.SocialMediaDto;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerAddress;
import com.athlunakms.influencer.entity.InfluencerContent;
import com.athlunakms.influencer.entity.InfluencerCooperation;
import com.athlunakms.influencer.entity.InfluencerSampleOrder;
import com.athlunakms.influencer.entity.SocialMedia;
import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.repository.InfluencerAddressRepository;
import com.athlunakms.influencer.repository.InfluencerCommissionOrderRepository;
import com.athlunakms.influencer.repository.InfluencerContentRepository;
import com.athlunakms.influencer.repository.InfluencerConversionOrderRepository;
import com.athlunakms.influencer.repository.InfluencerCooperationRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.InfluencerSampleOrderRepository;
import com.athlunakms.influencer.repository.RemittanceTaskRepository;
import com.athlunakms.influencer.repository.SocialMediaRepository;
import com.athlunakms.influencer.repository.SystemTagRepository;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.persistence.PersistenceContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 红人管理核心服务
 *
 * 职责
 * - 红人 CRUD创建/查询/更新/删除
 * - 批量导入红人
 * - 红人列表分页查询含多条件筛选
 * - 红人详情查询含标签/统计/社媒信息
 * - 批量操作更新负责人/追加标签/替换标签
 * - 活动时间戳更新样品/转化/内容
 * - 历史导入成本同步
 *
 * 依赖InfluencerRepository, SocialMediaRepository, SystemTagRepository 等
 */
@Service
public class InfluencerService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerService.class);
    private final InfluencerRepository influencerRepository;
    private final SocialMediaRepository socialMediaRepository;
    private final InfluencerAddressRepository addressRepository;
    private final SystemTagRepository systemTagRepository;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final InfluencerLogService logService;
    private final InfluencerContentRepository influencerContentRepository;
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final InfluencerConversionOrderRepository conversionOrderRepository;
    private final InfluencerCooperationRepository cooperationRepository;
    private final InfluencerCooperationService cooperationService;
    private final RemittanceTaskRepository remittanceTaskRepository;
    private final InfluencerCommissionOrderRepository influencerCommissionOrderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AiVectorSyncQueueService aiVectorSyncQueueService;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void initDb() {
        try {
            jdbcTemplate.execute("ALTER TABLE influencer ADD COLUMN IF NOT EXISTS is_deleted TINYINT(1) DEFAULT 0 NOT NULL");
            log.info("Successfully ensured is_deleted column exists in influencer table.");
        } catch (Exception e) {
            log.info("is_deleted column check: {}", e.getMessage());
        }
    }

    /**
     * 创建红人
     *
     * 流程创建红人实体 → 保存社媒账号 → 保存地址 → 记录操作日志
     *
     * @param dto      创建红人请求 DTO
     * @param operator 操作人姓名用于日志记录
     * @return 新建红人 ID
     */
    @Transactional
    public Long createInfluencer(InfluencerCreateDto dto, String operator) {
        if (operator == null) {
            operator = "SYS";
        }
        Influencer influencer = new Influencer();
        influencer.setRealName(dto.getRealName() != null ? dto.getRealName().trim().replaceAll("\\s+", " ") : null);
        influencer.setNickName(dto.getNickName() != null ? dto.getNickName().trim().replaceAll("\\s+", " ") : null);
        influencer.setEmail(dto.getEmail());
        influencer.setPhone(dto.getPhone());
        influencer.setCountry(dto.getCountry());
        influencer.setLanguage(dto.getLanguage());
        influencer.setRace(dto.getRace());
        influencer.setGender(dto.getGender());
        influencer.setDescription(dto.getDescription());
        influencer.setOrigin(Influencer.Origin.MANUAL);
        influencer.setIsPaid(dto.getIsPaid());
        influencer.setBrand(dto.getBrand());
        influencer.setSource(dto.getSource());
        influencer.setInfluencerType(dto.getInfluencerType());
        influencer.setOwnerId(dto.getOwnerId());
        influencer.setContactPersonId(dto.getContactPersonId());
        try {
            if (dto.getTagIds() != null) {
                influencer.setTags(this.objectMapper.writeValueAsString(dto.getTagIds()));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tags", e);
        }
        Influencer.Stage stage = Influencer.Stage.POOL;
        Influencer.Status status = Influencer.Status.PENDING;
        if (dto.getInitialStage() != null) {
            try {
                stage = Influencer.Stage.valueOf(dto.getInitialStage());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid initialStage: {}, using default POOL", dto.getInitialStage());
            }
        }
        if (dto.getInitialStatus() != null) {
            try {
                status = Influencer.Status.valueOf(dto.getInitialStatus());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid initialStatus: {}, using default PENDING", dto.getInitialStatus());
            }
        }
        influencer.setStage(stage);
        influencer.setStatus(status);
        influencer.setCommissionRate(dto.getCommissionRate());
        influencer.setPaymentMethod(dto.getPaymentMethod());
        influencer.setPaymentAccount(dto.getPaymentAccount());
        // 备用邮箱
        if (dto.getAuxiliaryEmails() != null) {
            influencer.setBackupEmail(dto.getAuxiliaryEmails());
        }
        Influencer saved = this.influencerRepository.save(influencer);
        if (dto.getSocialMediaList() != null && !dto.getSocialMediaList().isEmpty()) {
            for (SocialMediaDto smDto : dto.getSocialMediaList()) {
                if (!StringUtils.hasText(smDto.getPlatform()) || !StringUtils.hasText(smDto.getHandle()))
                    continue;
                SocialMedia sm = new SocialMedia();
                sm.setInfluencerId(saved.getId());
                sm.setPlatform(smDto.getPlatform());
                sm.setHandle(smDto.getHandle());
                sm.setUrl(smDto.getUrl());
                sm.setFollowerCount(smDto.getFollowerCount() != null ? smDto.getFollowerCount() : 0L);
                sm.setIsDefault(Boolean.TRUE.equals(smDto.getIsDefault()));
                SocialMedia savedSm = this.socialMediaRepository.save(sm);
                if (!sm.getIsDefault())
                    continue;
                saved.setDefaultSocialId(savedSm.getId());
                saved.setDefaultPlatform(savedSm.getPlatform());
                saved.setDefaultHandle(savedSm.getHandle());
                saved.setDefaultUrl(savedSm.getUrl());
                saved.setTotalFans(savedSm.getFollowerCount());
                this.influencerRepository.save(saved);
            }
        } else if (StringUtils.hasText(dto.getDefaultPlatform()) && StringUtils.hasText(dto.getDefaultHandle())) {
            SocialMedia sm = new SocialMedia();
            sm.setInfluencerId(saved.getId());
            sm.setPlatform(dto.getDefaultPlatform());
            sm.setHandle(dto.getDefaultHandle());
            sm.setUrl(dto.getDefaultProfileUrl());
            sm.setFollowerCount(dto.getDefaultFollowerCount() != null ? dto.getDefaultFollowerCount() : 0L);
            sm.setIsDefault(true);
            SocialMedia savedSm = this.socialMediaRepository.save(sm);
            saved.setDefaultSocialId(savedSm.getId());
            saved.setDefaultPlatform(savedSm.getPlatform());
            saved.setDefaultHandle(savedSm.getHandle());
            saved.setDefaultUrl(savedSm.getUrl());
            saved.setTotalFans(savedSm.getFollowerCount());
            this.influencerRepository.save(saved);
        }
        if (StringUtils.hasText(dto.getAddress()) || StringUtils.hasText(dto.getCountry())
                || StringUtils.hasText(dto.getCity())) {
            InfluencerAddress addr = new InfluencerAddress();
            addr.setInfluencerId(saved.getId());
            addr.setAddress(dto.getAddress());
            addr.setCity(dto.getCity());
            addr.setState(dto.getState());
            addr.setStreet1(dto.getStreet1());
            addr.setStreet2(dto.getStreet2());
            addr.setCountry(dto.getCountry());
            addr.setPostalCode(dto.getPostalCode());
            addr.setRecipientName(dto.getRealName());
            addr.setIsDefault(true);
            this.addressRepository.save(addr);
        }
        this.logService.logChange(saved.getId(), "创建", "", dto.getRealName() != null ? dto.getRealName() : "新红人",
                operator, "新建红人记录");
        this.aiVectorSyncQueueService.enqueue(saved.getId());
        return saved.getId();
    }

    /**
     * 分页查询红人列表
     *
     * 支持多条件筛选通过 JPA Specification 动态构建 SQL 查询
     * 批量查询标签、内容数、折扣码数、社媒账号等关联数据避免 N+1 问题
     *
     * @param filter 筛选条件含分页参数
     * @return 分页结果InfluencerListDto 列表
     */
    @Transactional(readOnly = true)
    public Page<InfluencerListDto> getList(InfluencerFilterDto filter) {
        int pageNum = Math.max(0, filter.getPage() - 1);
        int pageSize = filter.getSize() > 0 ? filter.getSize() : 20;
        Specification<Influencer> spec = this.createSpecification(filter);
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Influencer> page = this.influencerRepository.findAll(spec, pageable);
        List<Influencer> content = page.getContent();
        if (content.isEmpty()) {
            return Page.empty(pageable);
        }
        List<Long> influencerIds = content.stream().map(Influencer::getId).collect(Collectors.toList());

        // === 同步：标签解析（纯内存操作，无IO） ===
        HashSet<Long> allTagIds = new HashSet<>();
        Map<Long, List<Long>> influencerTagIdsMap = new HashMap<>();
        for (Influencer entity2 : content) {
            List<Long> tagIds = this.parseTagIds(entity2.getTags());
            influencerTagIdsMap.put(entity2.getId(), tagIds);
            allTagIds.addAll(tagIds);
        }
        Map<Long, String> tagNameMap = new HashMap<>();
        Map<Long, SystemTag> systemTagMap = new HashMap<>();
        if (!allTagIds.isEmpty()) {
            List<SystemTag> tags = this.systemTagRepository.findAllById(allTagIds);
            for (SystemTag tag : tags) {
                tagNameMap.put(tag.getId(), tag.getName());
                systemTagMap.put(tag.getId(), tag);
            }
        }

        // === 并行：7 次 IO 密集型子查询同时执行 ===
        Map<Long, Long> contentCountMap = new HashMap<>();
        Map<Long, Long> sampleCountMap = new HashMap<>();
        Map<Long, LocalDateTime> latestOrderTimeMap = new HashMap<>();
        Map<Long, List<SocialMediaDto>> influencerSocialMediaMap = new HashMap<>();
        Map<Long, BigDecimal> influencerPaymentMap = new HashMap<>();

        HashSet<Long> userIds = new HashSet<>();
        content.forEach(e -> {
            if (e.getOwnerId() != null) userIds.add(e.getOwnerId());
        });

        java.util.concurrent.CompletableFuture<Void> f1 = java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<Object[]> contentCounts = this.influencerContentRepository.countByInfluencerIds(influencerIds);
                for (Object[] row : contentCounts) contentCountMap.put((Long) row[0], (Long) row[1]);
            } catch (Exception ex) { log.error("Failed to fetch content counts", ex); }
        });

        java.util.concurrent.CompletableFuture<Void> f3 = java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<Object[]> sampleCounts = this.sampleOrderRepository.countByInfluencerIds(influencerIds);
                for (Object[] row : sampleCounts) sampleCountMap.put((Long) row[0], (Long) row[1]);
            } catch (Exception ex) { log.error("Failed to fetch sample counts", ex); }
        });

        java.util.concurrent.CompletableFuture<Void> f4 = java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<Object[]> latestOrders = this.sampleOrderRepository.findLatestOrderTimeByInfluencerIds(influencerIds);
                for (Object[] row : latestOrders) latestOrderTimeMap.put((Long) row[0], (LocalDateTime) row[1]);
            } catch (Exception ex) { log.error("Failed to fetch latest order times", ex); }
        });

        java.util.concurrent.CompletableFuture<Void> f5 = java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<SocialMedia> socialMedias = this.socialMediaRepository.findByInfluencerIdIn(influencerIds);
                for (SocialMedia sm : socialMedias) {
                    SocialMediaDto smDto = new SocialMediaDto();
                    smDto.setId(sm.getId());
                    smDto.setPlatform(sm.getPlatform());
                    smDto.setHandle(sm.getHandle());
                    smDto.setUrl(sm.getUrl());
                    smDto.setFollowerCount(sm.getFollowerCount());
                    smDto.setIsDefault(sm.getIsDefault());
                    influencerSocialMediaMap.computeIfAbsent(sm.getInfluencerId(), k -> new ArrayList<>()).add(smDto);
                }
            } catch (Exception ex) { log.warn("Failed to fetch social medias", ex); }
        });

        java.util.concurrent.CompletableFuture<Void> f7 = java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                List<Object[]> sums = this.cooperationRepository.sumAmountByInfluencerIds(influencerIds);
                for (Object[] row : sums) {
                    Long id = (Long) row[0];
                    String currency = (String) row[1];
                    BigDecimal amount = (BigDecimal) row[2];
                    BigDecimal rate = getExchangeRateToUsd(currency);
                    BigDecimal amountInUsd = amount.multiply(rate);
                    influencerPaymentMap.merge(id, amountInUsd, BigDecimal::add);
                }
            } catch (Exception ex) { log.warn("Failed to fetch payment sums", ex); }
        });

        // 用户名通过 RPC 获取（也并行）
        java.util.concurrent.CompletableFuture<Map<Long, String>> fUsers = java.util.concurrent.CompletableFuture.supplyAsync(() ->
            this.userServiceClient.getUserNames(new ArrayList<>(userIds))
        );

        // 等待所有并行任务完成
        try {
            java.util.concurrent.CompletableFuture.allOf(f1, f3, f4, f5, f7, fUsers).join();
        } catch (Exception ex) {
            log.error("Error waiting for parallel queries", ex);
        }
        Map<Long, String> userNames = fUsers.getNow(new HashMap<>());
        return page.map(entity -> {
            InfluencerListDto dto = this.convertToListDto(entity, userNames);
            dto.setHasContent(contentCountMap.getOrDefault(entity.getId(), 0L) > 0L);
            dto.setDiscountCodeCount(0);
            dto.setSampleCount(sampleCountMap.getOrDefault(entity.getId(), 0L).intValue());
            LocalDateTime latestOrderTime = latestOrderTimeMap.get(entity.getId());
            if (latestOrderTime != null) {
                dto.setLastSampleAt(latestOrderTime);
            }
            List<Long> tagIds = influencerTagIdsMap.getOrDefault(entity.getId(), new ArrayList<>());
            dto.setTagIds(tagIds);
            List<String> tagNames = new ArrayList<>();
            for (Long id : tagIds) {
                SystemTag t = systemTagMap.get(id);
                if (t == null)
                    continue;
                tagNames.add(t.getName());
                if (!"LIAISON".equals(t.getCategory()))
                    continue;
                dto.setContactPersonName(t.getName());
            }
            dto.setTags(tagNames);
            dto.setSocialMedias(influencerSocialMediaMap.getOrDefault(entity.getId(), new ArrayList<>()));
            dto.setDiscountCodes(new ArrayList<>());
            dto.setPaymentAmount(influencerPaymentMap.getOrDefault(entity.getId(), BigDecimal.ZERO));
            return dto;
        });
    }

    /**
     * 获取受筛选条件影响的各状态统计数量
     * 
     * @param filter 筛选条件
     * @return 状态名称 -> 数量的映射，包含 "ALL" 总数
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getStatusCounts(InfluencerFilterDto filter) {
        // 创建一个副本避免修改原 filter (虽然在当前 Controller 调用链中修改也无妨)
        Influencer.Status originalStatus = filter.getStatus();
        filter.setStatus(null); // 清除状态筛选以统计所有状态

        CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root<Influencer> root = query.from(Influencer.class);

        Specification<Influencer> spec = this.createSpecification(filter);
        Predicate predicate = spec.toPredicate(root, query, cb);

        query.multiselect(root.get("status"), cb.count(root));
        query.where(predicate);
        query.groupBy(root.get("status"));

        List<Object[]> results = this.entityManager.createQuery(query).getResultList();

        Map<String, Long> countMap = new HashMap<>();
        // 恢复原状态（好习惯）
        filter.setStatus(originalStatus);

        long total = 0;
        for (Object[] result : results) {
            Influencer.Status status = (Influencer.Status) result[0];
            Long count = (Long) result[1];
            if (status != null) {
                countMap.put(status.name(), count);
                total += count;
            }
        }
        countMap.put("ALL", total);

        return countMap;
    }

    /**
     * 解析标签 JSON 字符串为 ID 列表
     * 标签在数据库中以 JSON 数组格式存储如 "[1, 2, 3]"
     *
     * @param tagsJson JSON 格式的标签 ID 数组字符串
     * @return 标签 ID 列表解析失败返回空列表
     */
    private List<Long> parseTagIds(String tagsJson) {
        if (!StringUtils.hasText(tagsJson)) {
            return new ArrayList<>();
        }
        try {
            List<?> rawList = this.objectMapper.readValue(tagsJson,
                    this.objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
            return rawList.stream().filter(Objects::nonNull).map(obj -> {
                if (obj instanceof Number) {
                    return ((Number) obj).longValue();
                }
                try {
                    return Long.parseLong(obj.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse tags JSON: {}", tagsJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据 ID 获取红人详情含地址和社媒列表
     *
     * @param id 红人 ID
     * @return 红人实体已关联地址和社媒信息
     * @throws RuntimeException 红人不存在时抛出异常
     */
    @Transactional(readOnly = true)
    public Influencer getById(Long id) {
        Influencer influencer = this.influencerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Influencer not found"));
        List<InfluencerAddress> addresses = this.addressRepository.findByInfluencerId(id);
        if (!addresses.isEmpty()) {
            InfluencerAddress defaultAddr = addresses.stream().filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
                    .findFirst().orElse(addresses.get(0));
            influencer.setAddressInfo(defaultAddr);
        }
        influencer.setSocialMediaList(this.socialMediaRepository.findByInfluencerId(id));
        return influencer;
    }

    /**
     * 获取红人详情含标签/统计/负责人等完整信息
     *
     * 返回结构包含
     * - influencer: 红人基本信息
     * - tagIds/tagNames/tags: 标签相关信息
     * - ownerName/contactPersonName: 负责人/联络人姓名
     * - stats: 内容数/样品数/订单数/GMV/佣金统计
     *
     * @param id 红人 ID
     * @return 包含完整详情的 Map
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDetailWithTags(Long id) {
        Influencer influencer = this.getById(id);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("influencer", influencer);
        result.put("auxiliaryEmails", influencer.getBackupEmail());
        ArrayList<Long> userIds = new ArrayList<Long>();
        if (influencer.getOwnerId() != null) {
            userIds.add(influencer.getOwnerId());
        }
        if (influencer.getContactPersonId() != null) {
            userIds.add(influencer.getContactPersonId());
        }
        if (!userIds.isEmpty()) {
            Map<Long, String> names = this.userServiceClient.getUserNames(userIds);
            result.put("ownerName", names.getOrDefault(influencer.getOwnerId(), ""));
            result.put("contactPersonName", names.getOrDefault(influencer.getContactPersonId(), ""));
        }
        InfluencerStatsDto stats = InfluencerStatsDto.builder()
                .totalContentCount(this.influencerContentRepository.countByInfluencerId(id))
                .totalSampleCount(this.sampleOrderRepository.countByInfluencerId(id)).totalOrderCount(0L)
                .totalGmv(BigDecimal.ZERO).estimatedCommission(BigDecimal.ZERO).build();
        List<Object[]> conversionAgg = this.conversionOrderRepository.getAggregatedStatsByInfluencerId(id);
        if (!conversionAgg.isEmpty() && conversionAgg.get(0) != null) {
            Object[] row = conversionAgg.get(0);
            if (row[0] != null) {
                stats.setTotalOrderCount((Long) row[0]);
            }
            if (row[1] != null) {
                stats.setTotalGmv((BigDecimal) row[1]);
            }
            if (row[2] != null) {
                stats.setEstimatedCommission((BigDecimal) row[2]);
            }
        }
        result.put("stats", stats);
        List<Long> tagIds = this.parseTagIds(influencer.getTags());
        result.put("tagIds", tagIds);
        if (!tagIds.isEmpty()) {
            List<SystemTag> tags = this.systemTagRepository.findAllById(tagIds);
            List<Map<String, Object>> tagList = tags.stream().filter(tag -> tag != null && tag.getName() != null)
                    .map(tag -> {
                        HashMap<String, Object> tagMap = new HashMap<String, Object>();
                        tagMap.put("id", tag.getId());
                        tagMap.put("name", tag.getName());
                        tagMap.put("backgroundColor", tag.getBackgroundColor());
                        tagMap.put("borderColor", tag.getBorderColor());
                        tagMap.put("textColor", tag.getTextColor());
                        return tagMap;
                    }).collect(Collectors.toList());
            result.put("tags", tagList);
            result.put("tagNames", tags.stream().map(SystemTag::getName).filter(name -> name != null && !name.isEmpty())
                    .collect(Collectors.toList()));
        } else {
            result.put("tags", new ArrayList());
            result.put("tagNames", new ArrayList());
        }
        return result;
    }

    /**
     * 字段变更日志记录辅助方法
     * 仅当字段值实际发生变化时才记录日志
     *
     * @param id          红人 ID
     * @param fieldNameCh 字段中文名称用于日志展示
     * @param oldVal      旧值
     * @param newVal      新值null 时不记录
     * @param operator    操作人
     */
    private void logIfChanged(Long id, String fieldNameCh, Object oldVal, Object newVal, String operator) {
        String newStr;
        if (newVal == null) {
            return;
        }
        if (oldVal != null && oldVal.equals(newVal)) {
            return;
        }
        if (oldVal == null && "".equals(newVal)) {
            return;
        }
        String oldStr = oldVal == null ? "-" : String.valueOf(oldVal);
        if (oldStr.equals(newStr = String.valueOf(newVal))) {
            return;
        }
        this.logService.logChange(id, fieldNameCh, oldStr, newStr, operator, "修改基本信息");
    }

    /**
     * 更新红人信息
     *
     * 支持乐观锁版本检查逐字段比对并记录变更日志
     * 涉及字段真实姓名、昵称、国家、来源、类型、分佣、付费状态、品牌、联系方式、人种等
     *
     * @param dto      更新请求 DTO
     * @param operator 操作人姓名
     * @throws ObjectOptimisticLockingFailureException 版本冲突时抛出
     */
    @Transactional
    public void updateInfluencer(InfluencerUpdateDto dto, String operator) {
        Influencer existing = this.influencerRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Influencer not found"));
        if (operator == null) {
            operator = "SYS";
        }
        // 乐观锁检查
        if (dto.getVersion() != null && !dto.getVersion().equals(existing.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Influencer.class.getName(), existing.getId());
        }

        // --- 核心字段变更比对与记录 ---
        this.logIfChanged(existing.getId(), "真实姓名", existing.getRealName(), dto.getRealName(), operator);
        if (dto.getRealName() != null) {
            // 规范化空格：去除首尾空格并合并多余空格
            existing.setRealName(dto.getRealName().trim().replaceAll("\\s+", " "));
        }

        this.logIfChanged(existing.getId(), "昵称", existing.getNickName(), dto.getNickName(), operator);
        if (dto.getNickName() != null) {
            // 规范化空格：去除首尾空格并合并多余空格
            existing.setNickName(dto.getNickName().trim().replaceAll("\\s+", " "));
        }

        this.logIfChanged(existing.getId(), "国家", existing.getCountry(), dto.getCountry(), operator);
        if (dto.getCountry() != null) {
            existing.setCountry(dto.getCountry());
        }

        this.logIfChanged(existing.getId(), "红人来源", existing.getSource(), dto.getSource(), operator);
        if (dto.getSource() != null) {
            existing.setSource(dto.getSource());
        }

        this.logIfChanged(existing.getId(), "红人类型", existing.getInfluencerType(), dto.getInfluencerType(), operator);
        if (dto.getInfluencerType() != null) {
            existing.setInfluencerType(dto.getInfluencerType());
        }

        // 分佣比例特殊处理BigDecimal 比较
        if (dto.getCommissionRate() != null) {
            BigDecimal oldRate = existing.getCommissionRate();
            BigDecimal newRate = dto.getCommissionRate();
            if (oldRate == null || oldRate.compareTo(newRate) != 0) {
                String oldStr = oldRate == null ? "0" : oldRate.toString();
                String newStr = newRate.toString();
                this.logService.logChange(existing.getId(), "分佣比例", oldStr, newStr, operator, "手动调整");
            }
            existing.setCommissionRate(dto.getCommissionRate());
        }

        this.logIfChanged(existing.getId(), "是否付费的合作", existing.getIsPaid(), dto.getIsPaid(), operator);
        if (dto.getIsPaid() != null) {
            existing.setIsPaid(dto.getIsPaid());
        }

        this.logIfChanged(existing.getId(), "合作品牌", existing.getBrand(), dto.getBrand(), operator);
        if (dto.getBrand() != null) {
            existing.setBrand(dto.getBrand());
        }

        this.logIfChanged(existing.getId(), "邮箱", existing.getEmail(), dto.getEmail(), operator);
        if (dto.getEmail() != null) {
            existing.setEmail(dto.getEmail());
        }

        this.logIfChanged(existing.getId(), "手机号", existing.getPhone(), dto.getPhone(), operator);
        if (dto.getPhone() != null) {
            existing.setPhone(dto.getPhone());
        }

        this.logIfChanged(existing.getId(), "人种", existing.getRace(), dto.getRace(), operator);
        if (dto.getRace() != null) {
            existing.setRace(dto.getRace());
        }

        this.logIfChanged(existing.getId(), "性别", existing.getGender(), dto.getGender(), operator);
        if (dto.getGender() != null) {
            existing.setGender(dto.getGender());
        }

        this.logIfChanged(existing.getId(), "语言", existing.getLanguage(), dto.getLanguage(), operator);
        if (dto.getLanguage() != null) {
            existing.setLanguage(dto.getLanguage());
        }

        if (dto.getAvatarUrl() != null) {
            existing.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getDescription() != null) {
            existing.setDescription(dto.getDescription());
        }

        this.logIfChanged(existing.getId(), "打款方式", existing.getPaymentMethod(), dto.getPaymentMethod(), operator);
        if (dto.getPaymentMethod() != null) {
            existing.setPaymentMethod(dto.getPaymentMethod());
        }
        this.logIfChanged(existing.getId(), "打款账号", existing.getPaymentAccount(), dto.getPaymentAccount(), operator);
        if (dto.getPaymentAccount() != null) {
            existing.setPaymentAccount(dto.getPaymentAccount());
        }

        // 辅助邮箱
        if (dto.getAuxiliaryEmails() != null) {
            existing.setBackupEmail(dto.getAuxiliaryEmails());
        }

        // 标签变更记录
        if (dto.getTagIds() != null) {
            try {
                String oldTags = existing.getTags();
                String newTags = this.objectMapper.writeValueAsString(dto.getTagIds());
                if (oldTags == null || !oldTags.equals(newTags)) {
                    this.logService.logChange(existing.getId(), "标签配置", oldTags != null ? oldTags : "-", newTags,
                            operator, "修改标签");
                }
                existing.setTags(newTags);
            } catch (Exception e) {
                log.warn("Failed to serialize tags for influencer {}: {}", existing.getId(), e.getMessage());
            }
        }

        // 负责人变更
        if (dto.getOwnerId() != null) {
            if (existing.getOwnerId() == null || !existing.getOwnerId().equals(dto.getOwnerId())) {
                this.logService.logChange(existing.getId(), "负责人划转", String.valueOf(existing.getOwnerId()),
                        String.valueOf(dto.getOwnerId()), operator, "变更负责人");
            }
            existing.setOwnerId(dto.getOwnerId());
        }

        // 联络人变更
        if (dto.getContactPersonId() != null) {
            if (existing.getContactPersonId() == null
                    || !existing.getContactPersonId().equals(dto.getContactPersonId())) {
                this.logService.logChange(existing.getId(), "联络人交接", String.valueOf(existing.getContactPersonId()),
                        String.valueOf(dto.getContactPersonId()), operator, "变更联系人");
            }
            existing.setContactPersonId(dto.getContactPersonId());
        }
        this.influencerRepository.save(existing);
        this.aiVectorSyncQueueService.enqueue(existing.getId());
    }

    /**
     * 更新红人活动时间戳
     * 当红人产生新的业务活动样品/转化/内容时调用
     *
     * @param id           红人 ID
     * @param activityType 活动类型sample/conversion/content
     */
    @Transactional
    public void updateActivityTimestamp(Long id, String activityType) {
        Influencer influencer = this.influencerRepository.findById(id).orElse(null);
        if (influencer == null) {
            log.warn("Influencer not found for activity update: {}", id);
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        switch (activityType.toLowerCase()) {
            case "sample": {
                influencer.setLastSampleAt(now);
                log.info("Updated lastSampleAt for influencer {}", id);
                break;
            }
            case "conversion": {
                influencer.setLastConversionAt(now);
                log.info("Updated lastConversionAt for influencer {}", id);
                break;
            }
            case "content": {
                influencer.setLastContentAt(now);
                log.info("Updated lastContentAt for influencer {}", id);
                break;
            }
            default: {
                log.warn("Unknown activity type: {}", activityType);
                return;
            }
        }
        influencer.setLastActivityAt(now);
        this.influencerRepository.save(influencer);
    }

    /** 将红人实体转换为列表展示 DTO */
    private InfluencerListDto convertToListDto(Influencer entity, Map<Long, String> userNames) {
        InfluencerListDto dto = new InfluencerListDto();
        dto.setId(entity.getId());
        dto.setUniqueName(entity.getRealName());
        dto.setRealName(entity.getRealName());
        dto.setNickName(entity.getNickName());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setCountry(entity.getCountry());
        dto.setRace(entity.getRace());
        dto.setPlatformName(entity.getDefaultPlatform());
        dto.setDefaultHandle(entity.getDefaultHandle());
        dto.setProfileLink(entity.getDefaultUrl());
        dto.setFollowerCount(entity.getTotalFans());
        dto.setStage(entity.getStage());
        dto.setStatus(entity.getStatus());
        dto.setBrand(entity.getBrand());
        dto.setIsPaid(entity.getIsPaid());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        if (entity.getOrigin() != null) {
            dto.setSource(entity.getOrigin().name());
        }
        dto.setInfluencerType(entity.getInfluencerType());
        dto.setCommissionRate(entity.getCommissionRate());
        dto.setTotalOrders(entity.getTotalOrders());
        dto.setTotalFans(Integer.valueOf(entity.getTotalFans() != null ? entity.getTotalFans().intValue() : 0));
        dto.setOwnerId(entity.getOwnerId());
        dto.setOwnerName(userNames.getOrDefault(entity.getOwnerId(), ""));
        dto.setContactPersonId(null);
        dto.setContactPersonName(null);
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastSampleAt(entity.getLastSampleAt());
        dto.setLastConversionAt(entity.getLastConversionAt());
        dto.setDescription(entity.getDescription());
        dto.setBackupEmail(entity.getBackupEmail());
        dto.setSourceValue(entity.getSourceValue());
        dto.setAuditorName(entity.getAuditorName());
        dto.setAuditTime(entity.getAuditTime());
        return dto;
    }

    /**
     * 同步历史导入成本到付费合作记录
     * 从红人描述字段中提取 "Import Cost: xxx" 格式的金额
     * 自动创建对应的付费合作记录幂等不重复创建
     *
     * @param influencers 需要同步的红人列表
     */
    @Transactional
    public void syncHistoricalImportCosts(List<Influencer> influencers) {
        if (influencers == null || influencers.isEmpty()) {
            return;
        }
        Pattern pattern = Pattern.compile("Import Cost: ([0-9.]+)");
        for (Influencer influencer : influencers) {
            String desc = influencer.getDescription();
            if (desc == null || !desc.contains("Import Cost:"))
                continue;
            Matcher matcher = pattern.matcher(desc);
            while (matcher.find()) {
                try {
                    BigDecimal amount = new BigDecimal(matcher.group(1));
                    if (this.cooperationRepository.existsByInfluencerIdAndAmount(influencer.getId(), amount))
                        continue;
                    InfluencerCooperation coop = new InfluencerCooperation();
                    coop.setInfluencerId(influencer.getId());
                    coop.setCooperationMode("PAID");
                    coop.setAmount(amount);
                    coop.setCurrency("USD");
                    coop.setPaymentMethod("Other");
                    coop.setPaidAt(influencer.getCreatedAt());
                    coop.setRemark("Legacy Import Sync");
                    this.cooperationService.createCooperation(coop);
                    log.info("Synced legacy import cost {} to cooperation for influencer {}", amount,
                            influencer.getId());
                } catch (Exception e) {
                    log.warn("Failed to sync legacy cost for influencer {}: {}", influencer.getId(), e.getMessage());
                }
            }
        }
    }

    /**
     * 软删除红人
     * 替换为基于 is_deleted 的软删除并添加财务记录强校验拦截
     *
     * @param id 红人 ID
     */
    @Transactional
    public void deleteInfluencer(Long id) {
        if (this.remittanceTaskRepository.existsByInfluencerId(id) || 
            this.influencerCommissionOrderRepository.existsByInfluencerId(id)) {
            throw new RuntimeException("该红人名下存在财务结算或打款记录，为保证数据一致性，严禁删除此红人");
        }
        
        Influencer influencer = this.influencerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("红人不存在"));
            
        influencer.setIsDeleted(true);
        this.influencerRepository.save(influencer);
        
        this.logService.logChange(id, "删除", "存在", "已删除", "SYS", "删除红人记录(软删除)");
        log.info("Soft-deleted influencer ID: {}", id);
    }

    /** 批量删除红人逐个调用 deleteInfluencer */
    @Transactional
    public void batchDelete(List<Long> ids) {
        for (Long id : ids) {
            this.deleteInfluencer(id);
        }
    }

    /**
     * 构建 JPA 动态查询条件 (Specification)
     *
     * 采用模块化设计将不同维度的筛选逻辑拆分为独立的辅助方法提升代码可读性
     */
    private Specification<Influencer> createSpecification(InfluencerFilterDto filter) {
        return (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            // 1. 基础关键词搜索
            this.addSearchKeyPredicate(predicates, root, cb, filter.getSearchKey());
            if (filter.getNames() != null && !filter.getNames().isEmpty()) {
                List<Predicate> namePredicates = new ArrayList<>();
                for (String name : filter.getNames()) {
                    if (name.startsWith("#")) {
                        try {
                            Long id = Long.parseLong(name.substring(1));
                            namePredicates.add(cb.equal(root.get("id"), id));
                        } catch (NumberFormatException e) {
                            // 忽略无效ID
                        }
                    } else {
                        String noSpaceName = name.replaceAll("\\s+", "");
                        String likePattern = "%" + noSpaceName + "%";
                        jakarta.persistence.criteria.Expression<String> realNameNoSpace = cb.function("REPLACE", String.class, root.get("realName"), cb.literal(" "), cb.literal(""));
                        jakarta.persistence.criteria.Expression<String> nickNameNoSpace = cb.function("REPLACE", String.class, root.get("nickName"), cb.literal(" "), cb.literal(""));
                        namePredicates.add(cb.like(realNameNoSpace, likePattern));
                        namePredicates.add(cb.like(nickNameNoSpace, likePattern));
                    }
                }
                if (!namePredicates.isEmpty()) {
                    predicates.add(cb.or(namePredicates.toArray(new Predicate[0])));
                }
            }

            // 2. 核心状态筛选
            if (filter.getStage() != null)
                predicates.add(cb.equal(root.get("stage"), filter.getStage()));
            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            if (StringUtils.hasText(filter.getCountry()))
                predicates.add(cb.equal(root.get("country"), filter.getCountry()));

            // 3. 平台与社媒筛选
            this.addPlatformPredicate(predicates, root, cb, filter.getPlatform());
            this.addSocialPlatformPredicate(predicates, root, query, cb, filter.getSocialPlatform());

            // 4. 联系信息（包含辅助邮箱搜索）
            if (StringUtils.hasText(filter.getEmail())) {
                String emailPattern = "%" + filter.getEmail() + "%";
                predicates.add(cb.or(
                        cb.like(root.get("email"), emailPattern),
                        cb.like(root.get("backupEmail"), emailPattern)));
            }
            if (filter.getEmails() != null && !filter.getEmails().isEmpty()) {
                // 精确匹配主邮箱，或辅助邮箱包含目标邮箱
                List<Predicate> emailPredicates = new ArrayList<>();
                emailPredicates.add(root.get("email").in(filter.getEmails()));
                for (String em : filter.getEmails()) {
                    emailPredicates.add(cb.like(root.get("backupEmail"), "%" + em + "%"));
                }
                predicates.add(cb.or(emailPredicates.toArray(new Predicate[0])));
            }

            // 5. 来源、类型、族群
            if (StringUtils.hasText(filter.getOrigin())) {
                try {
                    Influencer.Origin originEnumValue = Influencer.Origin.valueOf(filter.getOrigin().toUpperCase());
                    predicates.add(cb.equal(root.get("origin"), originEnumValue));
                } catch (IllegalArgumentException e) {
                    /* 忽略非法来源 */ }
            }
            if (filter.getInfluencerType() != null && !filter.getInfluencerType().isEmpty()) {
                predicates.add(root.get("influencerType").in(filter.getInfluencerType()));
            }
            if (filter.getRace() != null && !filter.getRace().isEmpty()) {
                predicates.add(root.get("race").in(filter.getRace()));
            }

            // 6. 社媒账号与链接搜索
            this.addProfileLinkPredicate(predicates, root, query, cb, filter.getProfileLink());
            this.addSocialHandlePredicate(predicates, root, query, cb, filter.getSocialHandle());

            // 7. 业务属性
            if (filter.getIsPaid() != null)
                predicates.add(cb.equal(root.get("isPaid"), filter.getIsPaid()));
            if (StringUtils.hasText(filter.getBrand())) {
                predicates.add(cb.like(root.get("brand"), "%" + filter.getBrand() + "%"));
            }
            if (filter.getOwnerIds() != null && !filter.getOwnerIds().isEmpty()) {
                predicates.add(root.get("ownerId").in(filter.getOwnerIds()));
            }
            if (filter.getContactPersonIds() != null && !filter.getContactPersonIds().isEmpty()) {
                predicates.add(root.get("contactPersonId").in(filter.getContactPersonIds()));
            }

            // 8. 内容输出
            this.addContentOutputPredicate(predicates, root, query, cb, filter.getHasOutputContent());

            // 9. 样品单统计筛选
            this.addSampleCountPredicate(predicates, root, query, cb, filter);

            // 10. 标签筛选
            this.addTagPredicate(predicates, root, cb, filter.getTagIds(), "tags");
            this.addTagPredicate(predicates, root, cb, filter.getLiaisonTagIds(), "tags"); // 备注此处沿用
                                                                                           // filters.liaisonTagIds 的逻辑

            // 11. 粉丝数与时间区间
            if (filter.getFanRangeMin() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalFans"), filter.getFanRangeMin()));
            if (filter.getFanRangeMax() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("totalFans"), filter.getFanRangeMax()));
            this.addTimeRangePredicate(predicates, root, cb, filter);

            // 12. 空白字段筛选
            if (filter.getBlankFields() != null && !filter.getBlankFields().isEmpty()) {
                for (String field : filter.getBlankFields()) {
                    switch (field) {
                        case "name":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("realName")),
                                    cb.equal(root.get("realName"), "")));
                            break;
                        case "email":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("email")),
                                    cb.equal(root.get("email"), "")));
                            break;
                        case "country":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("country")),
                                    cb.equal(root.get("country"), "")));
                            break;
                        case "race":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("race")),
                                    cb.equal(root.get("race"), "")));
                            break;
                        case "influencerType":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("influencerType")),
                                    cb.equal(root.get("influencerType"), "")));
                            break;
                        case "owner":
                            predicates.add(cb.isNull(root.get("ownerId")));
                            break;
                        case "contactPerson":
                            Predicate noContactPersonId = cb.isNull(root.get("contactPersonId"));
                            List<com.athlunakms.influencer.entity.SystemTag> liaisonTags = this.systemTagRepository
                                    .findByCategoryAndEnabledTrue("LIAISON", org.springframework.data.domain.Sort.unsorted());
                            if (liaisonTags.isEmpty()) {
                                predicates.add(noContactPersonId);
                            } else {
                                List<Predicate> notLikePredicates = new ArrayList<>();
                                for (com.athlunakms.influencer.entity.SystemTag tag : liaisonTags) {
                                    notLikePredicates.add(cb.notLike(root.get("tags"), "%" + tag.getId() + "%"));
                                }
                                Predicate tagsNotLike = cb.and(notLikePredicates.toArray(new Predicate[0]));
                                Predicate tagsIsNull = cb.isNull(root.get("tags"));
                                Predicate tagsIsEmpty = cb.equal(root.get("tags"), "");
                                Predicate tagsEmptyOrNotLike = cb.or(tagsIsNull, tagsIsEmpty, tagsNotLike);
                                predicates.add(cb.and(noContactPersonId, tagsEmptyOrNotLike));
                            }
                            break;
                        case "platform":
                            predicates.add(cb.or(
                                    cb.isNull(root.get("platform")),
                                    cb.equal(root.get("platform"), "")));
                            break;
                        case "socialPlatform":
                            // 没有社媒账号记录
                            Subquery<Long> smSub = query.subquery(Long.class);
                            Root<SocialMedia> smBlankRoot = smSub.from(SocialMedia.class);
                            smSub.select(smBlankRoot.get("influencerId"))
                                    .where(cb.equal(smBlankRoot.get("influencerId"), root.get("id")));
                            predicates.add(cb.not(cb.exists(smSub)));
                            break;
                        case "socialHandle":
                            // 没有社媒账号记录（同 socialPlatform）
                            Subquery<Long> shSub = query.subquery(Long.class);
                            Root<SocialMedia> shBlankRoot = shSub.from(SocialMedia.class);
                            shSub.select(shBlankRoot.get("influencerId"))
                                    .where(cb.equal(shBlankRoot.get("influencerId"), root.get("id")));
                            predicates.add(cb.not(cb.exists(shSub)));
                            break;
                        default:
                            break;
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addSearchKeyPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.CriteriaBuilder cb, String searchKey) {
        if (StringUtils.hasText(searchKey)) {
            if (searchKey.startsWith("#")) {
                try {
                    Long id = Long.parseLong(searchKey.substring(1));
                    predicates.add(cb.equal(root.get("id"), id));
                    return;
                } catch (NumberFormatException e) {
                    // fall back to normal search
                }
            }
            String noSpaceKey = "%" + searchKey.replaceAll("\\s+", "") + "%";
            jakarta.persistence.criteria.Expression<String> realNameNoSpace = cb.function("REPLACE", String.class, root.get("realName"), cb.literal(" "), cb.literal(""));
            jakarta.persistence.criteria.Expression<String> nickNameNoSpace = cb.function("REPLACE", String.class, root.get("nickName"), cb.literal(" "), cb.literal(""));
            jakarta.persistence.criteria.Expression<String> emailNoSpace = cb.function("REPLACE", String.class, root.get("email"), cb.literal(" "), cb.literal(""));
            jakarta.persistence.criteria.Expression<String> backupEmailNoSpace = cb.function("REPLACE", String.class, root.get("backupEmail"), cb.literal(" "), cb.literal(""));
            
            jakarta.persistence.criteria.Expression<String> defaultHandleNoSpace = cb.function("REPLACE", String.class, root.get("defaultHandle"), cb.literal(" "), cb.literal(""));
            predicates.add(cb.or(
                    cb.like(realNameNoSpace, noSpaceKey),
                    cb.like(nickNameNoSpace, noSpaceKey),
                    cb.like(emailNoSpace, noSpaceKey),
                    cb.like(backupEmailNoSpace, noSpaceKey),
                    cb.like(defaultHandleNoSpace, noSpaceKey)));
        }
    }

    private void addPlatformPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.CriteriaBuilder cb, List<String> platforms) {
        if (platforms != null && !platforms.isEmpty()) {
            List<String> upperPlatforms = platforms.stream().map(String::toUpperCase).collect(Collectors.toList());
            predicates.add(cb.or(
                    root.get("defaultPlatform").in(upperPlatforms),
                    root.get("platform").in(upperPlatforms)));
        }
    }

    private void addSocialPlatformPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.AbstractQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb,
            List<String> socialPlatforms) {
        if (socialPlatforms != null && !socialPlatforms.isEmpty()) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SocialMedia> smRoot = subquery.from(SocialMedia.class);
            subquery.select(smRoot.get("influencerId")).where(smRoot.get("platform").in(socialPlatforms));
            predicates.add(root.get("id").in(subquery));
        }
    }

    private void addProfileLinkPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.AbstractQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb,
            String profileLink) {
        if (StringUtils.hasText(profileLink)) {
            String link = "%" + profileLink.trim().toLowerCase() + "%";
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SocialMedia> smRoot = subquery.from(SocialMedia.class);
            subquery.select(smRoot.get("influencerId")).where(cb.like(cb.lower(smRoot.get("url")), link));
            predicates.add(cb.or(cb.like(cb.lower(root.get("defaultUrl")), link), root.get("id").in(subquery)));
        }
    }

    private void addSocialHandlePredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.AbstractQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb,
            String socialHandle) {
        if (StringUtils.hasText(socialHandle)) {
            // 支持通过逗号、空格或换行等分隔符解析大批量从Excel粘贴的账号
            String[] handles = socialHandle.split("[,\\n\\s]+");
            List<String> validHandles = new ArrayList<>();
            for (String h : handles) {
                String trimmed = h.trim();
                if (!trimmed.isEmpty()) {
                    validHandles.add(trimmed.toLowerCase());
                }
            }
            
            if (!validHandles.isEmpty()) {
                if (validHandles.size() <= 5) {
                    // 少量 handle 仍用子查询 + LIKE（模糊搜索场景）
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<SocialMedia> smRoot = subquery.from(SocialMedia.class);
                    subquery.select(smRoot.get("influencerId"));
                    List<Predicate> likePredicates = new ArrayList<>();
                    for (String handle : validHandles) {
                        likePredicates.add(cb.like(smRoot.get("handle"), "%" + handle + "%"));
                    }
                    subquery.where(cb.or(likePredicates.toArray(new Predicate[0])));
                    predicates.add(root.get("id").in(subquery));
                } else {
                    // 大批量：先在应用层通过原生 SQL 查出 influencer_id，再用 IN 过滤
                    // 避免 Hibernate 将 IN 参数展开为 N 个独立子查询导致性能灾难
                    String placeholders = validHandles.stream().map(h -> "?").collect(Collectors.joining(","));
                    String sql = "SELECT DISTINCT influencer_id FROM influencer_social_media WHERE handle IN (" + placeholders + ")";
                    List<Long> influencerIds = this.jdbcTemplate.queryForList(sql, Long.class, validHandles.toArray());
                    
                    if (influencerIds.isEmpty()) {
                        predicates.add(cb.disjunction()); // 0=1 无结果
                    } else {
                        // 分批 IN，避免超过数据库参数限制
                        List<Predicate> inPredicates = new ArrayList<>();
                        int chunkSize = 1000;
                        for (int i = 0; i < influencerIds.size(); i += chunkSize) {
                            List<Long> chunk = influencerIds.subList(i, Math.min(influencerIds.size(), i + chunkSize));
                            inPredicates.add(root.get("id").in(chunk));
                        }
                        predicates.add(cb.or(inPredicates.toArray(new Predicate[0])));
                    }
                }
            }
        }
    }

    private void addContentOutputPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.AbstractQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb,
            Boolean hasOutput) {
        if (hasOutput != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<InfluencerContent> contentRoot = subquery.from(InfluencerContent.class);
            subquery.select(cb.literal(1L)).where(cb.equal(contentRoot.get("influencerId"), root.get("id")));
            predicates.add(hasOutput ? cb.exists(subquery) : cb.not(cb.exists(subquery)));
        }
    }

    private void addSampleCountPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.AbstractQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder cb,
            InfluencerFilterDto filter) {
        if (filter.getMinSampleCount() != null || filter.getMaxSampleCount() != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<InfluencerSampleOrder> sampleRoot = subquery.from(InfluencerSampleOrder.class);
            subquery.select(sampleRoot.get("influencerId")).groupBy(sampleRoot.get("influencerId"));
            List<Predicate> havingPredicates = new ArrayList<>();
            if (filter.getMinSampleCount() != null)
                havingPredicates.add(cb.ge(cb.count(sampleRoot), filter.getMinSampleCount()));
            if (filter.getMaxSampleCount() != null)
                havingPredicates.add(cb.le(cb.count(sampleRoot), filter.getMaxSampleCount()));
            subquery.having(havingPredicates.toArray(new Predicate[0]));
            predicates.add(root.get("id").in(subquery));
        }
    }

    private void addTagPredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.CriteriaBuilder cb, List<Long> tagIds, String tagField) {
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Predicate> tagPredicates = new ArrayList<>();
            for (Long tagId : tagIds) {
                tagPredicates.add(cb.like(root.get(tagField), "%" + tagId + "%"));
            }
            predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
        }
    }

    private void addTimeRangePredicate(List<Predicate> predicates, Root<Influencer> root,
            jakarta.persistence.criteria.CriteriaBuilder cb, InfluencerFilterDto filter) {
        if (StringUtils.hasText(filter.getTimeType()) && StringUtils.hasText(filter.getTimeStart())
                && StringUtils.hasText(filter.getTimeEnd())) {
            String field = "created".equalsIgnoreCase(filter.getTimeType()) ? "createdAt" : "updatedAt";
            try {
                LocalDateTime start = LocalDate.parse(filter.getTimeStart()).atStartOfDay();
                LocalDateTime end = LocalDate.parse(filter.getTimeEnd()).atTime(23, 59, 59);
                predicates.add(cb.between(root.get(field), start, end));
            } catch (Exception e) {
                log.warn("Invalid date range format: {} to {}", filter.getTimeStart(), filter.getTimeEnd());
            }
        }
    }

    // --- [ 批量导入与工具方法 ] ---

    /**
     * 批量导入红人
     *
     * 逐条处理导入数据跳过重复邮箱
     * 自动创建社媒账号Instagram/TikTok/YouTube并解析粉丝数
     * 支持联络员标签自动关联
     *
     * @param list 导入数据列表
     * @return 成功导入的红人数量
     */
    @Transactional
    public int batchImport(List<InfluencerImportDto> list) {
        int successCount = 0;
        for (InfluencerImportDto dto : list) {
            try {
                // 1. 重复检查
                if (StringUtils.hasText(dto.getEmail()) && this.influencerRepository.existsByEmail(dto.getEmail())) {
                    log.warn("Skipping import for dup email: {}", dto.getEmail());
                    continue;
                }

                // 2. 基础实体封装
                Influencer inf = new Influencer();
                String cleanName = dto.getRealName() != null ? dto.getRealName().trim().replaceAll("\\s+", " ") : null;
                inf.setRealName(cleanName);
                inf.setNickName(cleanName);
                inf.setEmail(dto.getEmail());
                inf.setCountry(dto.getCountry());
                inf.setPhone(dto.getPhone());
                inf.setOrigin(Influencer.Origin.MANUAL);
                inf.setSource("Import");
                inf.setInfluencerType(dto.getCategory());
                inf.setOwnerId(dto.getOwnerId());
                inf.setContactPersonId(dto.getContactPersonId());
                inf.setStage(Influencer.Stage.POOL);
                inf.setStatus(Influencer.Status.PENDING);

                // 3. 构建描述信息
                StringBuilder desc = new StringBuilder();
                if (StringUtils.hasText(dto.getKolTier()))
                    desc.append("Tier: ").append(dto.getKolTier()).append("\n");
                if (StringUtils.hasText(dto.getStyle()))
                    desc.append("Style: ").append(dto.getStyle()).append("\n");
                if (StringUtils.hasText(dto.getProject()))
                    desc.append("Project: ").append(dto.getProject()).append("\n");
                if (StringUtils.hasText(dto.getContentRequired()))
                    desc.append("Content: ").append(dto.getContentRequired()).append("\n");
                if (StringUtils.hasText(dto.getCostRaw()))
                    desc.append("Cost: ").append(dto.getCostRaw()).append("\n");
                if (StringUtils.hasText(dto.getPromoCode()))
                    desc.append("Promo Code: ").append(dto.getPromoCode()).append("\n");
                inf.setDescription(desc.toString());

                // 4. 标签处理 (联络员)
                List<Long> tagIds = new ArrayList<>();
                if (StringUtils.hasText(dto.getPicName())) {
                    List<SystemTag> liaisonTags = this.systemTagRepository
                            .findByCategoryAndNameAndEnabledTrue("LIAISON", dto.getPicName());
                    if (!liaisonTags.isEmpty()) {
                        tagIds.add(liaisonTags.get(0).getId());
                    }
                }
                if (!tagIds.isEmpty()) {
                    inf.setTags(this.objectMapper.writeValueAsString(tagIds));
                }

                // 5. 保存红人
                Influencer saved = this.influencerRepository.save(inf);

                // 6. 地址处理
                if (StringUtils.hasText(dto.getAddress())) {
                    InfluencerAddress addr = new InfluencerAddress();
                    addr.setInfluencerId(saved.getId());
                    addr.setAddress(dto.getAddress());
                    addr.setRecipientName(dto.getRealName());
                    addr.setCountry(dto.getCountry());
                    addr.setIsDefault(true);
                    this.addressRepository.save(addr);
                }

                // 7. 社媒账号处理 (自动解析三个主流平台)
                List<SocialMedia> smList = new ArrayList<>();
                if (StringUtils.hasText(dto.getIgLink()))
                    smList.add(
                            this.createSocialMedia(saved.getId(), "Instagram", dto.getIgLink(), dto.getFollowersRaw()));
                if (StringUtils.hasText(dto.getTtLink()))
                    smList.add(this.createSocialMedia(saved.getId(), "TikTok", dto.getTtLink(), dto.getFollowersRaw()));
                if (StringUtils.hasText(dto.getYtLink()))
                    smList.add(
                            this.createSocialMedia(saved.getId(), "YouTube", dto.getYtLink(), dto.getFollowersRaw()));

                if (!smList.isEmpty()) {
                    SocialMedia def = smList.get(0);
                    def.setIsDefault(true);
                    this.socialMediaRepository.saveAll(smList);
                    // 同步冗余字段到主表
                    saved.setDefaultPlatform(def.getPlatform());
                    saved.setDefaultUrl(def.getUrl());
                    saved.setTotalFans(def.getFollowerCount());
                    this.influencerRepository.save(saved);
                }
                this.aiVectorSyncQueueService.enqueue(saved.getId());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to import influencer: {}", dto.getEmail(), e);
            }
        }
        return successCount;
    }

    /**
     * 创建社交媒体账号实体
     * 自动从 URL 中提取账号名并解析粉丝数支持 k/m 后缀不区分大小写
     */
    private SocialMedia createSocialMedia(Long infId, String platform, String url, String fansRaw) {
        SocialMedia sm = new SocialMedia();
        sm.setInfluencerId(infId);
        sm.setPlatform(platform);
        sm.setUrl(url);
        sm.setHandle(this.extractHandle(url));
        long fans = 0L;
        if (StringUtils.hasText(fansRaw)) {
            String clean = fansRaw.toLowerCase().replace(",", "");
            double multiplier = 1.0;
            if (clean.endsWith("k")) {
                multiplier = 1000.0;
                clean = clean.replace("k", "");
            } else if (clean.endsWith("m")) {
                multiplier = 1000000.0;
                clean = clean.replace("m", "");
            }
            try {
                fans = (long) (Double.parseDouble(clean) * multiplier);
            } catch (Exception e) {
                log.warn("Failed to parse follower count '{}': {}", fansRaw, e.getMessage());
            }
        }
        sm.setFollowerCount(fans);
        return sm;
    }

    /** 从社媒 URL 中提取账号名取最后一个 / 之后的部分兼容结尾斜杠 */
    private String extractHandle(String url) {
        if (!StringUtils.hasText(url))
            return url;
        try {
            String tempUrl = url.trim();
            if (tempUrl.endsWith("/"))
                tempUrl = tempUrl.substring(0, tempUrl.length() - 1);
            int lastIndex = tempUrl.lastIndexOf("/");
            return lastIndex != -1 ? tempUrl.substring(lastIndex + 1) : tempUrl;
        } catch (Exception e) {
            return url;
        }
    }

    /**
     * 通过关键词列表查找匹配的红人 ID
     * 匹配规则邮箱精确匹配 OR 社媒账号名精确匹配
     *
     * @param keywords 关键词列表邮箱或账号名
     * @return 匹配的红人 ID 列表
     */
    @Transactional(readOnly = true)
    public List<Long> findIdsByKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }
        Specification<Influencer> spec = (root, query, cb) -> {
            List<String> validKeywords = new ArrayList<>();
            for (String keyword : keywords) {
                String k = keyword.trim();
                if (!k.isEmpty()) {
                    validKeywords.add(k.toLowerCase());
                }
            }
            
            if (validKeywords.isEmpty()) {
                return cb.disjunction(); // evaluates to always false OR 0=1
            }

            Predicate emailMatch = root.get("email").in(validKeywords);
            Predicate handleMatch = root.get("defaultHandle").in(validKeywords);

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<SocialMedia> smRoot = subquery.from(SocialMedia.class);
            subquery.select(smRoot.get("influencerId"))
                    .where(smRoot.get("handle").in(validKeywords));

            return cb.or(emailMatch, handleMatch, root.get("id").in(subquery));
        };
        return this.influencerRepository.findAll(spec).stream().map(Influencer::getId).collect(Collectors.toList());
    }

    /**
     * 批量更新红人字段
     *
     * 支持操作类型
     * - ownerId: 批量变更负责人
     * - commissionRate: 批量设置红人基础佣金率
     * - appendTags: 批量追加标签不影响已有标签
     * - replaceTags: 批量替换标签先移除指定标签再添加新标签
     *
     * @param dto 批量更新请求含 ids/field/value/removeTagIds
     */
    @Transactional
    public void batchUpdate(BatchUpdateDto dto, String operator) {
        if (dto.getIds() == null || dto.getIds().isEmpty()) {
            return;
        }
        if (operator == null || operator.isBlank()) {
            operator = "SYS";
        }
        List<Influencer> influencers = this.influencerRepository.findAllById(dto.getIds());
        if (influencers.isEmpty()) {
            return;
        }
        switch (dto.getField()) {
            case "ownerId": {
                Long ownerId = ((Number) dto.getValue()).longValue();
                for (Influencer inf : influencers) {
                    inf.setOwnerId(ownerId);
                }
                break;
            }
            case "commissionRate": {
                if (dto.getValue() == null) {
                    throw new IllegalArgumentException("commissionRate value is required");
                }
                BigDecimal newRate = new BigDecimal(dto.getValue().toString());
                if (newRate.compareTo(BigDecimal.ZERO) < 0 || newRate.compareTo(new BigDecimal("100")) > 0) {
                    throw new IllegalArgumentException("commissionRate must be between 0 and 100");
                }
                newRate = newRate.setScale(2, java.math.RoundingMode.HALF_UP);
                for (Influencer inf : influencers) {
                    BigDecimal oldRate = inf.getCommissionRate();
                    if (oldRate != null && oldRate.compareTo(newRate) == 0) {
                        continue;
                    }
                    String oldStr = oldRate == null ? "0" : oldRate.toPlainString();
                    inf.setCommissionRate(newRate);
                    this.logService.logChange(inf.getId(), "分佣比例", oldStr, newRate.toPlainString(), operator,
                            "批量调整");
                }
                break;
            }
            case "appendTags": {
                List<?> newTagNumbers = (List<?>) dto.getValue();
                List<Long> newTagIds = newTagNumbers.stream().map(n -> ((Number) n).longValue())
                        .collect(Collectors.toList());
                for (Influencer inf : influencers) {
                    List<Long> existingTags = new ArrayList<>();
                    if (StringUtils.hasText(inf.getTags())) {
                        try {
                            List<?> raw = this.objectMapper.readValue(inf.getTags(), List.class);
                            for (Object o : raw) {
                                existingTags.add(((Number) o).longValue());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to parse tags for influencer {}: {}", inf.getId(), e.getMessage());
                        }
                    }
                    for (Long tagId : newTagIds) {
                        if (existingTags.contains(tagId))
                            continue;
                        existingTags.add(tagId);
                    }
                    try {
                        inf.setTags(this.objectMapper.writeValueAsString(existingTags));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize tags for influencer {}", inf.getId(), e);
                    }
                }
                break;
            }
            case "replaceTags": {
                List<?> replaceTagNumbers = (List<?>) dto.getValue();
                List<Long> replaceTagIds = replaceTagNumbers.stream().map(n -> ((Number) n).longValue())
                        .collect(Collectors.toList());
                List<Long> toRemove = dto.getRemoveTagIds() != null ? dto.getRemoveTagIds() : new ArrayList<>();
                for (Influencer inf : influencers) {
                    List<Long> existing = new ArrayList<>();
                    if (StringUtils.hasText(inf.getTags())) {
                        try {
                            List<?> raw = this.objectMapper.readValue(inf.getTags(), List.class);
                            for (Object o : raw) {
                                existing.add(((Number) o).longValue());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to parse tags for influencer {}: {}", inf.getId(), e.getMessage());
                        }
                    }
                    existing.removeAll(toRemove);
                    for (Long tagId : replaceTagIds) {
                        if (existing.contains(tagId))
                            continue;
                        existing.add(tagId);
                    }
                    try {
                        inf.setTags(this.objectMapper.writeValueAsString(existing));
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize tags for influencer {}", inf.getId(), e);
                    }
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported batch update field: " + dto.getField());
            }
        }
        this.influencerRepository.saveAll(influencers);
    }

    private BigDecimal getExchangeRateToUsd(String currency) {
        if (currency == null || "USD".equalsIgnoreCase(currency)) {
            return BigDecimal.ONE;
        }
        switch (currency.toUpperCase()) {
            case "CNY": {
                return new BigDecimal("0.14"); // 1 CNY = 0.14 USD (Approx)
            }
            case "EUR": {
                return new BigDecimal("1.08"); // 1 EUR = 1.08 USD
            }
            case "GBP": {
                return new BigDecimal("1.25"); // 1 GBP = 1.25 USD
            }
            case "HKD": {
                return new BigDecimal("0.13"); // 1 HKD = 0.13 USD
            }
            default: {
                return BigDecimal.ONE;
            }
        }
    }

    public InfluencerService(InfluencerRepository influencerRepository, SocialMediaRepository socialMediaRepository,
            InfluencerAddressRepository addressRepository, SystemTagRepository systemTagRepository,
            ObjectMapper objectMapper, UserServiceClient userServiceClient, InfluencerLogService logService,
            InfluencerContentRepository influencerContentRepository,
            InfluencerSampleOrderRepository sampleOrderRepository,
            InfluencerConversionOrderRepository conversionOrderRepository,
            InfluencerCooperationRepository cooperationRepository, InfluencerCooperationService cooperationService,
            StorageServiceFactory storageServiceFactory,
            RemittanceTaskRepository remittanceTaskRepository,
            InfluencerCommissionOrderRepository influencerCommissionOrderRepository,
            JdbcTemplate jdbcTemplate,
            AiVectorSyncQueueService aiVectorSyncQueueService) {
        this.influencerRepository = influencerRepository;
        this.socialMediaRepository = socialMediaRepository;
        this.addressRepository = addressRepository;
        this.systemTagRepository = systemTagRepository;
        this.objectMapper = objectMapper;
        this.userServiceClient = userServiceClient;
        this.logService = logService;
        this.influencerContentRepository = influencerContentRepository;
        this.sampleOrderRepository = sampleOrderRepository;
        this.conversionOrderRepository = conversionOrderRepository;
        this.cooperationRepository = cooperationRepository;
        this.cooperationService = cooperationService;
        this.remittanceTaskRepository = remittanceTaskRepository;
        this.influencerCommissionOrderRepository = influencerCommissionOrderRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.aiVectorSyncQueueService = aiVectorSyncQueueService;
    }
}
