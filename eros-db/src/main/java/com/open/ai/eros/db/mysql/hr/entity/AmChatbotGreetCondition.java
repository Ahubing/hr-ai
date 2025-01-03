package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_greet_condition")
public class AmChatbotGreetCondition implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 曾就职单位
     */
    private String previousCompany;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 职位id
     */
    private Integer positionId;

    /**
     * 招聘职位
     */
    private String recruitPosition;

    /**
     * 年龄 18-35 不限
     */
    private String age;

    /**
     * 性别，男 女 不限
     */
    private String gender;

    /**
     * 经验要求。如：不限
     */
    private String experience;

    /**
     * 学历要求；如：不限
     */
    private String education;

    /**
     * 薪资待遇；如：不限
     */
    private String salary;

    /**
     * 求职意向；如：不限
     */
    private String jobIntention;


}
