package com.athlunakms.influencer.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
    private final Counter uploadCounter;
    private final Counter downloadCounter;
    private final Counter uploadFailCounter;
    private final Timer downloadTimer;
    private final Timer uploadTimer;

    public BusinessMetrics(MeterRegistry registry) {
        this.uploadCounter = Counter.builder((String)"content.uploads.total").description("Total successful file uploads").tag("type", "success").register(registry);
        this.uploadFailCounter = Counter.builder((String)"content.uploads.total").description("Total failed file uploads").tag("type", "failure").register(registry);
        this.downloadCounter = Counter.builder((String)"content.downloads.total").description("Total file downloads").register(registry);
        this.downloadTimer = Timer.builder((String)"content.downloads.duration").description("File download duration").register(registry);
        this.uploadTimer = Timer.builder((String)"content.uploads.duration").description("File upload duration").register(registry);
    }

    public void recordUploadSuccess() {
        this.uploadCounter.increment();
    }

    public void recordUploadFailure() {
        this.uploadFailCounter.increment();
    }

    public void recordDownload() {
        this.downloadCounter.increment();
    }

    public void recordDownloadDuration(long durationMs) {
        this.downloadTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordUploadDuration(long durationMs) {
        this.uploadTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }
}

