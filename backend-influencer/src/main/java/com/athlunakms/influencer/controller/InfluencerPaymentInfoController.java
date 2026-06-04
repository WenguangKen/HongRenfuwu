package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.InfluencerPaymentInfoDto;
import com.athlunakms.influencer.entity.InfluencerPaymentInfo;
import com.athlunakms.influencer.repository.InfluencerPaymentInfoRepository;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer"})
public class InfluencerPaymentInfoController {
    private final InfluencerPaymentInfoRepository paymentInfoRepository;

    @GetMapping(value={"/{influencerId}/payment-info"})
    public ResponseEntity<InfluencerPaymentInfoDto> getPaymentInfo(@PathVariable("influencerId") Long influencerId) {
        Optional<InfluencerPaymentInfo> infoOpt = this.paymentInfoRepository.findByInfluencerId(influencerId);
        if (infoOpt.isPresent()) {
            InfluencerPaymentInfoDto dto = new InfluencerPaymentInfoDto();
            BeanUtils.copyProperties(infoOpt.get(), dto);
            return ResponseEntity.ok(dto);
        }
        InfluencerPaymentInfoDto emptyDto = new InfluencerPaymentInfoDto();
        emptyDto.setInfluencerId(influencerId);
        return ResponseEntity.ok(emptyDto);
    }

    @PostMapping(value={"/{influencerId}/payment-info"})
    public ResponseEntity<InfluencerPaymentInfoDto> savePaymentInfo(@PathVariable("influencerId") Long influencerId, @RequestBody InfluencerPaymentInfoDto dto) {
        InfluencerPaymentInfo entity = this.paymentInfoRepository.findByInfluencerId(influencerId).orElse(new InfluencerPaymentInfo());
        entity.setInfluencerId(influencerId);
        entity.setBankCountry(dto.getBankCountry());
        entity.setBankName(dto.getBankName());
        entity.setBranchName(dto.getBranchName());
        entity.setBankAddress(dto.getBankAddress());
        entity.setSwiftCode(dto.getSwiftCode());
        entity.setRoutingNumber(dto.getRoutingNumber());
        entity.setAccountName(dto.getAccountName());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setBeneficiaryAddress(dto.getBeneficiaryAddress());
        entity.setPaypalAccount(dto.getPaypalAccount());
        entity = this.paymentInfoRepository.save(entity);
        InfluencerPaymentInfoDto resultDto = new InfluencerPaymentInfoDto();
        BeanUtils.copyProperties(entity, resultDto);
        return ResponseEntity.ok(resultDto);
    }

    public InfluencerPaymentInfoController(InfluencerPaymentInfoRepository paymentInfoRepository) {
        this.paymentInfoRepository = paymentInfoRepository;
    }
}

