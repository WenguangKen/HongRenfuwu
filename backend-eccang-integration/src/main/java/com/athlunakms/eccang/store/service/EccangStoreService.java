package com.athlunakms.eccang.store.service;

import com.athlunakms.eccang.store.dto.EccangStoreDto;
import com.athlunakms.eccang.store.dto.ShopInfoDto;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EccangStoreService {
    private static final Logger log = LoggerFactory.getLogger(EccangStoreService.class);
    private final EccangStoreRepository repository;

    public List<EccangStoreDto> getAllStores() {
        return this.repository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public EccangStoreDto createStore(EccangStoreDto dto) {
        EccangStore store = new EccangStore();
        store.setStoreName(dto.getStoreName());
        store.setEccangStoreCode(dto.getEccangStoreCode());
        store.setPlatform(dto.getPlatform());
        store.setNotes(dto.getNotes());
        store.setStatus("active");
        
        store = repository.save(store);
        return this.convertToDto(store);
    }

    @Transactional
    public EccangStoreDto updateStore(Long id, EccangStoreDto dto) {
        EccangStore store = repository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
        store.setStoreName(dto.getStoreName());
        store.setEccangStoreCode(dto.getEccangStoreCode());
        store.setPlatform(dto.getPlatform());
        store.setNotes(dto.getNotes());
        
        store = repository.save(store);
        return this.convertToDto(store);
    }

    @Transactional
    public void deleteStore(Long id) {
        this.repository.deleteById(id);
    }

    @Transactional
    public ShopInfoDto verifyAndRefresh(Long id) {
        EccangStore store = repository.findById(id).orElseThrow(() -> new RuntimeException("Store not found"));
        // TODO: Call Eccang API to verify store connection using global config
        
        store.setConnectionVerified(true);
        store.setLastVerifiedAt(LocalDateTime.now());
        store.setStatus("active");
        this.repository.save(store);
        
        ShopInfoDto shopInfo = new ShopInfoDto();
        shopInfo.setId(store.getEccangShopId());
        shopInfo.setDomain(store.getStoreName());
        return shopInfo;
    }

    private EccangStoreDto convertToDto(EccangStore store) {
        EccangStoreDto dto = new EccangStoreDto();
        dto.setId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setEccangStoreCode(store.getEccangStoreCode());
        dto.setPlatform(store.getPlatform());
        dto.setNotes(store.getNotes());
        dto.setStatus(store.getStatus());
        dto.setConnectionVerified(store.getConnectionVerified());
        dto.setLastVerifiedAt(store.getLastVerifiedAt());
        dto.setEccangShopId(store.getEccangShopId());
        dto.setCurrency(store.getCurrency());
        dto.setTimezone(store.getTimezone());
        dto.setCountryCode(store.getCountryCode());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        return dto;
    }
}
