package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.DashboardStatsDto;
import com.athlunakms.influencer.dto.DashboardTrendDto;
import com.athlunakms.influencer.dto.InfluencerRankDto;
import com.athlunakms.influencer.dto.StageDistributionDto;
import com.athlunakms.influencer.service.DashboardService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/dashboard"})
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping(value={"/stats"})
    public DashboardStatsDto getStats() {
        return this.dashboardService.getStats();
    }

    @GetMapping(value={"/trend"})
    public DashboardTrendDto getTrend(@RequestParam(value="days", defaultValue="15") int days, @RequestParam(value="startDate", required=false) String startDate, @RequestParam(value="endDate", required=false) String endDate) {
        return this.dashboardService.getTrend(days, startDate, endDate);
    }

    @GetMapping(value={"/top-gmv"})
    public List<InfluencerRankDto> getTopGMV(@RequestParam(value="days", defaultValue="7") int days) {
        return this.dashboardService.getTopGMV(days);
    }

    @GetMapping(value={"/top-orders"})
    public List<InfluencerRankDto> getTopOrders(@RequestParam(value="days", defaultValue="7") int days) {
        return this.dashboardService.getTopOrders(days);
    }

    @GetMapping(value={"/stage-distribution"})
    public List<StageDistributionDto> getStageDistribution() {
        return this.dashboardService.getStageDistribution();
    }


    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
}

