package com.athlunakms.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("Athluna KMS API \u6587\u6863").version("1.0.0").description("# Athluna KMS \u540e\u7aefAPI\u63a5\u53e3\u6587\u6863\n\n## \u63a5\u53e3\u7248\u672c\n- \u5f53\u524d\u7248\u672c\uff1av1.0\n- \u57fa\u7840\u8def\u5f84\uff1a/api/v1.0\n\n## \u8ba4\u8bc1\u65b9\u5f0f\n\u6240\u6709\u9700\u8981\u8ba4\u8bc1\u7684\u63a5\u53e3\u90fd\u9700\u8981\u5728\u8bf7\u6c42\u5934\u4e2d\u643a\u5e26JWT Token\uff1a\n```\nAuthorization: Bearer {token}\n```\n\n## \u4f7f\u7528\u8bf4\u660e\n1. \u9996\u5148\u8c03\u7528 `/api/v1.0/auth/captcha` \u83b7\u53d6\u9a8c\u8bc1\u7801\u952e\n2. \u7136\u540e\u8c03\u7528 `/api/v1.0/auth/login` \u8fdb\u884c\u767b\u5f55\uff0c\u83b7\u53d6Token\n3. \u70b9\u51fb\u53f3\u4e0a\u89d2 \"Authorize\" \u6309\u94ae\uff0c\u8f93\u5165 `Bearer {token}` \u8fdb\u884c\u8ba4\u8bc1\n4. \u4e4b\u540e\u6240\u6709\u9700\u8981\u8ba4\u8bc1\u7684\u63a5\u53e3\u90fd\u4f1a\u81ea\u52a8\u643a\u5e26Token\n\n## \u54cd\u5e94\u683c\u5f0f\n\u6240\u6709\u63a5\u53e3\u7edf\u4e00\u8fd4\u56de\u683c\u5f0f\uff1a\n```json\n{\n  \"code\": 200,\n  \"message\": \"\u6210\u529f\",\n  \"data\": {},\n  \"timestamp\": \"2025-01-XX 10:00:00\"\n}\n```\n\n## \u9519\u8bef\u7801\n- 200: \u6210\u529f\n- 400: \u8bf7\u6c42\u53c2\u6570\u9519\u8bef\n- 401: \u672a\u6388\u6743/Token\u8fc7\u671f\n- 403: \u65e0\u6743\u9650\n- 404: \u8d44\u6e90\u4e0d\u5b58\u5728\n- 500: \u670d\u52a1\u5668\u5185\u90e8\u9519\u8bef\n").contact(new Contact().name("Athluna KMS Team").email("support@athluna.com")).license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html"))).servers(List.of(new Server().url("http://localhost:8080").description("\u5f00\u53d1\u73af\u5883"), new Server().url("https://api.athluna.com").description("\u751f\u4ea7\u73af\u5883"))).addSecurityItem(new SecurityRequirement().addList("Bearer Authentication")).components(new Components().addSecuritySchemes("Bearer Authentication", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").description("JWT Token\u8ba4\u8bc1\uff0c\u683c\u5f0f\uff1aBearer {token}")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder().group("public").pathsToMatch(new String[]{"/api/v1.0/auth/**"}).build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder().group("\u7528\u6237\u7ba1\u7406").pathsToMatch(new String[]{"/api/v1.0/users/**"}).build();
    }

    @Bean
    public GroupedOpenApi roleApi() {
        return GroupedOpenApi.builder().group("\u89d2\u8272\u7ba1\u7406").pathsToMatch(new String[]{"/api/v1.0/roles/**"}).build();
    }

    @Bean
    public GroupedOpenApi permissionApi() {
        return GroupedOpenApi.builder().group("\u6743\u9650\u7ba1\u7406").pathsToMatch(new String[]{"/api/v1.0/permissions/**"}).build();
    }

    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder().group("\u7cfb\u7edf\u7ba1\u7406").pathsToMatch(new String[]{"/api/v1.0/system/**"}).build();
    }

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder().group("\u5168\u90e8\u63a5\u53e3").pathsToMatch(new String[]{"/api/v1.0/**"}).build();
    }
}

