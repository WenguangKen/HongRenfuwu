package com.athlunakms.webhook.service;

import com.athlunakms.webhook.entity.InfluencerSampleOrder;
import com.athlunakms.webhook.repository.InfluencerSampleOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 样品单自动匹配红人服务
 * 根据收件人姓名和邮箱自动匹配红人库中的数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SampleOrderAutoMatchService {

    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public boolean autoMatchInfluencer(InfluencerSampleOrder order) {
        if (Boolean.TRUE.equals(order.getManualOverride())) {
            log.debug("[AutoMatch] Order {} has manual override, skipping", order.getOrderName());
            return false;
        }

        if (order.getRecipientName() == null || order.getRecipientName().trim().isEmpty()
                || order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            log.debug("[AutoMatch] Order {} missing recipient name or email, skipping", order.getOrderName());
            return false;
        }

        String recipientName = order.getRecipientName().trim();
        String customerEmail = order.getCustomerEmail().trim().toLowerCase();

        // 使用 JdbcTemplate 直接查询红人表，避免依赖复杂的红人实体
        String sql = "SELECT id, real_name FROM influencer " +
                "WHERE TRIM(real_name) = ? AND TRIM(LOWER(email)) = ? " +
                "AND stage != 'TRASH' LIMIT 2";

        List<Map<String, Object>> matches = jdbcTemplate.queryForList(sql, recipientName, customerEmail);

        if (matches.isEmpty()) {
            log.debug("[AutoMatch] No influencer match found for order {} (name: {}, email: {})",
                    order.getOrderName(), recipientName, customerEmail);
            return false;
        }

        if (matches.size() > 1) {
            log.warn("[AutoMatch] Multiple influencers match for order {} (name: {}, email: {}), skipping",
                    order.getOrderName(), recipientName, customerEmail);
            return false;
        }

        Map<String, Object> influencer = matches.get(0);
        Long influencerId = ((Number) influencer.get("id")).longValue();
        String influencerName = (String) influencer.get("real_name");

        order.setInfluencerId(influencerId);
        order.setInfluencerName(influencerName);
        order.setAutoMatched(true);
        order.setManualOverride(false);

        sampleOrderRepository.save(order);

        // 自动将红人移入“合作中”状态
        try {
            log.info("[AutoMatch] Moving influencer {} to COOPERATING stage and updating last_sample_at", influencerId);
            // 更新红人状态和最近订单时间
            jdbcTemplate.update("UPDATE influencer SET status = 'COOPERATING', last_sample_at = NOW() WHERE id = ?",
                    influencerId);

            // 记录日志 (操作人为 SYS)
            jdbcTemplate.update(
                    "INSERT INTO influencer_change_log (influencer_id, field_name, old_value, new_value, operator, remark, created_at) "
                            +
                            "VALUES (?, ?, ?, ?, ?, ?, NOW())",
                    influencerId, "status", "UNKNOWN", "COOPERATING", "SYS",
                    "Auto matched influencer order: " + order.getOrderName());
        } catch (Exception e) {
            log.error("[AutoMatch] Failed to update influencer status for ID: {}", influencerId, e);
        }

        log.info("[AutoMatch] Successfully matched order {} to influencer {} (ID: {})",
                order.getOrderName(), influencerName, influencerId);
        return true;
    }
}
