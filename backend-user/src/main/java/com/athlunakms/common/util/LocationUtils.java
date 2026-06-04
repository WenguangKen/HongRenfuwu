package com.athlunakms.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocationUtils {
    private static final Logger log = LoggerFactory.getLogger(LocationUtils.class);

    public String getLocationByIP(String ip) {
        if (ip == null || ip.isEmpty()) {
            return "\u672a\u77e5";
        }
        if ("127.0.0.1".equals(ip) || "localhost".equals(ip) || "::1".equals(ip)) {
            return "\u672c\u5730";
        }
        return "\u672a\u77e5\u5730\u533a";
    }
}

