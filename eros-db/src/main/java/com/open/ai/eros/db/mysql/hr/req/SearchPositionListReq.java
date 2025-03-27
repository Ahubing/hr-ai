package com.open.ai.eros.db.mysql.hr.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Date 2025/1/4 21:17
 */
@Data
public class SearchPositionListReq {

    /**
     * 选填;招聘状态 1运行中，0暂停
     */
    @ApiModelProperty("选填;招聘状态 1运行中，0暂停")
    private Integer status;

    @ApiModelProperty("选填;是否开放职位 1开放，0关闭")
    private Integer isOpen;

    @ApiModelProperty("选填，账号id，chatbot配置添加的账号")
    private String accountId;

    @ApiModelProperty("选填，招聘人员id; -1不限，默认-1；请获取用户列表作为下拉框的选项(招聘人员跟账号检索都一样)")
    private Integer uid;

    @ApiModelProperty("选填，发布渠道；返回的platforms渠道列表，对应的渠道id")
    private Integer channel;

    @ApiModelProperty("选填，部门id")
    private Integer sectionId;

    @ApiModelProperty("选填，部门名称")
    private String sectionName;

    @ApiModelProperty("选填，职位id,对职位去重显示")
    private Integer positionId;

    @ApiModelProperty("选填，职位名称")
    private String positionName;

    @ApiModelProperty("选填，岗位id")
    private Integer positionPostId;

    @ApiModelProperty("选填，岗位名称")
    private String positionPostName;

    @ApiModelProperty("选填，城市")
    private String city;

    @ApiModelProperty("选填，页面，从1开始。默认1")
    private Integer page = 1;

    @ApiModelProperty("选填，每页数量。默认10")
    private Integer size = 10;

    @ApiModelProperty(value = "adminId",hidden = true)
    private Long adminId;

}
