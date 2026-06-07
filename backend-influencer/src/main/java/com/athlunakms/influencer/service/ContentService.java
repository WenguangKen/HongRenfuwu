package com.athlunakms.influencer.service;

import com.athlunakms.influencer.client.UserServiceClient;
import com.athlunakms.influencer.dto.ContentCreateDto;
import com.athlunakms.influencer.dto.ContentDto;
import com.athlunakms.influencer.entity.ContentStorageConfig;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerContent;
import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.repository.ContentStorageConfigRepository;
import com.athlunakms.influencer.repository.InfluencerContentRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.SocialMediaRepository;
import com.athlunakms.influencer.repository.SystemTagRepository;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ContentService {
    private static final Logger log = LoggerFactory.getLogger(ContentService.class);
    private final InfluencerContentRepository contentRepository;
    private final InfluencerRepository influencerRepository;
    private final ContentStorageConfigRepository storageConfigRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final SystemTagRepository systemTagRepository;
    private final EntityManager entityManager;
    private final MediaProcessingService mediaProcessingService;
    private final SocialMediaRepository socialMediaRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 构建内容筛选 Specification（公用逻辑，被 getContents 和 getGroupedContents 复用）
     * @return Specification 或 null（如果邮箱筛选无匹配结果）
     */
    private Specification<InfluencerContent> buildContentSpec(
            String status, Long influencerId, String influencerEmail, String taskGroupId,
            String orderNo, String productSku, String contentType, Boolean isCommercial, String defaultHandle,
            String contactPersonName, String owner, String tagIds, String influencerIds, String influencerName,
            String startTime, String endTime, String publishStartDate, String publishEndDate,
            Integer viewsMin, Integer viewsMax, Integer likesMin, Integer likesMax,
            Integer commentsMin, Integer commentsMax, Integer savesMin, Integer savesMax,
            Integer sharesMin, Integer sharesMax,
            BigDecimal erMin, BigDecimal erMax) {

        List<Long> matchedInfluencerIds = new ArrayList<>();
        
        // 1. 处理 influencerIds (逗号分隔的ID)
        if (influencerIds != null && !influencerIds.trim().isEmpty()) {
            String[] ids = influencerIds.split("[,\\s;\\n]+");
            for (String id : ids) {
                try {
                    matchedInfluencerIds.add(Long.parseLong(id.trim()));
                } catch (Exception e) { /* ignore */ }
            }
        }

        // 2. 处理 influencerName (逗号/分号/换行分隔的名称，不拆空格以保留完整姓名)
        if (influencerName != null && !influencerName.trim().isEmpty()) {
            String[] names = influencerName.split("[,;\\n]+");
            for (String name : names) {
                String trimmed = name.trim();
                if (trimmed.isEmpty()) continue;
                this.influencerRepository.findByNickNameContainingIgnoreCaseOrRealNameContainingIgnoreCase(trimmed, trimmed)
                    .forEach(inf -> matchedInfluencerIds.add(inf.getId()));
            }
        }

        // 3. 处理 influencerEmail (原有逻辑)
        if (influencerEmail != null && !influencerEmail.trim().isEmpty()) {
            String[] emails = influencerEmail.split("[,\\n]+");
            for (String e : emails) {
                String trimmed = e.trim();
                if (trimmed.isEmpty()) continue;
                this.influencerRepository.findByEmailContaining(trimmed)
                    .forEach(inf -> matchedInfluencerIds.add(inf.getId()));
            }
        }
        
        List<Long> finalInfluencerIds = matchedInfluencerIds.stream().distinct().collect(Collectors.toList());
        // 如果提供了筛选条件但没找到任何红人，直接返回 null 表示查无结果
        boolean hasInfluencerFilter = (influencerEmail != null && !influencerEmail.trim().isEmpty()) ||
                                     (influencerName != null && !influencerName.trim().isEmpty()) ||
                                     (influencerIds != null && !influencerIds.trim().isEmpty());
        if (hasInfluencerFilter && finalInfluencerIds.isEmpty()) {
            return null;
        }

        Specification<InfluencerContent> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isEmpty()) {
                if (status.startsWith("!")) {
                    predicates.add(cb.notEqual(root.get("status"), status.substring(1)));
                } else {
                    predicates.add(cb.equal(root.get("status"), status));
                }
            }
            if (influencerId != null) {
                predicates.add(cb.equal(root.get("influencerId"), influencerId));
            }
            if (!finalInfluencerIds.isEmpty()) {
                predicates.add(root.get("influencerId").in(finalInfluencerIds));
            }
            if (taskGroupId != null && !taskGroupId.trim().isEmpty()) {
                String[] tasks = taskGroupId.split("[,\\s;\\n]+");
                ArrayList<Predicate> taskPredicates = new ArrayList<>();
                for (String t : tasks) {
                    String trimmed = t.trim();
                    if (!trimmed.isEmpty()) {
                        taskPredicates.add(cb.equal(root.get("taskGroupId"), trimmed));
                    }
                }
                if (!taskPredicates.isEmpty()) {
                    predicates.add(cb.or(taskPredicates.toArray(new Predicate[0])));
                }
            }
            if (orderNo != null && !orderNo.trim().isEmpty()) {
                String[] orders = orderNo.split("[,\\s;\\n]+");
                ArrayList<Predicate> orderPredicates = new ArrayList<>();
                for (String o : orders) {
                    String trimmed = o.trim().toLowerCase();
                    if (!trimmed.isEmpty()) {
                        orderPredicates.add(cb.like(cb.lower(root.get("orderNo")), "%" + trimmed + "%"));
                    }
                }
                if (!orderPredicates.isEmpty()) {
                    predicates.add(cb.or(orderPredicates.toArray(new Predicate[0])));
                }
            }
            if (productSku != null && !productSku.trim().isEmpty()) {
                String[] skus = productSku.split("[,\\s;\\n]+");
                ArrayList<Predicate> skuPredicates = new ArrayList<>();
                for (String s : skus) {
                    String trimmed = s.trim().toLowerCase();
                    if (trimmed.isEmpty()) continue;
                    skuPredicates.add(cb.like(cb.lower(root.get("productSku")), "%" + trimmed + "%"));
                }
                if (!skuPredicates.isEmpty()) {
                    predicates.add(cb.or(skuPredicates.toArray(new Predicate[0])));
                }
            }
            if (contentType != null && !contentType.trim().isEmpty()) {
                String[] types = contentType.split("[,;\\n]+");
                ArrayList<Predicate> typePredicates = new ArrayList<>();
                for (String t : types) {
                    String trimmed = t.trim();
                    if (!trimmed.isEmpty()) {
                        typePredicates.add(cb.equal(root.get("contentType"), trimmed));
                    }
                }
                if (!typePredicates.isEmpty()) {
                    predicates.add(cb.or(typePredicates.toArray(new Predicate[0])));
                }
            }
            if (isCommercial != null) {
                predicates.add(cb.equal(root.get("isCommercial"), isCommercial));
            }
            if (tagIds != null && !tagIds.trim().isEmpty()) {
                String[] tagIdArr = tagIds.split("[,\\s;\\n]+");
                for (String tid : tagIdArr) {
                    String trimmed = tid.trim();
                    if (!trimmed.isEmpty()) {
                        predicates.add(cb.like(root.get("tags"), "%" + trimmed + "%"));
                    }
                }
            }
            if (startTime != null && !startTime.trim().isEmpty()) {
                try {
                    LocalDateTime start = LocalDate.parse(startTime.trim()).atStartOfDay();
                    predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), start));
                } catch (Exception e) {
                    log.warn("Invalid startTime format: {}", startTime);
                }
            }
            if (endTime != null && !endTime.trim().isEmpty()) {
                try {
                    LocalDateTime end = LocalDate.parse(endTime.trim()).atTime(23, 59, 59);
                    predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), end));
                } catch (Exception e) {
                    log.warn("Invalid endTime format: {}", endTime);
                }
            }
            if (publishStartDate != null && !publishStartDate.trim().isEmpty()) {
                try {
                    LocalDateTime start = LocalDate.parse(publishStartDate.trim()).atStartOfDay();
                    predicates.add(cb.greaterThanOrEqualTo(root.get("publishDate"), start));
                } catch (Exception e) {
                    log.warn("Invalid publishStartDate format: {}", publishStartDate);
                }
            }
            if (publishEndDate != null && !publishEndDate.trim().isEmpty()) {
                try {
                    LocalDateTime end = LocalDate.parse(publishEndDate.trim()).atTime(23, 59, 59);
                    predicates.add(cb.lessThanOrEqualTo(root.get("publishDate"), end));
                } catch (Exception e) {
                    log.warn("Invalid publishEndDate format: {}", publishEndDate);
                }
            }
            // 社媒数据范围筛选
            if (viewsMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("views"), viewsMin));
            if (viewsMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("views"), viewsMax));
            if (likesMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("likes"), likesMin));
            if (likesMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("likes"), likesMax));
            if (commentsMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("comments"), commentsMin));
            if (commentsMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("comments"), commentsMax));
            if (savesMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("saves"), savesMin));
            if (savesMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("saves"), savesMax));
            if (sharesMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("shares"), sharesMin));
            if (sharesMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("shares"), sharesMax));
            if (erMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("engagementRate"), erMin));
            if (erMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("engagementRate"), erMax));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Handle / ContactPerson / Owner 关联筛选
        if (defaultHandle != null && !defaultHandle.trim().isEmpty()
                || contactPersonName != null && !contactPersonName.trim().isEmpty()
                || owner != null && !owner.trim().isEmpty()) {
            List<Long> handleMatchIds = new ArrayList<>();
            if (defaultHandle != null && !defaultHandle.trim().isEmpty()) {
                String[] handles = defaultHandle.split("[,\\n]+");
                List<String> validHandles = new ArrayList<>();
                for (String h : handles) {
                    String trimmed = h.trim();
                    if (!trimmed.isEmpty()) validHandles.add(trimmed);
                }
                if (!validHandles.isEmpty()) {
                    // 使用 JdbcTemplate 批量查询，避免每个 handle 都发两个纯量查询
                    StringBuilder sb = new StringBuilder();
                    sb.append("SELECT DISTINCT id FROM influencer WHERE is_deleted = 0 AND LOWER(default_handle) LIKE ?");
                    List<Object> params = new ArrayList<>();
                    params.add("%" + validHandles.get(0).toLowerCase() + "%");
                    for (int i = 1; i < validHandles.size(); i++) {
                        sb.append(" OR LOWER(default_handle) LIKE ?");
                        params.add("%" + validHandles.get(i).toLowerCase() + "%");
                    }
                    List<Long> fromInfluencer = this.jdbcTemplate.queryForList(
                            sb.toString(), params.toArray(), Long.class);
                    handleMatchIds.addAll(fromInfluencer);

                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("SELECT DISTINCT influencer_id FROM influencer_social_media WHERE LOWER(handle) LIKE ?");
                    List<Object> params2 = new ArrayList<>();
                    params2.add("%" + validHandles.get(0).toLowerCase() + "%");
                    for (int i = 1; i < validHandles.size(); i++) {
                        sb2.append(" OR LOWER(handle) LIKE ?");
                        params2.add("%" + validHandles.get(i).toLowerCase() + "%");
                    }
                    List<Long> fromSocial = this.jdbcTemplate.queryForList(
                            sb2.toString(), params2.toArray(), Long.class);
                    handleMatchIds.addAll(fromSocial);

                    handleMatchIds = handleMatchIds.stream().distinct().collect(Collectors.toList());
                }
            }
            List<Long> contactMatchIds = new ArrayList<>();
            if (contactPersonName != null && !contactPersonName.trim().isEmpty()) {
                String[] names = contactPersonName.split("[,\\n]+");
                List<Long> matchingTagIds = new ArrayList<>();
                for (String n : names) {
                    String trimmed = n.trim();
                    if (trimmed.isEmpty()) continue;
                    this.systemTagRepository.findByCategoryAndEnabledTrue("LIAISON", Sort.by("name")).stream()
                            .filter(t -> t.getName().equalsIgnoreCase(trimmed))
                            .forEach(t -> matchingTagIds.add(t.getId()));
                }
                if (!matchingTagIds.isEmpty()) {
                    List<Influencer> candidates = new ArrayList<>();
                    for (Long tagId : matchingTagIds) {
                        candidates.addAll(this.influencerRepository.findByTagsContaining(String.valueOf(tagId)));
                    }
                    for (Influencer inf : candidates) {
                        List<Long> infTagIds = this.parseTagIds(inf.getTags());
                        if (infTagIds.stream().noneMatch(matchingTagIds::contains)) continue;
                        contactMatchIds.add(inf.getId());
                    }
                }
            }
            List<Long> ownerMatchIds = new ArrayList<>();
            if (owner != null && !owner.trim().isEmpty()) {
                String[] ownerNames = owner.split("[,\\n]+");
                List<Long> ownerUserIds = new ArrayList<>();
                for (String n : ownerNames) {
                    String trimmed = n.trim();
                    if (trimmed.isEmpty()) continue;
                    Long userId = this.userServiceClient.findUserIdByName(trimmed);
                    if (userId != null) ownerUserIds.add(userId);
                }
                if (!ownerUserIds.isEmpty()) {
                    List<Influencer> ownerInfluencers = this.influencerRepository.findByOwnerIdIn(ownerUserIds);
                    ownerMatchIds = ownerInfluencers.stream()
                            .map(Influencer::getId).distinct().collect(Collectors.toList());
                }
            }
            List<Long> fHandleIds = handleMatchIds.isEmpty() ? null : handleMatchIds;
            List<Long> fContactIds = contactMatchIds.isEmpty() ? null : contactMatchIds;
            List<Long> fOwnerIds = ownerMatchIds.isEmpty() ? null : ownerMatchIds;

            boolean handleRequested = defaultHandle != null && !defaultHandle.trim().isEmpty();
            boolean contactRequested = contactPersonName != null && !contactPersonName.trim().isEmpty();
            boolean ownerRequested = owner != null && !owner.trim().isEmpty();
            if ((handleRequested && fHandleIds == null) || (contactRequested && fContactIds == null)
                    || (ownerRequested && fOwnerIds == null)) {
                return Specification.where((r, q, c) -> c.disjunction()); // 返回永假条件
            }

            Specification<InfluencerContent> extraSpec = (root2, query2, cb2) -> {
                ArrayList<Predicate> p = new ArrayList<>();
                if (fHandleIds != null) p.add(root2.get("influencerId").in(fHandleIds));
                if (fContactIds != null) p.add(root2.get("influencerId").in(fContactIds));
                if (fOwnerIds != null) p.add(root2.get("influencerId").in(fOwnerIds));
                return cb2.and(p.toArray(new Predicate[0]));
            };
            spec = spec.and(extraSpec);
        }

        return spec;
    }

    /**
     * 批量预加载关联数据并转换为 DTO 列表
     */
    private List<ContentDto> batchConvertToDtos(List<InfluencerContent> contentList) {
        if (contentList.isEmpty()) return new ArrayList<>();

        java.util.Set<Long> influencerIdSet = contentList.stream()
                .map(InfluencerContent::getInfluencerId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, Influencer> influencerMap = influencerIdSet.isEmpty()
                ? Collections.emptyMap()
                : this.influencerRepository.findAllById(influencerIdSet).stream()
                        .collect(Collectors.toMap(Influencer::getId, i -> i));

        Map<Long, SystemTag> liaisonTagMap;
        try {
            liaisonTagMap = this.systemTagRepository
                    .findByCategoryAndEnabledTrue("LIAISON", Sort.by("name")).stream()
                    .collect(Collectors.toMap(SystemTag::getId, t -> t, (a, b) -> a));
        } catch (Exception e) {
            liaisonTagMap = Collections.emptyMap();
        }

        java.util.Set<Long> ownerIdSet = influencerMap.values().stream()
                .map(Influencer::getOwnerId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> ownerNameMap;
        try {
            ownerNameMap = ownerIdSet.isEmpty()
                    ? Collections.emptyMap()
                    : this.userServiceClient.getUserNames(new ArrayList<>(ownerIdSet));
        } catch (Exception e) {
            log.warn("Failed to batch-resolve owner names", e);
            ownerNameMap = Collections.emptyMap();
        }

        StorageService storage = null;
        try {
            storage = this.storageServiceFactory.getStorageService();
        } catch (Exception e) {
            log.warn("Failed to get storage service for batch URL generation", e);
        }

        Map<Long, SystemTag> finalLiaisonTagMap = liaisonTagMap;
        Map<Long, String> finalOwnerNameMap = ownerNameMap;
        StorageService finalStorage = storage;
        return contentList.stream()
                .map(entity -> this.toDtoBatch(entity, influencerMap, finalLiaisonTagMap, finalOwnerNameMap, finalStorage))
                .collect(Collectors.toList());
    }

    public Page<ContentDto> getContents(String status, Long influencerId, String influencerEmail, String taskGroupId,
            String orderNo, String productSku, String contentType, Boolean isCommercial, String defaultHandle,
            String contactPersonName, String owner, String tagIds, String influencerIds, String influencerName,
            String startTime, String endTime, String publishStartDate, String publishEndDate,
            Integer viewsMin, Integer viewsMax, Integer likesMin, Integer likesMax,
            Integer commentsMin, Integer commentsMax, Integer savesMin, Integer savesMax,
            Integer sharesMin, Integer sharesMax,
            BigDecimal erMin, BigDecimal erMax,
            Pageable pageable) {
        Specification<InfluencerContent> spec = buildContentSpec(status, influencerId, influencerEmail, taskGroupId,
                orderNo, productSku, contentType, isCommercial, defaultHandle, contactPersonName, owner, tagIds, influencerIds, influencerName,
                startTime, endTime, publishStartDate, publishEndDate,
                viewsMin, viewsMax, likesMin, likesMax, commentsMin, commentsMax, savesMin, savesMax, sharesMin, sharesMax,
                erMin, erMax);
        if (spec == null) {
            return Page.empty(pageable);
        }

        Page<InfluencerContent> page = this.contentRepository.findAll(spec, pageable);
        List<InfluencerContent> contentList = page.getContent();
        if (contentList.isEmpty()) {
            return page.map(this::toDto);
        }

        List<ContentDto> dtos = batchConvertToDtos(contentList);
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    /**
     * 按 taskGroupId + contentGroupIndex 分组分页查询内容列表。
     * 分页粒度为「内容项目」，每页返回 N 个不同的 (taskGroupId, contentGroupIndex) 组合。
     * totalElements 为符合条件的不同组合总数。
     */
    public Page<ContentDto> getGroupedContents(String status, Long influencerId, String influencerEmail, String taskGroupId,
            String orderNo, String productSku, String contentType, Boolean isCommercial, String defaultHandle,
            String contactPersonName, String owner, String tagIds, String influencerIds, String influencerName,
            String startTime, String endTime, String publishStartDate, String publishEndDate,
            Integer viewsMin, Integer viewsMax, Integer likesMin, Integer likesMax,
            Integer commentsMin, Integer commentsMax, Integer savesMin, Integer savesMax,
            Integer sharesMin, Integer sharesMax,
            BigDecimal erMin, BigDecimal erMax,
            int page, int size) {
        Specification<InfluencerContent> spec = buildContentSpec(status, influencerId, influencerEmail, taskGroupId,
                orderNo, productSku, contentType, isCommercial, defaultHandle, contactPersonName, owner, tagIds, influencerIds, influencerName,
                startTime, endTime, publishStartDate, publishEndDate,
                viewsMin, viewsMax, likesMin, likesMax, commentsMin, commentsMax, savesMin, savesMax, sharesMin, sharesMax,
                erMin, erMax);
        if (spec == null) {
            return Page.empty(PageRequest.of(page, size));
        }

        // 1) 一次查出所有匹配筛选条件的记录（筛选通过 Spring Data JPA 标准 Specification，保证准确）
        List<InfluencerContent> allMatching = this.contentRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "updatedAt"));

        if (allMatching.isEmpty()) {
            return Page.empty(PageRequest.of(page, size));
        }

        // 2) 按 (taskGroupId, contentGroupIndex) 分组，LinkedHashMap 保持排序顺序
        java.util.LinkedHashMap<String, List<InfluencerContent>> groups = new java.util.LinkedHashMap<>();
        for (InfluencerContent c : allMatching) {
            // 关键修复：逻辑与前端完全对齐。如果有任务ID则按任务ID+索引分组；如果没有则按单条ID独立成行。
            String gid = (c.getTaskGroupId() != null && !c.getTaskGroupId().isBlank()) 
                         ? c.getTaskGroupId() 
                         : "RAW_" + c.getId();
            String key = gid + "_" + (c.getContentGroupIndex() != null ? c.getContentGroupIndex() : 0);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        // 3) 总数 = 分组后的组数（与前端列表行数绝对一致）
        int totalGroups = groups.size();
        log.info("[GroupedContents] spec={}, allMatching={}, totalGroups={}, page={}, size={}", 
            spec != null ? "active" : "null", allMatching.size(), totalGroups, page, size);

        // 4) 分页截取当前页的 group
        List<String> allKeys = new ArrayList<>(groups.keySet());
        int start = page * size;
        int end = Math.min(start + size, totalGroups);
        if (start >= totalGroups) {
            return Page.empty(PageRequest.of(page, size));
        }
        List<String> pageKeys = allKeys.subList(start, end);

        // 5) 收集当前页的所有记录
        List<InfluencerContent> pageContent = new ArrayList<>();
        for (String key : pageKeys) {
            pageContent.addAll(groups.get(key));
        }

        // 6) 批量转换为 DTO
        List<ContentDto> dtos = batchConvertToDtos(pageContent);

        // 7) 总数 = 分组数，与前端显示行数完全一致
        return new PageImpl<>(dtos, PageRequest.of(page, size), totalGroups);
    }

    public ContentDto getContent(Long id) {
        InfluencerContent content = this.contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + id));
        return this.toDto(content);
    }

    @Transactional
    public ContentDto createContent(ContentCreateDto dto) {
        InfluencerContent content = new InfluencerContent();
        content.setInfluencerId(dto.getInfluencerId());
        content.setTitle(dto.getTitle());
        content.setDescription(dto.getDescription());
        content.setPlatform(dto.getPlatform());
        content.setPostType(dto.getPostType());
        content.setOwner(dto.getOwner());
        content.setOrderNo(dto.getOrderNo());
        content.setProductSku(dto.getProductSku());
        content.setContentType(dto.getContentType());
        if (dto.getPostUrl() != null) {
            content.setPostUrl(dto.getPostUrl());
        }
        if (dto.getIsCommercial() != null) {
            content.setIsCommercial(dto.getIsCommercial());
        }
        if (dto.getRemark() != null) {
            content.setRemark(dto.getRemark());
        }
        if (dto.getPublishDate() != null) {
            content.setPublishDate(dto.getPublishDate());
        }
        if (dto.getContentGroupIndex() != null) {
            content.setContentGroupIndex(dto.getContentGroupIndex());
        }
        if (dto.getTaskGroupId() == null || dto.getTaskGroupId().trim().isEmpty()) {
            String taskId = this.generateTaskId(dto.getPostType());
            content.setTaskGroupId(taskId);
        } else {
            content.setTaskGroupId(dto.getTaskGroupId());
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            try {
                content.setTags(this.objectMapper.writeValueAsString(dto.getTagIds()));
            } catch (Exception e) {
                log.error("Failed to serialize tags", e);
            }
        }
        content.setStatus("PENDING_UPLOAD");
        InfluencerContent saved = this.contentRepository.save(content);
        log.info("Created content: id={}, title={}, taskId={}", saved.getId(), saved.getTitle(),
                saved.getTaskGroupId());
        return this.toDto(saved);
    }

    private String generateTaskId(String postType) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = this.contentRepository.countByTaskGroupIdStartingWith(dateStr);
        String sequence = String.format("%02d", count + 1L);
        String suffix = "VIDEO".equalsIgnoreCase(postType) ? "V" : "P";
        return dateStr + sequence + suffix;
    }

    @Transactional
    public ContentDto uploadFile(Long contentId, MultipartFile file, MultipartFile thumbnail, Integer width, Integer height, Integer duration) {
        String fileKey;
        String thumbnailKey = null;
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        StorageService storage = this.storageServiceFactory.getStorageService();
        Influencer influencer = this.influencerRepository.findById(content.getInfluencerId())
                .orElseThrow(() -> new RuntimeException("红人不存在: " + content.getInfluencerId()));
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String sanitizedName = (influencer.getNickName() != null ? influencer.getNickName()
                : (influencer.getRealName() != null ? influencer.getRealName() : "unknown"))
                .replaceAll("[^a-zA-Z0-9]", "_");
        if (sanitizedName.isEmpty() || sanitizedName.matches("^_+$")) {
            sanitizedName = "user";
        }
        
        String relativePath = "originals/" + datePath + "/influencer-" + content.getInfluencerId() + "-" + sanitizedName;
        String thumbRelativePath = "thumbnails/" + datePath + "/influencer-" + content.getInfluencerId() + "-" + sanitizedName;
        String newFileName = this.generateFileName(influencer, content, file.getOriginalFilename());
        String contentType = file.getContentType();
        long fileSize = file.getSize();

        if (contentType != null && contentType.startsWith("image/")) {
            content.setPostType("IMAGE");
        } else if (contentType != null && contentType.startsWith("video/")) {
            content.setPostType("VIDEO");
        }

        // --- 应用客户端提供的元数据 ---
        if (width != null) content.setWidth(width);
        if (height != null) content.setHeight(height);
        if (duration != null) content.setDuration(duration);

        try {
            // 流式上传原始文件
            fileKey = storage.upload(file.getInputStream(), fileSize, relativePath, newFileName, contentType);
            
            // 如果提供了前端生成的缩略图，则上传
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbFileName = "thumb_" + newFileName.replaceAll("\\.[^.]+$", ".jpg");
                thumbnailKey = storage.upload(thumbnail.getInputStream(), thumbnail.getSize(), thumbRelativePath, thumbFileName, "image/jpeg");
            }
        } catch (IOException e) {
            log.error("Failed to read file stream for content: {}", contentId, e);
            throw new RuntimeException("读取文件流失败", e);
        }

        content.setFilePath(fileKey);
        if (thumbnailKey != null) {
            content.setThumbnailPath(thumbnailKey);
        }
        content.setFileName(newFileName);
        content.setFileSize(fileSize);
        content.setFileType(contentType);
        content.setStatus("PENDING_REVIEW");
        
        // --- 原子上传保证 ---
        try {
            InfluencerContent saved = this.contentRepository.save(content);
            log.info("Uploaded file for content: id={}, fileKey={}, fileName={}, postType={}", contentId, fileKey, newFileName, content.getPostType());
            
            // 触发后台 FFmpeg 提取（如果是视频且缺少缩略图）
            if ("VIDEO".equalsIgnoreCase(saved.getPostType()) && saved.getThumbnailPath() == null) {
                this.mediaProcessingService.extractVideoThumbnailAsync(saved.getId());
            }

            return this.toDto(saved);
        } catch (Exception e) {
            // 如果数据库保存失败，回滚存储中的文件
            log.error("Failed to save content to database after successful storage upload. Rolling back files in MinIO.", e);
            try {
                if (fileKey != null) storage.delete(fileKey);
                if (thumbnailKey != null) storage.delete(thumbnailKey);
            } catch (Exception cleanupException) {
                log.error("Failed to cleanup orphaned files in storage after database save failure: fileKey={}", fileKey, cleanupException);
            }
            throw new RuntimeException("保存文件信息到数据库失败，已回退上传的文件", e);
        }
    }

    private String generateFileName(Influencer influencer, InfluencerContent content, String originalFilename) {
        String name = influencer.getNickName() != null ? influencer.getNickName()
                : (influencer.getRealName() != null ? influencer.getRealName() : "unknown");
        name = name.replaceAll("[^a-zA-Z0-9]", "_");
        if (name.isEmpty() || name.matches("^_+$")) {
            name = "inf";
        }

        // 任务ID
        String taskId = content.getTaskGroupId() != null ? content.getTaskGroupId() : "NOTASK";
        taskId = taskId.replaceAll("[^a-zA-Z0-9\\-_]", "_");

        // SKU 处理：最多取前 5 个 SKU，避免文件名过长导致 MinIO 拒绝
        String rawSku = content.getProductSku() != null ? content.getProductSku() : "NOSKU";
        String skuPart;
        if (rawSku.contains(",")) {
            String[] skuArr = rawSku.split(",");
            int limit = Math.min(skuArr.length, 5);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < limit; i++) {
                String s = skuArr[i].trim().replaceAll("[^a-zA-Z0-9\\-_]", "_");
                if (s.isEmpty() || s.matches("^_+$")) continue;
                if (sb.length() > 0) sb.append("__");
                sb.append(s);
            }
            skuPart = sb.length() > 0 ? sb.toString() : "NOSKU";
        } else {
            skuPart = rawSku.replaceAll("[^a-zA-Z0-9\\-_]", "_");
        }
        // 最终安全截断，防止极端情况
        if (skuPart.length() > 150) {
            skuPart = skuPart.substring(0, 150);
        }

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = this.contentRepository.countByInfluencerId(influencer.getId());
        String sequence = String.format("%02d", count + 1L);
        // 加入毫秒时间戳防止并发场景下文件名重复
        String ts = String.valueOf(System.currentTimeMillis() % 100000);
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 彻底清理后缀名，防止传入带有不可见控制字符的扩展名
            extension = extension.replaceAll("[^a-zA-Z0-9.]", "");
        }
        // 新命名格式：红人名_任务ID_SKU列表_日期_序号_时间戳.扩展名
        return name + "_" + taskId + "_" + skuPart + "_" + dateStr + "_" + sequence + "_" + ts + extension;
    }

    public void uploadChunk(Long contentId, String uploadId, Integer chunkIndex, Integer totalChunks, MultipartFile chunk) {
        try {
            java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"), "athluna-uploads/" + uploadId);
            if (!tempDir.exists() && !tempDir.mkdirs()) {
                log.warn("Temp dir already created or failed to create: {}", tempDir.getAbsolutePath());
            }
            java.io.File chunkFile = new java.io.File(tempDir, String.valueOf(chunkIndex));
            chunk.transferTo(chunkFile);
            log.info("Saved chunk {}/{} for uploadId {}", chunkIndex + 1, totalChunks, uploadId);
        } catch (IOException e) {
            log.error("Failed to save chunk {} for uploadId {}", chunkIndex, uploadId, e);
            throw new RuntimeException("切片保存失败", e);
        }
    }

    @Transactional
    public ContentDto mergeChunks(Long contentId, String uploadId, String originalFilename, String fileType, Long totalSize, MultipartFile thumbnail, Integer width, Integer height, Integer duration) {
        java.io.File tempDir = new java.io.File(System.getProperty("java.io.tmpdir"), "athluna-uploads/" + uploadId);
        if (!tempDir.exists() || !tempDir.isDirectory()) {
            throw new RuntimeException("合并失败：找不到切片数据目录");
        }
        
        java.io.File[] chunks = tempDir.listFiles();
        if (chunks == null || chunks.length == 0) {
            throw new RuntimeException("分片数据为空");
        }
        
        // 过滤出数字命名的切片
        java.util.List<java.io.File> chunkList = new java.util.ArrayList<>();
        for (java.io.File cf : chunks) {
            if (cf.getName().matches("\\d+")) {
                chunkList.add(cf);
            }
        }
        chunkList.sort(java.util.Comparator.comparingInt(f -> Integer.parseInt(f.getName())));
        
        java.io.File mergedFile = new java.io.File(tempDir, originalFilename);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(mergedFile, false);
             java.nio.channels.FileChannel dest = fos.getChannel()) {
            for (java.io.File chunk : chunkList) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(chunk);
                     java.nio.channels.FileChannel src = fis.getChannel()) {
                    dest.transferFrom(src, dest.size(), src.size());
                }
            }
        } catch (IOException e) {
            log.error("Failed to merge chunks for uploadId: {}", uploadId, e);
            throw new RuntimeException("合并切片失败", e);
        }
        
        // --- 开始上传完整文件到存储 ---
        String fileKey;
        String thumbnailKey = null;
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        StorageService storage = this.storageServiceFactory.getStorageService();
        Influencer influencer = this.influencerRepository.findById(content.getInfluencerId())
                .orElseThrow(() -> new RuntimeException("红人不存在: " + content.getInfluencerId()));
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String safeName = (influencer.getNickName() != null ? influencer.getNickName()
                : (influencer.getRealName() != null ? influencer.getRealName() : "unknown"));
        String sanitizedName = safeName.replaceAll("[^a-zA-Z0-9]", "_");
        if (sanitizedName.isEmpty() || sanitizedName.matches("^_+$")) {
            sanitizedName = "user";
        }
        
        String relativePath = "originals/" + datePath + "/influencer-" + content.getInfluencerId() + "-" + sanitizedName;
        String thumbRelativePath = "thumbnails/" + datePath + "/influencer-" + content.getInfluencerId() + "-" + sanitizedName;
        String newFileName = this.generateFileName(influencer, content, originalFilename);
        
        if (fileType != null && fileType.startsWith("image/")) {
            content.setPostType("IMAGE");
        } else if (fileType != null && fileType.startsWith("video/")) {
            content.setPostType("VIDEO");
        }
        if (width != null) content.setWidth(width);
        if (height != null) content.setHeight(height);
        if (duration != null) content.setDuration(duration);

        try (java.io.FileInputStream fis = new java.io.FileInputStream(mergedFile)) {
            fileKey = storage.upload(fis, mergedFile.length(), relativePath, newFileName, fileType);
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbFileName = "thumb_" + newFileName.replaceAll("\\.[^.]+$", ".jpg");
                thumbnailKey = storage.upload(thumbnail.getInputStream(), thumbnail.getSize(), thumbRelativePath, thumbFileName, "image/jpeg");
            }
        } catch (IOException e) {
            log.error("Failed to upload merged file to storage for content: {}", contentId, e);
            throw new RuntimeException("上传文件到存储失败", e);
        } finally {
            org.springframework.util.FileSystemUtils.deleteRecursively(tempDir);
        }

        content.setFilePath(fileKey);
        if (thumbnailKey != null) {
            content.setThumbnailPath(thumbnailKey);
        }
        content.setFileName(newFileName);
        content.setFileSize(mergedFile.length());
        content.setFileType(fileType);
        content.setStatus("PENDING_REVIEW");
        
        try {
            InfluencerContent saved = this.contentRepository.save(content);
            log.info("Uploaded merged file for content: id={}, fileKey={}, fileName={}, postType={}", contentId, fileKey, newFileName, content.getPostType());
            if ("VIDEO".equalsIgnoreCase(saved.getPostType()) && saved.getThumbnailPath() == null) {
                this.mediaProcessingService.extractVideoThumbnailAsync(saved.getId());
            }
            return this.toDto(saved);
        } catch (Exception e) {
            log.error("Failed to save content to database after successful merged upload.", e);
            try {
                if (fileKey != null) storage.delete(fileKey);
                if (thumbnailKey != null) storage.delete(thumbnailKey);
            } catch (Exception cleanupException) {
                log.error("Failed to cleanup orphaned files in storage", cleanupException);
            }
            throw new RuntimeException("保存文件信息到数据库失败，已回退上传的文件", e);
        }
    }

    @Transactional
    public ContentDto renameFile(Long contentId, String newFileName) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        content.setFileName(newFileName);
        InfluencerContent saved = this.contentRepository.save(content);
        log.info("Renamed content file: id={}, newName={}", contentId, newFileName);
        return this.toDto(saved);
    }

    @Transactional
    public ContentDto reviewContent(Long contentId, boolean approved, String note, Long reviewerId) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        content.setStatus(approved ? "APPROVED" : "REJECTED");
        content.setReviewerId(reviewerId);
        content.setReviewedAt(LocalDateTime.now());
        content.setReviewNote(note);
        InfluencerContent saved = this.contentRepository.save(content);
        log.info("Reviewed content: id={}, approved={}", contentId, approved);
        return this.toDto(saved);
    }

    @Transactional
    public ContentDto updateContent(Long contentId, ContentCreateDto dto) {
        log.info("Updating content: id={}, dto={}", contentId, dto);
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        if ("APPROVED".equals(content.getStatus()) || "COMPLETED".equals(content.getStatus())) {
            throw new RuntimeException("内容已审核或已完成无法修改");
        }
        if (dto.getTitle() != null) {
            content.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            content.setDescription(dto.getDescription());
        }
        if (dto.getPlatform() != null) {
            content.setPlatform(dto.getPlatform());
        }
        if (dto.getPostType() != null) {
            content.setPostType(dto.getPostType());
        }
        if (dto.getOwner() != null) {
            content.setOwner(dto.getOwner());
        }
        if (dto.getTagIds() != null) {
            try {
                content.setTags(this.objectMapper.writeValueAsString(dto.getTagIds()));
            } catch (Exception e) {
                log.error("Failed to serialize tags", e);
            }
        }
        if (dto.getOrderNo() != null) {
            String oldOrderNo = content.getOrderNo();
            String newOrderNo = dto.getOrderNo();
            content.setOrderNo(newOrderNo);
            String taskGroupId = content.getTaskGroupId();
            if (taskGroupId != null && !taskGroupId.trim().isEmpty() && !newOrderNo.equals(oldOrderNo)) {
                try {
                    int count = this.contentRepository.updateOrderNoByTaskGroupId(taskGroupId, newOrderNo);
                    log.info("Bulk updated orderNo for taskGroup: taskGroupId={}, orderNo={}, affectedRows={}",
                            taskGroupId,
                            newOrderNo, count);
                } catch (Exception e) {
                    log.error("批量更新任务组订单号失败: {}", taskGroupId, e);
                    // 即使批量更新失败，也继续保存当前记录
                }
            } else if (taskGroupId == null || taskGroupId.trim().isEmpty()) {
                log.warn("任务组 ID 缺失，内容 id={}，跳过批量更新", contentId);
            }
        }
        if (dto.getProductSku() != null) {
            content.setProductSku(dto.getProductSku());
        }
        if (dto.getContentType() != null) {
            content.setContentType(dto.getContentType());
        }
        if (dto.getPostUrl() != null) {
            content.setPostUrl(dto.getPostUrl());
        }
        if (dto.getTaskGroupId() != null) {
            content.setTaskGroupId(dto.getTaskGroupId());
        }
        if (dto.getIsCommercial() != null) {
            content.setIsCommercial(dto.getIsCommercial());
        }
        if (dto.getRemark() != null) {
            content.setRemark(dto.getRemark());
        }
        if (dto.getPublishDate() != null) {
            content.setPublishDate(dto.getPublishDate());
        }
        if (dto.getContentGroupIndex() != null) {
            content.setContentGroupIndex(dto.getContentGroupIndex());
        }
        if (dto.getStatus() != null) {
            content.setStatus(dto.getStatus());
        }
        InfluencerContent saved = this.contentRepository.saveAndFlush(content);
        log.info("Successfully updated content: id={}, orderNo={}", contentId, saved.getOrderNo());
        return this.toDto(saved);
    }

    @Transactional
    public ContentDto updateSocialMetrics(Long contentId, Map<String, Integer> metrics) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));

        Integer views = metrics.getOrDefault("views", content.getViews());
        Integer likes = metrics.getOrDefault("likes", content.getLikes());
        Integer comments = metrics.getOrDefault("comments", content.getComments());
        Integer shares = metrics.getOrDefault("shares", content.getShares());
        Integer saves = metrics.getOrDefault("saves", content.getSaves());

        // 计算 Engagement Rate
        BigDecimal er = this.calculateEngagementRate(content.getContentType(), content.getInfluencerId(),
                views, likes, comments, shares, saves);

        // 使用 JPQL 直接更新，绕过 @UpdateTimestamp，避免修改 updated_at
        this.contentRepository.updateSocialMetricsById(contentId, views, likes, comments, shares, saves, er, LocalDateTime.now());

        // 重新查询返回最新数据
        InfluencerContent updated = this.contentRepository.findById(contentId).orElseThrow();
        log.info("Updated social metrics for content: id={}, er={}", contentId, er);
        return this.toDto(updated);
    }

    /**
     * 根据内容类型计算 Engagement Rate（百分比，保留2位小数）
     * 视频类（IG-Reel, Tiktok video, Youtube short, Youtube video）：ER = (likes + comments + shares) / views * 100
     * 图文类（IG-feed post, 其它等）：ER = (likes + comments + saves) / 粉丝数 * 100
     */
    private BigDecimal calculateEngagementRate(String contentType, Long influencerId,
            Integer views, Integer likes, Integer comments, Integer shares, Integer saves) {
        int l = likes != null ? likes : 0;
        int c = comments != null ? comments : 0;
        int sh = shares != null ? shares : 0;
        int sv = saves != null ? saves : 0;
        int v = views != null ? views : 0;

        boolean isVideo = "IG-Reel".equals(contentType) || "Tiktok video".equals(contentType)
                || "Youtube short".equals(contentType) || "Youtube video".equals(contentType);

        if (isVideo) {
            if (v <= 0) return BigDecimal.ZERO;
            return BigDecimal.valueOf(l + c + sh)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(v), 2, RoundingMode.HALF_UP);
        } else {
            // 图文类，取红人粉丝数
            long followerCount = 0;
            if (influencerId != null) {
                try {
                    List<com.athlunakms.influencer.entity.SocialMedia> socialMediaList =
                            this.socialMediaRepository.findByInfluencerId(influencerId);
                    // 优先取 isDefault=true 的，否则取第一条
                    followerCount = socialMediaList.stream()
                            .filter(sm -> Boolean.TRUE.equals(sm.getIsDefault()))
                            .findFirst()
                            .orElse(socialMediaList.isEmpty() ? null : socialMediaList.get(0))
                            != null ? socialMediaList.stream()
                            .filter(sm -> Boolean.TRUE.equals(sm.getIsDefault()))
                            .findFirst()
                            .map(com.athlunakms.influencer.entity.SocialMedia::getFollowerCount)
                            .orElse(socialMediaList.isEmpty() ? 0L : socialMediaList.get(0).getFollowerCount())
                            : 0L;
                } catch (Exception e) {
                    log.warn("Failed to get follower count for influencer: {}", influencerId);
                }
            }
            if (followerCount <= 0) return BigDecimal.ZERO;
            return BigDecimal.valueOf(l + c + sv)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(followerCount), 2, RoundingMode.HALF_UP);
        }
    }

    @Transactional
    public void deleteContent(Long contentId) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));

        // 如果该内容属于某个任务组，则级联删除同组所有内容（待上传+已完成）
        String taskGroupId = content.getTaskGroupId();
        if (taskGroupId != null && !taskGroupId.trim().isEmpty()) {
            List<InfluencerContent> groupContents = this.contentRepository.findByTaskGroupId(taskGroupId);
            for (InfluencerContent c : groupContents) {
                this.cleanupContentFiles(c);
            }
            this.contentRepository.deleteAll(groupContents);
            log.info("Cascade deleted content group: taskGroupId={}, count={}", taskGroupId, groupContents.size());
        } else {
            // 没有任务组，仅删除单条记录
            this.cleanupContentFiles(content);
            this.contentRepository.delete(content);
            log.info("Deleted single content: id={}", contentId);
        }
    }

    @Transactional
    public void batchDeleteContents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<InfluencerContent> contents = this.contentRepository.findAllById(ids);
        if (contents.isEmpty()) {
            return;
        }

        // 收集所有相关任务组 ID，级联删除同组内容
        java.util.Set<String> taskGroupIds = new java.util.HashSet<>();
        java.util.Set<Long> directDeleteIds = new java.util.HashSet<>();
        for (InfluencerContent content : contents) {
            String tgId = content.getTaskGroupId();
            if (tgId != null && !tgId.trim().isEmpty()) {
                taskGroupIds.add(tgId);
            } else {
                directDeleteIds.add(content.getId());
            }
        }

        // 级联删除任务组内的所有关联内容
        List<InfluencerContent> allToDelete = new ArrayList<>();
        for (String tgId : taskGroupIds) {
            List<InfluencerContent> groupContents = this.contentRepository.findByTaskGroupId(tgId);
            allToDelete.addAll(groupContents);
        }
        // 添加没有任务组的直接删除记录
        if (!directDeleteIds.isEmpty()) {
            allToDelete.addAll(this.contentRepository.findAllById(directDeleteIds));
        }

        // 去重（可能有重复 id）
        java.util.Map<Long, InfluencerContent> uniqueMap = new java.util.LinkedHashMap<>();
        for (InfluencerContent c : allToDelete) {
            uniqueMap.putIfAbsent(c.getId(), c);
        }
        List<InfluencerContent> uniqueContents = new ArrayList<>(uniqueMap.values());

        for (InfluencerContent content : uniqueContents) {
            this.cleanupContentFiles(content);
        }
        this.contentRepository.deleteAll(uniqueContents);
        log.info("Batch deleted contents (with cascade): requested={}, actual={}", ids.size(), uniqueContents.size());
    }

    @Transactional
    public void batchUpdateTags(List<Long> ids, List<Long> tagIds) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String tagsJson = null;
        if (tagIds != null && !tagIds.isEmpty()) {
            try {
                tagsJson = this.objectMapper.writeValueAsString(tagIds);
            } catch (Exception e) {
                log.error("Failed to serialize tagIds for batch update", e);
                throw new RuntimeException("标签处理失败");
            }
        }
        String finalTagsJson = tagsJson;
        List<InfluencerContent> contents = this.contentRepository.findAllById(ids);
        contents.forEach(content -> content.setTags(finalTagsJson));
        this.contentRepository.saveAll(contents);
    }

    @Transactional
    public void deleteByTaskGroupId(String taskGroupId) {
        if (taskGroupId == null || taskGroupId.trim().isEmpty()) {
            throw new RuntimeException("任务组 ID 不能为空");
        }
        List<InfluencerContent> contents = this.contentRepository.findByTaskGroupId(taskGroupId);
        if (contents.isEmpty()) {
            return;
        }
        for (InfluencerContent content : contents) {
            this.cleanupContentFiles(content);
        }
        this.contentRepository.deleteAll(contents);
        log.info("Deleted task group: taskGroupId={}, count={}", taskGroupId, contents.size());
    }

    private void cleanupContentFiles(InfluencerContent content) {
        if (content.getFilePath() != null) {
            try {
                StorageService storage = this.storageServiceFactory.getStorageService();
                storage.delete(content.getFilePath());
                if (content.getThumbnailPath() != null) {
                    storage.delete(content.getThumbnailPath());
                }
            } catch (Exception e) {
                log.warn("Failed to delete files for content: {}", content.getId(), e);
            }
        }
    }

    public Map<String, Object> getStorageStats() {
        ContentStorageConfig config = this.storageConfigRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("未找到启用的存储配置"));
        StorageService storage = this.storageServiceFactory.getStorageService();
        Map<String, ?> metrics = storage.getStorageMetrics();
        Long used = (Long) metrics.get("used");
        if ((used == null || used == 0L) && (used = this.contentRepository.sumTotalFileSize()) == null) {
            used = 0L;
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("used", used);
        Long total = (Long) metrics.get("total");
        if (total == null || total <= 0L) {
            total = config.getTotalQuota();
        }
        stats.put("total", total);
        stats.put("type", config.getStorageType());
        return stats;
    }

    public String getFileUrl(Long contentId) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        if (content.getFilePath() == null) {
            return null;
        }
        StorageService storage = this.storageServiceFactory.getStorageService();
        return storage.getPublicUrl(content.getFilePath());
    }

    public String getThumbnailUrl(Long contentId) {
        InfluencerContent content = this.contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("内容不存在: " + contentId));
        if (content.getThumbnailPath() == null) {
            return null;
        }
        StorageService storage = this.storageServiceFactory.getStorageService();
        return storage.getPublicUrl(content.getThumbnailPath());
    }

    private ContentDto toDto(InfluencerContent entity) {
        ContentDto dto = new ContentDto();
        dto.setId(entity.getId());
        dto.setInfluencerId(entity.getInfluencerId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPlatform(entity.getPlatform());
        dto.setPostType(entity.getPostType());
        dto.setContentType(entity.getContentType());
        dto.setPostUrl(entity.getPostUrl());
        dto.setFilePath(entity.getFilePath());
        dto.setFileName(entity.getFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setFileType(entity.getFileType());
        dto.setThumbnailPath(entity.getThumbnailPath());
        dto.setDuration(entity.getDuration());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setReviewerId(entity.getReviewerId());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setReviewNote(entity.getReviewNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSocialDataUpdatedAt(entity.getSocialDataUpdatedAt());
        dto.setTaskGroupId(entity.getTaskGroupId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setOwner(entity.getOwner());
        dto.setIsCommercial(entity.getIsCommercial());
        dto.setRemark(entity.getRemark());
        dto.setPublishDate(entity.getPublishDate());
        dto.setContentGroupIndex(entity.getContentGroupIndex());
        String fullSku = entity.getProductSku();
        if (fullSku != null) {
            String[] skuParts = fullSku.split("[,\uff0c]\\s*");
            if (skuParts.length > 1) {
                StringBuilder fullDisplay = new StringBuilder();
                for (String singleSku : skuParts) {
                    if ((singleSku = singleSku.trim()).isEmpty())
                        continue;
                    String resolved = this.resolveSkuWithVariant(singleSku, entity);
                    if (fullDisplay.length() > 0) {
                        fullDisplay.append(", ");
                    }
                    fullDisplay.append(resolved);
                }
                dto.setProductSku(fullDisplay.toString());
                dto.setVariantTitle(null);
            } else {
                String singleSku = fullSku.trim();
                dto.setProductSku(this.resolveSkuWithVariant(singleSku, entity));
                dto.setVariantTitle(null);
            }
        }
        dto.setViews(entity.getViews());
        dto.setLikes(entity.getLikes());
        dto.setComments(entity.getComments());
        dto.setShares(entity.getShares());
        dto.setSaves(entity.getSaves());
        dto.setEngagementRate(entity.getEngagementRate());
        Influencer influencer = this.influencerRepository.findById(entity.getInfluencerId()).orElse(null);
        if (influencer != null) {
            dto.setInfluencerName(
                    influencer.getNickName() != null ? influencer.getNickName() : influencer.getRealName());
            dto.setInfluencerEmail(influencer.getEmail());
            dto.setDefaultHandle(influencer.getDefaultHandle());
            List<Long> infTagIds = this.parseTagIds(influencer.getTags());
            if (!infTagIds.isEmpty()) {
                try {
                    Map<Long, SystemTag> liaisonTagMap = this.systemTagRepository
                            .findByCategoryAndEnabledTrue("LIAISON", Sort.by("name")).stream()
                            .collect(Collectors.toMap(SystemTag::getId, t -> t));
                    for (Long tagId : infTagIds) {
                        SystemTag liaisonTag = liaisonTagMap.get(tagId);
                        if (liaisonTag == null)
                            continue;
                        dto.setContactPersonName(liaisonTag.getName());
                        break;
                    }
                } catch (Exception e) {
                    log.warn("Failed to resolve liaison tag for influencer: {}", entity.getInfluencerId());
                }
            }
            if (influencer.getOwnerId() != null) {
                try {
                    Map<Long, String> names = this.userServiceClient
                            .getUserNames(Collections.singletonList(influencer.getOwnerId()));
                    // 仅当目前 ownerName 还是空时才用红人的代替一下，防止覆盖内容任务本体真实的名称
                    if (dto.getOwnerName() == null || dto.getOwnerName().isBlank()) {
                        dto.setOwnerName(names.getOrDefault(influencer.getOwnerId(), ""));
                    }
                } catch (Exception e) {
                    log.warn("Failed to resolve owner name for influencer: {}", entity.getInfluencerId());
                }
            }
        } else if (entity.getInfluencerId() != null) {
            dto.setInfluencerName("[已封存红人]");
        }

        // 核心修复：直接将内容表上的 owner (一般是 username 或 name) 赋值给 ownerName。否则如果没关联红人负责人，这里就会是空
        if (entity.getOwner() != null && !entity.getOwner().isBlank()
                && (dto.getOwnerName() == null || dto.getOwnerName().isBlank())) {
            dto.setOwnerName(entity.getOwner());
        }
        if (entity.getFilePath() != null) {
            try {
                StorageService storage = this.storageServiceFactory.getStorageService();
                dto.setPreviewUrl(storage.getPublicUrl(entity.getFilePath()));
                if (entity.getThumbnailPath() != null) {
                    dto.setThumbnailUrl(storage.getPublicUrl(entity.getThumbnailPath()));
                }
            } catch (Exception e) {
                log.warn("Failed to generate preview URL for content: {}", entity.getId());
            }
        }
        if (entity.getTags() != null) {
            try {
                dto.setTagIds(this.objectMapper.readValue(entity.getTags(), new TypeReference<List<Long>>() {
                }));
            } catch (Exception e) {
                log.warn("Failed to parse tags for content: {}", entity.getId());
            }
        }
        return dto;
    }

    /**
     * 批量版 toDto —— 使用预加载的数据，避免 N+1 查询。
     * 由 getContents() 批量查询后调用。
     */
    private ContentDto toDtoBatch(InfluencerContent entity, Map<Long, Influencer> influencerMap,
            Map<Long, SystemTag> liaisonTagMap, Map<Long, String> ownerNameMap, StorageService storage) {
        ContentDto dto = new ContentDto();
        dto.setId(entity.getId());
        dto.setInfluencerId(entity.getInfluencerId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPlatform(entity.getPlatform());
        dto.setPostType(entity.getPostType());
        dto.setContentType(entity.getContentType());
        dto.setPostUrl(entity.getPostUrl());
        dto.setFilePath(entity.getFilePath());
        dto.setFileName(entity.getFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setFileType(entity.getFileType());
        dto.setThumbnailPath(entity.getThumbnailPath());
        dto.setDuration(entity.getDuration());
        dto.setWidth(entity.getWidth());
        dto.setHeight(entity.getHeight());
        dto.setReviewerId(entity.getReviewerId());
        dto.setReviewedAt(entity.getReviewedAt());
        dto.setReviewNote(entity.getReviewNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSocialDataUpdatedAt(entity.getSocialDataUpdatedAt());
        dto.setTaskGroupId(entity.getTaskGroupId());
        dto.setOrderNo(entity.getOrderNo());
        dto.setOwner(entity.getOwner());
        dto.setIsCommercial(entity.getIsCommercial());
        dto.setRemark(entity.getRemark());
        dto.setPublishDate(entity.getPublishDate());
        dto.setContentGroupIndex(entity.getContentGroupIndex());
        // SKU 解析（仍使用原逻辑，这部分暂不改动）
        String fullSku = entity.getProductSku();
        if (fullSku != null) {
            String[] skuParts = fullSku.split("[,\uff0c]\\s*");
            if (skuParts.length > 1) {
                StringBuilder fullDisplay = new StringBuilder();
                for (String singleSku : skuParts) {
                    if ((singleSku = singleSku.trim()).isEmpty()) continue;
                    String resolved = this.resolveSkuWithVariant(singleSku, entity);
                    if (fullDisplay.length() > 0) fullDisplay.append(", ");
                    fullDisplay.append(resolved);
                }
                dto.setProductSku(fullDisplay.toString());
                dto.setVariantTitle(null);
            } else {
                dto.setProductSku(this.resolveSkuWithVariant(fullSku.trim(), entity));
                dto.setVariantTitle(null);
            }
        }
        dto.setViews(entity.getViews());
        dto.setLikes(entity.getLikes());
        dto.setComments(entity.getComments());
        dto.setShares(entity.getShares());
        dto.setSaves(entity.getSaves());
        dto.setEngagementRate(entity.getEngagementRate());

        // 使用预加载的 influencer 数据（无额外查询）
        Influencer influencer = influencerMap.get(entity.getInfluencerId());
        if (influencer != null) {
            dto.setInfluencerName(
                    influencer.getNickName() != null ? influencer.getNickName() : influencer.getRealName());
            dto.setInfluencerEmail(influencer.getEmail());
            dto.setDefaultHandle(influencer.getDefaultHandle());
            // 使用预加载的联络人标签（无额外查询）
            List<Long> infTagIds = this.parseTagIds(influencer.getTags());
            if (!infTagIds.isEmpty() && !liaisonTagMap.isEmpty()) {
                for (Long tagId : infTagIds) {
                    SystemTag liaisonTag = liaisonTagMap.get(tagId);
                    if (liaisonTag != null) {
                        dto.setContactPersonName(liaisonTag.getName());
                        break;
                    }
                }
            }
            // 使用预加载的 owner 名称（无额外 HTTP 调用）
            if (influencer.getOwnerId() != null) {
                String ownerName = ownerNameMap.get(influencer.getOwnerId());
                if (ownerName != null && (dto.getOwnerName() == null || dto.getOwnerName().isBlank())) {
                    dto.setOwnerName(ownerName);
                }
            }
        } else if (entity.getInfluencerId() != null) {
            dto.setInfluencerName("[已封存红人]");
        }

        if (entity.getOwner() != null && !entity.getOwner().isBlank()
                && (dto.getOwnerName() == null || dto.getOwnerName().isBlank())) {
            dto.setOwnerName(entity.getOwner());
        }
        // 使用预加载的 StorageService（无额外实例化）
        if (entity.getFilePath() != null && storage != null) {
            try {
                dto.setPreviewUrl(storage.getPublicUrl(entity.getFilePath()));
                if (entity.getThumbnailPath() != null) {
                    dto.setThumbnailUrl(storage.getPublicUrl(entity.getThumbnailPath()));
                }
            } catch (Exception e) {
                log.warn("Failed to generate preview URL for content: {}", entity.getId());
            }
        }
        if (entity.getTags() != null) {
            try {
                dto.setTagIds(this.objectMapper.readValue(entity.getTags(), new TypeReference<List<Long>>() {}));
            } catch (Exception e) {
                log.warn("Failed to parse tags for content: {}", entity.getId());
            }
        }
        return dto;
    }

    private List<Long> parseTagIds(String tagsJson) {
        if (tagsJson == null || tagsJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            List<?> rawList = this.objectMapper.readValue(tagsJson, List.class);
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
        } catch (Exception e) {
            log.error("Failed to parse influencer tags JSON: {}", tagsJson, e);
            return new ArrayList<>();
        }
    }

    private String resolveSkuWithVariant(String sku, InfluencerContent entity) {
        if (sku == null || sku.isBlank()) return sku;
        
        String specs = null;
        // 1. 优先使用内容记录上直接存储的变体名称
        if (entity.getVariantTitle() != null && !entity.getVariantTitle().isBlank()) {
            specs = entity.getVariantTitle().replace(" / ", "-").replace("/", "-")
                    .replaceAll("-null", "").replaceAll("-$", "");
        }
        
        // 2. 如果记录上没有，尝试从订单或易仓商品库中查找
        if (specs == null || specs.isBlank()) {
            String variantTitle = this.lookupVariantTitle(sku, entity);
            if (variantTitle != null && !variantTitle.isBlank()) {
                specs = variantTitle.replace(" / ", "-").replace("/", "-")
                        .replaceAll("-null", "").replaceAll("-$", "");
            }
        }
        
        if (specs == null || specs.isBlank()) {
            return sku.replaceAll("-null", "").replaceAll("-$", "");
        }
        
        // 核心修复：检查 SKU 是否已经包含了这些规格（防止重复追加）
        String cleanSku = sku.trim();
        String cleanSpecs = specs.trim();
        
        if (cleanSku.endsWith("-" + cleanSpecs)) {
            // 脏数据二次修复：如果发现 SKU 结尾已经重复出现了两次规格 (如 SKU-A-B-A-B)
            // 则截取掉最后一次重复的部分
            String truncated = cleanSku.substring(0, cleanSku.length() - cleanSpecs.length() - 1);
            if (truncated.endsWith("-" + cleanSpecs)) {
                return truncated.replaceAll("-null", "").replaceAll("-$", "");
            }
            return cleanSku.replaceAll("-null", "").replaceAll("-$", "");
        }
        
        if (cleanSku.equals(cleanSpecs)) {
            return cleanSku.replaceAll("-null", "").replaceAll("-$", "");
        }
        
        return cleanSku + "-" + cleanSpecs;
    }

    private String lookupVariantTitle(String sku, InfluencerContent entity) {
        if (entity.getOrderNo() != null && !entity.getOrderNo().isBlank()) {
            try {
                String vt;
                List<?> results = this.entityManager.createNativeQuery(
                        "SELECT i.variant_title FROM influencer_sample_order_item i JOIN influencer_sample_order o ON i.sample_order_id = o.id WHERE (o.order_name = :orderName OR o.order_name = CONCAT('#', :orderName) OR :orderName = CONCAT('#', o.order_name)) AND (i.sku = :sku OR i.sku LIKE CONCAT(:sku, '-%') OR i.sku LIKE CONCAT(:sku, ' %')) AND i.variant_title IS NOT NULL AND i.variant_title != 'Default Title' LIMIT 1")
                        .setParameter("orderName", entity.getOrderNo()).setParameter("sku", sku).getResultList();
                if (!results.isEmpty() && results.get(0) != null
                        && !(vt = results.get(0).toString().trim()).isBlank()) {
                    return vt;
                }
            } catch (Exception e) {
                log.debug("Failed to lookup variantTitle from order items for sku={}", sku);
            }
        }
        try {
            List<?> variants = this.entityManager
                    .createNativeQuery(
                            "SELECT option1, option2, option3 FROM eccang_product_variants WHERE sku = :sku LIMIT 1")
                    .setParameter("sku", sku).getResultList();
            if (!variants.isEmpty()) {
                Object[] row = (Object[]) variants.get(0);
                StringBuilder sb = new StringBuilder();
                for (Object o : row) {
                    String opt;
                    if (o == null || (opt = o.toString().trim()).isBlank() || "Default Title".equalsIgnoreCase(opt))
                        continue;
                    if (sb.length() > 0) {
                        sb.append(" / ");
                    }
                    sb.append(opt);
                }
                if (sb.length() > 0) {
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            log.debug("Failed to lookup variant options from product variants for sku={}", sku);
        }
        return null;
    }

    public ContentService(InfluencerContentRepository contentRepository, InfluencerRepository influencerRepository,
            ContentStorageConfigRepository storageConfigRepository, StorageServiceFactory storageServiceFactory,
            ObjectMapper objectMapper, UserServiceClient userServiceClient, SystemTagRepository systemTagRepository,
            EntityManager entityManager, MediaProcessingService mediaProcessingService,
            SocialMediaRepository socialMediaRepository, JdbcTemplate jdbcTemplate) {
        this.contentRepository = contentRepository;
        this.influencerRepository = influencerRepository;
        this.storageConfigRepository = storageConfigRepository;
        this.storageServiceFactory = storageServiceFactory;
        this.objectMapper = objectMapper;
        this.userServiceClient = userServiceClient;
        this.systemTagRepository = systemTagRepository;
        this.entityManager = entityManager;
        this.mediaProcessingService = mediaProcessingService;
        this.socialMediaRepository = socialMediaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
}
