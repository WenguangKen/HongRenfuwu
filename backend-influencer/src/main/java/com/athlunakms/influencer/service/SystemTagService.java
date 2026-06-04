package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.SystemTag;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.SystemTagRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemTagService {
    private final SystemTagRepository systemTagRepository;
    private final InfluencerRepository influencerRepository;

    public List<SystemTag> getTagsByCategory(String category) {
        List<SystemTag> tags = this.systemTagRepository.findByCategoryAndEnabledTrue(category, Sort.by(Sort.Direction.ASC, "sortOrder"));
        for (SystemTag tag : tags) {
            try {
                String pattern = "%" + tag.getId() + "%";
                long count = this.influencerRepository.countByTagsContaining(pattern);
                tag.setUsageCount((int)count);
            } catch (Exception e) {
                // 统计失败不影响标签列表展示
                tag.setUsageCount(0);
            }
        }
        return tags;
    }

    @Transactional
    public SystemTag createTag(SystemTag tag) {
        if (this.systemTagRepository.existsByCategoryAndName(tag.getCategory(), tag.getName())) {
            throw new RuntimeException("Tag with same name and category already exists");
        }
        tag.setSortOrder(tag.getSortOrder() != null ? tag.getSortOrder() : 0);
        tag.setEnabled(tag.getEnabled() != null ? tag.getEnabled() : true);
        return this.systemTagRepository.save(tag);
    }

    @Transactional
    public SystemTag updateTag(Long id, SystemTag tagDetails) {
        SystemTag tag = this.systemTagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setName(tagDetails.getName());
        tag.setDescription(tagDetails.getDescription());
        tag.setBackgroundColor(tagDetails.getBackgroundColor());
        tag.setBorderColor(tagDetails.getBorderColor());
        tag.setTextColor(tagDetails.getTextColor());
        tag.setSortOrder(tagDetails.getSortOrder() != null ? tagDetails.getSortOrder() : tag.getSortOrder());
        tag.setEnabled(tagDetails.getEnabled() != null ? tagDetails.getEnabled() : tag.getEnabled());
        tag.setUpdatedBy(tagDetails.getUpdatedBy());
        return this.systemTagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        this.systemTagRepository.deleteById(id);
    }

    public SystemTagService(SystemTagRepository systemTagRepository, InfluencerRepository influencerRepository) {
        this.systemTagRepository = systemTagRepository;
        this.influencerRepository = influencerRepository;
    }
}

