package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.ImportJob;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportJobRepository
extends JpaRepository<ImportJob, Long> {
    public List<ImportJob> findTop10ByOrderByCreatedAtDesc();
}

