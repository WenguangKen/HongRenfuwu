package com.athlunakms.eccang.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EccangProductDto {
    private Long id;
    private Long storeId;
    private String storeName;

    private String site;
    private String userAccount;
    private String parentAsin;
    private String asin;
    private String asinUrl;
    private Integer asinType;

    private String title;
    private String brand;
    private String status;
    private String fulfillmentType;
    private String currency;
    private String mainImage;
    private String smallImageUrl;
    private LocalDateTime openDateLocal;
    private Integer totalFbaInventory;
    private Integer variantCount;
    private LocalDateTime syncAt;

    /** 兼容旧前端字段 */
    private String platformAccount;
    private String siteCode;
    private String productLink;
    private String spu;
    private String handle;
    private String vendor;
    private String productType;
    private String onlineStoreUrl;
    private Integer fbaInventory;
    private String sku;
    private String platformSku;
    private String amazonShopName;
    private String attributesJson;
    private String optionsJson;
    private String categoryPath;

    private List<EccangVariantDto> variants;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
