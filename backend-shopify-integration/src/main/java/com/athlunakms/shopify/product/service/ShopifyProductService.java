package com.athlunakms.shopify.product.service;

import com.athlunakms.shopify.common.client.ShopifyApiClient;
import com.athlunakms.shopify.common.security.EncryptionService;
import com.athlunakms.shopify.order.dto.SyncProgressDto;
import com.athlunakms.shopify.product.dto.ShopifyProductDto;
import com.athlunakms.shopify.product.dto.ShopifyVariantDto;
import com.athlunakms.shopify.product.entity.ShopifyProduct;
import com.athlunakms.shopify.product.entity.ShopifyProductVariant;
import com.athlunakms.shopify.product.repository.ShopifyProductRepository;
import com.athlunakms.shopify.product.repository.ShopifyProductVariantRepository;
import com.athlunakms.shopify.store.entity.ShopifyStore;
import com.athlunakms.shopify.store.repository.ShopifyStoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopifyProductService {
    private static final Logger log = LoggerFactory.getLogger(ShopifyProductService.class);
    private final ShopifyProductRepository productRepository;
    private final ShopifyProductVariantRepository variantRepository;
    private final ShopifyStoreRepository storeRepository;
    private final ShopifyApiClient shopifyApiClient;
    private final EncryptionService encryptionService;
    private final Map<Long, SyncProgressDto> syncProgressMap = new ConcurrentHashMap();
    private final Object syncLock = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Async
    @Transactional
    public void syncProducts(Long storeId) {
        Object object = this.syncLock;
        synchronized (object) {
            SyncProgressDto existingProgress = (SyncProgressDto)this.syncProgressMap.get(storeId);
            if (existingProgress != null && "RUNNING".equals(existingProgress.getStatus())) {
                log.warn("Product sync already running for storeId: {}, skipping", (Object)storeId);
                return;
            }
            SyncProgressDto progress = SyncProgressDto.builder().status("RUNNING").startTime(Long.valueOf(System.currentTimeMillis())).processed(0).total(0).message("正在从 Shopify 获取商品数据...").build();
            this.syncProgressMap.put(storeId, progress);
        }
        SyncProgressDto progress = (SyncProgressDto)this.syncProgressMap.get(storeId);
        log.info("Starting optimized product sync for storeId: {}", (Object)storeId);
        ShopifyStore store = (ShopifyStore)this.storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found"));
        String url = this.encryptionService.decrypt(store.getStoreUrlEncrypted());
        String accessToken = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
        String storeDomain = store.getMyshopifyDomain() != null && !store.getMyshopifyDomain().trim().isEmpty() ? store.getMyshopifyDomain() : url;
        
        JsonNode productsNode = null;
        try {
            productsNode = this.shopifyApiClient.fetchProducts(storeDomain, accessToken);
        } catch (Exception e) {
            log.error("Aborting product sync due to API error: {}", (Object)e.getMessage());
            progress.setStatus("FAILED");
            progress.setMessage("获取 Shopify 商品时发生错误: " + e.getMessage());
            progress.setEndTime(Long.valueOf(System.currentTimeMillis()));
            return;
        }
        
        if (productsNode == null || !productsNode.isArray()) {
            log.warn("No products found for store: {}", (Object)store.getStoreName());
            progress.setStatus("COMPLETED");
            progress.setMessage("未发现商品或同步完成 (数据为空)");
            progress.setEndTime(Long.valueOf(System.currentTimeMillis()));
            return;
        }
        
        int totalCount = productsNode.size();
        progress.setTotal(totalCount);
        progress.setMessage(String.format("开始同步 %d 个商品...", totalCount));
        
        // 1. 批量获取本地商品索引
        List<ShopifyProduct> localProducts = this.productRepository.findByStoreId(storeId);
        Map<Long, ShopifyProduct> existingProductsMap = localProducts.stream()
            .collect(Collectors.toMap(ShopifyProduct::getShopifyProductId, p -> p, (a, b) -> a));
        
        List<ShopifyProduct> productsToSave = new ArrayList<>();
        Set<Long> syncedShopifyIds = new HashSet<>();
        
        // 2. 第一阶段：映射并保存商品主体
        for (JsonNode productNode : productsNode) {
            try {
                Long shopifyProductId = productNode.get("legacyResourceId").asLong();
                syncedShopifyIds.add(shopifyProductId);
                ShopifyProduct product = existingProductsMap.getOrDefault(shopifyProductId, new ShopifyProduct());
                this.mapJsonToEntity(productNode, product, storeId, storeDomain);
                productsToSave.add(product);
            } catch (Exception e) {
                log.error("Error mapping product node: {}", productNode.path("title").asText(), e);
            }
        }
        
        // 批量保存商品以获取 ID (对于新商品)
        List<ShopifyProduct> savedProductsList = this.productRepository.saveAll(productsToSave);
        Map<Long, ShopifyProduct> savedProductsMap = savedProductsList.stream()
            .collect(Collectors.toMap(ShopifyProduct::getShopifyProductId, p -> p));
        
        // 3. 第二阶段：预取所有相关变体并进行批量处理
        List<Long> productIds = savedProductsList.stream().map(ShopifyProduct::getId).collect(Collectors.toList());
        List<ShopifyProductVariant> allExistingVariants = this.variantRepository.findByProductIdIn(productIds);
        Map<Long, ShopifyProductVariant> existingVariantsMap = allExistingVariants.stream()
            .collect(Collectors.toMap(ShopifyProductVariant::getShopifyVariantId, v -> v, (a, b) -> a));
        
        List<ShopifyProductVariant> allVariantsToSave = new ArrayList<>();
        List<ShopifyProductVariant> allVariantsToDelete = new ArrayList<>();
        
        // 变体按产品 ID 分组，方便后续查找待删除项
        Map<Long, List<ShopifyProductVariant>> variantsByProductId = allExistingVariants.stream()
            .collect(Collectors.groupingBy(ShopifyProductVariant::getProductId));
            
        int processedCount = 0;
        for (JsonNode productNode : productsNode) {
            Long shopifyProductId = productNode.get("legacyResourceId").asLong();
            ShopifyProduct product = savedProductsMap.get(shopifyProductId);
            if (product == null) continue;
            
            this.processVariantsInBatch(productNode, product, existingVariantsMap, variantsByProductId.getOrDefault(product.getId(), new ArrayList<>()), allVariantsToSave, allVariantsToDelete);
            
            processedCount++;
            progress.setProcessed(processedCount);
            progress.setSuccess(processedCount); // 简化统计
            if (processedCount % 50 == 0 || processedCount == totalCount) {
                progress.setMessage(String.format("正在处理变体数据: %d / %d", processedCount, totalCount));
            }
        }
        
        // 批量执行变体操作
        if (!allVariantsToSave.isEmpty()) {
            this.variantRepository.saveAll(allVariantsToSave);
        }
        if (!allVariantsToDelete.isEmpty()) {
            this.variantRepository.deleteAll(allVariantsToDelete);
        }
        
        // 4. 处理已在 Shopify 删除的本地商品 (标记为疑似删除)
        int markedCount = 0;
        for (ShopifyProduct lp : localProducts) {
            if (!syncedShopifyIds.contains(lp.getShopifyProductId()) && 
                !"suspected_deleted".equals(lp.getStatus()) && !"deleted".equals(lp.getStatus())) {
                lp.setStatus("suspected_deleted");
                this.productRepository.save(lp);
                markedCount++;
            }
        }
        
        log.info("Synced products for storeId: {}. Processed {}, Marked {} as suspected_deleted.", storeId, totalCount, markedCount);
        progress.setStatus("COMPLETED");
        progress.setMessage(String.format("同步完成: 已更新 %d 个商品的详情与变体", totalCount));
        progress.setEndTime(Long.valueOf(System.currentTimeMillis()));
    }

    public SyncProgressDto getSyncProgress(Long storeId) {
        return (SyncProgressDto)this.syncProgressMap.get(storeId);
    }

    public void clearSyncProgress(Long storeId) {
        this.syncProgressMap.remove(storeId);
    }

    @Transactional
    public void syncProductsIncremental(Long storeId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Starting incremental product sync for storeId: {}, time range: {} to {}", new Object[]{storeId, startTime, endTime});
        ShopifyStore store = (ShopifyStore)this.storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found"));
        String url = this.encryptionService.decrypt(store.getStoreUrlEncrypted());
        String accessToken = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
        String storeDomain = store.getMyshopifyDomain() != null && !store.getMyshopifyDomain().trim().isEmpty() ? store.getMyshopifyDomain() : url;
        String startTimeUtc = startTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String endTimeUtc = endTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        log.info("Fetching products updated between {} and {}", (Object)startTimeUtc, (Object)endTimeUtc);
        JsonNode productsNode = this.shopifyApiClient.fetchProductsWithTimeFilter(storeDomain, accessToken, startTimeUtc, endTimeUtc);
        if (productsNode == null || !productsNode.isArray()) {
            log.info("No products found in time range for store: {}", (Object)store.getStoreName());
            return;
        }
        
        // 增量同步也使用优化逻辑
        List<ShopifyProduct> productsToSave = new ArrayList<>();
        for (JsonNode productNode : productsNode) {
            Long shopifyProductId = productNode.get("legacyResourceId").asLong();
            ShopifyProduct product = this.productRepository.findByShopifyProductId(shopifyProductId).orElse(new ShopifyProduct());
            this.mapJsonToEntity(productNode, product, storeId, storeDomain);
            productsToSave.add(product);
        }
        List<ShopifyProduct> savedProducts = this.productRepository.saveAll(productsToSave);
        Map<Long, ShopifyProduct> savedProductsMap = savedProducts.stream()
            .collect(Collectors.toMap(ShopifyProduct::getShopifyProductId, p -> p));
            
        List<Long> productIds = savedProducts.stream().map(ShopifyProduct::getId).collect(Collectors.toList());
        List<ShopifyProductVariant> existingVariants = this.variantRepository.findByProductIdIn(productIds);
        Map<Long, ShopifyProductVariant> existingVariantsMap = existingVariants.stream()
            .collect(Collectors.toMap(ShopifyProductVariant::getShopifyVariantId, v -> v, (a, b) -> a));
            
        List<ShopifyProductVariant> allToSave = new ArrayList<>();
        List<ShopifyProductVariant> allToDelete = new ArrayList<>();
        
        // 变体按产品 ID 分组，方便后续查找待删除项
        Map<Long, List<ShopifyProductVariant>> variantsByProductId = existingVariants.stream()
            .collect(Collectors.groupingBy(ShopifyProductVariant::getProductId));

        for (JsonNode productNode : productsNode) {
            Long shopifyProductId = productNode.get("legacyResourceId").asLong();
            ShopifyProduct product = savedProductsMap.get(shopifyProductId);
            if (product != null) {
                this.processVariantsInBatch(productNode, product, existingVariantsMap, variantsByProductId.getOrDefault(product.getId(), new ArrayList<>()), allToSave, allToDelete);
            }
        }
        
        if (!allToSave.isEmpty()) this.variantRepository.saveAll(allToSave);
        if (!allToDelete.isEmpty()) this.variantRepository.deleteAll(allToDelete);
        
        log.info("Incremental product sync completed for storeId: {}, synced {} products", (Object)storeId, (Object)savedProducts.size());
    }

    private void processVariantsInBatch(JsonNode productNode, ShopifyProduct product, 
                                      Map<Long, ShopifyProductVariant> existingVariantsMap,
                                      List<ShopifyProductVariant> productExistingVariants,
                                      List<ShopifyProductVariant> allToSave, 
                                      List<ShopifyProductVariant> allToDelete) {
        JsonNode variantsEdges = productNode.path("variants").path("edges");
        if (!variantsEdges.isArray()) return;
        
        Set<Long> payloadVariantIds = new HashSet<>();
        for (JsonNode edge : variantsEdges) {
            JsonNode variantNode = edge.path("node");
            Long shopifyVariantId = variantNode.path("legacyResourceId").asLong();
            if (shopifyVariantId == 0L) continue;
            
            payloadVariantIds.add(shopifyVariantId);
            ShopifyProductVariant variant = existingVariantsMap.getOrDefault(shopifyVariantId, new ShopifyProductVariant());
            
            this.mapJsonToVariantEntity(variantNode, variant, product);
            allToSave.add(variant);
        }
        
        // 处理待删除的变体 (在 DB 中但不在 payload 中)
        for (ShopifyProductVariant existing : productExistingVariants) {
            if (!payloadVariantIds.contains(existing.getShopifyVariantId())) {
                allToDelete.add(existing);
            }
        }
    }

    private void mapJsonToVariantEntity(JsonNode variantNode, ShopifyProductVariant variant, ShopifyProduct product) {
        Long shopifyVariantId = variantNode.path("legacyResourceId").asLong();
        variant.setProductId(product.getId());
        variant.setShopifyProductId(product.getShopifyProductId());
        variant.setShopifyVariantId(shopifyVariantId);
        variant.setTitle(variantNode.path("title").asText());
        variant.setSku(variantNode.path("sku").asText());
        variant.setBarcode(variantNode.path("barcode").asText());
        variant.setPrice(this.parseDecimal(variantNode.path("price").asText("0")));
        variant.setCompareAtPrice(variantNode.hasNonNull("compareAtPrice") ? this.parseDecimal(variantNode.get("compareAtPrice").asText()) : null);
        variant.setInventoryQuantity(Integer.valueOf(variantNode.path("inventoryQuantity").asInt(0)));
        variant.setPosition(Integer.valueOf(variantNode.path("position").asInt(1)));
        
        JsonNode selectedOptions = variantNode.path("selectedOptions");
        if (selectedOptions.isArray()) {
            if (selectedOptions.size() > 0) variant.setOption1(selectedOptions.get(0).path("value").asText(null));
            if (selectedOptions.size() > 1) variant.setOption2(selectedOptions.get(1).path("value").asText(null));
            if (selectedOptions.size() > 2) variant.setOption3(selectedOptions.get(2).path("value").asText(null));
        }
        
        if (product.getOnlineStoreUrl() != null && !product.getOnlineStoreUrl().isEmpty()) {
            variant.setOnlineStoreUrl(product.getOnlineStoreUrl() + "?variant=" + shopifyVariantId);
        }
        
        JsonNode imageNode = variantNode.path("image");
        if (!imageNode.isMissingNode() && !imageNode.isNull() && imageNode.has("url")) {
            variant.setImageUrl(imageNode.path("url").asText());
        } else {
            // 回退逻辑：如果变体没有图片，使用商品主图
            variant.setImageUrl(product.getMainImage());
        }
        
        JsonNode weightNode = variantNode.path("inventoryItem").path("measurement").path("weight");
        if (!weightNode.isMissingNode() && !weightNode.isNull() && !weightNode.isEmpty()) {
            variant.setWeight(this.parseDecimal(weightNode.path("value").asText("0")));
            variant.setWeightUnit(weightNode.path("unit").asText("kg"));
        } else {
            variant.setWeight(BigDecimal.ZERO);
            variant.setWeightUnit("kg");
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (variantNode.hasNonNull("createdAt")) {
            variant.setCreatedAt(LocalDateTime.parse(variantNode.get("createdAt").asText(), formatter));
        }
        if (variantNode.hasNonNull("updatedAt")) {
            variant.setUpdatedAt(LocalDateTime.parse(variantNode.get("updatedAt").asText(), formatter));
        }
    }

    private BigDecimal parseDecimal(String value) {
        try {
            return new BigDecimal(value);
        }
        catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<String> getAllDistinctSkus() {
        return this.variantRepository.findDistinctSkus();
    }

    public List<ShopifyProductDto> searchProducts(Long storeId, String keyword, String status) {
        Specification<ShopifyProduct> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            if (storeId != null) {
                predicates.add(cb.equal((Expression)root.get("storeId"), (Object)storeId));
            }
            if (status != null && !"all".equalsIgnoreCase(status)) {
                predicates.add(cb.equal((Expression)root.get("status"), (Object)status));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower((Expression)root.get("title")), pattern);
                Predicate spuPredicate = cb.like(cb.lower((Expression)root.get("spu")), pattern);
                Subquery subquery = query.subquery(Long.class);
                Root variantRoot = subquery.from(ShopifyProductVariant.class);
                subquery.select((Expression)variantRoot.get("productId"));
                Predicate skuMatch = cb.like(cb.lower((Expression)variantRoot.get("sku")), pattern);
                Predicate vTitleMatch = cb.like(cb.lower((Expression)variantRoot.get("title")), pattern);
                subquery.where((Expression)cb.or((Expression)skuMatch, (Expression)vTitleMatch));
                predicates.add(cb.or(new Predicate[]{titlePredicate, spuPredicate, root.get("id").in(new Expression[]{subquery})}));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        List<ShopifyProduct> entities = this.productRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        return this.convertToDtoList(entities);
    }

    /**
     * 分页查询商品，每页只返回指定数量的商品，大幅减少数据传输和变体查询量
     */
    public Page<ShopifyProductDto> searchProductsPaged(Long storeId, String keyword, String status, int page, int size) {
        Specification<ShopifyProduct> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            if (storeId != null) {
                predicates.add(cb.equal((Expression)root.get("storeId"), (Object)storeId));
            }
            if (status != null && !"all".equalsIgnoreCase(status)) {
                predicates.add(cb.equal((Expression)root.get("status"), (Object)status));
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower((Expression)root.get("title")), pattern);
                Predicate spuPredicate = cb.like(cb.lower((Expression)root.get("spu")), pattern);
                Subquery subquery = query.subquery(Long.class);
                Root variantRoot = subquery.from(ShopifyProductVariant.class);
                subquery.select((Expression)variantRoot.get("productId"));
                Predicate skuMatch = cb.like(cb.lower((Expression)variantRoot.get("sku")), pattern);
                Predicate vTitleMatch = cb.like(cb.lower((Expression)variantRoot.get("title")), pattern);
                subquery.where((Expression)cb.or((Expression)skuMatch, (Expression)vTitleMatch));
                predicates.add(cb.or(new Predicate[]{titlePredicate, spuPredicate, root.get("id").in(new Expression[]{subquery})}));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ShopifyProduct> entityPage = this.productRepository.findAll(spec, pageable);
        List<ShopifyProductDto> dtos = this.convertToDtoList(entityPage.getContent());
        return new PageImpl<>(dtos, pageable, entityPage.getTotalElements());
    }

    public List<ShopifyProductDto> getAllProducts() {
        List<ShopifyProduct> entities = this.productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return this.convertToDtoList(entities);
    }

    public List<ShopifyProductDto> getProductsByStoreId(Long storeId) {
        List<ShopifyProduct> entities = this.productRepository.findByStoreId(storeId);
        return this.convertToDtoList(entities);
    }

    private List<ShopifyProductDto> convertToDtoList(List<ShopifyProduct> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<ShopifyProductDto>();
        }
        List<Long> productIds = entities.stream().map(ShopifyProduct::getId).collect(Collectors.toList());
        Set<Long> storeIds = entities.stream().map(ShopifyProduct::getStoreId).collect(Collectors.toSet());
        List<ShopifyProductVariant> allVariants = this.variantRepository.findByProductIdIn(productIds);
        Map<Long, List<ShopifyProductVariant>> variantsMap = allVariants.stream().collect(Collectors.groupingBy(ShopifyProductVariant::getProductId));
        List<ShopifyStore> allStores = this.storeRepository.findAllById(storeIds);
        Map<Long, String> storeNamesMap = allStores.stream().collect(Collectors.toMap(ShopifyStore::getId, ShopifyStore::getStoreName, (a, b) -> a));
        return entities.stream().map(entity -> {
            ShopifyProductDto dto = new ShopifyProductDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setStoreName(storeNamesMap.get(entity.getStoreId()));
            List<ShopifyProductVariant> variants = variantsMap.getOrDefault(entity.getId(), new ArrayList<>());
            List variantDtos = variants.stream().map(v -> this.convertVariantToDto(v, entity)).collect(Collectors.toList());
            dto.setVariants(variantDtos);
            if (!variants.isEmpty()) {
                BigDecimal minPrice = variants.stream().map(ShopifyProductVariant::getPrice).filter(p -> p != null).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = variants.stream().map(ShopifyProductVariant::getPrice).filter(p -> p != null).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                dto.setMinPrice(minPrice);
                dto.setMaxPrice(maxPrice);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ShopifyProductDto> getProductsByGids(List<String> gids) {
        if (gids == null || gids.isEmpty()) {
            return new ArrayList<ShopifyProductDto>();
        }
        List<Long> ids = gids.stream().map(gid -> {
            try {
                return Long.parseLong(gid.substring(gid.lastIndexOf(47) + 1));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
        List<ShopifyProduct> entities = this.productRepository.findByShopifyProductIdIn(ids);
        return this.convertToDtoList(entities);
    }

    private ShopifyVariantDto convertVariantToDto(ShopifyProductVariant entity, ShopifyProduct product) {
        ShopifyVariantDto dto = new ShopifyVariantDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setSpu(product.getSpu());
        dto.setProductTitle(product.getTitle());
        return dto;
    }

    private void mapJsonToEntity(JsonNode node, ShopifyProduct product, Long storeId, String storeDomain) {
        Long shopifyProductId = node.get("legacyResourceId").asLong();
        product.setShopifyProductId(shopifyProductId);
        product.setStoreId(storeId);
        product.setTitle(node.path("title").asText());
        product.setBodyHtml(node.path("bodyHtml").asText());
        product.setProductType(node.path("productType").asText());
        product.setVendor(node.path("vendor").asText());
        String handle = node.path("handle").asText();
        product.setHandle(handle);
        String apiOnlineStoreUrl = node.path("onlineStoreUrl").asText(null);
        if (apiOnlineStoreUrl != null && !apiOnlineStoreUrl.isEmpty()) {
            product.setOnlineStoreUrl(apiOnlineStoreUrl);
        } else if (handle != null && !handle.isEmpty()) {
            product.setOnlineStoreUrl("https://" + storeDomain + "/products/" + handle);
        }
        product.setSpu(String.valueOf(shopifyProductId));
        int totalInv = 0;
        JsonNode variantsEdges = node.path("variants").path("edges");
        if (variantsEdges.isArray()) {
            for (JsonNode edge : variantsEdges) {
                totalInv += edge.path("node").path("inventoryQuantity").asInt(0);
            }
        }
        product.setTotalInventory(Integer.valueOf(totalInv));
        String shopifyStatus = node.path("status").asText("ACTIVE");
        if ("ARCHIVED".equalsIgnoreCase(shopifyStatus)) {
            product.setStatus("archived");
        } else if ("DRAFT".equalsIgnoreCase(shopifyStatus)) {
            product.setStatus("draft");
        } else if ("ACTIVE".equalsIgnoreCase(shopifyStatus)) {
            if (totalInv <= 0) {
                product.setStatus("out_of_stock");
            } else {
                product.setStatus("active");
            }
        } else {
            product.setStatus(shopifyStatus.toLowerCase());
        }
        JsonNode imagesEdges = node.path("images").path("edges");
        StringBuilder spuImagesSb = new StringBuilder();
        if (imagesEdges.isArray()) {
            for (JsonNode edge : imagesEdges) {
                if (spuImagesSb.length() > 0) {
                    spuImagesSb.append(",");
                }
                spuImagesSb.append(edge.path("node").path("url").asText());
            }
            if (imagesEdges.size() > 0) {
                product.setMainImage(imagesEdges.get(0).path("node").path("url").asText());
            }
        }
        product.setSpuImages(spuImagesSb.length() > 0 ? spuImagesSb.toString() : null);
        product.setOptionsJson(node.path("options").toString());
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if (node.hasNonNull("createdAt")) {
            product.setCreatedAt(LocalDateTime.parse(node.get("createdAt").asText(), formatter));
        }
        if (node.hasNonNull("updatedAt")) {
            product.setUpdatedAt(LocalDateTime.parse(node.get("updatedAt").asText(), formatter));
        }
        if (node.hasNonNull("publishedAt")) {
            product.setPublishedAt(LocalDateTime.parse(node.get("publishedAt").asText(), formatter));
        }
    }

    public ShopifyProductService(ShopifyProductRepository productRepository, ShopifyProductVariantRepository variantRepository, ShopifyStoreRepository storeRepository, ShopifyApiClient shopifyApiClient, EncryptionService encryptionService) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.storeRepository = storeRepository;
        this.shopifyApiClient = shopifyApiClient;
        this.encryptionService = encryptionService;
    }
}


