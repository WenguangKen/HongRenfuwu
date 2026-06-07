package com.athlunakms.eccang.product.controller;

import com.athlunakms.eccang.order.dto.SyncProgressDto;
import com.athlunakms.eccang.product.dto.EccangProductDto;
import com.athlunakms.eccang.product.service.EccangProductService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1.0/eccang-products"})
public class EccangProductController {
    private final EccangProductService service;

    @PostMapping(value={"/sync"})
    public ResponseEntity<Map<String, Object>> syncProducts(
            @RequestParam(value="storeId") Long storeId,
            @RequestParam(value="mode", required=false, defaultValue="full") String mode) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        if (this.service.isSyncRunning(storeId)) {
            response.put("success", false);
            response.put("message", "同步任务正在进行中，请勿重复触发");
            return ResponseEntity.ok(response);
        }
        String cooldownMsg = this.service.getSyncCooldownMessage(storeId);
        if (cooldownMsg != null) {
            response.put("success", false);
            response.put("message", cooldownMsg);
            return ResponseEntity.ok(response);
        }
        if ("incremental".equalsIgnoreCase(mode)) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                this.service.syncProductsIncremental(storeId, now.minusDays(1), now);
            });
            response.put("success", true);
            response.put("message", "增量同步任务已启动（拉取过去24小时更新的数据）");
        } else {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                this.service.syncProducts(storeId);
            });
            response.put("success", true);
            response.put("message", "全量同步任务已启动（分页限速模式）");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value={"/sync/fba"})
    public ResponseEntity<Map<String, Object>> syncFbaInventory(
            @RequestParam(value="storeId") Long storeId,
            @RequestParam(value="onlyActive", required=false, defaultValue="false") Boolean onlyActive) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        if (this.service.isSyncRunning(storeId)) {
            response.put("success", false);
            response.put("message", "同步任务正在进行中，请勿重复触发");
            return ResponseEntity.ok(response);
        }
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            this.service.syncFbaInventory(storeId, onlyActive);
        });
        response.put("success", true);
        response.put("message", "FBA库存同步任务已启动");
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

    @GetMapping(value={"/statistics"})
    public ResponseEntity<Map<String, Long>> getProductStatistics(
            @RequestParam(value="storeId", required=false) Long storeId,
            @RequestParam(value="platformAccount", required=false) String platformAccount,
            @RequestParam(value="site", required=false) String site,
            @RequestParam(value="keyword", required=false) String keyword,
            @RequestHeader(value="X-User-Name", required=false) String username) {
        return ResponseEntity.ok(this.service.getStatusStatistics(storeId, platformAccount, site, keyword, username));
    }

    @GetMapping(value={"/platform-accounts"})
    public ResponseEntity<List<String>> getPlatformAccounts(
            @RequestHeader(value="X-User-Name", required=false) String username) {
        return ResponseEntity.ok(this.service.getPlatformAccounts(username));
    }

    @GetMapping(value={"/sites"})
    public ResponseEntity<List<String>> getSites(
            @RequestHeader(value="X-User-Name", required=false) String username) {
        return ResponseEntity.ok(this.service.getSites(username));
    }

    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(value="storeId", required=false) Long storeId, 
            @RequestParam(value="platformAccount", required=false) String platformAccount,
            @RequestParam(value="site", required=false) String site,
            @RequestParam(value="keyword", required=false) String keyword, 
            @RequestParam(value="status", required=false) String status,
            @RequestParam(value="page", required=false) Integer page,
            @RequestParam(value="size", required=false) Integer size,
            @RequestHeader(value="X-User-Name", required=false) String username) {
        // 有分页参数时返回分页结果，否则保持向后兼容返回全部列表
        if (page != null && size != null) {
            return ResponseEntity.ok(this.service.searchProductsPaged(storeId, platformAccount, site, keyword, status, page, size, username));
        }
        return ResponseEntity.ok(this.service.searchProducts(storeId, platformAccount, site, keyword, status, username));
    }

    @GetMapping(value={"/store/{storeId}"})
    public ResponseEntity<List<EccangProductDto>> getProductsByStoreId(@PathVariable(value="storeId") Long storeId) {
        return ResponseEntity.ok(this.service.getProductsByStoreId(storeId));
    }

    @PostMapping(value={"/ids"})
    public ResponseEntity<List<EccangProductDto>> getProductsByGids(@RequestBody List<String> gids) {
        return ResponseEntity.ok(this.service.getProductsByGids(gids));
    }

    @GetMapping(value={"/skus"})
    public ResponseEntity<List<String>> getAllSkus() {
        return ResponseEntity.ok(this.service.getAllDistinctSkus());
    }

    public EccangProductController(EccangProductService service) {
        this.service = service;
    }
}


