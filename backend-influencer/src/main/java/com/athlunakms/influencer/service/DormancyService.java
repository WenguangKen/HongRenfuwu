package com.athlunakms.influencer.service;

import com.athlunakms.influencer.dto.BatchWorkflowDto;
import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerSettings;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.InfluencerSettingsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DormancyService {
    private static final Logger log = LoggerFactory.getLogger(DormancyService.class);
    private final InfluencerRepository influencerRepository;
    private final InfluencerSettingsRepository settingsRepository;
    private final InfluencerWorkflowService workflowService;
    private final ObjectMapper objectMapper;

    public DormancyCheckResult checkDormancyEligibility(Long influencerId) {
        InfluencerSettings settings = this.settingsRepository.findById("dormancy_rules").orElse(null);
        if (settings == null) {
            return new DormancyCheckResult(false, "No dormancy rules defined");
        }
        try {
            boolean eligible;
            JsonNode rules = this.objectMapper.readTree(settings.getSettingValue());
            if (!rules.path("enabled").asBoolean(false)) {
                return new DormancyCheckResult(false, "Dormancy rules are disabled");
            }
            Influencer inf = this.influencerRepository.findById(influencerId).orElse(null);
            if (inf == null) {
                return new DormancyCheckResult(false, "Influencer not found");
            }
            if (inf.getStatus() != Influencer.Status.COOPERATING) {
                return new DormancyCheckResult(false, "Influencer is not in COOPERATING status");
            }
            List<String> failReasons = new ArrayList<>();
            eligible = this.checkRules(inf, rules, failReasons);
            return new DormancyCheckResult(eligible, eligible ? "Eligible" : String.join(", ", failReasons));
        }
        catch (Exception e) {
            log.error("Check failed", (Throwable)e);
            return new DormancyCheckResult(false, "Error: " + e.getMessage());
        }
    }

    @Transactional
    public List<String> executeDormancyCheck(boolean dryRun) {
        log.info("Starting Dormancy Check. DryRun: {}", dryRun);
        ArrayList<String> resultLog = new ArrayList<String>();
        InfluencerSettings settings = this.settingsRepository.findById("dormancy_rules").orElse(null);
        if (settings == null) {
            resultLog.add("No dormancy rules found.");
            return resultLog;
        }
        try {
            JsonNode rules = this.objectMapper.readTree(settings.getSettingValue());
            if (!rules.path("enabled").asBoolean(false)) {
                resultLog.add("Dormancy check validation is disabled.");
                return resultLog;
            }
            List<Long> candidateIds = this.influencerRepository.findIdsByStatus(Influencer.Status.COOPERATING);
            // 批量加载所有候选红人，避免逐条 findById 的 N+1 查询
            List<Influencer> candidates = this.influencerRepository.findAllById(candidateIds);
            for (Influencer inf : candidates) {
                List<String> failReasons = new ArrayList<>();
                if (!this.checkRules(inf, rules, failReasons)) continue;
                String reason = "Auto-Dormancy: Meeting all inactivity criteria";
                if (dryRun) {
                    resultLog.add("Would move ID " + inf.getId() + " (" + inf.getRealName() + ") to DORMANT. Reason: " + reason);
                    continue;
                }
                try {
                    BatchWorkflowDto batch = new BatchWorkflowDto();
                    batch.setIds(List.of(inf.getId()));
                    batch.setTargetStatus(Influencer.Status.DORMANT);
                    batch.setReason(reason);
                    this.workflowService.batchChangeStatus(batch, "SYS");
                    resultLog.add("Moved ID " + inf.getId() + " to DORMANT.");
                }
                catch (Exception e) {
                    log.error("Failed to move influencer {} to dormant", inf.getId(), e);
                    resultLog.add("Failed to move ID " + inf.getId() + ": " + e.getMessage());
                }
            }
        }
        catch (Exception e) {
            log.error("Error executing dormancy check", (Throwable)e);
            resultLog.add("Error: " + e.getMessage());
        }
        return resultLog;
    }

    private boolean checkRules(Influencer inf, JsonNode rules, List<String> failReasons) {
        boolean checkNoPendingSample = rules.path("noPendingSample").asBoolean(true);
        boolean checkNoPendingContent = rules.path("noPendingContent").asBoolean(true);
        int lastSampleDays = rules.path("lastSampleDays").asInt(60);
        int lastConversionDays = rules.path("lastConversionDays").asInt(60);
        int lastContentDays = rules.path("lastContentDays").asInt(60);
        int manualRestoreGraceDays = rules.path("manualRestoreGraceDays").asInt(30);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thresholdSample = now.minusDays(lastSampleDays);
        LocalDateTime thresholdConversion = now.minusDays(lastConversionDays);
        LocalDateTime thresholdContent = now.minusDays(lastContentDays);
        LocalDateTime thresholdRestore = now.minusDays(manualRestoreGraceDays);
        boolean passed = true;
        if (checkNoPendingSample && Boolean.TRUE.equals(inf.getHasPendingSample())) {
            passed = false;
            failReasons.add("Has pending sample");
        }
        if (checkNoPendingContent && Boolean.TRUE.equals(inf.getHasPendingContent())) {
            passed = false;
            failReasons.add("Has pending content task");
        }
        if (inf.getLastSampleAt() == null) {
            passed = false;
            failReasons.add("No sample order history");
        } else if (inf.getLastSampleAt().isAfter(thresholdSample)) {
            passed = false;
            failReasons.add("Recent sample activity within " + lastSampleDays + " days");
        }
        if (inf.getLastConversionAt() == null) {
            passed = false;
            failReasons.add("No conversion order history");
        } else if (inf.getLastConversionAt().isAfter(thresholdConversion)) {
            passed = false;
            failReasons.add("Recent conversion order within " + lastConversionDays + " days");
        }
        if (inf.getLastContentAt() == null) {
            passed = false;
            failReasons.add("No content upload history");
        } else if (inf.getLastContentAt().isAfter(thresholdContent)) {
            passed = false;
            failReasons.add("Recent content upload within " + lastContentDays + " days");
        }
        if (inf.getLastManualRestoreAt() != null && inf.getLastManualRestoreAt().isAfter(thresholdRestore)) {
            passed = false;
            failReasons.add("Recently restored manually within " + manualRestoreGraceDays + " days");
        }
        return passed;
    }

    public DormancyService(InfluencerRepository influencerRepository, InfluencerSettingsRepository settingsRepository, InfluencerWorkflowService workflowService, ObjectMapper objectMapper) {
        this.influencerRepository = influencerRepository;
        this.settingsRepository = settingsRepository;
        this.workflowService = workflowService;
        this.objectMapper = objectMapper;
    }

    public static class DormancyCheckResult {
        private boolean eligible;
        private String message;

        public DormancyCheckResult(boolean eligible, String message) {
            this.eligible = eligible;
            this.message = message;
        }

        public boolean isEligible() {
            return this.eligible;
        }

        public String getMessage() {
            return this.message;
        }

        public void setEligible(boolean eligible) {
            this.eligible = eligible;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

