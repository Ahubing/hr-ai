package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * AI模型配置
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_model")
public class AmModel implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型值
     */
    private String value;

    /**
     * 模型描述
     */
    private String description;

    /**
     * temperature参数
     */
    private Double temperature;

    /**
     * top_p参数
     */
    private Double topP;

    /**
     * 状态: 1-启用, 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 是否为系统默认模型: 1-是, 0-否
     */
    private Integer isDefault;


}

