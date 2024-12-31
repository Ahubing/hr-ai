package com.open.ai.eros.db.mysql.ai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class MaskStatList implements Serializable {



    /**
     * 统计的天 格式 yyyyMMdd
     */
    private LocalDateTime statsDay;


    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 消费ai额度 单位美元
     */
    private Long cost;

    /**
     * 使用次数
     */
    private Long recordCount;

}
