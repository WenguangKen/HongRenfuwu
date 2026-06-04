package com.athlunakms.permission.dto;

import com.athlunakms.permission.dto.PermissionResponse;
import java.util.List;

public class PermissionResponse {
    private List<String> pagePermissions;
    private List<String> operationPermissions;
    private List<PermissionInfo> allPermissions;

    public void setPagePermissions(List<String> pagePermissions) {
        this.pagePermissions = pagePermissions;
    }

    public void setOperationPermissions(List<String> operationPermissions) {
        this.operationPermissions = operationPermissions;
    }

    public void setAllPermissions(List<PermissionInfo> allPermissions) {
        this.allPermissions = allPermissions;
    }

    public List<String> getPagePermissions() {
        return this.pagePermissions;
    }

    public List<String> getOperationPermissions() {
        return this.operationPermissions;
    }

    public List<PermissionInfo> getAllPermissions() {
        return this.allPermissions;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PermissionResponse)) {
            return false;
        }
        PermissionResponse other = (PermissionResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        List this$pagePermissions = this.getPagePermissions();
        List other$pagePermissions = other.getPagePermissions();
        if (this$pagePermissions == null ? other$pagePermissions != null : !((Object)this$pagePermissions).equals(other$pagePermissions)) {
            return false;
        }
        List this$operationPermissions = this.getOperationPermissions();
        List other$operationPermissions = other.getOperationPermissions();
        if (this$operationPermissions == null ? other$operationPermissions != null : !((Object)this$operationPermissions).equals(other$operationPermissions)) {
            return false;
        }
        List this$allPermissions = this.getAllPermissions();
        List other$allPermissions = other.getAllPermissions();
        return !(this$allPermissions == null ? other$allPermissions != null : !((Object)this$allPermissions).equals(other$allPermissions));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PermissionResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List $pagePermissions = this.getPagePermissions();
        result = result * 59 + ($pagePermissions == null ? 43 : ((Object)$pagePermissions).hashCode());
        List $operationPermissions = this.getOperationPermissions();
        result = result * 59 + ($operationPermissions == null ? 43 : ((Object)$operationPermissions).hashCode());
        List $allPermissions = this.getAllPermissions();
        result = result * 59 + ($allPermissions == null ? 43 : ((Object)$allPermissions).hashCode());
        return result;
    }

    public String toString() {
        return "PermissionResponse(pagePermissions=" + this.getPagePermissions() + ", operationPermissions=" + this.getOperationPermissions() + ", allPermissions=" + this.getAllPermissions() + ")";
    }

    public static class PermissionInfo {
        private Long id;
        private String permissionKey;
        private String title;
        private String type;
        private String parentKey;
        private String description;

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

        public void setParentKey(String parentKey) {
            this.parentKey = parentKey;
        }

        public void setDescription(String description) {
            this.description = description;
        }

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

        public String getParentKey() {
            return this.parentKey;
        }

        public String getDescription() {
            return this.description;
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
            return !(this$description == null ? other$description != null : !this$description.equals(other$description));
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
            String $parentKey = this.getParentKey();
            result = result * 59 + ($parentKey == null ? 43 : $parentKey.hashCode());
            String $description = this.getDescription();
            result = result * 59 + ($description == null ? 43 : $description.hashCode());
            return result;
        }

        public String toString() {
            return "PermissionResponse.PermissionInfo(id=" + this.getId() + ", permissionKey=" + this.getPermissionKey() + ", title=" + this.getTitle() + ", type=" + this.getType() + ", parentKey=" + this.getParentKey() + ", description=" + this.getDescription() + ")";
        }
    }
}

