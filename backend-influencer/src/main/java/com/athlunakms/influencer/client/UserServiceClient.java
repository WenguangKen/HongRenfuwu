package com.athlunakms.influencer.client;

import com.athlunakms.influencer.client.UserServiceClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UserServiceClient {
    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    @Value(value="${services.user.url:http://localhost:8080}")
    private String userServiceUrl;
    private static final long CACHE_TTL_MS = 300000L;
    private final ConcurrentHashMap<Long, CachedName> nameCache = new ConcurrentHashMap<>();

    public Map<Long, String> getUserNames(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<Long, String> result = new HashMap<Long, String>();
        ArrayList<Long> uncachedIds = new ArrayList<Long>();
        for (Long id2 : userIds) {
            CachedName cached = (CachedName)this.nameCache.get(id2);
            if (cached != null && !cached.isExpired()) {
                result.put(id2, cached.name);
                continue;
            }
            if (cached != null) {
                this.nameCache.remove(id2);
            }
            uncachedIds.add(id2);
        }
        if (uncachedIds.isEmpty()) {
            return result;
        }
        try {
            String idsStr = uncachedIds.stream().map(String::valueOf).collect(Collectors.joining(","));
            String url = UriComponentsBuilder.fromUriString((String)(this.userServiceUrl + "/api/v1.0/users/names")).queryParam("ids", new Object[]{idsStr}).toUriString();
            log.debug("Calling user service for {} uncached IDs: {}", (Object)uncachedIds.size(), (Object)url);
            ResponseEntity<Map<String, String>> response = this.restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, String>>() {}, new Object[0]);
            Map<String, String> fetched = response.getBody();
            if (fetched != null) {
                fetched.forEach((idStr, name) -> {
                    try {
                        Long id = Long.valueOf(idStr);
                        this.nameCache.put(id, new CachedName(name));
                        result.put(id, (String)name);
                    }
                    catch (NumberFormatException ex) {
                        log.warn("Invalid ID format from user service: {}", idStr);
                    }
                });
            }
        }
        catch (Exception e) {
            log.warn("Failed to get user names from user service, will fallback to DB: {}", (Object)e.getMessage());
        }
        List<Long> unresolvedIds = uncachedIds.stream().filter(id -> !result.containsKey(id)).collect(Collectors.toList());
        if (!unresolvedIds.isEmpty()) {
            try {
                String placeholders = unresolvedIds.stream().map(id -> "?").collect(Collectors.joining(","));
                List<Map<String, Object>> rows = new ArrayList<>();
                boolean sysUserSuccess = false;
                
                try {
                    String sql = "SELECT id, COALESCE(real_name, username) AS name FROM sys_user WHERE id IN (" + placeholders + ") AND deleted = 0";
                    rows = this.jdbcTemplate.queryForList(sql, unresolvedIds.toArray());
                    sysUserSuccess = true;
                } catch (Exception e1) {
                    log.debug("Failed querying sys_user (maybe table or column not exist): {}", e1.getMessage());
                }
                
                if (!sysUserSuccess || rows.isEmpty()) {
                    try {
                        String sql2 = "SELECT id, username AS name FROM users WHERE id IN (" + placeholders + ") AND status = 1";
                        List<Map<String, Object>> usersRows = this.jdbcTemplate.queryForList(sql2, unresolvedIds.toArray());
                        if (usersRows != null && !usersRows.isEmpty()) {
                            rows.addAll(usersRows);
                        }
                    } catch (Exception e2) {
                        log.warn("Query from users table also failed: {}", e2.getMessage());
                    }
                }
                
                int resolvedCount = 0;
                for (Map<String, Object> row : rows) {
                    Long id3 = ((Number)row.get("id")).longValue();
                    String name2 = (String)row.get("name");
                    if (name2 == null || name2.trim().isEmpty()) continue;
                    this.nameCache.put(id3, new CachedName(name2));
                    result.put(id3, name2);
                    resolvedCount++;
                }
                if (resolvedCount > 0) {
                    log.info("Resolved {} user names via direct DB fallback", resolvedCount);
                } else {
                    log.warn("Direct DB fallback returned no matching user names for IDs: {}", unresolvedIds);
                }
            }
            catch (Exception e) {
                log.warn("Unexpected error in DB fallback: {}", (Object)e.getMessage());
            }
        }
        return result;
    }

    public Long findUserIdByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String trimmedName = name.trim();
        try {
            Long foundId = null;
            try {
                String sql = "SELECT id FROM sys_user WHERE (username = ? OR real_name = ?) AND deleted = 0 LIMIT 1";
                List<Long> ids = this.jdbcTemplate.queryForList(sql, Long.class, trimmedName, trimmedName);
                if (ids != null && !ids.isEmpty() && ids.get(0) != null) {
                    foundId = ids.get(0);
                }
            } catch (Exception e1) {
                log.debug("Failed to query sys_user in findUserIdByName: {}", e1.getMessage());
            }
            
            if (foundId == null) {
                try {
                    String sql2 = "SELECT id FROM users WHERE username = ? AND status = 1 LIMIT 1";
                    List<Long> ids2 = this.jdbcTemplate.queryForList(sql2, Long.class, trimmedName);
                    if (ids2 != null && !ids2.isEmpty() && ids2.get(0) != null) {
                        foundId = ids2.get(0);
                    }
                } catch(Exception e2) {
                     log.debug("Failed to query users in findUserIdByName: {}", e2.getMessage());
                }
            }
            
            if (foundId != null) {
                log.debug("Found user ID {} for name '{}'", foundId, trimmedName);
                return foundId;
            }
            log.warn("No user found for name '{}'", trimmedName);
            return null;
        }
        catch (Exception e) {
            log.error("Failed to find user by name {}: {}", trimmedName, e.getMessage());
            return null;
        }
    }

    public Map<Long, String> getAllUserNames() {
        HashMap<Long, String> result = new HashMap<Long, String>();
        try {
            List<Map<String, Object>> rows = new ArrayList<>();
            boolean sysUserSuccess = false;
            
            try {
                String sql2 = "SELECT id, COALESCE(real_name, username) AS name FROM sys_user WHERE deleted = 0";
                rows = this.jdbcTemplate.queryForList(sql2);
                sysUserSuccess = true;
            } catch (Exception e1) {
                 log.debug("Failed to get all user names from sys_user: {}", e1.getMessage());
            }
            
            if (!sysUserSuccess || rows.isEmpty()) {
                try {
                    String sql = "SELECT id, username AS name FROM users WHERE status = 1";
                    List<Map<String, Object>> userRows = this.jdbcTemplate.queryForList(sql);
                    if (userRows != null && !userRows.isEmpty()) {
                        rows.addAll(userRows);
                    }
                } catch (Exception e2) {
                    log.warn("Failed to get all user names from users: {}", e2.getMessage());
                }
            }
            
            for (Map<String, Object> row : rows) {
                Long id = ((Number)row.get("id")).longValue();
                String name = (String)row.get("name");
                if (name == null || name.trim().isEmpty()) continue;
                result.put(id, name);
            }
        }
        catch (Exception e) {
            log.warn("Failed to get all user names: {}", (Object)e.getMessage());
        }
        return result;
    }

    public UserServiceClient(RestTemplate restTemplate, JdbcTemplate jdbcTemplate) {
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static class CachedName {
        final String name;
        final long timestamp;

        CachedName(String name) {
            this.name = name;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - this.timestamp > 300000L;
        }
    }
}

