package com.athlunakms.shopify.influencer.repository;

import com.athlunakms.shopify.influencer.entity.InfluencerReadOnly;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerReadOnlyRepository
extends JpaRepository<InfluencerReadOnly, Long> {
    @Query(value="SELECT i FROM InfluencerReadOnly i WHERE LOWER(i.influencerDiscountCode) = LOWER(:discountCode)")
    public Optional<InfluencerReadOnly> findByInfluencerDiscountCodeIgnoreCase(@Param(value="discountCode") String var1);

    public boolean existsByInfluencerDiscountCode(String var1);

    @Query(value="SELECT i FROM InfluencerReadOnly i WHERE LOWER(i.realName) = LOWER(:realName)")
    public Optional<InfluencerReadOnly> findByRealNameIgnoreCase(@Param(value="realName") String var1);

    @Query(value="SELECT i FROM InfluencerReadOnly i WHERE LOWER(i.nickName) = LOWER(:nickName)")
    public Optional<InfluencerReadOnly> findByNickNameIgnoreCase(@Param(value="nickName") String var1);
}

