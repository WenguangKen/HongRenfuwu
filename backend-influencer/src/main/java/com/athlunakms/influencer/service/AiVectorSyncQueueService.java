package com.athlunakms.influencer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 红人向量同步队列入队服务（供 AI 中枢定时拉取）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiVectorSyncQueueService {

    private final JdbcTemplate jdbcTemplate;

    public void enqueue(Long influencerId) {
        if (influencerId == null) {
            return;
        }
        try {
            jdbcTemplate.update(
                    "INSERT INTO ai_vector_sync_queue (influencer_id, status, retry_times) VALUES (?, 'PENDING', 0) " +
                            "ON DUPLICATE KEY UPDATE status = 'PENDING', retry_times = 0, error_msg = NULL, updated_at = NOW()",
                    influencerId);
        } catch (Exception e) {
            log.warn("红人 {} 加入 AI 向量同步队列失败（请确认已执行 ai_mysql_init.sql）: {}", influencerId, e.getMessage());
        }
    }
}
