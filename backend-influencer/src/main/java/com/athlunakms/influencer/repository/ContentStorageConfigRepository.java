package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.ContentStorageConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentStorageConfigRepository
extends JpaRepository<ContentStorageConfig, Long> {
    public Optional<ContentStorageConfig> findFirstByIsActiveTrue();

    public Optional<ContentStorageConfig> findByStorageType(String var1);
}

