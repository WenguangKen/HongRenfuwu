package com.athlunakms.influencer.service.storage;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.admin.MinioAdminClient;
import io.minio.admin.messages.info.Message;
import io.minio.messages.Item;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.CORSConfiguration;
import java.util.Arrays;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

public class MinioStorageService
        implements StorageService {
    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);

    /**
     * 清理 S3 对象键，确保不包含不支持的字符。
     * 仅保留 ASCII 字母、数字、斜杠、横杠、下划线和点。
     */
    private static String sanitizeKey(String key) {
        if (key == null) return key;
        // 将每个路径段单独清理，保留目录分隔符 '/'
        String[] parts = key.split("/");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append('/');
            sb.append(parts[i].replaceAll("[^a-zA-Z0-9._\\-]", "_"));
        }
        String sanitized = sb.toString();
        if (!sanitized.equals(key)) {
            log.warn("[MinIO] Sanitized S3 key: '{}' -> '{}'", key, sanitized);
        }
        return sanitized;
    }
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final ContentStorageConfig config;
    private final String bucketName;

    public MinioStorageService(ContentStorageConfig config) {
        this.config = config;
        this.bucketName = config.getBasePath();
        if (config.getEndpoint() == null || config.getEndpoint().isEmpty()) {
            throw new IllegalArgumentException("MinIO 必须配置 Endpoint 地址");
        }
        AwsBasicCredentials credentials = AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(config.getRegion() != null ? config.getRegion() : "us-east-1"))
                .endpointOverride(URI.create(config.getEndpoint()))
                .forcePathStyle(true)
                .build();
        this.presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(config.getRegion() != null ? config.getRegion() : "us-east-1"))
                .endpointOverride(URI.create(config.getEndpoint()))
                .build();
        log.debug("[MinIO] Storage service initialized - endpoint: {}, bucket: {}", config.getEndpoint(),
                this.bucketName);
        // 异步初始化：避免 ensureBucketPublic/ensureCors 的 S3 重试阻塞第一次请求
        Thread initThread = new Thread(() -> {
            try {
                this.ensureBucketPublic();
                this.ensureCors();
            } catch (Exception e) {
                log.warn("[MinIO] Failed to set public policy/CORS during async initialization: {}", e.getMessage());
            }
        }, "minio-init");
        initThread.setDaemon(true);
        initThread.start();
    }

    public String upload(MultipartFile file, String relativePath) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = this.getExtension(originalName);
            String newFileName = UUID.randomUUID().toString() + extension;
            String key = sanitizeKey(relativePath + "/" + newFileName);
            PutObjectRequest request = PutObjectRequest.builder().bucket(this.bucketName).key(key)
                    .contentType(file.getContentType()).contentLength(file.getSize()).build();
            this.s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("[MinIO] Uploaded file: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[MinIO] Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    public String upload(MultipartFile file, String relativePath, String fileName) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);
            PutObjectRequest request = PutObjectRequest.builder().bucket(this.bucketName).key(key)
                    .contentType(file.getContentType()).contentLength(file.getSize()).build();
            this.s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("[MinIO] Uploaded file with custom name: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[MinIO] Failed to upload file", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    public String upload(byte[] data, String relativePath, String fileName, String contentType) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);
            PutObjectRequest request = PutObjectRequest.builder().bucket(this.bucketName).key(key)
                    .contentType(contentType).contentLength((long) data.length).build();
            this.s3Client.putObject(request, RequestBody.fromBytes(data));
            log.info("[MinIO] Uploaded bytes: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[MinIO] Failed to upload bytes", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    public String upload(InputStream is, long size, String relativePath, String fileName, String contentType) {
        try {
            String key = sanitizeKey(relativePath + "/" + fileName);
            log.info("[MinIO DEBUG] Attempting putObject. Bucket: '{}', Key: '{}', ContentType: '{}', Size: {}", this.bucketName, key, contentType, size);
            PutObjectRequest request = PutObjectRequest.builder().bucket(this.bucketName).key(key)
                    .contentType(contentType).contentLength(size).build();
            this.s3Client.putObject(request, RequestBody.fromInputStream(is, size));
            log.info("[MinIO] Uploaded stream: {}", key);
            return key;
        } catch (Exception e) {
            log.error("[MinIO] Failed to upload stream", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    public InputStream getFileStream(String fileKey) {
        try {
            GetObjectRequest request = GetObjectRequest.builder().bucket(this.bucketName).key(fileKey).build();
            return this.s3Client.getObject(request);
        } catch (Exception e) {
            log.error("[MinIO] Failed to read file: {}", fileKey, e);
            throw new RuntimeException("文件读取失败: " + e.getMessage(), e);
        }
    }

    public FileMetadata getMetadata(String fileKey) {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileKey(fileKey);
        try {
            HeadObjectRequest request = HeadObjectRequest.builder().bucket(this.bucketName).key(fileKey).build();
            HeadObjectResponse response = this.s3Client.headObject(request);
            metadata.setExists(true);
            metadata.setSize(response.contentLength());
            metadata.setContentType(response.contentType());
            metadata.setLastModified(response.lastModified().toEpochMilli());
            int lastSlash = fileKey.lastIndexOf(47);
            metadata.setFileName(lastSlash >= 0 ? fileKey.substring(lastSlash + 1) : fileKey);
        } catch (NoSuchKeyException e) {
            metadata.setExists(false);
        } catch (Exception e) {
            log.error("[MinIO] Failed to get metadata: {}", fileKey, e);
            metadata.setExists(false);
        }
        return metadata;
    }

    public java.util.List<String> listAllObjects() {
        java.util.List<String> objects = new java.util.ArrayList<>();
        try {
            MinioClient minioClient = MinioClient.builder().endpoint(this.config.getEndpoint())
                    .credentials(this.config.getAccessKey(), this.config.getSecretKey()).build();
            Iterable<Result<Item>> results = minioClient
                    .listObjects(ListObjectsArgs.builder().bucket(this.bucketName).recursive(true).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    objects.add(item.objectName());
                }
            }
        } catch (Exception e) {
            log.error("[MinIO] Failed to list objects", e);
        }
        return objects;
    }

    public void delete(String fileKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(this.bucketName).key(fileKey).build();
            this.s3Client.deleteObject(request);
            log.info("[MinIO] Deleted file: {}", fileKey);
        } catch (Exception e) {
            log.error("[MinIO] Failed to delete file: {}", fileKey, e);
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }

    public String getPublicUrl(String fileKey) {
        String cdnDomain = this.config.getCdnDomain();
        if (cdnDomain != null && !cdnDomain.isEmpty()) {
            if (cdnDomain.endsWith("/")) {
                cdnDomain = cdnDomain.substring(0, cdnDomain.length() - 1);
            }
            return cdnDomain + "/" + fileKey;
        }
        String prefix = this.config.getPublicUrlPrefix();
        if (prefix != null && !prefix.isEmpty()) {
            if (prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            return prefix + "/" + fileKey;
        }
        String ep = this.config.getEndpoint();
        if (ep.endsWith("/")) {
            ep = ep.substring(0, ep.length() - 1);
        }
        return String.format("%s/%s/%s", ep, this.bucketName, fileKey);
    }

    public String getPresignedDownloadUrl(String fileKey, int expireMinutes) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder().bucket(this.bucketName).key(fileKey).build();
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expireMinutes)).getObjectRequest(getRequest).build();
            return this.presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("[MinIO] Failed to generate presigned URL: {}", fileKey, e);
            return this.getPublicUrl(fileKey);
        }
    }

    public boolean testConnection() {
        try {
            HeadBucketRequest request = HeadBucketRequest.builder().bucket(this.bucketName).build();
            this.s3Client.headBucket(request);
            log.info("[MinIO] Connection test passed - bucket: {}", this.bucketName);
            this.ensureBucketPublic();
            this.ensureCors();
            return true;
        } catch (NoSuchBucketException e) {
            try {
                this.s3Client.createBucket(CreateBucketRequest.builder().bucket(this.bucketName).build());
                log.info("[MinIO] Auto-created bucket: {}", this.bucketName);
                this.ensureBucketPublic();
                this.ensureCors();
                return true;
            } catch (Exception ex) {
                log.error("[MinIO] Failed to create bucket: {}", this.bucketName, ex);
                return false;
            }
        } catch (Exception e) {
            log.error("[MinIO] Connection test failed", e);
            return false;
        }
    }

    private void ensureBucketPublic() {
        try {
            String policy = "{\n" +
                    "  \"Version\":\"2012-10-17\",\n" +
                    "  \"Statement\":[\n" +
                    "    {\n" +
                    "      \"Sid\":\"PublicRead\",\n" +
                    "      \"Effect\":\"Allow\",\n" +
                    "      \"Principal\":\"*\",\n" +
                    "      \"Action\":[\"s3:GetObject\"],\n" +
                    "      \"Resource\":[\"arn:aws:s3:::" + this.bucketName + "/*\"]\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            this.s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                    .bucket(this.bucketName)
                    .policy(policy)
                    .build());
            log.info("[MinIO] Successfully set public read policy for bucket: {}", this.bucketName);
        } catch (Exception e) {
            log.error("[MinIO] Failed to set bucket policy", e);
        }
    }

    private void ensureCors() {
        try {
            CORSRule rule = CORSRule.builder()
                    .allowedHeaders("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                    .allowedOrigins("*") // 生产环境建议替换为具体域名
                    .exposeHeaders("ETag", "Content-Type")
                    .maxAgeSeconds(3600)
                    .build();

            this.s3Client.putBucketCors(PutBucketCorsRequest.builder()
                    .bucket(this.bucketName)
                    .corsConfiguration(CORSConfiguration.builder()
                            .corsRules(Arrays.asList(rule))
                            .build())
                    .build());
            log.info("[MinIO] Successfully set CORS configuration for bucket: {}", this.bucketName);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("501")) {
                log.warn("[MinIO] CORS configuration not supported by server (501 Not Implemented), skipping.");
            } else {
                log.error("[MinIO] Failed to set bucket CORS", e);
            }
        }
    }

    public Map<String, Long> getStorageMetrics() {
        Map<String, Long> metrics = new HashMap<>();
        long totalSize = 0L;
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
            MinioClient minioClient = MinioClient.builder().endpoint(this.config.getEndpoint())
                    .credentials(this.config.getAccessKey(), this.config.getSecretKey())
                    .httpClient(httpClient)
                    .build();
            Iterable<Result<Item>> results = minioClient
                    .listObjects(ListObjectsArgs.builder().bucket(this.bucketName).recursive(true).build());
            for (Result<Item> result : results) {
                Item item = result.get();
                totalSize += item.size();
            }
        } catch (Exception e) {
            log.error("[MinIO] Failed to calculate storage metrics", e);
        }
        metrics.put("used", totalSize);
        try {
            MinioAdminClient adminClient = MinioAdminClient.builder().endpoint(this.config.getEndpoint())
                    .credentials(this.config.getAccessKey(), this.config.getSecretKey())
                    .build();
            Message serverInfoObj = adminClient.getServerInfo();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            JsonNode root = mapper.valueToTree(serverInfoObj);
            log.info("[MinIO] Server info JSON keys: {}", root.fieldNames().hasNext() ? root.toString().substring(0, Math.min(root.toString().length(), 500)) : "empty");
            long totalPhysical = 0L;
            // Strategy 1: info.pools.*.*.rawCapacity
            JsonNode infoNode = root.path("info");
            JsonNode poolsNode = infoNode.isMissingNode() ? root.path("pools") : infoNode.path("pools");
            if (poolsNode.isObject()) {
                Iterator<String> poolNames = poolsNode.fieldNames();
                while (poolNames.hasNext()) {
                    JsonNode pool = poolsNode.get(poolNames.next());
                    if (!pool.isObject())
                        continue;
                    Iterator<String> setNames = pool.fieldNames();
                    while (setNames.hasNext()) {
                        JsonNode set = pool.get(setNames.next());
                        long cap = set.path("rawCapacity").asLong();
                        totalPhysical += cap;
                    }
                }
            }
            // Strategy 2: info.servers[].disks[].totalSpace
            if (totalPhysical == 0L) {
                JsonNode serversNode = infoNode.isMissingNode() ? root.path("servers") : infoNode.path("servers");
                if (serversNode.isArray()) {
                    for (JsonNode server : serversNode) {
                        JsonNode disks = server.path("disks");
                        if (!disks.isArray())
                            continue;
                        for (JsonNode disk : disks) {
                            long ts = disk.path("totalSpace").asLong();
                            if (ts == 0L) ts = disk.path("total_space").asLong();
                            if (ts == 0L) ts = disk.path("totalspace").asLong();
                            totalPhysical += ts;
                        }
                    }
                }
            }
            // Strategy 3: Deep search for rawCapacity
            if (totalPhysical == 0L) {
                totalPhysical = this.findFirstLong(root, "rawCapacity");
            }
            // Strategy 4: Deep search for totalSpace
            if (totalPhysical == 0L) {
                totalPhysical = this.sumAllLong(root, "totalSpace");
                if (totalPhysical == 0L) totalPhysical = this.sumAllLong(root, "total_space");
            }
            metrics.put("total", totalPhysical);
            log.info("[MinIO] Retrieved actual physical capacity: {} bytes ({} GB)", totalPhysical, totalPhysical / (1024L * 1024L * 1024L));
            try {
                long quota = adminClient.getBucketQuota(this.bucketName);
                if (quota > 0L) {
                    metrics.put("total", quota);
                    log.debug("[MinIO] Bucket quota active, overriding physical capacity: {} bytes", quota);
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            log.warn("[MinIO] Failed to retrieve server info via Admin API: {}. Will use Java filesystem total space as fallback.", e.getMessage());
            // Fallback: use Java File system to get the total space of the root filesystem
            try {
                java.io.File root = new java.io.File("/");
                long diskTotal = root.getTotalSpace();
                if (diskTotal > 0L) {
                    metrics.put("total", diskTotal);
                    log.info("[MinIO] Fallback: using OS disk total space: {} bytes ({} GB)", diskTotal, diskTotal / (1024L * 1024L * 1024L));
                } else {
                    metrics.put("total", 0L);
                }
            } catch (Exception diskErr) {
                log.warn("[MinIO] Disk total space fallback also failed: {}", diskErr.getMessage());
                metrics.put("total", 0L);
            }
        }
        return metrics;
    }

    private long findFirstLong(JsonNode node, String fieldName) {
        if (node.has(fieldName) && node.get(fieldName).isNumber()) {
            return node.get(fieldName).asLong();
        }
        if (node.isObject()) {
            Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                long res = this.findFirstLong(elements.next(), fieldName);
                if (res <= 0L)
                    continue;
                return res;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                long res = this.findFirstLong(child, fieldName);
                if (res <= 0L)
                    continue;
                return res;
            }
        }
        return 0L;
    }

    private long sumAllLong(JsonNode node, String fieldName) {
        long sum = 0L;
        if (node.has(fieldName) && node.get(fieldName).isNumber()) {
            sum += node.get(fieldName).asLong();
        }
        if (node.isObject()) {
            Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                sum += this.sumAllLong(elements.next(), fieldName);
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                sum += this.sumAllLong(child, fieldName);
            }
        }
        return sum;
    }

    public String getStorageType() {
        return "MINIO";
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
