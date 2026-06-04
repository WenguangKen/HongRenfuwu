package com.athlunakms.influencer.scheduler;

import com.athlunakms.influencer.dto.BatchWorkflowDto;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.InfluencerSampleOrderRepository;
import com.athlunakms.influencer.service.InfluencerWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InfluencerStatusSyncTask {
    private static final Logger log = LoggerFactory.getLogger(InfluencerStatusSyncTask.class);
    private final InfluencerRepository influencerRepository;
    private final InfluencerSampleOrderRepository influencerSampleOrderRepository;
    private final InfluencerWorkflowService workflowService;

    public InfluencerStatusSyncTask(InfluencerRepository influencerRepository,
                                    InfluencerSampleOrderRepository influencerSampleOrderRepository,
                                    InfluencerWorkflowService workflowService) {
        this.influencerRepository = influencerRepository;
        this.influencerSampleOrderRepository = influencerSampleOrderRepository;
        this.workflowService = workflowService;
    }

    /**
     * 每天定时扫一下有多少没有流转的红人，仅限待联系 (PENDING)、已联系 (CONTACTED) 和 沟通中 (COMMUNICATING) 的红人。
     * 将符合条件（有关联样品单、内容或订单）的红人自动流转至合作中。
     * 每天凌晨 3:00 执行
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncPendingInfluencerStatus() {
        log.info("Executing Scheduled Task: Syncing influencer status for PENDING, CONTACTED, COMMUNICATING...");

        List<Influencer.Status> targetStatuses = List.of(
                Influencer.Status.PENDING,
                Influencer.Status.CONTACTED,
                Influencer.Status.COMMUNICATING
        );

        int updatedCount = 0;

        for (Influencer.Status status : targetStatuses) {
            List<Long> ids = influencerRepository.findIdsByStatus(status);
            if (ids == null || ids.isEmpty()) {
                continue;
            }

            for (Long id : ids) {
                try {
                    Influencer influencer = influencerRepository.findById(id).orElse(null);
                    if (influencer != null) {
                        java.time.LocalDateTime sixMonthsAgo = java.time.LocalDateTime.now().minusMonths(6);

                        long sampleCount = influencerSampleOrderRepository.countByInfluencerId(id);
                        boolean hasSampleOrder = sampleCount > 0;
                        if (hasSampleOrder && influencer.getLastSampleAt() != null && influencer.getLastSampleAt().isBefore(sixMonthsAgo)) {
                            hasSampleOrder = false; // 大于半年的样品单不算
                        }

                        boolean hasContent = Boolean.TRUE.equals(influencer.getHasContent());
                        if (hasContent && influencer.getLastContentAt() != null && influencer.getLastContentAt().isBefore(sixMonthsAgo)) {
                            hasContent = false; // 大于半年的内容不算
                        }

                        boolean hasPendingSample = Boolean.TRUE.equals(influencer.getHasPendingSample());

                        boolean hasOrders = influencer.getTotalOrders() != null && influencer.getTotalOrders() > 0;
                        if (hasOrders && influencer.getLastConversionAt() != null && influencer.getLastConversionAt().isBefore(sixMonthsAgo)) {
                            hasOrders = false; // 大于半年的转化单不算
                        }

                        if (hasSampleOrder || hasContent || hasPendingSample || hasOrders) {
                            BatchWorkflowDto dto = new BatchWorkflowDto();
                            dto.setIds(List.of(id));
                            dto.setTargetStage(Influencer.Stage.ONBOARDED);
                            dto.setTargetStatus(Influencer.Status.COOPERATING);
                            dto.setReason("定时任务扫描到红人已有业务推进（样品单或内容），自动流转至合作中");

                            workflowService.batchChangeStatus(dto, "SYSTEM_SYNC_TASK");
                            updatedCount++;
                            log.info("Auto-transitioned influencer {} from {} to COOPERATING.", id, status);
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to auto-transition influencer {} status", id, e);
                }
            }
        }

        log.info("Scheduled Task Completed: Auto-transitioned {} influencers to COOPERATING.", updatedCount);
    }
}
