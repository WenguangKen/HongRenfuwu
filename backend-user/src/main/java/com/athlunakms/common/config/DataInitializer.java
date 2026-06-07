package com.athlunakms.common.config;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.role.entity.Role;
import com.athlunakms.role.entity.UserRole;
import com.athlunakms.role.repository.RoleRepository;
import com.athlunakms.role.repository.UserRoleRepository;
import com.athlunakms.user.dto.UserCreateRequest;
import com.athlunakms.user.entity.User;
import com.athlunakms.user.repository.UserRepository;
import com.athlunakms.user.service.UserService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value=1)
public class DataInitializer
implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String SUPER_ADMIN_EMAIL = "wenguangfeifan@outlook.com";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserService userService;
    @Value(value="${app.initial-admin.enabled:true}")
    private boolean initialAdminEnabled;
    @Value(value="${app.initial-admin.username:admin}")
    private String adminUsername;
    @Value(value="${app.initial-admin.email:admin@example.com}")
    private String adminEmail;
    @Value(value="${app.initial-admin.password:yijia6688}")
    private String adminPassword;
    @Value(value="${app.initial-admin.role-name:\u8d85\u7ea7\u7ba1\u7406\u5458}")
    private String adminRoleName;

    public void run(String ... args) {
        if (!this.initialAdminEnabled) {
            log.info("\u521d\u59cb\u7ba1\u7406\u5458\u8d26\u53f7\u521b\u5efa\u5df2\u7981\u7528");
            return;
        }
        try {
            Role adminRole = this.roleRepository.findByName(this.adminRoleName).orElse(null);
            if (adminRole == null) {
                log.warn("\u8d85\u7ea7\u7ba1\u7406\u5458\u89d2\u8272\u4e0d\u5b58\u5728\uff0c\u8bf7\u5148\u6267\u884c\u6570\u636e\u5e93\u8fc1\u79fb\u811a\u672c V4__seed_initial_data.sql");
                return;
            }
            this.ensureSuperAdminForEmail(SUPER_ADMIN_EMAIL, adminRole.getId());
            if (this.checkAdminUserExists()) {
                log.info("\u7ba1\u7406\u5458\u8d26\u53f7\u5df2\u5b58\u5728\uff0c\u8df3\u8fc7\u521d\u59cb\u5316");
                return;
            }
            this.createAdminUser(adminRole.getId());
            log.info("\u521d\u59cb\u7ba1\u7406\u5458\u8d26\u53f7\u521b\u5efa\u6210\u529f");
            log.warn("==========================================");
            log.warn("\u521d\u59cb\u7ba1\u7406\u5458\u8d26\u53f7\u4fe1\u606f\uff1a");
            log.warn("\u7528\u6237\u540d\uff1a{}", (Object)this.adminUsername);
            log.warn("\u90ae\u7bb1\uff1a{}", (Object)this.adminEmail);
            log.warn("\u5bc6\u7801\uff1a******\uff08\u5df2\u9690\u85cf\uff0c\u8bf7\u67e5\u770b\u914d\u7f6e\u6587\u4ef6\uff09");
            log.warn("\u89d2\u8272\uff1a{}", (Object)this.adminRoleName);
            log.warn("\u8bf7\u9996\u6b21\u767b\u5f55\u540e\u7acb\u5373\u4fee\u6539\u5bc6\u7801\uff01");
            log.warn("==========================================");
        }
        catch (Exception e) {
            log.error("\u521d\u59cb\u5316\u7ba1\u7406\u5458\u8d26\u53f7\u5931\u8d25: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    private boolean checkAdminUserExists() {
        try {
            if (this.userRepository.findByUsername(this.adminUsername).isPresent()) {
                return true;
            }
            String emailHash = this.hashEmail(this.adminEmail);
            return this.userRepository.existsByEmailHash(emailHash);
        }
        catch (Exception e) {
            log.debug("\u68c0\u67e5\u7ba1\u7406\u5458\u7528\u6237\u65f6\u51fa\u9519: {}", (Object)e.getMessage());
            return false;
        }
    }

    private void createAdminUser(Long adminRoleId) {
        try {
            UserCreateRequest request = new UserCreateRequest();
            request.setUsername(this.adminUsername);
            request.setEmail(this.adminEmail);
            request.setPassword(this.adminPassword);
            request.setRoleIds(Arrays.asList(adminRoleId));
            this.userService.createUser(request);
            log.info("\u7ba1\u7406\u5458\u7528\u6237\u521b\u5efa\u6210\u529f: username={}, email={}", (Object)this.adminUsername, (Object)this.adminEmail);
        }
        catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.EMAIL_ALREADY_EXISTS) {
                log.info("\u7ba1\u7406\u5458\u7528\u6237\u5df2\u5b58\u5728\uff0c\u8df3\u8fc7\u521b\u5efa");
                return;
            }
            log.error("\u521b\u5efa\u7ba1\u7406\u5458\u7528\u6237\u5931\u8d25: {}", (Object)e.getMessage(), (Object)e);
            throw e;
        }
        catch (Exception e) {
            log.error("\u521b\u5efa\u7ba1\u7406\u5458\u7528\u6237\u5931\u8d25: {}", (Object)e.getMessage(), (Object)e);
            throw e;
        }
    }

    private void ensureSuperAdminForEmail(String email, Long adminRoleId) {
        try {
            String emailHash = this.hashEmail(email);
            User user = this.userRepository.findByEmailHash(emailHash).orElse(null);
            if (user == null) {
                log.info("\u672a\u627e\u5230\u90ae\u7bb1 {} \u5bf9\u5e94\u7528\u6237\uff0c\u8df3\u8fc7\u8d85\u7ea7\u7ba1\u7406\u5458\u6388\u6743", (Object)email);
                return;
            }
            if (!this.userRoleRepository.existsByUserIdAndRoleId(user.getId(), adminRoleId)) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(adminRoleId);
                this.userRoleRepository.save(userRole);
                log.info("\u5df2\u4e3a\u7528\u6237 {} ({}) \u6388\u4e88\u8d85\u7ea7\u7ba1\u7406\u5458\u89d2\u8272", (Object)user.getUsername(), (Object)email);
            }
        } catch (Exception e) {
            log.warn("\u8d85\u7ea7\u7ba1\u7406\u5458\u6388\u6743\u68c0\u67e5\u5931\u8d25: {}", (Object)e.getMessage());
        }
    }

    private String hashEmail(String email) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(email.toLowerCase().trim().getBytes(StandardCharsets.UTF_8));
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
            throw new RuntimeException("\u8ba1\u7b97\u90ae\u7bb1\u54c8\u5e0c\u5931\u8d25", e);
        }
    }

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
            UserRoleRepository userRoleRepository, UserService userService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.userService = userService;
    }
}

