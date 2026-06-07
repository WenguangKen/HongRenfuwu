package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerConversionImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionImportLogRepository extends JpaRepository<InfluencerConversionImportLog, Long> {
}
