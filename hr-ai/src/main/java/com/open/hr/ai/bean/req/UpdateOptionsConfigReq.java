package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class UpdateOptionsConfigReq {

    /**
     * is_continue_follow
     */
    @NotNull(message = "is_continue_follow不能为空")
    @ApiModelProperty("is_continue_follow")
    private Integer is_continue_follow;


    /**
     * 用户id
     */
    private Long adminId;
}
