package com.open.hr.ai.bean.req;

import io.swagger.annotations.ApiModelProperty;
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
    private List<String> expectPosition;

    /**
     * 过滤的职位关键词
     */
    private List<String> filterPosition;

    /**
     * 年龄 18-35 不限
     */
    private String age;

    /**
     * 性别，男 女 不限
     */
    private String gender;

    /**
     * 工作年限；如：不限，应届生，1年以下，1-3年，3-5年，5-10年，10年以上
     */
    private List<String> workYears;

    /**
     * 通过resume的work_experiences和projects判断
     */
    private List<String> experience;

    /**
     * 过滤的 resume的work_experiences和projects判断
     */
    private List<String> filterExperience;

    /**
     * 学历要求(多选)；如：不限，初中。及以下，中专/技校，高中，大专，本科，硕士，博士
     */
    private List<String> degree;

    /**
     * 薪资待遇(单选)；如：不限,几k以下，几到几k，几k以上
     */
    private String salary;

    /**
     * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
     */
    private List<String> intention;

    /**
     * 技能；如：不限
     */
    private List<String> skills;

}
