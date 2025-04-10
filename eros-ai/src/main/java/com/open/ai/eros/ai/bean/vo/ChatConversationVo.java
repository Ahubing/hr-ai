package com.open.ai.eros.ai.bean.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-03
 */
@ApiModel("会话记录的实体类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatConversationVo implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * uuid
     */
    @ApiModelProperty("会话Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;

    /**
     * 会话名
     */
    @ApiModelProperty("会话名")
    private String name;

    /**
     * 面具的id
     */
    @ApiModelProperty("面具Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long maskId;

    /**
     * 头像
     */
    @ApiModelProperty("会话头像")
    private String avatar;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 创建人
     */
    @ApiModelProperty("创建账号")
    private Long userId;

    /**
     * 对话
     */
    @ApiModelProperty("最新对话消息")
    private ChatMessageVo chatMessage;


    @ApiModelProperty("未读的消息数")
    private int unReadChatMessageCount;


    /**
     * 分享的会话id
     */
    private String shareConversionId;


    @ApiModelProperty("知识库id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long knowledgeId;

    @ApiModelProperty("ai参数")
    private AIParamVo aiParamVo;

    @ApiModelProperty("关注")
    private boolean follow;

    @ApiModelProperty("会话类型")
    private Integer type;


}
