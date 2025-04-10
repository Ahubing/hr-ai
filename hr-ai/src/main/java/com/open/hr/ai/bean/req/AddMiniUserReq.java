package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 *  用户
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddMiniUserReq {


    /**
     * 账号
     */
    @NotEmpty(message = "账号不能为空")
    @ApiModelProperty(value = "账号", required = true)
    private String userName;



    /**
     * 密码
     */
    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String passWord;

    /**
     * 名称
     */
    @NotEmpty(message = "名称不能为空")
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话", required = false)
    private String mobile;

    /**
     * 微信
     */
    @ApiModelProperty(value = "微信", required = false)
    private String wechat;


}
