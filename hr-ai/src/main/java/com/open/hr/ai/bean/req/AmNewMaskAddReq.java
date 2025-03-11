package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
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
public class AmNewMaskAddReq {


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
    @ApiModelProperty(value = "是否开启 差异化优势")
    private Boolean differentiatedAdvantagesSwitch;

    /**
     * 差异化优势
     */
    @ApiModelProperty(value = "差异化优势")
    private DifferentiationAdvantage differentiationAdvantage;

    /**
     * 开启面试信息
     */
    @ApiModelProperty(value = "是否开启 面试信息 暂时不支持线上, 所以只需要填写面试地址")
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
     * 沟通脚本
     */
    @ApiModelProperty(value = "沟通脚本")
    @NotEmpty(message = "沟通脚本不能为空")
    private String CommunicationScript;


    /**
     * 过滤词
     */
    @ApiModelProperty(value = "过滤用户的词")
    private String filterWords;

    /**
     * 示例对话
     */
    @ApiModelProperty(value = "示例对话")
    private String exampleDialogues;


    @ApiModelProperty("流程code")
    private Integer code;

    /**
     * 打开交换微信
     */
    @ApiModelProperty(value = "是否开启交换微信")
    private Boolean openExchangeWeChat;

    /**
     * 交换手机号
     */
    @ApiModelProperty(value = "是否开启交换手机号")
    private Boolean openExchangePhone;

    @ApiModelProperty(value = "single-单面，group-群面",required = true)
    private String interviewType;

    @ApiModelProperty(value = "是否跳过节假日，1-是，2-否",required = true)
    private Integer skipHolidayStatus;

    @ApiModelProperty(value = "面试时段配置",required = true)
    private List<IcConfigUpdateReq> icConfigUpdateReqs = new ArrayList<>();


    /**
     * 打招呼话术
     */
    @ApiModelProperty(value = "打招呼话术",required = false,example = "打招呼话术")
    private String greetMessage;
}
