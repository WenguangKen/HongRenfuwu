package com.athlunakms.ai.dto.mail;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailTemplateAiRequest {
    /** 运营描述（可由结构化字段自动拼接） */
    private String brief;
    private String brand;
    private String commissionRate;
    private String extraNotes;
    private List<SampleProductItem> sampleProducts;
    /** 语言：en / zh / fr / de / ar / el / auto */
    private String language = "en";
    /**
     * 邮件类型 / 用途：invite, sample_followup, commission_negotiate,
     * content_brief, post_publish_thanks, payment_settle, reactivate,
     * polite_decline, general. 影响 AI 生成的语气和内容结构。
     */
    private String purpose = "invite";
    /**
     * 页面视觉风格：gradient（品牌渐变，默认）/ minimal / luxury / fresh / warm
     */
    private String visualStyle = "gradient";
    private String referenceHtml;
    private List<String> variables;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SampleProductItem {
        /** 商品 ID（可选，仅作上下文，AI 服务自身不需要） */
        private Long id;
        private String title;
        private String sku;
        private String spu;
        private String price;
        @JsonAlias({"image", "mainImage"})
        private String imageUrl;
    }
}
