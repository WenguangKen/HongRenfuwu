package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.InfluencerChangeLog;
import com.athlunakms.influencer.repository.InfluencerChangeLogRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfluencerLogService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerLogService.class);
    private final InfluencerChangeLogRepository logRepository;

    @Transactional
    public void logChange(Long influencerId, String fieldName, String oldValue, String newValue, String operator, String remark) {
        try {
            InfluencerChangeLog log = new InfluencerChangeLog();
            log.setInfluencerId(influencerId);
            log.setFieldName(fieldName);
            log.setOldValue(oldValue);
            log.setNewValue(newValue);
            log.setOperator(operator);
            log.setRemark(remark);
            this.logRepository.save(log);
        }
        catch (Exception e) {
            log.error("Failed to save influencer change log", e);
        }
    }

    public List<InfluencerChangeLog> getLogs(Long influencerId) {
        return this.logRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);
    }

    public InfluencerLogService(InfluencerChangeLogRepository logRepository) {
        this.logRepository = logRepository;
    }
}

