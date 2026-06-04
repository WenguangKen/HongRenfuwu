package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.RemittanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RemittanceTaskRepository extends JpaRepository<RemittanceTask, Long>, JpaSpecificationExecutor<RemittanceTask> {
    
    Optional<RemittanceTask> findByTaskNo(String taskNo);
    
    @Query("SELECT MAX(t.taskNo) FROM RemittanceTask t WHERE t.taskNo LIKE ?1")
    String findMaxTaskNoByPrefix(String prefix);
    
    List<RemittanceTask> findByInfluencerIdOrderByCreatedAtDesc(Long influencerId);

    boolean existsByInfluencerIdAndStatusNot(Long influencerId, RemittanceTask.RemittanceStatus status);
    
    boolean existsByInfluencerId(Long influencerId);
}
