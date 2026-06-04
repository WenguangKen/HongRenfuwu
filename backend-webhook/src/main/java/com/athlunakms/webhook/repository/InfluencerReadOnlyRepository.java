package com.athlunakms.webhook.repository;

import com.athlunakms.webhook.entity.InfluencerReadOnly;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerReadOnlyRepository
extends JpaRepository<InfluencerReadOnly, Long> {
    public Optional<InfluencerReadOnly> findByRealNameIgnoreCaseAndInfluencerType(String var1, String var2);

    public Optional<InfluencerReadOnly> findFirstByRealNameContainingIgnoreCaseAndInfluencerType(String var1, String var2);

    public Optional<InfluencerReadOnly> findByNickNameIgnoreCaseAndInfluencerType(String var1, String var2);
}

