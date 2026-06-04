package com.athlunakms.ai.service;

import com.athlunakms.ai.dto.CopilotLookupReplyRequest;
import com.athlunakms.ai.util.CopilotHonorificContext;
import dev.langchain4j.model.chat.ChatLanguageModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/** 查库完成后，由大模型根据结构化事实生成口语化回复（非固定模板） */
@Slf4j
@Service
@RequiredArgsConstructor
public class CopilotLookupReplyService {

    private final ChatLanguageModel chatLanguageModel;

    public String generate(CopilotLookupReplyRequest req) {
        String honorific = CopilotHonorificContext.resolveHonorific(req.getUserGender());
        String facts = formatFacts(req);
        String userMsg = req.getUserMessage() != null ? req.getUserMessage().trim() : "";

        String prompt =
                """
                你是 Athluna 红人系统里的助手「小A」，正在跟内部 BD 同事说话。
                系统已经完成了数据库查询和列表筛选，你只需根据下方【查库事实】用口语告诉对方结果。

                用户刚才问：%s
                称呼规则：用「%s」称呼用户（未设置性别则用「您」）。

                【查库事实 — 必须全部如实反映，禁止编造或遗漏】
                %s

                【怎么写】
                - 1~3 句话，像同事在微信里回消息：自然、简短、有温度。
                - 可以带一点语气词（哦、呢、～），但不要油腻、不要客服腔。
                - 不要用 bullet、不要编号、不要「结果如下」、不要报告体。
                - 必须交代：谁找到了 / 谁没找到、在什么列表/状态下、是否已经在左侧列表筛出来给对方看了。
                - 不要给查库以外的操作建议（例如「先寄样」），除非用户原话就在问这个。
                - 语气可参考这种风格（仅学语气，不要照抄句式）：「查完了，两个红人都有哦，而且都在休眠中，已经展示在列表了，您瞧瞧～」

                只输出给用户看的纯文本，不要 JSON、不要引号包裹、不要解释你的写作过程。
                """
                        .formatted(
                                userMsg.isEmpty() ? "（用户查询红人是否存在）" : userMsg,
                                honorific,
                                facts);

        try {
            String raw = chatLanguageModel.generate(prompt);
            String cleaned = cleanupReply(raw);
            if (StringUtils.hasText(cleaned)) {
                return cleaned;
            }
        } catch (Exception e) {
            log.warn("Copilot lookup reply AI failed: {}", e.getMessage());
        }
        return fallbackReply(req, honorific);
    }

    private static String formatFacts(CopilotLookupReplyRequest req) {
        StringBuilder sb = new StringBuilder();
        List<CopilotLookupReplyRequest.LookupEntry> entries = req.getEntries();
        if (entries == null || entries.isEmpty()) {
            sb.append("- 无查库条目\n");
        } else {
            for (CopilotLookupReplyRequest.LookupEntry e : entries) {
                if (Boolean.TRUE.equals(e.getFound())) {
                    sb.append("- @")
                            .append(nullToEmpty(e.getHandle()))
                            .append("：已找到");
                    if (StringUtils.hasText(e.getName())) {
                        sb.append("，名称 ").append(e.getName().trim());
                    }
                    if (StringUtils.hasText(e.getPageLabel()) || StringUtils.hasText(e.getTabLabel())) {
                        sb.append("，位于【")
                                .append(nullToEmpty(e.getPageLabel()))
                                .append(" · ")
                                .append(nullToEmpty(e.getTabLabel()))
                                .append("】");
                    }
                    sb.append('\n');
                } else {
                    sb.append("- @")
                            .append(nullToEmpty(e.getHandle()))
                            .append("：红人列表和资源池均未找到\n");
                }
            }
        }
        if (Boolean.TRUE.equals(req.getListUpdated())) {
            sb.append("- 页面动作：已在【")
                    .append(nullToEmpty(req.getListPageLabel()))
                    .append(" · ")
                    .append(nullToEmpty(req.getListTabLabel()))
                    .append("】按 handle 筛出并展示");
            if (StringUtils.hasText(req.getCombinedHandles())) {
                sb.append("（").append(req.getCombinedHandles().trim()).append("）");
            }
            sb.append('\n');
        } else if (entries != null && entries.stream().anyMatch(e -> Boolean.TRUE.equals(e.getFound()))) {
            sb.append("- 页面动作：已定位到对应页面\n");
        }
        return sb.toString().trim();
    }

    private static String cleanupReply(String raw) {
        if (raw == null) {
            return "";
        }
        String t = raw.trim();
        if (t.startsWith("```")) {
            t = t.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```\\s*$", "").trim();
        }
        t = t.replaceAll("^[\"「『]+|[\"」』]+$", "").trim();
        return t;
    }

    private static String fallbackReply(CopilotLookupReplyRequest req, String honorific) {
        String lead = "您".equals(honorific) ? "" : honorific + "，";
        List<CopilotLookupReplyRequest.LookupEntry> entries = req.getEntries();
        if (entries == null || entries.isEmpty()) {
            return lead + "查完了，暂时没有匹配结果，您换个关键词试试。";
        }
        long found = entries.stream().filter(e -> Boolean.TRUE.equals(e.getFound())).count();
        if (found == 0) {
            return lead + "查遍了列表和资源池，这几位都没找到，您核对下 handle 拼写。";
        }
        if (Boolean.TRUE.equals(req.getListUpdated())) {
            return lead
                    + "查完了，"
                    + found
                    + " 位在系统里，已经在【"
                    + nullToEmpty(req.getListTabLabel())
                    + "】列表筛出来给您看了。";
        }
        return lead + "查完了，" + found + " 位在系统里。";
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
