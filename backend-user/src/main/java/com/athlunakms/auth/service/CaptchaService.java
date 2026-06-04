package com.athlunakms.auth.service;

import com.athlunakms.auth.dto.CaptchaResponse;
import com.athlunakms.auth.entity.CaptchaRecord;
import com.athlunakms.auth.repository.CaptchaRecordRepository;
import com.athlunakms.common.exception.BusinessException;
import com.athlunakms.common.exception.ErrorCode;
import com.athlunakms.common.util.IPUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaptchaService {
    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);
    private final CaptchaRecordRepository captchaRecordRepository;
    private final com.athlunakms.common.security.captcha.CaptchaService captchaUtil;

    @Transactional
    public CaptchaResponse generateCaptcha(HttpServletRequest request) {
        String captchaCode = this.captchaUtil.generateCaptchaCode();
        String captchaKey = this.captchaUtil.generateCaptchaKey();
        String captchaCodeHash = this.captchaUtil.hashCaptchaCode(captchaCode);
        String ipAddress = IPUtils.getClientIP(request);
        LocalDateTime expiresAt = this.captchaUtil.getExpireTime();
        CaptchaRecord record = new CaptchaRecord();
        record.setCaptchaKey(captchaKey);
        record.setCaptchaCodeHash(captchaCodeHash);
        record.setIpAddress(ipAddress);
        record.setExpiresAt(expiresAt);
        this.captchaRecordRepository.save(record);
        log.debug("\u751f\u6210\u9a8c\u8bc1\u7801: key={}, ip={}", captchaKey, ipAddress);
        CaptchaResponse resp = new CaptchaResponse(captchaKey);
        String imageBase64 = this.captchaUtil.generateCaptchaImageBase64(captchaCode);
        resp.setCaptchaImage(imageBase64);
        return resp;
    }

    @Transactional
    public void validateCaptcha(String captchaKey, String captchaCode, String ipAddress) {
        CaptchaRecord record = this.captchaRecordRepository.findByCaptchaKey(captchaKey).orElseThrow(() -> new BusinessException(ErrorCode.CAPTCHA_ERROR, "\u9a8c\u8bc1\u7801\u4e0d\u5b58\u5728"));
        if (record.getIsUsed().booleanValue()) {
            throw new BusinessException(ErrorCode.CAPTCHA_USED, "\u9a8c\u8bc1\u7801\u5df2\u4f7f\u7528");
        }
        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.CAPTCHA_EXPIRED, "\u9a8c\u8bc1\u7801\u5df2\u8fc7\u671f");
        }
        if (!record.getIpAddress().equals(ipAddress)) {
            log.warn("\u9a8c\u8bc1\u7801IP\u4e0d\u5339\u914d: key={}, expected={}, actual={}", captchaKey, record.getIpAddress(), ipAddress);
        }
        if (!this.captchaUtil.validateCaptchaFormat(captchaCode)) {
            record.setAttemptCount(Integer.valueOf(record.getAttemptCount() + 1));
            this.captchaRecordRepository.save(record);
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "\u9a8c\u8bc1\u7801\u683c\u5f0f\u4e0d\u6b63\u786e");
        }
        String submittedHash = this.captchaUtil.hashCaptchaCode(captchaCode);
        if (!record.getCaptchaCodeHash().equals(submittedHash)) {
            record.setAttemptCount(Integer.valueOf(record.getAttemptCount() + 1));
            this.captchaRecordRepository.save(record);
            throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "\u9a8c\u8bc1\u7801\u9519\u8bef");
        }
        record.setIsUsed(Boolean.valueOf(true));
        this.captchaRecordRepository.save(record);
        log.debug("\u9a8c\u8bc1\u7801\u9a8c\u8bc1\u6210\u529f: key={}", captchaKey);
    }

    public CaptchaService(CaptchaRecordRepository captchaRecordRepository, com.athlunakms.common.security.captcha.CaptchaService captchaUtil) {
        this.captchaRecordRepository = captchaRecordRepository;
        this.captchaUtil = captchaUtil;
    }
}

