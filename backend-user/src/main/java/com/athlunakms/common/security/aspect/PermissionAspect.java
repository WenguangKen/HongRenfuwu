package com.athlunakms.common.security.aspect;

import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.security.annotation.RequirePermission;
import com.athlunakms.common.security.jwt.JwtTokenService;
import com.athlunakms.permission.dto.PermissionResponse;
import com.athlunakms.permission.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PermissionAspect {
    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);
    private static final String TOKEN_PREFIX = "Bearer ";
    private final JwtTokenService jwtTokenService;
    private final PermissionService permissionService;

    @Before(value="@annotation(com.athlunakms.common.security.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        Long userId;
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        if (annotation == null) {
            return;
        }
        Object[] requiredPermissions = annotation.value();
        if (requiredPermissions.length == 0) {
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("\u65e0\u6cd5\u83b7\u53d6\u5f53\u524d\u8bf7\u6c42\u4e0a\u4e0b\u6587");
            throw new BusinessException(ErrorCode.FORBIDDEN, "\u6743\u9650\u9a8c\u8bc1\u5931\u8d25");
        }
        HttpServletRequest request = attributes.getRequest();
        String token = this.extractToken(request);
        if (token == null) {
            log.warn("\u8bf7\u6c42\u672a\u643a\u5e26Token");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "\u672a\u767b\u5f55\u6216Token\u5df2\u8fc7\u671f");
        }
        try {
            userId = this.jwtTokenService.getUserIdFromToken(token);
            if (userId == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token\u65e0\u6548");
            }
        }
        catch (Exception e) {
            log.warn("Token\u89e3\u6790\u5931\u8d25", (Throwable)e);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Token\u65e0\u6548\u6216\u5df2\u8fc7\u671f");
        }
        PermissionResponse permissionResponse = this.permissionService.getCurrentUserPermissions(userId);
        ArrayList userPermissions = new ArrayList();
        userPermissions.addAll(permissionResponse.getPagePermissions());
        userPermissions.addAll(permissionResponse.getOperationPermissions());
        boolean hasPermission = this.checkUserPermission(userPermissions, (String[])requiredPermissions, annotation.logical());
        if (!hasPermission) {
            log.warn("\u7528\u6237 {} \u7f3a\u5c11\u6743\u9650: {}", (Object)userId, (Object)Arrays.toString(requiredPermissions));
            throw new BusinessException(ErrorCode.FORBIDDEN, "\u6743\u9650\u4e0d\u8db3");
        }
        log.debug("\u7528\u6237 {} \u6743\u9650\u9a8c\u8bc1\u901a\u8fc7: {}", (Object)userId, (Object)Arrays.toString(requiredPermissions));
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith(TOKEN_PREFIX)) {
            return authorization.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean checkUserPermission(List<String> userPermissions, String[] requiredPermissions, RequirePermission.LogicalType logical) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }
        if (userPermissions.contains("*")) {
            return true;
        }
        if (logical == RequirePermission.LogicalType.AND) {
            return Arrays.stream(requiredPermissions).allMatch(userPermissions::contains);
        }
        return Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);
    }

    public PermissionAspect(JwtTokenService jwtTokenService, PermissionService permissionService) {
        this.jwtTokenService = jwtTokenService;
        this.permissionService = permissionService;
    }
}

