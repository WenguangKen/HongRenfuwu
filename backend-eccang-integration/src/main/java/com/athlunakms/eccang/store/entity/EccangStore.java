package com.athlunakms.eccang.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="eccang_stores")
@Data
public class EccangStore {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="store_name", nullable=false)
    private String storeName;
    
    @Column(name="notes")
    private String notes;
    
    @Column(name="status")
    private String status;
    
    @Column(name="connection_verified")
    private Boolean connectionVerified = false;
    
    @Column(name="last_verified_at")
    private LocalDateTime lastVerifiedAt;
    
    @Column(name="eccang_shop_id")
    private Long eccangShopId;
    
    @Column(name="eccang_store_code")
    private String eccangStoreCode;
    
    @Column(name="platform")
    private String platform;
    
    @Column(name="currency")
    private String currency;
    
    @Column(name="timezone")
    private String timezone;
    
    @Column(name="country_code")
    private String countryCode;
    
    @Column(name="created_by")
    private Long createdBy;
    
    @CreationTimestamp
    @Column(name="created_at", updatable=false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
