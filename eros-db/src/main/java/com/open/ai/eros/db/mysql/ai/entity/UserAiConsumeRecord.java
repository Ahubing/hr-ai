package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户的ai消费记录
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_ai_consume_record")
public class UserAiConsumeRecord implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 面具
     */
    private Long maskId;

    /**
     * 模型标识
     */
    private String model;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 输入的token
     */
    private Long promptToken;

    private Long chatId;

    /**
     * ai回答的消耗
     */
    private Long relyToken;

    /**
     * 消耗的额度（单位 美元）
     */
    private Long cost;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 是否有分红
     */
    private Integer dividend;

    /**
     * 计费类型 1：额度 2：次数
     */
    private Integer costType;


}
