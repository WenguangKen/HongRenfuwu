package com.athlunakms.eccang.store.repository;

import com.athlunakms.eccang.store.entity.EccangStore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EccangStoreRepository
extends JpaRepository<EccangStore, Long> {
    public List<EccangStore> findByStatus(String var1);

    public Optional<EccangStore> findByEccangStoreCode(String eccangStoreCode);
}

