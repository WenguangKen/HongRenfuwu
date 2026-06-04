package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerSettingsRepository
extends JpaRepository<InfluencerSettings, String> {
}

