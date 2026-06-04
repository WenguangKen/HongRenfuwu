package com.athlunakms.influencer.service.storage;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

/**
 * AWS S3 存储服务实现
 * 支持 AWS S3、阿里云 OSSS3 兼容模式、MinIO 等
 */
@Slf4j
public class S3StorageService implements StorageService {

    /**
     * 清理 S3 对象键，确保不包含不支持的字符。
     * 仅保留 ASCII 字母、数字、斜杠、横杠、下划线和点。
     */
    private static String sanitizeKey(String key) {
        if (key == null) return key;
        String[] parts = key.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append('/');
            sb.append(parts[i].replaceAll("[^a-zA-Z0-9._\\-]", "_"));
        }
        String sanitized = sb.toString();
        if (!sanitized.equals(key)) {
            log.warn("[S3] Sanitized S3 key: '{}' -> '{}'", key, sanitized);
        }
        return sanitized;
    }

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final ContentStorageConfig config;
    private final String bucketName;

    public S3StorageService(ContentStorageConfig config) {
        this.config = config;
        this.bucketName = config.getBasePath(); // basePath 作为 bucket 名称

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.getAccessKey(),
                config.getSecretKey());

        // 构建 S3 客户端
        var clientBuilder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(config.getRegion() != null ? config.getRegion() : "us-east-1"));

        // 自定义端点用于阿里云 OSS、MinIO 等
        if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
            clientBuilder.endpointOverride(URI.create(config.getEndpoint()))
                    .forcePathStyle(true);  // MinIO 需要 path-style 访问
        }

        this.s3Client = clientBuilder.build();

        // 构建预签名器
        var presignerBuilder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(config.getRegion() != null ? config.getRegion() : "us-east-1"));

        if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
            presignerBuilder.endpointOverride(URI.create(config.getEndpoint()));
            // 注意：S3Presigner 不支持 forcePathStyle，需要在 endpoint 中包含 bucket
        }

        this.presigner = presignerBuilder.build();
    }

    @Override
    public String upload(org.springframework.web.multipart.MultipartFile file, String relativePath) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);
            String newFileName = UUID.randomUUID().toString() + extension;
            String key = sanitizeKey(relativePath + "/" + newFileName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("[S3] Uploaded file: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[S3] Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String upload(byte[] data, String relativePath, String fileName, String contentType) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) data.length)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(data));

            log.info("[S3] Uploaded bytes: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[S3] Failed to upload bytes", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String upload(InputStream is, long size, String relativePath, String fileName, String contentType) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(is, size));

            log.info("[S3] Uploaded stream: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[S3] Failed to upload stream", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream getFileStream(String fileKey) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            return s3Client.getObject(request);
        } catch (Exception e) {
            log.error("[S3] Failed to read file: {}", fileKey, e);
            throw new RuntimeException("文件读取失败: " + e.getMessage(), e);
        }
    }

    @Override
    public FileMetadata getMetadata(String fileKey) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileKey(fileKey);

        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            HeadObjectResponse response = s3Client.headObject(request);

            metadata.setExists(true);
            metadata.setSize(response.contentLength());
            metadata.setContentType(response.contentType());
            metadata.setLastModified(response.lastModified().toEpochMilli());

            // 从 key 中提取文件名
            int lastSlash = fileKey.lastIndexOf('/');
            metadata.setFileName(lastSlash >= 0 ? fileKey.substring(lastSlash + 1) : fileKey);
        } catch (NoSuchKeyException e) {
            metadata.setExists(false);
        } catch (Exception e) {
            log.error("[S3] Failed to get metadata: {}", fileKey, e);
            metadata.setExists(false);
        }

        return metadata;
    }

    @Override
    public java.util.List<String> listAllObjects() {
        java.util.List<String> objects = new java.util.ArrayList<>();
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response;
            do {
                response = s3Client.listObjectsV2(request);
                for (S3Object obj : response.contents()) {
                    objects.add(obj.key());
                }
                request = request.toBuilder()
                        .continuationToken(response.nextContinuationToken())
                        .build();
            } while (response.isTruncated());
        } catch (Exception e) {
            log.error("[S3] Failed to list objects", e);
        }
        return objects;
    }

    @Override
    public void delete(String fileKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(request);
            log.info("[S3] Deleted file: {}", fileKey);
        } catch (Exception e) {
            log.error("[S3] Failed to delete file: {}", fileKey, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPublicUrl(String fileKey) {
        // 如果配置了 CDN 域名，优先使用 CDN
        String cdnDomain = config.getCdnDomain();
        if (cdnDomain != null && !cdnDomain.isEmpty()) {
            if (cdnDomain.endsWith("/")) {
                cdnDomain = cdnDomain.substring(0, cdnDomain.length() - 1);
            }
            return cdnDomain + "/" + fileKey;
        }

        // 否则使用公开 URL 前缀
        String prefix = config.getPublicUrlPrefix();
        if (prefix != null && !prefix.isEmpty()) {
            if (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            return prefix + "/" + fileKey;
        }

        // 默认返回 S3 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                config.getRegion() != null ? config.getRegion() : "us-east-1",
                fileKey);
    }

    @Override
    public String getPresignedDownloadUrl(String fileKey, int expireMinutes) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expireMinutes))
                    .getObjectRequest(getRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("[S3] Failed to generate presigned URL: {}", fileKey, e);
            // 降级返回公开 URL
            return getPublicUrl(fileKey);
        }
    }

    @Override
    public boolean testConnection() {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(request);
            log.info("[S3] Connection test passed for bucket: {}", bucketName);
            return true;
        } catch (Exception e) {
            log.error("[S3] Connection test failed", e);
            return false;
        }
    }

    @Override
    public String getStorageType() {
        return "S3";
    }

    @Override
    public String upload(org.springframework.web.multipart.MultipartFile file, String relativePath, String fileName) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("[S3] Uploaded file with custom name: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[S3] Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.Map<String, Long> getStorageMetrics() {
        java.util.Map<String, Long> metrics = new java.util.HashMap<>();
        try {
            // 列出所有对象并计算它们的大小总和
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            long totalSize = 0;
            ListObjectsV2Response response;
            do {
                response = s3Client.listObjectsV2(request);
                for (S3Object obj : response.contents()) {
                    totalSize += obj.size();
                }
                request = request.toBuilder()
                        .continuationToken(response.nextContinuationToken())
                        .build();
            } while (response.isTruncated());

            metrics.put("used", totalSize);
            metrics.put("total", -1L); // S3 没有固定配额限制
        } catch (Exception e) {
            log.error("[S3] Failed to get storage metrics", e);
            metrics.put("used", 0L);
            metrics.put("total", -1L);
        }
        return metrics;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
