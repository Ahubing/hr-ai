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

import java.time.LocalDateTime;

/**
 * <p>
 * 兑换码表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@ApiModel("兑换码")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExchangeCodeVo {

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 兑换码类型 type: balance   rights
     */
    @ApiModelProperty("兑换码类型")
    private String type;


    @ApiModelProperty("兑换码类型描述")
    private String typeDesc;

    /**
     * 价值 额度 或者 权益
     */
    @ApiModelProperty("价值")
    private String bizValue;

    /**
     * 兑换码名称
     */
    @ApiModelProperty("兑换码名称")
    private String name;


    @ApiModelProperty("已使用数")
    private Long usedNum;


    /**
     * 发行的数量
     */
    @ApiModelProperty("发行的数量")
    private Long total;


    @ApiModelProperty("兑换码")
    private String code;


    @ApiModelProperty("兑换码状态")
    private Integer status;


    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
