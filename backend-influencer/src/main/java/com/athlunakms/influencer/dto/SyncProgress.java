package com.athlunakms.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncProgress {
    private String taskId;
    private String status;
    private Integer progress;
    private Integer current;
    private Integer total;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String error;
}
