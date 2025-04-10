package com.open.ai.eros.db.mysql.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户余额的记录表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_balance_record")
public class UserBalanceRecord implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 用户余额记录ID
     */
    @ApiModelProperty("记录ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 用户余额表ID
     */
    @ApiModelProperty(value = "用户余额表ID")
    private Long userBalanceId;

    /**
     * 余额类型 1：不可提现 2：可体现
     */
    private Integer userBalanceType;



    /**
     * 变化金额
     */
    @ApiModelProperty("变化金额")
    private String balance;

    /**
     * 变化类型
     */
    @ApiModelProperty("变化类型")
    private String type;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


}
