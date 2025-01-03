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
 * 职位沟通的相关信息
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_chatbot_chat_job")
public class AmChatbotChatJob implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer accountId;

    private Integer uid;

    /**
     * 职位id，脚本返回
     */
    @TableField("jobId")
    private Integer jobId;

    /**
     * 职位拓展信息
     */
    @TableField("jobData")
    private String jobData;

    /**
     * 职位名称
     */
    private String position;

    /**
     * 公司，招聘的公司名
     */
    private String company;

    /**
     * 薪水描述
     */
    private String salary;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 招聘地点
     */
    private String city;

    /**
     * 要求
     */
    private String requirement;


}
