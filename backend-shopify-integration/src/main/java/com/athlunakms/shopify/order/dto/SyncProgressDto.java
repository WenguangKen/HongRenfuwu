package com.athlunakms.shopify.order.dto;

import com.athlunakms.shopify.order.dto.SyncProgressDto;

public class SyncProgressDto {
    private String status;
    private int total;
    private int processed;
    private int success;
    private int error;
    private String message;
    private Long startTime;
    private Long endTime;

    public int getProgress() {
        if (this.total <= 0) {
            return 0;
        }
        return this.processed * 100 / this.total;
    }

    public static SyncProgressDtoBuilder builder() {
        return new SyncProgressDtoBuilder();
    }

    public String getStatus() {
        return this.status;
    }

    public int getTotal() {
        return this.total;
    }

    public int getProcessed() {
        return this.processed;
    }

    public int getSuccess() {
        return this.success;
    }

    public int getError() {
        return this.error;
    }

    public String getMessage() {
        return this.message;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SyncProgressDto)) {
            return false;
        }
        SyncProgressDto other = (SyncProgressDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getTotal() != other.getTotal()) {
            return false;
        }
        if (this.getProcessed() != other.getProcessed()) {
            return false;
        }
        if (this.getSuccess() != other.getSuccess()) {
            return false;
        }
        if (this.getError() != other.getError()) {
            return false;
        }
        Long this$startTime = this.getStartTime();
        Long other$startTime = other.getStartTime();
        if (this$startTime == null ? other$startTime != null : !((Object)this$startTime).equals(other$startTime)) {
            return false;
        }
        Long this$endTime = this.getEndTime();
        Long other$endTime = other.getEndTime();
        if (this$endTime == null ? other$endTime != null : !((Object)this$endTime).equals(other$endTime)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SyncProgressDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getTotal();
        result = result * 59 + this.getProcessed();
        result = result * 59 + this.getSuccess();
        result = result * 59 + this.getError();
        Long $startTime = this.getStartTime();
        result = result * 59 + ($startTime == null ? 43 : ((Object)$startTime).hashCode());
        Long $endTime = this.getEndTime();
        result = result * 59 + ($endTime == null ? 43 : ((Object)$endTime).hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "SyncProgressDto(status=" + this.getStatus() + ", total=" + this.getTotal() + ", processed=" + this.getProcessed() + ", success=" + this.getSuccess() + ", error=" + this.getError() + ", message=" + this.getMessage() + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() + ")";
    }

    public SyncProgressDto() {
    }

    public SyncProgressDto(String status, int total, int processed, int success, int error, String message, Long startTime, Long endTime) {
        this.status = status;
        this.total = total;
        this.processed = processed;
        this.success = success;
        this.error = error;
        this.message = message;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static class SyncProgressDtoBuilder {
        private String status;
        private int total;
        private int processed;
        private int success;
        private int error;
        private String message;
        private Long startTime;
        private Long endTime;

        SyncProgressDtoBuilder() {
        }

        public SyncProgressDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public SyncProgressDtoBuilder total(int total) {
            this.total = total;
            return this;
        }

        public SyncProgressDtoBuilder processed(int processed) {
            this.processed = processed;
            return this;
        }

        public SyncProgressDtoBuilder success(int success) {
            this.success = success;
            return this;
        }

        public SyncProgressDtoBuilder error(int error) {
            this.error = error;
            return this;
        }

        public SyncProgressDtoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SyncProgressDtoBuilder startTime(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public SyncProgressDtoBuilder endTime(Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public SyncProgressDto build() {
            return new SyncProgressDto(status, total, processed, success, error, message, startTime, endTime);
        }

        public String toString() {
            return "SyncProgressDto.SyncProgressDtoBuilder(status=" + this.status + ", total=" + this.total + ", processed=" + this.processed + ", success=" + this.success + ", error=" + this.error + ", message=" + this.message + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ")";
        }
    }
}

