package com.athlunakms.influencer.config;

import java.time.Duration;
import java.util.HashMap;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30L)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer((RedisSerializer)new StringRedisSerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer((RedisSerializer)new GenericJackson2JsonRedisSerializer())).disableCachingNullValues();
        HashMap<String, RedisCacheConfiguration> cacheConfigs = new HashMap<String, RedisCacheConfiguration>();
        cacheConfigs.put("contents", defaultConfig.entryTtl(Duration.ofMinutes(10L)));
        cacheConfigs.put("influencers", defaultConfig.entryTtl(Duration.ofHours(1L)));
        cacheConfigs.put("storageConfig", defaultConfig.entryTtl(Duration.ofHours(24L)));
        cacheConfigs.put("fileMetadata", defaultConfig.entryTtl(Duration.ofMinutes(5L)));
        return RedisCacheManager.builder((RedisConnectionFactory)factory).cacheDefaults(defaultConfig).withInitialCacheConfigurations(cacheConfigs).transactionAware().build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(factory);
        template.setKeySerializer((RedisSerializer)new StringRedisSerializer());
        template.setValueSerializer((RedisSerializer)new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer((RedisSerializer)new StringRedisSerializer());
        template.setHashValueSerializer((RedisSerializer)new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}

