package com.open.ai.eros.db.mysql.pay.service.impl;

import com.open.ai.eros.db.mysql.pay.entity.CurrencyRate;
import com.open.ai.eros.db.mysql.pay.mapper.CurrencyRateMapper;
import com.open.ai.eros.db.mysql.pay.service.ICurrencyRateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 货币汇率表 服务实现类
 * </p>
 *
 * @author Eros-AI
 * @since 2024-09-04
 */
@Service
public class CurrencyRateServiceImpl extends ServiceImpl<CurrencyRateMapper, CurrencyRate> implements ICurrencyRateService {


    public BigDecimal getRate(String unit){
        CurrencyRate rate = this.getBaseMapper().getRate(unit);
        if(rate==null){
            // CNY在这里
            return new BigDecimal(1);
        }
        return rate.getRate();
    }


}
