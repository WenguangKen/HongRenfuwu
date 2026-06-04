package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.entity.CommunicationLog;
import com.athlunakms.influencer.repository.CommunicationLogRepository;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/influencer/{influencerId}/communication-logs")
public class CommunicationLogController {

    private static final Logger log = LoggerFactory.getLogger(CommunicationLogController.class);
    private final CommunicationLogRepository repository;
    private final StorageServiceFactory storageServiceFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CommunicationLogController(CommunicationLogRepository repository, StorageServiceFactory storageServiceFactory) {
        this.repository = repository;
        this.storageServiceFactory = storageServiceFactory;
    }

    /** GET — 获取沟通记录列表(按时间倒序) */
    @GetMapping
    public List<CommunicationLog> list(@PathVariable("influencerId") Long influencerId) {
        return repository.findByInfluencerIdOrderByCreatedAtDesc(influencerId);
    }

    /** POST — 新增沟通记录（支持多张图片） */
    @PostMapping(consumes = {"multipart/form-data"})
    public CommunicationLog create(
            @PathVariable("influencerId") Long influencerId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {

        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasImages = images != null && !images.isEmpty();

        if (!hasContent && !hasImages) {
            throw new IllegalArgumentException("沟通内容和图片不能同时为空");
        }

        String operatorName = (operator != null && !operator.isEmpty()) ? operator : "未知用户";
        String imageUrlsJson = null;

        // 上传多张图片到云盘
        if (hasImages) {
            try {
                StorageService storageService = storageServiceFactory.getStorageService();
                String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
                String relativePath = "communication-logs/" + datePath;
                List<String> urls = new ArrayList<>();

                for (MultipartFile image : images != null ? images : new ArrayList<MultipartFile>()) {
                    if (image == null || image.isEmpty()) continue;
                    String originalName = image.getOriginalFilename();
                    String ext = "";
                    if (originalName != null && originalName.contains(".")) {
                        ext = originalName.substring(originalName.lastIndexOf("."));
                    }
                    String fileName = UUID.randomUUID().toString().substring(0, 8) + "_" + System.currentTimeMillis() + ext;
                    String fileKey = storageService.upload(image, relativePath, fileName);
                    String url = "/influencer-api/v1/files/" + fileKey;
                    urls.add(url);
                    log.info("Communication log image uploaded: fileKey={}, url={}", fileKey, url);
                }

                if (!urls.isEmpty()) {
                    imageUrlsJson = objectMapper.writeValueAsString(urls);
                }
            } catch (Exception e) {
                log.error("Failed to upload communication log images", e);
                throw new RuntimeException("图片上传失败: " + e.getMessage());
            }
        }

        CommunicationLog logEntry = new CommunicationLog(
                influencerId,
                hasContent ? (content != null ? content.trim() : null) : null,
                imageUrlsJson,
                operatorName
        );
        return repository.save(logEntry);
    }
}
