package com.open.ai.eros.ai.model.bean.vo.gpt;

import lombok.Data;

/**
 * @Author Administrator
 * @Date 2024/1/30 01:32
 */
@Data
public class ToolCall extends Object {
    private String id;
    private String type;
    private FunctionCall function;

}
