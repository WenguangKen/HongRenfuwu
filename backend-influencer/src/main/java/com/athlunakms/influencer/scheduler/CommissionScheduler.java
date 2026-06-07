package com.athlunakms.influencer.scheduler;

import com.athlunakms.influencer.service.CommissionSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CommissionScheduler {
    private static final Logger log = LoggerFactory.getLogger(CommissionScheduler.class);
    private final CommissionSettlementService settlementService;

    // 订单/分佣表已移除，定时结算已禁用
    public void scheduledSettlement() {
        log.debug("Commission scheduler disabled: order tables removed");
    }

    public int manualSettlement() {
        log.info("====== \u624b\u52a8\u5206\u4f63\u7ed3\u7b97\u4efb\u52a1\u5f00\u59cb ======");
        return this.settlementService.processSettlement();
    }

    public CommissionScheduler(CommissionSettlementService settlementService) {
        this.settlementService = settlementService;
    }
}

