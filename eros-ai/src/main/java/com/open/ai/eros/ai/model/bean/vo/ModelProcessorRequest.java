package com.open.ai.eros.ai.model.bean.vo;

import com.open.ai.eros.ai.bean.vo.AITextChatVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ModelProcessorRequest {

    private String model;

    private String template;

    private String request;

    private AITextChatVo chatVo;

    //private GptCompletionRequest chatCompletionRequest;
    //
    //private GeminiProvisionRequest geminiProvisionRequest;
    //
    //private ClaudeCompletionRequest claudeCompletionRequest;

}
