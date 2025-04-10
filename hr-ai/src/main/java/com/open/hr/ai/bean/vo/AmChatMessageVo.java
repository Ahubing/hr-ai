package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 招聘聊天消息表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmChatMessageVo  {


      private Long id;

    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * 发送用户id (招聘账号或者用户)
     */
    @ApiModelProperty(value = "发送用户id (招聘账号或者用户)")
    private Long userId;

    /**
     * 对话的角色
     */
    @ApiModelProperty(value = "对话的角色")
    private String role;

    /**
     * 模型
     */
    @ApiModelProperty(value = "模型,暂时为空")
    private String model;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * py客户端当前的消息id,用于过滤重复消息
     */
    @ApiModelProperty(value = "py客户端当前的消息id,用于过滤重复消息")
    private String chatId;

    /**
     * 消息类型 1 为真实的数据, -1 为虚拟的数据(ai生成的,发给客户端的时候,可能会发送失败,但是前端不会显示)
     */
    @ApiModelProperty(value = "消息类型 1 为真实的数据, -1 为虚拟的数据(ai生成的,发给客户端的时候,可能会发送失败,但是前端不会显示)")
    private Integer type;


}
