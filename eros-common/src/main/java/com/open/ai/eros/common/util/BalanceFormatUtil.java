package com.open.ai.eros.common.util;

import com.open.ai.eros.common.constants.BalanceUnitEnum;
import com.open.ai.eros.common.constants.CommonConstant;
import com.open.ai.eros.common.exception.BizException;

import java.math.BigDecimal;

/**
 * @类名：BigDecimalUtil
 * @项目名：web-eros-ai
 * @description：
 * @创建人：陈臣
 * @创建时间：2024/8/11 23:56
 */
public class BalanceFormatUtil {






    public static String getCNY(Long balance,String unit){
        if(BalanceUnitEnum.CNY.getUnit().equals(unit)){
            return String.valueOf(balance);
        }
        if(BalanceUnitEnum.DOLLAR.getUnit().equals(unit)){

        }
        throw new BizException("钱转化失败");
    }


    /**
     *     * 将余额转化为用户可见单位 四位小数
     *
     * @param balance
     * @return
     */
    public static String getUserExactBalance(Long balance) {
        if (balance == null) {
            return "0.00";
        }
        return new BigDecimal(balance).divide(new BigDecimal(CommonConstant.ONE_DOLLAR), 4, BigDecimal.ROUND_HALF_UP).toString();
    }


    /**
     * 将余额转化为用户可见单位 两位小数
     *
     * @param balance
     * @return
     */
    public static String getUserBalance(Long balance) {
        if (balance == null) {
            return "0.00";
        }
        return new BigDecimal(balance).divide(new BigDecimal(CommonConstant.ONE_DOLLAR), 2, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static void main(String[] args) {
        System.out.println(getUserBalance(CommonConstant.userInitBalance));
    }


}
