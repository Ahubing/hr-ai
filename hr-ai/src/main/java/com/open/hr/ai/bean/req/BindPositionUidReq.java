package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 14:27
 */
@Data
public class BindPositionUidReq {


    /**
     * 职位id
     */
    @NotNull(message = "职位id不能为空")
    @ApiModelProperty("必填，职位id")
    private Integer positionId;


    /**
     * 招聘人员id；
     */
    @NotNull(message = "招聘人员id不能为空")
    @ApiModelProperty("必填，招聘人员id；系统设置，用户管理里面的人作为招聘人员用")
    private Integer uid;




}
