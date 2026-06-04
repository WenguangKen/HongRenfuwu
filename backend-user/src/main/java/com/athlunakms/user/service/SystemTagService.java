package com.athlunakms.user.service;

import com.athlunakms.user.dto.SystemTagCreateDto;
import com.athlunakms.user.dto.SystemTagDto;
import com.athlunakms.user.entity.SystemTag;
import com.athlunakms.user.repository.SystemTagRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemTagService {
    private static final Logger log = LoggerFactory.getLogger(SystemTagService.class);
    private final SystemTagRepository tagRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<SystemTagDto> getTagsByCategory(String category) {
        List<SystemTag> tags = this.tagRepository.findByCategoryOrderBySortOrderAsc(category);
        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SystemTagDto> getEnabledTagsByCategory(String category) {
        List<SystemTag> tags = this.tagRepository.findByCategoryAndEnabledTrueOrderBySortOrderAsc(category);
        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SystemTagDto> getAllTags() {
        List<SystemTag> tags = this.tagRepository.findAllByOrderBySortOrderAsc();
        return tags.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public SystemTagDto createTag(SystemTagCreateDto dto) {
        if (this.tagRepository.existsByCategoryAndName(dto.getCategory(), dto.getName())) {
            throw new RuntimeException("\u6807\u7b7e\u540d\u79f0\u5df2\u5b58\u5728: " + dto.getName());
        }
        SystemTag tag = new SystemTag();
        tag.setCategory(dto.getCategory());
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        tag.setBackgroundColor(dto.getBackgroundColor() != null ? dto.getBackgroundColor() : "#e6f7ff");
        tag.setBorderColor(dto.getBorderColor() != null ? dto.getBorderColor() : "#91d5ff");
        tag.setTextColor(dto.getTextColor() != null ? dto.getTextColor() : "#1890ff");
        tag.setSortOrder(Integer.valueOf(dto.getSortOrder() != null ? dto.getSortOrder() : 0));
        tag.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        tag.setCreatedBy(this.getCurrentUsername());
        SystemTag saved = this.tagRepository.save(tag);
        log.info("Created tag: category={}, name={}", saved.getCategory(), saved.getName());
        return this.toDto(saved);
    }

    @Transactional
    public SystemTagDto updateTag(Long id, SystemTagCreateDto dto) {
        SystemTag tag = this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("\u6807\u7b7e\u4e0d\u5b58\u5728: " + id));
        if (!tag.getName().equals(dto.getName()) && this.tagRepository.existsByCategoryAndName(dto.getCategory(), dto.getName())) {
            throw new RuntimeException("\u6807\u7b7e\u540d\u79f0\u5df2\u5b58\u5728: " + dto.getName());
        }
        tag.setName(dto.getName());
        tag.setDescription(dto.getDescription());
        if (dto.getBackgroundColor() != null) {
            tag.setBackgroundColor(dto.getBackgroundColor());
        }
        if (dto.getBorderColor() != null) {
            tag.setBorderColor(dto.getBorderColor());
        }
        if (dto.getTextColor() != null) {
            tag.setTextColor(dto.getTextColor());
        }
        if (dto.getSortOrder() != null) {
            tag.setSortOrder(dto.getSortOrder());
        }
        if (dto.getEnabled() != null) {
            tag.setEnabled(dto.getEnabled());
        }
        SystemTag saved = this.tagRepository.save(tag);
        log.info("Updated tag: id={}, name={}", saved.getId(), saved.getName());
        return this.toDto(saved);
    }

    @Transactional
    public void deleteTag(Long id) {
        SystemTag tag = this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("\u6807\u7b7e\u4e0d\u5b58\u5728: " + id));
        this.tagRepository.delete(tag);
        log.info("Deleted tag: id={}, name={}", id, tag.getName());
    }

    public SystemTagDto getTag(Long id) {
        SystemTag tag = this.tagRepository.findById(id).orElseThrow(() -> new RuntimeException("\u6807\u7b7e\u4e0d\u5b58\u5728: " + id));
        return this.toDto(tag);
    }

    private SystemTagDto toDto(SystemTag tag) {
        SystemTagDto dto = new SystemTagDto();
        dto.setId(tag.getId());
        dto.setCategory(tag.getCategory());
        dto.setName(tag.getName());
        dto.setDescription(tag.getDescription());
        dto.setBackgroundColor(tag.getBackgroundColor());
        dto.setBorderColor(tag.getBorderColor());
        dto.setTextColor(tag.getTextColor());
        dto.setSortOrder(tag.getSortOrder());
        dto.setEnabled(tag.getEnabled());
        dto.setCreatedBy(tag.getCreatedBy());
        dto.setCreatedAt(tag.getCreatedAt() != null ? tag.getCreatedAt().format(FORMATTER) : null);
        dto.setUpdatedAt(tag.getUpdatedAt() != null ? tag.getUpdatedAt().format(FORMATTER) : null);
        dto.setCount(Long.valueOf(0L));
        return dto;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            return auth.getName();
        }
        return "System";
    }

    public SystemTagService(SystemTagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}

