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
    @ApiModelProperty(value = "是否开启打招呼任务",required = false, notes = "3个字段必须选一个,是否开启打招呼任务")
    private Integer isGreetOn;

    /**
     * 是否开启复聊
     */
    @ApiModelProperty( value= "isRechatOn不能为空",required = false, notes = "3个字段必须选一个,是否开启复聊")
    private Integer isRechatOn;

    /**
     * 是否开启ai跟进
     */
    @ApiModelProperty(value = "isAiOn不能为空",required = false, notes = "3个字段必须选一个,是否开启ai跟进")
    private Integer isAiOn;

    /**
     * 总开关
     */
    @ApiModelProperty(value = "全部开关",required = false, notes = "全部开关 1开启, 0 关闭")
    private Integer isAllOn;



}
