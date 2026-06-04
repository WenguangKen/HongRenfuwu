package com.athlunakms.eccang.sync.controller;

import com.athlunakms.eccang.sync.service.ProvinceSyncService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/eccang-provinces"})
public class EccangProvinceController {
    private final ProvinceSyncService provinceSyncService;

    @PostMapping(value={"/sync"})
    public ResponseEntity<?> syncProvinces(@RequestParam(value="storeId") Long storeId) {
        try {
            ProvinceSyncService.SyncResult result = this.provinceSyncService.syncProvinces(storeId);
            return ResponseEntity.ok(Map.of("message", "Sync completed successfully", "countriesProcessed", result.getTotalCountries(), "provincesProcessed", result.getTotalProvinces()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    public EccangProvinceController(ProvinceSyncService provinceSyncService) {
        this.provinceSyncService = provinceSyncService;
    }
}

