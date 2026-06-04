package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.ApiResponse;
import com.athlunakms.influencer.entity.CommissionSetting;
import com.athlunakms.influencer.service.CommissionSettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/commission"})
public class CommissionSettingController {
    private final CommissionSettlementService settlementService;

    @GetMapping(value={"/setting"})
    public ApiResponse<CommissionSetting> getSetting() {
        CommissionSetting setting = this.settlementService.getSetting();
        return ApiResponse.success(setting);
    }

    @PutMapping(value={"/setting"})
    public ApiResponse<CommissionSetting> updateSetting(@RequestBody CommissionSetting setting) {
        CommissionSetting updated = this.settlementService.updateSetting(setting);
        return ApiResponse.success(updated);
    }

    @PostMapping(value={"/check"})
    public ApiResponse<Integer> triggerSettlement() {
        int count = this.settlementService.processSettlement();
        return ApiResponse.success(count);
    }

    public CommissionSettingController(CommissionSettlementService settlementService) {
        this.settlementService = settlementService;
    }
}

