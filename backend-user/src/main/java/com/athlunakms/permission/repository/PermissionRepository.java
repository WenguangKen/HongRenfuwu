package com.athlunakms.permission.repository;

import com.athlunakms.permission.entity.Permission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository
extends JpaRepository<Permission, Long> {
    public Optional<Permission> findByPermissionKey(String var1);

    public List<Permission> findByType(Permission.PermissionType var1);

    public List<Permission> findByParentKey(String var1);

    @Query(value="SELECT DISTINCT p FROM Permission p INNER JOIN RolePermission rp ON p.id = rp.permissionId INNER JOIN UserRole ur ON rp.roleId = ur.roleId WHERE ur.userId = :userId")
    public List<Permission> findPermissionsByUserId(@Param(value="userId") Long var1);

    @Query(value="SELECT p FROM Permission p INNER JOIN RolePermission rp ON p.id = rp.permissionId WHERE rp.roleId = :roleId")
    public List<Permission> findPermissionsByRoleId(@Param(value="roleId") Long var1);
}

