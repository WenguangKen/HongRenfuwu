package com.athlunakms.shopify.store.controller;

import com.athlunakms.shopify.store.dto.ShopInfoDto;
import com.athlunakms.shopify.store.dto.ShopifyStoreDto;
import com.athlunakms.shopify.store.dto.ShopifyVerifyResponse;
import com.athlunakms.shopify.store.service.ShopifyStoreService;
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
@RequestMapping(value={"/v1.0/shopify-stores"})
public class ShopifyStoreController {
    private static final Logger log = LoggerFactory.getLogger(ShopifyStoreController.class);
    private final ShopifyStoreService storeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStores() {
        List<ShopifyStoreDto> stores = this.storeService.getAllStores();
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("content", stores);
        response.put("totalElements", stores.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ShopifyStoreDto> createStore(@RequestBody ShopifyStoreDto dto) {
        return ResponseEntity.ok(this.storeService.createStore(dto));
    }

    @PutMapping(value={"/{id}"})
    public ResponseEntity<ShopifyStoreDto> updateStore(@PathVariable(value="id") Long id, @RequestBody ShopifyStoreDto dto) {
        return ResponseEntity.ok(this.storeService.updateStore(id, dto));
    }

    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<Void> deleteStore(@PathVariable(value="id") Long id) {
        this.storeService.deleteStore(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value={"/{id}/verify"})
    public ResponseEntity<ShopifyVerifyResponse> verifyStore(@PathVariable(value="id") Long id) {
        try {
            ShopInfoDto shopInfo = this.storeService.verifyAndRefresh(id);
            return ResponseEntity.ok(ShopifyVerifyResponse.success(shopInfo));
        }
        catch (Exception e) {
            return ResponseEntity.ok(ShopifyVerifyResponse.error(e.getMessage(), "VERIFY_FAILED"));
        }
    }

    public ShopifyStoreController(ShopifyStoreService storeService) {
        this.storeService = storeService;
    }
}


