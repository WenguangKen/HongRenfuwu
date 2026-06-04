package com.athlunakms.role.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public class RoleUpdateRequest {
    @Size(max=50, message="\u89d2\u8272\u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26")
    private @Size(max=50, message="\u89d2\u8272\u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26") String name;
    @Size(max=500, message="\u89d2\u8272\u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7500\u4e2a\u5b57\u7b26")
    private @Size(max=500, message="\u89d2\u8272\u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7500\u4e2a\u5b57\u7b26") String description;
    private List<Long> permissionIds;
    private String status;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Long> getPermissionIds() {
        return this.permissionIds;
    }

    public String getStatus() {
        return this.status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoleUpdateRequest)) {
            return false;
        }
        RoleUpdateRequest other = (RoleUpdateRequest)o;
        if (!other.canEqual((Object)this)) {
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
        List this$permissionIds = this.getPermissionIds();
        List other$permissionIds = other.getPermissionIds();
        if (this$permissionIds == null ? other$permissionIds != null : !((Object)this$permissionIds).equals(other$permissionIds)) {
            return false;
        }
        String this$status = this.getStatus();
        String other$status = other.getStatus();
        return !(this$status == null ? other$status != null : !this$status.equals(other$status));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoleUpdateRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        List $permissionIds = this.getPermissionIds();
        result = result * 59 + ($permissionIds == null ? 43 : ((Object)$permissionIds).hashCode());
        String $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        return result;
    }

    public String toString() {
        return "RoleUpdateRequest(name=" + this.getName() + ", description=" + this.getDescription() + ", permissionIds=" + this.getPermissionIds() + ", status=" + this.getStatus() + ")";
    }
}

