package com.athlunakms.role.service;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.permission.entity.RolePermission;
import com.athlunakms.permission.repository.PermissionRepository;
import com.athlunakms.permission.repository.RolePermissionRepository;
import com.athlunakms.role.dto.RoleCreateRequest;
import com.athlunakms.role.dto.RoleResponse;
import com.athlunakms.role.dto.RoleUpdateRequest;
import com.athlunakms.role.entity.Role;
import com.athlunakms.role.repository.RoleRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import com.athlunakms.user.dto.UserResponse;
import com.athlunakms.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final AESEncryptionService encryptionService;

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        if (this.roleRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS, "\u89d2\u8272\u540d\u79f0\u5df2\u5b58\u5728");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setStatus(Role.RoleStatus.enabled);
        role = this.roleRepository.save(role);
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            this.assignPermissions(role.getId(), request.getPermissionIds());
        }
        log.info("\u521b\u5efa\u89d2\u8272: roleId={}, name={}", (Object)role.getId(), (Object)request.getName());
        return this.convertToResponse(role);
    }

    @Transactional
    public RoleResponse updateRole(Long roleId, RoleUpdateRequest request) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        Integer currentVersion = role.getVersion();
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            if (this.roleRepository.existsByName(request.getName())) {
                throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS, "\u89d2\u8272\u540d\u79f0\u5df2\u5b58\u5728");
            }
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(Role.RoleStatus.valueOf(request.getStatus()));
        }
        if (request.getPermissionIds() != null) {
            this.rolePermissionRepository.deleteByRoleId(roleId);
            this.assignPermissions(roleId, request.getPermissionIds());
        }
        role = this.roleRepository.save(role);
        log.info("\u66f4\u65b0\u89d2\u8272: roleId={}", roleId);
        return this.convertToResponse(role);
    }

    @Transactional(readOnly=true)
    public RoleResponse getRoleById(Long roleId) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        return this.convertToResponse(role);
    }

    @Transactional(readOnly=true)
    public Page<RoleResponse> getRoleList(Pageable pageable) {
        return this.roleRepository.findAll(pageable).map(arg_0 -> this.convertToResponse(arg_0));
    }

    @Transactional(readOnly=true)
    public List<UserResponse> getRoleUsers(Long roleId) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        List<Long> userIds = this.userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds.isEmpty()) {
            return List.of();
        }
        return userIds.stream().map(userId -> this.userRepository.findById(userId)).filter(Optional::isPresent).map(Optional::get).map(user -> {
            UserResponse response = new UserResponse();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setEmail(this.decryptEmail(user.getEmailEncrypted()));
            response.setStatus(user.getStatus());
            return response;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        this.rolePermissionRepository.deleteByRoleId(roleId);
        this.roleRepository.delete(role);
        log.info("\u5220\u9664\u89d2\u8272: roleId={}", roleId);
    }

    @Transactional
    public void updateRoleStatus(Long roleId, Role.RoleStatus status) {
        Role role = this.roleRepository.findById(roleId).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND, "\u89d2\u8272\u4e0d\u5b58\u5728"));
        role.setStatus(status);
        this.roleRepository.save(role);
        log.info("\u66f4\u65b0\u89d2\u8272\u72b6\u6001: roleId={}, status={}", roleId, status);
    }

    private void assignPermissions(Long roleId, List<Long> permissionIds) {
        List<RolePermission> permissions = permissionIds.stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            return rolePermission;
        }).collect(Collectors.toList());
        this.rolePermissionRepository.saveAll(permissions);
    }

    private String decryptEmail(String encryptedEmail) {
        try {
            return this.encryptionService.decrypt(encryptedEmail);
        }
        catch (Exception e) {
            log.warn("Failed to decrypt email, returning original: {}", (Object)e.getMessage());
            return encryptedEmail;
        }
    }

    private RoleResponse convertToResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setStatus(role.getStatus());
        response.setCreatedAt(role.getCreatedAt());
        response.setUpdatedAt(role.getUpdatedAt());
        List<Long> permissionIds = this.rolePermissionRepository.findPermissionIdsByRoleId(role.getId());
        List<RoleResponse.PermissionInfo> permissions = permissionIds.stream().map(permissionId -> this.permissionRepository.findById(permissionId)).filter(Optional::isPresent).map(Optional::get).map(permission -> {
            RoleResponse.PermissionInfo info = new RoleResponse.PermissionInfo();
            info.setId(permission.getId());
            info.setPermissionKey(permission.getPermissionKey());
            info.setTitle(permission.getTitle());
            info.setType(permission.getType().name());
            return info;
        }).collect(Collectors.toList());
        response.setPermissions(permissions);
        long userCount = this.userRoleRepository.findUserIdsByRoleId(role.getId()).size();
        response.setUserCount(Long.valueOf(userCount));
        return response;
    }

    public RoleService(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, UserRoleRepository userRoleRepository, UserRepository userRepository, AESEncryptionService encryptionService) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }
}

