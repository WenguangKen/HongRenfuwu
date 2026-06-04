package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.ContentCreateDto;
import com.athlunakms.influencer.dto.ContentDto;
import com.athlunakms.influencer.service.ContentService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value={"/v1/contents"})
public class ContentController {
    private static final Logger log = LoggerFactory.getLogger(ContentController.class);
    private final ContentService contentService;

    @GetMapping
    public ResponseEntity<Page<ContentDto>> getContents(
            @RequestParam(value="status", required=false) String status, 
            @RequestParam(value="influencerId", required=false) Long influencerId, 
            @RequestParam(value="influencerEmail", required=false) String influencerEmail, 
            @RequestParam(value="taskGroupId", required=false) String taskGroupId, 
            @RequestParam(value="orderNo", required=false) String orderNo, 
            @RequestParam(value="productSku", required=false) String productSku, 
            @RequestParam(value="contentType", required=false) String contentType,
            @RequestParam(value="isCommercial", required=false) Boolean isCommercial, 
            @RequestParam(value="defaultHandle", required=false) String defaultHandle, 
            @RequestParam(value="contactPersonName", required=false) String contactPersonName, 
            @RequestParam(value="owner", required=false) String owner,
            @RequestParam(value="tagIds", required=false) String tagIds,
            @RequestParam(value="startTime", required=false) String startTime,
            @RequestParam(value="endTime", required=false) String endTime,
            @RequestParam(value="publishStartDate", required=false) String publishStartDate,
            @RequestParam(value="publishEndDate", required=false) String publishEndDate,
            @RequestParam(value="viewsMin", required=false) Integer viewsMin,
            @RequestParam(value="viewsMax", required=false) Integer viewsMax,
            @RequestParam(value="likesMin", required=false) Integer likesMin,
            @RequestParam(value="likesMax", required=false) Integer likesMax,
            @RequestParam(value="commentsMin", required=false) Integer commentsMin,
            @RequestParam(value="commentsMax", required=false) Integer commentsMax,
            @RequestParam(value="savesMin", required=false) Integer savesMin,
            @RequestParam(value="savesMax", required=false) Integer savesMax,
            @RequestParam(value="sharesMin", required=false) Integer sharesMin,
            @RequestParam(value="sharesMax", required=false) Integer sharesMax,
            @RequestParam(value="erMin", required=false) BigDecimal erMin,
            @RequestParam(value="erMax", required=false) BigDecimal erMax,
            @RequestParam(value="influencerIds", required=false) String influencerIds,
            @RequestParam(value="influencerName", required=false) String influencerName,
            @RequestParam(value="page", defaultValue="0") int page, 
            @RequestParam(value="size", defaultValue="20") int size, 
            @RequestParam(value="sortBy", defaultValue="createdAt") String sortBy, 
            @RequestParam(value="sortDir", defaultValue="desc") String sortDir,
            @RequestParam(value="grouped", defaultValue="false") boolean grouped) {
        // 按任务ID分组分页（内容库使用）
        if (grouped) {
            Page<ContentDto> result = this.contentService.getGroupedContents(status, influencerId, influencerEmail, taskGroupId, orderNo, productSku, contentType, isCommercial, defaultHandle, contactPersonName, owner, tagIds, influencerIds, influencerName, startTime, endTime, publishStartDate, publishEndDate, viewsMin, viewsMax, likesMin, likesMax, commentsMin, commentsMax, savesMin, savesMax, sharesMin, sharesMax, erMin, erMax, page, size);
            return ResponseEntity.ok(result);
        }
        // 按记录分页（待上传等其它页面使用）
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<ContentDto> result = this.contentService.getContents(status, influencerId, influencerEmail, taskGroupId, orderNo, productSku, contentType, isCommercial, defaultHandle, contactPersonName, owner, tagIds, influencerIds, influencerName, startTime, endTime, publishStartDate, publishEndDate, viewsMin, viewsMax, likesMin, likesMax, commentsMin, commentsMax, savesMin, savesMax, sharesMin, sharesMax, erMin, erMax, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping(value={"/{id}"})
    public ResponseEntity<ContentDto> getContent(@PathVariable(value="id") Long id) {
        ContentDto content = this.contentService.getContent(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<ContentDto> createContent(@RequestBody ContentCreateDto dto) {
        ContentDto content = this.contentService.createContent(dto);
        return ResponseEntity.ok(content);
    }

    @PutMapping(value={"/{id}"})
    public ResponseEntity<ContentDto> updateContent(@PathVariable(value="id") Long id, @RequestBody ContentCreateDto dto) {
        ContentDto content = this.contentService.updateContent(id, dto);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping(value={"/{id}"})
    public ResponseEntity<?> deleteContent(@PathVariable(value="id") Long id) {
        this.contentService.deleteContent(id);
        return ResponseEntity.ok(Map.of("success", true, "message", "\u5220\u9664\u6210\u529f"));
    }

    @DeleteMapping(value={"/group/{taskGroupId}"})
    public ResponseEntity<?> deleteTaskGroup(@PathVariable(value="taskGroupId") String taskGroupId) {
        this.contentService.deleteByTaskGroupId(taskGroupId);
        return ResponseEntity.ok(Map.of("success", true, "message", "\u4efb\u52a1\u7ec4\u5220\u9664\u6210\u529f"));
    }

    @DeleteMapping(value={"/batch"})
    public ResponseEntity<?> batchDeleteContents(@RequestBody List<Long> ids) {
        this.contentService.batchDeleteContents(ids);
        return ResponseEntity.ok(Map.of("success", true, "message", "\u6279\u91cf\u5220\u9664\u6210\u529f"));
    }

    @PostMapping(value={"/batch-tags"})
    public ResponseEntity<?> batchUpdateTags(@RequestBody Map<String, Object> body) {
        Object idsObj = body.get("ids");
        Object tagIdsObj = body.get("tagIds");
        List<Long> ids = null;
        if (idsObj instanceof List) {
            ids = ((List<?>)idsObj).stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        }
        List<Long> tagIds = null;
        if (tagIdsObj instanceof List) {
            tagIds = ((List<?>)tagIdsObj).stream().map(o -> Long.valueOf(o.toString())).collect(Collectors.toList());
        }
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "未选择内容"));
        }
        this.contentService.batchUpdateTags(ids, tagIds);
        return ResponseEntity.ok(Map.of("success", true, "message", "批量更新标签成功"));
    }

    @GetMapping(value={"/storage/stats"})
    public ResponseEntity<?> getStorageStats() {
        return ResponseEntity.ok(this.contentService.getStorageStats());
    }

    @PostMapping(value={"/{id}/upload"})
    public ResponseEntity<ContentDto> uploadFile(
            @PathVariable(value="id") Long id, 
            @RequestPart(value="file") MultipartFile file,
            @RequestPart(value="thumbnail", required=false) MultipartFile thumbnail,
            @RequestParam(value="width", required=false) Integer width,
            @RequestParam(value="height", required=false) Integer height,
            @RequestParam(value="duration", required=false) Integer duration) {
        log.info("Uploading file for content: id={}, originalName={}, size={}", id, file.getOriginalFilename(), file.getSize());
        ContentDto content = this.contentService.uploadFile(id, file, thumbnail, width, height, duration);
        return ResponseEntity.ok(content);
    }

    @PostMapping(value={"/{id}/upload-chunk"})
    public ResponseEntity<Map<String, Object>> uploadChunk(
            @PathVariable(value="id") Long id,
            @RequestParam(value="uploadId") String uploadId,
            @RequestParam(value="chunkIndex") Integer chunkIndex,
            @RequestParam(value="totalChunks") Integer totalChunks,
            @RequestPart(value="chunk") MultipartFile chunk) {
        this.contentService.uploadChunk(id, uploadId, chunkIndex, totalChunks, chunk);
        return ResponseEntity.ok(Map.of("success", true, "message", "Chunk uploaded"));
    }

    @PostMapping(value={"/{id}/upload-merge"})
    public ResponseEntity<ContentDto> mergeChunks(
            @PathVariable(value="id") Long id,
            @RequestParam(value="uploadId") String uploadId,
            @RequestParam(value="fileName") String fileName,
            @RequestParam(value="fileType", required=false) String fileType,
            @RequestParam(value="totalSize", required=false) Long totalSize,
            @RequestPart(value="thumbnail", required=false) MultipartFile thumbnail,
            @RequestParam(value="width", required=false) Integer width,
            @RequestParam(value="height", required=false) Integer height,
            @RequestParam(value="duration", required=false) Integer duration) {
        ContentDto content = this.contentService.mergeChunks(id, uploadId, fileName, fileType, totalSize, thumbnail, width, height, duration);
        return ResponseEntity.ok(content);
    }

    @PutMapping(value={"/{id}/rename"})
    public ResponseEntity<ContentDto> renameFile(@PathVariable(value="id") Long id, @RequestBody Map<String, String> body) {
        String newFileName = body.get("fileName");
        if (newFileName == null || newFileName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ContentDto content = this.contentService.renameFile(id, newFileName.trim());
        return ResponseEntity.ok(content);
    }

    @PutMapping(value={"/{id}/social-metrics"})
    public ResponseEntity<ContentDto> updateSocialMetrics(@PathVariable(value="id") Long id, @RequestBody Map<String, Integer> metrics) {
        ContentDto content = this.contentService.updateSocialMetrics(id, metrics);
        return ResponseEntity.ok(content);
    }

    @PostMapping(value={"/{id}/review"})
    public ResponseEntity<ContentDto> reviewContent(@PathVariable(value="id") Long id, @RequestBody Map<String, Object> body) {
        Boolean approved = (Boolean)body.get("approved");
        String note = (String)body.get("note");
        Long reviewerId = body.get("reviewerId") != null ? Long.valueOf(body.get("reviewerId").toString()) : null;
        if (approved == null) {
            return ResponseEntity.badRequest().build();
        }
        ContentDto content = this.contentService.reviewContent(id, approved, note, reviewerId);
        return ResponseEntity.ok(content);
    }

    @GetMapping(value={"/{id}/preview-url"})
    public ResponseEntity<?> getPreviewUrl(@PathVariable(value="id") Long id) {
        String url = this.contentService.getFileUrl(id);
        return ResponseEntity.ok(Map.of("url", url != null ? url : ""));
    }

    @GetMapping(value={"/{id}/thumbnail-url"})
    public ResponseEntity<?> getThumbnailUrl(@PathVariable(value="id") Long id) {
        String url = this.contentService.getThumbnailUrl(id);
        return ResponseEntity.ok(Map.of("url", url != null ? url : ""));
    }

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }
}

