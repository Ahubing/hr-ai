package com.open.ai.eros.db.mysql.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客户端获取的任务列表
 * </p>
 *
 * @author Eros-AI
 * @since 2025-01-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("am_client_tasks")
public class AmClientTasks implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
      private String id;

    /**
     * boss账号id
     */
    private String bossId;

    /**
     * 任务类型，get_all_job同步职位，greet打招呼
     */
    private String taskType;

    /**
     * 任务数据
     */
    private String data;

    /**
     * 状态。0未开始，1进行中，2已完成，3失败
     */
    private Boolean status;

    /**
     * 重试次数，最多3次
     */
    private Integer retryTimes;

    /**
     * 打招呼成功次数
     */
    private Integer successTimes;

    /**
     * 失败原因
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
