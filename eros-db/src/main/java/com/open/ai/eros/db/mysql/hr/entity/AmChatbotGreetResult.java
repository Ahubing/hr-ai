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
 * 打招呼结果
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_greet_result")
public class AmChatbotGreetResult implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * boss的id
     */
    private Integer accountId;

    /**
     * 用户的id
     */
    private Long userId;

    /**
     * 任务id;有没有任务id,决定完成率
     */
    private Integer taskId;

    /**
     * 复聊标志，0未开始，其他 已进行到对应的item_id阶段
     */
    private Integer rechatItem;

    /**
     * 是否完成；0否，1是
     */
    private Boolean success;

    /**
     * 出错的话提示错误
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
