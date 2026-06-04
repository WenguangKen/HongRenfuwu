package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.InfluencerContent;
import com.athlunakms.influencer.repository.InfluencerContentRepository;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;

@Service
public class MediaProcessingService {

    private static final Logger log = LoggerFactory.getLogger(MediaProcessingService.class);

    private final InfluencerContentRepository contentRepository;
    private final StorageServiceFactory storageServiceFactory;

    public MediaProcessingService(InfluencerContentRepository contentRepository, StorageServiceFactory storageServiceFactory) {
        this.contentRepository = contentRepository;
        this.storageServiceFactory = storageServiceFactory;
    }

    @Async
    public void extractVideoThumbnailAsync(Long contentId) {
        log.info("Starting asynchronous video thumbnail extraction for contentId: {}", contentId);
        try {
            InfluencerContent content = contentRepository.findById(contentId).orElse(null);
            if (content == null || content.getFilePath() == null) {
                log.warn("Content or file path not found for contentId: {}", contentId);
                return;
            }

            // 如果缩略图已存在，则跳过
            if (content.getThumbnailPath() != null) {
                log.info("Thumbnail already exists for contentId: {}", contentId);
                return;
            }

            StorageService storage = storageServiceFactory.getStorageService();
            
            // 1. 下载原始视频到临时文件
            Path tempVideoFile = Files.createTempFile("video_" + UUID.randomUUID().toString(), ".mov");
            File videoFile = tempVideoFile.toFile();
            videoFile.deleteOnExit();

            try (InputStream is = storage.getFileStream(content.getFilePath())) {
                Files.copy(is, tempVideoFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // 2. 准备输出临时图像文件
            Path tempImageFile = Files.createTempFile("thumb_" + UUID.randomUUID().toString(), ".jpg");
            File imageFile = tempImageFile.toFile();
            imageFile.deleteOnExit();

            // 3. 使用 jave-all-deps 提取元数据和第一帧
            MultimediaObject multimediaObject = new MultimediaObject(videoFile);
            MultimediaInfo info = multimediaObject.getInfo();

            if (content.getDuration() == null) {
                content.setDuration((int) (info.getDuration() / 1000));
            }
            if (info.getVideo() != null) {
                if (content.getWidth() == null) content.setWidth(info.getVideo().getSize().getWidth());
                if (content.getHeight() == null) content.setHeight(info.getVideo().getSize().getHeight());
            }

            VideoAttributes videoAttr = new VideoAttributes();
            videoAttr.setCodec("mjpeg");
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("image2");
            attrs.setVideoAttributes(videoAttr);
            
            // 在第 1 秒处截图，如果总时长不足 2 秒，则在中间位置截图
            float offset = (info.getDuration() > 2000) ? 1.0f : (info.getDuration() / 2000.0f);
            attrs.setOffset(offset);
            attrs.setDuration(0.01f);

            Encoder encoder = new Encoder();
            encoder.encode(multimediaObject, imageFile, attrs);

            // 4. 将缩略图上传回存储
            if (imageFile.exists() && imageFile.length() > 0) {
                String originalPath = content.getFilePath();
                String thumbRelativePath = originalPath.replace("originals/", "thumbnails/");
                // 提取原始文件名并替换后缀
                String thumbFileName = "thumb_" + content.getFileName().replaceAll("\\.[^.]+$", ".jpg");
                
                // 保持相对目录结构
                String dirPath = thumbRelativePath.substring(0, thumbRelativePath.lastIndexOf("/"));
                
                try (InputStream thumbIs = Files.newInputStream(tempImageFile)) {
                    String thumbKey = storage.upload(thumbIs, imageFile.length(), dirPath, thumbFileName, "image/jpeg");
                    content.setThumbnailPath(thumbKey);
                    contentRepository.save(content);
                    log.info("Successfully created and uploaded video thumbnail for contentId: {}", contentId);
                }
            } else {
                log.warn("Failed to generate thumbnail image file for contentId: {}", contentId);
            }

            // 清理
            Files.deleteIfExists(tempVideoFile);
            Files.deleteIfExists(tempImageFile);

        } catch (Exception e) {
            log.error("Error extracting video thumbnail for contentId: {}", contentId, e);
        }
    }
}
