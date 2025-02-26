package com.open.hr.ai.bean.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class IcRecordPageReq {

    @NotNull
    @ApiModelProperty(value = "招聘员/管理员id",hidden = true)
    private Long adminId;

    @ApiModelProperty("页码")
    private Integer page = 1;

    @ApiModelProperty("显示数")
    private Integer pageSize = 10;

    /**
     * {@link com.open.hr.ai.constant.InterviewStatusEnum}
     */
    @ApiModelProperty("面试预约状态1-未取消，2-已取消")
    private Integer interviewStatus;

    /**
     * {@link com.open.hr.ai.constant.InterviewTypeEnum}
     */
    @ApiModelProperty("面试类型group-群面，single-单面")
    private Integer interviewType;
}
