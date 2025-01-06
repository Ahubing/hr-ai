package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateGreetConfigStatusReq {


    /**
     * 账号id
     */
    @ApiModelProperty("账号id")
    @NotNull(message = "accountId不能为空")
    private String accountId;

    /**
     * 是否开启打招呼任务
     */
    @NotNull(message = "isGreetOn不能为空")
    private Boolean isGreetOn;

    /**
     * 是否开启复聊
     */
    @NotNull(message = "isRechatOn不能为空")
    private Boolean isRechatOn;

    /**
     * 是否开启ai跟进
     */
    @NotNull(message = "isAiOn不能为空")
    private Boolean isAiOn;


}
