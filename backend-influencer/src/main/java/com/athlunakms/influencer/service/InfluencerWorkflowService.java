package com.athlunakms.influencer.service;

import com.athlunakms.influencer.dto.BatchWorkflowDto;
import com.athlunakms.influencer.dto.BatchWorkflowResultDto;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.StatusLog;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.StatusLogRepository;
import com.athlunakms.influencer.service.InfluencerLogService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfluencerWorkflowService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerWorkflowService.class);
    private final InfluencerRepository influencerRepository;
    private final StatusLogRepository statusLogRepository;
    private final InfluencerLogService logService;
    @Autowired
    @Lazy
    private InfluencerWorkflowService self;

    public BatchWorkflowResultDto batchChangeStatus(BatchWorkflowDto dto, String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        ArrayList<Long> successIds = new ArrayList<Long>();
        ArrayList<BatchWorkflowResultDto.FailedItem> failedItems = new ArrayList<BatchWorkflowResultDto.FailedItem>();
        for (Long id : dto.getIds()) {
            try {
                this.self.processSingleTransition(id, dto.getTargetStage(), dto.getTargetStatus(), dto.getReason(), finalOperator);
                successIds.add(id);
            }
            catch (Exception e) {
                log.error("Transition failed for ID {}", id, e);
                failedItems.add(new BatchWorkflowResultDto.FailedItem(id, "Error", e.getMessage()));
            }
        }
        BatchWorkflowResultDto result = new BatchWorkflowResultDto();
        result.setSuccessIds(successIds);
        result.setFailedItems(failedItems);
        return result;
    }

    @Transactional(rollbackFor={Exception.class}, propagation=Propagation.REQUIRES_NEW)
    public void processSingleTransition(Long id, Influencer.Stage targetStage, Influencer.Status targetStatus, String reason, String operator) {
        Influencer influencer = this.influencerRepository.findById(id).orElseThrow(() -> new RuntimeException("Influencer not found with ID: " + id));
        Influencer.Stage oldStage = influencer.getStage();
        Influencer.Status oldStatus = influencer.getStatus();

        // 状态流转不做限制，完全由用户自行决定

        if (oldStage == Influencer.Stage.TRASH && targetStage != null && targetStage != Influencer.Stage.TRASH) {
            if (targetStage == Influencer.Stage.ONBOARDED) {
                targetStatus = Influencer.Status.PENDING;
            } else if (targetStage == Influencer.Stage.POOL) {
                targetStatus = Influencer.Status.UNSCREENED;
            }
        }
        if (targetStage != null) {
            if (oldStage == Influencer.Stage.POOL && targetStage == Influencer.Stage.ONBOARDED) {
                influencer.setAuditorName(operator);
                influencer.setAuditTime(LocalDateTime.now());
            }
            influencer.setStage(targetStage);
        }
        if (targetStatus != null) {
            influencer.setStatus(targetStatus);
        }
        this.influencerRepository.save(influencer);
        String oldState = (oldStage != null ? oldStage.name() : "-") + " / " + (oldStatus != null ? oldStatus.name() : "-");
        String newState = (influencer.getStage() != null ? influencer.getStage().name() : "-") + " / " + (influencer.getStatus() != null ? influencer.getStatus().name() : "-");
        if (!oldState.equals(newState)) {
            this.logService.logChange(influencer.getId(), "\u72b6\u6001\u6d41\u8f6c", oldState, newState, operator, reason != null ? reason : "\u66f4\u65b0\u6d41\u7a0b\u72b6\u6001");
        }
        StatusLog log = new StatusLog();
        log.setInfluencerId(influencer.getId());
        log.setFromStage(oldStage);
        log.setToStage(influencer.getStage());
        log.setFromStatus(oldStatus);
        log.setToStatus(influencer.getStatus());
        log.setReason(reason != null ? reason : "Status Update");
        this.statusLogRepository.save(log);
    }

    public InfluencerWorkflowService(InfluencerRepository influencerRepository, StatusLogRepository statusLogRepository, InfluencerLogService logService) {
        this.influencerRepository = influencerRepository;
        this.statusLogRepository = statusLogRepository;
        this.logService = logService;
    }
}

