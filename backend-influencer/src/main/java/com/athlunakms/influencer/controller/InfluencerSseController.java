package com.athlunakms.influencer.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 红人变更 SSE 广播控制器
 *
 * 当红人信息变更时，通过 SSE 通知所有已连接的前端客户端刷新列表。
 * 前端通过 EventSource 连接 /v1/influencer/sse/changes 端点监听。
 */
@RestController
@RequestMapping("/v1/influencer/sse")
public class InfluencerSseController {
    private static final Logger log = LoggerFactory.getLogger(InfluencerSseController.class);
    private static final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * SSE 连接端点 — 前端通过 EventSource 连接
     */
    @GetMapping("/changes")
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L); // 无超时
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        // 发送初始连接确认
        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    /**
     * 广播红人变更事件 — 由 Service 层调用
     */
    public static void broadcastChange(String changeType, Long influencerId) {
        String data = changeType + ":" + influencerId;
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("influencer-changed").data(data));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
        log.debug("Broadcast influencer change: {} #{}, listeners: {}", changeType, influencerId, emitters.size());
    }
}
