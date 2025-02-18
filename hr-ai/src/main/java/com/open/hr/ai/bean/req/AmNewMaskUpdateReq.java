package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-13
 */

@ApiModel("新增面具信息类")
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AmNewMaskUpdateReq {


    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 面具名称
     */
    @NotEmpty(message = "面具名称不能为空")
    @ApiModelProperty(value = "面具名称", required = true)
    private String name;

    /**
     * 面具类别
     */
    @ApiModelProperty(value = "面具类别")
    private String type;

    /**
     * 模型来源  aws  az gpt  claude
     */
//    @NotEmpty(message = "渠道模版不能为空")
    @ApiModelProperty(value = "模型来源", required = true)
    private List<String> templateModel = Arrays.asList("OpenAI:gpt-4o");

    /**
     * 面具的详情说明
     */
    @ApiModelProperty(value = "面具的详情说明")
    private String introDesc;

    /**
     * 面具的简单说明
     */
    @ApiModelProperty(value = "面具的简单说明")
    private String intro;

    /**
     * 记忆上下文条数
     */
    @ApiModelProperty(value = "记忆上下文条数")
    private Integer contentsNumber;


    /**
     * 背景图
     */
    @ApiModelProperty(value = "背景图")
    private String avatar;


    /**
     * 公司的基本信息
     */
    @ApiModelProperty(value = "公司的基本信息", required = true)
    private CompanyInfo companyInfo;

    /**
     * 硬性要求
     */
    @ApiModelProperty(value = "硬性要求")
    private String otherArgue;

    /**
     * 差异化优势开启后,需要填写
     *
     * DifferentiationAdvantage
     * 面试地点
     *
     */
    @ApiModelProperty(value = "开启差异化优势开关")
    private Boolean differentiatedAdvantagesSwitch;


    /**
     * 差异化优势
     */
    @ApiModelProperty(value = "差异化优势")
    private DifferentiationAdvantage differentiationAdvantage;

    /**
     * 开启面试信息
     */
    @ApiModelProperty(value = "开启面试信息开关")
    private Boolean openInterviewSwitch;

    /**
     * 面试信息开关后, 需要填写面试地点
     */
    @ApiModelProperty(value = "面试地点")
    private String interviewAddress;

    /**
     * 其他招聘信息
     */
    @ApiModelProperty(value = "其他招聘信息")
    private String otherRecruitmentInfo;

    /**
     * 智能交互指令
     */
    @ApiModelProperty(value = "智能交互指令")
    private String style;

    /**
     * 状态  1 发布 2 待发布
     */
    private Integer status;


    /**
     * 沟通脚本
     */
    @ApiModelProperty(value = "沟通脚本")
    private String CommunicationScript;





}
