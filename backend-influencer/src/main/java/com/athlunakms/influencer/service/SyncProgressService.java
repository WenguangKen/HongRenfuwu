package com.athlunakms.influencer.service;

import com.athlunakms.influencer.dto.SyncProgress;
import java.time.Duration;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SyncProgressService {
    private static final Logger log = LoggerFactory.getLogger(SyncProgressService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PROGRESS_KEY_PREFIX = "sync:progress:";
    private static final Duration TTL = Duration.ofHours(1L);

    public void createTask(String taskId, int total) {
        SyncProgress progress = SyncProgress.builder()
                .taskId(taskId)
                .status("pending")
                .progress(0)
                .current(0)
                .total(total)
                .message("准备开始同步...")
                .startTime(LocalDateTime.now())
                .build();
        String key = PROGRESS_KEY_PREFIX + taskId;
        this.redisTemplate.opsForValue().set(key, progress, TTL);
        log.info("Created sync task: taskId={}, total={}", taskId, total);
    }

    public void updateProgress(String taskId, int current, String message) {
        String key = PROGRESS_KEY_PREFIX + taskId;
        SyncProgress progress = (SyncProgress) this.redisTemplate.opsForValue().get(key);
        if (progress != null) {
            progress.setCurrent(current);
            progress.setProgress((int) ((double) current * 100.0 / (double) progress.getTotal()));
            progress.setMessage(message);
            progress.setStatus("running");
            this.redisTemplate.opsForValue().set(key, progress, TTL);
            log.debug("Updated sync progress: taskId={}, progress={}%, current={}/{}", taskId, progress.getProgress(), current, progress.getTotal());
        } else {
            log.warn("Sync task not found: taskId={}", taskId);
        }
    }

    public void completeTask(String taskId) {
        String key = PROGRESS_KEY_PREFIX + taskId;
        SyncProgress progress = (SyncProgress) this.redisTemplate.opsForValue().get(key);
        if (progress != null) {
            progress.setStatus("completed");
            progress.setProgress(100);
            progress.setCurrent(progress.getTotal());
            progress.setMessage("同步完成");
            progress.setEndTime(LocalDateTime.now());
            this.redisTemplate.opsForValue().set(key, progress, TTL);
            log.info("Completed sync task: taskId={}, total={}", taskId, progress.getTotal());
        }
    }

    public void failTask(String taskId, String error) {
        String key = PROGRESS_KEY_PREFIX + taskId;
        SyncProgress progress = (SyncProgress) this.redisTemplate.opsForValue().get(key);
        if (progress != null) {
            progress.setStatus("failed");
            progress.setMessage("同步失败");
            progress.setError(error);
            progress.setEndTime(LocalDateTime.now());
            this.redisTemplate.opsForValue().set(key, progress, TTL);
            log.error("Failed sync task: taskId={}, error={}", taskId, error);
        }
    }

    public SyncProgress getProgress(String taskId) {
        String key = PROGRESS_KEY_PREFIX + taskId;
        return (SyncProgress) this.redisTemplate.opsForValue().get(key);
    }

    public void deleteTask(String taskId) {
        String key = PROGRESS_KEY_PREFIX + taskId;
        this.redisTemplate.delete(key);
        log.info("Deleted sync task: taskId={}", taskId);
    }

    public SyncProgressService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

