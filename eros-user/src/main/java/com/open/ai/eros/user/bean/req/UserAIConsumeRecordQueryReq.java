package com.open.ai.eros.user.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @类名：UserAIConsumeRecordQueryReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/18 13:04
 */
@ApiModel("查询用户消费ai记录类")
@Data
public class UserAIConsumeRecordQueryReq {


    @Max(100)
    @Min(1)
    @ApiModelProperty("页数")
    private Integer pageNum;

    @Max(100)
    @Min(10)
    @ApiModelProperty("分页size")
    private Integer pageSize;

}
