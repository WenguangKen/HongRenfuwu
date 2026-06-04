package com.athlunakms.eccang.store.controller;

import com.athlunakms.eccang.store.dto.ShopInfoDto;
import com.athlunakms.eccang.store.dto.EccangStoreDto;
import com.athlunakms.eccang.store.dto.EccangVerifyResponse;
import com.athlunakms.eccang.store.service.EccangStoreService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1.0/eccang-stores"})
public class EccangStoreController {
    private static final Logger log = LoggerFactory.getLogger(EccangStoreController.class);
    private final EccangStoreService storeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStores() {
        List<EccangStoreDto> stores = this.storeService.getAllStores();
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("content", stores);
        response.put("totalElements", stores.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EccangStoreDto> createStore(@RequestBody EccangStoreDto dto) {
        return ResponseEntity.ok(this.storeService.createStore(dto));
    }

    @PutMapping(value={"/{id}"})
    public ResponseEntity<EccangStoreDto> updateStore(@PathVariable(value="id") Long id, @RequestBody EccangStoreDto dto) {
        return ResponseEntity.ok(this.storeService.updateStore(id, dto));
    }

    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<Void> deleteStore(@PathVariable(value="id") Long id) {
        this.storeService.deleteStore(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value={"/{id}/verify"})
    public ResponseEntity<EccangVerifyResponse> verifyStore(@PathVariable(value="id") Long id) {
        try {
            ShopInfoDto shopInfo = this.storeService.verifyAndRefresh(id);
            return ResponseEntity.ok(EccangVerifyResponse.success(shopInfo));
        }
        catch (Exception e) {
            return ResponseEntity.ok(EccangVerifyResponse.error(e.getMessage(), "VERIFY_FAILED"));
        }
    }

    public EccangStoreController(EccangStoreService storeService) {
        this.storeService = storeService;
    }
}


