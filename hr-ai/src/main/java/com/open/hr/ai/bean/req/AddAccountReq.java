package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class AddAccountReq {


    /**
     * 招聘平台id
     */
    @NotNull(message = "招聘平台id不能为空")
    @ApiModelProperty(value = "招聘平台id", required = true)
    private Long platformId;

    /**
     * 账号
     */
    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(value = "账号", required = true)
    private String account;


    /**
     * 账号所属城市
     */
    @NotEmpty(message = "账号所属城市不能为空")
    @ApiModelProperty(value="账号所属城市",required = true)
    private String city;

    /**
     * 手机
     */
    @NotEmpty(message = "手机不能为空")
    @ApiModelProperty(value = "手机", required = true)
    private String mobile;


}
