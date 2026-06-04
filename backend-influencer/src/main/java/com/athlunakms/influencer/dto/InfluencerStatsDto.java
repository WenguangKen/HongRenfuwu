package com.athlunakms.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluencerStatsDto {
    private Long totalContentCount;
    private Long totalSampleCount;
    private Long totalOrderCount;
    private BigDecimal totalGmv;
    private BigDecimal estimatedCommission;
}
