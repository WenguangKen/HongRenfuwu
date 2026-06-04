package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.SocialMedia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialMediaRepository
extends JpaRepository<SocialMedia, Long> {
    public List<SocialMedia> findByInfluencerId(Long var1);

    public List<SocialMedia> findByInfluencerIdIn(List<Long> var1);

    public Optional<SocialMedia> findByPlatformAndHandle(String var1, String var2);

    @Query("SELECT DISTINCT s.influencerId FROM SocialMedia s WHERE LOWER(s.handle) LIKE LOWER(CONCAT('%', :handle, '%'))")
    public List<Long> findInfluencerIdsByHandleContainingIgnoreCase(@Param("handle") String handle);

    @Modifying
    @Query(value="DELETE FROM SocialMedia s WHERE s.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value="influencerId") Long var1);
}

