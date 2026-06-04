package com.athlunakms.user.repository;

import com.athlunakms.user.entity.Province;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository
extends JpaRepository<Province, Long> {
    public List<Province> findByCountryCodeAndEnabledTrueOrderByProvinceNameAsc(String var1);

    public Optional<Province> findByCountryCodeAndProvinceCode(String var1, String var2);

    public boolean existsByCountryCodeAndProvinceCode(String var1, String var2);

    public boolean existsByCountryCode(String var1);
}

