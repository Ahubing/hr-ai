package com.open.hr.ai.bean.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：ReviewStatusVo
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/25 16:12
 */

@ApiModel("流程状态列表")
@Data
public class ReviewStatusVo {


    @ApiModelProperty("流程code")
    private Integer code;

    @ApiModelProperty("流程key")
    private String key;

    @ApiModelProperty("流程名称")
    private String desc;


}
