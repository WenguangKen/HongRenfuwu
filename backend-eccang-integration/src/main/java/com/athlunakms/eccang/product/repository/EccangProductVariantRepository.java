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
public interface EccangProductVariantRepository extends JpaRepository<EccangProductVariant, Long> {

    List<EccangProductVariant> findByProductId(Long productId);

    List<EccangProductVariant> findByProductIdIn(Collection<Long> productIds);

    Optional<EccangProductVariant> findByUserAccountAndSku(String userAccount, String sku);

    List<EccangProductVariant> findBySkuIn(Collection<String> skus);

    @Query("SELECT v FROM EccangProductVariant v WHERE v.sku = :sku "
        + "AND UPPER(REPLACE(v.userAccount, '-', '_')) = :normalizedAccount")
    Optional<EccangProductVariant> findByNormalizedAccountAndSku(
        @Param("normalizedAccount") String normalizedAccount, @Param("sku") String sku);

    Optional<EccangProductVariant> findFirstBySkuIgnoreCase(String sku);

    @Modifying
    @Query("DELETE FROM EccangProductVariant v WHERE v.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM EccangProductVariant v WHERE v.id = :id")
    Optional<EccangProductVariant> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT DISTINCT v.sku FROM EccangProductVariant v WHERE v.sku IS NOT NULL AND v.sku != '' ORDER BY v.sku")
    List<String> findDistinctSkus();

    @Query("SELECT v FROM EccangProductVariant v WHERE v.sku IN :skus")
    List<EccangProductVariant> findAllBySkuIn(@Param("skus") Collection<String> skus);
}
