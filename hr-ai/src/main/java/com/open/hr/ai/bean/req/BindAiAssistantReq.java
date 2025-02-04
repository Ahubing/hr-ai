package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 职位绑定AI助手
 * @Date 2025/1/4 14:27
 */
@Data
public class BindAiAssistantReq {


    /**
     * 职位id
     */
    @NotNull(message = "职位id不能为空")
    @ApiModelProperty("必填，职位id")
    private Integer positionId;


    /**
     * ai助手id
     */
    @NotNull(message = "ai助手id不能为空")
    @ApiModelProperty("必填，ai助手id；前面的列表有返回")
    private Long aiAssistantId;




}
