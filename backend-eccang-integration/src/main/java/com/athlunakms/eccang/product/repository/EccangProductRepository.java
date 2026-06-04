package com.athlunakms.eccang.product.repository;

import com.athlunakms.eccang.product.entity.EccangProduct;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EccangProductRepository
extends JpaRepository<EccangProduct, Long>,
JpaSpecificationExecutor<EccangProduct> {
    public Optional<EccangProduct> findByEccangProductId(Long var1);

    public List<EccangProduct> findByStoreId(Long var1);

    public List<EccangProduct> findByEccangProductIdIn(Collection<Long> var1);
}

