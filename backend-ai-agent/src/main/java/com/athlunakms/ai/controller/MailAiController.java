package com.athlunakms.ai.controller;

import com.athlunakms.ai.dto.mail.MailIntentAnalyzeRequest;
import com.athlunakms.ai.dto.mail.MailIntentAnalyzeResponse;
import com.athlunakms.ai.dto.mail.MailPersonalizeRequest;
import com.athlunakms.ai.dto.mail.MailPersonalizeResponse;
import com.athlunakms.ai.dto.mail.MailTemplateAiRequest;
import com.athlunakms.ai.dto.mail.MailTemplateAiResponse;
import com.athlunakms.ai.service.MailAiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai-agent-api/v1/mail-ai")
@RequiredArgsConstructor
public class MailAiController {

    private final MailAiService mailAiService;

    @PostMapping("/generate-template")
    public MailTemplateAiResponse generateTemplate(@RequestBody MailTemplateAiRequest request) {
        return mailAiService.generateTemplate(request);
    }

    @PostMapping("/personalize")
    public MailPersonalizeResponse personalize(@RequestBody MailPersonalizeRequest request) {
        return mailAiService.personalize(request);
    }

    @PostMapping("/personalize-batch")
    public List<MailPersonalizeResponse> personalizeBatch(@RequestBody List<MailPersonalizeRequest> requests) {
        return mailAiService.personalizeBatch(requests);
    }

    @PostMapping("/analyze-intent")
    public MailIntentAnalyzeResponse analyzeIntent(@RequestBody MailIntentAnalyzeRequest request) {
        return mailAiService.analyzeIntent(request);
    }
}
