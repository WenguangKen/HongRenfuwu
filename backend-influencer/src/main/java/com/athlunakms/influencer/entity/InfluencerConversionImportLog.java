package com.athlunakms.influencer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "influencer_conversion_import_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerConversionImportLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_type", nullable = false, length = 20)
    private String importType; // SAMPLE_ID, CONVERSION_EXCEL

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount = 0;

    @Column(name = "success_count", nullable = false)
    private Integer successCount = 0;

    @Column(name = "skipped_count", nullable = false)
    private Integer skippedCount = 0;

    @Column(name = "failed_count", nullable = false)
    private Integer failedCount = 0;

    @Column(name = "error_details_json")
    private String errorDetailsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private Long createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalCount == null) this.totalCount = 0;
        if (this.successCount == null) this.successCount = 0;
        if (this.skippedCount == null) this.skippedCount = 0;
        if (this.failedCount == null) this.failedCount = 0;
    }
}
