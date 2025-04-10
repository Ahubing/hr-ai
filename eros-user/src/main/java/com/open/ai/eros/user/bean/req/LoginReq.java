package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求参数
 */
@ApiModel("用户登录的实体类")
public class LoginReq {


    @ApiModelProperty("邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;


    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }
}
