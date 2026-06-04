package com.athlunakms.eccang.order.service;

import com.athlunakms.eccang.order.entity.InfluencerChangeLog;
import com.athlunakms.eccang.order.entity.InfluencerSampleOrder;
import com.athlunakms.eccang.order.repository.InfluencerChangeLogRepository;
import com.athlunakms.eccang.order.repository.InfluencerSampleOrderRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SampleOrderAutoMatchService {
    private static final Logger log = LoggerFactory.getLogger(SampleOrderAutoMatchService.class);
    private final InfluencerSampleOrderRepository sampleOrderRepository;
    private final JdbcTemplate jdbcTemplate;
    private final InfluencerChangeLogRepository changeLogRepository;

    private void logChange(Long influencerId, String fieldName, String oldVal, String newVal, String operator,
            String remark) {
        if (influencerId == null) {
            return;
        }
        InfluencerChangeLog logEntry = new InfluencerChangeLog();
        logEntry.setInfluencerId(influencerId);
        logEntry.setFieldName(fieldName);
        logEntry.setOldValue(oldVal);
        logEntry.setNewValue(newVal);
        logEntry.setOperator(operator);
        logEntry.setRemark(remark);
        this.changeLogRepository.save(logEntry);
    }

    @Transactional
    public boolean autoMatchInfluencer(InfluencerSampleOrder order) {
        if (order.getRecipientName() == null || order.getRecipientName().trim().isEmpty()
                || order.getCustomerEmail() == null || order.getCustomerEmail().trim().isEmpty()) {
            log.debug("Order {} missing recipient name or email, skipping auto-match", order.getOrderName());
            return false;
        }

        String recipientName = order.getRecipientName().trim();
        String customerEmail = order.getCustomerEmail().trim().toLowerCase();

        String sql = "SELECT id, real_name, email\nFROM influencer\nWHERE TRIM(real_name) = ?\n  AND TRIM(LOWER(email)) = ?\n  AND stage != 'TRASH'\nLIMIT 2\n";

        List<Map<String, Object>> matches = this.jdbcTemplate.queryForList(sql,
                new Object[] { recipientName, customerEmail });

        if (matches.isEmpty()) {
            log.debug("No influencer match found for order {} (name: {}, email: {})",
                    order.getOrderName(), recipientName, customerEmail);
            return false;
        }
        if (matches.size() > 1) {
            log.warn("Multiple influencers match for order {} (name: {}, email: {}), skipping auto-match",
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
        this.sampleOrderRepository.save(order);
        
        // 自动将红人移入合作中状态（排除黑名单和不再合作的红人）
        try {
            log.info("Moving influencer {} to COOPERATING stage and updating last_sample_at", influencerId);
            int updated = this.jdbcTemplate.update(
                "UPDATE influencer SET status = 'COOPERATING', last_sample_at = NOW() " +
                "WHERE id = ? AND status NOT IN ('BLACKLIST', 'TERMINATED') AND stage != 'TRASH'",
                influencerId);
            if (updated > 0) {
                this.logChange(influencerId, "status", "UNKNOWN", "COOPERATING", "SYS",
                    "\u7cfb\u7edf\u90ae\u4ef6\u81ea\u52a8\u5339\u914d\u6837\u54c1\u5355: " + order.getOrderName());
            }
        } catch (Exception e) {
            log.error("Failed to update influencer status for ID: {}", influencerId, e);
        }
        
        // 将订单地址同步到红人收件地址库
        try {
            String recipientAddr = order.getRecipientAddress();
            if (recipientAddr != null && !recipientAddr.isBlank()) {
                String normalizedAddress = recipientAddr.trim().toLowerCase();
                int existing = this.jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM influencer_address WHERE influencer_id = ? AND LOWER(TRIM(address)) = ?",
                    Integer.class, influencerId, normalizedAddress);
                if (existing == 0) {
                    int addrCount = this.jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM influencer_address WHERE influencer_id = ?",
                        Integer.class, influencerId);
                    this.jdbcTemplate.update(
                        "INSERT INTO influencer_address (influencer_id, recipient_name, phone, email, country, " +
                        "address, is_default, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())",
                        influencerId,
                        order.getRecipientName(),
                        order.getRecipientPhone(),
                        order.getCustomerEmail(),
                        order.getRecipientCountry(),
                        recipientAddr,
                        addrCount == 0);
                    log.info("[AddressSync] Auto-saved address for influencer {} from auto-matched order {}",
                             influencerId, order.getOrderName());
                }
            }
        } catch (Exception e) {
            log.error("[AddressSync] Failed to sync address for influencer {}: {}", influencerId, e.getMessage());
        }
        
        this.logChange(influencerId, "\u65b0\u589e\u81ea\u52a8\u7ed1\u5b9a\u6837\u54c1\u5355", "-",
                order.getOrderName() + " (" + order.getEccangOrderNumber() + ")", "SYS",
                "\u7cfb\u7edf\u90ae\u4ef6\u81ea\u52a8\u5339\u914d\u6837\u54c1\u5355");
        log.info("Auto-matched order {} to influencer {} (ID: {})",
                order.getOrderName(), influencerName, influencerId);
        return true;
    }

    @Transactional
    public int batchAutoMatch() {
        List<InfluencerSampleOrder> unmatchedOrders = this.sampleOrderRepository.findAll().stream()
                .filter(order -> order.getInfluencerId() == null)
                .collect(java.util.stream.Collectors.toList());
        log.info("Starting batch auto-match for {} unmatched orders", unmatchedOrders.size());
        int matchedCount = 0;
        for (InfluencerSampleOrder order2 : unmatchedOrders) {
            try {
                if (Boolean.TRUE.equals(order2.getManualOverride())) {
                    log.debug("Order {} has manual override, skipping auto-match", order2.getOrderName());
                    continue;
                }
                if (!this.autoMatchInfluencer(order2))
                    continue;
                ++matchedCount;
            } catch (Exception e) {
                log.error("Error auto-matching order {}: {}", (Object) order2.getOrderName(), (Object) e.getMessage());
            }
        }
        log.info("Batch auto-match completed: {} orders matched out of {}", (Object) matchedCount,
                (Object) unmatchedOrders.size());
        return matchedCount;
    }

    public SampleOrderAutoMatchService(InfluencerSampleOrderRepository sampleOrderRepository, JdbcTemplate jdbcTemplate,
            InfluencerChangeLogRepository changeLogRepository) {
        this.sampleOrderRepository = sampleOrderRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.changeLogRepository = changeLogRepository;
    }
}
