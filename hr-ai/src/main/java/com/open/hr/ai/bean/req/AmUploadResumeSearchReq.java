package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @类名：MaskSearchReq
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/14 21:39
 */

@Data
public class AmUploadResumeSearchReq {

    /**
     * 搜索的关键字
     */
    @ApiModelProperty("搜索关键字")
    private String keywords;


    @ApiModelProperty("页码")
    private Integer page = 1;


    @ApiModelProperty("显示数")
    private Integer pageSize = 10;

    @ApiModelProperty("搜索岗位")
    private String position;


}
