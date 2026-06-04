package com.athlunakms.eccang.config.repository;

import com.athlunakms.eccang.config.entity.EccangConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EccangConfigRepository extends JpaRepository<EccangConfig, Long> {
}
