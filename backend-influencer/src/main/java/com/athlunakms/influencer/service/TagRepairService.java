package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagRepairService {
    private static final Logger log = LoggerFactory.getLogger(TagRepairService.class);
    private final InfluencerRepository influencerRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public void repairMissingTags() {
        log.info("Starting tag repair/backfill process...");
        List<Influencer> influencers = this.influencerRepository.findAll();
        for (Influencer influencer : influencers) {
            String type = influencer.getInfluencerType();
            if (type == null || type.trim().isEmpty()) continue;
            this.ensureTagRegistered(type.trim(), "INFLUENCER_TYPE");
        }
        log.info("Tag repair process completed.");
    }

    private void ensureTagRegistered(String name, String category) {
        try {
            String sqlCheck = "SELECT count(*) FROM system_tags WHERE name = ? AND category = ?";
            Integer count = this.jdbcTemplate.queryForObject(sqlCheck, Integer.class, name, category);
            if (count != null && count == 0) {
                log.info("Registering missing tag: {} in category {}", name, category);
                this.jdbcTemplate.update("INSERT INTO system_tags (name, category, created_at, updated_at, sort_order) VALUES (?, ?, NOW(), NOW(), 0)", name, category);
            }
        }
        catch (Exception e) {
            log.error("Failed to register tag: {} [{}]", name, category, e);
        }
    }

    public TagRepairService(InfluencerRepository influencerRepository, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.influencerRepository = influencerRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }
}

