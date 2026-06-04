package com.athlunakms.eccang.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EccangProductDto {
    private Long id;
    private Long eccangProductId;
    private Long storeId;
    private String storeName;
    private String title;
    private String bodyHtml;
    private String productType;
    private String handle;
    private String status;
    private String currency;
    private Integer totalInventory;
    private String spu;
    private String mainImage;
    private String spuImages;
    private String optionsJson;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime syncAt;
    private String onlineStoreUrl;
    private List<EccangVariantDto> variants;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    private String amazonShopName;
    private int fbaInventory;
    private String asin;
    private String sku;
}
