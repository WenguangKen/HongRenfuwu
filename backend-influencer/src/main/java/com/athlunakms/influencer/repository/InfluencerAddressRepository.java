package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerAddress;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerAddressRepository
extends JpaRepository<InfluencerAddress, Long> {
    public List<InfluencerAddress> findByInfluencerId(Long var1);

    public List<InfluencerAddress> findByInfluencerIdAndIsDefaultTrue(Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerAddress a WHERE a.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);
}

