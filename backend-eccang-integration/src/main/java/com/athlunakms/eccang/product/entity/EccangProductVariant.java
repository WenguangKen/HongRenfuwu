package com.athlunakms.eccang.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "eccang_product_variants")
@Data
public class EccangProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "platform_account", nullable = false, length = 128)
    private String userAccount;

    @Column(name = "parent_asin", length = 20)
    private String parentAsin;

    @Column(length = 20)
    private String asin;

    @Column(name = "product_link", length = 500)
    private String asinUrl;

    @Column(name = "asin_type")
    private Integer asinType;

    @Column(nullable = false, length = 128)
    private String sku;

    @Column(length = 500)
    private String title;

    private BigDecimal price;

    @Column(length = 128)
    private String option1;

    @Column(length = 128)
    private String option2;

    @Column(length = 128)
    private String option3;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "small_image_url", columnDefinition = "TEXT")
    private String smallImageUrl;

    @Column(name = "open_date_local")
    private LocalDateTime openDateLocal;

    @Column(name = "fba_inventory", nullable = false)
    private Integer fbaInventory = 0;

    @Column(name = "inventory_quantity", nullable = false)
    private Integer inventoryQuantity = 0;

    @Column(name = "fulfillment_type", length = 32)
    private String fulfillmentType;

    @Column(name = "warehouse_sku", length = 128)
    private String warehouseSku;

    @Column(nullable = false, length = 32)
    private String status = "active";

    private Integer position;

    @Column(name = "sync_at")
    private LocalDateTime syncAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void touchSyncAt() {
        this.syncAt = LocalDateTime.now();
    }
}
