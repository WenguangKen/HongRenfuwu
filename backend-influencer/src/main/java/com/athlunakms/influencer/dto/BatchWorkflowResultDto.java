package com.athlunakms.influencer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
public class BatchWorkflowResultDto {
    private List<Long> successIds;
    private List<FailedItem> failedItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FailedItem {
        private Long id;
        private String errorType;
        private String errorMessage;
    }
}
