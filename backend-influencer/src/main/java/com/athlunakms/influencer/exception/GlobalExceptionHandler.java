package com.athlunakms.influencer.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value={AsyncRequestNotUsableException.class, IOException.class})
    public void handleClientAbortException(Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("你的主机中的软件中终止了一个已建立的连接")) {
            return;
        }
        if (e.getClass().getName().contains("ClientAbortException")) {
            return;
        }
        log.warn("Connection or Async error: {}", e.getMessage());
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Unhandled exception caught", e);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal Server Error");
        body.put("message", e.getMessage());
        body.put("exception", e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
