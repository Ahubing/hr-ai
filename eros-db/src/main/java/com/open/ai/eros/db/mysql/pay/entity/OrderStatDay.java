package com.open.ai.eros.db.mysql.pay.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("order_stat_day")
public class OrderStatDay implements Serializable {

    private static final long serialVersionUID=1L;

      private Long id;

    /**
     * 统计的天 格式 yyyyMMdd
     */
    private LocalDateTime statsDay;

    /**
     * 成功支付次数
     */
    private Long recordCount;

    /**
     * 当天总共收入
     */
    private BigDecimal cost;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
