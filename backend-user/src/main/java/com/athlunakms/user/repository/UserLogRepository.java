package com.athlunakms.user.repository;

import com.athlunakms.user.entity.UserLog;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLogRepository
extends JpaRepository<UserLog, Long>,
JpaSpecificationExecutor<UserLog> {
    public Page<UserLog> findByUserIdOrderByCreatedAtDesc(Long var1, Pageable var2);

    public Page<UserLog> findByActionTypeOrderByCreatedAtDesc(String var1, Pageable var2);

    @Query(value="SELECT l FROM UserLog l WHERE l.createdAt BETWEEN :startTime AND :endTime ORDER BY l.createdAt DESC")
    public Page<UserLog> findByTimeRange(@Param(value="startTime") LocalDateTime var1, @Param(value="endTime") LocalDateTime var2, Pageable var3);
}

