package com.open.ai.eros.db.mysql.hr.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 打招呼条件
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class AmGreetConditionVo {

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
    private List<String> expectPosition;

    /**
     * 过滤的职位关键词
     */
    private List<String> filterPosition;

    /**
     * 年龄 18-35 空字符串为不限
     */
    private String age;

    /**
     * 0女，1男, 空为不限
     */
    private Integer gender;

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
     * 0初中及以下，1中专/技校，2高中，3大专，4本科，5硕士，6博士, -1未知
     */
    private List<Integer> degree;

    /**
     * 薪资待遇(单选)；如：不限,几k以下，几到几k，几k以上
     */
    private String salary;

    /**
     * 求职意向(多选）；如：不限,离职/离校-正在找工作，在职/在校-考虑机会，在职/在校-寻找新工作
     * 0离职/离校-正在找工作，1在职/在校-考虑机会，2在职/在校-寻找新工作, -1未知
     */
    private List<Integer> intention;

    /**
     * 技能；如：不限
     */
    private List<String> skills;

    /**
     * 是否开启打招呼特殊处理
     */
    private Integer greetHandle;


}
