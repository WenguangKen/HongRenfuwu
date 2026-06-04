package com.athlunakms.influencer.runner;

import com.athlunakms.influencer.service.InfluencerImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动修复运行器
 * 在服务启动完成后自动执行一次历史数据的 Handle 修复逻辑。
 */
@Component
public class DataRepairRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataRepairRunner.class);
    private final InfluencerImportService importService;

    public DataRepairRunner(InfluencerImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("===> Application started. Executing auto-repair for influencer handles...");
        try {
            int count = importService.repairHistoryHandles();
            if (count > 0) {
                log.info("===> Auto-repair finished. Successfully updated {} influencer records.", count);
            } else {
                log.info("===> Auto-repair finished. No records needed update.");
            }
        } catch (Exception e) {
            log.error("===> Auto-repair failed: {}", e.getMessage(), e);
        }
    }
}
