package com.athlunakms.eccang.product.repository;

import com.athlunakms.eccang.product.entity.EccangProductVariant;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EccangProductVariantRepository
extends JpaRepository<EccangProductVariant, Long> {
    public List<EccangProductVariant> findByProductId(Long var1);

    public List<EccangProductVariant> findByProductIdIn(Collection<Long> var1);

    public Optional<EccangProductVariant> findByEccangVariantId(Long var1);

    public Optional<EccangProductVariant> findFirstBySkuIgnoreCase(String var1);

    @Modifying
    @Query(value="DELETE FROM EccangProductVariant v WHERE v.productId = :productId")
    public void deleteByProductId(@Param(value="productId") Long var1);

    @Lock(value=LockModeType.PESSIMISTIC_WRITE)
    @Query(value="SELECT v FROM EccangProductVariant v WHERE v.id = :id")
    public Optional<EccangProductVariant> findByIdWithLock(@Param(value="id") Long var1);

    @Query(value="SELECT DISTINCT v.sku FROM EccangProductVariant v WHERE v.sku IS NOT NULL AND v.sku != '' AND v.sku != 'null' ORDER BY v.sku")
    public List<String> findDistinctSkus();
}

