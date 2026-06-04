package com.athlunakms.influencer.controller;

import com.athlunakms.influencer.dto.PaymentRecordDto;
import com.athlunakms.influencer.entity.InfluencerPayment;
import com.athlunakms.influencer.repository.InfluencerPaymentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/v1/influencer/{influencerId}/payments"})
public class InfluencerPaymentController {
    private final InfluencerPaymentRepository paymentRepository;

    @GetMapping
    public List<PaymentRecordDto> getPayments(@PathVariable(value="influencerId") Long influencerId) {
        return this.paymentRepository.findByInfluencerIdOrderByCreatedAtDesc(influencerId).stream().map(arg_0 -> this.convert(arg_0)).collect(Collectors.toList());
    }

    private PaymentRecordDto convert(InfluencerPayment entity) {
        PaymentRecordDto dto = new PaymentRecordDto();
        dto.setId(entity.getId());
        dto.setPaymentNo(entity.getPaymentNo());
        dto.setTransactionId(entity.getTransactionId());
        dto.setAmount(entity.getAmount());
        dto.setCurrency(entity.getCurrency());
        dto.setStatus(entity.getStatus());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setReceiverAccount(entity.getReceiverAccount());
        dto.setBatchId(entity.getBatchId());
        dto.setPayer(entity.getPayer());
        dto.setPaidAt(entity.getPaidAt());
        dto.setRemark(entity.getRemark());
        return dto;
    }

    public InfluencerPaymentController(InfluencerPaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}

