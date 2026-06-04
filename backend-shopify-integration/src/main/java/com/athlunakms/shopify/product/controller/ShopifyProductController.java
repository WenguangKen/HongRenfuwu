package com.athlunakms.shopify.product.controller;

import com.athlunakms.shopify.order.dto.SyncProgressDto;
import com.athlunakms.shopify.product.dto.ShopifyProductDto;
import com.athlunakms.shopify.product.service.ShopifyProductService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1.0/shopify-products"})
public class ShopifyProductController {
    private final ShopifyProductService service;

    @PostMapping(value={"/sync"})
    public ResponseEntity<Map<String, Object>> syncProducts(@RequestParam(value="storeId") Long storeId) {
        this.service.syncProducts(storeId);
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("success", true);
        response.put("message", "Sync task started successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping(value={"/sync/progress"})
    public ResponseEntity<SyncProgressDto> getSyncProgress(@RequestParam(value="storeId") Long storeId) {
        SyncProgressDto progress = this.service.getSyncProgress(storeId);
        if (progress == null) {
            return ResponseEntity.ok(SyncProgressDto.builder().status("IDLE").build());
        }
        return ResponseEntity.ok(progress);
    }

    @PostMapping(value={"/sync/clear"})
    public ResponseEntity<Map<String, Object>> clearSyncProgress(@RequestParam(value="storeId") Long storeId) {
        this.service.clearSyncProgress(storeId);
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value="storeId", required=false) Long storeId, 
            @RequestParam(value="keyword", required=false) String keyword, 
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="size", required=false) Integer size) {
        // 有分页参数时返回分页结果，否则保持向后兼容返回全部列表
        if (page != null && size != null) {
            return ResponseEntity.ok(this.service.searchProductsPaged(storeId, keyword, status, page, size));
        }
        return ResponseEntity.ok(this.service.searchProducts(storeId, keyword, status));
    }

    @GetMapping(value={"/store/{storeId}"})
    public ResponseEntity<List<ShopifyProductDto>> getProductsByStoreId(@PathVariable(value="storeId") Long storeId) {
        return ResponseEntity.ok(this.service.getProductsByStoreId(storeId));
    }

    @PostMapping(value={"/ids"})
    public ResponseEntity<List<ShopifyProductDto>> getProductsByGids(@RequestBody List<String> gids) {
        return ResponseEntity.ok(this.service.getProductsByGids(gids));
    }

    @GetMapping(value={"/skus"})
    public ResponseEntity<List<String>> getAllSkus() {
        return ResponseEntity.ok(this.service.getAllDistinctSkus());
    }

    public ShopifyProductController(ShopifyProductService service) {
        this.service = service;
    }
}


