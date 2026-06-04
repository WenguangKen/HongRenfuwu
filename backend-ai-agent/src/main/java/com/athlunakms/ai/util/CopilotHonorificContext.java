package com.athlunakms.ai.util;

/** 小A 对用户的称呼：男→小哥，女→公主 */
public final class CopilotHonorificContext {

    private CopilotHonorificContext() {}

    public static String resolveHonorific(Integer userGender) {
        if (userGender == null) {
            return "您";
        }
        return switch (userGender) {
            case 1 -> "小哥";
            case 2 -> "公主";
            default -> "您";
        };
    }

    public static String augmentUserMessage(String message, Integer userGender) {
        String honorific = resolveHonorific(userGender);
        String prefix =
                honorific.equals("您")
                        ? "[称呼：用户性别未设置，请用「您」称呼，语气亲切专业。]\n\n"
                        : "[称呼：请始终用「" + honorific + "」称呼当前用户，语气亲切专业。]\n\n";
        return prefix + message;
    }
}
