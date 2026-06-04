package com.athlunakms.ai.audit;

import com.athlunakms.ai.auth.InnerUserContext;
import com.athlunakms.ai.auth.InnerUserContextHolder;
import dev.langchain4j.agent.tool.Tool;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AiAuditLogAspect {

    private final NamedParameterJdbcTemplate pgJdbcTemplate;

    @Around("@annotation(toolAnnotation)")
    public Object auditToolExecution(ProceedingJoinPoint joinPoint, Tool toolAnnotation) throws Throwable {
        String toolName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        boolean isHighRisk = "exportInfluencerContacts".equals(toolName);
        InnerUserContext user = InnerUserContextHolder.get();
        String operator = user != null && user.getUsername() != null ? user.getUsername() : "UNKNOWN";

        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            try {
                String args = Arrays.toString(joinPoint.getArgs());
                if (args.length() > 500) {
                    args = args.substring(0, 500) + "...";
                }
                String sql = "INSERT INTO ai_audit_log (operator_username, tool_invoked, user_prompt, is_high_risk) " +
                        "VALUES (:operator, :toolName, :args, :isHighRisk)";
                pgJdbcTemplate.update(sql, new MapSqlParameterSource()
                        .addValue("operator", operator)
                        .addValue("toolName", toolName)
                        .addValue("args", args)
                        .addValue("isHighRisk", isHighRisk));
                if (isHighRisk) {
                    log.warn("[AI 审计] 高危工具调用: operator={}, tool={}", operator, toolName);
                }
            } catch (Exception e) {
                log.error("AI 审计落库异常", e);
            }
        }
    }
}
