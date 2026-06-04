package com.athlunakms.influencer.config;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import com.athlunakms.influencer.repository.ContentStorageConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig
implements WebMvcConfigurer {
    @Autowired
    private ContentStorageConfigRepository storageConfigRepository;

    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Object basePath;
        ContentStorageConfig config = this.storageConfigRepository.findFirstByIsActiveTrue().orElse(null);
        if (config != null && "LOCAL".equals(config.getStorageType()) && (basePath = config.getBasePath()) != null && !((String)basePath).isEmpty()) {
            if (!((String)basePath).endsWith("/")) {
                basePath = (String)basePath + "/";
            }
            String resourceLocation = "file:///" + ((String)basePath).replace("\\", "/");
            registry.addResourceHandler(new String[]{"/v1/files/**"}).addResourceLocations(new String[]{resourceLocation}).resourceChain(false);
        }
    }
}

