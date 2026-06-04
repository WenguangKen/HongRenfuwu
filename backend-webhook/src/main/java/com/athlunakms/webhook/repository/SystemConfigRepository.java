package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.SystemConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemConfigRepository
extends JpaRepository<SystemConfig, Long> {
    public Optional<SystemConfig> findByConfigKey(String var1);
}

