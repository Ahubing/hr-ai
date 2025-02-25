package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IcRecordAddReq {

    @ApiModelProperty("面具id")
    private Long maskId;

    @ApiModelProperty("管理员/受聘者id")
    private Long adminId;

    @ApiModelProperty("受聘者uid")
    private String employeeUid;

    @ApiModelProperty("面试开始时间")
    private LocalDateTime startTime;

}
