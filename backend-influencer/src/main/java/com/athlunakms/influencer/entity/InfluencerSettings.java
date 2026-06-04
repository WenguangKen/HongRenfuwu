package com.athlunakms.influencer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="influencer_settings")
public class InfluencerSettings {
    @Id
    @Column(name="setting_key", nullable=false)
    private String settingKey;
    @Column(name="setting_value", columnDefinition="TEXT")
    private String settingValue;
    private String description;
    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public String getSettingKey() {
        return this.settingKey;
    }

    public String getSettingValue() {
        return this.settingValue;
    }

    public String getDescription() {
        return this.description;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InfluencerSettings)) {
            return false;
        }
        InfluencerSettings other = (InfluencerSettings)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        String this$settingKey = this.getSettingKey();
        String other$settingKey = other.getSettingKey();
        if (this$settingKey == null ? other$settingKey != null : !this$settingKey.equals(other$settingKey)) {
            return false;
        }
        String this$settingValue = this.getSettingValue();
        String other$settingValue = other.getSettingValue();
        if (this$settingValue == null ? other$settingValue != null : !this$settingValue.equals(other$settingValue)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        if (this$description == null ? other$description != null : !this$description.equals(other$description)) {
            return false;
        }
        LocalDateTime this$updatedAt = this.getUpdatedAt();
        LocalDateTime other$updatedAt = other.getUpdatedAt();
        return !(this$updatedAt == null ? other$updatedAt != null : !((Object)this$updatedAt).equals(other$updatedAt));
    }

    protected boolean canEqual(Object other) {
        return other instanceof InfluencerSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $settingKey = this.getSettingKey();
        result = result * 59 + ($settingKey == null ? 43 : $settingKey.hashCode());
        String $settingValue = this.getSettingValue();
        result = result * 59 + ($settingValue == null ? 43 : $settingValue.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        LocalDateTime $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : ((Object)$updatedAt).hashCode());
        return result;
    }

    public String toString() {
        return "InfluencerSettings(settingKey=" + this.getSettingKey() + ", settingValue=" + this.getSettingValue() + ", description=" + this.getDescription() + ", updatedAt=" + this.getUpdatedAt() + ")";
    }
}

