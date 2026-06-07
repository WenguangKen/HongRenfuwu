package com.athlunakms.eccang.product.repository;

import com.athlunakms.eccang.product.entity.EccangProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EccangProductRepository
        extends JpaRepository<EccangProduct, Long>, JpaSpecificationExecutor<EccangProduct> {

    List<EccangProduct> findByStoreId(Long storeId);

    long countByStoreIdAndStatus(Long storeId, String status);

    long countByStoreId(Long storeId);

    Optional<EccangProduct> findByUserAccountAndParentAsin(String userAccount, String parentAsin);

    @Query("select distinct p.userAccount from EccangProduct p where p.userAccount is not null and p.userAccount <> ''")
    List<String> findDistinctUserAccounts();

    @Query("select distinct p.site, p.userAccount from EccangProduct p where p.storeId = :storeId and p.userAccount is not null and p.userAccount <> ''")
    List<Object[]> findDistinctSitesAndUserAccountsByStoreId(@Param("storeId") Long storeId);

    @Query("select p.site, p.userAccount, " +
           "sum(case when p.status = 'active' then 1L else 0L end), " +
           "sum(case when p.status = 'inactive' then 1L else 0L end), " +
           "count(p) " +
           "from EccangProduct p " +
           "where p.storeId = :storeId and p.userAccount is not null and p.userAccount <> '' " +
           "group by p.site, p.userAccount")
    List<Object[]> findProductCountsGroupedBySiteAndAccount(@Param("storeId") Long storeId);

    @Query("select p.site, p.userAccount, " +
           "sum(case when v.status = 'active' then 1L else 0L end), " +
           "sum(case when v.status = 'inactive' then 1L else 0L end), " +
           "count(v) " +
           "from EccangProductVariant v join EccangProduct p on v.productId = p.id " +
           "where p.storeId = :storeId and p.userAccount is not null and p.userAccount <> '' " +
           "group by p.site, p.userAccount")
    List<Object[]> findVariantCountsGroupedBySiteAndAccount(@Param("storeId") Long storeId);

    @Query("select count(v) from EccangProductVariant v join EccangProduct p on v.productId = p.id where p.storeId = :storeId")
    long countVariantsByStoreId(@Param("storeId") Long storeId);

    @Query("select count(v) from EccangProductVariant v join EccangProduct p on v.productId = p.id where p.storeId = :storeId and v.status = :status")
    long countVariantsByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") String status);

    @Query("select distinct p.site from EccangProduct p where p.site is not null and p.site <> ''")
    List<String> findDistinctSites();

    @Modifying
    @Transactional
    @Query(value = "UPDATE eccang_product_variants v JOIN eccang_products p ON v.product_id = p.id SET v.status = p.status WHERE v.status <> p.status", nativeQuery = true)
    int syncVariantStatusesWithProducts();
}
