package com.open.ai.eros.db.mysql.pay.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 货币汇率表
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("currency_rate")
public class CurrencyRate implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
      private Long id;

    /**
     * 货币编码
     */
    private String currencyCode;

    /**
     * 货币所属国家
     */
    private String currencyName;

    /**
     * 汇率
     */
    private BigDecimal rate;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
