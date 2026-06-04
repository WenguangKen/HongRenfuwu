package com.athlunakms.ai.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

/**
 * 过滤豆包模型泄漏的 &lt;|FunctionCallBegin|&gt; ... 原始工具调用，并解析工具名用于思考状态提示。
 */
public class CopilotStreamSanitizer {

    private static final Pattern FC_BEGIN =
            Pattern.compile("<\\|functionCallBegin\\|>", Pattern.CASE_INSENSITIVE);
    private static final Pattern FC_END =
            Pattern.compile("<\\|functionCallEnd\\|>", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOOL_NAME =
            Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    private final StringBuilder pending = new StringBuilder();
    private boolean insideFunctionCall;
    private String lastStatusHint;
    private String lastUiActionJson;

    public Optional<String> consume(String token) {
        lastStatusHint = null;
        if (!StringUtils.hasText(token)) {
            return Optional.empty();
        }
        pending.append(token);
        StringBuilder visible = new StringBuilder();
        drain(visible);
        return visible.length() > 0 ? Optional.of(visible.toString()) : Optional.empty();
    }

    public Optional<String> pollStatusHint() {
        if (!StringUtils.hasText(lastStatusHint)) {
            return Optional.empty();
        }
        String hint = lastStatusHint;
        lastStatusHint = null;
        return Optional.of(hint);
    }

    /** 从流中解析出的页面联动指令（如 searchInfluencers 参数） */
    public Optional<String> pollUiAction() {
        if (!StringUtils.hasText(lastUiActionJson)) {
            return Optional.empty();
        }
        String json = lastUiActionJson;
        lastUiActionJson = null;
        return Optional.of(json);
    }

    public Optional<String> flush() {
        StringBuilder visible = new StringBuilder();
        if (!insideFunctionCall && pending.length() > 0) {
            visible.append(pending);
            pending.setLength(0);
        }
        insideFunctionCall = false;
        pending.setLength(0);
        return visible.length() > 0 ? Optional.of(visible.toString()) : Optional.empty();
    }

    private void drain(StringBuilder visible) {
        while (pending.length() > 0) {
            if (insideFunctionCall) {
                Matcher endM = FC_END.matcher(pending);
                if (!endM.find()) {
                    return;
                }
                String block = pending.substring(0, endM.start());
                resolveStatusHint(block);
                pending.delete(0, endM.end());
                insideFunctionCall = false;
                continue;
            }

            Matcher beginM = FC_BEGIN.matcher(pending);
            if (!beginM.find()) {
                if (!mightBePartialTag()) {
                    visible.append(pending);
                    pending.setLength(0);
                }
                return;
            }
            int bStart = beginM.start();
            int bEnd = beginM.end();
            if (bStart > 0) {
                visible.append(pending.substring(0, bStart));
            }
            pending.delete(0, bEnd);
            insideFunctionCall = true;
        }
    }

    private boolean mightBePartialTag() {
        String s = pending.toString().toLowerCase();
        String full = "<|functioncallbegin|>";
        return full.startsWith(s) && s.length() < full.length();
    }

    private void resolveStatusHint(String block) {
        Matcher m = TOOL_NAME.matcher(block);
        if (!m.find()) {
            lastStatusHint = "正在调用智能体工具…";
            return;
        }
        String toolName = m.group(1);
        lastStatusHint = mapToolStatus(toolName);
        if ("searchInfluencers".equalsIgnoreCase(toolName)) {
            try {
                lastUiActionJson = CopilotUiActionBuilder.buildApplyInfluencerFilterFromToolCall(block);
            } catch (Exception ignored) {
                /* 解析失败则仅展示思考状态 */
            }
        }
    }

    private static String mapToolStatus(String toolName) {
        return switch (toolName) {
            case "searchInfluencers" -> "正在检索红人向量库与业务库…";
            case "lookupInfluencerByHandle" -> "正在按 handle 查询业务数据库…";
            case "searchOrders" -> "正在理解订单意图并查询订单库…";
            case "navigateSystemPage" -> "正在为您跳转系统页面…";
            case "openInfluencerDetail" -> "正在打开红人详情…";
            case "openInviteMailComposer" -> "正在打开邀约邮件编写页…";
            case "createInviteOutreachCampaign" -> "正在创建建联邮件任务…";
            case "exportInfluencerContacts", "exportFilteredInfluencers" -> "正在打开红人导出…";
            case "exportOrders" -> "正在打开订单导出…";
            default -> "正在执行「" + toolName + "」…";
        };
    }
}
