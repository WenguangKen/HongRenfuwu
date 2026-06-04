package com.athlunakms.permission.entity;

import com.athlunakms.permission.entity.Permission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="permissions", indexes={@Index(name="idx_type", columnList="type"), @Index(name="idx_parent_key", columnList="parent_key"), @Index(name="idx_permission_key", columnList="permission_key")})
public class Permission {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=100, name="permission_key")
    private String permissionKey;
    @Column(nullable=false, length=200)
    private String title;
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private PermissionType type;
    @Column(length=100, name="parent_key")
    private String parentKey;
    @Column(length=500)
    private String description;
    @Column(nullable=false, name="sort_order")
    private Integer sortOrder = 0;
    @Column(nullable=false, name="created_at", updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return this.id;
    }

    public String getPermissionKey() {
        return this.permissionKey;
    }

    public String getTitle() {
        return this.title;
    }

    public PermissionType getType() {
        return this.type;
    }

    public String getParentKey() {
        return this.parentKey;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Permission)) {
            return false;
        }
        Permission other = (Permission)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Integer this$sortOrder = this.getSortOrder();
        Integer other$sortOrder = other.getSortOrder();
        if (this$sortOrder == null ? other$sortOrder != null : !((Object)this$sortOrder).equals(other$sortOrder)) {
            return false;
        }
        String this$permissionKey = this.getPermissionKey();
        String other$permissionKey = other.getPermissionKey();
        if (this$permissionKey == null ? other$permissionKey != null : !this$permissionKey.equals(other$permissionKey)) {
            return false;
        }
        String this$title = this.getTitle();
        String other$title = other.getTitle();
        if (this$title == null ? other$title != null : !this$title.equals(other$title)) {
            return false;
        }
        PermissionType this$type = this.getType();
        PermissionType other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        String this$parentKey = this.getParentKey();
        String other$parentKey = other.getParentKey();
        if (this$parentKey == null ? other$parentKey != null : !this$parentKey.equals(other$parentKey)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        return !(this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Permission;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Integer $sortOrder = this.getSortOrder();
        result = result * 59 + ($sortOrder == null ? 43 : ((Object)$sortOrder).hashCode());
        String $permissionKey = this.getPermissionKey();
        result = result * 59 + ($permissionKey == null ? 43 : $permissionKey.hashCode());
        String $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        PermissionType $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        String $parentKey = this.getParentKey();
        result = result * 59 + ($parentKey == null ? 43 : $parentKey.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        return result;
    }

    public String toString() {
        return "Permission(id=" + this.getId() + ", permissionKey=" + this.getPermissionKey() + ", title=" + this.getTitle() + ", type=" + this.getType() + ", parentKey=" + this.getParentKey() + ", description=" + this.getDescription() + ", sortOrder=" + this.getSortOrder() + ", createdAt=" + this.getCreatedAt() + ")";
    }

    public static enum PermissionType {
        page,
        tab,
        operation;
    }
}

