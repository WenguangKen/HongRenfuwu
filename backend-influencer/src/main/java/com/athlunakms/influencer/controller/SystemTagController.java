package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.ApiResponse;
import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.service.SystemTagService;
import com.athlunakms.influencer.service.TagRepairService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统标签管理控制器
 *
 * 提供标签的 CRUD 操作及修复功能。
 */
@RestController
@RequestMapping(value = {"/v1/influencers/tags"})
public class SystemTagController {
    private static final Logger log = LoggerFactory.getLogger(SystemTagController.class);
    private final SystemTagService systemTagService;
    private final TagRepairService tagRepairService;

    public SystemTagController(SystemTagService systemTagService, TagRepairService tagRepairService) {
        this.systemTagService = systemTagService;
        this.tagRepairService = tagRepairService;
    }

    /**
     * 执行标签修复任务（补全缺失标签）
     */
    @PostMapping(value = {"/repair"})
    public ApiResponse<String> repairTags() {
        try {
            this.tagRepairService.repairMissingTags();
            log.info("标签修复任务执行完成");
            return ApiResponse.success("标签修复完成");
        } catch (Exception e) {
            log.error("标签修复失败", e);
            return ApiResponse.error("标签修复失败: " + e.getMessage());
        }
    }

    /**
     * 按类型查询标签列表
     *
     * @param type 标签类型（category）
     */
    @GetMapping
    public ApiResponse<List<SystemTag>> getTags(@RequestParam(value = "type") String type) {
        if (!StringUtils.hasText(type)) {
            return ApiResponse.error("标签类型(type)不能为空");
        }
        try {
            List<SystemTag> tags = this.systemTagService.getTagsByCategory(type);
            return ApiResponse.success(tags);
        } catch (Exception e) {
            log.error("查询标签失败: type={}", type, e);
            return ApiResponse.error("查询标签失败: " + e.getMessage());
        }
    }

    /**
     * 创建标签
     *
     * @param tag 标签信息（必须包含 category 和 name）
     */
    @PostMapping
    public ApiResponse<SystemTag> createTag(@RequestBody SystemTag tag) {
        if (tag == null || !StringUtils.hasText(tag.getCategory()) || !StringUtils.hasText(tag.getName())) {
            return ApiResponse.error("标签分类(category)和名称(name)不能为空");
        }
        try {
            SystemTag created = this.systemTagService.createTag(tag);
            log.info("创建标签成功: id={}, category={}, name={}", created.getId(), created.getCategory(), created.getName());
            return ApiResponse.success(created);
        } catch (Exception e) {
            log.error("创建标签失败: category={}, name={}", tag.getCategory(), tag.getName(), e);
            return ApiResponse.error("创建标签失败: " + e.getMessage());
        }
    }

    /**
     * 更新标签
     *
     * @param id  标签 ID
     * @param tag 更新内容
     */
    @PutMapping(value = {"/{id}"})
    public ApiResponse<SystemTag> updateTag(@PathVariable(value = "id") Long id, @RequestBody SystemTag tag) {
        if (tag == null) {
            return ApiResponse.error("更新的标签信息不能为空");
        }
        try {
            SystemTag updated = this.systemTagService.updateTag(id, tag);
            log.info("更新标签成功: id={}, category={}, name={}", updated.getId(), updated.getCategory(), updated.getName());
            return ApiResponse.success(updated);
        } catch (Exception e) {
            log.error("更新标签失败: id={}", id, e);
            return ApiResponse.error("更新标签失败: " + e.getMessage());
        }
    }

    /**
     * 删除标签
     *
     * @param id 标签 ID
     */
    @DeleteMapping(value = {"/{id}"})
    public ApiResponse<String> deleteTag(@PathVariable(value = "id") Long id) {
        try {
            this.systemTagService.deleteTag(id);
            log.info("删除标签成功: id={}", id);
            return ApiResponse.success("删除标签成功");
        } catch (Exception e) {
            log.error("删除标签失败: id={}", id, e);
            return ApiResponse.error("删除标签失败: " + e.getMessage());
        }
    }
}
