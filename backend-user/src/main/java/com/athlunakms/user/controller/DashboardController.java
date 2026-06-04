package com.athlunakms.user.controller;

import com.athlunakms.common.dto.ApiResponse;
import com.athlunakms.user.dto.DashboardStatsDto;
import com.athlunakms.user.dto.DashboardTrendDto;
import com.athlunakms.user.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/dashboard"})
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping(value={"/stats"})
    public ApiResponse<DashboardStatsDto> getStats() {
        DashboardStatsDto stats = this.dashboardService.getStats();
        return ApiResponse.success(stats);
    }

    @GetMapping(value={"/trend"})
    public ApiResponse<DashboardTrendDto> getTrend(@RequestParam(value="days", defaultValue="15") int days) {
        DashboardTrendDto trend = this.dashboardService.getTrend(days);
        return ApiResponse.success(trend);
    }

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
}

