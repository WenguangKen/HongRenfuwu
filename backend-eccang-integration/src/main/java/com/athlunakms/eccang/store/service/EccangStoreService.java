package com.athlunakms.eccang.store.service;

import com.athlunakms.eccang.store.dto.EccangStoreDto;
import com.athlunakms.eccang.store.dto.ShopInfoDto;
import com.athlunakms.eccang.store.entity.EccangStore;
import com.athlunakms.eccang.store.repository.EccangStoreRepository;
import com.athlunakms.eccang.product.repository.EccangProductRepository;
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
    private final EccangProductRepository productRepository;

    public List<EccangStoreDto> getAllStores() {
        return this.repository.findAll().stream()
            .map(this::convertToDto)
            .sorted(java.util.Comparator.comparing(EccangStoreDto::getActiveProductCount, java.util.Comparator.reverseOrder())
                .thenComparing(EccangStoreDto::getId, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
            .collect(Collectors.toList());
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
        
        if (store.getId() != null) {
            dto.setPlatformAccounts(this.productRepository.findDistinctSitesAndUserAccountsByStoreId(store.getId()).stream().map(arr -> {
                String site = arr[0] != null ? arr[0].toString() : "";
                String account = arr[1] != null ? arr[1].toString() : "";
                return site.isEmpty() ? account : "[" + site + "] " + account;
            }).sorted().collect(Collectors.toList()));
            
            dto.setActiveProductCount(this.productRepository.countByStoreIdAndStatus(store.getId(), "active"));
            dto.setInactiveProductCount(this.productRepository.countByStoreIdAndStatus(store.getId(), "inactive"));
            dto.setTotalProductCount(this.productRepository.countByStoreId(store.getId()));

            dto.setActiveSkuCount(this.productRepository.countVariantsByStoreIdAndStatus(store.getId(), "active"));
            dto.setInactiveSkuCount(this.productRepository.countVariantsByStoreIdAndStatus(store.getId(), "inactive"));
            dto.setTotalSkuCount(this.productRepository.countVariantsByStoreId(store.getId()));

            java.util.Map<String, EccangStoreDto.AccountProductCountDto> map = new java.util.HashMap<>();
            
            List<Object[]> countResults = this.productRepository.findProductCountsGroupedBySiteAndAccount(store.getId());
            for (Object[] arr : countResults) {
                String site = arr[0] != null ? arr[0].toString() : "";
                String account = arr[1] != null ? arr[1].toString() : "";
                String key = site + "|" + account;
                EccangStoreDto.AccountProductCountDto ac = new EccangStoreDto.AccountProductCountDto();
                ac.setSite(site);
                ac.setUserAccount(account);
                ac.setActiveProductCount(arr[2] != null ? ((Number) arr[2]).longValue() : 0L);
                ac.setInactiveProductCount(arr[3] != null ? ((Number) arr[3]).longValue() : 0L);
                ac.setTotalProductCount(arr[4] != null ? ((Number) arr[4]).longValue() : 0L);
                map.put(key, ac);
            }
            
            List<Object[]> variantResults = this.productRepository.findVariantCountsGroupedBySiteAndAccount(store.getId());
            for (Object[] arr : variantResults) {
                String site = arr[0] != null ? arr[0].toString() : "";
                String account = arr[1] != null ? arr[1].toString() : "";
                String key = site + "|" + account;
                EccangStoreDto.AccountProductCountDto ac = map.computeIfAbsent(key, k -> {
                    EccangStoreDto.AccountProductCountDto newAc = new EccangStoreDto.AccountProductCountDto();
                    newAc.setSite(site);
                    newAc.setUserAccount(account);
                    return newAc;
                });
                ac.setActiveSkuCount(arr[2] != null ? ((Number) arr[2]).longValue() : 0L);
                ac.setInactiveSkuCount(arr[3] != null ? ((Number) arr[3]).longValue() : 0L);
                ac.setTotalSkuCount(arr[4] != null ? ((Number) arr[4]).longValue() : 0L);
            }
            
            dto.setAccountCounts(map.values().stream()
                .sorted(java.util.Comparator.comparing(EccangStoreDto.AccountProductCountDto::getActiveProductCount, java.util.Comparator.reverseOrder())
                    .thenComparing(EccangStoreDto.AccountProductCountDto::getSite)
                    .thenComparing(EccangStoreDto.AccountProductCountDto::getUserAccount))
                .collect(Collectors.toList()));
        }
        
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        return dto;
    }
}
