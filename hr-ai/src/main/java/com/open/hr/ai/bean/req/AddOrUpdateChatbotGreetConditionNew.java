package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
public class AddOrUpdateChatbotGreetConditionNew {


    private Integer id;


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
     * 期望的职位关键词
     */
    @ApiModelProperty(value = "期望的职位关键词", required = false, notes = "期望的职位关键词")
    private List<String> expectPosition;

    /**
     * 过滤的职位关键词
     */
   @ApiModelProperty(value = "过滤的职位关键词", required = false, notes = "过滤的职位关键词")
    private List<String> filterPosition;

    /**
     * 年龄 18-35 不限
     */
    @ApiModelProperty(value = "年龄 18-35 ,不限", required = false, notes = "年龄 18-35, 不限")
    private String age;

    /**
     * 性别，1 男  0 女  -1 不限
     */
    @ApiModelProperty(value = "性别，1 男  0 女  -1 不限", required = false, notes = "性别，1 男  0 女  -1 不限")
    private Integer gender;

    /**
     * 工作年限；如：不限，应届生，1年以下，1-3年，3-5年，5-10年，10年以上
     */
    @ApiModelProperty(value = "工作年限", required = false, notes = "空置为 不限，应届生，1年以下，1-3年，3-5年，5-10年，10年以上")
    private List<String> workYears;

    /**
     * 通过resume的work_experiences和projects判断
     */
    @ApiModelProperty(value = "工作及项目经历", required = false, notes = "")
    private List<String> experience;

    /**
     * 过滤的 resume的work_experiences和projects判断
     */
    @ApiModelProperty(value = "过滤的工作及项目经历", required = false, notes = "")
    private List<String> filterExperience;

    /**
     * 学历要求(多选)；如：不限，初中。及以下，中专/技校，高中，大专，本科，硕士，博士
     */
    @ApiModelProperty(value = "学历要求", required = false, notes = "0初中及以下，1中专/技校，2高中，3大专，4本科，5硕士，6博士")
    private List<Integer> degree;

    /**
     * 薪资待遇(单选)；如：不限,几k以下，几到几k，几k以上
     */
    @ApiModelProperty(value = "不限,几k以下，几到几k，几k以上" ,required = false)
    private String salary;

    /**
     * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
     */
    @ApiModelProperty(value = "0离职/离校-正在找工作，1在职/在校-考虑机会，2在职/在校-寻找新工作, 空为不限",required = false)
    private List<Integer> intention;

    /**
     * 技能；如：不限
     */
    @ApiModelProperty(value = "python,java",required = false,notes = "空为不限")
    private List<String> skills;

    /**
     * 是否开启打招呼特殊处理
     */
    @ApiModelProperty(value = "是否开启用户打招呼的时候不进行期望岗位的过滤,1开启, 0 关闭",required = false,notes = "是否开启用户打招呼的时候不进行期望岗位的过滤 系统默认开启为1")
    private Integer greetHandle;

}
