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
@TableName("am_chatbot_greet_config")
public class AmChatbotGreetConfig implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer adminId;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 是否开启打招呼任务
     */
    private Integer isGreetOn;

    /**
     * 是否开启复聊
     */
    private Integer isRechatOn;

    /**
     * 是否开启ai跟进
     */
    private Integer isAiOn;


}
