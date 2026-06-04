package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.AdditionalAddressDto;
import com.athlunakms.influencer.entity.InfluencerAddress;
import com.athlunakms.influencer.repository.InfluencerAddressRepository;
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
@RequestMapping(value={"/v1/influencer/{influencerId}/addresses"})
public class InfluencerAddressController {
    private final InfluencerAddressRepository addressRepository;
    private final InfluencerLogService logService;

    @GetMapping
    public List<AdditionalAddressDto> getAddresses(@PathVariable(value="influencerId") Long influencerId) {
        return this.addressRepository.findByInfluencerId(influencerId).stream().map(arg_0 -> this.convert(arg_0)).collect(Collectors.toList());
    }

    @PostMapping
    public void addAddress(@PathVariable(value="influencerId") Long influencerId, @RequestBody AdditionalAddressDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        if (operator == null) {
            operator = "SYS";
        }
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            this.clearDefault(influencerId);
        }
        InfluencerAddress addr = new InfluencerAddress();
        addr.setInfluencerId(influencerId);
        this.updateEntity(addr, dto);
        this.addressRepository.save(addr);
        this.logService.logChange(influencerId, "添加收件地址", "-", dto.getAddress(), operator, "新增地址");
    }

    @PutMapping(value={"/{addressId}"})
    public void updateAddress(@PathVariable(value="influencerId") Long influencerId, @PathVariable(value="addressId") Long addressId, @RequestBody AdditionalAddressDto dto, @RequestHeader(value="X-User-Name", required=false) String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        this.addressRepository.findById(addressId).ifPresent(addr -> {
            if (Boolean.TRUE.equals(dto.getIsDefault())) {
                this.clearDefault(influencerId);
            }
            String oldAddr = addr.getAddress();
            this.updateEntity(addr, dto);
            this.addressRepository.save(addr);
            if (oldAddr == null || !oldAddr.equals(dto.getAddress())) {
                this.logService.logChange(influencerId, "修改收件地址", oldAddr != null ? oldAddr : "-", dto.getAddress(), finalOperator, "更新地址");
            }
        });
    }

    @DeleteMapping(value={"/{addressId}"})
    public void deleteAddress(@PathVariable(value="influencerId") Long influencerId, @PathVariable(value="addressId") Long addressId, @RequestHeader(value="X-User-Name", required=false) String operator) {
        String finalOperator = operator == null ? "SYS" : operator;
        this.addressRepository.findById(addressId).ifPresent(addr -> {
            this.logService.logChange(influencerId, "删除收件地址", addr.getAddress(), "-", finalOperator, "删除地址");
            this.addressRepository.deleteById(addressId);
        });
    }

    private void clearDefault(Long influencerId) {
        List<InfluencerAddress> defaults = this.addressRepository.findByInfluencerIdAndIsDefaultTrue(influencerId);
        for (InfluencerAddress addr : defaults) {
            addr.setIsDefault(false);
            this.addressRepository.save(addr);
        }
    }

    private void updateEntity(InfluencerAddress addr, AdditionalAddressDto dto) {
        addr.setRecipientName(dto.getRecipientName());
        addr.setPhone(dto.getPhone());
        addr.setEmail(dto.getEmail());
        addr.setAddress(dto.getAddress());
        addr.setCountry(dto.getCountry());
        addr.setPostalCode(dto.getPostalCode());
        if (dto.getIsDefault() != null) {
            addr.setIsDefault(dto.getIsDefault());
        }
    }

    private AdditionalAddressDto convert(InfluencerAddress entity) {
        AdditionalAddressDto dto = new AdditionalAddressDto();
        dto.setId(entity.getId());
        dto.setRecipientName(entity.getRecipientName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setCountry(entity.getCountry());
        dto.setPostalCode(entity.getPostalCode());
        dto.setIsDefault(entity.getIsDefault());
        return dto;
    }

    public InfluencerAddressController(InfluencerAddressRepository addressRepository, InfluencerLogService logService) {
        this.addressRepository = addressRepository;
        this.logService = logService;
    }
}

