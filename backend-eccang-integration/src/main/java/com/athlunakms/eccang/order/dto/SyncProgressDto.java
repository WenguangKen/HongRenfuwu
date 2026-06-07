package com.athlunakms.eccang.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncProgressDto {
    private String status;
    private int total;
    private int processed;
    private int success;
    private int error;
    private int added;           // 新增 SKU 数量
    private int updated;         // 更新 SKU 数量
    private int spuAdded;        // 新增 SPU 数量
    private int spuUpdated;      // 更新 SPU 数量
    private String message;
    private String errorDetails; // 错误原因明细
    private Long startTime;
    private Long endTime;

    public int getProgress() {
        if (this.total <= 0) {
            return 0;
        }
        return this.processed * 100 / this.total;
    }
}
