package com.athlunakms.ai.dto.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailPersonalizeRequest {
    private Long influencerId;
    /** 活动/合作简述 */
    private String brief;
    private String subjectTpl;
    private String htmlBodyTpl;
    private String textBodyTpl;
    private String language = "en";
    /**
     * 邮件类型：invite / sample_followup / commission_negotiate /
     * content_brief / post_publish_thanks / payment_settle / reactivate /
     * polite_decline / reply / general.
     * 影响 AI 生成的语气和信息结构。
     */
    private String purpose = "invite";
}
