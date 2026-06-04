package com.athlunakms.ai.dto.mail;

import lombok.Data;

@Data
public class MailPersonalizeResponse {
    private Long influencerId;
    private String displayName;
    private String handle;
    private String subject;
    private String htmlBody;
    private String textBody;
}
