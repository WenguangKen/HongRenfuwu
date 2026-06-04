package com.athlunakms.permission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(name="role_permissions", uniqueConstraints={@UniqueConstraint(name="uk_role_permission", columnNames={"role_id", "permission_id"})}, indexes={@Index(name="idx_role_id", columnList="role_id"), @Index(name="idx_permission_id", columnList="permission_id")})
public class RolePermission {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, name="role_id")
    private Long roleId;
    @Column(nullable=false, name="permission_id")
    private Long permissionId;
    @Column(nullable=false, name="created_at", updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return this.id;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public Long getPermissionId() {
        return this.permissionId;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RolePermission)) {
            return false;
        }
        RolePermission other = (RolePermission)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$roleId = this.getRoleId();
        Long other$roleId = other.getRoleId();
        if (this$roleId == null ? other$roleId != null : !((Object)this$roleId).equals(other$roleId)) {
            return false;
        }
        Long this$permissionId = this.getPermissionId();
        Long other$permissionId = other.getPermissionId();
        if (this$permissionId == null ? other$permissionId != null : !((Object)this$permissionId).equals(other$permissionId)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RolePermission;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $roleId = this.getRoleId();
        result = result * 59 + ($roleId == null ? 43 : ((Object)$roleId).hashCode());
        Long $permissionId = this.getPermissionId();
        result = result * 59 + ($permissionId == null ? 43 : ((Object)$permissionId).hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "RolePermission(id=" + this.getId() + ", roleId=" + this.getRoleId() + ", permissionId=" + this.getPermissionId() + ", createdAt=" + this.getCreatedAt() + ")";
    }
}

