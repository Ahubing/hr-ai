package com.open.hr.ai.bean.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户端获取的任务列表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
public class AmClientTasksVo {


    /**
     * 主键
     */
    private String id;


    /**
     * 任务类型，
     */
    private String taskType;


    /**
     * 状态。0未开始，1进行中，2已完成，3失败
     */
    private Integer status;


    /**
     *  总任务数
     */
    private Integer totalCount;

    /**
     *  成功任务数
     */
    private Integer successCount;


    /**
     * 任务详情
     */
    private String detail;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime updateTime;

}
