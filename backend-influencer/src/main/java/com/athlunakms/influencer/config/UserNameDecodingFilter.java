package com.athlunakms.influencer.config;

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

/**
 * 全局过滤器自动将 X-User-Name 请求头中的 URL 编码内容解码为 UTF-8 字符串
 * 前端使用 encodeURIComponent() 编码中文用户名以避免 HTTP header 非 ASCII 字符问题
 * 此过滤器在请求到达 Controller 之前统一解码避免每个 Controller 方法单独处理
 */
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
