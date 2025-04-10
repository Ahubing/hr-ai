package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息监听
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_greet_online_monitor")
public class AmChatbotGreetOnlineMonitor implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    /**
     * 是否为系统发送给应聘者，0否，1是
     */
    private Boolean isSystemSend;

    /**
     * 来源 uid
     */
    private Integer fromUid;

    /**
     * 接收者uid
     */
    private Integer toUid;

    /**
     * 发送的内容
     */
    private String content;

    /**
     * ai回复的内容
     */
    private String reply;

    /**
     * 拓展，保存messages信息
     */
    private String extendParams;

    /**
     * 创建日期
     */
    private LocalDateTime createTime;

    /**
     * 创建的时间戳
     */
    private Integer createTimestamp;


}
