package com.athlunakms.user.service;

import com.athlunakms.auth.service.SessionService;
import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.encryption.AESEncryptionService;
import com.athlunakms.common.security.encryption.BCryptService;
import com.athlunakms.role.entity.Role;
import com.athlunakms.role.entity.UserRole;
import com.athlunakms.role.repository.RoleRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import com.athlunakms.user.dto.UserCreateRequest;
import com.athlunakms.user.dto.UserResponse;
import com.athlunakms.user.dto.UserUpdateRequest;
import com.athlunakms.user.entity.User;
import com.athlunakms.user.repository.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final BCryptService bcryptService;
    private final AESEncryptionService encryptionService;
    private final SessionService sessionService;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String emailHash = this.hashEmail(request.getEmail());
        if (this.userRepository.existsByEmailHash(emailHash)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "\u90ae\u7bb1\u5df2\u88ab\u4f7f\u7528");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmailHash(emailHash);
        user.setEmailEncrypted(this.encryptionService.encrypt(request.getEmail()));
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhoneEncrypted(this.encryptionService.encrypt(request.getPhone()));
        }
        user.setPasswordHash(this.bcryptService.encode(request.getPassword()));
        user.setStatus(User.UserStatus.active);
        user.setAvatarUrl(request.getAvatarUrl());
        user = this.userRepository.save(user);
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            this.assignRoles(user.getId(), request.getRoleIds());
        }
        log.info("\u521b\u5efa\u7528\u6237: userId={}, email={}", (Object)user.getId(), (Object)request.getEmail());
        return this.convertToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "\u7528\u6237\u4e0d\u5b58\u5728"));
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().equals(this.decryptEmail(user.getEmailEncrypted()))) {
            String newEmailHash = this.hashEmail(request.getEmail());
            if (this.userRepository.existsByEmailHash(newEmailHash)) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "\u90ae\u7bb1\u5df2\u88ab\u4f7f\u7528");
            }
            user.setEmailHash(newEmailHash);
            user.setEmailEncrypted(this.encryptionService.encrypt(request.getEmail()));
        }
        if (request.getPhone() != null) {
            user.setPhoneEncrypted(this.encryptionService.encrypt(request.getPhone()));
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getRoleIds() != null) {
            HashSet oldRoleIds;
            List currentRoleIds = this.userRoleRepository.findRoleIdsByUserId(userId);
            HashSet newRoleIds = new HashSet(request.getRoleIds());
            if (!newRoleIds.equals(oldRoleIds = new HashSet<>(currentRoleIds))) {
                this.userRoleRepository.deleteByUserId(userId);
                this.assignRoles(userId, request.getRoleIds());
            }
        }
        user = this.userRepository.save(user);
        log.info("\u66f4\u65b0\u7528\u6237: userId={}", userId);
        return this.convertToResponse(user);
    }

    @Transactional(readOnly=true)
    public UserResponse getUserById(Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "\u7528\u6237\u4e0d\u5b58\u5728"));
        return this.convertToResponse(user);
    }

    @Transactional(readOnly=true)
    public Map<Long, String> getUserNamesByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return this.userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, User::getUsername));
    }

    @Transactional(readOnly=true)
    public Page<UserResponse> getUserList(String username, String phone, String email, User.UserStatus status, String role, Pageable pageable) {
        return this.userRepository.findAll((Specification<User>)(root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            if (username != null && !username.isEmpty()) {
                predicates.add(cb.like((Expression)root.get("username"), "%" + username + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (role != null && !role.isEmpty()) {
                Role roleEntity = this.roleRepository.findByName(role).orElse(null);
                if (roleEntity != null) {
                    List<Long> userIds = this.userRoleRepository.findUserIdsByRoleId(roleEntity.getId());
                    if (userIds.isEmpty()) {
                        predicates.add(cb.disjunction());
                    } else {
                        predicates.add(root.get("id").in(userIds));
                    }
                } else {
                    predicates.add(cb.disjunction());
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(user -> this.convertToResponse(user));
    }

    @Transactional
    public void updateUserStatus(Long userId, User.UserStatus status) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "\u7528\u6237\u4e0d\u5b58\u5728"));
        user.setStatus(status);
        this.userRepository.save(user);
        if (status == User.UserStatus.inactive) {
            this.sessionService.deactivateAllUserSessions(userId);
        }
        log.info("\u66f4\u65b0\u7528\u6237\u72b6\u6001: userId={}, status={}", (Object)userId, (Object)status);
    }

    @Transactional
    public void changePassword(Long userId, String newPassword) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "\u7528\u6237\u4e0d\u5b58\u5728"));
        user.setPasswordHash(this.bcryptService.encode(newPassword));
        this.userRepository.save(user);
        this.sessionService.deactivateAllUserSessions(userId);
        log.info("\u7528\u6237\u5bc6\u7801\u5df2\u4fee\u6539: userId={}", (Object)userId);
    }

    private void assignRoles(Long userId, List<Long> roleIds) {
        List roles = roleIds.stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            return userRole;
        }).collect(Collectors.toList());
        this.userRoleRepository.saveAll(roles);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        String email = this.decryptEmail(user.getEmailEncrypted());
        response.setEmail(email);
        String phone = user.getPhoneEncrypted() != null ? this.decryptPhone(user.getPhoneEncrypted()) : null;
        response.setPhone(phone);
        response.setStatus(user.getStatus());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setLastLoginTime(user.getLastLoginTime());
        response.setLastLoginIp(this.maskIp(user.getLastLoginIp()));
        response.setLastLoginLocation(user.getLastLoginLocation());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        List<Long> roleIds = this.userRoleRepository.findRoleIdsByUserId(user.getId());
        List<UserResponse.RoleInfo> roles = roleIds.stream().map(roleId -> this.roleRepository.findById(roleId)).filter(Optional::isPresent).map(Optional::get).map(role -> {
            UserResponse.RoleInfo roleInfo = new UserResponse.RoleInfo();
            roleInfo.setId(role.getId());
            roleInfo.setName(role.getName());
            roleInfo.setDescription(role.getDescription());
            return roleInfo;
        }).collect(Collectors.toList());
        response.setRoles(roles);
        return response;
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        if (prefix.length() <= 2) {
            return prefix + "***" + suffix;
        }
        return prefix.substring(0, 2) + "***" + suffix;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String maskIp(String ip) {
        if (ip == null || ip.isEmpty() || "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            return ip;
        }
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".***.***";
        }
        return ip;
    }

    private String hashEmail(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "\u8ba1\u7b97\u90ae\u7bb1\u54c8\u5e0c\u5931\u8d25");
        }
    }

    private String decryptEmail(String encryptedEmail) {
        try {
            return this.encryptionService.decrypt(encryptedEmail);
        }
        catch (Exception e) {
            log.error("\u89e3\u5bc6\u90ae\u7bb1\u5931\u8d25", (Throwable)e);
            return "***";
        }
    }

    private String decryptPhone(String encryptedPhone) {
        try {
            return encryptedPhone != null ? this.encryptionService.decrypt(encryptedPhone) : null;
        }
        catch (Exception e) {
            log.error("\u89e3\u5bc6\u7535\u8bdd\u5931\u8d25", (Throwable)e);
            return "***";
        }
    }

    public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository, BCryptService bcryptService, AESEncryptionService encryptionService, SessionService sessionService) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.bcryptService = bcryptService;
        this.encryptionService = encryptionService;
        this.sessionService = sessionService;
    }
}

