package com.athlunakms.webhook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="system_configs")
public class SystemConfig {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="config_key", nullable=false, unique=true)
    private String configKey;
    @Column(name="config_value", columnDefinition="TEXT")
    private String configValue;
    @Column(name="description")
    private String description;
    @Column(name="is_encrypted")
    private Boolean isEncrypted = false;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Long getId() {
        return this.id;
    }

    public String getConfigKey() {
        return this.configKey;
    }

    public String getConfigValue() {
        return this.configValue;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getIsEncrypted() {
        return this.isEncrypted;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemConfig)) {
            return false;
        }
        SystemConfig other = (SystemConfig)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Long this$id = this.getId();
        Long other$id = other.getId();
        if (this$id == null ? other$id != null : !((Object)this$id).equals(other$id)) {
            return false;
        }
        Boolean this$isEncrypted = this.getIsEncrypted();
        Boolean other$isEncrypted = other.getIsEncrypted();
        if (this$isEncrypted == null ? other$isEncrypted != null : !((Object)this$isEncrypted).equals(other$isEncrypted)) {
            return false;
        }
        String this$configKey = this.getConfigKey();
        String other$configKey = other.getConfigKey();
        if (this$configKey == null ? other$configKey != null : !this$configKey.equals(other$configKey)) {
            return false;
        }
        String this$configValue = this.getConfigValue();
        String other$configValue = other.getConfigValue();
        if (this$configValue == null ? other$configValue != null : !this$configValue.equals(other$configValue)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        LocalDateTime this$createdAt = this.getCreatedAt();
        LocalDateTime other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !((Object)this$createdAt).equals(other$createdAt)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $id = this.getId();
        result = result * 59 + ($id == null ? 43 : ((Object)$id).hashCode());
        Boolean $isEncrypted = this.getIsEncrypted();
        result = result * 59 + ($isEncrypted == null ? 43 : ((Object)$isEncrypted).hashCode());
        String $configKey = this.getConfigKey();
        result = result * 59 + ($configKey == null ? 43 : $configKey.hashCode());
        String $configValue = this.getConfigValue();
        result = result * 59 + ($configValue == null ? 43 : $configValue.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        LocalDateTime $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : ((Object)$createdAt).hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "SystemConfig(id=" + this.getId() + ", configKey=" + this.getConfigKey() + ", configValue=" + this.getConfigValue() + ", description=" + this.getDescription() + ", isEncrypted=" + this.getIsEncrypted() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

