package com.athlunakms.ai.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

/** 从用户自然语言解析粉丝数区间 */
public final class CopilotFanRangeParser {

    public record FanRange(Long fansMin, Long fansMax) {}

    private static final Pattern[] BELOW_PATTERNS = {
        Pattern.compile("([\\d.]+万?)\\s*粉丝?(以下|以内|不超过|少于|小于|低于)"),
        Pattern.compile("粉丝?([\\d.]+万?)(以下|以内|不超过|少于|小于|低于)"),
        Pattern.compile("(少于|小于|不超过|低于)([\\d.]+万?)\\s*粉丝?"),
    };

    private static final Pattern[] ABOVE_PATTERNS = {
        Pattern.compile("([\\d.]+万?)\\s*粉丝?(以上|超过|大于|不少于|不低于)"),
        Pattern.compile("粉丝?([\\d.]+万?)(以上|超过|大于|不少于|不低于)"),
        Pattern.compile("(超过|大于|不少于|不低于)([\\d.]+万?)\\s*粉丝?"),
    };

    private static final Pattern RANGE_PATTERN =
            Pattern.compile("([\\d.]+万?)\\s*(?:到|-|~|至)\\s*([\\d.]+万?)\\s*粉丝?");

    private CopilotFanRangeParser() {}

    public static FanRange parse(String text) {
        if (!StringUtils.hasText(text)) {
            return new FanRange(null, null);
        }
        String t = text.replaceAll("\\s+", "");

        Long below = firstNumber(BELOW_PATTERNS, t);
        if (below != null) {
            return new FanRange(null, below);
        }

        Long above = firstNumber(ABOVE_PATTERNS, t);
        if (above != null) {
            return new FanRange(above, null);
        }

        Matcher rangeM = RANGE_PATTERN.matcher(t);
        if (rangeM.find()) {
            Long a = parseCount(rangeM.group(1));
            Long b = parseCount(rangeM.group(2));
            if (a != null && b != null) {
                return new FanRange(Math.min(a, b), Math.max(a, b));
            }
        }

        return new FanRange(null, null);
    }

    public static String label(FanRange range) {
        if (range == null) {
            return "";
        }
        if (range.fansMin() != null && range.fansMax() != null) {
            return String.format("粉丝 %d–%d", range.fansMin(), range.fansMax());
        }
        if (range.fansMax() != null) {
            return String.format("粉丝 ≤ %d", range.fansMax());
        }
        if (range.fansMin() != null) {
            return String.format("粉丝 ≥ %d", range.fansMin());
        }
        return "";
    }

    private static Long firstNumber(Pattern[] patterns, String t) {
        for (Pattern p : patterns) {
            Matcher m = p.matcher(t);
            if (!m.find()) {
                continue;
            }
            for (int g = 1; g <= m.groupCount(); g++) {
                Long v = parseCount(m.group(g));
                if (v != null) {
                    return v;
                }
            }
        }
        return null;
    }

    private static Long parseCount(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String s = raw.trim();
        if (!s.matches(".*\\d.*")) {
            return null;
        }
        try {
            if (s.contains("万")) {
                double n = Double.parseDouble(s.replace("万", ""));
                return Math.round(n * 10000);
            }
            String digits = s.replaceAll("[^\\d]", "");
            if (digits.isEmpty()) {
                return null;
            }
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
