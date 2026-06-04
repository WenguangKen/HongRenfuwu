package com.athlunakms.ai.auth;

import lombok.Data;

/**
 * 内部企业级身份上下文 (AI中枢专用)
 */
@Data
public class InnerUserContext {
    private String username;
    // 角色：ADMIN, BUSINESS, OPERATOR, STAFF
    private String role;
}
