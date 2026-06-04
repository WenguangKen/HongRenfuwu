package com.athlunakms.influencer.service.storage;

import com.athlunakms.influencer.service.storage.FileMetadata;
import java.io.InputStream;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    public String upload(MultipartFile var1, String var2);

    public String upload(MultipartFile var1, String var2, String var3);

    public String upload(byte[] var1, String var2, String var3, String var4);

    public String upload(InputStream is, long size, String relativePath, String fileName, String contentType);

    public InputStream getFileStream(String var1);

    public FileMetadata getMetadata(String var1);

    public java.util.List<String> listAllObjects();

    public void delete(String var1);

    public String getPublicUrl(String var1);

    public String getPresignedDownloadUrl(String var1, int var2);

    public boolean testConnection();

    public Map<String, Long> getStorageMetrics();

    public String getStorageType();
}

