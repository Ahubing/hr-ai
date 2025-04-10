package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("更新AI模型请求")
public class AmModelUpdateReq {

    @ApiModelProperty("模型ID")
    @NotNull(message = "模型ID不能为空")
    private Long id;

    @ApiModelProperty("模型名称")
    private String name;

    @ApiModelProperty("模型值")
    private String value;

    @ApiModelProperty("模型描述")
    private String description;

    @ApiModelProperty("temperature参数")
    private Double temperature;

    @ApiModelProperty("top_p参数")
    private Double topP;

    @ApiModelProperty("状态: 1-启用, 0-禁用")
    private Integer status;

    @ApiModelProperty("是否为系统默认模型: 1-是, 0-否")
    private Integer isDefault;
}