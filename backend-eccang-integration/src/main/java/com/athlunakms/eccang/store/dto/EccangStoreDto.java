package com.athlunakms.eccang.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EccangStoreDto {
    private Long id;
    private String storeName;
    private String eccangStoreCode;
    private String platform;
    private String notes;
    private String status;
    private Boolean connectionVerified;
    private LocalDateTime lastVerifiedAt;
    private Long eccangShopId;
    private String currency;
    private String timezone;
    private String countryCode;
    private List<String> platformAccounts;
    private Long activeProductCount = 0L;
    private Long inactiveProductCount = 0L;
    private Long totalProductCount = 0L;
    private Long activeSkuCount = 0L;
    private Long inactiveSkuCount = 0L;
    private Long totalSkuCount = 0L;
    private List<AccountProductCountDto> accountCounts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class AccountProductCountDto {
        private String site;
        private String userAccount;
        private Long activeProductCount = 0L;
        private Long inactiveProductCount = 0L;
        private Long totalProductCount = 0L;
        private Long activeSkuCount = 0L;
        private Long inactiveSkuCount = 0L;
        private Long totalSkuCount = 0L;
    }
}
