package com.athlunakms.influencer.scheduler;

import com.athlunakms.influencer.service.CommissionSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CommissionScheduler {
    private static final Logger log = LoggerFactory.getLogger(CommissionScheduler.class);
    private final CommissionSettlementService settlementService;

    @Scheduled(cron="0 0 0,6,12,18 * * ?")
    public void scheduledSettlement() {
        log.info("====== \u5b9a\u65f6\u5206\u4f63\u7ed3\u7b97\u4efb\u52a1\u5f00\u59cb ======");
        long startTime = System.currentTimeMillis();
        try {
            int settledCount = this.settlementService.processSettlement();
            long duration = System.currentTimeMillis() - startTime;
            log.info("====== \u5b9a\u65f6\u5206\u4f63\u7ed3\u7b97\u4efb\u52a1\u5b8c\u6210\uff0c\u7ed3\u7b97 {} \u4e2a\u8ba2\u5355\uff0c\u8017\u65f6 {} ms ======", (Object)settledCount, (Object)duration);
        }
        catch (Exception e) {
            log.error("====== \u5b9a\u65f6\u5206\u4f63\u7ed3\u7b97\u4efb\u52a1\u5931\u8d25 ======", (Throwable)e);
        }
    }

    public int manualSettlement() {
        log.info("====== \u624b\u52a8\u5206\u4f63\u7ed3\u7b97\u4efb\u52a1\u5f00\u59cb ======");
        return this.settlementService.processSettlement();
    }

    public CommissionScheduler(CommissionSettlementService settlementService) {
        this.settlementService = settlementService;
    }
}

