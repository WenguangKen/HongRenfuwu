package com.athlunakms.permission.controller;

import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.permission.dto.PermissionResponse;
import com.athlunakms.permission.service.PermissionService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/permissions"})
public class PermissionController {
    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);
    private final PermissionService permissionService;

    @GetMapping(value={"/me"})
    public ApiResponse<PermissionResponse> getCurrentUserPermissions(Authentication authentication) {
        Long userId;
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("Unauthorized access to /permissions/me - authentication is null");
            return ApiResponse.error(401, "\u672a\u767b\u5f55");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            userId = (Long)principal;
        } else if (principal instanceof Integer) {
            userId = ((Integer)principal).longValue();
        } else if (principal instanceof String) {
            try {
                userId = Long.parseLong((String)principal);
            }
            catch (NumberFormatException e) {
                log.error("Principal is a String but not a Long: {}", principal);
                return ApiResponse.error(500, "\u8ba4\u8bc1\u7cfb\u7edf\u5f02\u5e38\uff1a\u7528\u6237ID\u7c7b\u578b\u9519\u8bef");
            }
        } else {
            log.error("Unexpected principal type: {}", principal.getClass().getName());
            return ApiResponse.error(500, "\u8ba4\u8bc1\u7cfb\u7edf\u5f02\u5e38\uff1a\u672a\u83b7\u53d6\u5230\u6709\u6548\u7684\u7528\u6237ID");
        }
        log.debug("Requesting permissions for userId: {}", userId);
        PermissionResponse permissions = this.permissionService.getCurrentUserPermissions(userId);
        log.debug("Found {} page permissions for userId: {}", permissions.getPagePermissions() != null ? permissions.getPagePermissions().size() : "null", userId);
        return ApiResponse.success(permissions);
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse.PermissionInfo>> getAllPermissions() {
        List<PermissionResponse.PermissionInfo> permissions = this.permissionService.getAllPermissions();
        return ApiResponse.success(permissions);
    }

    @DeleteMapping(value={"/cache"})
    public ApiResponse<Void> clearAllPermissionCache() {
        this.permissionService.clearAllUserPermissionCache();
        return ApiResponse.success("\u6240\u6709\u7528\u6237\u6743\u9650\u7f13\u5b58\u5df2\u6e05\u9664", null);
    }

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}

