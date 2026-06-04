package com.athlunakms.eccang.config.service;

import com.athlunakms.eccang.config.dto.EccangConfigDto;

public interface EccangConfigService {
    EccangConfigDto getConfig();
    EccangConfigDto saveConfig(EccangConfigDto configDto);
}
