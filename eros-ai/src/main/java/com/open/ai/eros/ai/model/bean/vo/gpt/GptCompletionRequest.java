package com.open.ai.eros.ai.model.bean.vo.gpt;

import com.alibaba.fastjson.annotation.JSONField;
import com.open.ai.eros.common.vo.ChatMessage;
import com.open.ai.eros.db.constants.AIRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GptCompletionRequest {

    private String model;

    /**
     * 模版
     */
    @JSONField(serialize = false)
    private String template;

    private LinkedList<ChatMessage> messages;

    private Double top_p;

    private Double temperature;

    private Integer n = 1;

    private Boolean stream = false;

    private List<String> stop;

    private Integer max_tokens;

    private Double presence_penalty = 0.0;

    private Double frequency_penalty = 0.0;

    private Map<String, Integer> logit_bias;

    private String user = AIRoleEnum.USER.getRoleName();

    /**
     * 工具
     */
    @JSONField(serialize = false)
    private List<String> tool;

    /**
     * 逆向知识库专用字段   知识库id  我方的知识库id
     */
    @JSONField(serialize = false)
    private String gizmo_id;

    private ResponseJsonFormat response_format;


    @JSONField(serialize = false)
    private Integer top_K;

    @JSONField(serialize = false)
    private Object metadata;

    @JSONField(serialize = false)
    private List<String> stop_sequences;

}
