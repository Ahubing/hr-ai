package com.open.ai.eros.ai.model.bean.vo.claude;

import com.alibaba.fastjson.annotation.JSONField;
import com.open.ai.eros.common.vo.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaudeCompletionRequest {

    private String model;

    private LinkedList<ChatMessage> messages;

    private Double top_p;

    private Double temperature;

    private Boolean stream = false;

//    private Integer stop;

    private Integer max_tokens;

    @JSONField(serialize = false)
    private String source = "api";

    private String system;

    private Object metadata;

    private List<String>stop_sequences;

    private Integer top_k;

}
