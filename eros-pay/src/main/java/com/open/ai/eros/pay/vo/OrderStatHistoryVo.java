package com.open.ai.eros.pay.vo;

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
public class OrderStatHistoryVo implements Serializable {



    /**
     * 成功支付次数
     */
    private Long recordCount;

    /**
     * 当天总共收入
     */
    private BigDecimal historyIncome;



}
