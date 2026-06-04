package com.athlunakms.eccang.influencer.repository;

import com.athlunakms.eccang.influencer.entity.SystemTagReadOnly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemTagReadOnlyRepository
extends JpaRepository<SystemTagReadOnly, Long> {
    public List<SystemTagReadOnly> findByCategoryAndEnabledTrue(String var1);
}

