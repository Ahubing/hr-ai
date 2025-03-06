package com.open.ai.eros.ai.tool.tmp;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IcRecordPageReq {

    @ApiModelProperty(value = "招聘员/管理员id",hidden = true)
    private Long adminId;

    @ApiModelProperty("页码")
    private Integer page = 1;

    @ApiModelProperty("显示数")
    private Integer pageSize = 10;

    @ApiModelProperty("面试预约状态1-未取消，2-已取消")
    private Integer interviewStatus;

    @ApiModelProperty("面试类型group-群面，single-单面")
    private String interviewType;
}
