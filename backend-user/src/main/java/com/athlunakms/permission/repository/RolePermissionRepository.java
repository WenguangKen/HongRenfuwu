package com.athlunakms.permission.repository;

import com.athlunakms.permission.entity.RolePermission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository
extends JpaRepository<RolePermission, Long> {
    @Query(value="SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId")
    public List<Long> findPermissionIdsByRoleId(@Param(value="roleId") Long var1);

    @Modifying
    @Query(value="DELETE FROM RolePermission rp WHERE rp.roleId = :roleId")
    public void deleteByRoleId(@Param(value="roleId") Long var1);

    public boolean existsByRoleIdAndPermissionId(Long var1, Long var2);
}

