package com.athlunakms.system.dto;

import com.athlunakms.system.entity.DatabaseConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DatabaseConfigRequest {
    @NotNull(message="\u914d\u7f6e\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a")
    private // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull(message="\u914d\u7f6e\u7c7b\u578b\u4e0d\u80fd\u4e3a\u7a7a") DatabaseConfig.ConfigType configType;
    @NotBlank(message="\u4e3b\u673a\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u4e3b\u673a\u5730\u5740\u4e0d\u80fd\u4e3a\u7a7a") String host;
    @NotNull(message="\u7aef\u53e3\u4e0d\u80fd\u4e3a\u7a7a")
    @Positive(message="\u7aef\u53e3\u5fc5\u987b\u5927\u4e8e0")
    private @NotNull(message="\u7aef\u53e3\u4e0d\u80fd\u4e3a\u7a7a") @Positive(message="\u7aef\u53e3\u5fc5\u987b\u5927\u4e8e0") Integer port;
    @NotBlank(message="\u6570\u636e\u5e93\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u6570\u636e\u5e93\u540d\u79f0\u4e0d\u80fd\u4e3a\u7a7a") String database;
    @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a") String username;
    @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    private @NotBlank(message="\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a") String password;
    private String description;
    private Boolean isActive = true;

    public DatabaseConfig.ConfigType getConfigType() {
        return this.configType;
    }

    public String getHost() {
        return this.host;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public void setConfigType(DatabaseConfig.ConfigType configType) {
        this.configType = configType;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DatabaseConfigRequest)) {
            return false;
        }
        DatabaseConfigRequest other = (DatabaseConfigRequest)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        Integer this$port = this.getPort();
        Integer other$port = other.getPort();
        if (this$port == null ? other$port != null : !((Object)this$port).equals(other$port)) {
            return false;
        }
        Boolean this$isActive = this.getIsActive();
        Boolean other$isActive = other.getIsActive();
        if (this$isActive == null ? other$isActive != null : !((Object)this$isActive).equals(other$isActive)) {
            return false;
        }
        DatabaseConfig.ConfigType this$configType = this.getConfigType();
        DatabaseConfig.ConfigType other$configType = other.getConfigType();
        if (this$configType == null ? other$configType != null : !this$configType.equals(other$configType)) {
            return false;
        }
        String this$host = this.getHost();
        String other$host = other.getHost();
        if (this$host == null ? other$host != null : !this$host.equals(other$host)) {
            return false;
        }
        String this$database = this.getDatabase();
        String other$database = other.getDatabase();
        if (this$database == null ? other$database != null : !this$database.equals(other$database)) {
            return false;
        }
        String this$username = this.getUsername();
        String other$username = other.getUsername();
        if (this$username == null ? other$username != null : !this$username.equals(other$username)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$description = this.getDescription();
        String other$description = other.getDescription();
        return !(this$description == null ? other$description != null : !this$description.equals(other$description));
    }

    protected boolean canEqual(Object other) {
        return other instanceof DatabaseConfigRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Integer $port = this.getPort();
        result = result * 59 + ($port == null ? 43 : ((Object)$port).hashCode());
        Boolean $isActive = this.getIsActive();
        result = result * 59 + ($isActive == null ? 43 : ((Object)$isActive).hashCode());
        DatabaseConfig.ConfigType $configType = this.getConfigType();
        result = result * 59 + ($configType == null ? 43 : $configType.hashCode());
        String $host = this.getHost();
        result = result * 59 + ($host == null ? 43 : $host.hashCode());
        String $database = this.getDatabase();
        result = result * 59 + ($database == null ? 43 : $database.hashCode());
        String $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }

    public String toString() {
        return "DatabaseConfigRequest(configType=" + this.getConfigType() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", database=" + this.getDatabase() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", description=" + this.getDescription() + ", isActive=" + this.getIsActive() + ")";
    }
}

