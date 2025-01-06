package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class AddPositionOptions {


    @NotNull(message = "positionId不能为空")
    @ApiModelProperty("职位id")
    private Integer positionId;

    @NotNull(message = "account_id不能为空")
    @ApiModelProperty("账号id;在chatbot基础设置添加的账号")
    private Integer accountId;

    @ApiModelProperty("AI方案,在广场角色里选。与rechat_option_id至少选一个")
    private Integer squareRoleId;

    @ApiModelProperty("复聊方案id,在方案列表选取。与square_role_id至少选一个")
    private Integer rechatOptionId;
}
