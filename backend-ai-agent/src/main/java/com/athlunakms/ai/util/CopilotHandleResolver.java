package com.athlunakms.ai.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/** 从用户话术解析社媒 handle，并直连红人服务查库（不依赖大模型是否调工具） */
@Component
public class CopilotHandleResolver {

    private static final Pattern[] HANDLE_PATTERNS = {
        Pattern.compile("handle\\s*(?:名字\\s*)?叫\\s*@?([a-zA-Z0-9._-]{2,64})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(?:handle|账号名?|帐号|用户名)\\s*(?:名叫|名字叫|是|为|叫)?\\s*@?([a-zA-Z0-9._-]{2,64})", Pattern.CASE_INSENSITIVE),
        Pattern.compile("@([a-zA-Z0-9._-]{2,64})"),
        Pattern.compile("(?:名叫|名字叫|叫做)\\s*@?([a-zA-Z0-9._-]{2,64})", Pattern.CASE_INSENSITIVE),
    };

    private final RestTemplate restTemplate;

    @Value("${services.backend-influencer.url}")
    private String influencerServiceUrl;

    public CopilotHandleResolver(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public record HandleLookupResult(String handle, List<Map<String, Object>> rows, String lookupError) {
        public boolean hasError() {
            return lookupError != null && !lookupError.isBlank();
        }
    }

    public record MultiHandleLookupResult(List<HandleLookupResult> items) {}

    public Optional<String> parseHandle(String message) {
        List<String> handles = parseHandles(message);
        return handles.isEmpty() ? Optional.empty() : Optional.of(handles.get(0));
    }

    public List<String> parseHandles(String message) {
        if (message == null || message.isBlank()) {
            return List.of();
        }
        String t = message.trim();
        List<String> multi = parseMultipleHandles(t);
        if (!multi.isEmpty()) {
            return multi;
        }
        return parseHandleSingle(t).map(List::of).orElse(List.of());
    }

    private Optional<String> parseHandleSingle(String t) {
        for (Pattern p : HANDLE_PATTERNS) {
            Matcher m = p.matcher(t);
            if (m.find()) {
                String h = m.group(1).replace("@", "").trim();
                if (!h.isEmpty() && !h.equalsIgnoreCase("handle")) {
                    return Optional.of(h);
                }
            }
        }
        if (handleRelatedQuestion(t)) {
            Matcher word = Pattern.compile("\\b([a-z][a-z0-9._-]{3,48})\\b", Pattern.CASE_INSENSITIVE).matcher(t);
            while (word.find()) {
                String w = word.group(1);
                if (!w.equalsIgnoreCase("handle") && !w.equalsIgnoreCase("tiktok")) {
                    return Optional.of(w);
                }
            }
        }
        return Optional.empty();
    }

    private List<String> parseMultipleHandles(String t) {
        Set<String> seen = new LinkedHashSet<>();

        Matcher at = Pattern.compile("@([a-zA-Z0-9._-]{2,64})", Pattern.CASE_INSENSITIVE).matcher(t);
        while (at.find()) {
            addHandle(seen, at.group(1));
        }
        if (seen.size() > 1) {
            return List.copyOf(seen);
        }
        seen.clear();

        String chunk = t;
        Matcher openChunk = Pattern.compile("(?:打开|查看|进入).*?[:：]?\\s*([^\\n]+)", Pattern.CASE_INSENSITIVE).matcher(t);
        if (openChunk.find()) {
            chunk = openChunk.group(1);
        }

        if (chunk.matches(".*[，,、；;\\n].*")) {
            for (String part : chunk.split("[，,、；;\\n]+")) {
                addHandleToken(seen, part);
            }
            if (seen.size() > 1) {
                return List.copyOf(seen);
            }
            seen.clear();
        }

        if (chunk.matches(".*(?:和|与|跟|以及).*")) {
            for (String part : chunk.split("(?:和|与|跟|以及)")) {
                addHandleToken(seen, part);
            }
            if (seen.size() > 1) {
                return List.copyOf(seen);
            }
            seen.clear();
        }

        if (handleRelatedQuestion(t)) {
            Matcher word = Pattern.compile("\\b([a-z][a-z0-9._-]{3,48})\\b", Pattern.CASE_INSENSITIVE).matcher(t);
            while (word.find()) {
                String w = word.group(1);
                if (!w.equalsIgnoreCase("handle")
                        && !w.equalsIgnoreCase("tiktok")
                        && !w.equalsIgnoreCase("instagram")
                        && !w.equalsIgnoreCase("youtube")
                        && !w.equalsIgnoreCase("eccang")) {
                    addHandle(seen, w);
                }
            }
            if (seen.size() > 1) {
                return List.copyOf(seen);
            }
        }
        return List.of();
    }

    private static void addHandleToken(Set<String> seen, String fragment) {
        Matcher m = Pattern.compile("(?:^|[\\s@])@?([a-z][a-z0-9._-]{2,48})\\b", Pattern.CASE_INSENSITIVE).matcher(fragment.trim());
        if (m.find()) {
            addHandle(seen, m.group(1));
        }
    }

    private static void addHandle(Set<String> seen, String raw) {
        if (raw == null) {
            return;
        }
        String h = raw.replace("@", "").trim();
        if (h.isEmpty() || h.equalsIgnoreCase("handle")) {
            return;
        }
        seen.add(h);
    }

    public boolean handleRelatedQuestion(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        if (orderRelatedQuestion(message) || discountRelatedQuestion(message)) {
            return false;
        }
        return message.matches(".*(handle|账号|帐号|社媒|@|数据库).*")
                || (message.matches(".*(红人|达人|kol|influencer).*")
                        && message.matches(".*(有没有|是否存在|查找|搜索).*"));
    }

    /** 订单语境：不走 handle 预检，交给大模型 searchOrders */
    public static boolean orderRelatedQuestion(String message) {
        return message != null
                && message.matches(".*(订单|order|单号|样品单|转化单|红人订单|交易号|eccang|绑定|关联红人).*");
    }

    public static boolean discountRelatedQuestion(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        if (orderRelatedQuestion(message)) {
            return false;
        }
        return message.matches(".*(折扣码|优惠码|promo\\s*code|coupon|discount\\s*code).*");
    }

    @SuppressWarnings("unchecked")
    public Optional<HandleLookupResult> lookup(String message) {
        List<String> handles = parseHandles(message);
        if (handles.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(lookupByHandle(handles.get(0)));
    }

    public MultiHandleLookupResult lookupMultiple(String message) {
        List<String> handles = parseHandles(message);
        if (handles.size() <= 1) {
            List<HandleLookupResult> items = new ArrayList<>();
            if (!handles.isEmpty()) {
                items.add(lookupByHandle(handles.get(0)));
            }
            return new MultiHandleLookupResult(items);
        }
        List<HandleLookupResult> items = new ArrayList<>();
        for (String h : handles) {
            items.add(lookupByHandle(h));
        }
        return new MultiHandleLookupResult(items);
    }

    @SuppressWarnings("unchecked")
    private HandleLookupResult lookupByHandle(String h) {
        String url =
                influencerServiceUrl
                        + "/internal/api/influencers/search-by-handle?handle="
                        + URLEncoder.encode(h, StandardCharsets.UTF_8);
        try {
            List<Map<String, Object>> rows = restTemplate.getForObject(url, List.class);
            return new HandleLookupResult(h, rows == null ? List.of() : rows, null);
        } catch (HttpStatusCodeException e) {
            String body = e.getResponseBodyAsString();
            String hint = body != null && body.contains("3065")
                    ? "红人库 SQL 兼容问题，请重启 backend-influencer(8082) 并确认已更新 search-by-handle。"
                    : "红人服务返回 " + e.getStatusCode().value() + "，请确认 8082 已启动。";
            return new HandleLookupResult(h, List.of(), hint);
        } catch (RestClientException e) {
            return new HandleLookupResult(h, List.of(), "无法连接红人服务 (8082)，请确认服务已启动后重试。");
        }
    }

    public String buildMultiReply(MultiHandleLookupResult multi) {
        List<HandleLookupResult> items = multi.items();
        if (items.isEmpty()) {
            return "";
        }
        if (items.size() == 1) {
            return buildReply(items.get(0));
        }
        StringBuilder sb = new StringBuilder("查完了，结果如下：\n");
        int found = 0;
        for (HandleLookupResult item : items) {
            if (item.hasError()) {
                sb.append("• @").append(item.handle()).append("：").append(item.lookupError()).append('\n');
                continue;
            }
            int count = item.rows().size();
            if (count == 0) {
                sb.append("• @").append(item.handle()).append("：红人列表和资源池均未找到\n");
            } else {
                found++;
                Map<String, Object> row = item.rows().get(0);
                String name = String.valueOf(row.getOrDefault("real_name", row.getOrDefault("nick_name", "")));
                sb.append("• @").append(item.handle()).append("：找到 ").append(name).append('\n');
            }
        }
        if (found == items.size()) {
            sb.append("以上红人均已在系统中。");
        } else if (found == 0) {
            sb.append("以上 handle 均未找到，请核对拼写或换关键词再试。");
        } else {
            sb.append("共 ").append(found).append('/').append(items.size()).append(" 位在系统中。");
        }
        if (found > 0) {
            sb.append(" 已为您切到第一位匹配红人对应 Tab。");
        }
        return sb.toString();
    }

    public String buildReply(HandleLookupResult result) {
        if (result.hasError()) {
            return result.lookupError()
                    + " 已在左侧【红人列表】按社媒账号「"
                    + result.handle()
                    + "」尝试筛选，请查看表格。";
        }
        String handle = result.handle();
        int count = result.rows().size();
        if (count == 0) {
            return "业务库中未找到 handle「" + handle + "」对应红人，左侧列表保持原状。可核对拼写或换关键词再试。";
        }
        if (count == 1) {
            Map<String, Object> row = result.rows().get(0);
            String name = String.valueOf(row.getOrDefault("real_name", row.getOrDefault("nick_name", "")));
            return "已找到 handle「" + handle + "」的红人：" + name + "（共 1 位）。已在【红人列表】按社媒账号筛选，请查看左侧表格。";
        }
        return "找到 " + count + " 位与 handle「" + handle + "」相关的红人，已在【红人列表】按社媒账号筛选展示。";
    }
}
