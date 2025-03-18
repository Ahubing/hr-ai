package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class AddPositionOptions {


    @NotNull(message = "positionId不能为空")
    @ApiModelProperty(value = "职位id", required = true, notes = "职位id不能为空")
    private Integer positionId;

    @NotEmpty(message = "account_id不能为空")
    @ApiModelProperty(value = "账号id;", required = true, notes = "不能为空, 在chatbot基础设置添加的账号")
    private String accountId;

    @ApiModelProperty(value = "AI方案", required = false, notes = "在广场角色里选。与rechat_option_id至少选一个")
    private Long amMaskId;

    @ApiModelProperty(value = "打招呼后复聊方案id", required = false, notes = "打招呼后复聊方案id,在方案列表选取。与square_role_id至少选一个")
    private Integer rechatOptionId;


    @ApiModelProperty(value = "用户询问信息后复聊方案id", required = false, notes = "用户询问信息后复聊方案id在方案列表选取。与square_role_id至少选一个")
    private Integer inquiryRechatOptionId;
}
