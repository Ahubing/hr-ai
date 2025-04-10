package com.open.ai.eros.pay.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.open.ai.eros.common.util.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单的日统计表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderStatDayVo implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 统计的天 格式 yyyyMMdd
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD)
    private LocalDateTime statsDay;

    /**
     * 成功支付次数
     */
    private Long recordCount;

    /**
     * 当天总共收入
     */
    private BigDecimal income;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DateUtils.FORMAT_YYYY_MM_DD_HHMMSS)
    private LocalDateTime createTime;


}
