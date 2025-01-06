package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 新增/编辑部门
 * @Date 2025/1/4 14:27
 */
@Data
public class AddOrUpdateSectionReq {

    @ApiModelProperty("部门id，编辑时必填")
    private Integer id;

    /**
     * 岗位名称
     */
    @NotNull(message = "部门名称")
    @ApiModelProperty("必填，部门名称")
    private String  name;


    /**
     * 用户id
     */
    private Long adminId;


}
