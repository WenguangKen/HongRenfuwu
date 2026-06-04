package com.athlunakms.eccang.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class EccangVariantDto {
    private Long id;
    private Long productId;
    private Long eccangVariantId;
    private String title;
    private String sku;
    private String barcode;
    private BigDecimal price;
    private BigDecimal compareAtPrice;
    private Integer inventoryQuantity;
    private String option1;
    private String option2;
    private String option3;
    private String imageUrl;
    private BigDecimal weight;
    private String weightUnit;
    private Integer position;
    private String spu;
    private String productTitle;
    private String asin;
}
