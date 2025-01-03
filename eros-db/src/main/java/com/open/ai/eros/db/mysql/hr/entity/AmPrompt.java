package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 提示词管理
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_prompt")
public class AmPrompt implements Serializable {

    private static final long serialVersionUID=1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer adminId;

    /**
     * 名称
     */
    private String name;

    /**
     * 模型
     */
    private String model;

    /**
     * AI客服回复
     */
    private String prompt;

    /**
     * AI引导prompt
     */
    private String prompt2;

    /**
     * 回复引导是否开启 类型.0html提示词，1为说明书AI生成提示词
     */
    private Boolean type;

    /**
     * 跟进引导是否开启
     */
    private Boolean typeA;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 人员ID
     */
    private Integer resumeId;

    /**
     * 简历存放地址
     */
    private String url;

    /**
     * 是否处理过
     */
    private Boolean isRead;

    /**
     * 标签
     */
    private String tags;


}
