package com.athlunakms.ai.controller;

import com.athlunakms.ai.dto.mail.MailIntentAnalyzeRequest;
import com.athlunakms.ai.dto.mail.MailIntentAnalyzeResponse;
import com.athlunakms.ai.dto.mail.MailPersonalizeRequest;
import com.athlunakms.ai.dto.mail.MailPersonalizeResponse;
import com.athlunakms.ai.dto.mail.MailTemplateAiRequest;
import com.athlunakms.ai.dto.mail.MailTemplateRefineRequest;
import com.athlunakms.ai.dto.mail.MailTemplateAiResponse;
import com.athlunakms.ai.service.MailAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/internal/api/mail")
@RequiredArgsConstructor
public class InternalMailAiController {

    private final MailAiService mailAiService;
    private final ObjectMapper objectMapper;

    /**
     * 接收 Map 以兼容 backend-mail 发送的 Map&lt;String,Object&gt; 请求体，
     * 手动转换为 MailTemplateAiRequest 避免嵌套类型反序列化 400 问题。
     */
    @PostMapping("/generate-template")
    public MailTemplateAiResponse generateTemplate(@RequestBody Map<String, Object> body) {
        log.info("generate-template received keys={} brand={} commissionRate={} language={} sampleProducts={}",
                body == null ? null : body.keySet(),
                body == null ? null : body.get("brand"),
                body == null ? null : body.get("commissionRate"),
                body == null ? null : body.get("language"),
                body == null ? null : body.get("sampleProducts"));
        MailTemplateAiRequest request;
        try {
            request = objectMapper.convertValue(body, MailTemplateAiRequest.class);
        } catch (IllegalArgumentException e) {
            log.warn("convertValue MailTemplateAiRequest failed: {}", e.getMessage());
            throw new IllegalArgumentException(
                    "邮件模版请求体字段不匹配：" + e.getMessage(), e);
        }
        return mailAiService.generateTemplate(request);
    }

    @PostMapping("/refine-template")
    public MailTemplateAiResponse refineTemplate(@RequestBody Map<String, Object> body) {
        MailTemplateRefineRequest request;
        try {
            request = objectMapper.convertValue(body, MailTemplateRefineRequest.class);
        } catch (IllegalArgumentException e) {
            log.warn("convertValue MailTemplateRefineRequest failed: {}", e.getMessage());
            throw new IllegalArgumentException(
                    "模版微调请求体字段不匹配：" + e.getMessage(), e);
        }
        return mailAiService.refineTemplate(request);
    }

    @PostMapping("/personalize")
    public MailPersonalizeResponse personalize(@RequestBody Map<String, Object> body) {
        MailPersonalizeRequest request;
        try {
            request = objectMapper.convertValue(body, MailPersonalizeRequest.class);
        } catch (IllegalArgumentException e) {
            log.warn("convertValue MailPersonalizeRequest failed: {}", e.getMessage());
            throw new IllegalArgumentException(
                    "邮件个性化请求体字段不匹配：" + e.getMessage(), e);
        }
        return mailAiService.personalize(request);
    }

    @PostMapping("/personalize-batch")
    public List<MailPersonalizeResponse> personalizeBatch(@RequestBody List<Map<String, Object>> bodies) {
        List<MailPersonalizeRequest> requests = bodies.stream()
                .map(b -> objectMapper.convertValue(b, MailPersonalizeRequest.class))
                .toList();
        return mailAiService.personalizeBatch(requests);
    }

    @PostMapping("/analyze-intent")
    public MailIntentAnalyzeResponse analyzeIntent(@RequestBody MailIntentAnalyzeRequest request) {
        return mailAiService.analyzeIntent(request);
    }
}
