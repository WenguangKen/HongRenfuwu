package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.CommissionSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionSettingRepository
extends JpaRepository<CommissionSetting, Long> {
    public Optional<CommissionSetting> findFirstByOrderByIdAsc();

    public Optional<CommissionSetting> findFirstByEnabledTrueOrderByIdAsc();
}

