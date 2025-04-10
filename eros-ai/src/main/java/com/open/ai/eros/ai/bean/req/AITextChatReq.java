package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @类名：AITextChatReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/8 21:25
 */

@ApiModel("文本聊天的入参")
@Data
public class AITextChatReq {


    /**
     * 模型标识
     */
    @NotEmpty(message = "类型模型标识不能为空")
    private String templateModel;


    /**
     * 面具id
     */
    private Long maskId;


    /**
     * 用户对话的id
     */
    @NotNull(message = "对话id不能为空")
    private Long chatId;

    /**
     * 会话id
     */
    @NotEmpty(message = "会话id不能为空")
    private String conversationId;

    @ApiModelProperty("分享Id")
    private Long shareMaskId;


}
