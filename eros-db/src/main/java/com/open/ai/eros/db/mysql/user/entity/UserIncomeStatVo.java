package com.open.ai.eros.db.mysql.user.entity;

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
public class UserIncomeStatVo implements Serializable {

    private static final long serialVersionUID=1L;


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 统计天
     */
    private LocalDateTime statDay;

    /**
     * 收益积分
     */
    private Long income;


}
