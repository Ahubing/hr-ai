package com.open.ai.eros.ai.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-03
 */
@ApiModel("消息的实体类")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatMessageVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty("消息Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 会话id
     */
    @ApiModelProperty("会话Id")
    private String conversationId;

    /**
     * 对话的角色
     */
    @ApiModelProperty("AI角色")
    private String role;

    /**
     * 内容
     */
    @ApiModelProperty("会话内容")
    private String content;


    @ApiModelProperty("发送人")
    private SenderVo senderVo;


    /**
     * 质量 默认 0  1：好 2：不好
     */
    @ApiModelProperty("质量 默认 0  1：好 2：不好 ")
    private Integer quality;



    /**
     * 是否已读  1 已读 2 未读
     */
    @ApiModelProperty("是否已读  1 已读 2 未读")
    private Integer readStatus;


    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

}
