package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 面试日历-配置
 * </p>
 *
 * @author Eros-AI
 * @since 2025-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ic_config")
public class IcConfig implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 关联的面具id
     */
    private Long maskId;

    /**
     * single-单面，group-群面
     */
    private String type;

    /**
     * 1-周一，2-周二...7周日。多个用,分隔
     */
    private Integer dayOfWeek;

    /**
     * 上午起始时间
     */
    private LocalDateTime morningStartTime;

    /**
     * 上午截止时间
     */
    private LocalDateTime morningEndTime;

    /**
     * 下午起始时间
     */
    private LocalDateTime afternoonStartTime;

    /**
     * 下午截止时间
     */
    private LocalDateTime afternoonEndTime;

    /**
     * 是否跳过节假日，1-是，2-否
     */
    private Integer skipHolidayStatus;


}
