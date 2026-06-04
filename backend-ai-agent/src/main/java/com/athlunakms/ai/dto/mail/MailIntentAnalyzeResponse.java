package com.athlunakms.ai.dto.mail;

import java.util.List;
import lombok.Data;

@Data
public class MailIntentAnalyzeResponse {
    private Integer intentScore;
    /** STRONG / MEDIUM / WEAK / REJECT / UNCLEAR */
    private String intentLabel;
    private String summaryZh;
    private List<String> keySignals;
    private String suggestedActionZh;
    private String replyLanguage;
    private String quotedPhrase;
}
