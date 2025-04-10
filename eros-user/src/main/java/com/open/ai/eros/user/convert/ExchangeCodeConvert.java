package com.open.ai.eros.user.convert;


import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.db.constants.ExchangeCodeTypeEnum;
import com.open.ai.eros.db.mysql.user.entity.ExchangeCode;
import com.open.ai.eros.user.bean.req.AddExchangeCodeReq;
import com.open.ai.eros.user.bean.req.UpdateExchangeCodeReq;
import com.open.ai.eros.user.bean.vo.CExchangeCodeVo;
import com.open.ai.eros.user.bean.vo.ExchangeCodeVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ExchangeCodeConvert {

    ExchangeCodeConvert I = Mappers.getMapper(ExchangeCodeConvert.class);


    ExchangeCode convertExchangeCode(AddExchangeCodeReq req);

    ExchangeCode convertExchangeCode(UpdateExchangeCodeReq req);


    @Mapping(target = "bizValue",source = "exchangeCode",qualifiedByName = "convertBizValue" )
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "getType" )
    ExchangeCodeVo convertExchangeCodeVo(ExchangeCode exchangeCode);


    @Mapping(target = "bizValue",source = "exchangeCode",qualifiedByName = "convertBizValue" )
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "getType" )
    CExchangeCodeVo convertCExchangeCodeVo(ExchangeCode exchangeCode);


    @Named("convertBizValue")
    default String convertBizValue(ExchangeCode exchangeCode){
        String type = exchangeCode.getType();
        if(type.contains("balance")){
            return BalanceFormatUtil.getUserBalance(Long.parseLong(exchangeCode.getBizValue()));
        }
        return exchangeCode.getBizValue();
    }


    @Named("getType")
    default String getType(String type){
        return ExchangeCodeTypeEnum.getDesc(type);
    }

}
