package com.athlunakms.user.dto;

public class SystemConfigDto {
    private String key;
    private String value;
    private String description;
    private Boolean isEncrypted;

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getIsEncrypted() {
        return this.isEncrypted;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemConfigDto)) {
            return false;
        }
        SystemConfigDto other = (SystemConfigDto)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Boolean this$isEncrypted = this.getIsEncrypted();
        Boolean other$isEncrypted = other.getIsEncrypted();
        if (this$isEncrypted == null ? other$isEncrypted != null : !((Object)this$isEncrypted).equals(other$isEncrypted)) {
            return false;
        }
        String this$key = this.getKey();
        String other$key = other.getKey();
        if (this$key == null ? other$key != null : !this$key.equals(other$key)) {
            return false;
        }
        String this$value = this.getValue();
        String other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemConfigDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Boolean $isEncrypted = this.getIsEncrypted();
        result = result * 59 + ($isEncrypted == null ? 43 : ((Object)$isEncrypted).hashCode());
        String $key = this.getKey();
        result = result * 59 + ($key == null ? 43 : $key.hashCode());
        String $value = this.getValue();
        result = result * 59 + ($value == null ? 43 : $value.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "SystemConfigDto(key=" + this.getKey() + ", value=" + this.getValue() + ", description=" + this.getDescription() + ", isEncrypted=" + this.getIsEncrypted() + ")";
    }
}

