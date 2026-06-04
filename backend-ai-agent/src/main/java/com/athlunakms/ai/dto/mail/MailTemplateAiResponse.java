package com.athlunakms.ai.dto.mail;

import java.util.List;
import lombok.Data;

@Data
public class MailTemplateAiResponse {
    private String subjectTpl;
    private String htmlBody;
    private String textBody;
    private List<String> suggestedVariables;
    private String notesZh;
}
