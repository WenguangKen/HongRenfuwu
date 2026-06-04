package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.CommunicationLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunicationLogRepository extends JpaRepository<CommunicationLog, Long> {
    List<CommunicationLog> findByInfluencerIdOrderByCreatedAtDesc(Long influencerId);
}
