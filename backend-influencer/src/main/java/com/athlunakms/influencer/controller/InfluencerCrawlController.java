package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.ApiResponse;
import com.athlunakms.influencer.entity.InfluencerCrawlTask;
import com.athlunakms.influencer.service.InfluencerCrawlService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/v1/influencer/crawl"})
public class InfluencerCrawlController {

    @Autowired
    private InfluencerCrawlService crawlService;

    @GetMapping(value = {"/tasks"})
    public ApiResponse<List<InfluencerCrawlTask>> getAllTasks() {
        List<InfluencerCrawlTask> tasks = crawlService.getAllTasks();
        return ApiResponse.success(tasks);
    }

    @PostMapping(value = {"/tasks"})
    public ApiResponse<InfluencerCrawlTask> createTask(@RequestBody InfluencerCrawlTask task) {
        InfluencerCrawlTask created = crawlService.createTask(task);
        return ApiResponse.success(created);
    }

    @DeleteMapping(value = {"/tasks/{id}"})
    public ApiResponse<Void> deleteTask(@PathVariable Long id) {
        crawlService.deleteTask(id);
        return ApiResponse.success(null);
    }

    @PostMapping(value = {"/tasks/{id}/start"})
    public ApiResponse<Void> startCrawlTask(@PathVariable Long id) {
        crawlService.startCrawlTask(id);
        return ApiResponse.success(null);
    }
}
