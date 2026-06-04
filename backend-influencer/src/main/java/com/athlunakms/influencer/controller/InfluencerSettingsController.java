package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.entity.InfluencerSettings;
import com.athlunakms.influencer.repository.InfluencerSettingsRepository;
import com.athlunakms.influencer.service.DormancyService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer/settings"})
public class InfluencerSettingsController {
    private final InfluencerSettingsRepository settingsRepository;
    private final DormancyService dormancyService;

    @GetMapping(value={"/{key}"})
    public InfluencerSettings getSetting(@PathVariable(value="key") String key) {
        return this.settingsRepository.findById(key).orElse(null);
    }

    @PutMapping(value={"/{key}"})
    public InfluencerSettings updateSetting(@PathVariable(value="key") String key, @RequestBody InfluencerSettings setting) {
        setting.setSettingKey(key);
        return this.settingsRepository.save(setting);
    }

    @PostMapping(value={"/dormancy-check"})
    public List<String> triggerDormancyCheck(@RequestParam(value="dryRun", defaultValue="true") boolean dryRun) {
        return this.dormancyService.executeDormancyCheck(dryRun);
    }

    public InfluencerSettingsController(InfluencerSettingsRepository settingsRepository, DormancyService dormancyService) {
        this.settingsRepository = settingsRepository;
        this.dormancyService = dormancyService;
    }
}

