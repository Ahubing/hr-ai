package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Author liuzilin
 * @Date 2025/1/16 10:57
 */
@Data
public class SearchGreetConfig {

    @NotEmpty(message = "account_id不能为空")
    @ApiModelProperty(value = "account_id",required = true,notes = "账号id")
    private String account_id;
}
