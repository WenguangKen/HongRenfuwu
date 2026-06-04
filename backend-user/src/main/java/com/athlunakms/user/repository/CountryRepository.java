package com.athlunakms.user.repository;

import com.athlunakms.user.entity.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository
extends JpaRepository<Country, Integer> {
    public Optional<Country> findByCode(String var1);

    @Query(value="SELECT c FROM Country c WHERE c.enabled = true ORDER BY c.sortOrder ASC, c.nameCn ASC")
    public List<Country> findAllEnabled();

    @Query(value="SELECT c FROM Country c ORDER BY c.sortOrder ASC, c.nameCn ASC")
    public List<Country> findAllOrderBySortOrder();

    public boolean existsByCode(String var1);
}

