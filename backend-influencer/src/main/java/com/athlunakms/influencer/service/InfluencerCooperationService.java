package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerCooperation;
import com.athlunakms.influencer.entity.RemittanceTask;
import com.athlunakms.influencer.repository.InfluencerCooperationRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.RemittanceTaskRepository;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfluencerCooperationService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerCooperationService.class);
    private final InfluencerCooperationRepository cooperationRepository;
    private final InfluencerRepository influencerRepository;
    private final RemittanceTaskRepository remittanceTaskRepository;

    public List<InfluencerCooperation> getCooperations(Long influencerId) {
        return this.cooperationRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);
    }

    private String convertRemittanceStatus(RemittanceTask.RemittanceStatus status) {
        if (status == null) return "-";
        switch (status) {
            case PENDING_AUDIT: return "待审核";
            case PENDING_PAYMENT: return "待打款";
            case PAID: return "已打款";
            case REJECTED: return "已拒绝";
            default: return status.name();
        }
    }

    @Transactional
    public InfluencerCooperation createCooperation(InfluencerCooperation cooperation) {
        // 统一模式字段确保 type 和 cooperationMode 同步优先使用 type对齐前端美化后的逻辑
        if (cooperation.getType() != null && cooperation.getCooperationMode() == null) {
            cooperation.setCooperationMode(cooperation.getType());
        }
        
        this.validateCooperation(cooperation);
        if (cooperation.getAmount() != null && cooperation.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            log.info("Skipping creation of zero-amount cooperation record for influencer {}", (Object)cooperation.getInfluencerId());
            return null;
        }
        InfluencerCooperation saved = this.cooperationRepository.save(cooperation);
        this.syncInfluencerPaidStatus(cooperation.getInfluencerId());
        return saved;
    }

    @Transactional
    public InfluencerCooperation updateCooperation(Long id, InfluencerCooperation updated) {
        return this.cooperationRepository.findById(id).map(existing -> {
            // 同样处理更新时的字段同步
            if (updated.getType() != null && updated.getCooperationMode() == null) {
                updated.setCooperationMode(updated.getType());
            }

            this.validateCooperation(updated);
            if (updated.getAmount() != null && updated.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                Long influencerId = existing.getInfluencerId();
                this.cooperationRepository.deleteById(id);
                this.syncInfluencerPaidStatus(influencerId);
                log.info("Deleted cooperation record {} because amount was set to 0", (Object)id);
                return null;
            }
            existing.setOrderNo(updated.getOrderNo());
            existing.setCooperationMode(updated.getCooperationMode());
            existing.setType(updated.getType());
            existing.setCurrency(updated.getCurrency());
            existing.setAmount(updated.getAmount());
            existing.setStatus(updated.getStatus());
            existing.setStartDate(updated.getStartDate());
            existing.setEndDate(updated.getEndDate());
            existing.setPaymentMethod(updated.getPaymentMethod());
            existing.setPaymentType(updated.getPaymentType());
            existing.setTaskNo(updated.getTaskNo());
            existing.setPaidAt(updated.getPaidAt());
            existing.setRemark(updated.getRemark());
            InfluencerCooperation saved = (InfluencerCooperation)this.cooperationRepository.save(existing);
            this.syncInfluencerPaidStatus(saved.getInfluencerId());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Cooperation record not found with id: " + id));
    }

    @Transactional
    public void deleteCooperation(Long id) {
        this.cooperationRepository.findById(id).ifPresent(record -> {
            Long influencerId = record.getInfluencerId();
            this.cooperationRepository.deleteById(id);
            this.syncInfluencerPaidStatus(influencerId);
        });
    }

    @Transactional
    public void cleanupZeroAmountCooperations() {
        this.cooperationRepository.deleteZeroAmountCooperations();
        log.info("Cleaned up all zero-amount cooperation records");
    }

    @Transactional
    public void syncAllPaidStatus() {
        // 先同步所有汇款任务到合作记录表
        List<RemittanceTask> allTasks = this.remittanceTaskRepository.findAll();
        log.info("Starting global remittance task sync for {} tasks", allTasks.size());
        for (RemittanceTask task : allTasks) {
            this.syncFromRemittanceTask(task);
        }

        List<Influencer> influencers = this.influencerRepository.findAll();
        log.info("Starting global isPaid status sync for {} influencers", influencers.size());
        for (Influencer influencer : influencers) {
            this.syncInfluencerPaidStatus(influencer.getId());
        }
        log.info("Completed global isPaid status sync");
    }

    @Transactional
    public void syncFromRemittanceTask(RemittanceTask task) {
        if (task.getStatus() == RemittanceTask.RemittanceStatus.REJECTED) {
            // 如果任务被拒绝，则物理删除合作记录（确保不计入付费，且不在付费合作展示）
            this.cooperationRepository.deleteByRemittanceTaskId(task.getId());
            log.info("Deleted cooperation record for rejected remittance task {}", task.getTaskNo());
        } else {
            // 否则创建或更新记录
            InfluencerCooperation coop = this.cooperationRepository.findByRemittanceTaskId(task.getId())
                    .orElseGet(InfluencerCooperation::new);
            
            coop.setInfluencerId(task.getInfluencerId());
            coop.setRemittanceTaskId(task.getId());
            coop.setOrderNo(task.getOrderNo());
            coop.setAmount(task.getAmount());
            coop.setCurrency(task.getCurrency());
            coop.setSourceType("REMITTANCE");
            coop.setType("汇款任务");
            coop.setCooperationMode("汇款任务");
            coop.setPaymentType(task.getPaymentType());
            coop.setStatus(convertRemittanceStatus(task.getStatus()));
            coop.setTaskNo(task.getTaskNo());
            coop.setRemark(task.getRemark());
            coop.setPaidAt(task.getPayTime());
            
            // 同步人员与时间
            coop.setCreatorName(task.getCreatorName());
            coop.setAuditorName(task.getAuditorName());
            coop.setAuditTime(task.getAuditTime());
            coop.setPayerName(task.getPayerName());
            coop.setPayTime(task.getPayTime());
            coop.setAuditRemark(task.getAuditRemark());
            coop.setPaymentRemark(task.getPaymentRemark());
            
            // 为了保持排序一致，同步创建时间
            if (coop.getCreatedAt() == null) {
                coop.setCreatedAt(task.getCreatedAt());
            }
            
            this.cooperationRepository.save(coop);
            log.info("Synced cooperation record for remittance task {}", task.getTaskNo());
        }
        // 同步完成后更新红人付费状态
        this.syncInfluencerPaidStatus(task.getInfluencerId());
    }

    public void syncInfluencerPaidStatus(Long influencerId) {
        this.influencerRepository.findById(influencerId).ifPresent(influencer -> {
            // 只要合作记录表里有任何明确金额记录（排除纯佣金协议）
            List<InfluencerCooperation> allRecords = this.cooperationRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);
            
            boolean hasPaidRecord = allRecords.stream().anyMatch(r -> {
                String mode = r.getCooperationMode() != null ? r.getCooperationMode() : r.getType();
                boolean isCommissionOnly = "COMMISSION".equalsIgnoreCase(mode) || "佣金合作".equalsIgnoreCase(mode);
                boolean hasAmount = r.getAmount() != null && r.getAmount().compareTo(BigDecimal.ZERO) > 0;
                
                return !isCommissionOnly && hasAmount;
            });

            if (influencer.getIsPaid() == null || influencer.getIsPaid() != hasPaidRecord) {
                influencer.setIsPaid(hasPaidRecord);
                this.influencerRepository.save(influencer);
                log.info("Synced isPaid status for influencer {}: {} (Records count: {})", influencerId, hasPaidRecord, allRecords.size());
            }
        });
    }

    private void validateCooperation(InfluencerCooperation cooperation) {
        String mode = cooperation.getCooperationMode();
        if ("COMMISSION".equalsIgnoreCase(mode) || "佣金合作".equalsIgnoreCase(mode)) {
            cooperation.setPaymentMethod(null);
            cooperation.setAmount(null);
        }
    }

    public InfluencerCooperationService(InfluencerCooperationRepository cooperationRepository, 
                                        InfluencerRepository influencerRepository,
                                        RemittanceTaskRepository remittanceTaskRepository) {
        this.cooperationRepository = cooperationRepository;
        this.influencerRepository = influencerRepository;
        this.remittanceTaskRepository = remittanceTaskRepository;
    }
}

