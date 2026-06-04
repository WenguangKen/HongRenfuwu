package com.athlunakms.influencer.service.storage;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class LocalStorageService
implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);
    private final ContentStorageConfig config;

    public LocalStorageService(ContentStorageConfig config) {
        this.config = config;
    }

    public String upload(MultipartFile file, String relativePath) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = this.getExtension(originalName);
            String newFileName = UUID.randomUUID().toString() + extension;
            Path targetDir = Paths.get(this.config.getBasePath(), relativePath);
            Files.createDirectories(targetDir, new FileAttribute[0]);
            Path targetPath = targetDir.resolve(newFileName);
            file.transferTo(targetPath);
            String fileKey = relativePath + "/" + newFileName;
            log.info("[LocalStorage] Uploaded file: {}", (Object)fileKey);
            return fileKey;
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to upload file", (Throwable)e);
            throw new RuntimeException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public String upload(MultipartFile file, String relativePath, String fileName) {
        try {
            Path targetDir = Paths.get(this.config.getBasePath(), relativePath);
            Files.createDirectories(targetDir, new FileAttribute[0]);
            Path targetPath = targetDir.resolve(fileName);
            file.transferTo(targetPath);
            String fileKey = relativePath + "/" + fileName;
            log.info("[LocalStorage] Uploaded file with custom name: {}", (Object)fileKey);
            return fileKey;
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to upload file", (Throwable)e);
            throw new RuntimeException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public String upload(byte[] data, String relativePath, String fileName, String contentType) {
        try {
            Path targetDir = Paths.get(this.config.getBasePath(), relativePath);
            Files.createDirectories(targetDir, new FileAttribute[0]);
            Path targetPath = targetDir.resolve(fileName);
            Files.write(targetPath, data, new OpenOption[0]);
            String fileKey = relativePath + "/" + fileName;
            log.info("[LocalStorage] Uploaded bytes: {}", (Object)fileKey);
            return fileKey;
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to upload bytes", (Throwable)e);
            throw new RuntimeException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public String upload(InputStream is, long size, String relativePath, String fileName, String contentType) {
        try {
            Path targetDir = Paths.get(this.config.getBasePath(), relativePath);
            Files.createDirectories(targetDir, new FileAttribute[0]);
            Path targetPath = targetDir.resolve(fileName);
            Files.copy(is, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            String fileKey = relativePath + "/" + fileName;
            log.info("[LocalStorage] Uploaded stream: {}", (Object)fileKey);
            return fileKey;
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to upload stream", (Throwable)e);
            throw new RuntimeException("\u6587\u4ef6\u4e0a\u4f20\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public InputStream getFileStream(String fileKey) {
        try {
            Path filePath = Paths.get(this.config.getBasePath(), fileKey);
            if (!Files.exists(filePath, new LinkOption[0])) {
                throw new FileNotFoundException("\u6587\u4ef6\u4e0d\u5b58\u5728: " + fileKey);
            }
            return new BufferedInputStream(Files.newInputStream(filePath, new OpenOption[0]));
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to read file: {}", (Object)fileKey, (Object)e);
            throw new RuntimeException("\u6587\u4ef6\u8bfb\u53d6\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public FileMetadata getMetadata(String fileKey) {
        Path filePath = Paths.get(this.config.getBasePath(), fileKey);
        FileMetadata metadata = new FileMetadata();
        metadata.setFileKey(fileKey);
        if (Files.exists(filePath, new LinkOption[0])) {
            try {
                metadata.setExists(true);
                metadata.setSize(Long.valueOf(Files.size(filePath)));
                metadata.setFileName(filePath.getFileName().toString());
                metadata.setContentType(Files.probeContentType(filePath));
                metadata.setLastModified(Long.valueOf(Files.getLastModifiedTime(filePath, new LinkOption[0]).toMillis()));
            }
            catch (IOException e) {
                log.error("[LocalStorage] Failed to get metadata: {}", (Object)fileKey, (Object)e);
            }
        } else {
            metadata.setExists(false);
        }
        return metadata;
    }

    public java.util.List<String> listAllObjects() {
        java.util.List<String> objects = new java.util.ArrayList<>();
        try {
            Path basePath = Paths.get(this.config.getBasePath(), new String[0]);
            if (Files.exists(basePath, new LinkOption[0])) {
                Files.walk(basePath).filter(Files::isRegularFile).forEach(path -> {
                    String relativePath = basePath.relativize(path).toString().replace("\\", "/");
                    objects.add(relativePath);
                });
            }
        } catch (IOException e) {
            log.error("[LocalStorage] Failed to list objects", e);
        }
        return objects;
    }

    public void delete(String fileKey) {
        try {
            Path filePath = Paths.get(this.config.getBasePath(), fileKey);
            if (Files.exists(filePath, new LinkOption[0])) {
                Files.delete(filePath);
                log.info("[LocalStorage] Deleted file: {}", (Object)fileKey);
            }
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to delete file: {}", (Object)fileKey, (Object)e);
            throw new RuntimeException("\u6587\u4ef6\u5220\u9664\u5931\u8d25: " + e.getMessage(), e);
        }
    }

    public String getPublicUrl(String fileKey) {
        String prefix = this.config.getPublicUrlPrefix();
        if (prefix == null || prefix.isEmpty()) {
            return fileKey;
        }
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        if (fileKey.startsWith("/")) {
            fileKey = fileKey.substring(1);
        }
        return prefix + "/" + fileKey;
    }

    public String getPresignedDownloadUrl(String fileKey, int expireMinutes) {
        return this.getPublicUrl(fileKey);
    }

    public boolean testConnection() {
        try {
            Path basePath = Paths.get(this.config.getBasePath(), new String[0]);
            if (!Files.exists(basePath, new LinkOption[0])) {
                Files.createDirectories(basePath, new FileAttribute[0]);
            }
            Path testFile = basePath.resolve(".test_" + System.currentTimeMillis());
            Files.write(testFile, "test".getBytes(), new OpenOption[0]);
            Files.delete(testFile);
            log.info("[LocalStorage] Connection test passed: {}", (Object)this.config.getBasePath());
            return true;
        }
        catch (IOException e) {
            log.error("[LocalStorage] Connection test failed", (Throwable)e);
            return false;
        }
    }

    public Map<String, Long> getStorageMetrics() {
        HashMap<String, Long> metrics = new HashMap<String, Long>();
        long totalSize = 0L;
        try {
            Path basePath = Paths.get(this.config.getBasePath(), new String[0]);
            if (Files.exists(basePath, new LinkOption[0])) {
                totalSize = Files.walk(basePath, new FileVisitOption[0]).filter(p -> p.toFile().isFile()).mapToLong(p -> p.toFile().length()).sum();
            }
        }
        catch (IOException e) {
            log.error("[LocalStorage] Failed to calculate storage metrics", (Throwable)e);
        }
        metrics.put("used", totalSize);
        metrics.put("total", 0L);
        return metrics;
    }

    public String getStorageType() {
        return "LOCAL";
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}

