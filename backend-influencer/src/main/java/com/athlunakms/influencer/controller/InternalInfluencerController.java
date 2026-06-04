package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.BatchWorkflowDto;
import com.athlunakms.influencer.dto.BatchWorkflowResultDto;
import com.athlunakms.influencer.service.InfluencerVectorProfileBuilder;
import com.athlunakms.influencer.service.InfluencerWorkflowService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部跨微服务调用接口
 * 供 backend-ai-agent 调用，不暴露给前端公网
 */
@RestController
@RequestMapping("/internal/api/influencers")
@RequiredArgsConstructor
public class InternalInfluencerController {

    private static final String PROFILE_SELECT = """
            SELECT i.id, i.real_name, i.nick_name, i.status, i.stage,
                   i.default_platform, i.default_handle, i.default_url,
                   i.country, i.language, i.race, i.gender,
                   i.influencer_type, i.brand, i.source, i.source_value, i.tags,
                   i.total_fans, i.total_orders, i.is_paid, i.description,
                   (SELECT GROUP_CONCAT(
                        CONCAT(sm.platform, '@', IFNULL(sm.handle, ''), ' 粉丝', IFNULL(sm.follower_count, 0))
                        ORDER BY sm.is_default DESC, sm.follower_count DESC
                        SEPARATOR ' | ')
                    FROM influencer_social_media sm
                    WHERE sm.influencer_id = i.id) AS social_summary,
                   (SELECT GROUP_CONCAT(st.name ORDER BY st.name SEPARATOR ',')
                    FROM system_tags st
                    WHERE i.tags IS NOT NULL AND i.tags <> '' AND i.tags <> '[]'
                      AND JSON_VALID(i.tags)
                      AND JSON_CONTAINS(i.tags, CAST(st.id AS JSON), '$')) AS tag_names
            FROM influencer i
            WHERE i.is_deleted = 0 AND i.id IN (%s)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final InfluencerWorkflowService workflowService;

    /**
     * 批量根据 ID 获取红人明细（供 AI 展示检索结果）
     */
    @PostMapping("/batch-details")
    public List<Map<String, Object>> getBatchDetails(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String inSql = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(String.format(PROFILE_SELECT, inSql));
        return enrichRows(rows);
    }

    /**
     * 拉取待同步向量的红人完整画像（含 embedText）
     */
    @GetMapping("/pending-sync")
    public List<Map<String, Object>> getPendingSyncData(@RequestParam(defaultValue = "10") int limit) {
        String fetchSql = "SELECT influencer_id FROM ai_vector_sync_queue " +
                "WHERE status IN ('PENDING', 'FAILED') AND retry_times < 3 LIMIT ?";
        List<Long> ids = jdbcTemplate.queryForList(fetchSql, Long.class, limit);

        if (ids.isEmpty()) {
            return List.of();
        }

        String inSql = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(String.format(PROFILE_SELECT, inSql));
        return enrichRows(rows);
    }

    private List<Map<String, Object>> enrichRows(List<Map<String, Object>> rows) {
        return rows.stream().map(row -> {
            Map<String, Object> out = new LinkedHashMap<>(row);
            out.put("platform", row.get("default_platform"));
            out.put("embed_text", InfluencerVectorProfileBuilder.buildEmbedText(row));
            out.put("is_cooperated", InfluencerVectorProfileBuilder.isCooperating(row));
            return out;
        }).toList();
    }

    /**
     * 按社媒 handle 查找红人（精确 + 模糊），供小A 工具调用
     */
    @GetMapping("/search-by-handle")
    public List<Map<String, Object>> searchByHandle(@RequestParam String handle) {
        if (handle == null || handle.isBlank()) {
            return List.of();
        }
        String h = handle.trim().replace("@", "");
        String like = "%" + h.toLowerCase() + "%";
        // 不用 SELECT DISTINCT + ORDER BY sm.handle：MySQL 5.7+ ONLY_FULL_GROUP_BY 会报 3065
        String sql = """
                SELECT i.id, i.real_name, i.nick_name, i.status, i.stage,
                       i.default_platform, i.default_handle, i.country, i.total_fans
                FROM influencer i
                INNER JOIN (
                    SELECT i2.id AS influencer_id,
                           MIN(CASE
                             WHEN LOWER(i2.default_handle) = LOWER(?)
                                  OR LOWER(sm.handle) = LOWER(?) THEN 0
                             ELSE 1
                           END) AS match_rank
                    FROM influencer i2
                    LEFT JOIN influencer_social_media sm ON sm.influencer_id = i2.id
                    WHERE i2.is_deleted = 0
                      AND (
                        LOWER(i2.default_handle) = LOWER(?)
                        OR LOWER(sm.handle) = LOWER(?)
                        OR LOWER(i2.default_handle) LIKE ?
                        OR LOWER(sm.handle) LIKE ?
                      )
                    GROUP BY i2.id
                ) ranked ON ranked.influencer_id = i.id
                ORDER BY ranked.match_rank, i.id DESC
                LIMIT 20
                """;
        return jdbcTemplate.queryForList(sql, h, h, h, h, like, like);
    }

    @PostMapping("/sync-status")
    public void updateSyncStatus(@RequestParam Long influencerId, @RequestParam String status, @RequestParam(required = false) String errorMsg) {
        if ("SUCCESS".equals(status)) {
            jdbcTemplate.update("UPDATE ai_vector_sync_queue SET status = 'SUCCESS', error_msg = NULL WHERE influencer_id = ?", influencerId);
        } else {
            jdbcTemplate.update("UPDATE ai_vector_sync_queue SET status = 'FAILED', retry_times = retry_times + 1, error_msg = ? WHERE influencer_id = ?", errorMsg, influencerId);
        }
    }

    /** AI 助手批量状态流转（待联系→已联系 等） */
    @PostMapping("/workflow/batch-status-change")
    public BatchWorkflowResultDto batchStatusChange(@RequestBody BatchWorkflowDto dto) {
        return workflowService.batchChangeStatus(dto, "AI-Copilot");
    }
}
