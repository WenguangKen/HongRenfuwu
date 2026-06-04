package com.athlunakms.role.service;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.permission.entity.Permission;
import com.athlunakms.permission.entity.RolePermission;
import com.athlunakms.permission.repository.PermissionRepository;
import com.athlunakms.permission.repository.RolePermissionRepository;
import com.athlunakms.permission.service.PermissionService;
import com.athlunakms.role.repository.RoleRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolePermissionService {
    private static final Logger log = LoggerFactory.getLogger(RolePermissionService.class);
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionService permissionService;

    @Transactional
    public void updateRolePermissions(Long roleId, List<String> pagePermissionKeys, List<String> operationPermissionKeys) {
        this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        ArrayList<String> allPermissionKeys = new ArrayList<String>();
        if (pagePermissionKeys != null) {
            allPermissionKeys.addAll(pagePermissionKeys);
        }
        if (operationPermissionKeys != null) {
            allPermissionKeys.addAll(operationPermissionKeys);
        }
        List<Permission> permissions = this.permissionRepository.findAll().stream().filter(p -> allPermissionKeys.contains(p.getPermissionKey())).collect(Collectors.toList());
        Set<String> foundKeys = permissions.stream().map(Permission::getPermissionKey).collect(Collectors.toSet());
        List<String> invalidKeys = allPermissionKeys.stream().filter(key -> !foundKeys.contains(key)).collect(Collectors.toList());
        if (!invalidKeys.isEmpty()) {
            log.warn("\u53d1\u73b0\u65e0\u6548\u7684\u6743\u9650\u952e: {}", invalidKeys);
        }
        List<Long> permissionIds = permissions.stream().map(Permission::getId).collect(Collectors.toList());
        this.rolePermissionRepository.deleteByRoleId(roleId);
        List<RolePermission> newPermissions = permissionIds.stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            return rolePermission;
        }).collect(Collectors.toList());
        this.rolePermissionRepository.saveAll(newPermissions);
        List<Long> userIds = this.userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            userIds.forEach(userId -> this.permissionService.clearUserPermissionCache(userId));
            log.info("\u5df2\u6e05\u9664 {} \u4e2a\u76f8\u5173\u7528\u6237\u7684\u6743\u9650\u7f13\u5b58", userIds.size());
        } else {
            log.debug("\u89d2\u8272 {} \u6ca1\u6709\u5173\u8054\u7528\u6237\uff0c\u65e0\u9700\u6e05\u9664\u7f13\u5b58", roleId);
        }
        log.info("\u66f4\u65b0\u89d2\u8272\u6743\u9650: roleId={}, permissionCount={}", roleId, permissionIds.size());
    }

    public RolePermissionService(RoleRepository roleRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, UserRoleRepository userRoleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.permissionService = permissionService;
    }
}

