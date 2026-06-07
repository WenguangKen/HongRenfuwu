package com.athlunakms.eccang.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class UserNameDecodingFilter implements Filter {

    private static final String HEADER_NAME = "X-User-Name";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String encodedName = httpRequest.getHeader(HEADER_NAME);
            if (encodedName != null && !encodedName.isEmpty()) {
                try {
                    String decodedName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
                    HttpServletRequest wrapped = new HttpServletRequestWrapper(httpRequest) {
                        @Override
                        public String getHeader(String name) {
                            if (HEADER_NAME.equalsIgnoreCase(name)) {
                                return decodedName;
                            }
                            return super.getHeader(name);
                        }

                        @Override
                        public Enumeration<String> getHeaders(String name) {
                            if (HEADER_NAME.equalsIgnoreCase(name)) {
                                return Collections.enumeration(Collections.singletonList(decodedName));
                            }
                            return super.getHeaders(name);
                        }
                    };
                    chain.doFilter(wrapped, response);
                    return;
                } catch (Exception e) {
                    // decoding failed, pass through original
                }
            }
        }
        chain.doFilter(request, response);
    }
}
