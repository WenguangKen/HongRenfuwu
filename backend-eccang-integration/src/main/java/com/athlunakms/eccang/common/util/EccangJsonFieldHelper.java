package com.athlunakms.eccang.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;

public final class EccangJsonFieldHelper {
    private static final String ECCANG_IMAGE_HOST = "http://nt6hat6.eccang.com";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EccangJsonFieldHelper() {
    }

    public static String firstText(JsonNode node, String... fieldNames) {
        if (node == null || fieldNames == null) {
            return null;
        }
        for (String fieldName : fieldNames) {
            String value = readTextValue(node.path(fieldName));
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    /** 易仓 user_account 在请求/响应中可能是字符串或数组，取第一个有效值 */
    public static String firstAccountText(JsonNode node, String... fieldNames) {
        return firstText(node, fieldNames);
    }

    private static String readTextValue(JsonNode valueNode) {
        if (valueNode == null || valueNode.isMissingNode() || valueNode.isNull()) {
            return null;
        }
        if (valueNode.isArray()) {
            for (JsonNode item : valueNode) {
                String value = readTextValue(item);
                if (StringUtils.hasText(value)) {
                    return value;
                }
            }
            return null;
        }
        String value = valueNode.asText();
        if (StringUtils.hasText(value) && !"null".equalsIgnoreCase(value)) {
            return value.trim();
        }
        return null;
    }

    public static String normalizePlatformAccount(String account) {
        if (!StringUtils.hasText(account)) {
            return "";
        }
        return account.trim().replace('-', '_').toUpperCase();
    }

    public static String normalizeUserAccount(String account) {
        return normalizePlatformAccount(account);
    }

    public static String variantAccountSkuKey(String account, String sku) {
        if (!StringUtils.hasText(sku)) {
            return "";
        }
        // SKU 必须转大写以匹配 MySQL utf8mb4_0900_ai_ci 排序规则的大小写不敏感行为，
        // 避免 Java HashMap 因大小写差异生成不同 key 而漏掉已存在记录，导致重复插入。
        return normalizePlatformAccount(account) + "|" + sku.trim().toUpperCase();
    }

    public static String resolveAsinUrl(JsonNode node) {
        return firstText(node, "asin_url", "asinUrl", "product_link", "productLink", "ref_url", "refUrl");
    }

    public static Integer parseAsinType(JsonNode node, Integer defaultValue) {
        String text = firstText(node, "asin_type", "asinType");
        if (!StringUtils.hasText(text)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** FBA 可售库存：优先可售数，其次总库存/仓内库存 */
    public static int parseFbaQuantity(JsonNode node) {
        int fulfillable = parseInt(node, "afn_fulfillable_quantity", "afnFulfillableQuantity");
        int total = parseInt(node, "afn_total_quantity", "afnTotalQuantity");
        int warehouse = parseInt(node, "afn_warehouse_quantity", "afnWarehouseQuantity");
        return Math.max(fulfillable, Math.max(total, warehouse));
    }

    public static int parseInt(JsonNode node, String... fieldNames) {
        String text = firstText(node, fieldNames);
        if (!StringUtils.hasText(text)) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(text.trim()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static boolean isPlaceholderImage(String url) {
        if (!StringUtils.hasText(url)) {
            return true;
        }
        String lower = url.toLowerCase();
        return lower.contains("noimg.jpg") || lower.contains("/images/base/noimg");
    }

    public static String extractImageUrl(JsonNode node) {
        String images = firstText(node, "productImages", "product_images");
        if (StringUtils.hasText(images)) {
            String first = images.split(",")[0].trim();
            if (!isPlaceholderImage(first)) {
                return normalizeImageUrl(first);
            }
        }

        String mainImg = firstText(node, "main_img", "mainImg", "mainImage");
        if (StringUtils.hasText(mainImg) && !isPlaceholderImage(mainImg)) {
            return normalizeImageUrl(mainImg);
        }
        return null;
    }

    public static String normalizeImageUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        String trimmed = url.trim();
        if (trimmed.startsWith("//")) {
            return "https:" + trimmed;
        }
        if (trimmed.startsWith("/")) {
            return ECCANG_IMAGE_HOST + trimmed;
        }
        if (trimmed.startsWith("http://")) {
            return "https://" + trimmed.substring(7);
        }
        return trimmed;
    }

    public static String amazonImageFromAsin(String asin) {
        return null;
    }

    public static String resolveDisplayImage(String currentImage, String asin) {
        if (!isPlaceholderImage(currentImage)) {
            return normalizeImageUrl(currentImage);
        }
        return null;
    }

    /**
     * 解析商品聚合主键：优先 SPU，其次去掉尺码后缀的 SKU 前缀，再退回单个 SKU。
     * WMS 接口每行一个仓库 SKU，且多数 product_spu 为空，需靠 SKU 前缀合并同款式多尺码。
     */
    public static String resolveProductKey(JsonNode node) {
        String spu = firstText(node, "product_spu", "productSpu");
        if (StringUtils.hasText(spu)) {
            return spu;
        }
        String sku = firstText(node, "product_sku", "productSku");
        if (!StringUtils.hasText(sku)) {
            return "UNKNOWN";
        }
        String stylePrefix = stripSizeSuffix(sku);
        if (StringUtils.hasText(stylePrefix)) {
            return stylePrefix;
        }
        return sku;
    }

    public static String stripSizeSuffix(String sku) {
        if (!StringUtils.hasText(sku)) {
            return sku;
        }
        return sku.replaceAll("-(?i)(XXL|XL|XS|2XL|3XL|4XL|5XL|[SML])$", "");
    }

    /** 亚马逊 Listing：按 账号 + 父ASIN 聚合多变体 */
    public static String resolveAmazonListingKey(JsonNode node) {
        String account = firstAccountText(node, "user_account", "userAccount", "platform_account", "platformAccount");
        String parentAsin = firstText(node, "parent_asin", "parentAsin");
        String asin = firstText(node, "asin");
        String productKey = StringUtils.hasText(parentAsin) ? parentAsin : asin;
        if (!StringUtils.hasText(productKey)) {
            productKey = firstText(node, "seller_sku", "sellerSku");
        }
        if (StringUtils.hasText(account) && StringUtils.hasText(productKey)) {
            return account + "|" + productKey;
        }
        return productKey != null ? productKey : "UNKNOWN";
    }

    public static String resolveAmazonListingSpu(JsonNode node) {
        String parentAsin = firstText(node, "parent_asin", "parentAsin");
        if (StringUtils.hasText(parentAsin)) {
            return parentAsin;
        }
        return firstText(node, "asin");
    }

    public static String resolveAmazonListingTitle(JsonNode node) {
        return firstText(node,
            "title", "item_name", "itemName", "product_name", "productName",
            "listing_title", "listingTitle", "platform_product_name", "platformProductName",
            "name", "product_title", "productTitle");
    }

    /** 从一组 Listing 行中取第一个有效标题，否则退回 ASIN/SKU */
    public static String resolveTitleFromNodes(List<JsonNode> nodes) {
        if (nodes != null) {
            for (JsonNode node : nodes) {
                String title = resolveAmazonListingTitle(node);
                if (StringUtils.hasText(title)) {
                    return title;
                }
            }
            JsonNode first = nodes.get(0);
            String asin = resolveAmazonListingSpu(first);
            if (StringUtils.hasText(asin)) {
                return asin;
            }
            String sku = firstText(first, "seller_sku", "sellerSku");
            if (StringUtils.hasText(sku)) {
                return sku;
            }
        }
        return "未命名商品";
    }

    /** 亚马逊 Listing：汇总多变体属性（颜色/尺码/配送等） */
    public static String buildAmazonListingAttributesJson(JsonNode primary, List<JsonNode> variants) {
        if (primary == null) {
            return null;
        }
        Map<String, Object> attrs = new LinkedHashMap<>();
        putIfText(attrs, "site", primary, "site");
        putIfText(attrs, "brand", primary, "brand");
        putIfText(attrs, "fulfillmentType", primary, "fulfillment_type", "fulfillmentType");
        putIfText(attrs, "itemStatus", primary, "item_status", "itemStatus");
        putIfText(attrs, "currency", primary, "currency_code", "currencyCode");

        java.util.LinkedHashSet<String> colors = new java.util.LinkedHashSet<>();
        java.util.LinkedHashSet<String> sizes = new java.util.LinkedHashSet<>();
        if (variants != null) {
            for (JsonNode node : variants) {
                addToSet(colors, firstText(node, "color"));
                addToSet(sizes, firstText(node, "size"));
            }
        }
        if (!colors.isEmpty()) {
            attrs.put("colors", colors);
        }
        if (!sizes.isEmpty()) {
            attrs.put("sizes", sizes);
        }
        if (attrs.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attrs);
        } catch (Exception e) {
            return null;
        }
    }

    private static void addToSet(java.util.Set<String> set, String value) {
        if (StringUtils.hasText(value)) {
            set.add(value.trim());
        }
    }

    public static String extractAsinFromUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("/(?:dp|gp/product)/([A-Z0-9]{10})", java.util.regex.Pattern.CASE_INSENSITIVE)
            .matcher(url);
        return matcher.find() ? matcher.group(1).toUpperCase() : null;
    }

    public static String parseSiteCode(String userAccount) {
        if (!StringUtils.hasText(userAccount)) {
            return null;
        }
        String[] parts = userAccount.split("_");
        if (parts.length >= 3) {
            return parts[parts.length - 2];
        }
        if (parts.length == 2) {
            return parts[1];
        }
        return parts[0];
    }

    /** 易仓 docId=504：产品分类路径（一级 > 二级 > 三级） */
    public static String buildCategoryPath(JsonNode node) {
        if (node == null) {
            return null;
        }

        String customCategory = firstText(node,
            "productCustomCategory", "product_custom_category", "customCategory");
        if (StringUtils.hasText(customCategory)) {
            return customCategory;
        }

        List<String> levels = new ArrayList<>();
        for (String field : new String[] {
            "procutCategoryName1", "procut_category_name1", "productCategoryName1", "product_category_name1",
            "procutCategoryName2", "procut_category_name2", "productCategoryName2", "product_category_name2",
            "procutCategoryName3", "procut_category_name3", "productCategoryName3", "product_category_name3"
        }) {
            String value = firstText(node, field);
            if (StringUtils.hasText(value)) {
                levels.add(value);
            }
        }
        if (!levels.isEmpty()) {
            return String.join(" > ", levels);
        }
        return null;
    }

    /** 易仓 docId=504：产品自定义属性 + 规格信息，序列化为 JSON */
    public static String buildAttributesJson(JsonNode node) {
        if (node == null) {
            return null;
        }

        Map<String, Object> attrs = new LinkedHashMap<>();
        putIfText(attrs, "color", node, "product_color_name", "productColorName");
        putIfText(attrs, "size", node, "product_size_name", "productSizeName");
        putIfText(attrs, "specs", node, "product_specs", "productSpecs");
        putIfText(attrs, "brandCode", node, "brandCode", "brand_code");
        putIfText(attrs, "brandName", node, "brandName", "brand_name");
        putIfText(attrs, "prlCode", node, "prl_code", "prlCode");
        putIfText(attrs, "prlName", node, "prl_name", "prlName");
        putIfText(attrs, "logisticAttribute", node, "logistic_attribute", "logisticAttribute");
        putIfText(attrs, "hsCode", node, "hs_code", "hsCode");
        putIfText(attrs, "declareNameCn", node, "pd_oversea_type_cn", "pdOverseaTypeCn");
        putIfText(attrs, "declareNameEn", node, "pd_oversea_type_en", "pdOverseaTypeEn");
        putIfText(attrs, "saleStatus", node, "sale_status_name", "saleStatusName", "sale_status", "saleStatus");
        putIfText(attrs, "productStatus", node, "product_status_name", "productStatusName");

        JsonNode propertyNode = node.path("property");
        if (propertyNode.isMissingNode()) {
            propertyNode = node.path("product_property");
        }
        if (propertyNode.isMissingNode()) {
            propertyNode = node.path("productProperty");
        }
        if (!propertyNode.isMissingNode() && !propertyNode.isNull()) {
            attrs.put("customProperties", propertyNode);
        }

        JsonNode customCategoryNode = node.path("productCustomCategory");
        if (customCategoryNode.isMissingNode()) {
            customCategoryNode = node.path("product_custom_category");
        }
        if (!customCategoryNode.isMissingNode() && !customCategoryNode.isNull() && !customCategoryNode.isTextual()) {
            attrs.put("customCategoryDetail", customCategoryNode);
        }

        if (attrs.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attrs);
        } catch (Exception e) {
            return null;
        }
    }

    private static void putIfText(Map<String, Object> attrs, String key, JsonNode node, String... fieldNames) {
        String value = firstText(node, fieldNames);
        if (StringUtils.hasText(value)) {
            attrs.put(key, value);
        }
    }
}
