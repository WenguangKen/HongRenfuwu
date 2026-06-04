package com.athlunakms.user.controller;

import com.athlunakms.common.annotation.LogOperation;
import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.user.dto.UserCreateRequest;
import com.athlunakms.user.dto.UserPasswordRequest;
import com.athlunakms.user.dto.UserResponse;
import com.athlunakms.user.dto.UserUpdateRequest;
import com.athlunakms.user.entity.User;
import com.athlunakms.user.entity.UserLog;
import com.athlunakms.user.service.UserLogService;
import com.athlunakms.user.service.UserService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/users"})
public class UserController {
    private final UserService userService;
    private final UserLogService userLogService;

    @GetMapping
    public ApiResponse<Page<UserResponse>> getUserList(@RequestParam(value="username", required=false) String username, @RequestParam(value="phone", required=false) String phone, @RequestParam(value="email", required=false) String email, @RequestParam(value="status", required=false) User.UserStatus status, @RequestParam(value="role", required=false) String role, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="size", defaultValue="10") int size, @RequestParam(value="sortBy", defaultValue="id") String sortBy, @RequestParam(value="direction", defaultValue="DESC") Sort.Direction direction) {
        PageRequest pageable = PageRequest.of((int)page, (int)size, (Sort)Sort.by((Sort.Direction)direction, (String[])new String[]{sortBy}));
        Page<UserResponse> users = this.userService.getUserList(username, phone, email, status, role, (Pageable)pageable);
        return ApiResponse.success(users);
    }

    @GetMapping(value={"/{id}"})
    public ApiResponse<UserResponse> getUserById(@PathVariable(value="id") Long id) {
        UserResponse user = this.userService.getUserById(id);
        return ApiResponse.success(user);
    }

    @GetMapping(value={"/names"})
    public Map<Long, String> getUserNames(@RequestParam(value="ids") List<Long> ids) {
        return this.userService.getUserNamesByIds(ids);
    }

    @PostMapping
    @LogOperation(value="\u521b\u5efa\u7528\u6237")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse user = this.userService.createUser(request);
        return ApiResponse.success("\u7528\u6237\u521b\u5efa\u6210\u529f", user);
    }

    @PutMapping(value={"/{id}"})
    @LogOperation(value="\u66f4\u65b0\u7528\u6237")
    public ApiResponse<UserResponse> updateUser(@PathVariable(value="id") Long id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse user = this.userService.updateUser(id, request);
        return ApiResponse.success("\u7528\u6237\u66f4\u65b0\u6210\u529f", user);
    }

    @PutMapping(value={"/{id}/status"})
    @LogOperation(value="\u66f4\u65b0\u7528\u6237\u72b6\u6001")
    public ApiResponse<Void> updateUserStatus(@PathVariable(value="id") Long id, @RequestParam(value="status") User.UserStatus status) {
        this.userService.updateUserStatus(id, status);
        return ApiResponse.success("\u7528\u6237\u72b6\u6001\u66f4\u65b0\u6210\u529f", null);
    }

    @PutMapping(value={"/{id}/password"})
    @LogOperation(value="\u4fee\u6539\u5bc6\u7801")
    public ApiResponse<Void> changePassword(@PathVariable(value="id") Long id, @Valid @RequestBody UserPasswordRequest request) {
        this.userService.changePassword(id, request.getNewPassword());
        return ApiResponse.success("\u5bc6\u7801\u4fee\u6539\u6210\u529f\uff0c\u8bf7\u91cd\u65b0\u767b\u5f55", null);
    }

    @GetMapping(value={"/{id}/logs"})
    public ApiResponse<Page<UserLog>> getUserLogs(@PathVariable(value="id") Long id, @RequestParam(value="actionType", required=false) String actionType, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value="startTime", required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime startTime, @RequestParam(value="endTime", required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime endTime, @RequestParam(value="page", defaultValue="0") int page, @RequestParam(value="size", defaultValue="10") int size) {
        PageRequest pageable = PageRequest.of((int)page, (int)size, (Sort)Sort.by((Sort.Direction)Sort.Direction.DESC, (String[])new String[]{"createdAt"}));
        Page<UserLog> logs = this.userLogService.getUserLogs(id, actionType, keyword, startTime, endTime, (Pageable)pageable);
        return ApiResponse.success(logs);
    }

    public UserController(UserService userService, UserLogService userLogService) {
        this.userService = userService;
        this.userLogService = userLogService;
    }
}

