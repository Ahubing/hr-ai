package com.open.ai.eros.ai.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.ai.bean.vo.AIParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @类名：UpdateChatConversationReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 21:00
 */
@ApiModel("获取会话消息的实体类")
@Data
public class UpdateChatConversationReq {

    @ApiModelProperty("会话Id")
    @NotEmpty(message = "id不能为空")
    private String id;

    @ApiModelProperty("会话名称")
    @NotEmpty(message = "对话名称不对称")
    private String name;


    /**
     * 会话类型 1：面具 2 知识库 3 常规
     */
    @ApiModelProperty("会话类型")
    private Integer type;

    /**
     * 面具id
     */
    @ApiModelProperty("面具id")
    private Long maskId;


    @ApiModelProperty("知识库id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knowledgeId;

    @ApiModelProperty("ai任务设置")
    private AIParamVo paramVo;

}
