package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 新增/编辑-岗位
 * @Date 2025/1/4 14:27
 */
@Data
public class AddPositionReq {

    @ApiModelProperty("岗位id，编辑时必填")
    private Integer id;

    /**
     * 职位id
     */
    @NotNull(message = "部门id不能为空")
    @ApiModelProperty("必填，部门id 加载部门列表获取")
    private Integer sectionId;


    /**
     * 岗位名称
     */
    @NotNull(message = "岗位名称不能为空")
    @ApiModelProperty("必填，岗位名称")
    private String  name;




}
