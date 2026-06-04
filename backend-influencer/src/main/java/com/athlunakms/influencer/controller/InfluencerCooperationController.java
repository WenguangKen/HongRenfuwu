package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.entity.InfluencerCooperation;
import com.athlunakms.influencer.service.InfluencerCooperationService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer"})
public class InfluencerCooperationController {
    private final InfluencerCooperationService cooperationService;

    @GetMapping(value={"/{influencerId}/cooperations"})
    public List<InfluencerCooperation> getCooperations(@PathVariable(value="influencerId") Long influencerId) {
        return this.cooperationService.getCooperations(influencerId);
    }

    @PostMapping(value={"/{influencerId}/cooperations"})
    public InfluencerCooperation createCooperation(@PathVariable(value="influencerId") Long influencerId, @RequestBody InfluencerCooperation cooperation) {
        cooperation.setInfluencerId(influencerId);
        return this.cooperationService.createCooperation(cooperation);
    }

    @PutMapping(value={"/cooperations/{id}"})
    public InfluencerCooperation updateCooperation(@PathVariable(value="id") Long id, @RequestBody InfluencerCooperation cooperation) {
        return this.cooperationService.updateCooperation(id, cooperation);
    }

    @DeleteMapping(value={"/cooperations/{id}"})
    public void deleteCooperation(@PathVariable(value="id") Long id) {
        this.cooperationService.deleteCooperation(id);
    }

    @PostMapping(value={"/sync-all-paid-status"})
    public void syncAllPaidStatus() {
        this.cooperationService.syncAllPaidStatus();
    }

    @DeleteMapping(value={"/cooperations/cleanup/zero-amount"})
    public void cleanupZeroAmount() {
        this.cooperationService.cleanupZeroAmountCooperations();
    }

    public InfluencerCooperationController(InfluencerCooperationService cooperationService) {
        this.cooperationService = cooperationService;
    }
}

