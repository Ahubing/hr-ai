package com.open.ai.eros.pay.order.convert;


import com.open.ai.eros.db.constants.OrderStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.Order;
import com.open.ai.eros.pay.order.bean.vo.OrderVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface OrderConvert {


    OrderConvert I = Mappers.getMapper(OrderConvert.class);

    @Mapping(target = "statusDesc",source = "status",qualifiedByName = "getStatus")
    @Mapping(target = "price",source = "price",qualifiedByName = "getPrice")
    OrderVo convertOrderVo(Order order);


    @Named("getPrice")
    default String getPrice(BigDecimal price){
        return price.toString();
    }

    @Named("getStatus")
    default String getStatus(Integer status){
       return OrderStatusEnum.getDesc(status);
    }

}
