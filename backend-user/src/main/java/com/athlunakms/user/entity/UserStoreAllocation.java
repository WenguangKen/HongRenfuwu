package com.athlunakms.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Table(name = "user_store_allocations", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_store_account", columnList = "platform_account, site_code")
})
@Data
public class UserStoreAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "platform_account", nullable = false, length = 128)
    private String platformAccount;

    @Column(name = "site_code", nullable = false, length = 16)
    private String siteCode;

    @Column(name = "allocated_at")
    private LocalDateTime allocatedAt = LocalDateTime.now();
}
