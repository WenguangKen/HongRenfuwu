package com.athlunakms.common.aspect;

import com.athlunakms.common.annotation.LogOperation;
import com.athlunakms.common.util.IPUtils;
import com.athlunakms.permission.entity.Permission;
import com.athlunakms.permission.repository.PermissionRepository;
import com.athlunakms.permission.repository.RolePermissionRepository;
import com.athlunakms.user.entity.UserLog;
import com.athlunakms.user.repository.UserLogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private final UserLogRepository userLogRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Set<String> SENSITIVE_FIELDS = Set.of("password", "oldPassword", "newPassword", "confirmPassword", "token", "refreshToken");

    @Around(value="@annotation(com.athlunakms.common.annotation.LogOperation)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request;
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = request = attributes != null ? attributes.getRequest() : null;
        if (request == null) {
            return joinPoint.proceed();
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            userId = (Long)authentication.getPrincipal();
        }
        String method = request.getMethod();
        String path = request.getRequestURI();
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        LogOperation logAnnotation = signature.getMethod().getAnnotation(LogOperation.class);
        String actionType = logAnnotation.value();
        if (actionType == null || actionType.isEmpty()) {
            actionType = this.getActionType(method, path);
        }
        Map params = this.getRequestParams(joinPoint);
        String actionDesc = logAnnotation.description();
        if (actionDesc == null || actionDesc.isEmpty()) {
            actionDesc = this.buildActionDesc(actionType, params);
        }
        if (actionDesc == null || actionDesc.isEmpty()) {
            actionDesc = this.getActionDesc(method, path);
        }
        Map maskedParams = this.maskParams(params);
        HashMap details = new HashMap();
        if ("\u66f4\u65b0\u89d2\u8272\u6743\u9650".equals(actionType)) {
            Object roleId = params.get("id");
            if (roleId != null) {
                details.put("roleId", roleId);
            }
        } else {
            details.put("params", maskedParams);
        }
        String ipAddress = IPUtils.getClientIP(request);
        String location = IPUtils.getLocation(ipAddress);
        String userAgent = request.getHeader("User-Agent");
        try {
            Object result = joinPoint.proceed();
            if (userId != null) {
                this.saveLog(userId, actionType, actionDesc, ipAddress, location, userAgent, details);
            }
            return result;
        }
        catch (Exception e) {
            details.put("error", e.getMessage());
            if (userId != null) {
                this.saveLog(userId, actionType, actionDesc + " (\u5931\u8d25)", ipAddress, location, userAgent, details);
            }
            throw e;
        }
    }

    private String buildActionDesc(String actionType, Map<String, Object> params) {
        String name;
        String username;
        if ("\u4fee\u6539\u5bc6\u7801".equals(actionType)) {
            String newPassword = this.findValue(params, "newPassword", "password");
            if (StringUtils.hasText(newPassword)) {
                return "\u4fee\u6539\u5bc6\u7801\u4e3a: " + this.maskString(newPassword);
            }
            return "\u6267\u884c\u4e86\u4fee\u6539\u5bc6\u7801\u64cd\u4f5c";
        }
        if (("\u521b\u5efa\u7528\u6237".equals(actionType) || "\u66f4\u65b0\u7528\u6237".equals(actionType)) && StringUtils.hasText(username = this.findValue(params, "username"))) {
            return (actionType.startsWith("\u521b\u5efa") ? "\u65b0\u5efa" : "\u7f16\u8f91") + "\u7528\u6237: " + username;
        }
        if (("\u521b\u5efa\u89d2\u8272".equals(actionType) || "\u66f4\u65b0\u89d2\u8272".equals(actionType)) && StringUtils.hasText(name = this.findValue(params, "name"))) {
            return (actionType.startsWith("\u521b\u5efa") ? "\u65b0\u5efa" : "\u7f16\u8f91") + "\u89d2\u8272: " + name;
        }
        if ("\u66f4\u65b0\u89d2\u8272\u6743\u9650".equals(actionType)) {
            return this.buildPermissionChangeDesc(params);
        }
        return actionType + "\u64cd\u4f5c";
    }

    private String buildPermissionChangeDesc(Map<String, Object> params) {
        try {
            Long roleId = null;
            Object idObj = params.get("id");
            if (idObj instanceof Long) {
                roleId = (Long)idObj;
            } else if (idObj instanceof String) {
                roleId = Long.parseLong((String)idObj);
            }
            if (roleId == null) {
                return "\u66f4\u65b0\u89d2\u8272\u6743\u9650\u64cd\u4f5c";
            }
            List<Long> oldPermissionIdList = this.rolePermissionRepository.findPermissionIdsByRoleId(roleId);
            HashSet<Long> oldPermissionIds = new HashSet<>(oldPermissionIdList);
            List<String> newPageKeys = this.extractPermissionKeys(params, "pagePermissions");
            List<String> newOperationKeys = this.extractPermissionKeys(params, "operationPermissions");
            ArrayList<String> allNewKeys = new ArrayList<>();
            allNewKeys.addAll(newPageKeys);
            allNewKeys.addAll(newOperationKeys);
            List<Permission> allPermissions = this.permissionRepository.findAll();
            Map<String, Permission> keyToPermission = allPermissions.stream().collect(Collectors.toMap(Permission::getPermissionKey, p -> p, (p1, p2) -> p1));
            Set<Long> newPermissionIds = allNewKeys.stream().map(keyToPermission::get).filter(Objects::nonNull).map(Permission::getId).collect(Collectors.toSet());
            HashSet<Long> addedIds = new HashSet<>(newPermissionIds);
            addedIds.removeAll(oldPermissionIds);
            HashSet<Long> removedIds = new HashSet<>(oldPermissionIds);
            removedIds.removeAll(newPermissionIds);
            StringBuilder desc = new StringBuilder();
            if (!removedIds.isEmpty()) {
                List<String> removedNames = removedIds.stream().map(id -> allPermissions.stream().filter(p -> p.getId().equals(id)).findFirst().map(Permission::getTitle).orElse("\u672a\u77e5\u6743\u9650")).collect(Collectors.toList());
                desc.append("\u53d6\u6d88\u4e86 [ ").append(String.join("  ", removedNames)).append(" ]");
            }
            if (!addedIds.isEmpty()) {
                if (desc.length() > 0) {
                    desc.append("\n");
                }
                List<String> addedNames = addedIds.stream().map(id -> allPermissions.stream().filter(p -> p.getId().equals(id)).findFirst().map(Permission::getTitle).orElse("\u672a\u77e5\u6743\u9650")).collect(Collectors.toList());
                desc.append("\u65b0\u589e\u4e86 [ ").append(String.join("  ", addedNames)).append(" ]");
            }
            if (desc.length() == 0) {
                return "\u66f4\u65b0\u89d2\u8272\u6743\u9650\u64cd\u4f5c\uff08\u65e0\u53d8\u66f4\uff09";
            }
            return desc.toString();
        }
        catch (Exception e) {
            log.error("\u6784\u5efa\u6743\u9650\u53d8\u66f4\u63cf\u8ff0\u5931\u8d25", (Throwable)e);
            return "\u66f4\u65b0\u89d2\u8272\u6743\u9650\u64cd\u4f5c";
        }
    }

    private List<String> extractPermissionKeys(Map<String, Object> params, String keyName) {
        try {
            Object requestObj;
            Object value = params.get(keyName);
            if (value == null && (requestObj = params.get("request")) != null) {
                Map requestMap = (Map)this.objectMapper.convertValue(requestObj, new TypeReference<Map<String, Object>>(){});
                value = requestMap.get(keyName);
            }
            if (value instanceof List) {
                return ((List<?>) value).stream().filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
            }
        }
        catch (Exception e) {
            log.debug("\u63d0\u53d6\u6743\u9650\u952e\u5931\u8d25: {}", (Object)keyName, (Object)e);
        }
        return Collections.emptyList();
    }

    private String findValue(Map<String, Object> params, String ... keys) {
        for (String key : keys) {
            Object val = params.get(key);
            if (val != null) {
                return val.toString();
            }
            for (Object arg : params.values()) {
                if (arg == null || arg instanceof String || arg instanceof Number || arg instanceof Boolean || arg instanceof Iterable) continue;
                try {
                    Map nestedMap = (Map)this.objectMapper.convertValue(arg, new TypeReference<Map<String, Object>>(){});
                    Object nestedVal = nestedMap.get(key);
                    if (nestedVal == null) continue;
                    return nestedVal.toString();
                }
                catch (Exception exception) {
                }
            }
        }
        return null;
    }

    private String maskString(String str) {
        if (!StringUtils.hasText(str)) {
            return "***";
        }
        if (str.length() <= 6) {
            return str.charAt(0) + "***" + str.charAt(str.length() - 1);
        }
        return str.substring(0, 3) + "***" + str.substring(str.length() - 3);
    }

    private Map<String, Object> maskParams(Map<String, Object> params) {
        if (params == null) {
            return Collections.emptyMap();
        }
        HashMap<String, Object> masked = new HashMap<String, Object>(params);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (this.isSensitiveField(key)) {
                if (val instanceof String) {
                    masked.put(key, this.maskString((String)val));
                    continue;
                }
                masked.put(key, "******");
                continue;
            }
            if (!(val instanceof Map)) continue;
            Map nestedMap = (Map)this.objectMapper.convertValue(val, new TypeReference<Map<String, Object>>(){});
            masked.put(key, this.maskParams(nestedMap));
        }
        return masked;
    }

    private boolean isSensitiveField(String fieldName) {
        if (fieldName == null) {
            return false;
        }
        String lower = fieldName.toLowerCase();
        for (String sensitive : SENSITIVE_FIELDS) {
            if (!lower.contains(sensitive)) continue;
            return true;
        }
        return false;
    }

    private void saveLog(Long userId, String actionType, String actionDesc, String ipAddress, String location, String userAgent, Map<String, Object> details) {
        try {
            UserLog userLog = new UserLog();
            userLog.setUserId(userId);
            userLog.setActionType(actionType);
            userLog.setActionDesc(actionDesc);
            userLog.setIpAddress(ipAddress);
            userLog.setLocation(location);
            userLog.setUserAgent(userAgent);
            userLog.setDetails(this.objectMapper.writeValueAsString(details));
            userLog.setCreatedAt(LocalDateTime.now());
            this.userLogRepository.save(userLog);
        }
        catch (Exception e) {
            log.error("\u4fdd\u5b58\u64cd\u4f5c\u65e5\u5fd7\u5931\u8d25", (Throwable)e);
        }
    }

    private String getActionDesc(String method, String path) {
        String type = this.getActionType(method, path);
        return "\u6267\u884c\u4e86" + type + "\u64cd\u4f5c";
    }

    private String getActionType(String method, String path) {
        if (path.contains("/auth/login")) {
            return "\u767b\u5f55";
        }
        if (path.contains("/auth/logout")) {
            return "\u767b\u51fa";
        }
        if (path.contains("/users")) {
            if (path.contains("/password")) {
                return "\u4fee\u6539\u5bc6\u7801";
            }
            if (path.contains("/status")) {
                return "\u66f4\u65b0\u7528\u6237\u72b6\u6001";
            }
            if (method.equals("POST")) {
                return "\u521b\u5efa\u7528\u6237";
            }
            if (method.equals("PUT")) {
                return "\u66f4\u65b0\u7528\u6237";
            }
            if (method.equals("DELETE")) {
                return "\u5220\u9664\u7528\u6237";
            }
        }
        if (path.contains("/roles")) {
            if (path.contains("/permissions")) {
                return "\u66f4\u65b0\u89d2\u8272\u6743\u9650";
            }
            if (method.equals("POST")) {
                return "\u521b\u5efa\u89d2\u8272";
            }
            if (method.equals("PUT")) {
                return "\u66f4\u65b0\u89d2\u8272";
            }
            if (method.equals("DELETE")) {
                return "\u5220\u9664\u89d2\u8272";
            }
        }
        return "\u901a\u7528\u64cd\u4f5c";
    }

    private Map<String, Object> getRequestParams(ProceedingJoinPoint joinPoint) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = this.getParameterNames(joinPoint);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null || args[i] instanceof HttpServletRequest) continue;
            String name = paramNames != null && i < paramNames.length ? paramNames[i] : "arg" + i;
            params.put(name, args[i]);
        }
        return params;
    }

    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        return signature.getParameterNames();
    }

    public LoggingAspect(UserLogRepository userLogRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository) {
        this.userLogRepository = userLogRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }
}

