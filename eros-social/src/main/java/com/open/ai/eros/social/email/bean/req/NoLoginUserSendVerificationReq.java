package com.open.ai.eros.social.email.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 用户登录
 */

@ApiModel("发验证码的实体类")
@Data
public class NoLoginUserSendVerificationReq {
    /**
     * 账号
     */
    @ApiModelProperty("邮箱")
    @NotEmpty(message = "邮箱不能为空")
    private String email;

}
