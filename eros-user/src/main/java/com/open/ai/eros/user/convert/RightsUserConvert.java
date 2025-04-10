package com.open.ai.eros.user.convert;


import com.open.ai.eros.db.constants.RightsTypeEnum;
import com.open.ai.eros.db.constants.UserRightsStatusEnum;
import com.open.ai.eros.db.mysql.pay.entity.RightsSnapshot;
import com.open.ai.eros.db.mysql.pay.entity.UserRights;
import com.open.ai.eros.user.bean.vo.UserRightsVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RightsUserConvert {


    RightsUserConvert I = Mappers.getMapper(RightsUserConvert.class);


    @Mapping(target = "canUseModel",source = "canUseModel",qualifiedByName = "convertCanUseModel")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "convertTypeDesc")
    UserRightsVo convertUserRightsVo(RightsSnapshot rights);

    @Mapping(target = "canUseModel",source = "canUseModel",qualifiedByName = "convertCanUseModel")
    @Mapping(target = "typeDesc",source = "type",qualifiedByName = "convertTypeDesc")
    UserRightsVo convertUserRightsVo(UserRights rights);


    @Named("convertTypeDesc")
    default String convertTypeDesc(String type){
        return RightsTypeEnum.getDesc(type);
    }


    @Named("convertStatusDesc")
    default String convertStatusDesc(Integer status){
        return UserRightsStatusEnum.getDesc(status);
    }


    @Named("convertCanUseModel")
    default String convertCanUseModel(String canUseModel){
        if("-".equals(canUseModel)){
            return "所有模型";
        }
        return canUseModel;
    }

}
