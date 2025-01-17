package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_greet_messages")
public class AmChatbotGreetMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer taskId;

    /**
     * 任务类型，0打招呼每日任务，1临时任务，2复聊任务
     */
    private Integer taskType;

    private String accountId;

    /**
     * 是否为系统发送给应聘者，0否，1是
     */
    private Integer isSystemSend;

    /**
     * 来源 uid
     */
    private Integer fromUid;

    /**
     * 接收者uid
     */
    private Integer toUid;

    /**
     * 状态，0未开始，1完成
     */
    private Integer status;

    /**
     * 发送的内容
     */
    private String content;

    /**
     * 其他参数;后续AI发送的消息集合,上下文，包括用户回复；存在回复才开始填入
     */
    private String extendParams;

    /**
     * 创建日期
     */
    private String createTime;

    /**
     * 执行的时间段
     */
    private String execTime;

    /**
     * 创建的时间戳
     */
    private Integer createTimestamp;


}
