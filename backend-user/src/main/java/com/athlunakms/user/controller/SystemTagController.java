package com.athlunakms.user.controller;

import com.athlunakms.user.dto.SystemTagCreateDto;
import com.athlunakms.user.dto.SystemTagDto;
import com.athlunakms.user.service.SystemTagService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/v1.0/tags"})
public class SystemTagController {
    private static final Logger log = LoggerFactory.getLogger(SystemTagController.class);
    private final SystemTagService tagService;

    @GetMapping
    public ResponseEntity<List<SystemTagDto>> getTagsByCategory(@RequestParam(value="category", required=false) String category, @RequestParam(value="enabledOnly", required=false, defaultValue="false") Boolean enabledOnly) {
        List<SystemTagDto> tags = category == null || category.isEmpty() ? this.tagService.getAllTags() : (Boolean.TRUE.equals(enabledOnly) ? this.tagService.getEnabledTagsByCategory(category) : this.tagService.getTagsByCategory(category));
        return ResponseEntity.ok(tags);
    }

    @GetMapping(value={"/{id}"})
    public ResponseEntity<SystemTagDto> getTag(@PathVariable(value="id") Long id) {
        return ResponseEntity.ok(this.tagService.getTag(id));
    }

    @PostMapping
    public ResponseEntity<SystemTagDto> createTag(@RequestBody SystemTagCreateDto dto) {
        return ResponseEntity.ok(this.tagService.createTag(dto));
    }

    @PutMapping(value={"/{id}"})
    public ResponseEntity<SystemTagDto> updateTag(@PathVariable(value="id") Long id, @RequestBody SystemTagCreateDto dto) {
        return ResponseEntity.ok(this.tagService.updateTag(id, dto));
    }

    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<Void> deleteTag(@PathVariable(value="id") Long id) {
        this.tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    public SystemTagController(SystemTagService tagService) {
        this.tagService = tagService;
    }
}

