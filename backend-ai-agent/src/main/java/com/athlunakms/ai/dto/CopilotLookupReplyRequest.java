package com.athlunakms.ai.dto;

import java.util.List;
import lombok.Data;

@Data
public class CopilotLookupReplyRequest {
    private String userMessage;
    private Integer userGender;
    private List<LookupEntry> entries;
    /** 是否已在左侧列表按 handle 筛出并展示 */
    private Boolean listUpdated;
    private String listPageLabel;
    private String listTabLabel;
    private String combinedHandles;

    @Data
    public static class LookupEntry {
        private String handle;
        private Boolean found;
        private String name;
        private String pageLabel;
        private String tabLabel;
    }
}
