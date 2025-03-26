package com.open.ai.eros.db.mysql.hr.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IcRecordPageReq {

    @ApiModelProperty(value = "招聘员/管理员id",hidden = true)
    private Long adminId;

    @ApiModelProperty("页码")
    private Integer page = 1;

    @ApiModelProperty("显示数")
    private Integer pageSize = 10;

    @ApiModelProperty("面试预约状态1-未取消，2-已取消，3-已过期")
    private Integer interviewStatus;

    @ApiModelProperty("面试类型group-群面，single-单面")
    private String interviewType;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("职位")
    private String postName;

    @ApiModelProperty("职位id(下拉框用)")
    private Integer postId;

    @ApiModelProperty("部门")
    private String deptName;

    @ApiModelProperty("部门id(下拉框用)")
    private Integer deptId;

    @ApiModelProperty("平台")
    private String platform;

    @ApiModelProperty("平台Id(下拉框用)")
    private Integer platformId;

    @ApiModelProperty("账号")
    private String employeeName;

    @ApiModelProperty("账号uid")
    private String employeeUid;

    @ApiModelProperty("查询开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("查询结束时间")
    private LocalDateTime endTime;
}
