package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerBalance;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerBalanceRepository
extends JpaRepository<InfluencerBalance, Long> {
    public Optional<InfluencerBalance> findByInfluencerId(Long var1);

    public boolean existsByInfluencerId(Long var1);

    @Modifying
    @Query(value="UPDATE InfluencerBalance b SET b.pendingAmount = b.pendingAmount + :amount, b.updatedAt = CURRENT_TIMESTAMP WHERE b.influencerId = :influencerId")
    public int addPendingAmount(@Param(value="influencerId") Long var1, @Param(value="amount") BigDecimal var2);

    @Modifying
    @Query(value="UPDATE InfluencerBalance b SET b.pendingAmount = b.pendingAmount - :amount, b.availableAmount = b.availableAmount + :amount, b.updatedAt = CURRENT_TIMESTAMP WHERE b.influencerId = :influencerId AND b.pendingAmount >= :amount")
    public int transferPendingToAvailable(@Param(value="influencerId") Long var1, @Param(value="amount") BigDecimal var2);

    @Modifying
    @Query(value="UPDATE InfluencerBalance b SET b.pendingAmount = b.pendingAmount - :amount, b.paidAmount = b.paidAmount + :amount, b.updatedAt = CURRENT_TIMESTAMP WHERE b.influencerId = :influencerId AND b.pendingAmount >= :amount")
    public int transferPendingToPaid(@Param(value="influencerId") Long var1, @Param(value="amount") BigDecimal var2);

    @Modifying
    @Query(value="UPDATE InfluencerBalance b SET b.paidAmount = b.paidAmount - :amount, b.pendingAmount = b.pendingAmount + :amount, b.updatedAt = CURRENT_TIMESTAMP WHERE b.influencerId = :influencerId AND b.paidAmount >= :amount")
    public int transferPaidToPending(@Param(value="influencerId") Long var1, @Param(value="amount") BigDecimal var2);

    @Query(value="SELECT b FROM InfluencerBalance b WHERE (b.pendingAmount > 0 OR b.availableAmount > 0 OR b.paidAmount > 0) AND (:influencer IS NULL OR b.influencerName LIKE %:influencer% OR b.influencerId IN (SELECT i.id FROM Influencer i WHERE i.email LIKE %:influencer%)) ORDER BY b.updatedAt DESC")
    public Page<InfluencerBalance> findAllWithNonZeroBalanceFiltered(@Param(value="influencer") String var1, Pageable var2);

    @Query(value="SELECT b FROM InfluencerBalance b WHERE (b.pendingAmount > 0 OR b.availableAmount > 0 OR b.paidAmount > 0) AND (:influencer IS NULL OR b.influencerName LIKE %:influencer%) AND (:email IS NULL OR b.influencerId IN (SELECT i.id FROM Influencer i WHERE i.email LIKE %:email%)) ORDER BY b.updatedAt DESC")
    public Page<InfluencerBalance> searchBalances(@Param(value="influencer") String var1, @Param(value="email") String var2, Pageable var3);

    @Query(value="SELECT b FROM InfluencerBalance b WHERE (b.pendingAmount > 0 OR b.availableAmount > 0 OR b.paidAmount > 0) ORDER BY b.updatedAt DESC")
    public Page<InfluencerBalance> findAllWithNonZeroBalance(Pageable var1);
}

