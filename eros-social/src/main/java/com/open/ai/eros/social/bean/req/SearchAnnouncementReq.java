package com.open.ai.eros.social.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("搜索公告类")
@Data
public class SearchAnnouncementReq {

    @ApiModelProperty("页数")
    private Integer pageNum = 1;

    @ApiModelProperty("每页条数")
    private Integer pageSize = 20;

    @ApiModelProperty("公告ID")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("状态")
    private Integer status;
}
