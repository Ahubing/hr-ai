package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @类名：UpdateChatMessageReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 20:10
 */
@ApiModel("更新聊天消息的实体类")
@Data
public class UpdateChatMessageReq {

    @ApiModelProperty("会话Id")
    @NotNull(message = "id不能为空")
    private Long id;


    @ApiModelProperty("面具id")
    private Long maskId;


    @ApiModelProperty("对话内容")
    @NotEmpty(message = "内容不能为空")
    private String content;

}
