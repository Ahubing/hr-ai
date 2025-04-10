package com.open.ai.eros.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("搜索渠道类")
@Data
public class ModelConfigSearchReq {

    /**
     * 搜索的keywords
     */
    @ApiModelProperty("渠道名")
    private String modelConfigName;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("模版")
    private String template;

    @ApiModelProperty("访问token")
    private String token;

    @ApiModelProperty("page")
    private Integer page = 1;

    @ApiModelProperty("pageSize")
    private Integer pageSize = 20;

}
