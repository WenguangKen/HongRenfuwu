package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.config.BusinessMetrics;
import com.athlunakms.influencer.service.storage.FileMetadata;
import com.athlunakms.influencer.service.storage.StorageService;
import com.athlunakms.influencer.service.storage.StorageServiceFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件服务控制器
 *
 * 提供文件读取、下载、预签名 URL 生成、缩略图获取等功能。
 * 所有文件操作通过 StorageService 抽象层完成（支持本地/MinIO）。
 */
@RestController
@RequestMapping(value = {"/v1/files"})
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    // ========== 常量定义 ==========
    /** 普通文件缓存时长：1 天 */
    private static final String CACHE_CONTROL_DEFAULT = "public, max-age=86400";
    /** 缩略图缓存时长：7 天 */
    private static final String CACHE_CONTROL_THUMBNAIL = "public, max-age=604800";
    /** 默认 Content-Type */
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    /** 缩略图源目录前缀 */
    private static final String THUMBNAIL_SRC_PREFIX = "originals/";
    /** 缩略图目标目录前缀 */
    private static final String THUMBNAIL_DST_PREFIX = "thumbnails/";
    /** 缩略图后缀 */
    private static final String THUMBNAIL_SUFFIX = "_thumb.jpg";
    /** 预签名 URL 最小过期分钟数 */
    private static final int PRESIGNED_MIN_MINUTES = 1;
    /** 预签名 URL 最大过期分钟数（7 天） */
    private static final int PRESIGNED_MAX_MINUTES = 10080;

    private final StorageServiceFactory storageServiceFactory;
    private final BusinessMetrics businessMetrics;

    public FileController(StorageServiceFactory storageServiceFactory, BusinessMetrics businessMetrics) {
        this.storageServiceFactory = storageServiceFactory;
        this.businessMetrics = businessMetrics;
    }

    /**
     * 通配路径文件读取 — 从 URI 路径中提取文件 key
     *
     * @param request HTTP 请求
     * @return 文件内容或 404
     */
    @GetMapping(value = {"/**"})
    public ResponseEntity<?> readFile(HttpServletRequest request) {
        String fullPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        String relativePath = fullPath;
        if (contextPath != null && !contextPath.isEmpty()) {
            relativePath = relativePath.substring(contextPath.length());
        }
        relativePath = relativePath.replaceFirst("^/v1/files/", "");
        if (relativePath.isEmpty()
                || relativePath.equals("read")
                || relativePath.equals("download")
                || relativePath.equals("thumbnail")
                || relativePath.equals("presigned-url")) {
            return ResponseEntity.notFound().build();
        }

        // 路径遍历防护：拒绝包含 ".." 的路径
        if (!isValidFileKey(relativePath)) {
            log.warn("Rejected path traversal attempt: {}", relativePath);
            return ResponseEntity.badRequest().build();
        }

        log.debug("File request for: {}", relativePath);
        return this.readFileByKey(relativePath);
    }

    /**
     * 按 key 读取文件（在线预览）
     *
     * @param key 文件存储 key
     * @return 文件流响应
     */
    @GetMapping(value = {"/read"})
    public ResponseEntity<?> readFileByKey(@RequestParam(value = "key") String key) {
        if (!isValidFileKey(key)) {
            return ResponseEntity.badRequest().build();
        }
        long startTime = System.currentTimeMillis();
        InputStream stream = null;
        try {
            StorageService storage = this.storageServiceFactory.getStorageService();
            FileMetadata metadata = storage.getMetadata(key);
            if (!metadata.isExists()) {
                return ResponseEntity.notFound().build();
            }
            stream = storage.getFileStream(key);
            InputStreamResource resource = new InputStreamResource(stream);
            String contentType = metadata.getContentType();
            if (contentType == null) {
                contentType = DEFAULT_CONTENT_TYPE;
            }
            this.businessMetrics.recordDownload();
            this.businessMetrics.recordDownloadDuration(System.currentTimeMillis() - startTime);
            // 注意：Spring 的 ResourceHttpMessageConverter 会在写入完成后关闭底层 InputStream
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(metadata.getSize().longValue())
                    .header("Cache-Control", CACHE_CONTROL_DEFAULT)
                    .body(resource);
        } catch (Exception e) {
            // 异常路径：手动关闭可能已打开的流，防止资源泄漏
            closeQuietly(stream);
            log.error("Failed to read file: key={}", key, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 文件下载（强制下载，非在线预览）
     *
     * @param key      文件存储 key
     * @param fileName 自定义下载文件名（可选）
     * @return 文件流响应（Content-Disposition: attachment）
     */
    @GetMapping(value = {"/download"})
    public ResponseEntity<?> downloadFile(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "fileName", required = false) String fileName) {
        if (!isValidFileKey(key)) {
            return ResponseEntity.badRequest().build();
        }
        long startTime = System.currentTimeMillis();
        InputStream stream = null;
        try {
            StorageService storage = this.storageServiceFactory.getStorageService();
            FileMetadata metadata = storage.getMetadata(key);
            if (!metadata.isExists()) {
                return ResponseEntity.notFound().build();
            }
            stream = storage.getFileStream(key);
            InputStreamResource resource = new InputStreamResource(stream);
            String downloadName = fileName != null ? fileName : metadata.getFileName();
            String encodedFileName = URLEncoder.encode(downloadName, StandardCharsets.UTF_8).replace("+", "%20");
            this.businessMetrics.recordDownload();
            this.businessMetrics.recordDownloadDuration(System.currentTimeMillis() - startTime);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(metadata.getSize().longValue())
                    .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (Exception e) {
            closeQuietly(stream);
            log.error("Failed to download file: key={}", key, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成预签名下载 URL
     *
     * @param key           文件存储 key
     * @param expireMinutes 过期时间（分钟），范围 [1, 10080]
     * @return 预签名 URL 及文件元信息
     */
    @GetMapping(value = {"/presigned-url"})
    public ResponseEntity<?> getPresignedUrl(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "expireMinutes", defaultValue = "60") int expireMinutes) {
        if (!isValidFileKey(key)) {
            return ResponseEntity.badRequest().build();
        }
        // 校验过期时间范围
        if (expireMinutes < PRESIGNED_MIN_MINUTES || expireMinutes > PRESIGNED_MAX_MINUTES) {
            log.warn("Invalid expireMinutes={}, must be in [{}, {}]", expireMinutes, PRESIGNED_MIN_MINUTES, PRESIGNED_MAX_MINUTES);
            return ResponseEntity.badRequest().build();
        }
        try {
            StorageService storage = this.storageServiceFactory.getStorageService();
            FileMetadata metadata = storage.getMetadata(key);
            if (!metadata.isExists()) {
                return ResponseEntity.notFound().build();
            }
            String presignedUrl = storage.getPresignedDownloadUrl(key, expireMinutes);
            return ResponseEntity.ok(Map.of(
                    "url", presignedUrl,
                    "expiresIn", expireMinutes * 60,
                    "fileName", metadata.getFileName(),
                    "size", metadata.getSize()));
        } catch (Exception e) {
            log.error("Failed to generate presigned URL: key={}", key, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取缩略图
     * 优先返回缩略图，若不存在则降级返回原图。
     *
     * @param key 原图存储 key
     * @return 缩略图或原图
     */
    @GetMapping(value = {"/thumbnail"})
    public ResponseEntity<?> getThumbnail(@RequestParam(value = "key") String key) {
        if (!isValidFileKey(key)) {
            return ResponseEntity.badRequest().build();
        }
        InputStream stream = null;
        try {
            // 构造缩略图 key: originals/xxx.png → thumbnails/xxx_thumb.jpg
            String thumbnailKey = key.replace(THUMBNAIL_SRC_PREFIX, THUMBNAIL_DST_PREFIX);
            int lastDot = thumbnailKey.lastIndexOf(".");
            if (lastDot > 0) {
                thumbnailKey = thumbnailKey.substring(0, lastDot) + THUMBNAIL_SUFFIX;
            }

            StorageService storage = this.storageServiceFactory.getStorageService();
            FileMetadata metadata = storage.getMetadata(thumbnailKey);
            if (!metadata.isExists()) {
                // 缩略图不存在，降级返回原图
                return this.readFileByKey(key);
            }

            stream = storage.getFileStream(thumbnailKey);
            InputStreamResource resource = new InputStreamResource(stream);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .contentLength(metadata.getSize().longValue())
                    .header("Cache-Control", CACHE_CONTROL_THUMBNAIL)
                    .body(resource);
        } catch (Exception e) {
            closeQuietly(stream);
            log.error("Failed to get thumbnail: key={}", key, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== 内部工具方法 ==========

    /**
     * 校验文件 key 安全性，防止路径遍历攻击
     *
     * @param key 文件 key
     * @return 是否安全
     */
    private boolean isValidFileKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        // 拒绝路径遍历
        if (key.contains("..") || key.contains("\\")) {
            return false;
        }
        // 拒绝绝对路径
        if (key.startsWith("/") || key.startsWith("~")) {
            return false;
        }
        return true;
    }

    /**
     * 安全关闭流（异常路径使用，防止资源泄漏）
     */
    private void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                log.warn("Failed to close stream", e);
            }
        }
    }
}
