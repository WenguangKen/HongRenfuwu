package com.athlunakms.common.security.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptService {
    private static final Logger log = LoggerFactory.getLogger(BCryptService.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public String encode(String rawPassword) {
        return this.passwordEncoder.encode((CharSequence)rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            log.warn("\u5bc6\u7801\u9a8c\u8bc1\u5931\u8d25: \u8f93\u5165\u5bc6\u7801\u6216\u5b58\u50a8\u54c8\u5e0c\u4e3a\u7a7a");
            return false;
        }
        boolean matches = this.passwordEncoder.matches((CharSequence)rawPassword, encodedPassword);
        if (!matches) {
            log.debug("\u5bc6\u7801\u4e0d\u5339\u914d");
        }
        return matches;
    }

    public boolean upgradeEncoding(String encodedPassword) {
        return this.passwordEncoder.upgradeEncoding(encodedPassword);
    }
}

