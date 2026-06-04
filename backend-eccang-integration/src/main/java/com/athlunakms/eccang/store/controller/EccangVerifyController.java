package com.athlunakms.eccang.store.controller;

import com.athlunakms.eccang.common.client.EccangApiService;
import com.athlunakms.eccang.common.exception.EccangApiException;
import com.athlunakms.eccang.store.dto.ShopInfoDto;
import com.athlunakms.eccang.store.dto.EccangVerifyRequest;
import com.athlunakms.eccang.store.dto.EccangVerifyResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1.0"})
public class EccangVerifyController {
    private static final Logger log = LoggerFactory.getLogger(EccangVerifyController.class);
    private final EccangApiService eccangApiService;

    @PostMapping(value={"/verify"})
    public ResponseEntity<EccangVerifyResponse> verifyConnection(@Valid @RequestBody EccangVerifyRequest request) {
        log.info("\u6536\u5230 Eccang \u9a8c\u8bc1\u8bf7\u6c42: {}", (Object)request.getStoreUrl());
        try {
            ShopInfoDto shopInfo = this.eccangApiService.getShopInfo(request.getStoreUrl(), request.getAccessToken());
            return ResponseEntity.ok(EccangVerifyResponse.success(shopInfo));
        }
        catch (EccangApiException e) {
            log.warn("Eccang \u9a8c\u8bc1\u5931\u8d25: {} - {}", (Object)e.getErrorCode(), (Object)e.getMessage());
            return ResponseEntity.status(e.getHttpStatus()).body(EccangVerifyResponse.error(e.getMessage(), e.getErrorCode()));
        }
        catch (Exception e) {
            log.error("Eccang \u9a8c\u8bc1\u65f6\u53d1\u751f\u672a\u77e5\u9519\u8bef", e);
            return ResponseEntity.internalServerError().body(EccangVerifyResponse.error("\u670d\u52a1\u5185\u90e8\u9519\u8bef", "INTERNAL_ERROR"));
        }
    }

    public EccangVerifyController(EccangApiService eccangApiService) {
        this.eccangApiService = eccangApiService;
    }
}


