package com.athlunakms.user.repository;

import com.athlunakms.user.entity.SystemTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemTagRepository
extends JpaRepository<SystemTag, Long> {
    public List<SystemTag> findByCategoryOrderBySortOrderAsc(String var1);

    public List<SystemTag> findByCategoryAndEnabledTrueOrderBySortOrderAsc(String var1);

    public boolean existsByCategoryAndName(String var1, String var2);

    public Optional<SystemTag> findByCategoryAndName(String var1, String var2);

    public long countByCategory(String var1);

    public void deleteByCategoryAndId(String var1, Long var2);

    public List<SystemTag> findAllByOrderBySortOrderAsc();
}

