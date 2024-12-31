package com.open.ai.eros.pay.goods.convert;


import com.open.ai.eros.db.constants.GoodTypeEnum;
import com.open.ai.eros.db.mysql.pay.entity.Goods;
import com.open.ai.eros.db.mysql.pay.entity.GoodsSnapshot;
import com.open.ai.eros.pay.goods.bean.req.GoodsAddReq;
import com.open.ai.eros.pay.goods.bean.req.GoodsUpdateReq;
import com.open.ai.eros.pay.goods.bean.vo.CGoodsVo;
import com.open.ai.eros.pay.goods.bean.vo.GoodsVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface GoodsConvert {


    GoodsConvert I = Mappers.getMapper(GoodsConvert.class);


    @Mapping(target = "price",source = "price",qualifiedByName = "getBigDecimalPrice")
    Goods convertGoods(GoodsAddReq req);


    @Mapping(target = "price",source = "price",qualifiedByName = "getBigDecimalPrice")
    Goods convertGoods(GoodsUpdateReq req);



    @Mapping(target = "price",source = "price",qualifiedByName = "getPrice")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "getType")
    CGoodsVo convertCGoodsVo(Goods goods);

    @Mapping(target = "price",source = "price",qualifiedByName = "getPrice")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "getType")
    GoodsVo convertGoodsVo(Goods goods);


    @Mapping(target = "goodsId",source = "id",qualifiedByName = "newGoodsId")
    @Mapping(target = "id",source = "id",qualifiedByName = "newId")
    GoodsSnapshot convertGoodsSnapshot(Goods goods);



    @Named("getType")
    default String getType(String type){
        return GoodTypeEnum.getDesc(type);
    }


    @Named("getPrice")
    default String getPrice(BigDecimal price){
        return price.toString();
    }


    @Named("getBigDecimalPrice")
    default BigDecimal getBigDecimalPrice(String price){
        return new BigDecimal(price);
    }





    @Named("newGoodsId")
    default Long newGoodsId(Long id){
        return id;
    }


    @Named("newId")
    default Long newId(Long id){
        return null;
    }


}
