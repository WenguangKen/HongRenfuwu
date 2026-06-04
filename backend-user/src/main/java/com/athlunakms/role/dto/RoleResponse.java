package com.athlunakms.role.dto;

import com.athlunakms.role.dto.RoleResponse;
import com.athlunakms.role.entity.Role;
import java.time.LocalDateTime;
import java.util.List;

public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Role.RoleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PermissionInfo> permissions;
    private Long userCount;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Role.RoleStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public List<PermissionInfo> getPermissions() {
        return this.permissions;
    }

    public Long getUserCount() {
        return this.userCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Role.RoleStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setPermissions(List<PermissionInfo> permissions) {
        this.permissions = permissions;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoleResponse)) {
            return false;
        }
        RoleResponse other = (RoleResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Long this$userCount = this.getUserCount();
        Long other$userCount = other.getUserCount();
        if (this$userCount == null ? other$userCount != null : !((Object)this$userCount).equals(other$userCount)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        Role.RoleStatus this$status = this.getStatus();
        Role.RoleStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt)) {
            return false;
        }
        List this$permissions = this.getPermissions();
        List other$permissions = other.getPermissions();
        return !(this$permissions == null ? other$permissions != null : !((Object)this$permissions).equals(other$permissions));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoleResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Long $userCount = this.getUserCount();
        result = result * 59 + ($userCount == null ? 43 : ((Object)$userCount).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Role.RoleStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        List $permissions = this.getPermissions();
        result = result * 59 + ($permissions == null ? 43 : ((Object)$permissions).hashCode());
        return result;
    }

    public String toString() {
        return "RoleResponse(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", status=" + this.getStatus() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", permissions=" + this.getPermissions() + ", userCount=" + this.getUserCount() + ")";
    }

    public static class PermissionInfo {
        private Long id;
        private String permissionKey;
        private String title;
        private String type;

        public Long getId() {
            return this.id;
        }

        public String getPermissionKey() {
            return this.permissionKey;
        }

        public String getTitle() {
            return this.title;
        }

        public String getType() {
            return this.type;
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

        public void setType(String type) {
            this.type = type;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PermissionInfo)) {
                return false;
            }
            PermissionInfo other = (PermissionInfo)o;
            if (!other.canEqual(this)) {
                return false;
            }
            Long this$id = this.getId();
            Long other$id = other.getId();
            if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
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
            String this$type = this.getType();
            String other$type = other.getType();
            return !(this$type == null ? other$type != null : !this$type.equals(other$type));
        }

        protected boolean canEqual(Object other) {
            return other instanceof PermissionInfo;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Long $id = this.getId();
            result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
            String $permissionKey = this.getPermissionKey();
            result = result * 59 + ($permissionKey == null ? 43 : $permissionKey.hashCode());
            String $title = this.getTitle();
            result = result * 59 + ($title == null ? 43 : $title.hashCode());
            String $type = this.getType();
            result = result * 59 + ($type == null ? 43 : $type.hashCode());
            return result;
        }

        public String toString() {
            return "RoleResponse.PermissionInfo(id=" + this.getId() + ", permissionKey=" + this.getPermissionKey() + ", title=" + this.getTitle() + ", type=" + this.getType() + ")";
        }
    }
}

