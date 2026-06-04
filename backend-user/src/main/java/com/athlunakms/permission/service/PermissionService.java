package com.athlunakms.permission.service;

import com.athlunakms.permission.dto.PermissionResponse;
import com.athlunakms.permission.entity.Permission;
import com.athlunakms.permission.repository.PermissionRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {
    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PERMISSION_CACHE_PREFIX = "user_permissions:";
    private static final long CACHE_TTL = 30L;

    @Transactional(readOnly = true)
    public PermissionResponse getCurrentUserPermissions(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        PermissionResponse cached = (PermissionResponse) this.redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        List<Permission> permissions = this.permissionRepository.findPermissionsByUserId(userId);
        List<String> pagePermissions = permissions.stream().filter(p -> p.getType() == Permission.PermissionType.page)
                .map(Permission::getPermissionKey).collect(Collectors.toList());
        List<String> operationPermissions = permissions.stream().filter(
                p -> p.getType() == Permission.PermissionType.tab || p.getType() == Permission.PermissionType.operation)
                .map(Permission::getPermissionKey).collect(Collectors.toList());
        PermissionResponse response = new PermissionResponse();
        response.setPagePermissions(pagePermissions);
        response.setOperationPermissions(operationPermissions);
        this.redisTemplate.opsForValue().set(cacheKey, response, 30L, TimeUnit.MINUTES);
        return response;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse.PermissionInfo> getAllPermissions() {
        List<Permission> permissions = this.permissionRepository.findAll();
        return permissions.stream().map(this::convertToInfo).collect(Collectors.toList());
    }

    public void clearUserPermissionCache(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        this.redisTemplate.delete(cacheKey);
    }

    public void clearAllUserPermissionCache() {
        try {
            String pattern = "user_permissions:*";
            Set<String> keys = this.redisTemplate.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                log.debug("\u6ca1\u6709\u9700\u8981\u6e05\u7406\u7684\u7528\u6237\u6743\u9650\u7f13\u5b58");
                return;
            }
            this.redisTemplate.delete(keys);
            log.info("\u5df2\u6e05\u9664\u7528\u6237\u6743\u9650\u7f13\u5b58\uff0c\u5171 {} \u6761", keys.size());
        } catch (Exception e) {
            log.error("\u6e05\u9664\u7528\u6237\u6743\u9650\u7f13\u5b58\u5931\u8d25", e);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getUserPermissions(Long userId) {
        String cacheKey = PERMISSION_CACHE_PREFIX + userId;
        PermissionResponse cached = (PermissionResponse) this.redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            ArrayList<String> allPermissions = new ArrayList<String>();
            allPermissions.addAll(cached.getPagePermissions());
            allPermissions.addAll(cached.getOperationPermissions());
            return allPermissions;
        }
        List<Permission> permissions = this.permissionRepository.findPermissionsByUserId(userId);
        return permissions.stream().map(Permission::getPermissionKey).collect(Collectors.toList());
    }

    private PermissionResponse.PermissionInfo convertToInfo(Permission permission) {
        PermissionResponse.PermissionInfo info = new PermissionResponse.PermissionInfo();
        info.setId(permission.getId());
        info.setPermissionKey(permission.getPermissionKey());
        info.setTitle(permission.getTitle());
        info.setType(permission.getType().name());
        info.setParentKey(permission.getParentKey());
        info.setDescription(permission.getDescription());
        return info;
    }

    public PermissionService(PermissionRepository permissionRepository, UserRoleRepository userRoleRepository,
            RedisTemplate<String, Object> redisTemplate) {
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.redisTemplate = redisTemplate;
    }
}
