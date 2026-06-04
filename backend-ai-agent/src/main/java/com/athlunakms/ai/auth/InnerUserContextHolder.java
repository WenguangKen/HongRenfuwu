package com.athlunakms.ai.auth;

public class InnerUserContextHolder {
    private static final ThreadLocal<InnerUserContext> contextHolder = new ThreadLocal<>();

    public static void set(InnerUserContext context) {
        contextHolder.set(context);
    }

    public static InnerUserContext get() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
