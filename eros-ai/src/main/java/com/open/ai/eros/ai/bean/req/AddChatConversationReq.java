package com.open.ai.eros.ai.bean.req;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.ai.bean.vo.AIParamVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @类名：addChatConversationReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 21:00
 */

@ApiModel("新增会话的实体类")
@Data
public class AddChatConversationReq {


    @ApiModelProperty("会话名称")
    @NotEmpty(message = "会话名称不对称")
    private String name;

    /**
     * 面具头像
     */
    @ApiModelProperty("面具头像")
    private String avatar;

    @ApiModelProperty("面具Id")
    private Long maskId;

    @ApiModelProperty("来源-面具聊天界面(mask) 可以重新创建一个新的会话")
    private String source;


    @ApiModelProperty("知识库id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knowledgeId;


    /**
     * 会话类型 1：面具 2 知识库 3 常规
     */
    @ApiModelProperty("会话类型")
    private Integer type;


    @ApiModelProperty("ai参数")
    private AIParamVo aiParamVo;


    /**
     * 分享的id
     */
    private Long shareMaskId;


}
