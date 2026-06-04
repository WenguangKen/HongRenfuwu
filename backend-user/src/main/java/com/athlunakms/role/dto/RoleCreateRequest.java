package com.athlunakms.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class RoleCreateRequest {
    @NotBlank(message="\u89d2\u8272\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max=50, message="\u89d2\u8272\u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26")
    private @NotBlank(message="\u89d2\u8272\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") @Size(max=50, message="\u89d2\u8272\u540d\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc750\u4e2a\u5b57\u7b26") String name;
    @Size(max=500, message="\u89d2\u8272\u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7500\u4e2a\u5b57\u7b26")
    private @Size(max=500, message="\u89d2\u8272\u63cf\u8ff0\u957f\u5ea6\u4e0d\u80fd\u8d85\u8fc7500\u4e2a\u5b57\u7b26") String description;
    @NotNull(message="\u6743\u9650ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotNull(message="\u6743\u9650ID\u5217\u8868\u4e0d\u80fd\u4e3a\u7a7a") List<Long> permissionIds;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Long> getPermissionIds() {
        return this.permissionIds;
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

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RoleCreateRequest)) {
            return false;
        }
        RoleCreateRequest other = (RoleCreateRequest)o;
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
        return !(this$permissionIds == null ? other$permissionIds != null : !((Object)this$permissionIds).equals(other$permissionIds));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoleCreateRequest;
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
        return result;
    }

    public String toString() {
        return "RoleCreateRequest(name=" + this.getName() + ", description=" + this.getDescription() + ", permissionIds=" + this.getPermissionIds() + ")";
    }
}

