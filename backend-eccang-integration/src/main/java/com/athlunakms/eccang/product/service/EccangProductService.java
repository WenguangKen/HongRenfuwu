package com.athlunakms.eccang.product.service;

import com.athlunakms.eccang.common.client.EccangApiClient;
import com.athlunakms.eccang.common.util.EccangJsonFieldHelper;
import com.athlunakms.eccang.config.entity.EccangConfig;
import com.athlunakms.eccang.config.repository.EccangConfigRepository;
import com.athlunakms.eccang.order.dto.SyncProgressDto;
import com.athlunakms.eccang.product.dto.EccangProductDto;
import com.athlunakms.eccang.product.dto.EccangVariantDto;
import com.athlunakms.eccang.product.entity.EccangProduct;
import com.athlunakms.eccang.product.entity.EccangProductVariant;
import com.athlunakms.eccang.product.repository.EccangProductRepository;
import com.athlunakms.eccang.product.repository.EccangProductVariantRepository;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;
import com.athlunakms.eccang.store.entity.UserStoreAllocation;
import com.athlunakms.eccang.store.repository.UserStoreAllocationRepository;
import com.athlunakms.eccang.common.security.EncryptionService;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EccangProductService {
    private static final Logger log = LoggerFactory.getLogger(EccangProductService.class);
    
    @Value("${eccang.sync.page-size:1000}")
    private int syncPageSize;

    @Value("${eccang.sync.primary-source:amazon-listing}")
    private String primarySource;

    private final EccangProductRepository productRepository;
    private final EccangProductVariantRepository variantRepository;
    private final EccangStoreRepository storeRepository;
    private final EccangApiClient eccangApiClient;
    private final EccangConfigRepository configRepository;
    private final UserStoreAllocationRepository userStoreAllocationRepository;
    private final EncryptionService encryptionService;
    private final Map<Long, SyncProgressDto> syncProgressMap = new ConcurrentHashMap<>();
    private final Object syncLock = new Object();

    public EccangProductService(EccangProductRepository productRepository, 
                                EccangProductVariantRepository variantRepository, 
                                EccangStoreRepository storeRepository, 
                                EccangApiClient eccangApiClient, 
                                EccangConfigRepository configRepository,
                                UserStoreAllocationRepository userStoreAllocationRepository,
                                EncryptionService encryptionService) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.storeRepository = storeRepository;
        this.eccangApiClient = eccangApiClient;
        this.configRepository = configRepository;
        this.userStoreAllocationRepository = userStoreAllocationRepository;
        this.encryptionService = encryptionService;
    }

    @Async
    @Transactional
    public void syncProducts(Long storeId) {
        synchronized (this.syncLock) {
            SyncProgressDto existingProgress = this.syncProgressMap.get(storeId);
            if (existingProgress != null && "RUNNING".equals(existingProgress.getStatus())) {
                log.warn("Product sync already running for storeId: {}, skipping", storeId);
                return;
            }
            SyncProgressDto progress = SyncProgressDto.builder()
                .status("RUNNING")
                .startTime(System.currentTimeMillis())
                .processed(0)
                .total(0)
                .message("正在从易仓获取商品数据...")
                .build();
            this.syncProgressMap.put(storeId, progress);
        }
        
        SyncProgressDto progress = this.syncProgressMap.get(storeId);
        log.info("Starting optimized product sync for storeId: {}", storeId);
        
        try {
            EccangStore store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found"));
                
            List<EccangConfig> configs = configRepository.findAll();
            if (configs.isEmpty()) {
                throw new RuntimeException("未配置全局易仓 API 密钥，请先在设置中配置");
            }
            EccangConfig config = configs.get(0);

            Map<String, String> wmsCategoryMap = new HashMap<>();
            Map<String, String> wmsImageMap = new HashMap<>();
            if ("amazon-listing".equalsIgnoreCase(primarySource)) {
                fetchWmsSkuData(config, wmsCategoryMap, wmsImageMap, progress);
            }

            int page = 1;
            int pageSize = syncPageSize;
            List<JsonNode> allProductsData = new ArrayList<>();
            while (true) {
                progress.setMessage(String.format("正在从易仓获取商品数据，正在拉取第 %d 页 (每页 %d 条，累计已拉取 %d 条)...", page, pageSize, allProductsData.size()));
                List<JsonNode> pageData;
                if ("amazon-listing".equalsIgnoreCase(primarySource)) {
                    pageData = eccangApiClient.getAmazonListing(config.getAppKey(), encryptionService.decrypt(config.getAppSecretEncrypted()), page, pageSize, null, null, null, null);
                } else {
                    pageData = eccangApiClient.getWmsProductList(config.getAppKey(), encryptionService.decrypt(config.getAppSecretEncrypted()), page, pageSize);
                }
                if (pageData == null || pageData.isEmpty()) {
                    break;
                }
                allProductsData.addAll(pageData);
                if (pageData.size() < pageSize) {
                    break;
                }
                page++;
            }
            
            if (allProductsData.isEmpty()) {
                progress.setStatus("COMPLETED");
                progress.setMessage("未发现商品或同步完成 (数据为空)");
                progress.setEndTime(System.currentTimeMillis());
                return;
            }
            
            int totalCount = allProductsData.size();
            progress.setTotal(totalCount);
            progress.setMessage(String.format("开始同步 %d 个商品...", totalCount));
            
            List<EccangProduct> productsToSave = new ArrayList<>();
            Set<String> syncedProductKeys = new HashSet<>();
            List<EccangProduct> existingProducts = productRepository.findByStoreId(storeId);
            
            if ("amazon-listing".equalsIgnoreCase(primarySource)) {
                // Group by userAccount + "|" + parentAsin
                Map<String, List<JsonNode>> groupedNodes = allProductsData.stream()
                    .collect(Collectors.groupingBy(node -> {
                        String userAccount = EccangJsonFieldHelper.firstText(node, "user_account", "userAccount", "platform_account", "platformAccount");
                        if (!StringUtils.hasText(userAccount)) {
                            userAccount = store.getStoreName();
                        }
                        String parentAsin = EccangJsonFieldHelper.resolveAmazonListingSpu(node);
                        if (!StringUtils.hasText(parentAsin)) {
                            parentAsin = EccangJsonFieldHelper.firstText(node, "asin", "parent_asin", "seller_sku", "sellerSku");
                        }
                        return userAccount + "|" + parentAsin;
                    }));

                Map<String, EccangProduct> existingProductsMap = existingProducts.stream()
                    .collect(Collectors.toMap(p -> p.getUserAccount() + "|" + p.getParentAsin(), p -> p, (a, b) -> a));

                for (Map.Entry<String, List<JsonNode>> entry : groupedNodes.entrySet()) {
                    String groupKey = entry.getKey();
                    List<JsonNode> variantNodes = entry.getValue();
                    String[] parts = groupKey.split("\\|", 2);
                    String userAccount = parts[0];
                    String parentAsin = parts[1];

                    syncedProductKeys.add(parentAsin);

                    EccangProduct product = existingProductsMap.getOrDefault(groupKey, new EccangProduct());
                    product.setStoreId(storeId);
                    product.setUserAccount(userAccount);
                    product.setParentAsin(parentAsin);
                    
                    JsonNode firstNode = variantNodes.get(0);
                    product.setTitle(EccangJsonFieldHelper.resolveTitleFromNodes(variantNodes));
                    product.setBrand(EccangJsonFieldHelper.firstText(firstNode, "brand", "brand_name"));
                    product.setSite(EccangJsonFieldHelper.firstText(firstNode, "site", "site_code"));
                    
                    String asin = EccangJsonFieldHelper.firstText(firstNode, "asin");
                    product.setAsin(asin != null ? asin : parentAsin);
                    
                    String asinUrl = "https://www.amazon.com/dp/" + product.getAsin();
                    product.setAsinUrl(asinUrl);
                    product.setAsinType(3); // Parent ASIN/group

                    boolean hasActiveVariant = false;
                    for (JsonNode vn : variantNodes) {
                        String varStatusText = EccangJsonFieldHelper.firstText(vn, "item_status", "itemStatus", "status", "product_status");
                        if (varStatusText != null && ("active".equalsIgnoreCase(varStatusText) || "1".equals(varStatusText) || "Active".equalsIgnoreCase(varStatusText))) {
                            hasActiveVariant = true;
                            break;
                        }
                    }
                    product.setStatus(hasActiveVariant ? "active" : "inactive");

                    product.setCurrency(EccangJsonFieldHelper.firstText(firstNode, "currency_code", "currencyCode", "currency"));
                    product.setFulfillmentType(EccangJsonFieldHelper.firstText(firstNode, "fulfillment_type", "fulfillmentType"));

                    String rawImage = null;
                    for (JsonNode vn : variantNodes) {
                        String img = EccangJsonFieldHelper.extractImageUrl(vn);
                        if (StringUtils.hasText(img)) {
                            rawImage = img;
                            break;
                        }
                    }
                    if (!StringUtils.hasText(rawImage)) {
                        for (JsonNode vn : variantNodes) {
                            String sku = EccangJsonFieldHelper.firstText(vn, "seller_sku", "sellerSku", "sku", "product_sku");
                            if (StringUtils.hasText(sku)) {
                                String wmsImg = wmsImageMap.get(sku.trim().toUpperCase());
                                if (StringUtils.hasText(wmsImg)) {
                                    rawImage = wmsImg;
                                    break;
                                }
                            }
                        }
                    }
                    String mainImg = EccangJsonFieldHelper.resolveDisplayImage(rawImage, product.getAsin());
                    product.setMainImage(mainImg);
                    if (mainImg != null && mainImg.contains("images-na.ssl-images-amazon.com")) {
                        product.setSmallImageUrl(mainImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL75_"));
                    } else {
                        product.setSmallImageUrl(mainImg);
                    }

                    product.setAttributesJson(EccangJsonFieldHelper.buildAmazonListingAttributesJson(firstNode, variantNodes));

                    int totalFbaInv = 0;
                    for (JsonNode vn : variantNodes) {
                        totalFbaInv += EccangJsonFieldHelper.parseInt(vn, "fba_inventory", "fbaInventory", "quantity", "inventory_quantity");
                    }
                    product.setTotalFbaInventory(totalFbaInv);
                    product.setVariantCount(variantNodes.size());

                    String openDateStr = EccangJsonFieldHelper.firstText(firstNode, "open_date_local", "open_date", "openDateLocal", "openDate", "open_date_local_time");
                    if (openDateStr != null && !openDateStr.isEmpty()) {
                        try {
                            String cleanDate = openDateStr.split("\\+")[0].trim().replace(" ", "T");
                            product.setOpenDateLocal(LocalDateTime.parse(cleanDate));
                        } catch (Exception e) {}
                    }

                    String categoryPath = null;
                    for (JsonNode vn : variantNodes) {
                        String sku = EccangJsonFieldHelper.firstText(vn, "seller_sku", "sellerSku", "sku", "product_sku");
                        if (StringUtils.hasText(sku)) {
                            String cachedPath = wmsCategoryMap.get(sku.trim().toUpperCase());
                            if (StringUtils.hasText(cachedPath)) {
                                categoryPath = cachedPath;
                                break;
                            }
                        }
                    }
                    product.setCategoryPath(categoryPath);

                    productsToSave.add(product);
                }

                List<EccangProduct> savedProductsList = productRepository.saveAll(productsToSave);
                Map<String, EccangProduct> savedProductsMap = savedProductsList.stream()
                    .collect(Collectors.toMap(p -> p.getUserAccount() + "|" + p.getParentAsin(), p -> p));

                List<String> allSkusInBatch = new ArrayList<>();
                for (Map.Entry<String, List<JsonNode>> entry : groupedNodes.entrySet()) {
                    for (JsonNode vn : entry.getValue()) {
                        String sku = EccangJsonFieldHelper.firstText(vn, "seller_sku", "sellerSku", "sku", "product_sku");
                        if (StringUtils.hasText(sku)) {
                            allSkusInBatch.add(sku);
                        }
                    }
                }
                List<EccangProductVariant> existingVariants = allSkusInBatch.isEmpty() ? new ArrayList<>() : variantRepository.findBySkuIn(allSkusInBatch);
                Map<String, EccangProductVariant> existingVariantsMap = existingVariants.stream()
                    .collect(Collectors.toMap(v -> v.getUserAccount() + "|" + v.getSku(), v -> v, (a, b) -> a));

                List<EccangProductVariant> allVariantsToSave = new ArrayList<>();
                int processedCount = 0;
                for (Map.Entry<String, List<JsonNode>> entry : groupedNodes.entrySet()) {
                    String groupKey = entry.getKey();
                    List<JsonNode> variantNodes = entry.getValue();
                    EccangProduct product = savedProductsMap.get(groupKey);
                    if (product == null) continue;

                    for (JsonNode vn : variantNodes) {
                        String sku = EccangJsonFieldHelper.firstText(vn, "seller_sku", "sellerSku", "sku", "product_sku");
                        if (!StringUtils.hasText(sku)) continue;

                        String varKey = product.getUserAccount() + "|" + sku;
                        EccangProductVariant variant = existingVariantsMap.getOrDefault(varKey, new EccangProductVariant());

                        variant.setProductId(product.getId());
                        variant.setSku(sku);
                        variant.setTitle(EccangJsonFieldHelper.resolveAmazonListingTitle(vn));
                        variant.setPrice(parseDecimal(EccangJsonFieldHelper.firstText(vn, "price", "landedPrice", "spUnitPrice", "sp_unit_price")));
                        
                        int fbaInv = EccangJsonFieldHelper.parseInt(vn, "fba_inventory", "fbaInventory");
                        variant.setFbaInventory(fbaInv);
                        variant.setInventoryQuantity(EccangJsonFieldHelper.parseInt(vn, "quantity", "inventory_quantity", "inventoryQuantity"));

                        variant.setUserAccount(product.getUserAccount());
                        
                        String asin = EccangJsonFieldHelper.firstText(vn, "asin");
                        variant.setAsin(asin);
                        variant.setParentAsin(product.getParentAsin());
                        
                        String varAsinUrl = "https://www.amazon.com/dp/" + asin;
                        variant.setAsinUrl(varAsinUrl);
                        variant.setAsinType(asin != null && asin.equals(product.getParentAsin()) ? 3 : 1);

                        String varRawImg = EccangJsonFieldHelper.extractImageUrl(vn);
                        if (!StringUtils.hasText(varRawImg) && StringUtils.hasText(sku)) {
                            String wmsImg = wmsImageMap.get(sku.trim().toUpperCase());
                            if (StringUtils.hasText(wmsImg)) {
                                varRawImg = wmsImg;
                            }
                        }
                        String varImg = EccangJsonFieldHelper.resolveDisplayImage(varRawImg, asin);
                        variant.setImageUrl(varImg);
                        if (varImg != null && varImg.contains("images-na.ssl-images-amazon.com")) {
                            variant.setSmallImageUrl(varImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL75_"));
                        } else {
                            variant.setSmallImageUrl(varImg);
                        }

                        variant.setFulfillmentType(EccangJsonFieldHelper.firstText(vn, "fulfillment_type", "fulfillmentType"));

                        String varStatusText = EccangJsonFieldHelper.firstText(vn, "item_status", "itemStatus", "status", "product_status");
                        if (varStatusText != null && ("active".equalsIgnoreCase(varStatusText) || "1".equals(varStatusText) || "Active".equalsIgnoreCase(varStatusText))) {
                            variant.setStatus("active");
                        } else {
                            variant.setStatus("inactive");
                        }

                        variant.setOption1(EccangJsonFieldHelper.firstText(vn, "color"));
                        variant.setOption2(EccangJsonFieldHelper.firstText(vn, "size"));

                        String varOpenDateStr = EccangJsonFieldHelper.firstText(vn, "open_date_local", "open_date", "openDateLocal", "openDate", "open_date_local_time");
                        if (varOpenDateStr != null && !varOpenDateStr.isEmpty()) {
                            try {
                                String cleanDate = varOpenDateStr.split("\\+")[0].trim().replace(" ", "T");
                                variant.setOpenDateLocal(LocalDateTime.parse(cleanDate));
                            } catch (Exception e) {}
                        }

                        allVariantsToSave.add(variant);
                        
                        processedCount++;
                        progress.setProcessed(processedCount);
                        progress.setSuccess(processedCount);
                        if (processedCount % 10 == 0 || processedCount == totalCount) {
                            progress.setMessage(String.format("正在处理变体数据: %d / %d", processedCount, totalCount));
                        }
                    }
                }

                if (!allVariantsToSave.isEmpty()) {
                    variantRepository.saveAll(allVariantsToSave);
                }
            } else {
                // WMS Sync (fallback)
                Map<String, EccangProduct> existingProductsMap = existingProducts.stream()
                    .collect(Collectors.toMap(EccangProduct::getParentAsin, p -> p, (a, b) -> a));

                Map<String, List<JsonNode>> groupedWmsNodes = allProductsData.stream()
                    .collect(Collectors.groupingBy(node -> {
                        String spu = node.path("product_spu").asText();
                        String sku = node.path("product_sku").asText();
                        return !StringUtils.hasText(spu) ? sku : spu;
                    }));

                for (Map.Entry<String, List<JsonNode>> entry : groupedWmsNodes.entrySet()) {
                    String key = entry.getKey();
                    List<JsonNode> variantNodes = entry.getValue();
                    JsonNode firstNode = variantNodes.get(0);
                    
                    syncedProductKeys.add(key);
                    EccangProduct product = existingProductsMap.getOrDefault(key, new EccangProduct());
                    
                    mapJsonToEntity(firstNode, product, storeId, store.getStoreName(), key);
                    
                    // Aggregate parent status from all variant nodes
                    boolean hasActiveVariant = false;
                    for (JsonNode vn : variantNodes) {
                        String eccangStatus = vn.path("product_status").asText("1");
                        if ("1".equals(eccangStatus) || "active".equalsIgnoreCase(eccangStatus) || "Active".equalsIgnoreCase(eccangStatus)) {
                            hasActiveVariant = true;
                            break;
                        }
                    }
                    product.setStatus(hasActiveVariant ? "active" : "inactive");
                    
                    productsToSave.add(product);
                }
                
                List<EccangProduct> savedProductsList = productRepository.saveAll(productsToSave);
                Map<String, EccangProduct> savedProductsMap = savedProductsList.stream()
                    .collect(Collectors.toMap(EccangProduct::getParentAsin, p -> p));
                    
                List<String> allWmsSkusInBatch = new ArrayList<>();
                for (Map.Entry<String, List<JsonNode>> entry : groupedWmsNodes.entrySet()) {
                    for (JsonNode vn : entry.getValue()) {
                        String sku = vn.path("product_sku").asText();
                        if (StringUtils.hasText(sku)) {
                            allWmsSkusInBatch.add(sku);
                        }
                    }
                }
                List<EccangProductVariant> existingVariants = allWmsSkusInBatch.isEmpty() ? new ArrayList<>() : variantRepository.findBySkuIn(allWmsSkusInBatch);
                Map<String, EccangProductVariant> existingVariantsMap = existingVariants.stream()
                    .collect(Collectors.toMap(EccangProductVariant::getSku, v -> v, (a, b) -> a));
                    
                List<EccangProductVariant> allVariantsToSave = new ArrayList<>();
                int processedCount = 0;
                for (Map.Entry<String, List<JsonNode>> entry : groupedWmsNodes.entrySet()) {
                    String key = entry.getKey();
                    List<JsonNode> variantNodes = entry.getValue();
                    EccangProduct product = savedProductsMap.get(key);
                    if (product == null) continue;
                    
                    for (JsonNode vn : variantNodes) {
                        String sku = vn.path("product_sku").asText();
                        if (!StringUtils.hasText(sku)) continue;
                        
                        EccangProductVariant variant = existingVariantsMap.getOrDefault(sku, new EccangProductVariant());
                        mapJsonToVariantEntity(vn, variant, product);
                        allVariantsToSave.add(variant);
                        
                        processedCount++;
                    }
                    progress.setProcessed(processedCount);
                    progress.setSuccess(processedCount);
                    if (processedCount % 10 == 0 || processedCount == totalCount) {
                        progress.setMessage(String.format("正在处理变体数据: %d / %d", processedCount, totalCount));
                    }
                }
                
                if (!allVariantsToSave.isEmpty()) {
                    variantRepository.saveAll(allVariantsToSave);
                }
            }

            // 标记未出现的为 suspected_deleted
            int markedCount = 0;
            for (EccangProduct lp : existingProducts) {
                if (!syncedProductKeys.contains(lp.getParentAsin()) && 
                    !"suspected_deleted".equals(lp.getStatus()) && !"deleted".equals(lp.getStatus())) {
                    lp.setStatus("suspected_deleted");
                    productRepository.save(lp);
                    markedCount++;
                }
            }
            
            log.info("Synced products for storeId: {}. Processed {}, Marked {} as suspected_deleted.", storeId, totalCount, markedCount);
            progress.setStatus("COMPLETED");
            progress.setMessage(String.format("同步完成: 已更新 %d 个商品的详情与变体", totalCount));
            progress.setEndTime(System.currentTimeMillis());

        } catch (Exception e) {
            log.error("Aborting product sync due to API error: {}", e.getMessage(), e);
            progress.setStatus("FAILED");
            progress.setMessage("获取 Eccang 商品时发生错误: " + e.getMessage());
            progress.setEndTime(System.currentTimeMillis());
        }
    }

    private void mapJsonToEntity(JsonNode node, EccangProduct product, Long storeId, String storeDomain, String key) {
        product.setStoreId(storeId);
        product.setParentAsin(key);
        product.setTitle(node.path("product_title").asText(""));
        product.setBrand(node.path("brand").asText(""));
        product.setSite(node.path("site").asText(""));
        product.setUserAccount(node.path("user_account").asText(""));
        product.setAsin(node.path("asin").asText(""));
        product.setAsinUrl(node.path("asin_url").asText(""));
        product.setAsinType(EccangJsonFieldHelper.parseAsinType(node, null));
        
        String eccangStatus = node.path("product_status").asText("1");
        if ("0".equals(eccangStatus)) {
            product.setStatus("inactive");
        } else if ("1".equals(eccangStatus)) {
            product.setStatus("active");
        } else if ("2".equals(eccangStatus)) {
            product.setStatus("draft");
        } else {
            product.setStatus("unknown");
        }
        
        String images = node.path("product_images").asText("");
        if (!images.isEmpty()) {
            String firstImg = images.split(",")[0].trim();
            if (firstImg.contains("images-na.ssl-images-amazon.com")) {
                product.setMainImage(firstImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL500_"));
            } else {
                product.setMainImage(firstImg);
            }
        } else {
            String asin = node.path("asin").asText("");
            if (StringUtils.hasText(asin)) {
                product.setMainImage(EccangJsonFieldHelper.amazonImageFromAsin(asin));
            }
        }
        
        // Populate smallImageUrl
        String smallImg = EccangJsonFieldHelper.firstText(node, "small_image_url", "small_image", "smallImage", "smallImageUrl");
        if (smallImg != null && !smallImg.isEmpty()) {
            product.setSmallImageUrl(smallImg);
        } else {
            String mainImg = product.getMainImage();
            if (mainImg != null && mainImg.contains("images-na.ssl-images-amazon.com")) {
                product.setSmallImageUrl(mainImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL75_"));
            } else {
                product.setSmallImageUrl(mainImg);
            }
        }
        
        // Populate openDateLocal
        String openDateStr = EccangJsonFieldHelper.firstText(node, "open_date_local", "open_date", "openDateLocal", "openDate", "open_date_local_time");
        if (openDateStr != null && !openDateStr.isEmpty()) {
            try {
                String cleanDate = openDateStr.split("\\+")[0].trim().replace(" ", "T");
                product.setOpenDateLocal(LocalDateTime.parse(cleanDate));
            } catch (Exception e) {}
        }
        
        // Date parsing is usually yyyy-MM-dd HH:mm:ss in Eccang API
        String addTime = node.path("product_add_time").asText(null);
        if (addTime != null && !addTime.isEmpty()) {
            try {
                product.setCreatedAt(LocalDateTime.parse(addTime.replace(" ", "T")));
            } catch (Exception e) {}
        }
        product.setCategoryPath(EccangJsonFieldHelper.buildCategoryPath(node));
    }
    
    private void mapJsonToVariantEntity(JsonNode node, EccangProductVariant variant, EccangProduct product) {
        variant.setProductId(product.getId());
        variant.setSku(node.path("product_sku").asText(""));
        variant.setTitle(node.path("product_title").asText(""));
        variant.setPrice(parseDecimal(node.path("sp_unit_price").asText("0")));
        variant.setInventoryQuantity(0);
        variant.setUserAccount(product.getUserAccount());
        variant.setAsin(node.path("asin").asText(""));
        variant.setAsinUrl(node.path("asin_url").asText(""));
        variant.setAsinType(EccangJsonFieldHelper.parseAsinType(node, null));
        variant.setParentAsin(product.getParentAsin());
        
        String eccangStatus = node.path("product_status").asText("1");
        if ("0".equals(eccangStatus)) {
            variant.setStatus("inactive");
        } else if ("1".equals(eccangStatus)) {
            variant.setStatus("active");
        } else if ("2".equals(eccangStatus)) {
            variant.setStatus("draft");
        } else {
            variant.setStatus("unknown");
        }
        
        // Use single image for variant if possible
        String images = node.path("product_images").asText("");
        if (!images.isEmpty()) {
            String firstImg = images.split(",")[0].trim();
            if (firstImg.contains("images-na.ssl-images-amazon.com")) {
                variant.setImageUrl(firstImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL500_"));
            } else {
                variant.setImageUrl(firstImg);
            }
        } else {
            String asin = node.path("asin").asText("");
            if (StringUtils.hasText(asin)) {
                variant.setImageUrl(EccangJsonFieldHelper.amazonImageFromAsin(asin));
            }
        }
        
        // Populate smallImageUrl
        String smallImg = EccangJsonFieldHelper.firstText(node, "small_image_url", "small_image", "smallImage", "smallImageUrl");
        if (smallImg != null && !smallImg.isEmpty()) {
            variant.setSmallImageUrl(smallImg);
        } else {
            String mainImg = variant.getImageUrl();
            if (mainImg != null && mainImg.contains("images-na.ssl-images-amazon.com")) {
                variant.setSmallImageUrl(mainImg.replaceAll("_(?:SL|UL|SX|SY)\\d+_", "_SL75_"));
            } else {
                variant.setSmallImageUrl(mainImg);
            }
        }
        
        // Populate openDateLocal
        String openDateStr = EccangJsonFieldHelper.firstText(node, "open_date_local", "open_date", "openDateLocal", "openDate", "open_date_local_time");
        if (openDateStr != null && !openDateStr.isEmpty()) {
            try {
                String cleanDate = openDateStr.split("\\+")[0].trim().replace(" ", "T");
                variant.setOpenDateLocal(LocalDateTime.parse(cleanDate));
            } catch (Exception e) {}
        }
    }

    private BigDecimal parseDecimal(String value) {
        try {
            if (value == null || value.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public SyncProgressDto getSyncProgress(Long storeId) {
        return syncProgressMap.get(storeId);
    }

    public void clearSyncProgress(Long storeId) {
        syncProgressMap.remove(storeId);
    }

    public List<String> getAllDistinctSkus() {
        return variantRepository.findDistinctSkus();
    }

    public boolean isSyncRunning(Long storeId) {
        SyncProgressDto progress = this.syncProgressMap.get(storeId);
        return progress != null && "RUNNING".equals(progress.getStatus());
    }

    public String getSyncCooldownMessage(Long storeId) {
        SyncProgressDto progress = this.syncProgressMap.get(storeId);
        if (progress != null && "COMPLETED".equals(progress.getStatus())) {
            long lastSyncTime = progress.getEndTime();
            long cooldownMs = 60 * 1000; // 1 minute cooldown
            long timePassed = System.currentTimeMillis() - lastSyncTime;
            if (timePassed < cooldownMs) {
                return String.format("同步过于频繁，请在 %d 秒后重试", (cooldownMs - timePassed) / 1000);
            }
        }
        return null;
    }

    @Async
    public void syncFbaInventory(Long storeId, Boolean onlyActive) {
        log.info("Mock FBA Inventory Sync triggered for storeId: {}, onlyActive: {}", storeId, onlyActive);
        // Sync is handled asynchronously or mocked
    }

    public List<String> getPlatformAccounts(String username) {
        if (StringUtils.hasText(username) && !isUserAdmin(username)) {
            List<UserStoreAllocation> allocations = getUserAllocatedStores(username);
            return allocations.stream()
                .map(UserStoreAllocation::getPlatformAccount)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        }
        return productRepository.findDistinctUserAccounts();
    }

    public List<String> getSites(String username) {
        if (StringUtils.hasText(username) && !isUserAdmin(username)) {
            List<UserStoreAllocation> allocations = getUserAllocatedStores(username);
            return allocations.stream()
                .map(UserStoreAllocation::getSiteCode)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        }
        return productRepository.findDistinctSites();
    }

    private boolean isUserAdmin(String username) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        Long userId = this.userStoreAllocationRepository.findUserIdByUsername(username.trim());
        if (userId == null) {
            return false;
        }
        return this.userStoreAllocationRepository.countAdminRolesByUserId(userId) > 0;
    }

    private List<UserStoreAllocation> getUserAllocatedStores(String username) {
        if (!StringUtils.hasText(username)) {
            return List.of();
        }
        Long userId = this.userStoreAllocationRepository.findUserIdByUsername(username.trim());
        if (userId == null) {
            return List.of();
        }
        return this.userStoreAllocationRepository.findByUserId(userId);
    }

    private Specification<EccangProduct> buildSearchSpec(Long storeId, String platformAccount, String site, String keyword, String status, String username) {
        return (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();
            if (storeId != null) {
                predicates.add(cb.equal(root.get("storeId"), storeId));
            }
            if (StringUtils.hasText(platformAccount)) {
                predicates.add(cb.equal(root.get("userAccount"), platformAccount));
            }
            if (StringUtils.hasText(site)) {
                predicates.add(cb.equal(root.get("site"), site));
            }
            
            if (StringUtils.hasText(status) && !"all".equalsIgnoreCase(status)) {
                if ("out_of_stock".equalsIgnoreCase(status)) {
                    Predicate isActive = cb.equal(root.get("status"), "active");
                    Predicate isZeroInv = cb.or(
                        cb.isNull(root.get("totalFbaInventory")),
                        cb.lessThanOrEqualTo(root.get("totalFbaInventory"), 0)
                    );
                    predicates.add(cb.and(isActive, isZeroInv));
                } else {
                    predicates.add(cb.equal(root.get("status"), status));
                }
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titlePredicate = cb.like(cb.lower(root.get("title")), pattern);
                Predicate spuPredicate = cb.like(cb.lower(root.get("parentAsin")), pattern);
                Predicate accountPredicate = cb.like(cb.lower(root.get("userAccount")), pattern);
                
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<EccangProductVariant> variantRoot = subquery.from(EccangProductVariant.class);
                subquery.select(variantRoot.get("productId"));
                Predicate skuMatch = cb.like(cb.lower(variantRoot.get("sku")), pattern);
                Predicate asinMatch = cb.like(cb.lower(variantRoot.get("asin")), pattern);
                subquery.where(cb.or(skuMatch, asinMatch));
                
                predicates.add(cb.or(titlePredicate, spuPredicate, accountPredicate, root.get("id").in(subquery)));
            }

            if (StringUtils.hasText(username) && !isUserAdmin(username)) {
                List<UserStoreAllocation> allocations = getUserAllocatedStores(username);
                if (allocations.isEmpty()) {
                    predicates.add(cb.disjunction());
                } else {
                    List<Predicate> orPredicates = new ArrayList<>();
                    for (UserStoreAllocation a : allocations) {
                        Predicate match = cb.and(
                            cb.equal(root.get("userAccount"), a.getPlatformAccount()),
                            cb.equal(root.get("site"), a.getSiteCode())
                        );
                        orPredicates.add(match);
                    }
                    predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Map<String, Long> getStatusStatistics(Long storeId, String platformAccount, String site, String keyword, String username) {
        Map<String, Long> stats = new java.util.HashMap<>();
        
        Specification<EccangProduct> allSpec = buildSearchSpec(storeId, platformAccount, site, keyword, "all", username);
        Specification<EccangProduct> activeSpec = buildSearchSpec(storeId, platformAccount, site, keyword, "active", username);
        Specification<EccangProduct> inactiveSpec = buildSearchSpec(storeId, platformAccount, site, keyword, "inactive", username);
        Specification<EccangProduct> outOfStockSpec = buildSearchSpec(storeId, platformAccount, site, keyword, "out_of_stock", username);
        
        stats.put("all", productRepository.count(allSpec));
        stats.put("active", productRepository.count(activeSpec));
        stats.put("inactive", productRepository.count(inactiveSpec));
        stats.put("out_of_stock", productRepository.count(outOfStockSpec));
        
        return stats;
    }

    public Page<EccangProductDto> searchProductsPaged(Long storeId, String platformAccount, String site, String keyword, String status, int page, int size, String username) {
        Specification<EccangProduct> spec = buildSearchSpec(storeId, platformAccount, site, keyword, status, username);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<EccangProduct> entityPage = productRepository.findAll(spec, pageable);
        List<EccangProductDto> dtos = convertToDtoList(entityPage.getContent());
        return new PageImpl<>(dtos, pageable, entityPage.getTotalElements());
    }

    public List<EccangProductDto> searchProducts(Long storeId, String platformAccount, String site, String keyword, String status, String username) {
        Specification<EccangProduct> spec = buildSearchSpec(storeId, platformAccount, site, keyword, status, username);
        List<EccangProduct> entities = productRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
        return convertToDtoList(entities);
    }

    public List<EccangProductDto> searchProducts(Long storeId, String keyword, String status) {
        return searchProducts(storeId, null, null, keyword, status, null);
    }

    public Page<EccangProductDto> searchProductsPaged(Long storeId, String keyword, String status, int page, int size) {
        return searchProductsPaged(storeId, null, null, keyword, status, page, size, null);
    }

    public void syncProductsIncremental(Long storeId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Incremental sync requested, currently fallback to full sync");
        syncProducts(storeId);
    }

    public List<EccangProductDto> getAllProducts() {
        List<EccangProduct> entities = productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return convertToDtoList(entities);
    }

    public List<EccangProductDto> getProductsByStoreId(Long storeId) {
        List<EccangProduct> entities = productRepository.findByStoreId(storeId);
        return convertToDtoList(entities);
    }

    private List<EccangProductDto> convertToDtoList(List<EccangProduct> entities) {
        if (entities == null || entities.isEmpty()) return new ArrayList<>();
        
        List<Long> productIds = entities.stream().map(EccangProduct::getId).collect(Collectors.toList());
        Set<Long> storeIds = entities.stream().map(EccangProduct::getStoreId).collect(Collectors.toSet());
        
        List<EccangProductVariant> allVariants = variantRepository.findByProductIdIn(productIds);
        Map<Long, List<EccangProductVariant>> variantsMap = allVariants.stream().collect(Collectors.groupingBy(EccangProductVariant::getProductId));
        
        List<EccangStore> allStores = storeRepository.findAllById(storeIds);
        Map<Long, String> storeNamesMap = allStores.stream().collect(Collectors.toMap(
            EccangStore::getId, 
            s -> s.getStoreName() != null ? s.getStoreName() : "", 
            (a, b) -> a
        ));
        Map<Long, String> amazonShopNamesMap = allStores.stream().collect(Collectors.toMap(
            EccangStore::getId, 
            s -> s.getNotes() != null ? s.getNotes() : "", 
            (a, b) -> a
        ));
        
        return entities.stream().map(entity -> {
            EccangProductDto dto = new EccangProductDto();
            BeanUtils.copyProperties(entity, dto);
            dto.setStoreName(storeNamesMap.get(entity.getStoreId()));
            dto.setAmazonShopName(amazonShopNamesMap.get(entity.getStoreId()));
            dto.setPlatformAccount(entity.getUserAccount());
            dto.setSiteCode(entity.getSite());
            dto.setProductLink(entity.getAsinUrl());
            dto.setSpu(entity.getParentAsin());
            dto.setHandle(entity.getParentAsin());
            
            List<EccangProductVariant> variants = variantsMap.getOrDefault(entity.getId(), new ArrayList<>());
            List<EccangVariantDto> variantDtos = variants.stream().map(v -> convertVariantToDto(v, entity)).collect(Collectors.toList());
            dto.setVariants(variantDtos);
            
            if (!variants.isEmpty()) {
                BigDecimal minPrice = variants.stream().map(EccangProductVariant::getPrice).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = variants.stream().map(EccangProductVariant::getPrice).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
                dto.setMinPrice(minPrice);
                dto.setMaxPrice(maxPrice);
                
                int fbaInventory = variants.stream().mapToInt(EccangProductVariant::getInventoryQuantity).sum();
                dto.setFbaInventory(fbaInventory);
                
                if (variantDtos.size() > 0 && variantDtos.get(0).getAsin() != null) {
                    dto.setAsin(variantDtos.get(0).getAsin());
                }
                dto.setSku(variantDtos.get(0).getSku());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    private void fetchWmsSkuData(EccangConfig config, Map<String, String> wmsCategoryMap, Map<String, String> wmsImageMap, SyncProgressDto progress) {
        try {
            int page = 1;
            int pageSize = 1000;
            String appKey = config.getAppKey();
            String appSecret = encryptionService.decrypt(config.getAppSecretEncrypted());
            int totalFetched = 0;
            while (true) {
                if (progress != null) {
                    progress.setMessage(String.format("正在从易仓拉取分类与图片数据进行关联映射 (已拉取第 %d 页，累计已拉取 %d 条)...", page, totalFetched));
                }
                log.info("Fetching WMS products for metadata mapping, page {}", page);
                List<JsonNode> wmsProducts = eccangApiClient.getWmsProductList(appKey, appSecret, page, pageSize);
                if (wmsProducts == null || wmsProducts.isEmpty()) {
                    wmsProducts = eccangApiClient.getProductList(appKey, appSecret, page, pageSize);
                    if (wmsProducts == null || wmsProducts.isEmpty()) {
                        break;
                    }
                }
                totalFetched += wmsProducts.size();
                for (JsonNode node : wmsProducts) {
                    String sku = EccangJsonFieldHelper.firstText(node, "product_sku", "productSku", "sku");
                    if (StringUtils.hasText(sku)) {
                        String skuUpper = sku.trim().toUpperCase();
                        
                        String categoryPath = EccangJsonFieldHelper.buildCategoryPath(node);
                        if (StringUtils.hasText(categoryPath)) {
                            wmsCategoryMap.put(skuUpper, categoryPath);
                        }
                        
                        String imgUrl = EccangJsonFieldHelper.extractImageUrl(node);
                        if (StringUtils.hasText(imgUrl)) {
                            wmsImageMap.put(skuUpper, imgUrl);
                        }
                    }
                }
                if (wmsProducts.size() < pageSize) {
                    break;
                }
                page++;
            }
            if (progress != null) {
                progress.setMessage(String.format("正在从易仓拉取分类与图片数据进行关联映射 (已完成拉取，共 %d 条)", totalFetched));
            }
            log.info("Fetched {} categories and {} images from WMS for mapping.", wmsCategoryMap.size(), wmsImageMap.size());
        } catch (Exception e) {
            log.error("Failed to fetch WMS products for metadata mapping", e);
        }
    }

    public List<EccangProductDto> getProductsByGids(List<String> gids) {
        return new ArrayList<>();
    }

    private EccangVariantDto convertVariantToDto(EccangProductVariant entity, EccangProduct product) {
        EccangVariantDto dto = new EccangVariantDto();
        BeanUtils.copyProperties(entity, dto);
        dto.setSpu(product.getParentAsin());
        dto.setProductTitle(product.getTitle());
        return dto;
    }
}
