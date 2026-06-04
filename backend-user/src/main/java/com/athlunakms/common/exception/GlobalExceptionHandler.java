package com.athlunakms.common.exception;

import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    private boolean isProduction() {
        for (String profile : this.environment.getActiveProfiles()) {
            if (!"prod".equalsIgnoreCase(profile)) continue;
            return true;
        }
        return false;
    }

    @ExceptionHandler(value={BusinessException.class})
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("\u4e1a\u52a1\u5f02\u5e38: {}", e.getMessage());
        return ResponseEntity.ok(ApiResponse.error(e.getErrorCode().getCode(), e.getMessage()));
    }

    @ExceptionHandler(value={MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
        HashMap errors = new HashMap();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("\u53c2\u6570\u9a8c\u8bc1\u5931\u8d25: {}", errors);
        return ResponseEntity.ok(ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), "\u53c2\u6570\u9a8c\u8bc1\u5931\u8d25: " + errors));
    }

    @ExceptionHandler(value={AccessDeniedException.class})
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("\u6743\u9650\u4e0d\u8db3: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(ErrorCode.FORBIDDEN.getCode(), "\u6743\u9650\u4e0d\u8db3"));
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        if (e instanceof AccessDeniedException) {
            return this.handleAccessDeniedException((AccessDeniedException)e);
        }
        log.error("\u7cfb\u7edf\u5f02\u5e38", e);
        String errorMessage = this.isProduction() ? "\u7cfb\u7edf\u5f02\u5e38\uff0c\u8bf7\u8054\u7cfb\u7ba1\u7406\u5458" : "\u7cfb\u7edf\u5f02\u5e38: " + e.getMessage() + (e.getCause() != null ? " (\u539f\u56e0: " + e.getCause().getMessage() + ")" : "");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(), errorMessage));
    }
}

