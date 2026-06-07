package com.athlunakms.eccang.product.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EccangVariantDto {
    private Long id;
    private Long productId;

    private String site;
    private String userAccount;
    private String parentAsin;
    private String asin;
    private String asinUrl;
    private Integer asinType;

    private String sku;
    private String title;
    private BigDecimal price;
    private String option1;
    private String option2;
    private String option3;
    private String imageUrl;
    private String smallImageUrl;
    private LocalDateTime openDateLocal;
    private Integer fbaInventory;
    private Integer inventoryQuantity;
    private String fulfillmentType;
    private String warehouseSku;
    private String status;
    private Integer position;

    /** 兼容旧前端 */
    private String platformAccount;
    private String productLink;
    private String spu;
    private String productTitle;
}
