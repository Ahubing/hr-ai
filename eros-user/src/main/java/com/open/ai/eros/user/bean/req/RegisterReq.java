package com.open.ai.eros.user.bean.req;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class RegisterReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 15, message = "密码长度必须在6到15个字符之间")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 10, message = "昵称长度不能超过10个字符")
    private String nickname;

    /**
     * 用户头像URL（可选）
     */
    @URL(message = "头像URL格式不正确")
    private String avatar;

    /**
     * 谷歌账号ID（可选，用于谷歌账号注册）
     */
    private String googleId;

    /**
     * 验证码（可选，如果需要邮箱验证）
     */
    private String verificationCode;
}
