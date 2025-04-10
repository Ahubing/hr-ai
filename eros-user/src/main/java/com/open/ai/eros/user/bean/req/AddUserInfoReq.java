package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author Administrator
 * @since 2023-09-07
 */

@ApiModel("用户注册的实体类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddUserInfoReq implements Serializable {


    @ApiModelProperty("邮箱")
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    /**
     * 登录密码
     */
    @ApiModelProperty("密码")
    @NotEmpty(message = "密码不能为空")
    private String password;

    /**
     * 用户名
     */
    @ApiModelProperty("昵称")
    @NotEmpty(message = "昵称不能为空")
    private String userName;


    /**
     * 验证码
     */
    @ApiModelProperty("验证码")
    //@NotEmpty(message = "验证码不能为空")
    private String verificationCode;


    @ApiModelProperty("协议")
    @NotNull(message = "协议不能为空")
    private Boolean agreeProtocol;

    @ApiModelProperty("邀请码")
    private String invitedCode;

}
