package com.open.ai.eros.user.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


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
public class CExchangeCodeVo {


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


    @ApiModelProperty("兑换码")
    private String code;

}
