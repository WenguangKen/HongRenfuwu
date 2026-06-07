package com.athlunakms.eccang.order.repository;

import com.athlunakms.eccang.order.entity.InfluencerConversionImportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerConversionImportLogRepository extends JpaRepository<InfluencerConversionImportLog, Long> {
}
