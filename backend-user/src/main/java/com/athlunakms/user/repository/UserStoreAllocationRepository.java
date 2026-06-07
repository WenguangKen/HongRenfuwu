package com.athlunakms.user.repository;

import com.athlunakms.user.entity.UserStoreAllocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoreAllocationRepository extends JpaRepository<UserStoreAllocation, Long> {
    List<UserStoreAllocation> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
