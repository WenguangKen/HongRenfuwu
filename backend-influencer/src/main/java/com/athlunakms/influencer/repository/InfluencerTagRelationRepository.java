package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerTagRelation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerTagRelationRepository
extends JpaRepository<InfluencerTagRelation, Long> {
    @Query(value="SELECT r.tagId FROM InfluencerTagRelation r WHERE r.influencerId = :influencerId")
    public List<Long> findTagIdsByInfluencerId(@Param(value="influencerId") Long var1);

    @Query(value="SELECT r.influencerId FROM InfluencerTagRelation r WHERE r.tagId = :tagId")
    public List<Long> findInfluencerIdsByTagId(@Param(value="tagId") Long var1);

    public List<InfluencerTagRelation> findByInfluencerId(Long var1);

    @Modifying
    @Query(value="DELETE FROM InfluencerTagRelation r WHERE r.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);

    public boolean existsByInfluencerIdAndTagId(Long var1, Long var2);

    @Query(value="SELECT COUNT(r) FROM InfluencerTagRelation r WHERE r.tagId = :tagId")
    public long countByTagId(@Param(value="tagId") Long var1);
}

