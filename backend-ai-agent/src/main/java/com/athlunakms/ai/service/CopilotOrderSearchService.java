package com.athlunakms.ai.service;

import com.athlunakms.ai.util.CopilotUiActionBuilder;
import com.athlunakms.ai.util.CopilotUiActionPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class CopilotOrderSearchService {

    private final RestTemplate restTemplate;
    private final CopilotUiActionPublisher uiActionPublisher;
    private final ObjectMapper objectMapper;

    @Value("${services.backend-eccang.url}")
    private String eccangServiceUrl;

    public record OrderSearchResult(
            String orderType,
            String pagePath,
            String pageLabel,
            int total,
            String orderNo,
            String eccangOrderId,
            String influencerName,
            String discountCode,
            String serviceError,
            List<BoundInfluencer> boundInfluencers) {

        public boolean hasError() {
            return serviceError != null && !serviceError.isBlank();
        }
    }

    public record BoundInfluencer(Long influencerId, String influencerName, String orderNo) {}

    public OrderSearchResult search(
            String sessionId,
            String orderType,
            String orderNo,
            String eccangOrderId,
            String influencerName,
            String discountCode)
            throws JsonProcessingException {
        String type = normalizeOrderType(orderType);
        String pagePath = pagePath(type);
        String pageLabel = pageLabel(pagePath);

        if (!StringUtils.hasText(orderNo)
                && !StringUtils.hasText(eccangOrderId)
                && !StringUtils.hasText(influencerName)
                && !StringUtils.hasText(discountCode)) {
            return new OrderSearchResult(
                    type,
                    pagePath,
                    pageLabel,
                    0,
                    null,
                    null,
                    null,
                    null,
                    "请至少提供订单号、交易号、关联红人或折扣码之一。",
                    List.of());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        if (StringUtils.hasText(orderNo)) {
            body.put("orderNo", orderNo.trim());
        }
        if (StringUtils.hasText(eccangOrderId)) {
            body.put("eccangOrderId", eccangOrderId.trim());
        }
        if (StringUtils.hasText(influencerName)) {
            body.put("influencerName", influencerName.trim());
        }
        if (StringUtils.hasText(discountCode)) {
            body.put("discountCode", discountCode.trim());
        }

        String searchPath =
                "conversion".equals(type)
                        ? "/v1/influencer-orders/conversion/search"
                        : "/v1/influencer-orders/sample/search";

        String url =
                UriComponentsBuilder.fromHttpUrl(eccangServiceUrl + searchPath)
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .toUriString();

        int total = 0;
        String serviceError = null;
        List<BoundInfluencer> bound = List.of();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            @SuppressWarnings("unchecked")
            Map<String, Object> page =
                    restTemplate.postForObject(url, new HttpEntity<>(body, headers), Map.class);
            if (page != null && page.get("totalElements") != null) {
                total = Integer.parseInt(page.get("totalElements").toString());
            }
            bound = extractBoundInfluencers(page);
        } catch (RestClientException e) {
            log.warn("订单检索失败 type={} orderNo={}", type, orderNo, e);
            serviceError = "无法连接订单服务 (8081)，请确认 backend-eccang-integration 已启动。";
        }

        String actionJson =
                CopilotUiActionBuilder.buildApplyOrderFilter(
                        pagePath, orderNo, eccangOrderId, influencerName, discountCode, total);
        uiActionPublisher.publish(sessionId, actionJson);

        return new OrderSearchResult(
                type,
                pagePath,
                pageLabel,
                total,
                orderNo,
                eccangOrderId,
                influencerName,
                discountCode,
                serviceError,
                bound);
    }

    @SuppressWarnings("unchecked")
    private List<BoundInfluencer> extractBoundInfluencers(Map<String, Object> page) {
        if (page == null || page.get("content") == null) {
            return List.of();
        }
        Object raw = page.get("content");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> m) {
                    rows.add((Map<String, Object>) m);
                } else {
                    try {
                        rows.add(objectMapper.convertValue(item, Map.class));
                    } catch (Exception ignored) {
                        /* skip */
                    }
                }
            }
        }
        List<BoundInfluencer> out = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (out.size() >= 5) {
                break;
            }
            String name = stringVal(row.get("influencerName"));
            Long id = longVal(row.get("influencerId"));
            String on =
                    firstNonBlank(
                            stringVal(row.get("orderNo")),
                            stringVal(row.get("longOrderNo")),
                            stringVal(row.get("orderName")));
            if (!StringUtils.hasText(name) && id == null) {
                continue;
            }
            out.add(new BoundInfluencer(id, name, on));
        }
        return out;
    }

    public String buildToolReply(OrderSearchResult r) {
        if (r.hasError()) {
            return r.serviceError() + " 已尝试在【" + r.pageLabel() + "】按条件筛选，请查看左侧列表。";
        }
        String cond = describeCondition(r);
        String binding = formatBinding(r.boundInfluencers());
        if (r.total() == 0) {
            return "【查询结果】在「" + r.pageLabel() + "」未找到 " + cond + " 的订单。列表保持原状。";
        }
        if (r.total() == 1) {
            String base =
                    "【查询结果】在「" + r.pageLabel() + "」找到 1 笔订单（" + cond + "）。已打开对应订单页并筛选。";
            return StringUtils.hasText(binding) ? base + " " + binding : base + " 请查看左侧表格「关联红人」列。";
        }
        return "【查询结果】在「"
                + r.pageLabel()
                + "」找到 "
                + r.total()
                + " 笔相关订单（"
                + cond
                + "）。已打开订单页并筛选展示。"
                + (StringUtils.hasText(binding) ? " " + binding : "");
    }

    private static String formatBinding(List<BoundInfluencer> bound) {
        if (bound == null || bound.isEmpty()) {
            return "";
        }
        if (bound.size() == 1) {
            BoundInfluencer b = bound.get(0);
            String name = StringUtils.hasText(b.influencerName()) ? b.influencerName() : "（未填姓名）";
            String idPart = b.influencerId() != null ? "，红人 ID " + b.influencerId() : "";
            return "该订单绑定红人：" + name + idPart + "。";
        }
        StringBuilder sb = new StringBuilder("涉及红人：");
        for (int i = 0; i < bound.size(); i++) {
            BoundInfluencer b = bound.get(i);
            if (i > 0) {
                sb.append("；");
            }
            sb.append(StringUtils.hasText(b.influencerName()) ? b.influencerName() : "未知");
            if (b.influencerId() != null) {
                sb.append("(ID ").append(b.influencerId()).append(")");
            }
        }
        sb.append("。");
        return sb.toString();
    }

    private static String stringVal(Object o) {
        return o == null ? null : o.toString().trim();
    }

    private static Long longVal(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return Long.parseLong(o.toString().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String firstNonBlank(String... vals) {
        for (String v : vals) {
            if (StringUtils.hasText(v)) {
                return v;
            }
        }
        return null;
    }

    private static String describeCondition(OrderSearchResult r) {
        if (StringUtils.hasText(r.orderNo())) {
            return "单号/交易号 " + r.orderNo();
        }
        if (StringUtils.hasText(r.eccangOrderId())) {
            return "Eccang 单号 " + r.eccangOrderId();
        }
        if (StringUtils.hasText(r.influencerName())) {
            return "关联红人 " + r.influencerName();
        }
        if (StringUtils.hasText(r.discountCode())) {
            return "折扣码 " + r.discountCode();
        }
        return "给定条件";
    }

    private static String normalizeOrderType(String orderType) {
        if (!StringUtils.hasText(orderType)) {
            return "sample";
        }
        String t = orderType.trim().toLowerCase();
        if (t.contains("转化") || t.contains("conversion")) {
            return "conversion";
        }
        if (t.contains("分佣") || t.contains("commission")) {
            return "commission";
        }
        return "sample";
    }

    private static String pagePath(String type) {
        return switch (type) {
            case "conversion" -> "/order/conversion";
            case "commission" -> "/commission/order";
            default -> "/order/sample";
        };
    }

    private static String pageLabel(String path) {
        return switch (path) {
            case "/order/conversion" -> "转化订单";
            case "/commission/order" -> "分佣订单";
            default -> "红人订单（样品）";
        };
    }
}
