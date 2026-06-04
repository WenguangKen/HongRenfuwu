package com.athlunakms.role.repository;

import com.athlunakms.role.entity.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository
extends JpaRepository<UserRole, Long> {
    @Query(value="SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    public List<Long> findRoleIdsByUserId(@Param(value="userId") Long var1);

    @Modifying
    @Query(value="DELETE FROM UserRole ur WHERE ur.userId = :userId")
    public void deleteByUserId(@Param(value="userId") Long var1);

    @Modifying
    @Query(value="DELETE FROM UserRole ur WHERE ur.roleId = :roleId")
    public void deleteByRoleId(@Param(value="roleId") Long var1);

    public boolean existsByUserIdAndRoleId(Long var1, Long var2);

    @Query(value="SELECT ur.userId FROM UserRole ur WHERE ur.roleId = :roleId")
    public List<Long> findUserIdsByRoleId(@Param(value="roleId") Long var1);
}

