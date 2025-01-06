package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class SyncPositionsReq {

    /**
     * is_continue_follow
     */
    @NotNull(message = "account_id不能为空")
    @ApiModelProperty("账号id;在chatbot基础设置添加的账号")
    private String accountId;


    /**
     * 用户id
     */
    private Long adminId;
}
