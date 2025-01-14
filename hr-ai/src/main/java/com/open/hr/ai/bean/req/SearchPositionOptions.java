package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchPositionOptions {

    @ApiModelProperty(value = "岗位id",required = false,notes = "岗位Id ")
    private Integer positionId;

    @NotEmpty(message = "accountId不能为空")
    @ApiModelProperty(value = "账号id",required = true,notes = "账号id 对应 bossId ")
    private String accountId;
}
