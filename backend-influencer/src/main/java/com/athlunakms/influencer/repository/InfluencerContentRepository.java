package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerContent;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface InfluencerContentRepository
        extends JpaRepository<InfluencerContent, Long>,
        JpaSpecificationExecutor<InfluencerContent> {
    public Page<InfluencerContent> findByInfluencerId(Long var1, Pageable var2);

    public Page<InfluencerContent> findByStatus(String var1, Pageable var2);

    public Page<InfluencerContent> findByInfluencerIdAndStatus(Long var1, String var2, Pageable var3);

    public List<InfluencerContent> findByInfluencerId(Long var1);

    public long countByStatus(String var1);

    public long countByInfluencerId(Long var1);

    @Query(value = "SELECT c.influencerId, COUNT(c) FROM InfluencerContent c WHERE c.influencerId IN :influencerIds GROUP BY c.influencerId")
    public List<Object[]> countByInfluencerIds(@Param(value = "influencerIds") List<Long> var1);

    public long countByTaskGroupIdStartingWith(String var1);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE InfluencerContent c SET c.orderNo = :orderNo WHERE c.taskGroupId = :taskGroupId")
    public int updateOrderNoByTaskGroupId(@Param(value = "taskGroupId") String var1,
            @Param(value = "orderNo") String var2);

    public List<InfluencerContent> findByTaskGroupId(String var1);

    public List<InfluencerContent> findByTaskGroupIdIn(List<String> taskGroupIds);

    @Query(value = "SELECT SUM(c.fileSize) FROM InfluencerContent c WHERE c.fileSize IS NOT NULL")
    public Long sumTotalFileSize();

    @Query("SELECT c.filePath FROM InfluencerContent c WHERE c.filePath IS NOT NULL")
    public List<String> findAllFilePaths();

    @Query("SELECT c.thumbnailPath FROM InfluencerContent c WHERE c.thumbnailPath IS NOT NULL")
    public List<String> findAllThumbnailPaths();

    @Modifying
    @Query(value = "DELETE FROM InfluencerContent c WHERE c.influencerId = :influencerId")
    public void deleteByInfluencerId(@Param(value = "influencerId") Long var1);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE InfluencerContent c SET c.views = :views, c.likes = :likes, " +
           "c.comments = :comments, c.shares = :shares, c.saves = :saves, " +
           "c.engagementRate = :engagementRate, " +
           "c.socialDataUpdatedAt = :now WHERE c.id = :id")
    int updateSocialMetricsById(@Param("id") Long id,
            @Param("views") Integer views, @Param("likes") Integer likes,
            @Param("comments") Integer comments, @Param("shares") Integer shares,
            @Param("saves") Integer saves, @Param("engagementRate") BigDecimal engagementRate,
            @Param("now") java.time.LocalDateTime now);
}
