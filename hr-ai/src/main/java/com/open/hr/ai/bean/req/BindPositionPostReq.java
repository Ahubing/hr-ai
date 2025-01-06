package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 职位关联岗位
 * @Date 2025/1/4 14:27
 */
@Data
public class BindPositionPostReq {


    /**
     * 职位id
     */
    @NotNull(message = "职位id不能为空")
    @ApiModelProperty("必填，职位id")
    private Integer positionId;


    /**
     * 岗位id
     */
    @NotNull(message = "岗位id不能为空")
    @ApiModelProperty("必填，岗位id")
    private Integer postId;




}
