package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.InfluencerSettings;
import com.athlunakms.influencer.repository.InfluencerSettingsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluencerStatusCleanupService {

    private final JdbcTemplate jdbcTemplate;
    private final InfluencerSettingsRepository settingsRepository;

    private static final String CLEANUP_FLAG_KEY = "status_cleanup_v1_done";

    @PostConstruct
    public void init() {
        // Execute cleanup on startup if not done yet
        if (isCleanupDone()) {
            log.info("[StatusCleanup] Cleanup already performed, skipping.");
            return;
        }

        try {
            log.info("[StatusCleanup] Starting one-time influencer status cleanup...");
            performCleanup();
            markCleanupDone();
            log.info("[StatusCleanup] Influencer status cleanup completed successfully.");
        } catch (Exception e) {
            log.error("[StatusCleanup] Failed to perform status cleanup", e);
        }
    }

    private boolean isCleanupDone() {
        return settingsRepository.findById(CLEANUP_FLAG_KEY)
                .map(s -> "true".equalsIgnoreCase(s.getSettingValue()))
                .orElse(false);
    }

    private void markCleanupDone() {
        InfluencerSettings settings = settingsRepository.findById(CLEANUP_FLAG_KEY)
                .orElse(new InfluencerSettings());
        settings.setSettingKey(CLEANUP_FLAG_KEY);
        settings.setSettingValue("true");
        settings.setDescription("Flag indicating that the one-time influencer status cleanup has been completed.");
        settingsRepository.save(settings);
    }

    @Transactional
    public void performCleanup() {
        // 订单表已移除，仅依据内容推进记录做一次性状态修正
        int cooperatingUpdated = jdbcTemplate.update(
                "UPDATE influencer i SET i.status = 'COOPERATING' " +
                        "WHERE i.stage != 'TRASH' AND i.status NOT IN ('BLACKLIST', 'TERMINATED') " +
                        "AND (i.has_content = 1 OR i.last_content_at IS NOT NULL)");

        log.info("[StatusCleanup] Batch update finished. Updated to COOPERATING: {}", cooperatingUpdated);

        // Add a generic log entry for the cleanup
        try {
            jdbcTemplate.update(
                    "INSERT INTO influencer_change_log (influencer_id, field_name, old_value, new_value, operator, remark, created_at) "
                            +
                            "VALUES (0, 'status', 'CLEANUP', 'BATCH', 'SYS', 'One-time startup status cleanup performed', NOW())");
        } catch (Exception e) {
            log.warn("[StatusCleanup] Failed to insert global cleanup log", e);
        }
    }
}
