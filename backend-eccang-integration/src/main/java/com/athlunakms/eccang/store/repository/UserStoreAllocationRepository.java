package com.athlunakms.eccang.store.repository;

import com.athlunakms.eccang.store.entity.UserStoreAllocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStoreAllocationRepository extends JpaRepository<UserStoreAllocation, Long> {
    
    List<UserStoreAllocation> findByUserId(Long userId);

    @Query(value = "SELECT id FROM users WHERE username = :username LIMIT 1", nativeQuery = true)
    Long findUserIdByUsername(@Param("username") String username);

    @Query(value = "SELECT COUNT(*) FROM user_roles ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id = :userId AND (r.name = '超级管理员' OR r.name = 'Admin')", nativeQuery = true)
    int countAdminRolesByUserId(@Param("userId") Long userId);
}
