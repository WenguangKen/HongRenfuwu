package com.athlunakms.ai.dto.mail;

import lombok.Data;

@Data
public class MailIntentAnalyzeRequest {
    private Long influencerId;
    private Long messageId;
    private String subject;
    private String replyBody;
    private String threadSummary;
}
