package com.open.ai.eros.db.mysql.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 权益
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rights")
public class Rights implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 权益名称
     */
    private String name;

    /**
     * 权益的具体行为
     */
    private Long rightsValue;

    /**
     * 更新规则
     */
    private String rule;



    /**
     * 可以使用的模型
     */
    private String canUseModel;



    /**
     * 说明
     */
    private String intro;

    private LocalDateTime createTime;


    /**
     * 1:上架 2：下架
     */
    private Integer status;

    /**
     * 权益类型
     */
    private String type;


    /**
     * 有效时间 -1 为永久 单位 小时
     */
    private Long effectiveTime;


    /**
     * 是否可累加  1：可累加 2：不可累加
     */
    private Integer canAdd;


}
