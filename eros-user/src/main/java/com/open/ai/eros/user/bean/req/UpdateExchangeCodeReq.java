package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 兑换码表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-27
 */
@ApiModel("修改兑换码")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateExchangeCodeReq{


    @NotNull(message = "id不能为空")
    @ApiModelProperty("id")
    private Long id;

    /**
     * 兑换码类型 type: balance   rights
     */
    @NotEmpty(message = "兑换码类型")
    @ApiModelProperty("兑换码类型")
    private String type;

    /**
     * 价值 额度 或者 权益
     */
    @NotEmpty(message = "价值不能为空")
    @ApiModelProperty("价值")
    private String bizValue;

    /**
     * 兑换码名称
     */
    @NotEmpty(message = "兑换码名称不能为空")
    @ApiModelProperty("兑换码名称")
    private String name;


    @ApiModelProperty("已使用数")
    private Long usedNum;


    /**
     * 发行的数量
     */
    @Max(999)
    @Min(1)
    @ApiModelProperty("发行的数量")
    private Long total;


    @ApiModelProperty("兑换码状态")
    @Max(2)
    @Min(1)
    private Integer status;

}
