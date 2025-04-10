package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@ApiModel("用户注册的实体类")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrAddUserReq implements Serializable {

    @ApiModelProperty("邮箱")
//    @NotEmpty(message = "邮箱不能为空")
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
    @ApiModelProperty("用户名")
    @NotEmpty(message = "用户名不能为空")
    private String username;


    @ApiModelProperty("公司信息")
    @NotEmpty(message = "公司信息不能为空")
    private String company;

    @ApiModelProperty("电话号码")
    @NotEmpty(message = "电话号码不能为空")
    private String mobile;


    @ApiModelProperty("权限")
    private String role;

    @ApiModelProperty("到期时间, 如果角色为vip 则必填,且不能小于当前时间")
    private LocalDateTime expireTime;

}
