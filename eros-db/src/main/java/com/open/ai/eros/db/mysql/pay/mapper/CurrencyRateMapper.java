package com.open.ai.eros.db.mysql.pay.mapper;

import com.open.ai.eros.db.mysql.pay.entity.CurrencyRate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 货币汇率表 Mapper 接口
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-04
 */

@Mapper
public interface CurrencyRateMapper extends BaseMapper<CurrencyRate> {


    @Select("select  * from currency_rate where currency_code = #{unit} limit 1 ")
    CurrencyRate getRate(@Param("unit") String unit);


}
