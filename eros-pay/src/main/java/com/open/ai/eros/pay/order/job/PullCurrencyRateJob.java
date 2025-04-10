package com.open.ai.eros.pay.order.job;

import com.open.ai.eros.common.constants.BalanceUnitEnum;
import com.open.ai.eros.db.mysql.pay.entity.CurrencyRate;
import com.open.ai.eros.db.mysql.pay.service.impl.CurrencyRateServiceImpl;
import com.open.ai.eros.pay.util.PayHttpUtil;
import com.open.ai.eros.pay.vo.CurrencyRateDataVo;
import com.open.ai.eros.pay.vo.CurrencyRateVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @类名：pullCurrencyRateJob
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/9/4 15:19
 */
@Component
@Slf4j
@EnableScheduling
public class PullCurrencyRateJob {


    @Autowired
    private CurrencyRateServiceImpl currencyRateService;





    /**
     * 更新超时订单数 1小时更新一次
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 )
    @Transactional(rollbackFor = Exception.class)
    public void pullCurrencyRate() throws Exception {
        log.info("开始获取货币汇率");
        //?startDate=2024-07-19&endDate=2024-07-19&pageNum=1&pageSize=30
        //String s = "https://www.chinamoney.com.cn/ags/ms/cm-u-bk-ccpr/CcprHisNew";

        CurrencyRateVo currencyRateVo = PayHttpUtil.balanceRate();
        CurrencyRateDataVo data = currencyRateVo.getData();
        if (data!=null) {
            //获取货币名称
            List<String> searchlist = data.getSearchlist();
            if(CollectionUtils.isEmpty(currencyRateVo.getRecords())){
                return;
            }
            //货币汇率
            List<String> values = currencyRateVo.getRecords().get(0).getValues();

            List<CurrencyRate> currencyRateList = new ArrayList<>();

            for (int i = 0; i < searchlist.size(); i++) {
                String unit = searchlist.get(i);
                String rate = values.get(i);

                BalanceUnitEnum unitEnum = null;
                if(unit.startsWith(BalanceUnitEnum.DOLLAR.getUnit())){
                    unitEnum = BalanceUnitEnum.DOLLAR;
                }
                if(unitEnum==null){
                    continue;
                }
                CurrencyRate currency = new CurrencyRate();
                currency.setCurrencyName(unitEnum.getDesc())
                        .setCurrencyCode(unitEnum.getUnit());
                currency.setCreateTime(LocalDateTime.now());
                currency.setRate(new BigDecimal(rate));
                currencyRateList.add(currency);
            }
            //删除数据
            currencyRateService.remove(null);
            //存入数据
            currencyRateService.saveBatch(currencyRateList);
        }
    }


}
