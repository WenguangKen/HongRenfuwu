package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerPaymentInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerPaymentInfoRepository
extends JpaRepository<InfluencerPaymentInfo, Long> {
    public Optional<InfluencerPaymentInfo> findByInfluencerId(Long var1);
}

