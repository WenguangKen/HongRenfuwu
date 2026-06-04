package com.athlunakms.ai.controller;

import com.athlunakms.ai.agent.InfluencerAgent;
import com.athlunakms.ai.dto.CopilotLookupReplyRequest;
import com.athlunakms.ai.dto.CopilotLookupReplyResponse;
import com.athlunakms.ai.service.CopilotLookupReplyService;
import com.athlunakms.ai.util.CopilotHonorificContext;
import com.athlunakms.ai.util.CopilotMessageAugmenter;
import com.athlunakms.ai.util.CopilotOffTopicGuard;
import com.athlunakms.ai.util.CopilotStreamSanitizer;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import dev.langchain4j.service.TokenStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/ai-agent-api/v1")
@RequiredArgsConstructor
public class CopilotChatController {

    private final InfluencerAgent influencerAgent;
    private final CopilotOffTopicGuard offTopicGuard;
    private final CopilotUiActionPublisher uiActionPublisher;
    private final CopilotLookupReplyService lookupReplyService;

    /**
     * 前端 CopilotKit 的流式对话入口 (SSE - Server Sent Events)
     * 接收前端自然语言并返回智能体的思考和结果流
     */
    @PostMapping(value = "/copilot-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestHeader(value = "X-Session-Id", defaultValue = "default-session") String sessionId,
            @RequestBody ChatRequest request) {
            
        log.info("收到来自 Copilot 的对话请求: Session={}, Msg={}", sessionId, request.getMessage());
        
        SseEmitter emitter = new SseEmitter(120000L);

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            emitter.completeWithError(new IllegalArgumentException("消息不能为空"));
            return emitter;
        }

        Optional<String> offTopicReply = offTopicGuard.replyIfOffTopic(request.getMessage());
        if (offTopicReply.isPresent()) {
            try {
                emitter.send(SseEmitter.event().data(offTopicReply.get()));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
            return emitter;
        }

        try {
            String honorific = CopilotHonorificContext.resolveHonorific(request.getUserGender());
            String status =
                    honorific.equals("您")
                            ? "小A 已收到，正在处理…"
                            : "小A 已收到，马上为" + honorific + "处理…";
            emitter.send(SseEmitter.event().name("status").data(status));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        uiActionPublisher.clear(sessionId);

        String userMessage =
                CopilotMessageAugmenter.augmentUserMessage(
                        request.getMessage().trim(), request.getUserGender(), request.getSessionContext());
        TokenStream tokenStream = influencerAgent.chat(sessionId, userMessage);
        CopilotStreamSanitizer sanitizer = new CopilotStreamSanitizer();

        tokenStream
            .onNext(token -> {
                try {
                    Optional<String> visible = sanitizer.consume(token);
                    if (visible.isPresent()) {
                        emitter.send(SseEmitter.event().data(visible.get()));
                    }
                    flushStreamSideEffects(emitter, sanitizer, sessionId);
                } catch (Exception e) {
                    emitter.completeWithError(e);
                }
            })
            .onComplete(response -> {
                try {
                    Optional<String> tail = sanitizer.flush();
                    if (tail.isPresent()) {
                        emitter.send(SseEmitter.event().data(tail.get()));
                    }
                    flushStreamSideEffects(emitter, sanitizer, sessionId);
                } catch (Exception e) {
                    log.warn("发送收尾流失败", e);
                }
                log.info("智能体对话流处理完成");
                uiActionPublisher.clear(sessionId);
                emitter.complete();
            })
            .onError(error -> {
                log.error("智能体流式对话异常", error);
                emitter.completeWithError(error);
            })
            .start();
            
        return emitter;
    }

    /** 查库/筛列表完成后，由 AI 根据结构化事实生成口语回复 */
    @PostMapping("/copilot-lookup-reply")
    public CopilotLookupReplyResponse lookupReply(@RequestBody CopilotLookupReplyRequest request) {
        CopilotLookupReplyResponse resp = new CopilotLookupReplyResponse();
        resp.setReply(lookupReplyService.generate(request));
        return resp;
    }

    private void flushStreamSideEffects(SseEmitter emitter, CopilotStreamSanitizer sanitizer, String sessionId)
            throws IOException {
        Optional<String> hint = sanitizer.pollStatusHint();
        if (hint.isPresent()) {
            emitter.send(SseEmitter.event().name("status").data(hint.get()));
        }
        Optional<String> fromStream = sanitizer.pollUiAction();
        if (fromStream.isPresent()) {
            emitter.send(SseEmitter.event().name("ui-action").data(fromStream.get()));
        }
        for (String actionJson : uiActionPublisher.drain(sessionId)) {
            emitter.send(SseEmitter.event().name("ui-action").data(actionJson));
        }
    }

    // 内部 DTO
    public static class ChatRequest {
        private String message;
        /** 1=男(小哥) 2=女(公主)，未传则用「您」 */
        private Integer userGender;
        /** 前端 sessionStorage 会话上下文，供 Agent 理解「这两个红人」等指代 */
        private java.util.Map<String, Object> sessionContext;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getUserGender() {
            return userGender;
        }

        public void setUserGender(Integer userGender) {
            this.userGender = userGender;
        }

        public java.util.Map<String, Object> getSessionContext() {
            return sessionContext;
        }

        public void setSessionContext(java.util.Map<String, Object> sessionContext) {
            this.sessionContext = sessionContext;
        }
    }
}
