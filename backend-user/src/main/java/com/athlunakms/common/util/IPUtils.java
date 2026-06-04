package com.athlunakms.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/*
 * Exception performing whole class analysis ignored.
 */
public class IPUtils {
    private static final String UNKNOWN = "unknown";
    private static final String[] IP_HEADERS = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};

    public static String getClientIP(HttpServletRequest request) {
        String header;
        String ip = null;
        String[] stringArray = IP_HEADERS;
        int n = stringArray.length;
        for (int i = 0; i < n && (!StringUtils.hasText((String)(ip = request.getHeader(header = stringArray[i]))) || "unknown".equalsIgnoreCase(ip)); ++i) {
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.hasText((String)ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    public static boolean isValidIP(String ip) {
        if (!StringUtils.hasText((String)ip)) {
            return false;
        }
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        if (ip.matches(ipv4Pattern)) {
            return true;
        }
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
        if (ip.matches(ipv6Pattern)) {
            return true;
        }
        return IPUtils.isLoopbackIP((String)ip);
    }

    public static boolean isLoopbackIP(String ip) {
        if (!StringUtils.hasText((String)ip)) {
            return false;
        }
        return "127.0.0.1".equals(ip) || "localhost".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip);
    }

    public static String getLocation(String ip) {
        if (!StringUtils.hasText((String)ip)) {
            return "\u672a\u77e5";
        }
        if (IPUtils.isLoopbackIP((String)ip)) {
            return "\u672c\u5730\u7f51\u7edc";
        }
        return "\u672a\u77e5";
    }
}

