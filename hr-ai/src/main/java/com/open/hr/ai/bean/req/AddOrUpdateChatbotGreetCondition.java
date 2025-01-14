package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 添加或更新打招呼筛选条件
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddOrUpdateChatbotGreetCondition  {


    private Integer id;

    /**
     * 曾就职单位
     */
    @ApiModelProperty(value = "曾就职单位", required = false, notes = "曾就职单位")
    private String previousCompany;

    /**
     * 账号id
     */
    @NotEmpty(message = "accountId不能为空")
    @ApiModelProperty(value = "账号id", required = true, notes = "accountId不能为空")
    private String accountId;

    /**
     * 职位id
     */
    @NotNull(message = "职位不能为空")
    @ApiModelProperty(value = "职位id", required = true, notes = "职位不能为空")
    private Integer positionId;

    /**
     * 招聘职位
     */
    @ApiModelProperty(value = "招聘职位", required = false, notes = "招聘职位")
    private String recruitPosition;

    /**
     * 年龄 18-35 不限
     */
    @ApiModelProperty(value = "年龄", required = false, notes = "18-35 不限")
    private String age;

    /**
     * 性别，男 女 不限
     */
    @ApiModelProperty(value = "性别", required = false, notes = "男 女 不限")

    private String gender;

    /**
     * 经验要求。如：不限
     */
    @ApiModelProperty(value = "经验要求", required = false, notes = "不限")
    private String experience;

    /**
     * 学历要求；如：不限
     */
    @ApiModelProperty(value = "学历要求", required = false, notes = "不限")
    private String education;

    /**
     * 薪资待遇；如：不限
     */
    @ApiModelProperty(value = "薪资待遇", required = false, notes = "不限")
    private String salary;

    /**
     * 求职意向；如：不限
     */
    @ApiModelProperty(value = "求职意向", required = false, notes = "不限")
    private String jobIntention;


}
