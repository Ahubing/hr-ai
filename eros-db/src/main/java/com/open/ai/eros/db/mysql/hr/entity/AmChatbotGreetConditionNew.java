package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 打招呼条件
 * </p>
 *
 * @author Eros-AI
 * @since 2025-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_greet_condition_new")
public class AmChatbotGreetConditionNew implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 职位id
     */
    private Integer positionId;

    /**
     * 期望的职位关键词
     */
    private String expectPosition;

    /**
     * 过滤的职位关键词
     */
    private String filterPosition;

    /**
     * 年龄 18-35 不限
     */
    private String age;

    /**
     * 性别，男 女 不限
     */
    private Integer gender;

    /**
     * 工作及项目经历：通过resume的work_experiences和projects判断
     */
    @TableField("workYears")
    private String workYears;

    /**
     * 通过resume的work_experiences和projects判断
     */
    private String experience;

    /**
     * 过滤的 resume的work_experiences和projects判断
     */
    private String filterExperience;

    /**
     * 学历要求(多选)；如：不限，初中。及以下，中专/技校，高中，大专，本科，硕士，博士
     */
    private String degree;

    /**
     * 薪资待遇(单选)；如：不限,几k以下，几到几k，几k以上
     */
    private String salary;

    /**
     * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
     */
    private String intention;

    /**
     * 技能；如：不限
     */
    private String skills;


    /**
     * 是否开启打招呼特殊处理
     */
    private Integer greetHandle;


}
