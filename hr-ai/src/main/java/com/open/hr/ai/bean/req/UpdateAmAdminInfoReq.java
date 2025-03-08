package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class UpdateAmAdminInfoReq {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id")
    private Long id;


    @ApiModelProperty("公司")
    private String company;

    @ApiModelProperty("手机")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;



    @ApiModelProperty("邮箱")
    private String role;

    @ApiModelProperty("到期时间")
    private LocalDateTime expireTime;

}
