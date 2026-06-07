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
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "eccang_products")
@Data
public class EccangProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "site_code", length = 16)
    private String site;

    @Column(name = "platform_account", nullable = false, length = 128)
    private String userAccount;

    @Column(name = "parent_asin", nullable = false, length = 20)
    private String parentAsin;

    /** 易仓 API 字段 asin_url，库表列名为 product_link */
    @Column(name = "product_link", length = 500)
    private String asinUrl;

    @Column(length = 20)
    private String asin;
    
    @Column(name = "asin_type")
    private Integer asinType;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 255)
    private String brand;

    @Column(nullable = false, length = 32)
    private String status = "active";

    @Column(name = "fulfillment_type", length = 32)
    private String fulfillmentType;

    @Column(length = 16)
    private String currency;

    @Column(name = "main_image", columnDefinition = "TEXT")
    private String mainImage;

    @Column(name = "small_image_url", columnDefinition = "TEXT")
    private String smallImageUrl;

    @Column(name = "open_date_local")
    private LocalDateTime openDateLocal;

    @Column(name = "attributes_json", columnDefinition = "TEXT")
    private String attributesJson;

    @Column(name = "category_path", length = 255)
    private String categoryPath;

    @Column(name = "total_fba_inventory", nullable = false)
    private Integer totalFbaInventory = 0;

    @Column(name = "variant_count", nullable = false)
    private Integer variantCount = 0;

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

    public String listingKey() {
        return storeId + "|" + userAccount + "|" + parentAsin;
    }
}
