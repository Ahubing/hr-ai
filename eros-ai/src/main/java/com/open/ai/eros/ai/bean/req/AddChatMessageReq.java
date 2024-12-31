package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @类名：UpdateChatMessageReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 20:10
 */
@ApiModel("新增聊天消息的实体类")
@Data
public class AddChatMessageReq {

    @ApiModelProperty("对话内容")
    @NotEmpty(message = "内容不能为空")
    private String content;


    /**
     * 会话id
     */
    @ApiModelProperty("会话id")
    @NotEmpty(message = "会话id不能为空")
    private String conversationId;


    /**
     * 模型标识
     */
    @ApiModelProperty("模型标识")
    private String templateModel;


    /**
     * 面具id
     */
    @ApiModelProperty("面具id")
    private Long maskId;


    @ApiModelProperty("分享Id")
    private Long shareMaskId;


    @ApiModelProperty("来源")
    private String source;


}
