package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 面具消耗的日统计表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mask_stat_day")
public class MaskStatDay implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 统计的天 格式 yyyyMMdd
     */
    private LocalDateTime statsDay;

    /**
     * 面具作者ID
     */
    private Long userId;

    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 消费ai额度 单位美元
     */
    private Long cost;

    /**
     * 记录条数
     */
    private Long recordCount;

    /**
     * 积分
     */
    private Long costPoints;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
