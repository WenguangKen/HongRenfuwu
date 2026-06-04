package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.SocialMediaDto;
import com.athlunakms.influencer.entity.SocialMedia;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.SocialMediaRepository;
import com.athlunakms.influencer.service.InfluencerLogService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer"})
public class InfluencerSocialMediaController {
    private final SocialMediaRepository socialMediaRepository;
    private final InfluencerRepository influencerRepository;
    private final InfluencerLogService logService;

    @GetMapping(value={"/{influencerId}/social-medias"})
    public List<SocialMediaDto> getSocialMedias(@PathVariable(value="influencerId") Long influencerId) {
        return this.socialMediaRepository.findByInfluencerId(influencerId).stream().map(arg_0 -> this.convert(arg_0)).collect(Collectors.toList());
    }

    @PostMapping(value={"/{influencerId}/social-medias"})
    public void addSocialMedia(@PathVariable(value="influencerId") Long influencerId, @RequestBody SocialMediaDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            this.clearDefault(influencerId);
        }
        SocialMedia sm = new SocialMedia();
        sm.setInfluencerId(influencerId);
        this.updateEntity(sm, dto);
        SocialMedia savedSm = this.socialMediaRepository.save(sm);
        this.logService.logChange(influencerId, "添加社媒信息", "-", dto.getPlatform() + " - " + dto.getHandle(), finalOperator, "新增社媒");
        if (Boolean.TRUE.equals(savedSm.getIsDefault())) {
            this.syncInfluencerFans(influencerId, savedSm);
        }
    }

    @PutMapping(value={"/social-medias/{id}"})
    public void updateSocialMedia(@PathVariable(value="id") Long id, @RequestBody SocialMediaDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        this.socialMediaRepository.findById(id).ifPresent(sm -> {
            boolean wasDefault = Boolean.TRUE.equals(sm.getIsDefault());
            boolean isDefault = Boolean.TRUE.equals(dto.getIsDefault());
            if (isDefault && !wasDefault) {
                this.clearDefault(sm.getInfluencerId());
            }
            String oldInfo = sm.getPlatform() + " - " + sm.getHandle();
            Long oldFollowers = sm.getFollowerCount();
            this.updateEntity(sm, dto);
            SocialMedia savedSm = this.socialMediaRepository.save(sm);
            String newInfo = savedSm.getPlatform() + " - " + savedSm.getHandle();
            if (!oldInfo.equals(newInfo) || oldFollowers != null && !oldFollowers.equals(dto.getFollowerCount())) {
                this.logService.logChange(sm.getInfluencerId(), "修改社媒信息", oldInfo, newInfo + " (粉丝数: " + dto.getFollowerCount() + ")", finalOperator, "更新社媒");
            }
            if (isDefault) {
                this.syncInfluencerFans(sm.getInfluencerId(), savedSm);
            }
        });
    }

    @DeleteMapping(value={"/social-medias/{id}"})
    public void deleteSocialMedia(@PathVariable(value="id") Long id, @RequestHeader(value="X-User-Name", required=false) String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        this.socialMediaRepository.findById(id).ifPresent(sm -> {
            this.logService.logChange(sm.getInfluencerId(), "删除社媒信息", sm.getPlatform() + " - " + sm.getHandle(), "-", finalOperator, "删除社媒");
            this.socialMediaRepository.deleteById(id);
        });
    }

    private void clearDefault(Long influencerId) {
        List<SocialMedia> defaults = this.socialMediaRepository.findByInfluencerId(influencerId).stream().filter(sm -> Boolean.TRUE.equals(sm.getIsDefault())).collect(Collectors.toList());
        for (SocialMedia sm2 : defaults) {
            sm2.setIsDefault(false);
            this.socialMediaRepository.save(sm2);
        }
    }

    private void syncInfluencerFans(Long influencerId, SocialMedia sm) {
        this.influencerRepository.findById(influencerId).ifPresent(influencer -> {
            influencer.setDefaultSocialId(sm.getId());
            influencer.setDefaultPlatform(sm.getPlatform());
            influencer.setDefaultHandle(sm.getHandle());
            influencer.setDefaultUrl(sm.getUrl());
            influencer.setTotalFans(sm.getFollowerCount());
            this.influencerRepository.save(influencer);
        });
    }

    private void updateEntity(SocialMedia sm, SocialMediaDto dto) {
        sm.setPlatform(dto.getPlatform());
        sm.setHandle(dto.getHandle());
        sm.setUrl(dto.getUrl());
        sm.setFollowerCount(dto.getFollowerCount());
        if (dto.getIsDefault() != null) {
            sm.setIsDefault(dto.getIsDefault());
        }
    }

    private SocialMediaDto convert(SocialMedia entity) {
        SocialMediaDto dto = new SocialMediaDto();
        dto.setId(entity.getId());
        dto.setPlatform(entity.getPlatform());
        dto.setHandle(entity.getHandle());
        dto.setUrl(entity.getUrl());
        dto.setFollowerCount(entity.getFollowerCount());
        dto.setIsDefault(entity.getIsDefault());
        return dto;
    }

    public InfluencerSocialMediaController(SocialMediaRepository socialMediaRepository, InfluencerRepository influencerRepository, InfluencerLogService logService) {
        this.socialMediaRepository = socialMediaRepository;
        this.influencerRepository = influencerRepository;
        this.logService = logService;
    }
}

