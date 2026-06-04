package com.athlunakms.shopify.store.service;

import com.athlunakms.shopify.common.client.ShopifyApiService;
import com.athlunakms.shopify.common.security.EncryptionService;
import com.athlunakms.shopify.store.dto.ShopInfoDto;
import com.athlunakms.shopify.store.dto.ShopifyStoreDto;
import com.athlunakms.shopify.store.entity.ShopifyStore;
import com.athlunakms.shopify.store.repository.ShopifyStoreRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Shopify 店铺管理服务
 * 负责店铺的创建、更新、删除以及连接验证
 */
@Service
@RequiredArgsConstructor
public class ShopifyStoreService {
    private static final Logger log = LoggerFactory.getLogger(ShopifyStoreService.class);
    private final ShopifyStoreRepository repository;
    private final EncryptionService encryptionService;
    private final ShopifyApiService shopifyApiService;

    public List<ShopifyStoreDto> getAllStores() {
        return this.repository.findAll().stream().map(arg_0 -> this.convertToDto(arg_0)).collect(Collectors.toList());
    }

    @Transactional
    public ShopifyStoreDto createStore(ShopifyStoreDto dto) {
        ShopifyStore store = new ShopifyStore();
        store.setStoreName(dto.getStoreName());
        store.setNotes(dto.getNotes());
        store.setStatus("active");
        store.setStoreUrlEncrypted(this.encryptionService.encrypt(dto.getStoreUrl()));
        store.setAccessTokenEncrypted(this.encryptionService.encrypt(dto.getAccessToken()));
        store = (ShopifyStore)this.repository.save(store);
        try {
            this.verifyStoreConnection(store);
        }
        catch (Exception e) {
            log.warn("\u521b\u5efa\u65f6\u81ea\u52a8\u9a8c\u8bc1\u5931\u8d25: {}", (Object)e.getMessage());
            store.setConnectionVerified(Boolean.valueOf(false));
            store.setStatus("error");
            this.repository.save(store);
        }
        return this.convertToDto(store);
    }

    @Transactional
    public ShopifyStoreDto updateStore(Long id, ShopifyStoreDto dto) {
        ShopifyStore store = (ShopifyStore)this.repository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
        store.setStoreName(dto.getStoreName());
        store.setNotes(dto.getNotes());
        if (StringUtils.hasText((String)dto.getStoreUrl())) {
            store.setStoreUrlEncrypted(this.encryptionService.encrypt(dto.getStoreUrl()));
            store.setConnectionVerified(Boolean.valueOf(false));
        }
        if (StringUtils.hasText((String)dto.getAccessToken())) {
            store.setAccessTokenEncrypted(this.encryptionService.encrypt(dto.getAccessToken()));
            store.setConnectionVerified(Boolean.valueOf(false));
        }
        store = (ShopifyStore)this.repository.save(store);
        return this.convertToDto(store);
    }

    @Transactional
    public void deleteStore(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional
    public ShopInfoDto verifyAndRefresh(Long id) {
        ShopifyStore store = (ShopifyStore)this.repository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
        String url = this.encryptionService.decrypt(store.getStoreUrlEncrypted());
        String token = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
        ShopInfoDto shopInfo = this.shopifyApiService.getShopInfo(url, token);
        store.setShopifyShopId(shopInfo.getId());
        store.setShopDomain(shopInfo.getDomain());
        store.setShopEmailEncrypted(this.encryptionService.encrypt(shopInfo.getEmail()));
        store.setPlanName(shopInfo.getPlan());
        store.setCurrency(shopInfo.getCurrency());
        store.setTimezone(shopInfo.getTimezone());
        store.setCountryCode(shopInfo.getCountryCode());
        store.setConnectionVerified(Boolean.valueOf(true));
        store.setLastVerifiedAt(LocalDateTime.now());
        store.setStatus("active");
        this.repository.save(store);
        return shopInfo;
    }

    private void verifyStoreConnection(ShopifyStore store) {
        String url = this.encryptionService.decrypt(store.getStoreUrlEncrypted());
        String token = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
        ShopInfoDto shopInfo = this.shopifyApiService.getShopInfo(url, token);
        store.setShopifyShopId(shopInfo.getId());
        store.setShopDomain(shopInfo.getDomain());
        store.setShopEmailEncrypted(this.encryptionService.encrypt(shopInfo.getEmail()));
        store.setPlanName(shopInfo.getPlan());
        store.setCurrency(shopInfo.getCurrency());
        store.setTimezone(shopInfo.getTimezone());
        store.setCountryCode(shopInfo.getCountryCode());
        store.setConnectionVerified(Boolean.valueOf(true));
        store.setLastVerifiedAt(LocalDateTime.now());
        store.setStatus("active");
        this.repository.save(store);
    }

    private ShopifyStoreDto convertToDto(ShopifyStore store) {
        ShopifyStoreDto dto = new ShopifyStoreDto();
        dto.setId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setNotes(store.getNotes());
        dto.setStatus(store.getStatus());
        dto.setConnectionVerified(store.getConnectionVerified());
        dto.setLastVerifiedAt(store.getLastVerifiedAt());
        try {
            dto.setStoreUrl(this.encryptionService.decrypt(store.getStoreUrlEncrypted()));
        }
        catch (Exception e) {
            dto.setStoreUrl("Error decrypting URL");
        }
        try {
            String token = this.encryptionService.decrypt(store.getAccessTokenEncrypted());
            dto.setMaskedAccessToken(this.maskToken(token));
        }
        catch (Exception e) {
            dto.setMaskedAccessToken("******");
        }
        dto.setShopifyShopId(store.getShopifyShopId());
        dto.setShopDomain(store.getShopDomain());
        try {
            dto.setShopEmail(this.encryptionService.decrypt(store.getShopEmailEncrypted()));
        }
        catch (Exception e) {
            dto.setShopEmail("Error decrypting email");
        }
        dto.setPlanName(store.getPlanName());
        dto.setCurrency(store.getCurrency());
        dto.setTimezone(store.getTimezone());
        dto.setCountryCode(store.getCountryCode());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        return dto;
    }

    private String maskToken(String token) {
        if (!StringUtils.hasText((String)token) || token.length() <= 16) {
            return "******";
        }
        String prefix = token.substring(0, 8);
        String suffix = token.substring(token.length() - 8);
        return prefix + "******" + suffix;
    }

}


