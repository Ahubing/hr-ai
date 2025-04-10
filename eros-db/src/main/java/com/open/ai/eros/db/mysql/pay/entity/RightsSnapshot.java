package com.open.ai.eros.db.mysql.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rights_snapshot")
public class RightsSnapshot implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 权益id
     */
    private Long rightsId;

    /**
     * 名称
     */
    private String name;

    /**
     * value
     */
    private Long rightsValue;

    /**
     * 更新规则
     */
    private String rule;

    private String intro;

    private LocalDateTime createTime;


    /**
     * 可以使用的模型
     */
    private String canUseModel;

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
