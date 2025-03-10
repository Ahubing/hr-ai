package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcRecordAddReq {

    @ApiModelProperty("面具id")
    private Long maskId;

    @ApiModelProperty(value = "管理员/受聘者id", hidden = true)
    private Long adminId;

    @ApiModelProperty("受聘者uid")
    private String employeeUid;

    @ApiModelProperty("面试开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("职位id")
    private Long positionId;

    @ApiModelProperty("boss_id/账号id")
    private String accountId;


}
