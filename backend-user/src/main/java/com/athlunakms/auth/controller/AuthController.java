package com.athlunakms.auth.controller;

import com.athlunakms.auth.dto.CaptchaResponse;
import com.athlunakms.auth.dto.LoginRequest;
import com.athlunakms.auth.dto.LoginResponse;
import com.athlunakms.auth.service.CaptchaService;
import com.athlunakms.auth.service.LoginService;
import com.athlunakms.auth.service.SessionService;
import com.athlunakms.common.annotation.LogOperation;
import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.common.security.jwt.JwtTokenService;
import com.athlunakms.user.dto.UserResponse;
import com.athlunakms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/auth"})
@Tag(name="\u8ba4\u8bc1\u7ba1\u7406", description="\u7528\u6237\u767b\u5f55\u3001\u767b\u51fa\u3001\u9a8c\u8bc1\u7801\u7b49\u8ba4\u8bc1\u76f8\u5173\u63a5\u53e3")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final CaptchaService captchaService;
    private final LoginService loginService;
    private final SessionService sessionService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @GetMapping(value={"/captcha"})
    @Operation(summary="\u83b7\u53d6\u9a8c\u8bc1\u7801", description="\u83b7\u53d6\u767b\u5f55\u9a8c\u8bc1\u7801\uff0c\u8fd4\u56de\u9a8c\u8bc1\u7801\u952e\uff08captchaKey\uff09\uff0c\u524d\u7aef\u4f7f\u7528\u6b64\u952e\u63d0\u4ea4\u9a8c\u8bc1\u7801")
    @ApiResponses(value={@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200", description="\u6210\u529f", content={@Content(schema=@Schema(implementation=CaptchaResponse.class))})})
    public ApiResponse<CaptchaResponse> getCaptcha(HttpServletRequest request) {
        CaptchaResponse response = this.captchaService.generateCaptcha(request);
        return ApiResponse.success(response);
    }

    @PostMapping(value={"/login"})
    @LogOperation(value="\u7528\u6237\u767b\u5f55")
    @Operation(summary="\u7528\u6237\u767b\u5f55", description="\u7528\u6237\u767b\u5f55\uff0c\u9a8c\u8bc1\u90ae\u7bb1\u3001\u5bc6\u7801\u548c\u9a8c\u8bc1\u7801\uff0c\u8fd4\u56deJWT Token\u548c\u7528\u6237\u4fe1\u606f")
    @ApiResponses(value={@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200", description="\u767b\u5f55\u6210\u529f", content={@Content(schema=@Schema(implementation=LoginResponse.class))}), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="2001", description="\u767b\u5f55\u5931\u8d25"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="2002", description="\u90ae\u7bb1\u6216\u5bc6\u7801\u9519\u8bef"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="2003", description="\u9a8c\u8bc1\u7801\u9519\u8bef"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="2012", description="\u8bf7\u6c42\u8fc7\u4e8e\u9891\u7e41")})
    public ApiResponse<LoginResponse> login(@Parameter(description="\u767b\u5f55\u8bf7\u6c42\u53c2\u6570", required=true) @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = this.loginService.login(request, httpRequest);
        return ApiResponse.success("\u767b\u5f55\u6210\u529f", response);
    }

    @GetMapping(value={"/me"})
    @Operation(summary="\u83b7\u53d6\u5f53\u524d\u7528\u6237\u4fe1\u606f", description="\u83b7\u53d6\u5f53\u524d\u5df2\u767b\u5f55\u7528\u6237\u7684\u4fe1\u606f")
    @SecurityRequirement(name="Bearer Authentication")
    public ApiResponse<UserResponse> getCurrentUser(Authentication authentication) {
        Long userId;
        if (authentication == null || authentication.getPrincipal() == null) {
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
        UserResponse user = this.userService.getUserById(userId);
        return ApiResponse.success(user);
    }

    @PostMapping(value={"/logout"})
    @LogOperation(value="\u7528\u6237\u767b\u51fa")
    @Operation(summary="\u7528\u6237\u767b\u51fa", description="\u7528\u6237\u767b\u51fa\uff0c\u4f7f\u5f53\u524d\u4f1a\u8bdd\u5931\u6548")
    @SecurityRequirement(name="Bearer Authentication")
    @ApiResponses(value={@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="200", description="\u767b\u51fa\u6210\u529f"), @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode="401", description="\u672a\u6388\u6743/Token\u8fc7\u671f")})
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String token = this.extractToken(request);
        if (token != null) {
            try {
                Long sessionId = this.jwtTokenService.getSessionIdFromToken(token);
                if (sessionId != null) {
                    this.sessionService.deactivateSession(sessionId);
                }
            }
            catch (Exception e) {
                log.warn("\u767b\u51fa\u65f6\u89e3\u6790Token\u5931\u8d25", e);
            }
        }
        return ApiResponse.success("\u767b\u51fa\u6210\u529f", null);
    }

    private String extractToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length());
        }
        return null;
    }

    public AuthController(CaptchaService captchaService, LoginService loginService, SessionService sessionService, JwtTokenService jwtTokenService, UserService userService) {
        this.captchaService = captchaService;
        this.loginService = loginService;
        this.sessionService = sessionService;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }
}

