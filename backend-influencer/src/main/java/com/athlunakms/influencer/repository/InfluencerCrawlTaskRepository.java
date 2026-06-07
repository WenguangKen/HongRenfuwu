package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerCrawlTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerCrawlTaskRepository extends JpaRepository<InfluencerCrawlTask, Long> {
}
