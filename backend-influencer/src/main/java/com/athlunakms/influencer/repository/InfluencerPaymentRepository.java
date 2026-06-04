package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.InfluencerPayment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerPaymentRepository
extends JpaRepository<InfluencerPayment, Long> {
    public List<InfluencerPayment> findByInfluencerIdOrderByCreatedAtDesc(Long var1);

    public List<InfluencerPayment> findByStatus(InfluencerPayment.PaymentStatus var1);

    public Optional<InfluencerPayment> findByPaymentNo(String var1);
}

