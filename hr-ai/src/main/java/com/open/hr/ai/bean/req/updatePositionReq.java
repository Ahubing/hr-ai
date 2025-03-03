package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 新增/编辑-岗位
 * @Date 2025/1/4 14:27
 */
@Data
public class updatePositionReq {

    @ApiModelProperty(value = "岗位id，必填", required = true, notes = "岗位id，必填")
    private Integer id;




    /**
     * 岗位描述
     */
    @NotEmpty(message = "岗位描述不能为空")
    @ApiModelProperty(value = "必填，岗位描述", required = true, notes = "岗位描述")
    private String  desc;




}
