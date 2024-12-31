package com.open.ai.eros.user.convert;


import com.open.ai.eros.common.util.BalanceFormatUtil;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatDay;
import com.open.ai.eros.db.mysql.user.entity.UserIncomeStatVo;
import com.open.ai.eros.user.bean.vo.UserIncomeStatDayVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserIncomeStatConvert {


    UserIncomeStatConvert I = Mappers.getMapper(UserIncomeStatConvert.class);


    UserIncomeStatDay convertUserIncomeStat(UserIncomeStatVo statVo);


    @Mapping(target = "income",source = "income", qualifiedByName = "getCostPoints")
    UserIncomeStatDayVo convertUserIncomeStatDayVo(UserIncomeStatVo statVo);



    @Mapping(target = "income",source = "income", qualifiedByName = "getCostPoints")
    UserIncomeStatDayVo convertUserIncomeStatDayVo(UserIncomeStatDay statVo);



    @Named("getCostPoints")
    default String getCostPoints(Long costPoints){
        return BalanceFormatUtil.getUserBalance(costPoints);
    }



}
