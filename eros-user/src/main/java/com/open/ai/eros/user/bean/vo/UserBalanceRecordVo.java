package com.open.ai.eros.user.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.open.ai.eros.common.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户余额记录VO
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */
@ApiModel("用户余额记录信息")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBalanceRecordVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户余额记录ID
     */
    @ApiModelProperty("记录ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

}
