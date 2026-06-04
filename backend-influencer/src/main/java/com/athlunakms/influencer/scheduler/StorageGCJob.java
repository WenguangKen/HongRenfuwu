package com.athlunakms.influencer.scheduler;

import com.athlunakms.influencer.repository.InfluencerContentRepository;
import com.athlunakms.influencer.service.FeishuNotifyService;
import com.athlunakms.influencer.service.storage.FileMetadata;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 存储审计任务（仅报告，不删除）
 *
 * 之前的 GC 实现会自动删除存储中不在 influencer_content 表中的文件，
 * 导致 vouchers/、imports/ 等其他业务模块的文件被误删。
 *
 * 现在改为【只记录不删除】模式，检测到孤立文件时通过飞书 Webhook 通知管理员。
 */
@Component
public class StorageGCJob {

    private static final Logger log = LoggerFactory.getLogger(StorageGCJob.class);

    /** 安全前缀列表：这些路径下的文件由其他业务模块管理，永远不应该被 GC 触碰 */
    private static final Set<String> SAFE_PREFIXES = Set.of(
            "vouchers/",            // 打款凭证
            "imports/",             // 红人导入文件
            "remittance-imports/",  // 汇款导入文件
            "communication-logs/",  // 沟通记录图片
            "avatars/",             // 红人头像
            "exports/"              // 导出文件
    );

    /** 飞书通知中最多列出的孤立文件数量，防止消息过长 */
    private static final int MAX_NOTIFY_FILES = 20;

    private final InfluencerContentRepository contentRepository;
    private final StorageServiceFactory storageServiceFactory;
    private final FeishuNotifyService feishuNotifyService;

    public StorageGCJob(InfluencerContentRepository contentRepository,
                        StorageServiceFactory storageServiceFactory,
                        FeishuNotifyService feishuNotifyService) {
        this.contentRepository = contentRepository;
        this.storageServiceFactory = storageServiceFactory;
        this.feishuNotifyService = feishuNotifyService;
    }

    /**
     * 每天凌晨 3:00 执行存储审计（仅报告，不删除任何文件）。
     * 发现孤立文件时通过飞书通知管理员。
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void auditStorageFiles() {
        log.info("[StorageAudit] Starting storage audit (report-only, no deletions)...");
        try {
            StorageService storage = storageServiceFactory.getStorageService();

            // 从数据库获取所有有效路径
            List<String> validFilePaths = contentRepository.findAllFilePaths();
            List<String> validThumbnailPaths = contentRepository.findAllThumbnailPaths();

            Set<String> validPaths = new HashSet<>();
            if (validFilePaths != null) validPaths.addAll(validFilePaths);
            if (validThumbnailPaths != null) validPaths.addAll(validThumbnailPaths);

            log.info("[StorageAudit] Found {} valid file/thumbnail paths in Database.", validPaths.size());

            // 从存储中获取所有对象
            List<String> allStorageObjects = storage.listAllObjects();
            log.info("[StorageAudit] Found {} total objects in Storage.", allStorageObjects.size());

            long safeSkipCount = 0;
            List<OrphanFileInfo> orphanFiles = new ArrayList<>();

            for (String objectKey : allStorageObjects) {
                // 忽略隐藏文件
                if (objectKey.startsWith(".") || objectKey.contains("/.")) continue;

                // 跳过安全前缀路径（由其他业务模块管理）
                boolean isSafe = false;
                for (String prefix : SAFE_PREFIXES) {
                    if (objectKey.startsWith(prefix)) {
                        isSafe = true;
                        safeSkipCount++;
                        break;
                    }
                }
                if (isSafe) continue;

                if (!validPaths.contains(objectKey)) {
                    // 获取文件元数据
                    FileMetadata metadata = null;
                    try {
                        metadata = storage.getMetadata(objectKey);
                    } catch (Exception e) {
                        log.warn("[StorageAudit] Failed to get metadata for: {}", objectKey);
                    }

                    long fileSize = (metadata != null && metadata.getSize() != null) ? metadata.getSize() : 0;
                    long lastModified = (metadata != null && metadata.getLastModified() != null) ? metadata.getLastModified() : 0;

                    orphanFiles.add(new OrphanFileInfo(objectKey, fileSize, lastModified));
                    log.info("[StorageAudit] Orphaned object detected (NOT deleted): {} (size={})", objectKey, formatSize(fileSize));
                }
            }

            log.info("[StorageAudit] Audit completed. Orphaned: {}, Safe-prefix skipped: {}, Total objects: {}",
                    orphanFiles.size(), safeSkipCount, allStorageObjects.size());

            // 有孤立文件时发送飞书通知
            if (!orphanFiles.isEmpty()) {
                sendFeishuNotification(orphanFiles, allStorageObjects.size(), safeSkipCount);
            }
        } catch (Exception e) {
            log.error("[StorageAudit] Error during storage audit.", e);
            // 审计异常也通知
            try {
                feishuNotifyService.sendCard("⚠️ 存储审计异常",
                        "**审计任务执行出错**\n\n错误信息: " + e.getMessage());
            } catch (Exception ne) {
                log.error("[StorageAudit] Failed to send error notification to Feishu.", ne);
            }
        }
    }

    /**
     * 发送孤立文件飞书通知
     */
    private void sendFeishuNotification(List<OrphanFileInfo> orphanFiles, int totalObjects, long safeSkipCount) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        long totalOrphanSize = orphanFiles.stream().mapToLong(f -> f.size).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("**检测到 ").append(orphanFiles.size()).append(" 个孤立文件**（仅通知，未删除）\n\n");

        int showCount = Math.min(orphanFiles.size(), MAX_NOTIFY_FILES);
        for (int i = 0; i < showCount; i++) {
            OrphanFileInfo f = orphanFiles.get(i);
            sb.append(i + 1).append(". `").append(f.key).append("`\n");
            sb.append("   大小: ").append(formatSize(f.size));
            if (f.lastModified > 0) {
                String modTime = Instant.ofEpochMilli(f.lastModified)
                        .atZone(ZoneId.of("Asia/Shanghai"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                sb.append(" | 最后修改: ").append(modTime);
            }
            sb.append("\n\n");
        }
        if (orphanFiles.size() > MAX_NOTIFY_FILES) {
            sb.append("... 还有 ").append(orphanFiles.size() - MAX_NOTIFY_FILES).append(" 个文件未列出\n\n");
        }

        sb.append("---\n");
        sb.append("孤立文件合计: **").append(formatSize(totalOrphanSize)).append("**\n");
        sb.append("存储总对象: ").append(totalObjects).append(" 个\n");
        sb.append("安全前缀跳过: ").append(safeSkipCount).append(" 个");

        feishuNotifyService.sendCard("📦 存储审计报告 — " + now, sb.toString());
        log.info("[StorageAudit] Feishu notification sent for {} orphaned files.", orphanFiles.size());
    }

    /**
     * 格式化文件大小为人类可读格式
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    /**
     * 孤立文件信息
     */
    private static class OrphanFileInfo {
        final String key;
        final long size;
        final long lastModified;

        OrphanFileInfo(String key, long size, long lastModified) {
            this.key = key;
            this.size = size;
            this.lastModified = lastModified;
        }
    }
}
