package com.athlunakms.influencer.scheduler;

import com.athlunakms.influencer.service.DormancyService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InfluencerDormancyTask {
    private static final Logger log = LoggerFactory.getLogger(InfluencerDormancyTask.class);
    private final DormancyService dormancyService;

    @Scheduled(cron="0 0 2 * * ?")
    public void runDailyDormancyCheck() {
        log.info("Executing Scheduled Daily Dormancy Check...");
        List results = this.dormancyService.executeDormancyCheck(false);
        log.info("Daily Dormancy Check Completed. {} actions taken.", (Object)results.size());
    }

    public InfluencerDormancyTask(DormancyService dormancyService) {
        this.dormancyService = dormancyService;
    }
}

