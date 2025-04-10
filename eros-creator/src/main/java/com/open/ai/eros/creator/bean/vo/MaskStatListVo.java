package com.open.ai.eros.creator.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
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
public class MaskStatListVo implements Serializable {



    /**
     * 统计的天 格式 yyyyMMdd
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime statsDay;


    /**
     * 面具id
     */
    private Long maskId;

    /**
     * 面具名称
     */
    private String maskName;

    /**
     * 消费ai额度 单位美元
     */
    private String cost;

    /**
     * 使用次数
     */
    private Long recordCount;
}
