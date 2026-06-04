package com.athlunakms.webhook.processor;

import com.fasterxml.jackson.databind.JsonNode;

public interface WebhookProcessor {
    public boolean supports(String var1);

    public void process(String var1, String var2, JsonNode var3);

    public String getCategory();
}

