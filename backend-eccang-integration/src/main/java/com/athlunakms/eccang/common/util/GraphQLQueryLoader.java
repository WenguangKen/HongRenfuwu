package com.athlunakms.eccang.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GraphQL 查询加载器
 * 将 .graphql 文件从 resources 目录加载并缓存避免硬编码字符串
 */
@Slf4j
@Component
public class GraphQLQueryLoader {

    private final Map<String, String> queryCache = new ConcurrentHashMap<>();

    /**
     * 获取指定名称的 GraphQL 查询
     * 
     * @param queryName 查询文件名不含 .graphql 后缀
     * @return 查询字符串
     */
    public String getQuery(String queryName) {
        return queryCache.computeIfAbsent(queryName, name -> {
            try {
                String path = "graphql/" + name + ".graphql";
                Resource resource = new ClassPathResource(path);
                if (!resource.exists()) {
                    log.error("GraphQL resource not found: {}", path);
                    throw new RuntimeException("GraphQL resource not found: " + path);
                }
                String query = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                log.debug("Loaded GraphQL query: {}", name);
                return query;
            } catch (IOException e) {
                log.error("Failed to load GraphQL query: {}", name, e);
                throw new RuntimeException("Failed to load GraphQL query: " + name, e);
            }
        });
    }
}
