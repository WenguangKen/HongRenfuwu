/**
 * 红人管理 REST 控制器
 *
 * API 前缀: /v1/influencer
 *
 * 提供红人管理的核心 REST API包括
 * - 红人 CRUD创建/查询/更新/删除
 * - 批量状态变更工作流
 * - 批量导入/更新
 * - 休眠检测
 * - 操作日志查询
 */
package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.BatchUpdateDto;
import com.athlunakms.influencer.dto.BatchWorkflowDto;
import com.athlunakms.influencer.dto.BatchWorkflowResultDto;
import com.athlunakms.influencer.dto.InfluencerCreateDto;
import com.athlunakms.influencer.dto.InfluencerFilterDto;
import com.athlunakms.influencer.dto.InfluencerImportDto;
import com.athlunakms.influencer.dto.InfluencerListDto;
import com.athlunakms.influencer.dto.InfluencerUpdateDto;
import com.athlunakms.influencer.entity.InfluencerChangeLog;
import com.athlunakms.influencer.service.DormancyService;
import com.athlunakms.influencer.service.InfluencerLogService;
import com.athlunakms.influencer.service.InfluencerService;
import com.athlunakms.influencer.service.InfluencerWorkflowService;
import com.athlunakms.influencer.service.InfluencerImportService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer"})
public class InfluencerController {
    private final InfluencerService influencerService;
    private final InfluencerWorkflowService workflowService;
    private final DormancyService dormancyService;
    private final InfluencerLogService logService;
    private final InfluencerImportService importService;

    /** POST /v1/influencer — 创建红人 */
    @PostMapping
    public Long createInfluencer(@RequestBody InfluencerCreateDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        Long id = this.influencerService.createInfluencer(dto, operator);
        InfluencerSseController.broadcastChange("created", id);
        return id;
    }

    /** PUT /v1/influencer — 更新红人信息 */
    @PutMapping
    public void updateInfluencer(@RequestBody InfluencerUpdateDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        this.influencerService.updateInfluencer(dto, operator);
        InfluencerSseController.broadcastChange("updated", dto.getId());
    }

    /** GET /v1/influencer/{id}/logs — 获取红人操作日志 */
    @GetMapping(value={"/{id}/logs"})
    public List<InfluencerChangeLog> getLogs(@PathVariable(value="id") Long id) {
        return this.logService.getLogs(id);
    }

    /** GET /v1/influencer — 分页查询红人列表支持多条件筛选 */
    @GetMapping
    public Page<InfluencerListDto> getList(InfluencerFilterDto filter) {
        return this.influencerService.getList(filter);
    }

    /** POST /v1/influencer/search — 批量搜索红人列表 (支持大容量筛选) */
    @PostMapping("/search")
    public Page<InfluencerListDto> searchList(@RequestBody InfluencerFilterDto filter) {
        return this.influencerService.getList(filter);
    }

    /** GET /v1/influencer/status-counts — 获取受筛选条件影响的各状态统计数量 */
    @GetMapping(value={"/status-counts"})
    public Map<String, Long> getStatusCounts(InfluencerFilterDto filter) {
        return this.influencerService.getStatusCounts(filter);
    }

    /** POST /v1/influencer/status-counts/search — 批量查询状态统计 (支持大容量筛选) */
    @PostMapping("/status-counts/search")
    public Map<String, Long> getStatusCountsPost(@RequestBody InfluencerFilterDto filter) {
        return this.influencerService.getStatusCounts(filter);
    }

    /** POST /v1/influencer/search-with-counts — 合并查询：列表数据 + 状态统计 (减少一次查询) */
    @PostMapping("/search-with-counts")
    public Map<String, Object> searchWithCounts(@RequestBody InfluencerFilterDto filter) {
        Map<String, Object> result = new HashMap<>();
        result.put("list", this.influencerService.getList(filter));
        result.put("statusCounts", this.influencerService.getStatusCounts(filter));
        return result;
    }

    /** GET /v1/influencer/{id} — 获取红人详情含标签/统计/社媒信息 */
    @GetMapping(value={"/{id}"})
    public Map<String, Object> getDetail(@PathVariable(value="id") Long id) {
        return this.influencerService.getDetailWithTags(id);
    }

    /** POST /v1/influencer/workflow/batch-status-change — 批量状态变更 */
    @PostMapping(value={"/workflow/batch-status-change"})
    public BatchWorkflowResultDto batchStatusChange(@RequestBody BatchWorkflowDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        BatchWorkflowResultDto result = this.workflowService.batchChangeStatus(dto, operator);
        InfluencerSseController.broadcastChange("batch-status", 0L);
        return result;
    }

    /** GET /v1/influencer/{id}/dormancy-check — 检查红人是否满足休眠条件 */
    @GetMapping(value={"/{id}/dormancy-check"})
    public DormancyService.DormancyCheckResult checkDormancy(@PathVariable(value="id") Long id) {
        return this.dormancyService.checkDormancyEligibility(id);
    }

    /** POST /v1/influencer/{id}/update-activity — 更新红人活动时间戳 */
    @PostMapping(value={"/{id}/update-activity"})
    public void updateActivity(@PathVariable(value="id") Long id, @RequestParam(value="activityType") String activityType) {
        this.influencerService.updateActivityTimestamp(id, activityType);
    }

    /** POST /v1/influencer/batch-import — 批量导入红人 */
    @PostMapping(value={"/batch-import"})
    public int batchImport(@RequestBody List<InfluencerImportDto> list) {
        return this.influencerService.batchImport(list);
    }

    /** POST /v1/influencer/batch-resolve-ids — 通过关键词批量查找红人 ID */
    @PostMapping(value={"/batch-resolve-ids"})
    public List<Long> batchResolveIds(@RequestBody List<String> keywords) {
        return this.influencerService.findIdsByKeywords(keywords);
    }

    /** POST /v1/influencer/batch-update — 批量更新红人字段 */
    @PostMapping(value={"/batch-update"})
    public void batchUpdate(@RequestBody BatchUpdateDto dto,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        this.influencerService.batchUpdate(dto, operator);
        InfluencerSseController.broadcastChange("batch-updated", 0L);
    }

    /** DELETE /v1/influencer/{id} — 删除单个红人 */
    @DeleteMapping(value={"/{id}"})
    public void deleteInfluencer(@PathVariable(value="id") Long id) {
        this.influencerService.deleteInfluencer(id);
        InfluencerSseController.broadcastChange("deleted", id);
    }



    /** DELETE /v1/influencer/batch — 批量删除红人 */
    @DeleteMapping(value={"/batch"})
    public void batchDelete(@RequestBody List<Long> ids) {
        this.influencerService.batchDelete(ids);
        InfluencerSseController.broadcastChange("batch-deleted", 0L);
    }

    /** POST /v1/influencer/repair-history — 修复历史数据的 Handle 字段显示 */
    @PostMapping("/repair-history")
    public int repairHistory() {
        return this.importService.repairHistoryHandles();
    }

    public InfluencerController(InfluencerService influencerService, 
                              InfluencerWorkflowService workflowService, 
                              DormancyService dormancyService, 
                              InfluencerLogService logService,
                              InfluencerImportService importService) {
        this.influencerService = influencerService;
        this.workflowService = workflowService;
        this.dormancyService = dormancyService;
        this.logService = logService;
        this.importService = importService;
    }
}

