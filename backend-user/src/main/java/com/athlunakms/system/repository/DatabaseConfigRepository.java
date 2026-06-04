package com.athlunakms.system.repository;

import com.athlunakms.system.entity.DatabaseConfig;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseConfigRepository
extends JpaRepository<DatabaseConfig, Long> {
    public List<DatabaseConfig> findByConfigType(DatabaseConfig.ConfigType var1);

    public List<DatabaseConfig> findByConfigTypeAndIsActive(DatabaseConfig.ConfigType var1, Boolean var2);

    public Optional<DatabaseConfig> findFirstByConfigTypeAndIsActiveOrderByIdAsc(DatabaseConfig.ConfigType var1, Boolean var2);
}

