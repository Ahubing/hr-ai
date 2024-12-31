package com.open.ai.eros.ai.model.bean.vo.gpt;

import lombok.Data;

/**
 * @Author Administrator
 * @Date 2024/1/30 01:32
 */
@Data
public class Tool extends Object {
    private String type;
    private GptFunction function;
}
