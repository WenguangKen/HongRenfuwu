package com.athlunakms.ai.dto.mail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailTemplateRefineRequest {
    private String subjectTpl;
    private String htmlBody;
    private String textBody;
    /** 用户微调指令，如「顶栏改蓝色、加大间距」 */
    private String instruction;
    /** 可选：当前页面风格 gradient/minimal/luxury/fresh/warm */
    private String visualStyle;
}
