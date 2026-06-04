package com.athlunakms.shopify.store.controller;

import com.athlunakms.shopify.common.client.ShopifyApiService;
import com.athlunakms.shopify.common.exception.ShopifyApiException;
import com.athlunakms.shopify.store.dto.ShopInfoDto;
import com.athlunakms.shopify.store.dto.ShopifyVerifyRequest;
import com.athlunakms.shopify.store.dto.ShopifyVerifyResponse;
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
public class ShopifyVerifyController {
    private static final Logger log = LoggerFactory.getLogger(ShopifyVerifyController.class);
    private final ShopifyApiService shopifyApiService;

    @PostMapping(value={"/verify"})
    public ResponseEntity<ShopifyVerifyResponse> verifyConnection(@Valid @RequestBody ShopifyVerifyRequest request) {
        log.info("\u6536\u5230 Shopify \u9a8c\u8bc1\u8bf7\u6c42: {}", (Object)request.getStoreUrl());
        try {
            ShopInfoDto shopInfo = this.shopifyApiService.getShopInfo(request.getStoreUrl(), request.getAccessToken());
            return ResponseEntity.ok(ShopifyVerifyResponse.success(shopInfo));
        }
        catch (ShopifyApiException e) {
            log.warn("Shopify \u9a8c\u8bc1\u5931\u8d25: {} - {}", (Object)e.getErrorCode(), (Object)e.getMessage());
            return ResponseEntity.status(e.getHttpStatus()).body(ShopifyVerifyResponse.error(e.getMessage(), e.getErrorCode()));
        }
        catch (Exception e) {
            log.error("Shopify \u9a8c\u8bc1\u65f6\u53d1\u751f\u672a\u77e5\u9519\u8bef", e);
            return ResponseEntity.internalServerError().body(ShopifyVerifyResponse.error("\u670d\u52a1\u5185\u90e8\u9519\u8bef", "INTERNAL_ERROR"));
        }
    }

    public ShopifyVerifyController(ShopifyApiService shopifyApiService) {
        this.shopifyApiService = shopifyApiService;
    }
}


