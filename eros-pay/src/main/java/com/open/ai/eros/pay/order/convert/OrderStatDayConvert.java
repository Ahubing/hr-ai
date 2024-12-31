package com.open.ai.eros.pay.order.convert;


import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.db.mysql.pay.entity.OrderStatDay;
import com.open.ai.eros.pay.order.bean.vo.OrderVo;
import com.open.ai.eros.pay.vo.OrderStatDayVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface OrderStatDayConvert {


    OrderStatDayConvert I = Mappers.getMapper(OrderStatDayConvert.class);

    @Mapping(target = "income",source = "cost",qualifiedByName = "getIncome")
    OrderStatDayVo convertOrderVo(OrderStatDay orderStatDay);

    @Named("getIncome")
    default String getIncome(BigDecimal cost){
        return cost.toString();
    }

}
