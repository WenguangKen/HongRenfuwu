package com.athlunakms.role.controller;

import com.athlunakms.common.annotation.LogOperation;
import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.role.dto.RoleCreateRequest;
import com.athlunakms.role.dto.RoleResponse;
import com.athlunakms.role.dto.RoleUpdateRequest;
import com.athlunakms.role.service.RolePermissionService;
import com.athlunakms.role.service.RoleService;
import com.athlunakms.user.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/roles")
public class RoleController {
    private final RoleService roleService;
    private final RolePermissionService rolePermissionService;

    @GetMapping
    public ApiResponse<Page<RoleResponse>> getRoleList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<RoleResponse> roles = this.roleService.getRoleList(pageable);
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> getRoleById(@PathVariable("id") Long id) {
        RoleResponse role = this.roleService.getRoleById(id);
        return ApiResponse.success(role);
    }

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse role = this.roleService.createRole(request);
        return ApiResponse.success("角色创建成功", role);
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable("id") Long id, @Valid @RequestBody RoleUpdateRequest request) {
        RoleResponse role = this.roleService.updateRole(id, request);
        return ApiResponse.success("角色更新成功", role);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable("id") Long id) {
        this.roleService.deleteRole(id);
        return ApiResponse.success("角色删除成功", null);
    }

    @PutMapping("/{id}/permissions")
    @LogOperation("更新角色权限")
    public ApiResponse<Void> updateRolePermissions(@PathVariable("id") Long id, @RequestBody RolePermissionRequest request) {
        this.rolePermissionService.updateRolePermissions(id, request.getPagePermissions(), request.getOperationPermissions());
        return ApiResponse.success("角色权限更新成功", null);
    }

    @GetMapping("/{id}/users")
    public ApiResponse<List<UserResponse>> getRoleUsers(@PathVariable("id") Long id) {
        List<UserResponse> users = this.roleService.getRoleUsers(id);
        return ApiResponse.success(users);
    }

    public RoleController(RoleService roleService, RolePermissionService rolePermissionService) {
        this.roleService = roleService;
        this.rolePermissionService = rolePermissionService;
    }

    public static class RolePermissionRequest {
        private List<String> pagePermissions;
        private List<String> operationPermissions;

        public List<String> getPagePermissions() {
            return this.pagePermissions;
        }

        public void setPagePermissions(List<String> pagePermissions) {
            this.pagePermissions = pagePermissions;
        }

        public List<String> getOperationPermissions() {
            return this.operationPermissions;
        }

        public void setOperationPermissions(List<String> operationPermissions) {
            this.operationPermissions = operationPermissions;
        }
    }
}
