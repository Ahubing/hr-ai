package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("添加AI模型请求")
public class AmModelAddReq {

    @ApiModelProperty("模型名称")
    @NotBlank(message = "模型名称不能为空")
    private String name;

    @ApiModelProperty("模型值")
    @NotBlank(message = "模型值不能为空")
    private String value;

    @ApiModelProperty("模型描述")
    private String description;

    @ApiModelProperty("temperature参数")
    private Double temperature = 0.7;

    @ApiModelProperty("top_p参数")
    private Double topP = 1.0;

    @ApiModelProperty("是否为系统默认模型: 1-是, 0-否")
    private Integer isDefault = 0;
}