package com.open.ai.eros.social.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel("新增公告类")
@Data
public class AddAnnouncementReq {

    /**
     * 公告的标题，不能为空
     */
    @ApiModelProperty("标题")
    @NotEmpty(message = "标题不能为空")
    private String title;

    /**
     *公告的内容，不能为空
     */
    @ApiModelProperty("内容")
    @NotEmpty(message = "内容不能为空")
    private String content;

    /**
     * 公告类型，1：系统，2：应用
     */
    @ApiModelProperty("公告类型")
    private Integer type;

    /**
     * 公告状态
     */
    @ApiModelProperty("公告状态")
    private Integer status;


    /**
     * 公告持续时间,默认5秒
     */
    @ApiModelProperty("公告持续时间")
    private Integer duration = 5000;

}
