package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.SystemTag;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemTagRepository
extends JpaRepository<SystemTag, Long> {
    public List<SystemTag> findByCategoryAndEnabledTrue(String var1, Sort var2);

    public List<SystemTag> findByCategoryAndNameAndEnabledTrue(String var1, String var2);

    public List<SystemTag> findByCategoryAndNameInAndEnabledTrue(String var1, List<String> var2);

    public List<SystemTag> findByNameIn(List<String> var1);

    public boolean existsByCategoryAndName(String var1, String var2);
}

