package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户收益日统计表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_income_stat_day")
public class UserIncomeStatDay implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 收益类型
     */
    private String type;

    /**
     * 统计天
     */
    private LocalDateTime statDay;

    /**
     * 收益积分
     */
    private Long income;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
