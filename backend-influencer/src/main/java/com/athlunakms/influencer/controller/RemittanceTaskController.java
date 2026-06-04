package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.*;
import com.athlunakms.influencer.service.RemittanceTaskService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/finance/remittance")
@RequiredArgsConstructor
public class RemittanceTaskController {

    private final RemittanceTaskService remittanceTaskService;

    @PostMapping
    public RemittanceTaskDto create(
            @RequestBody RemittanceCreateDto createDto,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        return remittanceTaskService.createRemittanceTask(createDto, operator);
    }

    @GetMapping
    public Page<RemittanceTaskDto> getList(RemittanceFilterDto filter) {
        return remittanceTaskService.getRemittanceTasks(filter);
    }

    @PostMapping("/search")
    public Page<RemittanceTaskDto> searchList(@RequestBody RemittanceFilterDto filter) {
        return remittanceTaskService.getRemittanceTasks(filter);
    }

    @GetMapping("/status-counts")
    public Map<String, Long> getStatusCounts(RemittanceFilterDto filter) {
        return remittanceTaskService.getStatusCounts(filter);
    }

    @PostMapping("/status-counts/search")
    public Map<String, Long> getStatusCountsPost(@RequestBody RemittanceFilterDto filter) {
        return remittanceTaskService.getStatusCounts(filter);
    }

    @GetMapping("/{id}")
    public RemittanceTaskDto getDetail(@PathVariable("id") Long id) {
        return remittanceTaskService.getDetail(id);
    }

    @PostMapping("/{id}/audit")
    public RemittanceTaskDto audit(
            @PathVariable("id") Long id,
            @RequestBody RemittanceAuditDto auditDto,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        return remittanceTaskService.auditTask(id, auditDto, null, operator);
    }

    @PutMapping("/{id}")
    public RemittanceTaskDto update(
            @PathVariable("id") Long id,
            @RequestBody RemittanceCreateDto updateDto,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        return remittanceTaskService.updateRemittanceTask(id, updateDto, operator);
    }

    @PostMapping("/{id}/pay")
    public RemittanceTaskDto pay(
            @PathVariable("id") Long id,
            @RequestBody RemittancePayDto payDto,
            @RequestHeader(value = "X-User-Name", required = false) String operator) {
        return remittanceTaskService.payTask(id, payDto, null, operator);
    }
}
