package com.athlunakms.influencer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * 飞书 Webhook 通知服务
 *
 * 通过飞书自定义机器人发送告警消息。
 * Webhook 地址配置在 application.yml 的 app.feishu.webhook-url 中。
 */
@Service
public class FeishuNotifyService {

    private static final Logger log = LoggerFactory.getLogger(FeishuNotifyService.class);

    private final RestTemplate restTemplate;

    @Value("${app.feishu.webhook-url:}")
    private String webhookUrl;

    public FeishuNotifyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 发送纯文本消息
     */
    public void sendText(String text) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[Feishu] Webhook URL not configured, skipping notification.");
            return;
        }
        try {
            Map<String, Object> body = Map.of(
                    "msg_type", "text",
                    "content", Map.of("text", text)
            );
            doPost(body);
        } catch (Exception e) {
            log.error("[Feishu] Failed to send text notification", e);
        }
    }

    /**
     * 发送富文本卡片消息
     *
     * @param title   卡片标题
     * @param content Markdown 内容（飞书卡片支持部分 Markdown）
     */
    public void sendCard(String title, String content) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[Feishu] Webhook URL not configured, skipping notification.");
            return;
        }
        try {
            Map<String, Object> body = Map.of(
                    "msg_type", "interactive",
                    "card", Map.of(
                            "header", Map.of(
                                    "title", Map.of("tag", "plain_text", "content", title),
                                    "template", "orange"
                            ),
                            "elements", List.of(
                                    Map.of(
                                            "tag", "markdown",
                                            "content", content
                                    )
                            )
                    )
            );
            doPost(body);
        } catch (Exception e) {
            log.error("[Feishu] Failed to send card notification", e);
        }
    }

    private void doPost(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject(webhookUrl, request, String.class);
        log.info("[Feishu] Notification sent. Response: {}", response);
    }
}
