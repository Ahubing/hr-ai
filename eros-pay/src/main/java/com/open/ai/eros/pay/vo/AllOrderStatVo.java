package com.open.ai.eros.pay.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
public class AllOrderStatVo implements Serializable {



    /**
     * 历史订单支付次数
     */
    private Long historyRecordCount;


    /**
     * 今天订单支付次数
     */
    private Long todayRecordCount;

    /**
     * 历史订单总共收入
     */
    private String historyIncome;

    /**
     * 今天总共收入
     */
    private String todayIncome;


    /**
     * 总共收入
     */
    private String totalIncome;



}
