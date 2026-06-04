package com.athlunakms.eccang.order.dto;

import java.util.Map;
import lombok.Data;

@Data
public class OrderDetailLogDto {
    private String type;
    private String time;
    private String content;
    private String operator;
    private Map<String, Object> details;
}
