package com.open.ai.eros.user.convert;


import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.db.constants.CostTypeEnum;
import com.open.ai.eros.db.mysql.ai.entity.UserAiConsumeRecord;
import com.open.ai.eros.user.bean.vo.UserAiConsumeRecordVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAiConsumeRecordConvert {


    UserAiConsumeRecordConvert I = Mappers.getMapper(UserAiConsumeRecordConvert.class);


    @Mapping(target = "costTypeDesc",source = "costType", qualifiedByName = "getCostType")
    @Mapping(target = "cost",source = "cost", qualifiedByName = "getCost")
    UserAiConsumeRecordVo convertUserAiConsumeRecordVo(UserAiConsumeRecord record);


    @Named("getCost")
    default String getCost(Long cost){
        return BalanceFormatUtil.getUserExactBalance(cost);
    }

    @Named("getCostType")
    default String getCostType(Integer costType){
        return CostTypeEnum.getDesc(costType);
    }

}
