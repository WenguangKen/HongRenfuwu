package com.athlunakms.system.controller;

import com.athlunakms.common.annotation.LogOperation;
import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.system.dto.DatabaseConfigRequest;
import com.athlunakms.system.dto.DatabaseConfigResponse;
import com.athlunakms.system.entity.DatabaseConfig;
import com.athlunakms.system.service.DatabaseConfigService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/system/database-configs"})
public class DatabaseConfigController {
    private final DatabaseConfigService configService;

    @GetMapping
    public ApiResponse<List<DatabaseConfigResponse>> getConfigList(@RequestParam(value="configType", required=false) DatabaseConfig.ConfigType configType) {
        List<DatabaseConfigResponse> configs = this.configService.getConfigList(configType);
        return ApiResponse.success(configs);
    }

    @GetMapping(value={"/{id}"})
    public ApiResponse<DatabaseConfigResponse> getConfigById(@PathVariable(value="id") Long id) {
        DatabaseConfigResponse config = this.configService.getConfigById(id);
        return ApiResponse.success(config);
    }

    @PostMapping
    @LogOperation(value="\u521b\u5efa\u6570\u636e\u5e93\u914d\u7f6e")
    public ApiResponse<DatabaseConfigResponse> createConfig(@Valid @RequestBody DatabaseConfigRequest request) {
        DatabaseConfigResponse config = this.configService.createConfig(request);
        return ApiResponse.success("\u6570\u636e\u5e93\u914d\u7f6e\u521b\u5efa\u6210\u529f", config);
    }

    @PutMapping(value={"/{id}"})
    @LogOperation(value="\u66f4\u65b0\u6570\u636e\u5e93\u914d\u7f6e")
    public ApiResponse<DatabaseConfigResponse> updateConfig(@PathVariable(value="id") Long id, @Valid @RequestBody DatabaseConfigRequest request) {
        DatabaseConfigResponse config = this.configService.updateConfig(id, request);
        return ApiResponse.success("\u6570\u636e\u5e93\u914d\u7f6e\u66f4\u65b0\u6210\u529f", config);
    }

    @DeleteMapping(value={"/{id}"})
    @LogOperation(value="\u5220\u9664\u6570\u636e\u5e93\u914d\u7f6e")
    public ApiResponse<Void> deleteConfig(@PathVariable(value="id") Long id) {
        this.configService.deleteConfig(id);
        return ApiResponse.success("\u6570\u636e\u5e93\u914d\u7f6e\u5220\u9664\u6210\u529f", null);
    }

    @PostMapping(value={"/test"})
    public ApiResponse<Void> testConnection(@Valid @RequestBody DatabaseConfigRequest request) {
        this.configService.testConnection(request);
        return ApiResponse.success("\u6570\u636e\u5e93\u8fde\u63a5\u6d4b\u8bd5\u6210\u529f", null);
    }

    public DatabaseConfigController(DatabaseConfigService configService) {
        this.configService = configService;
    }
}

