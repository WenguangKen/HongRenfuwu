package com.athlunakms.eccang.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
