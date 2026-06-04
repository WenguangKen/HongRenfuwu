package com.athlunakms.eccang.config.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EccangConfigDto {
    private Long id;
    private String appKey;
    private String appSecret; // Only used for requests, hidden in responses
    private String accessToken; // Only used for requests, hidden in responses
    private Boolean isConfigured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
